package com.byronlin.pokemo.activity

import android.os.Bundle
import android.util.Log
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.ui.NavigationUI
import com.byronlin.pokemo.R
import com.byronlin.pokemo.databinding.ActivityMainBinding
import com.byronlin.pokemo.datasource.PokemonNetworkDataSource
import com.byronlin.pokemo.repository.PokemonResourceLoader
import com.byronlin.pokemo.repository.PokemonRoomRepository
import com.byronlin.pokemo.utils.PKLog
import com.byronlin.pokemo.viewmodel.MainActivityViewModel
import com.byronlin.pokemo.viewmodel.MainFragmentViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding


    private val mainActivityViewModel: MainActivityViewModel by viewModels()

    //private val pokemonResourceLoader :PokemonResourceLoader = PokemonResourceLoader()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

//        val pokemonRoomRepository = PokemonRoomRepository(application)
//        val pokemonResourceLoader = PokemonResourceLoader(
//            pokemonRoomRepository,
//            PokemonNetworkDataSource()
//        )
//
//        @Suppress("UNCHECKED_CAST")
//        mainActivityViewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
//            override fun <T : ViewModel> create(modelClass: Class<T>): T {
//                return MainActivityViewModel(pokemonResourceLoader) as T
//            }
//        }).get(MainActivityViewModel::class.java)
        mainActivityViewModel.startLoadAllResource(this.applicationContext)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onLocalVoiceInteractionStopped() {
        super.onLocalVoiceInteractionStopped()
    }

    override fun onStop() {
        super.onStop()
        PKLog.v(TAG, "onStop")
    }

    override fun onResume() {
        super.onResume()
        PKLog.v(TAG, "onResume")
    }

    override fun onPause() {
        super.onPause()
        PKLog.v(TAG, "onPause")
    }

    override fun onDestroy() {
        super.onDestroy()
        PKLog.v(TAG, "onDestroy")
    }
}