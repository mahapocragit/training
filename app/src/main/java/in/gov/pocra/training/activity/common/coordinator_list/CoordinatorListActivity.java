package in.gov.pocra.training.activity.common.coordinator_list;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

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
import in.gov.pocra.training.model.online.ResponseModel;
import in.gov.pocra.training.util.ApConstants;
import in.gov.pocra.training.web_services.APIRequest;
import in.gov.pocra.training.web_services.APIServices;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Retrofit;

public class CoordinatorListActivity extends AppCompatActivity implements ApiCallbackCode, OnMultiRecyclerItemClickListener {

    private ImageView homeBack;
    private String roleId;
    private String userID;

    private ImageView addPersonImageView;
    private JSONArray sledCordJSONArray = new JSONArray();
    private RecyclerView coordinatorRView;
    private String sDate;
    private String eDate;
    private Button selectButton;
    private AdaptorCoordinatorList adaptorCoordinatorList;
    private ImageView searchIView;
    private EditText searchEText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coordinator_list);


        /** For actionbar title in center */
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.attendance_actionbar_layout);
        AppCompatTextView actionTitleTextView = (AppCompatTextView) getSupportActionBar().getCustomView().findViewById(R.id.actionTitleTextView);
        homeBack = (ImageView) getSupportActionBar().getCustomView().findViewById(R.id.backImageView);
        addPersonImageView = (ImageView) getSupportActionBar().getCustomView().findViewById(R.id.addPersonImageView);
        homeBack.setVisibility(View.VISIBLE);
       //  addPersonImageView.setVisibility(View.VISIBLE);
        actionTitleTextView.setText(getResources().getString(R.string.title_coordinator));

        initialization();
        defaultConfiguration();
        eventListener();
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

        searchEText = (EditText)findViewById(R.id.searchEText);
        searchIView = (ImageView)findViewById(R.id.searchIView);
        coordinatorRView = (RecyclerView) findViewById(R.id.coordinatorRView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        coordinatorRView.setLayoutManager(linearLayoutManager);

        selectButton = (Button)findViewById(R.id.confirmButton);

    }

    private void defaultConfiguration() {

        searchEText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                CoordinatorListActivity.this.adaptorCoordinatorList.filter(s.toString());
            }
        });

    }


    @Override
    protected void onResume() {
        super.onResume();

        String sledCordPerson = AppSettings.getInstance().getValue(this, ApConstants.kS_COORDINATOR, ApConstants.kS_COORDINATOR);
        try {
            if (!sledCordPerson.equalsIgnoreCase("kS_COORDINATOR")) {
                sledCordJSONArray = new JSONArray(sledCordPerson);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        getCoordinatorData();

    }



    private void eventListener() {

        addPersonImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CoordinatorListActivity.this, AddEditCoordinatorActivity.class);
                startActivity(intent);
            }
        });

        searchEText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchEText.setCursorVisible(true);
            }
        });

        searchIView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchIViewAction();
            }
        });

        selectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sledCordJSONArray = new JSONArray();
                try {

                    if (adaptorCoordinatorList != null) {

                        for (int i = 0; i < adaptorCoordinatorList.mJSONArray.length(); i++) {
                            JSONObject jsonObject = adaptorCoordinatorList.mJSONArray.getJSONObject(i);

                            if (jsonObject.getInt("is_selected") == 1) {

                                if (sledCordJSONArray.length() < 1) {
                                    sledCordJSONArray.put(jsonObject);
                                } else {
                                    Toast.makeText(CoordinatorListActivity.this, "More than 1 Coordinator not allowed", Toast.LENGTH_SHORT).show();
//                                    UIToastMessage.show(CoordinatorListActivity.this, "More than 1 Coordinator not allowed");
                                    //break;
                                }
                            }
                        }

                        if (sledCordJSONArray.length() > 0) {
                            AppSettings.getInstance().setValue(CoordinatorListActivity.this, ApConstants.kS_COORDINATOR, sledCordJSONArray.toString());
                            finish();
                        } else {
                            Toast.makeText(CoordinatorListActivity.this, "Select at least one Coordinator", Toast.LENGTH_SHORT).show();

//                            UIToastMessage.show(CoordinatorListActivity.this, "Select at least one Coordinator");
                        }

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

    }



    private void searchIViewAction() {

        String searchText = searchEText.getText().toString().trim();
        String sDate = getIntent().getStringExtra("startDate");
        String eDate = getIntent().getStringExtra("endDate");
        String desigId = getIntent().getStringExtra("desigId");

        /*if (searchText.equalsIgnoreCase("")){
            UIToastMessage.show(CoordinatorListActivity.this, "Please input search text");
        }else*/ if (sDate.equalsIgnoreCase("") || eDate.equalsIgnoreCase("")){
            Toast.makeText(CoordinatorListActivity.this, "Please Select start and End date of event", Toast.LENGTH_SHORT).show();
//            UIToastMessage.show(CoordinatorListActivity.this, "Please Select start and End date of event");
            finish();
        }else {
            getCoordinatorList(sDate, eDate,searchText,desigId);
        }

    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onMultiRecyclerViewItemClick(int i, Object o) {


        try {
            JSONObject jsonObject = (JSONObject)o;
            String itemId = jsonObject.getString("id");
            String isSelected = jsonObject.getString("is_selected");

            if (sledCordJSONArray != null){
                for (int k = 0; k<sledCordJSONArray.length(); k++){
                    JSONObject sledJSONObj = sledCordJSONArray.getJSONObject(k);
                    String sledId = sledJSONObj.getString("id");

                    if (sledId.equalsIgnoreCase(itemId) && isSelected.equalsIgnoreCase("0")){
                        sledCordJSONArray.remove(k);
                    }
                }

                AppSettings.getInstance().setValue(CoordinatorListActivity.this, ApConstants.kS_COORDINATOR, sledCordJSONArray.toString());

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    private JSONArray cordArrayWithSelection(JSONArray coordinatorJSONArray) {

        try {

            if (sledCordJSONArray.length()>0){

                for (int i = 0; i < sledCordJSONArray.length(); i++) {

                    String itemId = sledCordJSONArray.getJSONObject(i).getString("id");

                    for (int j = 0; j<coordinatorJSONArray.length();j++){
                        JSONObject jsonObject = coordinatorJSONArray.getJSONObject(j);
                        String resPerId = jsonObject.getString("id");

                        if (resPerId.equalsIgnoreCase(itemId)) {
                            jsonObject.put("is_selected","1");
                            coordinatorJSONArray.put(j,jsonObject);
                        }
                    }
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return coordinatorJSONArray;
    }



    private void getCoordinatorData() {
        String sDate = getIntent().getStringExtra("startDate");
        String eDate = getIntent().getStringExtra("endDate");
        String desigId = getIntent().getStringExtra("desigId");

        if (!sDate.equalsIgnoreCase("") && !eDate.equalsIgnoreCase("")){
            getCoordinatorList(sDate, eDate,"", desigId);
        }else {
            Toast.makeText(CoordinatorListActivity.this, "Please Select start and End date of event", Toast.LENGTH_SHORT).show();
//            UIToastMessage.show(CoordinatorListActivity.this, "Please Select start and End date of event");
            finish();
        }
    }


    /*private void getCoordinatorList(String sDate, String eDate, String searchText) {

        String sledGpId = getIntent().getStringExtra("gp_id");
        String sledVillageId = getIntent().getStringExtra("village_id");

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("grampanchayat_id", sledGpId);
            jsonObject.put("village_id", sledVillageId);
            jsonObject.put("search_string", searchText);
            jsonObject.put("role_id",roleId);
            jsonObject.put("api_key", ApConstants.kAUTHORITY_KEY);
            jsonObject.put("start_date", sDate);
            jsonObject.put("end_date", eDate);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString());
        AppinventorApi api = new AppinventorApi(this, APIServices.BASE_URL, "", ApConstants.kMSG, true);
        Retrofit retrofit = api.getRetrofitInstance();
        APIRequest apiRequest = retrofit.create(APIRequest.class);
        Call<JsonObject> responseCall = apiRequest.cordListRequest(requestBody);

        DebugLog.getInstance().d("event_coordinator_list=" + responseCall.request().toString());
        DebugLog.getInstance().d("event_coordinator_list=" + AppUtility.getInstance().bodyToString(responseCall.request()));

        api.postRequest(responseCall, this, 1);

        *//*String subTrainerOneUrl = APIServices.GET_TRAINER_URL;
        AppinventorIncAPI api = new AppinventorIncAPI(this, APIServices.API_URL, "", ApConstants.kMSG, true);
        api.getRequestData(subTrainerOneUrl, this, 5);*//*
    }*/


    // For Test
    private void getCoordinatorList(String sDate, String eDate, String searchText, String desigId) {

        String sledGpId = getIntent().getStringExtra("gp_id");
        String sledVillageCode = getIntent().getStringExtra("village_code");
        JSONArray sledPoOffMemArray = new JSONArray();;
        String sledPocraOffMem = getIntent().getStringExtra("po_off_mem");
        if (!sledPocraOffMem.equalsIgnoreCase("")){
            try {
                 sledPoOffMemArray = new JSONArray(sledPocraOffMem);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("grampanchayat_id", sledGpId);
            jsonObject.put("village_code", sledVillageCode);
            jsonObject.put("po_off_mem", sledPoOffMemArray);
            jsonObject.put("user_role",desigId);
            jsonObject.put("search_string", searchText);
            jsonObject.put("role_id",roleId);
            jsonObject.put("api_key", ApConstants.kAUTHORITY_KEY);
            jsonObject.put("start_date", sDate);
            jsonObject.put("end_date", eDate);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString());
        AppinventorApi api = new AppinventorApi(this, APIServices.BASE_URL, "", ApConstants.kMSG, true);
        Retrofit retrofit = api.getRetrofitInstance();
        APIRequest apiRequest = retrofit.create(APIRequest.class);
        Call<JsonObject> responseCall = apiRequest.cordListRequest(requestBody);

        DebugLog.getInstance().d("event_coordinator_list=" + responseCall.request().toString());
        DebugLog.getInstance().d("event_coordinator_list=" + AppUtility.getInstance().bodyToString(responseCall.request()));

        api.postRequest(responseCall, this, 1);

        /*String subTrainerOneUrl = APIServices.GET_TRAINER_URL;
        AppinventorIncAPI api = new AppinventorIncAPI(this, APIServices.API_URL, "", ApConstants.kMSG, true);
        api.getRequestData(subTrainerOneUrl, this, 5);*/
    }




    @Override
    public void onResponse(JSONObject jsonObject, int i) {

        try {

            if (jsonObject != null) {

                // Event type Response
                if (i == 1) {
                    ResponseModel cordRModel = new ResponseModel(jsonObject);
                    if (cordRModel.isStatus()){
                       JSONArray  coordinatorJSONArray = jsonObject.getJSONArray("data");
                        if (coordinatorJSONArray.length() >0){
                            JSONArray cordWithSelected = cordArrayWithSelection(coordinatorJSONArray);
                            adaptorCoordinatorList = new AdaptorCoordinatorList(this,cordWithSelected,this);
                            coordinatorRView.setAdapter(adaptorCoordinatorList);
                        }else {
                            adaptorCoordinatorList = new AdaptorCoordinatorList(this,new JSONArray(),this);
                            coordinatorRView.setAdapter(adaptorCoordinatorList);
                        }
                    }else {
                        adaptorCoordinatorList = new AdaptorCoordinatorList(this,new JSONArray(),this);
                        coordinatorRView.setAdapter(adaptorCoordinatorList);
                        Toast.makeText(this, ""+cordRModel.getMsg(), Toast.LENGTH_SHORT).show();
                        //UIToastMessage.show(this,cordRModel.getMsg());
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
