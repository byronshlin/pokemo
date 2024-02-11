package com.byronlin.pokemo.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.byronlin.pokemo.NavGraphDirections
import com.byronlin.pokemo.adapter.PokemonCollectionAdapter
import com.byronlin.pokemo.databinding.FragmentMainBinding
import com.byronlin.pokemo.model.MY_POKEMON
import com.byronlin.pokemo.utils.PKLog
import com.byronlin.pokemo.viewmodel.MainFragmentViewModel
import dagger.hilt.android.AndroidEntryPoint

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
@AndroidEntryPoint
class MainFragment : Fragment() {
    private val TAG = "MainFragment"
    private var _binding: FragmentMainBinding? = null

    private val binding get() = _binding!!

    private val homeViewModel: MainFragmentViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        PKLog.v(TAG, "onCreateView")
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        initView()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        PKLog.v(TAG, "onViewCreated")
        initViewModel()
    }

    private fun initView() {
        binding.mainRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.mainRecyclerView.adapter =
            PokemonCollectionAdapter(::onPick, ::onCapture, homeViewModel.cacheCollectionScrollState)
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
        homeViewModel.mainViewUILiveData.observe(viewLifecycleOwner) {
            PKLog.v(TAG, "newCollectionListLiveData refresh: ${it.size}")
            (binding.mainRecyclerView.adapter as PokemonCollectionAdapter).updateList2(it)
            binding.mainRecyclerView.scrollY = homeViewModel.cacheRecyclerScrollY
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        PKLog.v(TAG, "onAttach")
    }

    override fun onDetach() {
        super.onDetach()
        PKLog.v(TAG, "onDetach")
    }

    override fun onStart() {
        super.onStart()
        PKLog.v(TAG, "onStart")
    }

    override fun onResume() {
        super.onResume()
        PKLog.v(TAG, "onResume")
    }

    override fun onPause() {
        super.onPause()
        PKLog.v(TAG, "onPause")
    }

    override fun onStop() {
        super.onStop()
        PKLog.v(TAG, "onStop")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        PKLog.v(TAG, "onDestroyView")

        homeViewModel.cacheRecyclerScrollY = binding.mainRecyclerView.scrollY
        homeViewModel.cacheCollectionScrollState = (binding.mainRecyclerView?.adapter as? PokemonCollectionAdapter)?.onSaveDetailScrollState()

        _binding = null
    }
    override fun onDestroy() {
        super.onDestroy()
        PKLog.v(TAG, "onDestroy")
        homeViewModel.cacheRecyclerScrollY = 0
        homeViewModel.cacheCollectionScrollState = null

    }
}