package in.gov.pocra.training.activity.ca.person_list;

import android.content.Intent;

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

import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import in.co.appinventor.services_api.api.AppinventorApi;
import in.co.appinventor.services_api.app_util.AppUtility;
import in.co.appinventor.services_api.debug.DebugLog;
import in.co.appinventor.services_api.listener.ApiCallbackCode;
import in.co.appinventor.services_api.listener.OnMultiRecyclerItemClickListener;
import in.co.appinventor.services_api.widget.UIToastMessage;
import in.gov.pocra.training.R;
import in.gov.pocra.training.util.ApConstants;
import in.gov.pocra.training.web_services.APIRequest;
import in.gov.pocra.training.web_services.APIServices;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Retrofit;

public class LabourActivity extends AppCompatActivity implements ApiCallbackCode, OnMultiRecyclerItemClickListener {

    private ImageView homeBack;
    private ImageView addPersonImageView;
    private String action = "";

    private String subDivisionId = "";
    private String talukaId = "";
    private String villageName;
    private String villageCode = "";
    private RecyclerView laborRecyclerVew;
    private JSONArray labourList;
    private EditText searchEText;
    private AdaptorCaPerson adaptorCaPerson;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_labour);

        /** For actionbar title in center */
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.attendance_actionbar_layout);
        AppCompatTextView actionTitleTextView = (AppCompatTextView) getSupportActionBar().getCustomView().findViewById(R.id.actionTitleTextView);
        homeBack = (ImageView) getSupportActionBar().getCustomView().findViewById(R.id.backImageView);
        homeBack.setVisibility(View.VISIBLE);
        addPersonImageView = (ImageView) getSupportActionBar().getCustomView().findViewById(R.id.addPersonImageView);
        addPersonImageView.setVisibility(View.VISIBLE);
        actionTitleTextView.setText(getResources().getString(R.string.title_labour));

        // eDB = new EventDataBase(this);

        initialization();
        defaultConfiguration();
    }

    private void initialization() {
        subDivisionId = getIntent().getStringExtra("subDivId");
        talukaId = getIntent().getStringExtra("talukaId");
        villageCode = getIntent().getStringExtra("villageCode");
        villageName = getIntent().getStringExtra("villageName");

        searchEText = findViewById(R.id.searchEText);

        laborRecyclerVew = findViewById(R.id.laborRecyclerVew);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        laborRecyclerVew.setLayoutManager(linearLayoutManager);

    }

    private void defaultConfiguration() {
        // For Action Bar
        homeBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        addPersonImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LabourActivity.this, AddAgriLabourActivity.class);
                intent.putExtra("subDivId", subDivisionId);
                intent.putExtra("talukaId", talukaId);
                intent.putExtra("villageCode", villageCode);
                intent.putExtra("villageName", villageName);
                intent.putExtra("type", "Add");
                intent.putExtra("personType", "labour");
                startActivity(intent);
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
                if (adaptorCaPerson != null) {
                    LabourActivity.this.adaptorCaPerson.filter(s.toString());
                } else {
                    UIToastMessage.show(LabourActivity.this, "User not found");
                }
            }

        });

    }


    @Override
    public void onMultiRecyclerViewItemClick(int i, Object o) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        action = getIntent().getStringExtra("action");
        if (action.equalsIgnoreCase("get")) {
            addPersonImageView.setVisibility(View.GONE);
        }
        getAgriLabourList();
    }

    private void getAgriLabourList() {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("subdivision", subDivisionId);
            jsonObject.put("taluka_id", talukaId);
            jsonObject.put("census_code", villageCode);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString());
        AppinventorApi api = new AppinventorApi(this, APIServices.SSO_API_URL, "", ApConstants.kMSG, true);
        Retrofit retrofit = api.getRetrofitInstance();
        APIRequest apiRequest = retrofit.create(APIRequest.class);
        Call<JsonObject> responseCall = apiRequest.getCaLabourRequest(requestBody);

        DebugLog.getInstance().d("Agri_Labour_list_param=" + responseCall.request().toString());
        DebugLog.getInstance().d("Agri_Labour_param=" + AppUtility.getInstance().bodyToString(responseCall.request()));

        api.postRequest(responseCall, this, 1);
    }

    @Override
    public void onResponse(JSONObject jsonObject, int i) {
        if (jsonObject != null) {

            try {

                // get Labour Response
                if (i == 1) {
                    String status = jsonObject.getString("status");
                    if (status.equalsIgnoreCase("200")) {
                        labourList = jsonObject.getJSONArray("data");
                        adaptorCaPerson = new AdaptorCaPerson(this, labourList, action, "labour", this);
                        laborRecyclerVew.setAdapter(adaptorCaPerson);
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


}