package in.gov.pocra.training.activity.ps_hrd.dashboard;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.google.gson.JsonObject;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import in.co.appinventor.services_api.api.AppinventorApi;
import in.co.appinventor.services_api.app_util.AppUtility;
import in.co.appinventor.services_api.debug.DebugLog;
import in.co.appinventor.services_api.listener.ApiCallbackCode;
import in.co.appinventor.services_api.listener.OnMultiRecyclerItemClickListener;
import in.co.appinventor.services_api.settings.AppSettings;
import in.co.appinventor.services_api.widget.UIToastMessage;
import in.gov.pocra.training.R;
import in.gov.pocra.training.activity.ps_hrd.ps_upcoming_event.AdaptorPsComingEvent;
import in.gov.pocra.training.model.online.ResponseModel;
import in.gov.pocra.training.util.ApConstants;
import in.gov.pocra.training.util.ApUtil;
import in.gov.pocra.training.util.cal_decorators.EventDecorator;
import in.gov.pocra.training.util.cal_decorators.OneDayDecorator;
import in.gov.pocra.training.web_services.APIRequest;
import in.gov.pocra.training.web_services.APIServices;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Retrofit;


public class PsEventCaleActivity extends AppCompatActivity implements OnDateSelectedListener, ApiCallbackCode, OnMultiRecyclerItemClickListener {
    //


    private String roleId;
    private String userID;
    private String districtId;

    private ImageView homeBack;
    private MaterialCalendarView eCalendarView;
    private String selectedDate = "";
    private static final java.text.DateFormat FORMATTER = SimpleDateFormat.getDateInstance();
    private SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy-MM-dd");

    private final OneDayDecorator oneDayDecorator = new OneDayDecorator();

    private RecyclerView eCalenderRView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ps_event_cale);

        /** For actionbar title in center */
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.attendance_actionbar_layout);
        AppCompatTextView actionTitleTextView = (AppCompatTextView) getSupportActionBar().getCustomView().findViewById(R.id.actionTitleTextView);
        homeBack = (ImageView) getSupportActionBar().getCustomView().findViewById(R.id.backImageView);
        // addPersonImageView = (ImageView) getSupportActionBar().getCustomView().findViewById(R.id.addPersonImageView);
        homeBack.setVisibility(View.VISIBLE);
        actionTitleTextView.setText(getResources().getString(R.string.title_event_calender));

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


        eCalendarView = (MaterialCalendarView) findViewById(R.id.eCalendarView);
        eCalendarView.setOnDateChangedListener(this);
        eCalendarView.addDecorator(oneDayDecorator);

        CalendarDay today = CalendarDay.today();
        Date sDate = today.getDate();
        selectedDate = dateFormater.format(sDate);

        int monNo = today.getMonth();
        eCalendarView.setCurrentDate(today);
        eCalendarView.setSelectedDate(today);
        eCalendarView.setOnDateChangedListener(this);

        eCalenderRView = (RecyclerView) findViewById(R.id.eCalenderRView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        eCalenderRView.setLayoutManager(linearLayoutManager);

    }

    private void defaultConfiguration() {


    }

    @Override
    protected void onResume() {
        super.onResume();
        String comingEventTypeId = getIntent().getStringExtra("comingEventType");
        if (comingEventTypeId.equalsIgnoreCase("1")) {
            getEventsDates();
        } else {
            getAllEventsDatesByDistrictId();
        }
        String cDate = ApUtil.getDateByTimeStamp(ApUtil.getCurrentTimeStamp());
        getEventsAgainstDate(cDate);
    }


    private void setDateToCalender(JSONArray dateArray) {

        // JSONArray dateArray = ApUtil.getDateBetweenTwoDate("05-04-2019","10-04-2019");
        ArrayList<CalendarDay> dates = new ArrayList<>();

        for (int d = 0; d < dateArray.length(); d++) {

            try {
                //JSONObject dateJSON = dateArray.getJSONObject(d);
                String date = dateArray.getString(d);
                DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
                Date eDate = (Date) formatter.parse(date);
                CalendarDay calDate = CalendarDay.from(eDate);
                dates.add(calDate);

                // eCalendarView.setSelectedDate(calDate);
                // eCalendarView.setDateSelected(eDate,true);

            } catch (ParseException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        eCalendarView.addDecorator(new EventDecorator(this, R.color.bg_red, dates));
        // eCalendarView.addDecorator(new MySelectorDecorator(this));  // To show customized selector

    }

    private void setDateToCalenderFromresponse(JSONArray dateArray) {

//         JSONArray dateArray = ApUtil.getDateBetweenTwoDate("05-04-2019","10-04-2019");
        ArrayList<CalendarDay> dates = new ArrayList<>();

        for (int d = 0; d < dateArray.length(); d++) {

            try {
                JSONObject dataJson = dateArray.getJSONObject(d);
                // Schedule Start Date
                String sTime = dataJson.getString("start_date");
                String stDate = ApUtil.getDateByTimeStamp(sTime);
                Date startDate = new SimpleDateFormat("dd-MM-yyyy").parse(stDate);

                // Schedule End Date
                String eTime = dataJson.getString("end_date");
                String etDate = ApUtil.getDateByTimeStamp(eTime);
                Date endDate = new SimpleDateFormat("dd-MM-yyyy").parse(etDate);
                //JSONObject dateJSON = dateArray.getJSONObject(d);
                List<Date> dates1 = new ArrayList<Date>();

                ArrayList<Date> dateArrayList = new ArrayList<>();
                long interval = 24 * 1000 * 60 * 60; // 1 hour in millis

                long endTime = endDate.getTime(); // create your endTime here, possibly using Calendar or Date
                long curTime = startDate.getTime();

                while (curTime <= endTime) {
                    dates1.add(new Date(curTime));
                    curTime += interval;
                }
                DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");

                int dayCount = 0;
                for (int i = 0; i < dates1.size(); i++) {
                    JSONObject dateJson = new JSONObject();
                    Date lDate = (Date) dates1.get(i);
                    String dt = formatter.format(lDate);
                    String day = "Day " + ++dayCount;
                /*"day": "Days 1",
                        "date": "12-03-2019"*/
                    dateJson.put("day", day);
                    dateJson.put("date", dt);
                    dateArrayList.add(lDate);

                }
                for (int i = 0; i < dateArrayList.size(); i++) {
                    Date date = dateArrayList.get(i);
                    /*DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
                    Date eDate = (Date) formatter.parse(date);*/
                    CalendarDay calDate = CalendarDay.from(date);
                    dates.add(calDate);
                }


            } catch (ParseException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        eCalendarView.addDecorator(new EventDecorator(this, R.color.bg_blue, dates));
        // eCalendarView.addDecorator(new MySelectorDecorator(this));  // To show customized selector

    }

    private void getEventsDates() {

        try {
            JSONObject jsonObject = new JSONObject();

            jsonObject.put("user_id", userID);
            jsonObject.put("role_id", roleId);
            jsonObject.put("api_key", ApConstants.kAUTHORITY_KEY);

            RequestBody requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString());
            AppinventorApi api = new AppinventorApi(this, APIServices.BASE_URL, "", ApConstants.kMSG, true);
            Retrofit retrofit = api.getRetrofitInstance();
            APIRequest apiRequest = retrofit.create(APIRequest.class);
            Call<JsonObject> responseCall = apiRequest.psEventDateListRequest(requestBody);

            DebugLog.getInstance().d("event_dates_param=" + responseCall.request().toString());
            DebugLog.getInstance().d("event_dates_param=" + AppUtility.getInstance().bodyToString(responseCall.request()));
            api.postRequest(responseCall, this, 1);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    private void getAllEventsDatesByDistrictId() {

        try {
            JSONObject jsonObject = new JSONObject();

            jsonObject.put("user_id", userID);
            jsonObject.put("role_id", roleId);
            jsonObject.put("district_id", districtId);
            jsonObject.put("api_key", ApConstants.kAUTHORITY_KEY);

            RequestBody requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString());
            AppinventorApi api = new AppinventorApi(this, APIServices.BASE_URL, "", ApConstants.kMSG, true);
            Retrofit retrofit = api.getRetrofitInstance();
            APIRequest apiRequest = retrofit.create(APIRequest.class);
            Call<JsonObject> responseCall = apiRequest.psEventDateListByDistIdRequest(requestBody);

            DebugLog.getInstance().d("event_dates_param=" + responseCall.request().toString());
            DebugLog.getInstance().d("event_dates_param=" + AppUtility.getInstance().bodyToString(responseCall.request()));
            api.postRequest(responseCall, this, 1);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    @Override
    public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
        Date selectedDate = date.getDate();
        String sDate = ApUtil.getFormatedDateByDate(selectedDate);    // Date format is dd-mm-yyyy
        getEventsAgainstDate(sDate);
    }

    private void getEventsAgainstDate(String sDate) {

        try {
            JSONObject jsonObject = new JSONObject();

            jsonObject.put("user_id", userID);
            jsonObject.put("role_id", roleId);
            jsonObject.put("date", sDate);
            jsonObject.put("api_key", ApConstants.kAUTHORITY_KEY);

            RequestBody requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString());
            AppinventorApi api = new AppinventorApi(this, APIServices.BASE_URL, "", ApConstants.kMSG, true);
            Retrofit retrofit = api.getRetrofitInstance();
            APIRequest apiRequest = retrofit.create(APIRequest.class);
            Call<JsonObject> responseCall = apiRequest.psEventListByDateRequest(requestBody);

            DebugLog.getInstance().d("event_dates_param=" + responseCall.request().toString());
            DebugLog.getInstance().d("event_dates_param=" + AppUtility.getInstance().bodyToString(responseCall.request()));
            api.postRequest(responseCall, this, 2);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    @Override
    public void onMultiRecyclerViewItemClick(int i, Object o) {

    }


    @Override
    public void onResponse(JSONObject jsonObject, int i) {

        try {

            if (jsonObject != null) {

                // Event Closer Response
                if (i == 1) {
                    ResponseModel responseModel = new ResponseModel(jsonObject);
                    if (responseModel.isStatus()) {
                        JSONArray jsonArray = responseModel.getData();
                        if (jsonArray.length() > 0) {
                            setDateToCalender(jsonArray);
                        }

                    } else {
                        UIToastMessage.show(this, responseModel.getMsg());
                    }
                }

                if (i == 2) {
                    ResponseModel responseModel = new ResponseModel(jsonObject);
                    if (responseModel.isStatus()) {
                        JSONArray scheduleJSONArray = jsonObject.getJSONArray("data");
                        AdaptorPsComingEvent adaptorPsComingEvent = new AdaptorPsComingEvent(this, scheduleJSONArray, false, this);
                        eCalenderRView.setAdapter(adaptorPsComingEvent);
                        setDateToCalenderFromresponse(scheduleJSONArray);
                    } else {
                        AdaptorPsComingEvent adaptorPsComingEvent = new AdaptorPsComingEvent(this, null, false, this);
                        eCalenderRView.setAdapter(adaptorPsComingEvent);
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


