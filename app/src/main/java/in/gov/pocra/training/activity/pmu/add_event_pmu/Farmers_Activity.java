package in.gov.pocra.training.activity.pmu.add_event_pmu;

import android.app.AlertDialog;
import android.content.Intent;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
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
import in.co.appinventor.services_api.listener.OnMultiRecyclerItemClickListener;
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

public class Farmers_Activity extends AppCompatActivity implements OnMultiRecyclerItemClickListener, AlertListCallbackEventListener, ApiCallbackCode, ApiJSONObjCallback {

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
    private String villageId = "";

  /*  private LinearLayout roleLLayout;
    private TextView roleTView;
    private JSONArray roleJsonArray;
    private String pRoleId = "";*/

    private Button nextButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_farmers_);

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

        // For Role
       /* roleLLayout = (LinearLayout) findViewById(R.id.roleLLayout);
        roleTView = (TextView) findViewById(R.id.roleTView);*/

        nextButton = (Button) findViewById(R.id.nextButton);
    }

    private void defaultConfiguration() {

        // For District
        districtLLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ApUtil.checkInternetConnection(Farmers_Activity.this)) {

                    if (districtJsonArray != null) {
                        ApUtil.showCustomListPicker(districtTView, districtJsonArray, "Select District", "name", "id", Farmers_Activity.this, Farmers_Activity.this);

                    } else {
                        if (centerName.equalsIgnoreCase("Subdivision")) {
                            getDistrictList();
                        } else {
                            UIToastMessage.show(Farmers_Activity.this, "Please select location");
                        }
                    }

                } else {
                    UIToastMessage.show(Farmers_Activity.this, "No internet connection");
                }
            }
        });


        // For Sub-division
        subDivisionLLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ApUtil.checkInternetConnection(Farmers_Activity.this)) {

                    if (subDivisionJsonArray != null) {
                        ApUtil.showCustomListPicker(subDivisionTView, subDivisionJsonArray, "Select Sub-division", "name", "id", Farmers_Activity.this, Farmers_Activity.this);
                    } else {
                        if (centerName.equalsIgnoreCase("Subdivision") && !districtId.equalsIgnoreCase("")) {
                            getSubDivisionList(districtId);
                        } else {
                            UIToastMessage.show(Farmers_Activity.this, "Please select district");
                        }
                    }

                } else {
                    UIToastMessage.show(Farmers_Activity.this, "No internet connection");
                }
            }
        });



        // For Taluka
        talukaLLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ApUtil.checkInternetConnection(Farmers_Activity.this)) {

                    if (talukaJSONArray != null) {
                        ApUtil.showCustomListPicker(talukaTView, talukaJSONArray, "Select Taluka", "name", "id", Farmers_Activity.this, Farmers_Activity.this);
                    } else {
                        if (centerName.equalsIgnoreCase("Subdivision") && !subDivisionId.equalsIgnoreCase("")) {
                            getTalukaList(subDivisionId);
                        } else {
                            UIToastMessage.show(Farmers_Activity.this, "Please select Sub-Division");
                        }
                    }

                } else {
                    UIToastMessage.show(Farmers_Activity.this, "No internet connection");
                }
            }
        });


        // For Village
        villageLLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ApUtil.checkInternetConnection(Farmers_Activity.this)) {

                    if (villageJSONArray != null) {
                        ApUtil.showCustomListPicker(villageTView, villageJSONArray, "Select Village", "name", "id", Farmers_Activity.this, Farmers_Activity.this);
                    } else {
                        if (centerName.equalsIgnoreCase("Subdivision") && !talukaId.equalsIgnoreCase("")) {
                            getVillageList(talukaId);
                        } else {
                            UIToastMessage.show(Farmers_Activity.this, "Please select Taluka");
                        }
                    }

                } else {
                    UIToastMessage.show(Farmers_Activity.this, "No internet connection");
                }
            }
        });



        // For participant role
        /*roleLLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ApUtil.checkInternetConnection(Farmers_Activity.this)) {

                    if (roleJsonArray != null) {
                        ApUtil.showCustomListPicker(roleTView, roleJsonArray, "Select Roles/Posts/Positions ", "name", "id", Farmers_Activity.this, Farmers_Activity.this);
                    } else {
                        if (!centerName.isEmpty()) {
                            getRoleList(centerName);
                        } else {
                            UIToastMessage.show(Farmers_Activity.this, "Please select location");
                        }

                    }

                } else {
                    UIToastMessage.show(Farmers_Activity.this, "No internet connection");
                }
            }
        });*/


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
        getDistrictList();
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
        AppinventorIncAPI api = new AppinventorIncAPI(this, APIServices.API_URL, "", ApConstants.kMSG, true);
        api.getRequestData(subDivisionUrl, this, 2);
    }

    // get Taluka
    private void getTalukaList(String subDivID) {
        String subTalukaUrl = APIServices.GET_TALUKA_URL + subDivID;
        AppinventorIncAPI api = new AppinventorIncAPI(this, APIServices.API_URL, "", ApConstants.kMSG, true);
        api.getRequestData(subTalukaUrl, this, 3);
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

        api.postRequest(responseCall, this, 4);

    }


   /* private void getVillageList(String talukaId) {
        String subTalukaUrl = APIServices.GET_TALUKA_URL + talukaId;
        AppinventorIncAPI api = new AppinventorIncAPI(this, APIServices.API_URL, "", ApConstants.kMSG, true);
        api.getRequestData(subTalukaUrl, this, 4);
    }*/

   /* private void getRoleList(String centerName) {

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

    }*/

    @Override
    public void didSelectAlertViewListItem(TextView textView, String s) {

        if (textView == districtTView) {

            districtId = s;

            if (centerName.equalsIgnoreCase("Subdivision")) {
                subDivisionId = "";
                subDivisionTView.setText("");
                subDivisionLLayout.setVisibility(View.VISIBLE);

                talukaId = "";
                talukaTView.setText("");
                talukaLLayout.setVisibility(View.GONE);
                getSubDivisionList(districtId);

                /*pRoleId = "";
                roleTView.setText("");
                roleLLayout.setVisibility(View.GONE);*/
            }
        }


        if (textView == subDivisionTView) {
            subDivisionId = s;

            talukaId = "";
            talukaTView.setText("");
            talukaLLayout.setVisibility(View.VISIBLE);
            getTalukaList(subDivisionId);

            /*pRoleId = "";
            roleTView.setText("");
            roleLLayout.setVisibility(View.GONE);*/

        }

        if (textView == talukaTView) {
            villageId = s;

            villageId = "";
            villageTView.setText("");
            villageLLayout.setVisibility(View.VISIBLE);
            getVillageList(talukaId);

           /* pRoleId = "";
            roleTView.setText("");
            roleLLayout.setVisibility(View.VISIBLE);*/

        }

        /*if (textView == villageTView) {
            villageId = s;
        }*/

      /*  if (textView == roleTView) {
            pRoleId = s;

            villageId = "";
            villageTView.setText("");

            if (pRoleId.equalsIgnoreCase("28") || pRoleId.equalsIgnoreCase("26")){
                villageLLayout.setVisibility(View.VISIBLE);
            }else {
                villageLLayout.setVisibility(View.GONE);
            }
        }*/
    }


    private void nextButtonAction() {

        if (districtId.equalsIgnoreCase("") ){
            UIToastMessage.show(this,"Select district");
        }else if (subDivisionId.equalsIgnoreCase("") ){
            UIToastMessage.show(this,"Select Subdivision");
        }else if (talukaId.equalsIgnoreCase("") ){
            UIToastMessage.show(this,"Select Taluka");
        }/*else if ( pRoleId.equalsIgnoreCase("") ){
            UIToastMessage.show(this,"Select Role");
        }*/else {
            Intent intent = new Intent(this, PmuParticipantListActivity.class);
            intent.putExtra("center", centerName);
            intent.putExtra("distId", districtId);
            intent.putExtra("subDiv", subDivisionId);
            intent.putExtra("talukaId", talukaId);
            intent.putExtra("villageId", villageId);
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
               /* if (i == 3) {
                    ResponseModel responseModel = new ResponseModel(jsonObject);
                    if (responseModel.isStatus()) {
                        roleJsonArray = jsonObject.getJSONArray("data");
                    }
                }*/

                // Taluka Response
                if (i == 3) {
                    String status = jsonObject.getString("status");
                    if (status.equalsIgnoreCase("200")) {
                        talukaJSONArray = jsonObject.getJSONArray("data");
                    }
                }

                // Village Response
                if (i == 4) {
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
