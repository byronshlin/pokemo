package com.byronlin.pokemo.repository

import android.content.Context
import android.net.Uri
import androidx.annotation.WorkerThread
import com.byronlin.pokemo.datasource.PokemonNetworkDataSource
import com.byronlin.pokemo.room.data.DataHelper
import com.byronlin.pokemo.room.data.DescriptionInfo
import com.byronlin.pokemo.room.data.PokemonInfo
import com.byronlin.pokemo.room.data.PokemonResourceResult
import com.byronlin.pokemo.room.data.SpeciesInfo
import com.byronlin.pokemo.utils.PKLog
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

class PokemonResourceLoader(
    private val pokemonRoomRepository: PokemonRoomRepository,
    private val dataSource: PokemonNetworkDataSource
) {

    private val TAG = "PokemonResourceLoader"

    private val BATCH_COUNT = 20

    private val MAX_SIZE = 151

    @Volatile
    private var stop = false


    fun stop() {
        stop = true
    }

    @WorkerThread
    suspend fun startLoadResourceToLocal(context: Context, callback: ((Boolean) -> Unit)? = null) {
        stop = true
        val queryDao = pokemonRoomRepository.obtainPokemonDatabase(context).queryDao()
        val updateDao = pokemonRoomRepository.obtainPokemonDatabase(context).updateDao()

        val offset = queryDao.queryNext() ?: 0

        if (offset > MAX_SIZE) {
            callback?.invoke(true)
            return
        }

        if (stop) {
            callback?.invoke(false)
            return
        }
        //start load
        val next = loadResourceToLocalByBatch(context, offset, BATCH_COUNT)
        if (next == -1) {
            callback?.invoke(false)
            return
        } else {
            callback?.invoke(false)
        }
    }


    fun startLoadResourceToLocalAsFlow(context: Context) = flow {
        val queryDao = pokemonRoomRepository.obtainPokemonDatabase(context).queryDao()
        do {
            val offset = queryDao.queryNext() ?: 0

            if (offset > MAX_SIZE) {
                emit(offset)
                break
            }

            if (stop) {
                emit(offset)
                break
            }

            val limit = if (offset + BATCH_COUNT > MAX_SIZE) {
                MAX_SIZE - offset
            } else {
                BATCH_COUNT
            }

            //start load
            val next = loadResourceToLocalByBatch(context, offset, limit)
            if (next == -1) {
                emit(offset)
                break
            } else {
                emit(offset)
            }
            delay(1000)
        } while (true)
    }.flowOn(Dispatchers.IO)


    @WorkerThread
    suspend fun loadResourceToLocalOnce(context: Context, limit: Int, continueLoad: Boolean): Boolean {
        val queryDao = pokemonRoomRepository.obtainPokemonDatabase(context).queryDao()
        val offset = queryDao.queryNext() ?: 0
        PKLog.v(TAG, "loadResourceToLocalOnce: offset = ${offset}")
        if (!continueLoad && offset > 0) {
            return false
        }
        val begin = System.currentTimeMillis()
        val next = loadResourceToLocalByBatch(context, offset, limit)
        PKLog.v(TAG, "loadResourceToLocal~: response = ${next} spend = ${System.currentTimeMillis() - begin}")
        return true
    }

    private suspend fun loadResourceToLocalByBatch(
        context: Context,
        offset: Int,
        limit: Int
    ): Int {
        val updateDao = pokemonRoomRepository.obtainPokemonDatabase(context).updateDao()
        var begin = System.currentTimeMillis()
        val pokemonResourceResult = loadPokemonResource(offset, limit) ?: return -1

        PKLog.v(TAG, "loadResourceToLocalByBatch: ${System.currentTimeMillis() - begin}")

        val writeInfo = DataHelper.transferPokemonInfoListToWriteEntityInfo(
            pokemonResourceResult.pokemonInfoList,
            pokemonResourceResult.speciesInfoList
        )
        updateDao.loadToDatabase(writeInfo, pokemonResourceResult.next)
        return pokemonResourceResult.next
    }


    /**
     * 1. load pokemon resource list
     * 2. load pokemon info (20 thread) concurrently
     * 3. load species info (20 thread) concurrently
     */

    @WorkerThread
    private suspend fun loadPokemonResource(
        offset: Int,
        limit: Int
    ) = withContext(Dispatchers.IO) {


        var begin = System.currentTimeMillis()
        //#1
        val pokemonResource = dataSource.queryPokemonResources(offset, limit)
        PKLog.v(TAG, "queryPokemonResources: ${System.currentTimeMillis() - begin}")

        pokemonResource ?: return@withContext null

        val pokemonIdList: List<String> =
            pokemonResource.results.map { it.url.let { Uri.parse(it) }?.lastPathSegment }
                .filterNotNull()

        begin = System.currentTimeMillis()
        //#2
        val deferredList: List<Deferred<PokemonInfo?>> = pokemonIdList.map { pokemonId ->
            val deferred: Deferred<PokemonInfo?> = async {
                val pokemonResponse = dataSource.queryPokemon(pokemonId)

                PKLog.v(TAG, "queryPokemon: ${pokemonId} ${pokemonResponse != null}")

                if (pokemonResponse == null) {
                    null
                } else {
                    val id = pokemonResponse.id
                    val name = pokemonResponse.name
                    val types: List<String> =
                        pokemonResponse.types.map { it.type.name } ?: arrayListOf()

                    val posterUrl =
                        pokemonResponse.sprites?.spritesOtherResponse?.officialArtworkResponse?.frontDefaultUrl

                    val speciesData = pokemonResponse.species.let {
                        val speciesName = it.name
                        val speciesId = it.url.let { Uri.parse(it) }?.lastPathSegment ?: ""
                        Pair(speciesName, speciesId)
                    }
                    PokemonInfo(id, name, types, posterUrl!!, speciesData.second)
                }
            }
            deferred
        }

        val pokemonInfoList = deferredList.map {
            it.await()
        }.filterNotNull()


        PKLog.v(
            TAG,
            "load queryPokemon List : for ${pokemonIdList.size} spend=${System.currentTimeMillis() - begin}"
        )

        //#3
        val speciesIds = pokemonInfoList.map {
            it.speciesId
        }.distinct()

        begin = System.currentTimeMillis()
        val speciesInfoList = speciesIds.map {
            async {
                //PKLog.v(TAG, "querySpecies: ${it}")
                dataSource.querySpecies(it)
            }
        }.map {
            it.await()
        }.filterNotNull().map {
            val idFrom =
                it.evolvesFromSpecies?.url?.let { Uri.parse(it) }?.lastPathSegment

            val list = it.flavorTextEntries?.map {
                DescriptionInfo(language = it.language.name, description = it.flavor_text)
            } ?: arrayListOf()

            SpeciesInfo(
                it.id,
                it.name,
                idFrom,
                list
            )
        }
        PKLog.v(
            TAG,
            "load speciesInfoList for ${speciesIds.size} spend=${System.currentTimeMillis() - begin}"
        )

        //save next
        val next = pokemonResource.next?.let {
            Uri.parse(it).getQueryParameter("offset")?.toInt()
        } ?: pokemonResource.count

        return@withContext PokemonResourceResult(
            pokemonInfoList, speciesInfoList,
            next
        )
    }


}