package com.byronlin.pokemo.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.byronlin.pokemo.room.dao.PokemonQueryDao
import com.byronlin.pokemo.room.dao.PokemonUpdateDao
import com.byronlin.pokemo.room.entity.PokemonEntity
import com.byronlin.pokemo.room.entity.PokemonLoadEntity
import com.byronlin.pokemo.room.entity.PokemonTypesRelationshipEntity
import com.byronlin.pokemo.room.entity.SpeciesDescriptionEntity
import com.byronlin.pokemo.room.entity.SpeciesEntity



@Database(
    entities = [PokemonLoadEntity::class, PokemonEntity::class, PokemonTypesRelationshipEntity::class, SpeciesEntity::class, SpeciesDescriptionEntity::class],
    version = 1
)
abstract class PokemonRoomDatabase : RoomDatabase() {
    abstract fun updateDao(): PokemonUpdateDao
    abstract fun queryDao(): PokemonQueryDao


    companion object {
        const val DATABASE_NAME = "pokemon_database"
    }
}