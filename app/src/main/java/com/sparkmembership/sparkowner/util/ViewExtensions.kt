package com.sparkmembership.sparkfitness.util

import android.app.Activity
import android.app.Service
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import com.sparkmembership.sparkfitness.util.DateUtil.UTC_DATE_FORMAT
import com.sparkmembership.sparkfitness.util.DateUtil.calculateTime
import java.io.File
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone

fun String.showToast(context: Context, length: Int? = 0) {
    Toast.makeText(context, this, length ?: Toast.LENGTH_SHORT).show()
}

infix fun SimpleDateFormat.toChangeTimeFormat(sDate: String): String {

    try {
        UTC_DATE_FORMAT.timeZone = TimeZone.getTimeZone("UTC")
        val converted = UTC_DATE_FORMAT.parse(sDate)
        return this.format(converted as Date)
    } catch (e: ParseException) {
        e.printStackTrace()
        return ""
    }
}


fun String.calculateForumPostTime(): String {
    try {
        UTC_DATE_FORMAT.timeZone = TimeZone.getTimeZone("UTC")
        val converted = UTC_DATE_FORMAT.parse(this)
        return calculateTime(converted as Date)
    } catch (e: ParseException) {
        e.printStackTrace()
        return ""
    }
}



fun Context.createImageFile(): File {
    // Create an image file name
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
    val imageFileName = "JPEG_" + timeStamp + "_"
    val image = File.createTempFile(
        imageFileName, /* prefix */
        ".jpg", /* suffix */
        externalCacheDir      /* directory */
    )
    return image
}


fun Activity.showToast(message: String, context: Context? = applicationContext, length: Int? = 0) {
    Toast.makeText(context, message, length ?: Toast.LENGTH_SHORT).show()
}

fun showToast(context: Context,message: String) {

    Toast.makeText(context, message,Toast.LENGTH_SHORT).show()
}


fun ViewModel.showToast(message: String, context: Context, length: Int? = 0) {
    Toast.makeText(context, message, length ?: Toast.LENGTH_SHORT).show()
}

fun Fragment.showToast(message: String, context: Context, length: Int? = 0) {
    Toast.makeText(context, message, length ?: Toast.LENGTH_SHORT).show()
}



fun View.showKeyboard() {
    (this.context.getSystemService(Service.INPUT_METHOD_SERVICE) as? InputMethodManager)
        ?.showSoftInput(this, 0)
}


fun Activity.hideKeyboard() {
    hideKeyboard(currentFocus ?: View(this))
}


fun Context.hideKeyboard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}

fun View.hideKeyboard() {
    (this.context.getSystemService(Service.INPUT_METHOD_SERVICE) as? InputMethodManager)
        ?.hideSoftInputFromWindow(this.windowToken, 0)
}

fun View.toVisible() {
    this.visibility = View.VISIBLE
}

fun View.toGone() {
    this.visibility = View.GONE
}

fun View.toInvisible() {
    this.visibility = View.GONE
}


