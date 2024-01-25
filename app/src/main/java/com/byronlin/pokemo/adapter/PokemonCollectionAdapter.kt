package com.byronlin.pokemo.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListUpdateCallback
import androidx.recyclerview.widget.RecyclerView
import com.byronlin.pokemo.databinding.ItemPokemoCollectionBinding
import com.byronlin.pokemo.model.PokemonCollectionDisplayItem

class PokemonCollectionAdapter(
    private val onPick: (String) -> Unit,
    private val onCapture: (String, Boolean) -> Unit
) : RecyclerView.Adapter<PokemonCollectionAdapter.PokemonCollectionViewHolder>() {

    private var pokemonCollectionList = mutableListOf<PokemonCollectionDisplayItem>()

    fun updateList(list: List<PokemonCollectionDisplayItem>) {
        pokemonCollectionList = list.toMutableList()
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
        holder.binding.collectionRecyclerView.adapter =
            PokemonItemAdapter(onPick, onCapture)
                .apply { this.updateList(collectionItem.pokemonItemList) }
        holder.binding.bottomLine.visibility = if (collectionItem.isMyPokemon) {
            android.view.View.VISIBLE
        } else {
            android.view.View.GONE
        }
    }


    fun updateList2(list: List<PokemonCollectionDisplayItem>) {
        refreshPageByDiffUtil(list)
    }



    private fun refreshPageByDiffUtil(newList: List<PokemonCollectionDisplayItem>) {
        val oldList = ArrayList(pokemonCollectionList)

        pokemonCollectionList = newList.toMutableList()

        val diffResultBase = DiffUtil.calculateDiff(PageDiffCallback(oldList, pokemonCollectionList))

        diffResultBase.dispatchUpdatesTo(object : ListUpdateCallback {
            override fun onInserted(position: Int, count: Int) {
                this@PokemonCollectionAdapter.notifyItemRangeInserted(position, count)
            }

            override fun onRemoved(position: Int, count: Int) {
                this@PokemonCollectionAdapter.notifyItemRangeRemoved(position, count)
            }

            override fun onMoved(fromPosition: Int, toPosition: Int) {
                this@PokemonCollectionAdapter.notifyItemMoved(fromPosition, toPosition)
            }

            override fun onChanged(position: Int, count: Int, payload: Any?) {
                this@PokemonCollectionAdapter.notifyItemRangeChanged(
                    position,
                    count,
                    payload
                )
            }
        })
    }


    private class PageDiffCallback(
        private val oldList: List<PokemonCollectionDisplayItem>,
        private val newList: List<PokemonCollectionDisplayItem>
    ) : DiffUtil.Callback() {
        override fun getOldListSize() = oldList.size

        override fun getNewListSize() = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
            let {
                val oldItemType =
                    oldList.getOrNull(oldItemPosition)?.type
                val newItemType = newList.getOrNull(newItemPosition)?.type
                oldItemType != null && oldItemType.equals(newItemType)
            }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
            let {
                val oldItemType =
                    oldList.getOrNull(oldItemPosition)?.type
                val newItemType = newList.getOrNull(newItemPosition)?.type

                val oldItemContentSize =
                    oldList.getOrNull(oldItemPosition)?.pokemonItemList?.size
                val newItemContentSize =
                    newList.getOrNull(oldItemPosition)?.pokemonItemList?.size

                oldItemType != null && oldItemType.equals(newItemType) && oldItemContentSize == newItemContentSize
            }

        override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
            return newList[newItemPosition]
        }
    }




    class PokemonCollectionViewHolder(val binding: ItemPokemoCollectionBinding) : RecyclerView.ViewHolder(binding.root)
}