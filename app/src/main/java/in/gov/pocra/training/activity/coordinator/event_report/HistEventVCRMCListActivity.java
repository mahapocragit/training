package in.gov.pocra.training.activity.coordinator.event_report;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

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
import in.gov.pocra.training.R;
import in.gov.pocra.training.model.online.ResponseModel;
import in.gov.pocra.training.util.ApConstants;
import in.gov.pocra.training.web_services.APIRequest;
import in.gov.pocra.training.web_services.APIServices;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Retrofit;

public class HistEventVCRMCListActivity extends AppCompatActivity implements OnMultiRecyclerItemClickListener, ApiCallbackCode {

    private ImageView homeBack;
    private RecyclerView vcrmcRecyclerView;
    private String roleId;
    private String userID;
    private JSONArray vcrmcArrayList;
    private String schId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hist_event_vcrmc_list);


        /* ** For actionbar title in center ***/
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.attendance_actionbar_layout);
        AppCompatTextView actionTitleTextView = (AppCompatTextView) getSupportActionBar().getCustomView().findViewById(R.id.actionTitleTextView);
        homeBack = (ImageView) getSupportActionBar().getCustomView().findViewById(R.id.backImageView);
        homeBack.setVisibility(View.VISIBLE);
        actionTitleTextView.setText(getResources().getString(R.string.title_event_his_vcrmc));

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


        // To get User Id and Roll Id
        String rId = AppSettings.getInstance().getValue(this, ApConstants.kROLE_ID, ApConstants.kROLE_ID);
        String uId = AppSettings.getInstance().getValue(this, ApConstants.kUSER_ID, ApConstants.kUSER_ID);
        if (!rId.equalsIgnoreCase("kROLE_ID")) {
            roleId = rId;
        }
        if (!uId.equalsIgnoreCase("kUSER_ID")) {
            userID = uId;
        }


        vcrmcRecyclerView = (RecyclerView) findViewById(R.id.vcrmcRecyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        vcrmcRecyclerView.setLayoutManager(linearLayoutManager);

    }

    @Override
    protected void onResume() {
        super.onResume();

        String data = getIntent().getStringExtra("data");
        try {
            JSONObject jsonObject = new JSONObject(data);
            schId = jsonObject.getString("id");
            getVCRMCList(schId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    private void defaultConfiguration() {


    }


    /*****To get VCRMC list against schedule*****/
    private void getVCRMCList(String schId) {

        try {

            // To get Detail
            JSONObject param = new JSONObject();
            param.put("user_id", userID);
            param.put("role_id", roleId);
            param.put("schedule_id", schId);

            RequestBody requestBody = AppUtility.getInstance().getRequestBody(param.toString());
            AppinventorApi api = new AppinventorApi(this, APIServices.TR_API_URL, "", ApConstants.kMSG, true);
            Retrofit retrofit = api.getRetrofitInstance();
            APIRequest apiRequest = retrofit.create(APIRequest.class);
            Call<JsonObject> responseCall = apiRequest.getVCRMCAttendanceListRequest(requestBody);

            DebugLog.getInstance().d("VCRMC_list_history_param=" + responseCall.request().toString());
            DebugLog.getInstance().d("VCRMC_list_history_param=" + AppUtility.getInstance().bodyToString(responseCall.request()));

            api.postRequest(responseCall, this, 1);


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    @Override
    public void onMultiRecyclerViewItemClick(int i, Object o) {

        if (o != null) {
            JSONObject jsonObject = (JSONObject) o;
            try {
                String title = jsonObject.getString("name").trim();
                String attendType = jsonObject.getString("type").trim();
                String imgPath = AppUtility.getInstance().sanitizeJSONObj(jsonObject, "image");

                Intent intent = new Intent(this, HistEventVCRMCDetailActivity.class);
                intent.putExtra("data", jsonObject.toString());
                intent.putExtra("title", title);
                intent.putExtra("schId", schId);
                intent.putExtra("imagePath", imgPath);
                intent.putExtra("type", attendType);
                startActivity(intent);


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onResponse(JSONObject jsonObject, int i) {

        try {

            if (jsonObject != null) {
                // Schedule Response for PS HRD
                if (i == 1) {
                    ResponseModel responseModel = new ResponseModel(jsonObject);

                    if (responseModel.isStatus()) {
                        vcrmcArrayList = jsonObject.getJSONArray("data");
                        if (vcrmcArrayList.length() > 0) {

                            AdaptorHistEventVCRMC adaptorHistEventVCRMC = new AdaptorHistEventVCRMC(this, vcrmcArrayList, this);
                            vcrmcRecyclerView.setAdapter(adaptorHistEventVCRMC);
                        }
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
