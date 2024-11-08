package com.sparkmembership.sparkowner.presentation.ui.signIn

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.sparkmembership.sparkfitness.util.showToast
import com.sparkmembership.sparkfitness.util.toGone
import com.sparkmembership.sparkfitness.util.toVisible
import com.sparkmembership.sparkowner.R
import com.sparkmembership.sparkowner.data.remote.Resource
import com.sparkmembership.sparkowner.data.response.SignInResDto
import com.sparkmembership.sparkowner.databinding.FragmentSignInBinding
import com.sparkmembership.sparkowner.presentation.SharedViewModel
import com.sparkmembership.sparkowner.presentation.ui.MainActivity
import com.sparkmembership.sparkowner.util.isValidEmail
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@AndroidEntryPoint
class SignInFragment : Fragment() {


    lateinit var binding: FragmentSignInBinding
    private val signInViewModel by viewModels<SignInViewModel>()
    private val sharedViewModel by activityViewModels<SharedViewModel>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentSignInBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.signInButton.setOnClickListener {
            if (validateCredentials()) {
                callSignInApi()
            }
        }

        binding.forgotPassword.setOnClickListener {
            findNavController().navigate(R.id.forgotPasswordFragment)

        }

        validatingEmail()
        apiResponse()
    }

    private fun validatingEmail() {

        binding.emailEdt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val email = s.toString()
                when {
                    email.isEmpty() -> {
                        // If the email field is empty, hide the icon
                        binding.emailLayout.endIconDrawable = null
                    }

                    isValidEmail(email) -> {
                        // If the email is valid, show the check icon
                        binding.emailLayout.endIconDrawable =
                            ContextCompat.getDrawable(requireContext(), R.drawable.icon_check)
                    }

                    else -> {
                        // If the email is invalid, show the cross icon
                        binding.emailLayout.endIconDrawable =
                            ContextCompat.getDrawable(requireContext(), R.drawable.icon_cross)
                    }
                }
            }
        })

    }

    private fun callSignInApi() {
        lifecycleScope.launch {
            signInViewModel.signIn(
                signInViewModel.getLoginDetails(
                    binding.emailEdt.text.toString(),
                    binding.passwordEdt.text.toString()
                )!!,
                callback = { data ->
                    sharedViewModel.setSignInData(data)
                }
            )
        }

    }

    private fun validateCredentials(): Boolean {
        if (binding.emailEdt.text.isNullOrEmpty()) {
            showToast(requireContext(), getString(R.string.please_enter_email))
            return false
        }
        if (binding.passwordEdt.text.isNullOrEmpty()) {
            showToast(requireContext(), getString(R.string.please_enter_password))
            return false
        }

        if (!isValidEmail(binding.emailEdt.text.toString())) {
            showToast(requireContext(), getString(R.string.please_enter_valid_email))

            return false
        }

//        if (binding.passwordEdt.text.toString().length < 6){
//            showToast(requireContext(), getString(R.string.please_enter_valid_password))
//            return false
//        }
        return true

    }

    fun apiResponse() {
        signInViewModel.signInLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    binding.includedProgressLayout.loaderView.toVisible()
                }

                is Resource.Success -> {
                    navigateToRespectiveScreen(it.data)

                }

                is Resource.Error -> {
                    binding.includedProgressLayout.loaderView.toGone()
                    showToast(requireContext(), it.message.toString())
                }
            }
        }
    }

    private fun navigateToRespectiveScreen(data: SignInResDto?) {

        if (data?.result?.userDetails?.connectedLocations?.size == 1 || data?.result?.userDetails?.connectedLocations?.isEmpty() == true) {
            lifecycleScope.launch {
                delay(1000)
                binding.includedProgressLayout.loaderView.toGone()
                (activity as MainActivity).setMainGraph()
            }
        } else {
            binding.includedProgressLayout.loaderView.toGone()
            if (findNavController().graph.findNode(R.id.signInFragment) != null) {

                findNavController().navigate(R.id.connectedLocationFragment)
            }
        }
    }

}