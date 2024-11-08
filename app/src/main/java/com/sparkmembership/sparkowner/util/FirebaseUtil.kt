package com.sparkmembership.sparkowner.util

import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging

fun subscribeToFirebase(topic: String) {
    FirebaseMessaging.getInstance().subscribeToTopic(topic)
}

fun unSubscribeToFirebase(topic: String) {
    FirebaseMessaging.getInstance().unsubscribeFromTopic(topic)
        .addOnCompleteListener {}
}

fun getFirebaseToken(
    token: (String) -> Unit,
) {
    FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
        if (!task.isSuccessful) {
            return@addOnCompleteListener
        }
        val token = task.result ?: ""
        Log.e("<----FCM TOKEN---->", token)
        token(token)
    }
}