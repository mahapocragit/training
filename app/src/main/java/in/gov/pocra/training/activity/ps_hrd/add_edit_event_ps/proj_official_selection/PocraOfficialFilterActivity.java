package in.gov.pocra.training.activity.ps_hrd.add_edit_event_ps.proj_official_selection;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import in.co.appinventor.services_api.api.AppinventorApi;
import in.co.appinventor.services_api.api.AppinventorIncAPI;
import in.co.appinventor.services_api.app_util.AppUtility;
import in.co.appinventor.services_api.debug.DebugLog;
import in.co.appinventor.services_api.listener.AlertListCallbackEventListener;
import in.co.appinventor.services_api.listener.ApiCallbackCode;
import in.co.appinventor.services_api.listener.ApiJSONObjCallback;
import in.co.appinventor.services_api.widget.UIToastMessage;
import in.gov.pocra.training.R;
import in.gov.pocra.training.activity.common.coordinator_list.SearchPmuMemActivity;
import in.gov.pocra.training.model.online.ResponseModel;
import in.gov.pocra.training.util.ApConstants;
import in.gov.pocra.training.util.ApUtil;
import in.gov.pocra.training.util.AppHelper;
import in.gov.pocra.training.web_services.APIRequest;
import in.gov.pocra.training.web_services.APIServices;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Retrofit;

public class PocraOfficialFilterActivity extends AppCompatActivity implements AlertListCallbackEventListener, ApiJSONObjCallback, ApiCallbackCode {

    private ImageView homeBack;
    private String selectionType;

    private LinearLayout locationLLayout;
    private TextView locationTView;
    private JSONArray locationJsonArray;
    private String locationId = "";
    private String centerName = "";

    private LinearLayout districtLLayout;
    private TextView districtTView;
    private JSONArray districtJsonArray;
    private String districtId = "";

    private LinearLayout subDivisionLLayout;
    private TextView subDivisionTView;
    private JSONArray subDivisionJsonArray;
    private String subDivisionId = "";

    private LinearLayout roleLLayout;
    private TextView roleTView;
    private JSONArray roleJsonArray;
    private String pRoleId = "";

    private Button nextButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pocra_official_filter);

        /** For actionbar title in center */
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.attendance_actionbar_layout);
        AppCompatTextView actionTitleTextView = (AppCompatTextView) getSupportActionBar().getCustomView().findViewById(R.id.actionTitleTextView);
        homeBack = (ImageView) getSupportActionBar().getCustomView().findViewById(R.id.backImageView);
        homeBack.setVisibility(View.VISIBLE);
        actionTitleTextView.setText("PoCRA officials");

        initialization();
        defaultConfiguration();

    }

    private void initialization() {

        locationLLayout = (LinearLayout)findViewById(R.id.locationLLayout);
        locationTView = (TextView)findViewById(R.id.locationTView);
        districtLLayout = (LinearLayout)findViewById(R.id.districtLLayout);
        districtTView = (TextView)findViewById(R.id.districtTView);
        subDivisionLLayout = (LinearLayout)findViewById(R.id.subDivisionLLayout);
        subDivisionTView = (TextView)findViewById(R.id.subDivisionTView);
        roleLLayout = (LinearLayout)findViewById(R.id.roleLLayout);
        roleTView = (TextView)findViewById(R.id.roleTView);
        nextButton = (Button)findViewById(R.id.nextButton);
    }

    private void defaultConfiguration() {

        // For Action Bar
        homeBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        getDistrictList();

        // For Location
        locationJsonArray = AppHelper.getInstance().getMemLocationJsonArray();
        locationLLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (locationJsonArray != null) {
                    ApUtil.showCustomListPicker(locationTView, locationJsonArray, "Select Location", "name", "id", PocraOfficialFilterActivity.this, PocraOfficialFilterActivity.this);

                }
            }
        });


        // For District
        districtLLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ApUtil.checkInternetConnection(PocraOfficialFilterActivity.this)) {

                    if (districtJsonArray != null) {
                        ApUtil.showCustomListPicker(districtTView, districtJsonArray, "Select District", "name", "id", PocraOfficialFilterActivity.this, PocraOfficialFilterActivity.this);

                    } else {
                        if (!locationId.equalsIgnoreCase("") && !locationId.equalsIgnoreCase("1") ) {
                            getDistrictList();
                        } else {
                            UIToastMessage.show(PocraOfficialFilterActivity.this, "Please select location");
                        }
                    }

                } else {
                    UIToastMessage.show(PocraOfficialFilterActivity.this, "No internet connection");
                }
            }
        });


        // For Sub-division
        subDivisionLLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ApUtil.checkInternetConnection(PocraOfficialFilterActivity.this)) {

                    if (subDivisionJsonArray != null) {
                        ApUtil.showCustomListPicker(subDivisionTView, subDivisionJsonArray, "Select Sub-division", "name", "id", PocraOfficialFilterActivity.this, PocraOfficialFilterActivity.this);

                    } else {
                        if (centerName.equalsIgnoreCase("Sub-division") && !districtId.equalsIgnoreCase("")) {
                            getSubDivisionList(districtId);
                        } else {
                            UIToastMessage.show(PocraOfficialFilterActivity.this, "Please select district");
                        }

                    }

                } else {
                    UIToastMessage.show(PocraOfficialFilterActivity.this, "No internet connection");
                }
            }
        });



        // For participant role
        roleLLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ApUtil.checkInternetConnection(PocraOfficialFilterActivity.this)) {

                    if (roleJsonArray != null) {
                        ApUtil.showCustomListPicker(roleTView, roleJsonArray, "Select Roles/Posts/Positions ", "name", "id", PocraOfficialFilterActivity.this, PocraOfficialFilterActivity.this);
                    } else {
                        if (!centerName.isEmpty()) {
                            getRoleList(centerName);
                        } else {
                            UIToastMessage.show(PocraOfficialFilterActivity.this, "Please select location");
                        }

                    }

                } else {
                    UIToastMessage.show(PocraOfficialFilterActivity.this, "No internet connection");
                }
            }
        });


        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextButtonAction();
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        selectionType = getIntent().getStringExtra("selectionType");
    }



    // get DISTRICT
    private void getDistrictList() {
        AppinventorIncAPI api = new AppinventorIncAPI(this, APIServices.API_URL, "", ApConstants.kMSG, true);
        api.getRequestData(APIServices.GET_DIST_URL, this, 1);
    }

    private void getSubDivisionList(String districtId) {
        String subDivisionUrl = APIServices.GET_SUB_DIV_URL + districtId;
        AppinventorIncAPI api = new AppinventorIncAPI(this, APIServices.API_URL, "", ApConstants.kMSG, true);
        api.getRequestData(subDivisionUrl, this, 2);
    }


    private void getRoleList(String centerName) {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("center", centerName);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString());
        AppinventorApi api = new AppinventorApi(this, APIServices.SERVICE_API_URL, "", ApConstants.kMSG, true);
        Retrofit retrofit = api.getRetrofitInstance();
        APIRequest apiRequest = retrofit.create(APIRequest.class);
        Call<JsonObject> responseCall = apiRequest.getParticipantRoleRequest(requestBody);

        DebugLog.getInstance().d("role_list_param=" + responseCall.request().toString());
        DebugLog.getInstance().d("role_list_param=" + AppUtility.getInstance().bodyToString(responseCall.request()));

        api.postRequest(responseCall, this, 3);

    }


    private void nextButtonAction() {

        if (centerName.equalsIgnoreCase("")){
            UIToastMessage.show(this,"All fields are mandatory");
        }else if (locationId.equalsIgnoreCase("")){
            UIToastMessage.show(this,"All fields are mandatory");
        }else if (pRoleId.equalsIgnoreCase("")){
            UIToastMessage.show(this,"All fields are mandatory");
        }else {

            /*String c = centerName;
            String l = locationId;
            String r = pRoleId;*/

            if (selectionType.equalsIgnoreCase("member")){
                Intent intent = new Intent(this, PocraOfficialListActivity.class);
                intent.putExtra("center",centerName);
                intent.putExtra("location",locationId);
                intent.putExtra("pRoleId",pRoleId);
                startActivity(intent);
            }else if (selectionType.equalsIgnoreCase("coordinator")){
                Intent intent = new Intent(this, SearchPmuMemActivity.class);
                intent.putExtra("center",centerName);
                intent.putExtra("location",locationId);
                intent.putExtra("pRoleId",pRoleId);
                startActivity(intent);
            }

            // finish();
        }

    }

    @Override
    public void didSelectAlertViewListItem(TextView textView, String s) {

        if (textView == locationTView){
            String cName = locationTView.getText().toString().trim();
            centerName = ApUtil.toTitleCase(cName);

            if (centerName.equalsIgnoreCase("Pmu")){
                locationId = s;
                // locationTView.setAllCaps(true);
                getRoleList(centerName);

                districtId = "";
                districtTView.setText("");
                districtLLayout.setVisibility(View.GONE);

                subDivisionId = "";
                subDivisionTView.setText("");
                subDivisionLLayout.setVisibility(View.GONE);

                pRoleId = "";
                roleTView.setText("");
                roleLLayout.setVisibility(View.VISIBLE);

            }else if (centerName.equalsIgnoreCase("District")){

                // locationTView.setAllCaps(false);

                locationId = "";

                districtId = "";
                districtTView.setText("");
                districtLLayout.setVisibility(View.VISIBLE);

                pRoleId = "";
                roleTView.setText("");
                roleLLayout.setVisibility(View.GONE);

                subDivisionId = "";
                subDivisionTView.setText("");
                subDivisionLLayout.setVisibility(View.GONE);

                getDistrictList();

            }else if (centerName.equalsIgnoreCase("Subdivision")){

                // locationTView.setAllCaps(false);

                locationId = "";

                districtId = "";
                districtTView.setText("");
                districtLLayout.setVisibility(View.VISIBLE);

                subDivisionId = "";
                subDivisionTView.setText("");
                subDivisionLLayout.setVisibility(View.VISIBLE);

                pRoleId = "";
                roleTView.setText("");
                roleLLayout.setVisibility(View.GONE);

                getDistrictList();
            }
        }


        if (textView == districtTView){

            districtId = s;

            if (centerName.equalsIgnoreCase("District")){
                locationId = s;
                getRoleList(centerName);

                pRoleId = "";
                roleTView.setText("");
                roleLLayout.setVisibility(View.VISIBLE);

                subDivisionId = "";
                subDivisionTView.setText("");
                subDivisionLLayout.setVisibility(View.GONE);

            }else if (centerName.equalsIgnoreCase("Subdivision")){

                districtLLayout.setVisibility(View.VISIBLE);

                subDivisionId = "";
                subDivisionTView.setText("");
                subDivisionLLayout.setVisibility(View.VISIBLE);

                pRoleId = "";
                roleTView.setText("");
                roleLLayout.setVisibility(View.GONE);

                getSubDivisionList(districtId);
            }
        }



        if (textView == subDivisionTView){
            subDivisionId = s;
            if (centerName.equalsIgnoreCase("Subdivision")){
                locationId = s;
                getRoleList(centerName);
                pRoleId = "";
                roleTView.setText("");
                roleLLayout.setVisibility(View.VISIBLE);
            }

        }

        if (textView == roleTView){
            pRoleId = s;
        }

    }




    @Override
    public void onResponse(JSONObject jsonObject, int i) {

        try {

            if (jsonObject != null) {

                if (i == 1) {
                    ResponseModel distListRMode = new ResponseModel(jsonObject);
                    if (distListRMode.isStatus()) {
                        districtJsonArray = jsonObject.getJSONArray("data");
                    }
                }

                // Sub-division Response
                if (i == 2) {
                    String status = jsonObject.getString("status");
                    if (status.equalsIgnoreCase("200")) {
                        subDivisionJsonArray = jsonObject.getJSONArray("data");
                    }
                }


                // Role Response
                if (i == 3) {
                    ResponseModel responseModel = new ResponseModel(jsonObject);
                    if (responseModel.isStatus()) {
                        roleJsonArray = jsonObject.getJSONArray("data");
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
