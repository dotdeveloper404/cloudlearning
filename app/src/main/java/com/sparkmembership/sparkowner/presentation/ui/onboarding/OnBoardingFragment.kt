package com.sparkmembership.sparkowner.presentation.ui.onboarding

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.sparkmembership.sparkowner.R
import com.sparkmembership.sparkowner.databinding.FragmentOnBoardingBinding
import com.sparkmembership.sparkowner.util.applySpannableText
import com.sparkmembership.sparkowner.util.getData
import com.sparkmembership.sparkowner.util.wordsToStyle1
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OnBoardingFragment : Fragment() {

    lateinit var binding: FragmentOnBoardingBinding
    lateinit var onboardingPagerAdapter: OnBoardingPagerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOnBoardingBinding.inflate(inflater, container, false)

        binding.viewpager.isUserInputEnabled = true

        onboardingPagerAdapter = OnBoardingPagerAdapter(requireContext(), getData(requireContext()))
        binding.viewpager.adapter = onboardingPagerAdapter

        TabLayoutMediator(binding.tablayout, binding.viewpager) { tab, position ->
            tab.text = null
        }.attach()

        binding.viewpager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if (position == onboardingPagerAdapter.itemCount - 1) {
                    binding.btnNext.text = getString(R.string.lets_go)
                } else {
                    binding.btnNext.text = getString(R.string.next)
                }
            }
        })

        binding.btnNext.setOnClickListener {
            val currentItem = binding.viewpager.currentItem
            if (currentItem < onboardingPagerAdapter.itemCount - 1) {
                binding.viewpager.setCurrentItem(currentItem + 1, true)
            }else if (binding.btnNext.text == requireContext().getString(R.string.lets_go)){
                findNavController().navigate(R.id.signInFragment)
            }
        }

        applySpannableText(requireContext(),binding.signIn,requireContext().getString(R.string.already_know_so_sign_in), wordsToStyle1)


        binding.signIn.setOnClickListener {
            findNavController().navigate(R.id.signInFragment)

        }


        return binding.root
    }
}
