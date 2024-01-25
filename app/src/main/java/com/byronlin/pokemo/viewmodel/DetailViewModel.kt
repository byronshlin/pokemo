package com.byronlin.pokemo.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Insert
import com.byronlin.pokemo.model.PokemonDetails
import com.byronlin.pokemo.repository.PokemonRoomRepository
import com.byronlin.pokemo.room.entity.PokemonEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(private val roomHelper : PokemonRoomRepository) : ViewModel() {

    val _pokemonDetailLiveData: MutableLiveData<PokemonDetails> = MutableLiveData()
    val pokemonDetailLiveData: LiveData<PokemonDetails> = _pokemonDetailLiveData

    private val _idLiveData: MutableLiveData<String> = MutableLiveData()
    val idLiveData: LiveData<String> = _idLiveData

    fun queryPokemonDetail(context: Context, id: String) {

        viewModelScope.launch {
            val details: PokemonDetails = withContext(Dispatchers.IO) {
                val queryDao = roomHelper.obtainPokemonDatabase(context).queryDao()

                val pokemonEntity: PokemonEntity? = queryDao.queryPokemonEntityById(id)
                pokemonEntity?.let {
                    val speciesEntity =
                        pokemonEntity.let { queryDao.querySpeciesEntityBySpeciesId(pokemonEntity.id) }
                    val speciesDescriptionEntity =
                        speciesEntity?.let {
                            queryDao.querySpeciesDescriptionBySpeciesId(
                                speciesEntity.id
                            )
                        }

                    val defaultDescription =
                        speciesDescriptionEntity?.firstOrNull()?.description ?: ""

                    val typeList = queryDao.queryTypesOfPokemon(pokemonEntity.id)

                    val fromPokemonEntity : PokemonEntity? =
                        speciesEntity?.idOfFromSpecies?.let { queryDao.queryPokemonEntityById(it) }
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
            _idLiveData.value = id
            _pokemonDetailLiveData.value = details
        }
    }
}