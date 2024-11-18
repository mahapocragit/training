package in.gov.pocra.training.activity.common.district_list;

import android.content.Intent;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import in.co.appinventor.services_api.api.AppinventorIncAPI;
import in.co.appinventor.services_api.listener.ApiJSONObjCallback;
import in.co.appinventor.services_api.listener.OnMultiRecyclerItemClickListener;
import in.co.appinventor.services_api.settings.AppSettings;
import in.co.appinventor.services_api.widget.UIToastMessage;
import in.gov.pocra.training.R;
import in.gov.pocra.training.activity.pmu.pmu_report.PmuReportActivity;
import in.gov.pocra.training.activity.pmu.pmu_upcoming_event.PmuComingEventActivity;
import in.gov.pocra.training.activity.ps_hrd.add_edit_event_ps.vcrmc_mem_selection.SubDivListActivity;
import in.gov.pocra.training.model.online.ResponseModel;
import in.gov.pocra.training.util.ApConstants;
import in.gov.pocra.training.web_services.APIServices;

public class DistrictListActivity extends AppCompatActivity implements ApiJSONObjCallback, OnMultiRecyclerItemClickListener {

    private ImageView homeBack;
    private String roleId;
    private String userID;
    private RecyclerView districtRView;
    private String actType = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_district_list);

        /** For actionbar title in center */
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.attendance_actionbar_layout);
        AppCompatTextView actionTitleTextView = (AppCompatTextView) getSupportActionBar().getCustomView().findViewById(R.id.actionTitleTextView);
        homeBack = (ImageView) getSupportActionBar().getCustomView().findViewById(R.id.backImageView);
        // addPersonImageView = (ImageView) getSupportActionBar().getCustomView().findViewById(R.id.addPersonImageView);
        homeBack.setVisibility(View.VISIBLE);
        //  addPersonImageView.setVisibility(View.VISIBLE);
        actionTitleTextView.setText("Select District");

        initialization();
        defaultConfiguration();
        eventListener();
    }

    private void initialization() {

        homeBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        String rId = AppSettings.getInstance().getValue(this, ApConstants.kROLE_ID, ApConstants.kROLE_ID);
        String uId = AppSettings.getInstance().getValue(this, ApConstants.kUSER_ID, ApConstants.kUSER_ID);
        if (!rId.equalsIgnoreCase("kROLE_ID")) {
            roleId = rId;
        }
        if (!uId.equalsIgnoreCase("kUSER_ID")) {
            userID = uId;
        }

        districtRView = (RecyclerView) findViewById(R.id.districtRView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        districtRView.setLayoutManager(linearLayoutManager);

    }

    @Override
    protected void onResume() {
        super.onResume();

        actType = getIntent().getStringExtra("actType");
        getDistrictList();
    }

    private void defaultConfiguration() {

    }

    private void eventListener() {

    }

    // get DISTRICT
    private void getDistrictList() {
        AppinventorIncAPI api = new AppinventorIncAPI(this, APIServices.API_URL, "", ApConstants.kMSG, true);
        api.getRequestData(APIServices.GET_DIST_URL, this, 1);
    }


    @Override
    public void onMultiRecyclerViewItemClick(int i, Object o) {

        if (i == 1){
            JSONObject distJSON = (JSONObject)o;

            try {
                String distId = distJSON.getString("id");

                if (!distId.equalsIgnoreCase("")){

                    if (!actType.equalsIgnoreCase("") && actType.equalsIgnoreCase("upComingEvent")){
                        Intent intent = new Intent(DistrictListActivity.this, PmuComingEventActivity.class);
                        intent.putExtra("level","");
                        intent.putExtra("distId",distId);
                        intent.putExtra("eventType",ApConstants.kS_ALL_EVENT);
                        startActivity(intent);
                    }else if (!actType.equalsIgnoreCase("") && actType.equalsIgnoreCase("report")){
                        Intent intent = new Intent(DistrictListActivity.this, PmuReportActivity.class);
                        intent.putExtra("level","");
                        intent.putExtra("distId",distId);
                        intent.putExtra("eventType",ApConstants.kS_ALL_EVENT);
                        startActivity(intent);
                    }else if (!actType.equalsIgnoreCase("") && actType.equalsIgnoreCase("PMU_VCRMC")){
                        AppSettings.getInstance().setValue(this, ApConstants.kUSER_DIST_ID, distId);
                        Intent intent = new Intent(DistrictListActivity.this, SubDivListActivity.class);
                        startActivity(intent);
                    }else if (!actType.equalsIgnoreCase("") && actType.equalsIgnoreCase("PMU_FIELD_STAFF")){
                        AppSettings.getInstance().setValue(this, ApConstants.kUSER_DIST_ID, distId);
                        Intent intent = new Intent(DistrictListActivity.this, SubDivListActivity.class);
                        startActivity(intent);
                    }

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }


    @Override
    public void onResponse(JSONObject jsonObject, int i) {
        try {

            if (jsonObject != null) {
                // District Response
                if (i == 1) {
                    ResponseModel responseModel = new ResponseModel(jsonObject);
                    // String status = jsonObject.getString("status");
                    if (responseModel.isStatus()) {
                        JSONArray distJSONArray = jsonObject.getJSONArray("data");
                        if (distJSONArray.length()>0){
                            AdaptorDistrictList adaptorDistrictList = new AdaptorDistrictList(this,distJSONArray,this);
                            districtRView.setAdapter(adaptorDistrictList);
                        }

                    }else {
                        UIToastMessage.show(this,responseModel.getMsg());
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFailure(Throwable throwable, int i) {

    }


}
