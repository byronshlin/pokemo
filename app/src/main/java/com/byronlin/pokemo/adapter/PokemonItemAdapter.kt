package com.byronlin.pokemo.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.byronlin.pokemo.databinding.ItemPokemoBinding
import com.byronlin.pokemo.model.PokemonDisplayItem

class PokemonItemAdapter : RecyclerView.Adapter<PokemonItemAdapter.PokemonItemViewHolder>() {

    private var pokemonItemList = mutableListOf<PokemonDisplayItem>()

    fun updateList(list: List<PokemonDisplayItem>) {
        pokemonItemList.clear()
        pokemonItemList.addAll(list)
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PokemonItemViewHolder {
        return PokemonItemViewHolder(
            ItemPokemoBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return pokemonItemList.size
    }

    override fun onBindViewHolder(holder: PokemonItemViewHolder, position: Int) {
        val pokemonDisplayItem = pokemonItemList[position]

        holder.binding.pokemonName.text = pokemonDisplayItem.title
        Glide.with(holder.binding.root.context)
            .load(" https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/1.png")
            .fitCenter()
            .into(holder.binding.pokemonImage)


//        pokemonDisplayItem.imageUrl.let {
//            Glide.with(holder.binding.root.context).load(it)
//                .fitCenter()
//                .into(holder.binding.pokemonImage)
//        }
    }


    class PokemonItemViewHolder(val binding: ItemPokemoBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }
}