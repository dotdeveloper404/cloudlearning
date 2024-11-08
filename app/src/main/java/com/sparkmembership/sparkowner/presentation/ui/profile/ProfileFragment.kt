package com.sparkmembership.sparkowner.presentation.ui.profile

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.github.dhaval2404.imagepicker.ImagePicker
import com.sparkmembership.sparkfitness.util.bitmapToBase64
import com.sparkmembership.sparkfitness.util.imageConvertToBitmap
import com.sparkmembership.sparkfitness.util.showToast
import com.sparkmembership.sparkfitness.util.toGone
import com.sparkmembership.sparkfitness.util.toVisible
import com.sparkmembership.sparkowner.R
import com.sparkmembership.sparkowner.config.AppConfig
import com.sparkmembership.sparkowner.constant.HELP_URL
import com.sparkmembership.sparkowner.constant.No_Internet_Connection
import com.sparkmembership.sparkowner.data.request.ProfilePictureReqDTO
import com.sparkmembership.sparkowner.databinding.FragmentProfileBinding
import com.sparkmembership.sparkowner.data.remote.Resource
import com.sparkmembership.sparkowner.presentation.components.CustomButton
import com.sparkmembership.sparkowner.presentation.ui.MainActivity
import com.sparkmembership.sparkowner.util.PicassoUtil.loadImage
import com.sparkmembership.sparkowner.util.noInternetDialogBox
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject


@AndroidEntryPoint
class ProfileFragment : Fragment() {

    lateinit var binding : FragmentProfileBinding
    val profileViewModel: ProfileViewModel by viewModels()


    @Inject
    lateinit var appConfig: AppConfig

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentProfileBinding.inflate(inflater,container,false)
        (activity as MainActivity).setBottomBarNavigationVisibility(false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnBack.setOnClickListener{
            findNavController().popBackStack()
        }

        binding.btnChangePasswordLayout.setOnClickListener{
            lifecycleScope.launch {
                profileViewModel.resetPassword()
            }
        }

        binding.btnRateUs.setOnClickListener{
            findNavController().navigate(R.id.rateUsFragment)
        }


        binding.btnHelp.setOnClickListener {

        }


        binding.btnLogout.setOnClickListener {
            showLogoutDialogbox()
        }

        binding.btnChooseImage.setOnClickListener {

            openCameraGallery()
        }

        binding.btnHelp.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(HELP_URL))
            startActivity(intent)
        }


        setUserDetails()
        apiResponseChangePassword()
        uploadImageApiResponse()
        observeLogout()

    }

    private fun openCameraGallery() {
        ImagePicker.with(this)
            .crop()
            .compress(512)
            .maxResultSize(1080, 1080)
            .saveDir(File(requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!, "ImagePicker"))
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

            if (base64String.isNotEmpty()){
                uploadProfileImage(base64String)
            }

            Log.d("Base64Image", base64String)


        }
    } else if (result.resultCode == ImagePicker.RESULT_ERROR) {
        Toast.makeText(requireContext(), ImagePicker.getError(result.data), Toast.LENGTH_SHORT).show()
    } else {
        Toast.makeText(requireContext(), "Task Cancelled", Toast.LENGTH_SHORT).show()

    }
    }

    private fun uploadProfileImage(base64String: String) {

        val picture = ProfilePictureReqDTO(base64String)
        lifecycleScope.launch {
            profileViewModel.uploadPicture(picture)
        }
    }

    private fun uploadImageApiResponse(){
        profileViewModel.uploadProfilePictureData.observe(viewLifecycleOwner){
            when(it){
                is Resource.Loading -> {
                    binding.includedProgressLayout.loaderView.toVisible()

                }
                is Resource.Success -> {
                    binding.includedProgressLayout.loaderView.toGone()
                    loadImage(it.data!!.result.pictureURL, binding.profileImage)
                    profileViewModel.uploadProfilePictureData.removeObservers(viewLifecycleOwner)

                }
                is Resource.Error -> {
                    binding.includedProgressLayout.loaderView.toGone()
                    profileViewModel.uploadProfilePictureData.removeObservers(viewLifecycleOwner)

                }
            }
        }
    }

    private fun setUserDetails() {

        profileViewModel.profileLiveData.observe(viewLifecycleOwner){
            loadImage(it.userDetails.userImage, binding.profileImage)
            binding.username.text = buildString {
                append(it.userDetails.firstName)
                append(" ")
                append(it.userDetails.lastName)
            }
        }

        profileViewModel.connectedLocationData.observe(viewLifecycleOwner){
            binding.userLocation.text = it.locationName

        }


    }



    fun apiResponseChangePassword() {
        profileViewModel.resetPasswordLiveData.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { response ->
                when (response) {
                    is Resource.Loading -> {
                        binding.includedProgressLayout.loaderView.toVisible()

                    }

                    is Resource.Success -> {
                        binding.includedProgressLayout.loaderView.toGone()
                        showForgotDialogbox()

                    }

                    is Resource.Error -> {
                        binding.includedProgressLayout.loaderView.toGone()

                        if (response.message.equals(No_Internet_Connection)){
                            noInternetDialogBox(requireContext())
                        }else{
                            showToast(requireContext(), response.message.toString())
                        }


                    }
                }
            }
        }
    }


    private fun showForgotDialogbox() {

        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_box_forgot_password, null)

        val builder = AlertDialog.Builder(requireContext())
        builder.setView(dialogView)

        val dialog = builder.create()
        dialog.show()

        val button = dialogView.findViewById<CustomButton>(R.id.btnDialogButton)
        button.setOnClickListener {
            findNavController().popBackStack()
            dialog.dismiss()
        }

    }


    private fun showLogoutDialogbox() {

        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_logout, null)

        val builder = AlertDialog.Builder(requireContext())
        builder.setView(dialogView)

        val dialog = builder.create()
        dialog.show()

        val btnLogout = dialogView.findViewById<CustomButton>(R.id.btnDialogButton)
        val btnCancel = dialogView.findViewById<CustomButton>(R.id.btnCancel)

        btnLogout.setOnClickListener {

            lifecycleScope.launch {
                profileViewModel.logoutUser(profileViewModel.logoutReqDto())
            }

            dialog.dismiss()
        }

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

    }

    private fun observeLogout(){
        profileViewModel.logout.observe(viewLifecycleOwner){event ->
            event.getContentIfNotHandled()?.let { response ->
                when(response){
                    is Resource.Loading -> {
                        binding.includedProgressLayout.loaderView.toVisible()
                    }
                    is Resource.Error -> {
                        binding.includedProgressLayout.loaderView.toGone()
                        showToast(requireContext(),response.message.toString())
                    }
                    is Resource.Success -> {
                        binding.includedProgressLayout.loaderView.toGone()
                        profileViewModel.logout(requireContext())
                    }
                }
            }

        }
    }






}