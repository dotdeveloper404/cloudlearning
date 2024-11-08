package com.sparkmembership.sparkowner.data.response

data class AddNotesMetaDto (
    val result : NotesMetaResult? = null
):BaseResponse()

data class NotesMetaResult (
    val quickNotes: List<QuickNote>,
    val staffMembers: List<StaffMember>
)

data class QuickNote(
    val quickTemplate: String
)

data class StaffMember(
    val id: Int,
    val staffName: String
)