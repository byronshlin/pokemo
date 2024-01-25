package com.byronlin.pokemo.room.entity

import androidx.room.Entity


@Entity(
    tableName = "pokemonTypesRelationship",
    primaryKeys = ["idOfPokemon", "type"],
    indices = [androidx.room.Index(value = ["type"]), androidx.room.Index(value = ["idOfPokemon"])]
)
data class PokemonTypesRelationshipEntity(
    val idOfPokemon: String,
    val type: String
)