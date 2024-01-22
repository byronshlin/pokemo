package com.byronlin.pokemo.model

import com.byronlin.pokemo.room.entity.PokemonEntity
import com.byronlin.pokemo.room.entity.SpeciesEntity

data class PokemonDetails(
    val id: String,
    val name: String,
    val typeList: List<String>,
    val defaultPosterUrl: String?,
    val defaultSpeciesDescription: String,
    val evolvedPokemon: PokemonEntity?,
)