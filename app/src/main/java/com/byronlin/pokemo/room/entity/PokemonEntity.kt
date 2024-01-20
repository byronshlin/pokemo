package com.byronlin.pokemo.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Ignore


@Entity(tableName = "pokemon")
data class PokemonEntity(
    @PrimaryKey var id : String,
    var name : String,
    var posterUrl: String,
    var idOfSpecies: String
)