package in.gov.pocra.training.web_services;

import com.google.gson.JsonObject;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;

public interface APIRequest {


    // UAT Login
    @POST(APIServices.CA_OAUTH_URL)
    Call<JsonObject> oauthTokenRequest(@Body RequestBody params);

    // Production Login
    // SSO AUTH
    @POST(APIServices.REFRESH_TOKEN_API_URL)
    Call<JsonObject> refreshTokenRequest(@Body RequestBody params);

    @POST(APIServices.OTH_SERVICE_NEW_API_URL)
    Call<JsonObject> ssoAUTHRequest(@Body RequestBody params);

    //firebaseSaveTokenActiveDeactiveUsers
    @POST(APIServices.check_USER_ACTIVE_DEACTIVE)
    Call<JsonObject> checkActivateDeactivateUser(@Body RequestBody params);

    // Forgot Password OTP
    @POST(APIServices.GET_FORGOT_PASS_OTP)
    Call<JsonObject> sendForgotPassOtpRequest(@Body RequestBody params);

    // Resend Forgot Password OTP
    @POST(APIServices.RESEND_FORGOT_PASS_OTP)
    Call<JsonObject> resendForgotPassOtpRequest(@Body RequestBody params);

    // Reset Password
    @POST(APIServices.RESET_PASS)
    Call<JsonObject> resetPasswordRequest(@Body RequestBody params);

    // Update User profile
    @POST(APIServices.UPDATE_USER_PROFILE)
    Call<JsonObject> updateUserProfileRequest(@Body RequestBody params);

    // Update User Token
    @POST(APIServices.UPDATE_USER_TOKEN_URL)
    Call<JsonObject> updateUserTokenRequest(@Body RequestBody params);

    // GET NOTIFICATION UNREAD COUNT
    @POST(APIServices.GET_NOTIFICATION_UNREAD_COUNT)
    Call<JsonObject> getFirebaseUnreadNotificationCount(@Body RequestBody params);

//    @POST(APIServices.userdetails)
//    Call<JsonObject> getroledata(@Body RequestBody params);

    // Get Firebase Notification List
    @POST(APIServices.USER_NOTIFICATION_LIST)
    Call<JsonObject> getfirebaseNotificationList(@Body RequestBody params);

    //READ NOTIFICATION MESSAGE
    @POST(APIServices.READ_NOTIFICATION_MESSAGE)
    Call<JsonObject> readfirebaseNotification(@Body RequestBody params);

    //Switch role
    @POST(APIServices.userdetails)
    Call<JsonObject> getroledata(@Body RequestBody params);

    // User Logout
    @POST(APIServices.USER_LOGOUT_URL)
    Call<JsonObject> userLogoutRequest(@Body RequestBody params);

    // Update User profile Image
    @Multipart
    @POST(APIServices.UPDATE_USER_PROFILE_IMAGE)
    Call<JsonObject> uploadProfileImagesRequest(@Part MultipartBody.Part image, @PartMap Map<String, RequestBody> params);



    /** New Request*/

    // Event Type list
    @POST(APIServices.EVENT_TYPE_LIST_URL)
    Call<JsonObject> eventTypeRequest(@Body RequestBody params);

    // Event Type list
    @POST(APIServices.EVENT_SUB_TYPE_LIST_URL)
    Call<JsonObject> eventSubTypeRequest(@Body RequestBody params);

    // Get Resource person list
    @POST(APIServices.RES_PER_LIST_URL)
    Call<JsonObject> resPerListRequest(@Body RequestBody params);

    // Add Resource person
    @POST(APIServices.ADD_RES_PERSON_URL)
    Call<JsonObject> addResPerRequest(@Body RequestBody params);

    // Get Coordinator list
    @POST(APIServices.GET_VENUE_LIST_URL)
    Call<JsonObject> getVenueListRequest(@Body RequestBody params);

    // Get KVK list
    @POST(APIServices.GET_KVK_LIST_URL)
    Call<JsonObject> getKvkListRequest(@Body RequestBody params);

    // Get Coordinator Designation list
    @POST(APIServices.GET_CORD_DESIG_LIST_URL)
    Call<JsonObject> cordDesigListRequest(@Body RequestBody params);

    // Get Coordinator list
    @POST(APIServices.CORD_LIST_URL)
    Call<JsonObject> cordListRequest(@Body RequestBody params);

    // Get Official Coordinator list
    @POST(APIServices.OFFICIAL_CORD_LIST_URL)
    Call<JsonObject> officialCordListRequest(@Body RequestBody params);

    // Get VCRMC (GP) member list
    @POST(APIServices.GP_MEM_LIST_URL)
    Call<JsonObject> gpMemberListRequest(@Body RequestBody params);

    // Get VCRMC (GP) member Detail list
    @POST(APIServices.GET_PS_PARTICIPANTS_GROUP_URL)
    Call<JsonObject> getPsParticipentGroupRequest(@Body RequestBody params);

    // Get VCRMC (GP) member Detail list
    @POST(APIServices.GP_MEM_DETAIL_LIST_URL)
    Call<JsonObject> gpMemberDetailListRequest(@Body RequestBody params);

    //FOR CA
    // Get VCRMC (GP) member Detail list
    @POST(APIServices.GP_MEM_LIST_CA_URL)
    Call<JsonObject> gpMemberListCaRequest(@Body RequestBody params);

    // Get Village list
    @POST(APIServices.VILLAGE_LIST_URL)
    Call<JsonObject> villageListRequest(@Body RequestBody params);

    // Get farmer list
    @POST(APIServices.FARMER_LIST_URL)
    Call<JsonObject> farmerListRequest(@Body RequestBody params);
    @POST(APIServices.SHG_LIST_URL)
    Call<JsonObject> GETSHG_LIST(@Body RequestBody params);
    @POST(APIServices.FPC_LIST_URL)
    Call<JsonObject> GETPFCList(@Body RequestBody params);
    @POST(APIServices.farm_LIST_URL)
    Call<JsonObject> GetFarmlist(@Body RequestBody params);
    // Get farmer list
    @POST(APIServices.FACILITATOR_LIST_URL)
    Call<JsonObject> facilitatorListRequest(@Body RequestBody params);

    // Get Role list
    @POST(APIServices.ROLE_LIST_URL)
    Call<JsonObject> getParticipantRoleRequest(@Body RequestBody params);

    // Get Office staff Role list
    @POST(APIServices.ROLE_PMU_OFFICE_STAFF_LIST_URL)
    Call<JsonObject> getOfficeStaffRoleRequest(@Body RequestBody params);

    // Get Office staff Role list
    @POST(APIServices.ROLE_PMU_FIELD_STAFF_LIST_URL)
    Call<JsonObject> getFieldStaffRoleRequest(@Body RequestBody params);

    // Get Role list
    @POST(APIServices.GET_MEM_BY_ROLE_LOCATION_URL)
    Call<JsonObject> getParticipantRoleAndLocationRequest(@Body RequestBody params);

    // Get Other participants list
    @POST(APIServices.ADD_OTHER_PARTICIPANTS_URL)
    Call<JsonObject> addOtherParticipantsRequest(@Body RequestBody params);

    // Update other participants list
    @POST(APIServices.UPDATE_OTHER_PARTICIPANTS_URL)
    Call<JsonObject> updateOtherParticipantsRequest(@Body RequestBody params);

    // Get other participants list
    @POST(APIServices.GET_OTHER_PARTICIPANTS_URL)
    Call<JsonObject> getOtherParticipantsRequest(@Body RequestBody params);

    // Get event Member attend detail
    @POST(APIServices.PS_MEM_ATTEND_DETAIL_URL)
    Call<JsonObject> getMemAttendDetailRequest(@Body RequestBody params);

    // Create Ps-HRD Schedule
    @POST(APIServices.CREATE_PS_SCHEDULE_URL)
    Call<JsonObject> psCreateScheduleRequest(@Body RequestBody params);

    // Update Ps-HRD Schedule
    @POST(APIServices.UPDATE_PS_SCHEDULE_URL)
    Call<JsonObject> psUpdateScheduleRequest(@Body RequestBody params);

    // Get Ps-HRD Schedule list
    @POST(APIServices.GET_PS_SCHEDULE_URL)
    Call<JsonObject> psGetScheduledRequest(@Body RequestBody params);

    // Get Ps-HRD Schedule list
    @POST(APIServices.GET_PS_SCHEDULE_DIST_ID_URL)
    Call<JsonObject> psGetScheduledByDistIdRequest(@Body RequestBody params);


    // Get Ps-HRD Schedule Detail
    @POST(APIServices.GET_PS_SCHEDULE_DETAIL_URL)
    Call<JsonObject> psGetEventDetailRequest(@Body RequestBody params);

    // get Event Cancel Reason
    @POST(APIServices.EVENT_CANCEL_REASON_URL)
    Call<JsonObject> eventCancelReasonRequest(@Body RequestBody params);

    // Ps-HRD Cancel Schedule
    @POST(APIServices.PS_SCHEDULE_CANCEL_URL)
    Call<JsonObject> psCancelEventRequest(@Body RequestBody params);

    // Ps-HRD Closed Event list
    @POST(APIServices.PS_CLOSED_EVENT_LIST_URL)
    Call<JsonObject> psClosedEventListRequest(@Body RequestBody params);

    // Ps-HRD pmu Closed Event list
    @POST(APIServices.PS_PMU_CLOSED_EVENT_LIST_URL)
    Call<JsonObject> psPmuClosedEventListRequest(@Body RequestBody params);

    // Ps-HRD Event Date list
    @POST(APIServices.PS_EVENT_DATE_LIST_URL)
    Call<JsonObject> psEventDateListRequest(@Body RequestBody params);

    // Ps-HRD Event Date list bu dist id
    @POST(APIServices.PS_EVENT_DATE_BY_DIST_LIST_URL)
    Call<JsonObject> psEventDateListByDistIdRequest(@Body RequestBody params);

    // Ps-HRD Event list by Date
    @POST(APIServices.PS_EVENT_LIST_BY_DATE_URL)
    Call<JsonObject> psEventListByDateRequest(@Body RequestBody params);

    // get VCRMC Attendance GP + Officer list
    @POST(APIServices.GET_ATTENDANCE_GP_LIST)
    Call<JsonObject> getVCRMCAttendanceListRequest(@Body RequestBody params);

    // get VCRMC Member Detail list
    @POST(APIServices.GET_VCRMC_MEMBER_ATTENDANCE_DETAIL_LIST)
    Call<JsonObject> getVCRMCMemAttendDetailListRequest(@Body RequestBody params);

    // add/Edit VCRMC Member Detail
    @POST(APIServices.ADD_EDIT_VCRMC_MEM_DETAIL)
    Call<JsonObject> addEditVCRMCMemberDetailRequest(@Body RequestBody params);

    // VCRMC final attendance
    @POST(APIServices.FINAL_ATTENDANCE)
    Call<JsonObject> vCRMCFinalAttendanceRequest(@Body RequestBody params);

    // For CA
    // Create CA Schedule

    // Get group member Detail list
    @POST(APIServices.GET_CA_PARTICIPANTS_GROUP_URL)
    Call<JsonObject> getCaParticipantGroupRequest(@Body RequestBody params);

    @POST(APIServices.CREATE_CA_SCHEDULE_URL)
    Call<JsonObject> caCreateScheduleRequest(@Body RequestBody params);

    // Update CA Schedule
    @POST(APIServices.UPDATE_CA_SCHEDULE_URL)
    Call<JsonObject> caUpdateScheduleRequest(@Body RequestBody params);

    // Get CA Schedule
    @POST(APIServices.GET_CA_SCHEDULE_URL)
    Call<JsonObject> caGetScheduleRequest(@Body RequestBody params);

    // Get CA Taluka Id
    @POST(APIServices.GET_CA_TALUKA_URL)
    Call<JsonObject> caGetTalukaIdRequest(@Body RequestBody params);

    // Create CA Schedule
    @POST(APIServices.GET_CA_SCHEDULE_DETAIL_URL)
    Call<JsonObject> caGetScheduleDetailRequest(@Body RequestBody params);

    // Create CA Schedule
    @POST(APIServices.GET_CA_SUB_DIV_LIST_URL)
    Call<JsonObject> caGetSubDivListWithCountRequest(@Body RequestBody params);

    // Create CA Schedule
    @POST(APIServices.GET_CA_CLOSED_EVENT_SUB_DIV_LIST_URL)
    Call<JsonObject> caGetSubDivListWithClosedCountRequest(@Body RequestBody params);

    // CA Closed Event list
    @POST(APIServices.CA_CLOSED_EVENT_LIST_URL)
    Call<JsonObject> caClosedEventListRequest(@Body RequestBody params);

    // Ca all Closed Event list
    @POST(APIServices.CA_ALL_CLOSED_EVENT_LIST_URL)
    Call<JsonObject> caAllClosedEventListRequest(@Body RequestBody params);

    // Add Ca Labour
    @POST(APIServices.ADD_CA_LABOUR_URL)
    Call<JsonObject> addCaLabourRequest(@Body RequestBody params);

    // Get Ca Labour
    @POST(APIServices.GET_CA_LABOUR_URL)
    Call<JsonObject> getCaLabourRequest(@Body RequestBody params);

    // Add Ca Resource Person
    @POST(APIServices.ADD_CA_RESOURCE_P_URL)
    Call<JsonObject> addCaResourcePRequest(@Body RequestBody params);

    // Get Ca Resource Person
    @POST(APIServices.GET_CA_RESOURCE_P_URL)
    Call<JsonObject> getCaResourcePRequest(@Body RequestBody params);

//    @POST(APIServices.GET_CA_GET_RESOURCE_PERSON_URL)
//    Call<JsonObject> getCaResourcePersonListRequest(@Body RequestBody params);


    /***COORDINATOR APIS*/

    // Online

    // Coordinator Event list
    @POST(APIServices.CORD_EVENT_LIST_URL)
    Call<JsonObject> cordEventListRequest(@Body RequestBody params);

    // Coordinator Event Group list Schedule
    @POST(APIServices.CORD_EVENT_GROUP_LIST_URL)
    Call<JsonObject> cordEventGroupListRequest(@Body RequestBody params);

    // Coordinator Event Group Member list Schedule
    @POST(APIServices.CORD_EVENT_GROUP_MEM_LIST_URL)
    Call<JsonObject> cordEventGroupMemListRequest(@Body RequestBody params);

    // Add Other Group Member detail
    @POST(APIServices.ADD_OTHER_GROUP_MEM_URL)
    Call<JsonObject> addOtherMemDetailRequest(@Body RequestBody params);

    // Get Other Group Member list
    @POST(APIServices.GET_OTHER_GROUP_MEM_LIST_URL)
    Call<JsonObject> getOtherGroupMemListRequest(@Body RequestBody params);

    // Get Other Group Member Detail
    @POST(APIServices.GET_OTHER_GROUP_MEM_DETAIL_URL)
    Call<JsonObject> getOtherGroupMemDetailRequest(@Body RequestBody params);

    // Update Other Group Member Detail
    @POST(APIServices.UPDATE_OTHER_GROUP_MEM_DETAIL_URL)
    Call<JsonObject> updateOtherGroupMemDetailRequest(@Body RequestBody params);

    // Submit Member attendance for a day
    @POST(APIServices.SUBMIT_MEM_ATTEND_OF_DAY_URL)
    Call<JsonObject> submitMemAttendOfDayRequest(@Body RequestBody params);

    // Upload Attendance Image of a day
    @Multipart
    @POST(APIServices.UPLOAD_ATTEND_IMAGE)
    Call<JsonObject> uploadAttendImageRequest(@Part MultipartBody.Part image,@PartMap Map<String, RequestBody> params);

    // Get Attend image list of a day
    @POST(APIServices.GET_ATTEND_IMAGE_LIST)
    Call<JsonObject> getAttendImageListOfDayRequest(@Body RequestBody params);

    // Event Collage PDF request
    @POST(APIServices.EVENT_COLLAGE_PDF_URL)
    Call<JsonObject> getCollagePdfRequest(@Body RequestBody params);

    // Event Closer request
    @POST(APIServices.EVENT_CLOSER_URL)
    Call<JsonObject> eventCloserRequest(@Body RequestBody params);

    // get Closed Event list request
    @POST(APIServices.GET_CLOSED_EVENT_LIST_URL)
    Call<JsonObject> getClosedEventListRequest(@Body RequestBody params);

    // get Closed Event list request
    @POST(APIServices.GET_ATTENDANCE_REPORT_URL)
    Call<JsonObject> getAttendReportRequest(@Body RequestBody params);

    // get Consolidated Report request
    @POST(APIServices.GET_CONSOLIDATED_REPORT_URL)
    Call<JsonObject> getConsolidatedReportRequest(@Body RequestBody params);

    // get consolidated report with collage request
    @POST(APIServices.GET_CONSOLIDATED_REPORT_COLLAGE_URL)
    Call<JsonObject> getConsolidatedReportWithCollageRequest(@Body RequestBody params);



    // Add Session request
    @POST(APIServices.ADD_SESSION_URL)
    Call<JsonObject> addSessionRequest(@Body RequestBody params);

    // Get Session List request
    @POST(APIServices.GET_SESSION_LIST_URL)
    Call<JsonObject> getSessionListRequest(@Body RequestBody params);

    // Delete Session request
    /*@DELETE(APIServices.DELETE_SESSION_URL)
    Call<JsonObject> deleteSessionRequest(@Url String url);*/

    @POST(APIServices.DELETE_SESSION_URL)
    Call<JsonObject> deleteSessionRequest(@Body RequestBody params);





    // Offline

    // get Event Detail request
    @POST(APIServices.OFF_EVENT_DETAIL_URL)
    Call<JsonObject> offEventDetailRequest(@Body RequestBody params);



    // PMU

    // Create PMU Schedule
    @POST(APIServices.CREATE_PMU_SCHEDULE_URL)
    Call<JsonObject> pmuCreateScheduleRequest(@Body RequestBody params);

    // Update PMU Schedule
    @POST(APIServices.UPDATE_PMU_SCHEDULE_URL)
    Call<JsonObject> pmuUpdateScheduleRequest(@Body RequestBody params);

    // Get PMU Group list
    @POST(APIServices.GET_PMU_PARTICIPANTS_GROUP_URL)
    Call<JsonObject> getPmuGroupRequest(@Body RequestBody params);

    // get PMU district wise Scheduled event list
    @POST(APIServices.GET_PMU_EVENT_LIST_URL)
    Call<JsonObject> getPMUEventListRequest(@Body RequestBody params);

    // get PMU district wise Scheduled event list
    @POST(APIServices.GET_PMU_DIST_EVENT_LIST_URL)
    Call<JsonObject> getPMUDistWiseEventListRequest(@Body RequestBody params);

    @POST(APIServices.GP_VCRMC_MEMBER_LIST_URL)
    Call<JsonObject> getVCRMC_member_designation(@Body RequestBody params);

    // get PMU district wise Closed Scheduled event list
    @POST(APIServices.GET_PMU_DIST_CLOSED_EVENT_LIST_URL)
    Call<JsonObject> getPMUDistWiseClosedEventListRequest(@Body RequestBody params);

    // Get PMU Schedule Detail
    @POST(APIServices.GET_PMU_SCHEDULE_DETAIL_URL)
    Call<JsonObject> pmuGetEventDetailRequest(@Body RequestBody params);

    // Pmu Event Date list
    @POST(APIServices.PMU_EVENT_DATE_LIST_URL)
    Call<JsonObject> pmuEventDateListRequest(@Body RequestBody params);

    // Pmu Event Date list
    @POST(APIServices.PMU_ALL_EVENT_DATE_LIST_URL)
    Call<JsonObject> pmuAllEventDateListRequest(@Body RequestBody params);

    // Pmu Event Date list
    @POST(APIServices.GET_PMU_PARTICIPANT_LIST_URL)
    Call<JsonObject> getPmuParticipantListRequest(@Body RequestBody params);

    // PMU Event list by Date
    @POST(APIServices.PMU_EVENT_LIST_BY_DATE_URL)
    Call<JsonObject> pmuEventListByDateRequest(@Body RequestBody params);

    // PMU All Event list by Date
    @POST(APIServices.PMU_ALL_EVENT_LIST_BY_DATE_URL)
    Call<JsonObject> pmuAllEventListByDateRequest(@Body RequestBody params);

    // PMU Coming Event dist list
    @POST(APIServices.GET_PMU_COMING_EVENT_DIST_LIST_URL)
    Call<JsonObject> pmuComingEventDistListRequest(@Body RequestBody params);

    // PMU Closed Event dist list
    @POST(APIServices.GET_PMU_CLOSED_EVENT_DIST_LIST_URL)
    Call<JsonObject> pmuClosedEventDistListRequest(@Body RequestBody params);


// gauri

    // designation
    @POST(APIServices.GET_DESIGNATION_LIST_URL)
    Call<JsonObject> getDesignationRequest();



    /** OLD Request*/
    // Gram Panchayat List
    @POST(APIServices.GP_LIST_URL)
    Call<JsonObject> gpListRequest(@Body RequestBody params);

    // Save Schedule
    @POST(APIServices.SAVE_SCHEDULE_URL)
    Call<JsonObject> saveScheduleRequest(@Body RequestBody params);

    // get Schedule list
    @POST(APIServices.GET_SCHEDULE_URL)
    Call<JsonObject> getScheduleRequest(@Body RequestBody params);

    // Get Schedule Detail
    @POST(APIServices.GET_SCHEDULE_DETAIL)
    Call<JsonObject> getScheduleDetailRequest(@Body RequestBody params);

    // Update Schedule Detail
    @POST(APIServices.UPDATE_SCHEDULE_DETAIL)
    Call<JsonObject> updateScheduleDetailRequest(@Body RequestBody params);

    // get VCRMC Social category list
    /*@POST(APIServices.GET_VCRMC_SOCIAL_LIST)
    Call<JsonObject> getVCRMCSocCatListRequest(@Body RequestBody params);*/

    // Upload VCRMC Attendance Image
    @Multipart
    @POST(APIServices.UPLOAD_ATTENDANCE_IMAGE)
    Call<JsonObject> uploadAttendanceImageRequest(@Part MultipartBody.Part image,@PartMap Map<String, RequestBody> params);

    // Submit VCRMC Member attendance
    @POST(APIServices.SUBMIT_VCRMC_ATTENDANCE)
    Call<JsonObject> submitVCRMCMemberAttendanceRequest(@Body RequestBody params);

    // get Schedule History List
    @POST(APIServices.GET_SCHEDULE_HISTORY_LIST)
    Call<JsonObject> getScheduleHistoryListRequest(@Body RequestBody params);

    // to Send Reminder Request
    @POST(APIServices.SEND_REMINDER)
    Call<JsonObject> toSendReminderRequest(@Body RequestBody params);

    // to get Trainer List Request
    @POST(APIServices.GET_TRAINER_LIST)
    Call<JsonObject> toTrainerListRequest(@Body RequestBody params);

    //List of Activity
    @POST(APIServices.GET_LIST_OF_ACTIVITY)
    Call<JsonObject>getListOfActivityRequest(@Body RequestBody params);


}
