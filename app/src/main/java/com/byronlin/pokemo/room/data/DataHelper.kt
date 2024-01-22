package com.byronlin.pokemo.room.data

import com.byronlin.pokemo.room.entity.PokemonEntity
import com.byronlin.pokemo.room.entity.PokemonTypesRelationshipEntity
import com.byronlin.pokemo.room.entity.SpeciesDescriptionEntity
import com.byronlin.pokemo.room.entity.SpeciesEntity

object DataHelper {
    fun transferPokemonInfoListToWriteEntityInfo(pokemonInfoList: List<PokemonInfo>,
                                                 speciesInfoList: List<SpeciesInfo>): WriteEntityInfo {
        val speciesEntityList = speciesInfoList.map {
            SpeciesEntity(
                id = it.id,
                name = it.name,
                idOfFromSpecies = it.idOfFromSpecies
            )
        }

        val speciesDescriptionEntityList = speciesInfoList.flatMap {
            it.descriptionInfo.map { descriptionInfo ->
                SpeciesDescriptionEntity(
                    idOfSpecies = it.id,
                    language = descriptionInfo.language,
                    description = descriptionInfo.description
                )
            }
        }

        val typesList = pokemonInfoList.flatMap { pokemonInfo ->
            pokemonInfo.types.map {
                PokemonTypesRelationshipEntity(
                    idOfPokemon = pokemonInfo.id,
                    type = it
                )
            }
        }


        val pokemonEntityList = pokemonInfoList.mapNotNull {
            PokemonEntity(
                id = it.id,
                name = it.name ?: "",
                posterUrl = it.posterUrl ?: "",
                idOfSpecies = it.speciesId,
                0
            )
        }
        return WriteEntityInfo(
            speciesEntityList,
            speciesDescriptionEntityList,
            typesList,
            pokemonEntityList
        )
    }
}