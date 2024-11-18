package in.gov.pocra.training.activity.ps_hrd.add_edit_event_ps.proj_official_selection;

import android.content.DialogInterface;
import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

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
import in.gov.pocra.training.event_db.EventDataBase;
import in.gov.pocra.training.model.online.ResponseModel;
import in.gov.pocra.training.util.ApConstants;
import in.gov.pocra.training.web_services.APIRequest;
import in.gov.pocra.training.web_services.APIServices;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Retrofit;

public class PocraOfficialListActivity extends AppCompatActivity implements OnMultiRecyclerItemClickListener, ApiCallbackCode {

    EventDataBase eDB;
    private ImageView homeBack;

    private String roleId;
    private String userID;

    private String startDate;
    private String endDate;

    private String talukaId = "";


    private RecyclerView ffsMemRecyclerView;
    //private ImageView checkAll;
    private TextView checkAllTView;
    private JSONArray POMemberWithSelected;
    private String sledFFSMemId = "";
    private Boolean allSelected = false;
    private Boolean isFFSMemSelected = false;
    private JSONArray sledPoMemArray = null;
    private AdaptorPocraOfficialList adaptorPocraOfficialList;
    private Button confirmButton;

    private String center;
    private String location;
    private String participentRoleId;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pocra_official_list);


        /** For actionbar title in center */
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.attendance_actionbar_layout);
        AppCompatTextView actionTitleTextView = (AppCompatTextView) getSupportActionBar().getCustomView().findViewById(R.id.actionTitleTextView);
        homeBack = (ImageView) getSupportActionBar().getCustomView().findViewById(R.id.backImageView);
        // checkAll = (ImageView) getSupportActionBar().getCustomView().findViewById(R.id.addPersonImageView);
        // checkAll.setImageDrawable(getResources().getDrawable(R.drawable.ic_check_all_white));
        //checkAll.setVisibility(View.VISIBLE);
        homeBack.setVisibility(View.VISIBLE);

        eDB = new EventDataBase(this);

        actionTitleTextView.setText("PMU Participants");

        /*String talukaName = getIntent().getStringExtra("talukaName");
        String title = talukaName + " Facilitator";
        if (!title.equalsIgnoreCase("")) {
            actionTitleTextView.setText(title);
        } else {
            actionTitleTextView.setText("Facilitators");
        }*/


        initialization();
        defaultConfiguration();
    }


    private void initialization() {

        // To get User Id and Roll Id
        String rId = AppSettings.getInstance().getValue(this, ApConstants.kROLE_ID, ApConstants.kROLE_ID);
        String uId = AppSettings.getInstance().getValue(this, ApConstants.kUSER_ID, ApConstants.kUSER_ID);
        String sDate = AppSettings.getInstance().getValue(this, ApConstants.kS_EVENT_S_DATE, ApConstants.kS_EVENT_S_DATE);
        String eDate = AppSettings.getInstance().getValue(this, ApConstants.kS_EVENT_E_DATE, ApConstants.kS_EVENT_E_DATE);

        if (!rId.equalsIgnoreCase("kROLE_ID")) {
            roleId = rId;
        }
        if (!uId.equalsIgnoreCase("kUSER_ID")) {
            userID = uId;
        }
        if (!sDate.equalsIgnoreCase("kS_EVENT_S_DATE")) {
            startDate = sDate;
        }
        if (!eDate.equalsIgnoreCase("kS_EVENT_E_DATE")) {
            endDate = eDate;
        }


        checkAllTView = (TextView) findViewById(R.id.checkAllTView);

        ffsMemRecyclerView = (RecyclerView) findViewById(R.id.ffsMemRecyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        ffsMemRecyclerView.setLayoutManager(linearLayoutManager);

        confirmButton = (Button) findViewById(R.id.poConfirmButton);
    }


    @Override
    protected void onResume() {
        super.onResume();

        sledPoMemArray = eDB.getSledPOMemberList();

        /*String sledFacilitator = AppSettings.getInstance().getValue(this, ApConstants.kS_FACILITATOR_ARRAY, ApConstants.kS_FACILITATOR_ARRAY);
        try {
            if (!sledFacilitator.equalsIgnoreCase("kS_FACILITATOR_ARRAY")) {
                sledPoMemArray = new JSONArray(sledFacilitator);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }*/

        // sledPoMemArray = eDB.getSledFarmerList();

        /*talukaId = getIntent().getStringExtra("talukaId");
        if (!talukaId.equalsIgnoreCase("")) {
            getFacilitatorDetail(talukaId);
        }*/

        center = getIntent().getStringExtra("center");
        location = getIntent().getStringExtra("location");
        participentRoleId = getIntent().getStringExtra("pRoleId");

        if (!center.equalsIgnoreCase("") && !location.equalsIgnoreCase("")  && !participentRoleId.equalsIgnoreCase("") ) {
            // getFacilitatorDetail(talukaId);

            getParticipantByRoleAndLocation(center,location,participentRoleId);
        }

    }




    @Override
    public void onBackPressed() {
        // super.onBackPressed();
        if (isFFSMemSelected) {
            askUserBackPermission();
        } else {
            super.onBackPressed();
        }
    }


    private void defaultConfiguration() {

        // For Action Bar
        homeBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFFSMemSelected) {
                    askUserBackPermission();
                } else {
                    finish();
                }
            }
        });


        checkAllTView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (POMemberWithSelected != null) {

                    JSONArray checkedArray = new JSONArray();

                    try {

                        if (!allSelected) {

                            for (int i = 0; i < POMemberWithSelected.length(); i++) {
                                JSONObject memJSONObkject = POMemberWithSelected.getJSONObject(i);
                                memJSONObkject.put("is_selected",1);
                                checkedArray.put(memJSONObkject);

                                /*String memId = memJSONObkject.getString("mem_id");
                                eDB.updateGpMemIsSelected(memId,"1");*/
                            }

                            allSelected = true;
                            isFFSMemSelected = true;
                            //checkAll.setImageDrawable(getResources().getDrawable(R.drawable.ic_check_all_green));
                            checkAllTView.setText("Deselect All");

                        } else {
                            for (int i = 0; i < POMemberWithSelected.length(); i++) {
                                JSONObject memJSONObkject = POMemberWithSelected.getJSONObject(i);
                                memJSONObkject.put("is_selected",0);
                                checkedArray.put(memJSONObkject);
                                /*String memId = memJSONObkject.getString("mem_id");
                                eDB.updateGpMemIsSelected(memId,"0");*/
                            }
                            allSelected = false;
                            isFFSMemSelected = false;
                            // checkAll.setImageDrawable(getResources().getDrawable(R.drawable.ic_check_all_white));
                            checkAllTView.setText("Select All");
                        }

                        adaptorPocraOfficialList = new AdaptorPocraOfficialList(PocraOfficialListActivity.this, checkedArray, PocraOfficialListActivity.this);
                        ffsMemRecyclerView.setAdapter(adaptorPocraOfficialList);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }else {
                    UIToastMessage.show(PocraOfficialListActivity.this,"No member for selection");
                }
            }
        });


        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sledPoMemArray = new JSONArray();
                try {

                    if (adaptorPocraOfficialList != null) {
                        JSONArray poOffArray = adaptorPocraOfficialList.mJSONArray;
                        for (int i = 0; i < poOffArray.length(); i++) {
                            JSONObject jsonObject = poOffArray.getJSONObject(i);
                            String memId = jsonObject.getString("id");
                            if (jsonObject.getInt("is_selected") == 1) {
                                eDB.updatePOMemSelectionDetail(memId,"1");
                                sledPoMemArray.put(jsonObject);
                            }else {
                                eDB.updatePOMemSelectionDetail(memId,"0");
                                sledPoMemArray.put(jsonObject);
                            }
                        }

                        if (sledPoMemArray.length() > 0) {

                            AppSettings.getInstance().setValue(PocraOfficialListActivity.this, ApConstants.kS_FACILITATOR_ARRAY, sledPoMemArray.toString());
                            AppSettings.getInstance().setValue(PocraOfficialListActivity.this, ApConstants.kS_COORDINATOR, ApConstants.kS_COORDINATOR);
                            finish();
                        } else {
                            finish();
                        }

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }



    private void getParticipantByRoleAndLocation(String center, String location, String roleId) {

        JSONObject jsonObject = new JSONObject();
        try {

            jsonObject.put("center", center);
            jsonObject.put("location", location);
            jsonObject.put("role_id", roleId);
            // jsonObject.put( "secret", "sfIsJ1xRGaRdeptbXw");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString());
        AppinventorApi api = new AppinventorApi(this, APIServices.SERVICE_API_URL, "", ApConstants.kMSG, true);
        Retrofit retrofit = api.getRetrofitInstance();
        APIRequest apiRequest = retrofit.create(APIRequest.class);
        Call<JsonObject> responseCall = apiRequest.getParticipantRoleAndLocationRequest(requestBody);

        DebugLog.getInstance().d("pocra_official_list_param=" + responseCall.request().toString());
        DebugLog.getInstance().d("pocra_official_list_param=" + AppUtility.getInstance().bodyToString(responseCall.request()));

        api.postRequest(responseCall, this, 1);

    }


    private void getFacilitatorDetail(String taluka) {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("role_id", roleId);
            jsonObject.put("user_id", userID);
            jsonObject.put("taluka_id", taluka);
            jsonObject.put("search_string", "");
            jsonObject.put("start_date", startDate);
            jsonObject.put("end_date", endDate);
            jsonObject.put("api_key", ApConstants.kAUTHORITY_KEY);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString());
        AppinventorApi api = new AppinventorApi(this, APIServices.BASE_URL, "", ApConstants.kMSG, true);
        Retrofit retrofit = api.getRetrofitInstance();
        APIRequest apiRequest = retrofit.create(APIRequest.class);
        Call<JsonObject> responseCall = apiRequest.facilitatorListRequest(requestBody);

        DebugLog.getInstance().d("Facilitator_list_param=" + responseCall.request().toString());
        DebugLog.getInstance().d("Facilitator_list_param=" + AppUtility.getInstance().bodyToString(responseCall.request()));

        api.postRequest(responseCall, this, 1);

    }


    @Override
    public void onMultiRecyclerViewItemClick(int i, Object o) {

        try {
            JSONObject jsonObject = (JSONObject) o;
            String itemId = jsonObject.getString("id");
            String isSelected = jsonObject.getString("is_selected");

            if (sledPoMemArray != null) {

                for (int k = 0; k < sledPoMemArray.length(); k++) {
                    JSONObject sledJSONObj = sledPoMemArray.getJSONObject(k);
                    String sledId = sledJSONObj.getString("id");

                    if (sledId.equalsIgnoreCase(itemId) && isSelected.equalsIgnoreCase("0")) {
                        sledPoMemArray.remove(k);
                    }
                }

                AppSettings.getInstance().setValue(PocraOfficialListActivity.this, ApConstants.kS_FACILITATOR_ARRAY, sledPoMemArray.toString());
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    private void askUserBackPermission() {

        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Are you sure want to back, You may lost your selection of members?");
        alertDialogBuilder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // deselectAllMemOfFaciByTaluka(talukaId);
                finish();
            }
        });

        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }

    /*private void deselectAllMemOfFaciByTaluka(String taluka){

        if (ffsMemArray != null) {

            JSONArray memberByTaluka = eDB.getSledGpMemIdListByGpId(taluka); // TODO
            String sledMemId = AppUtility.getInstance().componentSeparatedByCommaJSONArray(memberByTaluka, "mem_id");
            String[] s = sledMemId.split(",");
            for (String sFFSMemId:s){
                eDB.updateGpMemIsSelected(sFFSMemId,"0");  // TODO
            }
        }
    }*/



    private void insertPocraOffParticipantDetail(JSONArray facilitatorJSONArray) {

        try {

            for (int i = 0; i < facilitatorJSONArray.length(); i++) {
                JSONObject faciJSON  = facilitatorJSONArray.getJSONObject(i);

                String pocraOffMemId = faciJSON.getString("id");
                String pocraOffMemRoleId = faciJSON.getString("role_id");
                String pocraOffMemFName = faciJSON.getString("first_name");
                String pocraOffMemMName = faciJSON.getString("middle_name");
                String pocraOffMemLName = faciJSON.getString("last_name");
                String pocraOffMemMobile = faciJSON.getString("mobile");
                String pocraOffMemGender = faciJSON.getString("gender");
                String pocraOffMemDesignation = faciJSON.getString("post");
                String pocraOffMemIsSelected = faciJSON.getString("is_selected");

                if (!eDB.isFacilitatorExist(pocraOffMemId)) {
                    eDB.insertFfsParticipantsDetail(pocraOffMemId,pocraOffMemRoleId,pocraOffMemFName,pocraOffMemMName,pocraOffMemLName,
                            pocraOffMemMobile,pocraOffMemGender,pocraOffMemDesignation,talukaId,pocraOffMemIsSelected);
                } else {
                    eDB.updateFfsParticipantsTableDetail(pocraOffMemId,pocraOffMemRoleId,pocraOffMemFName,pocraOffMemMName,pocraOffMemLName,
                            pocraOffMemMobile,pocraOffMemGender,pocraOffMemDesignation,talukaId,pocraOffMemIsSelected);

                    /*if (pocraOffMemIsSelected.equalsIgnoreCase("1")) {
                        eDB.updateFfsParticipantsTableDetail(pocraOffMemId,pocraOffMemRoleId,pocraOffMemFName,pocraOffMemMName,pocraOffMemLName,
                                pocraOffMemMobile,pocraOffMemGender,pocraOffMemDesignation,talukaId,pocraOffMemIsSelected);
                    } else {
                        eDB.updateFfsParticipantsTableDetail(pocraOffMemId,pocraOffMemRoleId,pocraOffMemFName,pocraOffMemMName,pocraOffMemLName,
                                pocraOffMemMobile,pocraOffMemGender,pocraOffMemDesignation,talukaId,pocraOffMemIsSelected);
                    }*/
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    @Override
    public void onResponse(JSONObject jsonObject, int i) {
        try {

            if (jsonObject != null) {

                // Event type Response
                if (i == 1) {

                    ResponseModel pOfficalLModel = new ResponseModel(jsonObject);

                    if (pOfficalLModel.isStatus()) {
                        JSONArray dataArray = pOfficalLModel.getData();

                        if (dataArray.length() > 0) {
                            POMemberWithSelected = POmemArrayWithSelection(dataArray);
                            insertPocraOffParticipantDetail(POMemberWithSelected);
                            adaptorPocraOfficialList = new AdaptorPocraOfficialList(PocraOfficialListActivity.this, POMemberWithSelected, PocraOfficialListActivity.this);
                            ffsMemRecyclerView.setAdapter(adaptorPocraOfficialList);
                        }


                        /*if (POMemberWithSelected.length() > 0) {
                            insertPocraOffParticipantDetail(POMemberWithSelected);
                            adaptorPocraOfficialList = new AdaptorPocraOfficialList(PocraOfficialListActivity.this, POMemberWithSelected, PocraOfficialListActivity.this);
                            ffsMemRecyclerView.setAdapter(adaptorPocraOfficialList);
                        }*/

                        /* JSONArray pOffMEmJSONArray = new JSONArray();
                        if (dataArray.length()>0){
                            for (int pom = 0; pom < dataArray.length();pom++ ){
                                JSONObject pomJSON = dataArray.getJSONObject(pom);
                                pomJSON.put("is_selected",0);
                                pOffMEmJSONArray.put(pomJSON);
                            }
                        }

                        if (pOffMEmJSONArray.length() > 0) {
                            // insertPocraOffParticipantDetail(facilitatorJSONArray);
                            JSONArray POMemberWithSelected = POmemArrayWithSelection(pOffMEmJSONArray);
                            adaptorPocraOfficialList = new AdaptorPocraOfficialList(PocraOfficialListActivity.this, POMemberWithSelected, PocraOfficialListActivity.this);
                            ffsMemRecyclerView.setAdapter(adaptorPocraOfficialList);
                        }*/

                    } else {
                        UIToastMessage.show(this, pOfficalLModel.getMsg());
                    }


                    /*if (fLModel.isStatus()) {
                        JSONObject dataJSON = jsonObject.getJSONObject("data");
                        JSONArray facilitatorJSONArray = dataJSON.getJSONArray("facilitator");

                        if (facilitatorJSONArray.length() > 0) {
                            insertPocraOffParticipantDetail(facilitatorJSONArray);
                            JSONArray POMemberWithSelected = POmemArrayWithSelection(facilitatorJSONArray);
                            adaptorPocraOfficialList = new AdaptorPocraOfficialList(PocraOfficialListActivity.this, POMemberWithSelected, PocraOfficialListActivity.this);
                            ffsMemRecyclerView.setAdapter(adaptorPocraOfficialList);
                        }
                    } else {
                        UIToastMessage.show(this, fLModel.getMsg());
                    }*/
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFailure(Object o, Throwable throwable, int i) {

    }


    // To get village array with selected village
    private JSONArray POmemArrayWithSelection(JSONArray facilitatorJSONArray) {

        try {

            if (sledPoMemArray != null && sledPoMemArray.length()>0) {

                for (int i = 0; i < sledPoMemArray.length(); i++) {

                    String memID = sledPoMemArray.getJSONObject(i).getString("id");

                    for (int j = 0; j < facilitatorJSONArray.length(); j++) {
                        JSONObject jsonObject = facilitatorJSONArray.getJSONObject(j);
                        String resPerId = jsonObject.getString("id");

                        if (resPerId.equalsIgnoreCase(memID)) {
                            jsonObject.put("is_selected", "1");
                            facilitatorJSONArray.put(j, jsonObject);
                        }
                    }
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return facilitatorJSONArray;
    }
}
