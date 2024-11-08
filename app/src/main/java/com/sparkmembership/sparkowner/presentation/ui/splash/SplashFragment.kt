package com.sparkmembership.sparkowner.presentation.ui.splash

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.sparkmembership.sparkowner.R
import com.sparkmembership.sparkowner.databinding.FragmentSplashBinding
import com.sparkmembership.sparkowner.presentation.ui.MainActivity


class SplashFragment : Fragment() {

    lateinit var binding : FragmentSplashBinding
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_splash, container, false)
        binding = FragmentSplashBinding.inflate(inflater, container, false)

        handler.postDelayed({

           (activity as MainActivity).initializeNavigation()
        }, 2000)

        return binding.root

    }

}