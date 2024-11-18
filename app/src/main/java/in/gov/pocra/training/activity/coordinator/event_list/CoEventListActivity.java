package in.gov.pocra.training.activity.coordinator.event_list;

import android.content.DialogInterface;
import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import in.co.appinventor.services_api.api.AppinventorApi;
import in.co.appinventor.services_api.app_util.AppUtility;
import in.co.appinventor.services_api.debug.DebugLog;
import in.co.appinventor.services_api.listener.ApiCallbackCode;
import in.co.appinventor.services_api.listener.OnMultiRecyclerItemClickListener;
import in.co.appinventor.services_api.settings.AppSettings;
import in.co.appinventor.services_api.util.Utility;
import in.co.appinventor.services_api.widget.UIToastMessage;
import in.gov.pocra.training.R;
import in.gov.pocra.training.event_db.CordOfflineDBase;
import in.gov.pocra.training.model.offline.OffCoordinatorDetailModel;
import in.gov.pocra.training.model.offline.OffFarmerDetailModel;
import in.gov.pocra.training.model.offline.OffGPMemDetailModel;
import in.gov.pocra.training.model.offline.OffResPersonDetailModel;
import in.gov.pocra.training.model.offline.OffSchDetailModel;
import in.gov.pocra.training.model.online.ResponseModel;
import in.gov.pocra.training.util.ApConstants;
import in.gov.pocra.training.web_services.APIRequest;
import in.gov.pocra.training.web_services.APIServices;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Retrofit;


public class CoEventListActivity extends AppCompatActivity implements ApiCallbackCode, OnMultiRecyclerItemClickListener {


    private ImageView homeBack;
    private RecyclerView recViewEvent;
    private String roleId;
    private String userID;
    private ImageView onOffImageView;
    private String onlineStatus;

    // For Offline
    private CordOfflineDBase cDBase;
    private JSONArray scheduleJSONArray;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_co);

        /* ** For actionbar title in center ***/
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.attendance_actionbar_layout);
        AppCompatTextView actionTitleTextView = getSupportActionBar().getCustomView().findViewById(R.id.actionTitleTextView);
        homeBack = getSupportActionBar().getCustomView().findViewById(R.id.backImageView);
        onOffImageView = getSupportActionBar().getCustomView().findViewById(R.id.addPersonImageView);

        // onOffImageView.setColorFilter(getResources().getColor(R.color.transparent), android.graphics.PorterDuff.Mode.MULTIPLY);
        homeBack.setVisibility(View.VISIBLE);
        onOffImageView.setVisibility(View.GONE);
        actionTitleTextView.setText(getResources().getString(R.string.title_event));

        cDBase = new CordOfflineDBase(this);
        initialization();
        defaultConfiguration();
    }
    private void initialization() {
        // For Action Bar
        homeBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        // To get User Id and Roll Id
        String rId = AppSettings.getInstance().getValue(this, ApConstants.kROLE_ID, ApConstants.kROLE_ID);
        String uId = AppSettings.getInstance().getValue(this, ApConstants.kUSER_ID, ApConstants.kUSER_ID);
        if (!rId.equalsIgnoreCase("kROLE_ID")) {
            roleId = rId;
        }
        if (!uId.equalsIgnoreCase("kUSER_ID")) {
            userID = uId;
        }

        recViewEvent = findViewById(R.id.recViewEvent);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recViewEvent.setLayoutManager(linearLayoutManager);
    }


    @Override
    protected void onResume() {
        super.onResume();

        onlineStatus = AppSettings.getInstance().getValue(CoEventListActivity.this, ApConstants.kONLINE_STATUS, ApConstants.kONLINE_STATUS);
        if (onlineStatus.equalsIgnoreCase("kONLINE_STATUS")) {
            AppSettings.getInstance().setValue(CoEventListActivity.this, ApConstants.kONLINE_STATUS, ApConstants.kONLINE);
            onlineStatus = ApConstants.kONLINE;
            onOffImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_online));
            getScheduleList();

        } else {

            if (onlineStatus.equalsIgnoreCase(ApConstants.kONLINE)) {
                onOffImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_online));
                getScheduleList();
            } else if (onlineStatus.equalsIgnoreCase(ApConstants.kOFFLINE)) {
                onOffImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_offline));
                if (Utility.checkConnection(this)) {
                    getScheduleList();
                } else {
                    getOffScheduleList();
                }
            }
        }
    }


    private void defaultConfiguration() {

        onOffImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onlineStatus = AppSettings.getInstance().getValue(CoEventListActivity.this, ApConstants.kONLINE_STATUS, ApConstants.kONLINE_STATUS);

                if (onlineStatus.equalsIgnoreCase(ApConstants.kONLINE)) {

                    onOffImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_offline));
                    AppSettings.getInstance().setValue(CoEventListActivity.this, ApConstants.kONLINE_STATUS, ApConstants.kOFFLINE);

                    if (AppUtility.getInstance().isConnected(CoEventListActivity.this)) {
                        getScheduleList();
                    } else {
                        getOffScheduleList();
                    }

                } else if (onlineStatus.equalsIgnoreCase(ApConstants.kOFFLINE)) {

                    onOffImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_online));
                    AppSettings.getInstance().setValue(CoEventListActivity.this, ApConstants.kONLINE_STATUS, ApConstants.kONLINE);
                    getScheduleList();

                }

            }
        });
    }


    @Override
    public void onMultiRecyclerViewItemClick(int i, Object o) {

        if (o != null) {
            JSONObject jsonObject = (JSONObject) o;
            try {
                String schId = jsonObject.getString("schedule_id").trim();

                /*if (i == 2) {   // for upload
                    syncEventOffDetail(schId);
                } else*/
                if (i == 3) {    // For delete
                    deleteEventOffDetail();
                } else if (i == 1) {
                    // To check other event data are available in DB table
                    long memCount = cDBase.getNoOfRecord(CordOfflineDBase.EVENT_DETAIL_TABLE);
                    if (memCount > 0) {
                        Toast.makeText(this, "Please Sync/delete previous offline detail", Toast.LENGTH_SHORT).show();
                       // UIToastMessage.show(this, "Please Sync/delete previous offline detail");
                    } else {
                        saveEventListIntoDb();
                        getEventOffDetail(schId);
                    }

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    private void saveEventListIntoDb() {

        if (scheduleJSONArray != null) {

            for (int i = 0; i < scheduleJSONArray.length(); i++) {

                JSONObject jsonObject = null;
                try {

                    jsonObject = scheduleJSONArray.getJSONObject(i);
                    String schIc = jsonObject.getString("schedule_id");
                    String cordId = jsonObject.getString("coordinator_id");
                    String cordRollId = jsonObject.getString("coordinator_role_id");
                    String type = jsonObject.getString("type");
                    String title = jsonObject.getString("title");
                    String participants = jsonObject.getString("participints");
                    String venue = jsonObject.getString("venue");
                    String start_date = jsonObject.getString("start_date");
                    String end_date = jsonObject.getString("end_date");
                    String closer = jsonObject.getString("is_event_closer");

                    cDBase.insertEventList(schIc, cordId, cordRollId, type, title, start_date, end_date, venue, participants, closer);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    // To get Online event List
    private void getScheduleList() {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("coordinator_id", userID);
            jsonObject.put("api_key", ApConstants.kAUTHORITY_KEY);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString());
        AppinventorApi api = new AppinventorApi(this, APIServices.BASE_URL, "", ApConstants.kMSG, true);
        Retrofit retrofit = api.getRetrofitInstance();
        APIRequest apiRequest = retrofit.create(APIRequest.class);
        Call<JsonObject> responseCall = apiRequest.cordEventListRequest(requestBody);

        DebugLog.getInstance().d("get_Schedule_param=" + responseCall.request().toString());
        DebugLog.getInstance().d("get_Schedule_param=" + AppUtility.getInstance().bodyToString(responseCall.request()));
        api.postRequest(responseCall, this, 1);

    }


    /****** Offline *******/

    // To Sync Event detail from online
    private void getEventOffDetail(String schId) {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("schedule_id", schId);
            jsonObject.put("api_key", ApConstants.kAUTHORITY_KEY);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString());
        AppinventorApi api = new AppinventorApi(this, APIServices.OFF_BASE_URL, "", ApConstants.kMSG, true);
        Retrofit retrofit = api.getRetrofitInstance();
        APIRequest apiRequest = retrofit.create(APIRequest.class);
        Call<JsonObject> responseCall = apiRequest.offEventDetailRequest(requestBody);

        DebugLog.getInstance().d("get_off_event_detail_param=" + responseCall.request().toString());
        DebugLog.getInstance().d("get_off_event_detail_param=" + AppUtility.getInstance().bodyToString(responseCall.request()));
        api.postRequest(responseCall, this, 2);

    }


    // To save event detail into device database
    private void saveEventDetailIntoDb(JSONArray eventDetail) {

        try {

            if (eventDetail.length() > 0) {
                JSONObject eventJson = eventDetail.getJSONObject(0);
                OffSchDetailModel offSchDetailModel = new OffSchDetailModel(eventJson);
                String schId = offSchDetailModel.getEvent_id();
                String eventTypeId = offSchDetailModel.getEvent_type();
                String eventTypeName = offSchDetailModel.getEvent_type_name();
                String eventTitle = offSchDetailModel.getEvent_title();
                String eventParticipant = offSchDetailModel.getEvent_participints();
                String eventSDate = offSchDetailModel.getEvent_start_date();
                String eventEDate = offSchDetailModel.getEvent_end_date();
                String eventVenue = offSchDetailModel.getEvent_venue();

                JSONArray eventGpMem = offSchDetailModel.getEvent_gp();
                JSONArray eventVillageMem = offSchDetailModel.getEvent_village();
                JSONArray eventResPerson = offSchDetailModel.getEvent_resource_person();
                JSONArray eventCordMem = offSchDetailModel.getEvent_coordinators();


                if (eventGpMem.length() > 0) {
                    boolean isGpInserted = false;

                    String groupType = "gp";
                    for (int g = 0; g < eventGpMem.length(); g++) {
                        JSONObject gpMemJSON = eventGpMem.getJSONObject(g);
                        OffGPMemDetailModel GPDModel = new OffGPMemDetailModel(gpMemJSON);
                        String GpMemId = GPDModel.getGp_mem_id();
                        String group_id = GPDModel.getGp_id();
                        String groupName = GPDModel.getGp_name();
                        String GpMemFName = GPDModel.getGp_mem_first_name();
                        String GpMemMName = GPDModel.getGp_mem_middle_name();
                        String GpMemLName = GPDModel.getGp_mem_last_name();
                        String GpMemGender = GPDModel.getGp_mem_gender();
                        String GpMemDesig = GPDModel.getGp_mem_designation();
                        String GpMemMobile = GPDModel.getGp_mem_mobile();

                        isGpInserted = cDBase.insertEventDetail(schId, userID, roleId, eventSDate, eventEDate, eventVenue, eventTypeId,
                                eventTypeName, eventTitle, eventParticipant, groupType, group_id, groupName, GpMemId, GpMemFName, GpMemMName, GpMemLName,
                                GpMemMobile, GpMemGender, GpMemDesig);

                    }

                    if (isGpInserted) {

                        boolean isVillageInserted = false;

                        groupType = "village";
                        int vm = eventVillageMem.length();
                        for (int v = 0; v < vm; v++) {
                            JSONObject villageMemJSON = eventVillageMem.getJSONObject(v);
                            OffFarmerDetailModel FDModel = new OffFarmerDetailModel(villageMemJSON);

                            String memId = FDModel.getMem_id();
                            String group_id = FDModel.getGroup_id();
                            String groupName = FDModel.getGroup_name();
                            String memFName = FDModel.getMem_first_name();
                            String memMName = FDModel.getMem_middle_name();
                            String memLName = FDModel.getMem_last_name();
                            String memGender = FDModel.getMem_gender();
                            String memDesig = FDModel.getMem_designation();
                            String memMobile = FDModel.getMem_mobile();

                            isVillageInserted = cDBase.insertEventDetail(schId, userID, roleId, eventSDate, eventEDate, eventVenue, eventTypeId,
                                    eventTypeName, eventTitle, eventParticipant, groupType, group_id, groupName, memId, memFName, memMName, memLName,
                                    memMobile, memGender, memDesig);

                        }

                        if (isVillageInserted) {
                            boolean isResPerInserted = false;

                            groupType = "resource_person";
                            int rm = eventResPerson.length();
                            for (int r = 0; r < rm; r++) {
                                JSONObject resPerMemJSON = eventResPerson.getJSONObject(r);
                                OffResPersonDetailModel rPerModel = new OffResPersonDetailModel(resPerMemJSON);

                                String memId = rPerModel.getMem_id();
                                String group_id = rPerModel.getGroup_id();
                                String groupName = rPerModel.getGroup_name();
                                String memFName = rPerModel.getMem_first_name();
                                String memMName = rPerModel.getMem_middle_name();
                                String memLName = rPerModel.getMem_last_name();
                                String memGender = rPerModel.getMem_gender();
                                String memDesig = rPerModel.getMem_designation();
                                String memMobile = rPerModel.getMem_mobile();

                                isResPerInserted = cDBase.insertEventDetail(schId, userID, roleId, eventSDate, eventEDate, eventVenue, eventTypeId,
                                        eventTypeName, eventTitle, eventParticipant, groupType, group_id, groupName, memId, memFName, memMName, memLName,
                                        memMobile, memGender, memDesig);

                            }

                            if (isResPerInserted) {

                                boolean isCordInserted = false;

                                groupType = "coordinators";
                                int cm = eventCordMem.length();
                                for (int c = 0; c < cm; c++) {
                                    JSONObject cordMemJSON = eventCordMem.getJSONObject(c);
                                    OffCoordinatorDetailModel cordModel = new OffCoordinatorDetailModel(cordMemJSON);

                                    String memId = cordModel.getMem_id();
                                    String group_id = cordModel.getGroup_id();
                                    String group_name = cordModel.getGroup_name();
                                    String memFName = cordModel.getMem_first_name();
                                    String memMName = cordModel.getMem_middle_name();
                                    String memLName = cordModel.getMem_last_name();
                                    String memGender = cordModel.getMem_gender();
                                    String memDesig = cordModel.getMem_designation();
                                    String memMobile = cordModel.getMem_mobile();

                                    isCordInserted = cDBase.insertEventDetail(schId, userID, roleId, eventSDate, eventEDate, eventVenue, eventTypeId,
                                            eventTypeName, eventTitle, eventParticipant, groupType, group_id, group_name, memId, memFName, memMName, memLName,
                                            memMobile, memGender, memDesig);

                                }

                                if (isCordInserted) {

                                    boolean isOtherInserted = false;

                                    groupType = "other";

                                    String memId = "";
                                    String group_id = "9999999";
                                    String group_name = "Other";
                                    String memFName = "";
                                    String memMName = "";
                                    String memLName = "";
                                    String memGender = "";
                                    String memDesig = "";
                                    String memMobile = "";

                                    isOtherInserted = cDBase.insertEventDetail(schId, userID, roleId, eventSDate, eventEDate, eventVenue, eventTypeId,
                                            eventTypeName, eventTitle, eventParticipant, groupType, group_id, group_name, memId, memFName, memMName, memLName,
                                            memMobile, memGender, memDesig);



                                    if (isOtherInserted){
                                        if (Utility.checkConnection(this)) {
                                            getScheduleList();
                                        } else {
                                            getOffScheduleList();
                                        }
                                        Toast.makeText(this, "Event data saved successfully", Toast.LENGTH_SHORT).show();
                                        //UIToastMessage.show(this, "Event data saved successfully");
                                    }

                                }

                            }

                        }

                    }

                }

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    // To get Offline event List
    private void getOffScheduleList() {

        JSONArray scheduleJSONArray = cDBase.getEventListByUserId(userID);

        if (scheduleJSONArray.length() > 0) {
            AdaptorEventList adaptorEventList = new AdaptorEventList(this, scheduleJSONArray, this);
            recViewEvent.setAdapter(adaptorEventList);
        } else {
            Toast.makeText(this, "No event list found in offline", Toast.LENGTH_SHORT).show();
            //UIToastMessage.show(this, "No event list found in offline");
            AdaptorEventList adaptorEventList = new AdaptorEventList(this, new JSONArray(), this);
            recViewEvent.setAdapter(adaptorEventList);
        }
    }

    // To sync event detail
    private void syncEventOffDetail(String schId) {


    }
    // To delete all offline event detail
    private void deleteEventOffDetail() {
        forCloserUserPermission();
    }


    // To take user permission to closer of event
    private void forCloserUserPermission() {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("If you synced your detail online then press Yes. \nOtherwise you lost your offline data. \nAre you sure want to delete..?");
        alertDialogBuilder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                cDBase.deleteAllData();
                if (AppUtility.getInstance().isConnected(CoEventListActivity.this)) {
                    getScheduleList();
                } else {
                    getOffScheduleList();
                }
            }
        });

        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }
    @Override
    public void onResponse(JSONObject jsonObject, int i) {
        try {
            if (jsonObject != null) {
                // Schedule Response for CA/Trainer
                if (i == 1) {
                    ResponseModel responseModel = new ResponseModel(jsonObject);
                    if (responseModel.isStatus()) {
                        scheduleJSONArray = jsonObject.getJSONArray("data");
                        Log.d("EventList","EventList==="+scheduleJSONArray.length());

                        if (scheduleJSONArray.length() > 0) {
                            AdaptorEventList adaptorEventList = new AdaptorEventList(this, scheduleJSONArray, this);
                            recViewEvent.setAdapter(adaptorEventList);
                        } else {
                            Toast.makeText(this, ""+responseModel.getMsg(), Toast.LENGTH_SHORT).show();
                            //UIToastMessage.show(this, responseModel.getMsg());
                        }

                    } else {
                        Toast.makeText(this, ""+responseModel.getMsg(), Toast.LENGTH_SHORT).show();
                        //UIToastMessage.show(this, responseModel.getMsg());
                    }
                }
                // For get event detail for offline
                if (i == 2) {
                    ResponseModel responseModel = new ResponseModel(jsonObject);
                    if (responseModel.isStatus()) {
                        JSONArray eventDetailArray = jsonObject.getJSONArray("data");

                        saveEventDetailIntoDb(eventDetailArray);

                    } else {
                        Toast.makeText(this, ""+responseModel.getMsg(), Toast.LENGTH_SHORT).show();
                       // UIToastMessage.show(this, responseModel.getMsg());
                    }


                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onFailure(Object o, Throwable throwable, int i) {

    }


}
