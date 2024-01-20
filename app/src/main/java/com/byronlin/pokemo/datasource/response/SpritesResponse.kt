package com.byronlin.pokemo.datasource.response

import com.google.gson.annotations.SerializedName

data class SpritesResponse(
    @SerializedName("other") val spritesOtherResponse: SpritesOtherResponse?
)