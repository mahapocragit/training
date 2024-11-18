package in.gov.pocra.training.activity.common.session_detail;

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

import in.co.appinventor.services_api.listener.OnMultiRecyclerItemClickListener;
import in.co.appinventor.services_api.settings.AppSettings;
import in.gov.pocra.training.R;
import in.gov.pocra.training.util.ApConstants;

public class SessionDetailActivity extends AppCompatActivity implements OnMultiRecyclerItemClickListener {

    private ImageView homeBack;
    private String roleId;
    private String userID;

    private RecyclerView sessionRView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session);


        /* ** For actionbar title in center ***/
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.attendance_actionbar_layout);
        AppCompatTextView actionTitleTextView = (AppCompatTextView) getSupportActionBar().getCustomView().findViewById(R.id.actionTitleTextView);
        homeBack = (ImageView) getSupportActionBar().getCustomView().findViewById(R.id.backImageView);
        // addSessionIView = (ImageView) getSupportActionBar().getCustomView().findViewById(R.id.addPersonImageView);
        homeBack.setVisibility(View.VISIBLE);
        actionTitleTextView.setText(getResources().getString(R.string.title_event_session));

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

        sessionRView = (RecyclerView)findViewById(R.id.sessionRView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false);
        sessionRView.setLayoutManager(linearLayoutManager);

    }


    @Override
    protected void onResume() {
        super.onResume();
        String sessionData = getIntent().getStringExtra("sessionData");

        if (!sessionData.equalsIgnoreCase("")){
            try {
                JSONArray sessionArray =  new JSONArray(sessionData);
                setSessionDetail(sessionArray);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }



    private void setSessionDetail(JSONArray sessionArray) {

        if (sessionArray.length()>0){
            AdaptorSessionDetailList adaptorSessionDetailList = new AdaptorSessionDetailList(this,sessionArray,this);
            sessionRView.setAdapter(adaptorSessionDetailList);
        }else {
            AdaptorSessionDetailList adaptorSessionDetailList = new AdaptorSessionDetailList(this,new JSONArray(),this);
            sessionRView.setAdapter(adaptorSessionDetailList);
        }
    }


    private void defaultConfiguration() {


    }


    @Override
    public void onMultiRecyclerViewItemClick(int i, Object o) {


    }

}
