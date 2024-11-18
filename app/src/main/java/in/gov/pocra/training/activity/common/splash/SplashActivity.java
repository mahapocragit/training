package in.gov.pocra.training.activity.common.splash;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.provider.Settings;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import in.co.appinventor.services_api.api.AppinventorApi;
import in.co.appinventor.services_api.api.AppinventorIncAPI;
import in.co.appinventor.services_api.app_util.AppUtility;
import in.co.appinventor.services_api.debug.DebugLog;
import in.co.appinventor.services_api.listener.ApiCallbackCode;
import in.co.appinventor.services_api.listener.ApiJSONObjCallback;
import in.co.appinventor.services_api.settings.AppSettings;
import in.co.appinventor.services_api.widget.UIToastMessage;
import in.gov.pocra.training.R;
import in.gov.pocra.training.activity.ca.dashboard.DashboardCaActivity;
import in.gov.pocra.training.activity.common.login.LoginActivity;
import in.gov.pocra.training.activity.coordinator.dashboard.DashboardCoordActivity;
import in.gov.pocra.training.activity.pmu.dashboard.DashboardPmuActivity;
import in.gov.pocra.training.activity.ps_hrd.dashboard.DashboardPsHrdActivity;
import in.gov.pocra.training.model.online.ResponseModel;
import in.gov.pocra.training.util.ApConstants;
import in.gov.pocra.training.web_services.APIRequest;
import in.gov.pocra.training.web_services.APIServices;
import in.gov.pocra.training.web_services.NotificationDisplayService;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Retrofit;


public class SplashActivity extends AppCompatActivity implements ApiJSONObjCallback,ApiCallbackCode {

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    String username, versionName,token,userID,machineId;
    int appID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_splash);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //token = NotificationDisplayService.getFCMToken();
        token = FirebaseInstanceId.getInstance().getToken();
       // Log.d("splashscreenToken", token);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                loginActivity();
            }
        },1500);

        TextView versionTView = (TextView)findViewById(R.id.versionTView);
        versionName  = AppUtility.getInstance().appVersionNumber(this);
        versionTView.setText("Version "+versionName);
        //versionTView.setText("Version "+appVersion+" Staging");


        getAppVersion();


    }

    private void getAppVersion() {
        String subTalukaUrl = APIServices.GET_APP_VERSION_URL;
        AppinventorIncAPI api = new AppinventorIncAPI(this, APIServices.SSO_API_URL, "", ApConstants.kMSG, false);
        api.getRequestData(subTalukaUrl, this, 1);

    }


    private void loginActivity() {
        AppSettings.getInstance().setValue(this,ApConstants.kDEVICE_TOKEN,token);
      String loginType = AppSettings.getInstance().getValue(this,ApConstants.kLOGIN_TYPE,ApConstants.kLOGIN_TYPE);

        /*  if (loginType.equalsIgnoreCase(ApConstants.kCOORD_TYPE)){
            showDashboard();
            startActivity(new Intent(SplashActivity.this, DashboardCoordActivity.class));
            finish();
        }else if (loginType.equalsIgnoreCase(ApConstants.kPS_TYPE)){
            showDashboard();
            startActivity(new Intent(SplashActivity.this, DashboardPsHrdActivity.class));
            finish();
        }else if (loginType.equalsIgnoreCase(ApConstants.kPMU_TYPE)){
            showDashboard();
            startActivity(new Intent(SplashActivity.this, DashboardPmuActivity.class));
            finish();
        }else if (loginType.equalsIgnoreCase(ApConstants.kCA_TYPE)){
            showDashboard();
            startActivity(new Intent(SplashActivity.this, DashboardCaActivity.class));
            finish();
        }else {
            startActivity(new Intent(SplashActivity.this, LoginActivity.class));
            finish();
        }*/
Log.d("dnfjdnfdfsf",ApConstants.kLOGIN_TYPE +loginType);
        if (loginType.equalsIgnoreCase(ApConstants.kLOGIN_TYPE)){
            startActivity(new Intent(SplashActivity.this, LoginActivity.class));
            finish();
        }else
        {
            showDashboard();
        }



    }

    private void showDashboard() {
        userID = AppSettings.getInstance().getValue(this, ApConstants.kUSER_ID, ApConstants.kUSER_ID);
        username = AppSettings.getInstance().getValue(this, ApConstants.kUSER_NAME, ApConstants.kUSER_NAME);
        appID = 6;
        machineId =  getMachineId();
        Log.d("appVersionLoggedDetails"," username=" + username+"  app_id=" + appID +  "  versionName="+ versionName +" token="+ token +" user_id="+userID + " device_ID="+machineId);
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("username", username);
            jsonObject.put("app_id", appID);
            jsonObject.put("version_number", versionName);
            jsonObject.put("fcm_token", token);
            jsonObject.put("user_id", userID);
            jsonObject.put("device_id", machineId);
            RequestBody requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString());
            AppinventorApi api = new AppinventorApi(this, APIServices.SSO_API_URL, "", ApConstants.kMSG, true);
            Retrofit retrofit = api.getRetrofitInstance();
            APIRequest apiRequest = retrofit.create(APIRequest.class);
            Call<JsonObject> responseCall = apiRequest.checkActivateDeactivateUser(requestBody);

            DebugLog.getInstance().d("Login_param=" + responseCall.request().toString());
            DebugLog.getInstance().d("Login_param=" + AppUtility.getInstance().bodyToString(responseCall.request()));
            api.postRequest(responseCall, this, 2);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public String getMachineId() {

        String android_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        return android_id;

    }

    @Override
    public void onResponse(JSONObject jsonObject, int i) {
        Log.d("ResponseData", String.valueOf(jsonObject));

        if (jsonObject != null) {

            try {
                if (i == 1) {

                    if (jsonObject.getString("status").equalsIgnoreCase("200")) {
                        ResponseModel responseModel = new ResponseModel(jsonObject);
                        if (responseModel.isStatus()) {
                            JSONObject versionData = jsonObject.getJSONObject("data");
                            String buildVersion = versionData.getString("build_version");
                            AppSettings.getInstance().setValue(this,ApConstants.kAPP_BUILD_VERSION,buildVersion);
                        }
                    }
                }
                if (i == 2) {
                    Log.d("ResponseData111", String.valueOf(jsonObject));
                    if (jsonObject.getString("status").equalsIgnoreCase("200")) {
                        ResponseModel responseModel = new ResponseModel(jsonObject);
                        if (responseModel.isStatus()) {

                            String loginType = AppSettings.getInstance().getValue(this,ApConstants.kLOGIN_TYPE,ApConstants.kLOGIN_TYPE);


                            if (loginType.equalsIgnoreCase(ApConstants.kCOORD_TYPE)){
                              //  showDashboard();
                                startActivity(new Intent(SplashActivity.this, DashboardCoordActivity.class));
                                finish();
                            }else if (loginType.equalsIgnoreCase(ApConstants.kPS_TYPE)){
                               // showDashboard();
                                startActivity(new Intent(SplashActivity.this, DashboardPsHrdActivity.class));
                                finish();
                            }else if (loginType.equalsIgnoreCase(ApConstants.kPMU_TYPE)){
                              //  showDashboard();
                                startActivity(new Intent(SplashActivity.this, DashboardPmuActivity.class));
                                finish();
                            }else if (loginType.equalsIgnoreCase(ApConstants.kCA_TYPE)){
                              //  showDashboard();
                                startActivity(new Intent(SplashActivity.this, DashboardCaActivity.class));
                                finish();
                            }else {
                                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                                finish();
                            }

                        }else{
                            UIToastMessage.show(this,"Users deactive");
                        }
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }

    @Override
    public void onFailure(Object o, Throwable throwable, int i) {

    }

    @Override
    public void onFailure(Throwable throwable, int i) {

    }


}
