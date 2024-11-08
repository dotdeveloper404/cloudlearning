package com.sparkmembership.sparkowner.presentation.ui.timeslip.TimeSlipsHistory

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.sparkmembership.sparkfitness.util.DateUtil.UTC_DATE_CHECK_FORMAT
import com.sparkmembership.sparkowner.R
import com.sparkmembership.sparkowner.adapter.TimeSlipsHistoryAdaptor
import com.sparkmembership.sparkowner.data.remote.Resource
import com.sparkmembership.sparkowner.data.response.TimeSlipHistory
import com.sparkmembership.sparkowner.databinding.FragmentTimeSlipsHistoryBinding
import com.sparkmembership.sparkowner.presentation.components.getFirstAndLastDayOfMonth
import com.sparkmembership.sparkowner.presentation.ui.MainActivity
import com.sparkmembership.sparkowner.presentation.ui.timeslip.TimeSlipViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TimeSlipsHistory : Fragment(), TimeSlipFilterBottomSheetFragment.OnButtonClickListener,
    TimeSlipsHistoryAdaptor.OnTimeSlipListener {
    lateinit var binding: FragmentTimeSlipsHistoryBinding
    private val timeSlipViewModel by viewModels<TimeSlipViewModel>()
    lateinit var timeSlipsHistoryAdaptor: TimeSlipsHistoryAdaptor
    var timeSlipHistoryList = ArrayList<TimeSlipHistory>()
    var filterTimeSlipDto = FilterTimeSlipDto()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTimeSlipsHistoryBinding.inflate(inflater, container, false)
        (activity as MainActivity).setBottomBarNavigationVisibility(false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolBar()
        getTimeSlipHistory()
        observerViewModel()
        initRecyclerView()
        searchFilter()

    }

    private fun setToolBar() {
        (activity as MainActivity).showToolbar()
        (activity as? MainActivity)?.initializeCustomToolbar(
            title = getString(R.string.time_slips),
            toolbarColor = R.color.colorPrimary,
            showBackButton = true,
            icons = listOf(
                R.drawable.icon_filter to {
                    val bottomSheetFragment =
                        TimeSlipFilterBottomSheetFragment(filterTimeSlipDto)
                    bottomSheetFragment.setListener(this)
                    bottomSheetFragment.show(
                        parentFragmentManager,
                        "TimeSlipFilterBottomSheetFragment"
                    )
                }
            ),
            onBackPress = {
                findNavController().popBackStack()

            }
        )


    }


    private fun searchFilter() {
        binding.allContactSearchView.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {


                if (s.isNullOrEmpty()) {
                    binding.allContactSearchView.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_search_icon, 0, 0, 0
                    )
                } else {
                    binding.allContactSearchView.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_search_icon, 0, R.drawable.ic_cancel_icon, 0
                    )
                }

                filter(s.toString())
                clearSearch()
            }

            override fun afterTextChanged(s: Editable?) {
                clearSearch()
            }
        })
    }

    private fun initRecyclerView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        timeSlipsHistoryAdaptor = TimeSlipsHistoryAdaptor(this)
        binding.recyclerView.adapter = timeSlipsHistoryAdaptor
    }

    private fun observerViewModel() {
        timeSlipViewModel.timeSlipHistoryDto.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    binding.includedProgressLayout.loaderView.visibility = View.VISIBLE
                    binding.notFoundDataLayout.visibility = View.GONE
                }

                is Resource.Success -> {
                    if (it.data?.result?.isNotEmpty() == true) {
                        timeSlipHistoryList.addAll(it.data.result)
                        timeSlipsHistoryAdaptor.setTimeSlipHistory(timeSlipHistoryList)
                    }
                    if (it.data?.result?.isEmpty() == true) binding.notFoundDataLayout.visibility =
                        View.VISIBLE
                    binding.includedProgressLayout.loaderView.visibility = View.GONE
                }

                is Resource.Error -> {
                    if (it.data?.result?.isEmpty() == true) binding.notFoundDataLayout.visibility =
                        View.VISIBLE
                    binding.includedProgressLayout.loaderView.visibility = View.GONE
                }
            }
        }
    }

    private fun getTimeSlipHistory() {
        val (firstDay, lastDay) = getFirstAndLastDayOfMonth(UTC_DATE_CHECK_FORMAT)

        if (timeSlipHistoryList.isEmpty()) {
            lifecycleScope.launch {
                timeSlipViewModel.timeSlipHistory(startDate = firstDay, endDate = lastDay)
            }
        }
    }

    private fun filter(text: String) {
        val filteredlist: ArrayList<TimeSlipHistory> = ArrayList()

        for (item in timeSlipHistoryList) {
            if (item.userName.lowercase().contains(text.lowercase())) {
                filteredlist.add(item)
            }
        }
        if (filteredlist.isEmpty()) {
            Toast.makeText(requireContext(), "No Data Found..", Toast.LENGTH_SHORT).show()
        } else {
            timeSlipsHistoryAdaptor.setTimeSlipHistory(filteredlist)
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

    override fun onApplyFilter(startDate: String, endDate: String) {
        timeSlipHistoryList.clear()
        lifecycleScope.launch {
            timeSlipViewModel.timeSlipHistory(startDate = startDate, endDate = endDate)
        }
    }

    override fun onResetFilter() {
        timeSlipHistoryList.clear()
        lifecycleScope.launch {
            timeSlipViewModel.timeSlipHistory()
        }
    }

    data class FilterTimeSlipDto(
        var startDate: String = "",
        var endDate: String = ""
    )

    override fun onItemClick(timeSlipHistory: TimeSlipHistory) {
        val bundle = Bundle().apply {
            putInt("timeSlipUserID", timeSlipHistory.userID)
            putString("userName", timeSlipHistory.userName)
        }
        findNavController().navigate(R.id.timeSlipHistoryDetail, bundle)
    }
}


