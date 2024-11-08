package com.sparkmembership.sparkowner.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.recyclerview.widget.RecyclerView
import com.sparkmembership.sparkowner.R
import com.sparkmembership.sparkowner.databinding.ItemCustomSpinnerBinding
import com.sparkmembership.sparkowner.databinding.ItemGenericSpinnerBinding
import com.sparkmembership.sparkowner.presentation.components.GenericDialogAdapter.ViewHolder


class SpinnerDialogAdapter<T>(
    private val data: List<T>,
    private val displayText: (T) -> String,
    private val initialSelectedItem: T?,  // New: Previously selected item
    private val onItemClickListener: (T) -> Unit
) : RecyclerView.Adapter<SpinnerDialogAdapter.ViewHolder<T>>() {

    private var selectedPosition: Int = data.indexOf(initialSelectedItem)  // Initialize with the selected item's position

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder<T> {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_custom_spinner, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder<T>, position: Int) {
        val item = data[position]
        holder.bind(item, displayText(item), position == selectedPosition)

        holder.itemView.setOnClickListener {
            val previousSelectedPosition = selectedPosition
            selectedPosition = holder.bindingAdapterPosition
            onItemClickListener(item)
            notifyItemChanged(previousSelectedPosition)
            notifyItemChanged(selectedPosition)
        }
    }

    override fun getItemCount(): Int = data.size

    class ViewHolder<T>(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val itemText: RadioButton = itemView.findViewById(R.id.spinner_item)

        fun bind(item: T, text: String, isSelected: Boolean) {
            itemText.text = text
            itemText.isChecked = isSelected
        }
    }
}