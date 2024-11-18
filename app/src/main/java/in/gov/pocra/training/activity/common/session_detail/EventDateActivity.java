package in.gov.pocra.training.activity.common.session_detail;

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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import in.co.appinventor.services_api.listener.OnMultiRecyclerItemClickListener;
import in.co.appinventor.services_api.settings.AppSettings;
import in.co.appinventor.services_api.widget.UIToastMessage;
import in.gov.pocra.training.R;
import in.gov.pocra.training.activity.pmu.pmu_report.PmuReportListActivity;
import in.gov.pocra.training.util.ApConstants;
import in.gov.pocra.training.util.ApUtil;

public class EventDateActivity extends AppCompatActivity implements OnMultiRecyclerItemClickListener {

    private ImageView homeBack;

    private RecyclerView eventDaysRView;
    private Button attendCloserBtn;
    private String sessionDetail = null;


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

        eventDaysRView = (RecyclerView) findViewById(R.id.eventDaysRView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        eventDaysRView.setLayoutManager(linearLayoutManager);

    }


    @Override
    protected void onResume() {
        super.onResume();
        sessionDetail = getIntent().getStringExtra("sessionDetail");
        String sessionDate = getIntent().getStringExtra("sessionDateArray");

        try {
            JSONArray daysArray = new JSONArray(sessionDate);

            // JSONArray daysArray = jsonObject.getJSONArray("EventDays");
            AdaptorEventDate adaptorEventDate = new AdaptorEventDate(this, daysArray,sessionDetail,this);
            eventDaysRView.setAdapter(adaptorEventDate);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void defaultConfiguration() {

    }


    @Override
    public void onMultiRecyclerViewItemClick(int i, Object o) {

        if (o != null) {
            JSONArray jsonArray  =  new JSONArray();
            JSONObject jsonObject = (JSONObject) o;
            try {

                String date = jsonObject.getString("date");
                if (i == 1) {
                    JSONArray SessionArray = new JSONArray(sessionDetail);
                    for (int j =0; SessionArray.length()>j;j++){
                        JSONObject sessionJson = SessionArray.getJSONObject(j);
                        String startTime = sessionJson.getString("start_time");
                        String sessDate = ApUtil.getDateByTimeStamp(startTime);
                        if (sessDate.equalsIgnoreCase(date)){
                            jsonArray.put(sessionJson);
                        }

                    }

                    if (jsonArray.length()>0){
                        Intent intent = new Intent(this, PmuReportListActivity.class);
                        intent.putExtra("sessionData", jsonArray.toString());
                        //Store this array data to one variable using appsetting
                        AppSettings.getInstance().setValue(EventDateActivity.this, ApConstants.Session_data_list,jsonArray.toString());
                        startActivity(intent);
                    }else {
                        UIToastMessage.show(this, "Session not available");
                    }



                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

}
