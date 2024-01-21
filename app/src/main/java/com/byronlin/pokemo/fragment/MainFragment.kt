package com.byronlin.pokemo.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
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
    private lateinit var mainActivityViewModel : MainActivityViewModel
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

//        Glide.with(this)
//            .load("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/1.png")
//           // .placeholder(R.drawable.ic_launcher_background)
//            .error(R.drawable.ic_launcher_background)
//            .into(binding.testingImage)

        homeViewModel.initMainViews(requireContext())
    }

    private fun renderView(){
        binding.mainRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.mainRecyclerView.adapter = PokemonCollectionAdapter()
    }

    private fun initViewModel(){

        mainActivityViewModel = ViewModelProvider(requireActivity()).get(MainActivityViewModel::class.java)

        homeViewModel.collectionsLiveData.observe(viewLifecycleOwner) {
            (binding.mainRecyclerView.adapter as PokemonCollectionAdapter).updateList(it)
        }


        mainActivityViewModel.loadCompleteLiveData.observe(viewLifecycleOwner){
            homeViewModel.initMainViews(requireContext())
        }
        
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}