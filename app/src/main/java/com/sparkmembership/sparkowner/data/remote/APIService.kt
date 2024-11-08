package com.sparkmembership.sparkowner.data.remote

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
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface APIService {
    @GET("contact")
    suspend fun getContacts(
        @Query("search") search: String? = null,
        @Query("contactType") contactType: String? = null,
        @Query("fTag") fTag: String? = null,
        @Query("fEliminateTags") fEliminateTags: String? = null,
        @Query("contactGroups") contactGroups: String? = null,
        @Query("fDOB") fDOB: Long? = null,
        @Query("contactsEnteredStart") contactsEnteredStart: String? = null,
        @Query("contactsEnteredEnd") contactsEnteredEnd: String? = null,
        @Query("StartAge") StartAge: Long? = null,
        @Query("EndAge") EndAge: Long? = null,
        @Query("SMSSubscribed") SMSSubscribed: Boolean? = null,
        @Query("fMemberships") fMemberships: String? = null,
        @Query("fClassRosters") fClassRosters: String? = null,
        @Query("pagesize") pageSize: Int? = null,
        @Query("pagenum") pageNum: Int? = null,
    ): Response<AllContactsResDto>

    @GET("contact/create/meta")
    suspend fun getContactTypes(): Response<ContactsMetaTypeResDto>

    @PUT("user/photo")
    suspend fun updateProfilePicture(
        @Body base64Image: ProfilePictureReqDTO
    ) : Response<UploadProfilePictureResDTO>

    @POST("user/rating")
    suspend fun rating(@Body rateUsReqDTO: RateUsReqDTO) : Response<RateUsResDTO>

    @POST("auth/logout")
    suspend fun logout(@Body logoutReqDTO: LogoutReqDTO): Response<BaseResponse>

    @GET("contact/filters")
    suspend fun getAllFilters() : Response<ContactFilterResDTO>

    @POST("contact")
    suspend fun addContacts(@Body contactRequest: ContactRequest) : Response<BaseResponse>

    @GET("user/timeslip/history")
    suspend fun getTimeSlipHistory(
        @Query("startDate") startDate: String? = null,
        @Query("endDate") endDate: String? = null,
        @Query("userName") userName: String? = null,
        @Query("pageSize") pageSize: Int? = null,
        @Query("pageIndex") pageIndex: Int? = null,
    ) : Response<TimeSlipHistoryDto>

    @GET("user/timeslip/user/{ID}/history")
    suspend fun getTimeSlipHistoryById(
        @Path("ID") userID: Int,
        @Query("pageSize") pageSize: Int? = null,
        @Query("pageIndex") pageIndex: Int? = null,
        @Query("endDate") endDate: String? = null,
        @Query("startDate") startDate: String? = null,
    ) : Response<TimeSlipUserHistoryDto>

    @GET("user/timeclock/user")
    suspend fun getUserTimeSlipHistory(
    ) : Response<UserTimeSlipDto>

    @POST("user/clock/in")
    suspend fun clockIn() : Response<ClockInDto>

    @PUT("user/clock/out/timeslip")
    suspend fun clockOut() : Response<ClockOutDto>

    @GET("contact/{ID}")
    suspend fun getContactDetails(
        @Path("ID") contactID : Long,
    ) : Response<ContactDetailsResDTO>

    @POST("contact/{ID}/profilePicture")
    suspend fun uploadContactDetailPicture(
        @Body base64Image: ProfilePictureReqDTO,
        @Path("ID") contactID: Long
    ) : Response<UploadProfilePictureResDTO>

    @GET("user/timeclock")
    suspend fun getStaffMemberTimeClock(
        @Query("date") date: String,
    ) : Response<StaffMemberTimeClockDto>

    @GET("contact/{ID}/notes")
    suspend fun getAllNotes(
        @Path("ID") contactId: Long,
        @Query("showSMSNotes") showSMSNotes: Boolean,
        @Query("showAllConnectedNotes") showAllConnectedNotes: Boolean,
        @Query("completedOrderNotes") completedOrderNotes: Boolean
    ) : Response<AllNotesDTO>

    @DELETE("notes/{noteID}")
    suspend fun deleteNote(
        @Path("noteID") noteId: Int,
    ) : Response<BaseResponse>

    @GET("notes/create/meta")
    suspend fun getAddNotesMetaData(
    ) : Response<AddNotesMetaDto>

    @POST("notes")
    suspend fun addNote(
        @Body addNoteEntity: AddNoteEntity
    ) : Response<BaseResponse>

    @GET("invoice/history")
    suspend fun getInvoiceHistory(
        @Query("contactID") contactID: Int? = null,
        @Query("invoiceType") invoiceType: Int? = null,
        @Query("startDate") startDate: String,
        @Query("endDate") endDate: String,
        @Query("pageIndex") pageIndex: Int? = null,
        @Query("pageSize") pageSize: Int? = null,
    ) : Response<InvoiceHistoryDto>

    @DELETE("invoice/{v}")
    suspend fun invoiceDetail(
        @Path("invoiceID ") invoiceID: Int,
    ) : Response<InvoiceDetailDto>
}