package in.gov.pocra.training.activity.ps_hrd.dashboard;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.gson.JsonObject;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import in.co.appinventor.services_api.api.AppinventorApi;
import in.co.appinventor.services_api.app_util.AppUtility;
import in.co.appinventor.services_api.debug.DebugLog;
import in.co.appinventor.services_api.listener.AlertListCallbackEventListener;
import in.co.appinventor.services_api.listener.ApiCallbackCode;
import in.co.appinventor.services_api.settings.AppSettings;
import in.co.appinventor.services_api.widget.UIToastMessage;
import in.gov.pocra.training.BuildConfig;
import in.gov.pocra.training.R;
import in.gov.pocra.training.activity.common.additionalCharge.activity.SelectoRoleActivity;
import in.gov.pocra.training.activity.common.coming_closed_dist_list.ComingClosedLocationListActivity;
import in.gov.pocra.training.activity.common.notification.NotificationListActivity;
import in.gov.pocra.training.activity.common.profile.MyProfileActivity;
import in.gov.pocra.training.activity.common.splash.SplashActivity;
import in.gov.pocra.training.activity.coordinator.dashboard.DashboardCoordActivity;
import in.gov.pocra.training.activity.coordinator.event_list.CoEventListActivity;
import in.gov.pocra.training.activity.pmu.dashboard.DashboardPmuActivity;
import in.gov.pocra.training.activity.ps_hrd.add_edit_event_ps.AddEditEventPsHrdActivity;
import in.gov.pocra.training.activity.ps_hrd.ps_report.PsReportActivity;
import in.gov.pocra.training.activity.ps_hrd.ps_upcoming_event.PsComingEventActivity;
import in.gov.pocra.training.model.online.ProfileModel;
import in.gov.pocra.training.model.online.ResponseModel;
import in.gov.pocra.training.util.ApConstants;
import in.gov.pocra.training.util.ApUtil;
import in.gov.pocra.training.util.AppHelper;
import in.gov.pocra.training.web_services.APIRequest;
import in.gov.pocra.training.web_services.APIServices;
import in.gov.pocra.training.web_services.ForceUpdateChecker;
import in.gov.pocra.training.web_services.NotificationDisplayService;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Retrofit;

import static in.gov.pocra.training.util.ApUtil.showForceUpdateDialog;

public class DashboardPsHrdActivity extends AppCompatActivity implements AlertListCallbackEventListener, ApiCallbackCode, ForceUpdateChecker.OnUpdateNeededListener  {

    private String token = "";
    private String userID;
    private String roleId;
    private String districtId;

    // For PS HRD
    private ImageView ps_logout;
    private TextView nameTView;
    private TextView desigTView;
    private TextView distTView;
    private ImageView profileIView;
    private LinearLayout addEventLLayout;
    private LinearLayout comingEventLLayout;
    private TextView comingEventTView;
    private LinearLayout assignedEventLLayout;
    private LinearLayout ps_calLLayout;
    private TextView ps_calTView;
    private LinearLayout reportLLayout;
    private TextView reportTView;
    private String loginType;
    private Transformation transformation;
    private ImageView ps_calIView;
    private String comingEventTypeId = "";
    private FirebaseAnalytics mFirebaseAnalytics;
    private static final int TIME_INTERVAL = 2000; // # milliseconds, desired time passed between two back presses.
    private long mBackPressed;
    private TextView version_textView;

    private ImageView ivNotificationCount;
    private TextView tv_notification_count;

    int notifiCountValue;

    private TextView switchtxt;
    String  switchstr;
    JSONObject login_Data;
    JSONArray array2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        ForceUpdateChecker.with(this).onUpdateNeeded(this).check();

        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "id");
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "name");
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "image");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

        // setContentView(R.layout.activity_dashboard_ps_hrd);
         setContentView(R.layout.content_dashboard_ps);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        String type = AppSettings.getInstance().getValue(this, ApConstants.kLOGIN_TYPE, ApConstants.kLOGIN_TYPE);
        if (!type.equalsIgnoreCase("kLOGIN_TYPE")) {
            loginType = type;
        }

        initialization();
        defaultConfiguration();
    }


    @Override
    public void onBackPressed()
    {
        if (mBackPressed + TIME_INTERVAL > System.currentTimeMillis())
        {
            super.onBackPressed();
            return;
        }
        else {
            Toast.makeText(getBaseContext(), "Tap back button in order to exit", Toast.LENGTH_SHORT).show(); }

        mBackPressed = System.currentTimeMillis();
    }


    private void initialization() {

        // For PS HRD
        nameTView = (TextView)findViewById(R.id.nameTView);
        desigTView = (TextView)findViewById(R.id.desigTView);
        distTView = (TextView)findViewById(R.id.distTView);
        profileIView = (ImageView)findViewById(R.id.profileIView);
        addEventLLayout = (LinearLayout) findViewById(R.id.addEventLLayout);
        comingEventLLayout = (LinearLayout) findViewById(R.id.comingEventLLayout);
        comingEventTView = (TextView)findViewById(R.id.comingEventTView);
        assignedEventLLayout = (LinearLayout)findViewById(R.id.assignedEventLLayout);
        ps_calLLayout = (LinearLayout) findViewById(R.id.ps_calLLayout);
        ps_calTView = (TextView)findViewById(R.id.ps_calTView);
        ps_calIView = (ImageView)findViewById(R.id.ps_calIView);
        reportLLayout = (LinearLayout) findViewById(R.id.reportLLayout);
        reportTView = (TextView)findViewById(R.id.reportTView);
        ps_logout = (ImageView)findViewById(R.id.ps_logout);
        version_textView = (TextView) findViewById(R.id.version_textView);

        ivNotificationCount = findViewById(R.id.iv_notification_image);
        tv_notification_count = findViewById(R.id.tv_notification_count);

        switchtxt= findViewById(R.id.switchtxt);

        switchtxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ii = new Intent(DashboardPsHrdActivity.this, SelectoRoleActivity.class);
                startActivity(ii);
                // requestDataValidation();
            }
        });
        switchstr =AppSettings.getInstance().getValue(this, ApConstants.kSwitch, ApConstants.kSwitch);
        if(switchstr.equalsIgnoreCase("yes")){

            switchtxt.setVisibility(View.VISIBLE);
        }else{
            switchtxt.setVisibility(View.GONE);
        }
        if(APIServices.BASE_URL.contains("https://ilab")) {
            String ver = "V " + BuildConfig.VERSION_NAME + " S";
            version_textView.setText(ver);
        }
        else{
            String ver = "V " + BuildConfig.VERSION_NAME;
            version_textView.setText(ver);
        }

        String uId = AppSettings.getInstance().getValue(this, ApConstants.kUSER_ID, ApConstants.kUSER_ID);
        if (!uId.equalsIgnoreCase("kUSER_ID")) {
            userID = uId;
        }

        // To get User Id and Roll Id
        String rId = AppSettings.getInstance().getValue(this, ApConstants.kROLE_ID, ApConstants.kROLE_ID);
        if (!rId.equalsIgnoreCase("kROLE_ID")) {
            roleId = rId;
        }

        String distID = AppSettings.getInstance().getValue(this, ApConstants.kUSER_DIST_ID, ApConstants.kUSER_DIST_ID);
        if (!distID.equalsIgnoreCase("kUSER_DIST_ID")) {
            districtId = distID;
        }

        String dToken = AppSettings.getInstance().getValue(this,ApConstants.kDEVICE_TOKEN,ApConstants.kDEVICE_TOKEN);
        if (!dToken.equalsIgnoreCase("") && !dToken.equalsIgnoreCase("kDEVICE_TOKEN") ){
            token = dToken;
        }else {
            token = NotificationDisplayService.getFCMToken();
        }
        // UIToastMessage.show(this,token);

        checkForAppUpdate();
    }



    private void defaultConfiguration() {

        ps_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                askUserPermission();
            }
        });

        // For Ps
        /* ps_calIView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashboardPsHrdActivity.this, PsEventCaleActivity.class);
                startActivity(intent);
            }
         });*/


        profileIView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {  // Commented for testing purpose
                Intent intent = new Intent(DashboardPsHrdActivity.this, MyProfileActivity.class);
                startActivity(intent);
            }
        });


        addEventLLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashboardPsHrdActivity.this, AddEditEventPsHrdActivity.class);
                intent.putExtra("type", "create");
                startActivity(intent);
            }
        });

        comingEventLLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONArray eventOptArray = AppHelper.getInstance().getComingEventOptArray();
                ApUtil.showCustomListPicker(comingEventTView, eventOptArray, "Select option", "name", "id", DashboardPsHrdActivity.this, DashboardPsHrdActivity.this);
            }
        });

        ps_calLLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONArray eventOptArray = AppHelper.getInstance().getComingEventCalOptArray();
                ApUtil.showCustomListPicker(ps_calTView, eventOptArray, "Select option", "name", "id", DashboardPsHrdActivity.this, DashboardPsHrdActivity.this);
            }
        });

        reportLLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONArray eventOptArray = AppHelper.getInstance().geEventReportOptArray();
                ApUtil.showCustomListPicker(reportTView, eventOptArray, "Select option", "name", "id", DashboardPsHrdActivity.this, DashboardPsHrdActivity.this);
            }
        });

        assignedEventLLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DashboardPsHrdActivity.this, CoEventListActivity.class));
            }
        });

        updateToken();

        getUnreadNotificationCount();
        requestDataValidation();
        ivNotificationCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DashboardPsHrdActivity.this, NotificationListActivity.class);
                startActivity(intent);
            }
        });

    }

    private void updateToken() {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("user_id", userID);
            jsonObject.put("role_id", roleId);
            jsonObject.put("token", token);
            jsonObject.put("app_id", "6");

            RequestBody requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString());
            AppinventorApi api = new AppinventorApi(this, APIServices.API_URL, "", ApConstants.kMSG, true);
            Retrofit retrofit = api.getRetrofitInstance();
            APIRequest apiRequest = retrofit.create(APIRequest.class);
            Call<JsonObject> responseCall = apiRequest.updateUserTokenRequest(requestBody);

            DebugLog.getInstance().d("event_dates_param=" + responseCall.request().toString());
            DebugLog.getInstance().d("event_dates_param=" + AppUtility.getInstance().bodyToString(responseCall.request()));
            api.postRequest(responseCall, this, 1);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setProfileDetail();
        getUnreadNotificationCount();
        requestDataValidation();
    }

    private void checkForAppUpdate() {
        String appBuildVersion = AppSettings.getInstance().getValue(this,ApConstants.kAPP_BUILD_VERSION,ApConstants.kAPP_BUILD_VERSION);
        int remoteAppVersion = 0;
        if (!appBuildVersion.equalsIgnoreCase("kAPP_BUILD_VERSION")){
            remoteAppVersion = Integer.valueOf(appBuildVersion);
        }
        int currentAppVersion = ApUtil.getAppVersionNumber(this);
        if (currentAppVersion<remoteAppVersion){
            showForceUpdateDialog(this);
        }
    }


    private void getUnreadNotificationCount() {
        userID = AppSettings.getInstance().getValue(this, ApConstants.kUSER_ID, ApConstants.kUSER_ID);
        try {
            JSONObject jsonObject = new JSONObject();

            jsonObject.put("user_id", userID);
            jsonObject.put("app_id", "6");

            RequestBody requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString());
            AppinventorApi api = new AppinventorApi(this, APIServices.API_URL, "", ApConstants.kMSG, true);
            Retrofit retrofit = api.getRetrofitInstance();
            APIRequest apiRequest = retrofit.create(APIRequest.class);
            Call<JsonObject> responseCall = apiRequest.getFirebaseUnreadNotificationCount(requestBody);

            DebugLog.getInstance().d("event_dates_param=" + responseCall.request().toString());
            DebugLog.getInstance().d("event_dates_param=" + AppUtility.getInstance().bodyToString(responseCall.request()));
            api.postRequest(responseCall, this, 3);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void requestDataValidation() {
        userID = AppSettings.getInstance().getValue(this, ApConstants.kUSER_ID, ApConstants.kUSER_ID);
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("user_id", userID);


            RequestBody requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString());
            AppinventorApi api = new AppinventorApi(this, APIServices.API_URL, "", ApConstants.kMSG, false);
            Retrofit retrofit = api.getRetrofitInstance();
            APIRequest apiRequest = retrofit.create(APIRequest.class);
            Call<JsonObject> responseCall = apiRequest.getroledata(requestBody);

            DebugLog.getInstance().d("event_dates_param=" + responseCall.request().toString());
            DebugLog.getInstance().d("event_dates_param=" + AppUtility.getInstance().bodyToString(responseCall.request()));
            api.postRequest(responseCall, this, 4);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void setProfileDetail() {

        String userData = AppSettings.getInstance().getValue(this, ApConstants.kLOGIN_DATA, ApConstants.kLOGIN_DATA);
        String desgtn = AppSettings.getInstance().getValue(this, ApConstants.kDesignation, ApConstants.kDesignation);
        if (!userData.equalsIgnoreCase("kLOGIN_DATA")) {
            try {
                JSONObject userJson = new JSONObject(userData);
                ProfileModel pModel = new ProfileModel(userJson);
                String lName = pModel.getLast_name();
                String fName = pModel.getFirst_name();
                String mName = pModel.getMiddle_name();
                String email = pModel.getEmail();
                String desig = pModel.getDesignation();
                String distName = pModel.getDistrict_name();
                String profileImage = pModel.getProfile_pic();

                String userFullName = fName + " " + mName + " " + lName;
                nameTView.setText(userFullName);
                desigTView.setText(desgtn);
                distTView.setText(distName);

                transformation = new RoundedTransformationBuilder()
                        .borderColor(getResources().getColor(R.color.colorPrimaryDark))
                        .borderWidthDp(1)
                        .cornerRadiusDp(10)
                        .oval(false)
                        .build();

               if (!profileImage.isEmpty()) {

                    Picasso.get()
                            .load(Uri.parse(profileImage))
                            .transform(transformation)
                            .resize(150, 150)
                            .centerCrop()
                            .into(profileIView);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }



    private void askUserPermission() {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Are you sure you want to logout?");
        alertDialogBuilder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                logoutRequest();
            }
        });

        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }



    @Override
    public void didSelectAlertViewListItem(TextView textView, String s) {

        if (textView == comingEventTView){
            comingEventTypeId = s;

            if (comingEventTypeId.equalsIgnoreCase("1")){
                Intent intent = new Intent(DashboardPsHrdActivity.this, PsComingEventActivity.class);
                intent.putExtra("distId","");
                intent.putExtra("eventOpt",ApConstants.kS_SELF_EVENT);
                startActivity(intent);
            }else {
                Intent intent = new Intent(DashboardPsHrdActivity.this, ComingClosedLocationListActivity.class);
                intent.putExtra("actType","upComingEvent");
                startActivity(intent);

                /*Intent intent = new Intent(DashboardPsHrdActivity.this, PsComingEventActivity.class);
                intent.putExtra("distId",districtId);
                intent.putExtra("eventOpt",ApConstants.kS_ALL_EVENT);
                startActivity(intent);*/
            }
            comingEventTView.setText(getResources().getString(R.string.pmu_upcoming_event));
        }

        if (textView == ps_calTView){
            comingEventTypeId = s;
            Intent intent = new Intent(DashboardPsHrdActivity.this, PsEventCaleActivity.class);
            intent.putExtra("comingEventType",comingEventTypeId);
            startActivity(intent);

            ps_calTView.setText(getResources().getString(R.string.pmu_upcoming_event_calender));
        }

        if (textView == reportTView){
            comingEventTypeId = s;

            if (comingEventTypeId.equalsIgnoreCase("1")){
                Intent intent = new Intent(DashboardPsHrdActivity.this, PsReportActivity.class);
                intent.putExtra("distId","");
                intent.putExtra("eventOpt",ApConstants.kS_SELF_EVENT);
                startActivity(intent);
            }else {
                Intent intent = new Intent(DashboardPsHrdActivity.this, ComingClosedLocationListActivity.class);
                intent.putExtra("actType","report");
                startActivity(intent);

                /*Intent intent = new Intent(DashboardPsHrdActivity.this, PsReportActivity.class);
                intent.putExtra("distId",districtId);
                intent.putExtra("eventOpt",ApConstants.kS_ALL_EVENT);
                startActivity(intent);*/
            }

            reportTView.setText(getResources().getString(R.string.pmu_reports));
        }
    }

    private void logoutRequest() {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("user_id", userID);
            jsonObject.put("role_id", roleId);
            jsonObject.put("token", token);
            jsonObject.put("app_id", "6");

            RequestBody requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString());
            AppinventorApi api = new AppinventorApi(this, APIServices.API_URL, "", ApConstants.kMSG, false);
            Retrofit retrofit = api.getRetrofitInstance();
            APIRequest apiRequest = retrofit.create(APIRequest.class);
            Call<JsonObject> responseCall = apiRequest.userLogoutRequest(requestBody);

            DebugLog.getInstance().d("event_dates_param=" + responseCall.request().toString());
            DebugLog.getInstance().d("event_dates_param=" + AppUtility.getInstance().bodyToString(responseCall.request()));
            api.postRequest(responseCall, this, 2);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onResponse(JSONObject jsonObject, int i) {
        try {
            if (jsonObject != null) {

                // get Event detail Response
                if (i == 2) {
                    ResponseModel responseModel = new ResponseModel(jsonObject);
                    if (responseModel.isStatus()) {
                        AppSettings.getInstance().setValue(this, ApConstants.kLOGIN_TYPE, "kLOGIN_TYPE");
                        Intent intent = new Intent(this, SplashActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        UIToastMessage.show(this, responseModel.getMsg());
                    }
                }
                if(i == 3){
                    ResponseModel responseModel = new ResponseModel(jsonObject);
                    DebugLog.getInstance().d("kNotificationCount1111=" + jsonObject);
                    if (responseModel.isStatus()) {

                        //AppSettings.getInstance().setIntValue(this, AppConstants.kNotificationCount, Integer.parseInt(jsonObject.getString("data")));
                        notifiCountValue=Integer.parseInt(jsonObject.getString("data"));
                        if(notifiCountValue >= 1){
                            Log.d("notificationCount", String.valueOf(notifiCountValue));
                            tv_notification_count.setText(""+notifiCountValue);


                        }else{

                            tv_notification_count.setVisibility(View.GONE);
                        }

                    }
                }
                if (i == 4) {
                    ResponseModel responseModel = new ResponseModel(jsonObject);

                    if (responseModel.isStatus()) {
                        login_Data = jsonObject.getJSONObject("data");

                        array2= new JSONArray();
                        array2 = login_Data.getJSONArray("roles");
                        String userLevel = AppSettings.getInstance().getValue(this, ApConstants.kUSER_LEVEL, ApConstants.kUSER_LEVEL);
                        if (array2.length() <= 1) {
                            //else if (profileModel.getPassword_change_requires() == 0) {

                            if (userLevel.equalsIgnoreCase("15") || userLevel.equalsIgnoreCase("14")) {

                                logoutRequest();

                            }

                        }else {
                            if(switchstr.equalsIgnoreCase("yes")){

                                switchtxt.setVisibility(View.VISIBLE);
                            }else{
                                switchtxt.setVisibility(View.GONE);
                            }
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
    public void onUpdateNeeded(final String updateUrl) {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("New version available")
                .setMessage("Please, update app to new version to continue reposting.")
                .setPositiveButton("Update",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                redirectStore(updateUrl);
                                // redirectStore(updateUrl);
                            }
                        }).setNegativeButton("No, thanks",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        }).create();
        dialog.show();
    }

    private void redirectStore(String updateUrl) {
        final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(updateUrl));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
