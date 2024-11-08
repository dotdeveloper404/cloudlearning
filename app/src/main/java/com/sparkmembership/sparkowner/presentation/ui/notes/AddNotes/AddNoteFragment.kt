package com.sparkmembership.sparkowner.presentation.ui.notes.AddNotes

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.sparkmembership.sparkfitness.util.DateUtil
import com.sparkmembership.sparkfitness.util.toGone
import com.sparkmembership.sparkfitness.util.toVisible
import com.sparkmembership.sparkowner.R
import com.sparkmembership.sparkowner.data.entity.AddNoteEntity
import com.sparkmembership.sparkowner.data.remote.Resource
import com.sparkmembership.sparkowner.data.request.ContactRequest
import com.sparkmembership.sparkowner.data.response.Note
import com.sparkmembership.sparkowner.data.response.NotesMetaResult
import com.sparkmembership.sparkowner.data.response.QuickNote
import com.sparkmembership.sparkowner.data.response.StaffMember
import com.sparkmembership.sparkowner.data.response.filter.ContactType
import com.sparkmembership.sparkowner.databinding.FragmentAddNoteBinding
import com.sparkmembership.sparkowner.databinding.FragmentContactAllnotesBinding
import com.sparkmembership.sparkowner.presentation.components.showDatePickerDialog
import com.sparkmembership.sparkowner.presentation.ui.MainActivity
import com.sparkmembership.sparkowner.presentation.ui.notes.Allnotes.AllNotesFilterBottomSheetFragment
import com.sparkmembership.sparkowner.presentation.ui.notes.NotesViewModel
import com.sparkmembership.sparkowner.util.showDialogWithRecyclerView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AddNoteFragment : Fragment() {
    lateinit var binding: FragmentAddNoteBinding
    private val notesViewModel by viewModels<NotesViewModel>()
    var notesMetaResult : NotesMetaResult? = null
    private var selectedQuickNoteItem: QuickNote? = null
    private var selectedStaffMemberItem: StaffMember? = null
    private var addNoteData : AddNoteEntity ?= null
    private var contactID: Long? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddNoteBinding.inflate(inflater, container, false)
        arguments?.let {
            contactID = it.getLong("contactID")
        }
        setToolBar()
        setClickListener()
        getAddNotesMetaData()
        observerNoteMetaData()
        setQuickNoteSpinner()
        setAssignToSpinner()
        setFollowUpDate()
        return binding.root
    }

    private fun setToolBar() {
        binding.assignToSpinner.rightDrawable(R.drawable.spinner_dropdown_icon)
        binding.selectQuickNoteSpinner.rightDrawable(R.drawable.spinner_dropdown_icon)
        (activity as? MainActivity)?.showToolbar()
        (activity as? MainActivity)?.initializeCustomToolbar(
            title = getString(R.string.add_note),
            toolbarColor = R.color.colorPrimary,
            showBackButton = true,
            icons = listOf(

            ), onBackPress = {
                findNavController().popBackStack()
            }
        )
    }

    private fun setClickListener() {
        binding.addNoteButton.setOnClickListener {
            if (binding.selectQuickNoteSpinner.getInputText().isNotEmpty() || binding.noteText.text.toString().isNotEmpty()){
                addNoteData = AddNoteEntity(
                    note = binding.noteText.text.toString(),
                    contactID = contactID!!,
                    assignedUserID = selectedStaffMemberItem?.id,
                    followUpDate = if (binding.followUpDate.text.trim().toString().isEmpty()) null else DateUtil.ConvertCalenderDatetoUtcFormat(binding.followUpDate.text.trim().toString())
                )
                lifecycleScope.launch {
                    notesViewModel.addNote(addNoteEntity = addNoteData!!)
                }
                observerAddNote()
            }else{
                binding.selectQuickNoteSpinner.showError()
            }
        }
    }

    private fun observerAddNote() {
        notesViewModel.addNoteDto.observe(viewLifecycleOwner){
            when (it) {
                is Resource.Loading -> {
                    binding.includedProgressLayout.loaderView.toVisible()
                }

                is Resource.Success -> {
                    findNavController().popBackStack()
                    binding.includedProgressLayout.loaderView.toGone()
                }

                is Resource.Error -> {
                    binding.includedProgressLayout.loaderView.toGone()
                }
            }
        }
    }

    private fun getAddNotesMetaData() {
        lifecycleScope.launch {
            notesViewModel.getAddNotesMetaData()
        }
    }

    private fun observerNoteMetaData() {
        notesViewModel.addNotesMetaDataDto.observe(viewLifecycleOwner){
            when (it) {
                is Resource.Loading -> {
                    binding.includedProgressLayout.loaderView.toVisible()
                }

                is Resource.Success -> {
                    notesMetaResult = it.data?.result
                    binding.includedProgressLayout.loaderView.toGone()
                }

                is Resource.Error -> {
                    binding.includedProgressLayout.loaderView.toGone()
                }
            }
        }
    }

    private fun setFollowUpDate() {
        binding.followUpDate.setOnClickListener {
            showDatePickerDialog(requireContext(), binding.followUpDate)
        }
    }
    private fun setQuickNoteSpinner() {
        binding.selectQuickNoteSpinner.setOnClickListener {
            showDialogWithRecyclerView(
                requireContext(),
                data = notesMetaResult!!.quickNotes,
                displayText = { it.quickTemplate },
                onItemSelect = { selectedItem  ->
                },
                onSubmitClick = { selectedItem->
                    binding.selectQuickNoteSpinner.textView.setText(selectedItem?.quickTemplate.toString())
                    binding.noteText.setText(selectedItem?.quickTemplate.toString())
                    selectedQuickNoteItem = selectedItem
                },
                selectedItem = selectedQuickNoteItem
            )
        }
    }

    private fun setAssignToSpinner() {
        binding.assignToSpinner.setOnClickListener {
            showDialogWithRecyclerView(
                requireContext(),
                data = notesMetaResult!!.staffMembers,
                displayText = { it.staffName },
                onItemSelect = { selectedItem  ->
                },
                onSubmitClick = { selectedItem->
                    binding.assignToSpinner.textView.text = selectedItem?.staffName
                    addNoteData?.assignedUserID = selectedItem?.id
                    selectedStaffMemberItem = selectedItem
                },
                selectedItem = selectedStaffMemberItem
            )
        }
    }

}