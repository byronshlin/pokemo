package com.byronlin.pokemo.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.byronlin.pokemo.model.PokemonCollectionDisplayItem
import com.byronlin.pokemo.model.PokemonDisplayItem
import com.byronlin.pokemo.repository.PokemonResourceLoader
import com.byronlin.pokemo.repository.PokemonRoomRepository
import com.byronlin.pokemo.room.entity.PokemonWithTypeEntity
import com.byronlin.pokemo.utils.PKLog
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


@HiltViewModel
class MainFragmentViewModel @Inject constructor(
    private val pokemonRoomRepository: PokemonRoomRepository
) : ViewModel() {
    private val TAG = "MainFragmentViewModel"


    private val allDataLiveData: LiveData<List<PokemonWithTypeEntity>> =
        pokemonRoomRepository.queryPokemonTypePairListLiveData()

    val newCollectionListLiveData: LiveData<List<PokemonCollectionDisplayItem>> =
        allDataLiveData.switchMap {
            val newLiveData: MutableLiveData<List<PokemonCollectionDisplayItem>> = MutableLiveData()
            viewModelScope.launch {
                val list  = withContext(Dispatchers.IO){
                    val dataMap = generateAllDataMap(it)
                    val capturedPokemonCollection = generatePokemonDisplayItem()
                    dataMap[MY_POKEMON] = capturedPokemonCollection
                    transferAllDataMapToCollectionList(dataMap)
                }
                newLiveData.value = list

                PKLog.v(TAG, "newCollectionListLiveData: emit!! = ${list.size}")
            }
            newLiveData
        }


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
            val begin = System.currentTimeMillis()
            val dataMap: Map<String, PokemonCollectionDisplayItem> = generateCollectionsByBatch() //for All
            PKLog.v(
                "MainFragmentViewModel",
                "generateCollectionsByBatch: spend = ${System.currentTimeMillis() - begin}"
            )
            val collectionList = transferAllDataMapToCollectionList(dataMap)
            _TypeAndCollectionMapLiveData.value = dataMap
            _collectionsLiveData.value = collectionList
        }
    }


    private fun transferAllDataMapToCollectionList(dataMap: Map<String, PokemonCollectionDisplayItem>): List<PokemonCollectionDisplayItem> {
        val typeSet = dataMap.keys
        val sortedTypeList = typeSet.toMutableList().apply {
            remove(MY_POKEMON)
            sort()
        }
        sortedTypeList.add(0, MY_POKEMON)

        val list = sortedTypeList.map {
            dataMap[it]
        }.filterNotNull()
        return list
    }


    private fun generateAllDataMap(pokemonWithTypeEntityList: List<PokemonWithTypeEntity>): MutableMap<String, PokemonCollectionDisplayItem> {
        val dataMap: MutableMap<String, PokemonCollectionDisplayItem> = mutableMapOf()
        val list: List<PokemonWithTypeEntity> = pokemonWithTypeEntityList


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
        return dataMap
    }

    private fun generatePokemonDisplayItem(): PokemonCollectionDisplayItem {
        val capturedPokemonCollection = pokemonRoomRepository.queryCapturePokemonList()
            .map {
                PokemonDisplayItem(
                    it.id, it.name, it.posterUrl,
                    it.captured == 1
                )
            }.let {
                PokemonCollectionDisplayItem(MY_POKEMON, it.toMutableList(), true)
            }
        return capturedPokemonCollection
    }

    private suspend fun generateCollectionsByBatch() =
        withContext(Dispatchers.IO) {
            val dataMap = generateAllDataMap(pokemonRoomRepository.queryPokemonTypePairList())
            val capturedPokemonCollection = generatePokemonDisplayItem()
            dataMap[MY_POKEMON] = capturedPokemonCollection
            return@withContext dataMap
        }

    fun catchPokemon(context: Context, id: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                pokemonRoomRepository.catchPokemon(id)
            }
            val dataMap = _TypeAndCollectionMapLiveData.value?.toMutableMap() ?: mutableMapOf()
            val originalList = _collectionsLiveData.value ?: arrayListOf()
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
            val originalList = _collectionsLiveData.value ?: arrayListOf()
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









