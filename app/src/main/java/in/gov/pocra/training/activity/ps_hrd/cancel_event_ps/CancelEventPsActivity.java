package in.gov.pocra.training.activity.ps_hrd.cancel_event_ps;

import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import in.gov.pocra.training.util.ApUtil;
import in.gov.pocra.training.web_services.APIRequest;
import in.gov.pocra.training.web_services.APIServices;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Retrofit;

public class CancelEventPsActivity extends AppCompatActivity implements ApiCallbackCode {

    private ImageView homeBack;
    private String roleId;
    private String userID;
    private String schId;

    private TextView otherReasonEText;
    private Button submitButton;
    private RadioGroup reasonRaGroup;
    private RadioGroup.LayoutParams rGrLyParms;
    private JSONArray reasonJSONArray;
    private String eCancelReason = "";
    private String eCancelReasonID = "";
    private String eStartDate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cancel_event_ps);

        /** For actionbar title in center */
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.attendance_actionbar_layout);
        AppCompatTextView actionTitleTextView = (AppCompatTextView) getSupportActionBar().getCustomView().findViewById(R.id.actionTitleTextView);
        homeBack = (ImageView) getSupportActionBar().getCustomView().findViewById(R.id.backImageView);
        // addPersonImageView = (ImageView) getSupportActionBar().getCustomView().findViewById(R.id.addPersonImageView);
        homeBack.setVisibility(View.VISIBLE);
        actionTitleTextView.setText(getResources().getString(R.string.title_add_edit_event));

        schId = getIntent().getStringExtra("sch_id");

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
        if (!rId.equalsIgnoreCase("kROLE_ID")) {
            roleId = rId;
        }
        String uId = AppSettings.getInstance().getValue(this, ApConstants.kUSER_ID, ApConstants.kUSER_ID);
        if (!uId.equalsIgnoreCase("kUSER_ID")) {
            userID = uId;
        }

        reasonRaGroup = (RadioGroup) findViewById(R.id.reasonRaGroup);
        otherReasonEText = (TextView) findViewById(R.id.otherReasonEText);
        submitButton = (Button) findViewById(R.id.submitButton);

    }



    private void defaultConfiguration() {

        getEventData();

    }


    private void eventListener() {

        reasonRaGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                try {

                    eCancelReasonID = String.valueOf(checkedId);
                    String reason = reasonJSONArray.getJSONObject(checkedId-1).getString("reasons");

                    if (reason.equalsIgnoreCase("Other")){
                        eCancelReason = "";
                        otherReasonEText.setVisibility(View.VISIBLE);
                    }else {
                        otherReasonEText.setVisibility(View.GONE);
                        eCancelReason = reason;
                        // UIToastMessage.show(CancelEventPsActivity.this,reason);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });


        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitButtonAction();
            }
        });
    }



    @Override
    protected void onResume() {
        super.onResume();

    }


    // To get Event Start Date
    private void getEventData() {

        String eData = getIntent().getStringExtra("data");
        try {
            JSONObject eJSONData  = new JSONObject(eData);
            String eSDate = eJSONData.getString("start_date");
            eStartDate = ApUtil.getDateByTimeStamp(eSDate);
            // eStartDate = ApUtil.getDateInDDMMYYYY(eSDate);
            getReasonList();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



    private void setReasonList(JSONArray reaJSONArray) {

        reasonJSONArray = new JSONArray();

        for (int r = 0; r < reaJSONArray.length(); r++) {
            RadioButton reasonRaButton = new RadioButton(this);
            JSONObject  newReasonJSON = new JSONObject();

            try {

                String eReasonID = reaJSONArray.getJSONObject(r).getString("id");
                String eReason = reaJSONArray.getJSONObject(r).getString("reasons");
                String newEReason = eReason.replace("##",eStartDate);

                newReasonJSON.put("id",eReasonID);
                newReasonJSON.put("reasons",newEReason);
                reasonJSONArray.put(newReasonJSON);

                reasonRaButton.setText(newEReason);
                reasonRaButton.setId(Integer.valueOf(eReasonID));
                rGrLyParms = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.WRAP_CONTENT, RadioGroup.LayoutParams.WRAP_CONTENT);
                rGrLyParms.setMargins(0,5,0,10);
                reasonRaGroup.addView(reasonRaButton, rGrLyParms);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    private void submitButtonAction() {

        if (otherReasonEText.getVisibility() == View.VISIBLE){
            eCancelReason = otherReasonEText.getText().toString().trim();
        }

        if (eCancelReason.equalsIgnoreCase("")){
            UIToastMessage.show(this,"Please select/input reason to cancel event");
        }else {

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("user_id", userID);
                jsonObject.put("id", schId);
                jsonObject.put("cancellation_reason", eCancelReason);
                jsonObject.put("cancellation_reason_id", eCancelReasonID);
                jsonObject.put("api_key", ApConstants.kAUTHORITY_KEY);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            RequestBody requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString());
            AppinventorApi api = new AppinventorApi(this, APIServices.BASE_URL, "", ApConstants.kMSG, true);
            Retrofit retrofit = api.getRetrofitInstance();
            APIRequest apiRequest = retrofit.create(APIRequest.class);
            Call<JsonObject> responseCall = apiRequest.psCancelEventRequest(requestBody);
            api.postRequest(responseCall, this, 1);

            DebugLog.getInstance().d("event_cancel_param=" + responseCall.request().toString());
            DebugLog.getInstance().d("event_cancel_param=" + AppUtility.getInstance().bodyToString(responseCall.request()));

        }

    }



    private void getReasonList() {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("api_key", ApConstants.kAUTHORITY_KEY);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString());
        AppinventorApi api = new AppinventorApi(this, APIServices.BASE_URL, "", ApConstants.kMSG, true);
        Retrofit retrofit = api.getRetrofitInstance();
        APIRequest apiRequest = retrofit.create(APIRequest.class);
        Call<JsonObject> responseCall = apiRequest.eventCancelReasonRequest(requestBody);
        api.postRequest(responseCall, this, 2);

        DebugLog.getInstance().d("event_cancel_reason_param =" + responseCall.request().toString());
        DebugLog.getInstance().d("event_cancel_reason_param =" + AppUtility.getInstance().bodyToString(responseCall.request()));

    }

    @Override
    public void onResponse(JSONObject jsonObject, int i) {

        try {

            if (jsonObject != null) {

                // Event type Response
                if (i == 1) {
                    ResponseModel eventCancelRModel = new ResponseModel(jsonObject);
                    if (eventCancelRModel.isStatus()) {
                        UIToastMessage.show(this,eventCancelRModel.getMsg());
                        finish();
                    }else {
                        UIToastMessage.show(this,eventCancelRModel.getMsg());
                    }
                }


                if (i == 2) {
                    ResponseModel eventCancelRModel = new ResponseModel(jsonObject);
                    if (eventCancelRModel.isStatus()) {

                        JSONArray reaJSONArray = eventCancelRModel.getData();
                        JSONObject otherObj = new JSONObject();
                        otherObj.put("id",String.valueOf(reaJSONArray.length()+1));
                        otherObj.put("reasons","Other");
                        reaJSONArray.put(otherObj);

                        setReasonList(reaJSONArray);

                    }else {
                        UIToastMessage.show(this,eventCancelRModel.getMsg());
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
