package in.gov.pocra.training.activity.ps_hrd.add_edit_event_ps.vcrmc_mem_selection;

import android.content.DialogInterface;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
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
import in.gov.pocra.training.activity.common.AdaptorVCRMCList;
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

public class GPMembersListActivity extends AppCompatActivity implements OnMultiRecyclerItemClickListener, ApiCallbackCode {

    EventDataBase eDB;
    private ImageView homeBack;

    private String gpId;
    private String gpName;

    private RecyclerView gpMemRecyclerView;
    //private ImageView checkAll;
    private TextView checkAllTView;
    private JSONArray gpMemArray;
    private String sledGpMemId = "";
    private JSONArray sledGpArray = null;
    private Boolean allSelected = false;
    private Boolean isGpMemSelected = false;
    private JSONArray sledGPMemArray = null;
    private AdaptorGPMembersList adaptorGPMembersList;
    private Button confirmButton;
    private String talukaID;
    private AdaptorDesignationMembersList adaptorDesignationMembersList;
    private String typeOfList;
    private AdaptorVCRMCList adaptorVCRMCList;
    private EditText searchEText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gp_members_list);


        /** For actionbar title in center */
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.attendance_actionbar_layout);
        AppCompatTextView actionTitleTextView = (AppCompatTextView) getSupportActionBar().getCustomView().findViewById(R.id.actionTitleTextView);
        homeBack = (ImageView) getSupportActionBar().getCustomView().findViewById(R.id.backImageView);
        searchEText = (EditText)findViewById(R.id.searchEText);

        // checkAll = (ImageView) getSupportActionBar().getCustomView().findViewById(R.id.addPersonImageView);
        // checkAll.setImageDrawable(getResources().getDrawable(R.drawable.ic_check_all_white));
        //checkAll.setVisibility(View.VISIBLE);
        // homeBack.setVisibility(View.VISIBLE);

        eDB = new EventDataBase(this);

        gpId = getIntent().getStringExtra("gpId").trim();
        gpName = getIntent().getStringExtra("gpName");
        talukaID = getIntent().getStringExtra("talukaID");
        typeOfList = getIntent().getStringExtra("type");
        String title = getIntent().getStringExtra("gpName") + " Members";

        if (!title.equalsIgnoreCase("")) {
            actionTitleTextView.setText(title);
        } else {
            actionTitleTextView.setText(getResources().getString(R.string.title_vcrmc_members));
        }


        initialization();
        defaultConfiguration();
    }


    private void initialization() {

        checkAllTView = (TextView) findViewById(R.id.checkAllTView);

        gpMemRecyclerView = (RecyclerView) findViewById(R.id.gpMemRecyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        gpMemRecyclerView.setLayoutManager(linearLayoutManager);

        confirmButton = (Button) findViewById(R.id.confirmButton);
    }


    @Override
    protected void onResume() {
        super.onResume();

        // To get Gp Member List
        if (typeOfList.equals("VCRMC")) {

            getGpMemberDetail();

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
                    GPMembersListActivity.this.adaptorGPMembersList.filter(s.toString());

                }
            });
        }
        if (typeOfList.equals("designation")) {
            getDesignationMember();

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
                    GPMembersListActivity.this.adaptorDesignationMembersList.filter(s.toString());

                }
            });
        }
    }

    private void getDesignationMember() {
        gpMemArray =  eDB.getGpMemberListBydesignationId(gpId,talukaID);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("taluka_id", talukaID);
            jsonObject.put("role_id", gpId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString());
        AppinventorApi api = new AppinventorApi(this, APIServices.BASE_URL, "", ApConstants.kMSG, true);
        Retrofit retrofit = api.getRetrofitInstance();
        APIRequest apiRequest = retrofit.create(APIRequest.class);
        Call<JsonObject> responseCall = apiRequest.getVCRMC_member_designation(requestBody);
        api.postRequest(responseCall, this, 1);

        DebugLog.getInstance().d("get_Scheduled_by_dist_param=" + responseCall.request().toString());
        DebugLog.getInstance().d("get_Scheduled_by_dist_param=" + AppUtility.getInstance().bodyToString(responseCall.request()));
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        /*if (isGpMemSelected) {
            askUserBackPermission();
        } else {
            super.onBackPressed();
        }*/
    }


    private void defaultConfiguration() {




        checkAllTView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (gpMemArray != null) {

                    try {

                        if (!allSelected) {
//                            confirmButton.setText("Confirm");
                            for (int i = 0; i < gpMemArray.length(); i++) {
                                JSONObject memJSONObkject = gpMemArray.getJSONObject(i);
                                String memId = memJSONObkject.getString("mem_id");
                                eDB.updateGpMemIsSelected(memId, "1");
                            }

                            allSelected = true;
                            isGpMemSelected = true;
                            //checkAll.setImageDrawable(getResources().getDrawable(R.drawable.ic_check_all_green));
                            checkAllTView.setText("Deselect All");

                        } else {
//                            confirmButton.setText("Cancel");
                            for (int i = 0; i < gpMemArray.length(); i++) {
                                JSONObject memJSONObkject = gpMemArray.getJSONObject(i);
                                String memId = memJSONObkject.getString("mem_id");
                                eDB.updateGpMemIsSelected(memId, "0");
                            }
                            isGpMemSelected = false;
                            allSelected = false;
                            // checkAll.setImageDrawable(getResources().getDrawable(R.drawable.ic_check_all_white));
                            checkAllTView.setText("Select All");
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    // To get Gp Member List
                    if (typeOfList.equals("VCRMC")) {


                        getGpMemberDetail();}
                    if (typeOfList.equals("designation")) {
                        getDesignationMember();

                    }

                } else {
                    UIToastMessage.show(GPMembersListActivity.this, "No member for selection");
                }
            }
        });


        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {

                    if (gpMemArray != null) {
                        if (typeOfList.equals("VCRMC")) {
                            if (!eDB.isGPMemSelectedByGpId(gpId)) {
                                askUserPermission();
                            } else {
                                eDB.updateGpIsSelected(gpId, "1");
                                AppSettings.getInstance().setValue(GPMembersListActivity.this, ApConstants.kS_COORDINATOR, ApConstants.kS_COORDINATOR);
                                finish();
                            }

                        }
                        if (typeOfList.equals("designation")) {
                            if (!eDB.isGPMemSelectedByDesignationId(gpId)) {
                                askUserPermission();
                            } else {
                                eDB.updatedesignationIsSelected(gpId, "1");
                                AppSettings.getInstance().setValue(GPMembersListActivity.this, ApConstants.kS_COORDINATOR, ApConstants.kS_COORDINATOR);
                                finish();
                            }
                        }


                    } else {
                        UIToastMessage.show(GPMembersListActivity.this, "Member not available");
                    }

                } catch (Exception e) {
                    UIToastMessage.show(GPMembersListActivity.this, "Member not available");
                    //e.printStackTrace();
                }
            }
        });

    }


    private void getGpMemberDetail() {

        gpMemArray = eDB.getGpMemberListByGpId(gpId);
        adaptorGPMembersList = new AdaptorGPMembersList(GPMembersListActivity.this, gpMemArray, GPMembersListActivity.this);
        gpMemRecyclerView.setAdapter(adaptorGPMembersList);

    }


    @Override
    public void onMultiRecyclerViewItemClick(int i, Object o) {
         if(i ==2){

             try {
                 JSONObject jsonObject = (JSONObject)o;
                 String mem_id = jsonObject.getString("is_selected");
                 if (mem_id.equalsIgnoreCase("1")) {
                     isGpMemSelected = true;
                     // eDB.updateGpMemIsSelected(mem_id,"1");
                     // confirmButton.setText("Confirm");
                 }
             } catch (JSONException e) {
                 e.printStackTrace();
             }
         }else {
             JSONArray adaptArray = adaptorGPMembersList.mJSONArray;

             if (adaptArray != null) {
                 isGpMemSelected = false;
                 for (int s = 0; s < adaptArray.length(); s++) {

                     try {
                         JSONObject jsonObject = adaptArray.getJSONObject(s);
                         String mem_id = jsonObject.getString("mem_is_selected");
                         if (mem_id.equalsIgnoreCase("1")) {
                             isGpMemSelected = true;
                             // eDB.updateGpMemIsSelected(mem_id,"1");
                             // confirmButton.setText("Confirm");
                         }
                     } catch (JSONException e) {
                         e.printStackTrace();
                     }
                 }
             }
         }
    }


    private void askUserPermission() {

        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("No Members are selected for Training. Are you sure?");
        alertDialogBuilder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deselectAllMemOfGpWithGP(gpId);
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

    private void askUserBackPermission() {

        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Are you sure want to back, You may lost your selection of members?");
        alertDialogBuilder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deselectAllMemOfGpWithGP(gpId);
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

    private void deselectAllMemOfGpWithGP(String gpId) {

        if (gpMemArray != null) {

            eDB.updateGpIsSelected(gpId, "0");
            JSONArray memBerByGP = eDB.getSledGpMemIdListByGpId(gpId);
            String sledMemId = AppUtility.getInstance().componentSeparatedByCommaJSONArray(memBerByGP, "mem_id");
            String[] s = sledMemId.split(",");
            for (String sGPMemId : s) {
                eDB.updateGpMemIsSelected(sGPMemId, "0");
            }
        }

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
                            eDB.insertGPWithMemDetail(talukaID,gpId,gpName,gpCode,gpIsSelected,memId,memName,memFName,memMName,memLName,memMobile,memMobile2,
                                    memDesigID,memDesigName,memGenderId,memGenderName,memSocCatId,memSocCatName,memHoldCat,memIsSelected);
                        }

                    }
                }
            }


           JSONArray memJSONArray =  eDB.getGpMemberListBydesignationId(gpId,talukaID);
            adaptorDesignationMembersList = new AdaptorDesignationMembersList(GPMembersListActivity.this, memJSONArray, GPMembersListActivity.this,talukaID);
            gpMemRecyclerView.setAdapter(adaptorDesignationMembersList);


        } catch (JSONException e) {
            e.printStackTrace();
        }
/*
        if (progress.isShowing()) {
            progress.dismiss();
        }*/
/*
        Intent intent = new Intent(this, GPListActivity.class);
        intent.putExtra("taluka_id",talukaId);
        startActivity(intent);*/
        // finish();

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
                            if (designationJSONArray.length() > 0) {
                                if (typeOfList.equals("designation")) {
                                    insertIntoGpTable(designationJSONArray);

                                }
                            }
                            if (typeOfList.equals("VCRMC")) {
                                adaptorVCRMCList = new AdaptorVCRMCList(GPMembersListActivity.this, designationJSONArray, talukaID, GPMembersListActivity.this);
                                gpMemRecyclerView.setAdapter(adaptorGPMembersList);
                            }
                    } else {
                        UIToastMessage.show(this, "NO VCRMC (GP) member found");
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
