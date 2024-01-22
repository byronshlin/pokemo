package com.byronlin.pokemo.room.dao

import androidx.room.Dao
import androidx.room.Query
import com.byronlin.pokemo.room.entity.PokemonEntity
import com.byronlin.pokemo.room.entity.PokemonTypesRelationshipEntity
import com.byronlin.pokemo.room.entity.SpeciesEntity


@Dao
interface PokemonQueryDao {


    @Query("SELECT next FROM pokemon_load ORDER BY lastTimeStamp DESC LIMIT 1")
    fun queryNext(): Int?

    @Query("SELECT * FROM pokemon")
    fun queryPokemonEntityList(): List<PokemonEntity>

    @Query("SELECT * FROM species")
    fun querySpeciesEntityList(): List<SpeciesEntity>

    @Query("SELECT * FROM pokemon WHERE id = :id")
    fun queryPokemonEntityById(id: String): PokemonEntity

    @Query("SELECT * FROM pokemon WHERE pokemon.id IN (SELECT idOfPokemon FROM pokemonTypesRelationship WHERE type = :type)")
    fun queryPokemonEntityListByType(type: String): List<PokemonEntity>

    @Query("SELECT * FROM pokemonTypesRelationship")
    fun queryPokemonTypes(): List<PokemonTypesRelationshipEntity>

    @Query("SELECT type FROM pokemonTypesRelationship WHERE idOfPokemon = :id")
    fun queryTypesOfPokemon(id: String): List<String>

    @Query("SELECT DISTINCT type FROM pokemonTypesRelationship")
    fun queryTypes(): List<String>

    @Query("SELECT * FROM pokemon WHERE captured = 1")
    fun queryCapturePokemonList() : List<PokemonEntity>
}