package com.sparkmembership.sparkowner.data.response.filter

data class Result(
    val classRosters: List<ClassRoster>,
    val contactTypes: List<ContactType>,
    val groups: List<Group>,
    val hasBirthdayInList: List<HasBirthdayIn>,
    val memberships: List<Membership>,
    val tags: List<Tag>
)