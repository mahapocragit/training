package in.gov.pocra.training.activity.pmu.add_event_pmu.pmu_mem_filter_list.pocra_office_staff;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import in.co.appinventor.services_api.listener.OnMultiRecyclerItemClickListener;
import in.co.appinventor.services_api.settings.AppSettings;
import in.co.appinventor.services_api.widget.UIToastMessage;
import in.gov.pocra.training.R;
import in.gov.pocra.training.activity.pmu.add_event_pmu.pmu_mem_filter_list.PmuParticipantListActivity;
import in.gov.pocra.training.model.online.ResponseModel;
import in.gov.pocra.training.util.ApConstants;
import in.gov.pocra.training.util.ApUtil;
import in.gov.pocra.training.util.AppHelper;
import in.gov.pocra.training.web_services.APIRequest;
import in.gov.pocra.training.web_services.APIServices;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Retrofit;

public class PmuParticipantFilterActivity extends AppCompatActivity implements OnMultiRecyclerItemClickListener, AlertListCallbackEventListener, ApiCallbackCode, ApiJSONObjCallback {

    private String roleId;
    private String userLaval;

    // For Ps-Hrd
    private String userDistrictId = "";
    private String userDistrictName = "";

    private ImageView homeBack;
    private AlertDialog.Builder builder;

    // For Location
    private LinearLayout locationLLayout;
    private TextView locationTView;
    private JSONArray locationJsonArray;
    private String centerName = "";
    private String locationId = "";

    private RadioGroup searchByRadioGroup;
    private RadioButton locationRBtn;
    private RadioButton roleRBtn;
    private String searchById = "";

    private LinearLayout districtLLayout;
    private TextView districtTView;
    private JSONArray districtJsonArray;
    private String districtId = "";
    private ImageView districtIView;

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
        setContentView(R.layout.activity_pmu_participant_filter_new);


        /** For actionbar title in center */
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.attendance_actionbar_layout);
        AppCompatTextView actionTitleTextView = (AppCompatTextView) getSupportActionBar().getCustomView().findViewById(R.id.actionTitleTextView);
        homeBack = (ImageView) getSupportActionBar().getCustomView().findViewById(R.id.backImageView);
        // addPersonImageView = (ImageView) getSupportActionBar().getCustomView().findViewById(R.id.addPersonImageView);
        homeBack.setVisibility(View.VISIBLE);
        actionTitleTextView.setText(getResources().getString(R.string.title_pmu_participants_filter));

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

        String rId = AppSettings.getInstance().getValue(this, ApConstants.kROLE_ID, ApConstants.kROLE_ID);
        if (!rId.equalsIgnoreCase("kROLE_ID")) {
            roleId = rId;
        }

        String rLaval = AppSettings.getInstance().getValue(this, ApConstants.kUSER_LEVEL, ApConstants.kUSER_LEVEL);
        if (!rLaval.equalsIgnoreCase("kUSER_LEVEL")) {
            userLaval = rLaval;
        }

        locationLLayout = (LinearLayout) findViewById(R.id.locationLLayout);
        locationTView = (TextView) findViewById(R.id.locationTView);

        searchByRadioGroup = (RadioGroup)findViewById(R.id.searchByRadioGroup);
        locationRBtn = (RadioButton)findViewById(R.id.locationRBtn);
        roleRBtn = (RadioButton)findViewById(R.id.roleRBtn);

        // For District
        districtLLayout = (LinearLayout) findViewById(R.id.districtLLayout);
        districtTView = (TextView) findViewById(R.id.districtTView);
        districtIView = (ImageView)findViewById(R.id.districtIView);

        // For Subdivision
        subDivisionLLayout = (LinearLayout) findViewById(R.id.subDivisionLLayout);
        subDivisionTView = (TextView) findViewById(R.id.subDivisionTView);

        // For Role
        roleLLayout = (LinearLayout) findViewById(R.id.roleLLayout);
        roleTView = (TextView) findViewById(R.id.roleTView);

        nextButton = (Button) findViewById(R.id.nextButton);
    }

    private void defaultConfiguration() {

        String loginData = AppSettings.getInstance().getValue(this,ApConstants.kLOGIN_DATA,ApConstants.kLOGIN_DATA);
        if (!loginData.equalsIgnoreCase("kLOGIN_DATA")){
            if (userLaval.equalsIgnoreCase("District")){
                try {
                    JSONObject loginJSON = new JSONObject(loginData);
                    userDistrictId = loginJSON.getString("district_id");
                    userDistrictName = loginJSON.getString("district_name");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }



        // For Location
        locationJsonArray = AppHelper.getInstance().getMemLocationJsonArray();
        locationLLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (locationJsonArray != null) {
                    ApUtil.showCustomListPicker(locationTView, locationJsonArray, "Select Location", "name", "id", PmuParticipantFilterActivity.this, PmuParticipantFilterActivity.this);
                }
            }
        });


        searchByRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.locationRBtn){
                    searchById = "location";
                    radioLocationAction();

                }else if (checkedId == R.id.roleRBtn){
                    searchById = "roleId";
                    radioRoleAction();
                }
            }
        });




        // For District
        districtLLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ApUtil.checkInternetConnection(PmuParticipantFilterActivity.this)) {

                    if (districtJsonArray != null) {
                        ApUtil.showCustomListPicker(districtTView, districtJsonArray, "Select District", "name", "id", PmuParticipantFilterActivity.this, PmuParticipantFilterActivity.this);

                    } else {
                        if (centerName.equalsIgnoreCase("District")) {
                            getDistrictList();
                        } else {
                            UIToastMessage.show(PmuParticipantFilterActivity.this, "Please select location");
                        }
                    }

                } else {
                    UIToastMessage.show(PmuParticipantFilterActivity.this, "No internet connection");
                }
            }
        });


        // For Sub-division
        subDivisionLLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ApUtil.checkInternetConnection(PmuParticipantFilterActivity.this)) {

                    if (subDivisionJsonArray != null) {
                        ApUtil.showCustomListPicker(subDivisionTView, subDivisionJsonArray, "Select Sub-division", "name", "id", PmuParticipantFilterActivity.this, PmuParticipantFilterActivity.this);
                    } else {
                        if (centerName.equalsIgnoreCase("Subdivision") && !districtId.equalsIgnoreCase("")) {
                            getSubDivisionList(districtId);
                        } else {
                            UIToastMessage.show(PmuParticipantFilterActivity.this, "Please select district");
                        }
                    }

                } else {
                    UIToastMessage.show(PmuParticipantFilterActivity.this, "No internet connection");
                }
            }
        });


        // For participant role
        roleLLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ApUtil.checkInternetConnection(PmuParticipantFilterActivity.this)) {

                    if (roleJsonArray != null) {
                        ApUtil.showCustomListPicker(roleTView, roleJsonArray, "Select Roles/Posts/Positions ", "name", "id", PmuParticipantFilterActivity.this, PmuParticipantFilterActivity.this);
                    } else {
                        if (!centerName.isEmpty()) {
                            getRoleList(centerName);
                        } else {
                            UIToastMessage.show(PmuParticipantFilterActivity.this, "Please select location");
                        }

                    }

                } else {
                    UIToastMessage.show(PmuParticipantFilterActivity.this, "No internet connection");
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

    }


    @Override
    public void onMultiRecyclerViewItemClick(int i, Object o) {

    }



    // get DISTRICT
    private void getDistrictList() {
        AppinventorIncAPI api = new AppinventorIncAPI(this, APIServices.API_URL, "", ApConstants.kMSG, true);
        api.getRequestData(APIServices.GET_DIST_URL, this, 1);
    }


    private void getSubDivisionList(String districtId) {
        String subDivisionUrl = APIServices.GET_SUB_DIV_URL + districtId;
        Log.d("param=","getSubDivisionList==="+subDivisionUrl);
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
        Call<JsonObject> responseCall = apiRequest.getOfficeStaffRoleRequest(requestBody);

        DebugLog.getInstance().d("role_list_param=" + responseCall.request().toString());
        DebugLog.getInstance().d("role_list_param=" + AppUtility.getInstance().bodyToString(responseCall.request()));

        api.postRequest(responseCall, this, 3);

    }



    private void radioLocationAction() {


        if (!centerName.equalsIgnoreCase("") && centerName.equalsIgnoreCase("District")) {

            if (userLaval.equalsIgnoreCase("District")){

                districtId = userDistrictId;
                districtTView.setText(userDistrictName);
                districtLLayout.setVisibility(View.VISIBLE);
                districtLLayout.setEnabled(false);
                getSubDivisionList(districtId);
                districtIView.setVisibility(View.GONE);

                pRoleId = "";
                roleTView.setText("");
                roleLLayout.setVisibility(View.GONE);

                subDivisionId = "";
                subDivisionTView.setText("");
                subDivisionLLayout.setVisibility(View.GONE);

            }else {
                districtId = "";
                districtTView.setText("");
                districtLLayout.setVisibility(View.VISIBLE);
                districtLLayout.setEnabled(true);
                districtIView.setVisibility(View.VISIBLE);

                pRoleId = "";
                roleTView.setText("");
                roleLLayout.setVisibility(View.GONE);

                subDivisionId = "";
                subDivisionTView.setText("");
                subDivisionLLayout.setVisibility(View.GONE);

            }

        }else if (!centerName.equalsIgnoreCase("") && centerName.equalsIgnoreCase("Subdivision")){

            if (userLaval.equalsIgnoreCase("District")){
                districtId = userDistrictId;
                districtTView.setText(userDistrictName);
                districtLLayout.setVisibility(View.VISIBLE);
                districtLLayout.setEnabled(false);
                districtIView.setVisibility(View.GONE);
                getSubDivisionList(districtId);

                subDivisionId = "";
                subDivisionTView.setText("");
                subDivisionLLayout.setVisibility(View.VISIBLE);

                pRoleId = "";
                roleTView.setText("");
                roleLLayout.setVisibility(View.GONE);
            }else {
                districtId = "";
                districtTView.setText("");
                districtLLayout.setVisibility(View.VISIBLE);
                districtLLayout.setEnabled(true);
                districtIView.setVisibility(View.VISIBLE);

                subDivisionId = "";
                subDivisionTView.setText("");
                subDivisionLLayout.setVisibility(View.VISIBLE);

                pRoleId = "";
                roleTView.setText("");
                roleLLayout.setVisibility(View.GONE);
            }


        }else {
            districtId = "";
            districtTView.setText("");
            districtLLayout.setVisibility(View.GONE);

            subDivisionId = "";
            subDivisionTView.setText("");
            subDivisionLLayout.setVisibility(View.GONE);

            pRoleId = "";
            roleTView.setText("");
            roleLLayout.setVisibility(View.VISIBLE);
        }
    }


    private void radioRoleAction() {

        if (!centerName.equalsIgnoreCase("") && centerName.equalsIgnoreCase("District")) {

            if (userLaval.equalsIgnoreCase("District")){
                // districtId = userDistrictId;
                districtId = "";
                districtTView.setText("");
                districtLLayout.setVisibility(View.GONE);

                subDivisionId = "";
                subDivisionTView.setText("");
                subDivisionLLayout.setVisibility(View.GONE);

                pRoleId = "";
                roleTView.setText("");
                roleLLayout.setVisibility(View.VISIBLE);

            }else {
                districtId = "";
                districtTView.setText("");
                districtLLayout.setVisibility(View.GONE);

                subDivisionId = "";
                subDivisionTView.setText("");
                subDivisionLLayout.setVisibility(View.GONE);

                pRoleId = "";
                roleTView.setText("");
                roleLLayout.setVisibility(View.VISIBLE);
            }


        }else if (!centerName.equalsIgnoreCase("") && centerName.equalsIgnoreCase("Subdivision")){

            if (userLaval.equalsIgnoreCase("District")){
                // districtId = userDistrictId;
                districtId = "";
                districtTView.setText("");
                districtLLayout.setVisibility(View.GONE);

                subDivisionId = "";
                subDivisionTView.setText("");
                subDivisionLLayout.setVisibility(View.GONE);

                pRoleId = "";
                roleTView.setText("");
                roleLLayout.setVisibility(View.VISIBLE);
            }else {
                districtId = "";
                districtTView.setText("");
                districtLLayout.setVisibility(View.GONE);

                subDivisionId = "";
                subDivisionTView.setText("");
                subDivisionLLayout.setVisibility(View.GONE);

                pRoleId = "";
                roleTView.setText("");
                roleLLayout.setVisibility(View.VISIBLE);
            }


        }else {
            districtId = "";
            districtTView.setText("");
            districtLLayout.setVisibility(View.GONE);

            subDivisionId = "";
            subDivisionTView.setText("");
            subDivisionLLayout.setVisibility(View.GONE);

            pRoleId = "";
            roleTView.setText("");
            roleLLayout.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void didSelectAlertViewListItem(TextView textView, String s) {

        if (textView == locationTView) {

            String cName = locationTView.getText().toString().trim();
            centerName = ApUtil.toTitleCase(cName);
            if (centerName.equalsIgnoreCase("Pmu")) {

                locationId = s;

                searchByRadioGroup.setVisibility(View.GONE);
                searchByRadioGroup.clearCheck();
                searchById = "";

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

            } else if (centerName.equalsIgnoreCase("District")) {
                locationId = "";

                searchByRadioGroup.setVisibility(View.VISIBLE);
                searchByRadioGroup.clearCheck();
                searchById = "";

                districtId = "";
                districtTView.setText("");
                districtLLayout.setVisibility(View.GONE);

                pRoleId = "";
                roleTView.setText("");
                roleLLayout.setVisibility(View.GONE);

                subDivisionId = "";
                subDivisionTView.setText("");
                subDivisionLLayout.setVisibility(View.GONE);

                getDistrictList();
                getRoleList(centerName);

            } else if (centerName.equalsIgnoreCase("Subdivision")) {
                locationId = "";

                searchByRadioGroup.setVisibility(View.VISIBLE);
                searchByRadioGroup.clearCheck();
                searchById = "";

                districtId = "";
                districtTView.setText("");
                districtLLayout.setVisibility(View.GONE);

                subDivisionId = "";
                subDivisionTView.setText("");
                subDivisionLLayout.setVisibility(View.GONE);

                pRoleId = "";
                roleTView.setText("");
                roleLLayout.setVisibility(View.GONE);

                getDistrictList();
                getRoleList(centerName);
            }
        }


        if (textView == districtTView) {

            districtId = s;

            if (centerName.equalsIgnoreCase("Subdivision")) {
                subDivisionId = "";
                subDivisionTView.setText("");
                getSubDivisionList(districtId);
            }

            /*if (centerName.equalsIgnoreCase("District")) {
                locationId = s;
                getRoleList(centerName);

                pRoleId = "";
                roleTView.setText("");
                roleLLayout.setVisibility(View.GONE);

                subDivisionId = "";
                subDivisionTView.setText("");
                subDivisionLLayout.setVisibility(View.GONE);

            } else if (centerName.equalsIgnoreCase("Subdivision")) {
                locationId = "";
                districtLLayout.setVisibility(View.VISIBLE);

                subDivisionId = "";
                subDivisionTView.setText("");
                subDivisionLLayout.setVisibility(View.VISIBLE);

                pRoleId = "";
                roleTView.setText("");
                roleLLayout.setVisibility(View.GONE);

                getSubDivisionList(districtId);
            }*/

        }


        if (textView == subDivisionTView) {
            subDivisionId = s;
            /*if (centerName.equalsIgnoreCase("Subdivision")) {
                locationId = s;

                getRoleList(centerName);
                pRoleId = "";
                roleTView.setText("");
                roleLLayout.setVisibility(View.GONE);

            }*/
        }

        if (textView == roleTView) {
            pRoleId = s;
        }


    }


    private void nextButtonAction() {

        if (centerName.equalsIgnoreCase("")) {
            UIToastMessage.show(this, "Select location");
        }else if (centerName.equalsIgnoreCase("District") && searchById.equalsIgnoreCase("") ){
            UIToastMessage.show(this,"Select search by option");
        }else if (centerName.equalsIgnoreCase("District") && searchById.equalsIgnoreCase("location") && districtId.equalsIgnoreCase("") ){
            UIToastMessage.show(this,"Select district");
        }else if (centerName.equalsIgnoreCase("District") && searchById.equalsIgnoreCase("roleId") && pRoleId.equalsIgnoreCase("") ){
            UIToastMessage.show(this,"Select role");
        }else if (centerName.equalsIgnoreCase("Subdivision") && searchById.equalsIgnoreCase("") ){
            UIToastMessage.show(this,"Select search by option");
        }else if (centerName.equalsIgnoreCase("Subdivision") && searchById.equalsIgnoreCase("location") && subDivisionId.equalsIgnoreCase("") ){
            UIToastMessage.show(this,"Select Subdivision");
        }else if (centerName.equalsIgnoreCase("Subdivision") && searchById.equalsIgnoreCase("roleId") && pRoleId.equalsIgnoreCase("") ){
            UIToastMessage.show(this,"Select Role");
        }else {

            Intent intent = new Intent(this, PmuParticipantListActivity.class);
            intent.putExtra("center", centerName);
            intent.putExtra("distId", districtId);
            intent.putExtra("subDiv", subDivisionId);
            intent.putExtra("talukaId", "");
            intent.putExtra("villageId", "");

            intent.putExtra("pRoleId", pRoleId);
            intent.putExtra("staffType", ApConstants.kEVENT_MEM_FFS_F);
            startActivity(intent);

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
