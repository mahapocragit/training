package in.gov.pocra.training.activity.coordinator.event_session;

import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import in.co.appinventor.services_api.api.AppinventorApi;
import in.co.appinventor.services_api.app_util.AppUtility;
import in.co.appinventor.services_api.debug.DebugLog;
import in.co.appinventor.services_api.listener.ApiCallbackCode;
import in.co.appinventor.services_api.settings.AppSettings;
import in.co.appinventor.services_api.widget.UIToastMessage;
import in.gov.pocra.training.R;
import in.gov.pocra.training.activity.ca.add_edit_event_ca.AddEditEventCaActivity;
import in.gov.pocra.training.model.online.ResponseModel;
import in.gov.pocra.training.util.ApConstants;
import in.gov.pocra.training.util.ApUtil;
import in.gov.pocra.training.util.TimePickerCallbackListener;
import in.gov.pocra.training.web_services.APIRequest;
import in.gov.pocra.training.web_services.APIServices;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Retrofit;

public class AddSessionActivity extends AppCompatActivity implements ApiCallbackCode, TimePickerCallbackListener {

    private ImageView homeBack;
    private String roleId;
    private String userID;

    private String eventDate;
    private String schId;

    private RelativeLayout sTimeRLayout;
    private TextView sTimeTView;
    private RelativeLayout eTimeRLayout;
    private TextView eTimeTView;
    private TextView sessionDescEText;

    private EditText fNameEditText;
    private EditText mNameEditText;
    private EditText lNameEditText;
    private EditText mobEditText;
    private RadioGroup genderRGroup;
    private RadioButton maleRButton;
    private RadioButton femaleRButton;
    private RadioButton tranRButton;

    private Button sSubmitButton;

    private String gender = "";
    private int startHour = 0;
    private int startMinuets = 0;
    private int endHour = 0;
    private int endMinuets = 0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_session);


        /* ** For actionbar title in center ***/
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.attendance_actionbar_layout);
        AppCompatTextView actionTitleTextView = (AppCompatTextView) getSupportActionBar().getCustomView().findViewById(R.id.actionTitleTextView);
        homeBack = (ImageView) getSupportActionBar().getCustomView().findViewById(R.id.backImageView);
        // uploadImageIView = (ImageView) getSupportActionBar().getCustomView().findViewById(R.id.addPersonImageView);
        homeBack.setVisibility(View.VISIBLE);
        // uploadImageIView.setImageDrawable(getResources().getDrawable(R.drawable.ic_menu_camera));
        // uploadImageIView.setVisibility(View.VISIBLE);
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

        // Get Event Date and id
        eventDate = getIntent().getStringExtra("eventDate");
        schId = getIntent().getStringExtra("schId");



        sTimeRLayout = (RelativeLayout)findViewById(R.id.sTimeRLayout);
        sTimeTView = (TextView)findViewById(R.id.sTimeTView);
        eTimeRLayout = (RelativeLayout)findViewById(R.id.eTimeRLayout);
        eTimeTView = (TextView)findViewById(R.id.eTimeTView);
        sessionDescEText = (TextView)findViewById(R.id.sessionDescEText);

        // Resource Person Detail
        fNameEditText = (EditText) findViewById(R.id.fNameEditText);
        mNameEditText = (EditText) findViewById(R.id.mNameEditText);
        lNameEditText = (EditText) findViewById(R.id.lNameEditText);
        mobEditText = (EditText) findViewById(R.id.mobEditText);
        genderRGroup = (RadioGroup) findViewById(R.id.genderRGroup);
        maleRButton = (RadioButton) findViewById(R.id.maleRButton);
        femaleRButton = (RadioButton) findViewById(R.id.femaleRButton);
        tranRButton = (RadioButton) findViewById(R.id.tranRButton);

        sSubmitButton = (Button)findViewById(R.id.sSubmitButton);




    }

    private void defaultConfiguration() {

        genderRGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {   // Male =1, Female = 2, Transgender = 3
                if (checkedId == R.id.maleRButton) {
                    gender = "1";
                } else if (checkedId == R.id.femaleRButton) {
                    gender = "2";
                } else if (checkedId == R.id.tranRButton) {
                    gender = "3";
                }
            }
        });


        sTimeRLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ApUtil.showTimePicker(AddSessionActivity.this,sTimeTView,AddSessionActivity.this);
            }
        });

        eTimeRLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (startHour > 0 || startMinuets > 0) {
//                    String strStartTime= String.valueOf(sTimeTView.getText());
//                    String strEndTime= String.valueOf(eTimeTView.getText());
//                    Log.d("MSK",""+strStartTime);
//                    Log.d("MSK",""+strEndTime);

                        ApUtil.showTimeAgainstStartTimePicker12(AddSessionActivity.this, eTimeTView, startHour, startMinuets, AddSessionActivity.this);

                }else {
                   // UIToastMessage.show(AddSessionActivity.this,"Select start time");
                    Toast.makeText(AddSessionActivity.this, "Select start time", Toast.LENGTH_SHORT).show();

                }
            }
        });


        sSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sSubmitButtonAction();
            }
        });

    }


    @Override
    public void onTimeSelected(TextView textView, int hour, int min, String amOrPm) {

        if (textView == sTimeTView){
            /*if (hour == 0){
                startHour = 12;
            }else {
                startHour = hour;
            }*/
            startHour = hour;
            startMinuets = min;
            sTimeTView.setError(null);
            eTimeTView.setText("");
        }

        if (textView == eTimeTView){
            endHour = hour;
            endMinuets = min;
            eTimeTView.setError(null);
        }

    }



    private void sSubmitButtonAction() {

        String sHour = String.valueOf(startHour);
        String sMinuets = String.valueOf(startMinuets);
        String startTime = "";
        if (startHour > 0){
            startTime = sHour+":"+sMinuets;
        }

        String eHour = String.valueOf(endHour);
        String eMinutes = String.valueOf(endMinuets);
        String endTime = "";
        if (endHour > 0){
            endTime = eHour+":"+eMinutes;
        }

        String sessionDesc = sessionDescEText.getText().toString().trim();
        String fName = fNameEditText.getText().toString().trim();
        String mName = mNameEditText.getText().toString().trim();
        String lName = lNameEditText.getText().toString().trim();
        String mobNum = mobEditText.getText().toString().trim();

        if (startTime.isEmpty()) {
            sTimeTView.setError("Select session start time");
            //UIToastMessage.show(this,"Select session start time");
            Toast.makeText(this, "Select session start time", Toast.LENGTH_SHORT).show();
            sTimeTView.requestFocus();
        } else if (endTime.isEmpty()) {
            sTimeTView.setError(null);
            eTimeTView.setError("Select session end time");
            //UIToastMessage.show(this,"Select session end time");
            Toast.makeText(this, "Select session end time", Toast.LENGTH_SHORT).show();
            eTimeTView.requestFocus();
        } else if (sessionDesc.isEmpty()) {
            eTimeTView.setError(null);
            sessionDescEText.setError("Enter session description");
            //UIToastMessage.show(this,"Enter session description");
            Toast.makeText(this, "Enter session description", Toast.LENGTH_SHORT).show();
            sessionDescEText.requestFocus();
        } else if (fName.isEmpty()) {
            sessionDescEText.setError(null);
            fNameEditText.setError("Enter first name");
            // UIToastMessage.show(this,"Enter first name");
            fNameEditText.requestFocus();
        } /*else if (mName.isEmpty()) {
            fNameEditText.setError(null);
            mNameEditText.setError("Enter middle name");
            mNameEditText.requestFocus();
        } */else if (lName.isEmpty()) {
            //mNameEditText.setError(null);
            fNameEditText.setError(null);
            lNameEditText.setError("Enter last name");
            // UIToastMessage.show(this,"Enter last name");
            lNameEditText.requestFocus();
        } else if (mobNum.isEmpty()) {
            lNameEditText.setError(null);
            mobEditText.setError("Enter mobile number");
            // UIToastMessage.show(this,"Enter mobile number");
            mobEditText.requestFocus();
        } else if (!AppUtility.getInstance().isValidCallingNumber(mobNum)) {
            mobEditText.setError(null);
            mobEditText.setError("Enter valid mobile number");
            // UIToastMessage.show(this,"Enter valid mobile number");
            mobEditText.requestFocus();
        } else if (mobNum.length() < 10) {
            mobEditText.setError(null);
            mobEditText.setError("Enter valid mobile number");
            // UIToastMessage.show(this,"Enter valid mobile number");
            mobEditText.requestFocus();
        } else if (gender.isEmpty()) {
            mobEditText.setError(null);
//            UIToastMessage.show(this, "Select gender");
            Toast.makeText(this, "Select gender", Toast.LENGTH_SHORT).show();
            // UIToastMessage.show(this,"Select gender");
        } else {


            try {

                // To get Detail
                JSONObject param = new JSONObject();
                param.put("schedule_event_id", schId);
                param.put("event_date", eventDate);
                param.put("start_time", startTime);
                param.put("end_time", endTime);
                param.put("session_desc", sessionDesc);
                param.put("first_name", fName);
                param.put("middle_name", mName);
                param.put("last_name", lName);
                param.put("mobile", mobNum);
                param.put("gender", gender);
                param.put("created_by", userID);
                param.put("role_id", roleId);
                param.put("api_key", ApConstants.kAUTHORITY_KEY); // Type and designation having same Id. (According API developer)


                RequestBody requestBody = AppUtility.getInstance().getRequestBody(param.toString());
                AppinventorApi api = new AppinventorApi(this, APIServices.BASE_URL, "", ApConstants.kMSG, true);
                Retrofit retrofit = api.getRetrofitInstance();
                APIRequest apiRequest = retrofit.create(APIRequest.class);
                Call<JsonObject> responseCall = apiRequest.addSessionRequest(requestBody);

                DebugLog.getInstance().d("Add_Session_param=" + responseCall.request().toString());
                DebugLog.getInstance().d("Add_Session_param=" + AppUtility.getInstance().bodyToString(responseCall.request()));

                api.postRequest(responseCall, this, 1);


            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }



    @Override
    public void onResponse(JSONObject jsonObject, int i) {


        try {

            if (jsonObject != null) {


                if (i == 1) {
                    ResponseModel responseModel = new ResponseModel(jsonObject);
                    if (responseModel.isStatus()) {
                        //UIToastMessage.show(this, responseModel.getMsg());
                        Toast.makeText(this, ""+responseModel.getMsg(), Toast.LENGTH_SHORT).show();
                        finish();
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
