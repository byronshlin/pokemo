package com.byronlin.pokemo.repository

import android.app.Application
import android.content.Context
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.room.Room
import com.byronlin.pokemo.room.PokemonRoomDatabase
import com.byronlin.pokemo.room.entity.PokemonEntity
import com.byronlin.pokemo.room.entity.PokemonWithTypeEntity

class PokemonRoomRepository(private val application: Application) {
    @WorkerThread
    fun obtainPokemonDatabase(context: Context): PokemonRoomDatabase {
        return getDatabase(context)
    }

    fun queryTypes(): List<String> {
        val queryDao = ensurePokemonDatabase().queryDao()
        return queryDao.queryTypes()
    }

    fun queryCapturePokemonList(): List<PokemonEntity> {
        val queryDao = ensurePokemonDatabase().queryDao()
        return queryDao.queryCapturePokemonList()
    }

    fun queryPokemonTypePairList(): List<PokemonWithTypeEntity> {
        val queryDao = ensurePokemonDatabase().queryDao()
        return queryDao.queryPokemonTypePairList()
    }

    fun queryPokemonTypePairListLiveData(): LiveData<List<PokemonWithTypeEntity>> {
        val queryDao = ensurePokemonDatabase().queryDao()
        return queryDao.queryPokemonTypePairListLiveData()
    }



    fun queryPokemonEntityListByType(type: String): List<PokemonEntity> {
        val queryDao = ensurePokemonDatabase().queryDao()
        return queryDao.queryPokemonEntityListByType(type)
    }

    fun catchPokemon(id: String) {
        val updateDao = ensurePokemonDatabase().updateDao()
        updateDao.catchPokemon(id)
    }

    fun releasePokemon(id: String) {
        val updateDao = ensurePokemonDatabase().updateDao()
        updateDao.releasePokemon(id)
    }

    private fun ensurePokemonDatabase(): PokemonRoomDatabase {
        return obtainPokemonDatabase(application)
    }


    companion object {
        private var LOCK = Any()

        @Volatile
        private var sCommonLibRoomDatabase: PokemonRoomDatabase? = null
        fun getDatabase(context: Context): PokemonRoomDatabase {
            return sCommonLibRoomDatabase ?: synchronized(LOCK) {
                if (sCommonLibRoomDatabase == null) {
                    sCommonLibRoomDatabase = Room.databaseBuilder(
                        context,
                        PokemonRoomDatabase::class.java,
                        PokemonRoomDatabase.DATABASE_NAME)
                        .build()
                    sCommonLibRoomDatabase
                } else sCommonLibRoomDatabase
                sCommonLibRoomDatabase!!
            }
        }

    }
}