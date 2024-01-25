package com.byronlin.pokemo.model

import com.byronlin.pokemo.room.entity.PokemonEntity

data class PokemonDetails(
    val id: String,
    val name: String,
    val typeList: List<String>,
    val defaultPosterUrl: String?,
    val defaultSpeciesDescription: String,
    val evolvedPokemon: PokemonEntity?,
)