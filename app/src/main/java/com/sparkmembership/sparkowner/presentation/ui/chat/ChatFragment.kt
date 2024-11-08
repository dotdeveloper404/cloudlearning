package com.sparkmembership.sparkowner.presentation.ui.chat

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sparkmembership.sparkowner.R
import com.sparkmembership.sparkowner.databinding.FragmentChatBinding
import com.sparkmembership.sparkowner.databinding.FragmentOnBoardingBinding

class ChatFragment : Fragment() {
    lateinit var binding: FragmentChatBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChatBinding.inflate(inflater, container, false)
        return binding.root
    }

}