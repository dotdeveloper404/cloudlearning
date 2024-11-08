package com.sparkmembership.sparkowner.presentation.ui.location

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.sparkmembership.sparkfitness.util.showToast
import com.sparkmembership.sparkfitness.util.toGone
import com.sparkmembership.sparkfitness.util.toVisible
import com.sparkmembership.sparkowner.data.remote.Resource
import com.sparkmembership.sparkowner.data.request.ChangeConnectedLocationReqDTO
import com.sparkmembership.sparkowner.data.response.ConnectedLocation
import com.sparkmembership.sparkowner.databinding.FragmentConnectedLocationBinding
import com.sparkmembership.sparkowner.presentation.SharedViewModel
import com.sparkmembership.sparkowner.presentation.listeners.OnConnectedLocationsItemClickListener
import com.sparkmembership.sparkowner.presentation.ui.MainActivity
import com.sparkmembership.sparkowner.presentation.ui.location.adapter.ConnectedLocationsAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@AndroidEntryPoint
class ConnectedLocationFragment : Fragment(), OnConnectedLocationsItemClickListener {

    lateinit var binding: FragmentConnectedLocationBinding
    lateinit var adapter: ConnectedLocationsAdapter
    private val connectedLocationViewModel by viewModels<ConnectedLocationViewModel>()
    private val sharedViewModel by activityViewModels<SharedViewModel>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentConnectedLocationBinding.inflate(layoutInflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getDataFromSharedViewModel()
        changeConnectedLocationApiRespose()
    }


    private fun getDataFromSharedViewModel() {
        sharedViewModel.signInLiveData.observe(viewLifecycleOwner) { signInData ->
            connectedLocationViewModel.setSignInData(signInData)
            setAdapters(signInData.result.userDetails.connectedLocations as ArrayList<ConnectedLocation>)
        }
    }

    private fun setAdapters(list: ArrayList<ConnectedLocation>?) {

        if (!list.isNullOrEmpty()) {
            val linearLayoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            binding.recyclerView.layoutManager = linearLayoutManager

            adapter = ConnectedLocationsAdapter(list, this)
            binding.recyclerView.adapter = adapter
        }
    }


    override fun onConnectedLocationsItemClick(connectedLocation: ConnectedLocation) {

        lifecycleScope.launch {
            connectedLocationViewModel.changeConnectedLocation(ChangeConnectedLocationReqDTO(connectedLocation.locationID, connectedLocation.userID), connectedLocation)
        }


    }

    fun changeConnectedLocationApiRespose(){
        connectedLocationViewModel.changeConnectedLocation.observe(viewLifecycleOwner){
            when(it){
                is Resource.Loading -> {
                    binding.includedProgressLayout.loaderView.toVisible()
                }
                is Resource.Success -> {
                    binding.includedProgressLayout.loaderView.toGone()
                    (activity as MainActivity).setMainGraph()
                }
                is Resource.Error -> {
                    binding.includedProgressLayout.loaderView.toGone()
                    showToast(requireContext(), it.message.toString())
                }
            }
        }

    }


}