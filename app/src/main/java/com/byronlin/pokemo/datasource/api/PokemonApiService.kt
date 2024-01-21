package com.byronlin.pokemo.datasource.api

import com.byronlin.pokemo.datasource.response.NamedAPIResourceList
import com.byronlin.pokemo.datasource.response.PokemonResponse
import com.byronlin.pokemo.datasource.response.SpeciesResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PokemonApiService {
    @GET("pokemon")
    fun getPokemonResources(@Query("offset") offset: Int, @Query("limit") limit: Int) : Call<NamedAPIResourceList>

    @GET("pokemon/{id}")
    fun getPokemon(@Path("id") id: String): Call<PokemonResponse>

    @GET("pokemon-species/{id}")
    fun getPokemonSpecies(@Path("id") id: String): Call<SpeciesResponse>
}