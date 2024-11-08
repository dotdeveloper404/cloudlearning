package com.sparkmembership.sparkfitness.util

import android.app.DownloadManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import com.sparkmembership.sparkowner.R
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream


fun imageConvertToBitmap(context: Context, imageUris: Uri): Bitmap {

    return when {
        Build.VERSION.SDK_INT < 28 -> MediaStore.Images.Media.getBitmap(
            context.contentResolver,
            imageUris
        )
        else -> {
            val source = ImageDecoder.createSource(context.contentResolver, imageUris)
            ImageDecoder.decodeBitmap(source)
        }
    }
}

fun bitmapToBase64(bitmap: Bitmap): String {
    val byteArrayOutputStream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
    val byteArray = byteArrayOutputStream.toByteArray()
    return Base64.encodeToString(byteArray, Base64.DEFAULT)
}



fun downloadImage(filename: String, downloadUrlOfImage: String, context: Context) {
    try {
        val dm: DownloadManager? =
            context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager?
        val downloadUri = Uri.parse(downloadUrlOfImage)
        val request: DownloadManager.Request = DownloadManager.Request(downloadUri)
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
            .setAllowedOverRoaming(false)
            .setTitle(filename)
            .setMimeType("image/jpeg") // Your file type. You can use this code to download other file types also.
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationInExternalPublicDir(
                Environment.DIRECTORY_PICTURES,
                File.separator + filename + ".jpg"
            )
        dm?.enqueue(request)
        Toast.makeText(context,
            context.getString(R.string.image_download_started), Toast.LENGTH_SHORT).show()
    } catch (e: Exception) {
        Log.e("DownloadImage", "downloadImage: ${e.message}", )
        Toast.makeText(context,
            context.getString(R.string.image_download_failed), Toast.LENGTH_SHORT).show()
    }


}

fun getFileNameFromPath(filePath: String): String {
    val file = File(filePath)
    return file.name
}


fun saveImageFromContentUri(
    filename: String,
    contentUri: Uri,
    context: Context
) {
    try {
        // Open an input stream from the content URI
        val inputStream = context.contentResolver.openInputStream(contentUri)

        // Specify the directory and file name where the image will be saved
        val imageFile = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
            "$filename.jpg"
        )

        // Create the output stream to write the file
        val outputStream = FileOutputStream(imageFile)

        // Buffer for transferring data from input stream to output stream
        val buffer = ByteArray(1024)
        var length: Int

        // Transfer the data from the content URI to the file
        while (inputStream?.read(buffer).also { length = it ?: -1 } != -1) {
            outputStream.write(buffer, 0, length)
        }

        // Close the streams
        inputStream?.close()
        outputStream.close()

        // Notify the system about the new file, so it appears in the gallery
        MediaScannerConnection.scanFile(
            context,
            arrayOf(imageFile.absolutePath),
            arrayOf("image/jpeg"),
            null
        )

        // Notify the user of success
        Toast.makeText(context, context.getString(R.string.image_download_started), Toast.LENGTH_SHORT).show()
    } catch (e: Exception) {
        Log.e("SaveImage", "saveImageFromContentUri: ${e.message} ")
        Toast.makeText(context, context.getString(R.string.image_download_failed), Toast.LENGTH_SHORT).show()
    }
}


fun loadImage(imageUrl: String, target: ImageView){
    Picasso.get().load(imageUrl).placeholder(R.drawable.ic_launcher_foreground).into(target)
}





