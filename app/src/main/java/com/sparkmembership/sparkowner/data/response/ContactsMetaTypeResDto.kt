package com.sparkmembership.sparkowner.data.response

import com.sparkmembership.sparkowner.data.response.filter.ContactType
import com.sparkmembership.sparkowner.data.response.filter.Tag

data class ContactsMetaTypeResDto(
    val result: ContactsMetaType,
):BaseResponse()

data class ContactsMetaType(
    val contactTypes: List<ContactType>,
    val genders: List<Gender>,
    val marketingSources: List<MarketingSource>,
    val phases: List<Phase>,
    val tags: List<Tag>
)

data class Gender(
    val genderID: Int,
    val genderName: String,
    val genderValue: String
)

data class MarketingSource(
    val marketingSource: String,
    val marketingSourceID: Int
)

data class Phase(
    val contactType: String,
    val phaseID: Int,
    val phaseName: String
)
