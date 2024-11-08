package com.sparkmembership.sparkowner.data.repository

import com.google.gson.Gson
import com.sparkmembership.sparkfitness.data.repository.BaseRepository
import com.sparkmembership.sparkowner.data.entity.AddNoteEntity
import com.sparkmembership.sparkowner.data.remote.APIService
import com.sparkmembership.sparkowner.data.request.ContactRequest
import com.sparkmembership.sparkowner.data.request.LogoutReqDTO
import com.sparkmembership.sparkowner.data.request.ProfilePictureReqDTO
import com.sparkmembership.sparkowner.data.request.RateUsReqDTO
import com.sparkmembership.sparkowner.data.response.AddNotesMetaDto
import com.sparkmembership.sparkowner.data.response.AllContactsResDto
import com.sparkmembership.sparkowner.data.response.AllNotesDTO
import com.sparkmembership.sparkowner.data.response.BaseResponse
import com.sparkmembership.sparkowner.data.response.ClockInDto
import com.sparkmembership.sparkowner.data.response.ClockOutDto
import com.sparkmembership.sparkowner.data.response.ContactDetailsResDTO
import com.sparkmembership.sparkowner.data.response.ContactsMetaTypeResDto
import com.sparkmembership.sparkowner.data.response.InvoiceDetailDto
import com.sparkmembership.sparkowner.data.response.InvoiceHistoryDto
import com.sparkmembership.sparkowner.data.response.RateUsResDTO
import com.sparkmembership.sparkowner.data.response.StaffMemberTimeClockDto
import com.sparkmembership.sparkowner.data.response.TimeSlip
import com.sparkmembership.sparkowner.data.response.TimeSlipHistoryDto
import com.sparkmembership.sparkowner.data.response.TimeSlipUserHistoryDto
import com.sparkmembership.sparkowner.data.response.UploadProfilePictureResDTO
import com.sparkmembership.sparkowner.data.response.UserTimeSlipDto
import com.sparkmembership.sparkowner.data.response.filter.ContactFilterResDTO
import com.sparkmembership.sparkowner.domain.repository.MainRepository
import javax.inject.Inject

class MainRepositoryImp @Inject constructor(
    private val mainAPIService: APIService
) : MainRepository, BaseRepository() {

    override suspend fun getContacts(
        pageIndex: Int?,
        pageSize: Int?,
        contactType: String?,
        fTag: String?,
        fEliminateTags: String?,
        contactGroups: String?,
        fDOB: Long?,
        contactsEnteredStart: String?,
        contactsEnteredEnd: String?,
        StartAge: Long?,
        EndAge: Long?,
        search: String?,
        classRoster: String?,
        membership: String?,
        smsSubscribed: Boolean?
    ): AllContactsResDto {
        val getContactsCall =
            mainAPIService.getContacts(
                pageNum = pageIndex, pageSize = pageSize, search = search,
                contactType = contactType, fTag = fTag, fEliminateTags = fEliminateTags,
                contactGroups = contactGroups, fDOB = fDOB, contactsEnteredStart = contactsEnteredStart,
                contactsEnteredEnd = contactsEnteredEnd, StartAge = StartAge, EndAge = EndAge,
                fClassRosters = classRoster, fMemberships = membership, SMSSubscribed = smsSubscribed
            )
        return when (val response = processCall { getContactsCall }) {
            is AllContactsResDto -> {
                response
            }

            else -> {
                Gson().fromJson(response.toString(), AllContactsResDto::class.java)
            }
        }
    }

    override suspend fun getContactTypes(): ContactsMetaTypeResDto {
        val getContactsTypeCall = mainAPIService.getContactTypes()
        return when (val response = processCall { getContactsTypeCall }) {
            is ContactsMetaTypeResDto -> {
                response
            }

            else -> {
                Gson().fromJson(response.toString(), ContactsMetaTypeResDto::class.java)
            }
        }
    }

    override suspend fun updateProfilePicture(profilePictureReqDTO: ProfilePictureReqDTO): UploadProfilePictureResDTO {

        val updateProfilePicture = mainAPIService.updateProfilePicture(profilePictureReqDTO)
        return when (val response = processCall { updateProfilePicture }) {
            is UploadProfilePictureResDTO -> {
                response
            }

            else -> {
                Gson().fromJson(response.toString(), UploadProfilePictureResDTO::class.java)
            }
        }
    }

    override suspend fun rating(userRateUsReqDTO: RateUsReqDTO): RateUsResDTO {
        val raring = mainAPIService.rating(userRateUsReqDTO)
        return when (val response = processCall { raring }) {
            is RateUsResDTO -> {
                response
            }

            else -> {
                Gson().fromJson(response.toString(), RateUsResDTO::class.java)
            }
        }
    }

    override suspend fun logout(logoutReqDTO: LogoutReqDTO): BaseResponse {

        val logout = mainAPIService.logout(logoutReqDTO)
        return when (val response = processCall { logout }) {
            is BaseResponse -> {
                response
            }

            else -> {
                Gson().fromJson(response.toString(), BaseResponse::class.java)
            }
        }
    }

    override suspend fun getAllContactFilters(): ContactFilterResDTO {

        val allContactsFilters = mainAPIService.getAllFilters()
        return when (val response = processCall { allContactsFilters }) {
            is ContactFilterResDTO -> {
                response
            }
            else -> {
                Gson().fromJson(response.toString(), ContactFilterResDTO::class.java)
            }

            }
    }

    override suspend fun addContacts(contactRequest: ContactRequest): BaseResponse {
        val addContact = mainAPIService.addContacts(contactRequest)
        return when (val response = processCall { addContact }) {
            is BaseResponse -> {
                response
            }
            else -> {
                Gson().fromJson(response.toString(), BaseResponse::class.java)
            }

        }
    }

    override suspend fun getTimeSlipHistory(
        startDate: String?,
        endDate: String?,
        userName: String?,
        pageSize: Int?,
        pageIndex: Int?
    ): TimeSlipHistoryDto {
        val timeSlipHistory = mainAPIService.getTimeSlipHistory(pageIndex = pageIndex, pageSize = pageSize, startDate = startDate, endDate = endDate)
        return when (val response = processCall { timeSlipHistory }) {
            is TimeSlipHistoryDto -> {
                response
            }
            else -> {
                Gson().fromJson(response.toString(), TimeSlipHistoryDto::class.java)
            }

        }
    }

    override suspend fun getTimeSlipHistoryById(
        startDate: String?,
        endDate: String?,
        id: Int,
        pageSize: Int?,
        pageIndex: Int?
    ): TimeSlipUserHistoryDto {
        val timeSlipHistoryById = mainAPIService.getTimeSlipHistoryById(userID = id,pageIndex = pageIndex, pageSize = pageSize, startDate = startDate, endDate = endDate)
        return when (val response = processCall { timeSlipHistoryById }) {
            is TimeSlipUserHistoryDto -> {
                response
            }
            else -> {
                Gson().fromJson(response.toString(), TimeSlipUserHistoryDto::class.java)
            }
        }
    }

    override suspend fun getUserTimeSlipHistory(): UserTimeSlipDto {
        val userTimeSlipHistory = mainAPIService.getUserTimeSlipHistory()
        return when (val response = processCall { userTimeSlipHistory }) {
            is UserTimeSlipDto -> {
                response
            }
            else -> {
                Gson().fromJson(response.toString(), UserTimeSlipDto::class.java)
            }
        }
    }

    override suspend fun clockIn(): ClockInDto {
        val clockIn = mainAPIService.clockIn()
        return when (val response = processCall { clockIn }) {
            is ClockInDto -> {
                response
            }
            else -> {
                Gson().fromJson(response.toString(), ClockInDto::class.java)
            }
        }
    }

    override suspend fun clockOut(): ClockOutDto {
        val clockOut = mainAPIService.clockOut()
        return when (val response = processCall { clockOut }) {
            is ClockOutDto -> {
                response
            }
            else -> {
                Gson().fromJson(response.toString(), ClockOutDto::class.java)
            }
        }
    }

    override suspend fun getContactDetails(contactID: Long): ContactDetailsResDTO {
        val getContactDetails = mainAPIService.getContactDetails(contactID)
        return when (val response = processCall { getContactDetails }) {
            is ContactDetailsResDTO -> {
                response
            }
            else -> {
                Gson().fromJson(response.toString(), ContactDetailsResDTO::class.java)
            }

            }
    }

    override suspend fun uploadContactDetailsPicture(profilePictureReqDTO: ProfilePictureReqDTO, contactID: Long): UploadProfilePictureResDTO {


        val updateProfilePicture = mainAPIService.uploadContactDetailPicture(profilePictureReqDTO, contactID)
        return when (val response = processCall { updateProfilePicture }) {
            is UploadProfilePictureResDTO -> {
                response
            }

            else -> {
                Gson().fromJson(response.toString(), UploadProfilePictureResDTO::class.java)
            }
        }

    }

    override suspend fun getStaffMemberTimeClock(date : String): StaffMemberTimeClockDto {
        val getStaffMemberTimeClock = mainAPIService.getStaffMemberTimeClock(date)
        return when (val response = processCall { getStaffMemberTimeClock }) {
            is StaffMemberTimeClockDto -> {
                response
            }
            else -> {
                Gson().fromJson(response.toString(), StaffMemberTimeClockDto::class.java)
            }
        }
    }

    override suspend fun getAllNotes(
        contactId: Long,
        showSMSNotes: Boolean,
        showAllConnectedNotes: Boolean,
        completedOrderNotes: Boolean
    ): AllNotesDTO {
        val getAllNotes = mainAPIService.getAllNotes(contactId, showSMSNotes, showAllConnectedNotes, completedOrderNotes)
        return when (val response = processCall { getAllNotes }) {
            is AllNotesDTO -> {
                response
            }
            else -> {
                Gson().fromJson(response.toString(), AllNotesDTO::class.java)
            }
        }
    }

    override suspend fun deleteNotes(noteId: Int): BaseResponse {
        val deleteNote = mainAPIService.deleteNote(noteId = noteId)
        return when (val response = processCall { deleteNote }) {
            is BaseResponse -> {
                response
            }
            else -> {
                Gson().fromJson(response.toString(), BaseResponse::class.java)
            }
        }
    }

    override suspend fun getAddNotesMetaData(): AddNotesMetaDto {
        val addNoteMetaData = mainAPIService.getAddNotesMetaData()
        return when (val response = processCall { addNoteMetaData }) {
            is AddNotesMetaDto -> {
                response
            }
            else -> {
                Gson().fromJson(response.toString(), AddNotesMetaDto::class.java)
            }
        }
    }

    override suspend fun addNote(addNoteEntity: AddNoteEntity): BaseResponse {
        val addNote = mainAPIService.addNote(addNoteEntity)
        return when (val response = processCall { addNote }) {
            is BaseResponse -> {
                response
            }
            else -> {
                Gson().fromJson(response.toString(), BaseResponse::class.java)
            }
        }
    }

    override suspend fun getInvoiceHistory(
        contactId: Int?,
        invoiceType: Int?,
        startDate: String,
        endDate: String,
        pageIndex: Int?,
        pageSize: Int?
    ): InvoiceHistoryDto {
        val getInvoiceHistory = mainAPIService.getInvoiceHistory(contactID = contactId, invoiceType = invoiceType, startDate = startDate, endDate = endDate)
        return when (val response = processCall { getInvoiceHistory }) {
            is InvoiceHistoryDto -> {
                response
            }
            else -> {
                Gson().fromJson(response.toString(), InvoiceHistoryDto::class.java)
            }
        }
    }

    override suspend fun getInvoiceDetail(invoiceID: Int): InvoiceDetailDto {
        val getInvoiceDetail = mainAPIService.invoiceDetail(invoiceID = invoiceID)
        return when (val response = processCall { getInvoiceDetail }) {
            is InvoiceDetailDto -> {
                response
            }
            else -> {
                Gson().fromJson(response.toString(), InvoiceDetailDto::class.java)
            }
        }
    }


}