package in.gov.pocra.training.activity.ca.add_edit_event_ca.ca_mem_filter_list;

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
import android.widget.TextView;
import android.widget.Toast;

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
import in.gov.pocra.training.web_services.APIRequest;
import in.gov.pocra.training.web_services.APIServices;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Retrofit;

public class CaFieldStaffFilterActivity extends AppCompatActivity implements OnMultiRecyclerItemClickListener, AlertListCallbackEventListener, ApiCallbackCode, ApiJSONObjCallback {

    private ImageView homeBack;
    private AlertDialog.Builder builder;

    // For Location
    private String centerName = "Subdivision";

    private LinearLayout districtLLayout;
    private TextView districtTView;
    private JSONArray districtJsonArray;
    private String districtId = "";

    private LinearLayout subDivisionLLayout;
    private TextView subDivisionTView;
    private ImageView subDivisionIView;
    private JSONArray subDivisionJsonArray;
    private String subDivisionId = "";

    private LinearLayout talukaLLayout;
    private TextView talukaTView;
    private JSONArray talukaJSONArray;
    private String talukaId = "";

    private LinearLayout villageLLayout;
    private TextView villageTView;
    private JSONArray villageJSONArray;
    private String villageId = "";

    private LinearLayout roleLLayout;
    private TextView roleTView;
    private JSONArray roleJsonArray;
    private String pRoleId = "";

    private Button nextButton;
    private String roleId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ca_field_staff_filter);

        /** For actionbar title in center */
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.attendance_actionbar_layout);
        AppCompatTextView actionTitleTextView = (AppCompatTextView) getSupportActionBar().getCustomView().findViewById(R.id.actionTitleTextView);
        homeBack = (ImageView) getSupportActionBar().getCustomView().findViewById(R.id.backImageView);
        // addPersonImageView = (ImageView) getSupportActionBar().getCustomView().findViewById(R.id.addPersonImageView);
        homeBack.setVisibility(View.VISIBLE);
        actionTitleTextView.setText(getResources().getString(R.string.title_participants_filter));

        initialization();
        defaultConfiguration();
    }

    private void initialization() {
        String rId = AppSettings.getInstance().getValue(this, ApConstants.kROLE_ID, ApConstants.kROLE_ID);
        if (!rId.equalsIgnoreCase("kROLE_ID")) {
            roleId = rId;
        }

        // For Action Bar
        homeBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // For District
        districtLLayout = (LinearLayout) findViewById(R.id.districtLLayout);
        districtTView = (TextView) findViewById(R.id.districtTView);

        // For Subdivision
        subDivisionLLayout = (LinearLayout) findViewById(R.id.subDivisionLLayout);
        subDivisionTView = (TextView) findViewById(R.id.subDivisionTView);
        subDivisionIView = (ImageView) findViewById(R.id.subDivisionIView);

        // For Taluka
        talukaLLayout = (LinearLayout) findViewById(R.id.talukaLLayout);
        talukaTView = (TextView) findViewById(R.id.talukaTView);

        // For Village
        villageLLayout = (LinearLayout) findViewById(R.id.villageLLayout);
        villageTView = (TextView) findViewById(R.id.villageTView);

        // For Role
        roleLLayout = (LinearLayout) findViewById(R.id.roleLLayout);
        roleTView = (TextView) findViewById(R.id.roleTView);

        nextButton = (Button) findViewById(R.id.nextButton);
    }

    private void defaultConfiguration() {

        getLoginData();

        subDivisionLLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (subDivisionJsonArray != null) {
                    Log.d("subDivisionLLayout","subDivisionLLayout="+subDivisionJsonArray);
                    ApUtil.showCustomListPicker(subDivisionTView, subDivisionJsonArray, "Select Sub-division", "subdivision_name", "subdivision_id", CaFieldStaffFilterActivity.this, CaFieldStaffFilterActivity.this);
                } else {
                    getLoginData();
                }
            }
        });


        // For Taluka
        talukaLLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ApUtil.checkInternetConnection(CaFieldStaffFilterActivity.this)) {

                    if (talukaJSONArray != null) {
                        ApUtil.showCustomListPicker(talukaTView, talukaJSONArray, "Select Taluka", "name", "id", CaFieldStaffFilterActivity.this, CaFieldStaffFilterActivity.this);
                    } else {
                        if (centerName.equalsIgnoreCase("Subdivision") && !subDivisionId.equalsIgnoreCase("")) {
                            getTalukaList(subDivisionId);
                        } else {
                            UIToastMessage.show(CaFieldStaffFilterActivity.this, "Please select Sub-Division");
                        }
                    }

                } else {
                    UIToastMessage.show(CaFieldStaffFilterActivity.this, "No internet connection");
                }
            }
        });


        // For Village
        villageLLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ApUtil.checkInternetConnection(CaFieldStaffFilterActivity.this)) {

                    if (villageJSONArray != null) {
                        ApUtil.showCustomListPicker(villageTView, villageJSONArray, "Select Village", "name", "id", CaFieldStaffFilterActivity.this, CaFieldStaffFilterActivity.this);
                    } else {
                        if (centerName.equalsIgnoreCase("Subdivision") && !talukaId.equalsIgnoreCase("")) {
                            getVillageList(talukaId);
                        } else {
                            UIToastMessage.show(CaFieldStaffFilterActivity.this, "Please select Taluka");
                        }
                    }

                } else {
                    UIToastMessage.show(CaFieldStaffFilterActivity.this, "No internet connection");
                }
            }
        });

        // For participant role
        roleLLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ApUtil.checkInternetConnection(CaFieldStaffFilterActivity.this)) {

                    if (roleJsonArray != null) {
                        ApUtil.showCustomListPicker(roleTView, roleJsonArray, "Select Roles/Posts/Positions ", "name", "id", CaFieldStaffFilterActivity.this, CaFieldStaffFilterActivity.this);
                    } else {
                        if (!centerName.isEmpty()) {
                            getRoleList(centerName);
                        } else {
                            UIToastMessage.show(CaFieldStaffFilterActivity.this, "Please select location");
                        }
                    }

                } else {
                    UIToastMessage.show(CaFieldStaffFilterActivity.this, "No internet connection");
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

    private void getLoginData() {
        String loginData = AppSettings.getInstance().getValue(this, ApConstants.kLOGIN_DATA, ApConstants.kLOGIN_DATA);
        Log.d("getLoginData","loginData="+loginData);
        if (!loginData.equalsIgnoreCase("kLOGIN_DATA")) {
            try {
                JSONObject loginJSON = new JSONObject(loginData);
                // JSONArray clusterData = loginJSON.getJSONArray("cluster_data");
                districtId = loginJSON.getString("district_id");
                Log.d("getLoginData","districtId="+districtId);
                districtTView.setText(loginJSON.getString("district_name"));
                subDivisionLLayout.setVisibility(View.VISIBLE);
                JSONArray trainingData = loginJSON.getJSONArray("training_data");
                JSONObject trainingJSON = trainingData.getJSONObject(0);
                JSONArray subArray = trainingJSON.getJSONArray("subdivisions");
                Log.d("getLoginData","subdivisions="+subArray);
                if (subArray.length() > 1) {
                    subDivisionLLayout.setEnabled(true);
                    subDivisionJsonArray = subArray;
                    subDivisionIView.setVisibility(View.VISIBLE);
                } else {
                    subDivisionJsonArray = new JSONArray();
                    subDivisionLLayout.setEnabled(false);
                    subDivisionIView.setVisibility(View.INVISIBLE);
                    subDivisionId = loginJSON.getString("subdivision_id");
                    subDivisionTView.setText(loginJSON.getString("subdivision_name"));
                    Log.d("getLoginData","loginJSON subDivisionTView="+loginJSON.getString("subdivision_name"));
                    talukaLLayout.setVisibility(View.VISIBLE);
                    getTalukaList(subDivisionId);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        getDistrictList();
    }


    @Override
    public void onMultiRecyclerViewItemClick(int i, Object o) {

    }

    // get DISTRICT
    private void getDistrictList() {
        AppinventorIncAPI api = new AppinventorIncAPI(this, APIServices.API_URL, "", ApConstants.kMSG, true);
        api.getRequestData(APIServices.GET_DIST_URL, this, 1);
        Log.d("DistrictList","getDistrictList="+APIServices.GET_DIST_URL);
        DebugLog.getInstance().d("getDistrictList=" + APIServices.GET_DIST_URL);
    }
    private void getSubDivisionList(String districtId) {

        String subDivisionUrl = APIServices.GET_SUB_DIV_URL + districtId;
        DebugLog.getInstance().d("getSubDivisionList=" + subDivisionUrl);
        Log.d("SubDivisionList","getSubDivisionList="+subDivisionUrl);
        AppinventorIncAPI api = new AppinventorIncAPI(this, APIServices.API_URL, "", ApConstants.kMSG, true);
        api.getRequestData(subDivisionUrl, this, 2);
    }

    // get Taluka
    private void getTalukaList(String subDivID) {
        String subTalukaUrl = APIServices.GET_TALUKA_URL + subDivID;
        DebugLog.getInstance().d("getTalukaList=" + subTalukaUrl);
        Log.d("TalukaList","getSubDivisionList="+subTalukaUrl);
        AppinventorIncAPI api = new AppinventorIncAPI(this, APIServices.API_URL, "", ApConstants.kMSG, true);
        api.getRequestData(subTalukaUrl, this, 4);
    }

    // get Village
    // get VCRMC Committee (GP)
    private void getVillageList(String talukaId) {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("taluka_id", talukaId);
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

        api.postRequest(responseCall, this, 5);
    }
   /* private void getVillageList(String talukaId) {
        String subTalukaUrl = APIServices.GET_TALUKA_URL + talukaId;
        AppinventorIncAPI api = new AppinventorIncAPI(this, APIServices.API_URL, "", ApConstants.kMSG, true);
        api.getRequestData(subTalukaUrl, this, 4);
    }*/

    private void getRoleList(String centerName) {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("center", centerName);
            jsonObject.put("api_key", ApConstants.kAUTHORITY_KEY);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString());
        AppinventorApi api = new AppinventorApi(this, APIServices.SERVICE_API_URL, "", ApConstants.kMSG, true);
        Retrofit retrofit = api.getRetrofitInstance();
        APIRequest apiRequest = retrofit.create(APIRequest.class);
        Call<JsonObject> responseCall = apiRequest.getFieldStaffRoleRequest(requestBody);

        DebugLog.getInstance().d("role_list_param=" + responseCall.request().toString());
        DebugLog.getInstance().d("role_list_param=" + AppUtility.getInstance().bodyToString(responseCall.request()));

        api.postRequest(responseCall, this, 3);

    }


    @Override
    public void didSelectAlertViewListItem(TextView textView, String s) {
        if (textView == districtTView) {

            districtId = s;
            Log.d("getLoginData","didSelectAlertViewListItem="+districtId);
            if (centerName.equalsIgnoreCase("Subdivision")) {
                subDivisionId = "";
                subDivisionTView.setText("");
                subDivisionLLayout.setVisibility(View.VISIBLE);

                talukaId = "";
                talukaTView.setText("");
                talukaLLayout.setVisibility(View.GONE);
                getSubDivisionList(districtId);

                pRoleId = "";
                roleTView.setText("");
                roleLLayout.setVisibility(View.GONE);
            }
        }
        if (textView == subDivisionTView) {
            subDivisionId = s;

            talukaId = "";
            talukaTView.setText("");
            talukaLLayout.setVisibility(View.VISIBLE);
            getTalukaList(subDivisionId);

            pRoleId = "";
            roleTView.setText("");
            roleLLayout.setVisibility(View.GONE);

        }
        if (textView == talukaTView) {
            talukaId = s;

            pRoleId = "";
            roleTView.setText("");
            roleLLayout.setVisibility(View.VISIBLE);
            getRoleList(centerName);
            getVillageList(talukaId);

        }

        if (textView == villageTView) {
            villageId = s;
        }

        if (textView == roleTView) {
            pRoleId = s;

            villageId = "";
            villageTView.setText("");

            if (pRoleId.equalsIgnoreCase("28") || pRoleId.equalsIgnoreCase("26") || pRoleId.equalsIgnoreCase("11") || pRoleId.equalsIgnoreCase("37")) {
                if (pRoleId.equalsIgnoreCase("37")){
                    villageTView.setHint("Select Village");
                }else {
                    villageTView.setHint("Select Village (Optional)");
                }
                villageLLayout.setVisibility(View.VISIBLE);
            } else {
                villageLLayout.setVisibility(View.GONE);
            }
        }
    }


    private void nextButtonAction() {

        if (districtId.equalsIgnoreCase("")) {
            //UIToastMessage.show(this, "Select district");
            Toast.makeText(getBaseContext(), "Select district", Toast.LENGTH_SHORT).show();
        } else if (subDivisionId.equalsIgnoreCase("")) {
           // UIToastMessage.show(this, "Select Subdivision");
            Toast.makeText(getBaseContext(), "Select Subdivision", Toast.LENGTH_SHORT).show();
        } else if (talukaId==null) {
            Toast.makeText(getBaseContext(), "Select Taluka", Toast.LENGTH_SHORT).show();
            //UIToastMessage.show(this, "Select Taluka");
        } else if (pRoleId.equalsIgnoreCase("")) {
           // UIToastMessage.show(this, "Select Role");
            Toast.makeText(getBaseContext(), "Select Role", Toast.LENGTH_SHORT).show();
        } else if (pRoleId.equalsIgnoreCase("11") && villageId.equalsIgnoreCase("")) {
            //UIToastMessage.show(this, "Select Village");
            Toast.makeText(getBaseContext(), "Select Village", Toast.LENGTH_SHORT).show();
        }else if (pRoleId.equalsIgnoreCase("37") && villageId.equalsIgnoreCase("")) {
           // UIToastMessage.show(this, "Select Village");
            Toast.makeText(getBaseContext(), "Select Village", Toast.LENGTH_SHORT).show();
        } else {
            Intent intent = new Intent(this, PmuParticipantListActivity.class);
            intent.putExtra("center", centerName);
            intent.putExtra("distId", districtId);
            intent.putExtra("subDiv", subDivisionId);
            intent.putExtra("talukaId", talukaId);
            intent.putExtra("villageId", villageId);
            intent.putExtra("pRoleId", pRoleId);
            intent.putExtra("staffType", ApConstants.kEVENT_MEM_PFS);
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

                // Taluka Response
                if (i == 4) {
                    String status = jsonObject.getString("status");
                    if (status.equalsIgnoreCase("200")) {
                        talukaJSONArray = jsonObject.getJSONArray("data");
                    }
                }

                // Village Response
                if (i == 5) {
                    String status = jsonObject.getString("status");
                    if (status.equalsIgnoreCase("200")) {
                        villageJSONArray = jsonObject.getJSONArray("data");
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
