package in.gov.pocra.training.activity.ps_hrd.ps_report;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import in.co.appinventor.services_api.api.AppinventorApi;
import in.co.appinventor.services_api.app_util.AppUtility;
import in.co.appinventor.services_api.debug.DebugLog;
import in.co.appinventor.services_api.listener.ApiCallbackCode;
import in.co.appinventor.services_api.settings.AppSettings;
import in.co.appinventor.services_api.widget.UIToastMessage;
import in.gov.pocra.training.R;
import in.gov.pocra.training.activity.pmu.pmu_report.PmuPDFReportActivity;
import in.gov.pocra.training.model.offline.OffPsReportListModel;
import in.gov.pocra.training.model.online.ResponseModel;
import in.gov.pocra.training.util.ApConstants;
import in.gov.pocra.training.web_services.APIRequest;
import in.gov.pocra.training.web_services.APIServices;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Retrofit;


public class PsReportListActivity extends AppCompatActivity implements View.OnClickListener, ApiCallbackCode {
    private RecyclerView ListRecyclerView;
    private ArrayList<OffPsReportListModel> list;
    private JSONArray closed_event_list,schedule_details_list;
    private String closed_event_list_str;
    private JSONObject closed_json_object,schedule_details_json_object;

    private String mSchId,date,schedule_deails;
    private JSONArray imgListArray;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ps_report_list);

        ListRecyclerView = (RecyclerView)findViewById(R.id.PsReportList);

        list = new ArrayList<>();

        fillList();

        Intent intent = getIntent();
        closed_event_list_str = intent.getStringExtra("closed_event_list");

        mSchId = AppSettings.getInstance().getValue(PsReportListActivity.this, ApConstants.Closed_event_schedule_details,"");

        try {
            closed_event_list = new JSONArray(closed_event_list_str);
            int position = intent.getIntExtra("position",0);
            closed_json_object = closed_event_list.getJSONObject(position);
            date = closed_json_object.getString("date");

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void fillList() {
        list.add(new OffPsReportListModel(getResources().getString(R.string.activity_list_1)));
        list.add(new OffPsReportListModel(getResources().getString(R.string.activity_list_1)));
        list.add(new OffPsReportListModel(getResources().getString(R.string.activity_list_3)));

        AdaptorPsReportList adaptorPsReportList = new AdaptorPsReportList(PsReportListActivity.this,list,new AdaptorPsReportList.RecyclerViewClickListener()
        {

            @Override
            public void onClick(View view, int position) {
                if (position == 0) {
                    Intent intent = new Intent(PsReportListActivity.this, EventReportActivity.class);
                    intent.putExtra("schId",mSchId);
                    intent.putExtra("closerDate",date);
                    intent.putExtra("type","day");
                    startActivity(intent);
                }
              /*  if (position == 1) {
                    startActivity(new Intent(Day2Sub2_2Activity.this, Day2Sub2_2Activity2.class));
                }*/
                if (position == 1) {

                    try{
                        String sessionData = AppSettings.getInstance().getValue(PsReportListActivity.this, ApConstants.Session_data_list,"");
                        // Intent intent = new Intent(PmuReportListActivity.this, SessionActivity.class);
                        Intent intent = new Intent(PsReportListActivity.this, PsReportSessionListActivity.class);
                        intent.putExtra("eventDate",date);
                        intent.putExtra("schId",mSchId);
                        startActivity(intent);


                    }catch (Exception e){

                    }
            }
                if (position == 2) {
//                    UIToastMessage.show(PmuReportListActivity.this, "coming soon");
                    getImageList();
                }
            }
        });
        ListRecyclerView.setLayoutManager(new LinearLayoutManager(PsReportListActivity.this,LinearLayoutManager.VERTICAL,false));
        ListRecyclerView.setAdapter(adaptorPsReportList);
        adaptorPsReportList.notifyDataSetChanged();
    }
    private void getImageList() {

        try {

            // To get Detail
            JSONObject param = new JSONObject();
            param.put("schedule_event_id", mSchId);
            param.put("attendance_date", date);
            param.put("api_key", ApConstants.kAUTHORITY_KEY);

            RequestBody requestBody = AppUtility.getInstance().getRequestBody(param.toString());
            AppinventorApi api = new AppinventorApi(this, APIServices.BASE_URL, "", ApConstants.kMSG, true);
            Retrofit retrofit = api.getRetrofitInstance();
            APIRequest apiRequest = retrofit.create(APIRequest.class);
            Call<JsonObject> responseCall = apiRequest.getAttendImageListOfDayRequest(requestBody);

            DebugLog.getInstance().d("attend_image_list_param=" + responseCall.request().toString());
            DebugLog.getInstance().d("attend_image_list_param=" + AppUtility.getInstance().bodyToString(responseCall.request()));

            api.postRequest(responseCall, this, 1);


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onClick(View view) {

    }


    @Override
    public void onResponse(JSONObject jsonObject, int i) {
        try {

            if (jsonObject != null) {

                if (i == 1) {
                    ResponseModel responseModel = new ResponseModel(jsonObject);

                    if (responseModel.isStatus()) {
                        String fUrl = jsonObject.getString("file_url");
                        Log.d("ImageUpload-ps","file_url=="+fUrl);
                        imgListArray = responseModel.getData();

                        if (imgListArray.length() > 0) {

                            for (int img = 0; img < imgListArray.length(); img++) {

                                JSONObject imgJSON = imgListArray.getJSONObject(img);
                                String imgType = imgJSON.getString("img_attend_type");
                                String imgName = imgJSON.getString("image_name");
                                String imgUrl = fUrl + imgName;

                                switch (imgType) {
                                    case "7":
                                        if (!imgUrl.isEmpty()) {
                                            String imgId7 = imgJSON.getString("img_id");
                                            String image_name = imgJSON.getString("image_name");
                                            String filpath = fUrl+image_name;
                                            Log.e("pmreport","filepath"+filpath);
                                            Intent intent = new Intent(PsReportListActivity.this, PmuPDFReportActivity.class);
                                            intent.putExtra("pdfurl",filpath);
                                            startActivity(intent);

//                                            UIToastMessage.show(PmuReportListActivity.this, "coming soon" + fUrl + "" + image_name);

                                       /*     if (responseModel.isStatus()) {
                                                UIToastMessage.show(this, responseModel.getMsg());
                                            } else {
                                                UIToastMessage.show(this, responseModel.getMsg());
                                            }*/
                                            /*Picasso.get()
                                                    .load(Uri.parse(imgUrl))
                                                    .into(sixImgIView);*/
                                        }
                                }


                            }

                        }


                        /*if (imgListArray.length() > 0) {
                            AdaptorAttendImage adaptorAttendImage = new AdaptorAttendImage(this, imgListArray, this, fUrl);
                            imgRView.setAdapter(adaptorAttendImage);
                        }*/
                    } else {
                        // uploadImageAction();
                        UIToastMessage.show(this, responseModel.getMsg());
                    }
                }

            }
        } catch (Exception e) {

        }

    }

    @Override
    public void onFailure(Object o, Throwable throwable, int i) {

    }
}
