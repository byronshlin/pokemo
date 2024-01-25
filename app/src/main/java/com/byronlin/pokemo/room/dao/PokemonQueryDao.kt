package com.byronlin.pokemo.room.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.byronlin.pokemo.room.entity.CaptureEntity
import com.byronlin.pokemo.room.entity.PokemonEntity
import com.byronlin.pokemo.room.entity.PokemonTypesRelationshipEntity
import com.byronlin.pokemo.room.entity.PokemonWithTypeEntity
import com.byronlin.pokemo.room.entity.SpeciesDescriptionEntity
import com.byronlin.pokemo.room.entity.SpeciesEntity


@Dao
interface PokemonQueryDao {
    @Query("SELECT next FROM pokemon_load ORDER BY lastTimeStamp DESC LIMIT 1")
    fun queryNext(): Int?


    @Query("SELECT * FROM pokemon limit 1")
    fun queryFirstPokemon(): PokemonEntity?

    @Query("SELECT * FROM pokemon")
    fun queryPokemonEntityList(): List<PokemonEntity>

    @Query("SELECT * FROM species")
    fun querySpeciesEntityList(): List<SpeciesEntity>

    @Query("SELECT * FROM species WHERE id = :id")
    fun querySpeciesEntityBySpeciesId(id: String): SpeciesEntity?

    @Query("SELECT * FROM speciesDescription WHERE idOfSpecies = :id")
    fun querySpeciesDescriptionBySpeciesId(id: String): List<SpeciesDescriptionEntity>

    @Query("SELECT * FROM pokemon WHERE id = :id")
    fun queryPokemonEntityById(id: String): PokemonEntity?

    @Query("SELECT * FROM pokemon WHERE pokemon.id IN (SELECT idOfPokemon FROM pokemonTypesRelationship WHERE type = :type)")
    fun queryPokemonEntityListByType(type: String): List<PokemonEntity>


    @Query("SELECT pokemon.id AS id, pokemon.name AS name, pokemon.posterUrl AS posterUrl, idOfSpecies, pokemonTypesRelationship.type AS type FROM pokemon, pokemonTypesRelationship WHERE pokemon.id = pokemonTypesRelationship.idOfPokemon")
    fun queryPokemonTypePairList(): List<PokemonWithTypeEntity>

    @Query("SELECT pokemon.id AS id, pokemon.name AS name, pokemon.posterUrl AS posterUrl, idOfSpecies, pokemonTypesRelationship.type AS type FROM pokemon, pokemonTypesRelationship WHERE pokemon.id = pokemonTypesRelationship.idOfPokemon")
    fun queryPokemonTypePairListLiveData(): LiveData<List<PokemonWithTypeEntity>>

    @Query("SELECT * FROM pokemonTypesRelationship")
    fun queryPokemonTypes(): List<PokemonTypesRelationshipEntity>

    @Query("SELECT type FROM pokemonTypesRelationship WHERE idOfPokemon = :id")
    fun queryTypesOfPokemon(id: String): List<String>

    @Query("SELECT DISTINCT type FROM pokemonTypesRelationship order by type ASC")
    fun queryTypes(): List<String>

    @Query("SELECT id, name, posterUrl, idOfSpecies FROM pokemon JOIN capture WHERE pokemon.id = capture.idOfPokemon ORDER BY capture.timeStamp DESC")
    fun queryCapturePokemonList(): List<PokemonEntity>

    @Query("SELECT * FROM capture WHERE capture.idOfPokemon = :id")
    fun queryCaptureEntity(id: String): CaptureEntity?


    @Query("SELECT * FROM capture")
    fun queryCaptureList(): List<CaptureEntity>

    @Query("SELECT * FROM capture")
    fun queryCaptureListLiveData(): LiveData<List<CaptureEntity>>
}