package in.gov.pocra.training.activity.pmu.pmu_report;

import android.content.Intent;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.AppCompatTextView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import in.co.appinventor.services_api.widget.UIToastMessage;
import in.gov.pocra.training.R;
import in.gov.pocra.training.util.ApConstants;

public class PmuReportActivity extends AppCompatActivity {

    private ImageView homeBack;

    private LinearLayout attendReportLLayout;
    private LinearLayout finalReportLLayout;
    private String eventType = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pmu_report);

        /* ** For actionbar title in center ***/
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.attendance_actionbar_layout);
        AppCompatTextView actionTitleTextView = (AppCompatTextView) getSupportActionBar().getCustomView().findViewById(R.id.actionTitleTextView);
        homeBack = (ImageView) getSupportActionBar().getCustomView().findViewById(R.id.backImageView);
        // addPersonImageView = (ImageView) getSupportActionBar().getCustomView().findViewById(R.id.addPersonImageView);
        homeBack.setVisibility(View.VISIBLE);
        // addPersonImageView.setVisibility(View.INVISIBLE);
        actionTitleTextView.setText(getResources().getString(R.string.title_reports));

        initialization();
        defaultConfiguration();
    }


    @Override
    protected void onResume() {
        super.onResume();

        eventType = getIntent().getStringExtra("eventType");

    }


    private void initialization() {
        // For Action Bar
        homeBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        attendReportLLayout = (LinearLayout)findViewById(R.id.attendanceReportLLayout);
        finalReportLLayout = (LinearLayout)findViewById(R.id.finalReportLLayout);
        finalReportLLayout.setVisibility(View.GONE);
    }

    private void defaultConfiguration() {

        attendReportLLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (eventType.equalsIgnoreCase("kS_SELF_EVENT")){
                    Intent intent = new Intent(PmuReportActivity.this, PmuClosedEventListByDistIDActivity.class);
                    intent.putExtra("distId","");
                    intent.putExtra("eventType", ApConstants.kS_SELF_EVENT);
                    startActivity(intent);
                }else {

                    String center = getIntent().getStringExtra("level");
                    if (center.equalsIgnoreCase("Pmu")){
                        allPmuClosedEventList(center,"","");
                    }else if (center.equalsIgnoreCase("District")){
                        String distId = getIntent().getStringExtra("distId");
                        if (!distId.equalsIgnoreCase("")){
                            allPmuClosedEventList(center,distId,"");
                        }else {
                            UIToastMessage.show(PmuReportActivity.this,"District id not found");
                        }
                    }else if (center.equalsIgnoreCase("Subdivision")){
                        String distId = getIntent().getStringExtra("distId");
                        String subDivId = getIntent().getStringExtra("subDivId");
                        if (!subDivId.equalsIgnoreCase("")){
                            allPmuClosedEventList(center,distId,subDivId);
                        }else {
                            UIToastMessage.show(PmuReportActivity.this,"District id not found");
                        }
                        // UIToastMessage.show(PmuReportActivity.this,"Coming Soon");
                    }
                }

            }
        });

        finalReportLLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIToastMessage.show(PmuReportActivity.this,"Coming Soon");
            }
        });

    }

    private void allPmuClosedEventList(String center, String distId,String subdivisionId) {
        Intent intent = new Intent(PmuReportActivity.this, PmuClosedEventListByDistIDActivity.class);
        intent.putExtra("level",center);
        intent.putExtra("distId",distId);
        intent.putExtra("subDivId",subdivisionId);
        intent.putExtra("eventType", ApConstants.kS_ALL_EVENT);
        startActivity(intent);
    }
}
