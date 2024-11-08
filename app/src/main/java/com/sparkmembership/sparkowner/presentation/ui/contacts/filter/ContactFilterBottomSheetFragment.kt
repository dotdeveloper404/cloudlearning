package com.sparkmembership.sparkowner.presentation.ui.contacts.filter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.sparkmembership.sparkfitness.util.toVisible
import com.sparkmembership.sparkowner.R
import com.sparkmembership.sparkowner.data.request.ContactFilterResult
import com.sparkmembership.sparkowner.data.request.FilterStringObject
import com.sparkmembership.sparkowner.data.response.filter.ClassRoster
import com.sparkmembership.sparkowner.data.response.filter.ContactFilterResDTO
import com.sparkmembership.sparkowner.data.response.filter.ContactType
import com.sparkmembership.sparkowner.data.response.filter.Group
import com.sparkmembership.sparkowner.data.response.filter.HasBirthdayIn
import com.sparkmembership.sparkowner.data.response.filter.Membership
import com.sparkmembership.sparkowner.data.response.filter.Tag
import com.sparkmembership.sparkowner.databinding.FragmentContactFilterBottomsheetBinding
import com.sparkmembership.sparkowner.presentation.components.showDatePickerDialog
import com.sparkmembership.sparkowner.presentation.listeners.OnApplyFilterListener
import com.sparkmembership.sparkowner.util.showDialogWithMultiSelectionRecyclerView
import com.sparkmembership.sparkowner.util.showDialogWithRecyclerView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ContactFilterBottomSheetFragment(val result: ContactFilterResDTO, var contactFilterResult: ContactFilterResult? ) :
    BottomSheetDialogFragment() {


    private lateinit var binding: FragmentContactFilterBottomsheetBinding


    private lateinit var contactFilterResDTO: ContactFilterResDTO
    private var contactTypeArrayList = ArrayList<ContactType>()
    private var taggedContactsArrayList = ArrayList<Tag>()
    private var notTaggedContactsArrayList = ArrayList<Tag>()
    private var ageGroupArrayList = ArrayList<Group>()
    private var hasBirthdayArrayList = ArrayList<HasBirthdayIn>()
    private var membershipArrayList = ArrayList<Membership>()
    private var classRosterArrayList = ArrayList<ClassRoster>()

    private var contactTypeString: String? = null
    private var taggedContactString: String? = null
    private var notTaggedContactString: String? = null
    private var groupAgeString: String? = null
    private var hasBirthdayString: String? = null
    private var contactEnteredStartString: String? = null
    private var contactEnteredEndString: String? = null
    private var membershipString: String? = null
    private var classRosterString: String? = null
    private lateinit var onApplyFilterListener: OnApplyFilterListener

    fun setListener(listener: OnApplyFilterListener) {
        onApplyFilterListener = listener
    }



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentContactFilterBottomsheetBinding.inflate(layoutInflater, container, false)
        bottomsheetSetting()

        contactFilterResDTO = result



        return binding.root
    }





    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
        contactType()
        taggedContact()
        notTaggedContact()
        ageGroup()
        hasBirthday()
        startDate()
        endDate()
        membership()
        classRoster()
        applyFilter()
        resetFilter()


        if (contactFilterResult != null){
            setDataToFields(contactFilterResult!!)
        }
    }

    private fun setDataToFields(it: ContactFilterResult) {


        // Use let to safely access properties and avoid null checks for each assignment
        it.contactTypes?.let { contactTypes ->
            contactTypeArrayList = ArrayList(contactTypes)
        }

        it.tagged?.let { tagged ->
            taggedContactsArrayList = ArrayList(tagged)
        }

        it.notTagged?.let { notTagged ->
            notTaggedContactsArrayList = ArrayList(notTagged)
        }

        it.groups?.let { groups ->
            ageGroupArrayList = ArrayList(groups)
        }

        it.memberships?.let { memberships ->
            membershipArrayList = ArrayList(memberships)
        }

        it.classRosters?.let { classRosters ->
            classRosterArrayList = ArrayList(classRosters)
        }

        it.birthdayIn?.let { hasBirthday ->
            hasBirthdayArrayList = ArrayList(hasBirthday)
        }


        binding.spinneStartDate.text = it.contactFilter.contactEnteredStartString
        binding.spinnerEndDate.text = it.contactFilter.contactEnteredEndString

        binding.editTextMiniAge.setText(it.contactFilter.ageMinString)
        binding.editTextMaxAge.setText(it.contactFilter.ageMaxString)

        addChipsToGroup(
            chipGroup = binding.chipGroupContactType,
            items = contactTypeArrayList.map { it.contactTypeName }.toMutableList(),
            onChipRemoved = { removedItem ->
                contactTypeArrayList.removeIf { it.contactTypeName == removedItem }
            }
        )

        addChipsToGroup(
            chipGroup = binding.chipGroupContactTagged,
            items = taggedContactsArrayList.map { it.tagName }.toMutableList(),
            onChipRemoved = { removedItem ->
                taggedContactsArrayList.removeIf { it.tagName == removedItem }
            }
        )

        addChipsToGroup(
            chipGroup = binding.chipGroupContactNotTagged,
            items = notTaggedContactsArrayList.map { it.tagName }.toMutableList(),
            onChipRemoved = { removedItem ->
                notTaggedContactsArrayList.removeIf { it.tagName == removedItem }
            }
        )

        addChipsToGroup(
            chipGroup = binding.chipGroupAgeGroup,
            items = ageGroupArrayList.map { it.groupName }.toMutableList(),
            onChipRemoved = { removedItem ->
                ageGroupArrayList.removeIf { it.groupName == removedItem }
            }
        )


        addChipsToGroup(
            chipGroup = binding.chipGroupHasBirthday,
            items = hasBirthdayArrayList.map { it.monthName }.toMutableList(),
            onChipRemoved = { removedItem ->
                hasBirthdayArrayList.removeIf { it.monthName == removedItem }
            }
        )


        addChipsToGroup(
            chipGroup = binding.chipGroupMembership,
            items = membershipArrayList.map { it.membershipName }.toMutableList(),
            onChipRemoved = { removedItem ->
                membershipArrayList.removeIf { it.membershipName == removedItem }
            }
        )


        addChipsToGroup(
            chipGroup = binding.chipGroupClassRoaster,
            items = classRosterArrayList.map { it.classRosterName }.toMutableList(),
            onChipRemoved = { removedItem ->
                classRosterArrayList.removeIf { it.classRosterName == removedItem }
            }
        )



    }

    private fun bottomsheetSetting() {

        dialog?.setOnShowListener { dialogInterface ->
            val bottomSheet = (dialogInterface as? BottomSheetDialog)
                ?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)

            if (bottomSheet != null) {
                bottomSheet.layoutParams.height = LayoutParams.MATCH_PARENT

                val behavior = BottomSheetBehavior.from(bottomSheet)
                behavior.isDraggable = false
                behavior.state = BottomSheetBehavior.STATE_EXPANDED

                behavior.skipCollapsed = true
                behavior.isHideable = false
            }
        }

    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            LayoutParams.MATCH_PARENT,
            LayoutParams.MATCH_PARENT
        )

    }

    private fun resetFilter() {

        binding.btnResetFilter.setOnClickListener {


            contactTypeArrayList.clear()
            taggedContactsArrayList.clear()
            notTaggedContactsArrayList.clear()
            ageGroupArrayList.clear()
            hasBirthdayArrayList.clear()
            membershipArrayList.clear()
            classRosterArrayList.clear()


            binding.spinneStartDate.text = ""
            binding.spinnerEndDate.text = ""

            binding.editTextMiniAge.setText("")
            binding.editTextMaxAge.setText("")

            val contactFilterResult = ContactFilterResult(
                contactTypes = emptyList(),
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

            onApplyFilterListener.onResetFilter(contactFilterResult)

            dismiss()
        }
    }


    private fun applyFilter() {

        binding.btnApplyFilter.setOnClickListener {

            if (binding.spinnerEndDate.text.toString().isNotEmpty()) {

                contactEnteredEndString = binding.spinnerEndDate.text.toString()
            }

            if (binding.spinneStartDate.text.toString().isNotEmpty()) {
                contactEnteredStartString = binding.spinneStartDate.text.toString()

            }

            contactTypeString = contactTypeArrayList
                .map { it.contactTypeID }
                .joinToString(separator = ",")

            taggedContactString = taggedContactsArrayList
                .map { it.tagID }
                .joinToString(separator = ",")

            notTaggedContactString = notTaggedContactsArrayList
                .map { it.tagID }
                .joinToString(separator = ",")

            groupAgeString = ageGroupArrayList
                .map { it.groupID }
                .joinToString(separator = ",")

            hasBirthdayString = hasBirthdayArrayList
                .map { it.monthID }
                .joinToString(separator = ",")

            membershipString =
                membershipArrayList.map { it.membershipTypeID }
                    .joinToString(separator = ",")

            classRosterString = classRosterArrayList
                .map { it.classRosterID }
                .joinToString(separator = ",")




            val filterStringObject = FilterStringObject(
                contactTyperString = contactTypeString,
                taggedContactString = taggedContactString,
                notTaggedContactString = notTaggedContactString,
                groupAgeString = groupAgeString,
                hasBirthdayString = hasBirthdayString,
                contactEnteredStartString = contactEnteredStartString,
                contactEnteredEndString = contactEnteredEndString,
                ageMinString = binding.editTextMiniAge.text.toString(),
                ageMaxString = binding.editTextMaxAge.text.toString(),
                membershipString = membershipString,
                classRosterString = classRosterString
            )


            val customObject = ContactFilterResult(
                contactTypes = contactTypeArrayList,
                tagged = taggedContactsArrayList,
                notTagged = notTaggedContactsArrayList,
                groups = ageGroupArrayList,
                memberships = membershipArrayList,
                classRosters = classRosterArrayList,
                startDate = contactEnteredStartString,
                endDate = contactEnteredEndString,
                birthdayIn = hasBirthdayArrayList,
                miniAge = binding.editTextMiniAge.text.toString(),
                maxAge = binding.editTextMaxAge.text.toString(),

                contactFilter = filterStringObject
            )

            onApplyFilterListener.onApplyFilter(customObject)
            binding.includedProgressLayout.loaderView.toVisible()
            dismiss()



        }
    }

    private fun classRoster() {

        binding.spinnerClassRoaster.setOnClickListener {

            showDialogWithMultiSelectionRecyclerView(
                requireContext(),
                title = getString(R.string.select_class_roster),
                data = contactFilterResDTO.result.classRosters as ArrayList<ClassRoster>,
                selectedData = classRosterArrayList,
                displayText = {
                    it.classRosterName
                },
                onItemSelect = { selectedItems ->
                    classRosterArrayList.clear()
                    classRosterArrayList.addAll(selectedItems)
                },
                onSubmitClick = {

                    addChipsToGroup(
                        chipGroup = binding.chipGroupClassRoaster,
                        items = classRosterArrayList.map { it.classRosterName }.toMutableList(),
                        onChipRemoved = { removedItem ->
                            classRosterArrayList.removeIf { it.classRosterName == removedItem }
                        }
                    )

                }

            )
        }

    }

    private fun membership() {

        binding.spinnerMembership.setOnClickListener {

            showDialogWithMultiSelectionRecyclerView(
                requireContext(),
                title = getString(R.string.select_membership),
                data = contactFilterResDTO.result.memberships as ArrayList<Membership>,
                selectedData = membershipArrayList,
                displayText = {
                    it.membershipName
                },
                onItemSelect = { selectedItems ->
                    membershipArrayList.clear()
                    membershipArrayList.addAll(selectedItems)
                },
                onSubmitClick = {

                    addChipsToGroup(
                        chipGroup = binding.chipGroupMembership,
                        items = membershipArrayList.map { it.membershipName }.toMutableList(),
                        onChipRemoved = { removedItem ->
                            membershipArrayList.removeIf { it.membershipName == removedItem }
                        }
                    )


                }


            )
        }

    }




    private fun endDate() {

        binding.spinnerEndDate.setOnClickListener {

            showDatePickerDialog(requireContext(), binding.spinnerEndDate)
        }

    }

    private fun startDate() {
        binding.spinneStartDate.setOnClickListener {

            showDatePickerDialog(requireContext(), binding.spinneStartDate )

        }

    }

    private fun hasBirthday() {

        binding.spinnerHasBirthday.setOnClickListener {
            showDialogWithRecyclerView(
                requireContext(),
                data = contactFilterResDTO.result.hasBirthdayInList as ArrayList<HasBirthdayIn>,
                displayText = {
                    it.monthName
                },
                selectedItem = hasBirthdayArrayList.firstOrNull(),  // Set the previously selected item
                onItemSelect = { selectedItem ->
                    hasBirthdayArrayList.clear()
                    hasBirthdayArrayList.add(selectedItem)
                },
                onSubmitClick = { selectedItem ->

                    addChipsToGroup(
                        chipGroup = binding.chipGroupHasBirthday,
                        items = hasBirthdayArrayList.map { it.monthName }.toMutableList(),
                        onChipRemoved = { removedItem ->
                            hasBirthdayArrayList.removeIf { it.monthName == removedItem }
                        }
                    )


                }
            )
        }

    }

    private fun ageGroup() {

        binding.spinnerAgeGroup.setOnClickListener {

            showDialogWithMultiSelectionRecyclerView(
                requireContext(),
                title = getString(R.string.select_age_group),
                data = contactFilterResDTO.result.groups as ArrayList<Group>,
                selectedData = ageGroupArrayList,
                displayText = {
                    it.groupName
                },
                onItemSelect = { selectedItems ->
                    ageGroupArrayList.clear()
                    ageGroupArrayList.addAll(selectedItems)
                },
                onSubmitClick = {

                    addChipsToGroup(
                        chipGroup = binding.chipGroupAgeGroup,
                        items = ageGroupArrayList.map { it.groupName }.toMutableList(),
                        onChipRemoved = { removedItem ->
                            ageGroupArrayList.removeIf { it.groupName == removedItem }
                        }
                    )



                }
            )
        }


    }

    private fun notTaggedContact() {

        binding.spinnerContactNotTagged.setOnClickListener {

            showDialogWithMultiSelectionRecyclerView(
                requireContext(),
                title = getString(R.string.select_contact_not_tagged),
                data = contactFilterResDTO.result.tags as ArrayList<Tag>,
                selectedData = notTaggedContactsArrayList,
                displayText = {
                    it.tagName
                },
                onItemSelect = { selectedItems ->
                    notTaggedContactsArrayList.clear()
                    notTaggedContactsArrayList.addAll(selectedItems)
                },
                onSubmitClick = {

                    addChipsToGroup(
                        chipGroup = binding.chipGroupContactNotTagged,
                        items = notTaggedContactsArrayList.map { it.tagName }.toMutableList(),
                        onChipRemoved = { removedItem ->
                            notTaggedContactsArrayList.removeIf { it.tagName == removedItem }
                        }
                    )



                }
            )
        }
    }

    private fun taggedContact() {

        binding.spinnerContactTagged.setOnClickListener {

            showDialogWithMultiSelectionRecyclerView(
                requireContext(),
                title = getString(R.string.select_tagged_contact),
                data = contactFilterResDTO.result.tags as ArrayList<Tag>,
                selectedData = taggedContactsArrayList,
                displayText = {
                    it.tagName
                },
                onItemSelect = { selectedItems ->
                    taggedContactsArrayList.clear()
                    taggedContactsArrayList.addAll(selectedItems)
                },
                onSubmitClick = {

                    addChipsToGroup(
                        chipGroup = binding.chipGroupContactTagged,
                        items = taggedContactsArrayList.map { it.tagName }.toMutableList(),
                        onChipRemoved = { removedItem ->
                            taggedContactsArrayList.removeIf { it.tagName == removedItem }
                        }
                    )


                }
            )
        }

    }

    private fun contactType() {
        binding.spinnerContactType.setOnClickListener {

            showDialogWithMultiSelectionRecyclerView(
                requireContext(),
                data = contactFilterResDTO.result.contactTypes as ArrayList<ContactType>,
                title = getString(R.string.select_contact_type),
                selectedData = contactTypeArrayList,
                displayText = {
                    it.contactTypeName
                },
                onItemSelect = { selectedItems ->
                    contactTypeArrayList.clear()
                    contactTypeArrayList.addAll(selectedItems)
                },
                onSubmitClick = {

                    addChipsToGroup(
                        chipGroup = binding.chipGroupContactType,
                        items = contactTypeArrayList.map { it.contactTypeName }.toMutableList(),
                        onChipRemoved = { removedItem ->
                            contactTypeArrayList.removeIf { it.contactTypeName == removedItem }
                        }
                    )

                }
            )
        }
    }

    private fun init() {
        binding.btnCancel.setOnClickListener {
            dismiss()
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



}

