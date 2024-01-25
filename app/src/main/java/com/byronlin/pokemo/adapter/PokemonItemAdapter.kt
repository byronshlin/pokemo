package com.byronlin.pokemo.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListUpdateCallback
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.byronlin.pokemo.R
import com.byronlin.pokemo.databinding.ItemPokemoBinding
import com.byronlin.pokemo.model.PokemonCollectionDisplayItem
import com.byronlin.pokemo.model.PokemonDisplayItem

class PokemonItemAdapter(
    private val onPick: (String) -> Unit,
    private val onCapture: (String, Boolean) -> Unit
) : RecyclerView.Adapter<PokemonItemAdapter.PokemonItemViewHolder>() {

    private var pokemonItemList = mutableListOf<PokemonDisplayItem>()
    fun updateList(list: List<PokemonDisplayItem>) {
        pokemonItemList.clear()
        pokemonItemList.addAll(list)
        notifyDataSetChanged()
    }

    fun updateList2(list: List<PokemonDisplayItem>){
        refreshPageByDiffUtil(list)
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

        pokemonDisplayItem.imageUrl.let {
            Glide.with(holder.binding.root.context)
                .load(it)
                .placeholder(R.drawable.fake)
                .fitCenter()
                .into(holder.binding.pokemonImage)
        }

        holder.binding.root.setOnClickListener {
            onPick.invoke(pokemonDisplayItem.id)
        }

        holder.binding.pokemonBall.setOnClickListener {
            onCapture.invoke(pokemonDisplayItem.id, pokemonDisplayItem.captured)
        }
    }


    private fun refreshPageByDiffUtil(newList: List<PokemonDisplayItem>) {
        val oldList = ArrayList(pokemonItemList)

        pokemonItemList = newList.toMutableList()

        val diffResultBase = DiffUtil.calculateDiff(PageDiffCallback(oldList, pokemonItemList))

        diffResultBase.dispatchUpdatesTo(object : ListUpdateCallback {
            override fun onInserted(position: Int, count: Int) {
                this@PokemonItemAdapter.notifyItemRangeInserted(position, count)
            }

            override fun onRemoved(position: Int, count: Int) {
                this@PokemonItemAdapter.notifyItemRangeRemoved(position, count)
            }

            override fun onMoved(fromPosition: Int, toPosition: Int) {
                this@PokemonItemAdapter.notifyItemMoved(fromPosition, toPosition)
            }

            override fun onChanged(position: Int, count: Int, payload: Any?) {
                this@PokemonItemAdapter.notifyItemRangeChanged(
                    position,
                    count,
                    payload
                )
            }
        })
    }


    private class PageDiffCallback(
        private val oldList: List<PokemonDisplayItem>,
        private val newList: List<PokemonDisplayItem>
    ) : DiffUtil.Callback() {
        override fun getOldListSize() = oldList.size

        override fun getNewListSize() = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
            let {
                val oldItemId =
                    oldList.getOrNull(oldItemPosition)?.id
                val newItemId = newList.getOrNull(newItemPosition)?.id
                oldItemId != null && oldItemId.equals(newItemId)
            }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
            let {
                val oldItemId =
                    oldList.getOrNull(oldItemPosition)?.id
                val newItemId = newList.getOrNull(newItemPosition)?.id
                oldItemId != null && oldItemId.equals(newItemId)
            }

        override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
            return newList[newItemPosition]
        }
    }

    class PokemonItemViewHolder(val binding: ItemPokemoBinding) : RecyclerView.ViewHolder(binding.root)
}