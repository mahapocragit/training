package in.gov.pocra.training.activity.coordinator.event_report;

import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
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

public class HistEventVCRMCDetailActivity extends AppCompatActivity implements ApiCallbackCode {

    private ImageView homeBack;
    private RecyclerView attendHistDetailRecyclerView;
    private String schId;
    private String VCRMCdata;
    private String attendType;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hist_event_vcrmc_detail);

        /*** For actionbar title in center ***/
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.attendance_actionbar_layout);
        AppCompatTextView actionTitleTextView = (AppCompatTextView) getSupportActionBar().getCustomView().findViewById(R.id.actionTitleTextView);
        homeBack = (ImageView) getSupportActionBar().getCustomView().findViewById(R.id.backImageView);
        homeBack.setVisibility(View.VISIBLE);
        actionTitleTextView.setText(getResources().getString(R.string.title_event_his_vcrmc_detail));

        initialization();
        defaultConfiguration();
    }

    private void initialization() {

        String title = getIntent().getStringExtra("title");
        schId = getIntent().getStringExtra("schId");
        VCRMCdata = getIntent().getStringExtra("data");
        attendType = getIntent().getStringExtra("type");
        TextView vcrmcTitleTextView = (TextView)findViewById(R.id.vcrmcHisTitleTextView);
        vcrmcTitleTextView.setText(title);

        imageView = (ImageView)findViewById(R.id.imageView);
        attendHistDetailRecyclerView = (RecyclerView)findViewById(R.id.attendHistDetailRecyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        attendHistDetailRecyclerView.setLayoutManager(linearLayoutManager);

    }

    private void defaultConfiguration() {

        try {

            JSONObject jsonObject = new JSONObject(VCRMCdata);
            String VCRMC_GP_Id = jsonObject.getString("id");
            attendType = jsonObject.getString("type");
            getVCRMCMemberDetailList(VCRMC_GP_Id, attendType);

        } catch (JSONException e) {
            e.printStackTrace();
        }


        homeBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        String imagePath = getIntent().getStringExtra("imagePath");
        if (!imagePath.isEmpty()) {

            Picasso.get()
                    .load(Uri.parse(imagePath))
                    .resize(150, 150)
                    .centerCrop()
                    .into(imageView);
        }

    }

    private void getVCRMCMemberDetailList(String vcrmc_gp_id, String attendType) {

        try {

            // To get Detail
            JSONObject param = new JSONObject();
            param.put("gp_id", vcrmc_gp_id);
            param.put("type", attendType);
            param.put("schedule_id", schId);

            RequestBody requestBody = AppUtility.getInstance().getRequestBody(param.toString());
            AppinventorApi api = new AppinventorApi(this, APIServices.TR_API_URL, "", ApConstants.kMSG, true);
            Retrofit retrofit = api.getRetrofitInstance();
            APIRequest apiRequest = retrofit.create(APIRequest.class);
            Call<JsonObject> responseCall = apiRequest.getVCRMCMemAttendDetailListRequest(requestBody);

            DebugLog.getInstance().d("VCRMC_Member_Detail_history_list_param=" + responseCall.request().toString());
            DebugLog.getInstance().d("VCRMC_Member_Detail_history_list_param=" + AppUtility.getInstance().bodyToString(responseCall.request()));

            api.postRequest(responseCall, this, 1);


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onResponse(JSONObject jsonObject, int i) {

        try {

            if (jsonObject != null) {
                // Response for VCRMC Member
                if (i == 1) {
                    ResponseModel responseModel = new ResponseModel(jsonObject);

                    if (responseModel.isStatus()) {
                        JSONArray vcrmcMemberArrayList = jsonObject.getJSONArray("data");
                        if (vcrmcMemberArrayList.length() > 0) {
                            AdaptorHistEventVCRMCDetail adaptorHistEventVCRMCDetail = new AdaptorHistEventVCRMCDetail(this, vcrmcMemberArrayList);
                            attendHistDetailRecyclerView.setAdapter(adaptorHistEventVCRMCDetail);
                        }

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
    public void onFailure(Object o, Throwable throwable, int i) {

    }
}
