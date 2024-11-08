package com.sparkmembership.sparkowner.presentation.components

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sparkmembership.sparkowner.R
import com.sparkmembership.sparkowner.databinding.ItemGenericSpinnerBinding


class GenericDialogAdapter<T>(
    private val data: ArrayList<T>,
    private val displayText: (T) -> String,
    selectedData: ArrayList<T>,
    private val onItemClickListener: OnItemClickListener<T>
) : RecyclerView.Adapter<GenericDialogAdapter.ViewHolder<T>>() {

    private val selectedItems = mutableSetOf<T>().apply {
        addAll(selectedData)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder<T> {
        return ViewHolder(ItemGenericSpinnerBinding.inflate(LayoutInflater.from(parent.context),parent,false))

    }

    override fun onBindViewHolder(holder: ViewHolder<T>, position: Int) {
        val item = data[position]
        holder.binding.spinnerText.text = displayText(item)
        holder.binding.spinnerItem.isChecked = selectedItems.contains(item)

        val drawableId: Int =
            if (selectedItems.contains(item)) R.drawable.icon_checked else R.drawable.icon_unchecked
        holder.binding.spinnerItem.setCompoundDrawablesWithIntrinsicBounds(drawableId, 0, 0, 0)

        holder.binding.spinnerItem.setOnClickListener {
            if (selectedItems.contains(item)) {
                selectedItems.remove(item)
            } else {
                selectedItems.add(item)
            }

            holder.binding.spinnerItem.setCompoundDrawablesWithIntrinsicBounds(drawableId, 0, 0, 0)
            onItemClickListener.onItemSelected(selectedItems.toList())
            notifyItemChanged(position)
        }
    }

    override fun getItemCount(): Int = data.size

    interface OnItemClickListener<T> {
        fun onItemSelected(selectedItems: List<T>)
    }

    class ViewHolder<T>(itemGenericSpinnerBinding: ItemGenericSpinnerBinding) : RecyclerView.ViewHolder(itemGenericSpinnerBinding.root) {
        val binding = itemGenericSpinnerBinding

    }
}

