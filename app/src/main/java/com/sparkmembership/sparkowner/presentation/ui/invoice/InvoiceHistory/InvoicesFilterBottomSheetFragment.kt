package com.sparkmembership.sparkowner.presentation.ui.invoice.InvoiceHistory

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.sparkmembership.sparkfitness.util.DateUtil.DATE_TIME_FORMAT
import com.sparkmembership.sparkfitness.util.DateUtil.UTC_DATE_FORMAT_3
import com.sparkmembership.sparkfitness.util.DateUtil.formatStringToDateAndTime
import com.sparkmembership.sparkfitness.util.DateUtil.formatUtcDateString
import com.sparkmembership.sparkfitness.util.toGone
import com.sparkmembership.sparkfitness.util.toVisible
import com.sparkmembership.sparkowner.R
import com.sparkmembership.sparkowner.adapter.ContactAdapter
import com.sparkmembership.sparkowner.data.enums.InvoiceStatus
import com.sparkmembership.sparkowner.data.remote.Resource
import com.sparkmembership.sparkowner.data.response.Contact
import com.sparkmembership.sparkowner.databinding.FragmentInvoicesFilterBottomSheetBinding
import com.sparkmembership.sparkowner.presentation.components.CustomButton
import com.sparkmembership.sparkowner.presentation.components.TextView
import com.sparkmembership.sparkowner.presentation.components.showDateTimePicker
import com.sparkmembership.sparkowner.presentation.ui.contacts.AllContacts.ContactsViewModel
import com.sparkmembership.sparkowner.util.PaginationScrollListener
import com.sparkmembership.sparkowner.util.showDialogWithRecyclerView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class InvoicesFilterBottomSheetFragment(val invoiceHistoryMeta : InvoicesFragment.InvoiceHistoryData) : BottomSheetDialogFragment(),
    ContactAdapter.OnContactListener {
    private lateinit var binding: FragmentInvoicesFilterBottomSheetBinding
    private var listener: OnButtonClickListener? = null
    private var dialogView : View? = null
    private var recyclerView : RecyclerView? = null
    private var cancelBtn : ImageView? = null
    private var btnSubmit : CustomButton? = null
    private var dialogProgressBar : View? = null
    private var allContactSearchView : TextView? = null
    private val contactsViewModel by viewModels<ContactsViewModel>()
    private lateinit var contactAdapter: ContactAdapter
    private var currentPage: Int = 0
    private var isLastPage = false
    private var totalPages: Long = 0
    private var size: Int = 25
    private var isLoading = false
    private var isSearching = false
    var contactList = ArrayList<Contact>()
    private var dialog : AlertDialog? = null
    var onSelectedInvoiceItem : InvoiceStatus? = null

    fun setListener(listener: OnButtonClickListener) {
        this.listener = listener
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentInvoicesFilterBottomSheetBinding.inflate(layoutInflater, container, false)
        getAllContacts(currentPage = currentPage, size = size)
        contactAdapter = ContactAdapter(this)
        observeViewModel()
        init()
        setItemClickListener()
        return binding.root
    }

    private fun init() {
        binding.startDateTime.text = formatStringToDateAndTime(invoiceHistoryMeta.startDate)
        binding.endDateTime.text = formatStringToDateAndTime(invoiceHistoryMeta.endDate)

        if (invoiceHistoryMeta.invoiceType != null){
            binding.selectInvoice.text = InvoiceStatus.entries[invoiceHistoryMeta.invoiceType!!].displayName

            addSingleChipToGroup(
                chipGroup = binding.chipGroupTags,
                item = InvoiceStatus.entries[invoiceHistoryMeta.invoiceType!!].displayName,
                onChipRemoved = {
                    binding.selectInvoice.text = ""
                }
            )
        }

        if (invoiceHistoryMeta.contact?.contactID != null){
            binding.contact.text =  invoiceHistoryMeta.contact?.firstName
        }


    }

    private fun observeViewModel() {
        contactsViewModel.allContacts.observe(viewLifecycleOwner) { event ->
            val it = event.getContentIfNotHandled()
            when (it) {
                is Resource.Loading -> {
                    binding.includedProgressLayout.loaderView.toVisible()
                    dialogProgressBar?.toVisible()
                }

                is Resource.Success -> {
                    contactList.addAll(it.data!!.result)
                    contactAdapter.setContacts(contactList)
                    if (it.data.result.size < 25) isLastPage = true
                    isLoading = false
                    dialogProgressBar?.toGone()
                    binding.includedProgressLayout.loaderView.toGone()
                }

                is Resource.Error -> {
                    binding.includedProgressLayout.loaderView.toGone()
                    dialogProgressBar?.toGone()
                }

                else -> {
                    dialogProgressBar?.toGone()
                }
            }
        }
    }

    private fun setItemClickListener() {
        binding.startDateTime.setOnClickListener{
            showDateTimePicker(requireContext(),binding.startDateTime)

        }
        binding.endDateTime.setOnClickListener{
            showDateTimePicker(requireContext(),binding.endDateTime)
        }

        binding.contact.setOnClickListener {
            setContactDialog()
        }

        binding.selectInvoice.setOnClickListener {
            showDialogWithRecyclerView(
                requireContext(),
                data = InvoiceStatus.entries,
                displayText = { it.displayName },
                onItemSelect = {
                },
                onSubmitClick = { selectedItem  ->
                    binding.selectInvoice.text = selectedItem?.displayName
                    invoiceHistoryMeta.invoiceType = selectedItem?.id
                    onSelectedInvoiceItem = selectedItem
                    addSingleChipToGroup(
                        chipGroup = binding.chipGroupTags,
                        item = selectedItem!!.displayName,
                        onChipRemoved = { removedItem ->
                            onSelectedInvoiceItem = null
                            invoiceHistoryMeta.invoiceType = null
                            binding.selectInvoice.text = ""
                        }
                    )
                },
                selectedItem = onSelectedInvoiceItem
            )

        }

        binding.btnApplyFilter.setOnClickListener{
            invoiceHistoryMeta.startDate = formatUtcDateString(binding.startDateTime.text.toString(),UTC_DATE_FORMAT_3,DATE_TIME_FORMAT)
            invoiceHistoryMeta.endDate = formatUtcDateString(binding.endDateTime.text.toString(),UTC_DATE_FORMAT_3,DATE_TIME_FORMAT)
            listener?.onApplyFilter()
            dismiss()
        }
        binding.btnResetFilter.setOnClickListener{
            listener?.onResetFilter()
            dismiss()
        }

        binding.closeButton.setOnClickListener {
            dismiss()
        }
    }

    fun addSingleChipToGroup(
        chipGroup: ChipGroup,
        item: String,
        onChipRemoved: (String) -> Unit
    ) {
        chipGroup.removeAllViews()

        val chip = layoutInflater.inflate(R.layout.item_chip, chipGroup, false) as Chip
        chip.text = item

        chip.setOnCloseIconClickListener {
            onChipRemoved(item)
            chipGroup.removeView(chip)
        }

        chipGroup.addView(chip)
    }

    private fun getAllContacts(
        currentPage: Int? = null,
        size: Int? = null,
        search: String? = null
    ) {
        lifecycleScope.launch {
            contactsViewModel.getAllContacts(
                pageNum = currentPage,
                pageSize = size,
                search = search
            )
        }
    }

    private fun setContactDialog(){
        dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_recyclerview, null)
        recyclerView = dialogView?.findViewById(R.id.recyclerView)
        cancelBtn = dialogView?.findViewById(R.id.cancel_icon)
        btnSubmit = dialogView?.findViewById(R.id.btnSubmit)
        btnSubmit?.visibility = View.GONE
        dialogProgressBar = dialogView?.findViewById(R.id.includedProgressLayout)
        allContactSearchView = dialogView?.findViewById(R.id.allContactSearchView)

        allContactSearchView?.visibility = View.VISIBLE

        recyclerView?.layoutManager = LinearLayoutManager(context)
        recyclerView?.adapter = contactAdapter

        dialog = MaterialAlertDialogBuilder(requireContext())
            .setView(dialogView)
            .show()

        cancelBtn?.setOnClickListener {
            currentPage = 0
            size = 25
            dialog?.dismiss()
        }

        onAllContactsSearch()

        recyclerView?.addOnScrollListener(object :
            PaginationScrollListener(recyclerView?.layoutManager as LinearLayoutManager) {
            override fun loadMoreItems() {
                isLoading = true
                currentPage += 1
                getAllContacts(currentPage, size)
            }

            override fun getTotalPageCount(): Long {
                return totalPages
            }

            override fun isLastPage(): Boolean {
                return isLastPage
            }

            override fun isLoading(): Boolean {
                return isLoading
            }

        })
    }

    private fun onAllContactsSearch() {
        val handler = Handler(Looper.getMainLooper())
        var runnable: Runnable? = null
        val debounceDelay: Long = 400

        allContactSearchView?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                runnable?.let { handler.removeCallbacks(it) }
                isSearching = true
                runnable = Runnable {
                    val inputLength = s?.length ?: 0
                    if (inputLength % 3 == 0 || inputLength > 3) {
                        getAllContacts(search = s.toString())
                        contactList.clear()
                    }
                }
                handler.postDelayed(runnable!!, debounceDelay)

                if (s.isNullOrEmpty()) {
                    allContactSearchView?.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_search_icon, 0, 0, 0
                    )
                } else {
                    allContactSearchView?.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_search_icon, 0, R.drawable.ic_cancel_icon, 0
                    )
                }

                clearSearch()
            }

            override fun afterTextChanged(s: Editable?) {
                if (s.toString().isEmpty()) {
                    isSearching = false
                    isLastPage = false
                    getAllContacts(currentPage = 0, size = 25)
                    contactList.clear()
                }
            }
        })
    }

    private fun clearSearch() {
        allContactSearchView?.setOnTouchListener { v, event ->
            if (event.action == android.view.MotionEvent.ACTION_UP) {
                val drawables = allContactSearchView?.compoundDrawables
                val rightDrawable = drawables?.get(2)
                if (rightDrawable != null) {
                    val drawableWidth = rightDrawable.bounds.width()
                    if (event.rawX >= (allContactSearchView?.right?.minus(drawableWidth)!!)) {
                        allContactSearchView?.setText("")
                        return@setOnTouchListener true
                    }
                }
            }
            false
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    interface OnButtonClickListener {
        fun onApplyFilter()
        fun onResetFilter()
    }

    override fun onItemClick(contact: Contact) {
        binding.contact.text = contact.firstName + " "+ contact.lastName
        invoiceHistoryMeta.contact = contact
        dialog?.dismiss()
    }
}