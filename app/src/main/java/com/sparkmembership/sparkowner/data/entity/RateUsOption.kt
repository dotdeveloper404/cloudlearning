package com.sparkmembership.sparkowner.data.entity

data class RateUsOption(
    val name: String= "",
    var isSelected: Boolean = false
){
    fun getAllOptions() : ArrayList<RateUsOption> = arrayListOf(
        RateUsOption("Slow loading"),
        RateUsOption("Customer service"),
        RateUsOption("App crash"),
        RateUsOption("Not functional"),
        RateUsOption("Not responding"),
        RateUsOption("Navigation")
    )
}

