package in.gov.pocra.training.activity.ps_hrd.add_edit_event_ps.add_edit_other_member;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
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
import in.gov.pocra.training.model.online.ResponseModel;
import in.gov.pocra.training.util.ApConstants;
import in.gov.pocra.training.web_services.APIRequest;
import in.gov.pocra.training.web_services.APIServices;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Retrofit;

public class OtherParticipantListActivity extends AppCompatActivity implements ApiCallbackCode, OnMultiRecyclerItemClickListener {

    private ImageView homeBack;
    private String roleId = "";
    private String userID = "";
    private String startDate = "";
    private String endDate = "";

    private TextView checkAllTView;
    private Boolean allSelected = false;
    private Boolean isOtherMemSelected = false;

    private ImageView addPersonImageView;
    private RecyclerView otherParticipantsRecyclerView;
    private Button confirmButton;
    private JSONArray othParticipantWithSelected ;
    private JSONArray sledOthParticipantsArray  = null;
    private AdaptorOtherParticipantList adaptorOtherParticipantList;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_participants_add_edit);

        /* ** For actionbar title in center ***/
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.attendance_actionbar_layout);
        AppCompatTextView actionTitleTextView = (AppCompatTextView) getSupportActionBar().getCustomView().findViewById(R.id.actionTitleTextView);
        homeBack = (ImageView) getSupportActionBar().getCustomView().findViewById(R.id.backImageView);
        addPersonImageView = (ImageView) getSupportActionBar().getCustomView().findViewById(R.id.addPersonImageView);
        homeBack.setVisibility(View.VISIBLE);
        addPersonImageView.setVisibility(View.VISIBLE);
        actionTitleTextView.setText("Other Participants");

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
        otherParticipantsRecyclerView = (RecyclerView) findViewById(R.id.otherParticipantsRecyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        otherParticipantsRecyclerView.setLayoutManager(linearLayoutManager);
        confirmButton = (Button) findViewById(R.id.confirmButton);
    }


    @Override
    protected void onResume() {
        super.onResume();

        String sledOthParticipats = AppSettings.getInstance().getValue(this, ApConstants.kS_OTH_PARTICIPANTS_ARRAY, ApConstants.kS_OTH_PARTICIPANTS_ARRAY);
        try {
            if (!sledOthParticipats.equalsIgnoreCase("kS_OTH_PARTICIPANTS_ARRAY")) {
                sledOthParticipantsArray = new JSONArray(sledOthParticipats);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        getOtherParticipantsList();
    }


    private void defaultConfiguration() {

        // For Action Bar
        homeBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // For Action Bar
        addPersonImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OtherParticipantListActivity.this, AddEditPsOtherParticipantsActivity.class);
                intent.putExtra("type", "Add");
                startActivity(intent);
            }
        });


        checkAllTView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (othParticipantWithSelected != null) {

                    JSONArray checkedArray = new JSONArray();

                    try {

                        if (!allSelected) {
                            for (int i = 0; i < othParticipantWithSelected.length(); i++) {
                                JSONObject memJSONObkject = othParticipantWithSelected.getJSONObject(i);
                                memJSONObkject.put("is_selected",1);
                                checkedArray.put(memJSONObkject);
                            }

                            allSelected = true;
                            isOtherMemSelected = true;

                            checkAllTView.setText("Deselect All");

                        } else {
                            for (int i = 0; i < othParticipantWithSelected.length(); i++) {
                                JSONObject memJSONObkject = othParticipantWithSelected.getJSONObject(i);
                                memJSONObkject.put("is_selected",0);
                                checkedArray.put(memJSONObkject);
                            }
                            isOtherMemSelected = false;
                            allSelected = false;

                            checkAllTView.setText("Select All");
                        }

                        adaptorOtherParticipantList = new AdaptorOtherParticipantList(OtherParticipantListActivity.this, checkedArray, OtherParticipantListActivity.this);
                        otherParticipantsRecyclerView.setAdapter(adaptorOtherParticipantList);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }else {
//                    UIToastMessage.show(OtherParticipantListActivity.this,"No member for selection");
                    Toast.makeText(getBaseContext(), "No member for selection", Toast.LENGTH_SHORT).show();
                }
            }
        });


        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sledOthParticipantsArray = new JSONArray();
                try {

                    if (adaptorOtherParticipantList != null) {

                        for (int i = 0; i < adaptorOtherParticipantList.mJSONArray.length(); i++) {
                            JSONObject jsonObject = adaptorOtherParticipantList.mJSONArray.getJSONObject(i);

                            if (jsonObject.getInt("is_selected") == 1) {
                                if (sledOthParticipantsArray.length() < 101) {
                                    sledOthParticipantsArray.put(jsonObject);
                                } else {
                                    Toast.makeText(OtherParticipantListActivity.this, "More than 101 other participant not allowed", Toast.LENGTH_SHORT).show();
//                                    UIToastMessage.show(OtherParticipantListActivity.this, "More than 101 other participant not allowed");
                                    break;
                                }
                            }
                        }

                        if (sledOthParticipantsArray.length() > 0) {
                            AppSettings.getInstance().setValue(OtherParticipantListActivity.this, ApConstants.kS_OTH_PARTICIPANTS_ARRAY, sledOthParticipantsArray.toString());
                            AppSettings.getInstance().setValue(OtherParticipantListActivity.this, ApConstants.kS_COORDINATOR, ApConstants.kS_COORDINATOR);
                            finish();
                        } else {
                            Toast.makeText(OtherParticipantListActivity.this, "Select at least one other participant", Toast.LENGTH_SHORT).show();
//                            UIToastMessage.show(OtherParticipantListActivity.this, "Select at least one other participant");
                        }

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }


    @Override
    public void onMultiRecyclerViewItemClick(int i, Object o) {

        try {
            JSONObject jsonObject = (JSONObject) o;
            String itemId = jsonObject.getString("id");
            String isSelected = jsonObject.getString("is_selected");

            if (sledOthParticipantsArray != null) {

                for (int k = 0; k < sledOthParticipantsArray.length(); k++) {
                    JSONObject sledJSONObj = sledOthParticipantsArray.getJSONObject(k);
                    String sledId = sledJSONObj.getString("id");

                    if (sledId.equalsIgnoreCase(itemId) && isSelected.equalsIgnoreCase("0")) {
                        sledOthParticipantsArray.remove(k);
                    }
                }

                AppSettings.getInstance().setValue(OtherParticipantListActivity.this, ApConstants.kS_OTH_PARTICIPANTS_ARRAY, sledOthParticipantsArray.toString());

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }


    private void getOtherParticipantsList() {

        try {

            // To get Detail
            JSONObject param = new JSONObject();
            param.put("role_id", roleId);
            param.put("created_by", userID);
            param.put("start_date", startDate);
            param.put("end_date", endDate);
            param.put("search_string", ""); // Type and designation having same Id. (According API developer)
            param.put("api_key", ApConstants.kAUTHORITY_KEY);

            RequestBody requestBody = AppUtility.getInstance().getRequestBody(param.toString());
            AppinventorApi api = new AppinventorApi(this, APIServices.BASE_URL, "", ApConstants.kMSG, true);
            Retrofit retrofit = api.getRetrofitInstance();
            APIRequest apiRequest = retrofit.create(APIRequest.class);
            Call<JsonObject> responseCall = apiRequest.getOtherParticipantsRequest(requestBody);

            DebugLog.getInstance().d("get_other_participants_list_param =" + responseCall.request().toString());
            DebugLog.getInstance().d("get_other_participants_list_param =" + AppUtility.getInstance().bodyToString(responseCall.request()));

            api.postRequest(responseCall, this, 1);


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
                    ResponseModel fLModel = new ResponseModel(jsonObject);
                    if (fLModel.isStatus()) {
                        JSONArray  participantsJSONArray = fLModel.getData();
                        if (participantsJSONArray.length() > 0) {
                            String allOthPartiId = AppUtility.getInstance().componentSeparatedByCommaJSONArray(participantsJSONArray, "id");
                            AppSettings.getInstance().setValue(this,ApConstants.kS_ALL_OTH_PARTICIPANTS_ARRAY,allOthPartiId);
                            othParticipantWithSelected = othParticipantArrayWithSelection(participantsJSONArray);
                            adaptorOtherParticipantList = new AdaptorOtherParticipantList(OtherParticipantListActivity.this, othParticipantWithSelected, OtherParticipantListActivity.this);
                            otherParticipantsRecyclerView.setAdapter(adaptorOtherParticipantList);
                        }
                    } else {
                        Toast.makeText(this, ""+fLModel.getMsg(), Toast.LENGTH_SHORT).show();
//                        UIToastMessage.show(this, fLModel.getMsg());
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
    private JSONArray othParticipantArrayWithSelection(JSONArray participantsJSONArray) {

        try {

            if (sledOthParticipantsArray != null) {

                for (int i = 0; i < sledOthParticipantsArray.length(); i++) {

                    String itemId = sledOthParticipantsArray.getJSONObject(i).getString("id");

                    for (int j = 0; j < participantsJSONArray.length(); j++) {
                        JSONObject jsonObject = participantsJSONArray.getJSONObject(j);
                        String resPerId = jsonObject.getString("id");

                        if (resPerId.equalsIgnoreCase(itemId)) {
                            jsonObject.put("is_selected", "1");
                            participantsJSONArray.put(j, jsonObject);
                        }
                    }
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return participantsJSONArray;
    }


}
