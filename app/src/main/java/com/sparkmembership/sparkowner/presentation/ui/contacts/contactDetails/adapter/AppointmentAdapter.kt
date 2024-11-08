package com.sparkmembership.sparkowner.presentation.ui.contacts.contactDetails.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.sparkmembership.sparkfitness.util.DateUtil
import com.sparkmembership.sparkfitness.util.DateUtil.APPOINTMENT_DATE_FORMAT
import com.sparkmembership.sparkowner.data.response.Appointment
import com.sparkmembership.sparkowner.databinding.ItemAppointmentBinding


class AppointmentAdapter(val arrayList: ArrayList<Appointment>) : Adapter<AppointmentAdapter.AppointmentViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppointmentViewHolder {
        return AppointmentViewHolder(ItemAppointmentBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return  arrayList.size
    }

    override fun onBindViewHolder(holder: AppointmentViewHolder, position: Int) {
        val appointment = arrayList[position]
        val date = DateUtil.formatUtcDateString(appointment.startTime, APPOINTMENT_DATE_FORMAT )
        holder.binding.appointmentDetails.text = "${appointment.name} - ${date} "

    }

    class AppointmentViewHolder(itemAppointmentBinding: ItemAppointmentBinding) : RecyclerView.ViewHolder(itemAppointmentBinding.root) {
        val binding = itemAppointmentBinding
    }

}