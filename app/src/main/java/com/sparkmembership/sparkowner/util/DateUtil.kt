package com.sparkmembership.sparkfitness.util

import android.location.Location
import android.os.Build
import android.util.Log
import com.sparkmembership.sparkowner.data.enums.MonthsEnum
import com.sparkmembership.sparkowner.data.response.StaffMemberTimeClockResult
import com.sparkmembership.sparkowner.data.response.TimeSlip
import com.sparkmembership.sparkowner.data.response.TimeSlipUserDetail
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.TimeUnit

object DateUtil {
    val UTC_DATE_FORMAT = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US)
    val UTC_DATE_FORMAT_2 = SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.US)
    val UTC_DATE_FORMAT_3 = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SS", Locale.US)
    val LOCAL_DATE_FORMAT = SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.getDefault())
    val UTC_DATE_CHECK_FORMAT = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    val UTC_DATE_MONTH_DAY = SimpleDateFormat("MM-dd", Locale.US)
    val TimeFormat = SimpleDateFormat("hh:mm aa", Locale.US)
    val LocalTimeFormat = SimpleDateFormat("hh:mm aa", Locale.getDefault())
    val MONTH_DATE_FORMAT = SimpleDateFormat("yyyy-MM", Locale.US)
    val CHAT_DATE_FORMATE = SimpleDateFormat("dd MMMM yyyy", Locale.US)
    val CHAT_DATE_FORMATE2 = SimpleDateFormat("MMMM dd, yyyy", Locale.US)
    val CALENDAR_FORMAT = SimpleDateFormat("dd MMM, yyyy", Locale.getDefault())
    val CALENDAR_FORMAT2 = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault())
    val CHAT_DATE_FORMATE4 = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    val APPOINTMENT_DATE_FORMAT = SimpleDateFormat("yyyy/MM/dd hh:mm a", Locale.US)
    val VACATION_DATE_FORMAT = SimpleDateFormat("yyyy/MM/dd", Locale.US)
    val DATE_TIME_FORMAT = SimpleDateFormat("MMMM dd, yyyy 'at' hh:mm a")

    private const val ONE_MINUTE_IN_MILLIS: Long = 60000 //milliseconds
    const val ONE_DAY_IN_MILLIS = 24 * 60 * 60 * 1000L
    

    /**
     * @param count - future days count from now which we want to load
     * @return list of future dates with specified length
     */
    fun getFutureDates(
        count: Int,
        startCalendar: Calendar = Calendar.getInstance(Locale.getDefault()),
        includeStart: Boolean = true
    ): MutableList<Date> {
        val futureDateList = mutableListOf<Date>()
        if (includeStart)
            futureDateList.add(startCalendar.time)
        for (i in 0 until count) {
            startCalendar.add(Calendar.DATE, 1)
            futureDateList.add(startCalendar.time)
        }
        return futureDateList
    }

    fun minusMinutesToDate(minutes: Int, beforeTime: Date): Date {
        val curTimeInMs = beforeTime.time
        return Date(curTimeInMs - minutes * ONE_MINUTE_IN_MILLIS)
    }

    fun addMinutesToDate(minutes: Int, beforeTime: Date): Date {
        val curTimeInMs = beforeTime.time
        return Date(curTimeInMs + minutes * ONE_MINUTE_IN_MILLIS)
    }


    private fun calculateWeeksPassed(startDate: Date, endDate: Date): Int {

        val timeDifferenceMillis = endDate.time - startDate.time
        val millisecondsInAWeek = 7 * 24 * 60 * 60 * 1000L
        val weeksPassed = (timeDifferenceMillis / millisecondsInAWeek).toInt()

        return weeksPassed
    }

    private fun calculateDaysAgo(targetDate: Date): String {

        val currentDate = Date()
        val diffInMilliseconds = currentDate.time - targetDate.time
        val daysago = TimeUnit.MILLISECONDS.toDays(diffInMilliseconds)

        return if (daysago > 6) calculateWeeksPassed(
            startDate = targetDate,
            endDate = currentDate
        ).toString().plus(" w")
        else if (daysago == 1L) daysago.toString().plus(" day ago") else daysago.toString()
            .plus(" days ago")
    }

    fun calculateTime(targetDate: Date): String {
        val currentDate = Date()
        val limitForSecAndMinute = 60
        val limitForHours = 24

        var postTime = ""

        val diffInMilliseconds = currentDate.time - targetDate.time
        val timeInSec = TimeUnit.MILLISECONDS.toSeconds(diffInMilliseconds)
        val timeInMinutes = TimeUnit.MILLISECONDS.toMinutes(diffInMilliseconds)
        val timeInHours = TimeUnit.MILLISECONDS.toHours(diffInMilliseconds)


        postTime = if (timeInSec > limitForSecAndMinute) {
            if (timeInMinutes > limitForSecAndMinute) {
                if (timeInHours > limitForHours) {
                    calculateDaysAgo(targetDate)
                } else {
                    timeInHours.toString().plus(" hr ago")
                }
            } else {
                timeInMinutes.toString().plus(" min ago")
            }
        } else {
            timeInSec.toString().plus(" sec ago")
        }

        return postTime
    }


    fun calculateDuration(startTimeStr: String, endTimeStr: String): String {
        // Convert the input strings to Date objects
        val startTime = convertStringToDate(startTimeStr, UTC_DATE_FORMAT)
        val endTime = convertStringToDate(endTimeStr, UTC_DATE_FORMAT)

        if (startTime != null && endTime != null) {
            // Calculate the difference in milliseconds
            val durationInMillis = endTime.time - startTime.time

            // Convert the duration to hours and minutes
            val hours = TimeUnit.MILLISECONDS.toHours(durationInMillis)
            val minutes = TimeUnit.MILLISECONDS.toMinutes(durationInMillis) % 60

            // Return the duration in the format "Xhr Ym"
            return "${hours}hr ${minutes}m"
        }

        // Return null if parsing fails
        return ""
    }




    fun convertStringToDate(
        strDate: String,
        format: SimpleDateFormat,
        isUTC: Boolean = true
    ): Date? {
        var date: Date? = null
        if (isUTC)
            format.timeZone = TimeZone.getTimeZone("UTC")
        try {
            date = format.parse(strDate)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return date
    }

    fun convertDateToString(
        dateObj: Date,
        format: SimpleDateFormat,
        isUTC: Boolean = true
    ): String {
        var dateStr = ""
        if (isUTC)
            format.timeZone = TimeZone.getTimeZone("UTC")
        try {
            dateStr = format.format(dateObj)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return dateStr
    }

    fun getMonthNameByValue(monthIndex: Int): String {
        return when (monthIndex) {
            MonthsEnum.JANUARY.monthNumber -> MonthsEnum.JANUARY.name
            MonthsEnum.FEBRUARY.monthNumber -> MonthsEnum.FEBRUARY.name
            MonthsEnum.MARCH.monthNumber -> MonthsEnum.MARCH.name
            MonthsEnum.APRIL.monthNumber -> MonthsEnum.APRIL.name
            MonthsEnum.MAY.monthNumber -> MonthsEnum.MAY.name
            MonthsEnum.JUNE.monthNumber -> MonthsEnum.JUNE.name
            MonthsEnum.JULY.monthNumber -> MonthsEnum.JULY.name
            MonthsEnum.AUGUST.monthNumber -> MonthsEnum.AUGUST.name
            MonthsEnum.SEPTEMBER.monthNumber -> MonthsEnum.SEPTEMBER.name
            MonthsEnum.OCTOBER.monthNumber -> MonthsEnum.OCTOBER.name
            MonthsEnum.NOVEMBER.monthNumber -> MonthsEnum.NOVEMBER.name
            MonthsEnum.DECEMBER.monthNumber -> MonthsEnum.DECEMBER.name
            else -> {
                MonthsEnum.JANUARY.name
            }
        }
    }

    fun formatUtcDateString(inputDateStr: String, outputFormat: SimpleDateFormat,inputFormat: SimpleDateFormat = UTC_DATE_FORMAT): String {
        return try {
            inputFormat.timeZone = TimeZone.getTimeZone("UTC")

            val parsedDate = inputFormat.parse(inputDateStr)

            outputFormat.timeZone = TimeZone.getDefault()

            outputFormat.format(parsedDate as Date)
        } catch (e: ParseException) {
            e.printStackTrace()
            ""
        }
    }

    fun getCurrentDate(): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // API level 26 or higher
            val currentDate = LocalDate.now()
            val dateFormatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy")
            currentDate.format(dateFormatter)
        } else {
            // Lower API levels
            val calendar = Calendar.getInstance()
            val dateFormat = CHAT_DATE_FORMATE2
            dateFormat.format(calendar.time)
        }
    }

    fun getTodayDate() : String {
        val date = Date()
        val formatter = SimpleDateFormat("yyyy-MM-dd")
        val formattedDate = formatter.format(date)
        return formattedDate
    }

    fun calculateYearDifference(startDate: String, endDate: String): Int {
        val dateFormat = CHAT_DATE_FORMATE2

        val start = dateFormat.parse(startDate) ?: return 0
        val end = dateFormat.parse(endDate) ?: return 0

        val startCalendar = Calendar.getInstance().apply { time = start }
        val endCalendar = Calendar.getInstance().apply { time = end }

        // Calculate year difference
        var yearDifference = endCalendar.get(Calendar.YEAR) - startCalendar.get(Calendar.YEAR)

        // Adjust for cases where the end date is before the start date's birthday in the current year
        if (endCalendar.get(Calendar.DAY_OF_YEAR) < startCalendar.get(Calendar.DAY_OF_YEAR)) {
            yearDifference--
        }

        return yearDifference
    }



    fun getYesterdayDate(): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Use java.time APIs if the API level is 26 or higher
            val formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.getDefault())
            val yesterday = LocalDate.now().minusDays(1)
            yesterday.format(formatter)
        } else {
            // Use SimpleDateFormat for API levels below 26
            val dateFormat = CHAT_DATE_FORMATE
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DAY_OF_YEAR, -1)
            dateFormat.format(calendar.time)
        }

    }

    fun splitDateAndTime(dateTimeStr: String): Pair<String, String> {
        // Split the input string into two parts based on the first space
        val parts = dateTimeStr.split(" ", limit = 2)

        // Return the parts as a Pair (date and time)
        return Pair(parts[0], parts[1])
    }


    fun ConvertCalenderDatetoUtcFormat(inputDate: String): String {
        return try {
            val date: Date

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val formatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy", Locale.getDefault())
                val localDate = LocalDate.parse(inputDate, formatter)
                date = Date.from(localDate.atStartOfDay(ZoneId.of("UTC")).toInstant())
            } else {
                val formatter = SimpleDateFormat("dd MMM, yyyy", Locale.getDefault())
                date = formatter.parse(inputDate) ?: throw IllegalArgumentException("Invalid date format: $inputDate")
            }

            val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
            outputFormat.timeZone = TimeZone.getTimeZone("UTC")
            outputFormat.format(date)
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }

    fun formatTime(time : String): String{
        val utcFormat = UTC_DATE_FORMAT
        utcFormat.timeZone = TimeZone.getTimeZone("UTC")

        val localFormat = LocalTimeFormat
        localFormat.timeZone = TimeZone.getDefault()
        val formattedTime : Date? = if (time != null && time.isNotEmpty()) {
            utcFormat.parse(time)
        } else {
            null
        }

        val formattedDate = formattedTime?.let { localFormat.format(it) } ?: ""

        return formattedDate
    }

    fun formatTimeInOut(timeIn: String?, timeOut: String?): String {
        val utcFormat = UTC_DATE_FORMAT
        utcFormat.timeZone = TimeZone.getTimeZone("UTC")

        val localFormat = LocalTimeFormat
        localFormat.timeZone = TimeZone.getDefault()

        val inDate: Date? = if (timeIn != null && timeIn.isNotEmpty()) {
            utcFormat.parse(timeIn)
        } else {
            null
        }

        val outDate: Date? = if (timeOut != null && timeOut.isNotEmpty()) {
            utcFormat.parse(timeOut)
        } else {
            null
        }

        val formattedIn = inDate?.let { localFormat.format(it) } ?: ""
        val formattedOut = outDate?.let { localFormat.format(it) } ?: ""
        return "$formattedIn - $formattedOut"
    }
    fun FormatTimeToDate(dateString: String): String {
        val date: Date = UTC_DATE_FORMAT.parse(dateString) ?: return ""
        return CHAT_DATE_FORMATE2.format(date)
    }

    fun calculateWorkHours(timeIn: String, timeOut: String): String {
        val format = UTC_DATE_FORMAT_2
        format.timeZone = TimeZone.getTimeZone("UTC")


        val startTime: Date = format.parse(timeIn.substring(0, 16)) ?: Date()
        val endTime: Date = format.parse(timeOut.substring(0, 16)) ?: Date()

        val durationMillis = maxOf(0, endTime.time - startTime.time)
        val totalHours = durationMillis / (1000 * 60 * 60)

        val remainingMinutes = ((durationMillis / (1000 * 60)) % 60) * 100 / 60.0
        val remainingMinutesRounded = kotlin.math.round(remainingMinutes)/ 100
        val remainingMinutesPercentage =  String.format("%.2f", remainingMinutesRounded - remainingMinutesRounded.toInt()).substring(1)
        return "$totalHours$remainingMinutesPercentage"
    }

    fun calculateWorkHour(timeSlips: List<StaffMemberTimeClockResult>) : String{
        var totalWorkHours = 0.0

        for (timeSlip in timeSlips) {
            if (timeSlip.timeOut != "" && timeSlip.timeOut != null) {
                val workHoursString  = calculateWorkHours(timeSlip.timeIn, timeSlip.timeOut)
                var workHours = workHoursString.toDouble()
                totalWorkHours += workHours
            }
        }
        return String.format("%.2f h", totalWorkHours)
    }

    fun calculateWorkHourForToday(timeSlips: List<TimeSlip>) : String{
        var totalWorkHours = 0.0
        val today = CHAT_DATE_FORMATE4.format(Date())

        val dateFormat = UTC_DATE_FORMAT

        val todayDate =  timeSlips.filter { entry ->
            val timeInDate: Date = dateFormat.parse(entry.timeIn)
            val timeInDateString = CHAT_DATE_FORMATE4.format(timeInDate)
            timeInDateString == today
        }

        for (timeSlip in todayDate) {
            if (timeSlip.timeOut != "" && timeSlip.timeOut != null) {
                val workHoursString  = calculateWorkHours(timeSlip.timeIn, timeSlip.timeOut)
                var workHours = workHoursString.toDouble()
                totalWorkHours += workHours
            }
        }
        return String.format("%.2f Hour(s)", totalWorkHours)
    }

    fun calculateWorkHourForMonth(timeSlips: List<TimeSlip>):String{
        var totalWorkHours = 0.0
        val currentMonthYear = MONTH_DATE_FORMAT.format(Date())
        val dateFormat = UTC_DATE_FORMAT

        val currentMonthTimeSlips = timeSlips.filter { entry ->
            val timeInDate: Date = dateFormat.parse(entry.timeIn)
            val timeInDateString = MONTH_DATE_FORMAT.format(timeInDate)
            timeInDateString == currentMonthYear
        }

        for (timeSlip in currentMonthTimeSlips) {
            if (timeSlip.timeOut != "" && timeSlip.timeOut != null) {
                val workHours  = calculateWorkHours(timeSlip.timeIn, timeSlip.timeOut).toDouble()
                totalWorkHours += workHours
            }
        }
        return String.format("%.2f Hour(s)", totalWorkHours)
    }

    fun processTimeSlipsByMonth(timeSlips: List<TimeSlip>): List<TimeSlipUserDetail> {
        val formatter = UTC_DATE_FORMAT

        val groupedByMonth = timeSlips.groupBy {
            val timeIn = formatter.parse(it.timeIn)
            val calendar = Calendar.getInstance()
            calendar.time = timeIn
            calendar.get(Calendar.YEAR) to calendar.get(Calendar.MONTH) + 1 // Pair<Year, Month>
        }

        val monthlyDetails = mutableListOf<TimeSlipUserDetail>()

        groupedByMonth.forEach { (yearMonth, slips) ->
            var totalWorkHours = 0.0
            val validTimeSlips = mutableListOf<TimeSlip>()

            slips.forEach { slip ->
                if (!slip.timeOut.isNullOrEmpty()) {
                    val workHour = calculateWorkHours(slip.timeIn, slip.timeOut).toDouble()
                    totalWorkHours += workHour
                }
                validTimeSlips.add(slip)
            }

            val currentMonthIndex  = Calendar.getInstance().apply {
                time = validTimeSlips.firstOrNull()?.let { formatter.parse(it.timeIn) } ?: formatter.parse(slips.first().timeIn)!!
            }.get(Calendar.MONTH)
            val monthNames = listOf("January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December")

            val currentMonth = currentMonthIndex + 1
            val currentYear = yearMonth.first
            val lastDayOfMonth = Calendar.getInstance().apply {
                set(Calendar.YEAR, currentYear)
                set(Calendar.MONTH, currentMonth)
                set(Calendar.DAY_OF_MONTH, 1)
                add(Calendar.MONTH, 1)
                set(Calendar.DAY_OF_MONTH, 0) // Get the last day of the month
            }.get(Calendar.DAY_OF_MONTH)
            val monthName = monthNames[currentMonthIndex]
            val currentPayPeriod = "$monthName 01-$lastDayOfMonth, $currentYear"
            val totalTime = String.format("%.2f Hour(s)", totalWorkHours)

            monthlyDetails.add(
                TimeSlipUserDetail(
                    currentPayPeriod = currentPayPeriod,
                    totalTime = totalTime,
                    timeSlip = validTimeSlips
                )
            )
        }

        return monthlyDetails
    }


    fun calculateCurrentWorkingTime(timeIn: String) : Long {
        val utcFormat = UTC_DATE_FORMAT
        utcFormat.timeZone = TimeZone.getTimeZone("UTC")

        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        dateFormat.timeZone = TimeZone.getDefault()

        val timeInFormat: Date = utcFormat.parse(timeIn)!!
        val formattedIn = timeInFormat.let { dateFormat.format(it) } ?: ""
        val dateIn = dateFormat.parse(formattedIn)

        val differenceInMillis = Date().time - dateIn.time
        val differenceInSeconds = differenceInMillis / 1000
        return differenceInSeconds
    }

    fun calculateDistance(startPoint: Location?, endPoint: Location?): Double {
        return if (startPoint != null && endPoint != null) {
            startPoint.distanceTo(endPoint).toDouble()
        } else {
            -1.0
        }
    }

    fun getCurrentTime(): String {
        val currentDate = Date()
        return LocalTimeFormat.format(currentDate)
    }

    fun formatIsoToLocalTimeLegacy(isoDateString: String): String? {
        val isoFormatter = UTC_DATE_FORMAT_3
        isoFormatter.timeZone = TimeZone.getTimeZone("UTC")
        return try {
            val date = isoFormatter.parse(isoDateString)
            val outputFormatter = DATE_TIME_FORMAT
            outputFormatter.timeZone = TimeZone.getDefault()
            outputFormatter.format(date)
        } catch (e: Exception) {
            println("Error parsing or formatting date: ${e.message}")
            null
        }
    }

    fun formatDateAndTime(calendar: Calendar): String {
        return DATE_TIME_FORMAT.format(calendar.time)
    }

    fun formatStringToDateAndTime(date: String): String {
        val formatUTC = UTC_DATE_FORMAT_2.parse(date)
        DATE_TIME_FORMAT.timeZone = TimeZone.getDefault()
        val formattedDate = DATE_TIME_FORMAT.format(formatUTC)


        return formattedDate
    }
}