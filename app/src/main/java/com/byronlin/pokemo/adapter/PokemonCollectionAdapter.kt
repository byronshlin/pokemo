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
    private val onCapture: (String, Boolean) -> Unit,
    saveStateMap: Map<String, Pair<Int, Int>>?
) : RecyclerView.Adapter<PokemonCollectionAdapter.PokemonCollectionViewHolder>() {

    private var pokemonCollectionList = mutableListOf<PokemonCollectionDisplayItem>()

    private var paddingSaveState: MutableMap<String, Pair<Int, Int>>? =
        saveStateMap?.let {
            mutableMapOf<String, Pair<Int, Int>>().apply { putAll(it) }
        }


    fun updateList(list: List<PokemonCollectionDisplayItem>) {
        pokemonCollectionList = list.toMutableList()
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PokemonCollectionViewHolder {
        val binding =
            ItemPokemoCollectionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PokemonCollectionViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return pokemonCollectionList.count()
    }

    override fun onBindViewHolder(holder: PokemonCollectionViewHolder, position: Int) {
        val collectionItem = pokemonCollectionList[position]

        holder.cacheType = collectionItem.type
        holder.binding.title.text = collectionItem.type
        holder.binding.count.text = collectionItem.pokemonItemList.size.toString()
        holder.binding.collectionRecyclerView.layoutManager =
            LinearLayoutManager(holder.itemView.context, LinearLayoutManager.HORIZONTAL, false)

        val adapter = PokemonItemAdapter(collectionItem.type, onPick, onCapture)
        holder.binding.collectionRecyclerView.adapter = adapter
        adapter.updateList2(collectionItem.pokemonItemList)


        holder.binding.bottomLine.visibility = if (collectionItem.isMyPokemon) {
            android.view.View.VISIBLE
        } else {
            android.view.View.GONE
        }

        val saveState = paddingSaveState?.get(collectionItem.type)
        paddingSaveState?.remove(collectionItem.type)

        saveState?.run {
            holder.binding.collectionRecyclerView.scrollToPosition(first)
            (holder.binding.collectionRecyclerView.layoutManager as? LinearLayoutManager)
                ?.scrollToPositionWithOffset(first, second)
        }
    }


    fun updateList2(list: List<PokemonCollectionDisplayItem>) {
        refreshPageByDiffUtil(list)
    }


    private fun refreshPageByDiffUtil(newList: List<PokemonCollectionDisplayItem>) {
        val oldList = ArrayList(pokemonCollectionList)

        pokemonCollectionList = newList.toMutableList()

        val diffResultBase =
            DiffUtil.calculateDiff(PageDiffCallback(oldList, pokemonCollectionList))

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

    var collectionRecyclerView: RecyclerView? = null
    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        collectionRecyclerView = recyclerView
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        collectionRecyclerView = null
    }


    override fun onViewRecycled(holder: PokemonCollectionViewHolder) {
        super.onViewRecycled(holder)

        holder.cacheType?.also {type->
            val savedState = holder.onSaveDetailScrollState()
            if (paddingSaveState == null) {
                paddingSaveState = mutableMapOf()
            }
            savedState?.also {
                paddingSaveState?.put(type, it)
            }
        }
        holder.cacheType = null
    }

    override fun onViewDetachedFromWindow(holder: PokemonCollectionViewHolder) {
        super.onViewDetachedFromWindow(holder)
        holder.cacheType?.also {tag->
            val savedState = holder.onSaveDetailScrollState()
            if (paddingSaveState == null) {
                paddingSaveState = mutableMapOf()
            }
            savedState?.also {
                paddingSaveState?.put(tag, it)
            }
        }
        holder.cacheType = null
    }

    fun onSaveDetailScrollState(): Map<String, Pair<Int, Int>> {
        val map = mutableMapOf<String, Pair<Int, Int>>()
        paddingSaveState?.run {
            map.putAll(this)
        }

        val size = pokemonCollectionList.size
        for (pos in 0 until size) {
            collectionRecyclerView?.findViewHolderForAdapterPosition(pos)?.let {
                it as? PokemonCollectionViewHolder
            }?.run {
                val pair = onSaveDetailScrollState()
                pair?.also {pair->
                    this.cacheType?.also {type->
                        map[type] = pair
                    }
                }
            }
        }
        return map
    }

    class PokemonCollectionViewHolder(val binding: ItemPokemoCollectionBinding) :
        RecyclerView.ViewHolder(binding.root) {
            var cacheType: String? = null
        fun onSaveDetailScrollState(): Pair<Int, Int>? {
            return (binding.collectionRecyclerView.layoutManager as? LinearLayoutManager)?.let {
                val visibleItemPos = it.findFirstVisibleItemPosition()
                val offset = it.findViewByPosition(visibleItemPos)?.left ?: 0
                Pair(visibleItemPos, offset)
            }
        }
    }
}