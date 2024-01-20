package com.byronlin.pokemo.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Transaction
import com.byronlin.pokemo.room.data.DataHelper
import com.byronlin.pokemo.room.data.PokemonInfo
import com.byronlin.pokemo.room.data.SpeciesInfo
import com.byronlin.pokemo.room.data.WriteEntityInfo
import com.byronlin.pokemo.room.entity.PokemonEntity
import com.byronlin.pokemo.room.entity.PokemonTypesEntity
import com.byronlin.pokemo.room.entity.SpeciesDescriptionEntity
import com.byronlin.pokemo.room.entity.SpeciesEntity


@Dao
interface PokemonUpdateDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrUpdateSpecies(species: SpeciesEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrUpdateSpeciesList(species: List<SpeciesEntity>)


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrUpdatePokemon(pokemonEntity: PokemonEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrUpdatePokemonList(pokemonEntityList: List<PokemonEntity>)


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrUpdateSpeciesDescription(speciesDescriptionEntity: SpeciesDescriptionEntity)


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrUpdateSpeciesDescriptionList(speciesDescriptionEntity: List<SpeciesDescriptionEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrUpdatePokemonType(pokemonTypesEntity: PokemonTypesEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrUpdatePokemonTypeList(pokemonTypesEntityList: List<PokemonTypesEntity>)


    @Transaction
    fun loadToDatabase(pokemonInfoList: List<PokemonInfo>, speciesInfoList: List<SpeciesInfo>) {
        val writeEntityInfo =
            DataHelper.transferPokemonInfoListToWriteEntityInfo(pokemonInfoList, speciesInfoList)
        insertOrUpdateSpeciesList(writeEntityInfo.speciesEntityList)
        insertOrUpdateSpeciesDescriptionList(writeEntityInfo.speciesDescriptionEntityList)
        insertOrUpdatePokemonTypeList(writeEntityInfo.pokemonTypesEntityList)
        insertOrUpdatePokemonList(writeEntityInfo.pokemonEntityList)
    }

    @Transaction
    fun loadToDatabase(writeEntityInfo: WriteEntityInfo) {
        insertOrUpdateSpeciesList(writeEntityInfo.speciesEntityList)
        insertOrUpdateSpeciesDescriptionList(writeEntityInfo.speciesDescriptionEntityList)
        insertOrUpdatePokemonTypeList(writeEntityInfo.pokemonTypesEntityList)
        insertOrUpdatePokemonList(writeEntityInfo.pokemonEntityList)
    }

    fun clearAll() {

    }
}