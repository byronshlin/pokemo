package com.byronlin.pokemo.room.entity

import androidx.room.Entity


@Entity(tableName = "speciesDescription", primaryKeys = ["idOfSpecies", "language"])
data class SpeciesDescriptionEntity(
    var description: String,
    var language: String,
    var idOfSpecies: String
)