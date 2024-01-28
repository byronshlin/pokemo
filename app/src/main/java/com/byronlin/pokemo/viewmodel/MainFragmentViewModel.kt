package com.byronlin.pokemo.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.byronlin.pokemo.model.MY_POKEMON
import com.byronlin.pokemo.model.PokemonCollectionDisplayItem
import com.byronlin.pokemo.model.PokemonDisplayItem
import com.byronlin.pokemo.repository.PokemonRoomRepository
import com.byronlin.pokemo.room.entity.CaptureEntity
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

    private val capturedListLiveData: LiveData<List<CaptureEntity>> =
        pokemonRoomRepository.queryCaptureListLiveData()


    private val allDataLiveData: LiveData<List<PokemonWithTypeEntity>> =
        pokemonRoomRepository.queryPokemonTypePairListLiveData()


    var cacheRecyclerScrollY = 0
    var cacheCollectionScrollState : Map<String, Pair<Int, Int>>? = mutableMapOf()


    private val newCollectionListLiveData: LiveData<List<PokemonCollectionDisplayItem>> =
        allDataLiveData.switchMap {
            val newLiveData: MutableLiveData<List<PokemonCollectionDisplayItem>> = MutableLiveData()
            viewModelScope.launch {
                val list = withContext(Dispatchers.IO) {
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


    val mainViewUILiveData: MediatorLiveData<List<PokemonCollectionDisplayItem>> =
        MediatorLiveData()

    init {

        mainViewUILiveData.addSource(newCollectionListLiveData) {
            mainViewUILiveData.value = it
        }
        mainViewUILiveData.addSource(capturedListLiveData) {
            val baseList = newCollectionListLiveData.value?.toMutableList() ?: arrayListOf()
            viewModelScope.launch {
                updateMyPocket(baseList).also {
                    mainViewUILiveData.value = it
                }
            }
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
                val newOne = PokemonCollectionDisplayItem(
                    pokemonCollectionDisplayItem.type,
                    pokemonCollectionDisplayItem.pokemonItemList.toMutableList().apply {
                        add(
                            PokemonDisplayItem(
                                pokemonWithTypeEntity.id,
                                pokemonWithTypeEntity.name,
                                pokemonWithTypeEntity.posterUrl,
                                pokemonWithTypeEntity.type == MY_POKEMON
                            )
                        )
                    },
                    pokemonCollectionDisplayItem.isMyPokemon
                )
                dataMap[pokemonWithTypeEntity.type] = newOne
            } ?: run {
                val item = PokemonCollectionDisplayItem(
                    pokemonWithTypeEntity.type,
                    arrayListOf<PokemonDisplayItem>().apply {
                        add(
                            PokemonDisplayItem(
                                pokemonWithTypeEntity.id,
                                pokemonWithTypeEntity.name,
                                pokemonWithTypeEntity.posterUrl,
                                pokemonWithTypeEntity.type == MY_POKEMON
                            )
                        )
                    }, false
                )
                dataMap[pokemonWithTypeEntity.type] = item
            }
        }
        return dataMap
    }

    private fun generatePokemonDisplayItem(): PokemonCollectionDisplayItem {
        val capturedPokemonCollection = pokemonRoomRepository.queryCapturePokemonList()
            .map {
                PokemonDisplayItem(
                    it.id, it.name, it.posterUrl,
                    true
                )
            }.let {
                PokemonCollectionDisplayItem(MY_POKEMON, it.toMutableList(), true)
            }
        return capturedPokemonCollection
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
    private suspend fun updateMyPocket(
        originalList: List<PokemonCollectionDisplayItem>
    ) = withContext(Dispatchers.IO) {

        val newCollectionList = originalList.toMutableList()

        val list = pokemonRoomRepository.queryCapturePokemonList().map {
            PokemonDisplayItem(it.id, it.name, it.posterUrl, true)
        }

        val newPokemonCollection = PokemonCollectionDisplayItem(
            MY_POKEMON,
            list,
            true
        )

        newCollectionList.removeIf {
            it.type == MY_POKEMON
        }
        newCollectionList.add(0, newPokemonCollection)

        return@withContext newCollectionList
    }
}









