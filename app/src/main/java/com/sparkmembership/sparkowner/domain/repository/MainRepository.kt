package com.sparkmembership.sparkowner.domain.repository

import com.sparkmembership.sparkowner.data.entity.AddNoteEntity
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
import com.sparkmembership.sparkowner.data.response.TimeSlipHistoryDto
import com.sparkmembership.sparkowner.data.response.TimeSlipUserHistoryDto
import com.sparkmembership.sparkowner.data.response.UploadProfilePictureResDTO
import com.sparkmembership.sparkowner.data.response.UserTimeSlipDto
import com.sparkmembership.sparkowner.data.response.filter.ContactFilterResDTO
import retrofit2.http.Query

interface MainRepository {
    suspend fun getContacts(
        pageNum: Int?, pageSize: Int?,contactType: String?,fTag: String?, fEliminateTags: String?,
        contactGroups: String?,fDOB: Long?,contactsEnteredStart: String?,contactsEnteredEnd: String?
        ,StartAge: Long?,EndAge: Long?,search:String?, classRoster: String?, membership: String?,
        smsSubscribed: Boolean?

    ): AllContactsResDto
    suspend fun getContactTypes(): ContactsMetaTypeResDto
    suspend fun updateProfilePicture(profilePictureReqDTO: ProfilePictureReqDTO): UploadProfilePictureResDTO
    suspend fun rating(userRateUsReqDTO: RateUsReqDTO) : RateUsResDTO
    suspend fun logout(logoutReqDTO: LogoutReqDTO) : BaseResponse
    suspend fun getAllContactFilters() : ContactFilterResDTO
    suspend fun addContacts(contactRequest: ContactRequest) : BaseResponse
    suspend fun getTimeSlipHistory( startDate: String?, endDate: String?, userName: String?, pageSize: Int?, pageIndex: Int?) : TimeSlipHistoryDto
    suspend fun getTimeSlipHistoryById( startDate: String?, endDate: String?, id: Int, pageSize: Int?, pageIndex: Int?) : TimeSlipUserHistoryDto
    suspend fun getUserTimeSlipHistory() : UserTimeSlipDto
    suspend fun clockIn() : ClockInDto
    suspend fun clockOut() : ClockOutDto
    suspend fun getContactDetails(contactID: Long) : ContactDetailsResDTO
    suspend fun uploadContactDetailsPicture(profilePictureReqDTO: ProfilePictureReqDTO, contactID:Long) : UploadProfilePictureResDTO
    suspend fun getStaffMemberTimeClock(date : String) : StaffMemberTimeClockDto
    suspend fun getAllNotes(contactId : Long, showSMSNotes: Boolean, showAllConnectedNotes: Boolean, completedOrderNotes: Boolean) : AllNotesDTO
    suspend fun deleteNotes(noteId : Int) : BaseResponse
    suspend fun getAddNotesMetaData() : AddNotesMetaDto
    suspend fun addNote(addNoteEntity: AddNoteEntity) : BaseResponse
    suspend fun getInvoiceHistory(contactId: Int?, invoiceType: Int?, startDate: String, endDate: String, pageIndex: Int?, pageSize: Int?) : InvoiceHistoryDto
    suspend fun getInvoiceDetail(invoiceID : Int) : InvoiceDetailDto

}