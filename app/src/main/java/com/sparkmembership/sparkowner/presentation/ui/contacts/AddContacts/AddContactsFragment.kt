package com.sparkmembership.sparkowner.presentation.ui.contacts.AddContacts

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.sparkmembership.sparkowner.R
import com.sparkmembership.sparkowner.adapter.ContactAdapter
import com.sparkmembership.sparkowner.data.enums.ContactTypeEnum
import com.sparkmembership.sparkowner.data.remote.Resource
import com.sparkmembership.sparkowner.data.response.Contact
import com.sparkmembership.sparkowner.data.response.ContactsMetaType
import com.sparkmembership.sparkowner.databinding.FragmentAddContactsBinding
import com.sparkmembership.sparkowner.presentation.components.CustomButton
import com.sparkmembership.sparkowner.presentation.components.TextView
import com.sparkmembership.sparkowner.presentation.components.showDatePickerDialog
import com.sparkmembership.sparkowner.presentation.ui.MainActivity
import com.sparkmembership.sparkowner.presentation.ui.contacts.AllContacts.ContactsViewModel
import com.sparkmembership.sparkowner.util.PaginationScrollListener
import com.sparkmembership.sparkowner.util.isValidEmail
import com.sparkmembership.sparkowner.util.showDialogWithRecyclerView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import androidx.appcompat.app.AlertDialog
import androidx.navigation.fragment.findNavController
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.sparkmembership.sparkfitness.util.DateUtil
import com.sparkmembership.sparkfitness.util.DateUtil.UTC_DATE_FORMAT
import com.sparkmembership.sparkfitness.util.showToast
import com.sparkmembership.sparkfitness.util.toGone
import com.sparkmembership.sparkfitness.util.toVisible
import com.sparkmembership.sparkowner.data.request.ContactRequest
import com.sparkmembership.sparkowner.data.response.Gender
import com.sparkmembership.sparkowner.data.response.MarketingSource
import com.sparkmembership.sparkowner.data.response.Phase
import com.sparkmembership.sparkowner.data.response.filter.ContactType
import com.sparkmembership.sparkowner.data.response.filter.Tag
import com.sparkmembership.sparkowner.presentation.ui.profile.ProfileViewModel
import com.sparkmembership.sparkowner.util.showDialogWithMultiSelectionRecyclerView
import kotlinx.coroutines.async

@AndroidEntryPoint
class AddContactsFragment : Fragment(), ContactAdapter.OnContactListener {

    lateinit var binding : FragmentAddContactsBinding
    private val ContactsViewModel by viewModels<ContactsViewModel>()
    private val AddContactsViewModel by viewModels<AddContactsViewModel>()
    var contactTypeList = ArrayList<ContactType>()
    private var taggedContactsArrayList = ArrayList<Tag>()
    private lateinit var contactsMetaTypeList : ContactsMetaType
    var contactList = ArrayList<Contact>()
    private var currentPage: Int = 0
    private var isLastPage = false
    private var totalPages: Long = 0
    private var size: Int = 25
    private var isLoading = false
    private var isSearching = false
    private lateinit var contactAdapter: ContactAdapter
    private var dialogView : View? = null
    private var recyclerView : RecyclerView? = null
    private var cancelBtn : ImageView? = null
    private var btnSubmit : CustomButton? = null
    private var dialogProgressBar : ProgressBar? = null
    private var allContactSearchView : TextView? = null
    private var selectedContactTypeItem: ContactType? = null
    private var dialog : AlertDialog? = null
    private var addContactData = ContactRequest()

    private var taggedContactString: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddContactsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as MainActivity).setBottomBarNavigationVisibility(false)
        getContactsType()
        getAllContacts(currentPage = currentPage, size = size)
        observerViewModel()
        init()
        setSubmitContactClickListener()
    }
    private fun init(){
        contactAdapter = ContactAdapter(this)
        setContactType()
        setGenderSpinner()
        setTagSpinner()
        setMarketingSourceDetailsSpinner()
        setReferredBySpinner()
        setProspectPhaseSpinner()
        setBirthdayListener()
    }
    private fun setDefaultFields() {
        val contactTypeDefaultValue = contactsMetaTypeList.contactTypes.filter { it.contactTypeID == "P" }.firstOrNull()
        val ProspectTypeDefaultValue = contactsMetaTypeList.phases.firstOrNull()
        binding.spinnerContactType.text = contactTypeDefaultValue?.contactTypeName
        addContactData.contactType = contactTypeDefaultValue?.contactTypeID

        if (contactsMetaTypeList.phases.filter { it.contactType == contactTypeDefaultValue?.contactTypeID }.isNotEmpty()){
            binding.PhaseTitle.text = contactTypeDefaultValue?.contactTypeName  + " Phase"
            binding.spinnerPhase.hint = "Enter " + contactTypeDefaultValue?.contactTypeName + " Phase"
            binding.spinnerPhase.visibility = View.VISIBLE
            binding.PhaseTitle.visibility = View.VISIBLE
            selectedContactTypeItem = contactTypeDefaultValue
            binding.spinnerPhase.text = ProspectTypeDefaultValue?.phaseName
            addContactData.leadPhaseID = ProspectTypeDefaultValue?.phaseID
        }
    }

    private fun setSubmitContactClickListener() {
        binding.buttonAddContact.setOnClickListener {
            val firstName = binding.firstNameEdittext.text.toString().trim()
            val email = binding.email.text.toString().trim()
            val middleName = binding.middleName.text.toString().trim()
            val lastname = binding.lastName.text.toString().trim()
            val dob = binding.birthday.text.toString().trim()
            val mobilePhone = binding.mobileNumber.text.toString().trim()

            if (middleName.isNotEmpty()) addContactData.middleName = middleName
            if (lastname.isNotEmpty()) addContactData.lastName = lastname
            if (dob.isNotEmpty()) addContactData.dob = DateUtil.ConvertCalenderDatetoUtcFormat(dob)
            if (mobilePhone.isNotEmpty()) addContactData.mobilePhone = mobilePhone
            if (firstName.isNotEmpty()) addContactData.firstName = firstName
            if (email.isNotEmpty()) addContactData.emailAddress = email

            lifecycleScope.launch {
                val firstNameValid = async { validateFirstName(firstName) }
                val emailValid = async { validateEmail(email) }

                if (firstNameValid.await() && emailValid.await()) {
                    AddContactsViewModel.addContacts(addContactData)
                }
            }
        }

        binding.firstNameEdittext.addTextChangedListener(createTextWatcher(binding.firstNameEdittext, binding.errorMessageFirstName))
        binding.email.addTextChangedListener(createTextWatcher(binding.email, binding.errorMessageEmail, ::isValidEmail))
    }

    private fun validateFirstName(firstName: String) : Boolean{
        if (firstName.isEmpty()) {
            showError(binding.firstNameEdittext, binding.errorMessageFirstName)
            binding.addContactsScrollView.post {
                binding.addContactsScrollView.smoothScrollTo(0, binding.firstNameEdittext.top)
            }
            return false
        } else {
            resetError(binding.firstNameEdittext, binding.errorMessageFirstName)
            return true
        }
    }

    private fun validateEmail(email: String) : Boolean {
        if (email.isNotEmpty() && !isValidEmail(email)) {
            showError(binding.email, binding.errorMessageEmail)
            binding.addContactsScrollView.post {
                binding.addContactsScrollView.smoothScrollTo(0, binding.email.top)
            }
            return false
        } else {
            resetError(binding.email, binding.errorMessageEmail)
            return true
        }
    }

    private fun showError(editText: EditText, errorMessage: TextView) {
        editText.background = ContextCompat.getDrawable(requireContext(), R.drawable.edittext_red_border)
        errorMessage.visibility = View.VISIBLE
    }

    private fun resetError(editText: EditText, errorMessage: TextView) {
        editText.background = ContextCompat.getDrawable(requireContext(), R.drawable.edittext_rounded_border)
        errorMessage.visibility = View.GONE
    }

    private fun createTextWatcher(editText: EditText, errorMessage: TextView, validationFunc: ((String) -> Boolean)? = null): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // No action needed here
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val isValid = validationFunc?.invoke(s.toString()) ?: true
                if (s.toString().isNotEmpty() && isValid) {
                    resetError(editText, errorMessage)
                }
            }

            override fun afterTextChanged(s: Editable?) {
                // No action needed here
            }
        }
    }

    private fun setBirthdayListener() {
        binding.birthday.setOnClickListener {
            showDatePickerDialog(requireContext(), binding.birthday)
        }
    }

    private fun setReferredBySpinner() {
        binding.spinnerReferredBy.setOnClickListener {
            setReferredByDialog()
        }
    }

    private fun setReferredByDialog(){
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

    private fun setContactType() {
        binding.spinnerContactType.setOnClickListener {
            showDialogWithRecyclerView(
                requireContext(),
                data = contactsMetaTypeList.contactTypes,
                displayText = { it.contactTypeName },
                onItemSelect = { selectedItem  ->
                },
                onSubmitClick = { selectedItem->
                    binding.spinnerContactType.text = selectedItem?.contactTypeName
                    addContactData.contactType = selectedItem?.contactTypeID
                    selectedContactTypeItem = selectedItem
                    if (contactsMetaTypeList.phases.filter { it.contactType == selectedContactTypeItem?.contactTypeID }.isNotEmpty()){
                        binding.PhaseTitle.text = selectedItem?.contactTypeName  + " Phase"
                        binding.spinnerPhase.hint = "Enter " + selectedItem?.contactTypeName + " Phase"
                        binding.spinnerPhase.text = ""
                        binding.spinnerPhase.visibility = View.VISIBLE
                        binding.PhaseTitle.visibility = View.VISIBLE
                    }else{
                        binding.spinnerPhase.visibility = View.GONE
                        binding.PhaseTitle.visibility = View.GONE
                    }
                },
                selectedItem = if (selectedContactTypeItem != null) selectedContactTypeItem else contactsMetaTypeList.contactTypes.filter { it.contactTypeID == "P" }.firstOrNull()
            )
        }
    }

    private fun setGenderSpinner() {
        var selectItemGender : Gender?= null
        binding.spinnerGender.setOnClickListener {
            showDialogWithRecyclerView(
                requireContext(),
                data = contactsMetaTypeList.genders,
                displayText = { it.genderName },
                onItemSelect = { selectedItem  ->

                },
                onSubmitClick = {selectedItem->
                    binding.spinnerGender.text = selectedItem?.genderName
                    addContactData.gender = selectedItem?.genderValue
                    selectItemGender = selectedItem
                    Toast.makeText(
                        requireContext(),
                        selectedItem?.genderName,
                        Toast.LENGTH_SHORT
                    ).show()
                },
                selectedItem = selectItemGender
            )
        }
    }

    private fun setTagSpinner() {
        binding.spinnerTags.setOnClickListener {
            showDialogWithMultiSelectionRecyclerView(
                requireContext(),
                data = contactsMetaTypeList.tags as ArrayList,
                displayText = { it.tagName },
                title = getString(R.string.select_tags),
                onItemSelect = { selectedItems ->
                    taggedContactsArrayList.clear()
                    taggedContactsArrayList.addAll(selectedItems)
                },
                selectedData = taggedContactsArrayList,
                onSubmitClick = { selectedItem  ->
                    addChipsToGroup(
                        chipGroup = binding.chipGroupTags,
                        items = taggedContactsArrayList.map { it.tagName }.toMutableList(),
                        onChipRemoved = { removedItem ->
                            taggedContactsArrayList.removeIf { it.tagName == removedItem }
                        }
                    )
                    taggedContactString = taggedContactsArrayList
                        .map { it.tagID }
                        .joinToString(separator = ",")

                    addContactData.tags = taggedContactString
                }
            )
        }
    }

    private fun setMarketingSourceDetailsSpinner() {
        var onSelectedMarketingSource : MarketingSource? = null
        binding.spinnerMarketingSource.setOnClickListener {
            showDialogWithRecyclerView(
                requireContext(),
                data = contactsMetaTypeList.marketingSources,
                displayText = { it.marketingSource },
                onItemSelect = {
                },
                onSubmitClick = { selectedItem  ->
                    binding.spinnerMarketingSource.text = selectedItem?.marketingSource
                    onSelectedMarketingSource = selectedItem
                    addContactData.marketingSource = selectedItem?.marketingSourceID
                    Toast.makeText(
                        requireContext(),
                        selectedItem?.marketingSource,
                        Toast.LENGTH_SHORT
                    ).show()
                },
                selectedItem = onSelectedMarketingSource
            )
        }
    }

    private fun setProspectPhaseSpinner(){
        var selectedProspectPhase : Phase? = null
        binding.spinnerPhase.setOnClickListener {
            showDialogWithRecyclerView(
                requireContext(),
                data = contactsMetaTypeList.phases.filter { it.contactType == selectedContactTypeItem?.contactTypeID },
                displayText = { it.phaseName },
                onItemSelect = {
                },
                onSubmitClick = { selectedItem  ->
                    binding.spinnerPhase.text = selectedItem?.phaseName
                    addContactData.leadPhaseID = selectedItem?.phaseID
                    selectedProspectPhase = selectedItem
                    Toast.makeText(
                        requireContext(),
                        selectedItem?.phaseName,
                        Toast.LENGTH_SHORT
                    ).show()
                },
                selectedItem = selectedProspectPhase
            )
        }
    }

    private fun getContactsType(){
        lifecycleScope.launch {
            ContactsViewModel.getContactTypes()
        }
    }

    private fun getAllContacts(
        currentPage: Int? = null,
        size: Int? = null,
        search: String? = null
    ) {
        lifecycleScope.launch {
            ContactsViewModel.getAllContacts(
                pageNum = currentPage,
                pageSize = size,
                search = search
            )
        }
    }

    private fun observerViewModel(){
        ContactsViewModel.contactTypesResDto.observe(viewLifecycleOwner){ response ->
            when(response){
                is Resource.Loading ->{
                    binding.includedProgressLayout.loaderView.visibility = View.VISIBLE
                }
                is Resource.Success ->{
                    contactTypeList.clear()
                    if (response.data != null){
                        contactsMetaTypeList = response.data.result
                    }
                    contactTypeList.addAll(response.data!!.result.contactTypes)
                    binding.includedProgressLayout.loaderView.visibility = View.GONE
                    setDefaultFields()
                }
                is Resource.Error -> {
                    binding.includedProgressLayout.loaderView.visibility = View.GONE
                }
            }
        }

        ContactsViewModel.allContacts.observe(viewLifecycleOwner) { event ->
            val it = event.getContentIfNotHandled()
            when (it) {
                is Resource.Loading -> {
                    dialogProgressBar?.toVisible()
                }

                is Resource.Success -> {
                    contactList.addAll(it.data!!.result)
                    contactAdapter.setContacts(contactList)
                    if (it.data.result.size < 25) isLastPage = true
                    isLoading = false
                    dialogProgressBar?.toGone()
                }

                is Resource.Error -> {

                    dialogProgressBar?.toGone()
                }

                else -> {
                    binding.includedProgressLayout.loaderView.toGone()
                }
            }
        }

        AddContactsViewModel.addContacts.observe(viewLifecycleOwner){ event ->
            event.getContentIfNotHandled()?.let { response ->
                when (response) {
                    is Resource.Loading -> {
                        binding.includedProgressLayout.loaderView.toVisible()
                    }

                    is Resource.Success -> {
                        binding.includedProgressLayout.loaderView.toGone()
                        AddContactsViewModel.addContacts.removeObservers(viewLifecycleOwner)
                        findNavController().popBackStack()
                    }

                    is Resource.Error -> {
                        AddContactsViewModel.addContacts.removeObservers(viewLifecycleOwner)
                        binding.includedProgressLayout.loaderView.toGone()
                        showToast(requireContext(), response.message.toString())
                    }
                }
            }
        }
    }

    fun addChipsToGroup(
        chipGroup: ChipGroup,
        items: MutableList<String>,
        onChipRemoved: (String) -> Unit
    ) {
        chipGroup.removeAllViews()
        for (item in items) {
            val chip = layoutInflater.inflate(R.layout.item_chip, chipGroup, false) as Chip
            chip.text = item

            chip.setOnCloseIconClickListener {
                onChipRemoved(item)
                chipGroup.removeView(chip)
            }

            chipGroup.addView(chip)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (activity as MainActivity).setBottomBarNavigationVisibility(true)
    }

    override fun onItemClick(contact: Contact) {
        binding.spinnerReferredBy.text = contact.firstName + " "+ contact.lastName
        addContactData.referredBy = contact.contactID
        dialog?.dismiss()
    }

}