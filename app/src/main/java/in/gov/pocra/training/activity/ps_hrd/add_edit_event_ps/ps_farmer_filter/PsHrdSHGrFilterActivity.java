package in.gov.pocra.training.activity.ps_hrd.add_edit_event_ps.ps_farmer_filter;

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
import in.co.appinventor.services_api.settings.AppSettings;
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

public class PsHrdSHGrFilterActivity extends AppCompatActivity implements OnMultiRecyclerItemClickListener, AlertListCallbackEventListener, ApiCallbackCode, ApiJSONObjCallback {


    private ImageView homeBack;
    private AlertDialog.Builder builder;

    // For Location
    private String centerName = "Subdivision";

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
        setContentView(R.layout.activity_shgfarmergroup);

        /** For actionbar title in center */
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.attendance_actionbar_layout);
        AppCompatTextView actionTitleTextView = (AppCompatTextView) getSupportActionBar().getCustomView().findViewById(R.id.actionTitleTextView);
        homeBack = (ImageView) getSupportActionBar().getCustomView().findViewById(R.id.backImageView);
        // addPersonImageView = (ImageView) getSupportActionBar().getCustomView().findViewById(R.id.addPersonImageView);
        homeBack.setVisibility(View.VISIBLE);
        actionTitleTextView.setText(getResources().getString(R.string.title_farmer_filter));

        initialization();
        defaultConfiguration();
    }

    private void initialization() {

        String distID = AppSettings.getInstance().getValue(this, ApConstants.kUSER_DIST_ID, ApConstants.kUSER_DIST_ID);
        if (!distID.equalsIgnoreCase("kUSER_DIST_ID")) {
            districtId = distID;
        }

        // For Action Bar
        homeBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

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

        nextButton.setText("Click for SHG Group");

    }




    private void defaultConfiguration() {

        // For Sub-division
        subDivisionLLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ApUtil.checkInternetConnection(PsHrdSHGrFilterActivity.this)) {

                    if (subDivisionJsonArray != null) {
                        ApUtil.showCustomListPicker(subDivisionTView, subDivisionJsonArray, "Select Sub-division", "name", "id", PsHrdSHGrFilterActivity.this, PsHrdSHGrFilterActivity.this);
                    } else {
                        if (centerName.equalsIgnoreCase("Subdivision") && !districtId.equalsIgnoreCase("")) {
                            getSubDivisionList(districtId);
                        } else {
                            UIToastMessage.show(PsHrdSHGrFilterActivity.this, "Please select district");
                        }
                    }

                } else {
                    UIToastMessage.show(PsHrdSHGrFilterActivity.this, "No internet connection");
                }
            }
        });


        // For Taluka
        talukaLLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ApUtil.checkInternetConnection(PsHrdSHGrFilterActivity.this)) {

                    if (talukaJSONArray != null) {
                        ApUtil.showCustomListPicker(talukaTView, talukaJSONArray, "Select Taluka", "name", "id", PsHrdSHGrFilterActivity.this, PsHrdSHGrFilterActivity.this);
                    } else {
                        if (centerName.equalsIgnoreCase("Subdivision") && !subDivisionId.equalsIgnoreCase("")) {
                            getTalukaList(subDivisionId);
                        } else {
                            UIToastMessage.show(PsHrdSHGrFilterActivity.this, "Please select Sub-Division");
                        }
                    }

                } else {
                    UIToastMessage.show(PsHrdSHGrFilterActivity.this, "No internet connection");
                }
            }
        });


        // For Village
        villageLLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ApUtil.checkInternetConnection(PsHrdSHGrFilterActivity.this)) {

                    if (villageJSONArray != null) {
                        ApUtil.showCustomListPicker(villageTView, villageJSONArray, "Select Village", "name", "code", PsHrdSHGrFilterActivity.this, PsHrdSHGrFilterActivity.this);
                    } else {
                        if (centerName.equalsIgnoreCase("Subdivision") && !talukaId.equalsIgnoreCase("")) {
                            getVillageList(talukaId);
                        } else {
                            UIToastMessage.show(PsHrdSHGrFilterActivity.this, "Please select Taluka");
                        }
                    }

                } else {
                    UIToastMessage.show(PsHrdSHGrFilterActivity.this, "No internet connection");
                }
            }
        });




        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextButtonAction();
            }
        });

        // To get Subdivision List
        subDivisionId = "";
        subDivisionTView.setText("");
        subDivisionLLayout.setVisibility(View.VISIBLE);
        getSubDivisionList(districtId);


    }

    @Override
    protected void onResume() {
        super.onResume();
       //  getListOfActivity();
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
    private void getComponentAndActivityList(String id,int reqCode) {
        String subTalukaUrl = APIServices.GET_COMPONENT_ACTIVITY_URL +villageCode+"/"+ id;
        AppinventorIncAPI api = new AppinventorIncAPI(this, APIServices.API_URL, "", ApConstants.kMSG, true);
        api.getRequestData(subTalukaUrl, this, reqCode);
    }

    @Override
    public void didSelectAlertViewListItem(TextView textView, String s) {

        /*if (textView == activityTView) {
            activityId = s;

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

        }*/

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

            getComponentAndActivityList("0",7);


        }


    }


    private void nextButtonAction() {

        if (subDivisionId.equalsIgnoreCase("")) {
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
            intent.putExtra("viCode", villageCode);
            intent.putExtra("distId", districtId);
            intent.putExtra("subDivId", subDivisionId);
            intent.putExtra("talId", talukaId);
            intent.putExtra("viName", villageName);

            intent.putExtra("vData", villageData.toString());
            this.startActivity(intent);

        }

    }


    private void getListOfActivity() {

        // To get Detail
        JSONObject param = new JSONObject();

        try {
            param.put("api_key", "a910d2ba49ef2e4a74f8e0056749b10d");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody requestBody = AppUtility.getInstance().getRequestBody(param.toString());
        AppinventorApi api = new AppinventorApi(this, APIServices.BASE_URL, "", ApConstants.kMSG, true);
        Retrofit retrofit = api.getRetrofitInstance();
        APIRequest apiRequest = retrofit.create(APIRequest.class);
        Call<JsonObject> responseCall = apiRequest.getListOfActivityRequest(requestBody);

        DebugLog.getInstance().d("VCRMC_Member_Detail_update_list_param=" + responseCall.request().toString());
        DebugLog.getInstance().d("VCRMC_Member_Detail_update_list_param=" + AppUtility.getInstance().bodyToString(responseCall.request()));

        api.postRequest(responseCall, this, 1);

    }

    @Override
    public void onResponse(JSONObject jsonObject, int i) {
        if (jsonObject != null) {

            try {
                if (i == 1) {

                    if (jsonObject.getString("status").equalsIgnoreCase("200")) {
                        ResponseModel responseModel = new ResponseModel(jsonObject);
                        if (responseModel.isStatus()) {
                            ActivityJsonArray = jsonObject.getJSONArray("data");

                        }
                    } else {
                        //UIToastMessage.show(this, jsonObject.getString("response"));
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
                    }else {
                        UIToastMessage.show(this,jsonObject.getString("response"));
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
