package com.byronlin.pokemo.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.byronlin.pokemo.repository.PokemonRoomRepository
import com.byronlin.pokemo.room.entity.PokemonEntity
import com.byronlin.pokemo.room.entity.SpeciesDescriptionEntity
import com.byronlin.pokemo.room.entity.SpeciesEntity
import com.byronlin.pokemo.test.MainDispatcherRule
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Rule

import org.junit.Test

class DetailViewModelTest {

    @get:Rule
    val liveDataRule = InstantTaskExecutorRule()

    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun queryPokemonDetail() = runTest{
        val repository = mockPokemonRoomRepository()
        val viewModel = DetailViewModel(repository,
           object : ViewModelDispatcherProvider{
               override fun getDispatcher(): CoroutineDispatcher {
                   return mainDispatcherRule.testDispatcher
               }
           })

        viewModel.pokemonDetailLiveData.observeForever {

        }
        viewModel.queryPokemonDetail("1")
        var pokemonDetails = viewModel.pokemonDetailLiveData.value
        assertNotNull(pokemonDetails)
        assertEquals("1", pokemonDetails?.id)
        assertEquals("pikachu", pokemonDetails?.name)
        assertEquals("electric", pokemonDetails?.typeList?.get(0))
        assertEquals("hello", pokemonDetails?.defaultSpeciesDescription)
        var fromPokemon = pokemonDetails?.evolvedPokemon
        assertNotNull(fromPokemon)
        assertEquals("2", fromPokemon?.id)
        assertEquals("mouse", fromPokemon?.name)
    }




    private fun mockPokemonRoomRepository(): PokemonRoomRepository {
        val pokemonRoomRepository = mockk<PokemonRoomRepository>()
        every { pokemonRoomRepository.queryPokemonEntityById("1") } returns PokemonEntity("1", "pikachu","", "1" )
        every { pokemonRoomRepository.queryPokemonEntityById("2") } returns PokemonEntity("2", "mouse","", "1" )

        every { pokemonRoomRepository.querySpeciesEntityBySpeciesId(any()) } returns SpeciesEntity("1", "pikachu", "2")
        every { pokemonRoomRepository.querySpeciesDescriptionBySpeciesId(any()) } returns arrayListOf<SpeciesDescriptionEntity>().apply {
            add(SpeciesDescriptionEntity("hello", "en", "1"))
        }
        every { pokemonRoomRepository.queryTypesOfPokemon(any()) } returns arrayListOf<String>().apply {
            add("electric")
        }
        return pokemonRoomRepository
    }
}