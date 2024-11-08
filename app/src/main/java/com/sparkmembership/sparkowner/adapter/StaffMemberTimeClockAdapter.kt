package com.sparkmembership.sparkowner.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sparkmembership.sparkfitness.util.DateUtil.calculateWorkHours
import com.sparkmembership.sparkfitness.util.DateUtil.formatTimeInOut
import com.sparkmembership.sparkowner.R
import com.sparkmembership.sparkowner.data.response.StaffMemberTimeClockResult
import com.sparkmembership.sparkowner.databinding.ItemStaffMemberTimeClockBinding

class StaffMemberTimeClockAdapter(val context : Context,val listener : OnStaffMemberListener) : RecyclerView.Adapter<StaffMemberTimeClockAdapter.EmployeeViewHolder>() {

    private var staffMemberTimeClockList: MutableList<StaffMemberTimeClockResult> = mutableListOf()

    fun setTimeSlipHistory(staffMemberTimeClockList: ArrayList<StaffMemberTimeClockResult>) {
        this.staffMemberTimeClockList = staffMemberTimeClockList
        notifyDataSetChanged()
    }

    class EmployeeViewHolder(val binding: ItemStaffMemberTimeClockBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmployeeViewHolder {
        val binding = ItemStaffMemberTimeClockBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EmployeeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EmployeeViewHolder, position: Int) {
        val staffMemberList = staffMemberTimeClockList[position]
        holder.binding.apply {
            employeeName.text = "${staffMemberList.firstName} ${staffMemberList.lastName}"
            if (staffMemberList.timeOut.isNullOrEmpty()) {
                clockedInOutLabel.text = context.getString(R.string.clocked_in_label)
                clockedInOutLabel.setTextColor(context.getColor(R.color.green))
                clockedInOutLabel.setBackgroundResource(R.drawable.rounded_background)
            } else {
                clockedInOutLabel.text = context.getString(R.string.clocked_out_label)
                clockedInOutLabel.setBackgroundResource(R.drawable.rounded_background_orange)
                clockedInOutLabel.setTextColor(context.getColor(R.color.colorAccent))
                totalTime.text = context.getString(R.string.work_hours, calculateWorkHours(staffMemberList.timeIn, staffMemberList.timeOut))
            }
            timeInOut.text = formatTimeInOut(staffMemberList.timeIn, staffMemberList.timeOut)

            holder.binding.root.setOnClickListener {
                listener.onClick(staffMemberList)
            }
        }
    }

    override fun getItemCount(): Int {
        return staffMemberTimeClockList.size
    }

    interface OnStaffMemberListener {
        fun onClick(staffMemberTimeClockResult: StaffMemberTimeClockResult)
    }
}
