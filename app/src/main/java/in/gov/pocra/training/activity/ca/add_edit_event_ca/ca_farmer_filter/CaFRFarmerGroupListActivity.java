package in.gov.pocra.training.activity.ca.add_edit_event_ca.ca_farmer_filter;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import in.gov.pocra.training.event_db.EventDataBase;
import in.gov.pocra.training.model.online.ResponseModel;
import in.gov.pocra.training.util.ApConstants;
import in.gov.pocra.training.web_services.APIRequest;
import in.gov.pocra.training.web_services.APIServices;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Retrofit;

public class CaFRFarmerGroupListActivity extends AppCompatActivity implements OnMultiRecyclerItemClickListener, ApiCallbackCode {
    // ApiCallbackCode,

    private ImageView homeBack;
    EventDataBase eDB;
    private TextView checkAllTView;
    private Boolean allSelected = false;
    private JSONArray SHGWithSelected = new JSONArray();
    private RecyclerView farmerRView;
    private String villageName = "";
    private String villageCode = "";
    private String activityId = "";
    private String activityName = "";
    private Button confirmButton;
    private JSONArray sledSHGJSONArray = new JSONArray();
    private AdaptoFRgroupList adaptoFRgroupList;
    String talukaId;

    String flagstr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shggroup_farmer_list);


        /** For actionbar title in center */
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.attendance_actionbar_layout);
        AppCompatTextView actionTitleTextView = (AppCompatTextView) getSupportActionBar().getCustomView().findViewById(R.id.actionTitleTextView);
        homeBack = (ImageView) getSupportActionBar().getCustomView().findViewById(R.id.backImageView);
        // addPersonImageView = (ImageView) getSupportActionBar().getCustomView().findViewById(R.id.addPersonImageView);
        homeBack.setVisibility(View.VISIBLE);
        talukaId = getIntent().getStringExtra("talId");
        Log.d("textttttttt", talukaId);
        String villageName = getIntent().getStringExtra("viName");
        String vData = getIntent().getStringExtra("viCode");
        flagstr = getIntent().getStringExtra("Flag");
        Log.d("Str12121", vData);
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

                if (SHGWithSelected != null) {

                    try {

                        if (!allSelected) {
                            for (int i = 0; i < SHGWithSelected.length(); i++) {
                                JSONObject memJSONObkject = SHGWithSelected.getJSONObject(i);
                                String farId = memJSONObkject.getString("id");
                                eDB.updateFGroupIsSelected(farId, "1");
                            }
                            allSelected = true;
                            checkAllTView.setText("Deselect All");

                        } else {
                            for (int i = 0; i < SHGWithSelected.length(); i++) {
                                JSONObject memJSONObkject = SHGWithSelected.getJSONObject(i);
                                String farId = memJSONObkject.getString("id");
                                eDB.updateFGroupIsSelected(farId, "0");
                            }
                            allSelected = false;
                            checkAllTView.setText("Select All");
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    getFarmergrouplist();
                } else {
                    Toast.makeText(CaFRFarmerGroupListActivity.this, "No member for selection", Toast.LENGTH_SHORT).show();
//                    UIToastMessage.show(CaFRFarmerGroupListActivity.this, "No member for selection");
                }
            }
        });

    }


    @Override
    protected void onResume() {
        super.onResume();

        String sledFarmers = AppSettings.getInstance().getValue(this, ApConstants.kS_farm_PARTICIPANTS_ARRAY, ApConstants.kS_farm_PARTICIPANTS_ARRAY);
        try {
            if (!sledFarmers.equalsIgnoreCase("kS_farm_PARTICIPANTS_ARRAY")) {
                sledSHGJSONArray = new JSONArray(sledFarmers);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        villageName = getIntent().getStringExtra("viName");
        villageCode = getIntent().getStringExtra("viCode");


        getFarmergrouplist();


    }


    private void confirmButtonAction() {

        sledSHGJSONArray = new JSONArray();
        try {

            if (adaptoFRgroupList != null) {

                for (int i = 0; i < adaptoFRgroupList.mJSONArray.length(); i++) {
                    JSONObject jsonObject = adaptoFRgroupList.mJSONArray.getJSONObject(i);

                    if (jsonObject.getInt("is_selected") == 1) {
                        if (sledSHGJSONArray.length() < 101) {
                            sledSHGJSONArray.put(jsonObject);
                            Log.d("textxtxtxtxt", String.valueOf(sledSHGJSONArray));
                        } else {
                            Toast.makeText(this, "More than 100 farmer not allowed", Toast.LENGTH_SHORT).show();
//                            UIToastMessage.show(CaFRFarmerGroupListActivity.this, "More than 100 farmer not allowed");
                            break;
                        }
                    }
                }

                if (sledSHGJSONArray.length() > 0) {

                    AppSettings.getInstance().setValue(CaFRFarmerGroupListActivity.this, ApConstants.kS_farm_PARTICIPANTS_ARRAY, sledSHGJSONArray.toString());


                    finish();
                } else {
                    Toast.makeText(this, "Select at least one farmer", Toast.LENGTH_SHORT).show();
//                    UIToastMessage.show(CaFRFarmerGroupListActivity.this, "Select at least one farmer");
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

            if (sledSHGJSONArray != null) {

                for (int k = 0; k < sledSHGJSONArray.length(); k++) {
                    JSONObject sledJSONObj = sledSHGJSONArray.getJSONObject(k);
                    String sledId = sledJSONObj.getString("id");

                    if (sledId.equalsIgnoreCase(itemId) && isSelected.equalsIgnoreCase("0")) {
                        sledSHGJSONArray.remove(k);
                    }
                }

                AppSettings.getInstance().setValue(CaFRFarmerGroupListActivity.this, ApConstants.kS_farm_PARTICIPANTS_ARRAY, sledSHGJSONArray.toString());

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
                    if (eDB.isFGseld(farmerId)) {
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


    private void insertSHGDetail(JSONArray farmerJSONArray) {

        try {
            String vData = getIntent().getStringExtra("vData");

            if (!vData.equalsIgnoreCase("")) {

                for (int i = 0; i < farmerJSONArray.length(); i++) {
                    JSONObject farJSON = farmerJSONArray.getJSONObject(i);


                    String village_name = AppUtility.getInstance().sanitizeJSONObj(farJSON, "village");
                    String vcode = AppUtility.getInstance().sanitizeJSONObj(farJSON, "village_census_code");
                    String group_name = AppUtility.getInstance().sanitizeJSONObj(farJSON, "group_name");
                    String contact_person = AppUtility.getInstance().sanitizeJSONObj(farJSON, "contact_person");
                    String contact_number = AppUtility.getInstance().sanitizeJSONObj(farJSON, "contact_number");
                    String group_flag = AppUtility.getInstance().sanitizeJSONObj(farJSON, "group_flag");
                    String fg_is_selected = AppUtility.getInstance().sanitizeJSONObj(farJSON, "is_selected");
                    String farmer_id = AppUtility.getInstance().sanitizeJSONObj(farJSON, "id");

                    if (!eDB.isFGseld(farmer_id)) {
                        eDB.insertFarmerGroup(village_name, vcode, group_name, contact_person, contact_number, group_flag, fg_is_selected, farmer_id);
                    } else {
                        eDB.updateFarmerGroup(village_name, vcode, group_name, contact_person, contact_number, group_flag, fg_is_selected, farmer_id);

                    }

                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void getFarmergrouplist() {

        JSONObject jsonObject = new JSONObject();
        try {

            jsonObject.put("village_census_code", villageCode);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString());
        AppinventorApi api = new AppinventorApi(this, APIServices.API_URL, "", ApConstants.kMSG, true);
        Retrofit retrofit = api.getRetrofitInstance();
        APIRequest apiRequest = retrofit.create(APIRequest.class);
        Call<JsonObject> responseCall = apiRequest.GetFarmlist(requestBody);

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
                        JSONArray SHGJSONArray = jsonObject.getJSONArray("data");
                        if (SHGJSONArray.length() > 0) {

                            SHGWithSelected = farmerArrayWithSelection(SHGJSONArray);
                            adaptoFRgroupList = new AdaptoFRgroupList(this, SHGWithSelected, this, activityId, activityName);
                            farmerRView.setAdapter(adaptoFRgroupList);
                            insertSHGDetail(SHGWithSelected);
                        }
                    } else {
                        Toast.makeText(this, ""+fLModel.getMsg(), Toast.LENGTH_SHORT).show();
//                        UIToastMessage.show(this, fLModel.getMsg());
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


}
