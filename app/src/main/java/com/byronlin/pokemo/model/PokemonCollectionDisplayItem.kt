package com.byronlin.pokemo.model

data class PokemonCollectionDisplayItem(val type: String,
                                   val pokemonItemList: MutableList<PokemonDisplayItem>,
        val isMyPokemon: Boolean = false)