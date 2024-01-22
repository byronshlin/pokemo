package com.byronlin.pokemo.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.byronlin.pokemo.R
import com.byronlin.pokemo.adapter.PokemonCollectionAdapter
import com.byronlin.pokemo.databinding.FragmentMainBinding
import com.byronlin.pokemo.viewmodel.MainActivityViewModel
import com.byronlin.pokemo.viewmodel.MainFragmentViewModel

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val homeViewModel: MainFragmentViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //        binding.buttonFirst.setOnClickListener {
//            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
//        }

        renderView()
        initViewModel()
        homeViewModel.startLoadResource(requireContext())
    }

    private fun renderView(){
        binding.mainRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.mainRecyclerView.adapter = PokemonCollectionAdapter(::onPick, ::onCapture)
    }


    private fun onPick(id: String){
        val bundle = Bundle()
        bundle.putString("id", id)
        findNavController().navigate(R.id.action_to_DetailFragment, bundle)
    }
    private fun onCapture(id: String, captured: Boolean){
        if (captured) {
            homeViewModel.releasePokemon(requireContext(), id)
        } else {
            homeViewModel.catchPokemon(requireContext(), id)
        }
    }

    private fun initViewModel(){


        homeViewModel.collectionsLiveData.observe(viewLifecycleOwner) {
            (binding.mainRecyclerView.adapter as PokemonCollectionAdapter).updateList(it)
        }

        homeViewModel.loadCompleteLiveData.observe(viewLifecycleOwner){
            homeViewModel.initMainViews(requireContext())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}