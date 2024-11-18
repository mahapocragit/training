package in.gov.pocra.training.activity.common.co_coordinator_list;

import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import in.co.appinventor.services_api.api.AppinventorApi;
import in.co.appinventor.services_api.app_util.AppUtility;
import in.co.appinventor.services_api.debug.DebugLog;
import in.co.appinventor.services_api.listener.ApiCallbackCode;
import in.co.appinventor.services_api.listener.ApiJSONObjCallback;
import in.co.appinventor.services_api.settings.AppSettings;
import in.co.appinventor.services_api.widget.UIToastMessage;
import in.gov.pocra.training.R;
import in.gov.pocra.training.model.online.ResponseModel;
import in.gov.pocra.training.model.online.VCRMCMemberDetailModel;
import in.gov.pocra.training.util.ApConstants;
import in.gov.pocra.training.web_services.APIRequest;
import in.gov.pocra.training.web_services.APIServices;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Retrofit;


public class AddEditCoCoordinatorActivity extends AppCompatActivity implements ApiCallbackCode, ApiJSONObjCallback {

    private ImageView homeBack;
    private EditText fNameEditText;
    private EditText mNameEditText;
    private EditText lNameEditText;
    private EditText mobEditText;
    private RadioGroup genderRGroup;
    private RadioButton maleRButton;
    private RadioButton femaleRButton;
    private RadioButton tranRButton;

    private Button registerButton;
    private String gender = "";
    private String actionType = "Add";
    private String memberDetail = "";
    private String roleId;
    private String userID;
    private String memId = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_res_person);

        /* ***For actionbar title in center*** */
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.custom_actionbar_layout);
        AppCompatTextView actionTitleTextView = (AppCompatTextView) getSupportActionBar().getCustomView().findViewById(R.id.actionTitleTextView);
        homeBack = (ImageView) getSupportActionBar().getCustomView().findViewById(R.id.backImageView);
        homeBack.setVisibility(View.VISIBLE);
        actionTitleTextView.setText(getResources().getString(R.string.title_add_edit_co_coordinator));

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


        fNameEditText = (EditText) findViewById(R.id.fNameEditText);
        mNameEditText = (EditText) findViewById(R.id.mNameEditText);
        lNameEditText = (EditText) findViewById(R.id.lNameEditText);
        mobEditText = (EditText) findViewById(R.id.mobEditText);
        genderRGroup = (RadioGroup) findViewById(R.id.genderRGroup);
        maleRButton = (RadioButton) findViewById(R.id.maleRButton);
        femaleRButton = (RadioButton) findViewById(R.id.femaleRButton);
        tranRButton = (RadioButton) findViewById(R.id.tranRButton);

        registerButton = (Button) findViewById(R.id.registerButton);


    }


    private void defaultConfiguration() {
        homeBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        // To set member Detail
        if (actionType.equalsIgnoreCase("update")) {
            registerButton.setText("Update");
            setMemberDetail(memberDetail);
        }

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (actionType.equalsIgnoreCase("update")) {
                    updateButtonAction();
                } else {
                    registerButtonAction();

                }
            }
        });

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

    }


    private void setMemberDetail(String membDetail) {

        try {
            JSONObject jsonObject = new JSONObject(membDetail);
            if (jsonObject.length() > 0) {
                VCRMCMemberDetailModel vcrmcMemberDetailModel = new VCRMCMemberDetailModel(jsonObject);

                memId = vcrmcMemberDetailModel.getId();
                String fName = vcrmcMemberDetailModel.getFname();
                String mName = vcrmcMemberDetailModel.getMname();
                String lName = vcrmcMemberDetailModel.getLname();
                String fullName = fName + " " + mName + " " + lName;
                String mobile = vcrmcMemberDetailModel.getMobile();
                gender = vcrmcMemberDetailModel.getGender();


                fNameEditText.setText(fName);
                mNameEditText.setText(mName);
                lNameEditText.setText(lName);
                mobEditText.setText(mobile);

                if (gender.equalsIgnoreCase("1")) {
                    maleRButton.setChecked(true);
                } else if (gender.equalsIgnoreCase("2")) {
                    femaleRButton.setChecked(true);
                } else if (gender.equalsIgnoreCase("3")) {
                    tranRButton.setChecked(true);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }




    private void updateButtonAction() {
        String fName = fNameEditText.getText().toString().trim();
        String mName = mNameEditText.getText().toString().trim();
        String lName = lNameEditText.getText().toString().trim();
        String mobNum = mobEditText.getText().toString().trim();

        if (fName.isEmpty()) {
            fNameEditText.setError("Enter first name");
            fNameEditText.requestFocus();
        } else if (mName.isEmpty()) {
            fNameEditText.setError(null);
            mNameEditText.setError("Enter middle name");
            mNameEditText.requestFocus();
        } else if (lName.isEmpty()) {
            mNameEditText.setError(null);
            lNameEditText.setError("Enter full name");
            lNameEditText.requestFocus();
        } else if (mobNum.isEmpty()) {
            lNameEditText.setError(null);
            mobEditText.setError("Enter mobile number");
            mobEditText.requestFocus();
        } else if (!AppUtility.getInstance().isValidCallingNumber(mobNum)) {
            mobEditText.setError(null);
            mobEditText.setError("Enter valid mobile number");
            mobEditText.requestFocus();
        } else if (mobNum.length() < 10) {
            mobEditText.setError(null);
            mobEditText.setError("Enter valid mobile number");
            mobEditText.requestFocus();
        } else if (gender.isEmpty()) {
            mobEditText.setError(null);
            UIToastMessage.show(this, "Select gender");
        } else {
            mobEditText.setError(null);

            try {

                // To get Detail
                JSONObject param = new JSONObject();
                param.put("created_by", userID);
                param.put("role_id", roleId);
                param.put("first_name", fName);
                param.put("middle_name", mName);
                param.put("last_name", lName);
                param.put("mobile", mobNum);
                param.put("api_key", ApConstants.kAUTHORITY_KEY); // Type and designation having same Id. (According API developer)
                param.put("gender", gender);


                RequestBody requestBody = AppUtility.getInstance().getRequestBody(param.toString());
                AppinventorApi api = new AppinventorApi(this, APIServices.TR_API_URL, "", ApConstants.kMSG, true);
                Retrofit retrofit = api.getRetrofitInstance();
                APIRequest apiRequest = retrofit.create(APIRequest.class);
                Call<JsonObject> responseCall = apiRequest.addEditVCRMCMemberDetailRequest(requestBody);

                DebugLog.getInstance().d("VCRMC_Member_Detail_attendance_list_param=" + responseCall.request().toString());
                DebugLog.getInstance().d("VCRMC_Member_Detail_attendance_list_param=" + AppUtility.getInstance().bodyToString(responseCall.request()));

                api.postRequest(responseCall, this, 2);


            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }


    private void registerButtonAction() {

        String fName = fNameEditText.getText().toString().trim();
        String mName = mNameEditText.getText().toString().trim();
        String lName = lNameEditText.getText().toString().trim();
        String mobNum = mobEditText.getText().toString().trim();


        if (fName.isEmpty()) {
            fNameEditText.setError("Enter first name");
            fNameEditText.requestFocus();
        } /*else if (mName.isEmpty()) {
            fNameEditText.setError(null);
            mNameEditText.setError("Enter middle name");
            mNameEditText.requestFocus();
        } */else if (lName.isEmpty()) {
            mNameEditText.setError(null);
            lNameEditText.setError("Enter last name");
            lNameEditText.requestFocus();
        } else if (mobNum.isEmpty()) {
            lNameEditText.setError(null);
            mobEditText.setError("Enter mobile number");
            mobEditText.requestFocus();
        } else if (!AppUtility.getInstance().isValidCallingNumber(mobNum)) {
            mobEditText.setError(null);
            mobEditText.setError("Enter valid mobile number");
            mobEditText.requestFocus();
        } else if (mobNum.length() < 10) {
            mobEditText.setError(null);
            mobEditText.setError("Enter valid mobile number");
            mobEditText.requestFocus();
        } else if (gender.isEmpty()) {
            mobEditText.setError(null);
            UIToastMessage.show(this, "Select gender");
        } else {

            try {

                // To get Detail
                JSONObject param = new JSONObject();
                param.put("created_by", userID);
                param.put("role_id", roleId);
                param.put("first_name", fName);
                param.put("middle_name", mName);
                param.put("last_name", lName);
                param.put("mobile", mobNum);
                param.put("api_key", ApConstants.kAUTHORITY_KEY); // Type and designation having same Id. (According API developer)
                param.put("gender", gender);

                RequestBody requestBody = AppUtility.getInstance().getRequestBody(param.toString());
                AppinventorApi api = new AppinventorApi(this, APIServices.BASE_URL, "", ApConstants.kMSG, true);
                Retrofit retrofit = api.getRetrofitInstance();
                APIRequest apiRequest = retrofit.create(APIRequest.class);
                Call<JsonObject> responseCall = apiRequest.addResPerRequest(requestBody);

                DebugLog.getInstance().d("Add_Resource_person_param=" + responseCall.request().toString());
                DebugLog.getInstance().d("Add_Resource_person_param=" + AppUtility.getInstance().bodyToString(responseCall.request()));

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
                DebugLog.getInstance().d("onResponse="+jsonObject);

                if (i == 1) {
                    ResponseModel responseModel = new ResponseModel(jsonObject);

                    if (responseModel.isStatus()) {
                        UIToastMessage.show(this, responseModel.getMsg());
                        JSONObject jsonObject1 = jsonObject.getJSONArray("data").getJSONObject(0);

                        jsonObject1.put("role_id", "0");
                        JSONArray jsonArray = new JSONArray();
                        jsonArray.put(jsonObject1);
                        AppSettings.getInstance().setValue(this, ApConstants.kS_CO_COORDINATOR, jsonArray.toString());

                        finish();
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
    public void onFailure(Throwable throwable, int i) {

    }

    @Override
    public void onFailure(Object o, Throwable throwable, int i) {

    }
}
