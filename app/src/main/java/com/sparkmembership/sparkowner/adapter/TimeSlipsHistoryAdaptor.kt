package com.sparkmembership.sparkowner.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sparkmembership.sparkowner.data.response.Contact
import com.sparkmembership.sparkowner.data.response.TimeSlipHistory
import com.sparkmembership.sparkowner.databinding.ItemTimeSlipHistoryBinding

class TimeSlipsHistoryAdaptor(val listener : TimeSlipsHistoryAdaptor.OnTimeSlipListener? = null) :
    RecyclerView.Adapter<TimeSlipsHistoryAdaptor.UserViewHolder>() {

    private var timeSlipHistoryList: MutableList<TimeSlipHistory> = mutableListOf()

    fun setTimeSlipHistory(timeSlipList: ArrayList<TimeSlipHistory>) {
        this.timeSlipHistoryList = timeSlipList
        notifyDataSetChanged()
    }

    class UserViewHolder(val binding: ItemTimeSlipHistoryBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = ItemTimeSlipHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val timeSlipItem = timeSlipHistoryList[position]
        holder.binding.tvName.text = timeSlipItem.userName
        holder.binding.tvDuration.text = timeSlipItem.totalTime
        holder.binding.timeSlipItem.setOnClickListener {
            listener?.onItemClick(timeSlipItem)
        }

    }

    override fun getItemCount(): Int = timeSlipHistoryList.size

    interface OnTimeSlipListener{
        fun onItemClick(timeSlipHistory:TimeSlipHistory)
    }
}
