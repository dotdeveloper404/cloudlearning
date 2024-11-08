package com.sparkmembership.sparkowner.presentation.ui.more

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import androidx.recyclerview.widget.LinearLayoutManager
import com.sparkmembership.sparkowner.R
import com.sparkmembership.sparkowner.SparkOwner.Companion.ctx
import com.sparkmembership.sparkowner.data.entity.MoreItem
import com.sparkmembership.sparkowner.data.entity.moreItems
import com.sparkmembership.sparkowner.databinding.FragmentMoreBinding
import com.sparkmembership.sparkowner.presentation.ui.MainActivity
import com.sparkmembership.sparkowner.presentation.ui.more.adapter.MoreAdapter
import com.sparkmembership.sparkowner.util.PicassoUtil.loadImage
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MoreFragment : Fragment(), MoreAdapter.OnItemListener {

    private lateinit var binding: FragmentMoreBinding
    private lateinit var moreAdapter: MoreAdapter
    private val moreViewModel by viewModels<MoreViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMoreBinding.inflate(inflater, container, false)
        (activity as MainActivity).setBottomBarNavigationVisibility(true)


        setProfileData()
        moreAdapterData()


        binding.goToProfile.setOnClickListener{
            findNavController().navigate(R.id.action_moreFragment_to_profileFragment)
        }



        setToolBar()

        return binding.root
    }

    private fun setToolBar() {

        (activity as MainActivity).hideToolbar()
    }

    private fun setProfileData() {
        moreViewModel.profileLiveData.observe(viewLifecycleOwner){
            loadImage(it.userDetails.userImage,binding.profileImage)
            binding.username.text = """${it.userDetails.firstName} ${it.userDetails.lastName}"""
        }

        moreViewModel.connectedLocationLiveData.observe(viewLifecycleOwner){
            binding.userLocation.text = it.locationName
        }

    }

    private fun moreAdapterData() {

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        moreAdapter = MoreAdapter(moreItems, this)
        binding.recyclerView.adapter = moreAdapter
    }

    override fun onItemClick(item: MoreItem) {

        if (item.title == context?.getString(R.string.view_contacts)){
            val bundle = Bundle()
            bundle.putString(getString(R.string.viewcontacts), item.title)
            bundle.putBoolean(getString(R.string.iscomingfrommore), true)
            findNavController().navigate(R.id.allContactsFragment, bundle)
        }
        if (item.title == context?.getString(R.string.view_time_slips)){
            findNavController().navigate(R.id.timeSlipsHistory)
        } else if (item.title == context?.getString(R.string.nav_clock_in_out)){
            findNavController().navigate(R.id.timeClockFragment)
        }else if (item.title == context?.getString(R.string.nav_who_is_in)){
            findNavController().navigate(R.id.whoIsInFragment)
        }else if (item.title== context?.getString(R.string.quickly_add_a_contact)){
            findNavController().navigate(R.id.addContactsFragment)
        }else if(item.title == context?.getString(R.string.invoice_history)){
            findNavController().navigate(R.id.invoicesFragment)
        }

    }

}