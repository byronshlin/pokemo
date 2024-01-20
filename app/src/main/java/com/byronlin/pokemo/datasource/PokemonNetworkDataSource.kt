package com.byronlin.pokemo.datasource

import com.byronlin.pokemo.datasource.api.PokemonApiService
import com.byronlin.pokemo.datasource.response.PokemonResourceResponse
import com.byronlin.pokemo.datasource.response.PokemonResponse
import com.byronlin.pokemo.datasource.response.SpeciesResponse
import com.byronlin.pokemo.model.Pokemon
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class PokemonNetworkDataSource {


    private var oAuthOkhttpClient: OkHttpClient? = null

    fun queryPokemonResources(offset: Int, limit: Int): PokemonResourceResponse? {
        val retrofit = obtainRetrofit()
        return (try {
            retrofit.create(PokemonApiService::class.java).getPokemonResources(offset, limit)
                .execute()
        } catch (e: Exception) {
            null
        })?.takeIf { it.isSuccessful }?.body()
    }

    fun queryPokemon(id: String): PokemonResponse? {
        val retrofit = obtainRetrofit()
        return (try {
            retrofit.create(PokemonApiService::class.java).getPokemon(id).execute()
        } catch (e: Exception) {
            null
        })?.takeIf { it.isSuccessful }?.body()
    }

    fun querySpecies(id: String): SpeciesResponse? {
        val retrofit = obtainRetrofit()
        return (try {
            retrofit.create(PokemonApiService::class.java).getPokemonSpecies(id).execute()
        } catch (e: Exception) {
            null
        })?.takeIf { it.isSuccessful }?.body()
    }


    private fun obtainRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://pokeapi.co/api/v2/")
            .client(getOAuthOkHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create()) // 支持RxJava
            .build()
    }


    private fun getOAuthOkHttpClient(): OkHttpClient {
        if (oAuthOkhttpClient == null) {
            oAuthOkhttpClient = OkHttpClient.Builder()
//                .addInterceptor { chain ->
//                    val original = chain.request()
//                    val requestBuilder = original.newBuilder()
//                        .header("Authorization", "Bearer " + "token")
//                        .method(original.method(), original.body())
//                    val request = requestBuilder.build()
//                    chain.proceed(request)
//                }
                .build()
        }
        return oAuthOkhttpClient!!
    }


}