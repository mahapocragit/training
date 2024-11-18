package in.gov.pocra.training.activity.common.coordinator_list;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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
import in.gov.pocra.training.R;
import in.gov.pocra.training.model.online.ResponseModel;
import in.gov.pocra.training.util.ApConstants;
import in.gov.pocra.training.util.ApUtil;
import in.gov.pocra.training.web_services.APIRequest;
import in.gov.pocra.training.web_services.APIServices;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Retrofit;

public class SearchPmuMemActivity extends AppCompatActivity implements ApiCallbackCode, OnMultiRecyclerItemClickListener {

    private ImageView homeBack;
    private String roleId;
    private String userID;

    private String center;
    private String location;
    private String participentRoleId;

    private JSONArray sledCordJSONArray = new JSONArray();
    private RecyclerView coordinatorRView;
    private Button selectButton;
    private AdaptorPmuCoordinatorList adaptorPmuCoordinatorList;
    private ImageView searchIView;
    private EditText searchEText;
    private String sDate = "";
    private String eDate = "";
    private JSONArray cordWithSelected = new JSONArray();
    private String sType = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_pmu_mem);


        /** For actionbar title in center */
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.attendance_actionbar_layout);
        AppCompatTextView actionTitleTextView = (AppCompatTextView) getSupportActionBar().getCustomView().findViewById(R.id.actionTitleTextView);
        homeBack = (ImageView) getSupportActionBar().getCustomView().findViewById(R.id.backImageView);
        // addPersonImageView = (ImageView) getSupportActionBar().getCustomView().findViewById(R.id.addPersonImageView);
        homeBack.setVisibility(View.VISIBLE);
        // addPersonImageView.setVisibility(View.VISIBLE);
        sType = getIntent().getStringExtra("selectionType");

        Log.d("MAYUUU","sType=="+sType);

        if (sType.equalsIgnoreCase("coordinator")){
            actionTitleTextView.setText(getResources().getString(R.string.title_coordinator));
        }else {
            actionTitleTextView.setText("Co-coordinator");
        }
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


    @Override
    protected void onResume() {
        super.onResume();

        if (sType.equalsIgnoreCase("kS_CO_COORDINATOR")){
            String sledCordPerson = AppSettings.getInstance().getValue(this, ApConstants.kS_CO_COORDINATOR, ApConstants.kS_CO_COORDINATOR);
            try {
                if (!sledCordPerson.equalsIgnoreCase("kS_CO_COORDINATOR")) {
                    sledCordJSONArray = new JSONArray(sledCordPerson);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else {
            String sledCordPerson = AppSettings.getInstance().getValue(this, ApConstants.kS_COORDINATOR, ApConstants.kS_COORDINATOR);
            try {
                if (!sledCordPerson.equalsIgnoreCase("kS_COORDINATOR")) {
                    sledCordJSONArray = new JSONArray(sledCordPerson);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }




        sDate = getIntent().getStringExtra("startDate");
        eDate = getIntent().getStringExtra("endDate");

        /*center = getIntent().getStringExtra("center");
        location = getIntent().getStringExtra("location");
        participentRoleId = getIntent().getStringExtra("pRoleId");

        if (!center.equalsIgnoreCase("") && !location.equalsIgnoreCase("")  && !participentRoleId.equalsIgnoreCase("") ) {
            // getFacilitatorDetail(talukaId);

            getParticipantByRoleAndLocation(center,location,participentRoleId);
        }*/

        if (cordWithSelected.length() > 0){
            selectButton.setVisibility(View.VISIBLE);
        }else {
            selectButton.setVisibility(View.GONE);
        }

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
                String mobile = searchEText.getText().toString();
                if (mobile.length() >= 10){
                    ApUtil.hideKeybord(searchIView,SearchPmuMemActivity.this);
                    searchIViewAction();
                }

            }
        });
    }


    private void eventListener() {

        searchEText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchEText.setCursorVisible(true);
            }
        });

        searchIView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ApUtil.hideKeybord(searchIView,SearchPmuMemActivity.this);
                searchIViewAction();
            }
        });


        selectButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sledCordJSONArray = new JSONArray();
                try {

                    if (adaptorPmuCoordinatorList != null) {
                        boolean leastMsg  = true;
                        for (int i = 0; i < adaptorPmuCoordinatorList.mJSONArray.length(); i++) {
                            JSONObject jsonObject = adaptorPmuCoordinatorList.mJSONArray.getJSONObject(i);
                            //String roleId = jsonObject.getString("role_id");

                            if (jsonObject.getInt("is_selected") == 1) {

                                if (sledCordJSONArray.length() < 1 ) {
                                    sledCordJSONArray.put(jsonObject);
                                    leastMsg = true;
                                } else {
//                                    UIToastMessage.show(SearchPmuMemActivity.this, "More than 1 Coordinator not allowed");
                                    Toast.makeText(SearchPmuMemActivity.this, "More than 1 Coordinator not allowed", Toast.LENGTH_SHORT).show();
                                    //break;
                                }

                                /*if (roleId.equalsIgnoreCase("15") || roleId.equalsIgnoreCase("14")){
                                    UIToastMessage.show(SearchPmuMemActivity.this, "Selected member can not assigned as coordinator/Co-coordinator");
                                    leastMsg = false;
                                }else {

                                    if (sledCordJSONArray.length() < 1) {
                                        sledCordJSONArray.put(jsonObject);
                                        leastMsg = true;
                                    } else {
                                        UIToastMessage.show(SearchPmuMemActivity.this, "More than 1 Coordinator not allowed");
                                        //break;
                                    }
                                }*/

                            }
                        }

                        if (sledCordJSONArray.length() > 0) {
                            if (sType.equalsIgnoreCase("co-coordinator")){
                                AppSettings.getInstance().setValue(SearchPmuMemActivity.this, ApConstants.kS_CO_COORDINATOR, sledCordJSONArray.toString());
                            }else {
                                AppSettings.getInstance().setValue(SearchPmuMemActivity.this, ApConstants.kS_COORDINATOR, sledCordJSONArray.toString());
                            }

                            finish();
                        } else {
                            if (leastMsg){
//                                UIToastMessage.show(SearchPmuMemActivity.this, "Select at least one Coordinator");
                                Toast.makeText(SearchPmuMemActivity.this, "Select at least one Coordinator", Toast.LENGTH_SHORT).show();
                            }

                        }

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

    }



    private void searchIViewAction() {

        String searchMobile = searchEText.getText().toString().trim();

        if (searchMobile.equalsIgnoreCase("")) {
//            UIToastMessage.show(SearchPmuMemActivity.this, "Please enter a PoCRA official's mobile number");
            Toast.makeText(this, "Please enter a PoCRA official's mobile number", Toast.LENGTH_SHORT).show();
        } else if (!AppUtility.getInstance().isValidPhoneNumber(searchMobile)) {
//            UIToastMessage.show(SearchPmuMemActivity.this, "Please enter valid mobile number");
            Toast.makeText(this, "Please enter valid mobile number", Toast.LENGTH_SHORT).show();
        }
//        else if (sType.equalsIgnoreCase("co-coordinator")) {
//
//            String sledCoordinator = AppSettings.getInstance().getValue(this, ApConstants.kS_COORDINATOR, ApConstants.kS_COORDINATOR);
//
//            try {
//                if (sledCordJSONArray != null) {
//                    sledCordJSONArray = new JSONArray(sledCoordinator);
//                    for (int i = 0; i < sledCordJSONArray.length(); i++) {
//                        JSONObject cordDetail = sledCordJSONArray.getJSONObject(i);
//                        String check_selected_cordinator = "";
//                        try {
//
//                            check_selected_cordinator = cordDetail.getString("username");
//////or condition apply
//
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//
//
//                        if(check_selected_cordinator.equalsIgnoreCase(searchMobile))
//                        {
////                            UIToastMessage.show(SearchPmuMemActivity.this, "This person already assigned as a Coordinator");
//                            Toast.makeText(this, "This person already assigned as a Coordinator", Toast.LENGTH_SHORT).show();
//
//                        }
//                        else
//                        {
//                            getMemberDetail(searchMobile);
//                        }
//                    }
//                }
//
//
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        } else {
            getMemberDetail(searchMobile);
//        }

    }

    @Override
    public void onMultiRecyclerViewItemClick(int i, Object o) {

    }


    private void getMemberDetail(String searchMobile) {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("mobile", searchMobile);
            jsonObject.put("start_date", sDate);
            jsonObject.put("end_date", eDate);
            jsonObject.put("api_key", ApConstants.kAUTHORITY_KEY);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString());
        AppinventorApi api = new AppinventorApi(this, APIServices.SERVICE_API_URL, "", ApConstants.kMSG, true);
        Retrofit retrofit = api.getRetrofitInstance();
        APIRequest apiRequest = retrofit.create(APIRequest.class);
        Call<JsonObject> responseCall = apiRequest.officialCordListRequest(requestBody);

        DebugLog.getInstance().d("pocra_official_mem_detail_param=" + responseCall.request().toString());
        DebugLog.getInstance().d("pocra_official_mem_detail_param=" + AppUtility.getInstance().bodyToString(responseCall.request()));

        api.postRequest(responseCall, this, 1);
    }



    private JSONArray cordArrayWithSelection(JSONArray coordinatorJSONArray) {

        try {

            if (sledCordJSONArray.length()>0){

                for (int i = 0; i < sledCordJSONArray.length(); i++) {

                    String itemId = sledCordJSONArray.getJSONObject(i).getString("id");
                    Log.d("MAYUUU","cordArrayWithSelection itemId="+itemId);

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



    @Override
    public void onResponse(JSONObject jsonObject, int i) {

        try {

            if (jsonObject != null) {

                // Event type Response
                if (i == 1) {

                    // {"status":200,"response":"Officer data","data":{"id":5772,"role_id":9,"username":"7620080142","email":null,"first_name":"P","middle_name":"D","last_name":"Khandagale","post":"Agriculture Assistant","post_short_name":"Agriculture Assistant","who_create_user":12,"who_assign_location":12,"is_active":1,"is_delete":0,"is_selected":0,"jurisdiction":[]}}


                    ResponseModel pOfficalLModel = new ResponseModel(jsonObject);

                    if (pOfficalLModel.isStatus()) {
                        JSONObject dataJSON = jsonObject.getJSONObject("data");
                        JSONArray coordinatorJSONArray = new JSONArray();
                        coordinatorJSONArray.put(dataJSON);

                        if (coordinatorJSONArray.length() >0){
                            cordWithSelected = cordArrayWithSelection(coordinatorJSONArray);
                            if (cordWithSelected.length() > 0){
                                selectButton.setVisibility(View.VISIBLE);
                            }else {
                                selectButton.setVisibility(View.GONE);
                            }
                            adaptorPmuCoordinatorList = new AdaptorPmuCoordinatorList(this,cordWithSelected,this);
                            coordinatorRView.setAdapter(adaptorPmuCoordinatorList);
                        }else {
                            adaptorPmuCoordinatorList = new AdaptorPmuCoordinatorList(this,new JSONArray(),this);
                            coordinatorRView.setAdapter(adaptorPmuCoordinatorList);
                        }
                    } else {
                        adaptorPmuCoordinatorList = new AdaptorPmuCoordinatorList(this,new JSONArray(),this);
                        coordinatorRView.setAdapter(adaptorPmuCoordinatorList);
                        Toast.makeText(this, ""+pOfficalLModel.getMsg(), Toast.LENGTH_SHORT).show();
//                        UIToastMessage.show(this, pOfficalLModel.getMsg());
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
