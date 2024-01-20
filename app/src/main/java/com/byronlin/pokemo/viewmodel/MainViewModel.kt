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
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel : ViewModel() {

    private val INIT_TYPE_NUMBER = 6

    private val _collectionsLiveData: MutableLiveData<List<PokemonCollectionDisplayItem>> =
        MutableLiveData()

    private val _pokemonTypesLiveData : MutableLiveData<List<String>> = MutableLiveData()

    private val _visibleCollectionOfTypes : MutableLiveData<HashMap<String,List<PokemonCollectionDisplayItem>>> = MutableLiveData()

    val collectionsLiveData: LiveData<List<PokemonCollectionDisplayItem>> = _collectionsLiveData
    val pokemonTypesLiveData : LiveData<List<String>> = _pokemonTypesLiveData

    val visibleMainCollectionOfTypes : LiveData<HashMap<String,List<PokemonCollectionDisplayItem>>> = _visibleCollectionOfTypes

    val _visibleTypes : MutableLiveData<List<String>> = MutableLiveData()

    val visibleTypes : LiveData<List<String>> = _visibleTypes




    fun initMainViews(context: Context){
        viewModelScope.launch {
            val types = withContext(Dispatchers.IO){
                PokemonRoomHelper.obtainPokemonDatabase(context).queryDao().queryPokemonTypes()
            }

            _pokemonTypesLiveData.value = types
            _visibleTypes.value = types.take(INIT_TYPE_NUMBER)

           

            _visibleTypes.value.map {
                Pair(it, withContext(Dispatchers.IO){
                })
            }.toMap().let {
                _visibleCollectionOfTypes.value = it as HashMap<String, List<PokemonCollectionDisplayItem>>
            }
        }
    }

    fun updateTypesOfCollections(context: Context) {
        viewModelScope.launch {
            val types = withContext(Dispatchers.IO){
                PokemonRoomHelper.obtainPokemonDatabase(context).queryDao().queryPokemonTypes()
            }
            _pokemonTypesLiveData.value = types



        }
    }


    fun updateMainCollections(context: Context){
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                val dao = PokemonRoomHelper.obtainPokemonDatabase(context).queryDao()
                _visibleTypes.value?.map {
                    Pair(it, dao.queryPokemonEntityListByType(it))
                }
            }
        }
    }







    fun updateCollections() {
        val dataList = mutableListOf<PokemonCollectionDisplayItem>()
        PokemonCollectionDisplayItem("fire", "Fire", mutableListOf<PokemonDisplayItem>().apply {
            add(
                PokemonDisplayItem(
                    "1",
                    "Bulbasaur",
                    "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/1.png"
                )
            )
            add(
                PokemonDisplayItem(
                    "2",
                    "Ivysaur",
                    "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/2.png"
                )
            )
            add(
                PokemonDisplayItem(
                    "3",
                    "Venusaur",
                    "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/3.png"
                )
            )
        }).also {
            dataList.add(it)
        }

        PokemonCollectionDisplayItem("grass", "Grass", mutableListOf<PokemonDisplayItem>().apply {
            add(
                PokemonDisplayItem(
                    "4",
                    "Charmander",
                    "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/4.png"
                )
            )
            add(
                PokemonDisplayItem(
                    "5",
                    "Charmeleon",
                    "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/5.png"
                )
            )
            add(
                PokemonDisplayItem(
                    "6",
                    "Charizard",
                    "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/6.png"
                )
            )
        }).also {
            dataList.add(it)
        }

        PokemonCollectionDisplayItem("water", "Water", mutableListOf<PokemonDisplayItem>().apply {
            add(
                PokemonDisplayItem(
                    "7",
                    "Squirtle",
                    "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/7.png"
                )
            )
            add(
                PokemonDisplayItem(
                    "8",
                    "Wartortle",
                    "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/8.png"
                )
            )
            add(
                PokemonDisplayItem(
                    "9",
                    "Blastoise",
                    "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/9.png"
                )
            )
        }).also {
            dataList.add(it)
        }

        PokemonCollectionDisplayItem("bug", "Bug", mutableListOf<PokemonDisplayItem>().apply {
            add(
                PokemonDisplayItem(
                    "10",
                    "Caterpie",
                    "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/10.png"
                )
            )
            add(
                PokemonDisplayItem(
                    "11",
                    "Metapod",
                    "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/11.png"
                )
            )
            add(
                PokemonDisplayItem(
                    "12",
                    "Butterfree",
                    "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/12.png"
                )
            )
            add(
                PokemonDisplayItem(
                    "13",
                    "Weedle",
                    "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/13.png"
                )
            )
            add(
                PokemonDisplayItem(
                    "14",
                    "Kakuna",
                    "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/14.png"
                )
            )
            add(
                PokemonDisplayItem(
                    "15",
                    "Beedrill",
                    "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/15.png"
                )
            )
        }).also {
            dataList.add(it)
        }

        PokemonCollectionDisplayItem("normal", "Normal", mutableListOf<PokemonDisplayItem>().apply {
            add(
                PokemonDisplayItem(
                    "16",
                    "Pidgey",
                    "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/16.png"
                )
            )
            add(
                PokemonDisplayItem(
                    "17",
                    "Pidgeotto",
                    "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/17.png"
                )
            )
            add(
                PokemonDisplayItem(
                    "18",
                    "Pidgeot",
                    "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/18.png"
                )
            )
            add(
                PokemonDisplayItem(
                    "19",
                    "Rattata",
                    "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/19.png"
                )
            )
            add(
                PokemonDisplayItem(
                    "20",
                    "Raticate",
                    "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/20.png"
                )
            )
            add(
                PokemonDisplayItem(
                    "21",
                    "Spearow",
                    "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/21.png"
                )
            )
            add(
                PokemonDisplayItem(
                    "22",
                    "Fearow",
                    "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/22.png"
                )
            )
            add(
                PokemonDisplayItem(
                    "23",
                    "Ekans",
                    "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/23.png"
                )
            )
            add(
                PokemonDisplayItem(
                    "24",
                    "Arbok",
                    "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/24.png"
                )
            )
            add(
                PokemonDisplayItem(
                    "25",
                    "Pikachu",
                    "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/25.png"
                )
            )
            add(
                PokemonDisplayItem(
                    "26",
                    "Raichu",
                    "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/26.png"
                )
            )
            add(
                PokemonDisplayItem(
                    "27",
                    "Sandshrew",
                    "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/27.png"
                )
            )
            add(
                PokemonDisplayItem(
                    "28",
                    "Sandslash",
                    "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/28.png"
                )
            )
            add(
                PokemonDisplayItem(
                    "29",
                    "Nidoran-f",
                    "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/29.png"
                )
            )
            add(
                PokemonDisplayItem(
                    "30",
                    "Nidorina",
                    "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/30.png"
                )
            )
        }).also {
            dataList.add(it)
        }
        _collectionsLiveData.postValue(dataList)
    }
}









