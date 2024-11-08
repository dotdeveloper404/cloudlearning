package com.sparkmembership.sparkowner.presentation.listeners

import com.sparkmembership.sparkowner.data.entity.RateUsOption

interface OnOptionSelectedListener {
    fun onOptionSelected(selectedOptions: ArrayList<RateUsOption>)
}
