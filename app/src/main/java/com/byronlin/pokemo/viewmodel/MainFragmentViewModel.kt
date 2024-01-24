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
import com.byronlin.pokemo.room.entity.PokemonWithTypeEntity
import com.byronlin.pokemo.utils.PKLog
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

    private var isLoading = false

    fun initMainViews(context: Context) {
        viewModelScope.launch {

            val types = withContext(Dispatchers.IO) {
                pokemonRoomRepository.queryTypes()
            }
            _pokemonTypesLiveData.value = types

            val visibleTypeList = mutableListOf<String>().apply {
                add(MY_POKEMON)
                addAll(types)
            }

            _visibleTypesLiveData.value = visibleTypeList

            val begin = System.currentTimeMillis()
            val dataMap: Map<String, PokemonCollectionDisplayItem> =
                //generateCollections(visibleTypeList)
                generateCollectionsByBatch(null) //for All
            PKLog.v("MainFragmentViewModel", "generateCollectionsByBatch: spend = ${System.currentTimeMillis() - begin}")

            val list = visibleTypeList.map {
                dataMap[it]
            }.filterNotNull() ?: arrayListOf()

            _visibleCollectionOfTypes.value = dataMap
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
                                Pair(type, PokemonCollectionDisplayItem(type, it.toMutableList(), true))
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
                                Pair(type, PokemonCollectionDisplayItem(type, it.toMutableList(), false))
                            }
                    }
                }
            }.toMap()
        }

    private suspend fun generateCollectionsByBatch(visibleTypeList: List<String>? = null)  = withContext(Dispatchers.IO) {
        val dataMap : MutableMap<String, PokemonCollectionDisplayItem> = mutableMapOf()
        if (visibleTypeList == null) {
            val list: List<PokemonWithTypeEntity> = pokemonRoomRepository.queryPokemonTypePairList()

            list.forEach {pokemonWithTypeEntity ->
                dataMap[pokemonWithTypeEntity.type]?.also {pokemonCollectionDisplayItem->
                    pokemonCollectionDisplayItem.pokemonItemList.add(
                        PokemonDisplayItem(pokemonWithTypeEntity.id, pokemonWithTypeEntity.name, pokemonWithTypeEntity.posterUrl, pokemonWithTypeEntity.captured == 1))
                }?:run {
                    val item = PokemonCollectionDisplayItem(pokemonWithTypeEntity.type,
                        arrayListOf(),false)
                    dataMap[pokemonWithTypeEntity.type] = item
                    item.pokemonItemList.add(
                        PokemonDisplayItem(
                            pokemonWithTypeEntity.id,
                            pokemonWithTypeEntity.name,
                            pokemonWithTypeEntity.posterUrl,
                            pokemonWithTypeEntity.captured == 1))
                }
            }
        } else {
            val list: List<PokemonWithTypeEntity> =
                pokemonRoomRepository.queryPokemonTypePairListByTypes(visibleTypeList)
            list.forEach { pokemonWithTypeEntity ->
                dataMap[pokemonWithTypeEntity.type]?.also { pokemonCollectionDisplayItem ->
                    pokemonCollectionDisplayItem.pokemonItemList.add(
                        PokemonDisplayItem(
                            pokemonWithTypeEntity.id,
                            pokemonWithTypeEntity.name,
                            pokemonWithTypeEntity.posterUrl,
                            pokemonWithTypeEntity.captured == 1
                        )
                    )
                } ?: run {
                    val item = PokemonCollectionDisplayItem(
                        pokemonWithTypeEntity.type,
                        arrayListOf(), false
                    )
                    dataMap[pokemonWithTypeEntity.type] = item
                    item.pokemonItemList.add(
                        PokemonDisplayItem(
                            pokemonWithTypeEntity.id,
                            pokemonWithTypeEntity.name,
                            pokemonWithTypeEntity.posterUrl,
                            pokemonWithTypeEntity.captured == 1
                        )
                    )
                }
            }
        }

        pokemonRoomRepository.queryCapturePokemonList()
            .map {
                PokemonDisplayItem(
                    it.id, it.name, it.posterUrl,
                    it.captured == 1
                )
            }.let {
                PokemonCollectionDisplayItem(MY_POKEMON, it.toMutableList(), true)
            }.also {
                dataMap[MY_POKEMON] = it
            }
        return@withContext dataMap
    }
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
}









