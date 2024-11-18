package in.gov.pocra.training.activity.pmu.add_event_pmu.pmu_farmer_filter;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;

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
import in.co.appinventor.services_api.widget.UIToastMessage;
import in.gov.pocra.training.R;
import in.gov.pocra.training.activity.ca.add_edit_event_ca.ca_farmer_filter.CaSHGFarmerGroupListActivity;
import in.gov.pocra.training.model.online.ResponseModel;
import in.gov.pocra.training.util.ApConstants;
import in.gov.pocra.training.util.ApUtil;
import in.gov.pocra.training.web_services.APIRequest;
import in.gov.pocra.training.web_services.APIServices;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Retrofit;

public class PmuShgFilterActivity extends AppCompatActivity implements OnMultiRecyclerItemClickListener, AlertListCallbackEventListener, ApiCallbackCode, ApiJSONObjCallback {


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
    private JSONArray subDivisionJsonArray;
    private String subDivisionId = "";


    private LinearLayout talukaLLayout;
    private TextView talukaTView;
    private JSONArray talukaJSONArray;
    private String talukaId = "";

    private LinearLayout villageLLayout;
    private TextView villageTView;
    private JSONArray villageJSONArray;
    private String villageCode = "";
    private String villageName = "";

    private LinearLayout componentLLayout;
    private TextView componentTView;
    private JSONArray componentJsonArray;
    private String componentId = "";

    private LinearLayout subComponentLLayout;
    private TextView subComponentTView;
    private JSONArray subComponentJsonArray;
    private String subComponentId = "";

    private LinearLayout mainActivityLLayout;
    private TextView mainActivityTView;
    private JSONArray mainActivityJsonArray;
    private String mainActivityId = "";

    private LinearLayout activityLLayout;
    private TextView activityTView;
    private JSONArray ActivityJsonArray;
    private String activityId = "";
    private String activityName = "";

    private Button nextButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pmushg_filter);

        /** For actionbar title in center */
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.attendance_actionbar_layout);
        AppCompatTextView actionTitleTextView = (AppCompatTextView) getSupportActionBar().getCustomView().findViewById(R.id.actionTitleTextView);
        homeBack = (ImageView) getSupportActionBar().getCustomView().findViewById(R.id.backImageView);
        // addPersonImageView = (ImageView) getSupportActionBar().getCustomView().findViewById(R.id.addPersonImageView);
        homeBack.setVisibility(View.VISIBLE);
        actionTitleTextView.setText("SHG Group");


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

        // For District
        districtLLayout = (LinearLayout) findViewById(R.id.districtLLayout);
        districtTView = (TextView) findViewById(R.id.districtTView);

        // For Subdivision
        subDivisionLLayout = (LinearLayout) findViewById(R.id.subDivisionLLayout);
        subDivisionTView = (TextView) findViewById(R.id.subDivisionTView);

        // For Taluka
        talukaLLayout = (LinearLayout) findViewById(R.id.talukaLLayout);
        talukaTView = (TextView) findViewById(R.id.talukaTView);

        // For Village
        villageLLayout = (LinearLayout) findViewById(R.id.villageLLayout);
        villageTView = (TextView) findViewById(R.id.villageTView);


        nextButton = (Button) findViewById(R.id.nextButton);
        nextButton.setText("Click for SHG List");
    }

    private void defaultConfiguration() {


        // For District
        districtLLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ApUtil.checkInternetConnection(PmuShgFilterActivity.this)) {

                    if (districtJsonArray != null) {
                        ApUtil.showCustomListPicker(districtTView, districtJsonArray, "Select District", "name", "id", PmuShgFilterActivity.this, PmuShgFilterActivity.this);

                    } else {
                        if (centerName.equalsIgnoreCase("Subdivision")) {
                            getDistrictList();
                        } else {
                            UIToastMessage.show(PmuShgFilterActivity.this, "Please select location");
                        }
                    }

                } else {
                    UIToastMessage.show(PmuShgFilterActivity.this, "No internet connection");
                }
            }
        });


        // For Sub-division
        subDivisionLLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ApUtil.checkInternetConnection(PmuShgFilterActivity.this)) {

                    if (subDivisionJsonArray != null) {
                        ApUtil.showCustomListPicker(subDivisionTView, subDivisionJsonArray, "Select Sub-division", "name", "id", PmuShgFilterActivity.this, PmuShgFilterActivity.this);
                    } else {
                        if (centerName.equalsIgnoreCase("Subdivision") && !districtId.equalsIgnoreCase("")) {
                            getSubDivisionList(districtId);
                        } else {
                            UIToastMessage.show(PmuShgFilterActivity.this, "Please select district");
                        }
                    }

                } else {
                    UIToastMessage.show(PmuShgFilterActivity.this, "No internet connection");
                }
            }
        });


        // For Taluka
        talukaLLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ApUtil.checkInternetConnection(PmuShgFilterActivity.this)) {

                    if (talukaJSONArray != null) {
                        ApUtil.showCustomListPicker(talukaTView, talukaJSONArray, "Select Taluka", "name", "id", PmuShgFilterActivity.this, PmuShgFilterActivity.this);
                    } else {
                        if (centerName.equalsIgnoreCase("Subdivision") && !subDivisionId.equalsIgnoreCase("")) {
                            getTalukaList(subDivisionId);
                        } else {
                            UIToastMessage.show(PmuShgFilterActivity.this, "Please select Sub-Division");
                        }
                    }

                } else {
                    UIToastMessage.show(PmuShgFilterActivity.this, "No internet connection");
                }
            }
        });


        // For Village
        villageLLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ApUtil.checkInternetConnection(PmuShgFilterActivity.this)) {

                    if (villageJSONArray != null) {
                        ApUtil.showCustomListPicker(villageTView, villageJSONArray, "Select Village", "name", "code", PmuShgFilterActivity.this, PmuShgFilterActivity.this);
                    } else {
                        if (centerName.equalsIgnoreCase("Subdivision") && !talukaId.equalsIgnoreCase("")) {
                            getVillageList(talukaId);
                        } else {
                            UIToastMessage.show(PmuShgFilterActivity.this, "Please select Taluka");
                        }
                    }

                } else {
                    UIToastMessage.show(PmuShgFilterActivity.this, "No internet connection");
                }
            }
        });
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextButtonAction();
            }
        });

        // To get District List
        districtId = "";
        districtTView.setText("");
        districtLLayout.setVisibility(View.VISIBLE);
        getDistrictList();

    }

    @Override
    protected void onResume() {
        super.onResume();
        // getListOfActivity();
    }


    private void getListOfActivity() {

        // To get Detail
        JSONObject param = new JSONObject();
        try {
            param.put("api_key", ApConstants.kAUTHORITY_KEY);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody requestBody = AppUtility.getInstance().getRequestBody(param.toString());
        AppinventorApi api = new AppinventorApi(this, APIServices.BASE_URL, "", ApConstants.kMSG, true);
        Retrofit retrofit = api.getRetrofitInstance();
        APIRequest apiRequest = retrofit.create(APIRequest.class);
        Call<JsonObject> responseCall = apiRequest.getListOfActivityRequest(requestBody);

        DebugLog.getInstance().d("get_farmer_activity_list_param=" + responseCall.request().toString());
        DebugLog.getInstance().d("get_farmer_activity_list_param=" + AppUtility.getInstance().bodyToString(responseCall.request()));

        api.postRequest(responseCall, this, 1);

    }


    // get DISTRICT
    private void getDistrictList() {
        AppinventorIncAPI api = new AppinventorIncAPI(this, APIServices.API_URL, "", ApConstants.kMSG, true);
        api.getRequestData(APIServices.GET_DIST_URL, this, 2);
    }


    private void getSubDivisionList(String districtId) {
        String subDivisionUrl = APIServices.GET_SUB_DIV_URL + districtId;
        AppinventorIncAPI api = new AppinventorIncAPI(this, APIServices.API_URL, "", ApConstants.kMSG, true);
        api.getRequestData(subDivisionUrl, this, 3);
    }

    // get Taluka
    private void getTalukaList(String subDivID) {
        String subTalukaUrl = APIServices.GET_TALUKA_URL + subDivID;
        AppinventorIncAPI api = new AppinventorIncAPI(this, APIServices.API_URL, "", ApConstants.kMSG, true);
        api.getRequestData(subTalukaUrl, this, 5);
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

        api.postRequest(responseCall, this, 6);

    }

    private void getComponentAndActivityList(String id, int reqCode) {
        String subTalukaUrl = APIServices.GET_COMPONENT_ACTIVITY_URL + villageCode + "/" + id;
        AppinventorIncAPI api = new AppinventorIncAPI(this, APIServices.API_URL, "", ApConstants.kMSG, true);
        api.getRequestData(subTalukaUrl, this, reqCode);
    }


    @Override
    public void didSelectAlertViewListItem(TextView textView, String s) {

        if (textView == districtTView) {

            districtId = s;

            subDivisionId = "";
            subDivisionTView.setText("");
            subDivisionLLayout.setVisibility(View.VISIBLE);
            getSubDivisionList(districtId);

            talukaId = "";
            talukaTView.setText("");
            talukaLLayout.setVisibility(View.GONE);

            villageCode = "";
            villageTView.setText("");
            villageLLayout.setVisibility(View.GONE);


        }


        if (textView == subDivisionTView) {
            subDivisionId = s;

            talukaId = "";
            talukaTView.setText("");
            talukaLLayout.setVisibility(View.VISIBLE);
            getTalukaList(subDivisionId);

            villageCode = "";
            villageTView.setText("");
            villageLLayout.setVisibility(View.GONE);


        }

        if (textView == talukaTView) {
            talukaId = s;

            villageCode = "";
            villageTView.setText("");
            villageLLayout.setVisibility(View.VISIBLE);
            getVillageList(talukaId);


        }

        if (textView == villageTView) {
            villageName = villageTView.getText().toString().trim();
            villageCode = s;
            // To get component

            getComponentAndActivityList("0", 7);


        }

        // Component
        if (textView == componentTView) {
            villageName = villageTView.getText().toString().trim();
            componentId = s;

            // To get sub component
            getComponentAndActivityList(s, 8);


        }

        // Sub Component
        if (textView == subComponentTView) {
            villageName = villageTView.getText().toString().trim();
            subComponentId = s;
            // To get Main Activity
            getComponentAndActivityList(s, 9);

        }

        // Main Activity
        if (textView == mainActivityTView) {
            villageName = villageTView.getText().toString().trim();
            mainActivityId = s;

            // To get activity
            getComponentAndActivityList(s, 10);


        }

        // Activity
        if (textView == activityTView) {
            villageName = villageTView.getText().toString().trim();
            activityId = s;
            activityName = activityTView.getText().toString().trim();
        }

    }


    private void nextButtonAction() {

        if (districtId.equalsIgnoreCase("")) {
            UIToastMessage.show(this, "Select district");
        } else if (subDivisionId.equalsIgnoreCase("")) {
            UIToastMessage.show(this, "Select Subdivision");
        } else if (talukaId.equalsIgnoreCase("")) {
            UIToastMessage.show(this, "Select Taluka");
        } else if (villageCode.equalsIgnoreCase("")) {
            UIToastMessage.show(this, "Select Village");
        } else {

            JSONObject villageData = new JSONObject();

            for (int v = 0; v < villageJSONArray.length(); v++) {
                try {
                    JSONObject vJson = villageJSONArray.getJSONObject(v);
                    String vCode = vJson.getString("code");
                    if (vCode.equalsIgnoreCase(villageCode)) {
                        villageData = vJson;
                        break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            Intent intent = new Intent(this, CaSHGFarmerGroupListActivity.class);
            intent.putExtra("distId", districtId);
            intent.putExtra("subDivId", subDivisionId);
            intent.putExtra("talId", talukaId);
            intent.putExtra("viCode", villageCode);
            intent.putExtra("viName", villageName);

            intent.putExtra("vData", villageData.toString());
            this.startActivity(intent);

            /*Intent intent = new Intent(this, PmuParticipantListActivity.class);
            intent.putExtra("center", centerName);
            intent.putExtra("distId", districtId);
            intent.putExtra("subDiv", subDivisionId);
            intent.putExtra("talukaId", talukaId);
            intent.putExtra("villageCode", villageCode);
            intent.putExtra("pRoleId", pRoleId);
            intent.putExtra("staffType", ApConstants.kEVENT_MEM_PFS);
            startActivity(intent);*/

        }

    }


    @Override
    public void onResponse(JSONObject jsonObject, int i) {
        if (jsonObject != null) {

            try {
                if (i == 2) {
                    ResponseModel distListRMode = new ResponseModel(jsonObject);
                    if (distListRMode.isStatus()) {
                        districtJsonArray = jsonObject.getJSONArray("data");
                    }
                }

                // Sub-division Response
                if (i == 3) {
                    String status = jsonObject.getString("status");
                    if (status.equalsIgnoreCase("200")) {
                        subDivisionJsonArray = jsonObject.getJSONArray("data");
                    }
                }

                // Taluka Response
                if (i == 5) {
                    String status = jsonObject.getString("status");
                    if (status.equalsIgnoreCase("200")) {
                        talukaJSONArray = jsonObject.getJSONArray("data");
                    }
                }

                // Village Response
                if (i == 6) {
                    String status = jsonObject.getString("status");
                    if (status.equalsIgnoreCase("200")) {
                        villageJSONArray = jsonObject.getJSONArray("data");
                    }
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }

    @Override
    public void onFailure(Throwable throwable, int i) {

    }

    @Override
    public void onFailure(Object o, Throwable throwable, int i) {

    }


    @Override
    public void onMultiRecyclerViewItemClick(int i, Object o) {

    }
}
