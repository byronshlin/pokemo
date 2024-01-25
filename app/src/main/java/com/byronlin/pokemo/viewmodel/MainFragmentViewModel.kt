package com.byronlin.pokemo.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.byronlin.pokemo.model.PokemonCollectionDisplayItem
import com.byronlin.pokemo.model.PokemonDisplayItem
import com.byronlin.pokemo.repository.PokemonResourceLoader
import com.byronlin.pokemo.repository.PokemonRoomRepository
import com.byronlin.pokemo.room.entity.PokemonWithTypeEntity
import com.byronlin.pokemo.utils.PKLog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainFragmentViewModel(
    private val pokemonRoomRepository: PokemonRoomRepository,
    private val pokemonResourceLoader: PokemonResourceLoader
) : ViewModel() {
    private val TAG = "MainFragmentViewModel"


    private val _collectionsLiveData: MutableLiveData<List<PokemonCollectionDisplayItem>> =
        MutableLiveData()


    private val _TypeAndCollectionMapLiveData: MutableLiveData<Map<String, PokemonCollectionDisplayItem>> =
        MutableLiveData()

    val collectionsLiveData: LiveData<List<PokemonCollectionDisplayItem>> = _collectionsLiveData

    private val MY_POKEMON = "My Pokemon"

    private val _loadCompleteLiveData: MutableLiveData<Boolean> = MutableLiveData()

    val loadCompleteLiveData: LiveData<Boolean> = _loadCompleteLiveData

    private var isLoading = false

    fun initMainViews() {
        viewModelScope.launch {
            val typeList = withContext(Dispatchers.IO) {
                pokemonRoomRepository.queryTypes()
            }

            val visibleTypeList = mutableListOf<String>().apply {
                add(MY_POKEMON)
                addAll(typeList)
            }
            val begin = System.currentTimeMillis()
            val dataMap: Map<String, PokemonCollectionDisplayItem> =
                generateCollectionsByBatch() //for All
            PKLog.v(
                "MainFragmentViewModel",
                "generateCollectionsByBatch: spend = ${System.currentTimeMillis() - begin}"
            )
            val list = visibleTypeList.map {
                dataMap[it]
            }.filterNotNull() ?: arrayListOf()

            _TypeAndCollectionMapLiveData.value = dataMap
            _collectionsLiveData.value = list
        }
    }

    private suspend fun generateCollections(context: Context, visibleTypeList: List<String>) =
        withContext(Dispatchers.IO) {
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
                                Pair(
                                    type,
                                    PokemonCollectionDisplayItem(type, it.toMutableList(), true)
                                )
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
                                Pair(
                                    type,
                                    PokemonCollectionDisplayItem(type, it.toMutableList(), false)
                                )
                            }
                    }
                }
            }.toMap()
        }

    private suspend fun generateCollectionsByBatch() =
        withContext(Dispatchers.IO) {
            val dataMap: MutableMap<String, PokemonCollectionDisplayItem> = mutableMapOf()
            val list: List<PokemonWithTypeEntity> = pokemonRoomRepository.queryPokemonTypePairList()

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

    fun startLoadAllResource(context: Context) {
        viewModelScope.launch {
            pokemonResourceLoader.startLoadResourceToLocalAsFlow(context)
                .collect { offset ->
                    PKLog.v(TAG, "startLoadAllResource: emit!! = ${offset}")
                    if (offset > 0) {
                        _loadCompleteLiveData.value = true
                    }
                }
        }
    }

    fun catchPokemon(context: Context, id: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                pokemonRoomRepository.catchPokemon(id)
            }
            val dataMap = _TypeAndCollectionMapLiveData.value?.toMutableMap() ?: mutableMapOf()
            val originalList = _collectionsLiveData.value?: arrayListOf()
            updateMyPocket(dataMap, originalList).also {
                _TypeAndCollectionMapLiveData.value = it.first!!
                _collectionsLiveData.value = it.second!!
            }
        }
    }

    fun releasePokemon(context: Context, id: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                pokemonRoomRepository.releasePokemon(id)
            }
            val dataMap = _TypeAndCollectionMapLiveData.value?.toMutableMap() ?: mutableMapOf()
            val originalList = _collectionsLiveData.value?: arrayListOf()
            updateMyPocket(dataMap, originalList).also {
                _TypeAndCollectionMapLiveData.value = it.first!!
                _collectionsLiveData.value = it.second!!
            }
        }
    }


    private suspend fun updateMyPocket(
        originalMap: Map<String, PokemonCollectionDisplayItem>,
        originalList: List<PokemonCollectionDisplayItem>
    ) = withContext(Dispatchers.IO) {
        val pair: Pair<Map<String, PokemonCollectionDisplayItem>, List<PokemonCollectionDisplayItem>> =
            run {
                val list = pokemonRoomRepository.queryCapturePokemonList().map {
                    PokemonDisplayItem(
                        it.id, it.name, it.posterUrl,
                        it.captured == 1
                    )
                }

                val dataMap = originalMap.toMutableMap()

                dataMap.get(MY_POKEMON)?.also {
                    it.pokemonItemList.clear()
                    it.pokemonItemList.addAll(list)
                } ?: run {
                    val item = PokemonCollectionDisplayItem(MY_POKEMON, list.toMutableList(), true)
                    dataMap[MY_POKEMON] = item
                }


                var myPocket = originalList?.find {
                    it.type == MY_POKEMON
                }
                if (myPocket != null) {
                    myPocket.pokemonItemList.clear()
                    myPocket.pokemonItemList.addAll(list)
                } else {
                    myPocket = PokemonCollectionDisplayItem(MY_POKEMON, list.toMutableList(), true)
                }

                val newList = originalList?.toMutableList()?.apply {
                    this.remove(myPocket)
                    add(0, myPocket)
                } ?: run {
                    arrayListOf<PokemonCollectionDisplayItem>().apply {
                        add(myPocket)
                    }
                }
                Pair(dataMap, newList)
            }
        return@withContext pair
    }
}









