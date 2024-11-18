package in.gov.pocra.training.activity.ca.person_list;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.AppCompatTextView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import in.co.appinventor.services_api.api.AppinventorApi;
import in.co.appinventor.services_api.app_util.AppUtility;
import in.co.appinventor.services_api.debug.DebugLog;
import in.co.appinventor.services_api.listener.ApiCallbackCode;
import in.co.appinventor.services_api.settings.AppSettings;
import in.gov.pocra.training.R;
import in.gov.pocra.training.model.online.ResponseModel;
import in.gov.pocra.training.util.ApConstants;
import in.gov.pocra.training.web_services.APIRequest;
import in.gov.pocra.training.web_services.APIServices;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Retrofit;

public class AddResourcePersonActivity extends AppCompatActivity implements ApiCallbackCode {

    private ImageView homeBack;
    private String roleId;
    private String userID;
    private String subDivisionId = "";
    private String distId = "";

    private EditText fNameEditText;
    private EditText mNameEditText;
    private EditText lNameEditText;
    private EditText mobEditText;
    private RadioGroup genderRGroup;
    private String genderId = "";
    private EditText professionEText;
    private Button registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_resource_person);

        /** For actionbar title in center */
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.attendance_actionbar_layout);
        AppCompatTextView actionTitleTextView = (AppCompatTextView) getSupportActionBar().getCustomView().findViewById(R.id.actionTitleTextView);
        homeBack = (ImageView) getSupportActionBar().getCustomView().findViewById(R.id.backImageView);
        homeBack.setVisibility(View.VISIBLE);
        actionTitleTextView.setText(getResources().getString(R.string.title_resource_person));

        initialization();
        defaultConfiguration();
        eventListener();
    }

    private void initialization() {

        // To get User Id and Roll Id
        String rId = AppSettings.getInstance().getValue(this, ApConstants.kROLE_ID, ApConstants.kROLE_ID);
        if (!rId.equalsIgnoreCase("kROLE_ID")) {
            roleId = rId;
        }
        String uId = AppSettings.getInstance().getValue(this, ApConstants.kUSER_ID, ApConstants.kUSER_ID);
        if (!uId.equalsIgnoreCase("kUSER_ID")) {
            userID = uId;
        }

        subDivisionId = getIntent().getStringExtra("subDivId");
        distId = getIntent().getStringExtra("distId");


        fNameEditText = findViewById(R.id.fNameEditText);
        mNameEditText = findViewById(R.id.mNameEditText);
        lNameEditText = findViewById(R.id.lNameEditText);
        mobEditText = findViewById(R.id.mobEditText);
        professionEText = findViewById(R.id.professionEText);
        genderRGroup = findViewById(R.id.genderRGroup);
        registerButton = (Button) findViewById(R.id.registerButton);
    }

    private void defaultConfiguration() {
        // For Action Bar
        homeBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }

    private void eventListener() {
        genderRGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i==R.id.maleRButton){
                    genderId = "1";
                }else if (i==R.id.femaleRButton){
                    genderId = "1";
                }else if (i==R.id.tranRButton){
                    genderId = "1";
                }
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerButtonAction();
            }
        });
    }

    private void registerButtonAction() {
        String fName = fNameEditText.getText().toString().trim();
        String mName = mNameEditText.getText().toString().trim();
        String lName = lNameEditText.getText().toString().trim();
        String mobile = mobEditText.getText().toString().trim();
        String profession = professionEText.getText().toString().trim();

        if (fName.equalsIgnoreCase("")) {
            Toast.makeText(this, "Enter first name", Toast.LENGTH_SHORT).show();
//            UIToastMessage.show(this, "Enter first name");
        }  else if (lName.equalsIgnoreCase("")) {
            Toast.makeText(this, "Enter Last name", Toast.LENGTH_SHORT).show();
//            UIToastMessage.show(this, "Enter last name");
        } else if (mobile.equalsIgnoreCase("")) {
            Toast.makeText(this, "Enter Mobile number", Toast.LENGTH_SHORT).show();
//            UIToastMessage.show(this, "Enter mobile number");
        } else if (!AppUtility.getInstance().isValidCallingNumber(mobile)) {
            Toast.makeText(this, "Enter valid mobile", Toast.LENGTH_SHORT).show();
//            UIToastMessage.show(this, "Enter valid mobile");
        } else if (genderId.equalsIgnoreCase("")) {
            Toast.makeText(this, "Select gender", Toast.LENGTH_SHORT).show();
//            UIToastMessage.show(this, "Select gender");
        } else if (profession.equalsIgnoreCase("")) {
            Toast.makeText(this, "Enter Profession", Toast.LENGTH_SHORT).show();
//            UIToastMessage.show(this, "Enter Profession");
        } else {

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("user_id", userID);
                jsonObject.put("first_name", fName);
                jsonObject.put("middle_name", mName);
                jsonObject.put("last_name", lName);
                jsonObject.put("mobile", mobile);
                jsonObject.put("gender", genderId);
                jsonObject.put("profession", profession);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            RequestBody requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString());
            AppinventorApi api = new AppinventorApi(this, APIServices.OTHER_BASE_URL, "", ApConstants.kMSG, true);
            Retrofit retrofit = api.getRetrofitInstance();
            APIRequest apiRequest = retrofit.create(APIRequest.class);
            Call<JsonObject> responseCall = apiRequest.addCaResourcePRequest(requestBody);

            DebugLog.getInstance().d("Village_list_param=" + responseCall.request().toString());
            DebugLog.getInstance().d("Village_list_param=" + AppUtility.getInstance().bodyToString(responseCall.request()));

            api.postRequest(responseCall, this, 1);

        }
    }


    @Override
    public void onResponse(JSONObject jsonObject, int i) {
        if (jsonObject != null) {

            if (i == 1) {
                ResponseModel responseModel = new ResponseModel(jsonObject);

                if (responseModel.isStatus()) {
                    Toast.makeText(this, "Resource Person Added Successfully!!", Toast.LENGTH_SHORT).show();
//                    UIToastMessage.show(this, "Resource Person Added Successfully!!");
                    finish();
                } else {
                    Toast.makeText(this, ""+responseModel.getMsg(), Toast.LENGTH_SHORT).show();
//                    UIToastMessage.show(this, responseModel.getMsg());
                }
            }

        }
    }

    @Override
    public void onFailure(Object o, Throwable throwable, int i) {

    }
}