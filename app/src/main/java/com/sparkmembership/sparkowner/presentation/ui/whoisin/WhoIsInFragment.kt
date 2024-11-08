package com.sparkmembership.sparkowner.presentation.ui.whoisin

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.sparkmembership.sparkfitness.util.DateUtil
import com.sparkmembership.sparkfitness.util.DateUtil.calculateWorkHour
import com.sparkmembership.sparkfitness.util.DateUtil.calculateWorkHours
import com.sparkmembership.sparkfitness.util.DateUtil.getTodayDate
import com.sparkmembership.sparkfitness.util.toVisible
import com.sparkmembership.sparkowner.R
import com.sparkmembership.sparkowner.adapter.StaffMemberTimeClockAdapter
import com.sparkmembership.sparkowner.data.remote.Resource
import com.sparkmembership.sparkowner.data.response.StaffMemberTimeClockResult
import com.sparkmembership.sparkowner.databinding.FragmentWhoIsInBinding
import com.sparkmembership.sparkowner.presentation.ui.MainActivity
import com.sparkmembership.sparkowner.util.applySpannableText
import com.sparkmembership.sparkowner.util.wordsToStyle1
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class WhoIsInFragment : Fragment(), StaffMemberTimeClockAdapter.OnStaffMemberListener {
    lateinit var binding: FragmentWhoIsInBinding
    private val whoIsInViewModel by viewModels<WhoIsInViewModel>()
    var staffMemberTimeClock = ArrayList<StaffMemberTimeClockResult>()
    var staffMemberTimeClockAdapter : StaffMemberTimeClockAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        binding = FragmentWhoIsInBinding.inflate(inflater, container, false)
        setToolBar()
        initRecyclerView()
        initPieChart()
        getStaffMemberClockTime()
        observerViewModel()
        return binding.root
    }

    private fun setToolBar() {

        (activity as MainActivity).showToolbar()

        (activity as? MainActivity)?.initializeCustomToolbar(
            title = getString(R.string.who_is_in),
            toolbarColor = R.color.colorPrimary,
            showBackButton = true,
            icons = listOf(),
            onBackPress = {
                findNavController().popBackStack()
            }
        )
    }

    private fun getStaffMemberClockTime() {
        lifecycleScope.launch {
            whoIsInViewModel.getStaffMemberTimeClock(getTodayDate())
        }
    }
    private fun observerViewModel() {
        whoIsInViewModel.staffMemberTimeClock.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    binding.includedProgressLayout.loaderView.visibility = View.VISIBLE
                    binding.notFoundDataLayout.visibility = View.GONE
                }

                is Resource.Success -> {
                    if (it.data?.result?.isNotEmpty() == true){
                        staffMemberTimeClock.addAll(it.data.result)
                        staffMemberTimeClockAdapter?.setTimeSlipHistory(staffMemberTimeClock)
                        val (clockInCount, clockOutCount) = totalClockInAndOut()
                        binding.clockInCount.text = getString(R.string.clocked_in, clockInCount)
                        binding.clockOutCount.text = getString(R.string.clocked_Out, clockOutCount)
                        applySpannableText(requireContext(),binding.totalHoursText,calculateWorkHour(staffMemberTimeClock),
                            wordsToStyle1
                        )
                    }else{
                        binding.notFoundDataLayout.toVisible()
                    }
                    binding.includedProgressLayout.loaderView.visibility = View.GONE
                    setPieChart()
                }

                is Resource.Error -> {
                    binding.notFoundDataLayout.visibility =
                        View.VISIBLE
                    binding.includedProgressLayout.loaderView.visibility = View.GONE
                }
            }
        }
    }

    private fun initRecyclerView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        staffMemberTimeClockAdapter = StaffMemberTimeClockAdapter(requireContext(),this)
        binding.recyclerView.adapter = staffMemberTimeClockAdapter
        binding.todayDateText.text = DateUtil.getCurrentDate()
    }

    private fun initPieChart(){
        binding.pieChart.setExtraOffsets(5f, 10f, 5f, 5f)
        binding.pieChart.setDrawHoleEnabled(true)
        binding.pieChart.setHoleColor(Color.TRANSPARENT)
        binding.pieChart.setTransparentCircleColor(Color.WHITE)
        binding.pieChart.setTransparentCircleAlpha(110)
        binding.pieChart.holeRadius = 75f
        binding.pieChart.transparentCircleRadius = 75f
        binding.pieChart.legend.isEnabled = false
        binding.pieChart.setDrawEntryLabels(false);
        binding.pieChart.description.isEnabled = false
        binding.pieChart.isHighlightPerTapEnabled = false
        binding.pieChart.invalidate()
    }

    private fun setPieChart() {
        val userTimeMap = mutableMapOf<String, Float>()

        for (staff in staffMemberTimeClock) {
            try {
                if (staff.timeOut != ""){
                    calculateWorkHours(staff.timeIn, staff.timeOut)
                    val durationInHours = calculateWorkHours(staff.timeIn, staff.timeOut).toDouble()

                    val userName = "${staff.firstName} ${staff.lastName}"
                    userTimeMap[userName] = userTimeMap.getOrDefault(userName, 0f) + durationInHours.toFloat()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        val entries = ArrayList<PieEntry>()
        for ((user, totalHours) in userTimeMap) {
            entries.add(PieEntry(totalHours, user))
        }

        val dataSet = PieDataSet(entries, "Staff Time")
        dataSet.setDrawIcons(false)
        dataSet.valueTextSize = 0F

        val colors: ArrayList<Int> = ArrayList()
        colors.add(ContextCompat.getColor(requireContext(), R.color.light_pink))
        colors.add(ContextCompat.getColor(requireContext(), R.color.light_lavender))
        colors.add(ContextCompat.getColor(requireContext(), R.color.pale_yellow))
        colors.add(ContextCompat.getColor(requireContext(), R.color.light_green))
        colors.add(ContextCompat.getColor(requireContext(), R.color.light_sky_blue))
        colors.add(ContextCompat.getColor(requireContext(), R.color.peachy_orange))
        colors.add(ContextCompat.getColor(requireContext(), R.color.deep_magenta))
        colors.add(ContextCompat.getColor(requireContext(), R.color.purple))
        dataSet.colors = colors.take(entries.size)

        val data = PieData(dataSet)
        binding.pieChart.setData(data)

        binding.pieChart.highlightValues(null)
        binding.totalTxt.toVisible()

    }

    fun totalClockInAndOut(): Pair<Int, Int> {
        var clockInCount = 0
        var clockOutCount = 0

        for (staff in staffMemberTimeClock) {
            if (!staff.timeOut.isNullOrEmpty()) {
                clockOutCount++
            } else {
                clockInCount++
            }
        }

        return Pair(clockInCount, clockOutCount)
    }

    override fun onClick(staffMemberTimeClockResult: StaffMemberTimeClockResult) {
        val userName = staffMemberTimeClockResult.firstName + " " + staffMemberTimeClockResult.lastName
        val bundle = Bundle().apply {
            putInt("timeSlipUserID", staffMemberTimeClockResult.userID)
            putString("userName", userName)
        }
        findNavController().navigate(R.id.timeSlipHistoryDetail,bundle)
    }

}