package com.byronlin.pokemo.datasource.response

import com.google.gson.annotations.SerializedName

data class OfficialArtworkResponse(
    @SerializedName("front_default") val frontDefaultUrl: String,
    @SerializedName("front_shiny") val frontShinyUrl: String
)