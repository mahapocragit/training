package in.gov.pocra.training.activity.common.mem_attend_detail;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import in.co.appinventor.services_api.api.AppinventorApi;
import in.co.appinventor.services_api.app_util.AppUtility;
import in.co.appinventor.services_api.debug.DebugLog;
import in.co.appinventor.services_api.listener.ApiCallbackCode;
import in.co.appinventor.services_api.settings.AppSettings;
import in.co.appinventor.services_api.widget.UIToastMessage;
import in.gov.pocra.training.R;
import in.gov.pocra.training.model.online.ResponseModel;
import in.gov.pocra.training.util.ApConstants;
import in.gov.pocra.training.web_services.APIRequest;
import in.gov.pocra.training.web_services.APIServices;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Retrofit;

public class MemAttendanceDetailActivity extends AppCompatActivity implements ApiCallbackCode {

    private ImageView homeBack;
    private String roleId;
    private String userID;

    private String memId;

    private TextView eInviteTView;
    private TextView ePTView;
    private TextView eATView;
    private RecyclerView memDRView;
    private String groupId;
    private String groupType;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mem_attend_detail);

        /** For actionbar title in center */
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.attendance_actionbar_layout);
        AppCompatTextView actionTitleTextView = (AppCompatTextView) getSupportActionBar().getCustomView().findViewById(R.id.actionTitleTextView);
        homeBack = (ImageView) getSupportActionBar().getCustomView().findViewById(R.id.backImageView);
        // addPersonImageView = (ImageView) getSupportActionBar().getCustomView().findViewById(R.id.addPersonImageView);
        homeBack.setVisibility(View.VISIBLE);

        String memName = getIntent().getStringExtra("title");
        actionTitleTextView.setText(memName);
        memId = getIntent().getStringExtra("mem_id");
        groupId = getIntent().getStringExtra("group_id");
        groupType = getIntent().getStringExtra("group_type");

        initialization();
        defaultConfiguration();
        eventListener();
    }

    private void initialization() {

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


        eInviteTView = (TextView)findViewById(R.id.eInviteTView);
        ePTView = (TextView)findViewById(R.id.ePTView);
        eATView = (TextView)findViewById(R.id.eATView);

        memDRView = (RecyclerView)findViewById(R.id.memDRView);
        LinearLayoutManager lLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        memDRView.setLayoutManager(lLayoutManager);

    }


    @Override
    protected void onResume() {
        super.onResume();
        getEventMemAttendDetail();
    }



    private void defaultConfiguration() {

       /* JSONArray sampArray = AppHelper.getInstance().getSampleJSONArray();
        AdaptorMemAttendDetail  adaptorMemAttendDetail = new AdaptorMemAttendDetail(this,sampArray);
        memDRView.setAdapter(adaptorMemAttendDetail);*/
    }

    private void eventListener() {

    }


    private void getEventMemAttendDetail() {


        JSONObject jsonObject = new JSONObject();
        try {
            // jsonObject.put("user_id", userID);
            // jsonObject.put("role_id", roleId);
            jsonObject.put("group_type", groupType);
            jsonObject.put("group_id", groupId);
            jsonObject.put("member_id", memId);
            jsonObject.put("api_key", ApConstants.kAUTHORITY_KEY);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString());
        AppinventorApi api = new AppinventorApi(this, APIServices.BASE_URL, "", ApConstants.kMSG, true);
        Retrofit retrofit = api.getRetrofitInstance();
        APIRequest apiRequest = retrofit.create(APIRequest.class);
        Call<JsonObject> responseCall = apiRequest.getMemAttendDetailRequest(requestBody);
        api.postRequest(responseCall, this, 1);

        DebugLog.getInstance().d("get_event_mem_detail_param=" + responseCall.request().toString());
        DebugLog.getInstance().d("get_event_mem_detail_param=" + AppUtility.getInstance().bodyToString(responseCall.request()));

    }


    @Override
    public void onResponse(JSONObject jsonObject, int i) {

        try {

            if (jsonObject != null) {
                // Schedule Response for PS HRD
                if (i == 1) {
                    ResponseModel responseModel = new ResponseModel(jsonObject);
                    if (responseModel.isStatus()) {
                        JSONObject dataJSON = jsonObject.getJSONObject("data");
                        if (dataJSON.length()>0){
                            // {"status":200,"response":"Member event Details","data":{"totalInvited":4,"totalPresent":2,"totalAbsent":3,"eventList":[{"schedule_event_id":324,"event_type":1,"event_type_name":"Training","participints":16,"start_date":1558895400,"end_date":1558981800,"other_venue":"","venue":"Krishi Vigyan Kendra, Village- Gandheli"},{"schedule_event_id":322,"event_type":1,"event_type_name":"Training","participints":16,"start_date":1558895400,"end_date":1558895400,"other_venue":"","venue":"Rameti, Aurangabad"},{"schedule_event_id":323,"event_type":1,"event_type_name":"Training","participints":16,"start_date":1558895400,"end_date":1558895400,"other_venue":"","venue":"Krishi Vigyan Kendra, Paithan Road"},{"schedule_event_id":326,"event_type":1,"event_type_name":"Training","participints":13,"start_date":1558895400,"end_date":1558895400,"other_venue":"","venue":"Rameti, Aurangabad"}]}}

                            String numOfEvent = dataJSON.getString("totalInvited");
                            String presentIn = dataJSON.getString("totalPresent");

                            String absent = String.valueOf(Integer.valueOf(numOfEvent)-Integer.valueOf(presentIn)); // Dummy, Only to show
                            String absentIn = dataJSON.getString("totalAbsent");

                            eInviteTView.setText(": "+numOfEvent+ " event");
                            ePTView.setText(": "+presentIn+ " event");
                            eATView.setText(": "+absent+ " event");

                            JSONArray eventArray = dataJSON.getJSONArray("eventList");
                            if (eventArray.length()>0){
                                AdaptorMemAttendDetail  adaptorMemAttendDetail = new AdaptorMemAttendDetail(this,eventArray);
                                memDRView.setAdapter(adaptorMemAttendDetail);
                            }else {
                                UIToastMessage.show(this, "Detail not available");
                            }

                        }else {
                            UIToastMessage.show(this, "Detail not available");
                        }

                    }else {
                        UIToastMessage.show(this,responseModel.getMsg());
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
