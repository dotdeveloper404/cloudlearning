package com.sparkmembership.sparkowner.util

import android.app.ActionBar.LayoutParams
import android.content.Context
import android.net.Uri
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.signature.ObjectKey
import com.sparkmembership.sparkowner.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

object GlideUtil {

    fun loadCircleImageURI(
        context: Context,
        path: Uri,
        img: ImageView
    ) {
        Glide.with(context)
            .load(path).diskCacheStrategy(DiskCacheStrategy.RESOURCE)
            .apply(RequestOptions.bitmapTransform(CircleCrop()))
            .into(img)
    }

    fun loadCircleImage(
        context: Context,
        url: String,
        lastUpdate: String,
        img: ImageView
    ) {
        Glide.with(context)
            .load(url).diskCacheStrategy(DiskCacheStrategy.RESOURCE)
            .placeholder(ContextCompat.getDrawable(context, R.drawable.image_placeholder))
            .apply(RequestOptions().override(100, 100))
            .signature(ObjectKey(lastUpdate))
            .into(img)
    }

    fun loadImage(
        context: Context,
        url: String,
        img: ImageView,
        defaultImage: Int
    ) {
        Glide.with(context)
            .load(url)
            .skipMemoryCache(true)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .placeholder(ContextCompat.getDrawable(context, defaultImage))
            .apply(RequestOptions().override(LayoutParams.WRAP_CONTENT, 400))
            .into(img)
    }


    fun loadImageWithCorner(
        context: Context,
        url: String,
        img: ImageView,

        ) {
        Glide.with(context)
            .load(url)
            .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
            // .placeholder(ContextCompat.getDrawable(context,R.drawable.placeholder))
            .transform(CenterCrop(), RoundedCorners(16)).into(img)

    }

    fun clearCache(context: Context) {
        GlobalScope.launch(Dispatchers.IO) {
            Glide.get(context).clearDiskCache()

        }
        GlobalScope.launch(Dispatchers.Main) {
            Glide.get(context).clearMemory()
        }
    }

}