package com.byronlin.pokemo.room

import android.content.Context
import androidx.annotation.WorkerThread
import androidx.lifecycle.map
import androidx.room.Room
import com.byronlin.pokemo.model.PokemonDisplayItem

object PokemonRoomHelper {




    private var sCommonLibRoomDatabase: PokemonRoomDatabase? = null

    @WorkerThread
    fun obtainPokemonDatabase(context: Context): PokemonRoomDatabase {
        sCommonLibRoomDatabase = sCommonLibRoomDatabase?: synchronized(this){
            sCommonLibRoomDatabase?: Room.databaseBuilder(
                context.applicationContext,
                PokemonRoomDatabase::class.java, PokemonRoomDatabase.DATABASE_NAME
            ).build()
        }
        return sCommonLibRoomDatabase!!
    }
}