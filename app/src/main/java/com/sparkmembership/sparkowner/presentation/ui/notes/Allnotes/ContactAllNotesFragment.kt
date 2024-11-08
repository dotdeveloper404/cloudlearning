package com.sparkmembership.sparkowner.presentation.ui.notes.Allnotes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.sparkmembership.sparkfitness.util.toGone
import com.sparkmembership.sparkfitness.util.toVisible
import com.sparkmembership.sparkowner.R
import com.sparkmembership.sparkowner.adapter.AllNotesAdapter
import com.sparkmembership.sparkowner.data.remote.Resource
import com.sparkmembership.sparkowner.data.response.Note
import com.sparkmembership.sparkowner.databinding.FragmentContactAllnotesBinding
import com.sparkmembership.sparkowner.presentation.ui.MainActivity
import com.sparkmembership.sparkowner.presentation.ui.contacts.filter.ContactFilterBottomSheetFragment
import com.sparkmembership.sparkowner.presentation.ui.notes.NotesViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ContactAllNotesFragment : Fragment(), AllNotesAdapter.OnNotesClickListener,
    AllNotesFilterBottomSheetFragment.OnButtonClickListener {
    lateinit var binding: FragmentContactAllnotesBinding
    private val notesViewModel by viewModels<NotesViewModel>()
    var notesList = ArrayList<Note>()
    private var contactID: Long? = null
    var allNotesAdapter : AllNotesAdapter? = null
    var filterNotesDto = FilterAllNotesDto()
    var deleteNoteId : Note? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentContactAllnotesBinding.inflate(inflater, container, false)
        arguments?.let {
            contactID = it.getLong("contactID")
        }
        setToolBar()
        initRecyclerView()
        initListeners()
        getAllNotes()
        observerNotes()
        return binding.root
    }

    private fun initListeners() {
        val bundle = Bundle()
        bundle.putLong("contactID", contactID!!)

        binding.fabAddNote.setOnClickListener {
            findNavController().navigate(R.id.addNoteFragment,bundle)
        }
    }

    private fun setToolBar() {
        (activity as? MainActivity)?.showToolbar()
        (activity as? MainActivity)?.initializeCustomToolbar(
            title = getString(R.string.notes),
            toolbarColor = R.color.colorPrimary,
            showBackButton = true,
            icons = listOf(
                R.drawable.icon_filter to {
                    val bottomSheetFragment =
                        AllNotesFilterBottomSheetFragment(filterNotesDto)
                    bottomSheetFragment.setListener(this)
                    bottomSheetFragment.show(parentFragmentManager, "AllNotesFilterBottomSheetFragment")
                }
            ), onBackPress = {
                findNavController().popBackStack()
            }
        )
    }


    private fun initRecyclerView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        allNotesAdapter = AllNotesAdapter(requireContext(),this,contactID)
        binding.recyclerView.adapter = allNotesAdapter
    }

    private fun observerNotes() {
        notesViewModel.allNotesDto.observe(viewLifecycleOwner){
            when (it) {
                is Resource.Loading -> {
                    binding.includedProgressLayout.loaderView.toVisible()
                    binding.notFoundDataLayout.toGone()
                }

                is Resource.Success -> {
                    notesList.clear()
                    binding.includedProgressLayout.loaderView.toGone()
                    notesList.addAll(it.data?.result as ArrayList<Note>)
                    allNotesAdapter?.setAllNoteList(notesList)
                    if (it.data.result.isEmpty()) binding.notFoundDataLayout.toVisible()
                }

                is Resource.Error -> {
                    binding.includedProgressLayout.loaderView.toGone()
                    binding.notFoundDataLayout.toVisible()
                }
            }
        }
    }

    private fun observerDeleteNote(deleteNoteId : Note) {
        notesViewModel.deleteNoteDto.observe(viewLifecycleOwner){
            when (it) {
                is Resource.Loading -> {
                    binding.includedProgressLayout.loaderView.toVisible()
                }

                is Resource.Success -> {
                    notesList.remove(deleteNoteId)
                    allNotesAdapter?.setAllNoteList(notesList)
                    if (notesList.size <= 0) binding.notFoundDataLayout.toVisible()
                    binding.includedProgressLayout.loaderView.toGone()
                }

                is Resource.Error -> {
                    binding.includedProgressLayout.loaderView.toGone()
                }
            }
        }
    }

    private fun getAllNotes() {
        lifecycleScope.launch {
            notesViewModel.getAllNotes(contactId = contactID!!,showSMSNotes = filterNotesDto.showSMSNotes,showAllConnectedNotes = filterNotesDto.showAllConnectedNotes,completedOrderNotes = filterNotesDto.completedOrderNotes)
        }
    }

    override fun onDelete(note: Note) {
        deleteNoteId = note
        lifecycleScope.launch {
            notesViewModel.deleteNote(noteId = note.noteID)
        }

        observerDeleteNote(deleteNoteId!!)

    }

    data class FilterAllNotesDto(
         var showSMSNotes : Boolean = true,
         var showAllConnectedNotes : Boolean = true,
         var completedOrderNotes : Boolean = false
    )

    override fun onApplyFilter() {
        lifecycleScope.launch {
            notesViewModel.getAllNotes(contactId = contactID!!,showSMSNotes = filterNotesDto.showSMSNotes,showAllConnectedNotes = filterNotesDto.showAllConnectedNotes,completedOrderNotes = filterNotesDto.completedOrderNotes)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        (activity as? MainActivity)?.hideToolbar()
    }

}