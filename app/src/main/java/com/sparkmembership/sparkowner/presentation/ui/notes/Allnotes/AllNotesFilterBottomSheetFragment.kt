package com.sparkmembership.sparkowner.presentation.ui.notes.Allnotes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.sparkmembership.sparkowner.databinding.FragmentAllNotesFilterBottomSheetBinding

class AllNotesFilterBottomSheetFragment(val filterNotesDto : ContactAllNotesFragment.FilterAllNotesDto) : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentAllNotesFilterBottomSheetBinding
    private var listener: OnButtonClickListener? = null

    fun setListener(listener: OnButtonClickListener) {
        this.listener = listener
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAllNotesFilterBottomSheetBinding.inflate(layoutInflater, container, false)
        init()
        setItemClickListener()
        return binding.root
    }

    private fun init(){
        binding.switchSmsNotes.isChecked = filterNotesDto.showSMSNotes
        binding.switchConnectedNotes.isChecked = filterNotesDto.showAllConnectedNotes
        binding.switchOrderNotes.isChecked = filterNotesDto.completedOrderNotes
        binding.btnCancel.setOnClickListener {
            dismiss()
        }


    }

    private fun setItemClickListener() {
        binding.switchSmsNotes.setOnCheckedChangeListener { _, isChecked ->
            filterNotesDto.showSMSNotes = isChecked
        }
        binding.switchConnectedNotes.setOnCheckedChangeListener { _, isChecked ->
            filterNotesDto.showAllConnectedNotes = isChecked
        }
        binding.switchOrderNotes.setOnCheckedChangeListener { _, isChecked ->
            filterNotesDto.completedOrderNotes = isChecked
        }

        binding.applyFilterButton.setOnClickListener {
            listener?.onApplyFilter()
            dismiss()
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    interface OnButtonClickListener {
        fun onApplyFilter()
    }
}