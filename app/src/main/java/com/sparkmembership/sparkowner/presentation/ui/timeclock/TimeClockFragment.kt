package com.sparkmembership.sparkowner.presentation.ui.timeclock

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.OnTokenCanceledListener
import com.sparkmembership.sparkfitness.util.DateUtil
import com.sparkmembership.sparkfitness.util.DateUtil.calculateCurrentWorkingTime
import com.sparkmembership.sparkfitness.util.DateUtil.calculateDistance
import com.sparkmembership.sparkfitness.util.DateUtil.calculateWorkHourForMonth
import com.sparkmembership.sparkfitness.util.DateUtil.calculateWorkHourForToday
import com.sparkmembership.sparkfitness.util.DateUtil.getCurrentDate
import com.sparkmembership.sparkfitness.util.DateUtil.processTimeSlipsByMonth
import com.sparkmembership.sparkfitness.util.showToast
import com.sparkmembership.sparkfitness.util.toGone
import com.sparkmembership.sparkfitness.util.toVisible
import com.sparkmembership.sparkowner.R
import com.sparkmembership.sparkowner.adapter.TimeSlipHistoryDetailAdaptor
import com.sparkmembership.sparkowner.data.remote.Resource
import com.sparkmembership.sparkowner.data.response.TimeSlip
import com.sparkmembership.sparkowner.data.response.TimeSlipUserDetail
import com.sparkmembership.sparkowner.databinding.FragmentTimeClockBinding
import com.sparkmembership.sparkowner.presentation.ui.MainActivity
import com.sparkmembership.sparkowner.presentation.ui.timeslip.TimeSlipViewModel
import com.sparkmembership.sparkowner.util.showDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TimeClockFragment : Fragment() {
    lateinit var binding: FragmentTimeClockBinding
    lateinit var timeSlipsHistoryDetailAdaptor: TimeSlipHistoryDetailAdaptor
    private val timeSlipViewModel by viewModels<TimeSlipViewModel>()
    var timeSlipHistoryList = ArrayList<TimeSlipUserDetail>()
    private val handler = Handler(Looper.getMainLooper())
    private var runnable: Runnable? = null

    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var seconds = 0
    private var isRunning = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                getLocation()
            } else {
                turnOnLocation()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTimeClockBinding.inflate(inflater, container, false)
        (activity as MainActivity).setBottomBarNavigationVisibility(false)
        setToolBar()
        init()
        getUserTimeSlipHistory()
        observeTimeSlipHistory()
        observerClockIn()
        observerClockOut()
        initRecyclerView()
        setClockInAndOutListener()
        return binding.root
    }

    private fun setToolBar() {
        (activity as MainActivity).showToolbar()
        (activity as? MainActivity)?.initializeCustomToolbar(
            title = getString(R.string.time_clock),
            toolbarColor = R.color.colorPrimary,
            showBackButton = true,
            icons = listOf(),
            onBackPress = {
                findNavController().popBackStack()

            }
        )


    }

    private fun init() {
        binding.itemDate.text = getCurrentDate()
    }

    private fun outsideLocation(){
        showDialog(
            requireContext(),
            getString(R.string.you_are_outside_the_location),
            getString(R.string.i_am_in),
            R.drawable.location_not_found
        ){
        }
    }

    private fun turnOnLocation(){
        showDialog(
            requireContext(),
            getString(R.string.turn_on_your_location),
            getString(R.string.go_to_setting),
            R.drawable.location_not_found
        ){
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(intent)
            findNavController().popBackStack()
        }
    }

    private fun updateTimerText() {
        val hours = seconds / 3600
        val minutes = (seconds % 3600) / 60
        val remainingMinutes = minutes * 100/60.0
        val remainingMinutesRounded = remainingMinutes/ 100
        val remainingMinutesPercentage =  String.format("%.2f", remainingMinutesRounded - remainingMinutesRounded.toInt()).substring(1)
        binding.duration.text = context?.getString(R.string.work_hours, "$hours$remainingMinutesPercentage")
    }

    private fun startTimer() {
        handler.post(object : Runnable {
            override fun run() {
                if (isRunning) {
                    seconds++
                    updateTimerText()
                    handler.postDelayed(this, 1000)
                }
            }
        })
    }

    private fun setClockInAndClockOut() {
        if(timeSlipHistoryList.isNotEmpty() && timeSlipHistoryList.first().timeSlip.first().timeOut.isNullOrEmpty() ){
            binding.clockinOutButton.text = getString(R.string.clock_out)
            binding.currentStartTime.text = getString(R.string.start_time)
            seconds = calculateCurrentWorkingTime(timeSlipHistoryList.first().timeSlip.first().timeIn).toInt()
            stopClock()
            binding.currentTime.text = DateUtil.formatTimeInOut(timeSlipHistoryList.first().timeSlip.first().timeIn,null).replace("-", "").trim()
            isRunning = true
            startTimer()
            updateTimerText()
            binding.clockedInText.toVisible()
        }else{
            binding.clockinOutButton.text = getString(R.string.clock_in)
            binding.currentStartTime.text = getString(R.string.lets_get_to_work)
            seconds = 0
            updateTimerText()
            startClock()
            isRunning = false
            binding.clockedInText.toGone()
        }
    }

    private fun setClockInAndOutListener() {
        binding.clockinOutButton.setOnClickListener {
            requestLocationPermission()
        }
    }

    private fun requestLocationPermission() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                getLocation()
            }

            else -> {
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }

    private fun getLocation() {
        binding.includedProgressLayout.loaderView.toVisible()
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                object : CancellationToken() {
                    override fun onCanceledRequested(p0: OnTokenCanceledListener) =
                        CancellationTokenSource().token

                    override fun isCancellationRequested() = false
                })
                .addOnSuccessListener { location: Location? ->
                    binding.includedProgressLayout.loaderView.toGone()

                    if (location != null) {
                        val distance = calculateDistance(location,timeSlipViewModel.userLocation)
                        if (distance != -0.1 && distance < 300){
                            if(timeSlipHistoryList.isNotEmpty() && timeSlipHistoryList.first().timeSlip.first().timeOut.isNullOrEmpty()){
                                clockOut()
                            }else{
                                clockIn()
                                if (!isRunning) {
                                    isRunning = true
                                    startTimer()
                                }
                            }
                        }else{
                            outsideLocation()
                        }
                    } else {
                        turnOnLocation()
                    }
                }
                .addOnFailureListener { e ->
                    showToast("Failed to get location: ${e.message}",requireContext())
                }
        } else {
            showDialog(
                requireContext(),
                getString(R.string.turn_on_your_location),
                getString(R.string.go_to_setting),
                R.drawable.location_not_found
            ){
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
                findNavController().popBackStack()
            }
        }
    }

    private fun startClock() {
        runnable = object : Runnable {
            override fun run() {
                binding.currentTime.text = DateUtil.getCurrentTime()
                handler.postDelayed(this, 1000)
            }
        }
        handler.post(runnable as Runnable)
    }

    private fun stopClock() {
        runnable?.let {
            handler.removeCallbacks(it)
            runnable = null
        }
    }


    private fun observerClockIn() {
        timeSlipViewModel.clockIn.observe(viewLifecycleOwner){
            when (it) {
                is Resource.Loading -> {
                    binding.includedProgressLayout.loaderView.toVisible()
                }

                is Resource.Success -> {
                    if (it.data?.result?.timeIn?.isNotEmpty() == true && timeSlipHistoryList.isNotEmpty()) {
                        val timeSlip = TimeSlip(timeIn = it.data.result.timeIn)
                        timeSlipHistoryList.first().timeSlip.add(0, timeSlip)
                        timeSlipsHistoryDetailAdaptor.notifyDataSetChanged()
                        timeSlipsHistoryDetailAdaptor.notifyItemChanged(0)
                        setClockInAndClockOut()
                    }
                    binding.includedProgressLayout.loaderView.toGone()
                }

                is Resource.Error -> {
                    binding.includedProgressLayout.loaderView.toGone()
                    if(it.data?.hasError == true){
                        showToast(getString(R.string.already_clocked_in), requireContext())
                    }
                    getUserTimeSlipHistory()
                }
            }
        }
    }

    private fun observerClockOut() {
        timeSlipViewModel.clockOut.observe(viewLifecycleOwner){
            when (it) {

                is Resource.Loading -> {
                    binding.includedProgressLayout.loaderView.toVisible()
                }

                is Resource.Success -> {
                    if (timeSlipHistoryList.isNotEmpty()){
                        timeSlipHistoryList.first().timeSlip.first().timeOut = it.data?.timestamp.toString()
                        timeSlipsHistoryDetailAdaptor.setTimeSlipHistoryDetail(timeSlipHistoryList)
                        binding.todayWorkingHour.text = calculateWorkHourForToday(timeSlipHistoryList.first().timeSlip)
                        binding.totalPayPeriod.text = calculateWorkHourForMonth(timeSlipHistoryList.first().timeSlip)
                        binding.includedProgressLayout.loaderView.toGone()
                        setClockInAndClockOut()
                    }
                }

                is Resource.Error -> {
                    binding.includedProgressLayout.loaderView.toGone()
                    if(it.data?.hasError == true){
                        showToast(getString(R.string.already_clocked_out), requireContext())
                    }
                    getUserTimeSlipHistory()
                }
            }
        }
    }

    private fun observeTimeSlipHistory() {
        timeSlipViewModel.userTimeSlipHistory.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    binding.includedProgressLayout.loaderView.toVisible()
                }

                is Resource.Success -> {
                    if (it.data?.result?.isNotEmpty() == true) {
                        timeSlipHistoryList.clear()
                        timeSlipHistoryList.addAll(processTimeSlipsByMonth(it.data.result))
                        timeSlipsHistoryDetailAdaptor.setTimeSlipHistoryDetail(timeSlipHistoryList)
                        binding.totalPayPeriod.text = calculateWorkHourForMonth(it.data.result)
                        binding.todayWorkingHour.text = calculateWorkHourForToday(it.data.result)
                    }
                    setClockInAndClockOut()
                    if (it.data?.result?.isEmpty() == true) binding.notFoundDataLayout.toVisible()
                    binding.includedProgressLayout.loaderView.toGone()
                }

                is Resource.Error -> {
                    if (it.data?.result?.isEmpty() == true) binding.notFoundDataLayout.toVisible()
                    binding.includedProgressLayout.loaderView.toGone()
                }
            }
        }
    }

    private fun getUserTimeSlipHistory() {
        lifecycleScope.launch {
            timeSlipViewModel.userTimeSlipHistory()
        }
    }

    private fun clockIn(){
        lifecycleScope.launch {
            timeSlipViewModel.clockIn()
        }
    }

    private fun clockOut(){
        lifecycleScope.launch {
            timeSlipViewModel.clockOut()
        }
    }

    private fun initRecyclerView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        timeSlipsHistoryDetailAdaptor = TimeSlipHistoryDetailAdaptor(true)
        binding.recyclerView.adapter = timeSlipsHistoryDetailAdaptor
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }
}