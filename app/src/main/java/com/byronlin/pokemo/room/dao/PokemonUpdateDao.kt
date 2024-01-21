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
import com.byronlin.pokemo.room.entity.PokemonLoadEntity
import com.byronlin.pokemo.room.entity.PokemonTypesRelationshipEntity
import com.byronlin.pokemo.room.entity.SpeciesDescriptionEntity
import com.byronlin.pokemo.room.entity.SpeciesEntity


@Dao
interface PokemonUpdateDao {

    @Insert
    fun insertLoadRecord(record: PokemonLoadEntity)

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
    fun insertOrUpdatePokemonType(pokemonTypesRelationshipEntity: PokemonTypesRelationshipEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrUpdatePokemonTypeList(pokemonTypesRelationshipEntityList: List<PokemonTypesRelationshipEntity>)


    @Transaction
    fun loadToDatabase(
        pokemonInfoList: List<PokemonInfo>,
        speciesInfoList: List<SpeciesInfo>,
        next: Int
    ) {
        val writeEntityInfo =
            DataHelper.transferPokemonInfoListToWriteEntityInfo(pokemonInfoList, speciesInfoList)
        insertOrUpdateSpeciesList(writeEntityInfo.speciesEntityList)
        insertOrUpdateSpeciesDescriptionList(writeEntityInfo.speciesDescriptionEntityList)
        insertOrUpdatePokemonTypeList(writeEntityInfo.pokemonTypesRelationshipEntityList)
        insertOrUpdatePokemonList(writeEntityInfo.pokemonEntityList)
        insertLoadRecord(PokemonLoadEntity(next = next))
    }

    @Transaction
    fun loadToDatabase(
        writeEntityInfo: WriteEntityInfo,
        next: Int) {
        insertOrUpdateSpeciesList(writeEntityInfo.speciesEntityList)
        insertOrUpdateSpeciesDescriptionList(writeEntityInfo.speciesDescriptionEntityList)
        insertOrUpdatePokemonTypeList(writeEntityInfo.pokemonTypesRelationshipEntityList)
        insertOrUpdatePokemonList(writeEntityInfo.pokemonEntityList)
        insertLoadRecord(PokemonLoadEntity(next = next))
    }

    fun clearAll() {

    }
}