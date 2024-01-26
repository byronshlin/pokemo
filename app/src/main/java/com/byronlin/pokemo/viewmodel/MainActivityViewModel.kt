package com.byronlin.pokemo.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.byronlin.pokemo.repository.PokemonResourceLoader
import com.byronlin.pokemo.repository.PokemonRoomRepository
import com.byronlin.pokemo.utils.PKLog
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val pokemonRoomRepository: PokemonRoomRepository,
    private val pokemonResourceLoader: PokemonResourceLoader
) :
    ViewModel() {
    private val TAG = "MainActivityViewModel"


    private val _loadStatusLiveData: MutableLiveData<LoadStatus> = MutableLiveData()

    val loadStatusLiveData: LiveData<LoadStatus> = _loadStatusLiveData

    fun startLoadAllResource(context: Context) {
        val job = viewModelScope.launch {
            _loadStatusLiveData.postValue(LoadStatus.START)

            val hasData = withContext(Dispatchers.IO) {
                pokemonRoomRepository.checkNotEmpty()
            }

            if (hasData) {
                _loadStatusLiveData.postValue(LoadStatus.FIRST_LOADED)
            }

            val flow = pokemonResourceLoader.startLoadResourceToLocalAsFlow()
            flow.collect { offset ->
                PKLog.v(TAG, "startLoadAllResource: emit!! = ${offset}")

                if (offset == -1) {
                    _loadStatusLiveData.postValue(LoadStatus.COMPLETE)
                } else {
                    _loadStatusLiveData.postValue(LoadStatus.FIRST_LOADED)
                }
            }
        }
//        job.invokeOnCompletion {
//            pokemonResourceLoader.stop()
//        }
    }

    fun stopLoad(){
        pokemonResourceLoader.stop()
    }
    override fun onCleared() {
        super.onCleared()
        PKLog.v(TAG, "onCleared")
    }

    enum class LoadStatus {
        START,
        FIRST_LOADED,
        COMPLETE
    }
}



