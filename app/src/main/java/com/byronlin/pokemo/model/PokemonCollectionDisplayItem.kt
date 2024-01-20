package com.byronlin.pokemo.model

data class PokemonCollectionDisplayItem(val type: String,
                                   val title: String,
                                   val pokemonItemList: List<PokemonDisplayItem>)