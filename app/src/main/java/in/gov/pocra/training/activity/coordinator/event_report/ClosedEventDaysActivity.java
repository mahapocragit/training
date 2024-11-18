package in.gov.pocra.training.activity.coordinator.event_report;

import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import in.co.appinventor.services_api.settings.AppSettings;
import in.gov.pocra.training.R;
import in.gov.pocra.training.util.ApConstants;
import in.gov.pocra.training.util.ApUtil;

public class ClosedEventDaysActivity extends AppCompatActivity {

    private ImageView homeBack;

    private String rollId;
    private String userID;
    private RecyclerView eventCDaysRView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_closed_event_days);

        /* ** For actionbar title in center ***/
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.attendance_actionbar_layout);
        AppCompatTextView actionTitleTextView = (AppCompatTextView) getSupportActionBar().getCustomView().findViewById(R.id.actionTitleTextView);
        homeBack = (ImageView) getSupportActionBar().getCustomView().findViewById(R.id.backImageView);
        homeBack.setVisibility(View.VISIBLE);

        actionTitleTextView.setText(getResources().getString(R.string.title_closed_event_days));


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
            rollId = rId;
        }

        if (!uId.equalsIgnoreCase("kUSER_ID")) {
            userID = uId;
        }

        eventCDaysRView = (RecyclerView) findViewById(R.id.eventCDaysRView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        eventCDaysRView.setLayoutManager(linearLayoutManager);

    }


    @Override
    protected void onResume() {
        super.onResume();
        String schDetail = getIntent().getStringExtra("data");


        try {
            JSONObject jsonObject = new JSONObject(schDetail);
            String schId = jsonObject.getString("schedule_id");
            String schSDate = jsonObject.getString("start_date");
            String sDate = ApUtil.getDateByTimeStamp(schSDate);

            String eEndDate =  jsonObject.getString("end_date");
            String eEDate = ApUtil.getDateByTimeStamp(eEndDate);

            AppSettings.getInstance().setValue(this, ApConstants.Closed_event_schedule_details, schId);

            JSONArray cDayArray = ApUtil.getDateBetweenTwoDate(sDate,eEDate);
            AdaptorClosedEventDays adaptorClosedEventDays = new AdaptorClosedEventDays(this, cDayArray, schId);
            eventCDaysRView.setAdapter(adaptorClosedEventDays);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void defaultConfiguration() {

    }

}
