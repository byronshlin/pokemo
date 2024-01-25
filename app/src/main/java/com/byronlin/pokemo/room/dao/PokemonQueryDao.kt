package com.byronlin.pokemo.room.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.byronlin.pokemo.room.entity.PokemonEntity
import com.byronlin.pokemo.room.entity.PokemonTypesRelationshipEntity
import com.byronlin.pokemo.room.entity.PokemonWithTypeEntity
import com.byronlin.pokemo.room.entity.SpeciesDescriptionEntity
import com.byronlin.pokemo.room.entity.SpeciesEntity


@Dao
interface PokemonQueryDao {


    @Query("SELECT next FROM pokemon_load ORDER BY lastTimeStamp DESC LIMIT 1")
    fun queryNext(): Int?

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


    @Query("SELECT pokemon.id AS id, pokemon.name AS name, pokemon.posterUrl AS posterUrl, idOfSpecies,  pokemon.captured As captured, pokemonTypesRelationship.type AS type FROM pokemon, pokemonTypesRelationship WHERE pokemon.id = pokemonTypesRelationship.idOfPokemon")
    fun queryPokemonTypePairList(): List<PokemonWithTypeEntity>

    @Query("SELECT pokemon.id AS id, pokemon.name AS name, pokemon.posterUrl AS posterUrl, idOfSpecies,  pokemon.captured As captured, pokemonTypesRelationship.type AS type FROM pokemon, pokemonTypesRelationship WHERE pokemon.id = pokemonTypesRelationship.idOfPokemon AND type IN (:types)")
    fun queryPokemonTypePairListByTypes(types: Array<String>): List<PokemonWithTypeEntity>

    @Query("SELECT * FROM pokemonTypesRelationship")
    fun queryPokemonTypes(): List<PokemonTypesRelationshipEntity>

    @Query("SELECT type FROM pokemonTypesRelationship WHERE idOfPokemon = :id")
    fun queryTypesOfPokemon(id: String): List<String>

    @Query("SELECT DISTINCT type FROM pokemonTypesRelationship")
    fun queryTypes(): List<String>

    @Query("SELECT * FROM pokemon WHERE captured = 1")
    fun queryCapturePokemonList() : List<PokemonEntity>


    @Query("SELECT * FROM pokemon WHERE id = :id")
    fun queryPokemonEntityLiveDataById(id: String): LiveData<PokemonEntity>

}