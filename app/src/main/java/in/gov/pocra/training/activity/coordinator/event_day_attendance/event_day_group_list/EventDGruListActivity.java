package in.gov.pocra.training.activity.coordinator.event_day_attendance.event_day_group_list;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
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
import in.gov.pocra.training.activity.coordinator.attend_image_upload.AttendImageUploadActivity;
import in.gov.pocra.training.activity.coordinator.event_day_attendance.attend_other_member.AttendOtherMemAddEditActivity;
import in.gov.pocra.training.activity.coordinator.event_day_attendance.event_day_group_mem_attendance.GruMemAttendanceActivity;
import in.gov.pocra.training.event_db.CordOfflineDBase;
import in.gov.pocra.training.model.online.ResponseModel;
import in.gov.pocra.training.util.ApConstants;
import in.gov.pocra.training.web_services.APIRequest;
import in.gov.pocra.training.web_services.APIServices;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Retrofit;

public class EventDGruListActivity extends AppCompatActivity implements OnMultiRecyclerItemClickListener, ApiCallbackCode {


    private ImageView homeBack;
    private ImageView uploadImageIView;

    private RecyclerView vcrmcRecyclerView;
    private String roleId;
    private String userID;
    private Boolean isAttendanceCompleted = true;
    private String schId;
    private String eventDate;
    private Button submitButton;

    private String onlineStatus;
    private CordOfflineDBase cDB;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_day_group_list);

        /* ** For actionbar title in center ***/
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.attendance_actionbar_layout);
        AppCompatTextView actionTitleTextView = (AppCompatTextView) getSupportActionBar().getCustomView().findViewById(R.id.actionTitleTextView);
        homeBack = (ImageView) getSupportActionBar().getCustomView().findViewById(R.id.backImageView);
        uploadImageIView = (ImageView) getSupportActionBar().getCustomView().findViewById(R.id.addPersonImageView);
        homeBack.setVisibility(View.VISIBLE);
        uploadImageIView.setImageDrawable(getResources().getDrawable(R.drawable.ic_add_a_photo));
        uploadImageIView.setVisibility(View.VISIBLE);
        String title = getIntent().getStringExtra("title");
        if (!title.equalsIgnoreCase("")){
            actionTitleTextView.setText(title);
        }else {
            actionTitleTextView.setText("Today's Event");
        }

        cDB = new CordOfflineDBase(this);

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

        // To get User Id and Roll Id
        String rId = AppSettings.getInstance().getValue(this, ApConstants.kROLE_ID, ApConstants.kROLE_ID);
        String uId = AppSettings.getInstance().getValue(this, ApConstants.kUSER_ID, ApConstants.kUSER_ID);
        if (!rId.equalsIgnoreCase("kROLE_ID")) {
            roleId = rId;
        }
        if (!uId.equalsIgnoreCase("kUSER_ID")) {
            userID = uId;
        }

        submitButton = (Button)findViewById(R.id.submitButton);
        vcrmcRecyclerView = (RecyclerView) findViewById(R.id.vcrmcRecyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        vcrmcRecyclerView.setLayoutManager(linearLayoutManager);

    }

    @Override
    protected void onResume() {
        super.onResume();

        eventDate = getIntent().getStringExtra("eventDate");
        schId = getIntent().getStringExtra("schId");


        onlineStatus = AppSettings.getInstance().getValue(EventDGruListActivity.this, ApConstants.kONLINE_STATUS, ApConstants.kONLINE_STATUS);
        if (onlineStatus.equalsIgnoreCase(ApConstants.kONLINE)) {
            getEventGroupList();
        }else {
            getOffEventGroupList();
        }

    }

    private void defaultConfiguration() {

        uploadImageIView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EventDGruListActivity.this, AttendImageUploadActivity.class);
                intent.putExtra("event_date",eventDate);
                intent.putExtra("schId",schId);
                startActivity(intent);
            }
        });


        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitButtonAction();
            }
        });

    }


    private void getOffEventGroupList() {

        JSONArray groupListArray = cDB.getGroupListByEventID(schId);
        if (groupListArray.length()>0){
            AdaptorEventDGruList adaptorEventDGruList = new AdaptorEventDGruList(this, groupListArray, this);
            vcrmcRecyclerView.setAdapter(adaptorEventDGruList);
        }else {
            UIToastMessage.show(this,"Group list not found");
        }

    }



    private void getEventGroupList() {

        try {
            // To get Detail
            JSONObject param = new JSONObject();
            param.put("schedule_id", schId);
            param.put("date", eventDate);
            param.put("api_key",ApConstants.kAUTHORITY_KEY);

            RequestBody requestBody = AppUtility.getInstance().getRequestBody(param.toString());
            AppinventorApi api = new AppinventorApi(this, APIServices.BASE_URL, "", ApConstants.kMSG, true);
            Retrofit retrofit = api.getRetrofitInstance();
            APIRequest apiRequest = retrofit.create(APIRequest.class);
            Call<JsonObject> responseCall = apiRequest.cordEventGroupListRequest(requestBody);

            DebugLog.getInstance().d("Event_group_list_param=" + responseCall.request().toString());
            DebugLog.getInstance().d("Event_group_list_param=" + AppUtility.getInstance().bodyToString(responseCall.request()));

            api.postRequest(responseCall, this, 1);


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    private void submitButtonAction() {

        try {
            // To get Detail
            JSONObject param = new JSONObject();
            param.put("schedule_id", schId);

            RequestBody requestBody = AppUtility.getInstance().getRequestBody(param.toString());
            AppinventorApi api = new AppinventorApi(this, APIServices.TR_API_URL, "", ApConstants.kMSG, true);
            Retrofit retrofit = api.getRetrofitInstance();
            APIRequest apiRequest = retrofit.create(APIRequest.class);
            Call<JsonObject> responseCall = apiRequest.vCRMCFinalAttendanceRequest(requestBody);

            DebugLog.getInstance().d("VCRMC_Final_attendance_request_param=" + responseCall.request().toString());
            DebugLog.getInstance().d("VCRMC_Final_attendance_request_param=" + AppUtility.getInstance().bodyToString(responseCall.request()));

            api.postRequest(responseCall, this, 2);


        } catch (JSONException e) {
            e.printStackTrace();
        }


    }




    @Override
    public void onMultiRecyclerViewItemClick(int i, Object o) {

        if (o != null) {

            // Temp For Testing

            JSONObject jsonObject = (JSONObject) o;
            try {

                String name = jsonObject.getString("name");
                String grId = jsonObject.getString("id");
                String attendType = jsonObject.getString("attendance_type");

                if (name.equalsIgnoreCase("Other")){
                    Intent intent = new Intent(this, AttendOtherMemAddEditActivity.class);
                    intent.putExtra("grpId",grId);
                    intent.putExtra("title", name);
                    intent.putExtra("event_date",eventDate);
                    intent.putExtra("attendType",attendType);
                    intent.putExtra("schId",schId);
                    startActivity(intent);
                }else {
                    Intent intent = new Intent(this, GruMemAttendanceActivity.class);
                    intent.putExtra("grpId",grId);
                    intent.putExtra("title", name);
                    intent.putExtra("attendDate",eventDate);
                    intent.putExtra("attendType",attendType);
                    intent.putExtra("schId",schId);
                    startActivity(intent);
                }



            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onResponse(JSONObject jsonObject, int i) {

        try {

            if (jsonObject != null) {
                // Schedule Response for PS HRD
                if (i == 1) {
                    ResponseModel responseModel = new ResponseModel(jsonObject);

                    if (responseModel.isStatus()) {

                        JSONArray groupListArray = responseModel.getData();
                        if (groupListArray.length()>0){
                            AdaptorEventDGruList adaptorEventDGruList = new AdaptorEventDGruList(this, groupListArray, this);
                            vcrmcRecyclerView.setAdapter(adaptorEventDGruList);
                        }

                    }else {
                        UIToastMessage.show(this,responseModel.getMsg());
                    }
                }


                // For final attendance confirmation
                if (i == 2) {
                    ResponseModel responseModel = new ResponseModel(jsonObject);

                    if (responseModel.isStatus()) {
                        UIToastMessage.show(this,responseModel.getMsg());
                        finish();
                    }else {
                        UIToastMessage.show(this,responseModel.getMsg());
                    }
                }


                // Attendance Image upload response
                if (i == 3) {
                    ResponseModel responseModel = new ResponseModel(jsonObject);

                    if (responseModel.isStatus()) {
                        UIToastMessage.show(this, responseModel.getMsg());
                        JSONObject data = jsonObject.getJSONObject("data");
                         // uploadedImageName = data.getString("file_name");
                        String imageUrl = data.getString("file_url");

                        // isFirstImageSelected = true;

                       /* if (!imageUrl.isEmpty()) {

                            Picasso.get()
                                    .load(Uri.parse(imageUrl))
                                    .resize(150, 150)
                                    .centerCrop()
                                    .into(firstImageView);
                        }
                        */


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
