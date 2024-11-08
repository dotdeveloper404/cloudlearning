package com.sparkmembership.sparkowner.presentation.ui.timeslip.TimeSlipsHistory

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.sparkmembership.sparkfitness.util.DateUtil
import com.sparkmembership.sparkfitness.util.DateUtil.CALENDAR_FORMAT
import com.sparkmembership.sparkfitness.util.DateUtil.CALENDAR_FORMAT2
import com.sparkmembership.sparkfitness.util.showToast
import com.sparkmembership.sparkowner.databinding.FragmentTimeSlipFilterBottomSheetBinding
import com.sparkmembership.sparkowner.presentation.components.getFirstAndLastDayOfMonth
import com.sparkmembership.sparkowner.presentation.components.showDatePickerDialog
import com.sparkmembership.sparkowner.presentation.components.stringToCalendar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TimeSlipFilterBottomSheetFragment(val filterTimeSlipDto: TimeSlipsHistory.FilterTimeSlipDto?) : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentTimeSlipFilterBottomSheetBinding
    private var listener: OnButtonClickListener? = null

    fun setListener(listener: OnButtonClickListener) {
        this.listener = listener
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTimeSlipFilterBottomSheetBinding.inflate(layoutInflater, container, false)
        init()
        setItemClickListener()
        return binding.root
    }

    private fun init() {

        val (firstDay, lastDay) = getFirstAndLastDayOfMonth(CALENDAR_FORMAT)

        if (filterTimeSlipDto?.startDate?.isNotEmpty() == true){
            binding.spinnerStartDate.text = filterTimeSlipDto.startDate
        }else{
            binding.spinnerStartDate.text = firstDay
        }
        if (filterTimeSlipDto?.endDate?.isNotEmpty() == true){
            binding.spinnerEndDate.text = filterTimeSlipDto.endDate
        }else{
            binding.spinnerEndDate.text = lastDay
        }
    }

    private fun setItemClickListener() {
        binding.spinnerStartDate.setOnClickListener {
            showDatePickerDialog(requireContext(), binding.spinnerStartDate,selectedDate = stringToCalendar(
                CALENDAR_FORMAT2,binding.spinnerStartDate.text.toString()))
        }
        binding.spinnerEndDate.setOnClickListener {
            showDatePickerDialog(requireContext(), binding.spinnerEndDate,selectedDate = stringToCalendar(CALENDAR_FORMAT2,binding.spinnerEndDate.text.toString()))
        }
        binding.btnApplyFilter.setOnClickListener {
            val startDate = DateUtil.ConvertCalenderDatetoUtcFormat(binding.spinnerStartDate.text.toString())
            val endDate = DateUtil.ConvertCalenderDatetoUtcFormat(binding.spinnerEndDate.text.toString())
            filterTimeSlipDto?.startDate = binding.spinnerStartDate.text.toString()
            filterTimeSlipDto?.endDate = binding.spinnerEndDate.text.toString()
            if (binding.spinnerStartDate.text.toString() != "" && binding.spinnerStartDate.text.toString() != ""){
                listener?.onApplyFilter(startDate,endDate)
            }else{
                showToast("Please Enter Date Correctly",requireContext())
            }
            dismiss()
        }
        binding.btnResetFilter.setOnClickListener {
            listener?.onResetFilter()
            filterTimeSlipDto?.startDate = ""
            filterTimeSlipDto?.endDate = ""
            dismiss()
        }
        binding.closeButton.setOnClickListener {
            dismiss()
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    interface OnButtonClickListener {
        fun onApplyFilter(startDate: String, endDate : String)
        fun onResetFilter()
    }

}