package com.sparkmembership.sparkowner.presentation.ui.rateUs

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.sparkmembership.sparkowner.data.entity.RateUsOption
import com.sparkmembership.sparkowner.databinding.ItemRateUsBinding
import com.sparkmembership.sparkowner.presentation.listeners.OnOptionSelectedListener

class RateUsAdapter(
    private val arrayList: ArrayList<RateUsOption>,
    private val isMultiSelect: Boolean,
    private val onOptionSelectedListener: OnOptionSelectedListener
) : Adapter<RateUsAdapter.RateUsViewHolder>() {


    private var selectedPosition = RecyclerView.NO_POSITION
    private val selectedOptionList = ArrayList<RateUsOption>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RateUsViewHolder {
        return RateUsViewHolder(
            ItemRateUsBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    override fun onBindViewHolder(holder: RateUsViewHolder, position: Int) {
        val option = arrayList[position]
        holder.binding.textOption.text = option.name

        holder.itemView.isSelected = option.isSelected


        holder.itemView.setOnClickListener {
            if (isMultiSelect) {
                // Multi-select logic
                option.isSelected = !option.isSelected
                notifyItemChanged(position)
                if (option.isSelected) {
                    selectedOptionList.add(option)
                } else {
                    selectedOptionList.remove(option)
                }
                onOptionSelectedListener.onOptionSelected(selectedOptionList)

            } else {
                // Single-select logic
                if (selectedPosition != RecyclerView.NO_POSITION) {
                    arrayList[selectedPosition].isSelected = false
                    notifyItemChanged(selectedPosition)
                }
                option.isSelected = true
                selectedPosition = position
                notifyItemChanged(position)
                selectedOptionList.clear()
                selectedOptionList.add(option)
                onOptionSelectedListener.onOptionSelected(selectedOptionList)
            }
        }
    }


    class RateUsViewHolder(itemRateUsBinding: ItemRateUsBinding) :
        ViewHolder(itemRateUsBinding.root) {

        val binding = itemRateUsBinding

    }
}