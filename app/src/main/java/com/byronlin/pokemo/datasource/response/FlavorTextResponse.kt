package com.byronlin.pokemo.datasource.response

import com.google.gson.annotations.SerializedName

data class FlavorTextResponse(
    @SerializedName("flavor_text") val flavor_text: String,
    @SerializedName("language") val language: NamedAPIResourceResponse
)