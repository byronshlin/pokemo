package com.byronlin.pokemo.repository

import android.content.Context
import android.net.Uri
import androidx.annotation.WorkerThread
import com.byronlin.pokemo.datasource.PokemonNetworkDataSource
import com.byronlin.pokemo.room.PokemonRoomHelper
import com.byronlin.pokemo.room.data.DataHelper
import com.byronlin.pokemo.room.data.DescriptionInfo
import com.byronlin.pokemo.room.data.PokemonInfo
import com.byronlin.pokemo.room.data.PokemonResourceResult
import com.byronlin.pokemo.room.data.SpeciesInfo
import com.byronlin.pokemo.utils.PKLog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class PokemonResourceLoader {

    private val TAG = "PokemonResourceLoader"
    private val dataSource = PokemonNetworkDataSource()

    private val BATCH_COUNT = 20

    private val MAX_SIZE = 151

    @Volatile
    private var stop = false

    private val pokemonRoomHelper = PokemonRoomHelper()

    fun stop() {
        stop = true
    }

    @WorkerThread
    fun startLoadResourceToLocal(context: Context, callback: ((Boolean) -> Unit)? = null) {
        stop = true
        val queryDao = pokemonRoomHelper.obtainPokemonDatabase(context).queryDao()
        val updateDao = pokemonRoomHelper.obtainPokemonDatabase(context).updateDao()

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
        val pokemonResourceResult = loadPokemonResource(offset, BATCH_COUNT)
        if (pokemonResourceResult == null) {
            callback?.invoke(false)
            return
        }


        val writeInfo = DataHelper.transferPokemonInfoListToWriteEntityInfo(
            pokemonResourceResult.pokemonInfoList,
            pokemonResourceResult.speciesInfoList
        )
        updateDao.loadToDatabase(writeInfo, pokemonResourceResult.next)
        callback?.invoke(false)

    }


    @WorkerThread
    fun loadResourceToLocalOnce(context: Context, limit: Int): Boolean {


        val queryDao = pokemonRoomHelper.obtainPokemonDatabase(context).queryDao()
        val updateDao = pokemonRoomHelper.obtainPokemonDatabase(context).updateDao()


        updateDao.clearAll()

        val offset = queryDao.queryNext() ?: 0

        if (offset > 0) {
            return true
        }

        val beginLoad = System.currentTimeMillis()

        //start load
        val pokemonResourceResult = loadPokemonResource(0, limit) ?: return false

        val writeInfo = DataHelper.transferPokemonInfoListToWriteEntityInfo(
            pokemonResourceResult.pokemonInfoList,
            pokemonResourceResult.speciesInfoList
        )
        updateDao.loadToDatabase(writeInfo, pokemonResourceResult.next)
        return true
    }


    private fun loadPokemonResource(
        offset: Int,
        limit: Int
    ): PokemonResourceResult? {
        val pokemonResource = dataSource.queryPokemonResources(offset, limit)
        PKLog.v(TAG, "loadPokemonResource: $pokemonResource")

        pokemonResource ?: return null

        val next = pokemonResource.next?.let {
            Uri.parse(it).getQueryParameter("offset")?.toInt()
        } ?: pokemonResource.count

        val pokemonInfoList: List<PokemonInfo> = pokemonResource.results.map {
            val name = it.name
            val pokemonId = it.url.let { Uri.parse(it) }?.lastPathSegment
            Pair(name, pokemonId)
        }.filter { it.second != null }
            .map {
                val pokemonResponse = dataSource.queryPokemon(it.second!!)
                if (pokemonResponse == null) {
                    null
                } else {
                    val id = pokemonResponse.id
                    val name = pokemonResponse.name
                    val types: List<String> =
                        pokemonResponse.types?.map { it.type.name } ?: arrayListOf()

                    val posterUrl =
                        pokemonResponse.sprites?.spritesOtherResponse?.officialArtworkResponse?.frontDefaultUrl

                    val speciesData = pokemonResponse.species.let {
                        val speciesName = it.name
                        val speciesId = it.url.let { Uri.parse(it) }?.lastPathSegment ?: ""
                        Pair(speciesName, speciesId)
                    }
                    PokemonInfo(id, name, types, posterUrl!!, speciesData.second)
                }
            }.filterNotNull() ?: arrayListOf()


        val speciesIds = pokemonInfoList.map {
            it.speciesId
        }.distinct()


        val speciesInfoList = speciesIds.map {
            dataSource.querySpecies(it)
        }.filterNotNull().map {
            val idFrom =
                it.evolvesFromSpecies?.url?.let { Uri.parse(it) }?.lastPathSegment

            val list = it.flavorTextEntries?.map {
                DescriptionInfo(it.flavor_text, it.language.name)
            } ?: arrayListOf()

            SpeciesInfo(
                it.id,
                it.name,
                idFrom,
                list
            )
        }
        return PokemonResourceResult(
            pokemonInfoList, speciesInfoList,
            next
        )
    }


}