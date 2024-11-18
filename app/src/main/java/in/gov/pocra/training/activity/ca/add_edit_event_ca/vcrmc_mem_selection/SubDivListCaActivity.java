package in.gov.pocra.training.activity.ca.add_edit_event_ca.vcrmc_mem_selection;

import android.content.Intent;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
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
import in.gov.pocra.training.activity.ps_hrd.add_edit_event_ps.vcrmc_mem_selection.AdaptorSubDivList;
import in.gov.pocra.training.activity.ps_hrd.add_edit_event_ps.vcrmc_mem_selection.TalukaListActivity;
import in.gov.pocra.training.util.ApConstants;
import in.gov.pocra.training.web_services.APIServices;

public class SubDivListCaActivity extends AppCompatActivity implements ApiJSONObjCallback, OnMultiRecyclerItemClickListener {

    private ImageView homeBack;

    private String districtID = "";                 // 6 For Aurangabad Dist Only for Testing purpose
    private RecyclerView subDivRView;
    private AppCompatTextView actionTitleTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_div_list_ca);

        /* ** For actionbar title in center ***/
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.attendance_actionbar_layout);
        actionTitleTextView = (AppCompatTextView) getSupportActionBar().getCustomView().findViewById(R.id.actionTitleTextView);
        homeBack = (ImageView) getSupportActionBar().getCustomView().findViewById(R.id.backImageView);
        homeBack.setVisibility(View.VISIBLE);


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

        String eventMemType = AppSettings.getInstance().getValue(this, ApConstants.kEVENT_MEM_TYPE,ApConstants.kEVENT_MEM_TYPE);
        if (!eventMemType.equalsIgnoreCase("") && eventMemType.equalsIgnoreCase(ApConstants.kEVENT_MEM_VCRMC)){
            actionTitleTextView.setText("VCRMC (GP)");
        }else if (!eventMemType.equalsIgnoreCase("") && eventMemType.equalsIgnoreCase(ApConstants.kEVENT_MEM_FARMER)){
            actionTitleTextView.setText("Farmer");
        }else if (!eventMemType.equalsIgnoreCase("") && eventMemType.equalsIgnoreCase(ApConstants.kEVENT_MEM_FFS_F)){
            actionTitleTextView.setText("FFS Facilitator");
        }else if (!eventMemType.equalsIgnoreCase("") && eventMemType.equalsIgnoreCase(ApConstants.kEVENT_MEM_PFS)){
            actionTitleTextView.setText("Pocra Field Staff");
        }

        String userDisID = AppSettings.getInstance().getValue(this, ApConstants.kUSER_DIST_ID, ApConstants.kUSER_DIST_ID);
        if (!userDisID.equalsIgnoreCase("kUSER_DIST_ID")) {
            districtID = userDisID;
        }

        subDivRView = (RecyclerView)findViewById(R.id.subDivRView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        subDivRView.setLayoutManager(linearLayoutManager);

    }

    private void defaultConfiguration() {
        getSubDivisionList(districtID);
    }

    @Override
    public void onMultiRecyclerViewItemClick(int i, Object o) {
        try {
            JSONObject jsonObject =  (JSONObject) o;
            String subDivId = jsonObject.getString("id");
            String subDivName = jsonObject.getString("name");

            Intent intent = new Intent(this, TalukaListActivity.class);
            intent.putExtra("subDivId",subDivId);
            intent.putExtra("subDivName",subDivName);
            startActivity(intent);
            //  finish();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    // get Sub-division
    private void getSubDivisionList(String distId) {
        String subDivisionUrl = APIServices.GET_SUB_DIV_URL + distId;
        AppinventorIncAPI api = new AppinventorIncAPI(this, APIServices.API_URL, "", ApConstants.kMSG, true);
        api.getRequestData(subDivisionUrl, this, 1);
        Log.d("param",""+subDivisionUrl);
    }


    @Override
    public void onResponse(JSONObject jsonObject, int i) {
        try {

            if (jsonObject != null) {


                // Sub-division Response
                if (i == 1) {
                    String status = jsonObject.getString("status");
                    if (status.equalsIgnoreCase("200")) {
                        JSONArray subDivisionJSONArray = jsonObject.getJSONArray("data");

                        if (subDivisionJSONArray != null) {
                            AdaptorSubDivList adaptorSubDivList = new AdaptorSubDivList(this, subDivisionJSONArray, this);
                            subDivRView.setAdapter(adaptorSubDivList);
                        }else {
                            UIToastMessage.show(SubDivListCaActivity.this, "No data found");
                        }

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
