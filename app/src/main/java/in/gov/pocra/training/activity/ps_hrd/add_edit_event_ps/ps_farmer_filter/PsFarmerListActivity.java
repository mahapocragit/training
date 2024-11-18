package in.gov.pocra.training.activity.ps_hrd.add_edit_event_ps.ps_farmer_filter;

import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
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
import in.co.appinventor.services_api.listener.OnMultiRecyclerItemClickListener;
import in.co.appinventor.services_api.settings.AppSettings;
import in.co.appinventor.services_api.widget.UIToastMessage;
import in.gov.pocra.training.R;
import in.gov.pocra.training.event_db.EventDataBase;
import in.gov.pocra.training.model.online.ResponseModel;
import in.gov.pocra.training.util.ApConstants;
import in.gov.pocra.training.web_services.APIRequest;
import in.gov.pocra.training.web_services.APIServices;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Retrofit;

public class PsFarmerListActivity extends AppCompatActivity implements ApiCallbackCode, OnMultiRecyclerItemClickListener {

    private ImageView homeBack;
    EventDataBase eDB;
    private RecyclerView farmerRView;
    private TextView checkAllTView;
    private Boolean allSelected = false;
    private JSONArray farmerWithSelected = new JSONArray();
    private String villageCode = "";
    private String activityId = "";
    private String activityName = "";
    private Button confirmButton;
    private JSONArray sledFarmerJSONArray = new JSONArray();
    private AdaptorPsFarmerList adaptorPsFarmerList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_village_farmer_list);


        /** For actionbar title in center */
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.attendance_actionbar_layout);
        AppCompatTextView actionTitleTextView = (AppCompatTextView) getSupportActionBar().getCustomView().findViewById(R.id.actionTitleTextView);
        homeBack = (ImageView) getSupportActionBar().getCustomView().findViewById(R.id.backImageView);
        // addPersonImageView = (ImageView) getSupportActionBar().getCustomView().findViewById(R.id.addPersonImageView);
        homeBack.setVisibility(View.VISIBLE);

        String villageName =  getIntent().getStringExtra("viName");
        if (villageName.equalsIgnoreCase("")){
            actionTitleTextView.setText("Selected Farmer");
        }else {
            actionTitleTextView.setText(villageName);
        }

        eDB = new EventDataBase(this);
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

        checkAllTView = (TextView) findViewById(R.id.checkAllTView);

        farmerRView = (RecyclerView)findViewById(R.id.farmerRView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        farmerRView.setLayoutManager(linearLayoutManager);

        confirmButton = (Button) findViewById(R.id.confirmButton);
    }

    private void defaultConfiguration() {
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmButtonAction();
            }
        });

        checkAllTView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (farmerWithSelected != null) {

                    try {

                        if (!allSelected) {
                            for (int i = 0; i < farmerWithSelected.length(); i++) {
                                JSONObject memJSONObkject = farmerWithSelected.getJSONObject(i);
                                String farId = memJSONObkject.getString("id");
                                eDB.updateFarmerIsSelected(farId,"1");
                            }

                            allSelected = true;
                            checkAllTView.setText("Deselect All");

                        } else {
                            for (int i = 0; i < farmerWithSelected.length(); i++) {
                                JSONObject memJSONObkject = farmerWithSelected.getJSONObject(i);
                                String farId = memJSONObkject.getString("id");
                                eDB.updateFarmerIsSelected(farId,"0");
                            }
                            allSelected = false;
                            checkAllTView.setText("Select All");
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    getFarmerList(activityId);

                } else {
                    UIToastMessage.show(PsFarmerListActivity.this, "No member for selection");
                }
            }
        });

    }


    @Override
    protected void onResume() {
        super.onResume();


        String sledFarmers = AppSettings.getInstance().getValue(this, ApConstants.kS_FARMER_ARRAY, ApConstants.kS_FARMER_ARRAY);
        try {
            if (!sledFarmers.equalsIgnoreCase("kS_FARMER_ARRAY")) {
                sledFarmerJSONArray = new JSONArray(sledFarmers);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        villageCode = getIntent().getStringExtra("viCode");
        activityId = getIntent().getStringExtra("activityId");
        activityName = getIntent().getStringExtra("activityName");
        if (!villageCode.equalsIgnoreCase("") && !activityId.equalsIgnoreCase("")){
            getFarmerList(activityId);
        }

    }



    private void confirmButtonAction() {

        sledFarmerJSONArray = new JSONArray();
        try {

            if (adaptorPsFarmerList != null) {

                for (int i = 0; i < adaptorPsFarmerList.mJSONArray.length(); i++) {
                    JSONObject jsonObject = adaptorPsFarmerList.mJSONArray.getJSONObject(i);

                    if (jsonObject.getInt("is_selected") == 1) {
                        if (sledFarmerJSONArray.length() < 101) {
                            sledFarmerJSONArray.put(jsonObject);
                        } else {
                            UIToastMessage.show(PsFarmerListActivity.this, "More than 100 farmer not allowed");
                            break;
                        }
                    }
                }

                if (sledFarmerJSONArray.length() > 0) {
                    AppSettings.getInstance().setValue(PsFarmerListActivity.this, ApConstants.kS_FARMER_ARRAY, sledFarmerJSONArray.toString());
                    AppSettings.getInstance().setValue(PsFarmerListActivity.this, ApConstants.kS_COORDINATOR, ApConstants.kS_COORDINATOR);
                    finish();
                } else {
                    UIToastMessage.show(PsFarmerListActivity.this, "Select at least one farmer");
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }



    @Override
    public void onMultiRecyclerViewItemClick(int i, Object o) {

        try {
            JSONObject jsonObject = (JSONObject)o;
            String itemId = jsonObject.getString("id");
            String isSelected = jsonObject.getString("is_selected");

            if (sledFarmerJSONArray != null){
                for (int k = 0; k<sledFarmerJSONArray.length(); k++){
                    JSONObject sledJSONObj = sledFarmerJSONArray.getJSONObject(k);
                    String sledId = sledJSONObj.getString("farmer_id");

                    if (sledId.equalsIgnoreCase(itemId) && isSelected.equalsIgnoreCase("0")){
                        sledFarmerJSONArray.remove(k);
                    }
                }

                AppSettings.getInstance().setValue(PsFarmerListActivity.this, ApConstants.kS_FARMER_ARRAY, sledFarmerJSONArray.toString());

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }



    // To get village array with selected village
    private JSONArray farmerArrayWithSelection(JSONArray farmerJSONArray) {
        JSONArray jsonArray = new JSONArray();
        try {

            if (farmerJSONArray.length() > 0) {

                for (int i = 0; i < farmerJSONArray.length(); i++) {

                    JSONObject farmerJSon = farmerJSONArray.getJSONObject(i);
                    String farmerId = farmerJSon.getString("id");
                    /*if (eDB.isFarmerSled(farmerId)){
                        farmerJSon.put("is_selected","1");
                        jsonArray.put(farmerJSon);
                    }else {
                        jsonArray.put(farmerJSon);
                    }*/

                    if (eDB.isFarmerAndActSled(farmerId,activityId)) {
                        farmerJSon.put("is_selected","1");
                    }
                    jsonArray.put(farmerJSon);

                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonArray;
    }



    private void insertFarmerDetail(JSONArray farmerJSONArray) {

        try {
            String vData = getIntent().getStringExtra("vData");

            if (!vData.equalsIgnoreCase("")){

                JSONObject viJSON = new JSONObject(vData);
                String villageID = viJSON.getString("id");
                String villageName = viJSON.getString("name");
                String villageCode = viJSON.getString("code");
                String villageIsSelected = viJSON.getString("is_selected");


                for (int i = 0; i<farmerJSONArray.length(); i++){
                    JSONObject farJSON = farmerJSONArray.getJSONObject(i);

                    String farmerId = AppUtility.getInstance().sanitizeJSONObj(farJSON, "id");
                    String farmerName = AppUtility.getInstance().sanitizeJSONObj(farJSON, "name");
                    String farmerIsSelected = farJSON.getString("is_selected");
                    String fMobile = AppUtility.getInstance().sanitizeJSONObj(farJSON, "mobile");
                    String designationId = AppUtility.getInstance().sanitizeJSONObj(farJSON, "designation_id");
                    String designationName = AppUtility.getInstance().sanitizeJSONObj(farJSON, "designation_name");
                    String genderId = AppUtility.getInstance().sanitizeJSONObj(farJSON, "gender_id");
                    String actv_is_selected = AppUtility.getInstance().sanitizeJSONObj(farJSON, "is_selected");

                    if (!eDB.isFarmerWithActExist(farmerId,activityId)) {
                        eDB.insertFarmerDetail(villageID,villageName,villageCode,villageIsSelected,activityId,activityName,actv_is_selected,farmerId,
                                farmerName,fMobile,genderId,"",designationId,designationName,farmerIsSelected);
                    }else {
                        /*if (farmerIsSelected.equalsIgnoreCase("1")){
                            eDB.updateFarmerDetail(villageID,villageName,villageCode,villageIsSelected,activityId,activityName,actv_is_selected,farmerId,
                                    farmerName,fMobile,genderId,"",designationId,designationName,farmerIsSelected);
                        }else {
                            eDB.updateFarmerDetail(villageID,villageName,villageCode,villageIsSelected,activityId,activityName,actv_is_selected,farmerId,
                                    farmerName,fMobile,genderId,"",designationId,designationName,farmerIsSelected);
                        }*/

                        eDB.updateFarmerDetail(villageID,villageName,villageCode,villageIsSelected,activityId,activityName,actv_is_selected,farmerId,
                                farmerName,fMobile,genderId,"",designationId,designationName,farmerIsSelected);
                    }
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }




    private void getFarmerList(String activityId) {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("activity_id", activityId);
            jsonObject.put("census_code", villageCode);
            //jsonObject.put("api_key", ApConstants.kAUTHORITY_KEY);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString());
        AppinventorApi api = new AppinventorApi(this, APIServices.API_URL, "", ApConstants.kMSG, true);
        Retrofit retrofit = api.getRetrofitInstance();
        APIRequest apiRequest = retrofit.create(APIRequest.class);
        Call<JsonObject> responseCall = apiRequest.farmerListRequest(requestBody);

        DebugLog.getInstance().d("farmer_list_param=" + responseCall.request().toString());
        DebugLog.getInstance().d("farmer_list_param=" + AppUtility.getInstance().bodyToString(responseCall.request()));

        api.postRequest(responseCall, this, 1);

    }



    @Override
    public void onResponse(JSONObject jsonObject, int i) {

        try {

            if (jsonObject != null) {

                // Event type Response
                if (i == 1) {
                    ResponseModel fLModel = new ResponseModel(jsonObject);
                    if (fLModel.isStatus()){
                        JSONArray farmerJSONArray = jsonObject.getJSONArray("data");
                        if (farmerJSONArray.length() > 0){

                            farmerWithSelected = farmerArrayWithSelection(farmerJSONArray);
                            adaptorPsFarmerList = new AdaptorPsFarmerList(this,farmerWithSelected,this,activityId, activityName);
                            farmerRView.setAdapter(adaptorPsFarmerList);

                            insertFarmerDetail(farmerWithSelected);

                        }
                    }else {
                        UIToastMessage.show(this,fLModel.getMsg());
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
