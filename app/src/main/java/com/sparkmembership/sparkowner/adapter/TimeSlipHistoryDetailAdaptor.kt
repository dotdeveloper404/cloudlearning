package com.sparkmembership.sparkowner.adapter

import androidx.recyclerview.widget.RecyclerView
import com.sparkmembership.sparkowner.data.response.TimeSlipUserDetail
import android.view.LayoutInflater
import android.view.ViewGroup
import com.sparkmembership.sparkfitness.util.DateUtil
import com.sparkmembership.sparkfitness.util.DateUtil.calculateWorkHourForMonth
import com.sparkmembership.sparkfitness.util.DateUtil.calculateWorkHours
import com.sparkmembership.sparkfitness.util.toVisible
import com.sparkmembership.sparkowner.data.response.TimeSlip
import com.sparkmembership.sparkowner.databinding.ItemTimeSlipDetailBinding
import com.sparkmembership.sparkowner.databinding.ItemTimeSlipDetailHeaderBinding

class TimeSlipHistoryDetailAdaptor(val currentMonthVisible : Boolean = false) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var timeSlipHistoryList: MutableList<TimeSlipUserDetail> = mutableListOf()

    private fun mergeTimeSlipUserDetails(
        existingDetail: TimeSlipUserDetail,
        newDetail: TimeSlipUserDetail
    ): TimeSlipUserDetail {
        val combinedTimeSlips = existingDetail.timeSlip + newDetail.timeSlip
        return existingDetail.copy(
            currentPayPeriod = existingDetail.currentPayPeriod,
            totalTime = existingDetail.totalTime,
            timeSlip = combinedTimeSlips as MutableList<TimeSlip>
        )
    }

    fun setTimeSlipHistoryDetail(newTimeSlipList: ArrayList<TimeSlipUserDetail>) {
         timeSlipHistoryList.clear()
        for (newDetail in newTimeSlipList) {
            val existingDetail = timeSlipHistoryList.find { it.currentPayPeriod == newDetail.currentPayPeriod }
            if (existingDetail != null) {
                val mergedDetail = mergeTimeSlipUserDetails(existingDetail, newDetail)
                timeSlipHistoryList[timeSlipHistoryList.indexOf(existingDetail)] = mergedDetail
            } else {
                timeSlipHistoryList.add(newDetail)
            }
        }
        notifyDataSetChanged()
    }

    companion object {
        const val VIEW_TYPE_HEADER = 0
        const val VIEW_TYPE_ITEM = 1
    }

    override fun getItemViewType(position: Int): Int {
        var count = 0
        for (detail in timeSlipHistoryList) {
            if (position == count) return VIEW_TYPE_HEADER
            count += 1 + detail.timeSlip.size
            if (position < count) return VIEW_TYPE_ITEM
        }
        throw IndexOutOfBoundsException("Invalid position")
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_HEADER) {
            val binding = ItemTimeSlipDetailHeaderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            HeaderViewHolder(binding)
        } else {
            val binding = ItemTimeSlipDetailBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            TimeSlipViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        var count = 0
        for (detail in timeSlipHistoryList) {
            if (position == count) {
                holder as HeaderViewHolder
                holder.bind(detail.currentPayPeriod,detail.timeSlip,currentMonthVisible)
                return
            }
            count += 1
            if (position < count + detail.timeSlip.size) {
                holder as TimeSlipViewHolder
                val timeSlip = detail.timeSlip[position - count]
                holder.bind(timeSlip)
                return
            }
            count += detail.timeSlip.size
        }
    }

    override fun getItemCount(): Int {
        return timeSlipHistoryList.sumOf { 1 + it.timeSlip.size }
    }

    class HeaderViewHolder(val binding: ItemTimeSlipDetailHeaderBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(payPeriod: String,totalTime:List<TimeSlip>,currentMonthVisible:Boolean) {
            binding.currentPayPeriod.text = payPeriod
            binding.totalMonthTime.text = calculateWorkHourForMonth(totalTime)
            if (currentMonthVisible){
                binding.currentPayPeriodLabel.toVisible()
            }
        }
    }

    class TimeSlipViewHolder(val binding: ItemTimeSlipDetailBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(timeSlip: TimeSlip) {
            if (timeSlip.timeOut != null && timeSlip.timeOut != ""){
                binding.totalTime.text = calculateWorkHours(timeSlip.timeIn, timeSlip.timeOut) + " Hour(s)"
            }else{
                binding.totalTime.text = "-"
            }
            binding.timeInOut.text = DateUtil.formatTimeInOut(timeSlip.timeIn, timeSlip.timeOut)
            binding.itemDate.text = DateUtil.FormatTimeToDate(timeSlip.timeIn)
        }
    }
}
