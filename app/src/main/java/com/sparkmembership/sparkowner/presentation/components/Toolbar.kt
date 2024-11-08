package com.sparkmembership.sparkowner.presentation.components

import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

fun AppCompatActivity.setupToolbar(
    toolbar: Toolbar,
    title: String,
    menuRes: Int? = null,
    onMenuItemClick: ((MenuItem) -> Boolean)? = null
) {
    setSupportActionBar(toolbar)
    supportActionBar?.title = title
    supportActionBar?.setDisplayUseLogoEnabled(true);


    menuRes?.let {
        toolbar.inflateMenu(it)
        toolbar.setOnMenuItemClickListener { item ->
            onMenuItemClick?.invoke(item) ?: false
        }
    }


}


