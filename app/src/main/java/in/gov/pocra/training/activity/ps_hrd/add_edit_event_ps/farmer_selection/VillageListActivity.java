package in.gov.pocra.training.activity.ps_hrd.add_edit_event_ps.farmer_selection;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import in.co.appinventor.services_api.api.AppinventorApi;
import in.co.appinventor.services_api.app_util.AppUtility;
import in.co.appinventor.services_api.debug.DebugLog;
import in.co.appinventor.services_api.listener.ApiCallbackCode;
import in.co.appinventor.services_api.listener.ApiJSONObjCallback;
import in.co.appinventor.services_api.listener.OnMultiRecyclerItemClickListener;
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


public class VillageListActivity extends AppCompatActivity implements ApiJSONObjCallback, ApiCallbackCode, OnMultiRecyclerItemClickListener {

    private ImageView homeBack;
    EventDataBase eDB;
    private String talukaID = "";
    private RecyclerView villageRecyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_village_list);

        /** For actionbar title in center */
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.attendance_actionbar_layout);
        AppCompatTextView actionTitleTextView = (AppCompatTextView) getSupportActionBar().getCustomView().findViewById(R.id.actionTitleTextView);
        homeBack = (ImageView) getSupportActionBar().getCustomView().findViewById(R.id.backImageView);
        // addPersonImageView = (ImageView) getSupportActionBar().getCustomView().findViewById(R.id.addPersonImageView);
        homeBack.setVisibility(View.VISIBLE);
        actionTitleTextView.setText("Select Farmer");

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

        villageRecyclerView = (RecyclerView)findViewById(R.id.villageRecyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        villageRecyclerView.setLayoutManager(linearLayoutManager);

    }

    private void defaultConfiguration() {

    }


    @Override
    protected void onResume() {
        super.onResume();

        talukaID = getIntent().getStringExtra("talukaId");
        if (!talukaID.equalsIgnoreCase("")){
            getVillageList(talukaID);
        }

    }

    @Override
    public void onMultiRecyclerViewItemClick(int i, Object o) {

    }



    // To get village array with selected village
    private JSONArray getFinalVillageArray(JSONArray sledVillageJSONArray) {
        JSONArray jsonArray = new JSONArray();

        try {

            if (sledVillageJSONArray.length() > 0) {

                for (int i = 0; i < sledVillageJSONArray.length(); i++) {

                    JSONObject villageJSon = sledVillageJSONArray.getJSONObject(i);
                    String viId = villageJSon.getString("id");
                    if (eDB.isVillageSled(viId)){
                        villageJSon.put("is_selected","1");
                        jsonArray.put(villageJSon);
                    }else {
                        jsonArray.put(villageJSon);
                    }

                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonArray;
    }




    // get VCRMC Committee (GP)
    private void getVillageList(String talID) {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("taluka_id", talID);
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

        api.postRequest(responseCall, this, 1);

    }



    @Override
    public void onResponse(JSONObject jsonObject, int i) {

        try {

            if (jsonObject != null) {

                // Village Response
                if (i == 1) {
                    ResponseModel responseModel = new ResponseModel(jsonObject);
                    if (responseModel.isStatus()) {
                        JSONArray villageJSONArray = jsonObject.getJSONArray("data");

                        if (villageJSONArray != null) {
                            // To set selected village as selected
                            JSONArray fSledVillageArray = getFinalVillageArray(villageJSONArray);
                            AdaptorVillageList adaptorVillageList = new AdaptorVillageList(VillageListActivity.this, fSledVillageArray, VillageListActivity.this);
                            villageRecyclerView.setAdapter(adaptorVillageList);
                        }else {
                            UIToastMessage.show(VillageListActivity.this, "Village not found");
                        }
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

    @Override
    public void onFailure(Throwable throwable, int i) {

    }




}
