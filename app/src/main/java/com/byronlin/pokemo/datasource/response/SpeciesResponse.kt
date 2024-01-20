package com.byronlin.pokemo.datasource.response

import com.google.gson.annotations.SerializedName

data class SpeciesResponse(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("evolves_from_species") val evolvesFromSpecies: NamedAPIResourceResponse?,
    @SerializedName("flavor_text_entries") val flavorTextEntries: List<FlavorTextResponse>?,
)