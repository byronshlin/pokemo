package com.byronlin.pokemo.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.byronlin.pokemo.databinding.ItemPokemoCollectionBinding
import com.byronlin.pokemo.model.PokemonCollectionDisplayItem

class PokemonCollectionAdapter(
    private val onPick: (String) -> Unit,
    private val onCapture: (String, Boolean) -> Unit
) : RecyclerView.Adapter<PokemonCollectionAdapter.PokemonCollectionViewHolder>() {

    private var pokemonCollectionList = mutableListOf<PokemonCollectionDisplayItem>()

    fun updateList(list: List<PokemonCollectionDisplayItem>) {
        pokemonCollectionList.clear()
        pokemonCollectionList.addAll(list)
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PokemonCollectionViewHolder {
        val binding = ItemPokemoCollectionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PokemonCollectionViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return pokemonCollectionList.count()
    }

    override fun onBindViewHolder(holder: PokemonCollectionViewHolder, position: Int) {
        var collectionItem = pokemonCollectionList[position]
        holder.binding.title.text = collectionItem.type
        holder.binding.count.text = collectionItem.pokemonItemList.size.toString()
        holder.binding.collectionRecyclerView.layoutManager = LinearLayoutManager(holder.itemView.context, LinearLayoutManager.HORIZONTAL, false)
        holder.binding.collectionRecyclerView.adapter = PokemonItemAdapter(onPick, onCapture).apply {
            updateList(collectionItem.pokemonItemList)
        }
        holder.binding.bottomLine.visibility = if (collectionItem.isMyPokemon) {
            android.view.View.VISIBLE
        } else {
            android.view.View.GONE
        }
    }


    class PokemonCollectionViewHolder(val binding: ItemPokemoCollectionBinding) : RecyclerView.ViewHolder(binding.root) {

    }
}