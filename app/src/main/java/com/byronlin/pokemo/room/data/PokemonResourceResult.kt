package com.byronlin.pokemo.room.data

class PokemonResourceResult(
    val pokemonInfoList : List<PokemonInfo>,
    val speciesInfoList : List<SpeciesInfo>,
    val next : Int
)