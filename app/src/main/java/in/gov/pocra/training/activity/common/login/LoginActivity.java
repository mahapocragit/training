package in.gov.pocra.training.activity.common.login;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.provider.Settings;
import android.util.Log;
import android.util.TypedValue;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;

import in.co.appinventor.services_api.api.AppinventorApi;
import in.co.appinventor.services_api.api.AppinventorIncAPI;
import in.co.appinventor.services_api.app_util.AppUtility;
import in.co.appinventor.services_api.debug.DebugLog;
import in.co.appinventor.services_api.listener.ApiCallbackCode;
import in.co.appinventor.services_api.settings.AppSettings;
import in.co.appinventor.services_api.widget.UIToastMessage;
import in.gov.pocra.training.R;
import in.gov.pocra.training.activity.ca.dashboard.DashboardCaActivity;
import in.gov.pocra.training.activity.common.additionalCharge.activity.SelectoRoleActivity;
import in.gov.pocra.training.activity.common.forgot_password.ForgotPasswordActivity;
import in.gov.pocra.training.activity.coordinator.dashboard.DashboardCoordActivity;
import in.gov.pocra.training.activity.pmu.dashboard.DashboardPmuActivity;
import in.gov.pocra.training.activity.ps_hrd.dashboard.DashboardPsHrdActivity;
import in.gov.pocra.training.model.online.ResponseModel;
import in.gov.pocra.training.util.ApConstants;
import in.gov.pocra.training.web_services.APIRequest;
import in.gov.pocra.training.web_services.APIServices;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Retrofit;


public class LoginActivity extends AppCompatActivity implements ApiCallbackCode {

    private EditText loginEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private TextView forgotPassTextView;
    String Sub_division_name,sub_division_id;
    String username, versionName,token,machineId,userID,strAddress;
    int appID,versionCode,sdkVersion;

    double latitude, longitude;
    String publicIP = null;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private FusedLocationProviderClient fusedLocationClient;
    private String strToken = null;
    String roll_id;
    String user_id;
    String user_token;
    String userLevel,destn;
    JSONObject login_Data;
    JSONArray array2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

//        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
//        getSupportActionBar().setCustomView(R.layout.custom_actionbar_layout);
//        AppCompatTextView actionTitleTextView = (AppCompatTextView) getSupportActionBar().getCustomView().findViewById(R.id.actionTitleTextView);
//        actionTitleTextView.setText(getResources().getString(R.string.title_login));

        initialization();
        defaultConfiguration();

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            getLocation();
        }
    }
    private void initialization() {
        loginEditText = findViewById(R.id.loginEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        forgotPassTextView = findViewById(R.id.forgotPassTextView);
    }
    private void defaultConfiguration() {

        token = FirebaseInstanceId.getInstance().getToken();
      //  Log.d("LoginScreenToken", token);
        versionName  = AppUtility.getInstance().appVersionNumber(this);

        loginButton.setOnClickListener(v -> loginButtonAction());
        forgotPassTextView.setOnClickListener(v -> forgotPassAction());
        loginEditText.setCustomSelectionActionModeCallback(new ActionMode.Callback() {

            public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                return false;
            }

            public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                return false;
            }

            public boolean onActionItemClicked(ActionMode actionMode, MenuItem item) {
                return false;
            }

            public void onDestroyActionMode(ActionMode actionMode) {
            }
        });
        loginEditText.setLongClickable(false);
        passwordEditText.setTextIsSelectable(false);
    }

    private void forgotPassAction() {
        startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
        // Toast.makeText(this,"Forgot text clicked", Toast.LENGTH_SHORT).show();
    }

    private void fetchOauthRefreshToken(String mob,String pass) {
        try {
//            String mob = mobileEditText.getText().toString();
//            String pass = passEditText.getText().toString();
            JSONObject jsonObject = new JSONObject();
            // Log.d("Mayu111","croppingSystemId=="+croppingSystem);
            jsonObject.put("username", mob.trim());
            jsonObject.put("pass", pass.trim());
            jsonObject.put("secret", "G2pyf4yqR5uhzLHDyG");
            RequestBody requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString());

         //   AppinventorIncAPI api = new AppinventorIncAPI(this, APIServices.SSO, "", new AppString(this).getkMSG_WAIT(), true);
            AppinventorApi api = new AppinventorApi(this, APIServices.API_URL, "", ApConstants.kMSG, true);

            Retrofit retrofit = api.getRetrofitInstance();
            APIRequest apiRequest = retrofit.create(APIRequest.class);
            Call<JsonObject> responseCall = apiRequest.refreshTokenRequest(requestBody);

            DebugLog.getInstance().d("param=" + responseCall.request());
            DebugLog.getInstance().d("param=" + AppUtility.getInstance().bodyToString(responseCall.request()));

            api.postRequest(responseCall, this, 4);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void loginButtonAction() {

        String mobile = loginEditText.getText().toString().trim();
        String pass = passwordEditText.getText().toString().trim();
        token = FirebaseInstanceId.getInstance().getToken();

        if (mobile.isEmpty()) {
            loginEditText.setError("Enter registered mobile number");
            loginEditText.requestFocus();
        } else if (!AppUtility.getInstance().isValidCallingNumber(mobile)) {
            loginEditText.setError("Enter valid mobile number");
            loginEditText.requestFocus();
        } else if (mobile.length() != 10) {
        //else if (mobile.length()<9 ) {
            loginEditText.setError("Enter valid mobile number");
            loginEditText.requestFocus();
        } else if (pass.isEmpty()) {
            loginEditText.setError(null);
            passwordEditText.requestFocus();
            passwordEditText.setError("Enter password");
        } else {
            passwordEditText.setError(null);
            fetchOauthRefreshToken(mobile,pass);
//            JSONObject jsonObject = new JSONObject();
//            try {
//                /*jsonObject.put("mob", mobile);
//                jsonObject.put("pass", pass);*/
//
//                // For sso Outh
//                jsonObject.put("username", mobile);
//                jsonObject.put("pass", pass);
//                jsonObject.put("secret", "sfIsJ1xRGaRdeptbXw");
//
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//            RequestBody requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString());
//            AppinventorApi api = new AppinventorApi(this, APIServices.API_URL, "", ApConstants.kMSG, true);
//            Retrofit retrofit = api.getRetrofitInstance();
//            APIRequest apiRequest = retrofit.create(APIRequest.class);
//            // Call<JsonObject> responseCall = apiRequest.oauthTokenRequest(requestBody);
//            Call<JsonObject> responseCall = apiRequest.ssoAUTHRequest(requestBody);
//            DebugLog.getInstance().d("Login_param=" + responseCall.request().toString());
//            DebugLog.getInstance().d("Login_param=" + AppUtility.getInstance().bodyToString(responseCall.request()));
//            api.postRequest(responseCall, this, 1);
        }
    }
    private void fetchLogin(String strRefreshToken)
    {
        String mob = loginEditText.getText().toString();
        String pass = passwordEditText.getText().toString();
        Log.d("MAYU111","updated token=="+strToken);

        String deviceName = Build.MODEL;
        String deviceName2 = Build.BRAND;
        // String deviceName3 = Build.MANUFACTURER;
        // String deviceName4 = Build.DEVICE;
        publicIP = getLocalIPv4Address();
        versionCode = getVersionCode(this);
        getAddressFromLocation(this, latitude, longitude);
        sdkVersion = Build.VERSION.SDK_INT;
        strAddress = getAddressFromLocation(this, latitude, longitude);
        Log.d("MAYU111","MODEL===="+deviceName);
        Log.d("MAYU111","BRAND===="+deviceName2);
        Log.d("MAYU111","sdkVersion===="+sdkVersion);
        Log.d("MAYU111","VERSION_CODES===="+versionCode);
        Log.d("MAYU111","strAddress===="+strAddress);

        JSONObject jsonObject = new JSONObject();
        try {

            JSONArray user_agent_JsonArray = new JSONArray();

            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put("model",deviceName );
            jsonObject1.put("sdk",sdkVersion );
            jsonObject1.put("brand", deviceName2);
            jsonObject1.put("version_code",versionCode );
            user_agent_JsonArray.put(jsonObject1);

            jsonObject.put("username", mob.trim());
            jsonObject.put("pass", pass.trim());
            jsonObject.put("secret", "G2pyf4yqR5uhzLHDyG");
            jsonObject.put("ip_address", publicIP);
            jsonObject.put("device", "mobile");
            jsonObject.put("user_agent", user_agent_JsonArray);
            jsonObject.put("latitude", latitude);
            jsonObject.put("longitude", longitude);
            jsonObject.put("address", strAddress);
            Log.d("MAYU111","publicIP===="+publicIP);
            Log.d("Mayu111","latitude===="+latitude);
            Log.d("Mayu111","longitude===="+longitude);
            jsonObject.put("refresh_token", strRefreshToken);

           // jsonObject.put("username", mob.trim());
          //  jsonObject.put("pass", pass.trim());
          //  jsonObject.put("secret", "sfIsJ1xRGaRdeptbXw");
          //  jsonObject.put("refresh_token", strRefreshToken);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString());

//            RuntimeAPI api = new RuntimeAPI(this, APIServices.BASE_API, "", AppConstants.kMSG_WAIT, true);
    //    AppinventorIncAPI api = new AppinventorIncAPI(this, APIServices.SSO, "", new AppString(this).getkMSG_WAIT(), true);
        AppinventorApi api = new AppinventorApi(this, APIServices.API_URL, "", ApConstants.kMSG, true);
        Retrofit retrofit = api.getRetrofitInstance();
        APIRequest apiRequest = retrofit.create(APIRequest.class);
        Call<JsonObject> responseCall = apiRequest.oauthTokenRequest(requestBody);
        DebugLog.getInstance().d("param=" + responseCall.request());
        DebugLog.getInstance().d("param=" + AppUtility.getInstance().bodyToString(responseCall.request()));
        api.postRequest(responseCall, this, 1);
        passwordEditText.setText("");
    }

    public int getActionBarHeight() {
        int actionBarHeight = getSupportActionBar().getHeight();
        TypedValue tv = new TypedValue();
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
        }
        return actionBarHeight;
    }
    private void saveTokenNcheckActiveDeactive(String userId,String UserName) {
        username = UserName;
        userID =userId;
        appID = 6;
        machineId =  getMachineId();
        token = FirebaseInstanceId.getInstance().getToken();
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
            AppinventorApi api = new AppinventorApi(this, APIServices.SSO_API_URL, "", ApConstants.kMSG, false);
            Retrofit retrofit = api.getRetrofitInstance();
            APIRequest apiRequest = retrofit.create(APIRequest.class);
            Call<JsonObject> responseCall = apiRequest.checkActivateDeactivateUser(requestBody);

            DebugLog.getInstance().d("Login_param=" + responseCall.request());
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
        try {
            if (jsonObject != null) {
                // District Response
                if (i == 1) {
                    ResponseModel responseModel = new ResponseModel(jsonObject);

                    if (responseModel.isStatus()) {
                         login_Data = jsonObject.getJSONObject("data");

                         roll_id = login_Data.getString("role_id");
                         user_id = login_Data.getString("id");
                         user_token = jsonObject.getString("token");
                         userLevel = login_Data.getString("level");
                         username = login_Data.getString("username");
                        destn=login_Data.getString("designation");

                        array2= new JSONArray();

                        // = profileModel.getRolearray();
                        try {
                            array2 = login_Data.getJSONArray("roles");

                        }catch(Exception e){

                        }
                        saveTokenNcheckActiveDeactive(user_id,username);

                    } else {
//                        UIToastMessage.show(this, responseModel.getMsg());
                        Toast.makeText(this, responseModel.getMsg(), Toast.LENGTH_SHORT).show();
                    }
                }
                if (i == 4) {
                    if (jsonObject != null) {
                        DebugLog.getInstance().d("onResponse=" + jsonObject);
                        ResponseModel response1 = new ResponseModel(jsonObject);

                        if (response1.isStatus()) {
                            strToken=response1.getRefreshToken();
                            Log.d("MAYU111","RefreshToken==="+strToken);
                            fetchLogin(strToken);
                        } else {
                            Toast.makeText(this, response1.getResponse(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                if (i == 2) {

                    if (jsonObject.getString("status").equalsIgnoreCase("200")) {
                        ResponseModel responseModel = new ResponseModel(jsonObject);
                        if (responseModel.isStatus()) {

                            if(array2.length() <= 1) {
                                AppSettings.getInstance().setValue(this, ApConstants.kROLE_ID, roll_id);
                                AppSettings.getInstance().setValue(this, ApConstants.kUSER_ID, user_id);
                                AppSettings.getInstance().setValue(this, ApConstants.kUSER_NAME, username);
                                AppSettings.getInstance().setValue(this, ApConstants.kUSER_LEVEL, userLevel);
                                AppSettings.getInstance().setValue(this, ApConstants.kUSER_TOKEN, user_token);
                                AppSettings.getInstance().setValue(this, ApConstants.kDesignation, destn);
                                AppSettings.getInstance().setValue(this, ApConstants.kLOGIN_DATA, login_Data.toString());

                                if (roll_id.equalsIgnoreCase("15") || roll_id.equalsIgnoreCase("14") || roll_id.equalsIgnoreCase("5" ) || roll_id.equalsIgnoreCase("4" )) {
                                    //15- PS-HRD    14- PS-AB
                                    Log.d("LoginMMM","1 DashboardPsHrdActivity");
                                    String ps_dist_id = login_Data.getString("district_id");
                                    AppSettings.getInstance().setValue(this, ApConstants.kUSER_DIST_ID, ps_dist_id);
                                    AppSettings.getInstance().setValue(this, ApConstants.kLOGIN_TYPE, ApConstants.kPS_TYPE);
                                    AppSettings.getInstance().setValue(this, ApConstants.kSwitch, "no");
                                    startActivity(new Intent(LoginActivity.this, DashboardPsHrdActivity.class));
                                    finish();

                                } else if (userLevel.equalsIgnoreCase("pmu")) {
                                    Log.d("LoginMMM","2 PMU");
                                    AppSettings.getInstance().setValue(this, ApConstants.kLOGIN_TYPE, ApConstants.kPMU_TYPE);
                                    AppSettings.getInstance().setValue(this, ApConstants.kSwitch, "no");
                                    startActivity(new Intent(LoginActivity.this, DashboardPmuActivity.class));
                                    finish();
                                } else if (roll_id.equalsIgnoreCase("8")||roll_id.equalsIgnoreCase("6")||roll_id.equalsIgnoreCase("34")||roll_id.equalsIgnoreCase("9")||roll_id.equalsIgnoreCase("30")) {// for CA,SDAO,TAO,AA,TC
                                    Log.d("LoginMMM","3 CA,SDAO,TAO");
                                    AppSettings.getInstance().setValue(this, ApConstants.kLOGIN_TYPE, ApConstants.kCA_TYPE);
                                    AppSettings.getInstance().setValue(this, ApConstants.kSwitch, "no");
                                    startActivity(new Intent(LoginActivity.this, DashboardCaActivity.class));
                                    finish();
                                }else if(roll_id.equalsIgnoreCase("26")) {// Add new for Krushi Tai
                                    Log.d("LoginMMM","6 Krushi Tai");
                                    AppSettings.getInstance().setValue(this, ApConstants.kLOGIN_TYPE, ApConstants.kCOORD_TYPE);
                                    AppSettings.getInstance().setValue(this, ApConstants.kSwitch, "no");
                                    startActivity(new Intent(LoginActivity.this, DashboardCoordActivity.class));
                                    finish();
                                } else if (!userLevel.equalsIgnoreCase("Global")) {
                                    Log.d("LoginMMM","4 Global");
                                    AppSettings.getInstance().setValue(this, ApConstants.kLOGIN_TYPE, ApConstants.kCOORD_TYPE);
                                    AppSettings.getInstance().setValue(this, ApConstants.kSwitch, "no");
                                    startActivity(new Intent(LoginActivity.this, DashboardCoordActivity.class));
                                    finish();
                                } else {
//                                    UIToastMessage.show(this, "You are not authorised for this application/ Contact admin");
                                    Toast.makeText(this, "You are not authorised for this application/ Contact admin", Toast.LENGTH_SHORT).show();
                                }

                        /*if (isCoordinator(roll_id)){
                            AppSettings.getInstance().setValue(this, ApConstants.kLOGIN_TYPE, ApConstants.kCOORD_TYPE);
                            startActivity(new Intent(LoginActivity.this, DashboardCoordActivity.class));
                            finish();
                        }else {
                            UIToastMessage.show(this, "You are not authorised for this application/ Contact your admin");
                        }*/

                            }else{
                               // AppSettings.getInstance().setValue(this, ApConstants.kROLE_ID, roll_id);
                                Log.d("LoginMMM","5 else");
                                AppSettings.getInstance().setValue(this, ApConstants.kUSER_ID, user_id);
                                AppSettings.getInstance().setValue(this, ApConstants.kUSER_NAME, username);
                                //AppSettings.getInstance().setValue(this, ApConstants.kUSER_LEVEL, userLevel);
                                AppSettings.getInstance().setValue(this, ApConstants.kUSER_TOKEN, user_token);
                                AppSettings.getInstance().setValue(this, ApConstants.kLOGIN_DATA, login_Data.toString());

                                Intent ii = new Intent(LoginActivity.this, SelectoRoleActivity.class);
                                startActivity(ii);
                            }
                        }else{
//                            UIToastMessage.show(this,"Users deactive");
                            Toast.makeText(this, "Users deactive", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isCoordinator(String role_id) {
        boolean result = false;
        switch (role_id) {
            case "8":
                result = true;
                break;

            case "9":
                result = true;
                break;

            case "12":
                result = true;
                break;

            case "14":
                result = true;
                break;

            case "27":
                result = true;
                break;
        }
        return result;
    }

    @Override
    public void onFailure(Object o, Throwable throwable, int i) {

    }
    @SuppressLint("MissingPermission")
    private void getLocation() {
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                        // Use latitude and longitude
                        Toast.makeText(LoginActivity.this,
                                "Latitude: " + latitude + ", Longitude: " + longitude,
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocation();
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public static String getLocalIPv4Address() {
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();
                Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
                while (inetAddresses.hasMoreElements()) {
                    InetAddress inetAddress = inetAddresses.nextElement();
                    // Check if it's not an IPv6 address and is not loopback address
                    if (!inetAddress.isLoopbackAddress() && inetAddress.getHostAddress().contains(".")) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    private int getVersionCode(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            // Return -1 or handle the exception accordingly
            return -1;
        }
    }
    public static String getAddressFromLocation(Context context, double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && addresses.size() > 0) {
                Address address = addresses.get(0);
                StringBuilder addressStringBuilder = new StringBuilder();
                for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                    addressStringBuilder.append(address.getAddressLine(i)).append("\n");
                }
                return addressStringBuilder.toString().trim();
            } else {
                return "Address not found";
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }
}
