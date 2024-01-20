package com.byronlin.pokemo.datasource.response

import com.google.gson.annotations.SerializedName

data class PokemonResponse(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String?,
    @SerializedName("species") val species: NamedAPIResourceResponse,
    @SerializedName("sprites") val sprites: SpritesResponse?,
    @SerializedName("types") val types: List<PokemonTypesResponse>,
)