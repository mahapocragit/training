package in.gov.pocra.training.activity.common.downloadOrViewPdf;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.AppCompatTextView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

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
import in.co.appinventor.services_api.util.Utility;
import in.co.appinventor.services_api.widget.UIToastMessage;
import in.gov.pocra.training.BuildConfig;
import in.gov.pocra.training.R;
import in.gov.pocra.training.model.online.ResponseModel;
import in.gov.pocra.training.util.ApConstants;
import in.gov.pocra.training.util.DownloadTask;
import in.gov.pocra.training.util.ManagePermission;
import in.gov.pocra.training.web_services.APIRequest;
import in.gov.pocra.training.web_services.APIServices;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Retrofit;

public class ViewOrDownloadPdfActivity extends AppCompatActivity implements ApiCallbackCode {

    private ImageView homeBack;
    private String userID;
    private String rollId;
    private Button viewButton;
    private Button shareButton;
    private File file;
    private String fileNameWithExt = "";
    private String uri;

    // For Permission
    private static final String IMAGE_DIRECTORY = "/PoCRA_TRAINING";
    private ManagePermission managePermissions;
    private static final int APP_PERMISSION_REQUEST_CODE = 111;
    private String schId;
    private String schDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_or_download_pdf);

        /* ** For actionbar title in center ***/
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.attendance_actionbar_layout);
        AppCompatTextView actionTitleTextView = (AppCompatTextView) getSupportActionBar().getCustomView().findViewById(R.id.actionTitleTextView);
        homeBack = (ImageView) getSupportActionBar().getCustomView().findViewById(R.id.backImageView);
        homeBack.setVisibility(View.VISIBLE);
        String schDate = getIntent().getStringExtra("date");
        actionTitleTextView.setText(schDate + " Report");

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
            rollId = rId;
        }

        if (!uId.equalsIgnoreCase("kUSER_ID")) {
            userID = uId;
        }

        viewButton = (Button) findViewById(R.id.viewButton);
        shareButton = (Button) findViewById(R.id.shareButton);
        shareButton.setVisibility(View.GONE);


        schId = getIntent().getStringExtra("schId");
        schDate = getIntent().getStringExtra("date");

        if (Utility.checkConnection(this)) {
            getPdfData(schId, schDate);
        }


    }


    private void getPdfData(String schId, String date) {

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
        api.postRequest(responseCall, this, 1);
    }


    private void defaultConfiguration() {

        viewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // viewButtonAction();

                if (Build.VERSION.SDK_INT < 19) {
                    if (!Utility.checkConnection(ViewOrDownloadPdfActivity.this)) {
                        offlinePdfView();
                    }else {
                        viewButtonAction();
                    }

                } else {
                    if (checkUserPermission()) {
                        if (!Utility.checkConnection(ViewOrDownloadPdfActivity.this)) {
                            offlinePdfView();
                        }else {
                            viewButtonAction();
                        }
                    }
                }
            }
        });

    }


    @Override
    protected void onResume() {
        super.onResume();

        if (!Utility.checkConnection(this)) {
            offlinePdfView();
        }
    }

    private void offlinePdfView() {

        final File dir = new File(Environment.getExternalStorageDirectory() + IMAGE_DIRECTORY);

        if (dir.exists()) {

            fileNameWithExt = "TAR_" + schId + "_" + schDate;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    file = new File(dir + fileNameWithExt);
                    if (file.exists()) {
                        view(file);
                    } else {
                        Toast.makeText(ViewOrDownloadPdfActivity.this, "PDF not found please check it online", Toast.LENGTH_SHORT).show();
                    }
                }
            }, 1000);

        } else {
            Toast.makeText(this, "PDF not found please check it online", Toast.LENGTH_SHORT).show();
        }

    }


    private void viewButtonAction() {

        final File dir = new File(Environment.getExternalStorageDirectory() + IMAGE_DIRECTORY);
        if (!dir.exists()) {
            dir.mkdirs();
        }


        if (AppUtility.getInstance().isConnected(this)) {
            new DownloadTask(this, uri, dir);
        }


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                file = new File(dir + fileNameWithExt);
                if (file.exists()) {
                    view(file);
                }
            }
        }, 1000);

    }



    public void view(File file) {

        // Uri path = Uri.fromFile(file);
        Uri path = null;

        if ((Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT)) {
            path = FileProvider.getUriForFile(getApplicationContext(), BuildConfig.APPLICATION_ID + ".android.fileprovider", file);
        } else {
            path = Uri.fromFile(file);
        }

        Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
        pdfIntent.setDataAndType(path, "application/pdf");
        pdfIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        pdfIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        pdfIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

        try {
            startActivity(pdfIntent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "No Application available to view PDF", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onResponse(JSONObject jsonObject, int i) {

        try {

            if (jsonObject != null) {

                // Schedule Response for CA/Trainer
                if (i == 1) {
                    ResponseModel responseModel = new ResponseModel(jsonObject);
                    if (responseModel.isStatus()) {

                        JSONObject pdfJSON = jsonObject.getJSONObject("data");
                        fileNameWithExt = "/" + pdfJSON.getString("file_name") + ".pdf";
                        uri = pdfJSON.getString("file_path");

                        if (Build.VERSION.SDK_INT < 19) {
                            viewButtonAction();
                        } else {
                            if (checkUserPermission()) {
                                viewButtonAction();
                            }
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
                    viewButtonAction();
                } else {
                    // toast("Permissions denied.")
                    managePermissions.checkPermission();
                }
            }
        }
    }
}
