package com.byronlin.pokemo.model

data class PokemonCollectionDisplayItem(val type: String,
                                   val pokemonItemList: List<PokemonDisplayItem>,
        val isMyPokemon: Boolean = false)