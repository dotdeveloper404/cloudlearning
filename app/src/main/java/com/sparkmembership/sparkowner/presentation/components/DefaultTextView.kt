package com.sparkmembership.sparkowner.presentation.components

import android.content.Context
import android.util.AttributeSet
import android.view.inputmethod.EditorInfo
import android.widget.LinearLayout
import com.sparkmembership.sparkowner.R


class DefaultTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    val textView: TextView
    private val labelText: TextView
    private val errorText: TextView

    init {
        inflate(context, R.layout.layout_default_text_view, this)
        textView = findViewById(R.id.input)
        labelText = findViewById(R.id.txtLabel)
        errorText = findViewById(R.id.txtError)
        attrs?.let { setAttributes(context, it) }
    }

    private fun setAttributes(context: Context, attrs: AttributeSet) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.DefaultInput)
        val errorText = typedArray.getString(R.styleable.DefaultInput_errorText)
        val labelText = typedArray.getString(R.styleable.DefaultInput_labelText)
        val hintText = typedArray.getString(R.styleable.DefaultInput_hintText)
        val inputType =
            typedArray.getInt(R.styleable.DefaultInput_android_inputType, EditorInfo.TYPE_NULL)

        typedArray.recycle()

        if (hintText != null) {
            this.textView.hint = hintText
        }

        if (labelText != null) {
            this.labelText.text = labelText
        }

        if (errorText != null) {
            this.errorText.text = errorText
        }

        if (inputType != EditorInfo.TYPE_NULL) {
            textView.inputType = inputType
        }

    }

    fun showError(){
        errorText.visibility = VISIBLE
    }

    fun resetError(){
        errorText.visibility = GONE
    }

    fun getInputText(): String {
        return textView.text.toString()
    }

    fun rightDrawable(drawable : Int){
        textView.setCompoundDrawablesWithIntrinsicBounds(0,0,drawable,0)
    }

}