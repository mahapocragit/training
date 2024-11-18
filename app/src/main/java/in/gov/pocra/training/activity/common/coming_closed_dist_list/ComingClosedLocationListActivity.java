package in.gov.pocra.training.activity.common.coming_closed_dist_list;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
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
import in.co.appinventor.services_api.settings.AppSettings;
import in.co.appinventor.services_api.widget.UIToastMessage;
import in.gov.pocra.training.R;
import in.gov.pocra.training.activity.ca.ca_report.CaReportActivity;
import in.gov.pocra.training.activity.ca.ca_upcoming_event.CaComingEventActivity;
import in.gov.pocra.training.activity.pmu.pmu_report.PmuReportActivity;
import in.gov.pocra.training.activity.pmu.pmu_upcoming_event.PmuComingEventActivity;
import in.gov.pocra.training.activity.ps_hrd.add_edit_event_ps.vcrmc_mem_selection.SubDivListActivity;
import in.gov.pocra.training.activity.ps_hrd.ps_report.PsReportActivity;
import in.gov.pocra.training.activity.ps_hrd.ps_upcoming_event.PsComingEventActivity;
import in.gov.pocra.training.model.online.ResponseModel;
import in.gov.pocra.training.util.ApConstants;
import in.gov.pocra.training.util.ApUtil;
import in.gov.pocra.training.util.AppHelper;
import in.gov.pocra.training.web_services.APIRequest;
import in.gov.pocra.training.web_services.APIServices;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Retrofit;

public class ComingClosedLocationListActivity extends AppCompatActivity implements ApiJSONObjCallback, OnMultiRecyclerItemClickListener, ApiCallbackCode, AlertListCallbackEventListener {

    private ImageView homeBack;
    private String roleId;
    private String userID;
    private String userLaval;

    private String actType = "";

    private LinearLayout locationLLayout;
    private JSONArray locationJsonArray;
    private TextView locationTView;
    private String centerName = "";
    private String locationId = "";

    private LinearLayout distLLayout;
    private TextView disTView;
    private RecyclerView districtRView;

    // For Subdivision
    private JSONArray distJSONArray;
    private String sledDistId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comimg_closed_location_list);

        /** For actionbar title in center */
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.attendance_actionbar_layout);
        AppCompatTextView actionTitleTextView = (AppCompatTextView) getSupportActionBar().getCustomView().findViewById(R.id.actionTitleTextView);
        homeBack = (ImageView) getSupportActionBar().getCustomView().findViewById(R.id.backImageView);
        // addPersonImageView = (ImageView) getSupportActionBar().getCustomView().findViewById(R.id.addPersonImageView);
        homeBack.setVisibility(View.VISIBLE);
        //  addPersonImageView.setVisibility(View.VISIBLE);
        actionTitleTextView.setText("Select Location");

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

        String rLaval = AppSettings.getInstance().getValue(this, ApConstants.kUSER_LEVEL, ApConstants.kUSER_LEVEL);
        if (!rLaval.equalsIgnoreCase("kUSER_LEVEL")) {
            userLaval = rLaval;
        }

        locationLLayout = (LinearLayout) findViewById(R.id.locationLLayout);
        locationTView = (TextView) findViewById(R.id.locationTView);

        distLLayout = (LinearLayout) findViewById(R.id.distLLayout);
        disTView = (TextView) findViewById(R.id.disTView);

        districtRView = (RecyclerView) findViewById(R.id.districtRView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        districtRView.setLayoutManager(linearLayoutManager);

    }

    @Override
    protected void onResume() {
        super.onResume();

        actType = getIntent().getStringExtra("actType");
        // getDistrictList();

    }


    private void defaultConfiguration() {
        locationJsonArray = new JSONArray();
        if (userLaval.equalsIgnoreCase("pmu")) {
            locationJsonArray = AppHelper.getInstance().getMemLocationJsonArray();
        } else if (userLaval.equalsIgnoreCase("district")) {
            locationJsonArray = AppHelper.getInstance().getDistMemLocationJsonArray();
        }

        locationLLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (locationJsonArray != null) {
                    ApUtil.showCustomListPicker(locationTView, locationJsonArray, "Select Location", "name", "id", ComingClosedLocationListActivity.this, ComingClosedLocationListActivity.this);
                }
            }
        });


        distLLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (distJSONArray != null) {
                    ApUtil.showCustomListPicker(disTView, distJSONArray, "Select District", "name", "id", ComingClosedLocationListActivity.this, ComingClosedLocationListActivity.this);
                } else {
                    getDistrictList();
                }
            }
        });


    }

    private void eventListener() {

    }


    private void getClosedDistrictList() {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("user_id", userID);
            jsonObject.put("role_id", roleId);
            jsonObject.put("api_key", ApConstants.kAUTHORITY_KEY);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString());
        AppinventorApi api = new AppinventorApi(this, APIServices.BASE_URL, "", ApConstants.kMSG, true);
        Retrofit retrofit = api.getRetrofitInstance();
        APIRequest apiRequest = retrofit.create(APIRequest.class);

        // Call<JsonObject> responseCall = null;
        if (!actType.equalsIgnoreCase("") && actType.equalsIgnoreCase("upComingEvent")) {
            Call<JsonObject> responseCall = apiRequest.pmuComingEventDistListRequest(requestBody);
            api.postRequest(responseCall, this, 1);
            DebugLog.getInstance().d("coming_event_dist_list_param=" + responseCall.request().toString());
            DebugLog.getInstance().d("coming_event_dist_list_param=" + AppUtility.getInstance().bodyToString(responseCall.request()));
        } else if (!actType.equalsIgnoreCase("") && actType.equalsIgnoreCase("report")) {
            Call<JsonObject> responseCall = apiRequest.pmuClosedEventDistListRequest(requestBody);
            api.postRequest(responseCall, this, 1);
            DebugLog.getInstance().d("closed_event_dist_list_param=" + responseCall.request().toString());
            DebugLog.getInstance().d("closed_event_dist_list_param=" + AppUtility.getInstance().bodyToString(responseCall.request()));
        }

        /*Call<JsonObject> responseCall = apiRequest.pmuClosedEventDistListRequest(requestBody);
        api.postRequest(responseCall, this, 1);*/

    }


    // get DISTRICT
    private void getDistrictList() {
        AppinventorIncAPI api = new AppinventorIncAPI(this, APIServices.API_URL, "", ApConstants.kMSG, true);
        api.getRequestData(APIServices.GET_DIST_URL, this, 2);
    }

    private void getSubDivisionListWithUCount() {
        AppinventorIncAPI api = new AppinventorIncAPI(this, APIServices.API_URL, "", ApConstants.kMSG, true);
        api.getRequestData(APIServices.GET_SUB_DIV_E_U_COUNT_URL, this, 4);
    }

    private void getSubDivisionListWithCCount() {
        AppinventorIncAPI api = new AppinventorIncAPI(this, APIServices.API_URL, "", ApConstants.kMSG, true);
        api.getRequestData(APIServices.GET_SUB_DIV_E_C_COUNT_URL, this, 4);
    }

    // get Sub-division
    private void getSubDivisionList(String distId) {
        String subDivisionUrl = APIServices.GET_SUB_DIV_URL + distId;
        AppinventorIncAPI api = new AppinventorIncAPI(this, APIServices.API_URL, "", ApConstants.kMSG, true);
        api.getRequestData(subDivisionUrl, this, 3);
    }

    private void getClosedSubDivisionList(String distId) {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("district_id", distId);
            jsonObject.put("api_key", ApConstants.kAUTHORITY_KEY);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString());
        AppinventorApi api = new AppinventorApi(this, APIServices.BASE_URL, "", ApConstants.kMSG, true);
        Retrofit retrofit = api.getRetrofitInstance();
        APIRequest apiRequest = retrofit.create(APIRequest.class);

        // Call<JsonObject> responseCall = null;
        if (!actType.equalsIgnoreCase("") && actType.equalsIgnoreCase("upComingEvent")) {
            Call<JsonObject> responseCall = apiRequest.caGetSubDivListWithCountRequest(requestBody);
            api.postRequest(responseCall, this, 3);
            DebugLog.getInstance().d("coming_event_sub_div_list_param=" + responseCall.request().toString());
            DebugLog.getInstance().d("coming_event_sub_div_list_param=" + AppUtility.getInstance().bodyToString(responseCall.request()));
        } else if (!actType.equalsIgnoreCase("") && actType.equalsIgnoreCase("report")) {
            Call<JsonObject> responseCall = apiRequest.caGetSubDivListWithClosedCountRequest(requestBody);
            api.postRequest(responseCall, this, 3);
            DebugLog.getInstance().d("closed_event_sub_div_list_param=" + responseCall.request().toString());
            DebugLog.getInstance().d("closed_event__sub_div_list_param=" + AppUtility.getInstance().bodyToString(responseCall.request()));
        }

    }


    @Override
    public void onMultiRecyclerViewItemClick(int i, Object o) {

        if (i == 1) {
            JSONObject distJSON = (JSONObject) o;

            try {
                String distId = "";
                if (centerName.equalsIgnoreCase("District")) {
                    distId = distJSON.getString("id");
                } else {
                    distId = sledDistId;
                }

                String subDivId = "";
                if (centerName.equalsIgnoreCase("Subdivision")) {
                    subDivId = distJSON.getString("id");
                    if (userLaval.equalsIgnoreCase("District")){
                        distId = sledDistId;
                    }else {
                        distId = distJSON.getString("d_id");
                    }

                }

                if (!distId.equalsIgnoreCase("")) {

                    if (!actType.equalsIgnoreCase("") && actType.equalsIgnoreCase("upComingEvent")) {

                        if (userLaval.equalsIgnoreCase("pmu")) {
                            Intent intent = new Intent(ComingClosedLocationListActivity.this, PmuComingEventActivity.class);
                            intent.putExtra("level", centerName);
                            intent.putExtra("distId", distId);
                            intent.putExtra("subDivId", subDivId);
                            intent.putExtra("eventType", ApConstants.kS_ALL_EVENT);
                            startActivity(intent);
                        } else if (userLaval.equalsIgnoreCase("District")) {
                            Intent intent = new Intent(ComingClosedLocationListActivity.this, PsComingEventActivity.class);
                            intent.putExtra("level", centerName);
                            intent.putExtra("distId", distId);
                            intent.putExtra("subDivId", subDivId);
                            intent.putExtra("eventOpt", ApConstants.kS_ALL_EVENT);
                            startActivity(intent);
                        } else {
                            Intent intent = new Intent(ComingClosedLocationListActivity.this, CaComingEventActivity.class);
                            intent.putExtra("level", centerName);
                            intent.putExtra("distId", distId);
                            intent.putExtra("subDivId", subDivId);
                            intent.putExtra("eventType", ApConstants.kS_ALL_EVENT);
                        }

                    } else if (!actType.equalsIgnoreCase("") && actType.equalsIgnoreCase("report")) {

                        if (userLaval.equalsIgnoreCase("pmu")) {
                            Intent intent = new Intent(ComingClosedLocationListActivity.this, PmuReportActivity.class);
                            intent.putExtra("level", centerName);
                            intent.putExtra("distId", distId);
                            intent.putExtra("subDivId", subDivId);
                            intent.putExtra("eventType", ApConstants.kS_ALL_EVENT);
                            startActivity(intent);
                        } else if (userLaval.equalsIgnoreCase("District")) {
                            Intent intent = new Intent(ComingClosedLocationListActivity.this, PsReportActivity.class);
                            intent.putExtra("level", centerName);
                            intent.putExtra("distId", distId);
                            intent.putExtra("subDivId", subDivId);
                            intent.putExtra("eventOpt", ApConstants.kS_ALL_EVENT);
                            startActivity(intent);
                        } else {
                            Intent intent = new Intent(ComingClosedLocationListActivity.this, CaReportActivity.class);
                            intent.putExtra("level", centerName);
                            intent.putExtra("distId", distId);
                            intent.putExtra("subDivId", subDivId);
                            intent.putExtra("eventOpt", ApConstants.kS_ALL_EVENT);
                            startActivity(intent);
                        }

                    } else if (!actType.equalsIgnoreCase("") && actType.equalsIgnoreCase("PMU_VCRMC")) {
                        AppSettings.getInstance().setValue(this, ApConstants.kUSER_DIST_ID, distId);
                        Intent intent = new Intent(ComingClosedLocationListActivity.this, SubDivListActivity.class);
                        startActivity(intent);
                    } else if (!actType.equalsIgnoreCase("") && actType.equalsIgnoreCase("PMU_FIELD_STAFF")) {
                        AppSettings.getInstance().setValue(this, ApConstants.kUSER_DIST_ID, distId);
                        Intent intent = new Intent(ComingClosedLocationListActivity.this, SubDivListActivity.class);
                        startActivity(intent);
                    }

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void didSelectAlertViewListItem(TextView textView, String s) {

        if (textView == locationTView) {

            String cName = locationTView.getText().toString().trim();
            centerName = ApUtil.toTitleCase(cName);

            if (centerName.equalsIgnoreCase("Pmu")) {

                locationId = s;
                districtRView.setVisibility(View.GONE);
                distLLayout.setVisibility(View.GONE);
                disTView.setText("");

                if (!actType.equalsIgnoreCase("") && actType.equalsIgnoreCase("upComingEvent")) {
                    Intent intent = new Intent(ComingClosedLocationListActivity.this, PmuComingEventActivity.class);
                    intent.putExtra("level", centerName);
                    intent.putExtra("distId", "");
                    intent.putExtra("eventType", ApConstants.kS_ALL_EVENT);
                    startActivity(intent);
                } else if (!actType.equalsIgnoreCase("") && actType.equalsIgnoreCase("report")) {
                    Intent intent = new Intent(ComingClosedLocationListActivity.this, PmuReportActivity.class);
                    intent.putExtra("level", centerName);
                    intent.putExtra("distId", "");
                    intent.putExtra("eventType", ApConstants.kS_ALL_EVENT);
                    startActivity(intent);
                }

            } else if (centerName.equalsIgnoreCase("District")) {

                if (userLaval.equalsIgnoreCase("pmu")) {
                    locationId = "";
                    sledDistId = s;
                    districtRView.setVisibility(View.VISIBLE);
                    distLLayout.setVisibility(View.GONE);
                    disTView.setText("");
                    getClosedDistrictList();
                } else if (userLaval.equalsIgnoreCase("district")) {
                    districtRView.setVisibility(View.GONE);
                    distLLayout.setVisibility(View.GONE);
                    String districtId = "";
                    if (userLaval.equalsIgnoreCase("district")) {
                        String distID = AppSettings.getInstance().getValue(this, ApConstants.kUSER_DIST_ID, ApConstants.kUSER_DIST_ID);
                        if (!distID.equalsIgnoreCase("kUSER_DIST_ID")) {
                            districtId = distID;
                        }
                    }
                    if (!actType.equalsIgnoreCase("") && actType.equalsIgnoreCase("upComingEvent")) {
                        Intent intent = new Intent(ComingClosedLocationListActivity.this, PsComingEventActivity.class);
                        intent.putExtra("level", centerName);
                        intent.putExtra("distId", districtId);
                        intent.putExtra("eventOpt", ApConstants.kS_ALL_EVENT);
                        startActivity(intent);
                    } else if (!actType.equalsIgnoreCase("") && actType.equalsIgnoreCase("report")) {
                        Intent intent = new Intent(ComingClosedLocationListActivity.this, PsReportActivity.class);
                        intent.putExtra("level", centerName);
                        intent.putExtra("subDivId", "");
                        intent.putExtra("distId", districtId);
                        intent.putExtra("eventOpt", ApConstants.kS_ALL_EVENT);
                        startActivity(intent);
                    }

                }


            } else if (centerName.equalsIgnoreCase("Subdivision")) {
                locationId = "";
                if (userLaval.equalsIgnoreCase("pmu")) {
                    distLLayout.setEnabled(true);
                    if (!actType.equalsIgnoreCase("") && actType.equalsIgnoreCase("upComingEvent")) {
                        getSubDivisionListWithUCount();
                    }else if (!actType.equalsIgnoreCase("") && actType.equalsIgnoreCase("report")) {
                        getSubDivisionListWithCCount();
                    }

                    // getDistrictList();
                    distLLayout.setVisibility(View.GONE);
                    districtRView.setVisibility(View.GONE);

                }else if (userLaval.equalsIgnoreCase("district")){

                    distLLayout.setEnabled(false);

                    String loginData = AppSettings.getInstance().getValue(this,ApConstants.kLOGIN_DATA,ApConstants.kLOGIN_DATA);
                    if (!loginData.equalsIgnoreCase("kLOGIN_DATA")){
                        try {
                            JSONObject loginJSON = new JSONObject(loginData);

                            String districtId = loginJSON.getString("district_id");
                            String districtName = loginJSON.getString("district_name");
                            sledDistId = districtId;
                            disTView.setText(districtName);

                            distLLayout.setVisibility(View.VISIBLE);
                            districtRView.setVisibility(View.VISIBLE);

                            getClosedSubDivisionList(sledDistId);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

        if (textView == disTView) {
            // getSubDivisionList(s);
            sledDistId = s;
            getClosedSubDivisionList(s);
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
                        if (distJSONArray.length() > 0) {
                            AdaptorComingDistrictList adaptorComingDistrictList = new AdaptorComingDistrictList(this, distJSONArray, this);
                            districtRView.setAdapter(adaptorComingDistrictList);
                        }

                    } else {
                        UIToastMessage.show(this, responseModel.getMsg());
                    }
                }

                if (i == 2) {
                    ResponseModel responseModel = new ResponseModel(jsonObject);
                    // String status = jsonObject.getString("status");
                    if (responseModel.isStatus()) {
                        distJSONArray = jsonObject.getJSONArray("data");
                    }
                }

                if (i == 3) {
                    ResponseModel responseModel = new ResponseModel(jsonObject);
                    // String status = jsonObject.getString("status");
                    if (responseModel.isStatus()) {
                        JSONArray subDivJSONArray = jsonObject.getJSONArray("data");
                        if (subDivJSONArray.length() > 0) {
                            districtRView.setVisibility(View.VISIBLE);
                            AdaptorComingSubdivisionList adaptorComingSubdivisionList = new AdaptorComingSubdivisionList(this, subDivJSONArray, this);
                            districtRView.setAdapter(adaptorComingSubdivisionList);
                        } else {
                            districtRView.setVisibility(View.GONE);
                        }

                    } else {
                        UIToastMessage.show(this, responseModel.getMsg());
                    }
                }

                if (i == 4) {
                    ResponseModel responseModel = new ResponseModel(jsonObject);
                    // String status = jsonObject.getString("status");
                    if (responseModel.isStatus()) {
                        JSONArray subDivJSONArray = jsonObject.getJSONArray("data");
                        if (subDivJSONArray.length() > 0) {
                            districtRView.setVisibility(View.VISIBLE);
                            distLLayout.setVisibility(View.GONE);
                            AdaptorComingSubdivisionWithDistrictList adaptorComingSubdivisionWithDistrictList = new AdaptorComingSubdivisionWithDistrictList(this, subDivJSONArray, this,this);
                            districtRView.setAdapter(adaptorComingSubdivisionWithDistrictList);
                        } else {
                            districtRView.setVisibility(View.GONE);
                        }

                    } else {
                        UIToastMessage.show(this, responseModel.getMsg());
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
