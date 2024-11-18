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
import in.gov.pocra.training.activity.common.login.LoginActivity;
import in.gov.pocra.training.model.online.ResponseModel;
import in.gov.pocra.training.util.ApConstants;
import in.gov.pocra.training.web_services.APIRequest;
import in.gov.pocra.training.web_services.APIServices;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Retrofit;

public class ResetPasswordActivity extends AppCompatActivity implements ApiCallbackCode {

    private ImageView homeBack;
    private EditText newPassEditText;
    private EditText confPassEditText;
    private Button submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        /* ** For actionbar title in center ***/
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.attendance_actionbar_layout);
        AppCompatTextView actionTitleTextView = (AppCompatTextView) getSupportActionBar().getCustomView().findViewById(R.id.actionTitleTextView);
        homeBack = (ImageView) getSupportActionBar().getCustomView().findViewById(R.id.backImageView);
        homeBack.setVisibility(View.VISIBLE);
        actionTitleTextView.setText(getResources().getString(R.string.title_reset_pass));

        initialization();
        defaultConfiguration();

    }

    private void initialization() {
        // For Action Bar
        homeBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        newPassEditText = (EditText)findViewById(R.id.newPassEditText);
        confPassEditText = (EditText)findViewById(R.id.confPassEditText);
        submitButton = (Button)findViewById(R.id.submitButton);
    }

    private void defaultConfiguration() {

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitButtonAction();
            }
        });

    }

    private void submitButtonAction() {

        String newPass = newPassEditText.getText().toString().trim();
        String confPass = confPassEditText.getText().toString().trim();
        String regMobile = getIntent().getStringExtra("regMobile");

        if (newPass.isEmpty()){
            newPassEditText.setError("Enter new password");
            newPassEditText.requestFocus();
        }else if (newPass.length() < 3){
            newPassEditText.setError(null);
            newPassEditText.setError("New password should be more then 3 word");
            newPassEditText.requestFocus();
        }else if (confPass.isEmpty()){
            newPassEditText.setError(null);
            confPassEditText.setError("Enter confirm password");
            confPassEditText.requestFocus();
        }else if (confPass.length() < 3){
            confPassEditText.setError(null);
            confPassEditText.setError("confirm password should be more then 3 word");
            confPassEditText.requestFocus();
        }else if (!newPass.equalsIgnoreCase(confPass)){
            confPassEditText.setError(null);
            confPassEditText.setError("confirm password should be same as New password");
            confPassEditText.requestFocus();
        }else {
            confPassEditText.setError(null);

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("mob", regMobile);
                jsonObject.put("new_password", newPass);
                jsonObject.put("confirm_password", confPass);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            RequestBody requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString());
            AppinventorApi api = new AppinventorApi(this, APIServices.API_URL, "", ApConstants.kMSG, true);
            Retrofit retrofit = api.getRetrofitInstance();
            APIRequest apiRequest = retrofit.create(APIRequest.class);
           // Call<JsonObject> responseCall = apiRequest.sendForgotPassOtpRequest(requestBody);
            Call<JsonObject> responseCall = apiRequest.resetPasswordRequest(requestBody);

            DebugLog.getInstance().d("Reset_password_param=" + responseCall.request().toString());
            DebugLog.getInstance().d("Reset_password_param=" + AppUtility.getInstance().bodyToString(responseCall.request()));

            api.postRequest(responseCall, this, 1);

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
                        UIToastMessage.show(this, responseModel.getMsg());
                        Intent intent = new Intent(this,LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
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
