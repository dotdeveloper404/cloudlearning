package com.sparkmembership.sparkowner.presentation.ui.rateUs

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.sparkmembership.sparkfitness.util.showToast
import com.sparkmembership.sparkfitness.util.toGone
import com.sparkmembership.sparkfitness.util.toVisible
import com.sparkmembership.sparkowner.R
import com.sparkmembership.sparkowner.constant.APP_URl
import com.sparkmembership.sparkowner.data.entity.RateUsOption
import com.sparkmembership.sparkowner.data.remote.Resource
import com.sparkmembership.sparkowner.data.request.RateUsReqDTO
import com.sparkmembership.sparkowner.databinding.FragmentRateUsBinding
import com.sparkmembership.sparkowner.presentation.listeners.OnOptionSelectedListener
import com.sparkmembership.sparkowner.presentation.ui.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RateUsFragment : Fragment(), OnOptionSelectedListener {

    lateinit var binding: FragmentRateUsBinding
    lateinit var adapter: RateUsAdapter
    val rateUsViewModel : RateUsViewModel by viewModels()
    var selectedEmojiValue = 0
    var selectedOptionsList = ArrayList<String>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentRateUsBinding.inflate(layoutInflater, container, false)

        (activity as MainActivity).setBottomBarNavigationVisibility(false)
        (activity as MainActivity).hideToolbar()


        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnCancel.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnSubmit.setOnClickListener {
            if (validateData()){
                callRateUsApi()
            }
        }


        setCharacterCount()
        setRatingEmoji()
        setRecyclerview()
        observeRateUsApi()
    }

    private fun observeRateUsApi() {

        rateUsViewModel.rateUsData.observe(viewLifecycleOwner){
            when(it){

                is Resource.Loading -> {
                    binding.includedProgressLayout.loaderView.toGone()
                }

                is Resource.Error -> {
                    binding.includedProgressLayout.loaderView.toVisible()
                    rateUsViewModel.rateUsData.removeObservers(viewLifecycleOwner)

                }

                is Resource.Success ->{
                    binding.includedProgressLayout.loaderView.toGone()
                    rateUsViewModel.rateUsData.removeObservers(viewLifecycleOwner)
                    findNavController().popBackStack()
                }
            }
        }
    }

    private fun callRateUsApi() {

        val rateUsReqDTO = RateUsReqDTO(
            selectedOptionsList,
            selectedEmojiValue,
            binding.editNote.text.toString()
        )

        lifecycleScope.launch {
            binding.includedProgressLayout.loaderView.toVisible()
            rateUsViewModel.rateUs(rateUsReqDTO)
        }
    }


    private fun validateData() : Boolean {
        if (selectedEmojiValue==0){
            showToast(requireContext(), getString(R.string.please_select_emoji))
            return false
        }
        if (binding.editNote.text.toString().isEmpty()){
            showToast(requireContext(), getString(R.string.please_enter_note))
            return false
        }
        if (selectedEmojiValue > 3 && selectedOptionsList.size==0){
            showToast(requireContext(), getString(R.string.select_any_option))
            return false
        }

        return true

    }

    private fun setCharacterCount() {
        binding.editNote.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {

                if ((s?.length ?: 0) > 149) {
                    binding.editNote.setText(s?.substring(0, 150))
                    binding.editNote.setSelection(150)
                }
                binding.textCharacterCount.text = "${s?.length ?: 0}/150"

            }
        })
    }

    private fun setRatingEmoji() {


        binding.emojiRadioGroup.setOnCheckedChangeListener { group, checkedId ->
            for (i in 0 until group.childCount) {
                val button = group.getChildAt(i) as RadioButton
                if (button.id == checkedId) {

                    button.alpha = 1f

                    selectedEmojiValue = when (button.id) {
                        R.id.angry_emoji -> 1
                        R.id.sad_emoji -> 2
                        R.id.neutral_emoji -> 3
                        R.id.happy_emoji -> 4
                        R.id.very_happy_emoji -> 5
                        else -> 0
                    }

                    if (selectedEmojiValue>3){
                        openPlayStoreForRating(requireContext())
                        binding.btnSubmit.toGone()
                        binding.recyclerView.toGone()
                        binding.textWhatswrong.toGone()
                    }else{
                        binding.btnSubmit.toVisible()
                        binding.recyclerView.toVisible()
                        binding.textWhatswrong.toVisible()

                    }

                } else {
                    button.alpha = 0.5f
                }
            }
        }


    }

    private fun setRecyclerview() {

        adapter = RateUsAdapter(RateUsOption().getAllOptions(), isMultiSelect = true, this)
        binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.recyclerView.adapter = adapter
    }


    fun openPlayStoreForRating(context: Context) {
        val packageName = context.packageName
        val uri = Uri.parse("market://details?id=$packageName")

        val intent = Intent(Intent.ACTION_VIEW, uri)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        try {
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            // If Play Store app is not available, open the link in a browser
            val playStoreUri = Uri.parse(APP_URl)
            val browserIntent = Intent(Intent.ACTION_VIEW, playStoreUri)
            browserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(browserIntent)
        }
    }


    override fun onOptionSelected(selectedOptions: ArrayList<RateUsOption>) {
        selectedOptionsList.clear()
        val newList = selectedOptions.map { it.name }
        selectedOptionsList.addAll(newList)
        Log.d("selectedOptionsList", selectedOptionsList.toString())
    }




}