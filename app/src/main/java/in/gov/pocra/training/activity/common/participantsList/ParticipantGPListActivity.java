package in.gov.pocra.training.activity.common.participantsList;

import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import org.json.JSONArray;
import org.json.JSONException;

import in.co.appinventor.services_api.listener.OnMultiRecyclerItemClickListener;
import in.co.appinventor.services_api.settings.AppSettings;
import in.gov.pocra.training.R;
import in.gov.pocra.training.activity.ca.add_edit_event_ca.AddEditEventCaActivity;
import in.gov.pocra.training.event_db.EventDataBase;
import in.gov.pocra.training.util.ApConstants;

public class ParticipantGPListActivity extends AppCompatActivity implements OnMultiRecyclerItemClickListener{

    private ImageView homeBack;
    private ImageView confirmImageView;
    EventDataBase eDB;

    private RecyclerView pGpRecyclerView;
   //  private JSONArray selectedGPJSONArray = new JSONArray();

    private String talukaID = "";
    private String groupType,sledMemArray1;
    private AppCompatTextView actionTitleTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_participant_gp_list);

        /* ** For actionbar title in center ***/
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.attendance_actionbar_layout);
        actionTitleTextView = (AppCompatTextView) getSupportActionBar().getCustomView().findViewById(R.id.actionTitleTextView);
        homeBack = (ImageView) getSupportActionBar().getCustomView().findViewById(R.id.backImageView);
        confirmImageView = (ImageView) getSupportActionBar().getCustomView().findViewById(R.id.addPersonImageView);
        confirmImageView.setImageDrawable(getResources().getDrawable(R.drawable.check));
        homeBack.setVisibility(View.VISIBLE);

        eDB = new EventDataBase(this);

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
        pGpRecyclerView = (RecyclerView) findViewById(R.id.pGpRecyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        pGpRecyclerView.setLayoutManager(linearLayoutManager);

    }


    @Override
    protected void onResume() {
        super.onResume();

        groupType = getIntent().getStringExtra("sledMemType");
        Log.d("tesrtrttt",groupType);
        sledMemArray1 = getIntent().getStringExtra("sledMemArray");
        if (groupType.equalsIgnoreCase("VCRMC(GP)")){
            actionTitleTextView.setText("VCRMC (GP)");

            JSONArray selectedGPJSONArray = eDB.getSelectedGpList();
            String actionType = getIntent().getStringExtra("actionType");
            AdaptorParticipantGPList adaptorParticipantGPList = new AdaptorParticipantGPList(this,selectedGPJSONArray,actionType,groupType, this);
            pGpRecyclerView.setAdapter(adaptorParticipantGPList);
        } else {
            actionTitleTextView.setText("Farmers Activity");
            JSONArray selectedActJSONArray = eDB.getSledActivityList();
            String actionType = getIntent().getStringExtra("actionType");
            AdaptorParticipantGPList adaptorParticipantGPList = new AdaptorParticipantGPList(this,selectedActJSONArray,actionType, groupType,this);
            pGpRecyclerView.setAdapter(adaptorParticipantGPList);
        }
    }

    private void defaultConfiguration() {

    }

    @Override
    public void onMultiRecyclerViewItemClick(int i, Object o) {

       /* try {

            JSONObject toSelectObj = (JSONObject) o;
            String itemId = toSelectObj.getString("id");
            String itemName = toSelectObj.getString("Name");
            if (selectedGPJSONArray.length() > 0) {

                *//*Intent intent = new Intent(ParticipantGPListActivity.this, ParticipantsListActivity.class);
                intent.putExtra("sledMemType",itemName);
                intent.putExtra("sledMemArray",sledFarmerJSONArray.toString());
                startActivity(intent);*//*
            }

        } catch (Exception e) {
            e.printStackTrace();
        }*/


    }

}
