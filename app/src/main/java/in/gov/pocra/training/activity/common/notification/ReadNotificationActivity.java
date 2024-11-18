package in.gov.pocra.training.activity.common.notification;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import in.co.appinventor.services_api.api.AppinventorApi;
import in.co.appinventor.services_api.api.AppinventorIncAPI;
import in.co.appinventor.services_api.app_util.AppUtility;
import in.co.appinventor.services_api.debug.DebugLog;
import in.co.appinventor.services_api.listener.ApiCallbackCode;
import in.co.appinventor.services_api.listener.OnMultiRecyclerItemClickListener;
import in.co.appinventor.services_api.settings.AppSettings;
import in.co.appinventor.services_api.util.Utility;
import in.co.appinventor.services_api.widget.UIAlertView;
import in.co.appinventor.services_api.widget.UIToastMessage;
import in.gov.pocra.training.R;
import in.gov.pocra.training.activity.ca.dashboard.DashboardCaActivity;
import in.gov.pocra.training.activity.common.login.LoginActivity;
import in.gov.pocra.training.activity.common.splash.SplashActivity;
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

public class ReadNotificationActivity extends AppCompatActivity implements ApiCallbackCode, OnMultiRecyclerItemClickListener {

    private TextView tittleTextView;
    private TextView messageTextView,dateTextView,readTextView,urlInfoTextView;
    private ImageView checkImageView;
    String notificationstr,notificationPayloadData,data,notification_data,is_read,created_at,createdatetime;
    String title,body,urlLink,notificationId,user_action;
    //private AppSession session;
    String loginType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_notification);

        initComponents();
        setConfiguration();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

//            case R.id.action_villa_sync:
//
//                if (Utility.checkConnection(this)) {
//                    syncDownClusterVillages();
//                } else {
//                    UIAlertView.getOurInstance().show(this, new AppString(this).getkNETWORK());
//                }

            // return true;

            case android.R.id.home:

                loginType = AppSettings.getInstance().getValue(this,ApConstants.kLOGIN_TYPE,ApConstants.kLOGIN_TYPE);

                if (loginType.equalsIgnoreCase(ApConstants.kCOORD_TYPE)){
                    startActivity(new Intent(this, DashboardCoordActivity.class));
                    finish();
                }else if (loginType.equalsIgnoreCase(ApConstants.kPS_TYPE)){
                    startActivity(new Intent(this, DashboardPsHrdActivity.class));
                    finish();
                }else if (loginType.equalsIgnoreCase(ApConstants.kPMU_TYPE)){
                    startActivity(new Intent(this, DashboardPmuActivity.class));
                    finish();
                }else if (loginType.equalsIgnoreCase(ApConstants.kCA_TYPE)){
                    startActivity(new Intent(this, DashboardCaActivity.class));
                    finish();
                }else {
                    startActivity(new Intent(this, LoginActivity.class));
                    finish();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void initComponents() {

        //session = new AppSession(this);

        tittleTextView = findViewById(R.id.tittleTextView);
        messageTextView = findViewById(R.id.messageTextView);
        dateTextView = findViewById(R.id.dateTextView);
        urlInfoTextView = findViewById(R.id.urlInfoTextView);



    }

    private void setConfiguration() {

        if (getSupportActionBar() != null) {
            getSupportActionBar().setElevation(0);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }


      loginType = AppSettings.getInstance().getValue(this,ApConstants.kLOGIN_TYPE,ApConstants.kLOGIN_TYPE);

        if (!(loginType.equalsIgnoreCase(ApConstants.kLOGIN_TYPE))) {
            if (getIntent() != null) {
                String mData = getIntent().getStringExtra("noticationData");
                if (!(null == mData)) {
                    try {
                        JSONObject jsonObject = new JSONObject(mData);
                        NotificationListmodel model = new NotificationListmodel(jsonObject);
                        Log.d("array", jsonObject.toString());
                        notification_data = model.getNotification_data();
                        //is_read =model.getIs_read();
                        //created_at =model.getCreated_at();
                        String unixcreatedTimeStamp = model.getCreated_at();
                        if (!(unixcreatedTimeStamp == null)) {
                            try {
                                // convert seconds to milliseconds
                                Date date = new java.util.Date(Long.parseLong(unixcreatedTimeStamp) * 1000L);
                                // the format of your date
                                SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd-MM-yyyy");
                                // give a timezone reference for formatting (see comment at the bottom)
                                //sdf.setTimeZone(java.util.TimeZone.getTimeZone("GMT-4"));
                                created_at = sdf.format(date);
                                Log.d("createdatetime", created_at);
                            } catch (NumberFormatException e) {
                                System.out.println("not a number");
                            }
                        }
                        notificationId = model.getId();

                        try {
                            JSONObject json = new JSONObject(notification_data);
                            notificationstr = json.getString("notification");
                            JSONObject notification = new JSONObject(notificationstr);
                            title = notification.getString("title");
                            body = notification.getString("body");
                            notificationPayloadData = json.getString("data");
                            JSONObject notificationData = new JSONObject(notificationPayloadData);
                            urlLink = notificationData.getString("urlLink");
                            Log.d("shshshsh", title + "" + " " + body + " " + urlLink);


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else {
                    Bundle bundle = getIntent().getExtras();// add these lines of code to get data from notification
                    if (bundle != null) {
                        title = bundle.getString("title");
                        body = bundle.getString("body");
                        urlLink = bundle.getString("urlLink");
                        String unixcreatedTimeStamp = bundle.getString("createdate");
                        if (!(unixcreatedTimeStamp == null)) {
                            try {
                                // convert seconds to milliseconds
                                Date date = new java.util.Date(Long.parseLong(unixcreatedTimeStamp) * 1000L);
                                // the format of your date
                                SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd-MM-yyyy");
                                // give a timezone reference for formatting (see comment at the bottom)
                                //sdf.setTimeZone(java.util.TimeZone.getTimeZone("GMT-4"));
                                created_at = sdf.format(date);
                                Log.d("createdatetime", created_at);
                            } catch (NumberFormatException e) {
                                System.out.println("not a number");
                            }
                        }
                        notificationId = bundle.getString("notificationId");
                        Log.d("noticationData", title + " " + body + " " + urlLink + " " + notificationId + " " + created_at);
                    }
                }
            }
            if (Utility.checkConnection(this)) {
                readNotication();
            } else {
                UIToastMessage.show(ReadNotificationActivity.this, "No internet connection");
            }

            if (urlLink == null) {
                urlInfoTextView.setVisibility(View.GONE);
                tittleTextView.setText("" + title);
                messageTextView.setText("Message   : " + body);
                dateTextView.setText("" + created_at);
            } else {
                tittleTextView.setText("" + title);
                messageTextView.setText("Message : " + body);
                dateTextView.setText("" + created_at);
                urlInfoTextView.setTextColor(Color.parseColor("#000080"));
                urlInfoTextView.setVisibility(View.VISIBLE);
                urlInfoTextView.setText("" + urlLink);
                urlInfoTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (urlLink.contains("http")) {
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlLink));
                            startActivity(browserIntent);
                        }
                    }
                });
            }
        }else{
            startActivity(new Intent(this, SplashActivity.class));
            finish();
        }
    }

    private void readNotication() {
        try {
            user_action = "Read";
            int note= Integer.parseInt(notificationId);
            List<Integer> list = new ArrayList<Integer>();
            list.add(note);

            JSONArray array = new JSONArray();
            for (int i = 0; i < list.size(); i++) {
                array.put(list.get(i));
            }
            JSONObject obj = new JSONObject();
            try {
                obj.put("result", array);
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            Log.d("dsfgghsdfgsdf",obj.toString());
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("user_action", user_action);
            jsonObject.put("notification_ids", obj);


            RequestBody requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString());
            AppinventorApi api = new AppinventorApi(this, APIServices.API_URL, "", ApConstants.kMSG, true);
            Retrofit retrofit = api.getRetrofitInstance();
            APIRequest apiRequest = retrofit.create(APIRequest.class);
            Call<JsonObject> responseCall = apiRequest.readfirebaseNotification(requestBody);

            DebugLog.getInstance().d("event_dates_param=" + responseCall.request().toString());
            DebugLog.getInstance().d("event_dates_param=" + AppUtility.getInstance().bodyToString(responseCall.request()));
            api.postRequest(responseCall, this, 1);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    @Override
    public void onResponse(JSONObject jsonObject, int i) {
        Log.d("jsonObjectData", jsonObject.toString());
        if (jsonObject != null) {
            ResponseModel response = new ResponseModel(jsonObject);

            if (i == 1) {
                if (response.isStatus()) {
                   // UIToastMessage.show(this, response.getResponse());
                } else {
                    UIToastMessage.show(this, response.getResponse());
                }
            }


        }

    }

    @Override
    public void onFailure(Object o, Throwable throwable, int i) {

    }

    @Override
    public void onMultiRecyclerViewItemClick(int i, Object o) {

    }

}