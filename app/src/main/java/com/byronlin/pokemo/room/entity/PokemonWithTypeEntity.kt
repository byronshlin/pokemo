package com.byronlin.pokemo.room.entity

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey

data class PokemonWithTypeEntity(
    val id: String,
    val name: String,
    val posterUrl: String,
    val idOfSpecies: String,
    var captured: Int,
    var type: String,
)