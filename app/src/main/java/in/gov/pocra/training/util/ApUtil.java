package in.gov.pocra.training.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;

import androidx.annotation.RequiresApi;
import androidx.core.content.FileProvider;

import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import in.co.appinventor.services_api.app_util.AppUtility;
import in.co.appinventor.services_api.listener.AlertListCallbackEventListener;
import in.co.appinventor.services_api.listener.DatePickerCallbackListener;
import in.co.appinventor.services_api.widget.UIToastMessage;
import in.gov.pocra.training.BuildConfig;
import in.gov.pocra.training.R;

import static android.content.Context.INPUT_METHOD_SERVICE;

@SuppressWarnings("unchecked")
public class ApUtil {

    private Context _context;
    private static final String IMAGE_DIRECTORY = "/PoCRA_TRAINING";


    // constructor
    public ApUtil(Context context) {
        this._context = context;
    }

    public static boolean checkInternetConnection(Context mContext) {
        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = cm.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected() && networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            System.out.println("true wifi");
            return true;
        }

        if (networkInfo != null && networkInfo.isConnected() && networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
            System.out.println("true edge");
            return true;
        }
        if (networkInfo != null && networkInfo.isConnected()) {
            System.out.println("true net");
            return true;
        }
        System.out.println("false");
        return false;
    }


    public static String getDateByTimeStamp(String timeStamp) {
        Calendar cal = Calendar.getInstance();
        TimeZone tz = cal.getTimeZone();//get your local time zone.
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        sdf.setTimeZone(tz);//set time zone.
        long time = Long.parseLong(timeStamp) * 1000L;
        String formatDate = sdf.format(new Date(time));
        return formatDate;
    }

    public static String getDateTimeByTimeStamp(String timeStamp) {
        Calendar cal = Calendar.getInstance();
        TimeZone tz = cal.getTimeZone();//get your local time zone.
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm a", Locale.getDefault());
        sdf.setTimeZone(tz);//set time zone.
        long time = Long.parseLong(timeStamp) * 1000L;
        String formatDate = sdf.format(new Date(time));
        return formatDate;
    }

    public static String getDateYMDByTimeStamp(String timeStamp) {
        Calendar cal = Calendar.getInstance();
        TimeZone tz = cal.getTimeZone();//get your local time zone.
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        sdf.setTimeZone(tz);//set time zone.
        long time = Long.parseLong(timeStamp) * 1000L;
        String formatDate = sdf.format(new Date(time));
        return formatDate;
    }


    public static String getFormatedDateByDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        String formatDate = sdf.format(date);
        return formatDate;
    }


    public static String getTimeByTimeStamp(String timeStamp) {
        Calendar cal = Calendar.getInstance();
        TimeZone tz = cal.getTimeZone();//get your local time zone.
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        sdf.setTimeZone(tz);//set time zone.
        long time = Long.parseLong(timeStamp) * 1000L;
        String formatTime = sdf.format(new Date(time));
        return formatTime;
    }

    public static String con12to24HrsTimeFormat(String startDate, String timeStamp) {
        String startDateTime = startDate + " " + timeStamp;
        DateFormat inputDf = new SimpleDateFormat("yyyy-MM-dd hh:mm", Locale.US);
        SimpleDateFormat outPutDf = new SimpleDateFormat("HH:mm", Locale.US);
        String output = null;
        try {
            //Conversion of input String to date
            Date date = inputDf.parse(startDateTime);
            //old date format to new date format
            output = outPutDf.format(date);
        } catch (ParseException pe) {
            pe.printStackTrace();
        }
        return output;
    }

    public static String con12AMPMto24HrsTimeFormat(String startDate, String timeStamp) {
        String startDateTime = startDate + " " + timeStamp;
        DateFormat inputDf = new SimpleDateFormat("yyyy-MM-dd hh:mm a", Locale.US);
        SimpleDateFormat outPutDf = new SimpleDateFormat("HH:mm", Locale.US);
        String output = null;
        try {
            //Conversion of input String to date
            Date date = inputDf.parse(startDateTime);
            //old date format to new date format
            output = outPutDf.format(date);
        } catch (ParseException pe) {
            pe.printStackTrace();
        }
        return output;
    }


    public static Boolean isTimeExpired(String timeStamp) {

        Boolean result = false;
        long currentTimeStamp = System.currentTimeMillis() / 1000;
        long givenTimeStamp = Long.parseLong(timeStamp);
        if (currentTimeStamp > givenTimeStamp) {
            result = true;
        }
        return result;
    }

    public static String getCurrentTimeStamp() {
        Long tsLong = System.currentTimeMillis() / 1000;
        String ts = tsLong.toString();
        return ts;
    }

    public static Boolean isFutureDate(String date) {
        Boolean result = false;
        try {
            String userDateFormat = "dd/MM/yyyy";
            DateFormat sdf = new SimpleDateFormat(userDateFormat, Locale.ENGLISH);
            Date strDate = sdf.parse(date);
            if (new Date().before(strDate)) {
                result = true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return result;
    }


    public static String getDateInDDMMYYYY(String YYYYMMDD) {
        String d = "";
        if (!YYYYMMDD.equalsIgnoreCase("")) {
            Date cDate = null;

            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            try {
                cDate = new SimpleDateFormat("yyyy-MM-dd").parse(YYYYMMDD);
                Calendar c = Calendar.getInstance();
                c.setTime(cDate);
                c.add(Calendar.DATE, 0);
                Date dayPlushOne = c.getTime();
                d = dateFormat.format(dayPlushOne);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return d;
    }

    public static String getDateInDDMMYYYYWithSeperator(String YYYYMMDD, String seperator) {
        String d = "";
        if (!YYYYMMDD.equalsIgnoreCase("")) {
            Date cDate = null;

            DateFormat dateFormat = new SimpleDateFormat("dd" + seperator + "MM" + seperator + "yyyy");
            try {
                cDate = new SimpleDateFormat("yyyy-MM-dd").parse(YYYYMMDD);
                Calendar c = Calendar.getInstance();
                c.setTime(cDate);
                c.add(Calendar.DATE, 0);
                Date dayPlushOne = c.getTime();
                d = dateFormat.format(dayPlushOne);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return d;
    }


    public static JSONArray getDateBetweenTwoDate(String str_date, String end_date) {


        JSONArray dateArray = new JSONArray();
        List<Date> dates = new ArrayList<Date>();

        try {
            DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
            Date sDate = (Date) formatter.parse(str_date);
            Date eDate = (Date) formatter.parse(end_date);
            long interval = 24 * 1000 * 60 * 60; // 1 hour in millis
            long endTime = eDate.getTime(); // create your endTime here, possibly using Calendar or Date
            long curTime = sDate.getTime();

            while (curTime <= endTime) {
                dates.add(new Date(curTime));
                curTime += interval;
            }

            int dayCount = 0;
            for (int i = 0; i < dates.size(); i++) {
                JSONObject dateJson = new JSONObject();
                Date lDate = (Date) dates.get(i);
                String dt = formatter.format(lDate);
                String day = "Day " + ++dayCount;
                /*"day": "Days 1",
                        "date": "12-03-2019"*/
                dateJson.put("day", day);
                dateJson.put("date", dt);
                dateArray.put(dateJson);

            }

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return dateArray;
    }


    public static Boolean isDatePassed(String date) {

        boolean result = false;

        try {

            if (new SimpleDateFormat("dd-MM-yyyy").parse(date).before(new Date())) {
                result = true;
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return result;
    }

    public static Boolean isDateEqual(String sDate, String eDate) {

        boolean result = false;
        try {
            if (new SimpleDateFormat("dd-MM-yyyy").parse(sDate).equals(new SimpleDateFormat("dd-MM-yyyy").parse(eDate))) {
                result = true;
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
    }


    private static final int MEGABYTE = 1024 * 1024;

    public static void downloadPdfFile(String fileUrl, File directory) {
        try {

            URL url = new URL(fileUrl);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setDoOutput(true);
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            FileOutputStream fileOutputStream = new FileOutputStream(directory);
            int totalSize = urlConnection.getContentLength();

            byte[] buffer = new byte[MEGABYTE];
            int bufferLength = 0;
            while ((bufferLength = inputStream.read(buffer)) > 0) {
                fileOutputStream.write(buffer, 0, bufferLength);
            }
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static boolean isSDCardPresent() {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            return true;
        }
        return false;
    }


   /* public static void showDatePickerBtnTwoDates(Context context, Date sDate,Date eDate, final TextView dTextView, final DatePickerRequestListener callbackListener) {
        Calendar calender = Calendar.getInstance();
        int mYear = calender.get(1);
        int mMonth = calender.get(2);
        int mDay = calender.get(5);
        DatePickerDialog datePickerDialog1 = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
            @SuppressLint({"SetTextI18n"})
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                callbackListener.onDateSelected(dTextView, dayOfMonth, monthOfYear + 1, year);
                dTextView.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
            }
        }, mYear, mMonth, mDay);
        long sMillis = sDate.getTime();
        long eMillis = eDate.getTime();
        datePickerDialog1.getDatePicker().setMaxDate(sMillis);
        datePickerDialog1.getDatePicker().setMinDate(eMillis);
        datePickerDialog1.show();
    }*/


    public static void showDatePickerBtnTwoDates(Context context, Date sDate, Date eDate, final TextView textView, final DatePickerCallbackListener callbackListener) {
//        Calendar calender = Calendar.getInstance();
//        int mYear = calender.get(1);
//        int mMonth = calender.get(2);
//        int mDay = calender.get(5);

        int mYear = Calendar.YEAR;
        int mMonth = Calendar.MONTH;
        int mDay = Calendar.DAY_OF_MONTH;
        DatePickerDialog datePickerDialog1 = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
            @SuppressLint({"SetTextI18n"})
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                callbackListener.onDateSelected(textView, dayOfMonth, monthOfYear + 1, year);
                textView.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
            }
        }, mYear, mMonth, mDay);
        long sMillis = sDate.getTime();
        long eMillis = eDate.getTime();
        datePickerDialog1.getDatePicker().setMinDate(sMillis);
        datePickerDialog1.getDatePicker().setMaxDate(eMillis);
        datePickerDialog1.show();
    }

    public static void showDatePickerBtnTwoDatesWithOutTextView(Context context, Date sDate, Date eDate, final TextView textView, final DatePickerCallbackListener callbackListener) {
//        Calendar calender = Calendar.getInstance();
//        int mYear = calender.get(1);
//        int mMonth = calender.get(2);
//        int mDay = calender.get(5);
//
        int mYear = Calendar.YEAR;
        int mMonth = Calendar.MONTH;
        int mDay = Calendar.DAY_OF_MONTH;


        DatePickerDialog datePickerDialog1 = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
            @SuppressLint({"SetTextI18n"})
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                callbackListener.onDateSelected(textView, dayOfMonth, monthOfYear + 1, year);
                // textView.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
            }
        }, mYear, mMonth, mDay);
        long sMillis = sDate.getTime();
        long eMillis = eDate.getTime();
        datePickerDialog1.getDatePicker().setMinDate(sMillis);
        datePickerDialog1.getDatePicker().setMaxDate(eMillis);
        // datePickerDialog1.getDatePicker().findViewById(context.getResources().getIdentifier("day","id","android")).setVisibility(View.GONE);

        datePickerDialog1.getDatePicker().setSpinnersShown(true);
        datePickerDialog1.getDatePicker().setCalendarViewShown(false);
        LinearLayout pickerParentLayout = (LinearLayout) datePickerDialog1.getDatePicker().getChildAt(0);
        LinearLayout pickerSpinnersHolder = (LinearLayout) pickerParentLayout.getChildAt(0);
        pickerSpinnersHolder.getChildAt(0).setVisibility(View.GONE);
    }


    public static void showTimePicker(Context context, final TextView textView, final TimePickerCallbackListener timeCallbackListener) {
        Calendar calender = Calendar.getInstance();
        final int hour = calender.get(Calendar.HOUR_OF_DAY);
        int minute = calender.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int selectedHour, int selectedMinute) {


                Time time = new Time(selectedHour, selectedMinute, 0);

                //little h uses 12 hour format and big H uses 24 hour format
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("H:mm a");

                //format takes in a Date, and Time is a sublcass of Date
                String s = simpleDateFormat.format(time);
                textView.setText(s);
                timeCallbackListener.onTimeSelected(textView, selectedHour, selectedMinute, s);

                /*if (selectedHour <= 12) {
                    String timeType = "AM";
                    timeCallbackListener.onTimeSelected(textView, selectedHour, selectedMinute, timeType);
//                    textView.setText(selectedHour + ":" + selectedMinute + " " + timeType);
                } else {
                    String timeType = "PM";
                    int newHour = selectedHour - 12;
                    timeCallbackListener.onTimeSelected(textView, selectedHour, selectedMinute, timeType);
//                    textView.setText(newHour + ":" + selectedMinute + " " + timeType);
                }*/

            }
        }, hour, minute, true);

        timePickerDialog.setTitle("Select Time");
        timePickerDialog.show();
    }

    public static void showTimePicker12(Context context, final TextView textView, final TimePickerCallbackListener timeCallbackListener) {
        Calendar calender = Calendar.getInstance();
        final int hour = calender.get(Calendar.HOUR_OF_DAY);
        int minute = calender.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int selectedHour, int selectedMinute) {

                Time time = new Time(selectedHour, selectedMinute, 0);

                //little h uses 12 hour format and big H uses 24 hour format
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("h:mm a");

                //format takes in a Date, and Time is a sublcass of Date
                String s = simpleDateFormat.format(time);
                textView.setText(s);
                timeCallbackListener.onTimeSelected(textView, selectedHour, selectedMinute, s);

                /*if (selectedHour <= 12) {
                    String timeType = "AM";
                    timeCallbackListener.onTimeSelected(textView, selectedHour, selectedMinute, timeType);
//                    textView.setText(selectedHour + ":" + selectedMinute + " " + timeType);
                } else {
                    String timeType = "PM";
                    int newHour = selectedHour - 12;
                    timeCallbackListener.onTimeSelected(textView, selectedHour, selectedMinute, timeType);
//                    textView.setText(newHour + ":" + selectedMinute + " " + timeType);
                }*/

            }
        }, hour, minute, true);

        timePickerDialog.setTitle("Select Time");
        timePickerDialog.show();
    }

    public static void showTimeAgainstStartTimePicker(final Context context, final TextView textView, final int startHour, final int startMinuets, final TimePickerCallbackListener timeCallbackListener) {
        Calendar calender = Calendar.getInstance();
        final int hour = calender.get(Calendar.HOUR_OF_DAY);
        int minute = calender.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog1 = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int selectedHour, int selectedMinute) {

                if (selectedHour > startHour || (selectedHour == startHour && selectedMinute > startMinuets)) {

                    Time time = new Time(selectedHour, selectedMinute, 0);

                    //little h uses 12 hour format and big H uses 24 hour format
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("H:mm a");

                    //format takes in a Date, and Time is a sublcass of Date
                    String s = simpleDateFormat.format(time);
                    textView.setText(s);
                    timeCallbackListener.onTimeSelected(textView, selectedHour, selectedMinute, s);

                    /*if (selectedHour <= 12) {
                        String timeType = "AM";
                        timeCallbackListener.onTimeSelected(textView, selectedHour, selectedMinute, timeType);
                        textView.setText(selectedHour + ":" + selectedMinute + " " + timeType);
                    } else {
                        String timeType = "PM";
                        int newHour = selectedHour - 12;
                        timeCallbackListener.onTimeSelected(textView, selectedHour, selectedMinute, timeType);
                        textView.setText(newHour + ":" + selectedMinute + " " + timeType);
                    }*/
                } else {
                    UIToastMessage.show(context, "End Time cannot be before the Start Time");
                }
            }
        }, hour, minute, true);
        timePickerDialog1.setTitle("");
        timePickerDialog1.show();
    }

    public static void showTimeAgainstStartTimePicker12(final Context context, final TextView textView, final int startHour, final int startMinuets, final TimePickerCallbackListener timeCallbackListener) {
        Calendar calender = Calendar.getInstance();
        final int hour = calender.get(Calendar.HOUR_OF_DAY);
        int minute = calender.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog1 = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int selectedHour, int selectedMinute) {

                if (selectedHour > startHour || (selectedHour == startHour && selectedMinute > startMinuets)) {

                    Time time = new Time(selectedHour, selectedMinute, 0);

                    //little h uses 12 hour format and big H uses 24 hour format
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("h:mm a");

                    //format takes in a Date, and Time is a sublcass of Date
                    String s = simpleDateFormat.format(time);
                    textView.setText(s);
                    timeCallbackListener.onTimeSelected(textView, selectedHour, selectedMinute, s);

                    /*if (selectedHour <= 12) {
                        String timeType = "AM";
                        timeCallbackListener.onTimeSelected(textView, selectedHour, selectedMinute, timeType);
                        textView.setText(selectedHour + ":" + selectedMinute + " " + timeType);
                    } else {
                        String timeType = "PM";
                        int newHour = selectedHour - 12;
                        timeCallbackListener.onTimeSelected(textView, selectedHour, selectedMinute, timeType);
                        textView.setText(newHour + ":" + selectedMinute + " " + timeType);
                    }*/
                } else {
                    //UIToastMessage.show(context, "End Time cannot be before the Start Time");
                    Toast.makeText(context.getApplicationContext(), "End Time cannot be before the Start Time", Toast.LENGTH_SHORT).show();
                }
            }
        }, hour, minute, true);
        timePickerDialog1.setTitle("");
        timePickerDialog1.show();
    }


    public static void showTimeBeforeStartTimePicker12(final Context context, final TextView textView, final int startHour, final int startMinuets, final TimePickerCallbackListener timeCallbackListener) {
        Calendar calender = Calendar.getInstance();
        final int hour = calender.get(Calendar.HOUR_OF_DAY);
        int minute = calender.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog1 = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int selectedHour, int selectedMinute) {

                if (selectedHour < startHour || (selectedHour == startHour && selectedMinute < startMinuets)) {

                    Time time = new Time(selectedHour, selectedMinute, 0);

                    //little h uses 12 hour format and big H uses 24 hour format
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("h:mm a");

                    //format takes in a Date, and Time is a sublcass of Date
                    String s = simpleDateFormat.format(time);
                    textView.setText(s);
                    timeCallbackListener.onTimeSelected(textView, selectedHour, selectedMinute, s);

                    /*if (selectedHour <= 12) {
                        String timeType = "AM";
                        timeCallbackListener.onTimeSelected(textView, selectedHour, selectedMinute, timeType);
                        textView.setText(selectedHour + ":" + selectedMinute + " " + timeType);
                    } else {
                        String timeType = "PM";
                        int newHour = selectedHour - 12;
                        timeCallbackListener.onTimeSelected(textView, selectedHour, selectedMinute, timeType);
                        textView.setText(newHour + ":" + selectedMinute + " " + timeType);
                    }*/

                } else {
                    //UIToastMessage.show(context, "Report Time cannot be after the Start Time");
                    Toast.makeText(context.getApplicationContext(), "Report Time cannot be after the Start Time", Toast.LENGTH_SHORT).show();
                }
            }
        }, hour, minute, true);
        timePickerDialog1.setTitle("");
        timePickerDialog1.show();
    }

    public static void shareItemWithDelay(final String url, final Context context, long delay) {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                Picasso.get().load(url).into(new Target() {

                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        Intent i = new Intent(Intent.ACTION_SEND);
                        i.setType("image/*");
                        i.putExtra(Intent.EXTRA_STREAM, getLocalBitmapUri(bitmap, context));
                        context.startActivity(Intent.createChooser(i, "Share Image"));
                    }

                    @Override
                    public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {
                    }
                });
            }
        }, delay);


    }

    public static void shareImage(String url, final Context context) {

        Picasso.get().load(url).into(new Target() {

            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("image/*");
                i.putExtra(Intent.EXTRA_STREAM, getLocalBitmapUri(bitmap, context));
                context.startActivity(Intent.createChooser(i, "Share Image"));
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
            }
        });
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static void sharePdfWithDelay(final String url, final String fileNameWithExt, final Context context, long delay) {


        //final File dir = new File(Environment.getExternalStorageDirectory() + IMAGE_DIRECTORY);
        final File dir= new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), IMAGE_DIRECTORY);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        if (AppUtility.getInstance().isConnected(context)) {

            final File file = new File(dir + "/" + fileNameWithExt);

            if (file.exists()) {

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        Uri uri;
                        if ((Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT)) {
                            uri = FileProvider.getUriForFile(context.getApplicationContext(), BuildConfig.APPLICATION_ID + ".android.fileprovider", file);
                        } else {
                            uri = Uri.fromFile(file);
                        }

                        Intent share = new Intent();
                        share.setAction(Intent.ACTION_SEND);
                        share.setType("application/pdf");
                        share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        share.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                        share.putExtra(Intent.EXTRA_STREAM, uri);
                        context.startActivity(Intent.createChooser(share, "Share PDF"));

                    }
                }, delay - 500);


            } else {
                new DownloadTask(context, url, dir);

                new Handler().postDelayed(new Runnable() {
                    @Override

                    public void run() {

                        File file = new File(dir + "/" + fileNameWithExt);

                        if (file.exists()) {
                            Uri uri;
                            if ((Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT)) {
                                uri = FileProvider.getUriForFile(context.getApplicationContext(), BuildConfig.APPLICATION_ID + ".android.fileprovider", file);
                            } else {
                                uri = Uri.fromFile(file);
                            }

                            Intent share = new Intent();
                            share.setAction(Intent.ACTION_SEND);
                            share.setType("application/pdf");
                            share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            share.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                            share.putExtra(Intent.EXTRA_STREAM, uri);
                            context.startActivity(Intent.createChooser(share, "Share PDF"));
                        }
                    }
                }, delay);
            }

        }




       /* new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent share = new Intent();
                share.setAction(Intent.ACTION_SEND);
                share.setType("application/pdf");
                share.putExtra(Intent.EXTRA_STREAM, url);
                context.startActivity(Intent.createChooser(share, "Share PDF"));
            }
        }, delay);*/
    }


    public static Uri getLocalBitmapUri(Bitmap bmp, Context context) {
        Uri bmpUri = null;
        try {
            File file = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "share_image_" + System.currentTimeMillis() + ".png");
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.close();
            bmpUri = Uri.fromFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmpUri;
    }


   /* public static void showCustomListPicker(final TextView v, JSONArray ja, String title, String type, String typeId, Activity act, final AlertListCallbackEventListener callBackListener) {
        final CharSequence[] items = new CharSequence[ja.length()];

        final ArrayList<String> name = new ArrayList<String>();

        final List<String> selectedIndexArray = new ArrayList();

        for(int i = 0; i < ja.length(); ++i) {
            try {
                items[i] = ja.getJSONObject(i).getString(type);
                selectedIndexArray.add(ja.getJSONObject(i).getString(typeId));
                name.add(ja.getJSONObject(i).getString(type));
            } catch (JSONException var13) {
                var13.printStackTrace();
            }
        }

        final AlertDialog.Builder listPicker = new AlertDialog.Builder(act);
        listPicker.setTitle(title);
        LayoutInflater inflater = act.getLayoutInflater();
        View convertView = (View) inflater.inflate(R.layout.custom_list_picker, null);
        listPicker.setView(convertView);
        final ListView lv = (ListView) convertView.findViewById(R.id.listView);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(act,android.R.layout.simple_list_item_1,name);
        lv.setAdapter(adapter);
        listPicker.setCancelable(true);


        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                v.setText(items[position].toString());
                callBackListener.didSelectAlertViewListItem(v, (String)selectedIndexArray.get(position));

            }
        });

        listPicker.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // v.setText(items[which].toString());
                callBackListener.didSelectAlertViewListItem(v, (String)selectedIndexArray.get(which));
            }
        });
        listPicker.show();
    }*/

    public static void showCustomListPicker(final TextView v, JSONArray ja, String title, String type, String typeId, Activity act, final AlertListCallbackEventListener callBackListener) {
        final CharSequence[] items = new CharSequence[ja.length()];

        final ArrayList<String> name = new ArrayList<String>();
        Log.d("showCustomListPicker","name=="+name);
        final List<String> selectedIndexArray = new ArrayList();

        for (int i = 0; i < ja.length(); ++i) {
            try {
                items[i] = ja.getJSONObject(i).getString(type);
                selectedIndexArray.add(ja.getJSONObject(i).getString(typeId));
                name.add(ja.getJSONObject(i).getString(type));
            } catch (JSONException var13) {
                var13.printStackTrace();
            }
        }

        AlertDialog.Builder listPicker = new AlertDialog.Builder(act);
        listPicker.setTitle(title);
        LayoutInflater inflater = act.getLayoutInflater();
        View convertView = (View) inflater.inflate(R.layout.custom_list_picker, null);
        listPicker.setView(convertView);
        ListView lv = (ListView) convertView.findViewById(R.id.listView);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(act, android.R.layout.simple_list_item_1, name);
        lv.setAdapter(adapter);
        final AlertDialog alertDialog = listPicker.show();
        listPicker.setCancelable(true);


        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                v.setText(items[position].toString());
                callBackListener.didSelectAlertViewListItem(v, (String) selectedIndexArray.get(position));
                alertDialog.dismiss();
            }
        });



        /*listPicker.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                v.setText(items[which].toString());
                // callBackListener.didSelectAlertViewListItem(v, (String)selectedIndexArray.get(which));
                callBackListener.didSelectAlertViewListItem(v, String.valueOf(which));
            }
        });*/


    }


    public static void hideKeybord(View view, Context context) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }


    public static String toTitleCase(String str) {

        if (str == null) {
            return null;
        }

        boolean space = true;
        StringBuilder builder = new StringBuilder(str);
        final int len = builder.length();

        for (int i = 0; i < len; ++i) {
            char c = builder.charAt(i);
            if (space) {
                if (!Character.isWhitespace(c)) {
                    // Convert to title case and switch out of whitespace mode.
                    builder.setCharAt(i, Character.toTitleCase(c));
                    space = false;
                }
            } else if (Character.isWhitespace(c)) {
                space = true;
            } else {
                builder.setCharAt(i, Character.toLowerCase(c));
            }
        }

        return builder.toString();
    }


    public static double distanceByLatLong(double lat1,
                                           double lat2, double lon1,
                                           double lon2) {

        // The math module contains a function
        // named toRadians which converts from
        // degrees to radians.
        lon1 = Math.toRadians(lon1);
        lon2 = Math.toRadians(lon2);
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);

        // Haversine formula
        double dlon = lon2 - lon1;
        double dlat = lat2 - lat1;
        double a = Math.pow(Math.sin(dlat / 2), 2)
                + Math.cos(lat1) * Math.cos(lat2)
                * Math.pow(Math.sin(dlon / 2), 2);

        double c = 2 * Math.asin(Math.sqrt(a));

        // Radius of earth in kilometers. Use 3956
        // for miles
        double r = 6371;

        // calculate the result
        return (c * r);
    }

    // driver code
    /*public static void main(String[] args)
    {
        double lat1 = 53.32055555555556;
        double lat2 = 53.31861111111111;
        double lon1 = -1.7297222222222221;
        double lon2 = -1.6997222222222223;
        System.out.println(distanceByLatLong(lat1, lat2,
                lon1, lon2) + " K.M");
    }*/


    // For toggle View on item click
    private static void slide_up(Context ctx, View v) {

        Animation a = AnimationUtils.loadAnimation(ctx, R.anim.slide_up);
        if (a != null) {
            a.reset();
            if (v != null) {
                v.clearAnimation();
                v.startAnimation(a);
            }
        }
    }

    private static void slide_down(Context ctx, View v) {

        Animation a = AnimationUtils.loadAnimation(ctx, R.anim.slide_down);
        if (a != null) {
            a.reset();
            if (v != null) {
                v.clearAnimation();
                v.startAnimation(a);
            }
        }
    }

    public static void toggle_contents(Context ctx, View v) {

        if (v.isShown()) {
            slide_up(ctx, v);
            v.setVisibility(View.GONE);
        } else {
            v.setVisibility(View.VISIBLE);
            slide_down(ctx, v);
        }
    }

    public static void showForceUpdateDialog(final Context context) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setIcon(context.getResources().getDrawable(R.mipmap.ic_launcher));
        alertDialogBuilder.setTitle(context.getString(R.string.youAreNotUpdatedTitle));
        alertDialogBuilder.setMessage(context.getString(R.string.youAreNotUpdatedMessage));
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton(R.string.update, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                launchPlayStoreApp(context);
                dialog.cancel();
            }
        });

        alertDialogBuilder.setNegativeButton(R.string.letter, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        alertDialogBuilder.show();
    }


    public static void launchPlayStoreApp(Context context) {

        final String appPackageName = context.getPackageName(); // getPackageName() from Context or Activity object
        try {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }

    }

    public static int getAppVersionNumber(Context context) {
        int versionCode = 0;
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            String version = pInfo.versionName;
            versionCode = pInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }

    // To short JSONArray by Key
    public static JSONArray getShortJsonArrayAToZ(Context context, final String key, JSONArray jsonArray) {
        JSONArray sJsonArray = new JSONArray();
        try {
            List<JSONObject> list = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                list.add(jsonArray.getJSONObject(i));
            }
            Collections.sort(list, new Comparator<JSONObject>() {
                @Override
                public int compare(JSONObject a, JSONObject b) {
                    String str1 = new String();
                    String str2 = new String();
                    try {
                        str1 = (String)a.get(key);
                        str2 = (String)b.get(key);
                    } catch(JSONException e) {
                        e.printStackTrace();
                    }
                    return str1.compareTo(str2);
                }

            });
            for(int i = 0; i < jsonArray.length(); i++) {
                sJsonArray.put(list.get(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return sJsonArray;
    }
}



