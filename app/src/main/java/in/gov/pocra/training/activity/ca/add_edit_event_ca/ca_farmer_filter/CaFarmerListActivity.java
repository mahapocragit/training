package in.gov.pocra.training.activity.ca.add_edit_event_ca.ca_farmer_filter;

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

public class CaFarmerListActivity extends AppCompatActivity implements OnMultiRecyclerItemClickListener, ApiCallbackCode {
    // ApiCallbackCode,

    private ImageView homeBack;
    EventDataBase eDB;
    private TextView checkAllTView;
    private Boolean allSelected = false;
    private JSONArray farmerWithSelected = new JSONArray();
    private RecyclerView farmerRView;
    private String villageName = "";
    private String villageCode = "";
    private String activityId = "";
    private String activityName = "";
    private Button confirmButton;
    private JSONArray sledFarmerJSONArray = new JSONArray();
    private AdaptorCaFarmerList adaptorCaFarmerList;



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

        String villageName = getIntent().getStringExtra("viName");
        if (villageName.equalsIgnoreCase("")) {
            actionTitleTextView.setText("Selected Farmer");
        } else {
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

        farmerRView = (RecyclerView) findViewById(R.id.farmerRView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
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
                    getFarmerList();

                } else {
                    UIToastMessage.show(CaFarmerListActivity.this, "No member for selection");
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

        villageName = getIntent().getStringExtra("viName");
        villageCode = getIntent().getStringExtra("viCode");
        activityId = getIntent().getStringExtra("activityId");
        activityName = getIntent().getStringExtra("activityName");
        if (!villageCode.equalsIgnoreCase("") && !activityId.equalsIgnoreCase("")) {
            getFarmerList();
        }

    }


    private void confirmButtonAction() {

        sledFarmerJSONArray = new JSONArray();
        try {

            if (adaptorCaFarmerList != null) {

                for (int i = 0; i < adaptorCaFarmerList.mJSONArray.length(); i++) {
                    JSONObject jsonObject = adaptorCaFarmerList.mJSONArray.getJSONObject(i);

                    if (jsonObject.getInt("is_selected") == 1 ) {
                        if (sledFarmerJSONArray.length() < 101) {
                            sledFarmerJSONArray.put(jsonObject);
                        } else {
                            UIToastMessage.show(CaFarmerListActivity.this, "More than 100 farmer not allowed");
                            break;
                        }
                    }
                }

                if (sledFarmerJSONArray.length() > 0) {
                    AppSettings.getInstance().setValue(CaFarmerListActivity.this, ApConstants.kS_FARMER_ARRAY, sledFarmerJSONArray.toString());
                    AppSettings.getInstance().setValue(CaFarmerListActivity.this, ApConstants.kS_COORDINATOR, ApConstants.kS_COORDINATOR);
                    finish();
                } else {
                    UIToastMessage.show(CaFarmerListActivity.this, "Select at least one farmer");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Override
    public void onMultiRecyclerViewItemClick(int i, Object o) {

        try {
            JSONObject jsonObject = (JSONObject) o;
            String itemId = jsonObject.getString("id");
            String isSelected = jsonObject.getString("is_selected");

            if (sledFarmerJSONArray != null) {

                for (int k = 0; k < sledFarmerJSONArray.length(); k++) {
                    JSONObject sledJSONObj = sledFarmerJSONArray.getJSONObject(k);
                    String sledId = sledJSONObj.getString("id");

                    if (sledId.equalsIgnoreCase(itemId) && isSelected.equalsIgnoreCase("0")) {
                        sledFarmerJSONArray.remove(k);
                    }
                }

                AppSettings.getInstance().setValue(CaFarmerListActivity.this, ApConstants.kS_FARMER_ARRAY, sledFarmerJSONArray.toString());

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
                    if (eDB.isFarmerAndActSled(farmerId,activityId)) {
                        farmerJSon.put("is_selected", "1");
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

            if (!vData.equalsIgnoreCase("")) {

                JSONObject viJSON = new JSONObject(vData);
                String villageID = viJSON.getString("id");
                String villageName = viJSON.getString("name");
                String villageCode = viJSON.getString("code");
                String villageIsSelected = viJSON.getString("is_selected");

                for (int i = 0; i < farmerJSONArray.length(); i++) {
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
                        eDB.insertFarmerDetail(villageID, villageName, villageCode, villageIsSelected, activityId, activityName, actv_is_selected, farmerId,
                                farmerName, fMobile, genderId, "", designationId, designationName, farmerIsSelected);
                    } else {
                        eDB.updateFarmerDetail(villageID, villageName, villageCode, villageIsSelected, activityId, activityName, actv_is_selected, farmerId,
                                farmerName, fMobile, genderId, "", designationId, designationName, farmerIsSelected);
                    }
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void getFarmerList() {

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
                    if (fLModel.isStatus()) {
                        JSONArray farmerJSONArray = jsonObject.getJSONArray("data");
                        if (farmerJSONArray.length() > 0) {

                            farmerWithSelected = farmerArrayWithSelection(farmerJSONArray);
                            adaptorCaFarmerList = new AdaptorCaFarmerList(this, farmerWithSelected, this, activityId, activityName);
                            farmerRView.setAdapter(adaptorCaFarmerList);
                            insertFarmerDetail(farmerWithSelected);
                        }
                    } else {
                        UIToastMessage.show(this, fLModel.getMsg());
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

    // For Farmer Response

    /*@Override
    public void onSuccess(String s) {

        if (!s.equalsIgnoreCase("") && !s.equalsIgnoreCase("[]")){
            try {
                JSONArray data = new JSONArray(s);
                if (data.length()>0){
                    adaptorCaFarmerList = new AdaptorCaFarmerList(this, data, this, activityId);
                    farmerRView.post(new Runnable() {
                        @Override
                        public void run() {
                            farmerRView.setAdapter(adaptorCaFarmerList);
                        }
                    });
                }
//                else {
//                    this.runOnUiThread(new Runnable() {
//                        public void run() {
//                            UIToastMessage.show(CaFarmerListActivity.this,"Farmer not found");
//                        }
//                    });
//                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else {
            this.runOnUiThread(new Runnable() {
                public void run() {
                    UIToastMessage.show(CaFarmerListActivity.this,"Farmer not found");
                    onStop();
                }

            });
        }

    }

    @Override
    public void onError(int i, String s) {

    }*/
}
