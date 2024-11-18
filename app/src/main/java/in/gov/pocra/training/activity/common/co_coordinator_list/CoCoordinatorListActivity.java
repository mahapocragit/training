package in.gov.pocra.training.activity.common.co_coordinator_list;

import android.content.Intent;
import android.os.Bundle;
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

public class CoCoordinatorListActivity extends AppCompatActivity implements ApiCallbackCode, OnMultiRecyclerItemClickListener {

    private ImageView homeBack;
    private String roleId;
    private String userID;

    private ImageView addPersonImageView;
    private JSONArray sledCoCoordinatorJSONArray = new JSONArray();
    private RecyclerView resPersonRView;
    private AdaptorCoCoordinatorList adaptorCoCoordinatorList;

    private Button selectButton;
    private ImageView searchIView;
    private EditText searchEText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_res_person_list);


        /** For actionbar title in center */
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.attendance_actionbar_layout);
        AppCompatTextView actionTitleTextView = (AppCompatTextView) getSupportActionBar().getCustomView().findViewById(R.id.actionTitleTextView);
        homeBack = (ImageView) getSupportActionBar().getCustomView().findViewById(R.id.backImageView);
        addPersonImageView = (ImageView) getSupportActionBar().getCustomView().findViewById(R.id.addPersonImageView);
        homeBack.setVisibility(View.VISIBLE);
        addPersonImageView.setVisibility(View.INVISIBLE);
        actionTitleTextView.setText("Co-Coordinator");

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
        resPersonRView = (RecyclerView) findViewById(R.id.resPersonRView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        resPersonRView.setLayoutManager(linearLayoutManager);

        selectButton = (Button) findViewById(R.id.confirmButton);

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
                CoCoordinatorListActivity.this.adaptorCoCoordinatorList.filter(s.toString());
            }
        });

    }



    @Override
    protected void onResume() {
        super.onResume();

        String sledCoCoordinator = AppSettings.getInstance().getValue(this, ApConstants.kS_CO_COORDINATOR, ApConstants.kS_CO_COORDINATOR);
        try {
            if (!sledCoCoordinator.equalsIgnoreCase("kS_CO_COORDINATOR")) {
                sledCoCoordinatorJSONArray = new JSONArray(sledCoCoordinator);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        getResourcePersonData();
    }




    private void eventListener() {

        addPersonImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CoCoordinatorListActivity.this, AddEditCoCoordinatorActivity.class);
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
                sledCoCoordinatorJSONArray = new JSONArray();
                try {

                    if (adaptorCoCoordinatorList != null) {

                        for (int i = 0; i < adaptorCoCoordinatorList.mJSONArray.length(); i++) {
                            JSONObject jsonObject = adaptorCoCoordinatorList.mJSONArray.getJSONObject(i);

                            if (jsonObject.getInt("is_selected") == 1) {
                                if (sledCoCoordinatorJSONArray.length() < 1) {
                                    sledCoCoordinatorJSONArray.put(jsonObject);
                                } else {
                                    UIToastMessage.show(CoCoordinatorListActivity.this, "More than 1 Coordinator not allowed");
                                    break;
                                }
                            }
                        }

                        if (sledCoCoordinatorJSONArray.length() > 0) {
                            AppSettings.getInstance().setValue(CoCoordinatorListActivity.this, ApConstants.kS_CO_COORDINATOR, sledCoCoordinatorJSONArray.toString());
                            finish();
                        } else {
                            UIToastMessage.show(CoCoordinatorListActivity.this, "Select at least one Co-Coordinator");
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

        /*if (searchText.equalsIgnoreCase("")){
            UIToastMessage.show(CoCoordinatorListActivity.this, "Please input search text");
        }else*/ if (sDate.equalsIgnoreCase("") || eDate.equalsIgnoreCase("")) {
            UIToastMessage.show(CoCoordinatorListActivity.this, "Please Select start and End date of event");
        } else {
            getResPersonList(sDate, eDate, searchText);
        }

    }


    @Override
    public void onMultiRecyclerViewItemClick(int i, Object o) {

        try {
            JSONObject jsonObject = (JSONObject)o;
            String itemId = jsonObject.getString("id");
            String isSelected = jsonObject.getString("is_selected");

            if (sledCoCoordinatorJSONArray != null){
                for (int k = 0; k< sledCoCoordinatorJSONArray.length(); k++){
                    JSONObject sledJSONObj = sledCoCoordinatorJSONArray.getJSONObject(k);
                    String sledId = sledJSONObj.getString("id");

                    if (sledId.equalsIgnoreCase(itemId) && isSelected.equalsIgnoreCase("0")){
                        sledCoCoordinatorJSONArray.remove(k);
                    }
                }
                // AppSettings.getInstance().setValue(CoCoordinatorListActivity.this, ApConstants.kS_RES_PERSON, sledCoCoordinatorJSONArray.toString());
                AppSettings.getInstance().setValue(CoCoordinatorListActivity.this, ApConstants.kS_CO_COORDINATOR, sledCoCoordinatorJSONArray.toString());
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private JSONArray resPersonWithSelection(JSONArray resPerJSONArray) {

        JSONArray fCoCoordinatorArray = new JSONArray();

        try {

            String cordId = "";
            String sledCordPerson = AppSettings.getInstance().getValue(this, ApConstants.kS_COORDINATOR, ApConstants.kS_COORDINATOR);
            if (!sledCordPerson.equalsIgnoreCase("kS_COORDINATOR")) {
                JSONArray sledCordJSONArray = new JSONArray(sledCordPerson);
                if (sledCordJSONArray.length()>0){
                    cordId  = sledCordJSONArray.getJSONObject(0).getString("id");
                }
            }

            if (sledCoCoordinatorJSONArray.length()>0){

                for (int i = 0; i < sledCoCoordinatorJSONArray.length(); i++) {

                    String itemId = sledCoCoordinatorJSONArray.getJSONObject(i).getString("id");

                    for (int j = 0; j<resPerJSONArray.length();j++){
                        JSONObject jsonObject = resPerJSONArray.getJSONObject(j);
                        String resPerId = jsonObject.getString("id");

                        if (!cordId.equalsIgnoreCase("")){
                            if (!cordId.equalsIgnoreCase(resPerId)){
                                if (resPerId.equalsIgnoreCase(itemId)) {
                                    jsonObject.put("is_selected","1");
                                    fCoCoordinatorArray.put(j,jsonObject);
                                }
                            }
                        }else {
                            if (resPerId.equalsIgnoreCase(itemId)) {
                                jsonObject.put("is_selected","1");
                                fCoCoordinatorArray.put(j,jsonObject);
                            }
                        }
                    }
                }

            }else {

                for (int j = 0; j<resPerJSONArray.length();j++){
                    JSONObject jsonObject = resPerJSONArray.getJSONObject(j);
                    String resPerId = jsonObject.getString("id");

                    if (!cordId.equalsIgnoreCase("")){
                        if (!cordId.equalsIgnoreCase(resPerId)){
                            fCoCoordinatorArray.put(jsonObject);
                        }
                    }else {
                        fCoCoordinatorArray.put(jsonObject);
                    }
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return fCoCoordinatorArray;
    }


    private void getResourcePersonData() {
        String sDate = getIntent().getStringExtra("startDate");
        String eDate = getIntent().getStringExtra("endDate");

        if (!sDate.equalsIgnoreCase("") && !eDate.equalsIgnoreCase("")) {
            getResPersonList(sDate, eDate,"");
        } else {
            UIToastMessage.show(CoCoordinatorListActivity.this, "Please Select End date");
        }
    }


    private void getResPersonList(String sDate, String eDate, String searchText) {

        String desigId =  getIntent().getStringExtra("desigId");
        String sledGpId = getIntent().getStringExtra("gp_id");
        String sledVillageId = getIntent().getStringExtra("village_code");
        JSONArray sledPoOffMemArray = new JSONArray();
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
            jsonObject.put("village_code", sledVillageId);
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
//        Call<JsonObject> responseCall = apiRequest.resPerListRequest(requestBody);
        Call<JsonObject> responseCall = apiRequest.cordListRequest(requestBody);

        DebugLog.getInstance().d("event_res_per_list=" + responseCall.request().toString());
        DebugLog.getInstance().d("event_res_per_list=" + AppUtility.getInstance().bodyToString(responseCall.request()));

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
                    ResponseModel eventListRMode = new ResponseModel(jsonObject);
                    if (eventListRMode.isStatus()) {
                        JSONArray resPerJSONArray = jsonObject.getJSONArray("data");
                        if (resPerJSONArray.length() > 0) {
                            JSONArray coCoordinatorSelected = resPersonWithSelection(resPerJSONArray);
                            adaptorCoCoordinatorList = new AdaptorCoCoordinatorList(this, coCoordinatorSelected, this);
                            resPersonRView.setAdapter(adaptorCoCoordinatorList);
                        }else {
                            adaptorCoCoordinatorList = new AdaptorCoCoordinatorList(this, new JSONArray(), this);
                            resPersonRView.setAdapter(adaptorCoCoordinatorList);
                        }
                    }else {
                        adaptorCoCoordinatorList = new AdaptorCoCoordinatorList(this, new JSONArray(), this);
                        resPersonRView.setAdapter(adaptorCoCoordinatorList);
                        UIToastMessage.show(this,eventListRMode.getMsg());
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
