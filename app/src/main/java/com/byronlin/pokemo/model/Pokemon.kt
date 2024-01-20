package com.byronlin.pokemo.model

import com.google.gson.annotations.SerializedName

data class Pokemon(
    val id: String,
    val name: String,
    val type: PokemonType,
    val defaultPosterUrl: String
)