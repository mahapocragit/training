package in.gov.pocra.training.activity.pmu.pmu_upcoming_event;

import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import in.co.appinventor.services_api.api.AppinventorApi;
import in.co.appinventor.services_api.app_util.AppUtility;
import in.co.appinventor.services_api.debug.DebugLog;
import in.co.appinventor.services_api.listener.ApiCallbackCode;
import in.co.appinventor.services_api.listener.OnMultiRecyclerItemClickListener;
import in.co.appinventor.services_api.settings.AppSettings;
import in.co.appinventor.services_api.widget.UIToastMessage;
import in.gov.pocra.training.R;
import in.gov.pocra.training.activity.ca.ca_upcoming_event.CaComingEventActivity;
import in.gov.pocra.training.model.online.ProfileModel;
import in.gov.pocra.training.model.online.ResponseModel;
import in.gov.pocra.training.util.ApConstants;
import in.gov.pocra.training.web_services.APIRequest;
import in.gov.pocra.training.web_services.APIServices;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Retrofit;

public class PmuComingEventActivity extends AppCompatActivity implements ApiCallbackCode, OnMultiRecyclerItemClickListener {

    private ImageView homeBack;
    private String eventType;
    private RecyclerView pmuComingEventRView;
    private String roleId;
    private String userID;
    private RelativeLayout searchRLayout;
    private EditText searchEText;
    private JSONArray scheduleJSONArray = null;
    private String loginType;
    private JSONArray eventByDistJSONArray;
    private JSONArray eventBySubDivJSONArray;
    private AdaptorPmuComingEvent adaptorPmuComingEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pmu_coming_event);

        /* ** For actionbar title in center ***/
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.attendance_actionbar_layout);
        AppCompatTextView actionTitleTextView = (AppCompatTextView) getSupportActionBar().getCustomView().findViewById(R.id.actionTitleTextView);
        homeBack = (ImageView) getSupportActionBar().getCustomView().findViewById(R.id.backImageView);
        // addPersonImageView = (ImageView) getSupportActionBar().getCustomView().findViewById(R.id.addPersonImageView);
        homeBack.setVisibility(View.VISIBLE);
        // addPersonImageView.setVisibility(View.INVISIBLE);
        actionTitleTextView.setText(getResources().getString(R.string.title_event));

        String type = AppSettings.getInstance().getValue(this, ApConstants.kLOGIN_TYPE, ApConstants.kLOGIN_TYPE);
        if (!type.equalsIgnoreCase("kLOGIN_TYPE")) {
            loginType = type;
        }

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
        eventType = getIntent().getStringExtra("eventType");

        // To get User Id and Roll Id
        String rId = AppSettings.getInstance().getValue(this, ApConstants.kROLE_ID, ApConstants.kROLE_ID);
        String uId = AppSettings.getInstance().getValue(this, ApConstants.kUSER_ID, ApConstants.kUSER_ID);
        if (!rId.equalsIgnoreCase("kROLE_ID")) {
            roleId = rId;
        }
        if (!uId.equalsIgnoreCase("kUSER_ID")) {
            userID = uId;
        }
        searchEText = findViewById(R.id.searchEText);
        searchRLayout = findViewById(R.id.searchRLayout);
        pmuComingEventRView = (RecyclerView) findViewById(R.id.pmuComingEventRView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        pmuComingEventRView.setLayoutManager(linearLayoutManager);
    }


    @Override
    protected void onResume() {
        super.onResume();

        getScheduleListByEventType();
    }

    private void defaultConfiguration() {

        if (eventType.equalsIgnoreCase("kS_ALL_EVENT")) {
            String center = getIntent().getStringExtra("level");
            if (center.equalsIgnoreCase("Pmu")) {
                getAllPmuScheduledEventList(center, "");
            } else if (center.equalsIgnoreCase("District")) {
                String distId = getIntent().getStringExtra("distId");
                if (!distId.equalsIgnoreCase("")) {
                    getAllPmuScheduledEventList(center, distId);
                } else {
                    UIToastMessage.show(this, "District id not found");
                }
            } else if (center.equalsIgnoreCase("Subdivision")) {
                // UIToastMessage.show(this,"Coming Soon");
                String distId = getIntent().getStringExtra("distId");
                String subDivisionId = getIntent().getStringExtra("subDivId");
                if (!subDivisionId.equalsIgnoreCase("")) {
                    getAllSubDivScheduledEventList(center, distId, subDivisionId);
                } else {
                    UIToastMessage.show(this, "District id not found");
                }
            }

            // searchRLayout.setVisibility(View.VISIBLE);
        } else {
            searchRLayout.setVisibility(View.GONE);
        }

        searchEText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    PmuComingEventActivity.this.adaptorPmuComingEvent.filter(s.toString());
                }catch (Exception e){}
            }
        });
    }


    private void getScheduleListByEventType() {

        if (eventType.equalsIgnoreCase("kS_SELF_EVENT")) {
            getScheduleList();
        }/*else {
            String center = getIntent().getStringExtra("level");
            if (center.equalsIgnoreCase("Pmu")){
                getAllPmuScheduledEventList(center,"");
            }else if (center.equalsIgnoreCase("District")){
                String distId = getIntent().getStringExtra("distId");
                if (!distId.equalsIgnoreCase("")){
                    getAllPmuScheduledEventList(center,distId);
                }else {
                    UIToastMessage.show(this,"District id not found");
                }
            }else if (center.equalsIgnoreCase("Subdivision")){
                // UIToastMessage.show(this,"Coming Soon");
                String distId = getIntent().getStringExtra("distId");
                String subDivisionId = getIntent().getStringExtra("subDivId");
                if (!subDivisionId.equalsIgnoreCase("")){
                    getAllSubDivScheduledEventList(center,distId,subDivisionId);
                }else {
                    UIToastMessage.show(this,"District id not found");
                }
            }
        }*/
    }


    @Override
    public void onMultiRecyclerViewItemClick(int i, Object o) {

        if (i == 1) {

            String mobile = "";
            String sch_id = "";
            String eType = "";

            JSONObject jsonObject = (JSONObject) o;
            try {
                sch_id = jsonObject.getString("id");
                eType = jsonObject.getString("type");

                String user_data = AppSettings.getInstance().getValue(this, ApConstants.kLOGIN_DATA, ApConstants.kLOGIN_DATA);
                if (!user_data.equalsIgnoreCase("kLOGIN_DATA")) {

                    JSONObject userJson = new JSONObject(user_data);
                    ProfileModel pModel = new ProfileModel(userJson);
                    mobile = pModel.getMobile();
                }

                eventCancelAction(eType, sch_id, mobile);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    private void getScheduleList() {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("user_id", userID);
            jsonObject.put("role_id", roleId);
            jsonObject.put("api_key", ApConstants.kAUTHORITY_KEY);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString());
        AppinventorApi api = new AppinventorApi(this, APIServices.TR_API_URL, "", ApConstants.kMSG, true);
        Retrofit retrofit = api.getRetrofitInstance();
        APIRequest apiRequest = retrofit.create(APIRequest.class);
        Call<JsonObject> responseCall = apiRequest.getPMUEventListRequest(requestBody);
        api.postRequest(responseCall, this, 1);

        DebugLog.getInstance().d("get_Schedule_param=" + responseCall.request().toString());
        DebugLog.getInstance().d("get_Schedule_param=" + AppUtility.getInstance().bodyToString(responseCall.request()));

    }


    private void getAllPmuScheduledEventList(String centerName, String distId) {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("level", centerName);
            jsonObject.put("user_id", userID);
            jsonObject.put("role_id", roleId);
            jsonObject.put("district_id", distId);
            jsonObject.put("sub_division_id", distId);
            jsonObject.put("api_key", ApConstants.kAUTHORITY_KEY);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString());
        AppinventorApi api = new AppinventorApi(this, APIServices.BASE_URL, "", ApConstants.kMSG, true);
        Retrofit retrofit = api.getRetrofitInstance();
        APIRequest apiRequest = retrofit.create(APIRequest.class);
        Call<JsonObject> responseCall = apiRequest.getPMUDistWiseEventListRequest(requestBody);
        api.postRequest(responseCall, this, 2);

        DebugLog.getInstance().d("get_Scheduled_by_dist_param=" + responseCall.request().toString());
        DebugLog.getInstance().d("get_Scheduled_by_dist_param=" + AppUtility.getInstance().bodyToString(responseCall.request()));

    }


    private void getAllSubDivScheduledEventList(String center, String distId, String subDivisionId) {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("schedule_id", "");
            jsonObject.put("user_id", userID);
            jsonObject.put("role_id", roleId);
            jsonObject.put("level", center);
            jsonObject.put("district_id", distId);
            jsonObject.put("sub_division_id", subDivisionId);
            jsonObject.put("api_key", ApConstants.kAUTHORITY_KEY);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString());
        AppinventorApi api = new AppinventorApi(this, APIServices.BASE_URL, "", ApConstants.kMSG, true);
        Retrofit retrofit = api.getRetrofitInstance();
        APIRequest apiRequest = retrofit.create(APIRequest.class);
        Call<JsonObject> responseCall = apiRequest.caGetScheduleRequest(requestBody);
        api.postRequest(responseCall, this, 4);

        DebugLog.getInstance().d("get_Scheduled_by_sub_div_param=" + responseCall.request().toString());
        DebugLog.getInstance().d("get_Scheduled_by_sub_div_param=" + AppUtility.getInstance().bodyToString(responseCall.request()));

    }


    private void eventCancelAction(String eType, String sch_id, String mobile) {

        String uId = AppSettings.getInstance().getValue(this, ApConstants.kUSER_ID, ApConstants.kUSER_ID);
        if (!uId.equalsIgnoreCase("kUSER_ID")) {
            userID = uId;
        }


        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("user_id", userID);
            jsonObject.put("id", sch_id);
            jsonObject.put("event_type", eType);
            jsonObject.put("mobileNo", mobile);
            jsonObject.put("cancellation_reason", "");
            jsonObject.put("cancellation_reason_id", "4");
            jsonObject.put("api_key", ApConstants.kAUTHORITY_KEY);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString());
        AppinventorApi api = new AppinventorApi(this, APIServices.BASE_URL, "", ApConstants.kMSG, true);
        Retrofit retrofit = api.getRetrofitInstance();
        APIRequest apiRequest = retrofit.create(APIRequest.class);
        Call<JsonObject> responseCall = apiRequest.psCancelEventRequest(requestBody);
        api.postRequest(responseCall, this, 3);

        DebugLog.getInstance().d("event_cancel_param=" + responseCall.request().toString());
        DebugLog.getInstance().d("event_cancel_param=" + AppUtility.getInstance().bodyToString(responseCall.request()));
    }


    @Override
    public void onResponse(JSONObject jsonObject, int i) {

        try {

            if (jsonObject != null) {
                // Schedule Response for PS HRD
                if (i == 1) {
                    ResponseModel responseModel = new ResponseModel(jsonObject);
                    if (responseModel.isStatus()) {
                        //scheduleJSONArray = AppHelper.getInstance().getSampleJSONArray();
                        scheduleJSONArray = jsonObject.getJSONArray("data");
                        AdaptorPmuComingEvent adaptorPmuComingEvent = new AdaptorPmuComingEvent(this, scheduleJSONArray, "d",true, this);
                        pmuComingEventRView.setAdapter(adaptorPmuComingEvent);
                    } else {
                        AdaptorPmuComingEvent adaptorPmuComingEvent = new AdaptorPmuComingEvent(this, new JSONArray(), "d",false, this);
                        pmuComingEventRView.setAdapter(adaptorPmuComingEvent);
                        UIToastMessage.show(this, responseModel.getMsg());
                    }
                }


                if (i == 2) {
                    ResponseModel responseModel = new ResponseModel(jsonObject);
                    if (responseModel.isStatus()) {
                        // scheduleJSONArray = jsonObject.getJSONArray("data");
                        eventByDistJSONArray = jsonObject.getJSONArray("data");
                        AdaptorPmuComingEvent adaptorPmuComingEvent = new AdaptorPmuComingEvent(this, eventByDistJSONArray, "p",false, this);
                        pmuComingEventRView.setAdapter(adaptorPmuComingEvent);
                    } else {
                        AdaptorPmuComingEvent adaptorPmuComingEvent = new AdaptorPmuComingEvent(this, new JSONArray(), "p",false, this);
                        pmuComingEventRView.setAdapter(adaptorPmuComingEvent);
                        UIToastMessage.show(this, responseModel.getMsg());
                    }
                }

                if (i == 3) {
                    ResponseModel eventCancelRModel = new ResponseModel(jsonObject);
                    if (eventCancelRModel.isStatus()) {
                        UIToastMessage.show(this, eventCancelRModel.getMsg());
                        getScheduleListByEventType();
                    } else {
                        UIToastMessage.show(this, eventCancelRModel.getMsg());
                    }
                }

                if (i == 4) {
                    ResponseModel responseModel = new ResponseModel(jsonObject);
                    if (responseModel.isStatus()) {
                        // scheduleJSONArray = jsonObject.getJSONArray("data");
                        eventBySubDivJSONArray = jsonObject.getJSONArray("data");
                        adaptorPmuComingEvent = new AdaptorPmuComingEvent(this, eventBySubDivJSONArray, "s",false, this);
                        pmuComingEventRView.setAdapter(adaptorPmuComingEvent);
                    } else {
                        adaptorPmuComingEvent = new AdaptorPmuComingEvent(this, new JSONArray(), "s",false, this);
                        pmuComingEventRView.setAdapter(adaptorPmuComingEvent);
                        UIToastMessage.show(this, responseModel.getMsg());
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
