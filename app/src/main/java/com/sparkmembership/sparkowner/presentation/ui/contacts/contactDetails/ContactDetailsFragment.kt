package com.sparkmembership.sparkowner.presentation.ui.contacts.contactDetails

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.sparkmembership.sparkfitness.util.DateUtil
import com.sparkmembership.sparkfitness.util.bitmapToBase64
import com.sparkmembership.sparkfitness.util.imageConvertToBitmap
import com.sparkmembership.sparkfitness.util.toGone
import com.sparkmembership.sparkfitness.util.toVisible
import com.sparkmembership.sparkowner.R
import com.sparkmembership.sparkowner.constant.No_Internet_Connection
import com.sparkmembership.sparkowner.data.remote.Resource
import com.sparkmembership.sparkowner.data.request.ProfilePictureReqDTO
import com.sparkmembership.sparkowner.data.response.Appointment
import com.sparkmembership.sparkowner.data.response.ContactDetailsResDTO
import com.sparkmembership.sparkowner.data.response.Contacts
import com.sparkmembership.sparkowner.data.response.CustomField
import com.sparkmembership.sparkowner.databinding.FragmentContactDetailsBinding
import com.sparkmembership.sparkowner.presentation.components.TextView
import com.sparkmembership.sparkowner.presentation.listeners.OnContactDetailConnectedLocationListener
import com.sparkmembership.sparkowner.presentation.ui.contacts.contactDetails.adapter.AppointmentAdapter
import com.sparkmembership.sparkowner.presentation.ui.contacts.contactDetails.adapter.ConntectLocationContactDetailAdapter
import com.sparkmembership.sparkowner.presentation.ui.contacts.contactDetails.adapter.CustomAdapter
import com.sparkmembership.sparkowner.util.GlideUtil
import com.sparkmembership.sparkowner.util.navigationAnimation
import com.sparkmembership.sparkowner.util.noInternetDialogBox
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.File

@AndroidEntryPoint
class ContactDetailsFragment : Fragment(), OnContactDetailConnectedLocationListener {

    private lateinit var binding: FragmentContactDetailsBinding
    private val contactDetailsViewModel: ContactDetailsViewModel by viewModels()
    private var contactID: Long = 0
    private lateinit var contactDetailsResDTO: ContactDetailsResDTO
    private lateinit var customAdapter: CustomAdapter
    private lateinit var appointmentAdapter: AppointmentAdapter
    private lateinit var connectedLocationAdapter: ConntectLocationContactDetailAdapter

    private var vacationString = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentContactDetailsBinding.inflate(layoutInflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        getBundleData()
        initializeView()
        collapsingToolbarLayout()
        observeContactDetailApi()
        uploadPicture()
        observerUploadProfilePictureApi()
        setClickListener()

    }

    private fun setClickListener() {
        binding.btnNotes.setOnClickListener {
            if (contactDetailsResDTO.result.contactID != null && contactDetailsResDTO.result.contactID != 0) {
            val bundle = Bundle()
            val contactID = contactDetailsResDTO.result.contactID.toLong()
            bundle.putLong("contactID", contactID)
                findNavController().navigate(R.id.contactAllNotesFragment,bundle)
            }
        }
    }

    private fun initializeView() {
        binding.backButtonCollapsed.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.txtReferedBy.setOnClickListener {
            if (contactDetailsResDTO.result.refferedByContactID != null && contactDetailsResDTO.result.refferedByContactID != 0) {
                val referedByContactID = contactDetailsResDTO.result.refferedByContactID!!.toLong()
                val bundle = Bundle()
                bundle.putLong("contactID", referedByContactID)
                val navigation = navigationAnimation(R.anim.slide_in_right, R.anim.slide_out_left)
                findNavController().navigate(R.id.contactDetailsFragment, bundle, navigation)
            }

        }


    }

    private fun getBundleData() {

        with(arguments) {
            this?.getLong("contactID").let {
                contactID = it ?: 0
                getContactDetails(contactID)
            }
        }
    }

    private fun observerUploadProfilePictureApi() {

        contactDetailsViewModel.uploadProfilePictureData.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { result ->

                when (result) {
                    is Resource.Loading -> {
                        binding.includedProgressLayout.loaderView.toVisible()
                    }

                    is Resource.Success -> {
                        binding.includedProgressLayout.loaderView.toGone()
                        GlideUtil.loadImage(
                            requireContext(),
                            result.data!!.result.pictureURL,
                            binding.profileImage,
                            R.drawable.image_placeholder
                        )
                        GlideUtil.loadImage(
                            requireContext(),
                            result.data!!.result.pictureURL,
                            binding.profileImageSmall,
                            R.drawable.image_placeholder
                        )

                    }

                    is Resource.Error -> {
                        binding.includedProgressLayout.loaderView.toGone()
                        if (result.message.equals(No_Internet_Connection)) {
                            noInternetDialogBox(requireContext())
                        }
                    }

                }
            }


        }
    }

    private fun uploadPicture() {

        binding.btnChooseImage.setOnClickListener {

            openCameraGallery()
        }

    }

    private fun openCameraGallery() {
        ImagePicker.with(this)
            .crop()
            .compress(512)
            .maxResultSize(1080, 1080)
            .saveDir(
                File(
                    requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!,
                    getString(R.string.imagepicker)
                )
            )
            .createIntent { intent ->
                startActivityForResult.launch(intent)
            }

    }

    private val startActivityForResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            if (data != null) {
                val uri: Uri = data.data!!

                val bitmap = imageConvertToBitmap(requireContext(), uri)
                val base64String = bitmapToBase64(bitmap)

                if (base64String.isNotEmpty()) {
                    uploadProfileImage(base64String, contactID)
                }


            }
        } else if (result.resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(requireContext(), ImagePicker.getError(result.data), Toast.LENGTH_SHORT)
                .show()
        } else {
            Toast.makeText(requireContext(), getString(R.string.task_cancelled), Toast.LENGTH_SHORT)
                .show()

        }
    }

    private fun uploadProfileImage(base64String: String, contactID: Long) {

        val picture = ProfilePictureReqDTO(base64String)
        lifecycleScope.launch {
            contactDetailsViewModel.uploadPicture(picture, contactID)
        }
    }


    private fun getContactDetails(contactID: Long) {

        lifecycleScope.launch {
            if (contactID > 0) {
                contactDetailsViewModel.getContactDetails(contactID)
            }
        }
    }

    private fun observeContactDetailApi() {

        contactDetailsViewModel.contactDetails.observe(viewLifecycleOwner) { response ->
            val it = response.peekContent()
            when (it) {
                is Resource.Loading -> {
                    binding.includedProgressLayout.loaderView.toVisible()

                }

                is Resource.Success -> {
                    binding.includedProgressLayout.loaderView.toGone()

                    contactDetailsResDTO = it.data!!
                    setDataToFields(contactDetailsResDTO)

                }

                is Resource.Error -> {
                    binding.includedProgressLayout.loaderView.toGone()
                    if (it.message.equals(No_Internet_Connection)) {
                        noInternetDialogBox(requireContext())
                    }
                }
            }
        }
    }

    private fun setDataToFields(details: ContactDetailsResDTO) {

        details.result.let {

            binding.cardView1.toVisible()
            binding.atatMemeberHeading.toVisible()
            binding.cardView2.toVisible()

            GlideUtil.loadImage(
                requireContext(),
                it.profileImage,
                binding.profileImage,
                R.drawable.image_placeholder
            )
            binding.profileName.text = "${it.firstName} ${it.lastName}"
            binding.profileEmail.text = it.emailAddress

            GlideUtil.loadImage(
                requireContext(),
                it.profileImage,
                binding.profileImageSmall,
                R.drawable.image_placeholder
            )
            binding.profileNameSmall.text = "${it.firstName} ${it.lastName}"
            binding.profileEmailSmall.text = it.emailAddress

            setTextFieldValue(
                binding.txtEntered,
                DateUtil.formatUtcDateString(it.dateEntered, DateUtil.CHAT_DATE_FORMATE2)
            )
            setTextFieldValue(binding.txtContactId, it.contactID.toString())

            val startDate = DateUtil.formatUtcDateString(it.birthDate, DateUtil.CHAT_DATE_FORMATE2)
            val age = DateUtil.calculateYearDifference(startDate, DateUtil.getCurrentDate())

            setTextFieldValue(
                binding.txtBirthday,
                DateUtil.formatUtcDateString(
                    it.birthDate,
                    DateUtil.CHAT_DATE_FORMATE2
                ) + " ($age years old)"
            )
            setTextFieldValue(binding.txtGender, it.gender)

            if (it.vacationStart != null && it.vacationEnd != null) {
                val startVacationDate =
                    DateUtil.formatUtcDateString(it.vacationStart!!, DateUtil.VACATION_DATE_FORMAT)
                val endVacationDate =
                    DateUtil.formatUtcDateString(it.vacationEnd!!, DateUtil.VACATION_DATE_FORMAT)

                vacationString = getString(R.string.on_vacation, startVacationDate, endVacationDate)
            }
            setTextFieldValue(binding.txtVacations, vacationString)
            setTextFieldValue(binding.txtNeedToSee, it.needToSeeDetails ?: "")

            if (it.isCovidVaccinationDone) {
                binding.covidCheckBox.setBackgroundResource(R.drawable.icon_checked)
            } else {
                binding.covidCheckBox.setBackgroundResource(R.drawable.icon_unchecked)
            }

            setTextFieldValue(binding.txtStudentPhase, it.studentPhase ?: "")
            if (it.newMemberMaximizer) setTextFieldValue(
                binding.txtMemberMaximizer,
                getString(R.string.yes)
            ) else setTextFieldValue(binding.txtMemberMaximizer, getString(R.string.no))
            setTextFieldValue(binding.txtStudentPhase, it.studentPhase ?: "")
            if (it.usingStudentApp) setTextFieldValue(
                binding.txtUsingStudentApp,
                getString(R.string.yes)
            ) else setTextFieldValue(binding.txtUsingStudentApp, getString(R.string.no))

            if (it.mobilePhones.isNotEmpty()) {
                setTextFieldValue(
                    binding.txtMobile1Header,
                    it.mobilePhones.getOrNull(0)?.label ?: ""
                )
                setTextFieldValue(
                    binding.txtMobilePhone1,
                    it.mobilePhones.getOrNull(0)?.mobile ?: ""
                )

                setTextFieldValue(
                    binding.txtMobile2Header,
                    it.mobilePhones.getOrNull(1)?.label ?: ""
                )
                setTextFieldValue(
                    binding.txtMobilePhone2,
                    it.mobilePhones.getOrNull(1)?.mobile ?: ""
                )
            } else {
                setTextFieldValue(binding.txtMobilePhone1, "")
                setTextFieldValue(binding.txtMobilePhone2, "")

            }

            setTextFieldValue(binding.txtEmailAddress, it.emailAddress ?: "")
            setTextFieldValue(binding.txtLastSeen, it.lastSeen ?: "")
            val authorizedPersons = it.authorizedPersons.joinToString(separator = ",")
            setTextFieldValue(binding.txtAuthorizedToDropOffPickup, authorizedPersons)
            setTextFieldValue(binding.txtMedicalConcerns, it.medicalConcerns ?: "")
            setTextFieldValue(binding.txtAboutMe, it.about ?: "")

            it.guardians.let { guardians ->
                if (guardians.isNotEmpty()) {
                    // Set values for the first guardian
                    setTextFieldValue(
                        binding.guardian1Header,
                        "${guardians[0].labelName} (${guardians[0].label})"
                    )
                    setTextFieldValue(
                        binding.txtGuardian1Name,
                        "${guardians[0].firstName} (${guardians[0].lastName})"
                    )

                    // Set values for the second guardian
                    if (guardians.size > 1) {
                        setTextFieldValue(
                            binding.guardian2Header,
                            "${guardians[1].labelName} (${guardians[1].label})"
                        )
                        setTextFieldValue(
                            binding.txtGuardian2Name,
                            "${guardians[1].firstName} (${guardians[1].lastName})"
                        )
                    }

                    // Set values for the third guardian
                    if (guardians.size > 2) {
                        setTextFieldValue(
                            binding.guardian3Header,
                            "${guardians[2].labelName} (${guardians[2].label})"
                        )
                        setTextFieldValue(
                            binding.txtGuardian3Name,
                            "${guardians[2].firstName} (${guardians[2].lastName})"
                        )
                    }
                }
            }


            setTextFieldValue(binding.txtReferedBy, it.refferedBy ?: "")
            if (it.paymentMethodOnFile) setTextFieldValue(
                binding.txtPaymentMethodOnFile,
                "Yes"
            ) else setTextFieldValue(binding.txtPaymentMethodOnFile, "NO")

            if (it.sendReceiptForAutoPayment) {
                binding.checkboxAutoPayment.setBackgroundResource(R.drawable.icon_checked)
            } else {
                binding.checkboxAutoPayment.setBackgroundResource(R.drawable.icon_unchecked)
            }


            it.ataMembership?.let {
                setTextFieldValue(binding.txtNumber, it.number)
                val expirationDate =
                    DateUtil.formatUtcDateString(it.expiration, DateUtil.CHAT_DATE_FORMATE2)

                setTextFieldValue(binding.txtExpire, expirationDate)
            }

            it.customFields.let {
                binding.customeFieldsRecyclerView.layoutManager =
                    LinearLayoutManager(requireContext())
                customAdapter = CustomAdapter(it as ArrayList<CustomField>)
                binding.customeFieldsRecyclerView.adapter = customAdapter

            }



            it.appointments.let {
                binding.appointmentRecyclerView.layoutManager =
                    LinearLayoutManager(requireContext())
                appointmentAdapter = AppointmentAdapter(it as ArrayList<Appointment>)
                binding.appointmentRecyclerView.adapter = appointmentAdapter

            }

            it.connectedContacts.let {
                binding.connectedContactsRecyclerView.layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                connectedLocationAdapter =
                    ConntectLocationContactDetailAdapter(
                        requireContext(),
                        it as ArrayList<Contacts>,
                        this
                    )
                binding.connectedContactsRecyclerView.adapter = connectedLocationAdapter
            }


            if (it.assignedTags.isNotEmpty()) {
                binding.cardView3.toVisible()
            } else {
                binding.cardView3.toGone()
            }


            addChipsToGroup(
                chipGroup = binding.assignedTagsChipGrp,
                items = it.assignedTags.toMutableList(),
            )


        }


    }

    private fun setTextFieldValue(textView: TextView, data: String?) {
        if (data.isNullOrEmpty()) {
            textView.text = getString(R.string.na)
        } else {
            textView.text = data
        }
    }

    private fun collapsingToolbarLayout() {

        binding.appBar.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
            val totalScrollRange = appBarLayout.totalScrollRange
            val collapseRatio = Math.abs(verticalOffset).toFloat() / totalScrollRange

            binding.profileImage.scaleX = 1 - (0.5f * collapseRatio)
            binding.profileImage.scaleY = 1 - (0.5f * collapseRatio)

            binding.profileName.textSize = 24f - (8f * collapseRatio)
        }

        binding.appBar.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
            val totalScrollRange = appBarLayout.totalScrollRange

            if (Math.abs(verticalOffset) == totalScrollRange) {
                binding.profileContainerCollapsed.toVisible()
                binding.profileContainerExpanded.toGone()
            } else {
                binding.profileContainerCollapsed.toGone()
                binding.profileContainerExpanded.toVisible()
            }
        }
    }

    fun addChipsToGroup(
        chipGroup: ChipGroup,
        items: MutableList<String>,
    ) {
        chipGroup.removeAllViews()
        for (item in items) {
            val chip = layoutInflater.inflate(R.layout.item_simple_chip, chipGroup, false) as Chip
            chip.text = item

            chipGroup.addView(chip)
        }
    }

    override fun onItemClick(contact: Contacts) {

        contactID = contact.contactID.toLong()
        val bundle = Bundle()
        bundle.putLong("contactID", contactID)
        val navigation = navigationAnimation(R.anim.slide_in_right, R.anim.slide_out_left)
        findNavController().navigate(R.id.contactDetailsFragment, bundle, navigation)

    }


}