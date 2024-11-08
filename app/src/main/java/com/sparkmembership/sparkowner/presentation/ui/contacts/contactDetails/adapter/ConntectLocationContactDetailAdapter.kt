package com.sparkmembership.sparkowner.presentation.ui.contacts.contactDetails.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.sparkmembership.sparkowner.R
import com.sparkmembership.sparkowner.data.response.Contacts
import com.sparkmembership.sparkowner.databinding.ItemConnectedLocationsContactDetailsBinding
import com.sparkmembership.sparkowner.presentation.listeners.OnContactDetailConnectedLocationListener
import com.sparkmembership.sparkowner.util.GlideUtil

class ConntectLocationContactDetailAdapter(val context: Context, val list:ArrayList<Contacts>, val onContactListener: OnContactDetailConnectedLocationListener) :
    Adapter<ConntectLocationContactDetailAdapter.ConnectedViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConnectedViewHolder {
        return ConnectedViewHolder(ItemConnectedLocationsContactDetailsBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ConnectedViewHolder, position: Int) {
        val contacts = list[position]
        GlideUtil.loadImage(context,contacts.profileImage,holder.binding.profilePicture, R.drawable.image_placeholder)
        holder.itemView.setOnClickListener {
            onContactListener.onItemClick(contacts)
        }
    }


    class ConnectedViewHolder(item: ItemConnectedLocationsContactDetailsBinding) : ViewHolder(item.root){
        val binding = item
    }


}