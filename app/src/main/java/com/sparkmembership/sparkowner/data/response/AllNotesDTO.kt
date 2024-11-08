package com.sparkmembership.sparkowner.data.response

data class AllNotesDTO (
    val result: List<Note>
):BaseResponse()

data class Note(
    val noteID: Int,
    val contactID: Int,
    val note: String,
    val timestamp: String,
    val staffName: String,
    val followUpDate: String,
    val contactFirstName: String,
    val contactLastName: String,
    val assignedStaffID: Int,
    val assignedStaffName: String
)