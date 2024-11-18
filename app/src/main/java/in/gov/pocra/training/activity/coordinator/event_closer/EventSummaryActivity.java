package in.gov.pocra.training.activity.coordinator.event_closer;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;

import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import in.co.appinventor.services_api.api.AppinventorApi;
import in.co.appinventor.services_api.app_util.AppUtility;
import in.co.appinventor.services_api.debug.DebugLog;
import in.co.appinventor.services_api.listener.ApiCallbackCode;
import in.co.appinventor.services_api.settings.AppSettings;
import in.gov.pocra.training.R;
import in.gov.pocra.training.activity.coordinator.event_list.ThankYouActivity;
import in.gov.pocra.training.model.online.ResponseModel;
import in.gov.pocra.training.util.ApConstants;
import in.gov.pocra.training.util.ApUtil;
import in.gov.pocra.training.util.ManagePermission;
import in.gov.pocra.training.web_services.APIRequest;
import in.gov.pocra.training.web_services.APIServices;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Retrofit;

public class EventSummaryActivity extends AppCompatActivity implements ApiCallbackCode {

    private ImageView homeBack;
    private WebView webView;
    private ProgressDialog pDialog;
    String GoogleDocs = "http://docs.google.com/gview?embedded=true&url=";
    private ManagePermission managePermissions;
    private static final int APP_PERMISSION_REQUEST_CODE = 111;
    private static final String IMAGE_DIRECTORY = "/PoCRA_TRAINING";

    private String roleId;
    private String userID;
    private String closerDate = ""; // Value taken for testing
    private String schId;
    private Button cancelBtn;
    private Button confBtn;

    private String buttonAction = "confirm";
    // private String buttonAction = "share PDF";

    private String collagePdfUrl;
    private String collagePdfName = "";
    private boolean collageToShare = false;
    private String pdfShummaryUrl;
    private String pdfShummaryName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_summary);


        /* ** For actionbar title in center ***/
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.attendance_actionbar_layout);
        AppCompatTextView actionTitleTextView = (AppCompatTextView) getSupportActionBar().getCustomView().findViewById(R.id.actionTitleTextView);
        homeBack = (ImageView) getSupportActionBar().getCustomView().findViewById(R.id.backImageView);
        // uploadImageIView = (ImageView) getSupportActionBar().getCustomView().findViewById(R.id.addPersonImageView);
        homeBack.setVisibility(View.VISIBLE);
        // uploadImageIView.setImageDrawable(getResources().getDrawable(R.drawable.ic_menu_camera));
        // uploadImageIView.setVisibility(View.VISIBLE);
        actionTitleTextView.setText(getResources().getString(R.string.title_event_summary));

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

        schId = getIntent().getStringExtra("schId");
        closerDate = getIntent().getStringExtra("closerDate");

        webView = (WebView) findViewById(R.id.summaryWView);
        cancelBtn = (Button) findViewById(R.id.cancelBtn);
        confBtn = (Button) findViewById(R.id.confBtn);

    }


    private void defaultConfiguration() {

        // To get event summary
        String schId = getIntent().getStringExtra("schId");
        getEventSummery(schId);

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("MAYUUUU","btnAction="+buttonAction);
                if (buttonAction.equalsIgnoreCase("confirm")){
                   // eventCloserRequest(); // add by mayu
                    finish();
                }else {
                    collageToShare = false;
                    eventCloserRequest();
                }
            }
        });


        confBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d("MAYUUUU","buttonAction"+buttonAction);
                if (buttonAction.equalsIgnoreCase("confirm")) {
                    Log.d("MAYUUUU","forCloserUserPermission called.....");
                    forCloserUserPermission();
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        collageToShare = true;
                        eventCloserRequest();
                        Log.d("MAYUUUU","confBtn if.....");
                    } else {
                        if (checkUserPermission()) {
                            collageToShare = true;
                            eventCloserRequest();
                            Log.d("MAYUUUU","confBtn else.....");
                        }
                    }
                }
            }
        });
    }

    private void pdfDownloadAndShare() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            ApUtil.sharePdfWithDelay(pdfShummaryUrl, pdfShummaryName+".pdf",EventSummaryActivity.this,2000);
        }
    }


    // TO load Image in WebView
    private void loadImageByUrl(String imgUrl) {

        WebSettings settings = webView.getSettings();
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        webView.setInitialScale(1);
        webView.setBackgroundColor(getResources().getColor(R.color.bg_white));
        webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setSupportZoom(true);
        webView.loadUrl(imgUrl);

    }

    // TO load PDF in WebView

    private void loadPdfByUrl(String pdfUrl) {

       // webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.setInitialScale(1);
        webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setSupportZoom(true);

       // pDialog = ProgressDialog.show(this, "Loading", "Please wait...", true);
       // pDialog.setCancelable(true);

        webView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
               // pDialog.show();
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, final String url) {
                super.onPageFinished(view, url);
                if(view.getContentHeight() == 0){
                    view.reload();
                } else {
                   // pDialog.dismiss();
                }

            }
        });
        Log.d("MAYUUU","PDF=="+GoogleDocs + pdfUrl);
//        webView.loadUrl(GoogleDocs + pdfUrl);
        webView.loadUrl(pdfUrl);
        Log.d("MAYUUU","webView=="+pdfUrl);
    }


    private void forCloserUserPermission() {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Confirm, Are you sure want to close event?");
        alertDialogBuilder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d("MAYUUUU","forCloserUserPermission called.....");
                confBtn.setText("Share Pdf");
                cancelBtn.setText("Exit");
                buttonAction = "share PDF";

            }
        });

        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }

    private void forReportSharePermission() {

        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Are you want share report");
        alertDialogBuilder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                eventCloserRequest();
            }
        });

        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                eventCloserRequest();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }

    @Override
    public void onBackPressed() {
        eventCloserRequest();
        Toast.makeText(this, "Event Closed Successfully...", Toast.LENGTH_SHORT).show();
        super.onBackPressed();
    }

    private void getEventSummery(String schId) {

        try {

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("schedule_id", schId);
            jsonObject.put("attendance_date", closerDate);
            jsonObject.put("api_key", ApConstants.kAUTHORITY_KEY);

            RequestBody requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString());
            AppinventorApi api = new AppinventorApi(this, APIServices.BASE_URL, "", ApConstants.kMSG, true);
            Retrofit retrofit = api.getRetrofitInstance();
            APIRequest apiRequest = retrofit.create(APIRequest.class);
            Call<JsonObject> responseCall = apiRequest.getConsolidatedReportWithCollageRequest(requestBody);

            DebugLog.getInstance().d("event_summery_param=" + responseCall.request().toString());
            DebugLog.getInstance().d("event_summery_param=" + AppUtility.getInstance().bodyToString(responseCall.request()));
            api.postRequest(responseCall, this, 1);


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    private void getCollagePdfRequest() {

        if (closerDate.equalsIgnoreCase("")) {
           // UIToastMessage.show(this, "Event Closer date not found");
            Toast.makeText(this, "Event Closer date not found", Toast.LENGTH_SHORT).show();
        } else {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("schedule_id", schId);
                jsonObject.put("api_key", ApConstants.kAUTHORITY_KEY);

                RequestBody requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString());
                AppinventorApi api = new AppinventorApi(this, APIServices.BASE_URL, "", ApConstants.kMSG, true);
                Retrofit retrofit = api.getRetrofitInstance();
                APIRequest apiRequest = retrofit.create(APIRequest.class);
                Call<JsonObject> responseCall = apiRequest.getCollagePdfRequest(requestBody);

                DebugLog.getInstance().d("collage_pdf_param=" + responseCall.request().toString());
                DebugLog.getInstance().d("collage_pdf_param=" + AppUtility.getInstance().bodyToString(responseCall.request()));
                api.postRequest(responseCall, this, 2);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    private void eventCloserRequest() {

        if (closerDate.equalsIgnoreCase("")) {
//            UIToastMessage.show(this, "Event Closer date not found");
            Toast.makeText(this, "Event Closer date not found", Toast.LENGTH_SHORT).show();
        } else {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("schedule_event_id", schId);
                jsonObject.put("user_id", userID);
                jsonObject.put("event_closer_role_id", roleId);
                jsonObject.put("event_closer_date", closerDate);
                jsonObject.put("api_key", ApConstants.kAUTHORITY_KEY);

                RequestBody requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString());
                AppinventorApi api = new AppinventorApi(this, APIServices.BASE_URL, "", ApConstants.kMSG, true);
                Retrofit retrofit = api.getRetrofitInstance();
                APIRequest apiRequest = retrofit.create(APIRequest.class);
                Call<JsonObject> responseCall = apiRequest.eventCloserRequest(requestBody);

                DebugLog.getInstance().d("event_closer_param=" + responseCall.request().toString());
                DebugLog.getInstance().d("event_closer_param=" + AppUtility.getInstance().bodyToString(responseCall.request()));
                api.postRequest(responseCall, this, 3);


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onResponse(JSONObject jsonObject, int i) {

        try {
            if (jsonObject != null) {

                // To get summary of event
                if (i == 1) {
                    ResponseModel responseModel = new ResponseModel(jsonObject);
                    if (responseModel.isStatus()) {

                        JSONObject dataJSON = jsonObject.getJSONObject("data");
                      //   String pdfUrl = dataJSON.getString("file_path");
                        pdfShummaryName = dataJSON.getString("file_name");
                        pdfShummaryUrl = dataJSON.getString("file_path");
                        Log.d("",""+pdfShummaryUrl);
                        if (AppUtility.getInstance().checkURL(pdfShummaryUrl)) {
                            Log.d("MAYUUU","onResponse PDF1=="+GoogleDocs + pdfShummaryUrl);
                            loadPdfByUrl(pdfShummaryUrl);
                            Log.d("MAYUUU","onResponse webView=="+pdfShummaryUrl);
                        } else {
                           // UIToastMessage.show(this, "URL not found  or Please check pdf url");
                            Toast.makeText(this, "URL not found  or Please check pdf url", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                       // UIToastMessage.show(this, responseModel.getMsg());
                        Toast.makeText(this, ""+responseModel.getMsg(), Toast.LENGTH_SHORT).show();
                    }
                }


                // Event Closer Response
                if (i == 2) {
                    ResponseModel responseModel = new ResponseModel(jsonObject);
                    if (responseModel.isStatus()) {
                        //eventCloserRequest();
                        JSONObject collageJOSN = jsonObject.getJSONObject("data");

                        collagePdfUrl = collageJOSN.getString("file_path");
                        collagePdfName = collageJOSN.getString("file_name");
                        if (AppUtility.getInstance().checkURL(collagePdfUrl)) {
                            Log.d("MAYUUU","onResponse PDF2=="+GoogleDocs + collagePdfUrl);
                            loadPdfByUrl(collagePdfUrl);
                        }
                        confBtn.setText("Share Pdf");
                        cancelBtn.setText("Exit");
                        buttonAction = "share PDF";
                        //UIToastMessage.show(this, responseModel.getMsg());

                    } else {
                        // UIToastMessage.show(this, responseModel.getMsg());
                        Toast.makeText(this, ""+responseModel.getMsg(), Toast.LENGTH_SHORT).show();
                    }
                }


                // Event Closer Response
                if (i == 3) {
                    ResponseModel responseModel = new ResponseModel(jsonObject);
                    if (responseModel.isStatus()) {

                        if (collageToShare){
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                pdfDownloadAndShare();
                            } else {
                                if (checkUserPermission()) {
                                    pdfDownloadAndShare();
                                }
                            }
                        }
                        Intent intent = new Intent(EventSummaryActivity.this, ThankYouActivity.class);
                        startActivity(intent);

//                        UIToastMessage.show(this, responseModel.getMsg());
                        Toast.makeText(this, ""+responseModel.getMsg(), Toast.LENGTH_SHORT).show();

                    } else {
//                         UIToastMessage.show(this, responseModel.getMsg());
                        Toast.makeText(this, ""+responseModel.getMsg(), Toast.LENGTH_SHORT).show();
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






    // For Permission

    /**** For permission with ManagePermissionClass*****/
    private boolean checkUserPermission() {

        //  int permissionCamera = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        int permissionWrite = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permissionStorage = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);

        ArrayList<String> listPermissionsNeeded = new ArrayList<>();

       /* if (permissionCamera != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }*/

        if (permissionWrite != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        if (permissionStorage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }


        if (!listPermissionsNeeded.isEmpty()) {
            managePermissions = new ManagePermission(this, listPermissionsNeeded, APP_PERMISSION_REQUEST_CODE);
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), APP_PERMISSION_REQUEST_CODE);
            return false;
        }
        return true;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case APP_PERMISSION_REQUEST_CODE: {
                boolean isPermissionsGranted = managePermissions.processPermissionsResult(requestCode, permissions, grantResults);
                if (isPermissionsGranted) {
                    pdfDownloadAndShare();
                } else {
                    // toast("Permissions denied.")
                    managePermissions.checkPermission();
                }
            }
        }
    }
}
