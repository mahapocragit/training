package in.gov.pocra.training.activity.ca.add_edit_event_ca;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import in.co.appinventor.services_api.api.AppinventorApi;
import in.co.appinventor.services_api.api.AppinventorIncAPI;
import in.co.appinventor.services_api.app_util.AppUtility;
import in.co.appinventor.services_api.debug.DebugLog;
import in.co.appinventor.services_api.listener.AlertListCallbackEventListener;
import in.co.appinventor.services_api.listener.ApiCallbackCode;
import in.co.appinventor.services_api.listener.ApiJSONObjCallback;
import in.co.appinventor.services_api.listener.DatePickerCallbackListener;
import in.co.appinventor.services_api.listener.OnMultiRecyclerItemClickListener;
import in.co.appinventor.services_api.settings.AppSettings;
import in.co.appinventor.services_api.widget.UIToastMessage;
import in.gov.pocra.training.R;
import in.gov.pocra.training.activity.ca.add_edit_event_ca.ca_farmer_filter.AdaptorSelectedFPC;
import in.gov.pocra.training.activity.ca.add_edit_event_ca.ca_farmer_filter.AdaptorSelectedFR;
import in.gov.pocra.training.activity.ca.add_edit_event_ca.ca_farmer_filter.AdaptorSelectedSGF;
import in.gov.pocra.training.activity.ca.add_edit_event_ca.ca_farmer_filter.CaFarmerFilterActivity;
import in.gov.pocra.training.activity.ca.add_edit_event_ca.ca_farmer_filter.FPCgrFarmerCaActivity;
import in.gov.pocra.training.activity.ca.add_edit_event_ca.ca_farmer_filter.FRgrFarmerCaActivity;
import in.gov.pocra.training.activity.ca.add_edit_event_ca.ca_farmer_filter.SHGgrFarmerCaActivity;
import in.gov.pocra.training.activity.ca.add_edit_event_ca.ca_mem_filter_list.CaFieldStaffFilterActivity;
import in.gov.pocra.training.activity.ca.add_edit_event_ca.ca_mem_filter_list.CaParticipantFilterActivity;
import in.gov.pocra.training.activity.ca.person_list.ResourcePersonActivity;
import in.gov.pocra.training.activity.ca.add_edit_event_ca.vcrmc_mem_selection.GPListCaActivity;
import in.gov.pocra.training.activity.common.co_coordinator_list.CoCoordinatorListActivity;
import in.gov.pocra.training.activity.common.coordinator_list.CoordinatorListActivity;
import in.gov.pocra.training.activity.common.coordinator_list.SearchPmuMemActivity;
import in.gov.pocra.training.activity.common.participantsList.ParticipantGPListActivity;
import in.gov.pocra.training.activity.common.participantsList.ParticipantsListActivity;
import in.gov.pocra.training.activity.ps_hrd.add_edit_event_ps.AdaptorSelectedCoCoord;
import in.gov.pocra.training.activity.ps_hrd.add_edit_event_ps.AdaptorSelectedCoordinator;
import in.gov.pocra.training.activity.ps_hrd.add_edit_event_ps.AdaptorSelectedFarmer;
import in.gov.pocra.training.activity.ps_hrd.add_edit_event_ps.AdaptorSelectedGP;
import in.gov.pocra.training.activity.ps_hrd.add_edit_event_ps.AdaptorSelectedOthParticipants;
import in.gov.pocra.training.activity.ps_hrd.add_edit_event_ps.AdaptorSelectedPOMember;
import in.gov.pocra.training.activity.ps_hrd.add_edit_event_ps.add_edit_other_member.OtherParticipantListActivity;
import in.gov.pocra.training.activity.ps_hrd.add_edit_event_ps.ps_farmer_filter.PsHrdFarmerFilterActivity;
import in.gov.pocra.training.activity.ps_hrd.add_edit_event_ps.vcrmc_mem_selection.TalukaListActivity;
import in.gov.pocra.training.event_db.EventDataBase;
import in.gov.pocra.training.model.online.GPMemberDetailModel;
import in.gov.pocra.training.model.online.GPModel;
import in.gov.pocra.training.model.online.ProfileModel;
import in.gov.pocra.training.model.online.ResponseModel;
import in.gov.pocra.training.model.online.TrainingDetailModel;
import in.gov.pocra.training.util.ApConstants;
import in.gov.pocra.training.util.ApUtil;
import in.gov.pocra.training.util.TimePickerCallbackListener;
import in.gov.pocra.training.web_services.APIRequest;
import in.gov.pocra.training.web_services.APIServices;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Retrofit;

public class AddEditEventCaActivity extends AppCompatActivity implements DatePickerCallbackListener, AlertListCallbackEventListener, ApiJSONObjCallback, ApiCallbackCode, OnMultiRecyclerItemClickListener, TimePickerCallbackListener {

    EventDataBase eDB;
    private ProgressDialog progress;

    private ImageView homeBack;
    private String roleId = "";
    private String userID = "";
    private String userLaval = "";
    private String districtId = "";
    private String districtName = "";
    private String subDivisionId = "";
    private String subDivisionName;
    private String schId = "";
    private String talukaId = "";
    private String actionType = "create";
    private String userMobile;

    private LinearLayout eventTypeLLayout;
    private TextView eventTypeTextView;
    private JSONArray eventTypeJSONArray = null;
    private String eventTypeId = "";
    private String eventType = "";
    private boolean isBeneficiaryFarmers = false;
    String[] strArr = null;
    private LinearLayout eventSubTypeLLayout;
    private TextView eventSubTypeTView;
    private JSONArray eventSubTypeJSONArray = null;
    private String eventSubTypeId = "";
    private String eventSubType = "";

    private LinearLayout eventTitleLLayout;
    private TextView eventTitleEditText;
    private String otherType;

    private RelativeLayout eventStartDateLLayout;
    private TextView eventStartDateTextView;
    private String eventStartDate = "";
    private String scheduledStartDate = "";
    private RelativeLayout eventEndDateLLayout;
    private TextView eventEndDateTextView;
    private String eventEndDate = "";
    private String scheduledEndDate = "";

    private RelativeLayout eventSTimeRLayout;
    private TextView eventSTimeTView;
    private int startHour = 0;
    private int startMinuets = 0;
    private String startTime = "";
    private String startTimeAmPm = "";

    private RelativeLayout eventETimeRLayout;
    private TextView eventETimeTView;
    private int endHour = 0;
    private int endMinuets = 0;
    private String endTime = "";
    private String endTimeAmPm = "";

    // For Report Day and time
    private RelativeLayout eventRDateRLayout;
    private TextView eventRDateTView;
    private String reportDate = "";

    private RelativeLayout eventRTimeRLayout;
    private TextView eventRTimeTView;
    private int reportHour = 0;
    private int reportMinuets = 0;
    private String reportTime = "";
    private String reportTimeAmPm = "";

    // Participant Group
    private LinearLayout selectPGroupLLayout;
    private JSONArray partiGroupJSONArray;
    private TextView selPGroupTView;
    private String partiGroupType = "";
    private String pGroupId = "";

    // VCRMC (GP)
    private RelativeLayout selectVCRMCRLayout;
    private TextView sledVCRMCCountTView;
    private TextView sledVCRMCMoreTView;

    private TextView sledVCRMCTView;
    private LinearLayout sledGPLLayout;                     // Selected VCRMC
    private RecyclerView sledGPRView;
    private JSONArray sledGPJSONArray = null;
    private AdaptorSelectedGP adaptorSelectedGP;
    private String sledGPId = "";
    private ArrayList<String> toUpdateGpIdSled = new ArrayList<>();
    private ArrayList<String> toUpdateMem_idSled = new ArrayList<>();

    // Farmer
    private RelativeLayout selectFarmerRLayout;
    private TextView sledFarmerCountTView;
    private TextView sledFarmerMoreTView;

    private LinearLayout sledFarmerLLayout;                 // Selected Farmer
    private RecyclerView sledFarmerRView;
    private TextView sledFarmerTView;
    private JSONArray sledFarmerJSONArray = null;
    private JSONArray sledSHGJSONArray = null;
    private JSONArray sledFPCJSONArray = null;
    private JSONArray sledFRJSONArray = null;
    private JSONArray sledFActivityJSONArray = null;
    private AdaptorSelectedFarmer adaptorSelectedfarmer;
    private AdaptorSelectedSGF adaptorSelectedSGF;
    private AdaptorSelectedFPC adaptorSelectedFpc;
    private AdaptorSelectedFR adaptorSelectedFR;

    private String sledVillageId = "";
    private String sledFarVillageCode = "";
    private String sledActivityId = "";
    // Pocra official
    private RelativeLayout selectPOMRLayout;
    private TextView sledPOMCountTView;
    private TextView sledPOMMoreTView;
    private LinearLayout sledPOMemberLLayout;                 // Selected Facilitator
    private RecyclerView sledPOMemberRView;
    private TextView sledPOMemberTView;
    private JSONArray sledPOMemberJSONArray = null;
    private AdaptorSelectedPOMember adaptorSelectedPOMember;
    private JSONArray sledFacilitatorId = new JSONArray();
    // CA Resource_person
    private RelativeLayout selectCaResourceRLayout;
    private TextView selectCaResourceCountTView;
    private TextView selectCaResourceMoreTView;
    private JSONArray sledCaResourceJSONArray = null;
    private JSONArray sledCaResource = new JSONArray();

    // Other Participants
    private RelativeLayout selectOtherRLayout;
    private TextView sledOtherCountTView;
    private TextView sledOtherMoreTView;

    private LinearLayout sledOthParticipantLLayout;                 // Other Participants
    private RecyclerView sledOthParticipantRView;
    private TextView sledOthParticipantTView;
    private JSONArray sledOthParticipantJSONArray = null;
    private AdaptorSelectedOthParticipants adaptorSelectedOthParticipant;
    private JSONArray sledOthParticipantId = new JSONArray();


    //////////////////////////////////////////
    private RelativeLayout selectshgRLayout;
    private TextView sledshgCountTView;
    private TextView sledshgMoreTView;
    private TextView sledShgTView,notetxt;
    private LinearLayout sledshgLLayout;
    private RecyclerView sledshgRView;


    private RelativeLayout selectfpcRLayout;
    private TextView sledfpcCountTView;
    private TextView sledfpcMoreTView;
    private TextView sledFpcTView;
    private LinearLayout sledFpcLLayout;
    private RecyclerView sledFpcRView;


    private RelativeLayout selectFGRLayout;
    private TextView sledFGCountTView;
    private TextView sledFGMoreTView;
    private TextView sledFGTView;
    private LinearLayout sledFGLLayout;
    private RecyclerView sledFGRView;


    private ArrayList<String> toUpdateFarmer_idSled = new ArrayList<>();
    private ArrayList<String> toUpdateVillageSled = new ArrayList<>();

    private TextView participantsEditText;
    private String memCount;
    private TextView venueLocationEditText;
    private LinearLayout vDistLLayout;
    private TextView vDistTView;
    private JSONArray vDistJSONArray = null;
    private String vDistId = "";
    private String vDistName = "";
    private LinearLayout venueLLayout;
    private TextView venueLocationTView;
    private JSONArray eventVenueJSONArray = null;
    private String eventVenueId = "";
    private String eventVenue = "";
    private String eventOtherVenue;
    private LinearLayout venueELLayout;
    private LinearLayout villageLLayout;
    private TextView villageTView;
    private String census_code = "";
    private String venue_village = "";
    private JSONArray villageJSONArray = null;
    private LinearLayout kvkLLayout;
    private TextView kvkTView;
    private String kvkId = "";
    private JSONArray kvkJSONArray = null;
    private LinearLayout desigLLayout;
    //    private TextView desigTextView;
    private JSONArray desigJSONArray = null;
    private JSONArray coDesignationJSONArray = null;
    private String desigId = "";

    // Coordinator
    private LinearLayout coordinatorLLayout;
    private LinearLayout sledCordLLayout;
    private RecyclerView sledCordRView;
    private TextView coordinatorTextView;
    private JSONArray sledCordJSONArray = null;
    private AdaptorSelectedCoordinator adaptorSelectedCoordinator;
    private JSONArray sledCordId = null;
    private LinearLayout coCordLinearLayout;
    private LinearLayout sledCoCoordLLayout;
    private RecyclerView sledCoCoordRView;
    private TextView coCordTextView;
    private JSONArray sledCoCoordJSONArray = null;
    private AdaptorSelectedCoCoord adaptorSelectedCoCoord;
    private JSONArray sledCoCoordinatorId = null;
    private JSONArray sledCoCoordinatorArray = null;
    private Button createButton;
    private boolean toOnSDateUpdate = true;
    private boolean toOnEDateUpdate = true;
    String serverCurrentDate = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_event_ca);

        /** For actionbar title in center */
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.attendance_actionbar_layout);
        AppCompatTextView actionTitleTextView = (AppCompatTextView) getSupportActionBar().getCustomView().findViewById(R.id.actionTitleTextView);
        homeBack = (ImageView) getSupportActionBar().getCustomView().findViewById(R.id.backImageView);
        // addPersonImageView = (ImageView) getSupportActionBar().getCustomView().findViewById(R.id.addPersonImageView);
        homeBack.setVisibility(View.VISIBLE);
        actionTitleTextView.setText(getResources().getString(R.string.title_add_edit_event));

        eDB = new EventDataBase(this);

        initialization();
        defaultConfiguration();
        eventListener();
    }


    private void initialization() {

        // For Action Bar
        homeBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sledGPJSONArray != null || sledFarmerJSONArray != null || sledSHGJSONArray != null || sledCoCoordJSONArray != null || sledCordJSONArray != null || sledPOMemberJSONArray != null || sledCaResourceJSONArray != null || sledOthParticipantJSONArray != null) {
                    if (actionType.equalsIgnoreCase("create")) {
                        askUserPermission();
                    } else {
                        clearInputFields();
                        finish();
                    }
                } else {
                    finish();
                }
            }
        });
        String userDistId = AppSettings.getInstance().getValue(this, ApConstants.kUSER_DIST_ID, ApConstants.kUSER_DIST_ID);
        Log.d("AddEditEventCaActivity=","MAYU_userDistId=="+userDistId);
        // To get User Id and Roll Id
        String rId = AppSettings.getInstance().getValue(this, ApConstants.kROLE_ID, ApConstants.kROLE_ID);
        Log.d("AddEditEventCaActivity=","MAYU_rId=="+roleId);
        if (!rId.equalsIgnoreCase("kROLE_ID")) {
            roleId = rId;
        }
        String uId = AppSettings.getInstance().getValue(this, ApConstants.kUSER_ID, ApConstants.kUSER_ID);
        if (!uId.equalsIgnoreCase("kUSER_ID")) {
            userID = uId;
        }
        String rLaval = AppSettings.getInstance().getValue(this, ApConstants.kUSER_LEVEL, ApConstants.kUSER_LEVEL);
        if (!rLaval.equalsIgnoreCase("kUSER_LEVEL")) {
            userLaval = rLaval;
        }
        getTalukaIdByCaId();

        String loginData = AppSettings.getInstance().getValue(this, ApConstants.kLOGIN_DATA, ApConstants.kLOGIN_DATA);
        if (!loginData.equalsIgnoreCase("kLOGIN_DATA")) {
            try {
                JSONObject loginJSON = new JSONObject(loginData);
                // JSONArray clusterData = loginJSON.getJSONArray("cluster_data");
                subDivisionId = loginJSON.getString("subdivision_id");
                subDivisionName = loginJSON.getString("subdivision_name");

                // subDivisionTView.setText(loginJSON.getString("subdivision_name"));
                districtId = loginJSON.getString("district_id");
                districtName = loginJSON.getString("district_name");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        // Initialization of VIEWs
        eventTypeLLayout = (LinearLayout) findViewById(R.id.eventTypeLinearLayout);
        eventTypeTextView = (TextView) findViewById(R.id.eventTypeTextView);

        eventSubTypeLLayout = (LinearLayout) findViewById(R.id.eventSubTypeLLayout);
        eventSubTypeTView = (TextView) findViewById(R.id.eventSubTypeTView);

        eventTitleLLayout = (LinearLayout) findViewById(R.id.eventTitleLLayout);
        eventTitleEditText = (EditText) findViewById(R.id.eventTitleEditText);

        eventStartDateLLayout = (RelativeLayout) findViewById(R.id.eventStartDateLLayout);
        eventStartDateTextView = (TextView) findViewById(R.id.eventStartDateTextView);
        eventEndDateLLayout = (RelativeLayout) findViewById(R.id.eventEndDateLLayout);
        eventEndDateTextView = (TextView) findViewById(R.id.eventEndDateTextView);

        // For Time
        eventSTimeRLayout = (RelativeLayout) findViewById(R.id.eventSTimeRLayout);
        eventSTimeTView = (TextView) findViewById(R.id.eventSTimeTView);

        eventETimeRLayout = (RelativeLayout) findViewById(R.id.eventETimeRLayout);
        eventETimeTView = (TextView) findViewById(R.id.eventETimeTView);

        // For Report Day and time
        eventRDateRLayout = (RelativeLayout) findViewById(R.id.eventRDateRLayout);
        eventRDateTView = (TextView) findViewById(R.id.eventRDateTView);

        eventRTimeRLayout = (RelativeLayout) findViewById(R.id.eventRTimeRLayout);
        eventRTimeTView = (TextView) findViewById(R.id.eventRTimeTView);

        // participant Group
        selectPGroupLLayout = (LinearLayout) findViewById(R.id.selectPGroupLLayout);
        selPGroupTView = (TextView) findViewById(R.id.selPGroupTView);

        // VCRMC (GP)
        selectVCRMCRLayout = (RelativeLayout) findViewById(R.id.selectVCRMCRLayout);
        sledVCRMCCountTView = (TextView) findViewById(R.id.sledVCRMCCountTView);
        sledVCRMCMoreTView = (TextView) findViewById(R.id.sledVCRMCMoreTView);
        sledVCRMCTView = (TextView) findViewById(R.id.sledVCRMCTView);
        sledGPLLayout = (LinearLayout) findViewById(R.id.sledGPLLayout);
        sledGPRView = (RecyclerView) findViewById(R.id.sledGPRView);
        LinearLayoutManager sledGPLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        sledGPRView.setLayoutManager(sledGPLayoutManager);

        // Farmer
        selectFarmerRLayout = (RelativeLayout) findViewById(R.id.selectFarmerRLayout);
        sledFarmerCountTView = (TextView) findViewById(R.id.sledFarmerCountTView);
        sledFarmerMoreTView = (TextView) findViewById(R.id.sledFarmerMoreTView);
        sledFarmerLLayout = (LinearLayout) findViewById(R.id.sledFarmerLLayout);
        sledFarmerTView = (TextView) findViewById(R.id.sledFarmerTView);
        sledFarmerRView = (RecyclerView) findViewById(R.id.sledFarmerRView);
        LinearLayoutManager sledFarmerLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        sledFarmerRView.setLayoutManager(sledFarmerLayoutManager);


        // SHG
        selectshgRLayout = (RelativeLayout) findViewById(R.id.selectshgRLayout);
        sledshgCountTView = (TextView) findViewById(R.id.sledshgCountTView);
        sledshgMoreTView = (TextView) findViewById(R.id.sledshgMoreTView);

        sledShgTView = (TextView) findViewById(R.id.sledShgTView);
        sledshgLLayout = (LinearLayout) findViewById(R.id.sledshgLLayout);
        sledshgRView = (RecyclerView) findViewById(R.id.sledshgRView);

        LinearLayoutManager shgLL = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        sledshgRView.setLayoutManager(shgLL);


        // FPC
        selectfpcRLayout = (RelativeLayout) findViewById(R.id.selectfpcRLayout);
        sledfpcCountTView = (TextView) findViewById(R.id.sledfpcCountTView);
        sledfpcMoreTView = (TextView) findViewById(R.id.sledfpcMoreTView);

        sledFpcTView = (TextView) findViewById(R.id.sledFpcTView);
        sledFpcLLayout = (LinearLayout) findViewById(R.id.sledFpcLLayout);
        sledFpcRView = (RecyclerView) findViewById(R.id.sledFpcRView);

        LinearLayoutManager fpcLL = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        sledFpcRView.setLayoutManager(fpcLL);


        // FG
        selectFGRLayout = (RelativeLayout) findViewById(R.id.selectFGRLayout);
        sledFGCountTView = (TextView) findViewById(R.id.sledFGCountTView);
        sledFGMoreTView = (TextView) findViewById(R.id.sledFGMoreTView);

        sledFGTView = (TextView) findViewById(R.id.sledFGTView);
        sledFGLLayout = (LinearLayout) findViewById(R.id.sledFGLLayout);
        sledFGRView = (RecyclerView) findViewById(R.id.sledFGRView);

        LinearLayoutManager FGLL = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        sledFGRView.setLayoutManager(FGLL);


        // PoCRA Official Member
        selectPOMRLayout = (RelativeLayout) findViewById(R.id.selectPOMRLayout);
        sledPOMCountTView = (TextView) findViewById(R.id.sledPOMCountTView);
        sledPOMMoreTView = (TextView) findViewById(R.id.sledPOMMoreTView);

        sledPOMemberLLayout = (LinearLayout) findViewById(R.id.sledPOMemberLLayout);
        sledPOMemberTView = (TextView) findViewById(R.id.sledPOMemberTView);
        sledPOMemberRView = (RecyclerView) findViewById(R.id.sledPOMemberRView);
        LinearLayoutManager sledPOMemberLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        sledPOMemberRView.setLayoutManager(sledPOMemberLayoutManager);


        // CA Resource Person
        selectCaResourceRLayout = (RelativeLayout) findViewById(R.id.selectCaResourceRLayout);
        selectCaResourceCountTView = (TextView) findViewById(R.id.selectCaResourceCountTView);
        selectCaResourceMoreTView = (TextView) findViewById(R.id.selectCaResourceMoreTView);

        // Other Participants
        selectOtherRLayout = (RelativeLayout) findViewById(R.id.selectOtherRLayout);
        sledOtherCountTView = (TextView) findViewById(R.id.sledOtherCountTView);
        sledOtherMoreTView = (TextView) findViewById(R.id.sledOtherMoreTView);

        sledOthParticipantLLayout = (LinearLayout) findViewById(R.id.sledOthParticipantLLayout);
        sledOthParticipantTView = (TextView) findViewById(R.id.sledOthParticipantTView);
        sledOthParticipantRView = (RecyclerView) findViewById(R.id.sledOthParticipantRView);
        LinearLayoutManager sledOthParticipantLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        sledOthParticipantRView.setLayoutManager(sledOthParticipantLayoutManager);

        // For Participants Count
        participantsEditText = (TextView) findViewById(R.id.participantsEditText);
        participantsEditText.setEnabled(false);

        // For designation
        desigLLayout = (LinearLayout) findViewById(R.id.desigLLayout);
        // desigTextView = (TextView) findViewById(R.id.desigTextView);

        // For Selected Coordinator
        coordinatorLLayout = (LinearLayout) findViewById(R.id.coordinatorLLayout);
        sledCordLLayout = (LinearLayout) findViewById(R.id.sledCordLLayout);
        sledCordRView = (RecyclerView) findViewById(R.id.sledCordRView);
        LinearLayoutManager sledCordLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        sledCordRView.setLayoutManager(sledCordLayoutManager);
        coordinatorTextView = (TextView) findViewById(R.id.coordinatorTextView);

        // For Selected Co-coordinator
        coCordLinearLayout = (LinearLayout) findViewById(R.id.coCordLinearLayout);
        sledCoCoordLLayout = (LinearLayout) findViewById(R.id.sledCoCoordLLayout);
        sledCoCoordRView = (RecyclerView) findViewById(R.id.sledCoCoordRView);
        LinearLayoutManager sledCoCordLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        sledCoCoordRView.setLayoutManager(sledCoCordLayoutManager);
        coCordTextView = (TextView) findViewById(R.id.coCordTextView);

        // For Venue District
        vDistLLayout = (LinearLayout) findViewById(R.id.vDistLLayout);
        vDistTView = (TextView) findViewById(R.id.vDistTView);
        Log.d("AddEditEventCaActivity=","districtName="+districtName);
        Log.d("AddEditEventCaActivity=","districtID="+districtId);
        vDistTView.setText(districtName);

        getCaVillageList();

        // For Venue
        venueLLayout = (LinearLayout) findViewById(R.id.venueLLayout);
        venueLocationTView = (TextView) findViewById(R.id.venueLocationTView);

        kvkLLayout = (LinearLayout) findViewById(R.id.kvkLLayout);
        kvkTView = (TextView) findViewById(R.id.kvkTView);

        villageLLayout = (LinearLayout) findViewById(R.id.villageLLayout);
        villageTView = (TextView) findViewById(R.id.villageTView);

        venueELLayout = (LinearLayout) findViewById(R.id.venueELLayout);
        venueLocationEditText = (TextView) findViewById(R.id.venueLocationEditText);

        createButton = (Button) findViewById(R.id.createButton);
        notetxt= (TextView) findViewById(R.id.notetxt);

        clearInputFields();

        /** flag For Add 0r Update Schedule */
        actionType = getIntent().getStringExtra("type");

        if (actionType.equalsIgnoreCase("update")) {

            String sch_id = getIntent().getStringExtra("sch_id");
            Log.d("tetetete", sch_id);
            if (!sch_id.equalsIgnoreCase("")) {
                createButton.setText("update");

                eventTypeLLayout.setBackgroundResource(R.drawable.edit_border_bg);
                eventTypeLLayout.setEnabled(false);

                eventSubTypeLLayout.setBackgroundResource(R.drawable.edit_border_bg);
                eventSubTypeLLayout.setEnabled(false);

                eventTitleEditText.setBackgroundResource(R.drawable.edit_border_bg);
                eventTitleEditText.setEnabled(false);

                selPGroupTView.setText("Selected participants");
                selectPGroupLLayout.setBackgroundResource(R.drawable.edit_border_bg);
                selectPGroupLLayout.setEnabled(false);

                participantsEditText.setBackgroundResource(R.drawable.edit_border_bg);

                desigLLayout.setBackgroundResource(R.drawable.edit_border_bg);
                desigLLayout.setEnabled(false);

                coordinatorLLayout.setBackgroundResource(R.drawable.edit_border_bg);
                coordinatorLLayout.setEnabled(false);

                   coCordLinearLayout.setBackgroundResource(R.drawable.edit_border_bg);
                coCordLinearLayout.setEnabled(false);

                findViewById(R.id.eventTypeImageView).setVisibility(View.INVISIBLE);
                findViewById(R.id.subTypeImageView).setVisibility(View.INVISIBLE);
                findViewById(R.id.selPGroupImageView).setVisibility(View.INVISIBLE);
                findViewById(R.id.addCoordinatorImageView).setVisibility(View.INVISIBLE);
                findViewById(R.id.addCoCordImageView).setVisibility(View.INVISIBLE);

                getScheduledEventBySchId(sch_id);
            }

        }
        String user_data = AppSettings.getInstance().getValue(this, ApConstants.kLOGIN_DATA, ApConstants.kLOGIN_DATA);
        if (!user_data.equalsIgnoreCase("kLOGIN_DATA")) {

            try {
                JSONObject userJson = new JSONObject(user_data);
                ProfileModel pModel = new ProfileModel(userJson);
                userMobile = pModel.getMobile();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    protected void onResume() {
        super.onResume();

        // For Selected VCRMC (GP)
        setSelectedGP();

        // For Selected Farmer
        setSelectedFarmer();
        setSelctSHGgroup();
        setSelctFPCgroup();

        setSelctFRgroup();
        // For Selected Resource Person
        setSelectedFacilitator();

        // For Selected Resource Person
        setSelectedResourcePerson();

        // For Selected Other Participants
        setSelectedOtherParticipants();

        // For Selected Coordinator
        setSelectedCoordinator();

        // For Selected Co-Coordinator
        setSelectedCoCoord();

        // To get number of selected participant
        getNumberOfParticipant();

        // TO get District list
        getDistrictList();

        // To get Venue list
        // getVenueList();
        if (!districtId.equalsIgnoreCase("")) {
            getVenueList(districtId);
        }

        // to get Designation list
        // getDesigList();

        // to get Role list
        getRoleList("Subdivision");


        // to get Designation list
        // getCocoordinatorDesigList();
        getCoCordiRoleList("Subdivision");

    }


    // VCRMC (GP)
    private void setSelectedGP() {
        sledGPJSONArray = eDB.getSelectedGpList();

        if (sledGPJSONArray.length() > 0) {
            selectVCRMCRLayout.setVisibility(View.VISIBLE);
            adaptorSelectedGP = new AdaptorSelectedGP(AddEditEventCaActivity.this, sledGPJSONArray, actionType, AddEditEventCaActivity.this, this);
            sledGPRView.setAdapter(adaptorSelectedGP);
        } else {
            sledGPJSONArray = null;
            selectVCRMCRLayout.setVisibility(View.GONE);
        }

        if (sledGPJSONArray != null) {
            sledGPId = AppUtility.getInstance().componentSeparatedByCommaJSONArray(sledGPJSONArray, "gp_id");
            selPGroupTView.setError(null);
        } else {
            selectVCRMCRLayout.setVisibility(View.GONE);
        }
        getNumberOfParticipant();

    }

    // SHG
    private void setSelctSHGgroup() {

        sledSHGJSONArray = eDB.getShglist();

        if (sledSHGJSONArray != null && sledSHGJSONArray.length() > 0) {
            selectshgRLayout.setVisibility(View.VISIBLE);
            adaptorSelectedSGF = new AdaptorSelectedSGF(AddEditEventCaActivity.this, sledSHGJSONArray, actionType, AddEditEventCaActivity.this);
            sledshgRView.setAdapter(adaptorSelectedSGF);
            getNumberOfParticipant();
        } else {
            sledSHGJSONArray = null;
            selectshgRLayout.setVisibility(View.GONE);
        }
    }


    private void setSelctFPCgroup() {

        sledFPCJSONArray = eDB.getFpcList();

        if (sledFPCJSONArray != null && sledFPCJSONArray.length() > 0) {
            selectfpcRLayout.setVisibility(View.VISIBLE);
            adaptorSelectedFpc = new AdaptorSelectedFPC(AddEditEventCaActivity.this, sledFPCJSONArray, actionType, AddEditEventCaActivity.this);
            sledFpcRView.setAdapter(adaptorSelectedFpc);
            getNumberOfParticipant();
        } else {
            sledFPCJSONArray = null;
            selectfpcRLayout.setVisibility(View.GONE);
        }
    }


    // Farmer Group
    private void setSelctFRgroup() {

        sledFRJSONArray = eDB.getFarmerGrouplist();

        if (sledFRJSONArray != null && sledFRJSONArray.length() > 0) {
            selectFGRLayout.setVisibility(View.VISIBLE);
            adaptorSelectedFR = new AdaptorSelectedFR(AddEditEventCaActivity.this, sledFRJSONArray, actionType, AddEditEventCaActivity.this);
            sledFGRView.setAdapter(adaptorSelectedFR);
            getNumberOfParticipant();
        } else {
            sledFRJSONArray = null;
            selectFGRLayout.setVisibility(View.GONE);
        }
    }


    // Farmer
    private void setSelectedFarmer() {

        sledFarmerJSONArray = eDB.getSledFarmerList();
        if (sledFarmerJSONArray.length() > 0) {
            selectFarmerRLayout.setVisibility(View.VISIBLE);
            adaptorSelectedfarmer = new AdaptorSelectedFarmer(AddEditEventCaActivity.this, sledFarmerJSONArray, actionType, AddEditEventCaActivity.this);
            sledFarmerRView.setAdapter(adaptorSelectedfarmer);
        } else {
            sledFarmerJSONArray = null;
            selectFarmerRLayout.setVisibility(View.GONE);
        }


        if (sledFarmerJSONArray != null) {
            JSONArray farActivityArray = eDB.getSledActivityList();
            sledFarVillageCode = AppUtility.getInstance().componentSeparatedByCommaJSONArray(farActivityArray, "vil_code");
            sledActivityId = AppUtility.getInstance().componentSeparatedByCommaJSONArray(farActivityArray, "vil_act_id");

            sledFarmerTView.setError(null);
        } else {
            selectFarmerRLayout.setVisibility(View.GONE);
        }

        getNumberOfParticipant();

    }


    // FFS Facilitator
    private void setSelectedFacilitator() {

        try {
            sledPOMemberJSONArray = eDB.getSledPOMemberList();
            if (sledPOMemberJSONArray.length() > 0) {

                if (sledPOMemberJSONArray.length() > 0) {
                    selectPOMRLayout.setVisibility(View.VISIBLE);
                    adaptorSelectedPOMember = new AdaptorSelectedPOMember(AddEditEventCaActivity.this, sledPOMemberJSONArray, actionType, AddEditEventCaActivity.this);
                    sledPOMemberRView.setAdapter(adaptorSelectedPOMember);
                } else {
                    sledPOMemberJSONArray = null;
                    selectPOMRLayout.setVisibility(View.GONE);

                }

                if (sledPOMemberJSONArray != null) {
                    sledFacilitatorId = new JSONArray();
                    for (int i = 0; i < sledPOMemberJSONArray.length(); i++) {
                        JSONObject faciDetail = sledPOMemberJSONArray.getJSONObject(i);
                        JSONObject sledCord = new JSONObject();
                        sledCord.put("facilitator_id", faciDetail.getString("id"));
                        sledCord.put("role_id", faciDetail.getString("role_id"));
                        sledFacilitatorId.put(sledCord);
                    }
                    participantsEditText.setError(null);
                }

            } else {
                sledPOMemberJSONArray = null;
                selectPOMRLayout.setVisibility(View.GONE);
            }
            getNumberOfParticipant();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setSelectedResourcePerson() {
        String sledResourcePerson = AppSettings.getInstance().getValue(this, ApConstants.kS_CA_RES_PERSON_ARRAY, ApConstants.kS_CA_RES_PERSON_ARRAY);

        try {
            if (!sledResourcePerson.equalsIgnoreCase("kS_CA_RES_PERSON_ARRAY")) {
                sledCaResourceJSONArray = new JSONArray(sledResourcePerson);

                if (sledCaResourceJSONArray.length() > 0) {
                    selectCaResourceRLayout.setVisibility(View.VISIBLE);
                } else {
                    sledCaResourceJSONArray = null;
                    selectCaResourceRLayout.setVisibility(View.GONE);
                }

                if (sledCaResourceJSONArray != null) {
                    sledCaResource = new JSONArray();
                    for (int i = 0; i < sledCaResourceJSONArray.length(); i++) {
                        JSONObject resourceDetail = sledCaResourceJSONArray.getJSONObject(i);
                        JSONObject sledResource = new JSONObject();
                        sledResource.put("ca_rp_id", resourceDetail.getString("id"));
                        sledResource.put("rp_gender", resourceDetail.getString("gender"));
                        sledCaResource.put(sledResource);
                    }
                    participantsEditText.setError(null);
                }

            } else {
                sledCaResourceJSONArray = null;
                selectCaResourceRLayout.setVisibility(View.GONE);
            }
            getNumberOfParticipant();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    // Other Participants
    private void setSelectedOtherParticipants() {

        String sledOthParticipants = AppSettings.getInstance().getValue(this, ApConstants.kS_OTH_PARTICIPANTS_ARRAY, ApConstants.kS_OTH_PARTICIPANTS_ARRAY);
        try {
            if (!sledOthParticipants.equalsIgnoreCase("kS_OTH_PARTICIPANTS_ARRAY")) {

                sledOthParticipantJSONArray = new JSONArray(sledOthParticipants);

                if (sledOthParticipantJSONArray.length() > 0) {
                    selectOtherRLayout.setVisibility(View.VISIBLE);
                    adaptorSelectedOthParticipant = new AdaptorSelectedOthParticipants(AddEditEventCaActivity.this, sledOthParticipantJSONArray, actionType, AddEditEventCaActivity.this);
                    sledOthParticipantRView.setAdapter(adaptorSelectedOthParticipant);
                } else {
                    sledOthParticipantJSONArray = null;
                    selectOtherRLayout.setVisibility(View.GONE);
                }


                if (sledOthParticipantJSONArray != null) {
                    sledOthParticipantId = new JSONArray();
                    for (int i = 0; i < sledOthParticipantJSONArray.length(); i++) {
                        JSONObject faciDetail = sledOthParticipantJSONArray.getJSONObject(i);
                        JSONObject sledCord = new JSONObject();
                        sledCord.put("othParticipants_id", faciDetail.getString("id"));
                        sledCord.put("role_id", "0");
                        sledOthParticipantId.put(sledCord);
                    }
                    participantsEditText.setError(null);
                }

            } else {
                sledOthParticipantJSONArray = null;
                selectOtherRLayout.setVisibility(View.GONE);
            }
            getNumberOfParticipant();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    // Resource Person
    private void setSelectedCoCoord() {

        String sledCoCoord = AppSettings.getInstance().getValue(this, ApConstants.kS_CO_COORDINATOR, ApConstants.kS_CO_COORDINATOR);
        try {

            if (!sledCoCoord.equalsIgnoreCase("kS_CO_COORDINATOR")) {
                sledCoCoordJSONArray = new JSONArray(sledCoCoord);
                if (sledCoCoordJSONArray.length() > 0) {
                    sledCoCoordLLayout.setVisibility(View.VISIBLE);
                    adaptorSelectedCoCoord = new AdaptorSelectedCoCoord(AddEditEventCaActivity.this, sledCoCoordJSONArray, actionType, AddEditEventCaActivity.this);
                    sledCoCoordRView.setAdapter(adaptorSelectedCoCoord);
                } else {
                    sledCoCoordJSONArray = null;
                    sledCoCoordinatorId = null;
                    sledCoCoordLLayout.setVisibility(View.GONE);
                }

                if (sledCoCoordJSONArray != null) {
                    sledCoCoordinatorId = new JSONArray();
                    sledCoCoordinatorArray = new JSONArray();

                    for (int i = 0; i < sledCoCoordJSONArray.length(); i++) {
                        JSONObject resPerDetail = sledCoCoordJSONArray.getJSONObject(i);
                        String co_coardRoleId = resPerDetail.getString("role_id");

                        if (co_coardRoleId.equalsIgnoreCase("0")) {
                            JSONObject resPerCord = new JSONObject();
                            resPerCord.put("rp_id", resPerDetail.getString("id"));
                            sledCoCoordinatorId.put(resPerCord);
                        } else {
                            JSONObject resPerCord = new JSONObject();
                            resPerCord.put("coordinator_id", resPerDetail.getString("id"));
                            resPerCord.put("role_id", resPerDetail.getString("role_id"));
                            sledCoCoordinatorArray.put(resPerCord);
                        }
                    }
                    coCordTextView.setError(null);
                }

            } else {
                sledCoCoordJSONArray = null;
                sledCoCoordinatorId = null;
                sledCoCoordLLayout.setVisibility(View.GONE);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    // Coordinator
    private void setSelectedCoordinator() {

        String sledCoordinator = AppSettings.getInstance().getValue(this, ApConstants.kS_COORDINATOR, ApConstants.kS_COORDINATOR);
        Log.d("trtetetet", sledCoordinator);
        try {
            if (!sledCoordinator.equalsIgnoreCase("kS_COORDINATOR")) {
                sledCordJSONArray = new JSONArray(sledCoordinator);
                if (sledCordJSONArray.length() > 0) {
                    sledCordLLayout.setVisibility(View.VISIBLE);
                    adaptorSelectedCoordinator = new AdaptorSelectedCoordinator(AddEditEventCaActivity.this, sledCordJSONArray, actionType, AddEditEventCaActivity.this);
                    sledCordRView.setAdapter(adaptorSelectedCoordinator);
                } else {
                    sledCordJSONArray = null;
                    sledCordLLayout.setVisibility(View.GONE);
                }

                if (sledCordJSONArray != null) {
                    sledCordId = new JSONArray();
                    for (int i = 0; i < sledCordJSONArray.length(); i++) {
                        JSONObject cordDetail = sledCordJSONArray.getJSONObject(i);
                        JSONObject sledCord = new JSONObject();
                        sledCord.put("coordinator_id", cordDetail.getString("id"));
                        sledCord.put("role_id", cordDetail.getString("role_id"));
                        sledCordId.put(sledCord);
                    }

                    coordinatorTextView.setError(null);
                }

            } else {
                sledCordJSONArray = null;
                sledCordId = null;
                sledCordLLayout.setVisibility(View.GONE);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    // To get Number of participants
    private void getNumberOfParticipant() {
        memCount = "";
        int gpMemCount = 0;
        int farmerCount = 0;
        int shgCount = 0;
        int fpcCount = 0;
        int frCount = 0;
        int facilitatorCount = 0;
        int caResourceCount = 0;
        int otherCount = 0;

        if (sledGPJSONArray != null) {

            try {
                for (int i = 0; i < sledGPJSONArray.length(); i++) {
                    JSONObject jsonObject = sledGPJSONArray.getJSONObject(i);
                    String gpId = jsonObject.getString("gp_id");
                    JSONArray memJSONArray = eDB.getSledGpMemIdListByGpId(gpId);

                    for (int j = 0; j < memJSONArray.length(); j++) {
                        JSONObject memJson = memJSONArray.getJSONObject(j);
                        String isSelected = memJson.getString("mem_is_selected");
                        if (isSelected.equalsIgnoreCase("1")) {
                            gpMemCount++;
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if (sledFarmerJSONArray != null) {
            try {
                for (int i = 0; i < sledFarmerJSONArray.length(); i++) {
                    JSONObject jsonObject = sledFarmerJSONArray.getJSONObject(i);
                    String isSelected = jsonObject.getString("is_selected");
                    if (isSelected.equalsIgnoreCase("1")) {
                        farmerCount++;
                    }

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (sledSHGJSONArray != null) {
            try {
                for (int i = 0; i < sledSHGJSONArray.length(); i++) {
                    JSONObject jsonObject = sledSHGJSONArray.getJSONObject(i);
                    String isSelected = jsonObject.getString("is_selected");
                    if (isSelected.equalsIgnoreCase("1")) {
                        shgCount++;
                    }

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (sledFPCJSONArray != null) {
            try {
                for (int i = 0; i < sledFPCJSONArray.length(); i++) {
                    JSONObject jsonObject = sledFPCJSONArray.getJSONObject(i);
                    String isSelected = jsonObject.getString("is_selected");
                    if (isSelected.equalsIgnoreCase("1")) {
                        fpcCount++;
                    }

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (sledFRJSONArray != null) {
            try {
                for (int i = 0; i < sledFRJSONArray.length(); i++) {
                    JSONObject jsonObject = sledFRJSONArray.getJSONObject(i);
                    String isSelected = jsonObject.getString("is_selected");
                    if (isSelected.equalsIgnoreCase("1")) {
                        frCount++;
                    }

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if (sledPOMemberJSONArray != null) {
            try {
                for (int i = 0; i < sledPOMemberJSONArray.length(); i++) {
                    JSONObject jsonObject = sledPOMemberJSONArray.getJSONObject(i);
                    String isSelected = jsonObject.getString("is_selected");
                    if (isSelected.equalsIgnoreCase("1")) {
                        facilitatorCount++;
                    }

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if (sledCaResourceJSONArray != null) {
            try {
                for (int i = 0; i < sledCaResourceJSONArray.length(); i++) {
                    JSONObject jsonObject = sledCaResourceJSONArray.getJSONObject(i);
                    String isSelected = jsonObject.getString("is_selected");
                    if (isSelected.equalsIgnoreCase("1")) {
                        caResourceCount++;
                    }

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if (sledOthParticipantJSONArray != null) {
            try {
                for (int i = 0; i < sledOthParticipantJSONArray.length(); i++) {
                    JSONObject jsonObject = sledOthParticipantJSONArray.getJSONObject(i);
                    String isSelected = jsonObject.getString("is_selected");
                    if (isSelected.equalsIgnoreCase("1")) {
                        otherCount++;
                    }

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        memCount = String.valueOf(gpMemCount + farmerCount + facilitatorCount + caResourceCount + otherCount + shgCount + fpcCount + frCount);

        if (selectVCRMCRLayout.getVisibility() == View.VISIBLE) {
            sledVCRMCCountTView.setText(" : " + gpMemCount);
        }
        if (selectFarmerRLayout.getVisibility() == View.VISIBLE) {
            sledFarmerCountTView.setText(" : " + farmerCount);
        }

        if (selectshgRLayout.getVisibility() == View.VISIBLE) {
            sledshgCountTView.setText(" : " + shgCount);
        }

        if (selectfpcRLayout.getVisibility() == View.VISIBLE) {
            sledfpcCountTView.setText(" : " + fpcCount);
        }
        if (selectFGRLayout.getVisibility() == View.VISIBLE) {
            sledFGCountTView.setText(" : " + frCount);
        }
        if (selectPOMRLayout.getVisibility() == View.VISIBLE) {
            sledPOMCountTView.setText(" : " + facilitatorCount);
        }
        if (selectCaResourceRLayout.getVisibility() == View.VISIBLE) {
            selectCaResourceCountTView.setText(" : " + caResourceCount);
        }
        if (selectOtherRLayout.getVisibility() == View.VISIBLE) {
            sledOtherCountTView.setText(" : " + otherCount);
        }

        participantsEditText.setText("Selected participants = " + memCount);
        participantsEditText.setError(null);
        // sledCordJSONArray = null;
    }


    @Override
    public void onBackPressed() {
        if (sledGPJSONArray != null || sledFarmerJSONArray != null || sledCoCoordJSONArray != null || sledCordJSONArray != null || sledPOMemberJSONArray != null || sledSHGJSONArray != null || sledFPCJSONArray != null || sledFRJSONArray != null) {
            if (actionType.equalsIgnoreCase("create")) {
                askUserPermission();
            } else {
                clearInputFields();
                finish();
            }
        } else {
            super.onBackPressed();
        }
    }


    private void defaultConfiguration() {

        getEventTypeList();

        // to get participants Group List
        getParticipantGroupList();

        sledVCRMCMoreTView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sledGPJSONArray = eDB.getSelectedGpList();

                Intent intent = new Intent(AddEditEventCaActivity.this, ParticipantGPListActivity.class);
                intent.putExtra("sledMemType", "VCRMC(GP)");
                intent.putExtra("sledMemArray", sledGPJSONArray.toString());
                intent.putExtra("actionType", actionType);
                startActivity(intent);
            }
        });


        sledFarmerMoreTView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sledFarmerJSONArray = eDB.getSledFarmerList();
                Intent intent = new Intent(AddEditEventCaActivity.this, ParticipantGPListActivity.class);
                intent.putExtra("sledMemType", "Beneficiary Farmer)");
                intent.putExtra("sledMemArray", sledFarmerJSONArray.toString());
                intent.putExtra("actionType", actionType);
                intent.putExtra("groupType", "fActivity");
                startActivity(intent);
            }
        });


        sledshgMoreTView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sledSHGJSONArray = eDB.getShglist();
                Intent intent = new Intent(AddEditEventCaActivity.this, ParticipantsListActivity.class);
                intent.putExtra("sledMemType", "SHG");
                intent.putExtra("sledMemArray", sledSHGJSONArray.toString());
                intent.putExtra("actionType", actionType);
                startActivity(intent);
            }
        });


        sledfpcMoreTView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sledFPCJSONArray = eDB.getFpcList();
                Intent intent = new Intent(AddEditEventCaActivity.this, ParticipantsListActivity.class);
                intent.putExtra("sledMemType", "FPC");
                intent.putExtra("sledMemArray", sledFPCJSONArray.toString());
                intent.putExtra("actionType", actionType);
                startActivity(intent);
            }
        });


        sledFGMoreTView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sledFRJSONArray = eDB.getFarmerGrouplist();
                Intent intent = new Intent(AddEditEventCaActivity.this, ParticipantsListActivity.class);
                intent.putExtra("sledMemType", "FG");
                intent.putExtra("sledMemArray", sledFRJSONArray.toString());
                intent.putExtra("actionType", actionType);
                startActivity(intent);
            }
        });


        sledPOMMoreTView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sledPOMemberJSONArray = eDB.getSledPOMemberList();
                Intent intent = new Intent(AddEditEventCaActivity.this, ParticipantsListActivity.class);
                intent.putExtra("sledMemType", "PoCRA Official");
                intent.putExtra("sledMemArray", sledPOMemberJSONArray.toString());
                intent.putExtra("actionType", actionType);
                startActivity(intent);
            }
        });

        selectCaResourceMoreTView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sledResourceParticipants = AppSettings.getInstance().getValue(AddEditEventCaActivity.this, ApConstants.kS_CA_RES_PERSON_ARRAY, ApConstants.kS_CA_RES_PERSON_ARRAY);

                try {
                    if (!sledResourceParticipants.equalsIgnoreCase("kS_CA_RES_PERSON_ARRAY")) {
                        sledCaResourceJSONArray = new JSONArray(sledResourceParticipants);
                        Intent intent = new Intent(AddEditEventCaActivity.this, ParticipantsListActivity.class);
                        intent.putExtra("sledMemType", "Resource Person");
                        intent.putExtra("sledMemArray", sledCaResourceJSONArray.toString());
                        intent.putExtra("actionType", actionType);
                        startActivity(intent);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });


        sledOtherMoreTView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sledOthParticipants = AppSettings.getInstance().getValue(AddEditEventCaActivity.this, ApConstants.kS_OTH_PARTICIPANTS_ARRAY, ApConstants.kS_OTH_PARTICIPANTS_ARRAY);

                try {
                    if (!sledOthParticipants.equalsIgnoreCase("kS_OTH_PARTICIPANTS_ARRAY")) {
                        sledOthParticipantJSONArray = new JSONArray(sledOthParticipants);
                        Intent intent = new Intent(AddEditEventCaActivity.this, ParticipantsListActivity.class);
                        intent.putExtra("sledMemType", "Other Participants");
                        intent.putExtra("sledMemArray", sledOthParticipantJSONArray.toString());
                        intent.putExtra("actionType", actionType);
                        startActivity(intent);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }


    private void eventListener() {

        eventTypeLLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ApUtil.checkInternetConnection(AddEditEventCaActivity.this)) {

                    if (eventTypeJSONArray != null) {
                        // AppUtility.getInstance().showListPicker(eventTypeTextView, eventTypeJSONArray, "Select Training Type", "name", "id", AddEditEventPsHrdActivity.this, AddEditEventPsHrdActivity.this);
                        ApUtil.showCustomListPicker(eventTypeTextView, eventTypeJSONArray, "Select Training Type", "name", "id", AddEditEventCaActivity.this, AddEditEventCaActivity.this);
                    } else {
                        getEventTypeList();
                    }

                } else {
//                    UIToastMessage.show(AddEditEventCaActivity.this, "No internet connection");
                    Toast.makeText(AddEditEventCaActivity.this, "No internet connection", Toast.LENGTH_LONG);
                }

            }
        });


        eventSubTypeLLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ApUtil.checkInternetConnection(AddEditEventCaActivity.this)) {

                    if (eventSubTypeJSONArray != null) {
                        // AppUtility.getInstance().showListPicker(eventSubTypeTView, eventSubTypeJSONArray, "Select event sub Type", "name", "id", AddEditEventPsHrdActivity.this, AddEditEventPsHrdActivity.this);
                        ApUtil.showCustomListPicker(eventSubTypeTView, eventSubTypeJSONArray, "Select event sub Type", "topic_name", "id", AddEditEventCaActivity.this, AddEditEventCaActivity.this);

                    } else {
                        if (!eventTypeId.equalsIgnoreCase("")) {
                            getEventSubTypeList(eventTypeId);
                        } else {
//                            UIToastMessage.show(AddEditEventCaActivity.this, "Please select Event type");
                            Toast.makeText(AddEditEventCaActivity.this, "Please select Event type", Toast.LENGTH_LONG);
                        }
                    }

                } else {
//                    UIToastMessage.show(AddEditEventCaActivity.this, "No internet connection");
                    Toast.makeText(AddEditEventCaActivity.this, "No internet connection", Toast.LENGTH_LONG);
                }

            }
        });


        eventStartDateLLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (!serverCurrentDate.equalsIgnoreCase("")) {

                        Date cDate = new SimpleDateFormat("dd-MM-yyyy").parse(serverCurrentDate);
                        Calendar c = Calendar.getInstance();
                        c.setTime(cDate);
                       //c.add(Calendar.DATE, 2);  // for after 2 day date//
                        c.add(Calendar.DATE, 1);  // for after 1 day date   //production
                        //c.add(Calendar.DATE, 0);  // for current day date    // test
                        Date dayPlushOne = c.getTime();
                        AppUtility.getInstance().showFutureDatePicker(AddEditEventCaActivity.this, dayPlushOne, eventStartDateTextView, AddEditEventCaActivity.this);
                    } else {
                        Date cDate = new Date();
                        Calendar c = Calendar.getInstance();
                        c.setTime(cDate);
                        // c.add(Calendar.DATE, 2);  // for after 2 day date
                        c.add(Calendar.DATE, 1);  // for after 1 day date  //   Production
                       //c.add(Calendar.DATE, 0);  // for current day date    // test
                        Date dayPlushOne = c.getTime();
                        AppUtility.getInstance().showFutureDatePicker(AddEditEventCaActivity.this, dayPlushOne, eventStartDateTextView, AddEditEventCaActivity.this);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }
        });

        eventTypeTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                eventStartDate = "";
                eventStartDateTextView.setText("");
                eventSTimeTView.setText("");
                startTime = "";
                startTimeAmPm = "";

                eventEndDate = "";
                eventEndDateTextView.setText("");
                eventETimeTView.setText("");
                endTime = "";
                endTimeAmPm = "";
            }
        });


        eventEndDateLLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               try{
                if (!eventStartDate.equalsIgnoreCase("")) {
                    Date cDate = null;

                        cDate = new SimpleDateFormat("yyyy-MM-dd").parse(eventStartDate);

                        // For Start Date
                        Calendar c = Calendar.getInstance();
                        c.setTime(cDate);
                        c.add(Calendar.DATE, 0);
                        Date fromStartDate = c.getTime();

                        // For End Date
                        Calendar ce = Calendar.getInstance();
                        ce.setTime(cDate);
                        ce.add(Calendar.DATE, 5);
                        Date toDate = ce.getTime();
                        //AppUtility.getInstance().showFutureDatePicker(AddEditEventPsHrdActivity.this, fromStartDate, eventEndDateTextView, AddEditEventPsHrdActivity.this);
                        ApUtil.showDatePickerBtnTwoDates(AddEditEventCaActivity.this, fromStartDate, toDate, eventEndDateTextView, AddEditEventCaActivity.this);
                } else {
                    //UIToastMessage.show(AddEditEventCaActivity.this, "Please Select start date");
                    Toast.makeText(AddEditEventCaActivity.this, "Please Select start date", Toast.LENGTH_LONG);
                }
               } catch (ParseException e) {
                   e.printStackTrace();
               }
            }
        });

        // For Event Time
        eventSTimeRLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (eventEndDate==null) {
                    Toast.makeText(AddEditEventCaActivity.this, "Please Select end date", Toast.LENGTH_LONG);

                } else {
                   // UIToastMessage.show(AddEditEventCaActivity.this, "Select end date");
                    ApUtil.showTimePicker12(AddEditEventCaActivity.this, eventSTimeTView, AddEditEventCaActivity.this);
                }
            }
        });

        eventETimeRLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try{
                if (startHour > 0 || startMinuets > 0) {
                    if (eventStartDate.equalsIgnoreCase(eventEndDate)) {
                        ApUtil.showTimeAgainstStartTimePicker12(AddEditEventCaActivity.this, eventETimeTView, startHour, startMinuets, AddEditEventCaActivity.this);
                    } else {
                        ApUtil.showTimePicker12(AddEditEventCaActivity.this, eventETimeTView, AddEditEventCaActivity.this);
                    }
                } else {
                    UIToastMessage.show(AddEditEventCaActivity.this, "Select start time");
                }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        // For Report Day
        eventRDateRLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                if (!eventStartDate.equalsIgnoreCase("")) {
                    Date eDate = null;
                    Date fromDate = null;
                        eDate = new SimpleDateFormat("yyyy-MM-dd").parse(eventStartDate);
                        String cDate = ApUtil.getDateByTimeStamp(ApUtil.getCurrentTimeStamp());
                        String eSDate = ApUtil.getDateInDDMMYYYYWithSeperator(eventStartDate, "-");
                        JSONArray jsonArray = ApUtil.getDateBetweenTwoDate(cDate, eSDate);  // date return With start date
                        if (eSDate.equalsIgnoreCase(cDate)) {
                            Calendar cs = Calendar.getInstance();
                            cs.setTime(eDate);
                            cs.add(Calendar.DATE, 0);
                            fromDate = cs.getTime();
                        } else if (jsonArray.length() == 2) {
                            Calendar cs = Calendar.getInstance();
                            cs.setTime(eDate);
                            cs.add(Calendar.DATE, 0);
                            fromDate = cs.getTime();
                        } else if (jsonArray.length() >= 3) {
                            Calendar cs = Calendar.getInstance();
                            cs.setTime(eDate);
                            cs.add(Calendar.DATE, -1);
                            fromDate = cs.getTime();
                        }
                        /* else {
                            Calendar cs = Calendar.getInstance();
                            cs.setTime(eDate);
                            cs.add(Calendar.DATE, -2);
                            fromDate = cs.getTime();
                        }*/

                        // Start date as to date
                        Calendar c = Calendar.getInstance();
                        c.setTime(eDate);
                        c.add(Calendar.DATE, 0);
                        Date toDate = c.getTime();


                        ApUtil.showDatePickerBtnTwoDates(AddEditEventCaActivity.this, fromDate, toDate, eventRDateTView, AddEditEventCaActivity.this);

                } else {
                    //UIToastMessage.show(AddEditEventCaActivity.this, "Please Select start date");
                    Toast.makeText(AddEditEventCaActivity.this, "Please Select end date", Toast.LENGTH_LONG);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
            }
        });

        // For Report Time
        eventRTimeRLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               try{
                if (!reportDate.equalsIgnoreCase("")) {
                    if (startHour > 0 || startMinuets > 0) {
                        // if (eventStartDate.equalsIgnoreCase(reportDate)) {
                        if (ApUtil.isDateEqual(eventStartDate, reportDate)) {
                            ApUtil.showTimeBeforeStartTimePicker12(AddEditEventCaActivity.this, eventRTimeTView, startHour, startMinuets, AddEditEventCaActivity.this);
                        } else {
                            ApUtil.showTimePicker12(AddEditEventCaActivity.this, eventRTimeTView, AddEditEventCaActivity.this);
                        }
                    } else {
                        UIToastMessage.show(AddEditEventCaActivity.this, "Select start time");
                    }
                } else {
                    UIToastMessage.show(AddEditEventCaActivity.this, "Select Report date");
                }
             } catch (Exception e) {
                e.printStackTrace();
            }
            }
        });
        selectPGroupLLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (eventEndDate.equalsIgnoreCase("")) {
                    //UIToastMessage.show(AddEditEventCaActivity.this, "Please Select End date");
                    Toast.makeText(AddEditEventCaActivity.this, "Please Select End date", Toast.LENGTH_LONG);
                } else if (isFarmerToSelect()) {
                    AppSettings.getInstance().setValue(AddEditEventCaActivity.this, ApConstants.kEVENT_MEM_TYPE, ApConstants.kEVENT_MEM_FARMER);
                    Intent intent = new Intent(AddEditEventCaActivity.this, PsHrdFarmerFilterActivity.class);
                    startActivity(intent);
                } else {

                    if (ApUtil.checkInternetConnection(AddEditEventCaActivity.this)) {
                        if (partiGroupJSONArray != null) {
                            ApUtil.showCustomListPicker(selPGroupTView, partiGroupJSONArray, "Select Participant Group Type", "name", "id", AddEditEventCaActivity.this, AddEditEventCaActivity.this);
                        } else {
                            getParticipantGroupList();
                        }
                    } else {
                        UIToastMessage.show(AddEditEventCaActivity.this, "No internet connection");
                    }
                }

            }
        });


        venueLLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ApUtil.checkInternetConnection(AddEditEventCaActivity.this)) {

                    if (eventVenueJSONArray != null) {
                        ApUtil.showCustomListPicker(venueLocationTView, eventVenueJSONArray, "Select Venue Location", "name", "id", AddEditEventCaActivity.this, AddEditEventCaActivity.this);
                    } else {
                        Log.d("AddEditEventCaActivity=","venueLLayout_vDistId==="+vDistId);
                        if (!vDistId.equalsIgnoreCase("")) {
                            getVenueList(districtId);
                        } else {
                            //UIToastMessage.show(AddEditEventCaActivity.this, "Please select district");
                            Toast.makeText(AddEditEventCaActivity.this, "Please select district", Toast.LENGTH_SHORT).show();
                        }
                    }

                } else {
                    Toast.makeText(AddEditEventCaActivity.this, "No internet connection", Toast.LENGTH_SHORT).show();
                    //UIToastMessage.show(AddEditEventCaActivity.this, "No internet connection");
                }

            }
        });

        villageLLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (villageJSONArray != null) {
                    ApUtil.showCustomListPicker(villageTView, villageJSONArray, "Select Village", "name", "code", AddEditEventCaActivity.this, AddEditEventCaActivity.this);
                } else {
                    getCaVillageList();
                }

            }
        });


        kvkLLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ApUtil.checkInternetConnection(AddEditEventCaActivity.this)) {

                    if (kvkJSONArray != null) {
                        // AppUtility.getInstance().showListPicker(kvkTView, kvkJSONArray, "Select Kvk", "name", "id", AddEditEventPsHrdActivity.this, AddEditEventPsHrdActivity.this);
                        ApUtil.showCustomListPicker(kvkTView, kvkJSONArray, "Select Kvk", "name", "id", AddEditEventCaActivity.this, AddEditEventCaActivity.this);
                    } else {
                        getKvkList();
                    }

                } else {
                    UIToastMessage.show(AddEditEventCaActivity.this, "No internet connection");
                }

            }
        });


        coordinatorLLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (eventEndDate.equalsIgnoreCase("")) {
                    //UIToastMessage.show(AddEditEventCaActivity.this, "Please Select End date");
                    Toast.makeText(AddEditEventCaActivity.this, "Please Select End date", Toast.LENGTH_LONG);
                } else if (sledGPId.equalsIgnoreCase("") && sledFarVillageCode.equalsIgnoreCase("") && sledFacilitatorId.length() <= 0 && sledCaResource == null && sledSHGJSONArray == null && sledFPCJSONArray == null && sledFRJSONArray == null) {
                    UIToastMessage.show(AddEditEventCaActivity.this, "Please Select VCRMC(GP) members or Farmer or PoCRA Officials or Resource Person");
                }/*else if (desigId.equalsIgnoreCase("")) {
                    UIToastMessage.show(AddEditEventPsHrdActivity.this, "Please Select designation");
                } */ else {

                    if (ApUtil.checkInternetConnection(AddEditEventCaActivity.this)) {

                        if (desigJSONArray != null) {
                            // AppUtility.getInstance().showListPicker(coordinatorTextView, desigJSONArray, "Select designation", "name", "id", AddEditEventPsHrdActivity.this, AddEditEventPsHrdActivity.this);
                            ApUtil.showCustomListPicker(coordinatorTextView, desigJSONArray, "Select designation", "name", "id", AddEditEventCaActivity.this, AddEditEventCaActivity.this);
                        } else {
                            // getDesigList();
                            getRoleList("Subdivision");
                        }

                    } else {
                        UIToastMessage.show(AddEditEventCaActivity.this, "No internet connection");
                    }

                }
            }
        });

        coCordLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (eventEndDate.equalsIgnoreCase("")) {
                    //UIToastMessage.show(AddEditEventCaActivity.this, "Please Select End date");
                    Toast.makeText(AddEditEventCaActivity.this, "Please Select End date", Toast.LENGTH_LONG);
                } else if (sledGPId.equalsIgnoreCase("") && sledFarVillageCode.equalsIgnoreCase("") && sledFacilitatorId.length() <= 0 && sledCaResource == null && sledSHGJSONArray == null && sledFPCJSONArray == null && sledFRJSONArray == null) {
                    UIToastMessage.show(AddEditEventCaActivity.this, "Please Select VCRMC(GP) members or Farmer or PoCRA Officials or Resource Person");
                } else {
                    if (ApUtil.checkInternetConnection(AddEditEventCaActivity.this)) {

                        if (coDesignationJSONArray != null) {
                            // AppUtility.getInstance().showListPicker(coCordTextView, coDesignationJSONArray, "Select designation", "name", "id", AddEditEventPsHrdActivity.this, AddEditEventPsHrdActivity.this);
                            ApUtil.showCustomListPicker(coCordTextView, coDesignationJSONArray, "Select designation", "name", "id", AddEditEventCaActivity.this, AddEditEventCaActivity.this);
                        } else {
                            // getCocoordinatorDesigList();
                            getCoCordiRoleList("Subdivision");
                        }
                    } else {
                        UIToastMessage.show(AddEditEventCaActivity.this, "No internet connection");
                    }
                }
            }
        });

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (actionType.equalsIgnoreCase("update")) {
                    if (toOnSDateUpdate && toOnEDateUpdate) {
                        updateButtonAction();
                    } else {
                       // UIToastMessage.show(AddEditEventCaActivity.this, "Coordinator not available for selected date");
                        Toast.makeText(AddEditEventCaActivity.this, "Coordinator not available for selected date", Toast.LENGTH_LONG);
                    }

                } else {
                    createButtonAction();
                }
            }
        });

    }

    private boolean isFarmerToSelect() {
        boolean result = false;
        if ((eventTypeId.equalsIgnoreCase("1") && eventSubTypeId.equalsIgnoreCase("113")) || (eventTypeId.equalsIgnoreCase("3") && eventSubTypeId.equalsIgnoreCase("114"))) {
            result = true;
        }
        return result;
    }


    private void startProgressDialog(Context context) {
        progress = new ProgressDialog(context);
        progress.setMessage("please Wait ...");
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();
    }


    // Alert for data loss
    private void askUserPermission() {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("You will lose all your filled data, are you sure you want to go back? ");
        alertDialogBuilder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                clearInputFields();
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


    /******* Get Lists Data *******/

    // get Event Type
    private void getTalukaIdByCaId() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("role_id", userID);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString());
        AppinventorApi api = new AppinventorApi(this, APIServices.API_VCRMC_URL, "", ApConstants.kMSG, true);
        Retrofit retrofit = api.getRetrofitInstance();
        APIRequest apiRequest = retrofit.create(APIRequest.class);
        Call<JsonObject> responseCall = apiRequest.caGetTalukaIdRequest(requestBody);

        DebugLog.getInstance().d("event_taluka_id=" + responseCall.request().toString());
        DebugLog.getInstance().d("event_taluka_id=" + AppUtility.getInstance().bodyToString(responseCall.request()));

        api.postRequest(responseCall, this, 16);
    }


    // get Event Type
    private void getEventTypeList() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("role_id", roleId);
            jsonObject.put("level", userLaval);
            jsonObject.put("api_key", ApConstants.kAUTHORITY_KEY);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString());
        AppinventorApi api = new AppinventorApi(this, APIServices.BASE_URL, "", ApConstants.kMSG, true);
        Retrofit retrofit = api.getRetrofitInstance();
        APIRequest apiRequest = retrofit.create(APIRequest.class);
        Call<JsonObject> responseCall = apiRequest.eventTypeRequest(requestBody);

        DebugLog.getInstance().d("event_type_list=" + responseCall.request().toString());
        DebugLog.getInstance().d("event_type_list=" + AppUtility.getInstance().bodyToString(responseCall.request()));

        api.postRequest(responseCall, this, 1);
    }


    private void getEventSubTypeList(String eventTypeId) {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("event_type_id", eventTypeId);
            jsonObject.put("role_id", roleId);
            jsonObject.put("api_key", ApConstants.kAUTHORITY_KEY);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString());
        AppinventorApi api = new AppinventorApi(this, APIServices.BASE_URL, "", ApConstants.kMSG, true);
        Retrofit retrofit = api.getRetrofitInstance();
        APIRequest apiRequest = retrofit.create(APIRequest.class);
        Call<JsonObject> responseCall = apiRequest.eventSubTypeRequest(requestBody);

        DebugLog.getInstance().d("event_sub_type_list=" + responseCall.request().toString());
        DebugLog.getInstance().d("event_sub_type_list=" + AppUtility.getInstance().bodyToString(responseCall.request()));

        api.postRequest(responseCall, this, 8);

    }


    private void getParticipantGroupList() {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("role_id", roleId);
            jsonObject.put("api_key", ApConstants.kAUTHORITY_KEY);
            jsonObject.put("event_type_id", eventTypeId); // new added param for CA = 3 march 23
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString());
        AppinventorApi api = new AppinventorApi(this, APIServices.BASE_URL, "", ApConstants.kMSG, true);
        Retrofit retrofit = api.getRetrofitInstance();
        APIRequest apiRequest = retrofit.create(APIRequest.class);
        Call<JsonObject> responseCall = apiRequest.getCaParticipantGroupRequest(requestBody);
        Log.d("teztsds12", AppUtility.getInstance().bodyToString(responseCall.request()));
        DebugLog.getInstance().d("participant_group_list=" + responseCall.request().toString());
        DebugLog.getInstance().d("param" + responseCall.request().toString());
        DebugLog.getInstance().d("participant_group_list=" + AppUtility.getInstance().bodyToString(responseCall.request()));

        api.postRequest(responseCall, this, 4);
    }
    // get All DISTRICT
    private void getDistrictList() {
        AppinventorIncAPI api = new AppinventorIncAPI(this, APIServices.BASE_URL, "", ApConstants.kMSG, true);
        api.getRequestData(APIServices.GET_ALL_DIST_URL, this, 13);
    }


    // get Event Type
    private void getVenueList(String distId) {
        JSONObject jsonObject = new JSONObject();
        Log.d("AddEditEventCaActivity=","getVenueList_vDistId==="+distId);
        try {
            jsonObject.put("district_id", distId);
            jsonObject.put("role_id", roleId);
            jsonObject.put("api_key", ApConstants.kAUTHORITY_KEY);
            jsonObject.put("event_type_id", eventTypeId); // add new param for CA = 3 march 23
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString());
        AppinventorApi api = new AppinventorApi(this, APIServices.BASE_URL, "", ApConstants.kMSG, true);
        Retrofit retrofit = api.getRetrofitInstance();
        APIRequest apiRequest = retrofit.create(APIRequest.class);
        Call<JsonObject> responseCall = apiRequest.getVenueListRequest(requestBody);
        Log.d("param", AppUtility.getInstance().bodyToString(responseCall.request()));
        DebugLog.getInstance().d("event_venue_list=" + responseCall.request().toString());
        DebugLog.getInstance().d("event_venue_list=" + AppUtility.getInstance().bodyToString(responseCall.request()));

        api.postRequest(responseCall, this, 9);
    }


    // get Village List
    private void getCaVillageList() {
        String loginData = AppSettings.getInstance().getValue(this, ApConstants.kLOGIN_DATA, ApConstants.kLOGIN_DATA);
        if (!loginData.equalsIgnoreCase("kLOGIN_DATA")) {
            try {
                JSONObject loginJSON = new JSONObject(loginData);
                // villageJSONArray = loginJSON.getJSONArray("village_data");
                JSONArray caVillageJSONArray = loginJSON.getJSONArray("village_data");
                villageJSONArray = ApUtil.getShortJsonArrayAToZ(this, "name", caVillageJSONArray);
                // JSONObject villageList =
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    private void getKvkList() {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("api_key", ApConstants.kAUTHORITY_KEY);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString());
        AppinventorApi api = new AppinventorApi(this, APIServices.BASE_URL, "", ApConstants.kMSG, true);
        Retrofit retrofit = api.getRetrofitInstance();
        APIRequest apiRequest = retrofit.create(APIRequest.class);
        Call<JsonObject> responseCall = apiRequest.getKvkListRequest(requestBody);

        DebugLog.getInstance().d("kvk_list=" + responseCall.request().toString());
        DebugLog.getInstance().d("kvk_list=" + AppUtility.getInstance().bodyToString(responseCall.request()));

        api.postRequest(responseCall, this, 10);

    }


    private void getScheduledEventBySchId(String Sch_id) {
        String level = AppSettings.getInstance().getValue(this, ApConstants.kUSER_LEVEL, ApConstants.kUSER_LEVEL);

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("schedule_id", Sch_id);
            jsonObject.put("user_id", userID);
            jsonObject.put("role_id", roleId);
            jsonObject.put("level", level);
            jsonObject.put("api_key", ApConstants.kAUTHORITY_KEY);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString());
        AppinventorApi api = new AppinventorApi(this, APIServices.BASE_URL, "", ApConstants.kMSG, true);
        Retrofit retrofit = api.getRetrofitInstance();
        APIRequest apiRequest = retrofit.create(APIRequest.class);
        Call<JsonObject> responseCall = apiRequest.psGetEventDetailRequest(requestBody);

        DebugLog.getInstance().d("event_detail_by_id_param=" + responseCall.request().toString());
        DebugLog.getInstance().d("event_detail_by_id_param=" + AppUtility.getInstance().bodyToString(responseCall.request()));

        api.postRequest(responseCall, this, 5);
    }

    private void getDesigList() {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("api_key", ApConstants.kAUTHORITY_KEY);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString());
        AppinventorApi api = new AppinventorApi(this, APIServices.API_URL, "", ApConstants.kMSG, true);
        Retrofit retrofit = api.getRetrofitInstance();
        APIRequest apiRequest = retrofit.create(APIRequest.class);
        Call<JsonObject> responseCall = apiRequest.cordDesigListRequest(requestBody);

        DebugLog.getInstance().d("designation_list=" + responseCall.request().toString());
        DebugLog.getInstance().d("designation_list=" + AppUtility.getInstance().bodyToString(responseCall.request()));

        api.postRequest(responseCall, this, 11);

    }

    private void getRoleList(String centerName) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("center", centerName);
            jsonObject.put("api_key", ApConstants.kAUTHORITY_KEY);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString());
        AppinventorApi api = new AppinventorApi(this, APIServices.SERVICE_API_URL, "", ApConstants.kMSG, true);
        Retrofit retrofit = api.getRetrofitInstance();
        APIRequest apiRequest = retrofit.create(APIRequest.class);
        Call<JsonObject> responseCall = apiRequest.getFieldStaffRoleRequest(requestBody);

        DebugLog.getInstance().d("role_list_param=" + responseCall.request().toString());
        DebugLog.getInstance().d("role_list_param=" + AppUtility.getInstance().bodyToString(responseCall.request()));

        api.postRequest(responseCall, this, 11);

    }

    private void getCoCordiRoleList(String centerName) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("center", centerName);
            jsonObject.put("api_key", ApConstants.kAUTHORITY_KEY);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString());
        AppinventorApi api = new AppinventorApi(this, APIServices.SERVICE_API_URL, "", ApConstants.kMSG, true);
        Retrofit retrofit = api.getRetrofitInstance();
        APIRequest apiRequest = retrofit.create(APIRequest.class);
        Call<JsonObject> responseCall = apiRequest.getFieldStaffRoleRequest(requestBody);

        DebugLog.getInstance().d("role_list_param=" + responseCall.request().toString());
        DebugLog.getInstance().d("role_list_param=" + AppUtility.getInstance().bodyToString(responseCall.request()));

        api.postRequest(responseCall, this, 12);

    }


    private void getCocoordinatorDesigList() {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("api_key", ApConstants.kAUTHORITY_KEY);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString());
        AppinventorApi api = new AppinventorApi(this, APIServices.API_URL, "", ApConstants.kMSG, true);
        Retrofit retrofit = api.getRetrofitInstance();
        APIRequest apiRequest = retrofit.create(APIRequest.class);
        Call<JsonObject> responseCall = apiRequest.cordDesigListRequest(requestBody);

        DebugLog.getInstance().d("designation_list=" + responseCall.request().toString());
        DebugLog.getInstance().d("designation_list=" + AppUtility.getInstance().bodyToString(responseCall.request()));

        api.postRequest(responseCall, this, 12);

    }


    /* Set scheduled event Data for update */
    private void setScheduledEventData(JSONArray eventJsonArray) {

        // Getting Data
        try {
            JSONObject eventDetailJson = eventJsonArray.getJSONObject(0);

            TrainingDetailModel trDModel = new TrainingDetailModel(eventDetailJson);
            schId = trDModel.getId();

            eventTypeId = trDModel.getEvent_type();
            String event_type_name = trDModel.getEvent_type_name();

            eventSubTypeLLayout.setVisibility(View.VISIBLE);
            eventSubTypeId = trDModel.getEvent_sub_type_id();
            eventSubType = trDModel.getEvent_sub_type_name();
            eventSubTypeTView.setText(eventSubType);
            if (eventSubType.equalsIgnoreCase("Others")) {
                eventTitleLLayout.setVisibility(View.VISIBLE);
            } else {
                eventTitleLLayout.setVisibility(View.GONE);
            }

            String title = trDModel.getTitle();
            eventTypeTextView.setText(event_type_name);
            eventTitleEditText.setText(title);

            memCount = trDModel.getParticipints();
            Log.d("texttttt", memCount);
            participantsEditText.setText("Selected participants = " + memCount);
            String eventStartTime = trDModel.getStart_date();
            scheduledStartDate = eventStartTime;
            eventStartDate = ApUtil.getDateYMDByTimeStamp(eventStartTime);
            AppSettings.getInstance().setValue(this, ApConstants.kS_EVENT_E_DATE, eventStartDate);
            String eventEndTime = trDModel.getEnd_date();
            scheduledEndDate = eventEndTime;
            eventEndDate = ApUtil.getDateYMDByTimeStamp(eventEndTime);
            AppSettings.getInstance().setValue(this, ApConstants.kS_EVENT_E_DATE, eventEndDate);

            startTime = trDModel.getEvent_start_time();
            startTimeAmPm = startTime;
            String startTime24Hrs = ApUtil.con12AMPMto24HrsTimeFormat(eventStartDate, startTime);
            String[] time = startTime24Hrs.split(" ");
            String sTime = time[0].trim();
            String[] timeNew = sTime.split(":");
            startHour = Integer.parseInt(timeNew[0].trim());
            startMinuets = Integer.parseInt(timeNew[1].trim());

            eventSTimeTView.setText(startTime);
            endTime = trDModel.getEvent_end_time();
            eventETimeTView.setText(endTime);

            String eventReportDate = trDModel.getReporting_date();
            if (!eventReportDate.equalsIgnoreCase("")) {
                reportDate = ApUtil.getDateYMDByTimeStamp(eventReportDate);
                String dispReportDate = ApUtil.getDateByTimeStamp(eventReportDate);
                eventRDateTView.setText(dispReportDate);
            }

            reportTime = trDModel.getReporting_time();
            eventRTimeTView.setText(reportTime);

//            vDistId = trDModel.getDistrict_id();
//            Log.d("AddEditEventCaActivity=","vDistId==="+vDistId);
//            String districtName = trDModel.getDistrict_name();
//            Log.d("AddEditEventCaActivity=","trDModel_districtName==="+districtName);
//            vDistTView.setText(districtName);

            if (!vDistId.equalsIgnoreCase("")) {
                venueLLayout.setVisibility(View.VISIBLE);
            } else {
                venueLLayout.setVisibility(View.GONE);
            }

            census_code = trDModel.getCensus_code();
            venue_village = trDModel.getVillage_name();
            villageTView.setText(venue_village);

            eventVenue = trDModel.getVenue_name();
            eventVenueId = trDModel.getVenue();
            eventOtherVenue = trDModel.getOther_venue();

            if (eventVenueId.equalsIgnoreCase("1")) {
                venueELLayout.setVisibility(View.VISIBLE);
                venueLocationEditText.setText(eventOtherVenue);
                kvkLLayout.setVisibility(View.GONE);
            } else if (eventVenueId.equalsIgnoreCase("64")) {
                venueELLayout.setVisibility(View.VISIBLE);
                venueLocationEditText.setText(eventOtherVenue);
                kvkLLayout.setVisibility(View.GONE);
            } else if (eventVenueId.equalsIgnoreCase("2")) {
                kvkLLayout.setVisibility(View.VISIBLE);
                kvkTView.setText(eventOtherVenue);
                venueELLayout.setVisibility(View.GONE);
            } else {
                kvkLLayout.setVisibility(View.GONE);
                venueELLayout.setVisibility(View.GONE);
            }

            venueLocationTView.setText(eventVenue);

            String dispEventStartDate = ApUtil.getDateByTimeStamp(eventStartTime);
            eventStartDateTextView.setText(dispEventStartDate);
            String dispEventEndDate = ApUtil.getDateByTimeStamp(eventEndTime);
            eventEndDateTextView.setText(dispEventEndDate);
            participantsEditText.setText("Selected participants = " + memCount);


            // FOR GP
            JSONArray gpArray = trDModel.getGp();

            if (gpArray != null && gpArray.length() > 0) {
                String inTaluka_Id = "";

                // Selected GP Members
                String sGPMem = AppUtility.getInstance().componentSeparatedByCommaJSONArray(gpArray, "members_id");
                String[] s = sGPMem.split(",");
                toUpdateMem_idSled.addAll(Arrays.asList(s));

                // Selected GPs
                String sGP = AppUtility.getInstance().componentSeparatedByCommaJSONArray(gpArray, "gp_id");
                String[] gp = sGP.split(",");
                toUpdateGpIdSled.addAll(Arrays.asList(gp));

                // Tp get gp by ca id
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

                api.postRequest(responseCall, this, 6);


            }


            // FOR Village
            JSONArray villArray = trDModel.getVillage();

            if (villArray != null) {

                for (int f = 0; f < villArray.length(); f++) {

                    JSONObject vJSON = villArray.getJSONObject(f);
                    String actId = vJSON.getString("activity_id");
                    String actv_is_selected = "1";
                    String farmerId = vJSON.getString("farmer_id");
                    String farmerName = vJSON.getString("farmers_name");
                    String fMobile = vJSON.getString("mobile");
                    String farmerIsSelected = "1";
                    String genderId = vJSON.getString("gender");
                    String actName = vJSON.getString("activity_name");
                    String villageCode = vJSON.getString("village_code");
                    String designationId = "2";
                    String designationName = vJSON.getString("designation_name");

                    eDB.insertFarmerDetail("", "", villageCode, "", actId, actName, actv_is_selected, farmerId,
                            farmerName, fMobile, genderId, "", designationId, designationName, farmerIsSelected);

                }

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        setSelectedFarmer();
                    }
                }, 1000);

            }


            // For Facilitator Array
            final JSONArray poMemAr = trDModel.getFacilitator();
            if (poMemAr != null) {

                JSONArray fArray = new JSONArray();
                for (int ri = 0; ri < poMemAr.length(); ri++) {
                    JSONObject poMemJosn = poMemAr.getJSONObject(ri);
                    poMemJosn.put("is_selected", 1);
                    fArray.put(poMemJosn);
                }

                insertPocraOffParticipantDetail(poMemAr);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        setSelectedFacilitator();
                    }
                }, 1000);
                AppSettings.getInstance().setValue(this, ApConstants.kS_FACILITATOR_ARRAY, fArray.toString());
            }

            // for shg data in edit mode...

            final JSONArray poMemAr1 = trDModel.getSHGEditMode();
            if (poMemAr1 != null) {

                final JSONArray fArray1 = new JSONArray();
                for (int ri = 0; ri < poMemAr1.length(); ri++) {
                    JSONObject poMemJosn = poMemAr1.getJSONObject(ri);
                    poMemJosn.put("is_selected", 1);
                    fArray1.put(poMemJosn);
                }
                insertShgParticipants(poMemAr1);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        setSelctSHGgroup();
                    }
                }, 1000);

                AppSettings.getInstance().setValue(this, ApConstants.kS_SHG_PARTICIPANTS_ARRAY, fArray1.toString());
            }


            // for fpc data in edit mode...

            final JSONArray poMemAr2 = trDModel.getFPCEditMode();
            if (poMemAr2 != null) {

                final JSONArray fArray12 = new JSONArray();
                for (int ri = 0; ri < poMemAr2.length(); ri++) {
                    JSONObject poMemJosn = poMemAr2.getJSONObject(ri);
                    poMemJosn.put("is_selected", 1);
                    fArray12.put(poMemJosn);
                }
                insertFpcParticipants(poMemAr2);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        setSelctFPCgroup();
                    }
                }, 1000);


                AppSettings.getInstance().setValue(this, ApConstants.kS_FPC_PARTICIPANTS_ARRAY, fArray12.toString());
            }



            // for farmer group data in edit mode...

            final JSONArray poMemAr22 = trDModel.getFREditMode();
            if (poMemAr22 != null) {

                final JSONArray fArray122 = new JSONArray();
                for (int ri = 0; ri < poMemAr22.length(); ri++) {
                    JSONObject poMemJosn = poMemAr22.getJSONObject(ri);
                    poMemJosn.put("is_selected", 1);
                    fArray122.put(poMemJosn);
                }

                insertFarmerGroupParticipants(poMemAr22);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        setSelctFRgroup();
                    }
                }, 1000);

                AppSettings.getInstance().setValue(this, ApConstants.kS_farm_PARTICIPANTS_ARRAY, fArray122.toString());
            }



            // For Ca Resource Person
            JSONArray caResourceArray = trDModel.getCa_resource_person();
            if (caResourceArray != null) {

                JSONArray caReArray = new JSONArray();
                for (int ri = 0; ri < caResourceArray.length(); ri++) {
                    JSONObject resourceJosn = caResourceArray.getJSONObject(ri);
                    resourceJosn.put("is_selected", 1);
                    caReArray.put(resourceJosn);
                }

                AppSettings.getInstance().setValue(this, ApConstants.kS_CA_RES_PERSON_ARRAY, caReArray.toString());
            }


            // For Other participant Array
            JSONArray otherArray = trDModel.getOther();
            if (otherArray != null) {

                JSONArray othArray = new JSONArray();
                for (int ri = 0; ri < otherArray.length(); ri++) {
                    JSONObject otherJosn = otherArray.getJSONObject(ri);
                    otherJosn.put("is_selected", 1);
                    othArray.put(otherJosn);
                }

                AppSettings.getInstance().setValue(this, ApConstants.kS_OTH_PARTICIPANTS_ARRAY, othArray.toString());
            }
            // For Other participant Array


            // For Co-coordinator

            JSONArray coCoordinatorArray = null;
            JSONArray coCoordArray = trDModel.getCo_coordinators();
            JSONArray othCoCoordArray = trDModel.getResource_person();

            if (othCoCoordArray != null && othCoCoordArray.length() > 0) {

                coCoordinatorArray = othCoCoordArray;

                if (coCoordinatorArray != null) {
                    JSONArray coCoordSled = new JSONArray();
                    for (int ri = 0; ri < coCoordinatorArray.length(); ri++) {
                        JSONObject coCoJSON = new JSONObject();
                        JSONObject cCJosn = coCoordinatorArray.getJSONObject(ri);
                        String cCId = cCJosn.getString("rp_id");
                        String cCFName = cCJosn.getString("first_name");
                        String cCMName = cCJosn.getString("middle_name");
                        String cCLName = cCJosn.getString("last_name");
                        //String cCName = cCFName +" "+cCMName +" "+cCLName ;
                        coCoJSON.put("id", cCId);
                        coCoJSON.put("role_id", "0");
                        coCoJSON.put("first_name", cCFName);
                        coCoJSON.put("middle_name", cCMName);
                        coCoJSON.put("last_name", cCLName);
                        coCoJSON.put("is_selected", "1");
                        coCoordSled.put(coCoJSON);
                    }
                    AppSettings.getInstance().setValue(this, ApConstants.kS_CO_COORDINATOR, coCoordSled.toString());
                }

            } else if (coCoordArray != null) {
                coCoordinatorArray = coCoordArray;


                if (coCoordinatorArray != null) {
                    JSONArray coCoordSled = new JSONArray();
                    for (int ri = 0; ri < coCoordinatorArray.length(); ri++) {
                        JSONObject coCoJSON = new JSONObject();
                        JSONObject cCJosn = coCoordinatorArray.getJSONObject(ri);
                        String cCId = cCJosn.getString("id");
                        String cCRoleId = cCJosn.getString("role_id");
                        String cCFName = cCJosn.getString("first_name");
                        String cCMName = cCJosn.getString("middle_name");
                        String cCLName = cCJosn.getString("last_name");
                        String mobile = cCJosn.getString("mobile");

                        //String cCName = cCFName +" "+cCMName +" "+cCLName ;
                        coCoJSON.put("id", cCId);
                        coCoJSON.put("role_id", cCRoleId);
                        coCoJSON.put("first_name", cCFName);
                        coCoJSON.put("middle_name", cCMName);
                        coCoJSON.put("last_name", cCLName);
                        coCoJSON.put("mobile", mobile);
                        coCoJSON.put("is_selected", "1");
                        coCoordSled.put(coCoJSON);
                    }
                    AppSettings.getInstance().setValue(this, ApConstants.kS_CO_COORDINATOR, coCoordSled.toString());
                }

            }
            // For Coordinator Person
            JSONArray coordinators = trDModel.getCoordinators();
            if (coordinators != null) {
                desigId = coordinators.getJSONObject(0).getString("role_id");
                JSONArray cordA = new JSONArray();
                for (int ri = 0; ri < coordinators.length(); ri++) {
                    JSONObject cordJosn = coordinators.getJSONObject(ri);
                    cordJosn.put("is_selected", "1");
                    cordA.put(cordJosn);
                }
                AppSettings.getInstance().setValue(this, ApConstants.kS_COORDINATOR, cordA.toString());
            }


            setSelectedCoordinator();
            setSelectedCoCoord();
            setSelectedFacilitator();
            setSelectedResourcePerson();
            setSelectedOtherParticipants();



        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void insertPocraOffParticipantDetail(JSONArray facilitatorJSONArray) {

        try {

            for (int i = 0; i < facilitatorJSONArray.length(); i++) {
                JSONObject faciJSON = facilitatorJSONArray.getJSONObject(i);

                String pocraOffMemId = faciJSON.getString("id");
                String pocraOffMemRoleId = faciJSON.getString("role_id");
                String pocraOffMemFName = faciJSON.getString("first_name");
                String pocraOffMemMName = faciJSON.getString("middle_name");
                String pocraOffMemLName = faciJSON.getString("last_name");
                String pocraOffMemMobile = faciJSON.getString("mobile");
                String pocraOffMemGender = faciJSON.getString("gender");
                String pocraOffMemDesignation = faciJSON.getString("designation");
                String pocraOffMemIsSelected = "1";

                if (!eDB.isFacilitatorExist(pocraOffMemId)) {
                    eDB.insertFfsParticipantsDetail(pocraOffMemId, pocraOffMemRoleId, pocraOffMemFName, pocraOffMemMName, pocraOffMemLName,
                            pocraOffMemMobile, pocraOffMemGender, pocraOffMemDesignation, talukaId, pocraOffMemIsSelected);
                } else {
                    eDB.updateFfsParticipantsTableDetail(pocraOffMemId, pocraOffMemRoleId, pocraOffMemFName, pocraOffMemMName, pocraOffMemLName,
                            pocraOffMemMobile, pocraOffMemGender, pocraOffMemDesignation, talukaId, pocraOffMemIsSelected);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }




    private void insertShgParticipants(JSONArray ShgJSONArray) {

        try {

            for (int i = 0; i < ShgJSONArray.length(); i++) {
                JSONObject farJSON = ShgJSONArray.getJSONObject(i);

                String vcode = AppUtility.getInstance().sanitizeJSONObj(farJSON, "village_census_code");
                String s_name = AppUtility.getInstance().sanitizeJSONObj(farJSON, "name");
                String fMobile = AppUtility.getInstance().sanitizeJSONObj(farJSON, "mobile");
                String flag = AppUtility.getInstance().sanitizeJSONObj(farJSON, "group_flag");
                String sproname = AppUtility.getInstance().sanitizeJSONObj(farJSON, "chief_promoter_president");
                String shg_is_selected = AppUtility.getInstance().sanitizeJSONObj(farJSON, "is_selected");
                String farmer_id = AppUtility.getInstance().sanitizeJSONObj(farJSON, "id");

                if (!eDB.isshgseld(farmer_id)) {
                    eDB.insertshg(vcode, flag, s_name, sproname, shg_is_selected, fMobile, farmer_id);
                } else {
                    eDB.updateShg(vcode, flag, s_name, sproname, shg_is_selected, fMobile, farmer_id);

                }

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    private void insertFpcParticipants(JSONArray ShgJSONArray) {

        try {


            for (int i = 0; i < ShgJSONArray.length(); i++) {
                JSONObject farJSON = ShgJSONArray.getJSONObject(i);

                String taluka_id = AppUtility.getInstance().sanitizeJSONObj(farJSON, "taluka_id");
                String name = AppUtility.getInstance().sanitizeJSONObj(farJSON, "name");
                String contact_no = AppUtility.getInstance().sanitizeJSONObj(farJSON, "contact_no");
                String group_flag = AppUtility.getInstance().sanitizeJSONObj(farJSON, "group_flag");
                String contact_person = AppUtility.getInstance().sanitizeJSONObj(farJSON, "contact_person");
                String is_selected = AppUtility.getInstance().sanitizeJSONObj(farJSON, "is_selected");
                String cin = AppUtility.getInstance().sanitizeJSONObj(farJSON, "cin");
                String farmer_id = AppUtility.getInstance().sanitizeJSONObj(farJSON, "id");

                if (!eDB.isFpcseld(farmer_id)) {
                    eDB.insertFpc(taluka_id, name, contact_no, group_flag, contact_person, is_selected, cin, farmer_id);
                } else {
                    eDB.updateFpc(taluka_id, name, contact_no, group_flag, contact_person, is_selected, cin, farmer_id);
                }

            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void insertFarmerGroupParticipants(JSONArray FarmerGroupJSONArray) {

        try {

                for (int i = 0; i < FarmerGroupJSONArray.length(); i++) {
                    JSONObject farJSON = FarmerGroupJSONArray.getJSONObject(i);
                    String village_name = AppUtility.getInstance().sanitizeJSONObj(farJSON, "village");
                    String vcode = AppUtility.getInstance().sanitizeJSONObj(farJSON, "village_census_code");
                    String group_name = AppUtility.getInstance().sanitizeJSONObj(farJSON, "group_name");
                    String contact_person = AppUtility.getInstance().sanitizeJSONObj(farJSON, "contact_person");
                    String contact_number = AppUtility.getInstance().sanitizeJSONObj(farJSON, "contact_number");
                    String group_flag = AppUtility.getInstance().sanitizeJSONObj(farJSON, "group_flag");
                    String fg_is_selected = AppUtility.getInstance().sanitizeJSONObj(farJSON, "is_selected");
                    String farmer_id = AppUtility.getInstance().sanitizeJSONObj(farJSON, "id");

                    if (!eDB.isFGseld(farmer_id)) {
                        eDB.insertFarmerGroup(village_name, vcode, group_name, contact_person, contact_number, group_flag, fg_is_selected, farmer_id);
                    } else {
                        eDB.updateFarmerGroup(village_name, vcode, group_name, contact_person, contact_number, group_flag, fg_is_selected, farmer_id);
                    }
                }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }




    private void clearInputFields() {

        eventTypeId = "";
        eventSubTypeId = "";
        eventSubType = "";
        vDistId = "";
        eventStartDate = "";
        eventEndDate = "";
        eventStartDateTextView.setText("");
        eventEndDateTextView.setText("");

        AppSettings.getInstance().setValue(AddEditEventCaActivity.this, ApConstants.kS_GP_ARRAY, ApConstants.kS_GP_ARRAY);
        AppSettings.getInstance().setValue(AddEditEventCaActivity.this, ApConstants.kS_FARMER_ARRAY, ApConstants.kS_FARMER_ARRAY);
        AppSettings.getInstance().setValue(AddEditEventCaActivity.this, ApConstants.kS_SHG_PARTICIPANTS_ARRAY, ApConstants.kS_SHG_PARTICIPANTS_ARRAY);
        AppSettings.getInstance().setValue(AddEditEventCaActivity.this, ApConstants.kS_FPC_PARTICIPANTS_ARRAY, ApConstants.kS_FPC_PARTICIPANTS_ARRAY);
        AppSettings.getInstance().setValue(AddEditEventCaActivity.this, ApConstants.kS_farm_PARTICIPANTS_ARRAY, ApConstants.kS_farm_PARTICIPANTS_ARRAY);
        AppSettings.getInstance().setValue(AddEditEventCaActivity.this, ApConstants.kS_FACILITATOR_ARRAY, ApConstants.kS_FACILITATOR_ARRAY);
        AppSettings.getInstance().setValue(AddEditEventCaActivity.this, ApConstants.kS_CA_RES_PERSON_ARRAY, ApConstants.kS_CA_RES_PERSON_ARRAY);
        AppSettings.getInstance().setValue(AddEditEventCaActivity.this, ApConstants.kS_OTH_PARTICIPANTS_ARRAY, ApConstants.kS_OTH_PARTICIPANTS_ARRAY);
        AppSettings.getInstance().setValue(AddEditEventCaActivity.this, ApConstants.kS_RES_PERSON, ApConstants.kS_RES_PERSON);
        AppSettings.getInstance().setValue(AddEditEventCaActivity.this, ApConstants.kS_COORDINATOR, ApConstants.kS_COORDINATOR);
        AppSettings.getInstance().setValue(AddEditEventCaActivity.this, ApConstants.kS_CO_COORDINATOR, ApConstants.kS_CO_COORDINATOR);
        AppSettings.getInstance().setValue(AddEditEventCaActivity.this, ApConstants.kS_EVENT_S_DATE, ApConstants.kS_EVENT_S_DATE);
        AppSettings.getInstance().setValue(AddEditEventCaActivity.this, ApConstants.kS_EVENT_E_DATE, ApConstants.kS_EVENT_E_DATE);
        eDB.deleteAllData();
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onMultiRecyclerViewItemClick(int i, Object o) {

        try {

            if (i == 1) {


                if (sledGPJSONArray != null) {

                    JSONObject jsonObject = (JSONObject) o;
                    String id = jsonObject.getString("gp_id");

                    for (int j = 0; j < sledGPJSONArray.length(); j++) {
                        JSONObject GPJSONObject = sledGPJSONArray.getJSONObject(j);
                        String itemId = GPJSONObject.getString("gp_id");

                        if (itemId.equalsIgnoreCase(id)) {
                            sledGPJSONArray.remove(j);

                            eDB.updateGpIsSelected(itemId, "0");
                            JSONArray memBerByGP = eDB.getSledGpMemIdListByGpId(itemId);
                            String sledMemId = AppUtility.getInstance().componentSeparatedByCommaJSONArray(memBerByGP, "mem_id");
                            String[] s = sledMemId.split(",");
                            for (String sGPMemId : s) {
                                eDB.updateGpMemIsSelected(sGPMemId, "0");
                            }
                        }
                    }


                    AppSettings.getInstance().setValue(AddEditEventCaActivity.this, ApConstants.kS_GP_ARRAY, sledGPJSONArray.toString());
                    sledGPId = AppUtility.getInstance().componentSeparatedByCommaJSONArray(sledGPJSONArray, "gp_id");
                    // For Hiding Layout if all removed
                    if (sledGPJSONArray.length() < 1) {
                        sledGPJSONArray = null;
                        sledVCRMCTView.setVisibility(View.GONE);
                        sledGPLLayout.setVisibility(View.GONE);
                    }

                    getNumberOfParticipant();
                }
            } else if (i == 7) {
                try {
                    if (sledSHGJSONArray != null) {

                        JSONObject jsonObject = (JSONObject) o;
                        String farmerId = jsonObject.getString("village_census_code");

                        for (int j = 0; j < sledSHGJSONArray.length(); j++) {
                            JSONObject farmerJSON = sledSHGJSONArray.getJSONObject(j);
                            String itemId = farmerJSON.getString("village_census_code");

                            if (itemId.equalsIgnoreCase(farmerId)) {
                                sledSHGJSONArray.remove(j);
                                eDB.updateShgIsSelected(itemId, "0");
                            }
                        }
                        AppSettings.getInstance().setValue(AddEditEventCaActivity.this, ApConstants.kS_SHG_PARTICIPANTS_ARRAY, sledSHGJSONArray.toString());
                        //sledVillageId = AppUtility.getInstance().componentSeparatedByCommaJSONArray(sledFarmerJSONArray, "village_id");
                        sledFarVillageCode = AppUtility.getInstance().componentSeparatedByCommaJSONArray(sledSHGJSONArray, "village_code");

                        // For Hiding Layout if all removed
                        if (sledSHGJSONArray.length() < 1) {
                            sledSHGJSONArray = null;
                            selectFarmerRLayout.setVisibility(View.GONE);
                            // sledFarmerTView.setVisibility(View.GONE);
                            // sledFarmerLLayout.setVisibility(View.GONE);
                        }

                        getNumberOfParticipant();
                    }
                } catch (Exception e) {

                }
            } else if (i == 8) {
                try {
                    if (sledFPCJSONArray != null) {

                        JSONObject jsonObject = (JSONObject) o;
                        String farmerId = jsonObject.getString("taluka_id");

                        for (int j = 0; j < sledFPCJSONArray.length(); j++) {
                            JSONObject farmerJSON = sledFPCJSONArray.getJSONObject(j);
                            String itemId = farmerJSON.getString("taluka_id");

                            if (itemId.equalsIgnoreCase(farmerId)) {
                                sledFPCJSONArray.remove(j);
                                eDB.updateShgIsSelected(itemId, "0");
                            }
                        }
                        AppSettings.getInstance().setValue(AddEditEventCaActivity.this, ApConstants.kS_FPC_PARTICIPANTS_ARRAY, sledFPCJSONArray.toString());
                        //sledVillageId = AppUtility.getInstance().componentSeparatedByCommaJSONArray(sledFarmerJSONArray, "village_id");
                        sledFarVillageCode = AppUtility.getInstance().componentSeparatedByCommaJSONArray(sledFPCJSONArray, "village_code");

                        // For Hiding Layout if all removed
                        if (sledFPCJSONArray.length() < 1) {
                            sledFPCJSONArray = null;
                            selectFarmerRLayout.setVisibility(View.GONE);
                            // sledFarmerTView.setVisibility(View.GONE);
                            // sledFarmerLLayout.setVisibility(View.GONE);
                        }

                        getNumberOfParticipant();
                    }
                } catch (Exception e) {

                }
            } else if (i == 9) {
                try {
                    if (sledFRJSONArray != null) {

                        JSONObject jsonObject = (JSONObject) o;
                        String farmerId = jsonObject.getString("village_census_code");

                        for (int j = 0; j < sledSHGJSONArray.length(); j++) {
                            JSONObject farmerJSON = sledFRJSONArray.getJSONObject(j);
                            String itemId = farmerJSON.getString("village_census_code");

                            if (itemId.equalsIgnoreCase(farmerId)) {
                                sledFRJSONArray.remove(j);
                                eDB.updateShgIsSelected(itemId, "0");
                            }
                        }
                        AppSettings.getInstance().setValue(AddEditEventCaActivity.this, ApConstants.kS_farm_PARTICIPANTS_ARRAY, sledFRJSONArray.toString());
                        //sledVillageId = AppUtility.getInstance().componentSeparatedByCommaJSONArray(sledFarmerJSONArray, "village_id");
                        sledFarVillageCode = AppUtility.getInstance().componentSeparatedByCommaJSONArray(sledFRJSONArray, "village_code");

                        // For Hiding Layout if all removed
                        if (sledFRJSONArray.length() < 1) {
                            sledFRJSONArray = null;
                            selectFarmerRLayout.setVisibility(View.GONE);
                            // sledFarmerTView.setVisibility(View.GONE);
                            // sledFarmerLLayout.setVisibility(View.GONE);
                        }

                        getNumberOfParticipant();
                    }
                } catch (Exception e) {

                }
            } else if (i == 2) {

                if (sledFarmerJSONArray != null) {

                    JSONObject jsonObject = (JSONObject) o;
                    String farmerId = jsonObject.getString("id");

                    for (int j = 0; j < sledFarmerJSONArray.length(); j++) {
                        JSONObject farmerJSON = sledFarmerJSONArray.getJSONObject(j);
                        String itemId = farmerJSON.getString("id");

                        if (itemId.equalsIgnoreCase(farmerId)) {
                            sledFarmerJSONArray.remove(j);
                            eDB.updateFarmerIsSelected(itemId, "0");
                        }
                    }
                    AppSettings.getInstance().setValue(AddEditEventCaActivity.this, ApConstants.kS_FARMER_ARRAY, sledFarmerJSONArray.toString());
                    //sledVillageId = AppUtility.getInstance().componentSeparatedByCommaJSONArray(sledFarmerJSONArray, "village_id");
                    sledFarVillageCode = AppUtility.getInstance().componentSeparatedByCommaJSONArray(sledFarmerJSONArray, "village_id");
                    sledActivityId = AppUtility.getInstance().componentSeparatedByCommaJSONArray(sledFarmerJSONArray, "vil_act_id");

                    // For Hiding Layout if all removed
                    if (sledFarmerJSONArray.length() < 1) {
                        sledFarmerJSONArray = null;
                        selectFarmerRLayout.setVisibility(View.GONE);
                        // sledFarmerTView.setVisibility(View.GONE);
                        // sledFarmerLLayout.setVisibility(View.GONE);
                    }

                    getNumberOfParticipant();
                }

            } else if (i == 3) {

                if (sledCoCoordJSONArray != null) {

                    JSONObject jsonObject = (JSONObject) o;
                    String id = jsonObject.getString("id");

                    for (int j = 0; j < sledCoCoordJSONArray.length(); j++) {
                        JSONObject resPerJSONObject = sledCoCoordJSONArray.getJSONObject(j);
                        String itemId = resPerJSONObject.getString("id");

                        if (itemId.equalsIgnoreCase(id)) {
                            sledCoCoordJSONArray.remove(j);
                        }
                    }
                    AppSettings.getInstance().setValue(AddEditEventCaActivity.this, ApConstants.kS_CO_COORDINATOR, sledCoCoordJSONArray.toString());

                    // For Hiding Layout if all removed
                    if (sledCoCoordJSONArray.length() < 1) {
                        sledCoCoordJSONArray = null;
                        sledCoCoordinatorId = null;
                        coCordTextView.setText("");
                        sledCoCoordLLayout.setVisibility(View.GONE);
                    }
                }


            } else if (i == 4) {

                if (sledCordJSONArray != null) {

                    JSONObject jsonObject = (JSONObject) o;
                    String id = jsonObject.getString("id");

                    for (int j = 0; j < sledCordJSONArray.length(); j++) {
                        JSONObject cordJSONObject = sledCordJSONArray.getJSONObject(j);
                        String itemId = cordJSONObject.getString("id");

                        if (itemId.equalsIgnoreCase(id)) {
                            sledCordJSONArray.remove(j);
                        }
                    }

                    AppSettings.getInstance().setValue(AddEditEventCaActivity.this, ApConstants.kS_COORDINATOR, sledCordJSONArray.toString());

                    // For Hiding Layout if all removed
                    if (sledCordJSONArray.length() < 1) {
                        sledCordJSONArray = null;
                        sledCordId = null;
                        coordinatorTextView.setText("");
                        sledCordLLayout.setVisibility(View.GONE);
                    }

                }

            } else if (i == 6) {

                if (sledOthParticipantJSONArray != null) {

                    JSONObject jsonObject = (JSONObject) o;
                    String id = jsonObject.getString("id");

                    for (int j = 0; j < sledOthParticipantJSONArray.length(); j++) {
                        JSONObject othPartiJSONObject = sledOthParticipantJSONArray.getJSONObject(j);
                        String itemId = othPartiJSONObject.getString("id");

                        if (itemId.equalsIgnoreCase(id)) {
                            sledOthParticipantJSONArray.remove(j);
                        }
                    }
                    AppSettings.getInstance().setValue(AddEditEventCaActivity.this, ApConstants.kS_OTH_PARTICIPANTS_ARRAY, sledOthParticipantJSONArray.toString());

                    // For Hiding Layout if all removed
                    if (sledOthParticipantJSONArray.length() < 1) {
                        sledOthParticipantJSONArray = null;
                        sledOthParticipantId = new JSONArray();
                        sledOthParticipantTView.setVisibility(View.GONE);
                        sledOthParticipantLLayout.setVisibility(View.GONE);
                    }
                    getNumberOfParticipant();
                }
            } else if (i == 7) {

                if (sledCaResourceJSONArray != null) {

                    JSONObject jsonObject = (JSONObject) o;
                    String id = jsonObject.getString("id");

                    for (int j = 0; j < sledCaResourceJSONArray.length(); j++) {
                        JSONObject othPartiJSONObject = sledCaResourceJSONArray.getJSONObject(j);
                        String itemId = othPartiJSONObject.getString("id");

                        if (itemId.equalsIgnoreCase(id)) {
                            sledCaResourceJSONArray.remove(j);
                        }
                    }
                    AppSettings.getInstance().setValue(AddEditEventCaActivity.this, ApConstants.kS_CA_RES_PERSON_ARRAY, sledCaResourceJSONArray.toString());

                    // For Hiding Layout if all removed
                    if (sledCaResourceJSONArray.length() < 1) {
                        sledCaResourceJSONArray = null;
                        sledCaResource = new JSONArray();
                        selectCaResourceCountTView.setVisibility(View.GONE);
                        selectCaResourceRLayout.setVisibility(View.GONE);
                    }
                    getNumberOfParticipant();
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    @Override
    public void onTimeSelected(TextView textView, int hour, int min, String amOrPm) {

        if (textView == eventSTimeTView) {
            /*if (hour == 0){
                startHour = 12;
            }else {
                startHour = hour;
            }*/
            startHour = hour;
            startMinuets = min;
            startTimeAmPm = amOrPm;
            eventETimeTView.setText("");

            endHour = 0;
            endMinuets = 0;
            eventSTimeTView.setError(null);

            // report time comment
            eventRTimeTView.setText("");
            reportHour = 0;
            reportMinuets = 0;

        }

        if (textView == eventETimeTView) {
            endHour = hour;
            endMinuets = min;
            endTimeAmPm = amOrPm;
            eventETimeTView.setError(null);
        }

        // For Report time
        if (textView == eventRTimeTView) {
            reportHour = hour;
            reportMinuets = min;
            reportTimeAmPm = amOrPm;
            eventRTimeTView.setError(null);
        }

    }


    @Override
    public void didSelectAlertViewListItem(TextView textView, String s) {

        if (textView == eventTypeTextView) {
            eventTypeId = s;
            eventTypeTextView.setError(null);
            eventType = eventTypeTextView.getText().toString().trim();

            eventSubTypeLLayout.setVisibility(View.VISIBLE);
            eventTitleLLayout.setVisibility(View.GONE);
            getNumberOfParticipant();
            getEventSubTypeList(eventTypeId);
            eventSubTypeTView.setText("");
            eventSubTypeId = "";

            // removeSledFarmer();
        }


        if (textView == eventSubTypeTView) {
            eventSubTypeId = s;
            eventSubTypeTView.setError(null);

            eventSubType = eventSubTypeTView.getText().toString().trim();

            // if (eventSubType.equalsIgnoreCase("Others")) {
            if (eventSubType.equalsIgnoreCase("इतर विषयक") || eventSubType.equalsIgnoreCase("Others") || eventSubType.equalsIgnoreCase("Others(इतर विषयक)")) {
                eventTitleLLayout.setVisibility(View.VISIBLE);
                otherType = eventSubType;
            } /*else if (eventTypeId.equalsIgnoreCase("2") && eventSubTypeId.equalsIgnoreCase("17")){
                eventTitleLLayout.setVisibility(View.VISIBLE);
            }*/ else {
                eventTitleLLayout.setVisibility(View.GONE);
            }


            if (isFarmerToSelect()) {
                selPGroupTView.setHint("Select farmers");

                selectVCRMCRLayout.setVisibility(View.GONE);
                selectPOMRLayout.setVisibility(View.GONE);
                selectOtherRLayout.setVisibility(View.GONE);
                removeSledParticipantExceptFarmer();
            } else {
                selPGroupTView.setHint("Select participants");
                selectFarmerRLayout.setVisibility(View.GONE);
                removeSledFarmer();
            }
        }


        if (textView == selPGroupTView) {
            pGroupId = s;
            selPGroupTView.setError(null);
            partiGroupType = selPGroupTView.getText().toString().trim();
            Log.d("Mayuuu","pGroupId="+pGroupId);
            Log.d("Mayuuu","roleid="+roleId);
            Log.d("Mayuuu","partiGroupType="+partiGroupType);
            if (partiGroupType.equalsIgnoreCase("VCRMC") || pGroupId.equalsIgnoreCase("1")) {
                if(roleId.equalsIgnoreCase("6"))
                {
                AppSettings.getInstance().setValue(AddEditEventCaActivity.this, ApConstants.kEVENT_MEM_TYPE, ApConstants.kEVENT_MEM_VCRMC);
                    Intent intent = new Intent(AddEditEventCaActivity.this,TalukaListActivity.class);  // Comment for testing
                   //Intent intent = new Intent(AddEditEventCaActivity.this, CaFarmerFilterActivity.class);
                    intent.putExtra("subDivId",subDivisionId);
                    intent.putExtra("subDivName",subDivisionName);
                    startActivity(intent);
                }else {
                AppSettings.getInstance().setValue(AddEditEventCaActivity.this, ApConstants.kEVENT_MEM_TYPE, ApConstants.kEVENT_MEM_VCRMC);
                    Intent intent = new Intent(AddEditEventCaActivity.this, GPListCaActivity.class);  // Comment for testing
                    //Intent intent = new Intent(AddEditEventCaActivity.this, CaFarmerFilterActivity.class);
                    startActivity(intent);
                }

            } else if (partiGroupType.equalsIgnoreCase("Farmer") || pGroupId.equalsIgnoreCase("2")) {
                AppSettings.getInstance().setValue(AddEditEventCaActivity.this, ApConstants.kEVENT_MEM_TYPE, ApConstants.kEVENT_MEM_FARMER);
                Intent intent = new Intent(AddEditEventCaActivity.this, CaFarmerFilterActivity.class);
                startActivity(intent);
            } else if (partiGroupType.equalsIgnoreCase("SHG") || pGroupId.equalsIgnoreCase("7")) {
                AppSettings.getInstance().setValue(AddEditEventCaActivity.this, ApConstants.kEVENT_MEM_TYPE, ApConstants.kEVENT_MEM_FARMER);
                Intent intent = new Intent(AddEditEventCaActivity.this, SHGgrFarmerCaActivity.class);
                startActivity(intent);
            } else if (partiGroupType.equalsIgnoreCase("FPC") || pGroupId.equalsIgnoreCase("8")) {
                AppSettings.getInstance().setValue(AddEditEventCaActivity.this, ApConstants.kEVENT_MEM_TYPE, ApConstants.kEVENT_MEM_FARMER);
                Intent intent = new Intent(AddEditEventCaActivity.this, FPCgrFarmerCaActivity.class);
                startActivity(intent);
            } else if (partiGroupType.equalsIgnoreCase("Famers Group") || pGroupId.equalsIgnoreCase("9")) {
                AppSettings.getInstance().setValue(AddEditEventCaActivity.this, ApConstants.kEVENT_MEM_TYPE, ApConstants.kEVENT_MEM_FARMER);
                Intent intent = new Intent(AddEditEventCaActivity.this, FRgrFarmerCaActivity.class);
                startActivity(intent);
            } else if (partiGroupType.equalsIgnoreCase("PoCRA Office Staff") || pGroupId.equalsIgnoreCase("3")) {
                AppSettings.getInstance().setValue(AddEditEventCaActivity.this, ApConstants.kEVENT_MEM_TYPE, ApConstants.kEVENT_MEM_FFS_F);
                Intent intent = new Intent(AddEditEventCaActivity.this, CaParticipantFilterActivity.class);
                startActivity(intent);
            } else if (partiGroupType.equalsIgnoreCase("PoCRA Field Officials") || pGroupId.equalsIgnoreCase("5")) {
                AppSettings.getInstance().setValue(AddEditEventCaActivity.this, ApConstants.kEVENT_MEM_TYPE, ApConstants.kEVENT_MEM_PFS);
                Intent intent = new Intent(AddEditEventCaActivity.this, CaFieldStaffFilterActivity.class);
                intent.putExtra("actType", "PMU_FIELD_STAFF");
                startActivity(intent);
            } else if (partiGroupType.equalsIgnoreCase("Others") || pGroupId.equalsIgnoreCase("4")) {
                Intent intent = new Intent(this, OtherParticipantListActivity.class);
                startActivity(intent);
            } else if (partiGroupType.equalsIgnoreCase("Resource Person") || pGroupId.equalsIgnoreCase("6")) {
                Intent intent = new Intent(this, ResourcePersonActivity.class);
                intent.putExtra("action", "get");
                startActivity(intent);
            }
            selPGroupTView.setText("");
        }
        if (textView == vDistTView) {
            vDistId = s;
            vDistTView.setError(null);
            if (!vDistId.equalsIgnoreCase("")) {
                getVenueList(vDistId);
                venueLLayout.setVisibility(View.VISIBLE);
                venueELLayout.setVisibility(View.GONE);
                notetxt.setVisibility(View.GONE);
            } else {
                venueLLayout.setVisibility(View.GONE);
                venueELLayout.setVisibility(View.GONE);
                notetxt.setVisibility(View.GONE);
            }

            venueLocationTView.setText("");
            eventVenueId = "";

        }


        if (textView == venueLocationTView) {
            eventVenueId = s;
            venueLocationTView.setError(null);
            eventVenue = eventSubTypeTView.getText().toString().trim();
            // getVenueList();

            // For KVK list
            /*if (eventVenueId.equalsIgnoreCase("2")) {
                getKvkList();
                kvkLLayout.setVisibility(View.VISIBLE);
            } else {
                kvkLLayout.setVisibility(View.GONE);
            }*/

            if (eventVenueId.equalsIgnoreCase("1")) {
                venueELLayout.setVisibility(View.VISIBLE);
                notetxt.setVisibility(View.GONE);
                venueLocationEditText.setHint("Enter Venue Location(Allowed only 30 Characters)");

            } else if (eventVenueId.equalsIgnoreCase("64")) {
                venueELLayout.setVisibility(View.VISIBLE);
                notetxt.setVisibility(View.VISIBLE);
                venueLocationEditText.setHint("Please Enter Shorten Url(Allowed only 30 Characters)");


            } else {
                notetxt.setVisibility(View.GONE);

                venueELLayout.setVisibility(View.GONE);
            }
        }

        if (textView == villageTView) {
            census_code = s;
            venue_village = villageTView.getText().toString().trim();
            villageTView.setError(null);
        }


        if (textView == kvkTView) {
            kvkId = s;
            kvkTView.setError(null);
            // eventVenue = eventSubTypeTView.getText().toString().trim();
            // getVenueList();
            if (kvkId.equalsIgnoreCase("1")) {
                venueELLayout.setVisibility(View.VISIBLE);
            } else {
                venueELLayout.setVisibility(View.GONE);
            }
        }


        if (textView == coordinatorTextView) {
            desigId = s;
            String sledFacilitator = "";
            sledCoCoordJSONArray = null;
//            To clear co-coordinator detail after changing coordinator detail
//            AppSettings.getInstance().setValue(this, ApConstants.kS_CO_COORDINATOR, ApConstants.kS_CO_COORDINATOR);
//            sledCoCoordJSONArray = null;
//            sledCoCoordinatorId = null;
//            coCordTextView.setText("");
//            sledCoCoordLLayout.setVisibility(View.GONE);

            JSONArray sledPoMemArray = new JSONArray();
            if (sledPOMemberJSONArray != null) {
                sledPoMemArray = sledPOMemberJSONArray;
            }

            if (desigId.equalsIgnoreCase("0")) {
                /*Intent intent = new Intent(this, PocraOfficialFilterActivity.class);
                intent.putExtra("selectionType","coordinator");
                startActivity(intent);*/
                Intent intent = new Intent(this, SearchPmuMemActivity.class);
                intent.putExtra("startDate", eventStartDate);
                intent.putExtra("endDate", eventEndDate);
                intent.putExtra("selectionType", "coordinator");
                startActivity(intent);

            } else {
                Intent intent = new Intent(AddEditEventCaActivity.this, CoordinatorListActivity.class);
                intent.putExtra("gp_id", sledGPId);
                // intent.putExtra("village_id", sledVillageId);
                intent.putExtra("village_code", sledFarVillageCode);
                intent.putExtra("po_off_mem", sledPoMemArray.toString());
                intent.putExtra("desigId", desigId);
                intent.putExtra("startDate", eventStartDate);
                intent.putExtra("endDate", eventEndDate);
                startActivity(intent);
            }

            coordinatorTextView.setText("");
        }

        if (textView == coCordTextView) {
            desigId = s;

            JSONArray sledPoMemArray = new JSONArray();
            if (sledPOMemberJSONArray != null) {
                sledPoMemArray = sledPOMemberJSONArray;
            }

            if (desigId.equalsIgnoreCase("0")) {
                Intent intent = new Intent(this, SearchPmuMemActivity.class);
                intent.putExtra("startDate", eventStartDate);
                intent.putExtra("endDate", eventEndDate);
                intent.putExtra("selectionType", "co-coordinator");
                startActivity(intent);
                // Commented By Santosh as per new requirement
                // Intent intent = new Intent(this, AddEditCoCoordinatorActivity.class);
                // startActivity(intent);
            } else {
                Intent intent = new Intent(AddEditEventCaActivity.this, CoCoordinatorListActivity.class);
                intent.putExtra("gp_id", sledGPId);
                // intent.putExtra("village_id", sledVillageId);
                intent.putExtra("village_code", sledFarVillageCode);
                intent.putExtra("po_off_mem", sledPoMemArray.toString());
                intent.putExtra("desigId", desigId);
                intent.putExtra("startDate", eventStartDate);
                intent.putExtra("endDate", eventEndDate);
                startActivity(intent);
            }

            coCordTextView.setText("");
        }

    }

    // To deselect/Remove array Selected farmer
    private void removeSledFarmer() {
        if (sledFarmerJSONArray != null) {
            eDB.deleteAllData();
        }

        setSelectedFarmer();
    }

    // To Remove other selected participants
    private void removeSledParticipantExceptFarmer() {
        AppSettings.getInstance().setValue(AddEditEventCaActivity.this, ApConstants.kS_GP_ARRAY, ApConstants.kS_GP_ARRAY);
        AppSettings.getInstance().setValue(AddEditEventCaActivity.this, ApConstants.kS_FARMER_ARRAY, ApConstants.kS_FARMER_ARRAY);
        AppSettings.getInstance().setValue(AddEditEventCaActivity.this, ApConstants.kS_FACILITATOR_ARRAY, ApConstants.kS_FACILITATOR_ARRAY);
        AppSettings.getInstance().setValue(AddEditEventCaActivity.this, ApConstants.kS_OTH_PARTICIPANTS_ARRAY, ApConstants.kS_OTH_PARTICIPANTS_ARRAY);
        AppSettings.getInstance().setValue(AddEditEventCaActivity.this, ApConstants.kS_RES_PERSON, ApConstants.kS_RES_PERSON);
        AppSettings.getInstance().setValue(AddEditEventCaActivity.this, ApConstants.kS_COORDINATOR, ApConstants.kS_COORDINATOR);
        AppSettings.getInstance().setValue(AddEditEventCaActivity.this, ApConstants.kS_CO_COORDINATOR, ApConstants.kS_CO_COORDINATOR);
        eDB.deleteAllData();

        setSelectedGP();
        sledGPId = "";
        setSelectedFacilitator();
        sledFacilitatorId = new JSONArray();
        setSelectedResourcePerson();
        setSelectedOtherParticipants();
        sledOthParticipantId = new JSONArray();
    }


    @Override
    public void onDateSelected(TextView textView, int i, int i1, int i2) {

        if (textView == eventStartDateTextView) {
            if (actionType.equalsIgnoreCase("Update")) {
                if (!scheduledStartDate.equalsIgnoreCase("")) {
                    try {
                        eventStartDate = String.valueOf(i2) + "-" + String.valueOf(i1) + "-" + String.valueOf(i);
                        eventStartDateTextView.setError(null);
                        eventEndDateTextView.setText("");
                        eventEndDate = "";

                        eventRDateTView.setText("");
                        reportDate = "";

                        eventRTimeTView.setText("");
                        reportTime = "";

                        AppSettings.getInstance().setValue(this, ApConstants.kS_EVENT_S_DATE, eventStartDate);

                        // For Start Date
                        String eSDate = ApUtil.getDateYMDByTimeStamp(scheduledStartDate);
                        Date sSdate = new SimpleDateFormat("yyyy-MM-dd").parse(eSDate);
                        Date sDate = new SimpleDateFormat("yyyy-MM-dd").parse(eventStartDate);

                        // For End Date
                        String eEDate = ApUtil.getDateYMDByTimeStamp(scheduledEndDate);
                        Date sEdate = new SimpleDateFormat("yyyy-MM-dd").parse(eEDate);

                        if (sDate.after(sEdate)) {
                            toOnSDateUpdate = true;
                        } else if (sDate.before(sSdate)) {
                            Calendar c = Calendar.getInstance();
                            c.setTime(sSdate);
                            c.add(Calendar.DATE, -1);  // for after 1 day date
                            Date sSDateMinusOne = c.getTime();
                            String sSDateMinus = new SimpleDateFormat("yyyy-MM-dd").format(sSDateMinusOne);
                            isCoordinatorAvailable(eventStartDate, sSDateMinus, "startDate");
                        }

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }

            } else {
                eventStartDate = String.valueOf(i2) + "-" + String.valueOf(i1) + "-" + String.valueOf(i);
                eventStartDateTextView.setError(null);
                eventEndDateTextView.setText("");
                eventEndDate = "";

                eventRDateTView.setText("");
                reportDate = "";

                eventRTimeTView.setText("");
                reportTime = "";

                AppSettings.getInstance().setValue(this, ApConstants.kS_EVENT_S_DATE, eventStartDate);
            }
        }

        if (textView == eventEndDateTextView) {
            if (actionType.equalsIgnoreCase("Update")) {
                if (!scheduledEndDate.equalsIgnoreCase("")) {
                    try {

                        eventEndDate = String.valueOf(i2) + "-" + String.valueOf(i1) + "-" + String.valueOf(i);
                        eventEndDateTextView.setError(null);
                        eventSTimeTView.setText("");
                        startHour = 0;
                        startMinuets = 0;
                        startTime = "";
                        eventETimeTView.setText("");
                        endHour = 0;
                        endMinuets = 0;
                        endTime = "";

                        // For Start Date
                        String eSDate = ApUtil.getDateYMDByTimeStamp(scheduledStartDate);
                        Date eSdate = new SimpleDateFormat("yyyy-MM-dd").parse(eSDate);
                        Date sDate = new SimpleDateFormat("yyyy-MM-dd").parse(eventStartDate);


                        // For End Date
                        AppSettings.getInstance().setValue(this, ApConstants.kS_EVENT_E_DATE, eventEndDate);
                        String sEDate = ApUtil.getDateYMDByTimeStamp(scheduledEndDate);
                        Date sEndDate = new SimpleDateFormat("yyyy-MM-dd").parse(sEDate);
                        Date eDate = new SimpleDateFormat("yyyy-MM-dd").parse(eventEndDate);

                        if (sDate.after(sEndDate) || eDate.before(eSdate)) {
                            isCoordinatorAvailable(eventStartDate, eventEndDate, "endDate");
                        } else if (eDate.after(sEndDate)) {
                            Calendar c = Calendar.getInstance();
                            c.setTime(sEndDate);
                            c.add(Calendar.DATE, 1);  // for after 1 day date
                            Date sEDatePlushOne = c.getTime();
                            String sEDatePlush = new SimpleDateFormat("yyyy-MM-dd").format(sEDatePlushOne);
                            isCoordinatorAvailable(sEDatePlush, eventEndDate, "endDate");
                        } else if (eSdate.equals(sDate) && sEndDate.equals(eDate)) {
                            toOnSDateUpdate = true;
                            toOnEDateUpdate = true;
                        }

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                }

            } else {
                sledCordJSONArray = null;
                //sledResPerJSONArray = null;
                eventEndDate = String.valueOf(i2) + "-" + String.valueOf(i1) + "-" + String.valueOf(i);
                eventEndDateTextView.setError(null);
                eventSTimeTView.setText("");
                startHour = 0;
                startMinuets = 0;
                startTime = "";
                eventETimeTView.setText("");
                endHour = 0;
                endMinuets = 0;
                endTime = "";
                AppSettings.getInstance().setValue(this, ApConstants.kS_EVENT_E_DATE, eventEndDate);
            }
        }


        // For Report day
        if (textView == eventRDateTView) {
            reportDate = String.valueOf(i2) + "-" + String.valueOf(i1) + "-" + String.valueOf(i);
            reportHour = 0;
            reportMinuets = 0;

            eventRTimeTView.setText("");
            reportTime = "";

            eventRDateTView.setError(null);
        }
    }

    private void isCoordinatorAvailable(String startDate, String endDate, String date) {

        sledGPJSONArray = eDB.getSelectedGpList();

        if (sledGPJSONArray != null) {
            sledGPId = AppUtility.getInstance().componentSeparatedByCommaJSONArray(sledGPJSONArray, "gp_id");
        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("grampanchayat_id", sledGPId);
            jsonObject.put("village_id", sledVillageId);
            jsonObject.put("po_off_mem", "");
            jsonObject.put("user_role", desigId);
            jsonObject.put("search_string", "");
            jsonObject.put("role_id", roleId);
            jsonObject.put("api_key", ApConstants.kAUTHORITY_KEY);
            jsonObject.put("start_date", startDate);
            jsonObject.put("end_date", endDate);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString());
        AppinventorApi api = new AppinventorApi(this, APIServices.BASE_URL, "", ApConstants.kMSG, true);
        Retrofit retrofit = api.getRetrofitInstance();
        APIRequest apiRequest = retrofit.create(APIRequest.class);
        Call<JsonObject> responseCall = apiRequest.cordListRequest(requestBody);

        DebugLog.getInstance().d("event_coordinator_list=" + responseCall.request().toString());
        DebugLog.getInstance().d("event_coordinator_list=" + AppUtility.getInstance().bodyToString(responseCall.request()));

        if (date.equalsIgnoreCase("startDate")) {
            api.postRequest(responseCall, this, 14);
        } else {
            api.postRequest(responseCall, this, 15);
        }

    }


    // For Create schedule
    private void createButtonAction() {
        getNumberOfParticipant();

        if (startHour > 0) {
            startTime = startTimeAmPm;
        }

        if (endHour > 0) {
            endTime = endTimeAmPm;
        }

        if (reportHour > 0) {
            reportTime = reportTimeAmPm;
        }

        String eventTitle = eventTitleEditText.getText().toString().trim();
        String eventSubType = eventSubTypeTView.getText().toString().trim();
        String allOthParticipantsId = AppSettings.getInstance().getValue(this, ApConstants.kS_ALL_OTH_PARTICIPANTS_ARRAY, ApConstants.kS_ALL_OTH_PARTICIPANTS_ARRAY);
        String allOthPartiId;
        if (!allOthParticipantsId.equalsIgnoreCase("kS_ALL_OTH_PARTICIPANTS_ARRAY")) {
            allOthPartiId = allOthParticipantsId;
        } else {
            allOthPartiId = "";
        }

        String venueTitle = "";
        if (eventVenueId.equalsIgnoreCase("1") || eventVenueId.equalsIgnoreCase("64")) {
            venueTitle = venueLocationEditText.getText().toString().trim();
        } else if (eventVenueId.equalsIgnoreCase("2")) {
            venueTitle = kvkTView.getText().toString().trim();
        }

        if (eventTypeId.equalsIgnoreCase("")) {
            eventTypeTextView.setError("Select event type");
//            UIToastMessage.show(this, "Select event type");
            Toast.makeText(getBaseContext(), "Select event type", Toast.LENGTH_SHORT).show();
            eventTypeTextView.requestFocus();
            eventTitleEditText.setText("");
        } else if (eventSubTypeId.equalsIgnoreCase("")) {
            eventSubTypeTView.setError("Select event sub type");
//            UIToastMessage.show(this, "Select event sub type");
            Toast.makeText(getBaseContext(), "Select event sub type", Toast.LENGTH_SHORT).show();
            eventSubTypeTView.requestFocus();
            eventTitleEditText.setText("");
        } /*else if (eventSubType.equalsIgnoreCase("Others")) {
            eventTitleEditText.setError("Input event title");
            eventTitleEditText.requestFocus();
        }*/ else if (eventSubType.equalsIgnoreCase(otherType) && eventTitle.equalsIgnoreCase("")) {
            eventTitleEditText.setError("Input event title");
            eventTitleEditText.requestFocus();
        } /*else if (eventSubTypeId.equalsIgnoreCase("1") && eventTitle.equalsIgnoreCase("")) {
            eventTitleEditText.setError("Input event title");
            eventTitleEditText.requestFocus();
        }*/ else if (eventStartDate.equalsIgnoreCase("")) {
            eventStartDateTextView.setError("Please Select start date");
//            UIToastMessage.show(this, "Please select start date");
            Toast.makeText(getBaseContext(), "Please Select start date", Toast.LENGTH_SHORT).show();
            eventStartDateTextView.requestFocus();
        } else if (eventEndDate.equalsIgnoreCase("")) {
            eventEndDateTextView.setError("Please Select End Date");
//            UIToastMessage.show(this, "Please select end date");
            Toast.makeText(getBaseContext(), "Please Select End Date", Toast.LENGTH_SHORT).show();
            eventEndDateTextView.requestFocus();
        } else if (startTime.isEmpty()) {
            eventSTimeTView.setError("Select event start time");
//            UIToastMessage.show(this, "Select event start time");
            Toast.makeText(getBaseContext(), "Select event start time", Toast.LENGTH_SHORT).show();
            eventSTimeTView.requestFocus();
        } else if (endTime.isEmpty()) {
            eventSTimeTView.setError(null);
            eventETimeTView.setError("Select event end time");
//            UIToastMessage.show(this, "Select event end time");
            Toast.makeText(getBaseContext(), "Select event end time", Toast.LENGTH_SHORT).show();
            eventSTimeTView.requestFocus();
        } else if (reportDate.equalsIgnoreCase("")) {
            eventETimeTView.setError(null);
            eventRDateTView.setError("Please Select Reporting Date");
//            UIToastMessage.show(this, "Please Select Reporting Date");
            Toast.makeText(getBaseContext(), "Please Select Reporting Date", Toast.LENGTH_SHORT).show();
            eventRDateTView.requestFocus();
        } else if (reportTime.isEmpty()) {
            eventRDateTView.setError(null);
            eventRTimeTView.setError("Select event Reporting time");
//            UIToastMessage.show(this, "Select event Reporting time");
            Toast.makeText(getBaseContext(), "Select event Reporting time", Toast.LENGTH_SHORT).show();
            eventRTimeTView.requestFocus();
        } else if (memCount.isEmpty() || memCount.equalsIgnoreCase("0")) {
            eventRTimeTView.setError(null);
            selPGroupTView.setError("Select Participants");
//            UIToastMessage.show(this, "Select Participants");
            Toast.makeText(getBaseContext(), "Select Participants", Toast.LENGTH_SHORT).show();
            selPGroupTView.requestFocus();
        } else if (memCount.equalsIgnoreCase("")) {
            selPGroupTView.setError(null);
            participantsEditText.setError("Select participants");
            participantsEditText.requestFocus();
        } else if (census_code.equalsIgnoreCase("")) {
            participantsEditText.setError(null);
            villageTView.setError("Select Village");
//            UIToastMessage.show(this, "Select Village");
            Toast.makeText(getBaseContext(), "Select Village", Toast.LENGTH_SHORT).show();
            villageTView.requestFocus();
        } else if (eventVenueId.equalsIgnoreCase("")) {
            villageTView.setError(null);
            venueLocationTView.setError("Select venue/Location");
//            UIToastMessage.show(this, "Select venue/Location");
            Toast.makeText(getBaseContext(), "Select venue/Location", Toast.LENGTH_SHORT).show();
            venueLocationTView.requestFocus();
            venueLocationEditText.setText("");
        } /* else if (eventVenueId.equalsIgnoreCase("2") && kvkId.equalsIgnoreCase("")) {
            venueLocationTView.setError(null);
            kvkTView.setError("Select KVK");
            UIToastMessage.show(this, "Select KVK");
            kvkTView.requestFocus();
            venueLocationEditText.setText("");
        }*/ else if (eventVenueId.equalsIgnoreCase("1") && venueTitle.equalsIgnoreCase("")) {
            villageTView.setError(null);
            venueLocationEditText.setError("Input venue/Location");
//            UIToastMessage.show(this, "Input venue/Location");
            Toast.makeText(getBaseContext(), "Input venue/Location", Toast.LENGTH_SHORT).show();
            venueLocationEditText.requestFocus();
        } /*else if (eventVenueId.equalsIgnoreCase("64") && venueTitle.equalsIgnoreCase("")) {
            kvkTView.setError(null);
            venueLocationEditText.setError("Input venue/Location");
            UIToastMessage.show(this, "Input link");
            venueLocationEditText.requestFocus();
        }*/ else if (sledCordId == null) {
            venueLocationEditText.setError(null);
            coordinatorTextView.setError("Select Coordinator");
//            UIToastMessage.show(this, "Select Coordinator");
            Toast.makeText(getBaseContext(), "Select Coordinator", Toast.LENGTH_SHORT).show();
            coordinatorTextView.requestFocus();
        } else {

            final JSONObject jsonObject = new JSONObject();
            JSONArray fSledGPArray = getFinalSledGPArray(sledGPJSONArray);
            JSONArray fSledFarmerArray = getFinalSledFarmerArray(sledFarmerJSONArray);
            JSONArray fSledshgArray = sledSHGJSONArray;
            JSONArray fSledfpcArray = sledFPCJSONArray;
            JSONArray fSledfrArray = sledFRJSONArray;
            try {

                jsonObject.put("event_type", eventTypeId);
                jsonObject.put("sub_division_id", subDivisionId);
                jsonObject.put("event_sub_type", eventSubTypeId);
                jsonObject.put("title", eventTitle);

                if (fSledGPArray != null && fSledGPArray.length() > 0) {
                    jsonObject.put("gp", fSledGPArray);
                } else {
                    jsonObject.put("gp", new JSONArray());
                }

                // if (fSledFarmerArray != null && isFarmerToSelect()) {
                if (fSledFarmerArray != null) {
                    jsonObject.put("village", fSledFarmerArray);
                } else {
                    jsonObject.put("village", new JSONArray());
                }
                if (fSledshgArray != null) {
                    jsonObject.put("SHG", fSledshgArray);
                } else {
                    jsonObject.put("SHG", new JSONArray());
                }
                if (fSledfpcArray != null) {
                    jsonObject.put("FPC", fSledfpcArray);
                } else {
                    jsonObject.put("FPC", new JSONArray());
                }
                if (fSledfrArray != null) {
                    jsonObject.put("Farmers_group", fSledfrArray);
                } else {
                    jsonObject.put("Farmers_group", new JSONArray());
                }


                // for Resource person
                jsonObject.put("ca_resource_person", sledCaResource);

                jsonObject.put("facilitator", sledFacilitatorId);
                jsonObject.put("other", sledOthParticipantId);
                jsonObject.put("allOtherParticipantId", allOthPartiId);
                jsonObject.put("participints", memCount);
                jsonObject.put("start_date", eventStartDate);
                jsonObject.put("event_start_time", startTime);
                jsonObject.put("event_end_time", endTime);
                jsonObject.put("end_date", eventEndDate);
                // For reporting date time
                jsonObject.put("reporting_date", reportDate);
                jsonObject.put("reporting_time", reportTime);
                jsonObject.put("district_id", districtId);
                jsonObject.put("census_code", census_code);
                jsonObject.put("village_name", venue_village);
                jsonObject.put("venue", eventVenueId);
                jsonObject.put("other_venue", venueTitle);
                jsonObject.put("coordinator", sledCordId);
                jsonObject.put("resource_person", sledCoCoordinatorId);

                jsonObject.put("user_id", userID);
                jsonObject.put("role_id", roleId);
                jsonObject.put("creater_location_id", subDivisionId);
                jsonObject.put("creater_level", userLaval);

                jsonObject.put("user_mobile_no", userMobile);

                if (sledCoCoordinatorArray == null) {
                    jsonObject.put("co_coordinator", null);
                } else {
                    jsonObject.put("co_coordinator", sledCoCoordinatorArray);
                }

                jsonObject.put("media", ApConstants.kMEDIA_TYPE);
                jsonObject.put("api_key", ApConstants.kAUTHORITY_KEY);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            final RequestBody requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString());

            // For User permission
            final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setMessage("Are you sure want to create event?");
            alertDialogBuilder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    AppinventorApi api = new AppinventorApi(AddEditEventCaActivity.this, APIServices.BASE_URL, "", ApConstants.kMSG, true);
                    Retrofit retrofit = api.getRetrofitInstance();
                    APIRequest apiRequest = retrofit.create(APIRequest.class);
                    Call<JsonObject> responseCall = apiRequest.caCreateScheduleRequest(requestBody);

                    DebugLog.getInstance().d("Create_schedule_param=" + responseCall.request().toString());
                    DebugLog.getInstance().d("Create_schedule_param=" + AppUtility.getInstance().bodyToString(responseCall.request()));

                    api.postRequest(responseCall, AddEditEventCaActivity.this, 2);

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
    }


    // VCRMC(GP) request JSONArray
    private JSONArray getFinalSledGPArray(JSONArray sledGPJSONArray) {
        JSONArray jsonArray = new JSONArray();

        try {

            if (sledGPJSONArray != null && sledGPJSONArray.length() > 0) {

                for (int i = 0; i < sledGPJSONArray.length(); i++) {

                    JSONObject jsonObject = new JSONObject();

                    JSONObject gpJSon = sledGPJSONArray.getJSONObject(i);
                    String taId = gpJSon.getString("taluka_id");
                    String gpId = gpJSon.getString("gp_id");
                    JSONArray memJSONArray = eDB.getSledGpMemIdListByGpId(gpId);
                    String gpMemID = AppUtility.getInstance().componentSeparatedByCommaJSONArray(memJSONArray, "mem_id");

                    jsonObject.put("taluka_id", taId);
                    jsonObject.put("gp_id", gpId);
                    jsonObject.put("members_id", gpMemID);
                    jsonArray.put(jsonObject);

                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonArray;
    }


    // Farmer request JSONArray
    private JSONArray getFinalSledFarmerArray(JSONArray sledFarmerArray) {
        JSONArray jsonArray = new JSONArray();
        try {

            if (sledFarmerArray != null && sledFarmerArray.length() > 0) {

                for (int j = 0; j < sledFarmerArray.length(); j++) {
                    JSONObject nFarJSON = new JSONObject();
                    JSONObject farmerJSON = sledFarmerArray.getJSONObject(j);

                    nFarJSON.put("activity_id", farmerJSON.getString("vil_act_id"));
                    nFarJSON.put("village_code", farmerJSON.getString("village_code"));
                    nFarJSON.put("farmer_id", farmerJSON.getString("id"));

                    jsonArray.put(nFarJSON);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonArray;

    }


    // For Update schedule
    private void updateButtonAction() {

        if (startHour > 0) {
            startTime = startTimeAmPm;
        }

        if (endHour > 0) {
            endTime = endTimeAmPm;
        }

        if (reportHour > 0) {
            reportTime = reportTimeAmPm;
        }


        String eventTitle = eventTitleEditText.getText().toString().trim();
        String eventSubType = eventSubTypeTView.getText().toString().trim();
        String allOthParticipantsId = AppSettings.getInstance().getValue(this, ApConstants.kS_ALL_OTH_PARTICIPANTS_ARRAY, ApConstants.kS_ALL_OTH_PARTICIPANTS_ARRAY);
        String allOthPartiId;
        if (!allOthParticipantsId.equalsIgnoreCase("kS_ALL_OTH_PARTICIPANTS_ARRAY")) {
            allOthPartiId = allOthParticipantsId;
        } else {
            allOthPartiId = "";
        }

        String venueTitle = "";
        if (eventVenueId.equalsIgnoreCase("1") || eventVenueId.equalsIgnoreCase("64")) {
            venueTitle = venueLocationEditText.getText().toString().trim();
        } else if (eventVenueId.equalsIgnoreCase("2")) {
            venueTitle = kvkTView.getText().toString().trim();
        }

        if (eventTypeId.equalsIgnoreCase("")) {
            eventTypeTextView.setError("Select event type");
//            UIToastMessage.show(this, "Select event type");
            Toast.makeText(getBaseContext(), "Select event type", Toast.LENGTH_SHORT).show();
            eventTypeTextView.requestFocus();
            eventTitleEditText.setText("");
        } else if (eventSubTypeId.equalsIgnoreCase("")) {
            eventSubTypeTView.setError("Select event sub type");
//            UIToastMessage.show(this, "Select event sub type");
            Toast.makeText(getBaseContext(), "Select event sub type", Toast.LENGTH_SHORT).show();
            eventSubTypeTView.requestFocus();
            eventTitleEditText.setText("");
        } else if (eventSubType.equalsIgnoreCase(otherType) && eventTitle.equalsIgnoreCase("")) {
            eventTitleEditText.setError("Input event title");
            eventTitleEditText.requestFocus();
        } else if (eventStartDate.equalsIgnoreCase("")) {
            eventStartDateTextView.setError("Please Select start date");
//            UIToastMessage.show(this, "Please select start date");
            Toast.makeText(getBaseContext(), "Please Select start date", Toast.LENGTH_SHORT).show();
            eventStartDateTextView.requestFocus();
        } else if (eventEndDate.equalsIgnoreCase("")) {
            eventEndDateTextView.setError("Please Select End Date");
//            UIToastMessage.show(this, "Please select end date");
            Toast.makeText(getBaseContext(), "Please Select End Date", Toast.LENGTH_SHORT).show();
            eventEndDateTextView.requestFocus();
        } else if (startTime.isEmpty()) {
            eventSTimeTView.setError("Select event start time");
//            UIToastMessage.show(this, "Select event start time");
            Toast.makeText(getBaseContext(), "Select event start time", Toast.LENGTH_SHORT).show();
            eventSTimeTView.requestFocus();
        } else if (endTime.isEmpty()) {
            eventSTimeTView.setError(null);
            eventETimeTView.setError("Select event end time");
//            UIToastMessage.show(this, "Select event end time");
            Toast.makeText(getBaseContext(), "Select event end time", Toast.LENGTH_SHORT).show();
            eventSTimeTView.requestFocus();
        } else if (reportDate.equalsIgnoreCase("")) {
            eventETimeTView.setError(null);
            eventRDateTView.setError("Please Select Reporting Date");
//            UIToastMessage.show(this, "Please Select Reporting Date");
            Toast.makeText(getBaseContext(), "Please Select Reporting Date", Toast.LENGTH_SHORT).show();
            eventRDateTView.requestFocus();
        } else if (reportTime.isEmpty()) {
            eventRDateTView.setError(null);
            eventRTimeTView.setError("Select event Reporting time");
//            UIToastMessage.show(this, "Select event Reporting time");
            Toast.makeText(getBaseContext(), "Select event Reporting time", Toast.LENGTH_SHORT).show();
            eventRTimeTView.requestFocus();
        } else if (memCount.isEmpty() || memCount.equalsIgnoreCase("0")) {
            eventRTimeTView.setError(null);
            selPGroupTView.setError("Select Participants");
//            UIToastMessage.show(this, "Select Participants");
            Toast.makeText(getBaseContext(), "Select Participants", Toast.LENGTH_SHORT).show();
            selPGroupTView.requestFocus();
        } else if (memCount.equalsIgnoreCase("")) {
            participantsEditText.setError("Select participants");
            participantsEditText.requestFocus();
        } else if (census_code.equalsIgnoreCase("")) {
            villageTView.setError("Select Village");
//            UIToastMessage.show(this, "Select Village");
            Toast.makeText(getBaseContext(), "Select Village", Toast.LENGTH_SHORT).show();
            villageTView.requestFocus();
        } else if (eventVenueId.equalsIgnoreCase("")) {
            villageTView.setError(null);
            venueLocationTView.setError("Select venue/Location");
//            UIToastMessage.show(this, "Select venue/Location");
            Toast.makeText(getBaseContext(), "Select event sub type", Toast.LENGTH_SHORT).show();
            venueLocationTView.requestFocus();
            venueLocationEditText.setText("");
        }/* else if (eventVenueId.equalsIgnoreCase("2") && kvkId.equalsIgnoreCase("")) {
            venueLocationTView.setError(null);
            kvkTView.setError("Select KVK");
            UIToastMessage.show(this, "Select KVK");
            kvkTView.requestFocus();
            venueLocationEditText.setText("");
        } else if (villageId.equalsIgnoreCase("")) {
            kvkTView.setError(null);
            venueLocationEditText.setError("Select Village");
            UIToastMessage.show(this, "Select Village");
            venueLocationEditText.requestFocus();
        }*/ else if (eventVenueId.equalsIgnoreCase("1") && venueTitle.equalsIgnoreCase("")) {
            villageTView.setError(null);
            venueLocationEditText.setError("Input venue/Location");
            UIToastMessage.show(this, "Input venue/Location");
            venueLocationEditText.requestFocus();
        } /*else if (eventVenueId.equalsIgnoreCase("64") && venueTitle.equalsIgnoreCase("")) {
            kvkTView.setError(null);
            venueLocationEditText.setError("Input venue/Location");
            UIToastMessage.show(this, "Input link");
            venueLocationEditText.requestFocus();
        }*/ else if (sledCordId == null) {
            venueLocationEditText.setError(null);
            coordinatorTextView.setError("Select Coordinator");
            UIToastMessage.show(this, "Select Coordinator");
            coordinatorTextView.requestFocus();
        }/*else if (sledCoCoordinatorId == null) {
            venueLocationEditText.setError(null);
            coCordTextView.setError("Select Co-coordinator");
            UIToastMessage.show(this, "Select co-coordinator");
            coCordTextView.requestFocus();
        }*/ else {

            JSONObject jsonObject = new JSONObject();
            JSONArray fSledGPArray = getFinalSledGPArray(sledGPJSONArray);
            JSONArray fSledFarmerArray = getFinalSledFarmerArray(sledFarmerJSONArray);
            JSONArray fSledshgArray = sledSHGJSONArray;
            JSONArray fSledfpcArray = sledFPCJSONArray;
            JSONArray fSledfrArray = sledFRJSONArray;
            try {

                jsonObject.put("id", schId);
                jsonObject.put("sub_division_id", subDivisionId);
                jsonObject.put("event_type", eventTypeId);
                jsonObject.put("event_sub_type", eventSubTypeId);
                jsonObject.put("title", eventTitle);

                if (fSledGPArray != null && fSledGPArray.length() > 0) {
                    jsonObject.put("gp", fSledGPArray);
                } else {
                    jsonObject.put("gp", new JSONArray());
                }

                if (fSledFarmerArray != null) {
                    jsonObject.put("village", fSledFarmerArray);
                } else {
                    jsonObject.put("village", new JSONArray());
                }
                if (fSledshgArray != null) {
                    jsonObject.put("SHG", fSledshgArray);
                } else {
                    jsonObject.put("SHG", new JSONArray());
                }
                if (fSledfpcArray != null) {
                    jsonObject.put("FPC", fSledfpcArray);
                } else {
                    jsonObject.put("FPC", new JSONArray());
                }
                if (fSledfrArray != null) {
                    jsonObject.put("Farmers_group", fSledfrArray);
                } else {
                    jsonObject.put("Farmers_group", new JSONArray());
                }

                // for Resource person
                jsonObject.put("ca_resource_person", sledCaResource);

                jsonObject.put("facilitator", sledFacilitatorId);
                jsonObject.put("other", sledOthParticipantId);
                jsonObject.put("allOtherParticipantId", allOthPartiId);
                jsonObject.put("participints", memCount);
                jsonObject.put("start_date", eventStartDate);
                jsonObject.put("end_date", eventEndDate);
                jsonObject.put("event_start_time", startTime);
                jsonObject.put("event_end_time", endTime);
                // For reporting date time
                jsonObject.put("reporting_date", reportDate);
                jsonObject.put("reporting_time", reportTime);
                Log.d("MAYU","vDistId="+vDistId);
                Log.d("MAYU","districtId="+districtId);
               // jsonObject.put("district_id", vDistId);
                jsonObject.put("district_id", districtId);
                jsonObject.put("census_code", census_code);
                jsonObject.put("village_name", venue_village);
                jsonObject.put("venue", eventVenueId);
                jsonObject.put("other_venue", venueTitle);
                jsonObject.put("coordinator", sledCordId);
                jsonObject.put("coordinator_id", AppUtility.getInstance().componentSeparatedByCommaJSONArray(sledCordId, "coordinator_id"));
                jsonObject.put("resource_person", sledCoCoordinatorId);

                jsonObject.put("user_id", userID);
                jsonObject.put("role_id", roleId);
                jsonObject.put("creater_location_id", subDivisionId);
                jsonObject.put("creater_level", userLaval);
                jsonObject.put("user_mobile_no", userMobile);

                if (sledCoCoordinatorArray == null) {
                    jsonObject.put("co_coordinator", null);
                } else {
                    jsonObject.put("co_coordinator", sledCoCoordinatorArray);
                }

                jsonObject.put("media", ApConstants.kMEDIA_TYPE);
                jsonObject.put("api_key", ApConstants.kAUTHORITY_KEY);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            final RequestBody requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString());
            // For User permission

            final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setMessage("Are you sure want to Update event?");
            alertDialogBuilder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    AppinventorApi api = new AppinventorApi(AddEditEventCaActivity.this, APIServices.BASE_URL, "", ApConstants.kMSG, true);
                    Retrofit retrofit = api.getRetrofitInstance();
                    APIRequest apiRequest = retrofit.create(APIRequest.class);
                    Call<JsonObject> responseCall = apiRequest.caUpdateScheduleRequest(requestBody);

                    DebugLog.getInstance().d("Update_schedule_param=" + responseCall.request().toString());
                    DebugLog.getInstance().d("Update_schedule_param=" + AppUtility.getInstance().bodyToString(responseCall.request()));

                    api.postRequest(responseCall, AddEditEventCaActivity.this, 3);

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
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onResponse(JSONObject jsonObject, int i) {

        try {

            if (jsonObject != null) {

                // Event type Response
                if (i == 1) {
                    ResponseModel eventListRMode = new ResponseModel(jsonObject);
                    if (eventListRMode.isStatus()) {
                        eventTypeJSONArray = jsonObject.getJSONArray("data");
                    }
                }


                // Create Schedule Response
                if (i == 2) {
                    Log.d("djsghfdgh0",jsonObject.toString());
                    ResponseModel responseModel = new ResponseModel(jsonObject);
                    if (responseModel.isStatus()) {
                        String msg = responseModel.getMsg();
//                        UIToastMessage.show(this, msg);
                        Toast.makeText(this, ""+msg, Toast.LENGTH_SHORT).show();
                        clearInputFields();
                        finish();
                    } else {
//                        UIToastMessage.show(this, responseModel.getMsg());
                        Toast.makeText(this, ""+responseModel.getMsg(), Toast.LENGTH_SHORT).show();
                    }
                }


                // Update Schedule Response
                if (i == 3) {
                    ResponseModel responseModel = new ResponseModel(jsonObject);
                    if (responseModel.isStatus()) {
//                        UIToastMessage.show(this, responseModel.getMsg());
                        Toast.makeText(this, ""+responseModel.getMsg(), Toast.LENGTH_SHORT).show();
                        clearInputFields();
                        finish();
                    } else {
//                        UIToastMessage.show(this, responseModel.getMsg());
                        Toast.makeText(this, ""+responseModel.getMsg(), Toast.LENGTH_SHORT).show();
                    }
                }


                // get Participants Group Response
                if (i == 4) {
                    ResponseModel responseModel = new ResponseModel(jsonObject);
                    if (responseModel.isStatus()) {
                        partiGroupJSONArray = responseModel.getData();
                    } else {
                        //UIToastMessage.show(this, responseModel.getMsg());
                        Toast.makeText(this, ""+responseModel.getMsg(), Toast.LENGTH_SHORT).show();
                    }
                }


                // get Event detail Response
                if (i == 5) {
                    ResponseModel responseModel = new ResponseModel(jsonObject);
                    if (responseModel.isStatus()) {
                        JSONArray eventJSONArray = responseModel.getData();
                        setScheduledEventData(eventJSONArray);
                    } else {
                       // UIToastMessage.show(this, responseModel.getMsg());
                        Toast.makeText(this, ""+responseModel.getMsg(), Toast.LENGTH_SHORT).show();
                    }
                }


                // Set detail Response
                if (i == 6) {

                    ResponseModel responseModel = new ResponseModel(jsonObject);

                    if (responseModel.isStatus()) {

                        startProgressDialog(this);
                        JSONArray gpJSONArray = responseModel.getData();

                        try {

                            for (int gI = 0; gI < gpJSONArray.length(); gI++) {
                                JSONObject gpJSONObject = gpJSONArray.getJSONObject(gI);

                                // For Gp Detail
                                GPModel gpModel = new GPModel(gpJSONObject);
                                String gpId = gpModel.getId();
                                String gpName = gpModel.getName();
                                String gpCode = gpModel.getCode();
                                String gpIsSelected = gpModel.getIs_selected();
                                JSONArray jsonArray = gpJSONObject.getJSONArray("vcrmc_member");

                                // For Member Detail
                                if (jsonArray.length() > 0) {

                                    for (int j = 0; j < jsonArray.length(); j++) {

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

                                        if (!eDB.isGpMemExist(memId)) {
                                            eDB.insertGPByCaWithMemDetail(userID, gpId, gpName, gpCode, gpIsSelected, memId, memName, memFName, memMName, memLName, memMobile, memMobile2,
                                                    memDesigID, memDesigName, memGenderId, memGenderName, memSocCatId, memSocCatName, memHoldCat, memIsSelected);
                                        }
                                    }
                                }
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        // to Set Selected GP
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                for (String sGP : toUpdateGpIdSled) {
                                    eDB.updateGpIsSelected(sGP, "1");
                                }

                                for (String sMemGP : toUpdateMem_idSled) {
                                    eDB.updateGpMemIsSelected(sMemGP, "1");
                                }

                                setSelectedGP();
                            }
                        }, 500);

                        if (progress.isShowing()) {
                            progress.dismiss();
                        }

                    } else {
                        UIToastMessage.show(this, responseModel.getMsg());
                    }
                }


                if (i == 7) {

                    ResponseModel fLModel = new ResponseModel(jsonObject);
                    if (fLModel.isStatus()) {

                        JSONArray farmerJSONArray = jsonObject.getJSONArray("data");
                        if (farmerJSONArray.length() > 0) {

                            for (int fI = 0; fI < farmerJSONArray.length(); fI++) {
                                JSONObject farJSON = farmerJSONArray.getJSONObject(fI);

                                String farmerId = AppUtility.getInstance().sanitizeJSONObj(farJSON, "id");
                                String farmerName = AppUtility.getInstance().sanitizeJSONObj(farJSON, "name");
                                String villageCode = AppUtility.getInstance().sanitizeJSONObj(farJSON, "census_code");
                                String farmerIsSelected = farJSON.getString("is_selected");
                                String fMobile = AppUtility.getInstance().sanitizeJSONObj(farJSON, "mobile");
                                String designationId = AppUtility.getInstance().sanitizeJSONObj(farJSON, "designation_id");
                                String designationName = AppUtility.getInstance().sanitizeJSONObj(farJSON, "designation_name");
                                String genderId = AppUtility.getInstance().sanitizeJSONObj(farJSON, "gender_id");
                                String actId = AppUtility.getInstance().sanitizeJSONObj(farJSON, "activity_id");
                                String actName = AppUtility.getInstance().sanitizeJSONObj(farJSON, "activity_name");
                                String actv_is_selected = AppUtility.getInstance().sanitizeJSONObj(farJSON, "actv_is_selected");
                                ;

                                if (!eDB.isFarmerExist(farmerId)) {
                                    eDB.insertFarmerDetail("", "", villageCode, "", actId, actName, actv_is_selected, farmerId,
                                            farmerName, fMobile, genderId, "", designationId, designationName, farmerIsSelected);
                                }
                            }


                            for (String fId : toUpdateFarmer_idSled) {
                                if (eDB.isFarmerExist(fId)) {
                                    eDB.updateFarmerIsSelected(fId, "1");
                                }

                            }

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    setSelectedFarmer();
                                }
                            }, 500);
                        }

                    } else {
                        UIToastMessage.show(this, fLModel.getMsg());
                    }
                }


                // Event sub type Response
                if (i == 8) {
                    ResponseModel eventListRMode = new ResponseModel(jsonObject);
                    if (eventListRMode.isStatus()) {
                        // eventSubTypeJSONArray = jsonObject.getJSONArray("data");
                        JSONArray subTypeJSONArray = jsonObject.getJSONArray("data");

                        //  To Remove Farmer // ---> Commented By Santosh
                        for (int e = 0; e < subTypeJSONArray.length(); e++) {
                            JSONObject subEventJson = subTypeJSONArray.getJSONObject(e);
                            String id = subEventJson.getString("id");
                            if ((eventTypeId.equalsIgnoreCase("1") && id.equalsIgnoreCase("113")) || (eventTypeId.equalsIgnoreCase("3") && id.equalsIgnoreCase("114"))) {
                                subTypeJSONArray.remove(e);
                            }
                        }
                        eventSubTypeJSONArray = subTypeJSONArray;
                    }
                }


                // Event venue Response
                if (i == 9) {
                    ResponseModel eventListRMode = new ResponseModel(jsonObject);
                    if (eventListRMode.isStatus()) {
                        // {"status":200,"response":"Venue List","data":[{"id":4,"name":"Rameti, Amaravati"},{"id":5,"name":"Krishi Vigyan Kendra,“Chirantan” Madhuban Colony, Camp"},{"id":6,"name":"Krishi Vigyan Kendra, PO.Badnera (Durgapur)"}]}
                        eventVenueJSONArray = eventListRMode.getData();
                    }
                }


                // Event kvk venue Response
                if (i == 10) {
                    ResponseModel kvkListRMode = new ResponseModel(jsonObject);
                    if (kvkListRMode.isStatus()) {
                        kvkJSONArray = jsonObject.getJSONArray("data");
                    }
                }

                // coordinator designation Response
                if (i == 11) {
                    ResponseModel desigListRMode = new ResponseModel(jsonObject);

                    if (desigListRMode.isStatus()) {
                        if (sledGPJSONArray != null || sledFarmerJSONArray != null || sledSHGJSONArray != null || sledFPCJSONArray != null || sledFRJSONArray != null) {
                            desigJSONArray = jsonObject.getJSONArray("data");
                            JSONObject jsonObject0 = new JSONObject();
                            jsonObject0.put("name", "Select from other PoCRA officials");
                            jsonObject0.put("id", 0);
                            desigJSONArray.put(jsonObject0);
                        } else {
                            desigJSONArray = new JSONArray();
                            JSONObject jsonObject0 = new JSONObject();
                            jsonObject0.put("name", "Select from other PoCRA officials");
                            jsonObject0.put("id", 0);
                            desigJSONArray.put(jsonObject0);
                        }
                    }
                }

                //  co-coordinator designation Response
                if (i == 12) {
                    ResponseModel coDesigListRMode = new ResponseModel(jsonObject);
                    if (coDesigListRMode.isStatus()) {
                        if (sledGPJSONArray != null || sledFarmerJSONArray != null || sledSHGJSONArray != null || sledFPCJSONArray != null || sledFRJSONArray != null) {
                            coDesignationJSONArray = jsonObject.getJSONArray("data");
                            JSONObject jsonObject0 = new JSONObject();
                            jsonObject0.put("name", "Select from other PoCRA officials");
                            jsonObject0.put("id", 0);
                            coDesignationJSONArray.put(jsonObject0);
                        } else {
                            coDesignationJSONArray = new JSONArray();
                            JSONObject jsonObject0 = new JSONObject();
                            jsonObject0.put("name", "Select from other PoCRA officials");
                            jsonObject0.put("id", 0);
                            coDesignationJSONArray.put(jsonObject0);
                        }
                    }

                    //  commented As per new requirement - Santosh
                    /*if (kvkListRMode.isStatus()) {
                        coDesignationJSONArray = jsonObject.getJSONArray("data");
                        JSONObject jsonObject0 = new JSONObject();
                        // jsonObject0.put("name", "Add Other");
                        jsonObject0.put("name", "Select from other PoCRA officials");
                        jsonObject0.put("id", 0);
                        coDesignationJSONArray.put(jsonObject0);
                    }*/
                }

                // district list Response
                if (i == 13) {
                    ResponseModel distListRMode = new ResponseModel(jsonObject);
                    if (distListRMode.isStatus()) {
                        vDistJSONArray = jsonObject.getJSONArray("data");
                    }
                }


                // is Coordinator Available On start date Response
                if (i == 14) {
                    ResponseModel distListRMode = new ResponseModel(jsonObject);
                    if (distListRMode.isStatus()) {
                        JSONArray coordinatorList = jsonObject.getJSONArray("data");
                        Boolean result = false;
                        for (int c = 0; c < sledCordId.length(); c++) {
                            JSONObject cordJSON = sledCordId.getJSONObject(c);
                            String coId = cordJSON.getString("coordinator_id");
                            if (coordinatorList.length() > 0) {
                                for (int ac = 0; ac < coordinatorList.length(); ac++) {
                                    JSONObject aCoJS = coordinatorList.getJSONObject(ac);
                                    String aCoId = aCoJS.getString("id");
                                    if (coId.equalsIgnoreCase(aCoId)) {
                                        result = true;
                                    }
                                }
                            }
                        }

                        if (result) {
                            toOnSDateUpdate = true;
                        } else {
                            if (eventStartDate.equalsIgnoreCase(ApUtil.getDateYMDByTimeStamp(scheduledStartDate)) && eventEndDate.equalsIgnoreCase(ApUtil.getDateYMDByTimeStamp(scheduledEndDate))) {
                                toOnSDateUpdate = true;
                            } else {
                                toOnSDateUpdate = false;
                            }
                        }
                    }
                }

                // is Coordinator Available on End date Response
                if (i == 15) {
                    ResponseModel distListRMode = new ResponseModel(jsonObject);
                    if (distListRMode.isStatus()) {
                        JSONArray coordinatorList = jsonObject.getJSONArray("data");
                        Boolean result = false;
                        for (int c = 0; c < sledCordId.length(); c++) {
                            JSONObject cordJSON = sledCordId.getJSONObject(c);
                            String coId = cordJSON.getString("coordinator_id");
                            if (coordinatorList.length() > 0) {
                                for (int ac = 0; ac < coordinatorList.length(); ac++) {
                                    JSONObject aCoJS = coordinatorList.getJSONObject(ac);
                                    String aCoId = aCoJS.getString("id");
                                    if (coId.equalsIgnoreCase(aCoId)) {
                                        result = true;
                                    }
                                }
                            }
                        }

                        if (result) {
                            toOnEDateUpdate = true;
                        } else {
                            if (eventStartDate.equalsIgnoreCase(ApUtil.getDateYMDByTimeStamp(scheduledStartDate)) && eventEndDate.equalsIgnoreCase(ApUtil.getDateYMDByTimeStamp(scheduledEndDate))) {
                                toOnEDateUpdate = true;
                            } else {
                                toOnEDateUpdate = false;
                            }

                        }
                    }
                }

                // get Event detail Response
                if (i == 16) {
                    ResponseModel responseModel = new ResponseModel(jsonObject);
                    if (responseModel.isStatus()) {
                        JSONArray talJSONArray = responseModel.getData();
                        if (talJSONArray.length() > 0) {
                            JSONObject talJSON = talJSONArray.getJSONObject(0);
                            talukaId = talJSON.getString("taluka_id");
                        }
                    } else {
                        UIToastMessage.show(this, responseModel.getMsg());
                    }
                }


                // Get CA Village Response
                if (i == 17) {
                    ResponseModel eventListRMode = new ResponseModel(jsonObject);
                    if (eventListRMode.isStatus()) {
                        villageJSONArray = eventListRMode.getData();
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
