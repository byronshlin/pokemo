package com.byronlin.pokemo.room.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.byronlin.pokemo.room.entity.PokemonEntity
import com.byronlin.pokemo.room.entity.SpeciesEntity


@Dao
interface PokemonQueryDao {

    @Query("SELECT * FROM pokemon")
    fun queryPokemonEntityList(): List<PokemonEntity>

    @Query("SELECT * FROM species")
    fun querySpeciesEntityList(): List<SpeciesEntity>

    @Query("SELECT * FROM pokemon WHERE id = :id")
    fun queryPokemonEntityById(id: String): PokemonEntity

    @Query("SELECT * FROM pokemon WHERE id IN (SELECT idOfPokemon FROM pokemonTypes WHERE type = :type)")
    fun queryPokemonEntityListByType(type: String): List<PokemonEntity>


    @Query("SELECT * FROM pokemon WHERE id IN (SELECT idOfPokemon FROM pokemonTypes WHERE type IN(:types))")
    fun queryPokemonEntityListListByTypes(types: Array<String>): List<List<PokemonEntity>>


    @Query("SELECT DINSTICT(pokemonTypes.type) FROM pokemon, pokemonTypes WHERE pokemon.id = pokemonTypes.idOfPokemon")
    fun queryPokemonTypes(): List<String>
}