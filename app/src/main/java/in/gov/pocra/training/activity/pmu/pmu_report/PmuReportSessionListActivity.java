package in.gov.pocra.training.activity.pmu.pmu_report;

import android.content.DialogInterface;
import android.content.Intent;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

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
import in.co.appinventor.services_api.widget.UIToastMessage;
import in.gov.pocra.training.R;
import in.gov.pocra.training.activity.coordinator.event_session.AddSessionActivity;
import in.gov.pocra.training.model.online.ResponseModel;
import in.gov.pocra.training.util.ApConstants;
import in.gov.pocra.training.web_services.APIRequest;
import in.gov.pocra.training.web_services.APIServices;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Retrofit;

public class PmuReportSessionListActivity extends AppCompatActivity implements ApiCallbackCode, OnMultiRecyclerItemClickListener {
    private ImageView homeBack;
    private String roleId;
    private String userID;
    private String eventDate;
    private String schId;
    private ImageView addSessionIView;
    private RecyclerView sessionRView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pmu_report_session_list);

        /* ** For actionbar title in center ***/
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.attendance_actionbar_layout);
        AppCompatTextView actionTitleTextView = (AppCompatTextView) getSupportActionBar().getCustomView().findViewById(R.id.actionTitleTextView);
        homeBack = (ImageView) getSupportActionBar().getCustomView().findViewById(R.id.backImageView);
        addSessionIView = (ImageView) getSupportActionBar().getCustomView().findViewById(R.id.addPersonImageView);
        homeBack.setVisibility(View.VISIBLE);
        addSessionIView.setImageDrawable(getResources().getDrawable(R.drawable.ic_add_circle));
        addSessionIView.setVisibility(View.GONE);
        actionTitleTextView.setText(getResources().getString(R.string.title_event_session));

        initialization();
        defaultConfiguration();
    }

    private void initialization() {

        homeBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // To get User Id and Roll Id
        String rId = AppSettings.getInstance().getValue(this, ApConstants.kROLE_ID, ApConstants.kROLE_ID);
        String uId = AppSettings.getInstance().getValue(this, ApConstants.kUSER_ID, ApConstants.kUSER_ID);
        if (!rId.equalsIgnoreCase("kROLE_ID")) {
            roleId = rId;
        }

        if (!uId.equalsIgnoreCase("kUSER_ID")) {
            userID = uId;
        }


        sessionRView = (RecyclerView)findViewById(R.id.ReportsessionRView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false);
        sessionRView.setLayoutManager(linearLayoutManager);

        eventDate = getIntent().getStringExtra("eventDate");
        schId = getIntent().getStringExtra("schId");

    }


    @Override
    protected void onResume() {
        super.onResume();

        getSessionList();
    }



    private void defaultConfiguration() {

        addSessionIView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PmuReportSessionListActivity.this, AddSessionActivity.class);
                intent.putExtra("schId",schId);
                intent.putExtra("eventDate",eventDate);
                startActivity(intent);

            }
        });

    }


    private void getSessionList() {

        try {

            // To get Detail
            JSONObject param = new JSONObject();
            param.put("schedule_event_id", schId);
            param.put("event_date", eventDate);
            param.put("api_key", ApConstants.kAUTHORITY_KEY); // Type and designation having same Id. (According API developer)


            RequestBody requestBody = AppUtility.getInstance().getRequestBody(param.toString());
            AppinventorApi api = new AppinventorApi(this, APIServices.BASE_URL, "", ApConstants.kMSG, true);
            Retrofit retrofit = api.getRetrofitInstance();
            APIRequest apiRequest = retrofit.create(APIRequest.class);
            Call<JsonObject> responseCall = apiRequest.getSessionListRequest(requestBody);

            DebugLog.getInstance().d("get_session_list_param=" + responseCall.request().toString());
            DebugLog.getInstance().d("get_session_list_param=" + AppUtility.getInstance().bodyToString(responseCall.request()));

            api.postRequest(responseCall, this, 1);


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onMultiRecyclerViewItemClick(int i, Object o) {


        try {
            JSONObject sessionData = (JSONObject)o;
            if (i == 1){

                final String sesId = sessionData.getString("id");

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setMessage("Are you sure you want to cancel the session? ");
                alertDialogBuilder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        JSONObject param = new JSONObject();
                        try {
                            param.put("id", sesId);
                            param.put("api_key", ApConstants.kAUTHORITY_KEY); // Type and designation having same Id. (According API developer)

                            RequestBody requestBody = AppUtility.getInstance().getRequestBody(param.toString());
                            AppinventorApi api = new AppinventorApi(PmuReportSessionListActivity.this, APIServices.BASE_URL, "", ApConstants.kMSG, true);
                            Retrofit retrofit = api.getRetrofitInstance();
                            APIRequest apiRequest = retrofit.create(APIRequest.class);
                            Call<JsonObject> responseCall = apiRequest.deleteSessionRequest(requestBody);

                            DebugLog.getInstance().d("remove_session_param=" + responseCall.request().toString());
                            DebugLog.getInstance().d("remove_session_param=" + AppUtility.getInstance().bodyToString(responseCall.request()));

                            api.postRequest(responseCall, PmuReportSessionListActivity.this, 2);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

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

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    @Override
    public void onResponse(JSONObject jsonObject, int i) {

        try {

            if (jsonObject != null) {

                if (i == 1) {
                    ResponseModel responseModel = new ResponseModel(jsonObject);
                    if (responseModel.isStatus()) {

                        JSONArray dataArray = responseModel.getData();

                        if (dataArray.length()>0){
                            AdaptorReportSessionList adaptorReportSessionList = new AdaptorReportSessionList(this,dataArray,this);
                            sessionRView.setAdapter(adaptorReportSessionList);
                        }else {
                            AdaptorReportSessionList adaptorReportSessionList = new AdaptorReportSessionList(this,new JSONArray(),this);
                            sessionRView.setAdapter(adaptorReportSessionList);
                        }

                    } else {
                        AdaptorReportSessionList adaptorReportSessionList = new AdaptorReportSessionList(this,new JSONArray(),this);
                        sessionRView.setAdapter(adaptorReportSessionList);
                        UIToastMessage.show(this, responseModel.getMsg());
                    }
                }


                if (i == 2) {

                    ResponseModel responseModel = new ResponseModel(jsonObject);
                    if (responseModel.isStatus()) {
                        UIToastMessage.show(this, responseModel.getMsg());
                        getSessionList();
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
