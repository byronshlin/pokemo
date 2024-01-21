package com.byronlin.pokemo.room.data

import com.byronlin.pokemo.room.entity.PokemonEntity
import com.byronlin.pokemo.room.entity.PokemonTypesRelationshipEntity
import com.byronlin.pokemo.room.entity.SpeciesDescriptionEntity
import com.byronlin.pokemo.room.entity.SpeciesEntity

class WriteEntityInfo(
    val speciesEntityList: List<SpeciesEntity>,
    val speciesDescriptionEntityList: List<SpeciesDescriptionEntity>,
    val pokemonTypesRelationshipEntityList: List<PokemonTypesRelationshipEntity>,
    val pokemonEntityList: List<PokemonEntity>
)