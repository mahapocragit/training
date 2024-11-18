package in.gov.pocra.training.activity.ca.person_list;

import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import in.gov.pocra.training.R;
import in.gov.pocra.training.model.online.ResponseModel;
import in.gov.pocra.training.util.ApConstants;
import in.gov.pocra.training.util.ApUtil;
import in.gov.pocra.training.web_services.APIRequest;
import in.gov.pocra.training.web_services.APIServices;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Retrofit;

public class AddAgriLabourActivity extends AppCompatActivity implements AlertListCallbackEventListener, ApiJSONObjCallback, ApiCallbackCode {

    private ImageView homeBack;
    private String roleId;
    private String userID;

    private String subDivisionId = "";
    private String talukaId = "";
    private String villageName;
    private String villageCode = "";

    private EditText fNameEditText;
    private EditText mNameEditText;
    private EditText lNameEditText;
    private EditText mobEditText;
    private RadioGroup genderRGroup;
    private String genderId = "";
    private LinearLayout socCatLinearLayout;
    private TextView socCatTextView;
    private JSONArray socialCategoryArray;
    private String socialCategoryId = "";
    private Button registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_labour);

        /** For actionbar title in center */
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.attendance_actionbar_layout);
        AppCompatTextView actionTitleTextView = (AppCompatTextView) getSupportActionBar().getCustomView().findViewById(R.id.actionTitleTextView);
        homeBack = (ImageView) getSupportActionBar().getCustomView().findViewById(R.id.backImageView);
        homeBack.setVisibility(View.VISIBLE);
        // addPersonImageView = (ImageView) getSupportActionBar().getCustomView().findViewById(R.id.addPersonImageView);
        // addPersonImageView.setVisibility(View.GONE);
        actionTitleTextView.setText(getResources().getString(R.string.title_labour));


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
        talukaId = getIntent().getStringExtra("talukaId");
        villageCode = getIntent().getStringExtra("villageCode");
        villageName = getIntent().getStringExtra("villageName");

        fNameEditText = findViewById(R.id.fNameEditText);
        mNameEditText = findViewById(R.id.mNameEditText);
        lNameEditText = findViewById(R.id.lNameEditText);
        mobEditText = findViewById(R.id.mobEditText);
        genderRGroup = findViewById(R.id.genderRGroup);
        // For social Category
        socCatLinearLayout = (LinearLayout) findViewById(R.id.socCatLinearLayout);
        socCatTextView = (TextView) findViewById(R.id.socCatTextView);

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
        getSocialCategoryList();

    }

    private void eventListener() {

        genderRGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == R.id.maleRButton) {
                    genderId = "1";
                } else if (i == R.id.femaleRButton) {
                    genderId = "1";
                } else if (i == R.id.tranRButton) {
                    genderId = "1";
                }
            }
        });


        // For Sub-division
        socCatLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (socialCategoryArray != null) {
                    ApUtil.showCustomListPicker(socCatTextView, socialCategoryArray, "Select Social Category", "name", "id", AddAgriLabourActivity.this, AddAgriLabourActivity.this);
                } else {
                    getSocialCategoryList();
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


    private void getSocialCategoryList() {
        // It is GET Method
        AppinventorIncAPI api = new AppinventorIncAPI(this, APIServices.API_URL, "", ApConstants.kMSG, true);
        api.getRequestData(APIServices.GET_VCRMC_SOCIAL_LIST, this, 1);

    }

    @Override
    public void didSelectAlertViewListItem(TextView textView, String s) {

        if (textView == socCatTextView) {
            socialCategoryId = s;
        }

    }

    private void registerButtonAction() {

        String fName = fNameEditText.getText().toString().trim();
        String mName = mNameEditText.getText().toString().trim();
        String lName = lNameEditText.getText().toString().trim();
        String mobile = mobEditText.getText().toString().trim();

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
        } else if (socialCategoryId.equalsIgnoreCase("")) {
            Toast.makeText(this, "Select social category", Toast.LENGTH_SHORT).show();
//            UIToastMessage.show(this, "Select social category");
        } else {

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("user_id", userID);
                jsonObject.put("subdivision", subDivisionId);
                jsonObject.put("taluka_id", talukaId);
                // jsonObject.put("village_id", "");
                jsonObject.put("census_code", villageCode);
                jsonObject.put("first_name", fName);
                jsonObject.put("middle_name", mName);
                jsonObject.put("last_name", lName);
                jsonObject.put("mobile", mobile);
                jsonObject.put("gender", genderId);
                jsonObject.put("social_category_id", socialCategoryId);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            RequestBody requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString());
            AppinventorApi api = new AppinventorApi(this, APIServices.SSO_API_URL, "", ApConstants.kMSG, true);
            Retrofit retrofit = api.getRetrofitInstance();
            APIRequest apiRequest = retrofit.create(APIRequest.class);
            Call<JsonObject> responseCall = apiRequest.addCaLabourRequest(requestBody);

            DebugLog.getInstance().d("Village_list_param=" + responseCall.request().toString());
            DebugLog.getInstance().d("Village_list_param=" + AppUtility.getInstance().bodyToString(responseCall.request()));

            api.postRequest(responseCall, this, 2);

        }
    }


    @Override
    public void onResponse(JSONObject jsonObject, int i) {
        if (jsonObject != null) {

            try {

                // Sub-division Response
                if (i == 1) {
                    ResponseModel responseModel = new ResponseModel(jsonObject);
                    if (responseModel.isStatus()) {
                        socialCategoryArray = jsonObject.getJSONArray("data");
                    }
                }

                // Sub-division Response
                if (i == 2) {
                    ResponseModel responseModel = new ResponseModel(jsonObject);
                    if (responseModel.isStatus()) {
                        Toast.makeText(this, ""+responseModel.getMsg(), Toast.LENGTH_SHORT).show();
//                        UIToastMessage.show(this, responseModel.getMsg());
                        finish();
                    } else {
                        Toast.makeText(this, ""+responseModel.getMsg(), Toast.LENGTH_SHORT).show();
//                        UIToastMessage.show(this, responseModel.getMsg());
                    }
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public void onFailure(Object o, Throwable throwable, int i) {

    }

    @Override
    public void onFailure(Throwable throwable, int i) {

    }
}