package com.byronlin.pokemo.room.entity

import androidx.room.Entity

@Entity(tableName = "capture", primaryKeys = ["idOfPokemon"])
data class CaptureEntity(
    val idOfPokemon: String,
    val timeStamp: Long = System.currentTimeMillis()
)