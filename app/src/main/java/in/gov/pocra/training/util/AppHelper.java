/*
 * Copyright (c) 2017. Appinventor Inc. All right reserved.
 * https://appinventor.co.in
 * Author Name: Vinod Vishwakarma
 * Linked In: https://www.linkedin.com/in/vvishwakarma
 * Email ID: vish.vino@gmail.com
 * Last Modified : 5/8/17 3:43 PM
 */

package in.gov.pocra.training.util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.text.format.DateFormat;

/**
 * Created by vvishwakarma on 29/03/17.
 */

public class AppHelper {

    private static AppHelper appHelper = null;

    public AppHelper() {

    }

    public static AppHelper getInstance() {
        if (appHelper == null) {
            appHelper = new AppHelper();
        }
        return appHelper;
    }



    public JSONArray getFilterOldNewArray() {
        JSONArray jsonArray = new JSONArray();
        try {
            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put("id", "0");
            jsonObject1.put("name", "NEW");

            JSONObject jsonObject2 = new JSONObject();
            jsonObject2.put("id", "1");
            jsonObject2.put("name", "OLD");

            jsonArray.put(jsonObject1);
            jsonArray.put(jsonObject2);


        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonArray;
    }


    public JSONArray getMemLocationJsonArray() {
        JSONArray jsonArray = new JSONArray();
        try {
            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put("id", "1");
            jsonObject1.put("name", "PMU");

            JSONObject jsonObject2 = new JSONObject();
            jsonObject2.put("id", "2");
            jsonObject2.put("name", "District");

            JSONObject jsonObject3 = new JSONObject();
            jsonObject3.put("id", "3");
            jsonObject3.put("name", "Subdivision");

            jsonArray.put(jsonObject1);
            jsonArray.put(jsonObject2);
            jsonArray.put(jsonObject3);


        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonArray;
    }

    public JSONArray getDistMemLocationJsonArray() {
        JSONArray jsonArray = new JSONArray();
        try {

            JSONObject jsonObject2 = new JSONObject();
            jsonObject2.put("id", "2");
            jsonObject2.put("name", "District");

            JSONObject jsonObject3 = new JSONObject();
            jsonObject3.put("id", "3");
            jsonObject3.put("name", "Subdivision");

            jsonArray.put(jsonObject2);
            jsonArray.put(jsonObject3);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonArray;
    }

    public JSONArray getSubDivMemLocationJsonArray() {
        JSONArray jsonArray = new JSONArray();
        try {

            JSONObject jsonObject3 = new JSONObject();
            jsonObject3.put("id", "3");
            jsonObject3.put("name", "Subdivision");

            jsonArray.put(jsonObject3);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonArray;
    }




    public JSONArray getPmuMemOptJsonArray() {
        JSONArray jsonArray = new JSONArray();
        try {
            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put("id", "1");
            jsonObject1.put("name", "Location");

            JSONObject jsonObject2 = new JSONObject();
            jsonObject2.put("id", "2");
            jsonObject2.put("name", "Designation");

            jsonArray.put(jsonObject1);
            jsonArray.put(jsonObject2);


        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonArray;
    }


    public JSONArray getPsEventTypeArray() {
        JSONArray jsonArray = new JSONArray();
        try {
            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put("id", "1");
            jsonObject1.put("name", "Training");
            jsonObject1.put("isChecked","0");

            JSONObject jsonObject2 = new JSONObject();
            jsonObject2.put("id", "2");
            jsonObject2.put("name", "Exposure");
            jsonObject2.put("isChecked","0");

            JSONObject jsonObject3 = new JSONObject();
            jsonObject3.put("id", "3");
            jsonObject3.put("name", "Workshop");
            jsonObject3.put("isChecked","0");

            jsonArray.put(jsonObject1);
            jsonArray.put(jsonObject2);
            jsonArray.put(jsonObject3);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonArray;
    }

    public JSONArray getPmuEventTypeArray() {
        JSONArray jsonArray = new JSONArray();
        try {
            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put("id", "1");
            jsonObject1.put("name", "Training");
            jsonObject1.put("isChecked","0");

            JSONObject jsonObject2 = new JSONObject();
            jsonObject2.put("id", "2");
            jsonObject2.put("name", "Exposure");
            jsonObject2.put("isChecked","0");

            JSONObject jsonObject3 = new JSONObject();
            jsonObject3.put("id", "3");
            jsonObject3.put("name", "Workshop");
            jsonObject3.put("isChecked","0");

            JSONObject jsonObject4 = new JSONObject();
            jsonObject4.put("id", "4");
            jsonObject4.put("name", "Training");
            jsonObject4.put("isChecked","0");

            jsonArray.put(jsonObject1);
            jsonArray.put(jsonObject2);
            jsonArray.put(jsonObject3);
            jsonArray.put(jsonObject4);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonArray;
    }



    public JSONArray reasonOfEventCancelArray() {
        JSONArray jsonArray = new JSONArray();
        try {
            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put("id", "1");
            jsonObject1.put("reason", "Reason 1");

            JSONObject jsonObject2 = new JSONObject();
            jsonObject2.put("id", "2");
            jsonObject2.put("reason", "Reason 2");

            JSONObject jsonObject3 = new JSONObject();
            jsonObject3.put("id", "3");
            jsonObject3.put("reason", "Reason 3");

            JSONObject jsonObject4 = new JSONObject();
            jsonObject4.put("id", "4");
            jsonObject4.put("reason", "Reason 4");

            JSONObject jsonObject5 = new JSONObject();
            jsonObject5.put("id", "5");
            jsonObject5.put("reason", "Other");

            jsonArray.put(jsonObject1);
            jsonArray.put(jsonObject2);
            jsonArray.put(jsonObject3);
            jsonArray.put(jsonObject4);
            jsonArray.put(jsonObject5);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonArray;
    }



    public JSONArray getGpMemListDemoJsonArray() {
        JSONArray jsonArray = new JSONArray();
        try {
            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put("id", "1");
            jsonObject1.put("name", "Chairman");
            jsonObject1.put("isChecked","0");

            JSONObject jsonObject2 = new JSONObject();
            jsonObject2.put("id", "2");
            jsonObject2.put("name", "Vice Chairman");
            jsonObject2.put("isChecked","0");

            JSONObject jsonObject3 = new JSONObject();
            jsonObject3.put("id", "3");
            jsonObject3.put("name", "GP Member (male)");
            jsonObject3.put("isChecked","0");

            JSONObject jsonObject4 = new JSONObject();
            jsonObject4.put("id", "4");
            jsonObject4.put("name", "GP Member (female)");
            jsonObject4.put("isChecked","0");

            JSONObject jsonObject5 = new JSONObject();
            jsonObject5.put("id", "5");
            jsonObject5.put("name", "Progressive farmer (General)");
            jsonObject5.put("isChecked","0");

            JSONObject jsonObject6 = new JSONObject();
            jsonObject6.put("id", "6");
            jsonObject6.put("name", "Progressive farmer (SC/ST/OBC/VJMT)");
            jsonObject6.put("isChecked","0");

            JSONObject jsonObject7 = new JSONObject();
            jsonObject7.put("id", "7");
            jsonObject7.put("name", "Female farmer (General)");
            jsonObject7.put("isChecked","0");

            JSONObject jsonObject8 = new JSONObject();
            jsonObject8.put("id", "8");
            jsonObject8.put("name", "Female farmer (SC/ST)");
            jsonObject8.put("isChecked","0");

            JSONObject jsonObject9 = new JSONObject();
            jsonObject9.put("id", "9");
            jsonObject9.put("name", "Female farmer (OBC/VJMT)");
            jsonObject9.put("isChecked","0");

            JSONObject jsonObject10 = new JSONObject();
            jsonObject10.put("id", "10");
            jsonObject10.put("name", "Farmer federation/Group(FPO/FPC)");
            jsonObject10.put("isChecked","0");

            JSONObject jsonObject11 = new JSONObject();
            jsonObject11.put("id", "11");
            jsonObject11.put("name", "Women Self Help group(SHG)");
            jsonObject11.put("isChecked","0");

            JSONObject jsonObject12 = new JSONObject();
            jsonObject12.put("id", "12");
            jsonObject12.put("name", "Agriculture allied farmer 1");
            jsonObject12.put("isChecked","0");

            JSONObject jsonObject13 = new JSONObject();
            jsonObject13.put("id", "13");
            jsonObject13.put("name", "Agriculture allied farmer 2");
            jsonObject13.put("isChecked","0");

            jsonArray.put(jsonObject1);
            jsonArray.put(jsonObject2);
            jsonArray.put(jsonObject3);
            jsonArray.put(jsonObject4);
            jsonArray.put(jsonObject5);
            jsonArray.put(jsonObject6);
            jsonArray.put(jsonObject7);
            jsonArray.put(jsonObject8);
            jsonArray.put(jsonObject9);
            jsonArray.put(jsonObject10);
            jsonArray.put(jsonObject11);
            jsonArray.put(jsonObject12);
            jsonArray.put(jsonObject13);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonArray;
    }



    public JSONArray getPsGroupArray() {
        JSONArray jsonArray = new JSONArray();
        try {

            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put("id", "1");
            jsonObject1.put("name", "VCRMC1");
            jsonObject1.put("isChecked","0");

            JSONObject jsonObject2 = new JSONObject();
            jsonObject2.put("id", "2");
            jsonObject2.put("name", "VCRMC2");
            jsonObject2.put("isChecked","0");

            JSONObject jsonObject3 = new JSONObject();
            jsonObject3.put("id", "3");
            jsonObject3.put("name", "VCRMC3");
            jsonObject3.put("isChecked","0");

            JSONObject jsonObject4 = new JSONObject();
            jsonObject4.put("id", "3");
            jsonObject4.put("name", "Officer");
            jsonObject4.put("isChecked","0");

            JSONObject jsonObject5 = new JSONObject();
            jsonObject5.put("id", "3");
            jsonObject5.put("name", "Other");
            jsonObject5.put("isChecked","0");

            jsonArray.put(jsonObject1);
            jsonArray.put(jsonObject2);
            jsonArray.put(jsonObject3);
            jsonArray.put(jsonObject4);
            jsonArray.put(jsonObject5);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonArray;
    }


    public JSONArray getFieldOfficialListArray() {
        JSONArray jsonArray = new JSONArray();
        try {

            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put("id", "1");
            jsonObject1.put("name", "DSAO");
            jsonObject1.put("isChecked","0");

            JSONObject jsonObject2 = new JSONObject();
            jsonObject2.put("id", "2");
            jsonObject2.put("name", "PD Atma");
            jsonObject2.put("isChecked","0");

            JSONObject jsonObject3 = new JSONObject();
            jsonObject3.put("id", "3");
            jsonObject3.put("name", "SDAO");
            jsonObject3.put("isChecked","0");

            JSONObject jsonObject4 = new JSONObject();
            jsonObject4.put("id", "4");
            jsonObject4.put("name", "PS Agriculture");
            jsonObject4.put("isChecked","0");

            JSONObject jsonObject5 = new JSONObject();
            jsonObject5.put("id", "5");
            jsonObject5.put("name", "Agriculture Supervisor");
            jsonObject5.put("isChecked","0");

            JSONObject jsonObject6 = new JSONObject();
            jsonObject6.put("id", "6");
            jsonObject6.put("name", "Agriculture Assistant");
            jsonObject6.put("isChecked","0");

            JSONObject jsonObject7 = new JSONObject();
            jsonObject7.put("id", "7");
            jsonObject7.put("name", "PS HRD");
            jsonObject7.put("isChecked","0");

            JSONObject jsonObject8 = new JSONObject();
            jsonObject8.put("id", "8");
            jsonObject8.put("name", "CA");
            jsonObject8.put("isChecked","0");

            jsonArray.put(jsonObject1);
            jsonArray.put(jsonObject2);
            jsonArray.put(jsonObject4);
            jsonArray.put(jsonObject5);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonArray;
    }


    public JSONArray getPMUArray() {
        JSONArray jsonArray = new JSONArray();
        try {

            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put("id", "1");
            jsonObject1.put("name", "PMU Official");
            jsonObject1.put("isChecked","0");

            JSONObject jsonObject2 = new JSONObject();
            jsonObject2.put("id", "2");
            jsonObject2.put("name", "Field Official");
            jsonObject2.put("isChecked","0");

            JSONObject jsonObject4 = new JSONObject();
            jsonObject4.put("id", "3");
            jsonObject4.put("name", "Officer");
            jsonObject4.put("isChecked","0");

            JSONObject jsonObject5 = new JSONObject();
            jsonObject5.put("id", "3");
            jsonObject5.put("name", "Other");
            jsonObject5.put("isChecked","0");

            jsonArray.put(jsonObject1);
            jsonArray.put(jsonObject2);
            jsonArray.put(jsonObject4);
            jsonArray.put(jsonObject5);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonArray;
    }


    public JSONArray getComingEventOptArray() {
        JSONArray jsonArray = new JSONArray();
        try {

            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put("id", "1");
            jsonObject1.put("name", "My Upcoming Events");

            JSONObject jsonObject2 = new JSONObject();
            jsonObject2.put("id", "2");
            jsonObject2.put("name", "All Upcoming Events");

            jsonArray.put(jsonObject1);
            jsonArray.put(jsonObject2);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonArray;
    }


    public JSONArray getComingEventCalOptArray() {
        JSONArray jsonArray = new JSONArray();
        try {

            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put("id", "1");
            jsonObject1.put("name", "My Events Calendar");

            JSONObject jsonObject2 = new JSONObject();
            jsonObject2.put("id", "2");
            jsonObject2.put("name", "All Events Calendar");

            jsonArray.put(jsonObject1);
            jsonArray.put(jsonObject2);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonArray;
    }


    public JSONArray geEventReportOptArray() {
        JSONArray jsonArray = new JSONArray();
        try {

            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put("id", "1");
            jsonObject1.put("name", "My Attendance Reports");

            JSONObject jsonObject2 = new JSONObject();
            jsonObject2.put("id", "2");
            jsonObject2.put("name", "All Attendance Reports");

            jsonArray.put(jsonObject1);
            jsonArray.put(jsonObject2);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonArray;
    }


    public JSONArray getAddPersonOptArray() {
        JSONArray jsonArray = new JSONArray();
        try {

            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put("id", "1");
            jsonObject1.put("name", "Agri Labour");

            JSONObject jsonObject2 = new JSONObject();
            jsonObject2.put("id", "2");
            jsonObject2.put("name", "Resource Person");

            jsonArray.put(jsonObject1);
            jsonArray.put(jsonObject2);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonArray;
    }


    public JSONArray getCancelUpdateArray() {
        JSONArray jsonArray = new JSONArray();
        try {

            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put("id", "1");
            jsonObject1.put("name", "Update");

            JSONObject jsonObject2 = new JSONObject();
            jsonObject2.put("id", "2");
            jsonObject2.put("name", "Cancel");

            jsonArray.put(jsonObject1);
            jsonArray.put(jsonObject2);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonArray;
    }



    public JSONArray getSampleJSONArray() {
        JSONArray jsonArray = new JSONArray();
        try {
            JSONObject jsonObject1 = new JSONObject();

            jsonObject1.put("is_selected", "0");
            jsonObject1.put("fld_testimonialId", "1");
            jsonObject1.put("fld_testimonialDesc", null);
            jsonObject1.put("fld_testimonialImg", "https://imgd.aeplcdn.com/476x268/ec/4C/26/10742/img/l/Tata-Xenon-XT-Right-Front-Three-Quater-18280.jpg?v=201711021421&q=85");

            JSONObject jsonObject2 = new JSONObject();
            jsonObject2.put("is_selected", "0");
            jsonObject2.put("fld_testimonialId", "2");
            jsonObject2.put("fld_testimonialDesc", null);
            jsonObject2.put("fld_testimonialImg", "https://cdni.autocarindia.com/Utils/ImageResizer.ashx?n=http%3A%2F%2Fcdni.autocarindia.com%2FExtraImages%2F20161017120714_xenoz.jpg&h=578&w=872&c=0&q=100");

            JSONObject jsonObject3 = new JSONObject();
            jsonObject3.put("is_selected", "0");
            jsonObject3.put("fld_testimonialId", "3");
            jsonObject3.put("fld_testimonialDesc", null);
            jsonObject3.put("fld_testimonialImg", "http://www.tatamotors.com.au/images/photos/Xenon/Tata-Xenon-4.jpg");

            JSONObject jsonObject4 = new JSONObject();
            jsonObject4.put("is_selected", "0");
            jsonObject4.put("fld_testimonialId", "4");
            jsonObject4.put("fld_testimonialDesc", null);
            jsonObject4.put("fld_testimonialImg", "https://s3.caradvice.com.au/thumb/960/500/wp-content/uploads/2013/10/Tata-Xenon-ute-2-625x408.jpg");

            JSONObject jsonObject5 = new JSONObject();
            jsonObject5.put("is_selected", "0");
            jsonObject5.put("fld_testimonialId", "5");
            jsonObject5.put("fld_testimonialDesc", null);
            jsonObject5.put("fld_testimonialImg", "http://www.theautomotiveindia.com/forums/attachments/official-road-tests/182538d1445440969-tata-xenon-4x4-road-test-drive-review-dsc_0984.jpg");

            jsonArray.put(jsonObject1);
            jsonArray.put(jsonObject2);
            jsonArray.put(jsonObject3);
            jsonArray.put(jsonObject4);
            jsonArray.put(jsonObject5);


        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonArray;
    }




    public String FormatDate(String date){
        String fDate = "";
        SimpleDateFormat format = new SimpleDateFormat("yyyy-mm-dd");
        Date newDate = null;
        try {
            if (!date.equalsIgnoreCase("")){
                newDate = format.parse(date);
                format = new SimpleDateFormat("dd-mm-yyyy hh:mm aa");
                fDate = format.format(newDate);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return fDate;
    }

    public String FormatMonthDate(String date){
        String fDate = "";
       // Date newDate = null;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        try {
            if (!date.equalsIgnoreCase("")){
                Date newDate = format.parse(date);
               // format = new SimpleDateFormat("dd-mm-yyyy hh:mm aa");
               // fDate = format.format(newDate);

                String dayOfTheWeek = (String) DateFormat.format("EEEE", newDate); // Thursday
                String day          = (String) DateFormat.format("dd",   newDate); // 20
                String monthString  = (String) DateFormat.format("MMM",  newDate); // Jun
                String monthNumber  = (String) DateFormat.format("MM",   newDate); // 06
                String year         = (String) DateFormat.format("yyyy", newDate); // 2013

                fDate = monthString+" "+day;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return fDate;
    }

    public String getVerificationCode(String message) {
        String code = null;

        int start = 0;
        int length = 6;
        code = message.substring(start, start + length);
        return code.trim().replace(" ", "");

        //int index = message.indexOf(com.tatamotors.tma.app_util.AppConstants.kDELIMITER);
        /*if (index != -1) {
            int start = 0;
            int length = 6;
            code = message.substring(start, start + length);
            //return code;
        }*/
    }



    public JSONArray getPostImgOptionArray() {
        JSONArray jsonArray = new JSONArray();
        try {
            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put("id", "0");
            jsonObject1.put("name", "Delete");

            JSONObject jsonObject2 = new JSONObject();
            jsonObject2.put("id", "1");
            jsonObject2.put("name", "Set Default Cover");

            JSONObject jsonObject3 = new JSONObject();
            jsonObject3.put("id", "2");
            jsonObject3.put("name", "Cancel");

            jsonArray.put(jsonObject1);
            jsonArray.put(jsonObject2);
            jsonArray.put(jsonObject3);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonArray;
    }




}
