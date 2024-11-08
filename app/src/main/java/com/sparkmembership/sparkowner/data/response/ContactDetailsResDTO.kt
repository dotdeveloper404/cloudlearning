package com.sparkmembership.sparkowner.data.response

data class ContactDetailsResDTO(
    val result: Details
):BaseResponse()

data class Details(
    val contactID: Int,
    val firstName: String,
    val lastName: String,
    val profileImage: String,
    val profilePicLastUpdate: String?,
    val dateEntered: String,
    val birthDate: String,
    val age: Int,
    val gender: String,
    val vacationStart: String?,
    val vacationEnd: String?,
    val needToSee: Boolean,
    val needToSeeDetails: String?,
    val isCovidVaccinationDone: Boolean,
    val studentPhase: String?,
    val newMemberMaximizer: Boolean,
    val medicalConcerns: String?,
    val guardians: List<Guardian>,
    val appointments: List<Appointment>,
    val usingStudentApp: Boolean,
    val mobilePhones: List<MobilePhone>,
    val emailAddress: String,
    val lastSeen: String?,
    val connectedContacts: List<Contacts>,
    val authorizedPersons: List<String>,
    val about: String?,
    val address1: String?,
    val address2: String?,
    val city: String?,
    val country: String?,
    val refferedByContactID: Int?,
    val refferedBy: String?,
    val paymentMethodOnFile: Boolean,
    val sendReceiptForAutoPayment: Boolean,
    val ataMembership: AtaMembership?,
    val assignedTags: List<String>,
    val leadPhaseID: Int?,
    val familyID: Int?,
    val customFields: List<CustomField>
)

data class Guardian(
    val labelName: String,
    val label: String,
    val firstName: String,
    val lastName: String
)

data class Appointment(
    val name: String,
    val startTime: String,
    val color : String
)

data class MobilePhone(
    val label: String,
    val mobile: String
)

data class Contacts(
    val name: String,
    val contactID: Int,
    val profileImage: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val mobilePhone: String?,
    val dob: String,
    val profilePicLastUpdate: String?
)

data class AtaMembership(
    val number: String,
    val expiration: String
)

data class CustomField(
    val name: String,
    val value: String
)
