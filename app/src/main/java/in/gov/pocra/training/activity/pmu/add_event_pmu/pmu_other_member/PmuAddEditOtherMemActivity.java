package in.gov.pocra.training.activity.pmu.add_event_pmu.pmu_other_member;

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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import in.co.appinventor.services_api.api.AppinventorIncAPI;
import in.co.appinventor.services_api.app_util.AppUtility;
import in.co.appinventor.services_api.listener.ApiCallbackCode;
import in.co.appinventor.services_api.listener.ApiJSONObjCallback;
import in.co.appinventor.services_api.settings.AppSettings;
import in.co.appinventor.services_api.widget.UIToastMessage;
import in.gov.pocra.training.R;
import in.gov.pocra.training.model.online.ResponseModel;
import in.gov.pocra.training.model.online.VCRMCMemberDetailModel;
import in.gov.pocra.training.util.ApConstants;
import in.gov.pocra.training.web_services.APIServices;


public class PmuAddEditOtherMemActivity extends AppCompatActivity implements  ApiCallbackCode, ApiJSONObjCallback {

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
    private String socialCategory = "";
    private String actionType = "Add";
    private JSONArray vcrmcSocCatArray = null;
    private String memberDetail = "";
    private String roleId;
    private String userID;
    private String memId = "";
    private String memberType;
    private String vcrmcGpId;
    private String schId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pmu_add_edit_other_mem);

        /* ***For actionbar title in center*** */
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.custom_actionbar_layout);
        AppCompatTextView actionTitleTextView = (AppCompatTextView) getSupportActionBar().getCustomView().findViewById(R.id.actionTitleTextView);
        homeBack = (ImageView) getSupportActionBar().getCustomView().findViewById(R.id.backImageView);
        homeBack.setVisibility(View.VISIBLE);
        actionTitleTextView.setText(getResources().getString(R.string.title_add_edit_other_mem));

        initialization();
        defaultConfiguration();

    }

    private void initialization() {

//        memberType = getIntent().getStringExtra("memberType");
//        actionType = getIntent().getStringExtra("type");
//        memberDetail = getIntent().getStringExtra("memberData");
//        vcrmcGpId = getIntent().getStringExtra("gp_id");
//        schId = getIntent().getStringExtra("schId");

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

        getSocialCategoryList();

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
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.maleRButton) {
                    gender = "M";
                } else if (checkedId == R.id.femaleRButton) {
                    gender = "F";
                } else if (checkedId == R.id.tranRButton) {
                    gender = "T";
                }
            }
        });
    }



    private void getSocialCategoryList() {

        // It is GET Method
        AppinventorIncAPI api = new AppinventorIncAPI(this, APIServices.API_URL, "", ApConstants.kMSG, true);
        api.getRequestData(APIServices.GET_VCRMC_SOCIAL_LIST, this, 1);

        // It id post method
        /*JSONObject param = new JSONObject();
        RequestBody requestBody = AppUtility.getInstance().getRequestBody(param.toString());
        AppinventorApi api = new AppinventorApi(this, APIServices.TR_API_URL, "", ApConstants.kMSG, true);
        Retrofit retrofit = api.getRetrofitInstance();
        APIRequest apiRequest = retrofit.create(APIRequest.class);
        Call<JsonObject> responseCall = apiRequest.getVCRMCSocCatListRequest(requestBody);

        DebugLog.getInstance().d("VCRMC_Social_category_list_param=" + responseCall.request().toString());
        DebugLog.getInstance().d("VCRMC_Social_category_list_param=" + AppUtility.getInstance().bodyToString(responseCall.request()));

        api.postRequest(responseCall, this, 1);*/



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
                memberType = vcrmcMemberDetailModel.getType();
                gender = vcrmcMemberDetailModel.getGender();
                socialCategory = vcrmcMemberDetailModel.getSocial_category();

                fNameEditText.setText(fName);
                mNameEditText.setText(mName);
                lNameEditText.setText(lName);
                mobEditText.setText(mobile);

                if (gender.equalsIgnoreCase("m")){
                    maleRButton.setChecked(true);
                }else if (gender.equalsIgnoreCase("f")){
                    femaleRButton.setChecked(true);
                }else if (gender.equalsIgnoreCase("T")){
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
        }  else {

            finish();
            /*try {

                // To get Detail
                JSONObject param = new JSONObject();
                param.put("user_id", userID);
                param.put("id", memId);
                param.put("type", memberType);
                param.put("f_name", fName);
                param.put("m_name", mName);
                param.put("l_name", lName);
                param.put("gp_id", vcrmcGpId);
                param.put("mobile_no", mobNum);
                param.put("schedule_id",schId);
                param.put("designation", memberType); // Type and designation having same Id. (According API developer)
                param.put("gender", gender);
                param.put("social_category", socialCategory);

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
            }*/


        }
    }


    private void registerButtonAction() {

        String fName = fNameEditText.getText().toString().trim();
        String mName = mNameEditText.getText().toString().trim();
        String lName = lNameEditText.getText().toString().trim();
        String mobNum = mobEditText.getText().toString().trim();
        if (memberType.equalsIgnoreCase("other")){
            memberType = "12";
        }


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

            finish();
            /*try {

                // To get Detail
                JSONObject param = new JSONObject();
                param.put("user_id", userID);
                param.put("id", memId);
                param.put("type", memberType);
                param.put("f_name", fName);
                param.put("m_name", mName);
                param.put("l_name", lName);
                param.put("gp_id", vcrmcGpId);
                param.put("mobile_no", mobNum);
                param.put("schedule_id",schId);
                param.put("designation", memberType); // Type and designation having same Id. (According API developer)
                param.put("gender", gender);
                param.put("social_category", socialCategory);

                RequestBody requestBody = AppUtility.getInstance().getRequestBody(param.toString());
                AppinventorApi api = new AppinventorApi(this, APIServices.TR_API_URL, "", ApConstants.kMSG, true);
                Retrofit retrofit = api.getRetrofitInstance();
                APIRequest apiRequest = retrofit.create(APIRequest.class);
                Call<JsonObject> responseCall = apiRequest.addEditVCRMCMemberDetailRequest(requestBody);

                DebugLog.getInstance().d("VCRMC_Member_Detail_attendance_list_param=" + responseCall.request().toString());
                DebugLog.getInstance().d("VCRMC_Member_Detail_attendance_list_param=" + AppUtility.getInstance().bodyToString(responseCall.request()));

                api.postRequest(responseCall, this, 3);


            } catch (JSONException e) {
                e.printStackTrace();
            }*/

        }
    }


    @Override
    public void onResponse(JSONObject jsonObject, int i) {

        try {

            if (jsonObject != null) {

                // VCRMC Member Social Category Response
                if (i == 1) {
                    ResponseModel responseModel = new ResponseModel(jsonObject);
                    if (responseModel.isStatus()) {
                        vcrmcSocCatArray = jsonObject.getJSONArray("data");
                    } else {
                        UIToastMessage.show(this, responseModel.getMsg());
                    }
                }

                if (i == 2) {
                    ResponseModel responseModel = new ResponseModel(jsonObject);
                    if (responseModel.isStatus()) {
                        UIToastMessage.show(this, responseModel.getMsg());
                    } else {
                        UIToastMessage.show(this, responseModel.getMsg());
                    }
                }
                if (i == 3) {
                    ResponseModel responseModel = new ResponseModel(jsonObject);
                    if (responseModel.isStatus()) {
                        UIToastMessage.show(this, responseModel.getMsg());
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
