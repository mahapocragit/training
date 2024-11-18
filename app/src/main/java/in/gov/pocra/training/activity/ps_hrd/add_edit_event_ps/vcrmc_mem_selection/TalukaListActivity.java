package in.gov.pocra.training.activity.ps_hrd.add_edit_event_ps.vcrmc_mem_selection;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import in.co.appinventor.services_api.api.AppinventorApi;
import in.co.appinventor.services_api.api.AppinventorIncAPI;
import in.co.appinventor.services_api.app_util.AppUtility;
import in.co.appinventor.services_api.debug.DebugLog;
import in.co.appinventor.services_api.listener.ApiCallbackCode;
import in.co.appinventor.services_api.listener.ApiJSONObjCallback;
import in.co.appinventor.services_api.listener.OnMultiRecyclerItemClickListener;
import in.co.appinventor.services_api.settings.AppSettings;
import in.co.appinventor.services_api.widget.UIToastMessage;
import in.gov.pocra.training.R;
import in.gov.pocra.training.activity.pmu.add_event_pmu.pmu_mem_filter_list.pocra_field_staff.PoCRAFieldStaffFilterActivity;
import in.gov.pocra.training.activity.ps_hrd.add_edit_event_ps.proj_official_selection.PocraOfficialListActivity;
import in.gov.pocra.training.activity.ps_hrd.add_edit_event_ps.farmer_selection.VillageListActivity;
import in.gov.pocra.training.event_db.EventDataBase;
import in.gov.pocra.training.model.online.GPMemberDetailModel;
import in.gov.pocra.training.model.online.GPModel;
import in.gov.pocra.training.model.online.ResponseModel;
import in.gov.pocra.training.util.ApConstants;
import in.gov.pocra.training.web_services.APIRequest;
import in.gov.pocra.training.web_services.APIServices;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Retrofit;

public class TalukaListActivity extends AppCompatActivity implements ApiJSONObjCallback, OnMultiRecyclerItemClickListener, ApiCallbackCode {

    EventDataBase eDB;
    private ImageView homeBack;
    private RecyclerView talukaRView;
    private AppCompatTextView actionTitleTextView;
    private String talukaId;
    private ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taluka_list);

        eDB = new EventDataBase(this);

        /* ** For actionbar title in center ***/
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.attendance_actionbar_layout);
        actionTitleTextView = (AppCompatTextView) getSupportActionBar().getCustomView().findViewById(R.id.actionTitleTextView);
        homeBack = (ImageView) getSupportActionBar().getCustomView().findViewById(R.id.backImageView);
        homeBack.setVisibility(View.VISIBLE);

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

        String eventMemType = AppSettings.getInstance().getValue(this, ApConstants.kEVENT_MEM_TYPE, ApConstants.kEVENT_MEM_TYPE);
        if (!eventMemType.equalsIgnoreCase("") && eventMemType.equalsIgnoreCase(ApConstants.kEVENT_MEM_VCRMC)){
            actionTitleTextView.setText("VCRMC (GP)");
        }else if (!eventMemType.equalsIgnoreCase("") && eventMemType.equalsIgnoreCase(ApConstants.kEVENT_MEM_FARMER)){
            actionTitleTextView.setText("Farmer");
        }else if (!eventMemType.equalsIgnoreCase("") && eventMemType.equalsIgnoreCase(ApConstants.kEVENT_MEM_FFS_F)){
            actionTitleTextView.setText("FFS Facilitator");
        }

        talukaRView = (RecyclerView) findViewById(R.id.talukaRView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        talukaRView.setLayoutManager(linearLayoutManager);

    }

    private void defaultConfiguration() {

        String subDivID = getIntent().getStringExtra("subDivId");
        if (!subDivID.equalsIgnoreCase("")) {
            getTalukaList(subDivID);
        }

    }


    private void startProgressDialog(Context context) {
        progress = new ProgressDialog(context);
        progress.setMessage("please Wait ...");
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();
    }


    @Override
    public void onMultiRecyclerViewItemClick(int i, Object o) {


        try {
            JSONObject jsonObject = (JSONObject) o;

            String eventMemType = AppSettings.getInstance().getValue(this, ApConstants.kEVENT_MEM_TYPE, ApConstants.kEVENT_MEM_TYPE);

            talukaId = jsonObject.getString("id");
            String talukaName = jsonObject.getString("name");

            if (!eventMemType.equalsIgnoreCase("") && eventMemType.equalsIgnoreCase(ApConstants.kEVENT_MEM_VCRMC)) {

                getGPMemberData();
                /*Intent intent = new Intent(this, GPListActivity.class);
                intent.putExtra("talukaId", talukaId);
                intent.putExtra("talukaName", talukaName);
                startActivity(intent);startProgressDialog
                finish();*/
            } else if (!eventMemType.equalsIgnoreCase("") && eventMemType.equalsIgnoreCase(ApConstants.kEVENT_MEM_FARMER)) {
                Intent intent = new Intent(this, VillageListActivity.class);
                intent.putExtra("talukaId", talukaId);
                intent.putExtra("talukaName", talukaName);
                startActivity(intent);
                finish();
            }else if (!eventMemType.equalsIgnoreCase("") && eventMemType.equalsIgnoreCase(ApConstants.kEVENT_MEM_FFS_F)) {
                Intent intent = new Intent(this, PocraOfficialListActivity.class);
                intent.putExtra("talukaId", talukaId);
                intent.putExtra("talukaName", talukaName);
                startActivity(intent);
                finish();
                // UIToastMessage.show(this,"To select facilitator");
            }else if (!eventMemType.equalsIgnoreCase("") && eventMemType.equalsIgnoreCase(ApConstants.kEVENT_MEM_PFS)) {
                Intent intent = new Intent(this, PoCRAFieldStaffFilterActivity.class);
                intent.putExtra("talukaId", talukaId);
                intent.putExtra("talukaName", talukaName);
                startActivity(intent);
                finish();
                // UIToastMessage.show(this,"To select facilitator");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }




    // get Taluka
    private void getTalukaList(String subDivID) {
        String subTalukaUrl = APIServices.GET_TALUKA_URL + subDivID;
        AppinventorIncAPI api = new AppinventorIncAPI(this, APIServices.API_URL, "", ApConstants.kMSG, true);
        api.getRequestData(subTalukaUrl, this, 1);
        Log.d("param",""+subTalukaUrl);
    }


    // GET GP with Member detail
    private void getGPMemberData() {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("taluka_id", talukaId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString());
        AppinventorApi api = new AppinventorApi(this, APIServices.API_URL, "", ApConstants.kMSG, true);
        Retrofit retrofit = api.getRetrofitInstance();
        APIRequest apiRequest = retrofit.create(APIRequest.class);
        Call<JsonObject> responseCall = apiRequest.gpMemberDetailListRequest(requestBody);

        DebugLog.getInstance().d("GP_param=" + responseCall.request().toString());
        DebugLog.getInstance().d("GP_param=" + AppUtility.getInstance().bodyToString(responseCall.request()));

        api.postRequest(responseCall, this, 2);

        startProgressDialog(this);

    }


    // To Insert GP and its member Detail into DB
    private void insertIntoGpTable(JSONArray gpJSONArray) {

        try {

            for (int i = 0; i < gpJSONArray.length(); i++) {
                JSONObject jsonObject = gpJSONArray.getJSONObject(i);

                // For Gp Detail
                GPModel gpModel = new GPModel(jsonObject);
                String gpId = gpModel.getId();
                String gpName = gpModel.getName();
                String gpCode = gpModel.getCode();
                String gpIsSelected = gpModel.getIs_selected();
                // JSONArray jsonArray = gpModel.getVcrmc_member();
                JSONArray jsonArray = jsonObject.getJSONArray("vcrmc_member");

                // For Member Detail
                if (jsonArray.length() > 0){

                    for (int j=0; j<jsonArray.length(); j++){

                        JSONObject gpMemberJson = jsonArray.getJSONObject(j);
                        GPMemberDetailModel gpMemberModel = new GPMemberDetailModel(gpMemberJson);

                        String memId = gpMemberModel.getMem_id();
                        String memName = gpMemberModel.getMem_name();
                        String memFName = gpMemberModel.getMem_first_name();
                        String memMName = gpMemberModel.getMem_middle_name();
                        String memLName = gpMemberModel.getMem_last_name();
                        String memMobile = gpMemberModel.getMem_mobile();
                        String memMobile2 = gpMemberModel.getMem_mobile2();
                        String memDesigID = gpMemberModel.getMem_designation_id();
                        String memDesigName = gpMemberModel.getMem_designation_name();
                        String memGenderId = gpMemberModel.getMem_gender_id();
                        String memGenderName = gpMemberModel.getMem_gender_name();
                        String memSocCatId = gpMemberModel.getMem_social_category_id();
                        String memSocCatName = gpMemberModel.getMem_social_category_name();
                        String memHoldCat = gpMemberModel.getMem_land_hold_category();
                        String memIsSelected = gpMemberModel.getMem_is_selected();


                        if (!eDB.isGpMemExist(memId)){
                            eDB.insertGPWithMemDetail(talukaId,gpId,gpName,gpCode,gpIsSelected,memId,memName,memFName,memMName,memLName,memMobile,memMobile2,
                                    memDesigID,memDesigName,memGenderId,memGenderName,memSocCatId,memSocCatName,memHoldCat,memIsSelected);
                        }


                    }
                }
            }



        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (progress.isShowing()) {
            progress.dismiss();
        }

        Intent intent = new Intent(this, GPListActivity.class);
        intent.putExtra("taluka_id",talukaId);
        startActivity(intent);
        // finish();

    }



    @Override
    public void onResponse(JSONObject jsonObject, int i) {

        try {

            if (jsonObject != null) {

                // Taluka Response
                if (i == 1) {
                    String status = jsonObject.getString("status");
                    if (status.equalsIgnoreCase("200")) {
                        JSONArray talukaJSONArray = jsonObject.getJSONArray("data");

                        if (talukaJSONArray != null) {
                            AdaptorTalukaList adaptorTalukaList = new AdaptorTalukaList(this, talukaJSONArray, this);
                            talukaRView.setAdapter(adaptorTalukaList);
                        } else {
                            UIToastMessage.show(TalukaListActivity.this, "No data found");
                        }

                    }
                }


                // Get GP member list
                if (i == 2) {
                    ResponseModel responseModel = new ResponseModel(jsonObject);
                    if (responseModel.isStatus()) {

                        JSONArray gpJSONArray = jsonObject.getJSONArray("data");
                        if (gpJSONArray.length() > 0) {
                            /*Intent intent = new Intent(this, GPListActivity.class);
                            intent.putExtra("taluka_id","78");
                            startActivity(intent);*/
                            insertIntoGpTable(gpJSONArray);
                        }
                    } else {
                        if (progress.isShowing()) {
                            progress.dismiss();
                        }
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

    @Override
    public void onFailure(Throwable throwable, int i) {

    }


}
