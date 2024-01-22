package com.byronlin.pokemo.room

import android.content.Context
import androidx.annotation.WorkerThread
import androidx.lifecycle.map
import androidx.room.Room
import com.byronlin.pokemo.model.PokemonDisplayItem

class PokemonRoomHelper {
    @WorkerThread
    fun obtainPokemonDatabase(context: Context): PokemonRoomDatabase {
        if (sCommonLibRoomDatabase == null) {
            synchronized(LOCK){
                sCommonLibRoomDatabase = Room.databaseBuilder(
                    context.applicationContext,
                    PokemonRoomDatabase::class.java, PokemonRoomDatabase.DATABASE_NAME
                ).build()
            }
        }

        return sCommonLibRoomDatabase!!
    }

    companion object {
        private var sCommonLibRoomDatabase: PokemonRoomDatabase? = null
        private var LOCK  = Any()
    }
}