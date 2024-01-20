package com.byronlin.pokemo.datasource

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class PokemonNetworkDataSourceTest {

    @Test
    fun queryPokemonResources_Test() {
        val response = PokemonNetworkDataSource().queryPokemonResources(0, 10)
        Assert.assertTrue(response != null)
        Assert.assertEquals(response?.results?.size ?: -1, 10)
    }

    @Test
    fun queryPokemon_Test() {
        val response = PokemonNetworkDataSource().queryPokemon("1")
        Assert.assertTrue(response != null)
    }

    fun querySpecies_Test() {
        val response = PokemonNetworkDataSource().querySpecies("1")
        Assert.assertTrue(response != null)
    }
}