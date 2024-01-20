package com.byronlin.pokemo.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "species")
data class SpeciesEntity(
    @PrimaryKey val id: String,
    val name: String,
    val idOfFromSpecies: String?
)