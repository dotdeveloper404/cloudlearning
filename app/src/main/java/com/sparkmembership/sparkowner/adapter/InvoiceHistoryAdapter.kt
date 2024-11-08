package com.sparkmembership.sparkowner.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sparkmembership.sparkfitness.util.DateUtil.formatStringToDateAndTime
import com.sparkmembership.sparkowner.R
import com.sparkmembership.sparkowner.data.response.InvoiceHistory
import com.sparkmembership.sparkowner.databinding.ItemInvoiceBinding

class InvoiceHistoryAdapter(val context : Context, val listener : OnInvoiceItem) : RecyclerView.Adapter<InvoiceHistoryAdapter.EmployeeViewHolder>() {

    private var invoiceHistoryList: MutableList<InvoiceHistory> = mutableListOf()

    fun setTimeSlipHistory(invoiceHistoryList: ArrayList<InvoiceHistory>) {
        this.invoiceHistoryList = invoiceHistoryList
        notifyDataSetChanged()
    }

    class EmployeeViewHolder(val binding: ItemInvoiceBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmployeeViewHolder {
        val binding = ItemInvoiceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EmployeeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EmployeeViewHolder, position: Int) {
        val invoiceItem = invoiceHistoryList[position]
        holder.binding.apply {
            employeeName.text = invoiceItem.contactName
            if (invoiceItem.amountStillOwed > 0.0) {
                paymentStatus.text = context.getString(R.string.unPaid)
                paymentStatus.setBackgroundResource(R.drawable.rounded_background_orange)
                paymentStatus.setTextColor(context.getColor(R.color.colorAccent))
            } else {
                paymentStatus.text = context.getString(R.string.paid)
                paymentStatus.setTextColor(context.getColor(R.color.green))
                paymentStatus.setBackgroundResource(R.drawable.rounded_background)
            }
            invoiceDate.text = formatStringToDateAndTime(invoiceItem.invoiceTime)
            invoiceNo.text = context.getString(R.string.invoiceNumber,invoiceItem.invoiceID.toString())
            totalAmount.text = context.getString(R.string.totalAmount,invoiceItem.total.toString())
            holder.binding.root.setOnClickListener {
                listener.onClick(invoiceItem)
            }
        }
    }

    override fun getItemCount(): Int {
        return invoiceHistoryList.size
    }

    interface OnInvoiceItem {
        fun onClick(invoiceHistoryDto: InvoiceHistory)
    }
}
