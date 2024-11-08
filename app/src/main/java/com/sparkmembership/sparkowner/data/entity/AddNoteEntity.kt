package com.sparkmembership.sparkowner.data.entity

data class AddNoteEntity(
    val note: String,
    val contactID: Long,
    var assignedUserID: Int?= null,
    val followUpDate: String?= null
)

