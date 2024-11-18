package in.gov.pocra.training.activity.pmu.add_event_pmu.pmu_mem_filter_list;

import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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
import in.gov.pocra.training.event_db.EventDataBase;
import in.gov.pocra.training.model.online.ResponseModel;
import in.gov.pocra.training.util.ApConstants;
import in.gov.pocra.training.web_services.APIRequest;
import in.gov.pocra.training.web_services.APIServices;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Retrofit;

public class PmuParticipantListActivity extends AppCompatActivity implements OnMultiRecyclerItemClickListener, ApiCallbackCode {

    EventDataBase eDB;
    private ImageView homeBack;

    private String roleId;
    private String userID;

    private String startDate;
    private String endDate;

    private String center = "";
    private String districtId = "";
    private String subDivisionId = "";
    private String talukaId = "";
    private String villageId = "";
    private String pRoleId = "";
    private String staffType = "";

    private RecyclerView ffsMemRecyclerView;
    private ImageView checkAll;
    //private TextView checkAllTView;
    private EditText searchEText;
    private ImageView searchIView;
    private JSONArray POMemberWithSelected;
    private String sledFFSMemId = "";
    private Boolean allSelected = false;
    private Boolean isFFSMemSelected = false;
    private JSONArray sledPoMemArray = null;
    private AdaptorPmuParticipantList adaptorPmuParticipantList;
    private Button confirmButton;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pmu_pocra_official_list);


        /** For actionbar title in center */
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.attendance_actionbar_layout);
        AppCompatTextView actionTitleTextView = (AppCompatTextView) getSupportActionBar().getCustomView().findViewById(R.id.actionTitleTextView);
        homeBack = (ImageView) getSupportActionBar().getCustomView().findViewById(R.id.backImageView);
        checkAll = (ImageView) getSupportActionBar().getCustomView().findViewById(R.id.addPersonImageView);
        checkAll.setImageDrawable(getResources().getDrawable(R.drawable.ic_check_all_white));
        checkAll.setVisibility(View.VISIBLE);
        homeBack.setVisibility(View.VISIBLE);

        eDB = new EventDataBase(this);
        actionTitleTextView.setText(getResources().getString(R.string.title_pmu_participants));

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

        searchEText = (EditText)findViewById(R.id.searchEText);
        searchIView = (ImageView)findViewById(R.id.searchIView);
        // checkAllTView = (TextView) findViewById(R.id.checkAllTView);

        ffsMemRecyclerView = (RecyclerView) findViewById(R.id.ffsMemRecyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true);
        ffsMemRecyclerView.setLayoutManager(linearLayoutManager);

//        LinearLayoutManager llm = new LinearLayoutManager(this);
//        llm.setOrientation(LinearLayoutManager.VERTICAL);
//        ffsMemRecyclerView.setLayoutManager(llm);

        confirmButton = (Button) findViewById(R.id.confirmButton);
    }


    @Override
    protected void onResume() {
        super.onResume();

        sledPoMemArray = eDB.getSledPOMemberList();

        center = getIntent().getStringExtra("center");
        districtId = getIntent().getStringExtra("distId");
        subDivisionId = getIntent().getStringExtra("subDiv");
        talukaId = getIntent().getStringExtra("talukaId");
        staffType = getIntent().getStringExtra("staffType");
        villageId = getIntent().getStringExtra("villageId");
        pRoleId = getIntent().getStringExtra("pRoleId");

        if (!center.equalsIgnoreCase("") ) {
            getParticipantList();
        }else {
//            UIToastMessage.show(this,"Location not found");
            Toast.makeText(getBaseContext(), "Location not found", Toast.LENGTH_SHORT).show();
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


        searchEText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchEText.setCursorVisible(true);
            }
        });


        searchIView.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!center.equalsIgnoreCase("") ) {
                    // getParticipantList();
                }else {
//                    UIToastMessage.show(PmuParticipantListActivity.this,"Location not found");
                    Toast.makeText(getBaseContext(), "Location not found", Toast.LENGTH_SHORT).show();
                }
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
                if (adaptorPmuParticipantList != null){
                    PmuParticipantListActivity.this.adaptorPmuParticipantList.filter(s.toString());
                }else {
//                    UIToastMessage.show(PmuParticipantListActivity.this, "List not found");
                    Toast.makeText(getBaseContext(), "List not found", Toast.LENGTH_SHORT).show();
                }
            }
        });


        checkAll.setOnClickListener(new View.OnClickListener() {
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
                                String memId = memJSONObkject.getString("id");
                                eDB.updatePOMemSelectionDetail(memId,"1");
                                /*String memId = memJSONObkject.getString("mem_id");
                                eDB.updateGpMemIsSelected(memId,"1");*/
                            }

                            allSelected = true;
                            isFFSMemSelected = true;
                            checkAll.setImageDrawable(getResources().getDrawable(R.drawable.ic_check_all_green));
                            //checkAllTView.setText("Deselect All");

                        } else {
                            for (int i = 0; i < POMemberWithSelected.length(); i++) {
                                JSONObject memJSONObkject = POMemberWithSelected.getJSONObject(i);
                                memJSONObkject.put("is_selected",0);
                                checkedArray.put(memJSONObkject);
                                String memId = memJSONObkject.getString("id");
                                eDB.updatePOMemSelectionDetail(memId,"0");
                                /*String memId = memJSONObkject.getString("mem_id");
                                eDB.updateGpMemIsSelected(memId,"0");*/
                            }
                            isFFSMemSelected = false;
                            allSelected = false;
                            checkAll.setImageDrawable(getResources().getDrawable(R.drawable.ic_check_all_white));
                            //checkAllTView.setText("Select All");
                        }

                        adaptorPmuParticipantList = new AdaptorPmuParticipantList(PmuParticipantListActivity.this, checkedArray, PmuParticipantListActivity.this);
                        ffsMemRecyclerView.setAdapter(adaptorPmuParticipantList);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }else {
//                    UIToastMessage.show(PmuParticipantListActivity.this,"No member for selection");
                    Toast.makeText(getBaseContext(), "No member for selection", Toast.LENGTH_SHORT).show();
                }
            }
        });


        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sledPoMemArray = new JSONArray();
                try {

                    if (adaptorPmuParticipantList != null) {

                        for (int i = 0; i < adaptorPmuParticipantList.mJSONArray.length(); i++) {
                            JSONObject jsonObject = adaptorPmuParticipantList.mJSONArray.getJSONObject(i);
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
                            AppSettings.getInstance().setValue(PmuParticipantListActivity.this, ApConstants.kS_FACILITATOR_ARRAY, sledPoMemArray.toString());
                            AppSettings.getInstance().setValue(PmuParticipantListActivity.this, ApConstants.kS_COORDINATOR, ApConstants.kS_COORDINATOR);
                            finish();
                        } else {
                            finish();
                            // UIToastMessage.show(PmuParticipantListActivity.this, "Select at least one participant");
                            Toast.makeText(PmuParticipantListActivity.this, "Select at least one participant", Toast.LENGTH_SHORT).show();

                        }

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }



    private void getParticipantList() {

        JSONObject param = new JSONObject();

        if (staffType.equalsIgnoreCase(ApConstants.kEVENT_MEM_FFS_F)){
            try {

                param.put("level", center);
                param.put("district_id", districtId);
                param.put("subDiv_id", subDivisionId);
                param.put("role_id", pRoleId);
                param.put("taluka_id", talukaId);
                param.put("village_id", villageId);
                param.put("api_key", ApConstants.kAUTHORITY_KEY);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else if (staffType.equalsIgnoreCase(ApConstants.kEVENT_MEM_PFS)){
            try {
                param.put("level", center);
                param.put("district_id", districtId);
                param.put("subDiv_id", subDivisionId);
                param.put("role_id", pRoleId);
                param.put("taluka_id", talukaId);
                param.put("village_id", villageId);
                param.put("api_key", ApConstants.kAUTHORITY_KEY);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        RequestBody requestBody = AppUtility.getInstance().getRequestBody(param.toString());
        AppinventorApi api = new AppinventorApi(this, APIServices.BASE_URL, "", ApConstants.kMSG, true);
        Retrofit retrofit = api.getRetrofitInstance();
        APIRequest apiRequest = retrofit.create(APIRequest.class);
        //chk g
        Call<JsonObject> responseCall = apiRequest.getPmuParticipantListRequest(requestBody);

        DebugLog.getInstance().d("pmu_official_list_param=" + responseCall.request().toString());
        DebugLog.getInstance().d("pmu_official_list_param=" + AppUtility.getInstance().bodyToString(responseCall.request()));

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


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
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

                AppSettings.getInstance().setValue(PmuParticipantListActivity.this, ApConstants.kS_FACILITATOR_ARRAY, sledPoMemArray.toString());
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
                    if (pocraOffMemIsSelected.equalsIgnoreCase("1")) {
                        eDB.updateFfsParticipantsTableDetail(pocraOffMemId,pocraOffMemRoleId,pocraOffMemFName,pocraOffMemMName,pocraOffMemLName,
                                pocraOffMemMobile,pocraOffMemGender,pocraOffMemDesignation,talukaId,pocraOffMemIsSelected);
                    } else {
                        eDB.updateFfsParticipantsTableDetail(pocraOffMemId,pocraOffMemRoleId,pocraOffMemFName,pocraOffMemMName,pocraOffMemLName,
                                pocraOffMemMobile,pocraOffMemGender,pocraOffMemDesignation,talukaId,pocraOffMemIsSelected);
                    }
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
                            Log.d("Loginpmu","*5"+dataArray.length());
                            Log.d("Loginpmu","*6"+dataArray);

                            POMemberWithSelected = POmemArrayWithSelection(dataArray);
                            insertPocraOffParticipantDetail(POMemberWithSelected);
                            adaptorPmuParticipantList = new AdaptorPmuParticipantList(PmuParticipantListActivity.this, POMemberWithSelected, PmuParticipantListActivity.this);
                            ffsMemRecyclerView.setAdapter(adaptorPmuParticipantList);
                        }
                    } else {
                       // UIToastMessage.show(this, pOfficalLModel.getMsg());
                        Toast.makeText(this, pOfficalLModel.getMsg(), Toast.LENGTH_SHORT).show();
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


    // To get village array with selected village
    private JSONArray POmemArrayWithSelection(JSONArray facilitatorJSONArray) {

        try {

            if (sledPoMemArray != null) {

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
