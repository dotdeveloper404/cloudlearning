package com.sparkmembership.sparkowner.presentation.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.sparkmembership.sparkowner.R
import com.sparkmembership.sparkowner.databinding.FragmentHomeBinding
import com.sparkmembership.sparkowner.presentation.ui.MainActivity

class HomeFragment : Fragment() {
    lateinit var binding: FragmentHomeBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        setToolBar()
        return binding.root
    }

    private fun setToolBar() {
        (activity as MainActivity).showToolbar()

        (activity as? MainActivity)?.initializeCustomToolbar(
            title = getString(R.string.home),
            toolbarColor = android.R.color.transparent,
            showBackButton = false,
            icons = listOf(
                R.drawable.icon_clock_home to {

                },
                R.drawable.icon_user to {

                },
                R.drawable.icon_notification to {

                }

            ),
            onBackPress = {
            }
        )

    }
}