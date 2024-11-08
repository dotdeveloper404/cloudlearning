package com.sparkmembership.sparkowner.presentation.ui.forgotPassword

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.sparkmembership.sparkfitness.util.showToast
import com.sparkmembership.sparkfitness.util.toGone
import com.sparkmembership.sparkfitness.util.toVisible
import com.sparkmembership.sparkowner.R
import com.sparkmembership.sparkowner.databinding.FragmentForgotPasswordBinding
import com.sparkmembership.sparkowner.data.remote.Resource
import com.sparkmembership.sparkowner.presentation.components.CustomButton
import com.sparkmembership.sparkowner.util.isValidEmail
import com.sparkmembership.sparkowner.util.showDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ForgotPasswordFragment : Fragment() {

    lateinit var binding: FragmentForgotPasswordBinding
    val forgotPasswordViewModel: ForgotPasswordViewModel by viewModels()



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentForgotPasswordBinding.inflate(layoutInflater, container, false)

        binding.btnForgotPassword.setOnClickListener{
            if (isValidate()){
                lifecycleScope.launch {
                    forgotPasswordViewModel.forgotPassword(
                        forgotPasswordViewModel.fogetPasswordObject(email = binding.emailEdt.text.toString())
                    )
                }
            }
        }

        apiResponse()

        return binding.root
    }





    private fun isValidate(): Boolean {
        if (!isValidEmail(binding.emailEdt.text.toString())){
            binding.emailLayout.error = getString(R.string.please_enter_valid_email)
            return false
        }
        return true
    }


    fun apiResponse(){
        forgotPasswordViewModel.forgotPasswordLiveData.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { response ->
                when (response) {
                    is Resource.Loading -> {
                        binding.includedProgressLayout.loaderView.toVisible()
                    }

                    is Resource.Success -> {
                        binding.includedProgressLayout.loaderView.toGone()
                        showDialog(
                            requireContext(),
                            getString(R.string.password_link_is_sent_to_your_email),
                            getString(R.string.okay),
                            R.drawable.password_link
                        ){
                            findNavController().popBackStack()
                        }

                    }

                    is Resource.Error -> {
                        binding.includedProgressLayout.loaderView.toGone()
                        showToast(requireContext(), response.message.toString())
                    }
                }
            }
        }
    }
}