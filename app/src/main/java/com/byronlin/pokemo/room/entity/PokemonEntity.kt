package com.byronlin.pokemo.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Ignore


@Entity(tableName = "pokemon")
data class PokemonEntity(
    @PrimaryKey val id : String,
    val name : String,
    val posterUrl: String,
    val idOfSpecies: String,
    @ColumnInfo(name = "captured") var captured: Int
)