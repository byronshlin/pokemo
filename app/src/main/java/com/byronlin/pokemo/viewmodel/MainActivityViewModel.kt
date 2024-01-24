package com.byronlin.pokemo.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.byronlin.pokemo.repository.PokemonResourceLoader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivityViewModel: ViewModel() {
    //private val pokemonResourceLoader = PokemonResourceLoader()
    private val _loadCompleteLiveData: MutableLiveData<Boolean> = MutableLiveData()

    val loadCompleteLiveData: LiveData<Boolean> = _loadCompleteLiveData

    fun startLoadResource(context: Context){
//        viewModelScope.launch(Dispatchers.IO) {
//            pokemonResourceLoader.loadResourceToLocalOnce(context, 151)
//        }
    }
}