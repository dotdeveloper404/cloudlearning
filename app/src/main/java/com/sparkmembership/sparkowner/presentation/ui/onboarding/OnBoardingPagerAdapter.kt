package com.sparkmembership.sparkowner.presentation.ui.onboarding

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.sparkmembership.sparkowner.R
import com.sparkmembership.sparkowner.data.entity.OnBoarding
import com.sparkmembership.sparkowner.databinding.ItemOnboardingBinding
import com.sparkmembership.sparkowner.util.applySpannableText
import com.sparkmembership.sparkowner.util.wordsToStyle1

class OnBoardingPagerAdapter(val context: Context, val list : ArrayList<OnBoarding>):
    Adapter<OnBoardingPagerAdapter.PagerViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagerViewHolder {
        return PagerViewHolder(ItemOnboardingBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: PagerViewHolder, position: Int) {

        val onBoardingData = list[position]
        holder.binding.imageView.setImageResource(onBoardingData.mainImage)
        applySpannableText(context,holder.binding.onBoardingText,onBoardingData.desc, wordsToStyle1)
    }


    inner class  PagerViewHolder(itemOnboardingBinding: ItemOnboardingBinding) : ViewHolder(itemOnboardingBinding.root){
        var binding = itemOnboardingBinding
    }


}