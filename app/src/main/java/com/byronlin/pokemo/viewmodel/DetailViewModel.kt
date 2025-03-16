package com.byronlin.pokemo.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.byronlin.pokemo.PokemonApplication
import com.byronlin.pokemo.model.PokemonDetails
import com.byronlin.pokemo.repository.PokemonRoomRepository
import com.byronlin.pokemo.room.entity.PokemonEntity
import com.byronlin.pokemo.utils.PKLog
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val roomHelper: PokemonRoomRepository,
    private val dispatcherProvider: ViewModelDispatcherProvider
    ) : ViewModel() {
    private val TAG = "DetailViewModel"

    private val _idStateFlow = MutableStateFlow<String?>(null)
    val pokemonDetailStateFlow : StateFlow<PokemonDetails?> = _idStateFlow.filterNotNull().flatMapLatest { id->
        flow {
            emit(generatePokemonDetails(id))
        }.flowOn(Dispatchers.IO)
    }.stateIn(viewModelScope, started = SharingStarted.WhileSubscribed(), null)

    fun queryPokemonDetail(id: String) {
        viewModelScope.launch {
            _idStateFlow.emit(id)
        }
    }


    fun getPokemonDetails(): PokemonDetails? {
        return pokemonDetailStateFlow.value
    }

    private suspend fun generatePokemonDetails(
        id: String
    ): PokemonDetails {
        PKLog.v(TAG, "generatePokemonDetails id: $id")
        val details: PokemonDetails = withContext(dispatcherProvider.getDispatcher()) {
            val pokemonEntity: PokemonEntity? = roomHelper.queryPokemonEntityById(id)
            pokemonEntity?.let {
                val speciesEntity =
                    pokemonEntity.let { roomHelper.querySpeciesEntityBySpeciesId(pokemonEntity.id) }
                val speciesDescriptionEntity =
                    speciesEntity?.let {
                        roomHelper.querySpeciesDescriptionBySpeciesId(
                            speciesEntity.id
                        )
                    }

                val defaultDescription =
                    speciesDescriptionEntity?.firstOrNull()?.description ?: ""

                val typeList = roomHelper.queryTypesOfPokemon(pokemonEntity.id)

                val fromPokemonEntity: PokemonEntity? =
                    speciesEntity?.idOfFromSpecies?.let { roomHelper.queryPokemonEntityById(it) }
                PokemonDetails(
                    id = pokemonEntity.id ?: "",
                    name = pokemonEntity.name ?: "",
                    typeList = typeList,
                    defaultPosterUrl = pokemonEntity.posterUrl,
                    defaultSpeciesDescription = defaultDescription,
                    evolvedPokemon = fromPokemonEntity
                )
            } ?: PokemonDetails(
                id = "",
                name = "",
                typeList = listOf(),
                defaultPosterUrl = "",
                defaultSpeciesDescription = "",
                evolvedPokemon = null
            )
        }
        return details
    }


    override fun onCleared() {
        super.onCleared()
        PKLog.v(TAG, "onCleared")
    }
}