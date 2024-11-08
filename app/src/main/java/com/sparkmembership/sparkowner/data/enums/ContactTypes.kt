package com.sparkmembership.sparkowner.data.enums

enum class ContactTypeEnum(val id: String) {
    PROSPECT_LEAD("P"),
    ON_A_TRIAL("T"),
    FORMERLY_ON_A_TRIAL("U"),
    FORMER_PROSPECT("Q"),
    STUDENTS("C"),
    INACTIVE_STUDENTS("L"),
    FORMER_STUDENTS_STILL_PAYING("I"),
    FORMER_STUDENTS("F"),
    AFTER_SCHOOLER("D"),
    CAMPER("E"),
    FITNESS("Z"),
    VENDOR_SUPPLIER("V"),
    BUSINESS_COMPANY("B"),
    STAFF_EMPLOYEE("S"),
    OTHER("O")
}
