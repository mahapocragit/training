package in.gov.pocra.training.activity.common.forgot_password;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import in.co.appinventor.services_api.api.AppinventorApi;
import in.co.appinventor.services_api.app_util.AppUtility;
import in.co.appinventor.services_api.debug.DebugLog;
import in.co.appinventor.services_api.listener.ApiCallbackCode;
import in.co.appinventor.services_api.widget.UIToastMessage;
import in.gov.pocra.training.R;
import in.gov.pocra.training.model.online.ResponseModel;
import in.gov.pocra.training.util.ApConstants;
import in.gov.pocra.training.web_services.APIRequest;
import in.gov.pocra.training.web_services.APIServices;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Retrofit;


public class ForgotPasswordActivity extends AppCompatActivity implements ApiCallbackCode {

    private EditText regMobNumEditText;
    private Button sendOtpButton;
    private ImageView homeBack;
    private String regMobile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.custom_actionbar_layout);
        AppCompatTextView actionTitleTextView = (AppCompatTextView) getSupportActionBar().getCustomView().findViewById(R.id.actionTitleTextView);
        homeBack = (ImageView) getSupportActionBar().getCustomView().findViewById(R.id.backImageView);
        homeBack.setVisibility(View.VISIBLE);
        actionTitleTextView.setText(getResources().getString(R.string.title_forgot_pass));

        initialization();
        defaultConfiguration();
    }

    private void initialization() {
        regMobNumEditText = (EditText)findViewById(R.id.regMobNumEditText);
        sendOtpButton = (Button)findViewById(R.id.sendOtpButton);
    }


    private void defaultConfiguration() {

        sendOtpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendOtpButtonAction();
            }
        });

        homeBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void sendOtpButtonAction() {

        regMobile = regMobNumEditText.getText().toString().trim();

        if (regMobile.isEmpty()){
            regMobNumEditText.setError("Enter your register mobile number");
            regMobNumEditText.requestFocus();
        }else if (!AppUtility.getInstance().isValidCallingNumber(regMobile)){
            regMobNumEditText.setError("Enter valid mobile number");
            regMobNumEditText.requestFocus();
        }else if (regMobile.length()!= 10){
            regMobNumEditText.setError("Enter valid mobile number");
            regMobNumEditText.requestFocus();
        }else {
            regMobNumEditText.setError(null);

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("mob", regMobile);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            RequestBody requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString());
            AppinventorApi api = new AppinventorApi(this, APIServices.API_URL, "", ApConstants.kMSG, true);
            Retrofit retrofit = api.getRetrofitInstance();
            APIRequest apiRequest = retrofit.create(APIRequest.class);
            Call<JsonObject> responseCall = apiRequest.sendForgotPassOtpRequest(requestBody);

            DebugLog.getInstance().d("Send_pass_otp_param=" + responseCall.request().toString());
            DebugLog.getInstance().d("Send_pass_otp_param=" + AppUtility.getInstance().bodyToString(responseCall.request()));

            api.postRequest(responseCall, this, 1);

           /* startActivity(new Intent(ForgotPasswordActivity.this, OtpVerificationActivity.class));
            finish();*/
        }

    }


    @Override
    public void onResponse(JSONObject jsonObject, int i) {
        try {

            if (jsonObject != null) {
                // District Response
                if (i == 1) {
                    ResponseModel responseModel = new ResponseModel(jsonObject);

                    if (responseModel.isStatus()) {
                        JSONObject otpJSON = jsonObject.getJSONObject("data");
                        Intent intent = new Intent(this,OtpVerificationActivity.class);
                        intent.putExtra("otpData",otpJSON.toString());
                        startActivity(intent);
                        finish();

                    } else  {
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
