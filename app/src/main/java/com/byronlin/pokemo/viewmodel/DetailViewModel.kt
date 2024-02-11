package com.byronlin.pokemo.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.byronlin.pokemo.model.PokemonDetails
import com.byronlin.pokemo.repository.PokemonRoomRepository
import com.byronlin.pokemo.room.entity.PokemonEntity
import com.byronlin.pokemo.utils.PKLog
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(private val roomHelper: PokemonRoomRepository,) : ViewModel() {
    private val TAG = "DetailViewModel"


    private val _idLiveData: MutableLiveData<String> = MutableLiveData()
    val pokemonDetailLiveData: LiveData<PokemonDetails> = _idLiveData.switchMap {
        val liveData: MutableLiveData<PokemonDetails> = MutableLiveData()
        viewModelScope.launch {
            val details = generatePokemonDetails(it)
            liveData.postValue(details)
        }
        liveData
    }

    fun queryPokemonDetail(id: String) {
        _idLiveData.value = id
    }

    private suspend fun generatePokemonDetails(
        id: String
    ): PokemonDetails {
        val details: PokemonDetails = withContext(Dispatchers.IO) {
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