package com.sparkmembership.sparkowner.util

import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.navigation.NavOptions
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.sparkmembership.sparkowner.R
import com.sparkmembership.sparkowner.adapter.SpinnerDialogAdapter
import com.sparkmembership.sparkowner.data.entity.OnBoarding
import com.sparkmembership.sparkowner.presentation.components.CustomButton
import com.sparkmembership.sparkowner.presentation.components.GenericDialogAdapter
import com.sparkmembership.sparkowner.presentation.ui.MainActivity
import java.io.File

fun getData(context: Context): ArrayList<OnBoarding> {
    return arrayListOf(
        OnBoarding(
            R.drawable.onboarding_1,
            context.getString(R.string.onboarding_text_1)
        ),
        OnBoarding(
            R.drawable.onboarding_2,
            context.getString(R.string.onboarding_text_2)
        ),
        OnBoarding(
            R.drawable.onboarding_3,
            context.getString(R.string.onboarding_text_3)
        ),
    )
}

fun applySpannableText(
    context: Context,
    textView: TextView,
    fullText: String,
    wordsToStyle: Map<String, Pair<Int, Float?>>
) {
    val spannableString = SpannableString(fullText)

    for ((word, style) in wordsToStyle) {
        val (colorId, textSize) = style
        val color = ContextCompat.getColor(context, colorId)
        var startIndex = fullText.indexOf(word)
        while (startIndex >= 0) {
            val endIndex = startIndex + word.length
            spannableString.setSpan(
                ForegroundColorSpan(color),
                startIndex,
                endIndex,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            if (textSize != null) {
                spannableString.setSpan(
                    RelativeSizeSpan(textSize),
                    startIndex,
                    endIndex,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
            // Find next occurrence of the word
            startIndex = fullText.indexOf(word, endIndex)
        }
    }

    textView.text = spannableString
}

val wordsToStyle1 = mapOf(
    "Schedule" to Pair(R.color.color_onboarding, null),
    "Task" to Pair(R.color.color_onboarding, null),
    "New" to Pair(R.color.color_onboarding, null),
    "Feature" to Pair(R.color.color_onboarding, null),
    "on" to Pair(R.color.color_onboarding, null),
    "Dashboard" to Pair(R.color.color_onboarding, null),
    "Event," to Pair(R.color.color_onboarding, null),
    "Appointment," to Pair(R.color.color_onboarding, null),
    "Report," to Pair(R.color.color_onboarding, null),
    "Invoices," to Pair(R.color.color_onboarding, null),
    "Attendance" to Pair(R.color.color_onboarding, null),
    "logs" to Pair(R.color.color_onboarding, null),
    "Sign" to Pair(R.color.color_onboarding, null),
    "In" to Pair(R.color.color_onboarding, null),
    "h" to Pair(R.color.colorAppTextSecondary, 0.5f)
)

val emailPattern = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}$"
val emailMatcher = Regex(emailPattern)

fun isValidEmail(email: String): Boolean {
    return emailMatcher.matches(email)
}

fun <T> showDialogWithMultiSelectionRecyclerView(
    context: Context,
    title: String,
    data: ArrayList<T>,
    displayText: (T) -> String,
    selectedData: ArrayList<T>,
    onItemSelect: (List<T>) -> Unit,
    onSubmitClick: (List<T>) -> Unit
) {
    val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_recyclerview, null)
    val recyclerView: RecyclerView = dialogView.findViewById(R.id.recyclerView)
    val cancelBtn: ImageView = dialogView.findViewById(R.id.cancel_icon)
    val btnSubmit: CustomButton = dialogView.findViewById(R.id.btnSubmit)
    val dialogTitle: TextView = dialogView.findViewById(R.id.dialog_title)

    var selectedValues = ArrayList<T>()
    dialogTitle.setText(title)

    recyclerView.layoutManager = LinearLayoutManager(context)

    val adapter = GenericDialogAdapter(
        data,
        displayText,
        selectedData, // Pass the selected items to the adapter
        object : GenericDialogAdapter.OnItemClickListener<T> {
            override fun onItemSelected(selectedItems: List<T>) {
                onItemSelect(selectedItems)
                selectedValues = ArrayList(selectedItems) // Convert to ArrayList
            }
        })



    recyclerView.adapter = adapter

    // Create the dialog
    val dialog = MaterialAlertDialogBuilder(context)
        .setView(dialogView)
        .setCancelable(true)
        .create()
    dialog.show()

    cancelBtn.setOnClickListener {
        dialog.dismiss()
    }

    btnSubmit.setOnClickListener {
        onSubmitClick(selectedValues)
        dialog.dismiss()
    }
}

fun <T> showDialogWithRecyclerView(
    context: Context,
    data: List<T>,
    displayText: (T) -> String,
    selectedItem: (T)?,  // New: To pass the previously selected item
    onItemSelect: (T) -> Unit,
    onSubmitClick: (T?) -> Unit
) {
    val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_recyclerview, null)
    val recyclerView: RecyclerView = dialogView.findViewById(R.id.recyclerView)
    val cancelBtn: ImageView = dialogView.findViewById(R.id.cancel_icon)
    val btnSubmit: CustomButton = dialogView.findViewById(R.id.btnSubmit)

    recyclerView.layoutManager = LinearLayoutManager(context)

    var selectedValue: T? = selectedItem

    val adapter = SpinnerDialogAdapter(
        data,
        displayText,
        selectedItem,
        onItemClickListener = { item ->
            selectedValue = item
            onItemSelect(item)
        }
    )

    recyclerView.adapter = adapter

    val dialog = MaterialAlertDialogBuilder(context)
        .setView(dialogView)
        .show()

    cancelBtn.setOnClickListener {
        dialog.dismiss()
    }

    btnSubmit.setOnClickListener {
        onSubmitClick(selectedValue)  // Pass the selected value
        dialog.dismiss()
    }
}


fun noInternetDialogBox(context: Context) {

    val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_no_internet, null)

    val builder = AlertDialog.Builder(context)
    builder.setView(dialogView)

    val dialog = builder.create()
    dialog.show()

    val btnLogout = dialogView.findViewById<CustomButton>(R.id.btnGoToSettings)
    val btnCancel = dialogView.findViewById<CustomButton>(R.id.btnClose)

    btnLogout.setOnClickListener {
        val intent = Intent(Settings.ACTION_WIFI_SETTINGS)
        context.startActivity(intent)
        dialog.dismiss()
    }

    btnCancel.setOnClickListener {
        dialog.dismiss()
    }

}

fun showDialog(
    context: Context,
    dialogText: String,
    btnDialogText: String,
    dialogImageId: Int,
    buttonAction: () -> Unit,
) {
    val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_box_forgot_password, null)
    val dialogMessage = dialogView.findViewById<TextView>(R.id.dialog_message)
    val button = dialogView.findViewById<CustomButton>(R.id.btnDialogButton)
    val dialogImage = dialogView.findViewById<ImageView>(R.id.dialog_image)

    button.text = btnDialogText
    dialogMessage.text = dialogText
    dialogImage.setImageResource(dialogImageId)
    val builder = AlertDialog.Builder(context)
    builder.setView(dialogView)

    val dialog = builder.create()
    dialog.show()

    button.setOnClickListener {
        buttonAction()
        dialog.dismiss()
    }
}

fun deleteCache(context: Context) {
    try {
        val dir = context.cacheDir
        deleteDir(dir)
    } catch (e: java.lang.Exception) {
        e.printStackTrace()
    }
}

private fun deleteDir(dir: File?): Boolean {
    return if (dir != null && dir.isDirectory) {
        val children = dir.list()
        if (children != null) {
            for (i in children.indices) {
                val success = deleteDir(File(dir, children[i]))
                if (!success) {
                    return false
                }
            }
        }
        dir.delete()
    } else if (dir != null && dir.isFile) {
        dir.delete()
    } else {
        false
    }
}

fun navigationAnimation(enterAnimation: Int, exitAnimation: Int): NavOptions {
    val navOptions = NavOptions.Builder()
        .setEnterAnim(enterAnimation)
        .setExitAnim(exitAnimation)
        .build()

    return navOptions
}

fun showToolBar(activity: MainActivity) {
    activity.showToolbar()
}

fun hideToolBar(activity: MainActivity) {
    activity.hideToolbar()
}


