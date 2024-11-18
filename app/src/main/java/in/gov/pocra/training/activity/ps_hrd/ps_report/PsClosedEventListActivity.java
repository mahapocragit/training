package in.gov.pocra.training.activity.ps_hrd.ps_report;

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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import in.co.appinventor.services_api.api.AppinventorApi;
import in.co.appinventor.services_api.app_util.AppUtility;
import in.co.appinventor.services_api.debug.DebugLog;
import in.co.appinventor.services_api.listener.ApiCallbackCode;
import in.co.appinventor.services_api.settings.AppSettings;
import in.co.appinventor.services_api.widget.UIToastMessage;
import in.gov.pocra.training.R;
import in.gov.pocra.training.activity.ca.ca_report.CaClosedEventListActivity;
import in.gov.pocra.training.model.online.ResponseModel;
import in.gov.pocra.training.util.ApConstants;
import in.gov.pocra.training.web_services.APIRequest;
import in.gov.pocra.training.web_services.APIServices;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Retrofit;

public class PsClosedEventListActivity extends AppCompatActivity implements ApiCallbackCode {

    private ImageView homeBack;
    private String eventOpt;
    private ImageView filterDateIV;
    private String filterDate;
    private EditText searchEText;
    private RelativeLayout searchRLayout;

    private RecyclerView psClosedEventRView;
    private String roleId;
    private String userID;
    private AdaptorPsClosedEvent adaptorPsClosedEvent;
    private String lavel = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ps_closed_event_list);

        /*** For actionbar title in center ***/
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.attendance_actionbar_layout);
        AppCompatTextView actionTitleTextView = (AppCompatTextView) getSupportActionBar().getCustomView().findViewById(R.id.actionTitleTextView);
        homeBack = (ImageView) getSupportActionBar().getCustomView().findViewById(R.id.backImageView);
        homeBack.setVisibility(View.VISIBLE);
        filterDateIV = (ImageView) getSupportActionBar().getCustomView().findViewById(R.id.addPersonImageView);
        filterDateIV.setImageDrawable(getResources().getDrawable(R.drawable.ic_filter_list));
        filterDateIV.setVisibility(View.VISIBLE);
        actionTitleTextView.setText(getResources().getString(R.string.title_closed_event));

        initialization();
        defaultConfiguration();
    }

    private void initialization() {

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
        searchRLayout = findViewById(R.id.searchRLayout);
        searchEText = findViewById(R.id.searchEText);
        psClosedEventRView = (RecyclerView)findViewById(R.id.psClosedEventRView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        psClosedEventRView.setLayoutManager(linearLayoutManager);

        if (eventOpt.equalsIgnoreCase("kS_ALL_EVENT")){
            lavel = getIntent().getStringExtra("level");
            String distId = getIntent().getStringExtra("distId");
            String subDivId = getIntent().getStringExtra("subDivId");
            if (lavel.equalsIgnoreCase("Subdivision")){
                getClosedEventListBySubDivId(distId,subDivId,"");
            }else {
                getClosedEventListByDistrictId(distId,"");
            }
            searchRLayout.setVisibility(View.VISIBLE);
        }else {
            searchRLayout.setVisibility(View.GONE);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();

        // String eventOpt = getIntent().getStringExtra("eventOpt");
        if (eventOpt.equalsIgnoreCase("kS_SELF_EVENT")){
            getClosedEventList("");
        }/*else {
            String lavel = getIntent().getStringExtra("level");
            String distId = getIntent().getStringExtra("distId");
            String subDivId = getIntent().getStringExtra("subDivId");
            if (lavel.equalsIgnoreCase("Subdivision")){
                getClosedEventListBySubDivId(distId,subDivId,"");
            }else {
                getClosedEventListByDistrictId(distId,"");
            }
        }*/
    }


    private void defaultConfiguration() {
        homeBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        filterDateIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // For Start Date
                Date fromDate = null;
                try {
                    // cDate = new SimpleDateFormat("yyyy-MM-dd").parse(eventStartDate);
                    fromDate = new SimpleDateFormat("yyyy-MM-dd").parse("2019-01-01");
                    Calendar fc = Calendar.getInstance();
                    fc.setTime(fromDate);
                    int fromYear = fc.get(Calendar.YEAR);
                    int fMonth = fc.get(Calendar.MONTH);

                    Calendar tc = Calendar.getInstance();
                    Date toDate = new Date();
                    tc.setTime(toDate);
                    int toYear = tc.get(Calendar.YEAR);
                    int cMonth = tc.get(Calendar.MONTH);

                    // ApUtil.showDatePickerBtnTwoDatesWithOutTextView(CaClosedEventListActivity.this, fromStartDate, toDate, null, CaClosedEventListActivity.this);


                    // For month picker
//                    MonthPickerDialog.Builder builder = new MonthPickerDialog.Builder(PsClosedEventListActivity.this, new MonthPickerDialog.OnDateSetListener() {
//                        @Override
//                        public void onDateSet(int selectedMonth, int selectedYear) {
//                            String lavel = getIntent().getStringExtra("level");
//                            String eventOpt = getIntent().getStringExtra("eventOpt");
//                            if (eventOpt.equalsIgnoreCase("kS_SELF_EVENT")){
//                                getClosedEventList(selectedYear + "-" + (selectedMonth + 1));
//                            }else {
//                                String distId = getIntent().getStringExtra("distId");
//                                String subDivId = getIntent().getStringExtra("subDivId");
//                                if (lavel.equalsIgnoreCase("Subdivision")){
//                                    getClosedEventListBySubDivId(distId,subDivId,selectedYear + "-" + (selectedMonth + 1));
//                                }else {
//                                    getClosedEventListByDistrictId(distId,selectedYear + "-" + (selectedMonth + 1));
//                                }
//                            }
//                        }
//                    }, tc.get(Calendar.YEAR), tc.get(Calendar.MONTH));
//
//                    builder.setActivatedMonth(cMonth)
//                            .setMinYear(fromYear)
//                            .setActivatedYear(toYear)
//                            .setMaxYear(toYear)
//                            .setMinMonth(Calendar.JANUARY)
//                            .setMaxMonth(Calendar.DECEMBER)
//                            .setTitle("Select month")
//                            .build()
//                            .show();

                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });

        searchEText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                PsClosedEventListActivity.this.adaptorPsClosedEvent.filter(s.toString());
            }
        });

    }


    private void getClosedEventList(String filterDate) {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("user_id", userID);
            jsonObject.put("role_id", roleId);
            jsonObject.put("filter_date", filterDate);
            jsonObject.put("api_key", ApConstants.kAUTHORITY_KEY);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString());
        AppinventorApi api = new AppinventorApi(this, APIServices.BASE_URL, "", ApConstants.kMSG, true);
        Retrofit retrofit = api.getRetrofitInstance();
        APIRequest apiRequest = retrofit.create(APIRequest.class);
        Call<JsonObject> responseCall = apiRequest.psClosedEventListRequest(requestBody);

        DebugLog.getInstance().d("get_PS_Closed_Event_List_param=" + responseCall.request().toString());
        DebugLog.getInstance().d("get_PS_Closed_Event_List_param=" + AppUtility.getInstance().bodyToString(responseCall.request()));

        api.postRequest(responseCall, this, 1);
    }





    private void getClosedEventListByDistrictId(String distId, String filterDate) {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("user_id", userID);
            jsonObject.put("role_id", roleId);
            jsonObject.put("district_id", distId);
            jsonObject.put("filter_date", filterDate);
            jsonObject.put("api_key", ApConstants.kAUTHORITY_KEY);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString());
        AppinventorApi api = new AppinventorApi(this, APIServices.BASE_URL, "", ApConstants.kMSG, true);
        Retrofit retrofit = api.getRetrofitInstance();
        APIRequest apiRequest = retrofit.create(APIRequest.class);
        Call<JsonObject> responseCall = apiRequest.psPmuClosedEventListRequest(requestBody);

        DebugLog.getInstance().d("get_PS_pmu_Closed_Event_List_param=" + responseCall.request().toString());
        DebugLog.getInstance().d("get_PS_pmu_Closed_Event_List_param=" + AppUtility.getInstance().bodyToString(responseCall.request()));

        api.postRequest(responseCall, this, 1);
    }

    private void getClosedEventListBySubDivId(String distId, String subDivId, String filterDate) {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("user_id", userID);
            jsonObject.put("role_id", roleId);
            jsonObject.put("district_id", distId);
            jsonObject.put("sub_division_id", subDivId);
            jsonObject.put("filter_date", filterDate);
            jsonObject.put("api_key", ApConstants.kAUTHORITY_KEY);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString());
        AppinventorApi api = new AppinventorApi(this, APIServices.BASE_URL, "", ApConstants.kMSG, true);
        Retrofit retrofit = api.getRetrofitInstance();
        APIRequest apiRequest = retrofit.create(APIRequest.class);
        Call<JsonObject> responseCall = apiRequest.caAllClosedEventListRequest(requestBody);

        DebugLog.getInstance().d("get_PS_pmu_Closed_Event_List_param=" + responseCall.request().toString());
        DebugLog.getInstance().d("get_PS_pmu_Closed_Event_List_param=" + AppUtility.getInstance().bodyToString(responseCall.request()));

        api.postRequest(responseCall, this, 1);


    }



    @Override
    public void onResponse(JSONObject jsonObject, int i) {
        try {

            if (jsonObject != null) {
                // Schedule Response for PS HRD
                if (i == 1) {
                    ResponseModel responseModel = new ResponseModel(jsonObject);
                    if (responseModel.isStatus()) {
                        JSONArray scheduleHistJSONArray = jsonObject.getJSONArray("data");
                        adaptorPsClosedEvent = new AdaptorPsClosedEvent(this,scheduleHistJSONArray,lavel);
                        psClosedEventRView.setAdapter(adaptorPsClosedEvent);
                    }else {
                        adaptorPsClosedEvent = new AdaptorPsClosedEvent(this,new JSONArray(),lavel);
                        psClosedEventRView.setAdapter(adaptorPsClosedEvent);
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
