package com.byronlin.pokemo.datasource.response

import com.google.gson.annotations.SerializedName

data class PokemonTypeResponse(
    @SerializedName("name") val name: String,
    @SerializedName("url") val url: String
)