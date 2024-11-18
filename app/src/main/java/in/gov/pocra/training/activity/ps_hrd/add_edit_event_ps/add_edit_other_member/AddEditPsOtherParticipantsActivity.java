package in.gov.pocra.training.activity.ps_hrd.add_edit_event_ps.add_edit_other_member;

import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import in.co.appinventor.services_api.api.AppinventorApi;
import in.co.appinventor.services_api.api.AppinventorIncAPI;
import in.co.appinventor.services_api.app_util.AppUtility;
import in.co.appinventor.services_api.debug.DebugLog;
import in.co.appinventor.services_api.listener.AlertListCallbackEventListener;
import in.co.appinventor.services_api.listener.ApiCallbackCode;
import in.co.appinventor.services_api.listener.ApiJSONObjCallback;
import in.co.appinventor.services_api.settings.AppSettings;
import in.co.appinventor.services_api.widget.UIToastMessage;
import in.gov.pocra.training.R;
import in.gov.pocra.training.event_db.CordOfflineDBase;
import in.gov.pocra.training.model.online.ResponseModel;
import in.gov.pocra.training.util.ApConstants;
import in.gov.pocra.training.util.ApUtil;
import in.gov.pocra.training.web_services.APIRequest;
import in.gov.pocra.training.web_services.APIServices;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Retrofit;


public class AddEditPsOtherParticipantsActivity extends AppCompatActivity implements AlertListCallbackEventListener, ApiCallbackCode, ApiJSONObjCallback {


    private CordOfflineDBase cDB;
    private ImageView homeBack;

    private EditText fNameEditText;
    private EditText mNameEditText;
    private EditText lNameEditText;
    private EditText mobEditText;
    private RadioGroup genderRGroup;
    private RadioButton maleRButton;
    private RadioButton femaleRButton;
    private RadioButton tranRButton;
    private EditText desigEText;
    private TextView socCatTextView;
    private LinearLayout catLinearLayout;
    private ImageView socCatImageView;
    private Button registerButton;
    private String gender = "";
    private String designation = "";
    private String actionType = "Add";
    private JSONArray vcrmcSocCatArray = null;
    private String memberDetail = "";
    private String roleId;
    private String userID;
    private String memId = "";
    private String gruId;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_other_member);

        /* ***For actionbar title in center*** */
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.custom_actionbar_layout);
        AppCompatTextView actionTitleTextView = (AppCompatTextView) getSupportActionBar().getCustomView().findViewById(R.id.actionTitleTextView);
        homeBack = (ImageView) getSupportActionBar().getCustomView().findViewById(R.id.backImageView);
        homeBack.setVisibility(View.VISIBLE);
        actionTitleTextView.setText(getResources().getString(R.string.title_add_edit_member));

        cDB = new CordOfflineDBase(this);

        initialization();
        defaultConfiguration();

    }




    private void initialization() {

        actionType = getIntent().getStringExtra("type");

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
        desigEText = (EditText)findViewById(R.id.desigEText);
        catLinearLayout = (LinearLayout) findViewById(R.id.catLinearLayout);
        socCatTextView = (TextView) findViewById(R.id.socCatTextView);
        socCatImageView = (ImageView) findViewById(R.id.socCatImageView);
        registerButton = (Button) findViewById(R.id.registerButton);

        // getSocialCategoryList();

    }


    private void defaultConfiguration() {
        homeBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mobEditText.setEnabled(true);

        // To set member Detail
        if (actionType.equalsIgnoreCase("update")) {
            registerButton.setText("Update");
            String otherPersonDetail = getIntent().getStringExtra("memberData");
            mobEditText.setEnabled(false);
            getSetOtherMemberDetail(otherPersonDetail);

        }else {
            mobEditText.setEnabled(true);
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
                    gender = "1";
                } else if (checkedId == R.id.femaleRButton) {
                    gender = "2";
                } else if (checkedId == R.id.tranRButton) {
                    gender = "3";
                }
            }
        });

        catLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ApUtil.checkInternetConnection(AddEditPsOtherParticipantsActivity.this)) {
                    if (vcrmcSocCatArray != null) {
                        AppUtility.getInstance().showListPicker(socCatTextView, vcrmcSocCatArray, "Select Social Category", "name", "id", AddEditPsOtherParticipantsActivity.this, AddEditPsOtherParticipantsActivity.this);
                    } else {
                        getSocialCategoryList();
                    }
                } else {
                    UIToastMessage.show(AddEditPsOtherParticipantsActivity.this, "No internet connection");
                }
            }
        });
    }





    private void getSetOtherMemberDetail(String otherPartiDetail) {

        try {
            // To get Detail
            JSONObject othMemDetailJson = new JSONObject(otherPartiDetail);

            final String memId = othMemDetailJson.getString("id");
            String fName = othMemDetailJson.getString("first_name");
            String mName = othMemDetailJson.getString("middle_name");
            String lName = othMemDetailJson.getString("last_name");
            final  String name = fName +" "+ mName +" "+ lName;
            String designation = othMemDetailJson.getString("designation");
            String mobile = othMemDetailJson.getString("mobile");
            gender = othMemDetailJson.getString("gender");


            fNameEditText.setText(fName);
            mNameEditText.setText(mName);
            lNameEditText.setText(lName);
            mobEditText.setText(mobile);
            desigEText.setText(designation);

            if (gender.equalsIgnoreCase("1")) {
                maleRButton.setChecked(true);
            } else if (gender.equalsIgnoreCase("2")) {
                femaleRButton.setChecked(true);
            } else if (gender.equalsIgnoreCase("3")) {
                tranRButton.setChecked(true);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
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


    private void setMemberDetail(JSONObject jsonObject) {

        try {

            if (jsonObject.length() > 0) {

                memId = jsonObject.getString("id");

                String fName = jsonObject.getString("first_name");
                String mName = jsonObject.getString("middle_name");
                String lName = jsonObject.getString("last_name");
                String fullName = fName + " " + mName + " " + lName;
                String mobile = jsonObject.getString("mobile");
                // String attendType = jsonObject.getString("designation");
                gender = jsonObject.getString("gender_id");
                String designation = jsonObject.getString("designation");

                fNameEditText.setText(fName);
                mNameEditText.setText(mName);
                lNameEditText.setText(lName);
                mobEditText.setText(mobile);
                desigEText.setText(designation);

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




    // Register other person
    private void registerButtonAction() {

        ApUtil.hideKeybord(registerButton,this);

        String fName = fNameEditText.getText().toString().trim();
        String mName = mNameEditText.getText().toString().trim();
        String lName = lNameEditText.getText().toString().trim();
        String mobNum = mobEditText.getText().toString().trim();
        String designation = desigEText.getText().toString().trim();

        if (fName.isEmpty()) {
            fNameEditText.setError("Enter first name");
            fNameEditText.requestFocus();
        }  else if (lName.isEmpty()) {
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
        }/*else if (designation.isEmpty()){
            UIToastMessage.show(this,"Enter designation");
        }*/ else {
            socCatTextView.setError(null);

            try {


                // To get Detail
                JSONObject param = new JSONObject();
                param.put("created_by", userID);
                param.put("first_name", fName);
                param.put("middle_name", mName);
                param.put("last_name", lName);
                param.put("mobile", mobNum);
                param.put("role_id", roleId);
                param.put("designation", designation);
                param.put("gender", gender);
                param.put("api_key", ApConstants.kAUTHORITY_KEY);

                RequestBody requestBody = AppUtility.getInstance().getRequestBody(param.toString());
                AppinventorApi api = new AppinventorApi(this, APIServices.BASE_URL, "", ApConstants.kMSG, true);
                Retrofit retrofit = api.getRetrofitInstance();
                APIRequest apiRequest = retrofit.create(APIRequest.class);
                Call<JsonObject> responseCall = apiRequest.addOtherParticipantsRequest(requestBody);

                DebugLog.getInstance().d("Add_other_participants_param=" + responseCall.request().toString());
                DebugLog.getInstance().d("Add_other_participants_param=" + AppUtility.getInstance().bodyToString(responseCall.request()));

                api.postRequest(responseCall, this, 3);


            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }



    // Update other person detail
    private void updateButtonAction() {

        ApUtil.hideKeybord(registerButton,this);

        String fName = fNameEditText.getText().toString().trim();
        String mName = mNameEditText.getText().toString().trim();
        String lName = lNameEditText.getText().toString().trim();
        String mobNum = mobEditText.getText().toString().trim();
        String designation = desigEText.getText().toString().trim();

        if (fName.isEmpty()) {
            fNameEditText.setError("Enter first name");
            fNameEditText.requestFocus();
        } /*else if (mName.isEmpty()) {
            fNameEditText.setError(null);
            mNameEditText.setError("Enter middle name");
            mNameEditText.requestFocus();
        }*/ else if (lName.isEmpty()) {
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
        }/*else if (designation.isEmpty()){
            UIToastMessage.show(this,"Enter designation");
        }*/ else {
            socCatTextView.setError(null);

            try {

                JSONObject param = new JSONObject();
                param.put("created_by", userID);
                param.put("first_name", fName);
                param.put("middle_name", mName);
                param.put("last_name", lName);
                param.put("mobile", mobNum);
                param.put("role_id", roleId);
                param.put("designation", designation);
                param.put("gender", gender);
                param.put("api_key", ApConstants.kAUTHORITY_KEY);

                RequestBody requestBody = AppUtility.getInstance().getRequestBody(param.toString());
                AppinventorApi api = new AppinventorApi(this, APIServices.BASE_URL, "", ApConstants.kMSG, true);
                Retrofit retrofit = api.getRetrofitInstance();
                APIRequest apiRequest = retrofit.create(APIRequest.class);
                Call<JsonObject> responseCall = apiRequest.addOtherParticipantsRequest(requestBody);

                DebugLog.getInstance().d("Add_other_participants_param=" + responseCall.request().toString());
                DebugLog.getInstance().d("Add_other_participants_param=" + AppUtility.getInstance().bodyToString(responseCall.request()));

                api.postRequest(responseCall, this, 3);

                /*// To get Detail
                JSONObject param = new JSONObject();
                param.put("user_id", userID);
                param.put("id", memId);
                param.put("first_name", fName);
                param.put("middle_name", mName);
                param.put("last_name", lName);
                param.put("mobile", mobNum);
                param.put("designation", designation);
                param.put("gender", gender);
                param.put("role_id", roleId);
                param.put("api_key", ApConstants.kAUTHORITY_KEY);

                RequestBody requestBody = AppUtility.getInstance().getRequestBody(param.toString());
                AppinventorApi api = new AppinventorApi(this, APIServices.BASE_URL, "", ApConstants.kMSG, true);
                Retrofit retrofit = api.getRetrofitInstance();
                APIRequest apiRequest = retrofit.create(APIRequest.class);
                Call<JsonObject> responseCall = apiRequest.updateOtherGroupMemDetailRequest(requestBody);

                DebugLog.getInstance().d("Update_other_member_detail_param=" + responseCall.request().toString());
                DebugLog.getInstance().d("Update_other_member_detail_param=" + AppUtility.getInstance().bodyToString(responseCall.request()));

                api.postRequest(responseCall, this, 2);*/


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }




    @Override
    public void didSelectAlertViewListItem(TextView textView, String s) {

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
//                        UIToastMessage.show(this, responseModel.getMsg());
                        Toast.makeText(this, ""+responseModel.getMsg(), Toast.LENGTH_SHORT).show();
                    }
                }


                // Update Other Person
                if (i == 2) {
                    ResponseModel responseModel = new ResponseModel(jsonObject);
                    if (responseModel.isStatus()) {
//                        UIToastMessage.show(this, responseModel.getMsg());
                        Toast.makeText(this, ""+responseModel.getMsg(), Toast.LENGTH_SHORT).show();
                    } else {
//                        UIToastMessage.show(this, responseModel.getMsg());
                        Toast.makeText(this, ""+responseModel.getMsg(), Toast.LENGTH_SHORT).show();
                    }
                }


                // Add new person
                if (i == 3) {
                    ResponseModel responseModel = new ResponseModel(jsonObject);
                    if (responseModel.isStatus()) {
//                        UIToastMessage.show(this, responseModel.getMsg());
                        Toast.makeText(this, ""+responseModel.getMsg(), Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
//                        UIToastMessage.show(this, responseModel.getMsg());
                        Toast.makeText(this, ""+responseModel.getMsg(), Toast.LENGTH_SHORT).show();
                    }
                }

                // Get a person detail
                /*if (i == 4) {
                    ResponseModel responseModel = new ResponseModel(jsonObject);
                    if (responseModel.isStatus()) {

                        JSONArray otherPData = responseModel.getData();
                        JSONObject otherJson = otherPData.getJSONObject(0);
                        setMemberDetail(otherJson);


                    } else {
                        UIToastMessage.show(this, responseModel.getMsg());
                    }
                }*/

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
