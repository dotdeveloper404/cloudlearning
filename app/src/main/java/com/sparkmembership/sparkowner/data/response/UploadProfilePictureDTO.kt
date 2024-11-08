package com.sparkmembership.sparkowner.data.response

data class UploadProfilePictureResDTO(
    val result: Results
):BaseResponse()


data class Results(
    val pictureURL: String,
    val profilePictureUpdatedTime: String
)