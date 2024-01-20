package com.byronlin.pokemo.datasource.response

import com.google.gson.annotations.SerializedName

data class SpeciesOfPokemonResponse(
    @SerializedName("name") val name: String,
    @SerializedName("url") val url: String
)