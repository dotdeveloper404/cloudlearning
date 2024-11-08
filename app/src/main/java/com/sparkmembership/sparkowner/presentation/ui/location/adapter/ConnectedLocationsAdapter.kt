package com.sparkmembership.sparkowner.presentation.ui.location.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.sparkmembership.sparkowner.data.response.ConnectedLocation
import com.sparkmembership.sparkowner.databinding.ItemConnectedLocationBinding
import com.sparkmembership.sparkowner.presentation.listeners.OnConnectedLocationsItemClickListener

class ConnectedLocationsAdapter(val arrayList: ArrayList<ConnectedLocation>, val listener: OnConnectedLocationsItemClickListener) : RecyclerView.Adapter<ConnectedLocationsAdapter.MyViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(ItemConnectedLocationBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val connectedLocation = arrayList[position]
        holder.binding.textView.text = connectedLocation.locationName

        holder.itemView.setOnClickListener {
            listener.onConnectedLocationsItemClick(connectedLocation)
        }
    }

    class MyViewHolder(itemConnectedLocationBinding: ItemConnectedLocationBinding) : ViewHolder(itemConnectedLocationBinding.root){

        val binding = itemConnectedLocationBinding
    }
}