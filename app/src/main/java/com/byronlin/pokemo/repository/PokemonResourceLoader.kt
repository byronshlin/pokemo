package com.byronlin.pokemo.repository

import android.net.Uri
import com.byronlin.pokemo.datasource.PokemonNetworkDataSource
import com.byronlin.pokemo.room.data.DescriptionInfo
import com.byronlin.pokemo.room.data.PokemonInfo
import com.byronlin.pokemo.room.data.SpeciesInfo

class PokemonResourceLoader {

    private val TAG = "PokemonResourceLoader"
    private val dataSource = PokemonNetworkDataSource()

    fun loadResourceToLocal() {
        val list = loadPokemonResource()
    }


    fun loadPokemonResource(): Pair<List<PokemonInfo>, List<SpeciesInfo>> {
        val pokemonResource = dataSource.queryPokemonResources(0, 20)
        val pokemonInfoList: List<PokemonInfo> = pokemonResource?.results?.map {
            val name = it.name
            val pokemonId = it.url.let { Uri.parse(it) }?.lastPathSegment
            Pair<String, String?>(name, pokemonId)
        }?.filter { it.second != null }
            ?.map {
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
            }?.filterNotNull() ?: arrayListOf()


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
        return Pair(pokemonInfoList, speciesInfoList)
    }


}