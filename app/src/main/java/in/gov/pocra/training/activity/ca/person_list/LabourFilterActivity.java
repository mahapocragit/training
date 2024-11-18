package in.gov.pocra.training.activity.ca.person_list;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import in.co.appinventor.services_api.api.AppinventorApi;
import in.co.appinventor.services_api.api.AppinventorIncAPI;
import in.co.appinventor.services_api.app_util.AppUtility;
import in.co.appinventor.services_api.debug.DebugLog;
import in.co.appinventor.services_api.listener.AlertListCallbackEventListener;
import in.co.appinventor.services_api.listener.ApiCallbackCode;
import in.co.appinventor.services_api.listener.ApiJSONObjCallback;
import in.co.appinventor.services_api.settings.AppSettings;
import in.co.appinventor.services_api.widget.UIToastMessage;
import in.gov.pocra.training.R;
import in.gov.pocra.training.util.ApConstants;
import in.gov.pocra.training.util.ApUtil;
import in.gov.pocra.training.web_services.APIRequest;
import in.gov.pocra.training.web_services.APIServices;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Retrofit;

public class LabourFilterActivity extends AppCompatActivity implements ApiJSONObjCallback, AlertListCallbackEventListener, ApiCallbackCode {

    private ImageView homeBack;

    private String action = "";

    private LinearLayout subDivisionLLayout;
    private TextView subDivisionTView;
    private JSONArray subDivisionJsonArray;
    private ImageView subDivisionIView;
    private String subDivisionId = "";

    private LinearLayout talukaLinearLayout;
    private TextView talukaTextView;
    private JSONArray talukaJSONArray;
    private String talukaId = "";

    private LinearLayout villageLinearLayout;
    private TextView villageTextView;
    private JSONArray villageArray;
    private String villageName;
    private String villageCode = "";

    private Button nextButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_labor_filter);

        /** For actionbar title in center */
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.attendance_actionbar_layout);
        AppCompatTextView actionTitleTextView = (AppCompatTextView) getSupportActionBar().getCustomView().findViewById(R.id.actionTitleTextView);
        homeBack = (ImageView) getSupportActionBar().getCustomView().findViewById(R.id.backImageView);
        homeBack.setVisibility(View.VISIBLE);
        actionTitleTextView.setText(getResources().getString(R.string.title_labour));

        initialization();
        defaultConfiguration();
        eventListener();

    }

    private void initialization() {

        // For Subdivision
        subDivisionLLayout = (LinearLayout) findViewById(R.id.subDivisionLLayout);
        subDivisionTView = (TextView) findViewById(R.id.subDivisionTView);
        subDivisionIView = (ImageView) findViewById(R.id.subDivisionIView);

        // For Taluka
        talukaLinearLayout = (LinearLayout) findViewById(R.id.talukaLinearLayout);
        talukaTextView = (TextView) findViewById(R.id.talukaTextView);

        // for Village
        villageLinearLayout = findViewById(R.id.villageLinearLayout);
        villageTextView = findViewById(R.id.villageTextView);

        nextButton = findViewById(R.id.nextButton);
    }

    private void defaultConfiguration() {
        // For Action Bar
        homeBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        getLoginData();

    }


    @Override
    protected void onResume() {
        super.onResume();
        action = getIntent().getStringExtra("action");
    }

    private void eventListener() {

        // For Sub-division
        subDivisionLLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (subDivisionJsonArray != null) {
                    ApUtil.showCustomListPicker(subDivisionTView, subDivisionJsonArray, "Select Sub-division", "subdivision_name", "subdivision_id", LabourFilterActivity.this, LabourFilterActivity.this);
                } else {
                    getLoginData();
                }
            }
        });

        // For Taluka
        talukaLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ApUtil.checkInternetConnection(LabourFilterActivity.this)) {

                    if (talukaJSONArray != null) {
                        ApUtil.showCustomListPicker(talukaTextView, talukaJSONArray, "Select Taluka", "name", "id", LabourFilterActivity.this, LabourFilterActivity.this);
                    } else {
                        if (!subDivisionId.equalsIgnoreCase("")) {
                            getTalukaList(subDivisionId);
                        } else {
                            UIToastMessage.show(LabourFilterActivity.this, "Please select Sub-Division");
                        }
                    }

                } else {
                    UIToastMessage.show(LabourFilterActivity.this, "No internet connection");
                }
            }
        });


        villageLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ApUtil.checkInternetConnection(LabourFilterActivity.this)) {
                    if (villageArray != null) {
                        AppUtility.getInstance().showListPicker(villageTextView, villageArray, "Select Village", "name", "code", LabourFilterActivity.this, LabourFilterActivity.this);
                    }
                } else {
                    UIToastMessage.show(LabourFilterActivity.this, "No internet connection");
                }
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nextButtonAction();
            }
        });

    }



    private void getLoginData() {

        String loginData = AppSettings.getInstance().getValue(this, ApConstants.kLOGIN_DATA, ApConstants.kLOGIN_DATA);
        if (!loginData.equalsIgnoreCase("kLOGIN_DATA")) {
            try {
                JSONObject loginJSON = new JSONObject(loginData);
//                districtId = loginJSON.getString("district_id");
//                districtTView.setText(loginJSON.getString("district_name"));
                JSONArray trainingData = loginJSON.getJSONArray("training_data");
                JSONObject trainingJSON = trainingData.getJSONObject(0);
                JSONArray subArray = trainingJSON.getJSONArray("subdivisions");
                if (subArray.length() > 1) {
                    subDivisionLLayout.setEnabled(true);
                    subDivisionJsonArray = subArray;
                    subDivisionIView.setVisibility(View.VISIBLE);
                } else {
                    subDivisionJsonArray = new JSONArray();
                    subDivisionLLayout.setEnabled(false);
                    subDivisionIView.setVisibility(View.INVISIBLE);
                    subDivisionId = loginJSON.getString("subdivision_id");
                    subDivisionTView.setText(loginJSON.getString("subdivision_name"));
                    getTalukaList(subDivisionId);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    // get Taluka
    private void getTalukaList(String subDivID) {
        String subTalukaUrl = APIServices.GET_TALUKA_URL + subDivID;
        AppinventorIncAPI api = new AppinventorIncAPI(this, APIServices.API_URL, "", ApConstants.kMSG, true);
        api.getRequestData(subTalukaUrl, this, 1);
    }

    // get Village
    private void getVillageList(String talukaId) {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("taluka_id", talukaId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString());
        AppinventorApi api = new AppinventorApi(this, APIServices.API_URL, "", ApConstants.kMSG, true);
        Retrofit retrofit = api.getRetrofitInstance();
        APIRequest apiRequest = retrofit.create(APIRequest.class);
        Call<JsonObject> responseCall = apiRequest.villageListRequest(requestBody);

        DebugLog.getInstance().d("Village_list_param=" + responseCall.request().toString());
        DebugLog.getInstance().d("Village_list_param=" + AppUtility.getInstance().bodyToString(responseCall.request()));

        api.postRequest(responseCall, this, 2);

    }


    @Override
    public void didSelectAlertViewListItem(TextView textView, String s) {

        if (textView == subDivisionTView) {
            subDivisionId = s;

            talukaId = "";
            talukaTextView.setText("");
            getTalukaList(subDivisionId);

            villageCode = "";
            villageTextView.setText("");

        }

        if (textView == talukaTextView) {
            talukaId = s;

            villageCode = "";
            villageTextView.setText("");

            getVillageList(talukaId);

        }

        if (textView == villageTextView) {
            villageName = villageTextView.getText().toString().trim();
            villageCode = s;

        }
    }


    private void nextButtonAction() {

        if (subDivisionId.equalsIgnoreCase("")){
            UIToastMessage.show(this,"Please select subdivision");
        }else if (talukaId.equalsIgnoreCase("")){
            UIToastMessage.show(this,"Please select Taluka");
        }else if (villageCode.equalsIgnoreCase("")){
            UIToastMessage.show(this,"Please select Village");
        }else {

            Intent intent = new Intent(LabourFilterActivity.this, LabourActivity.class);
            intent.putExtra("action", action);
            intent.putExtra("subDivId", subDivisionId);
            intent.putExtra("talukaId", talukaId);
            intent.putExtra("villageCode", villageCode);
            intent.putExtra("villageName", villageName);
            startActivity(intent);

        }
    }


    @Override
    public void onResponse(JSONObject jsonObject, int i) {

        if (jsonObject != null) {

            try {

                // Taluka Response
                if (i == 1) {
                    String status = jsonObject.getString("status");
                    if (status.equalsIgnoreCase("200")) {
                        talukaJSONArray = jsonObject.getJSONArray("data");
                    }
                }

                // Village Response
                if (i == 2) {
                    String status = jsonObject.getString("status");
                    if (status.equalsIgnoreCase("200")) {
                        villageArray = jsonObject.getJSONArray("data");
                    }
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }

    @Override
    public void onFailure(Object o, Throwable throwable, int i) {

    }

    @Override
    public void onFailure(Throwable throwable, int i) {

    }
}