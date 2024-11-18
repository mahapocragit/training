package in.gov.pocra.training.activity.ca.ca_upcoming_event;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

import in.co.appinventor.services_api.api.AppinventorApi;
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
import in.gov.pocra.training.activity.ca.add_edit_event_ca.AddEditEventCaActivity;
import in.gov.pocra.training.activity.ca.add_edit_event_ca.ca_farmer_filter.AdaptorSelectedFPC;
import in.gov.pocra.training.activity.ca.add_edit_event_ca.ca_farmer_filter.AdaptorSelectedFR;
import in.gov.pocra.training.activity.ca.add_edit_event_ca.ca_farmer_filter.AdaptorSelectedSGF;
import in.gov.pocra.training.activity.common.participantsList.ParticipantGPListActivity;
import in.gov.pocra.training.activity.common.participantsList.ParticipantsListActivity;
import in.gov.pocra.training.activity.ps_hrd.add_edit_event_ps.AdaptorSelectedCoCoord;
import in.gov.pocra.training.activity.ps_hrd.add_edit_event_ps.AdaptorSelectedCoordinator;
import in.gov.pocra.training.activity.ps_hrd.add_edit_event_ps.AdaptorSelectedFarmer;
import in.gov.pocra.training.activity.ps_hrd.add_edit_event_ps.AdaptorSelectedGP;
import in.gov.pocra.training.activity.ps_hrd.add_edit_event_ps.AdaptorSelectedOthParticipants;
import in.gov.pocra.training.activity.ps_hrd.add_edit_event_ps.AdaptorSelectedPOMember;
import in.gov.pocra.training.activity.ps_hrd.ps_upcoming_event.PsUpcomingEventDetailActivity;
import in.gov.pocra.training.event_db.EventDataBase;
import in.gov.pocra.training.model.online.GPMemberDetailModel;
import in.gov.pocra.training.model.online.GPModel;
import in.gov.pocra.training.model.online.ResponseModel;
import in.gov.pocra.training.model.online.TrainingDetailModel;
import in.gov.pocra.training.util.ApConstants;
import in.gov.pocra.training.util.ApUtil;
import in.gov.pocra.training.web_services.APIRequest;
import in.gov.pocra.training.web_services.APIServices;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Retrofit;

public class CaUpcomingEventDetailActivity extends AppCompatActivity implements DatePickerCallbackListener, AlertListCallbackEventListener, ApiJSONObjCallback, ApiCallbackCode, OnMultiRecyclerItemClickListener {

    EventDataBase eDB;
    private ProgressDialog progress;
    private String actionType = "View";

    private ImageView homeBack;
    private String roleId = "";
    private String userID = "";
    private String districtId = "";
    private String schId = "";
    private String talukaId = "";

    private LinearLayout eventTypeLLayout;
    private TextView eventTypeTextView;
    private JSONArray eventTypeJSONArray = null;
    private String eventTypeId = "";
    private String eventType = "";
    private boolean isBeneficiaryFarmers = false;

    private LinearLayout eventSubTypeLLayout;
    private TextView eventSubTypeTView;
    private JSONArray eventSubTypeJSONArray = null;
    private String eventSubTypeId = "";
    private String eventSubType = "";

    private LinearLayout eventTitleLLayout;
    private TextView eventTitleEditText;

    private RelativeLayout selectCaResourceRLayout;
    private TextView selectCaResourceCountTView;
    private TextView selectCaResourceMoreTView;
    private JSONArray sledCaResourceJSONArray = null;
    private JSONArray sledCaResource = new JSONArray();


    private TextView eventStartDateTextView;
    private String eventStartDate = "";
    private TextView eventEndDateTextView;
    private String eventEndDate = "";
    private TextView eventStartTimeTextView;
    private TextView eventEndTimeTextView;

    private TextView eventReportingDateTView;
    private String eventReportingDate = "";

    private TextView eventReportingTimeTView;
    private String eventReportingTime;

    // Participant Group
    private LinearLayout selectPGroupLLayout;
    private JSONArray partiGroupJSONArray;
    private TextView selPGroupTView;
    private String partiGroupType = "";
    private String pGroupId = "";

    //////////////////////////////////////////////////////////////

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
    private AdaptorSelectedFarmer adaptorSelectedfarmer;
    private String sledVillageId = "";

    // Pocra official
    private RelativeLayout selectPOMRLayout;
    private TextView sledPOMCountTView;
    private TextView sledPOMMoreTView;

    private LinearLayout sledPOMemberLLayout;                 // Selected Facilitator
    private RecyclerView sledPOMemberRView;
    private TextView sledPOMemberTView;
    private JSONArray sledPOMemberJSONArray = null;
    private AdaptorSelectedPOMember adaptorSelectedPOMember;
    private JSONArray sledFacilitatorId = null;

    // Other Participants
    private RelativeLayout selectOtherRLayout;
    private TextView sledOtherCountTView;
    private TextView sledOtherMoreTView;

    private LinearLayout sledOthParticipantLLayout;                 // Selected Facilitator
    private RecyclerView sledOthParticipantRView;
    private TextView sledOthParticipantTView;
    private JSONArray sledOthParticipantJSONArray = null;
    private AdaptorSelectedOthParticipants adaptorSelectedOthParticipant;
    private JSONArray sledOthParticipantId = new JSONArray();

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

    private LinearLayout kvkLLayout;
    private TextView kvkTView;
    private String kvkId = "";
    private JSONArray kvkJSONArray = null;

    private LinearLayout desigLLayout;
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


    private JSONArray sledSHGJSONArray = null;
    private JSONArray sledFPCJSONArray = null;
    private JSONArray sledFRJSONArray = null;


    private AdaptorSelectedSGF adaptorSelectedSGF;
    private AdaptorSelectedFPC adaptorSelectedFpc;
    private AdaptorSelectedFR adaptorSelectedFR;


    //////////////////////////////////////////
    private RelativeLayout selectshgRLayout;
    private TextView sledshgCountTView;
    private TextView sledshgMoreTView;
    private TextView sledShgTView;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ca_upcoming_event_detail_);

        /** For actionbar title in center */
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.attendance_actionbar_layout);
        AppCompatTextView actionTitleTextView = (AppCompatTextView) getSupportActionBar().getCustomView().findViewById(R.id.actionTitleTextView);
        homeBack = (ImageView) getSupportActionBar().getCustomView().findViewById(R.id.backImageView);
        homeBack.setVisibility(View.VISIBLE);
        actionTitleTextView.setText("Event Detail");

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
                clearInputFields();
                finish();

            }
        });


        // To get User Id and Roll Id
        String rId = AppSettings.getInstance().getValue(this, ApConstants.kROLE_ID, ApConstants.kROLE_ID);
        if (!rId.equalsIgnoreCase("kROLE_ID")) {
            roleId = rId;
        }
        String uId = AppSettings.getInstance().getValue(this, ApConstants.kUSER_ID, ApConstants.kUSER_ID);
        if (!uId.equalsIgnoreCase("kUSER_ID")) {
            userID = uId;
        }
        String distID = AppSettings.getInstance().getValue(this, ApConstants.kUSER_DIST_ID, ApConstants.kUSER_DIST_ID);
        if (!distID.equalsIgnoreCase("kUSER_DIST_ID")) {
            districtId = distID;
        }


        // Initialization of VIEWs

        eventTypeLLayout = (LinearLayout) findViewById(R.id.eventTypeLinearLayout);
        eventTypeTextView = (TextView) findViewById(R.id.eventTypeTextView);
        eventTitleEditText = (TextView) findViewById(R.id.eventTitleEditText);

        eventSubTypeLLayout = (LinearLayout) findViewById(R.id.eventSubTypeLLayout);
        eventSubTypeTView = (TextView) findViewById(R.id.eventSubTypeTView);

        eventTitleLLayout = (LinearLayout) findViewById(R.id.eventTitleLLayout);
        eventTitleEditText = (EditText) findViewById(R.id.eventTitleEditText);


        eventStartDateTextView = (TextView) findViewById(R.id.eventStartDateTextView);
        eventEndDateTextView = (TextView) findViewById(R.id.eventEndDateTextView);
        eventStartTimeTextView = (TextView) findViewById(R.id.eventStartTimeTextView);
        eventEndTimeTextView = (TextView) findViewById(R.id.eventEndTimeTextView);

        eventReportingDateTView = (TextView) findViewById(R.id.eventReportingDateTView);
        eventReportingTimeTView = (TextView) findViewById(R.id.eventReportingTimeTView);

        // participant Group
        selectPGroupLLayout = (LinearLayout) findViewById(R.id.selectParOptLLayout);
        selPGroupTView = (TextView) findViewById(R.id.selPGroupTView);


        ///////////////////////////////////////////////////////////////////////////////////////////////////////
        // VCRMC (GP)
        selectVCRMCRLayout = (RelativeLayout) findViewById(R.id.selectVCRMCRLayout);
        sledVCRMCCountTView = (TextView) findViewById(R.id.sledVCRMCCountTView);
        sledVCRMCMoreTView = (TextView) findViewById(R.id.sledVCRMCMoreTView);

        // selectVCRMCLLayout = (LinearLayout)findViewById(R.id.selectVCRMCLLayout);
        sledVCRMCTView = (TextView) findViewById(R.id.sledVCRMCTView);
        sledGPLLayout = (LinearLayout) findViewById(R.id.sledGPLLayout);
        sledGPRView = (RecyclerView) findViewById(R.id.sledGPRView);
        LinearLayoutManager sledGPLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        sledGPRView.setLayoutManager(sledGPLayoutManager);


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


        // Farmer
        selectFarmerRLayout = (RelativeLayout) findViewById(R.id.selectFarmerRLayout);
        sledFarmerCountTView = (TextView) findViewById(R.id.sledFarmerCountTView);
        sledFarmerMoreTView = (TextView) findViewById(R.id.sledFarmerMoreTView);

        // selectFarmerLLayout = (LinearLayout) findViewById(R.id.selectFarmerLLayout);
        sledFarmerLLayout = (LinearLayout) findViewById(R.id.sledFarmerLLayout);
        sledFarmerTView = (TextView) findViewById(R.id.sledFarmerTView);
        sledFarmerRView = (RecyclerView) findViewById(R.id.sledFarmerRView);
        LinearLayoutManager sledFarmerLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        sledFarmerRView.setLayoutManager(sledFarmerLayoutManager);


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

///////////////////////////////////////////////////////////////////////////////////////////////

        // For Participants Count
        participantsEditText = (EditText) findViewById(R.id.participantsEditText);
        participantsEditText.setEnabled(false);

        // For designation
        desigLLayout = (LinearLayout) findViewById(R.id.desigLLayout);

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

        // For Venue
        venueLLayout = (LinearLayout) findViewById(R.id.venueLLayout);
        venueLocationTView = (TextView) findViewById(R.id.venueLocationTView);

        kvkLLayout = (LinearLayout) findViewById(R.id.kvkLLayout);
        kvkTView = (TextView) findViewById(R.id.kvkTView);

        venueELLayout = (LinearLayout) findViewById(R.id.venueELLayout);
        venueLocationEditText = (TextView) findViewById(R.id.venueLocationEditText);
        eventTitleEditText = (EditText) findViewById(R.id.eventTitleEditText);


        String sch_id = getIntent().getStringExtra("sch_id");
        if (!sch_id.equalsIgnoreCase("")) {

            //eventTypeLLayout.setBackgroundResource(R.drawable.edit_border_bg);
            eventTypeLLayout.setEnabled(false);

            //eventSubTypeLLayout.setBackgroundResource(R.drawable.edit_border_bg);
            eventSubTypeLLayout.setEnabled(false);

            // eventTitleEditText.setBackgroundResource(R.drawable.edit_border_bg);
            eventTitleEditText.setEnabled(false);

            // selectPGroupLLayout.setBackgroundResource(R.drawable.edit_border_bg);
            selectPGroupLLayout.setEnabled(false);

//                selectFarmerLLayout.setBackgroundResource(R.drawable.edit_border_bg);
//                selectFarmerLLayout.setEnabled(false);

            // sledFacilitatorLLayout.setBackgroundResource(R.drawable.edit_border_bg);
            // sledFacilitatorLLayout.setEnabled(false);

            // participantsEditText.setBackgroundResource(R.drawable.edit_border_bg);
            participantsEditText.setEnabled(false);

            // vDistLLayout.setBackgroundResource(R.drawable.edit_border_bg);
            vDistLLayout.setEnabled(false);

            // venueLLayout.setBackgroundResource(R.drawable.edit_border_bg);
            venueLLayout.setEnabled(false);

            // kvkLLayout.setBackgroundResource(R.drawable.edit_border_bg);
            kvkLLayout.setEnabled(false);

            // desigLLayout.setBackgroundResource(R.drawable.edit_border_bg);
            desigLLayout.setEnabled(false);

            // coordinatorLLayout.setBackgroundResource(R.drawable.edit_border_bg);
            coordinatorLLayout.setEnabled(false);

            // coCordLinearLayout.setBackgroundResource(R.drawable.edit_border_bg);
            coCordLinearLayout.setEnabled(false);

            getScheduledEventBySchId(sch_id);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();


        getTalukaIdByCaId();

        // For Selected VCRMC (GP)
        setSelectedGP();

        // For Selected Farmer
        setSelectedFarmer();

        setSelctSHGgroup();
        setSelctFPCgroup();
        setSelctFRgroup();


        // For Selected Coordinator
        setSelectedCoordinator();

        // For Selected Facilitator
        setSelectedFacilitator();

        // For Selected CA Resource Person
        setSelectedResourcePerson();

        // For Selected Other participants
        setSelectedOtherParticipants();

        // For Selected Co-Coordinator
        setSelectedCoCoord();


        // To get number of selected participant
        getNumberOfParticipant();

    }


    // VCRMC (GP)
    private void setSelectedGP() {

        sledGPJSONArray = eDB.getSelectedGpList();

        if (sledGPJSONArray.length() > 0) {
            selectVCRMCRLayout.setVisibility(View.VISIBLE);
            //sledVCRMCTView.setVisibility(View.VISIBLE);
            //sledGPLLayout.setVisibility(View.VISIBLE);
            adaptorSelectedGP = new AdaptorSelectedGP(CaUpcomingEventDetailActivity.this, sledGPJSONArray, actionType, CaUpcomingEventDetailActivity.this, this);
            sledGPRView.setAdapter(adaptorSelectedGP);
        } else {
            sledGPJSONArray = null;
            selectVCRMCRLayout.setVisibility(View.GONE);
            //sledVCRMCTView.setVisibility(View.GONE);
            //sledGPLLayout.setVisibility(View.GONE);
        }

        if (sledGPJSONArray != null) {
            sledGPId = AppUtility.getInstance().componentSeparatedByCommaJSONArray(sledGPJSONArray, "gp_id");
            selPGroupTView.setError(null);
        } else {
            selectVCRMCRLayout.setVisibility(View.GONE);
            //sledVCRMCTView.setVisibility(View.GONE);
            //sledGPLLayout.setVisibility(View.GONE);
        }

        getNumberOfParticipant();
    }


    // Farmer
    private void setSelectedFarmer() {

        sledFarmerJSONArray = eDB.getSledFarmerList();
        if (sledFarmerJSONArray.length() > 0) {
            selectFarmerRLayout.setVisibility(View.VISIBLE);
            //sledFarmerTView.setVisibility(View.VISIBLE);
            //sledFarmerLLayout.setVisibility(View.VISIBLE);
            adaptorSelectedfarmer = new AdaptorSelectedFarmer(CaUpcomingEventDetailActivity.this, sledFarmerJSONArray, actionType, CaUpcomingEventDetailActivity.this);
            sledFarmerRView.setAdapter(adaptorSelectedfarmer);
        } else {
            sledFarmerJSONArray = null;
            selectFarmerRLayout.setVisibility(View.GONE);
            //sledFarmerTView.setVisibility(View.GONE);
            //sledFarmerLLayout.setVisibility(View.GONE);
        }


        if (sledFarmerJSONArray != null) {
            JSONArray villageArray = eDB.getSledVillageList();
            //sledVillageId = AppUtility.getInstance().componentSeparatedByCommaJSONArray(villageArray, "village_id");
            sledFarmerTView.setError(null);
        } else {
            selectFarmerRLayout.setVisibility(View.GONE);
            //sledFarmerTView.setVisibility(View.GONE);
            //sledFarmerLLayout.setVisibility(View.GONE);
        }

        getNumberOfParticipant();

    }


    // SHG
    private void setSelctSHGgroup() {

        sledSHGJSONArray = eDB.getShglist();

        if (sledSHGJSONArray != null && sledSHGJSONArray.length() > 0) {
            selectshgRLayout.setVisibility(View.VISIBLE);
            adaptorSelectedSGF = new AdaptorSelectedSGF(CaUpcomingEventDetailActivity.this, sledSHGJSONArray, actionType, CaUpcomingEventDetailActivity.this);
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
            adaptorSelectedFpc = new AdaptorSelectedFPC(CaUpcomingEventDetailActivity.this, sledFPCJSONArray, actionType, CaUpcomingEventDetailActivity.this);
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
            adaptorSelectedFR = new AdaptorSelectedFR(CaUpcomingEventDetailActivity.this, sledFRJSONArray, actionType, CaUpcomingEventDetailActivity.this);
            sledFGRView.setAdapter(adaptorSelectedFR);
            getNumberOfParticipant();
        } else {
            sledFRJSONArray = null;
            selectFGRLayout.setVisibility(View.GONE);
        }
    }


    // FFS Facilitator
    private void setSelectedFacilitator() {

        String sledFacilitator = AppSettings.getInstance().getValue(this, ApConstants.kS_FACILITATOR_ARRAY, ApConstants.kS_FACILITATOR_ARRAY);
        try {
            // sledPOMemberJSONArray = eDB.getSledPOMemberList();
            if (!sledFacilitator.equalsIgnoreCase("kS_FACILITATOR_ARRAY")) {

                sledPOMemberJSONArray = new JSONArray(sledFacilitator);
                if (sledPOMemberJSONArray.length() > 0) {
                    selectPOMRLayout.setVisibility(View.VISIBLE);
                    //sledPOMemberTView.setVisibility(View.VISIBLE);
                    //sledPOMemberLLayout.setVisibility(View.VISIBLE);
                    adaptorSelectedPOMember = new AdaptorSelectedPOMember(CaUpcomingEventDetailActivity.this, sledPOMemberJSONArray, actionType, CaUpcomingEventDetailActivity.this);
                    sledPOMemberRView.setAdapter(adaptorSelectedPOMember);
                } else {
                    sledPOMemberJSONArray = null;
                    selectPOMRLayout.setVisibility(View.GONE);
                    //sledPOMemberTView.setVisibility(View.GONE);
                    //sledPOMemberLLayout.setVisibility(View.GONE);

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

                    // sledCordId = AppUtility.getInstance().componentSeparatedByCommaJSONArray(sledCordJSONArray, "id");
                }

            } else {
                sledPOMemberJSONArray = null;
                selectPOMRLayout.setVisibility(View.GONE);
                //sledPOMemberTView.setVisibility(View.GONE);
                //sledPOMemberLLayout.setVisibility(View.GONE);
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
                    //sledOthParticipantTView.setVisibility(View.VISIBLE);
                    //sledOthParticipantLLayout.setVisibility(View.VISIBLE);
                    adaptorSelectedOthParticipant = new AdaptorSelectedOthParticipants(CaUpcomingEventDetailActivity.this, sledOthParticipantJSONArray, actionType, CaUpcomingEventDetailActivity.this);
                    sledOthParticipantRView.setAdapter(adaptorSelectedOthParticipant);
                } else {
                    sledOthParticipantJSONArray = null;
                    selectOtherRLayout.setVisibility(View.GONE);
                    //sledOthParticipantTView.setVisibility(View.GONE);
                    //sledOthParticipantLLayout.setVisibility(View.GONE);
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
                //sledOthParticipantTView.setVisibility(View.GONE);
                //sledOthParticipantLLayout.setVisibility(View.GONE);
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
                    adaptorSelectedCoCoord = new AdaptorSelectedCoCoord(CaUpcomingEventDetailActivity.this, sledCoCoordJSONArray, actionType, CaUpcomingEventDetailActivity.this);
                    sledCoCoordRView.setAdapter(adaptorSelectedCoCoord);
                } else {
                    sledCoCoordJSONArray = null;
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
                sledCoCoordLLayout.setVisibility(View.GONE);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    // Coordinator
    private void setSelectedCoordinator() {

        String sledCoordinator = AppSettings.getInstance().getValue(this, ApConstants.kS_COORDINATOR, ApConstants.kS_COORDINATOR);
        try {
            if (!sledCoordinator.equalsIgnoreCase("kS_COORDINATOR")) {
                sledCordJSONArray = new JSONArray(sledCoordinator);
                if (sledCordJSONArray.length() > 0) {
                    sledCordLLayout.setVisibility(View.VISIBLE);
                    adaptorSelectedCoordinator = new AdaptorSelectedCoordinator(CaUpcomingEventDetailActivity.this, sledCordJSONArray, actionType, CaUpcomingEventDetailActivity.this);
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
                sledCordLLayout.setVisibility(View.GONE);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    // setSelectedFacilitator

    // To get Number of participants
    private void getNumberOfParticipant() {
        memCount = "";
        int gpMemCount = 0;
        int farmerCount = 0;
        int facilitatorCount = 0;
        int caResourceCount = 0;
        int otherCount = 0;
        int shgCount = 0;
        int fpcCount = 0;
        int frCount = 0;

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


        memCount = String.valueOf(gpMemCount + farmerCount + facilitatorCount + otherCount + caResourceCount + shgCount + fpcCount + frCount);

        if (selectVCRMCRLayout.getVisibility() == View.VISIBLE) {
            sledVCRMCCountTView.setText(" : " + String.valueOf(gpMemCount));
        }
        if (selectFarmerRLayout.getVisibility() == View.VISIBLE) {
            sledFarmerCountTView.setText(" : " + String.valueOf(farmerCount));
        }
        if (selectPOMRLayout.getVisibility() == View.VISIBLE) {
            sledPOMCountTView.setText(" : " + String.valueOf(facilitatorCount));
        }
        if (selectCaResourceRLayout.getVisibility() == View.VISIBLE) {
            selectCaResourceCountTView.setText(" : " + String.valueOf(caResourceCount));
        }
        if (selectOtherRLayout.getVisibility() == View.VISIBLE) {
            sledOtherCountTView.setText(" : " + String.valueOf(otherCount));
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

        participantsEditText.setText(" = " + memCount);
        participantsEditText.setError(null);
        sledCordJSONArray = null;
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        clearInputFields();
    }


    private void defaultConfiguration() {

        sledVCRMCMoreTView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sledGPJSONArray = eDB.getSelectedGpList();

                Intent intent = new Intent(CaUpcomingEventDetailActivity.this, ParticipantGPListActivity.class);
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
                Intent intent = new Intent(CaUpcomingEventDetailActivity.this, ParticipantGPListActivity.class);
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
                Intent intent = new Intent(CaUpcomingEventDetailActivity.this, ParticipantsListActivity.class);
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
                Intent intent = new Intent(CaUpcomingEventDetailActivity.this, ParticipantsListActivity.class);
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
                Intent intent = new Intent(CaUpcomingEventDetailActivity.this, ParticipantsListActivity.class);
                intent.putExtra("sledMemType", "FG");
                intent.putExtra("sledMemArray", sledFRJSONArray.toString());
                intent.putExtra("actionType", actionType);
                startActivity(intent);
            }
        });


        sledPOMMoreTView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // sledPOMemberJSONArray = eDB.getSledPOMemberList();
                String sledPocraOfficials = AppSettings.getInstance().getValue(CaUpcomingEventDetailActivity.this, ApConstants.kS_FACILITATOR_ARRAY, ApConstants.kS_FACILITATOR_ARRAY);
                if (!sledPocraOfficials.equalsIgnoreCase("kS_FACILITATOR_ARRAY")) {
                    try {
                        sledPOMemberJSONArray = new JSONArray(sledPocraOfficials);
                        Intent intent = new Intent(CaUpcomingEventDetailActivity.this, ParticipantsListActivity.class);
                        intent.putExtra("sledMemType", "PoCRA Official");
                        intent.putExtra("sledMemArray", sledPOMemberJSONArray.toString());
                        intent.putExtra("actionType", actionType);
                        startActivity(intent);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        });

        selectCaResourceMoreTView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sledResourceParticipants = AppSettings.getInstance().getValue(CaUpcomingEventDetailActivity.this, ApConstants.kS_CA_RES_PERSON_ARRAY, ApConstants.kS_CA_RES_PERSON_ARRAY);

                try {
                    if (!sledResourceParticipants.equalsIgnoreCase("kS_CA_RES_PERSON_ARRAY")) {
                        sledCaResourceJSONArray = new JSONArray(sledResourceParticipants);
                        Intent intent = new Intent(CaUpcomingEventDetailActivity.this, ParticipantsListActivity.class);
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
                String sledOthParticipants = AppSettings.getInstance().getValue(CaUpcomingEventDetailActivity.this, ApConstants.kS_OTH_PARTICIPANTS_ARRAY, ApConstants.kS_OTH_PARTICIPANTS_ARRAY);

                try {

                    if (!sledOthParticipants.equalsIgnoreCase("kS_OTH_PARTICIPANTS_ARRAY")) {

                        sledOthParticipantJSONArray = new JSONArray(sledOthParticipants);
                        Intent intent = new Intent(CaUpcomingEventDetailActivity.this, ParticipantsListActivity.class);
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
            if (eventSubType.equalsIgnoreCase("Others")) {
                eventTitleLLayout.setVisibility(View.VISIBLE);
            } else {
                eventTitleLLayout.setVisibility(View.GONE);
            }
            eventSubTypeTView.setText(eventSubType);

            String title = trDModel.getTitle();
            memCount = trDModel.getParticipints();
            String eventStartTime = trDModel.getStart_date();
            eventStartDate = ApUtil.getDateYMDByTimeStamp(eventStartTime);
            AppSettings.getInstance().setValue(this, ApConstants.kS_EVENT_E_DATE, eventStartDate);
            String eventEndTime = trDModel.getEnd_date();
            eventEndDate = ApUtil.getDateYMDByTimeStamp(eventEndTime);
            AppSettings.getInstance().setValue(this, ApConstants.kS_EVENT_E_DATE, eventEndDate);
            eventStartTimeTextView.setText(trDModel.getEvent_start_time());
            eventEndTimeTextView.setText(trDModel.getEvent_end_time());

            vDistId = trDModel.getDistrict_id();
            String districtName = trDModel.getDistrict_name();
            vDistTView.setText(districtName);
            eventVenueId = trDModel.getVenue();
            eventVenue = trDModel.getVenue_name();
            eventOtherVenue = trDModel.getOther_venue();

            if (eventVenueId.equalsIgnoreCase("1") || eventVenueId.equalsIgnoreCase("64")) {
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

            eventTypeTextView.setText(event_type_name);
            eventTitleEditText.setText(title);
            String dispEventStartDate = ApUtil.getDateByTimeStamp(eventStartTime);
            eventStartDateTextView.setText(dispEventStartDate);
            String dispEventEndDate = ApUtil.getDateByTimeStamp(eventEndTime);
            eventEndDateTextView.setText(dispEventEndDate);

            String eventReportDate = trDModel.getReporting_date();
            if (!eventReportDate.equalsIgnoreCase("")) {
                eventReportingDate = ApUtil.getDateYMDByTimeStamp(eventReportDate);
                String dispReportDate = ApUtil.getDateByTimeStamp(eventReportDate);
                eventReportingDateTView.setText(dispReportDate);
            }

            eventReportingTime = trDModel.getReporting_time();
            eventReportingTimeTView.setText(eventReportingTime);

            participantsEditText.setText(memCount);


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

                // Tp get gp by CA id
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
            final JSONArray facilitatorAr = trDModel.getFacilitator();
            if (facilitatorAr != null) {

                JSONArray fArray = new JSONArray();
                for (int ri = 0; ri < facilitatorAr.length(); ri++) {
                    JSONObject faciJosn = facilitatorAr.getJSONObject(ri);
                    faciJosn.put("is_selected", 1);
                    fArray.put(faciJosn);
                }

                insertPocraOffParticipantDetail(facilitatorAr);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        setSelectedFacilitator();

                    }
                }, 1000);

                AppSettings.getInstance().setValue(this, ApConstants.kS_FACILITATOR_ARRAY, fArray.toString());
            }


            // for shg data in edit mode...

            JSONArray poMemAr1 = trDModel.getSHGEditMode();
            if (poMemAr1 != null) {

                final JSONArray fArray1 = new JSONArray();
                for (int ri = 0; ri < poMemAr1.length(); ri++) {
                    JSONObject poMemJosn = poMemAr1.getJSONObject(ri);
                    poMemJosn.put("is_selected", 1);
                    fArray1.put(poMemJosn);
                }


                insertShgParticipants(fArray1);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        setSelctSHGgroup();
                    }
                }, 1000);

                AppSettings.getInstance().setValue(this, ApConstants.kS_SHG_PARTICIPANTS_ARRAY, fArray1.toString());
            }


            // for fpc data in edit mode...

            JSONArray poMemAr2 = trDModel.getFPCEditMode();
            if (poMemAr2 != null) {

                final JSONArray fArray12 = new JSONArray();
                for (int ri = 0; ri < poMemAr2.length(); ri++) {
                    JSONObject poMemJosn = poMemAr2.getJSONObject(ri);
                    poMemJosn.put("is_selected", 1);
                    fArray12.put(poMemJosn);
                }


                insertFpcParticipants(fArray12);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        setSelctFPCgroup();
                    }
                }, 1000);

                AppSettings.getInstance().setValue(this, ApConstants.kS_FPC_PARTICIPANTS_ARRAY, fArray12.toString());
            }


            // for farmer group data in edit mode...

            JSONArray poMemAr22 = trDModel.getFREditMode();
            if (poMemAr22 != null) {

                final JSONArray fArray122 = new JSONArray();
                for (int ri = 0; ri < poMemAr22.length(); ri++) {
                    JSONObject poMemJosn = poMemAr22.getJSONObject(ri);
                    poMemJosn.put("is_selected", 1);
                    fArray122.put(poMemJosn);
                }


                insertFarmerGroupParticipants(fArray122);
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


            // For other Array
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
            setSelectedResourcePerson();
            setSelectedFacilitator();
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

        AppSettings.getInstance().setValue(CaUpcomingEventDetailActivity.this, ApConstants.kS_GP_ARRAY, ApConstants.kS_GP_ARRAY);
        AppSettings.getInstance().setValue(CaUpcomingEventDetailActivity.this, ApConstants.kS_FARMER_ARRAY, ApConstants.kS_FARMER_ARRAY);
        AppSettings.getInstance().setValue(CaUpcomingEventDetailActivity.this, ApConstants.kS_FACILITATOR_ARRAY, ApConstants.kS_FACILITATOR_ARRAY);
        AppSettings.getInstance().setValue(CaUpcomingEventDetailActivity.this, ApConstants.kS_CA_RES_PERSON_ARRAY, ApConstants.kS_CA_RES_PERSON_ARRAY);
        AppSettings.getInstance().setValue(CaUpcomingEventDetailActivity.this, ApConstants.kS_OTH_PARTICIPANTS_ARRAY, ApConstants.kS_OTH_PARTICIPANTS_ARRAY);
        AppSettings.getInstance().setValue(CaUpcomingEventDetailActivity.this, ApConstants.kS_RES_PERSON, ApConstants.kS_RES_PERSON);
        AppSettings.getInstance().setValue(CaUpcomingEventDetailActivity.this, ApConstants.kS_COORDINATOR, ApConstants.kS_COORDINATOR);
        AppSettings.getInstance().setValue(CaUpcomingEventDetailActivity.this, ApConstants.kS_CO_COORDINATOR, ApConstants.kS_CO_COORDINATOR);
        AppSettings.getInstance().setValue(CaUpcomingEventDetailActivity.this, ApConstants.kS_EVENT_S_DATE, ApConstants.kS_EVENT_S_DATE);
        AppSettings.getInstance().setValue(CaUpcomingEventDetailActivity.this, ApConstants.kS_EVENT_E_DATE, ApConstants.kS_EVENT_E_DATE);
        AppSettings.getInstance().setValue(CaUpcomingEventDetailActivity.this, ApConstants.kS_SHG_PARTICIPANTS_ARRAY, ApConstants.kS_SHG_PARTICIPANTS_ARRAY);
        AppSettings.getInstance().setValue(CaUpcomingEventDetailActivity.this, ApConstants.kS_FPC_PARTICIPANTS_ARRAY, ApConstants.kS_FPC_PARTICIPANTS_ARRAY);
        AppSettings.getInstance().setValue(CaUpcomingEventDetailActivity.this, ApConstants.kS_farm_PARTICIPANTS_ARRAY, ApConstants.kS_farm_PARTICIPANTS_ARRAY);
        eDB.deleteAllData();
    }


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


                    AppSettings.getInstance().setValue(CaUpcomingEventDetailActivity.this, ApConstants.kS_GP_ARRAY, sledGPJSONArray.toString());
                    sledGPId = AppUtility.getInstance().componentSeparatedByCommaJSONArray(sledGPJSONArray, "gp_id");
                    // For Hiding Layout if all removed
                    if (sledGPJSONArray.length() < 1) {
                        sledGPJSONArray = null;
                        sledVCRMCTView.setVisibility(View.GONE);
                        sledGPLLayout.setVisibility(View.GONE);
                    }

                    getNumberOfParticipant();
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
                    AppSettings.getInstance().setValue(CaUpcomingEventDetailActivity.this, ApConstants.kS_FARMER_ARRAY, sledFarmerJSONArray.toString());
                    sledVillageId = AppUtility.getInstance().componentSeparatedByCommaJSONArray(sledFarmerJSONArray, "village_id");

                    // For Hiding Layout if all removed
                    if (sledFarmerJSONArray.length() < 1) {
                        sledFarmerJSONArray = null;
                        sledFarmerTView.setVisibility(View.GONE);
                        sledFarmerLLayout.setVisibility(View.GONE);
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
                    AppSettings.getInstance().setValue(CaUpcomingEventDetailActivity.this, ApConstants.kS_CO_COORDINATOR, sledCoCoordJSONArray.toString());

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
                    AppSettings.getInstance().setValue(CaUpcomingEventDetailActivity.this, ApConstants.kS_COORDINATOR, sledCordJSONArray.toString());

                    // For Hiding Layout if all removed
                    if (sledCordJSONArray.length() < 1) {
                        sledCordJSONArray = null;
                        sledCordId = null;
                        coordinatorTextView.setText("");
                        sledCordLLayout.setVisibility(View.GONE);
                    }

                }

            } else if (i == 5) {

                if (sledPOMemberJSONArray != null) {

                    JSONObject jsonObject = (JSONObject) o;
                    String id = jsonObject.getString("id");

                    for (int j = 0; j < sledPOMemberJSONArray.length(); j++) {
                        JSONObject ffsJSONObject = sledPOMemberJSONArray.getJSONObject(j);
                        String itemId = ffsJSONObject.getString("id");

                        if (itemId.equalsIgnoreCase(id)) {
                            sledPOMemberJSONArray.remove(j);
                            eDB.updatePOMemSelectionDetail(itemId, "0");
                        }
                    }
                    AppSettings.getInstance().setValue(CaUpcomingEventDetailActivity.this, ApConstants.kS_FACILITATOR_ARRAY, sledPOMemberJSONArray.toString());

                    // For Hiding Layout if all removed
                    if (sledPOMemberJSONArray.length() < 1) {
                        sledPOMemberJSONArray = null;
                        sledFacilitatorId = null;
                        sledPOMemberTView.setVisibility(View.GONE);
                        sledPOMemberLLayout.setVisibility(View.GONE);
                    }

                    getNumberOfParticipant();
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

                    AppSettings.getInstance().setValue(CaUpcomingEventDetailActivity.this, ApConstants.kS_OTH_PARTICIPANTS_ARRAY, sledOthParticipantJSONArray.toString());

                    // For Hiding Layout if all removed
                    if (sledOthParticipantJSONArray.length() < 1) {
                        sledOthParticipantJSONArray = null;
                        sledOthParticipantId = null;
                        sledOthParticipantTView.setVisibility(View.GONE);
                        sledOthParticipantLLayout.setVisibility(View.GONE);
                    }
                    getNumberOfParticipant();
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    @Override
    public void didSelectAlertViewListItem(TextView textView, String s) {

    }


    @Override
    public void onDateSelected(TextView textView, int i, int i1, int i2) {


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

                String villageId = "";
                for (int i = 0; i < sledFarmerArray.length(); i++) {

                    JSONObject jsonObject = new JSONObject();

                    JSONObject gpJSon = sledFarmerArray.getJSONObject(i);
                    String vId = gpJSon.getString("village_id");

                    if (!vId.equalsIgnoreCase(villageId)) {
                        JSONArray fJSONArray = eDB.getSledFarmerListByVillage(vId);
                        String farIDString = AppUtility.getInstance().componentSeparatedByCommaJSONArray(fJSONArray, "farmer_id");
                        jsonObject.put("village_id", vId);
                        jsonObject.put("farmer_id", farIDString);
                        jsonArray.put(jsonObject);
                        villageId = vId;
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonArray;
    }


    @Override
    public void onResponse(JSONObject jsonObject, int i) {

        try {

            if (jsonObject != null) {

                // get Event detail Response
                if (i == 5) {
                    ResponseModel responseModel = new ResponseModel(jsonObject);
                    if (responseModel.isStatus()) {
                        JSONArray eventJSONArray = responseModel.getData();
                        setScheduledEventData(eventJSONArray);

                    } else {
                        UIToastMessage.show(this, responseModel.getMsg());
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
                                            eDB.insertGPWithMemDetail(talukaId, gpId, gpName, gpCode, gpIsSelected, memId, memName, memFName, memMName, memLName, memMobile, memMobile2,
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
                            startProgressDialog(this);


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

                            if (progress.isShowing()) {
                                progress.dismiss();
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
