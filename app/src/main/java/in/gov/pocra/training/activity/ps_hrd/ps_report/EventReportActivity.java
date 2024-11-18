package in.gov.pocra.training.activity.ps_hrd.ps_report;

import android.Manifest;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;

import android.util.Log;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import in.co.appinventor.services_api.api.AppinventorApi;
import in.co.appinventor.services_api.app_util.AppUtility;
import in.co.appinventor.services_api.debug.DebugLog;
import in.co.appinventor.services_api.listener.ApiCallbackCode;
import in.co.appinventor.services_api.settings.AppSettings;
import in.co.appinventor.services_api.widget.UIToastMessage;
import in.gov.pocra.training.R;
import in.gov.pocra.training.model.online.ResponseModel;
import in.gov.pocra.training.util.ApConstants;
import in.gov.pocra.training.util.DownloadTask;
import in.gov.pocra.training.util.ManagePermission;
import in.gov.pocra.training.util.PdfWebViewClient;
import in.gov.pocra.training.web_services.APIRequest;
import in.gov.pocra.training.web_services.APIServices;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Retrofit;

public class EventReportActivity extends AppCompatActivity implements ApiCallbackCode {

    private ImageView homeBack;

    private ManagePermission managePermissions;
    private static final String REPORT_DIRECTORY = "/PoCRA_TRAINING/PoCRA_TRAINING_REPORT";
    private static final int APP_PERMISSION_REQUEST_CODE = 111;

    private WebView webView;
    private ProgressDialog pDialog;
    String GoogleDocs = "http://docs.google.com/gview?embedded=true&url=";
    private String pdfUrl = "";
    private File reportFile = null;

   // private ImageView downloadIView;
    private PdfWebViewClient pdfWebViewClient;
    // private PdfWebViewClient_v1 pdfWebViewClient;
    private String roleId;
    private String userID;
    private String closerDate = ""; // Value taken for testing
    private String schId;

    private String buttonAction = "confirm";
    private String collageImgUrl;
    private String reportType;

TextView viewpdf,downloadIView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_report);


        /* ** For actionbar title in center ***/
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.attendance_actionbar_layout);
        AppCompatTextView actionTitleTextView = (AppCompatTextView) getSupportActionBar().getCustomView().findViewById(R.id.actionTitleTextView);
        homeBack = (ImageView) getSupportActionBar().getCustomView().findViewById(R.id.backImageView);
        // uploadImageIView = (ImageView) getSupportActionBar().getCustomView().findViewById(R.id.addPersonImageView);
        homeBack.setVisibility(View.VISIBLE);
        // uploadImageIView.setImageDrawable(getResources().getDrawable(R.drawable.ic_menu_camera));
        // uploadImageIView.setVisibility(View.VISIBLE);
        actionTitleTextView.setText("Event Report");


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
        downloadIView = (TextView) findViewById(R.id.downloadIView);
        webView = (WebView) findViewById(R.id.reportWView);
        viewpdf = (TextView) findViewById(R.id.viewpdf);
        viewpdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(pdfUrl));
                startActivity(browserIntent);
            }
        });

    }


    private void defaultConfiguration() {

        downloadIView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT < 19) {
                    DownloadPDF();
                } else {
                    if (checkUserPermission()) {
                        DownloadPDF();
                    }
                }

            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void DownloadPDF() {
        //String url= "https://docs.google.com/gview?embedded=true&url=https://url.pdf";
       // String url= "https://docs.google.com/gview?embedded=true&url=";
       // File reportDirectory = new File(Environment.getExternalStorageDirectory() + REPORT_DIRECTORY);
        File reportDirectory= new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), REPORT_DIRECTORY);
        if (!reportDirectory.exists()) {
            reportDirectory.mkdirs();
        }

       // registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        Log.d("EventReportActivity=","Download PDF Url ="+pdfUrl);
        new DownloadTask(this,pdfUrl,reportDirectory);
    }
    BroadcastReceiver onComplete=new BroadcastReceiver() {
        public void onReceive(Context ctxt, Intent intent) {
            // your code
        }
    };
    @Override
    protected void onResume() {
        super.onResume();

        // To get event summary
        String schId = getIntent().getStringExtra("schId");
        String closerDate = getIntent().getStringExtra("closerDate");
        String reportType = getIntent().getStringExtra("type");

        if (reportType.equalsIgnoreCase("day")){
            getDayPdfData(schId,closerDate);
        }else if (reportType.equalsIgnoreCase("cons")){
            getEventSummery(schId,closerDate);
        }

    }
    // TO load PDF in WebView
    private void loadPdfByUrl(String pdfUrl) {

        pDialog = new ProgressDialog(this);
        pDialog.setTitle("Loading");
        pDialog.setMessage("Please wait...");
        pDialog.setIndeterminate(false);
        //pDialog = ProgressDialog.show(this, "Loading", "Please wait...", true);
        pDialog.setCancelable(false);

       // webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.setInitialScale(1);
        webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setSupportZoom(true);


        // pDialog.show();
        webView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                pDialog.show();
                return true;
            }
            @Override
            public void onPageFinished(WebView view, final String url) {
                //pDialog.dismiss();
                if(view.getContentHeight() == 0){
                    view.reload();
                } else {
                    pDialog.dismiss();
                }
                super.onPageFinished(view, url);
            }
        });

        webView.setWebChromeClient(new WebChromeClient(){
            public void onProgressChanged(WebView view, int progress) {
                if (progress < 100) {
                    pDialog.show();
                }
                if (progress >= 100) {
                    pDialog.dismiss();
                }
            }
        });

        webView.loadUrl(GoogleDocs + pdfUrl);

        webView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
            }
        });

    }



    private void getEventSummery(String schId, String closerDate) {

        try {

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("schedule_id", schId);
            jsonObject.put("attendance_date", closerDate);
            jsonObject.put("api_key", ApConstants.kAUTHORITY_KEY);

            RequestBody requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString());
            // AppinventorApi api = new AppinventorApi(this, APIServices.BASE_URL, "", ApConstants.kMSG, true);
            AppinventorApi api = new AppinventorApi(this, APIServices.BASE_URL, "", "Please Wait..", true);
            Retrofit retrofit = api.getRetrofitInstance();
            APIRequest apiRequest = retrofit.create(APIRequest.class);
            // Call<JsonObject> responseCall = apiRequest.getConsolidatedReportRequest(requestBody);
            Call<JsonObject> responseCall = apiRequest.getConsolidatedReportWithCollageRequest(requestBody);

            DebugLog.getInstance().d("event_summery_with_image_param=" + responseCall.request().toString());
            DebugLog.getInstance().d("event_summery_with_image_param=" + AppUtility.getInstance().bodyToString(responseCall.request()));
            api.postRequest(responseCall, this, 1);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }



    private void getDayPdfData(String schId, String date) {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("schedule_id", schId);
            jsonObject.put("attendance_date", date);
            jsonObject.put("api_key", ApConstants.kAUTHORITY_KEY);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString());
        AppinventorApi api = new AppinventorApi(this, APIServices.BASE_URL, "", ApConstants.kMSG, true);
        Retrofit retrofit = api.getRetrofitInstance();
        APIRequest apiRequest = retrofit.create(APIRequest.class);
        Call<JsonObject> responseCall = apiRequest.getAttendReportRequest(requestBody);

        DebugLog.getInstance().d("get_report_pdf_param=" + responseCall.request().toString());
        DebugLog.getInstance().d("get_report_pdf_param=" + AppUtility.getInstance().bodyToString(responseCall.request()));
        api.postRequest(responseCall, this, 2);
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

                        pdfUrl = dataJSON.getString("file_path");
                        if (AppUtility.getInstance().checkURL(pdfUrl)) {
                            if (!pdfUrl.equalsIgnoreCase("")){
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {

                                      viewpdf.setVisibility(View.VISIBLE);
                                        downloadIView.setVisibility(View.VISIBLE);
                                    }

                                },1000);

                            }


                        } else {
                            UIToastMessage.show(this, "URL not found  or Please check pdf url");
                        }

                    } else {
                        UIToastMessage.show(this, responseModel.getMsg());
                    }
                }


                if (i == 2) {
                    ResponseModel responseModel = new ResponseModel(jsonObject);
                    if (responseModel.isStatus()) {

                        JSONObject pdfJSON = jsonObject.getJSONObject("data");
                        String fileNameWithExt = "/" + pdfJSON.getString("file_name") + ".pdf";
                        pdfUrl = pdfJSON.getString("file_path");
                        if (AppUtility.getInstance().checkURL(pdfUrl)) {
                            viewpdf.setVisibility(View.VISIBLE);
                            downloadIView.setVisibility(View.VISIBLE);
                        } else {
                            UIToastMessage.show(this, "pdf not found");
                        }

                    } else {
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



    /**** For permission with ManagePermissionClass*****/
    private boolean checkUserPermission() {

        int permissionWrite = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permissionStorage = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);

        ArrayList<String> listPermissionsNeeded = new ArrayList<>();

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
                    DownloadPDF();
                } else {
                    // toast("Permissions denied.")
                    managePermissions.checkPermission();
                }
            }
        }
    }
}
