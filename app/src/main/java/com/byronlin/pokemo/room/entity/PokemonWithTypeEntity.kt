package com.byronlin.pokemo.room.entity

data class PokemonWithTypeEntity(
    val id: String,
    val name: String,
    val posterUrl: String,
    val idOfSpecies: String,
    var type: String
)