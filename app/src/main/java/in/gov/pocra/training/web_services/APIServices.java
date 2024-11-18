package in.gov.pocra.training.web_services;

public interface APIServices {
    /*****  production url start ******/
    /* SSO Service URL*/
//    String API_URL = "https://api-ffs.mahapocra.gov.in/v21/";
//    String API_VCRMC_URL = "https://api-ffs.mahapocra.gov.in/v22/";
//    String NOTIFICATION_API_URL = "https://api-sma.mahapocra.gov.in/v22/";
//    String SSO_API_URL = "https://sso.mahapocra.gov.in/";
//    String SSO_FARMER_API = "https://sso.mahapocra.gov.in/farmerService/village-activity-farmers";
//    String SHG_Group_API = "https://api-buildup.mahapocra.gov.in/v10/buildup/getList-shg";
//    String FPC_Group_API = "https://api-buildup.mahapocra.gov.in/v10/buildup/getList-fpc";
//    String farm_Group_API = "https://api-buildup.mahapocra.gov.in/v10/buildup/getList-farmers-group";
//    // DBT Farmer URL
//    String DBT_BASE_URL = "https://dbt.mahapocra.gov.in/";
//    String DBT_API_URL = DBT_BASE_URL +"Office/APPData/Reports.asmx/GetPreSanIssueListFFS";
//    //***** For Production ****//*
//    String OTHER_BASE_URL = "https://api-buildup.mahapocra.gov.in/v10/";                    // For Online
//    String BASE_URL = "https://api-buildup.mahapocra.gov.in/v10/buildup/";                    // For Online
//    String OFF_BASE_URL = "https://api-buildup.mahapocra.gov.in/v10/buildupOffline/";        // For Offline
//    //**** To get Component anc activity List *//*
//    String GET_COMPONENT_ACTIVITY_URL =  "https://sso.mahapocra.gov.in/farmerService/get-activities/";
//    /*****  production url End ******/


//    /*****  Test url start ******/
    String API_URL = "https://ilab-ffs-api.mahapocra.gov.in/v21/";
    String API_VCRMC_URL = "https://ilab-ffs-api.mahapocra.gov.in/v22/";
    String NOTIFICATION_API_URL = "https://ilab-sma-api.mahapocra.gov.in/v22/";

     String SSO_API_URL = "https://ilab-sso.mahapocra.gov.in/";
     String SSO_FARMER_API = "https://ilab-sso.mahapocra.gov.in/farmerService/village-activity-farmers";
    String SHG_Group_API = "https://ilab-training-api.mahapocra.gov.in/v10/buildup/getList-shg";
    String FPC_Group_API = "https://ilab-training-api.mahapocra.gov.in/v10/buildup/getList-fpc";
    String farm_Group_API = "https://ilab-training-api.mahapocra.gov.in/v10/buildup/getList-farmers-group";
    // DBT Farmer URL
    String DBT_BASE_URL = "https://dbt.mahapocra.gov.in/";
    String DBT_API_URL = DBT_BASE_URL +"Office/APPData/Reports.asmx/GetPreSanIssueListFFS";

    String OTHER_BASE_URL = "https://ilab-training-api.mahapocra.gov.in/v10/";                        //  For Online
    String BASE_URL = "https://ilab-training-api.mahapocra.gov.in/v10/buildup/";                  //  For Online
    String OFF_BASE_URL = "https://ilab-training-api.mahapocra.gov.in/v10/buildupOffline/";        // For Offline

    //**** To get Component anc activity List *//*
    String GET_COMPONENT_ACTIVITY_URL =  "https://ilab-sso.mahapocra.gov.in/farmerService/get-activities/";

//    /*****  Test url End ******/



    /*  Common  */
    String SERVICE_API_URL = SSO_API_URL+"userService/";
    String REFRESH_TOKEN_API_URL = SSO_API_URL+"authServicenew/refresh-token";// FOR SSO AUTH
   // String OTH_SERVICE_API_URL = SSO_API_URL+"authService/sso";// FOR SSO AUTH - old
    String OTH_SERVICE_NEW_API_URL = SSO_API_URL+"authServicenew/sso";// FOR SSO AUTH - new
    String check_USER_ACTIVE_DEACTIVE = SSO_API_URL+"authService/checkAppVersionLoggedDetails";

    String GET_APP_VERSION_URL = SSO_API_URL+"masterService/get-latest-apps-version-details";      // FOR get version

    String UPDATE_USER_TOKEN_URL = API_URL +"userService/push-notification-token-add-update";      // FOR Token Update

    //firebase
    String GET_NOTIFICATION_UNREAD_COUNT = NOTIFICATION_API_URL +"fcmService/get-Unread-notifications-count";    //NOTIFICATION COUNT
    String USER_NOTIFICATION_LIST = NOTIFICATION_API_URL +"fcmService/get-user-activities-on-notification";
    String READ_NOTIFICATION_MESSAGE = NOTIFICATION_API_URL +"fcmService/mark-notification-as-read-overview";

    //Switch Role
    String userdetails = "authService/userdetail";
    String USER_LOGOUT_URL = API_URL +"userService/push-notification-token-log-out";      // FOR Token Update

    /* PS-HRD */
    /***** Post Urls ****/

    // get Event Type List
    String EVENT_TYPE_LIST_URL = BASE_URL + "get-event-type";

    // get Event Type List
    String EVENT_SUB_TYPE_LIST_URL = BASE_URL + "get-eventTypeSubCategory";

    // get PS participants Group List
    String GET_PS_PARTICIPANTS_GROUP_URL = BASE_URL + "PS-Participants-Group-List";

    // get VCRMC (GP) member List
    String GP_MEM_LIST_URL = BASE_URL + "vcrmc-member-list";

    // All VCRMC (GP) member detail List
    String GP_MEM_DETAIL_LIST_URL = API_VCRMC_URL + "buildupService/grampanchayt-vcrmc-member";

    // For CA
    String GP_MEM_LIST_CA_URL = API_VCRMC_URL + "buildupService/grampanchayt-vcrmc-memberByCA-id";
    String GP_VCRMC_MEMBER_LIST_URL = API_VCRMC_URL + "buildupService/grampanchayt-vcrmc-memberByRoleId";

    // Get Village List
    String VILLAGE_LIST_URL = API_URL + "masterService/villages-by-taluka";

    // Get FARMER List
    // String FARMER_LIST_URL = API_URL + "buildupService/farmers-by-village";
    //  String FARMER_LIST_URL = BASE_URL + "get-farmers-list";
    String FARMER_LIST_URL = SSO_FARMER_API;
    String SHG_LIST_URL = SHG_Group_API;
    String FPC_LIST_URL = FPC_Group_API;
    String farm_LIST_URL = farm_Group_API;
    String FACILITATOR_LIST_URL = BASE_URL + "facilitator-list";

    // Get Role of Participants
    String ROLE_LIST_URL = SERVICE_API_URL + "roles";
///
    // Get Facilitator List
    // Get Role of PMU OFFICE STAFF
    String ROLE_PMU_OFFICE_STAFF_LIST_URL = SERVICE_API_URL + "roles-pmu-office-staff";

    // Get Role of PMU FIELD STAFF
    //String ROLE_PMU_FIELD_STAFF_LIST_URL = SERVICE_API_URL + "roles-pmu-field-staff";
    String ROLE_PMU_FIELD_STAFF_LIST_URL = BASE_URL + "roles-pmu-field-staff";
    // http://uat-api-buildup.mahapocra.gov.in/v5/buildup/roles-pmu-field-staff

    // Get Participants by role and location
    String GET_MEM_BY_ROLE_LOCATION_URL = SERVICE_API_URL + "users-by-role_location";

    // Add Other participants
    String ADD_OTHER_PARTICIPANTS_URL = BASE_URL + "add-other-participants";

    // Update Other participants
    String UPDATE_OTHER_PARTICIPANTS_URL = BASE_URL + "add-other-participants";

    // Get Other participants
    String GET_OTHER_PARTICIPANTS_URL = BASE_URL + "get-other-participants";

    // get Resource person List
    String RES_PER_LIST_URL = BASE_URL + "get-resource-person";

    // Add Resource Person
    String ADD_RES_PERSON_URL = BASE_URL + "add-resource-person";

    // get Coordinator List
    String CORD_LIST_URL = BASE_URL + "coordinator-list";
    // String CORD_LIST_URL = BASE_URL + "coordinator-list-test";      // For testing purpose

    // get Official Coordinator List
    String OFFICIAL_CORD_LIST_URL = BASE_URL + "officials-coordinator";

    // get Coordinator DESIGNATION List
    String GET_CORD_DESIG_LIST_URL = API_URL + "buildupService/get-designation";

    // get Venue List
    String GET_VENUE_LIST_URL = BASE_URL + "get-venue-master-list";

    // get KVK List
    String GET_KVK_LIST_URL = BASE_URL + "get-kvk-list";

    // Ps-Hrd Member Attend Detail
    String PS_MEM_ATTEND_DETAIL_URL = BASE_URL + "get-member-attendance-detail";

    // Create Ps-Hrd Schedule
    String CREATE_PS_SCHEDULE_URL = BASE_URL + "create-schedule";

    // Update Ps-Hrd Schedule
    String UPDATE_PS_SCHEDULE_URL = BASE_URL + "update-schedule";

    // get Ps-Hrd Scheduled list
    String GET_PS_SCHEDULE_URL = BASE_URL + "get-schedule";

    // get Ps-Hrd Scheduled list
    String GET_PS_SCHEDULE_DIST_ID_URL = BASE_URL + "get-schedule-PSHRD-Dist-list";

    // get Ps-Hrd Scheduled Detail
    String GET_PS_SCHEDULE_DETAIL_URL = BASE_URL + "get-schedule-details";

    // get Event Cancel Reason
    String EVENT_CANCEL_REASON_URL = BASE_URL + "cancellation-reasons";

    // Ps-Hrd Cancel Schedule
    String PS_SCHEDULE_CANCEL_URL = BASE_URL + "cancel-schedule";

    // Ps-Hrd Closed Event List
    String PS_CLOSED_EVENT_LIST_URL = BASE_URL + "get-closed-event-list-to-PSHRD";

    // Ps-Hrd PMU Closed Event List
    String PS_PMU_CLOSED_EVENT_LIST_URL = BASE_URL + "get-closed-event-list-to-PSHRD-PMU";

    // Ps-Hrd Event date List
    String PS_EVENT_DATE_LIST_URL = BASE_URL + "get-schedule-date";

    // Ps-Hrd Event date by dist id List
    String PS_EVENT_DATE_BY_DIST_LIST_URL = BASE_URL + "get-schedule-date";

    // Ps-Hrd Event list by date
    String PS_EVENT_LIST_BY_DATE_URL = BASE_URL + "get-schedule-list-by-date";



    // For CA

    // Create Ps-Hrd Schedule
    String  CREATE_CA_SCHEDULE_URL = BASE_URL + "create-schedule-ca";

    // Create Ps-Hrd Schedule
    String UPDATE_CA_SCHEDULE_URL = BASE_URL + "update-schedule-ca";

    // Create Ps-Hrd Schedule
    String GET_CA_SCHEDULE_URL = BASE_URL + "get-ca-schedule";

    // Create Ps-Hrd Schedule
    String GET_CA_TALUKA_URL = API_VCRMC_URL + "masterService/get-taluka-id-by-CA-ID";
    // http://api-ffs.mahapocra.gov.in/v22/masterService/get-taluka-id-by-CA-ID

    // CA Schedule Detail
    String GET_CA_SCHEDULE_DETAIL_URL = BASE_URL + "get-ca-schedule-details";

    // Subdivision list with event count
    String GET_CA_SUB_DIV_LIST_URL = BASE_URL + "districts-ca-upcomming-event-count";

    String GET_CA_CLOSED_EVENT_SUB_DIV_LIST_URL = BASE_URL + "sub-division-ca-closed-event-count";

    // CA closed Event list
    String CA_CLOSED_EVENT_LIST_URL = BASE_URL + "get-closed-event-list-to-CA";

    // CA all closed Event list
    String CA_ALL_CLOSED_EVENT_LIST_URL = BASE_URL + "get-closed-event-list-to-CA-PSHRD-PMU";

    // get PS participants Group List
    String GET_CA_PARTICIPANTS_GROUP_URL = BASE_URL + "PS-Participants-Group-List-CA";


    // Add CA labour
    String ADD_CA_LABOUR_URL = SSO_API_URL + "agrilabour/add-agri-labour-details";
    // Get CA labour
    String GET_CA_LABOUR_URL = SSO_API_URL + "agrilabour/get-agri-labour-details";

    // Add CA Resource Person
    String ADD_CA_RESOURCE_P_URL = OTHER_BASE_URL + "othercontroller/add-resource-person-details ";
    // Get CA Resource Person
    String GET_CA_RESOURCE_P_URL = OTHER_BASE_URL + "othercontroller/get-resource-person-details";

    //
    // get CA Resource Person List
    String GET_CA_GET_RESOURCE_PERSON_URL = "http://uat-api-buildup.mahapocra.gov.in/v10/othercontroller/get-resource-person-details";


    /***COORDINATOR APIS*/

    /***** Post Urls ****/

    // Online

    // get Event Type List
    String CORD_EVENT_LIST_URL = BASE_URL + "schedule-list-to-coordinator";

    // get Event Group List
    String CORD_EVENT_GROUP_LIST_URL = BASE_URL + "EventParticipantsGroupList";

    // get Event Group Member List
    String CORD_EVENT_GROUP_MEM_LIST_URL = BASE_URL + "EventParticipantsMemberList";

    // Add Other Group Member
    String ADD_OTHER_GROUP_MEM_URL = BASE_URL + "add-other-person";

    // Get Other Group Member list
    String GET_OTHER_GROUP_MEM_LIST_URL = BASE_URL + "get-other-person";

    // Get Other group member detail
    String GET_OTHER_GROUP_MEM_DETAIL_URL = BASE_URL + "other-person-details";

    // Update Other group member detail
    String UPDATE_OTHER_GROUP_MEM_DETAIL_URL = BASE_URL + "update-other-person";

    // Submit member attendance against Day
    String SUBMIT_MEM_ATTEND_OF_DAY_URL = BASE_URL + "save-attendance";

    //  Upload Attendance Image of a day
    String UPLOAD_ATTEND_IMAGE = BASE_URL + "upload-attendance-img";

    //  Get attend Image list of a day
    String GET_ATTEND_IMAGE_LIST = BASE_URL + "get-attendance-img";

    // Event Collage PDF URL
    String EVENT_COLLAGE_PDF_URL = BASE_URL + "get-consolidated-collage-pdf";


    // Event closer URL
    String EVENT_CLOSER_URL = BASE_URL + "event-closer";

    // get Closed Event list URL
    String GET_CLOSED_EVENT_LIST_URL = BASE_URL + "get-closed-event-list";

    // get Day Attendance Report URL
    String GET_ATTENDANCE_REPORT_URL = BASE_URL + "get-attendance-report";

    // get consolidated Report URL
    String GET_CONSOLIDATED_REPORT_URL = BASE_URL + "get-consolidated-attendance-report";

    // get consolidated Report with collage URL
    String GET_CONSOLIDATED_REPORT_COLLAGE_URL = BASE_URL + "get-consolidated-attendance-report-collage";

    // Add Session URL
    String ADD_SESSION_URL = BASE_URL + "add-session";

    // Get Session list URL
    String GET_SESSION_LIST_URL = BASE_URL + "get-session-list";

    // Delete Session URL
    String DELETE_SESSION_URL = BASE_URL + "delete-evnt-session";

    // Offline
    // Get a Event Detail URL
    String OFF_EVENT_DETAIL_URL = OFF_BASE_URL + "get-event-details";




    // PMU APIs

    /***** Post Urls ****/

    // Create PMU Schedule
    String CREATE_PMU_SCHEDULE_URL = BASE_URL + "create-schedule-pmu";

    // Update PMU Schedule
    String UPDATE_PMU_SCHEDULE_URL = BASE_URL + "update-schedule-pmu";

    // PMU Event date List
    String PMU_EVENT_DATE_LIST_URL = BASE_URL + "get-pmu-schedule-date";

    // PMU aLL Event date List
    String PMU_ALL_EVENT_DATE_LIST_URL = BASE_URL + "get-pmu-schedule-date-all";

    // PMU Event list by date
    String PMU_EVENT_LIST_BY_DATE_URL = BASE_URL + "get-pmu-schedule-list-by-date";

    // PMU Event list by date
    String PMU_ALL_EVENT_LIST_BY_DATE_URL = BASE_URL + "get-pmu-schedule-list-by-all-date";

    // get PMU participants Group List
    String GET_PMU_PARTICIPANTS_GROUP_URL = BASE_URL + "PS-Participants-Group-List-PMU";

    // Get Event list by URL
    String GET_PMU_EVENT_LIST_URL = BASE_URL + "get-schedule";

    // Get Event list by distId URL
    // String GET_PMU_DIST_EVENT_LIST_URL = BASE_URL + "get-pmu-schedule";
    String GET_PMU_DIST_EVENT_LIST_URL = BASE_URL + "get-pmu-schedule-new";

    // get PMU Scheduled Detail
    String GET_PMU_SCHEDULE_DETAIL_URL = BASE_URL + "get-pmu-schedule-details";

    // Get closed Event list by distId URL
    //String GET_PMU_DIST_CLOSED_EVENT_LIST_URL = BASE_URL + "get-pmu-closed-event-list-to-PSHRD";
    String GET_PMU_DIST_CLOSED_EVENT_LIST_URL = BASE_URL + "get-pmu-closed-event-list-to-PSHRD-new";

    // Pmu Event date List
    String GET_PMU_PARTICIPANT_LIST_URL = BASE_URL + "pmu-participant-list";

    // Pmu Coming Event dist List
    String GET_PMU_COMING_EVENT_DIST_LIST_URL = BASE_URL + "districts-pmu-upcomming-event-count";

    // Pmu Closed Event dist List
    String GET_PMU_CLOSED_EVENT_DIST_LIST_URL = BASE_URL + "districts-pmu-closed-event-count";

    // vcrmc designation
    String GET_DESIGNATION_LIST_URL = API_URL + "buildupService/get-vcrmc-designation";

    /***** Post Urls ****/
    // Login
//    String CA_OAUTH_URL = API_URL + "authService/sso"; // old
    String CA_OAUTH_URL = API_URL + "authService/ffsmobile"; // new

    // GP List
    String GP_LIST_URL = API_URL + "masterService/get-grampanchayt-by-taluka";

    // Forgot Password otp
    String GET_FORGOT_PASS_OTP = SSO_API_URL + "authService/forgot-password-send-otp";

    // Resend Forgot Password otp
    String RESEND_FORGOT_PASS_OTP = SSO_API_URL + "authService/resend-otp";

    // Reset Password
    String RESET_PASS = SSO_API_URL + "authService/reset-password";

    // Update Profile detail
    String UPDATE_USER_PROFILE = SSO_API_URL + "userService/update-profile";

    // Update Profile image
    String UPDATE_USER_PROFILE_IMAGE = SSO_API_URL + "userService/update-profile-picture";

    // get VCRMC social category
    String GET_VCRMC_SOCIAL_LIST = API_URL + "userService/social-categories";    // It is in Training



    /***** OLD API ****/
    String TR_API_URL = "http://13.232.240.52/training/trainingapi/";
    // http://13.232.240.52/training/trainingapi/save_schedule


    // Save Schedule
    String SAVE_SCHEDULE_URL = TR_API_URL + "save_schedule";

    // get Schedule list
    String GET_SCHEDULE_URL = TR_API_URL+"schedulelist";

    // Get Schedule Detail
    String GET_SCHEDULE_DETAIL = TR_API_URL + "getschedule";

    // Update Schedule Detail
    String UPDATE_SCHEDULE_DETAIL = TR_API_URL + "edit_schedule";

    // get GP and officer List for Attendance
    String GET_ATTENDANCE_GP_LIST = TR_API_URL + "vcrmc-list";

    // get VCRMC member Detail List for Attendance
    String GET_VCRMC_MEMBER_ATTENDANCE_DETAIL_LIST = TR_API_URL + "vcrmc-memberlist";

    // add/edit VCRMC member detail
    String ADD_EDIT_VCRMC_MEM_DETAIL = TR_API_URL + "add-edit-vcrmcmember";

    // upload attendance image
    String UPLOAD_ATTENDANCE_IMAGE = TR_API_URL + "upload-file";

    // Submit attendance image
    String SUBMIT_VCRMC_ATTENDANCE = TR_API_URL + "attendance";

    // Submit final attendance image
    String FINAL_ATTENDANCE = TR_API_URL + "final-attendance";

    // get Schedule history List
    String GET_SCHEDULE_HISTORY_LIST = TR_API_URL + "scedule-history";

    // to send Reminder
    String SEND_REMINDER = TR_API_URL + "send-reminder";

    // to get Trainer List
    String GET_TRAINER_LIST = TR_API_URL + "get_trainer1";


    /****** Get Urls ******/

    // TO get District,Sub Division, Taluka and GP

    // common get API Api
    String C_API_URL = API_URL+"masterService/";

    // get District
    String GET_DIST_URL = C_API_URL + "districts";

    // get All District List
    String GET_ALL_DIST_URL = BASE_URL + "all_districts";

    // get Sub division
    String GET_SUB_DIV_URL = C_API_URL + "subdivisions/";

    // get Sub division with Upcoming Event count
    String GET_SUB_DIV_E_U_COUNT_URL = BASE_URL + "sub-division-ca-upcomming-event-count-new";

    // get Sub division with Closed Event count
    String GET_SUB_DIV_E_C_COUNT_URL = BASE_URL + "sub-division-ca-closed-event-count-new";


    // get Taluka
    String GET_TALUKA_URL = C_API_URL + "talukas-by-subdivision/";

    // get Trainer List
    String GET_TRAINER_URL = TR_API_URL+"get_trainerlist/";

    // for list of Activity
    String GET_LIST_OF_ACTIVITY = BASE_URL +"get-activity/";

}
