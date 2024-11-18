package in.gov.pocra.training.activity.common.forgot_password;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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
import in.gov.pocra.training.util.ApUtil;
import in.gov.pocra.training.web_services.APIRequest;
import in.gov.pocra.training.web_services.APIServices;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Retrofit;


public class OtpVerificationActivity extends AppCompatActivity implements ApiCallbackCode {

    private EditText otpOneEditText;
    private EditText otpTwoEditText;
    private EditText otpThreeEditText;
    private EditText otpFourEditText;
    private Button otpSubmitButton;
    private String regMobile;
    private String responseOtp;
    private TextView resendOtpTextView;
    private String expiredTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_verification);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.custom_actionbar_layout);
        AppCompatTextView actionTitleTextView = (AppCompatTextView) getSupportActionBar().getCustomView().findViewById(R.id.actionTitleTextView);
        actionTitleTextView.setText(getResources().getString(R.string.title_forgot_pass));

        initialization();
        defaultConfiguration();
    }

    private void initialization() {

        otpOneEditText= (EditText)findViewById(R.id.otpOneEditText);
        otpTwoEditText= (EditText)findViewById(R.id.otpTwoEditText);
        otpThreeEditText= (EditText)findViewById(R.id.otpThreeEditText);
        otpFourEditText= (EditText)findViewById(R.id.otpFourEditText);
        otpSubmitButton= (Button)findViewById(R.id.otpSubmitButton);
        resendOtpTextView = (TextView)findViewById(R.id.resendOtpTextView);

    }



    private void defaultConfiguration() {

        String otpData = getIntent().getStringExtra("otpData");
        try {
            JSONObject jsonObject = new JSONObject(otpData);
            responseOtp = jsonObject.getString("otp");
            regMobile = jsonObject.getString("mob");
            expiredTime = jsonObject.getString("otp_expired_at");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // For Resend Visibility
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                resendOtpTextView.setVisibility(View.VISIBLE);
            }
        },120000);

        /*otpSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // startActivity(new Intent(OtpVerificationActivity.this, LoginActivity.class));
                Intent intent = new Intent(OtpVerificationActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });*/

        otpOneEditText.addTextChangedListener(new GenericTextWatcher(otpOneEditText));
        otpTwoEditText.addTextChangedListener(new GenericTextWatcher(otpTwoEditText));
        otpThreeEditText.addTextChangedListener(new GenericTextWatcher(otpThreeEditText));
        otpFourEditText.addTextChangedListener(new GenericTextWatcher(otpFourEditText));

        otpSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                otpSubmitButtonAction();
            }
        });

        resendOtpTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resendOtpAction();
            }
        });

    }

    private void resendOtpAction() {

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
        Call<JsonObject> responseCall = apiRequest.resendForgotPassOtpRequest(requestBody);

        DebugLog.getInstance().d("Send_pass_otp_param=" + responseCall.request().toString());
        DebugLog.getInstance().d("Send_pass_otp_param=" + AppUtility.getInstance().bodyToString(responseCall.request()));

        api.postRequest(responseCall, this, 1);

    }




    /****For Edit text auto focus after entering/deleting OTP ****/
    public class GenericTextWatcher implements TextWatcher {

        private View view;

        private GenericTextWatcher(View view) {
            this.view = view;
        }

        @Override
        public void afterTextChanged(Editable editable) {
            // TODO Auto-generated method stub
            String text = editable.toString();
            switch (view.getId()) {
                case R.id.otpOneEditText:
                    if (text.length() == 1)
                        otpTwoEditText.requestFocus();
                    break;
                case R.id.otpTwoEditText:
                    if (text.length() == 1)
                        otpThreeEditText.requestFocus();
                    break;
                case R.id.otpThreeEditText:
                    if (text.length() == 1)
                        otpFourEditText.requestFocus();
                    break;

                case R.id.otpFourEditText:
                    break;

            }
        }

        @Override
        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            // TODO Auto-generated method stub

            switch (view.getId()) {
                case R.id.otpOneEditText:
                    break;
                case R.id.otpTwoEditText:
                    if (arg3 == 0)
                        otpOneEditText.requestFocus();
                    break;
                case R.id.otpThreeEditText:
                    if (arg3 == 0)
                        otpTwoEditText.requestFocus();
                    break;
                case R.id.otpFourEditText:
                    if (arg3 == 0)
                        otpThreeEditText.requestFocus();
                    break;

            }
        }
    }



    private void otpSubmitButtonAction() {

        String finalOtp = "";

        String otpOne = otpOneEditText.getText().toString().trim();
        String otpTwo = otpTwoEditText.getText().toString().trim();
        String otpThree = otpThreeEditText.getText().toString().trim();
        String otpFour = otpFourEditText.getText().toString().trim();

        if (otpOne.isEmpty()){
            UIToastMessage.show(this,"Enter Otp");
            otpOneEditText.requestFocus();
        }else if (otpTwo.isEmpty()){
            UIToastMessage.show(this,"Enter Otp");
            otpTwoEditText.requestFocus();
        }else if (otpThree.isEmpty()){
            UIToastMessage.show(this,"Enter Otp");
            otpThreeEditText.requestFocus();
        }else if (otpFour.isEmpty()){
            UIToastMessage.show(this,"Enter Otp");
            otpFourEditText.requestFocus();
        }else {
            otpFourEditText.setError(null);
            finalOtp = otpOne+otpTwo+otpThree+otpFour;

            if (!ApUtil.isTimeExpired(expiredTime)){
                if (finalOtp.equalsIgnoreCase(responseOtp)){
                    Intent intent = new Intent(this,ResetPasswordActivity.class);
                    intent.putExtra("regMobile",regMobile);
                    startActivity(intent);
                    finish();
                }else {
                    UIToastMessage.show(this,"Please enter valid OTP");
                }
            }else {
                UIToastMessage.show(this,"OTP time get expired");
            }


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

                        resendOtpTextView.setVisibility(View.INVISIBLE);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                resendOtpTextView.setVisibility(View.VISIBLE);
                            }
                        },120000);

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
