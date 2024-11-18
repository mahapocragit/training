package in.gov.pocra.training.activity.ca.person_list;

import android.content.Intent;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import in.gov.pocra.training.activity.common.co_coordinator_list.CoCoordinatorListActivity;
import in.gov.pocra.training.activity.ps_hrd.add_edit_event_ps.add_edit_other_member.OtherParticipantListActivity;
import in.gov.pocra.training.model.online.ResponseModel;
import in.gov.pocra.training.util.ApConstants;
import in.gov.pocra.training.web_services.APIRequest;
import in.gov.pocra.training.web_services.APIServices;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Retrofit;

public class ResourcePersonActivity extends AppCompatActivity implements ApiCallbackCode, OnMultiRecyclerItemClickListener {

    private ImageView homeBack;
    private ImageView addPersonImageView;
    private JSONArray sledCaResourcePJSONArray = new JSONArray();
    private String action = "";
    private JSONArray rePersonJSONArray;
    private EditText searchEText;
    private RecyclerView rePersonRecyclerVew;
    private AdaptorCaPerson adaptorCaPerson;
    private Button confirmButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resource_person);

        /** For actionbar title in center */
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.attendance_actionbar_layout);
        AppCompatTextView actionTitleTextView = (AppCompatTextView) getSupportActionBar().getCustomView().findViewById(R.id.actionTitleTextView);
        homeBack = (ImageView) getSupportActionBar().getCustomView().findViewById(R.id.backImageView);
        homeBack.setVisibility(View.VISIBLE);
        addPersonImageView = (ImageView) getSupportActionBar().getCustomView().findViewById(R.id.addPersonImageView);
        addPersonImageView.setVisibility(View.VISIBLE);
        actionTitleTextView.setText(getResources().getString(R.string.title_resource_person));

        initialization();
        defaultConfiguration();
        eventListener();
    }

    private void initialization() {
        searchEText = findViewById(R.id.searchEText);
        rePersonRecyclerVew = findViewById(R.id.rePersonRecyclerVew);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        rePersonRecyclerVew.setLayoutManager(linearLayoutManager);
        confirmButton = (Button)findViewById(R.id.confirmButton);
    }

    private void defaultConfiguration() {
        // For Action Bar
        homeBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        addPersonImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ResourcePersonActivity.this, AddResourcePersonActivity.class);
                intent.putExtra("type", "Add");
                intent.putExtra("personType", "resource");
                startActivity(intent);
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        action = getIntent().getStringExtra("action");
        if (action.equalsIgnoreCase("get")){
            addPersonImageView.setVisibility(View.GONE);
            confirmButton.setVisibility(View.VISIBLE);
        }else {
            addPersonImageView.setVisibility(View.VISIBLE);
            confirmButton.setVisibility(View.GONE);
        }
        getResourcePersonList();
    }


    private void eventListener() {

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
                if (adaptorCaPerson != null){
                    ResourcePersonActivity.this.adaptorCaPerson.filter(s.toString());
                }else {
                    Toast.makeText(ResourcePersonActivity.this, "User not found", Toast.LENGTH_SHORT).show();
//                    UIToastMessage.show(ResourcePersonActivity.this, "User not found");
                }
            }
        });


        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sledCaResourcePJSONArray = new JSONArray();
                try {

                    if (adaptorCaPerson != null) {

                        for (int i = 0; i < adaptorCaPerson.mJSONArray.length(); i++) {
                            JSONObject jsonObject = adaptorCaPerson.mJSONArray.getJSONObject(i);

                            if (jsonObject.getInt("is_selected") == 1) {
                                if (sledCaResourcePJSONArray.length() < 101) {
                                    sledCaResourcePJSONArray.put(jsonObject);
                                } else {
                                    UIToastMessage.show(ResourcePersonActivity.this, "More than 101 other participant not allowed");
                                    break;
                                }
                            }
                        }

                        if (sledCaResourcePJSONArray.length() > 0) {
                            AppSettings.getInstance().setValue(ResourcePersonActivity.this, ApConstants.kS_CA_RES_PERSON_ARRAY, sledCaResourcePJSONArray.toString());
                            AppSettings.getInstance().setValue(ResourcePersonActivity.this, ApConstants.kS_COORDINATOR, ApConstants.kS_COORDINATOR);
                            finish();
                        } else {
                            UIToastMessage.show(ResourcePersonActivity.this, "Select at least one other participant");
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


    }

    private void getResourcePersonList() {

        JSONObject jsonObject = new JSONObject();
        RequestBody requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString());
        AppinventorApi api = new AppinventorApi(this, APIServices.OTHER_BASE_URL, "", ApConstants.kMSG, true);
        Retrofit retrofit = api.getRetrofitInstance();
        APIRequest apiRequest = retrofit.create(APIRequest.class);
        Call<JsonObject> responseCall = apiRequest.getCaResourcePRequest(requestBody);

        DebugLog.getInstance().d("Village_list_param=" + responseCall.request().toString());
        DebugLog.getInstance().d("Village_list_param=" + AppUtility.getInstance().bodyToString(responseCall.request()));

        api.postRequest(responseCall, this, 1);

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onMultiRecyclerViewItemClick(int i, Object o) {

        try {
            JSONObject jsonObject = (JSONObject)o;
            String personMobile = jsonObject.getString("mobile");
            String isSelected = jsonObject.getString("is_selected");

            if (sledCaResourcePJSONArray != null){
                for (int k = 0; k< sledCaResourcePJSONArray.length(); k++){
                    JSONObject sledJSONObj = sledCaResourcePJSONArray.getJSONObject(k);
                    String sledPersonMob = sledJSONObj.getString("mobile");

                    if (sledPersonMob.equalsIgnoreCase(personMobile) && isSelected.equalsIgnoreCase("0")){
                        sledCaResourcePJSONArray.remove(k);
                    }
                }
                AppSettings.getInstance().setValue(ResourcePersonActivity.this, ApConstants.kS_CA_RES_PERSON_ARRAY, sledCaResourcePJSONArray.toString());
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onResponse(JSONObject jsonObject, int i) {

        if (jsonObject != null) {

            try {
                // To get Resource Person
                if (i == 1) {
                    ResponseModel responseModel = new ResponseModel(jsonObject);
                    if (responseModel.isStatus()) {
                        rePersonJSONArray = jsonObject.getJSONArray("data");
                        adaptorCaPerson = new AdaptorCaPerson(this,rePersonJSONArray,action,"resource",this);
                        rePersonRecyclerVew.setAdapter(adaptorCaPerson);
                    }else {
                        UIToastMessage.show(this,responseModel.getMsg());
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
}