package in.gov.pocra.training.activity.common.participantsList;

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

import in.co.appinventor.services_api.app_util.AppUtility;
import in.co.appinventor.services_api.listener.OnMultiRecyclerItemClickListener;
import in.co.appinventor.services_api.settings.AppSettings;
import in.gov.pocra.training.R;
import in.gov.pocra.training.event_db.EventDataBase;
import in.gov.pocra.training.util.ApConstants;

public class ParticipantsListActivity extends AppCompatActivity implements OnMultiRecyclerItemClickListener {

    EventDataBase eDB;
    private RecyclerView pRecyclerView;
    private ImageView homeBack;
    private JSONArray dataArray = new JSONArray();
    private AppCompatTextView actionTitleTextView;
    private String sledMemType;
    String actionType;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_participents);

        /*** For actionbar title in center ***/
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.custom_actionbar_layout);
        actionTitleTextView = (AppCompatTextView) getSupportActionBar().getCustomView().findViewById(R.id.actionTitleTextView);
        homeBack = (ImageView) getSupportActionBar().getCustomView().findViewById(R.id.backImageView);
        homeBack.setVisibility(View.VISIBLE);

        eDB = new EventDataBase(this);
          actionType = getIntent().getStringExtra("actionType");
        initialization();
        defaultConfiguration();
    }

    private void initialization() {

        pRecyclerView = (RecyclerView)findViewById(R.id.pRecyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        pRecyclerView.setLayoutManager(linearLayoutManager);

    }

    private void defaultConfiguration() {
        homeBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        sledMemType = getIntent().getStringExtra("sledMemType");



        if (sledMemType.equalsIgnoreCase("VCRMC(GP)")){
            String gpId = getIntent().getStringExtra("gpId").trim();
            String gpName = getIntent().getStringExtra("gpName");
            actionTitleTextView.setText(gpName);

            JSONArray gpMemArrayArray = eDB.getGpMemberListByGpId(gpId);
            for (int i = 0; i<gpMemArrayArray.length();i++){
                JSONObject dataJson = new JSONObject();
                try {
                    JSONObject memJson = gpMemArrayArray.getJSONObject(i);
                    String memSelection =memJson.getString("mem_is_selected");
                    if (memSelection.equalsIgnoreCase("1")){

                        dataJson.put("first_name",memJson.getString("mem_first_name"));
                        dataJson.put("middle_name",memJson.getString("mem_middle_name"));
                        dataJson.put("last_name",memJson.getString("mem_last_name"));
                        dataJson.put("designation",memJson.getString("mem_designation_name"));
                        dataJson.put("mobile",memJson.getString("mem_mobile"));
                        dataJson.put("gp_id",memJson.getString("gp_id"));
                        dataJson.put("mem_id",memJson.getString("mem_id"));
                        dataJson.put("mem_is_selected",memJson.getString("mem_is_selected"));

                        dataArray.put(dataJson);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            if (dataArray.length()>0){
                AdaptorParticipantsList adaptorParticipantsList = new AdaptorParticipantsList(this,dataArray,sledMemType,actionType,this);
                pRecyclerView.setAdapter(adaptorParticipantsList);
            }

        } else if (sledMemType.equalsIgnoreCase("Farmer")){
            String actId = getIntent().getStringExtra("actId").trim();
            String actName = getIntent().getStringExtra("actName");
            actionTitleTextView.setText(actName);
            JSONArray farmerArrayArray = eDB.getSledFarmerListByActivity(actId);
            for (int i = 0; i<farmerArrayArray.length();i++){
                JSONObject dataJson = new JSONObject();
                try {
                    JSONObject memJson = farmerArrayArray.getJSONObject(i);
                    String memSelection =memJson.getString("is_selected");
                    if (memSelection.equalsIgnoreCase("1")){

                        dataJson.put("farmer_id",memJson.getString("id"));
                        dataJson.put("first_name",memJson.getString("name"));
                        dataJson.put("middle_name","");
                        dataJson.put("last_name","");
                        dataJson.put("mobile",memJson.getString("mobile"));
                        dataJson.put("act_id",memJson.getString("vil_act_id"));
                        dataJson.put("gender",memJson.getString("gender"));
                        dataJson.put("designation",memJson.getString("designation"));
                        dataArray.put(dataJson);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            if (dataArray.length()>0){
                AdaptorParticipantsList adaptorParticipantsList = new AdaptorParticipantsList(this,dataArray,sledMemType,actionType,this);
                pRecyclerView.setAdapter(adaptorParticipantsList);
            }

        }else if (sledMemType.equalsIgnoreCase("Resource Person")){
            actionTitleTextView.setText(sledMemType);
            String data = getIntent().getStringExtra("sledMemArray");
            try {
                JSONArray resourceDataArray = new JSONArray(data);
                for (int i = 0; i<resourceDataArray.length();i++){
                    JSONObject dataJson = new JSONObject();
                    try {
                        JSONObject memJson = resourceDataArray.getJSONObject(i);
                        String memSelection = memJson.getString("is_selected");
                        if (memSelection.equalsIgnoreCase("1")){

                            dataJson.put("first_name",memJson.getString("person_name"));
                            dataJson.put("middle_name","");
                            dataJson.put("last_name","");
                            dataJson.put("mobile",memJson.getString("mobile"));
                            dataJson.put("gender",memJson.getString("gender"));
                            dataJson.put("designation","Resource Person");
                            dataArray.put(dataJson);

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                if (dataArray.length()>0){
                    AdaptorParticipantsList adaptorParticipantsList = new AdaptorParticipantsList(this,dataArray,sledMemType,actionType,this);
                    pRecyclerView.setAdapter(adaptorParticipantsList);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }else {
            actionTitleTextView.setText(sledMemType);
            String data = getIntent().getStringExtra("sledMemArray");
            try {
                dataArray = new JSONArray(data);
                if (dataArray.length()>0){
                    AdaptorParticipantsList adaptorParticipantsList = new AdaptorParticipantsList(this,dataArray,sledMemType,actionType,this);
                    pRecyclerView.setAdapter(adaptorParticipantsList);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


    }

    @Override
    public void onMultiRecyclerViewItemClick(int i, Object o) {

        try {

            if (i == 1) {

                if (dataArray != null) {
                    if (sledMemType.equalsIgnoreCase("VCRMC(GP)")){

                       /* JSONObject jsonObject = (JSONObject) o;
                        String id = jsonObject.getString("gp_id");

                        for (int j = 0; j < dataArray.length(); j++) {
                            JSONObject GPJSONObject = dataArray.getJSONObject(j);
                            String itemId = GPJSONObject.getString("gp_id");

                            if (itemId.equalsIgnoreCase(id)) {
                                dataArray.remove(j);

                                eDB.updateGpIsSelected(itemId, "0");
                                JSONArray memBerByGP = eDB.getSledGpMemIdListByGpId(itemId);
                                String sledMemId = AppUtility.getInstance().componentSeparatedByCommaJSONArray(memBerByGP, "mem_id");
                                String[] s = sledMemId.split(",");
                                for (String sGPMemId : s) {
                                    eDB.updateGpMemIsSelected(sGPMemId, "0");
                                }
                            }
                        }


                        // For Hiding Layout if all removed
                        if (dataArray.length() < 1) {
                            dataArray = null;
                            finish();
                        }
*/   // comment by shashank because by above code if i remove one vcrmc member then all members of that particular GP was removed by gp_id....

                        JSONObject jsonObject = (JSONObject) o;
                        String id = jsonObject.getString("mem_id");

                        for (int j = 0; j < dataArray.length(); j++) {
                            JSONObject GPJSONObject = dataArray.getJSONObject(j);
                            String itemId = GPJSONObject.getString("mem_id");

                            if (itemId.equalsIgnoreCase(id)) {
                                dataArray.remove(j);
                                eDB.updateGpMemIsSelected(itemId, "0");

                            }
                        }


                        // For Hiding Layout if all removed
                        if (dataArray.length() < 1) {
                            dataArray = null;
                            finish();
                        }




                    }else if (sledMemType.equalsIgnoreCase("Farmer")){

                        JSONObject jsonObject = (JSONObject) o;
                        String farmerId = jsonObject.getString("farmer_id");

                        for (int j = 0; j < dataArray.length(); j++) {
                            JSONObject farmerJSON = dataArray.getJSONObject(j);
                            String itemId = farmerJSON.getString("farmer_id");

                            if (itemId.equalsIgnoreCase(farmerId)) {
                                dataArray.remove(j);
                                eDB.updateFarmerIsSelected(itemId, "0");
                            }
                        }


                        // For Hiding Layout if all removed
                        if (dataArray.length() < 1) {
                            dataArray = null;
                            finish();
                        }

                    }else if (sledMemType.equalsIgnoreCase("PoCRA Official")){

                        JSONObject jsonObject = (JSONObject) o;
                        String id = jsonObject.getString("id");

                        for (int j = 0; j < dataArray.length(); j++) {
                            JSONObject ffsJSONObject = dataArray.getJSONObject(j);
                            String itemId = ffsJSONObject.getString("id");

                            if (itemId.equalsIgnoreCase(id)) {
                                dataArray.remove(j);
                                eDB.updatePOMemSelectionDetail(itemId, "0");
                            }
                        }
                        AppSettings.getInstance().setValue(ParticipantsListActivity.this, ApConstants.kS_FACILITATOR_ARRAY, dataArray.toString());

                        // For Hiding Layout if all removed
                        if (dataArray.length() < 1) {
                            dataArray = null;
                            finish();
                        }

                    }else if (sledMemType.equalsIgnoreCase("SHG")){

                        JSONObject jsonObject = (JSONObject) o;
                        String id = jsonObject.getString("id");

                        for (int j = 0; j < dataArray.length(); j++) {
                            JSONObject ffsJSONObject = dataArray.getJSONObject(j);
                            String itemId = ffsJSONObject.getString("id");

                            if (itemId.equalsIgnoreCase(id)) {
                                dataArray.remove(j);
                                eDB.updateShgIsSelected(itemId, "0");
                            }
                        }
                        AppSettings.getInstance().setValue(ParticipantsListActivity.this, ApConstants.kS_SHG_PARTICIPANTS_ARRAY, dataArray.toString());

                        // For Hiding Layout if all removed
                        if (dataArray.length() < 1) {
                            dataArray = null;
                            finish();
                        }

                    }else if (sledMemType.equalsIgnoreCase("FPC")){

                        JSONObject jsonObject = (JSONObject) o;
                        String id = jsonObject.getString("id");

                        for (int j = 0; j < dataArray.length(); j++) {
                            JSONObject ffsJSONObject = dataArray.getJSONObject(j);
                            String itemId = ffsJSONObject.getString("id");

                            if (itemId.equalsIgnoreCase(id)) {
                                dataArray.remove(j);
                                eDB.updateFpcIsSelected(itemId, "0");
                            }
                        }
                        AppSettings.getInstance().setValue(ParticipantsListActivity.this, ApConstants.kS_FPC_PARTICIPANTS_ARRAY, dataArray.toString());

                        // For Hiding Layout if all removed
                        if (dataArray.length() < 1) {
                            dataArray = null;
                            finish();
                        }

                    }else if (sledMemType.equalsIgnoreCase("FG")){

                        JSONObject jsonObject = (JSONObject) o;
                        String id = jsonObject.getString("id");

                        for (int j = 0; j < dataArray.length(); j++) {
                            JSONObject ffsJSONObject = dataArray.getJSONObject(j);
                            String itemId = ffsJSONObject.getString("id");

                            if (itemId.equalsIgnoreCase(id)) {
                                dataArray.remove(j);
                                eDB.updateFGroupIsSelected(itemId, "0");
                            }
                        }
                        AppSettings.getInstance().setValue(ParticipantsListActivity.this, ApConstants.kS_farm_PARTICIPANTS_ARRAY, dataArray.toString());

                        // For Hiding Layout if all removed
                        if (dataArray.length() < 1) {
                            dataArray = null;
                            finish();
                        }

                    }else if (sledMemType.equalsIgnoreCase("Other Participants")){
                        JSONObject jsonObject = (JSONObject) o;
                        String id = jsonObject.getString("id");

                        for (int j = 0; j < dataArray.length(); j++) {
                            JSONObject othPartiJSONObject = dataArray.getJSONObject(j);
                            String itemId = othPartiJSONObject.getString("id");

                            if (itemId.equalsIgnoreCase(id)) {
                                dataArray.remove(j);
                            }
                        }
                        AppSettings.getInstance().setValue(ParticipantsListActivity.this, ApConstants.kS_OTH_PARTICIPANTS_ARRAY, dataArray.toString());

                        // For Hiding Layout if all removed
                        if (dataArray.length() < 1) {
                            dataArray = null;
                            finish();
                        }
                    }else if (sledMemType.equalsIgnoreCase("Resource Person")){
                        JSONObject jsonObject = (JSONObject) o;
                        String mobile = jsonObject.getString("mobile");

                        for (int j = 0; j < dataArray.length(); j++) {
                            JSONObject resourceJSONObject = dataArray.getJSONObject(j);
                            String mo = resourceJSONObject.getString("mobile");

                            if (mo.equalsIgnoreCase(mobile)) {
                                dataArray.remove(j);
                            }
                        }
                        AppSettings.getInstance().setValue(ParticipantsListActivity.this, ApConstants.kS_CA_RES_PERSON_ARRAY, dataArray.toString());

                        // For Hiding Layout if all removed
                        if (dataArray.length() < 1) {
                            dataArray = null;
                            finish();
                        }
                    }


                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


}
