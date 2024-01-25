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
class PokemonNetworkDataSource @Inject constructor(){
    fun queryPokemonResources(offset: Int, limit: Int): NamedAPIResourceList? {
        val retrofit = obtainRetrofit()
        return (try {
            retrofit.create(PokemonApiService::class.java).getPokemonResources(offset, limit)
                .execute()
        } catch (e: Exception) {
            PKLog.e("PokemonNetworkDataSource", "queryPokemonResources", e)
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
        if (sRetrofit == null) {
            sRetrofit = Retrofit.Builder()
                .baseUrl("https://pokeapi.co/api/v2/")
                .client(getOAuthOkHttpClient())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create()) // 支持RxJava
                .build()
        }
        return sRetrofit!!
    }


    private fun getOAuthOkHttpClient(): OkHttpClient {
        if (sOkhttpClient == null) {
            sOkhttpClient = OkHttpClient.Builder()
                .addInterceptor(HttpLoggingInterceptor(
                    object : HttpLoggingInterceptor.Logger {
                        override fun log(message: String) {
                            Log.d("PKLog", "CP_OKHTTP $message")
                        }
                    }
                ).apply {
                    level = HttpLoggingInterceptor.Level.NONE
                })
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
        return sOkhttpClient!!
    }

    companion object {
        private var sOkhttpClient: OkHttpClient? = null
        private var sRetrofit: Retrofit? = null
    }


}