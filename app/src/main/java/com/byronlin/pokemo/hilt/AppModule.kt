package com.byronlin.pokemo.hilt

import android.content.Context
import androidx.room.Room
import com.byronlin.pokemo.datasource.PokemonNetworkDataSource
import com.byronlin.pokemo.room.PokemonRoomDatabase
import com.byronlin.pokemo.viewmodel.ViewModelDispatcherProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

//    @Provides
//    @Singleton
//    fun providePokemonRoomRepository(@ApplicationContext context: Context): PokemonRoomRepository {
//        return PokemonRoomRepository(context)
//    }


    @Provides
    @Singleton
    fun provideViewModelDispatcherProvider(): ViewModelDispatcherProvider {
        return object : ViewModelDispatcherProvider {
            override fun getDispatcher() = kotlinx.coroutines.Dispatchers.IO
        }
    }

    @Provides
    @Singleton
    fun providePokemonNetworkDataSource(): PokemonNetworkDataSource {
        return PokemonNetworkDataSource()
    }

    @Provides
    @Singleton
    fun providePokemonRoomDatabase(@ApplicationContext context: Context): PokemonRoomDatabase {
        return getDatabase(context)
    }


    @Volatile
    private var sCommonLibRoomDatabase: PokemonRoomDatabase? = null
    fun getDatabase(context: Context): PokemonRoomDatabase {
        return sCommonLibRoomDatabase ?: synchronized(AppModule) {
            if (sCommonLibRoomDatabase == null) {
                sCommonLibRoomDatabase = Room.databaseBuilder(
                    context,
                    PokemonRoomDatabase::class.java,
                    PokemonRoomDatabase.DATABASE_NAME
                )
                    .build()
                sCommonLibRoomDatabase
            } else sCommonLibRoomDatabase
            sCommonLibRoomDatabase!!
        }
    }


}