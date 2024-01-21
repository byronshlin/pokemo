package com.byronlin.pokemo.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "pokemon_load")
data class PokemonLoadEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0, val next: Int, val lastTimeStamp: Long=System.currentTimeMillis())