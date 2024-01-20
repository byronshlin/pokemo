package com.byronlin.pokemo.room.entity

import androidx.room.Entity


@Entity(
    tableName = "pokemonTypes",
    primaryKeys = ["idOfPokemon", "type"],
    indices = [androidx.room.Index(value = ["type"])]
)
data class PokemonTypesEntity(
    val idOfPokemon: String,
    val type: String
)