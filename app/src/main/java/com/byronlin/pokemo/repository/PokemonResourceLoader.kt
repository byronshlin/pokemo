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
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class PokemonResourceLoader @Inject constructor(
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

    fun startLoadResourceToLocalAsFlow(context: Context) = flow {
        do {
            PKLog.v(TAG, "startLoadResourceToLocalAsFlow  obtainPokemonDatabase")
            val offset = pokemonRoomRepository.queryNext()

            if (offset > MAX_SIZE) {
                emit(-1)
                break
            }

            if (stop) {
                emit(-1)
                break
            }

            val limit = if (offset + BATCH_COUNT > MAX_SIZE) {
                MAX_SIZE - offset
            } else {
                BATCH_COUNT
            }

            //start load
            val next = loadResourceToLocalByBatch(offset, limit)
            if (next == -1) {
                emit(-1)
                break
            } else {
                emit(next)
            }
            delay(1000)
        } while (true)
    }.flowOn(Dispatchers.IO)


    private suspend fun loadResourceToLocalByBatch(
        offset: Int,
        limit: Int
    ): Int {
        var begin = System.currentTimeMillis()
        val pokemonResourceResult = loadPokemonResource(offset, limit) ?: return -1

        PKLog.v(TAG, "loadResourceToLocalByBatch: ${System.currentTimeMillis() - begin}")

        val writeInfo = DataHelper.transferPokemonInfoListToWriteEntityInfo(
            pokemonResourceResult.pokemonInfoList,
            pokemonResourceResult.speciesInfoList
        )
        pokemonRoomRepository.loadToDatabase(writeInfo, pokemonResourceResult.next)
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