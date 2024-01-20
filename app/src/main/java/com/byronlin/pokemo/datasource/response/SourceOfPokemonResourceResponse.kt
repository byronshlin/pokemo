package com.byronlin.pokemo.datasource.response

import com.google.gson.annotations.SerializedName

data class SourceOfPokemonResourceResponse(
    @SerializedName("name") val name: String,
    @SerializedName("url") val url: String
)