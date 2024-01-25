package com.byronlin.pokemo.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "pokemon")
data class PokemonEntity(
    @PrimaryKey val id: String,
    val name: String,
    val posterUrl: String,
    val idOfSpecies: String
)