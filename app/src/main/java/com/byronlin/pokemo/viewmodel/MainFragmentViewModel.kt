package com.byronlin.pokemo.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.byronlin.pokemo.model.PokemonCollectionDisplayItem
import com.byronlin.pokemo.model.PokemonDisplayItem
import com.byronlin.pokemo.room.PokemonRoomHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainFragmentViewModel : ViewModel() {

    private val INIT_TYPE_NUMBER = 6

    private val _collectionsLiveData: MutableLiveData<List<PokemonCollectionDisplayItem>> =
        MutableLiveData()

    private val _pokemonTypesLiveData: MutableLiveData<List<String>> = MutableLiveData()

    private val _visibleCollectionOfTypes: MutableLiveData<Map<String, PokemonCollectionDisplayItem>> =
        MutableLiveData()

    val collectionsLiveData: LiveData<List<PokemonCollectionDisplayItem>> = _collectionsLiveData
    val pokemonTypesLiveData: LiveData<List<String>> = _pokemonTypesLiveData

    val visibleMainCollectionOfTypes: LiveData<Map<String, PokemonCollectionDisplayItem>> =
        _visibleCollectionOfTypes

    val _visibleTypes: MutableLiveData<List<String>> = MutableLiveData()

    val visibleTypes: LiveData<List<String>> = _visibleTypes


    fun initMainViews(context: Context) {
        viewModelScope.launch {
            val types = withContext(Dispatchers.IO) {
                PokemonRoomHelper.obtainPokemonDatabase(context).queryDao().queryTypes()
            }

            _pokemonTypesLiveData.value = types
            _visibleTypes.value = types.take(INIT_TYPE_NUMBER)

            _visibleTypes.value?.let { typeList ->
                withContext(Dispatchers.IO) {
                    typeList.map { type ->
                        PokemonRoomHelper.obtainPokemonDatabase(context).queryDao()
                            .queryPokemonEntityListByType(type)
                            .map {
                                PokemonDisplayItem(it.id, it.name, it.posterUrl)
                            }.let {
                                Pair(type, PokemonCollectionDisplayItem(type, it))
                            }
                    }
                }
            }?.toMap().let {
                _visibleCollectionOfTypes.value = it
            }

            val list = _visibleTypes.value?.map {
                _visibleCollectionOfTypes.value?.get(it)
            }?.filterNotNull() ?: arrayListOf()

            //
            _collectionsLiveData.value = list
        }
    }

    fun updateTypesOfCollections(context: Context) {
        viewModelScope.launch {
            val types = withContext(Dispatchers.IO) {
                PokemonRoomHelper.obtainPokemonDatabase(context).queryDao().queryTypes()
            }
            _pokemonTypesLiveData.value = types
        }
    }


    fun updateMainCollections(context: Context) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val dao = PokemonRoomHelper.obtainPokemonDatabase(context).queryDao()
                _visibleTypes.value?.map {
                    Pair(it, dao.queryPokemonEntityListByType(it))
                }
            }
        }
    }


}









