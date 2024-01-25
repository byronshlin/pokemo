package com.byronlin.pokemo.repository

import android.content.Context
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.room.Room
import com.byronlin.pokemo.room.PokemonRoomDatabase
import com.byronlin.pokemo.room.entity.CaptureEntity
import com.byronlin.pokemo.room.entity.PokemonEntity
import com.byronlin.pokemo.room.entity.PokemonWithTypeEntity
import com.byronlin.pokemo.room.entity.SpeciesDescriptionEntity
import com.byronlin.pokemo.room.entity.SpeciesEntity
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PokemonRoomRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    @WorkerThread
    fun obtainPokemonDatabase(context: Context): PokemonRoomDatabase {
        return getDatabase(context)
    }

    fun checkNotEmpty(): Boolean {
        val queryDao = ensurePokemonDatabase().queryDao()
        return queryDao.queryFirstPokemon() != null
    }

    fun queryTypes(): List<String> {
        val queryDao = ensurePokemonDatabase().queryDao()
        return queryDao.queryTypes()
    }

    fun querySpeciesDescriptionBySpeciesId(id: String): List<SpeciesDescriptionEntity> {
        val queryDao = ensurePokemonDatabase().queryDao()
        return queryDao.querySpeciesDescriptionBySpeciesId(id)
    }

    fun querySpeciesEntityBySpeciesId(id: String): SpeciesEntity? {
        val queryDao = ensurePokemonDatabase().queryDao()
        return queryDao.querySpeciesEntityBySpeciesId(id)
    }

    fun queryPokemonEntityById(id: String): PokemonEntity? {
        val queryDao = ensurePokemonDatabase().queryDao()
        return queryDao.queryPokemonEntityById(id)
    }

    fun queryTypesOfPokemon(id: String): List<String> {
        val queryDao = ensurePokemonDatabase().queryDao()
        return queryDao.queryTypesOfPokemon(id)
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


    fun queryPokemonEntityList(type: String): List<PokemonEntity> {
        val queryDao = ensurePokemonDatabase().queryDao()
        return queryDao.queryPokemonEntityListByType(type)
    }

    fun queryPokemonEntityListByType(type: String): List<PokemonEntity> {
        val queryDao = ensurePokemonDatabase().queryDao()
        return queryDao.queryPokemonEntityListByType(type)
    }

    fun catchPokemon(id: String) {
        ensurePokemonDatabase().updateDao().catchPokemon(CaptureEntity(id))
    }

    fun releasePokemon(id: String) {
        ensurePokemonDatabase().queryDao().queryCaptureEntity(id)?.also {
            ensurePokemonDatabase().updateDao().releasePokemon(it)
        }
    }

    fun queryCaptureListLiveData(): LiveData<List<CaptureEntity>> {
        return ensurePokemonDatabase().queryDao().queryCaptureListLiveData()
    }

    private fun ensurePokemonDatabase(): PokemonRoomDatabase {
        return obtainPokemonDatabase(context)
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
                        PokemonRoomDatabase.DATABASE_NAME
                    )
                        .build()
                    sCommonLibRoomDatabase
                } else sCommonLibRoomDatabase
                sCommonLibRoomDatabase!!
            }
        }

    }
}