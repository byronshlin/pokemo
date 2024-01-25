package com.byronlin.pokemo.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.byronlin.pokemo.repository.PokemonResourceLoader
import com.byronlin.pokemo.utils.PKLog
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(private val pokemonResourceLoader: PokemonResourceLoader): ViewModel() {
    private val TAG = "MainActivityViewModel"
    fun startLoadAllResource(context: Context) {
        viewModelScope.launch {
            val flow = pokemonResourceLoader.startLoadResourceToLocalAsFlow(context)
            flow.collect { offset ->
                    PKLog.v(TAG, "startLoadAllResource: emit!! = ${offset}")
                }
        }
    }

}