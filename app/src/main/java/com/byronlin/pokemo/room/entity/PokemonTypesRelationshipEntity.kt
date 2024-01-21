package com.byronlin.pokemo.room.entity

import androidx.room.Entity


@Entity(
    tableName = "pokemonTypesRelationship",
    primaryKeys = ["idOfPokemon", "type"],
    indices = [androidx.room.Index(value = ["type"])]
)
data class PokemonTypesRelationshipEntity(
    val idOfPokemon: String,
    val type: String
)