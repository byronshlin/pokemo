package com.byronlin.pokemo.room.entity

import androidx.room.Entity

@Entity(tableName = "capture", primaryKeys = ["id"])
data class CaptureEntity(
    val id: String
)