package com.sparkmembership.sparkowner.data.request

import com.sparkmembership.sparkowner.data.response.filter.ClassRoster
import com.sparkmembership.sparkowner.data.response.filter.ContactType
import com.sparkmembership.sparkowner.data.response.filter.Group
import com.sparkmembership.sparkowner.data.response.filter.HasBirthdayIn
import com.sparkmembership.sparkowner.data.response.filter.Membership
import com.sparkmembership.sparkowner.data.response.filter.Tag


data class ContactFilterResult(
    var contactTypes: List<ContactType>,
    val tagged: List<Tag>,
    val notTagged: List<Tag>,
    val groups: List<Group>,
    val memberships: List<Membership>,
    val classRosters: List<ClassRoster>,
    val birthdayIn: List<HasBirthdayIn>,
    val miniAge : String? = null,
    val maxAge : String? = null,
    val startDate : String? = null,
    val endDate : String? = null,
    val contactFilter: FilterStringObject
)

data class FilterStringObject(
    var contactTyperString: String? = null,
    var taggedContactString: String? = null,
    var notTaggedContactString: String? = null,
    var groupAgeString: String? = null,
    var hasBirthdayString: String? = null,
    var contactEnteredStartString: String? = null,
    var contactEnteredEndString: String? = null,
    var ageMinString: String? = null,
    var ageMaxString: String? = null,
    var membershipString: String? = null,
    var classRosterString: String? = null,
)
