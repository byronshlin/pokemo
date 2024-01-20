package com.byronlin.pokemo.datasource.response

import com.google.gson.annotations.SerializedName

data class SpritesOtherResponse(
    @SerializedName("official-artwork") val officialArtworkResponse: OfficialArtworkResponse
)