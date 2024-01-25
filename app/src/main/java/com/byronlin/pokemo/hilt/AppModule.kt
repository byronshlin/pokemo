package com.byronlin.pokemo.hilt

import android.content.Context
import com.byronlin.pokemo.datasource.PokemonNetworkDataSource
import com.byronlin.pokemo.repository.PokemonRoomRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun providePokemonRoomRepository(@ApplicationContext context: Context): PokemonRoomRepository {
        return PokemonRoomRepository(context)
    }

    @Provides
    @Singleton
    fun providePokemonNetworkDataSource(): PokemonNetworkDataSource {
        return PokemonNetworkDataSource()
    }
}