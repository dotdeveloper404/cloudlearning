package com.sparkmembership.sparkowner.util

import android.widget.ImageView
import com.sparkmembership.sparkowner.R
import com.squareup.picasso.Picasso

object PicassoUtil {

    fun loadImage(imageUrl: String?, target: ImageView) {
        if (imageUrl.isNullOrEmpty()) {
            Picasso.get()
                .load(R.drawable.image_placeholder)
                .into(target)
        } else {
            Picasso.get()
                .load(imageUrl)
                .placeholder(R.drawable.image_placeholder)
                .into(target)
        }
    }
}
