package com.sparkmembership.sparkowner.presentation.ui.more.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sparkmembership.sparkowner.data.entity.MoreItem
import com.sparkmembership.sparkowner.data.enums.ItemTypeMore
import com.sparkmembership.sparkowner.data.response.Contact
import com.sparkmembership.sparkowner.databinding.ItemHeaderBinding
import com.sparkmembership.sparkowner.databinding.ItemMoreBinding

class MoreAdapter(private val items: List<MoreItem>,val onItemListener : OnItemListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_HEADER = 0
        private const val TYPE_LIST_ITEM = 1
    }

    class HeaderViewHolder(val binding: ItemHeaderBinding) : RecyclerView.ViewHolder(binding.root)

    class ListItemViewHolder(val binding: ItemMoreBinding) : RecyclerView.ViewHolder(binding.root){

    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position].type) {
            ItemTypeMore.HEADER -> TYPE_HEADER
            ItemTypeMore.LIST_ITEM -> TYPE_LIST_ITEM
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_HEADER -> {
                val binding = ItemHeaderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                HeaderViewHolder(binding)
            }
            TYPE_LIST_ITEM -> {
                val binding = ItemMoreBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                ListItemViewHolder(binding)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = items[position]

        when (holder) {
            is HeaderViewHolder -> {
                holder.binding.headerTitle.text = item.title
            }
            is ListItemViewHolder -> {
                holder.binding.listItemTitle.text = item.title
                item.iconResId?.let {
                    holder.binding.listItemIcon.setImageResource(it)
                }
                holder.binding.itemMore.setOnClickListener {
                       onItemListener.onItemClick(item)
                }

                holder.binding.itemMore.setOnClickListener {
                    onItemListener.onItemClick(item)
                }
            }
        }

    }

    override fun getItemCount(): Int = items.size

    interface OnItemListener{
        fun onItemClick(item: MoreItem)
    }
}
