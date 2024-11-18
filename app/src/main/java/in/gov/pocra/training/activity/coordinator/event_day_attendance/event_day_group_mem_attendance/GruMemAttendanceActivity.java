package in.gov.pocra.training.activity.coordinator.event_day_attendance.event_day_group_mem_attendance;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
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
import in.co.appinventor.services_api.widget.UIToastMessage;
import in.gov.pocra.training.R;
import in.gov.pocra.training.activity.coordinator.attend_image_upload.AttendImageUploadActivity;
import in.gov.pocra.training.event_db.CordOfflineDBase;
import in.gov.pocra.training.model.online.ResponseModel;
import in.gov.pocra.training.util.ApConstants;
import in.gov.pocra.training.web_services.APIRequest;
import in.gov.pocra.training.web_services.APIServices;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Retrofit;

public class GruMemAttendanceActivity extends AppCompatActivity implements OnMultiRecyclerItemClickListener, ApiCallbackCode {

    private ImageView homeBack;
    private ImageView uploadImageIView;
    private CordOfflineDBase cDB;

    private EditText searchEText;
    private ImageView searchIView;
    private EditText searchMobText;
    private ImageView searchMobView;
    private Button submitButton;

    private AdaptorGruMemAttendance adaptorGruMemAttendance;
    private String presentPeopleList = "";
    private String schId;
    private RecyclerView atdRecyclerView;
    private String attendType = "vcrmc";
    private String roleId;
    private String userID;
    private String presentPeopleType;
    private TextView gTitleTextView;
    private String grpId;
    private String attendDate;
    private String onlineStatus;
    private JSONArray presentMemArray = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gru_mem_attendance);

        /* ** For actionbar title in center ***/
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.attendance_actionbar_layout);
        AppCompatTextView actionTitleTextView = (AppCompatTextView) getSupportActionBar().getCustomView().findViewById(R.id.actionTitleTextView);
        homeBack = (ImageView) getSupportActionBar().getCustomView().findViewById(R.id.backImageView);
        uploadImageIView = (ImageView) getSupportActionBar().getCustomView().findViewById(R.id.addPersonImageView);
        homeBack.setVisibility(View.VISIBLE);
        uploadImageIView.setImageDrawable(getResources().getDrawable(R.drawable.ic_add_a_photo));
        uploadImageIView.setVisibility(View.VISIBLE);
        actionTitleTextView.setText("Member Attendance");

        cDB = new CordOfflineDBase(this);

        initialization();
        defaultConfiguration();
    }


    private void initialization() {


        // To get User Id and Roll Id
        String rId = AppSettings.getInstance().getValue(this, ApConstants.kROLE_ID, ApConstants.kROLE_ID);
        String uId = AppSettings.getInstance().getValue(this, ApConstants.kUSER_ID, ApConstants.kUSER_ID);
        if (!rId.equalsIgnoreCase("kROLE_ID")) {
            roleId = rId;
        }
        if (!uId.equalsIgnoreCase("kUSER_ID")) {
            userID = uId;
        }

        String title = getIntent().getStringExtra("title");
        schId = getIntent().getStringExtra("schId");
        attendType = getIntent().getStringExtra("attendType");
        grpId = getIntent().getStringExtra("grpId");
        attendDate = getIntent().getStringExtra("attendDate");

        searchEText = (EditText)findViewById(R.id.searchEText);
        searchIView = (ImageView)findViewById(R.id.searchIView);
        searchMobText = (EditText)findViewById(R.id.searchMobText);
        searchMobView = (ImageView)findViewById(R.id.searchMobView);
        submitButton = (Button) findViewById(R.id.submitButton);
        gTitleTextView = (TextView) findViewById(R.id.gTitleTextView);
        gTitleTextView.setText(title);

        atdRecyclerView = (RecyclerView) findViewById(R.id.atdRecyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        atdRecyclerView.setLayoutManager(linearLayoutManager);

    }


    @Override
    public void onBackPressed() {
        if (presentPeopleList.length() > 0) {
            forBackAskUserPermission();
        } else {
            super.onBackPressed();
        }
    }


    private void defaultConfiguration() {

        onlineStatus = AppSettings.getInstance().getValue(GruMemAttendanceActivity.this, ApConstants.kONLINE_STATUS, ApConstants.kONLINE_STATUS);

        searchEText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchEText.setCursorVisible(true);
            }
        });



        searchEText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                GruMemAttendanceActivity.this.adaptorGruMemAttendance.filter(s.toString());
            }
        });

        searchMobText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchMobText.setCursorVisible(true);
            }
        });



        searchMobText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                GruMemAttendanceActivity.this.adaptorGruMemAttendance.filterByMob(s.toString());
            }
        });

        if (onlineStatus.equalsIgnoreCase(ApConstants.kONLINE)) {
            getGroupMemberDetailList();
        } else {

            /**** FOR OFFLINE */

            if (attendType.equalsIgnoreCase("Other")) {      // For Other member
                JSONArray otherGrpMemArray = cDB.getOtherMemByEventId(schId);
                if (otherGrpMemArray.length() > 0) {
                    adaptorGruMemAttendance = new AdaptorGruMemAttendance(this, otherGrpMemArray, this, attendType, grpId, schId);
                    atdRecyclerView.setAdapter(adaptorGruMemAttendance);
                }
            } else {
                // For Remaining member except other
                JSONArray gruMemArrayList = cDB.getGrpMemListByGrpId(grpId);
                if (gruMemArrayList.length() > 0) {
                    adaptorGruMemAttendance = new AdaptorGruMemAttendance(this, gruMemArrayList, this, attendType, grpId, schId);
                    atdRecyclerView.setAdapter(adaptorGruMemAttendance);
                } else {
                    UIToastMessage.show(this, "Group Member list not found");
                }
            }
        }


        // For Action Bar
        homeBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (presentPeopleList.length() > 0) {
                    forBackAskUserPermission();
                } else {
                    finish();
                }
            }
        });


        uploadImageIView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GruMemAttendanceActivity.this, AttendImageUploadActivity.class);
                intent.putExtra("event_date", attendDate);
                intent.putExtra("schId", schId);
                startActivity(intent);
            }
        });


        // Other
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (onlineStatus.equalsIgnoreCase(ApConstants.kONLINE)) {
                    saveButtonAction();
                } else {
                    /**** FOR OFFLINE */
                    saveOffButtonAction();
                }

            }
        });

    }


    @Override
    public void onMultiRecyclerViewItemClick(int i, Object o) {
        // getPresentPeople();

        if (i == 1) {

            presentPeopleList = "";
            try {
                presentMemArray = new JSONArray();

                if (adaptorGruMemAttendance != null) {

                    for (int m = 0; m < adaptorGruMemAttendance.mJSONArray.length(); m++) {
                        JSONObject jsonObject = adaptorGruMemAttendance.mJSONArray.getJSONObject(m);
                        if (jsonObject.getInt("is_selected") == 1) {
                            presentMemArray.put(jsonObject);
                        }
                    }
                }

                presentPeopleList = AppUtility.getInstance().componentSeparatedByCommaJSONArray(presentMemArray, "id");
                //presentPeopleType = AppUtility.getInstance().componentSeparatedByCommaJSONArray(jsonArray, "type");

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }




    // To get VCRMC Member Detail list for attendance
    private void getGroupMemberDetailList() {

        try {

            // To get Detail
            JSONObject param = new JSONObject();
            param.put("group_id", grpId);
            param.put("type", attendType);
            param.put("schedule_id", schId);
            param.put("attendance_date", attendDate);
            param.put("api_key", ApConstants.kAUTHORITY_KEY);

            RequestBody requestBody = AppUtility.getInstance().getRequestBody(param.toString());
            AppinventorApi api = new AppinventorApi(this, APIServices.BASE_URL, "", ApConstants.kMSG, true);
            Retrofit retrofit = api.getRetrofitInstance();
            APIRequest apiRequest = retrofit.create(APIRequest.class);
            Call<JsonObject> responseCall = apiRequest.cordEventGroupMemListRequest(requestBody);

            DebugLog.getInstance().d("group_Member_list_param=" + responseCall.request().toString());
            DebugLog.getInstance().d("group_Member_list_param=" + AppUtility.getInstance().bodyToString(responseCall.request()));

            api.postRequest(responseCall, this, 1);


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // For Offline save attendance
    private void saveOffButtonAction() {

        boolean result = false;

        if (presentMemArray != null) {

            // To remove Already inserted attendance for selected group against date
            if (cDB.isGroupAttendanceExist(grpId,attendDate)) {
                // To override previous attendance
                cDB.removeAttendanceBySchIdDateGrpId(attendDate,grpId);
            }

            // To add insert attendance for selected group against date
            for (int p = 0; p < presentMemArray.length(); p++) {
                try {
                    JSONObject presentJSON = presentMemArray.getJSONObject(p);
                    String memId = presentJSON.getString("id");
                    String mob = presentJSON.getString("mobile");

                    String time = AppUtility.getInstance().getCurrentSyncDateTime();

                    result = cDB.insertAttendanceDetail(schId, grpId, attendType, attendDate, userID, roleId, memId, mob, time);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            if (result) {
                UIToastMessage.show(this, "Attendance saved successfully");
                finish();
            } else {
                UIToastMessage.show(this, "Please Try Again");
            }

        } else {
            UIToastMessage.show(this, "Please select attendance");
        }

    }


    // For Submit attendance
    private void saveButtonAction() {

        if (presentPeopleList.isEmpty()) {
            UIToastMessage.show(this, "Please select attendance");
        } else {

            try {

                // To get Detail
                JSONObject param = new JSONObject();
                param.put("user_id", userID);
                param.put("role_id", roleId);
                param.put("group_id", grpId);
                param.put("attendance_type", attendType);
                param.put("attendance_date", attendDate);
                param.put("schedule_event_id", schId);
                param.put("member_id", presentPeopleList);
                param.put("api_key", ApConstants.kAUTHORITY_KEY);

                RequestBody requestBody = AppUtility.getInstance().getRequestBody(param.toString());
                AppinventorApi api = new AppinventorApi(this, APIServices.BASE_URL, "", ApConstants.kMSG, true);
                Retrofit retrofit = api.getRetrofitInstance();
                APIRequest apiRequest = retrofit.create(APIRequest.class);
                Call<JsonObject> responseCall = apiRequest.submitMemAttendOfDayRequest(requestBody);

                DebugLog.getInstance().d("member_attendance_request_param=" + responseCall.request().toString());
                DebugLog.getInstance().d("member_attendance_request_param=" + AppUtility.getInstance().bodyToString(responseCall.request()));

                api.postRequest(responseCall, this, 3);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }


    private void getPresentPeopleList(JSONArray gruMemArrayList) {

          presentPeopleList = "";
        try {

            presentMemArray = new JSONArray();

            if (gruMemArrayList != null) {

                for (int m = 0; m < gruMemArrayList.length(); m++) {
                    JSONObject jsonObject = gruMemArrayList.getJSONObject(m);
                    if (jsonObject.getInt("is_selected") == 1) {
                        presentMemArray.put(jsonObject);
                    }
                }
            }

            presentPeopleList = AppUtility.getInstance().componentSeparatedByCommaJSONArray(presentMemArray, "id");

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    // To take user permission
    // for going back because of data loss

    private void forBackAskUserPermission() {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Are you sure to go back without saving data? \nYour data will be loss.");
        alertDialogBuilder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
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
    public void onResponse(JSONObject jsonObject, int i) {

        try {

            if (jsonObject != null) {
                // Schedule Response for PS HRD
                if (i == 1) {
                    ResponseModel responseModel = new ResponseModel(jsonObject);

                    if (responseModel.isStatus()) {
                        JSONArray gruMemArrayList = responseModel.getData();
                        if (gruMemArrayList.length() > 0) {
                            adaptorGruMemAttendance = new AdaptorGruMemAttendance(this, gruMemArrayList, this, attendType, grpId, schId);
                            atdRecyclerView.setAdapter(adaptorGruMemAttendance);
                            getPresentPeopleList(gruMemArrayList);
                        }

                    } else {
                        UIToastMessage.show(this, responseModel.getMsg());
                    }
                }


                // Attendance submit response
                if (i == 3) {
                    ResponseModel responseModel = new ResponseModel(jsonObject);

                    if (responseModel.isStatus()) {
                        //UIToastMessage.show(this, responseModel.getMsg());
                        Toast.makeText(this, ""+responseModel.getMsg(), Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                       // UIToastMessage.show(this, responseModel.getMsg());
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
