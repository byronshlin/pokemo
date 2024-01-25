package com.byronlin.pokemo.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.byronlin.pokemo.NavGraphDirections
import com.byronlin.pokemo.adapter.PokemonCollectionAdapter
import com.byronlin.pokemo.databinding.FragmentMainBinding
import com.byronlin.pokemo.datasource.PokemonNetworkDataSource
import com.byronlin.pokemo.repository.PokemonResourceLoader
import com.byronlin.pokemo.repository.PokemonRoomRepository
import com.byronlin.pokemo.utils.PKLog
import com.byronlin.pokemo.viewmodel.MainFragmentViewModel

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class MainFragment : Fragment() {
    private val TAG = "MainFragment"
    private var _binding: FragmentMainBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var homeViewModel: MainFragmentViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        PKLog.v(TAG, "onCreateView")
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        renderView()
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewModel()
        //homeViewModel.initMainViews()
        //homeViewModel.startLoadResource(requireContext(), false)

        homeViewModel.startLoadAllResource(requireContext())
    }

    private fun renderView() {
        binding.mainRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.mainRecyclerView.adapter = PokemonCollectionAdapter(::onPick, ::onCapture)
    }


    private fun onPick(id: String) {
        findNavController().navigate(NavGraphDirections.actionToDetailFragment(id))
    }

    private fun onCapture(id: String, captured: Boolean) {
        if (captured) {
            homeViewModel.releasePokemon(requireContext(), id)
        } else {
            homeViewModel.catchPokemon(requireContext(), id)
        }
    }

    private fun initViewModel() {
        //TODO hilt!!
        val pokemonRoomRepository = PokemonRoomRepository(requireActivity().application)
        val pokemonResourceLoader = PokemonResourceLoader(
            pokemonRoomRepository,
            PokemonNetworkDataSource()
        )
        @Suppress("UNCHECKED_CAST")
        homeViewModel = ViewModelProvider(this,
            object : ViewModelProvider.NewInstanceFactory() {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return MainFragmentViewModel(
                        pokemonRoomRepository,
                        pokemonResourceLoader
                    ) as T
                }
            }
        )[MainFragmentViewModel::class.java]

        homeViewModel.collectionsLiveData.observe(viewLifecycleOwner) {
            //(binding.mainRecyclerView.adapter as PokemonCollectionAdapter).updateList(it)
        }

        homeViewModel.loadCompleteLiveData.observe(viewLifecycleOwner) {
            //homeViewModel.initMainViews()
        }

        homeViewModel.newCollectionListLiveData.observe(viewLifecycleOwner) {
            PKLog.v(TAG, "newCollectionListLiveData refresh: ${it.size}")
            (binding.mainRecyclerView.adapter as PokemonCollectionAdapter).updateList(it)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        PKLog.v(TAG, "onDestroyView")
        _binding = null
    }
}