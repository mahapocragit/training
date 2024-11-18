package in.gov.pocra.training.activity.ca.add_edit_event_ca.vcrmc_mem_selection;

import android.app.ProgressDialog;
import android.content.Context;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
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
import in.gov.pocra.training.R;
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

public class GPListCaActivity extends AppCompatActivity implements OnMultiRecyclerItemClickListener, ApiCallbackCode {
    private String userID;
    private ImageView homeBack;
    private ImageView confirmImageView;
    EventDataBase eDB;
    private ProgressDialog progress;

    private RecyclerView gpRecyclerView;
    private AdaptorCaGPList adaptorCaGPList = null;
    private JSONArray selectedGPJSONArray = new JSONArray();

    // private String talukaID = "";
    private  TextView VCRMCListTextView;
    private TextView designationTextView;
    private AdaptorCaVCRMCList adaptorVCRMCList = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_g_p_list_ca);

        /* ** For actionbar title in center ***/
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.attendance_actionbar_layout);
        AppCompatTextView actionTitleTextView = (AppCompatTextView) getSupportActionBar().getCustomView().findViewById(R.id.actionTitleTextView);
        homeBack = (ImageView) getSupportActionBar().getCustomView().findViewById(R.id.backImageView);
        confirmImageView = (ImageView) getSupportActionBar().getCustomView().findViewById(R.id.addPersonImageView);
        confirmImageView.setImageDrawable(getResources().getDrawable(R.drawable.check));
        homeBack.setVisibility(View.VISIBLE);

        actionTitleTextView.setText("Select VCRMC (GP)");

        eDB = new EventDataBase(this);
        // talukaID = getIntent().getStringExtra("taluka_id");

        initialization();
        defaultConfiguration();
    }

    private void initialization() {

        String uId = AppSettings.getInstance().getValue(this, ApConstants.kUSER_ID, ApConstants.kUSER_ID);
        if (!uId.equalsIgnoreCase("kUSER_ID")) {
            userID = uId;
        }
        designationTextView = (TextView)findViewById(R.id.designationTextView);
        VCRMCListTextView = (TextView)findViewById(R.id.VCRMCListTextView);

        VCRMCListTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                VCRMCListTextView.setBackground(getResources().getDrawable(R.drawable.button_bg));
                designationTextView.setBackground(getResources().getDrawable(R.drawable.button_bg_grey));
                VCRMCListTextView.setPadding(5,5,5,5);
                designationTextView.setPadding(5,5,5,5);
                getGPList();

                /*selectedGPJSONArray = eDB.getSelectedGpList();
                if (selectedGPJSONArray.length()>0){
                    getGPList();
                    confirmImageView.setVisibility(View.VISIBLE);
                }*/
            }
        });

        designationTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                VCRMCListTextView.setBackground(getResources().getDrawable(R.drawable.button_bg_grey));
                designationTextView.setBackground(getResources().getDrawable(R.drawable.button_bg));
                VCRMCListTextView.setPadding(5,5,5,5);
                designationTextView.setPadding(5,5,5,5);
                getDesignation();
            }
        });


        // For Action Bar
        homeBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        confirmImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        gpRecyclerView = (RecyclerView) findViewById(R.id.gpRecyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        gpRecyclerView.setLayoutManager(linearLayoutManager);

    }

    private void getDesignation() {

        AppinventorApi api = new AppinventorApi(this, APIServices.BASE_URL, "", ApConstants.kMSG, true);
        Retrofit retrofit = api.getRetrofitInstance();
        APIRequest apiRequest = retrofit.create(APIRequest.class);
        //chk g
        Call<JsonObject> responseCall = apiRequest.getDesignationRequest();

        DebugLog.getInstance().d("pmu_official_list_param=" + responseCall.request().toString());
        DebugLog.getInstance().d("pmu_official_list_param=" + AppUtility.getInstance().bodyToString(responseCall.request()));

        api.postRequest(responseCall, this, 1);
    }


    @Override
    protected void onResume() {
        super.onResume();
        VCRMCListTextView.setBackground(getResources().getDrawable(R.drawable.button_bg));
        designationTextView.setBackground(getResources().getDrawable(R.drawable.button_bg_grey));
        VCRMCListTextView.setPadding(5,5,5,5);
        designationTextView.setPadding(5,5,5,5);
        getGPList();
    }


    private void getGPList() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("ca_id", userID);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString());
        AppinventorApi api = new AppinventorApi(this, APIServices.API_URL, "", ApConstants.kMSG, true);
        Retrofit retrofit = api.getRetrofitInstance();
        APIRequest apiRequest = retrofit.create(APIRequest.class);
        Call<JsonObject> responseCall = apiRequest.gpMemberListCaRequest(requestBody);

        DebugLog.getInstance().d("GP_by_ca_param=" + responseCall.request().toString());
        DebugLog.getInstance().d("GP_by_ca_param=" + AppUtility.getInstance().bodyToString(responseCall.request()));

        api.postRequest(responseCall, this, 2);
        startProgressDialog(this);
    }

    private void startProgressDialog(Context context) {
        progress = new ProgressDialog(context);
        progress.setMessage("please Wait ...");
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();
    }

    private void defaultConfiguration() {

    }

    /******* Insert GP Data *******/
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
                            eDB.insertGPByCaWithMemDetail(userID,gpId,gpName,gpCode,gpIsSelected,memId,memName,memFName,memMName,memLName,memMobile,memMobile2,
                                    memDesigID,memDesigName,memGenderId,memGenderName,memSocCatId,memSocCatName,memHoldCat,memIsSelected);
                        }
                    }
                }else {
                    if (!eDB.isGPExist(gpId)){
                        eDB.insertGPByCaWithMemDetail(userID,gpId,gpName,gpCode,gpIsSelected,"","","","",
                                "","","","","","",
                                "","","","","");
                    }
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (progress.isShowing()) {
            progress.dismiss();
        }

        getGPListFromDB();
        // finish();

    }


    /******* Get Lists Data *******/


    private void getGPListFromDB() {

        JSONArray gPArray  = eDB.getGpListByCA(userID);
        if (gPArray.length()>0){
            //to do designation
            adaptorCaGPList = new AdaptorCaGPList(GPListCaActivity.this, gPArray, GPListCaActivity.this);
            gpRecyclerView.setAdapter(adaptorCaGPList);
        }else {
            Toast.makeText(this, "NO VCRMC (GP) member found", Toast.LENGTH_SHORT).show();
//            UIToastMessage.show(this,"NO VCRMC (GP) member found");
        }
    }


    @Override
    public void onMultiRecyclerViewItemClick(int i, Object o) {

        try {

            JSONObject toSelectObj = (JSONObject) o;
            String itemId = toSelectObj.getString("id");
            String itemName = toSelectObj.getString("Name");
            if (selectedGPJSONArray.length() > 0) {

                /*for (int k = 0; k < selectedGPJSONArray.length(); k++) {
                    JSONObject jsonObject = selectedGPJSONArray.getJSONObject(k);
                    String preItemId = jsonObject.getString("id");

                    if (itemId.equalsIgnoreCase(preItemId)) {
                        UIToastMessage.show(GPListActivity.this, itemName + " is already selected");
                    }
                }*/
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    @Override
    public void onResponse(JSONObject jsonObject, int i) {
        try {

            if (jsonObject != null) {
                // Event type Response
                if (i == 1) {
                    ResponseModel eventListRMode = new ResponseModel(jsonObject);
                    if (eventListRMode.isStatus()) {
                        JSONArray designationJSONArray = jsonObject.getJSONArray("data");
                        if (designationJSONArray.length()>0){
                            //to do designation
                            adaptorVCRMCList = new AdaptorCaVCRMCList(GPListCaActivity.this, designationJSONArray,userID, GPListCaActivity.this);
                            gpRecyclerView.setAdapter(adaptorVCRMCList);
                        }else {
                            adaptorVCRMCList = new AdaptorCaVCRMCList(GPListCaActivity.this, new JSONArray(),userID, GPListCaActivity.this);
                            gpRecyclerView.setAdapter(adaptorVCRMCList);
                            Toast.makeText(this, "NO VCRMC (GP) member found", Toast.LENGTH_SHORT).show();
//                            UIToastMessage.show(this,"NO VCRMC (GP) member found");
                        }
                    }
                }

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
                        adaptorCaGPList = new AdaptorCaGPList(GPListCaActivity.this, new JSONArray(), GPListCaActivity.this);
                        gpRecyclerView.setAdapter(adaptorCaGPList);
                        Toast.makeText(this, ""+responseModel.getMsg(), Toast.LENGTH_SHORT).show();
//                        UIToastMessage.show(this, responseModel.getMsg());
                    }
                }

            }}catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onFailure(Object o, Throwable throwable, int i) {

    }
}
