package com.byronlin.pokemo.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.byronlin.pokemo.model.PokemonCollectionDisplayItem
import com.byronlin.pokemo.model.PokemonDisplayItem
import com.byronlin.pokemo.repository.PokemonResourceLoader
import com.byronlin.pokemo.room.PokemonRoomHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainFragmentViewModel : ViewModel() {



    private val INIT_TYPE_NUMBER = Int.MAX_VALUE

    private val pokemonRoomHelper : PokemonRoomHelper = PokemonRoomHelper()
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


    private val MY_POKEMON = "My Pokemon"

    private val pokemonResourceLoader = PokemonResourceLoader()
    private val _loadCompleteLiveData: MutableLiveData<Boolean> = MutableLiveData()

    val loadCompleteLiveData: LiveData<Boolean> = _loadCompleteLiveData



    fun initMainViews(context: Context) {
        viewModelScope.launch {
            val types = withContext(Dispatchers.IO) {
                pokemonRoomHelper.obtainPokemonDatabase(context).queryDao().queryTypes()
            }

            _pokemonTypesLiveData.value = types
            _visibleTypes.value =
                mutableListOf<String>().apply {
                    add(MY_POKEMON)
                    addAll(types.take(INIT_TYPE_NUMBER))
                }

            _visibleTypes.value?.let { typeList ->
                withContext(Dispatchers.IO) {
                    typeList.map { type ->
                        if (type == MY_POKEMON) {
                            pokemonRoomHelper.obtainPokemonDatabase(context).queryDao()
                                .queryCapturePokemonList()
                                .map {
                                    PokemonDisplayItem(it.id, it.name, it.posterUrl,
                                        it.captured == 1
                                    )
                                }.let {
                                    Pair(type, PokemonCollectionDisplayItem(type, it, true))
                                }
                        } else {
                            pokemonRoomHelper.obtainPokemonDatabase(context).queryDao()
                                .queryPokemonEntityListByType(type)
                                .map {
                                    PokemonDisplayItem(it.id, it.name, it.posterUrl, it.captured == 1)
                                }.let {
                                    Pair(type, PokemonCollectionDisplayItem(type, it, false))
                                }
                        }
                    }
                }
            }?.toMap().let {
                _visibleCollectionOfTypes.value = it
            }
            val list = _visibleTypes.value?.map {
                _visibleCollectionOfTypes.value?.get(it)
            }?.filterNotNull() ?: arrayListOf()
            _collectionsLiveData.value = list
        }
    }

    fun updateTypesOfCollections(context: Context) {
        viewModelScope.launch {
            val types = withContext(Dispatchers.IO) {
                pokemonRoomHelper.obtainPokemonDatabase(context).queryDao().queryTypes()
            }
            _pokemonTypesLiveData.value = types
        }
    }


    fun updateMainCollections(context: Context) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val dao = pokemonRoomHelper.obtainPokemonDatabase(context).queryDao()
                _visibleTypes.value?.map {
                    Pair(it, dao.queryPokemonEntityListByType(it))
                }
            }
        }
    }


    fun startLoadResource(context: Context){
        viewModelScope.launch {
            val action = withContext(Dispatchers.IO) {
                pokemonResourceLoader.loadResourceToLocalOnce(context, 30)
            }
            if (action) {
                _loadCompleteLiveData.value = true
            }
        }
    }


    fun catchPokemon(context: Context, id: String){
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                pokemonRoomHelper.obtainPokemonDatabase(context).updateDao().catchPokemon(id)
            }
        }
    }

    fun releasePokemon(context: Context, id: String){
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                pokemonRoomHelper.obtainPokemonDatabase(context).updateDao().releasePokemon(id)
            }
        }
    }


}









