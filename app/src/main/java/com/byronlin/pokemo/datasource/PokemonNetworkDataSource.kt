package com.byronlin.pokemo.datasource

import android.util.Log
import com.byronlin.pokemo.datasource.api.PokemonApiService
import com.byronlin.pokemo.datasource.response.NamedAPIResourceList
import com.byronlin.pokemo.datasource.response.PokemonResponse
import com.byronlin.pokemo.datasource.response.SpeciesResponse
import com.byronlin.pokemo.utils.PKLog
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class PokemonNetworkDataSource @Inject constructor() {
    private val okHttpClient: OkHttpClient by lazy { createOkHttpClient() }
    private val retrofit: Retrofit by lazy { createRetrofit() }
    fun queryPokemonResources(offset: Int, limit: Int): NamedAPIResourceList? {
        return (try {
            retrofit.create(PokemonApiService::class.java).getPokemonResources(offset, limit)
                .execute()
        } catch (e: Exception) {
            PKLog.e("PokemonNetworkDataSource", "queryPokemonResources", e)
            null
        })?.takeIf { it.isSuccessful }?.body()
    }

    fun queryPokemon(id: String): PokemonResponse? {
        return (try {
            retrofit.create(PokemonApiService::class.java).getPokemon(id).execute()
        } catch (e: Exception) {
            null
        })?.takeIf { it.isSuccessful }?.body()
    }

    fun querySpecies(id: String): SpeciesResponse? {
        return (try {
            retrofit.create(PokemonApiService::class.java).getPokemonSpecies(id).execute()
        } catch (e: Exception) {
            null
        })?.takeIf { it.isSuccessful }?.body()
    }


    private fun createRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://pokeapi.co/api/v2/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .build()
    }

    private fun createOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor(
                object : HttpLoggingInterceptor.Logger {
                    override fun log(message: String) {
                        Log.d("PKLog", "CP_OKHTTP $message")
                    }
                }
            ).apply {
                level = HttpLoggingInterceptor.Level.NONE
            })
            .build()
    }


}