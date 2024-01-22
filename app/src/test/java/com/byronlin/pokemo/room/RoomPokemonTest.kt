package com.byronlin.pokemo.room

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.byronlin.pokemo.room.dao.PokemonQueryDao
import com.byronlin.pokemo.room.dao.PokemonUpdateDao
import com.byronlin.pokemo.room.data.DataHelper
import com.byronlin.pokemo.room.data.DescriptionInfo
import com.byronlin.pokemo.room.data.PokemonInfo
import com.byronlin.pokemo.room.data.SpeciesInfo
import com.byronlin.pokemo.room.entity.CaptureEntity
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RoomPokemonTest {

    private lateinit var db: PokemonRoomDatabase
    private lateinit var pokemonUpdateDao: PokemonUpdateDao
    private lateinit var pokemonQueryDao: PokemonQueryDao


    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, PokemonRoomDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()
    }

    @Test
    fun transferPokemonInfoListToWriteEntityInfoTest() {

        val info =
            DataHelper.transferPokemonInfoListToWriteEntityInfo(pokenmonlist, speciesInfoList)

        Assert.assertEquals(info.pokemonEntityList.size, 6)

        info.pokemonEntityList.zip(
            pokenmonlist
        ).forEach {
            Assert.assertEquals(it.first.id, it.second.id)
            Assert.assertEquals(it.first.name, it.second.name)
            Assert.assertEquals(it.first.posterUrl, it.second.posterUrl)
            Assert.assertEquals(it.first.idOfSpecies, it.second.speciesId)
        }
        Assert.assertEquals(info.speciesEntityList.size, 5)

        info.speciesEntityList.zip(
            speciesInfoList
        ).forEach {
            Assert.assertEquals(it.first.id, it.second.id)
            Assert.assertEquals(it.first.name, it.second.name)
            Assert.assertEquals(it.first.idOfFromSpecies, it.second.idOfFromSpecies)
        }

        Assert.assertEquals(info.speciesDescriptionEntityList.size, 6)

        Assert.assertEquals(
            info.speciesDescriptionEntityList[0].language,
            speciesInfoList[0].descriptionInfo[0].language
        )
        Assert.assertEquals(
            info.speciesDescriptionEntityList[0].description,
            speciesInfoList[0].descriptionInfo[0].description
        )

        Assert.assertEquals(
            info.speciesDescriptionEntityList[1].language,
            speciesInfoList[0].descriptionInfo[1].language
        )
        Assert.assertEquals(
            info.speciesDescriptionEntityList[1].description,
            speciesInfoList[0].descriptionInfo[1].description
        )


        Assert.assertEquals(
            info.speciesDescriptionEntityList[2].language,
            speciesInfoList[1].descriptionInfo[0].language
        )
        Assert.assertEquals(
            info.speciesDescriptionEntityList[2].description,
            speciesInfoList[1].descriptionInfo[0].description
        )

        Assert.assertEquals(
            info.speciesDescriptionEntityList[3].language,
            speciesInfoList[2].descriptionInfo[0].language
        )
        Assert.assertEquals(
            info.speciesDescriptionEntityList[3].description,
            speciesInfoList[2].descriptionInfo[0].description
        )

        Assert.assertEquals(
            info.speciesDescriptionEntityList[4].language,
            speciesInfoList[3].descriptionInfo[0].language
        )
        Assert.assertEquals(
            info.speciesDescriptionEntityList[4].description,
            speciesInfoList[3].descriptionInfo[0].description
        )

        Assert.assertEquals(
            info.speciesDescriptionEntityList[5].language,
            speciesInfoList[4].descriptionInfo[0].language
        )
        Assert.assertEquals(
            info.speciesDescriptionEntityList[5].description,
            speciesInfoList[4].descriptionInfo[0].description
        )
        Assert.assertEquals(info.pokemonTypesRelationshipEntityList.size, 10)
    }

    @Test
    fun testLoadToDatabase() {
        val info =
            DataHelper.transferPokemonInfoListToWriteEntityInfo(pokenmonlist, speciesInfoList)
        db.updateDao().loadToDatabase( info, 10)

        db.queryDao().queryNext().also { result ->
            Assert.assertNotNull(result)
            Assert.assertEquals(result, 10)
        }

        db.queryDao().queryPokemonEntityList().also { result ->
            Assert.assertNotNull(result)
            Assert.assertEquals(result!!.size, 6)
        }

        db.queryDao().queryPokemonTypes().also { result ->
            Assert.assertNotNull(result)
            Assert.assertEquals(result!!.size, 10)
        }


        db.queryDao().queryTypesOfPokemon("1").also { result ->
            Assert.assertNotNull(result)
            Assert.assertEquals( 2, result!!.size)

            Assert.assertEquals("grass", result[0])
            Assert.assertEquals( "poison", result[1])
        }

        db.queryDao().queryTypes().also { result ->
            Assert.assertNotNull(result)
            Assert.assertEquals(result!!.size, 4)
        }

        db.updateDao().catchPokemon("1")

        db.queryDao().queryCapturePokemonList().also { result ->
            Assert.assertNotNull(result)
            Assert.assertEquals(result!!.size, 1)
            Assert.assertEquals(result[0].id, "1")
            Assert.assertEquals(result[0].captured, 1)
        }

        db.updateDao().releasePokemon("1")

        db.queryDao().queryCapturePokemonList().also { result ->
            Assert.assertNotNull(result)
            Assert.assertEquals(result!!.size, 0)
        }

    }


    private val speciesInfoList = listOf(
        SpeciesInfo(
            "1",
            "bulbasaur",
            null,
            listOf(
                DescriptionInfo(
                    "en",
                    "Bulbasaur can be seen napping in bright sunlight. There is a seed on its back. By soaking up the sun's rays, the seed grows progressively larger."
                ),
                DescriptionInfo(
                    "zh",
                    "妙蛙種子可以在明亮的陽光下看到它在小睡。它背上有一顆種子。透過吸收陽光的能量，這顆種子逐漸變得更大"
                )
            )
        ),
        SpeciesInfo(
            "2",
            "ivysaur",
            "1",
            listOf(
                DescriptionInfo(
                    "en",
                    "There is a bud on this Pokémon's back. To support its weight, Ivysaur's legs and trunk grow thick and strong. If it starts spending more time lying in the sunlight, it's a sign that the bud will bloom into a large flower soon."
                )
            )
        ),
        SpeciesInfo(
            "3",
            "venusaur",
            "2",
            listOf(
                DescriptionInfo(
                    "en",
                    "There is a large flower on Venusaur's back. The flower is said to take on vivid colors if it gets plenty of nutrition and sunlight. The flower's aroma soothes the emotions of people."
                )
            )
        ),
        SpeciesInfo(
            "4",
            "charmander",
            null,
            listOf(
                DescriptionInfo(
                    "en",
                    "The flame on its tail indicates Charmander's life force. If it is healthy, the flame burns brightly."
                )
            )
        ),
        SpeciesInfo(
            "5",
            "charmeleon",
            "4",
            listOf(
                DescriptionInfo(
                    "en",
                    "Charmeleon mercilessly destroys its foes using its sharp claws. If it encounters a strong foe, it turns aggressive. In this excited state, the flame at the tip of its tail flares with a bluish white color."
                )
            )
        )
    )


    private val pokenmonlist = listOf(
        PokemonInfo(
            "1",
            "bulbasaur",
            listOf("grass", "poison"),
            "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/1.png",
            "1"
        ),
        PokemonInfo(
            "2",
            "ivysaur",
            listOf("grass", "poison"),
            "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/2.png",
            "2"
        ),
        PokemonInfo(
            "3",
            "venusaur",
            listOf("grass", "poison"),
            "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/3.png",
            "3"
        ),
        PokemonInfo(
            "4",
            "charmander",
            listOf("fire"),
            "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/4.png",
            "4"
        ),
        PokemonInfo(
            "5",
            "charmeleon",
            listOf("fire"),
            "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/5.png",
            "5"
        ),
        PokemonInfo(
            "6",
            "charizard",
            listOf("fire", "flying"),
            "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/6.png",
            "5"
        )
    )

}