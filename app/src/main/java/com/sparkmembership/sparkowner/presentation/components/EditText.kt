package com.sparkmembership.sparkowner.presentation.components

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.view.ContextThemeWrapper
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.res.ResourcesCompat
import com.google.android.material.theme.overlay.MaterialThemeOverlay.wrap
import com.sparkmembership.sparkowner.R


class EditText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) :      AppCompatEditText( ContextThemeWrapper(context, R.style.EditText_Style), attrs,defStyleAttr) {

    init {
        init()
    }

    private fun init() {
    }
}

