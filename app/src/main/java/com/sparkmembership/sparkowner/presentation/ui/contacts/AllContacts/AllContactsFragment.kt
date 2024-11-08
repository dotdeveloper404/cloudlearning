package com.sparkmembership.sparkowner.presentation.ui.contacts.AllContacts

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.sparkmembership.sparkfitness.util.toGone
import com.sparkmembership.sparkfitness.util.toVisible
import com.sparkmembership.sparkowner.R
import com.sparkmembership.sparkowner.adapter.ContactAdapter
import com.sparkmembership.sparkowner.constant.No_Internet_Connection
import com.sparkmembership.sparkowner.data.remote.Resource
import com.sparkmembership.sparkowner.data.request.ContactFilterResult
import com.sparkmembership.sparkowner.data.request.FilterStringObject
import com.sparkmembership.sparkowner.data.response.Contact
import com.sparkmembership.sparkowner.data.response.filter.ContactFilterResDTO
import com.sparkmembership.sparkowner.databinding.FragmentAllContactsBinding
import com.sparkmembership.sparkowner.presentation.SharedViewModel
import com.sparkmembership.sparkowner.presentation.listeners.OnApplyFilterListener
import com.sparkmembership.sparkowner.presentation.ui.MainActivity
import com.sparkmembership.sparkowner.presentation.ui.contacts.filter.ContactFilterBottomSheetFragment
import com.sparkmembership.sparkowner.util.PaginationScrollListener
import com.sparkmembership.sparkowner.util.hideToolBar
import com.sparkmembership.sparkowner.util.noInternetDialogBox
import com.sparkmembership.sparkowner.util.showToolBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class AllContactsFragment : Fragment(), OnApplyFilterListener, ContactAdapter.OnContactListener {

    lateinit var binding: FragmentAllContactsBinding
    private lateinit var contactAdapter: ContactAdapter
    var contactList = ArrayList<Contact>()
    private val contactsViewModel by viewModels<ContactsViewModel>()
    private val sharedViewModel by activityViewModels<SharedViewModel>()

    private var isSearchFocused  = false


    private var currentPage: Int = 0
    private var isLastPage = false
    private var totalPages: Long = 0
    private var size: Int = 25
    private var isLoading = false
    private var isSearching = false

    private var contactType: String? = null
    private var taggedContact: String ? = null
    private var notTaggedContact: String ? = null
    private var groupAge: String ? = null
    private var hasBirthday: Long? = null
    private var contactEnteredStart: String ? = null
    private var contactEnteredEnd: String ? = null
    private var ageMin: String ? = null
    private var ageMax: String ? = null
    private var membership: String ? = null
    private var classRoster: String ? = null
    private var contactFilterResDTO: ContactFilterResDTO? = null
    private var filterResult : ContactFilterResult? = null
    private var defaultContactType: String? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAllContactsBinding.inflate(inflater, container, false)
        handleArguments()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        observerViewModel()
        onScrollGetMoreData()
        onAllContactsSearch()
        getAllFilterContact()

        observeFilterContactApi(defaultContactType)


    }



    private fun init() {

        binding.recyclerViewContacts.layoutManager = LinearLayoutManager(context)
        contactAdapter = ContactAdapter(this)
        binding.recyclerViewContacts.adapter = contactAdapter

        binding.fabAddContact.setOnClickListener {
            findNavController().navigate(R.id.addContactsFragment)
        }

        (activity as MainActivity).showToolbar()

        (activity as? MainActivity)?.initializeCustomToolbar(
            title = getString(R.string.all_contacts),
            toolbarColor = R.color.colorPrimary,
            showBackButton = false,
            icons = listOf(
                R.drawable.icon_filter to {
                    val bottomSheetFragment = ContactFilterBottomSheetFragment(contactFilterResDTO!!, filterResult)
                    bottomSheetFragment.setListener(this)
                   bottomSheetFragment.show(parentFragmentManager, "ContactFilterBottomSheetFragment")
                }
            ), onBackPress = {
                findNavController().popBackStack()
            }
        )

    }

    private fun handleArguments(){
        with(arguments){
            this?.getString("viewContacts")?.let {
                defaultContactType = it
            }
            this?.getBoolean("isComingFromMore").let {

            }
        }
    }


    private fun getAllContacts(
        currentPage: Int? = null,
        size: Int? = null,
        search: String? = null,
        contactType: String? = null,
        taggedContact: String? = null,
        notTaggedContact: String? = null,
        groupAge: String? = null,
        hasBirthday: Long? = null,
        contactEnteredStart: String? = null,
        contactEnteredEnd: String? = null,
        ageMin: String? = null,
        ageMax: String? = null,
        membership: String? = null,
        classRoster: String? = null
    ) {
        lifecycleScope.launch {
            contactsViewModel.getAllContacts(
                pageNum = currentPage,
                pageSize = size,
                search = search,
                contactType = contactType,
                fTag = taggedContact,
                fEliminateTags = notTaggedContact,
                contactGroups = groupAge,
                fDOB = hasBirthday,
                contactsEnteredStart = contactEnteredStart,
                contactsEnteredEnd = contactEnteredEnd,
                StartAge = ageMin?.toLong(),
                EndAge = ageMax?.toLong(),
                membership = membership,
                classRoster = classRoster
            )
        }
    }

    private fun observerViewModel() {
        contactsViewModel.allContacts.observe(viewLifecycleOwner) { event ->
            val it = event.peekContent()
            when (it) {
                is Resource.Loading -> {
                    binding.includedProgressLayout.loaderView.visibility = View.VISIBLE
                    binding.notFoundDataLayout.toGone()
                }

                is Resource.Success -> {
                    contactList.addAll(it.data!!.result)
                    contactAdapter.setContacts(contactList)
                    if (it.data.result.isEmpty()) binding.notFoundDataLayout.toVisible()
                    if (it.data.result.size < 25) isLastPage = true
                    isLoading = false
                    isSearching = false
                    binding.includedProgressLayout.loaderView.toGone()
                }

                is Resource.Error -> {

                    binding.includedProgressLayout.loaderView.visibility = View.GONE
                    if (it.message.equals(No_Internet_Connection)){
                        noInternetDialogBox(requireContext())
                        if (contactList.isEmpty()){
                            binding.notFoundDataLayout.toVisible()
                        }
                    }else{
                        if (it.data?.result?.isEmpty() == true) binding.notFoundDataLayout.toVisible()
                    }

                }

                else -> {
                    binding.includedProgressLayout.loaderView.toGone()
                }
            }
        }
    }

    private fun onScrollGetMoreData() {
        binding.recyclerViewContacts.addOnScrollListener(object :
            PaginationScrollListener(binding.recyclerViewContacts.layoutManager as LinearLayoutManager) {
            override fun loadMoreItems() {
                if (!isSearching) {
                    isLoading = true
                    currentPage += 1
                    getAllContacts(
                        currentPage = currentPage, size = size, contactType = contactType,
                        taggedContact = taggedContact, notTaggedContact = notTaggedContact, groupAge = groupAge,
                        hasBirthday = hasBirthday, contactEnteredStart = contactEnteredStart,
                        contactEnteredEnd = contactEnteredEnd, ageMin = ageMin, ageMax = ageMax,
                        membership = membership, classRoster = classRoster
                    )                }
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

        binding.allContactSearchView.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                runnable?.let { handler.removeCallbacks(it) }
                isSearching = true
                runnable = Runnable {
                    val inputLength = s?.length ?: 0
                    if ((inputLength % 3 == 0 || inputLength > 3) && s.toString().isNotEmpty()) {
                        getAllContacts(search = s.toString())
                        contactList.clear()
                    }
                }
                handler.postDelayed(runnable!!, debounceDelay)

                if (s.isNullOrEmpty()) {
                    binding.allContactSearchView.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_search_icon, 0, 0, 0
                    )

                } else {
                    binding.allContactSearchView.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_search_icon, 0, R.drawable.ic_cancel_icon, 0
                    )
                }

                clearSearch()
            }

            override fun afterTextChanged(s: Editable?) {

                if (s.toString().isEmpty() && isSearchFocused ) {
                    isSearching = false
                    isLastPage = false
                    getAllContacts(currentPage = 0, size = 25)
                    contactList.clear()
                }
            }
        })

        binding.allContactSearchView.setOnFocusChangeListener { _, hasFocus ->
           isSearchFocused = hasFocus
        }
    }

    private fun clearSearch() {
        binding.allContactSearchView.setOnTouchListener { v, event ->
            if (event.action == android.view.MotionEvent.ACTION_UP) {
                val drawables = binding.allContactSearchView.compoundDrawables
                val rightDrawable = drawables[2]
                if (rightDrawable != null) {
                    val drawableWidth = rightDrawable.bounds.width()
                    if (event.rawX >= (binding.allContactSearchView.right - drawableWidth)) {
                        binding.allContactSearchView.setText("")
                        return@setOnTouchListener true
                    }
                }
            }
            false
        }

    }


    private fun getAllFilterContact() {

        lifecycleScope.launch {

            contactsViewModel.getAllContactFilters()
        }
    }

    private fun observeFilterContactApi(contactTypeDefaults: String?) {
        contactsViewModel.allContactFilters.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Loading -> {
                    binding.includedProgressLayout.loaderView.toVisible()
                }

                is Resource.Success -> {
                    contactFilterResDTO = response.data

                    if (contactTypeDefaults != null){
                        getAllContacts(currentPage = currentPage, size = size )
                    }else{
                        setContactTypeData(contactFilterResDTO!!)
                    }

                }

                is Resource.Error -> {
                    binding.includedProgressLayout.loaderView.toGone()

                }
            }
        }
    }

    private fun setContactTypeData(contactFilterResDTO: ContactFilterResDTO) {

        val filterContactType = contactFilterResDTO?.result!!.contactTypes.filter { contactType ->
            contactType.contactTypeName.equals("Prospect / Lead") || contactType.contactTypeName.equals("On A Trial")
        }

        val contactTypeString = filterContactType
            .map { it.contactTypeID }
            .joinToString(separator = ",")

        filterResult = filterResult?.copy(contactTypes = filterContactType) ?: ContactFilterResult(
            contactTypes = filterContactType,
            tagged = emptyList(),
            notTagged = emptyList(),
            groups = emptyList(),
            memberships = emptyList(),
            classRosters = emptyList(),
            birthdayIn = emptyList(),
            miniAge = null,
            maxAge = null,
            startDate = null,
            endDate = null,
            contactFilter = FilterStringObject()
        )

        getAllContacts(currentPage = currentPage, size = size, contactType =contactTypeString )
    }

    override fun onApplyFilter(contactFilterResult: ContactFilterResult) {

        filterResult = contactFilterResult

        if (contactFilterResult != null){
            contactType = contactFilterResult.contactFilter .contactTyperString
            taggedContact = contactFilterResult.contactFilter.taggedContactString
            notTaggedContact = contactFilterResult.contactFilter.notTaggedContactString
            groupAge = contactFilterResult.contactFilter.groupAgeString

            if (!contactFilterResult.contactFilter.hasBirthdayString.isNullOrEmpty() ){
                hasBirthday = contactFilterResult.contactFilter.hasBirthdayString?.toLong()
            }

            contactEnteredStart = contactFilterResult.contactFilter.contactEnteredStartString
            contactEnteredEnd = contactFilterResult.contactFilter.contactEnteredEndString

            if (ageMin != null){

                ageMin = contactFilterResult.contactFilter.ageMinString
            }
            if (ageMax != null){

                ageMax = contactFilterResult.contactFilter.ageMaxString
            }

            membership = contactFilterResult.contactFilter.membershipString
            classRoster = contactFilterResult.contactFilter.classRosterString

            binding.allContactSearchView.setText("")
            contactList.clear()
            contactAdapter.notifyDataSetChanged()

            getAllContacts(
                currentPage = currentPage, size = size, contactType = contactType,
                taggedContact = taggedContact, notTaggedContact = notTaggedContact, groupAge = groupAge,
                hasBirthday = hasBirthday, contactEnteredStart = contactEnteredStart,
                contactEnteredEnd = contactEnteredEnd, ageMin = ageMin, ageMax = ageMax,
                membership = membership, classRoster = classRoster
            )
        }

    }

    override fun onResetFilter(contactFilterResult: ContactFilterResult) {
        filterResult = contactFilterResult

    }

    override fun onItemClick(contact: Contact) {
       val contactID = contact.contactID.toLong()
        val bundle = Bundle()
        bundle.putLong("contactID", contactID)
        findNavController().navigate(R.id.contactDetailsFragment,bundle )
    }

    override fun onPause() {
        super.onPause()
        hideToolBar(activity as MainActivity)
    }

    override fun onResume() {
        super.onResume()
        showToolBar(activity as MainActivity)
    }


}