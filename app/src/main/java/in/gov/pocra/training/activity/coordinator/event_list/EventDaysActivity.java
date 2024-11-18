package in.gov.pocra.training.activity.coordinator.event_list;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import in.co.appinventor.services_api.listener.ApiCallbackCode;
import in.co.appinventor.services_api.listener.OnMultiRecyclerItemClickListener;
import in.co.appinventor.services_api.settings.AppSettings;
import in.co.appinventor.services_api.util.Utility;
import in.co.appinventor.services_api.widget.UIToastMessage;
import in.gov.pocra.training.R;
import in.gov.pocra.training.activity.coordinator.event_closer.EventSummaryActivity;
import in.gov.pocra.training.activity.coordinator.event_session.SessionActivity;
import in.gov.pocra.training.event_db.CordOfflineDBase;
import in.gov.pocra.training.model.online.ResponseModel;
import in.gov.pocra.training.util.ApConstants;
import in.gov.pocra.training.util.ApUtil;

public class EventDaysActivity extends AppCompatActivity implements ApiCallbackCode, OnMultiRecyclerItemClickListener {

    private ImageView homeBack;
    private CordOfflineDBase cDB;

    private String loginType;
    private String roleId;
    private String userID;
    private RecyclerView eventDaysRView;
    private Button attendCloserBtn;
    private String schId;
    private String closerDate = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_days);

        /* ** For actionbar title in center ***/
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.attendance_actionbar_layout);
        AppCompatTextView actionTitleTextView = (AppCompatTextView) getSupportActionBar().getCustomView().findViewById(R.id.actionTitleTextView);
        homeBack = (ImageView) getSupportActionBar().getCustomView().findViewById(R.id.backImageView);
        // uploadImageIView = (ImageView) getSupportActionBar().getCustomView().findViewById(R.id.addPersonImageView);
        homeBack.setVisibility(View.VISIBLE);
        // uploadImageIView.setImageDrawable(getResources().getDrawable(R.drawable.ic_menu_camera));
        // uploadImageIView.setVisibility(View.VISIBLE);
        actionTitleTextView.setText(getResources().getString(R.string.title_event_days));

        cDB = new CordOfflineDBase(this);

        String logType = AppSettings.getInstance().getValue(this, ApConstants.kLOGIN_TYPE, ApConstants.kLOGIN_TYPE);
        if (!logType.equalsIgnoreCase("kLOGIN_TYPE")) {
            loginType = logType;
        }

        initialization();
        defaultConfiguration();
    }

    private void initialization() {
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

        eventDaysRView = (RecyclerView) findViewById(R.id.eventDaysRView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        eventDaysRView.setLayoutManager(linearLayoutManager);

        attendCloserBtn = (Button) findViewById(R.id.attendCloserBtn);

    }


    @Override
    protected void onResume() {
        super.onResume();
        String schDetail = getIntent().getStringExtra("data");

        try {
            JSONObject jsonObject = new JSONObject(schDetail);
            schId = jsonObject.getString("schedule_id");
            Log.d("onResume","schedule_id="+schId);
            String schSDate = jsonObject.getString("start_date");
            String sDate = ApUtil.getDateByTimeStamp(schSDate);

            String eEndDate = jsonObject.getString("end_date");
            String eEDate = ApUtil.getDateByTimeStamp(eEndDate);

            final String currentDate = ApUtil.getDateByTimeStamp(ApUtil.getCurrentTimeStamp());
            if (eEDate.equalsIgnoreCase(currentDate) || ApUtil.isDatePassed(eEDate)) {
                closerDate = eEDate;
                attendCloserBtn.setVisibility(View.VISIBLE);
            }

            JSONArray daysArray = ApUtil.getDateBetweenTwoDate(sDate, eEDate);
            // JSONArray daysArray = jsonObject.getJSONArray("EventDays");
            AdaptorEventDays adaptorEventDays = new AdaptorEventDays(this, daysArray, schId, this);
            eventDaysRView.setAdapter(adaptorEventDays);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void defaultConfiguration() {
        attendCloserBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forCloserUserPermission();
            }
        });
    }


    @Override
    public void onMultiRecyclerViewItemClick(int i, Object o) {

        if (o != null) {
            JSONObject jsonObject = (JSONObject) o;
            try {
                String date = jsonObject.getString("date");

                if (i == 1) {
                    UIToastMessage.show(this, "Sync Action for "+date);
                    JSONArray presentArray = new JSONArray();
                    JSONObject presentJSON = new JSONObject();

                    // For Other member detail by date
                    JSONArray othMemArray = cDB.getOtherMemListByEventIdDate(schId, date);
                    if (othMemArray.length()>0){
                        UIToastMessage.show(this,String.valueOf(" Other mem are "+othMemArray.toString()));
                    }

                    // For Attendance Detail by date
                    JSONArray preGPMemArray = cDB.getPresentMemByDateGroup(date,"gp");
                    JSONArray preVillageMemArray = cDB.getPresentMemByDateGroup(date,"village");
                    JSONArray preOthMemArray = cDB.getPresentMemByDateGroup(date,"other");
                    JSONArray preCordMemArray = cDB.getPresentMemByDateGroup(date,"coordinator");
                    JSONArray preTrMemArray = cDB.getPresentMemByDateGroup(date,"trainer");

                    presentJSON.put("gp",preGPMemArray);
                    presentJSON.put("village",preVillageMemArray);
                    presentJSON.put("other",preOthMemArray);
                    presentJSON.put("coordinators",preCordMemArray);
                    presentJSON.put("resource_person",preTrMemArray);

                    presentArray.put(presentJSON);
                    // long numOfMemPresent = cDB.getNoOfRecord(CordOfflineDBase.EVENT_MEM_ATTEND_TABLE);
                    if (presentArray.length()>0){
                        UIToastMessage.show(this,String.valueOf(" present members are "+presentArray.toString()));
                    }

                }else if (i == 2){

                    if (Utility.checkConnection(this)){
                        Intent intent = new Intent(this, SessionActivity.class);
                        intent.putExtra("eventDate",date);
                        intent.putExtra("schId",schId);
                        startActivity(intent);
                    }else {
                        UIToastMessage.show(this,"No internet connection");
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }


    private void forCloserUserPermission() {

        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        // alertDialogBuilder.setMessage("Are you sure want to close event attendance? \nYou can't do any update further for this event");
        alertDialogBuilder.setMessage("Are you sure you want to close this event? \nYou can't do any update further for this event");

                alertDialogBuilder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // eventCloserRequest();
                Intent intent = new Intent(EventDaysActivity.this, EventSummaryActivity.class);
                intent.putExtra("schId",schId);
                intent.putExtra("closerDate",closerDate);
                startActivity(intent);
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




    @Override
    public void onResponse(JSONObject jsonObject, int i) {
        try {

            if (jsonObject != null) {

                // Event Closer Response
                if (i == 1) {
                    ResponseModel responseModel = new ResponseModel(jsonObject);
                    if (responseModel.isStatus()) {
                        UIToastMessage.show(this, responseModel.getMsg());
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
