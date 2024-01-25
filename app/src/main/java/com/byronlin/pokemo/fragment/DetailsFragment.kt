package com.byronlin.pokemo.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.byronlin.pokemo.NavGraphDirections
import com.byronlin.pokemo.R
import com.byronlin.pokemo.databinding.FragmentSecondBinding
import com.byronlin.pokemo.model.PokemonDetails
import com.byronlin.pokemo.repository.PokemonRoomRepository
import com.byronlin.pokemo.utils.PKLog
import com.byronlin.pokemo.viewmodel.DetailViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class DetailsFragment : Fragment() {
    private val TAG = "DetailsFragment"
    private var _binding: FragmentSecondBinding? = null

    private val binding get() = _binding!!

    private val detailViewModel: DetailViewModel by viewModels()
    private val args: DetailsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        PKLog.v(TAG, "onCreateView")
        _binding = FragmentSecondBinding.inflate(inflater, container, false)

        binding.fromPokemonImage.setOnClickListener {
            detailViewModel.pokemonDetailLiveData.value?.evolvedPokemon?.id?.let {
                findNavController().navigate(NavGraphDirections.actionToDetailFragment(it))
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewModel()
        args.pokemonId.let {
            detailViewModel.queryPokemonDetail(it?:"")
        }
    }

    private fun initViewModel(){
//        @Suppress("UNCHECKED_CAST")
//        detailViewModel = ViewModelProvider(this, object:ViewModelProvider.NewInstanceFactory(){
//            override fun <T : ViewModel> create(modelClass: Class<T>): T {
//                return DetailViewModel(PokemonRoomRepository(requireActivity().application)) as T
//            }
//        })[DetailViewModel::class.java]

        detailViewModel.pokemonDetailLiveData.observe(viewLifecycleOwner) {
            renderView(it)
        }
    }


    private fun renderView(details: PokemonDetails){
        if (details.id.isEmpty()) {
            return
        }

        binding.idText.text = "#${details.id}"
        binding.backBtn.setOnClickListener {
            findNavController().navigateUp()
        }
        Glide.with(this).load(details.defaultPosterUrl).placeholder(R.drawable.fake)
            .fitCenter().into(binding.pokemonImage)
        binding.pokemonName.text = details.name
        binding.speciesDescription.text = details.defaultSpeciesDescription
        details.evolvedPokemon?.also {fromPokemonEntity ->
            binding.evolvedGroup.visibility = View.VISIBLE
            binding.fromPokemonName.text = fromPokemonEntity.name
            Glide.with(this).load(fromPokemonEntity.posterUrl).placeholder(R.drawable.fake)
                .fitCenter().into(binding.fromPokemonImage)
        }?:run {
            binding.evolvedGroup.visibility = View.GONE
        }
        details.typeList.forEach {
            val textView = View.inflate(requireContext(), R.layout.label_type, null) as
                    TextView
            textView.text = it
            binding.typeBox.addView(textView)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        PKLog.v(TAG, "onDestroyView")
        _binding = null
    }
}