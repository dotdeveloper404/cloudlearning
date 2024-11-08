package com.sparkmembership.sparkowner.presentation.components

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.text.format.DateUtils.formatDateTime
import com.sparkmembership.sparkfitness.util.DateUtil.CALENDAR_FORMAT
import com.sparkmembership.sparkfitness.util.DateUtil.CALENDAR_FORMAT2
import com.sparkmembership.sparkfitness.util.DateUtil.formatDateAndTime
import com.sparkmembership.sparkowner.R
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

fun showDatePickerDialog(context: Context,textView: TextView,currentMonth : Boolean = false,selectedDate: Calendar? = null){
    val currentDate = Calendar.getInstance()
    val year: Int
    val month: Int
    val day: Int

    val startOfMonth = currentDate.apply {
        set(Calendar.DAY_OF_MONTH, 1)
    }.timeInMillis

    val endOfMonth = currentDate.apply {
        set(Calendar.DAY_OF_MONTH, currentDate.getActualMaximum(Calendar.DAY_OF_MONTH))
    }.timeInMillis

    if (selectedDate != null) {
        year = selectedDate.get(Calendar.YEAR)
        month = selectedDate.get(Calendar.MONTH)
        day = selectedDate.get(Calendar.DAY_OF_MONTH)
    } else {
        year = currentDate.get(Calendar.YEAR)
        month = currentDate.get(Calendar.MONTH)
        day =   currentDate.get(Calendar.DAY_OF_MONTH)
    }

    val datePickerDialog = DatePickerDialog(
        context, { _, year, month, day ->
            textView.text =  formatDate(year, month, day)
        },
        year,
        month,
        day
    )
    if (currentMonth){
        datePickerDialog.datePicker.minDate = startOfMonth
        datePickerDialog.datePicker.maxDate = endOfMonth
    }
//    else{
//        datePickerDialog.datePicker.maxDate = System.currentTimeMillis()
//    }

    datePickerDialog.show()
    datePickerDialog.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(context.getColor(R.color.colorAccent))
    datePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(context.getColor(R.color.colorAccent))
}
fun showDateTimePicker(context: Context,textView: TextView,selectedDateTime: Calendar? = null) {
    val calendar = selectedDateTime ?: Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)
    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    val minute = calendar.get(Calendar.MINUTE)

    val datePickerDialog = DatePickerDialog(
        context,
        { _, selectedYear, selectedMonth, selectedDay ->
            val timePickerDialog = TimePickerDialog(
                context,
                { _, selectedHour, selectedMinute ->
                    calendar.set(selectedYear, selectedMonth, selectedDay, selectedHour, selectedMinute)
                    val formattedDateTime = formatDateAndTime(calendar)
                    textView.text = formattedDateTime
                },
                hour, minute, true
            )
            timePickerDialog.show()
        },
        year, month, day
    )
    datePickerDialog.show()
}


fun formatDate(year: Int, month: Int, day: Int): String {
    val dateFormat = CALENDAR_FORMAT2
    val calendar = Calendar.getInstance()
    calendar.set(year, month, day)
    return dateFormat.format(calendar.time)
}

fun stringToCalendar(formater: SimpleDateFormat,dateString: String): Calendar? {
    return try {
        val date = formater.parse(dateString)
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar
    } catch (e: Exception) {
        null // Return null if parsing fails
    }
}

fun getFirstAndLastDayOfMonth(formater: SimpleDateFormat): Pair<String, String> {
    val calendar = Calendar.getInstance()
    calendar.set(Calendar.DAY_OF_MONTH, 1)
    val firstDayOfMonth = calendar.time
    calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
    val lastDayOfMonth = calendar.time

    val formattedFirstDay = formater.format(firstDayOfMonth)
    val formattedLastDay = formater.format(lastDayOfMonth)

    return Pair(formattedFirstDay, formattedLastDay)
}
