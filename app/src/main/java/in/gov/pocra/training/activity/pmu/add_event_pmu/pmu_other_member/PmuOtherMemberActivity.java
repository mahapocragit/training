package in.gov.pocra.training.activity.pmu.add_event_pmu.pmu_other_member;

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
import in.co.appinventor.services_api.widget.UIToastMessage;
import in.gov.pocra.training.R;
import in.gov.pocra.training.activity.coordinator.event_day_attendance.attend_other_member.AdaptorAttendOtherMemberAddEdit;
import in.gov.pocra.training.activity.coordinator.event_day_attendance.event_day_group_mem_attendance.GruMemAttendanceActivity;
import in.gov.pocra.training.model.online.ResponseModel;
import in.gov.pocra.training.util.ApConstants;
import in.gov.pocra.training.util.ApUtil;
import in.gov.pocra.training.web_services.APIRequest;
import in.gov.pocra.training.web_services.APIServices;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Retrofit;

public class PmuOtherMemberActivity extends AppCompatActivity implements ApiCallbackCode {

    private ImageView homeBack;
    private ImageView addPersonImageView;
    private RecyclerView otherMemRecyclerView;
    private Button oSubmitButton;

    private String schId;
    private String VCRMCdata;
    private String attendType;
    private String VCRMC_GP_Id;
    private String title;
    private String schDate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pmu_other_member);

        /* ** For actionbar title in center ***/
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.attendance_actionbar_layout);
        AppCompatTextView actionTitleTextView = (AppCompatTextView) getSupportActionBar().getCustomView().findViewById(R.id.actionTitleTextView);
        homeBack = (ImageView) getSupportActionBar().getCustomView().findViewById(R.id.backImageView);
        addPersonImageView = (ImageView) getSupportActionBar().getCustomView().findViewById(R.id.addPersonImageView);
        homeBack.setVisibility(View.VISIBLE);
        addPersonImageView.setVisibility(View.VISIBLE);
        actionTitleTextView.setText("Other Member's");


        initialization();
        defaultConfiguration();
    }

    private void initialization() {

        title = getIntent().getStringExtra("title");
        schId = getIntent().getStringExtra("schId");
        VCRMCdata = getIntent().getStringExtra("data");
        attendType = getIntent().getStringExtra("type");
        schDate = getIntent().getStringExtra("schDate");


        otherMemRecyclerView = (RecyclerView)findViewById(R.id.otherMemRecyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        otherMemRecyclerView.setLayoutManager(linearLayoutManager);
        oSubmitButton = (Button) findViewById(R.id.oSubmitButton);
    }


    @Override
    protected void onResume() {
        super.onResume();
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
                Intent intent = new Intent(PmuOtherMemberActivity.this, PmuAddEditOtherMemActivity.class);
                startActivity(intent);
            }
        });

        oSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!ApUtil.isFutureDate(schDate)){
                    Intent intent = new Intent(PmuOtherMemberActivity.this, GruMemAttendanceActivity.class);
                    intent.putExtra("title",title);
                    intent.putExtra("data",VCRMCdata);
                    intent.putExtra("schId",schId);
                    intent.putExtra("type",attendType);
                    startActivity(intent);
                    finish();
                }else {
                    UIToastMessage.show(PmuOtherMemberActivity.this, "Can't do attendance for this schedule Today");
                }

            }
        });

    }


    private void getOtherMemberDetail( String vcrmcId, String type) {

        try {

            // To get Detail
            JSONObject param = new JSONObject();
            param.put("gp_id", vcrmcId);
            param.put("type", type);
            param.put("schedule_id", schId);

            RequestBody requestBody = AppUtility.getInstance().getRequestBody(param.toString());
            AppinventorApi api = new AppinventorApi(this, APIServices.TR_API_URL, "", ApConstants.kMSG, true);
            Retrofit retrofit = api.getRetrofitInstance();
            APIRequest apiRequest = retrofit.create(APIRequest.class);
            Call<JsonObject> responseCall = apiRequest.getVCRMCMemAttendDetailListRequest(requestBody);

            DebugLog.getInstance().d("VCRMC_Member_Detail_update_list_param=" + responseCall.request().toString());
            DebugLog.getInstance().d("VCRMC_Member_Detail_update_list_param=" + AppUtility.getInstance().bodyToString(responseCall.request()));

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
                        JSONArray vcrmcMemberArrayList = jsonObject.getJSONArray("data");
                        if (vcrmcMemberArrayList.length() > 0) {
                            AdaptorAttendOtherMemberAddEdit adaptorAttendOtherMemberAddEdit = new AdaptorAttendOtherMemberAddEdit(this, vcrmcMemberArrayList, attendType, VCRMC_GP_Id, schId);
                            otherMemRecyclerView.setAdapter(adaptorAttendOtherMemberAddEdit);
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
