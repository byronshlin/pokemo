package com.byronlin.pokemo.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.byronlin.pokemo.model.PokemonCollectionDisplayItem
import com.byronlin.pokemo.model.PokemonDisplayItem
import com.byronlin.pokemo.repository.PokemonResourceLoader
import com.byronlin.pokemo.repository.PokemonRoomRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainFragmentViewModel(
    private val pokemonRoomRepository: PokemonRoomRepository,
    private val pokemonResourceLoader: PokemonResourceLoader
) : ViewModel() {


    private val INIT_TYPE_NUMBER = Int.MAX_VALUE

    private val _collectionsLiveData: MutableLiveData<List<PokemonCollectionDisplayItem>> =
        MutableLiveData()

    private val _pokemonTypesLiveData: MutableLiveData<List<String>> = MutableLiveData()

    private val _visibleCollectionOfTypes: MutableLiveData<Map<String, PokemonCollectionDisplayItem>> =
        MutableLiveData()

    val collectionsLiveData: LiveData<List<PokemonCollectionDisplayItem>> = _collectionsLiveData
    val pokemonTypesLiveData: LiveData<List<String>> = _pokemonTypesLiveData

    val visibleMainCollectionOfTypes: LiveData<Map<String, PokemonCollectionDisplayItem>> =
        _visibleCollectionOfTypes

    val _visibleTypesLiveData: MutableLiveData<List<String>> = MutableLiveData()

    val visibleTypesLiveData: LiveData<List<String>> = _visibleTypesLiveData

    private val MY_POKEMON = "My Pokemon"

    private val _loadCompleteLiveData: MutableLiveData<Boolean> = MutableLiveData()

    val loadCompleteLiveData: LiveData<Boolean> = _loadCompleteLiveData

    fun initMainViews(context: Context) {
        viewModelScope.launch {
            val types = withContext(Dispatchers.IO) {
                pokemonRoomRepository.queryTypes()
            }
            _pokemonTypesLiveData.value = types
            _visibleTypesLiveData.value =
                mutableListOf<String>().apply {
                    add(MY_POKEMON)
                    addAll(types.take(INIT_TYPE_NUMBER))
                }

            val visibleTypeList = _visibleTypesLiveData.value

            val map: Map<String, PokemonCollectionDisplayItem> = visibleTypeList?.let { generateCollections(context, it) }?: emptyMap()

            _visibleCollectionOfTypes.value = map

            val list = _visibleTypesLiveData.value?.map {
                _visibleCollectionOfTypes.value?.get(it)
            }?.filterNotNull() ?: arrayListOf()
            _collectionsLiveData.value = list
        }
    }

    private suspend fun generateCollections(context: Context, visibleTypeList: List<String>)  = withContext(Dispatchers.IO) {
            visibleTypeList.let { typeList ->
                typeList.map { type ->
                    if (type == MY_POKEMON) {
                        pokemonRoomRepository.queryCapturePokemonList()
                            .map {
                                PokemonDisplayItem(
                                    it.id, it.name, it.posterUrl,
                                    it.captured == 1
                                )
                            }.let {
                                Pair(type, PokemonCollectionDisplayItem(type, it, true))
                            }
                    } else {
                        pokemonRoomRepository.queryPokemonEntityListByType(type)
                            .map {
                                PokemonDisplayItem(
                                    it.id,
                                    it.name,
                                    it.posterUrl,
                                    it.captured == 1
                                )
                            }.let {
                                Pair(type, PokemonCollectionDisplayItem(type, it, false))
                            }
                    }
                }
            }.toMap()
        }


    fun updateTypesOfCollections(context: Context) {
        viewModelScope.launch {
            val types = withContext(Dispatchers.IO) {
                pokemonRoomRepository.queryTypes()
            }
            _pokemonTypesLiveData.value = types
        }
    }


    fun updateMainCollections(context: Context) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                _visibleTypesLiveData.value?.map {
                    Pair(it, pokemonRoomRepository.queryPokemonEntityListByType(it))
                }
            }
        }
    }


    private var isLoading = false
    fun startLoadResource(context: Context, continueLoad: Boolean) {
        viewModelScope.launch {

            if (isLoading) {
                return@launch
            }

            isLoading = true
            val action = withContext(Dispatchers.IO) {
                pokemonResourceLoader.loadResourceToLocalOnce(context, 20, continueLoad)
            }
            if (action) {
                _loadCompleteLiveData.value = true
            }
            isLoading = false
        }
    }


    fun catchPokemon(context: Context, id: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                pokemonRoomRepository.catchPokemon(id)
            }
        }
    }

    fun releasePokemon(context: Context, id: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                pokemonRoomRepository.releasePokemon(id)
            }
        }
    }



    fun startDownloadPokemonResource(context: Context) {
        viewModelScope.launch {
            //val flow =  pokemonResourceLoader.startLoadResourceToLocal2(context)
        }
    }


}









