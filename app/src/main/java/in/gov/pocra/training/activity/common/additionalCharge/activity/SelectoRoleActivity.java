package in.gov.pocra.training.activity.common.additionalCharge.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import in.co.appinventor.services_api.api.AppinventorApi;
import in.co.appinventor.services_api.app_util.AppConstants;
import in.co.appinventor.services_api.app_util.AppUtility;
import in.co.appinventor.services_api.debug.DebugLog;
import in.co.appinventor.services_api.listener.ApiCallbackCode;
import in.co.appinventor.services_api.listener.OnMultiRecyclerItemClickListener;
import in.co.appinventor.services_api.settings.AppSettings;
import in.co.appinventor.services_api.widget.UIToastMessage;
import in.gov.pocra.training.R;
import in.gov.pocra.training.activity.ca.dashboard.DashboardCaActivity;
import in.gov.pocra.training.activity.common.additionalCharge.adapter.RoleListAdapter;
import in.gov.pocra.training.activity.common.login.LoginActivity;
import in.gov.pocra.training.activity.coordinator.dashboard.DashboardCoordActivity;
import in.gov.pocra.training.activity.pmu.dashboard.DashboardPmuActivity;
import in.gov.pocra.training.activity.ps_hrd.dashboard.DashboardPsHrdActivity;
import in.gov.pocra.training.model.online.ProfileModel;
import in.gov.pocra.training.model.online.ResponseModel;
import in.gov.pocra.training.util.ApConstants;
import in.gov.pocra.training.web_services.APIRequest;
import in.gov.pocra.training.web_services.APIServices;
import in.gov.pocra.training.web_services.ForceUpdateChecker;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Retrofit;

public class SelectoRoleActivity extends AppCompatActivity implements ApiCallbackCode,  OnMultiRecyclerItemClickListener {

    RecyclerView rolelist;
    private JSONArray mDataArray;
    String role_id;
    String userID;
    String destn = null,level = null,dist=null,dist_Id=null;

    String user_id;
    String user_token;
    String userLevel;
    JSONObject login_Data;

    JSONArray array2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selecto_role);
        rolelist = findViewById(R.id.rolelist);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        rolelist.setLayoutManager(layoutManager);
        rolelist.setHasFixedSize(true);
        rolelist.setItemAnimator(new DefaultItemAnimator());

        String loginData = AppSettings.getInstance().getValue(this, ApConstants.kLOGIN_DATA, ApConstants.kLOGIN_DATA);
        if (!loginData.equalsIgnoreCase("kLOGIN_DATA")) {
            try {
                JSONObject loginJSON = new JSONObject(loginData);
                mDataArray=loginJSON.getJSONArray("roles");
                Log.d("mDataArray", String.valueOf(mDataArray));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        String switchstr = AppSettings.getInstance().getValue(this, ApConstants.kSwitch, ApConstants.kSwitch);
        if(switchstr.equalsIgnoreCase("yes")){
            requestDataValidation();
        }else{
            RoleListAdapter adapter = new RoleListAdapter(this,  this, mDataArray);
            rolelist.setAdapter(adapter);
        }


    }
    private void requestDataValidation() {
        userID = AppSettings.getInstance().getValue(this, ApConstants.kUSER_ID, ApConstants.kUSER_ID);
        try {
            JSONObject jsonObject = new JSONObject();

            jsonObject.put("user_id", userID);

            RequestBody requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString());
            AppinventorApi api = new AppinventorApi(this, APIServices.SSO_API_URL, "", ApConstants.kMSG, true);
            Retrofit retrofit = api.getRetrofitInstance();
            APIRequest apiRequest = retrofit.create(APIRequest.class);
            // Call<JsonObject> responseCall = apiRequest.oauthTokenRequest(requestBody);
            Call<JsonObject> responseCall = apiRequest.getroledata(requestBody);

            DebugLog.getInstance().d("Login_param=" + responseCall.request().toString());
            DebugLog.getInstance().d("Login_param=" + AppUtility.getInstance().bodyToString(responseCall.request()));

            api.postRequest(responseCall, this, 1);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMultiRecyclerViewItemClick(int i, Object o) {
        JSONObject jsonObject = (JSONObject) o;
        Log.d("xjghjfdkh",jsonObject.toString());

        try {
            role_id= String.valueOf(jsonObject.getInt("role_id"));
            destn=jsonObject.getString("designation");
            level=jsonObject.getString("level");
            dist_Id=jsonObject.getString("district_id");
            // dist=jsonObject.getString("district_name");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("role_dist_level",role_id+" "+destn+" "+level);

        if (role_id.equalsIgnoreCase("15") || role_id.equalsIgnoreCase("14")/* || roll_id.equalsIgnoreCase("24" ) || roll_id.equalsIgnoreCase("27" )*/) {
            //15- PS-HRD    14- PS-AB
            AppSettings.getInstance().setValue(this, ApConstants.kROLE_ID, role_id);
            AppSettings.getInstance().setValue(this, ApConstants.kUSER_DIST_ID, dist_Id);
            AppSettings.getInstance().setValue(this, ApConstants.kLOGIN_TYPE, ApConstants.kPS_TYPE);
            AppSettings.getInstance().setValue(this, ApConstants.kUSER_LEVEL, level);
            AppSettings.getInstance().setValue(this, ApConstants.kDesignation, destn);
            AppSettings.getInstance().setValue(this, ApConstants.kSwitch, "yes");
//            startActivity(new Intent(SelectoRoleActivity.this, DashboardPsHrdActivity.class));
//            finish();
            Intent intent = new Intent(SelectoRoleActivity.this, DashboardPsHrdActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();

        } else if (level.equalsIgnoreCase("pmu")) {
            AppSettings.getInstance().setValue(this, ApConstants.kROLE_ID, role_id);
            AppSettings.getInstance().setValue(this, ApConstants.kLOGIN_TYPE, ApConstants.kPMU_TYPE);
            AppSettings.getInstance().setValue(this, ApConstants.kUSER_LEVEL, level);
            AppSettings.getInstance().setValue(this, ApConstants.kDesignation, destn);
            AppSettings.getInstance().setValue(this, ApConstants.kSwitch, "yes");
//            startActivity(new Intent(SelectoRoleActivity.this, DashboardPmuActivity.class));
//            finish();
            Intent intent = new Intent(SelectoRoleActivity.this, DashboardPmuActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        } else if (role_id.equalsIgnoreCase("8")) {// for CA
            AppSettings.getInstance().setValue(this, ApConstants.kROLE_ID,role_id);
            AppSettings.getInstance().setValue(this, ApConstants.kLOGIN_TYPE, ApConstants.kCA_TYPE);
            AppSettings.getInstance().setValue(this, ApConstants.kUSER_LEVEL, level);
            AppSettings.getInstance().setValue(this, ApConstants.kDesignation, destn);
            AppSettings.getInstance().setValue(this, ApConstants.kSwitch, "yes");
//            startActivity(new Intent(SelectoRoleActivity.this, DashboardCaActivity.class));
//            finish();
            Intent intent = new Intent(SelectoRoleActivity.this, DashboardCaActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        } else if (!level.equalsIgnoreCase("Global")) {
            AppSettings.getInstance().setValue(this, ApConstants.kROLE_ID, role_id);
            AppSettings.getInstance().setValue(this, ApConstants.kLOGIN_TYPE, ApConstants.kCOORD_TYPE);
            AppSettings.getInstance().setValue(this, ApConstants.kUSER_LEVEL, level);
            AppSettings.getInstance().setValue(this, ApConstants.kDesignation, destn);
            AppSettings.getInstance().setValue(this, ApConstants.kSwitch, "yes");
//            startActivity(new Intent(SelectoRoleActivity.this, DashboardCoordActivity.class));
//            finish();
            Intent intent = new Intent(SelectoRoleActivity.this, DashboardCoordActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onResponse(JSONObject jsonObject, int i) {
        try {

            if (jsonObject != null) {
                // District Response
                if (i == 1) {
                    ResponseModel responseModel = new ResponseModel(jsonObject);

                    if (responseModel.isStatus()) {
                        login_Data = jsonObject.getJSONObject("data");

                        array2= new JSONArray();

                        // = profileModel.getRolearray();
                        array2 = login_Data.getJSONArray("roles");
                        if (array2.length() > 1) {

                            RoleListAdapter adapter = new RoleListAdapter(this, this, array2);
                            rolelist.setAdapter(adapter);

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




}