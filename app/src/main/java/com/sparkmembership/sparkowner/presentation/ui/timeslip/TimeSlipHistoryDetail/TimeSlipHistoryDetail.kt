package com.sparkmembership.sparkowner.presentation.ui.timeslip.TimeSlipHistoryDetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.sparkmembership.sparkfitness.util.DateUtil.UTC_DATE_CHECK_FORMAT
import com.sparkmembership.sparkowner.R
import com.sparkmembership.sparkowner.adapter.TimeSlipHistoryDetailAdaptor
import com.sparkmembership.sparkowner.data.remote.Resource
import com.sparkmembership.sparkowner.data.response.TimeSlipUserDetail
import com.sparkmembership.sparkowner.databinding.FragmentTimeSlipHistoryDetailBinding
import com.sparkmembership.sparkowner.presentation.components.getFirstAndLastDayOfMonth
import com.sparkmembership.sparkowner.presentation.ui.MainActivity
import com.sparkmembership.sparkowner.presentation.ui.timeslip.TimeSlipViewModel
import com.sparkmembership.sparkowner.presentation.ui.timeslip.TimeSlipsHistory.TimeSlipFilterBottomSheetFragment
import com.sparkmembership.sparkowner.presentation.ui.timeslip.TimeSlipsHistory.TimeSlipsHistory.FilterTimeSlipDto
import com.sparkmembership.sparkowner.util.PaginationScrollListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TimeSlipHistoryDetail : Fragment(), TimeSlipFilterBottomSheetFragment.OnButtonClickListener {
    lateinit var binding : FragmentTimeSlipHistoryDetailBinding
    private var timeSlipUserID: Int? = null
    private var userName: String? = null
    private val timeSlipViewModel by viewModels<TimeSlipViewModel>()
    lateinit var timeSlipsHistoryDetailAdaptor : TimeSlipHistoryDetailAdaptor
    var timeSlipHistoryList = ArrayList<TimeSlipUserDetail>()
    private var currentPage: Int = 0
    private var isLastPage = false
    private var totalPages: Long = 0
    private var size: Int = 25
    private var isLoading = false
    private var startDate = ""
    private var endDate = ""

    var filterTimeSlipDto = FilterTimeSlipDto()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTimeSlipHistoryDetailBinding.inflate(inflater, container, false)
        arguments?.let {
            timeSlipUserID = it.getInt("timeSlipUserID")
            userName = it.getString("userName")
        }

        setToolBar()
        initRecyclerView()
        initializeDefaultDates()
        getTimeSlipHistoryDetail()
        observerViewModel()
        onScrollGetMoreData()
        return binding.root
    }

    private fun setToolBar() {
        (activity as MainActivity).showToolbar()
        (activity as? MainActivity)?.initializeCustomToolbar(
            title = userName.toString(),
            toolbarColor = R.color.colorPrimary,
            showBackButton = true,
            icons = listOf(
                R.drawable.icon_filter to {
                    val bottomSheetFragment =
                        TimeSlipFilterBottomSheetFragment(filterTimeSlipDto)
                    bottomSheetFragment.setListener(this)
                    bottomSheetFragment.show(parentFragmentManager, "TimeSlipFilterBottomSheetFragment")
                }
            ),
            onBackPress = {
                findNavController().popBackStack()

            }
        )


    }


    private fun initializeDefaultDates() {
        val (firstDay, lastDay) = getFirstAndLastDayOfMonth(UTC_DATE_CHECK_FORMAT)
        startDate = firstDay
        endDate = lastDay
    }

    private fun initRecyclerView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        timeSlipsHistoryDetailAdaptor = TimeSlipHistoryDetailAdaptor()
        binding.recyclerView.adapter = timeSlipsHistoryDetailAdaptor
    }

    private fun observerViewModel() {
        timeSlipViewModel.timeSlipDetailDto.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    binding.includedProgressLayout.loaderView.visibility = View.VISIBLE
                    binding.notFoundDataLayout.visibility = View.GONE
                }

                is Resource.Success -> {
                    if (it.data?.result?.isNotEmpty() == true){
                        timeSlipHistoryList.addAll(it.data.result)
                        timeSlipsHistoryDetailAdaptor.setTimeSlipHistoryDetail(timeSlipHistoryList)
                        if ((it.data.result.last().timeSlip.size) < 25) isLastPage = true
                    }
                    if (it.data?.result?.isEmpty() == true) binding.notFoundDataLayout.visibility =
                        View.VISIBLE
                    isLoading = false
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

    private fun onScrollGetMoreData() {
        binding.recyclerView.addOnScrollListener(object :
            PaginationScrollListener(binding.recyclerView.layoutManager as LinearLayoutManager) {
            override fun loadMoreItems() {
                isLoading = true
                currentPage += 1
                getTimeSlipHistoryDetail()
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

    private fun getTimeSlipHistoryDetail() {

        lifecycleScope.launch {
            timeSlipViewModel.timeSlipHistoryById(userId = timeSlipUserID!!, pageIndex = currentPage, pageSize = size, startDate = startDate, endDate = endDate)
        }
    }

    override fun onApplyFilter(startDate: String, endDate: String) {
        timeSlipHistoryList.clear()
        this.startDate = startDate
        this.endDate = endDate
        currentPage = 0
        size = 25
        lifecycleScope.launch {
            timeSlipViewModel.timeSlipHistoryById(startDate = startDate , endDate = endDate,userId = timeSlipUserID!!, pageIndex = currentPage, pageSize = size)
        }
    }

    override fun onResetFilter() {
        timeSlipHistoryList.clear()
        initializeDefaultDates()
        currentPage = 0
        size = 25
        isLoading = true
        isLastPage = false
        lifecycleScope.launch {
            timeSlipViewModel.timeSlipHistoryById(userId = timeSlipUserID!!, pageIndex = currentPage, pageSize = size,startDate = startDate , endDate = endDate)
        }
    }
}