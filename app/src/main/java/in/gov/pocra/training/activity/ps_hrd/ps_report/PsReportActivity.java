package in.gov.pocra.training.activity.ps_hrd.ps_report;

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

public class PsReportActivity extends AppCompatActivity {

    private ImageView homeBack;

    private LinearLayout attendReportLLayout;
    private LinearLayout finalReportLLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ps_report);

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



    private void initialization() {
        // For Action Bar
        homeBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        attendReportLLayout = (LinearLayout)findViewById(R.id.attendReportLLayout);
        finalReportLLayout = (LinearLayout)findViewById(R.id.finalReportLLayout);
        finalReportLLayout.setVisibility(View.GONE);

    }

    private void defaultConfiguration() {
        final String level = getIntent().getStringExtra("level");
        final String eventOpt = getIntent().getStringExtra("eventOpt");
        final String distId = getIntent().getStringExtra("distId");
        final String subDivId = getIntent().getStringExtra("subDivId");

        attendReportLLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PsReportActivity.this,PsClosedEventListActivity.class);
                intent.putExtra("level",level);
                intent.putExtra("eventOpt",eventOpt);
                intent.putExtra("distId",distId);
                intent.putExtra("subDivId",subDivId);
                startActivity(intent);
            }
        });

        finalReportLLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIToastMessage.show(PsReportActivity.this,"Coming Soon");
            }
        });
    }

}
