package com.byronlin.pokemo.repository

import android.content.Context
import com.byronlin.pokemo.model.PokemonDisplayItem
import com.byronlin.pokemo.room.PokemonRoomHelper

class PokemonRepository {

    fun queryPokemonDisplayItemListByType(context: Context, type: String) {
        val queryDao = PokemonRoomHelper.obtainPokemonDatabase(context).queryDao()

        var list = queryDao.queryPokemonEntityListByType(type).value

        list?.map {
            PokemonDisplayItem(it.id, it.name, it.posterUrl)
        }?: arrayListOf()
    }
}