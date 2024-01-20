package com.byronlin.pokemo.datasource.response

import com.google.gson.annotations.SerializedName

data class PokemonTypesResponse(
    @SerializedName("type") val type: PokemonTypeResponse,
)