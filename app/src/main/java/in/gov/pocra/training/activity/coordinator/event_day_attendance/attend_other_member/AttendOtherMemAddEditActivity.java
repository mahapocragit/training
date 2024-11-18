package in.gov.pocra.training.activity.coordinator.event_day_attendance.attend_other_member;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

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
import in.gov.pocra.training.R;
import in.gov.pocra.training.activity.coordinator.add_edit_other_member.AdaptorCordOtherMemList;
import in.gov.pocra.training.activity.coordinator.add_edit_other_member.AddEditOtherMemberActivity;
import in.gov.pocra.training.activity.coordinator.event_day_attendance.event_day_group_mem_attendance.GruMemAttendanceActivity;
import in.gov.pocra.training.event_db.CordOfflineDBase;
import in.gov.pocra.training.model.online.ResponseModel;
import in.gov.pocra.training.util.ApConstants;
import in.gov.pocra.training.web_services.APIRequest;
import in.gov.pocra.training.web_services.APIServices;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Retrofit;

public class AttendOtherMemAddEditActivity extends AppCompatActivity implements ApiCallbackCode, OnMultiRecyclerItemClickListener {

    private ImageView homeBack;
    private String onlineStatus;
    private CordOfflineDBase cDB;

    private ImageView addPersonImageView;
    private RecyclerView otherMemRecyclerView;
    private Button nextButton;

    private String title;
    private String schId;
    private String gruId;
    private String attendType;
    private String attendDate;
    private JSONArray otherGrpMemArray = new JSONArray();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attend_other_member_add_edit);

        /* ** For actionbar title in center ***/
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.attendance_actionbar_layout);
        AppCompatTextView actionTitleTextView = (AppCompatTextView) getSupportActionBar().getCustomView().findViewById(R.id.actionTitleTextView);
        homeBack = (ImageView) getSupportActionBar().getCustomView().findViewById(R.id.backImageView);
        addPersonImageView = (ImageView) getSupportActionBar().getCustomView().findViewById(R.id.addPersonImageView);
        homeBack.setVisibility(View.VISIBLE);
        addPersonImageView.setVisibility(View.VISIBLE);
        actionTitleTextView.setText("Other Members");

        cDB = new CordOfflineDBase(this);


        initialization();
        defaultConfiguration();
    }

    private void initialization() {

        title = getIntent().getStringExtra("title");
        schId = getIntent().getStringExtra("schId");
        gruId = getIntent().getStringExtra("grpId");
        attendDate = getIntent().getStringExtra("event_date");
        attendType = getIntent().getStringExtra("attendType");


        otherMemRecyclerView = (RecyclerView)findViewById(R.id.otherMemRecyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        otherMemRecyclerView.setLayoutManager(linearLayoutManager);
        nextButton = (Button) findViewById(R.id.oNextButton);
    }


    @Override
    protected void onResume() {
        super.onResume();

        onlineStatus = AppSettings.getInstance().getValue(AttendOtherMemAddEditActivity.this, ApConstants.kONLINE_STATUS, ApConstants.kONLINE_STATUS);

        if (onlineStatus.equalsIgnoreCase(ApConstants.kONLINE)) {
            getOtherMemList();
        }else {
            long memOtherCount = cDB.getNoOfRecord(CordOfflineDBase.EVENT_OTHER_MEM_TABLE);
            if (memOtherCount>0){
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getOffOtherMemList();
                    }
                },500);

            }else {
//                UIToastMessage.show(AttendOtherMemAddEditActivity.this,"Other member not found");
                Toast.makeText(AttendOtherMemAddEditActivity.this, "Other member not found", Toast.LENGTH_SHORT).show();
            }

        }

        /*try {
            JSONObject jsonObject = new JSONObject(VCRMCdata);
            VCRMC_GP_Id = jsonObject.getString("id");
            attendType = jsonObject.getString("type");
            getOtherMemberDetail(VCRMC_GP_Id, attendType);

        } catch (JSONException e) {
            e.printStackTrace();
        }*/

    }




    private void defaultConfiguration() {

        // For Action Bar
        homeBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // For Action Bar
        addPersonImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AttendOtherMemAddEditActivity.this, AddEditOtherMemberActivity.class);
                intent.putExtra("attendType",attendType);
                intent.putExtra("type","Add");
                intent.putExtra("attendDate",attendDate);
                intent.putExtra("schId",schId);

                startActivity(intent);
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (otherGrpMemArray.length()>0){
                    Intent intent = new Intent(AttendOtherMemAddEditActivity.this, GruMemAttendanceActivity.class);
                    intent.putExtra("title",title);
                    intent.putExtra("grpId",gruId);
                    intent.putExtra("schId",schId);
                    intent.putExtra("attendDate",attendDate);
                    intent.putExtra("attendType",attendType);
                    startActivity(intent);
                    finish();
                }else {
//                    UIToastMessage.show(AttendOtherMemAddEditActivity.this,"Please Add other person detail");
                    Toast.makeText(AttendOtherMemAddEditActivity.this, "Please Add other person detail", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    @Override
    public void onMultiRecyclerViewItemClick(int i, Object o) {

    }


    private void getOffOtherMemList() {

        otherGrpMemArray = cDB.getOtherMemByEventId(schId);
        if (otherGrpMemArray.length()>0){
            AdaptorCordOtherMemList adaptorCordOtherMemList = new AdaptorCordOtherMemList(AttendOtherMemAddEditActivity.this,otherGrpMemArray,AttendOtherMemAddEditActivity.this);
            otherMemRecyclerView.setAdapter(adaptorCordOtherMemList);
        }

    }


    private void getOtherMemList( ) {
        try {

            // To get Detail
            JSONObject param = new JSONObject();

            param.put("schedule_event_id",schId);
            param.put("search_string", ""); // Type and designation having same Id. (According API developer)
            param.put("api_key", ApConstants.kAUTHORITY_KEY);

            RequestBody requestBody = AppUtility.getInstance().getRequestBody(param.toString());
            AppinventorApi api = new AppinventorApi(this, APIServices.BASE_URL, "", ApConstants.kMSG, true);
            Retrofit retrofit = api.getRetrofitInstance();
            APIRequest apiRequest = retrofit.create(APIRequest.class);
            Call<JsonObject> responseCall = apiRequest.getOtherGroupMemListRequest(requestBody);

            DebugLog.getInstance().d("get_other_member_list_param=" + responseCall.request().toString());
            DebugLog.getInstance().d("get_other_member_list_param=" + AppUtility.getInstance().bodyToString(responseCall.request()));

            api.postRequest(responseCall, this, 1);


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onResponse(JSONObject jsonObject, int i) {
        try {

            if (jsonObject != null) {
                // Schedule Response member list
                if (i == 1) {
                    ResponseModel responseModel = new ResponseModel(jsonObject);
                    if (responseModel.isStatus()) {
                        otherGrpMemArray = responseModel.getData();
                        if (otherGrpMemArray.length()>0){
                            AdaptorCordOtherMemList adaptorCordOtherMemList = new AdaptorCordOtherMemList(AttendOtherMemAddEditActivity.this,otherGrpMemArray,AttendOtherMemAddEditActivity.this);
                            otherMemRecyclerView.setAdapter(adaptorCordOtherMemList);
                        }

                    } else {
                        //UIToastMessage.show(this, responseModel.getMsg());
                        Toast.makeText(this, ""+responseModel.getMsg(), Toast.LENGTH_SHORT).show();
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
