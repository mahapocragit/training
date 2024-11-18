package in.gov.pocra.training.activity.ps_hrd.ps_upcoming_event;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
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
import in.gov.pocra.training.activity.pmu.pmu_upcoming_event.PmuComingEventActivity;
import in.gov.pocra.training.activity.ps_hrd.ps_report.PsClosedEventListActivity;
import in.gov.pocra.training.model.online.ProfileModel;
import in.gov.pocra.training.model.online.ResponseModel;
import in.gov.pocra.training.util.ApConstants;
import in.gov.pocra.training.web_services.APIRequest;
import in.gov.pocra.training.web_services.APIServices;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Retrofit;

public class PsComingEventActivity extends AppCompatActivity implements ApiCallbackCode, OnMultiRecyclerItemClickListener {

    private ImageView homeBack;
    private String eventOpt;
    private RecyclerView psComingEventRView;
    private String roleId;
    private String userID;
    private String userLaval;
    private RelativeLayout searchRLayout;
    private EditText searchEText;
    private JSONArray scheduleJSONArray = null;
    private String loginType;
    private AdaptorPsComingEvent adaptorPsComingEvent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ps_coming_event);

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
         eventOpt = getIntent().getStringExtra("eventOpt");

        // To get User Id and Roll Id
        String rId = AppSettings.getInstance().getValue(this, ApConstants.kROLE_ID, ApConstants.kROLE_ID);
        String uId = AppSettings.getInstance().getValue(this, ApConstants.kUSER_ID, ApConstants.kUSER_ID);
        if (!rId.equalsIgnoreCase("kROLE_ID")) {
            roleId = rId;
        }

        if (!uId.equalsIgnoreCase("kUSER_ID")) {
            userID = uId;
        }

        String rLaval = AppSettings.getInstance().getValue(this, ApConstants.kUSER_LEVEL, ApConstants.kUSER_LEVEL);
        if (!rLaval.equalsIgnoreCase("kUSER_LEVEL")) {
            userLaval = rLaval;
        }
        searchEText = findViewById(R.id.searchEText);
        searchRLayout = findViewById(R.id.searchRLayout);
        psComingEventRView = (RecyclerView) findViewById(R.id.psComingEventRView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        psComingEventRView.setLayoutManager(linearLayoutManager);

    }


    @Override
    protected void onResume() {
        super.onResume();

        if (eventOpt.equalsIgnoreCase("kS_SELF_EVENT")){
            getScheduleList();
        }
    }


    private void defaultConfiguration() {

        if (eventOpt.equalsIgnoreCase("kS_ALL_EVENT")){
            String center = getIntent().getStringExtra("level");
            String distId = getIntent().getStringExtra("distId");
            if (center.equalsIgnoreCase("Subdivision")){
                String subDivisionId = getIntent().getStringExtra("subDivId");
                if (!subDivisionId.equalsIgnoreCase("")){
                    getAllSubDivScheduledEventList(center,distId,subDivisionId);
                }else {
                    UIToastMessage.show(this,"District id not found");
                }
            }else {
                getScheduleListByDistrictId(distId);
            }
            // searchRLayout.setVisibility(View.VISIBLE);
        }else {
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
                    PsComingEventActivity.this.adaptorPsComingEvent.filter(s.toString());
                }catch (Exception e){}

            }
        });
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
        AppinventorApi api = new AppinventorApi(this, APIServices.BASE_URL, "", ApConstants.kMSG, true);
        Retrofit retrofit = api.getRetrofitInstance();
        APIRequest apiRequest = retrofit.create(APIRequest.class);
        Call<JsonObject> responseCall = apiRequest.psGetScheduledRequest(requestBody);
        api.postRequest(responseCall, this, 1);

        DebugLog.getInstance().d("get_Schedule_list_param=" + responseCall.request().toString());
        DebugLog.getInstance().d("get_Schedule_list_param=" + AppUtility.getInstance().bodyToString(responseCall.request()));

    }


    private void getScheduleListByDistrictId(String distId) {

        JSONObject jsonObject = new JSONObject();
        try {

            jsonObject.put("user_id", userID);
            jsonObject.put("role_id", roleId);
            jsonObject.put("creater_level",userLaval);
            jsonObject.put("district_id", distId);
            jsonObject.put("api_key", ApConstants.kAUTHORITY_KEY);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString());
        AppinventorApi api = new AppinventorApi(this, APIServices.BASE_URL, "", ApConstants.kMSG, true);
        Retrofit retrofit = api.getRetrofitInstance();
        APIRequest apiRequest = retrofit.create(APIRequest.class);
        Call<JsonObject> responseCall = apiRequest.psGetScheduledByDistIdRequest(requestBody);
        api.postRequest(responseCall, this, 2);

        DebugLog.getInstance().d("get_Schedule_list_param=" + responseCall.request().toString());
        DebugLog.getInstance().d("get_Schedule_list_param=" + AppUtility.getInstance().bodyToString(responseCall.request()));

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
        api.postRequest(responseCall, this, 2);

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


    @Override
    public void onResponse(JSONObject jsonObject, int i) {
        try {

            if (jsonObject != null) {
                // Schedule Response for PS HRD
                if (i == 1) {
                    ResponseModel responseModel = new ResponseModel(jsonObject);
                    if (responseModel.isStatus()) {
                        scheduleJSONArray = jsonObject.getJSONArray("data");
                        adaptorPsComingEvent = new AdaptorPsComingEvent(this, scheduleJSONArray, true, this);
                        psComingEventRView.setAdapter(adaptorPsComingEvent);
                    } else {
                        adaptorPsComingEvent = new AdaptorPsComingEvent(this, new JSONArray(), false, this);
                        psComingEventRView.setAdapter(adaptorPsComingEvent);
                        UIToastMessage.show(this, responseModel.getMsg());
                    }
                }

                if (i == 2) {
                    ResponseModel responseModel = new ResponseModel(jsonObject);
                    if (responseModel.isStatus()) {
                        scheduleJSONArray = jsonObject.getJSONArray("data");
                        adaptorPsComingEvent = new AdaptorPsComingEvent(this, scheduleJSONArray, false, this);
                        psComingEventRView.setAdapter(adaptorPsComingEvent);
                    } else {
                        adaptorPsComingEvent = new AdaptorPsComingEvent(this, new JSONArray(), false, this);
                        psComingEventRView.setAdapter(adaptorPsComingEvent);
                        UIToastMessage.show(this, responseModel.getMsg());
                    }
                }


                if (i == 3) {
                    ResponseModel eventCancelRModel = new ResponseModel(jsonObject);
                    if (eventCancelRModel.isStatus()) {
                        UIToastMessage.show(this, eventCancelRModel.getMsg());
                        getScheduleList();
                    } else {
                        UIToastMessage.show(this, eventCancelRModel.getMsg());
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
