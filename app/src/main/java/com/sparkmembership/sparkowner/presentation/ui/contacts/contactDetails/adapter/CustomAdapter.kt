package com.sparkmembership.sparkowner.presentation.ui.contacts.contactDetails.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.sparkmembership.sparkowner.data.response.CustomField
import com.sparkmembership.sparkowner.databinding.ItemCustomFieldsBinding

class CustomAdapter(val list: ArrayList<CustomField>) : Adapter<CustomAdapter.CustomViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        return CustomViewHolder(ItemCustomFieldsBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        val customField = list[position]
        holder.binding.txtName.text = customField.name
        holder.binding.txtValue.text = customField.value
    }

    class CustomViewHolder(itemCustomFieldsBinding: ItemCustomFieldsBinding) : ViewHolder(itemCustomFieldsBinding.root){

        val binding = itemCustomFieldsBinding

    }
}