/*
 * Copyright (c) 2019. Runtime Solutions Pvt Ltd. All right reserved.
 * Web URL  http://runtime-solutions.com
 * Author Name: Vinod Vishwakarma
 * Linked In: https://www.linkedin.com/in/vvishwakarma
 * Official Email ID : vinod@runtime-solutions.com
 * Email ID: vish.vino@gmail.com
 * Last Modified : 30/8/19 12:10 PM
 */

package in.gov.pocra.training.activity.common.notification;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import in.co.appinventor.services_api.api.AppinventorApi;
import in.co.appinventor.services_api.app_util.AppUtility;
import in.co.appinventor.services_api.debug.DebugLog;
import in.co.appinventor.services_api.listener.ApiCallbackCode;
import in.co.appinventor.services_api.listener.OnMultiRecyclerItemClickListener;
import in.co.appinventor.services_api.settings.AppSettings;
import in.co.appinventor.services_api.util.Utility;
import in.co.appinventor.services_api.widget.UIToastMessage;
import in.gov.pocra.training.R;
import in.gov.pocra.training.model.online.ResponseModel;
import in.gov.pocra.training.util.ApConstants;
import in.gov.pocra.training.web_services.APIRequest;
import in.gov.pocra.training.web_services.APIServices;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Retrofit;

public class NotificationListActivity extends AppCompatActivity implements ApiCallbackCode, OnMultiRecyclerItemClickListener {

    private RecyclerView recyclerView;
   // private AppSession session;
    private int ca_id;
    private JSONArray mDataArray;
    int appID;
    String userID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notification_list_activity);

        initComponents();
        setConfiguration();
    }


    private void initComponents() {

       // session = new AppSession(this);

        recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    private void setConfiguration() {

        if (getSupportActionBar() != null) {
            getSupportActionBar().setElevation(0);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if (Utility.checkConnection(this)) {
            getnoticationList();
        } else {
            UIToastMessage.show(NotificationListActivity.this, "No internet connection");
        }

    }
    @Override
    protected void onResume() {
        super.onResume();
        if (Utility.checkConnection(this)) {
            getnoticationList();
        } else {
            UIToastMessage.show(NotificationListActivity.this, "No internet connection");
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.ca_vill_board, menu);
        return true;
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
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }



    @Override
    public void onMultiRecyclerViewItemClick(int i, Object o) {

        JSONObject jsonObject = (JSONObject)o;

        Intent intent = new Intent(this, ReadNotificationActivity.class);
        intent.putExtra("noticationData", jsonObject.toString());
        startActivity(intent);
    }

    synchronized void getnoticationList() {
        try {
            userID = AppSettings.getInstance().getValue(this, ApConstants.kUSER_ID, ApConstants.kUSER_ID);
            JSONObject jsonObject = new JSONObject();
            //        return AppSettings.getInstance().getIntValue(mContext, AppConstants.kUSER_ID, 0);

           // appID =  session.getAppId();
            jsonObject.put("user_id", userID);
            jsonObject.put("app_id", "6");

            RequestBody requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString());
            AppinventorApi api = new AppinventorApi(this, APIServices.NOTIFICATION_API_URL, "", ApConstants.kMSG, true);
            Retrofit retrofit = api.getRetrofitInstance();
            APIRequest apiRequest = retrofit.create(APIRequest.class);
            Call<JsonObject> responseCall = apiRequest.getfirebaseNotificationList(requestBody);

            DebugLog.getInstance().d("event_dates_param=" + responseCall.request().toString());
            DebugLog.getInstance().d("event_dates_param=" + AppUtility.getInstance().bodyToString(responseCall.request()));
            api.postRequest(responseCall, this, 1);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }





    @Override
    public void onResponse(JSONObject jsonObject, int i) {
        if (jsonObject != null) {
            ResponseModel responseModel = new ResponseModel(jsonObject);

            if (i == 1) {

                if (responseModel.isStatus()) {
                    JSONArray jsonArray = responseModel.getDataArray();
                    if (jsonArray.length() > 0) {
                        NotificationMsgListAdapter adapter = new NotificationMsgListAdapter(this, this, jsonArray);
                        recyclerView.setAdapter(adapter);

                    }

                } else {
                    UIToastMessage.show(this, responseModel.getMsg());
                }
            }



        }
    }

    @Override
    public void onFailure(Object o, Throwable throwable, int i) {

    }






}
