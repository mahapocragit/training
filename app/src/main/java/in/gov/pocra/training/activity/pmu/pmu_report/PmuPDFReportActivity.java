package in.gov.pocra.training.activity.pmu.pmu_report;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;

import java.io.File;
import java.util.ArrayList;

import in.gov.pocra.training.R;
import in.gov.pocra.training.util.DownloadTask;
import in.gov.pocra.training.util.ManagePermission;
import in.gov.pocra.training.util.PdfWebViewClient;

public class PmuPDFReportActivity extends AppCompatActivity {

    private ImageView homeBack;
    private WebView webView;
    private PdfWebViewClient pdfWebViewClient;
    private String pdfurl;
    private ImageView downloadIView;
    private ManagePermission managePermissions;
    private static final int APP_PERMISSION_REQUEST_CODE = 111;
    private static final String REPORT_DIRECTORY = "/PoCRA_TRAINING/PoCRA_TRAINING_REPORT";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
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
        actionTitleTextView.setText("PDF Report");
        initialization();

    }



    private void initialization() {
        pdfurl = getIntent().getStringExtra("pdfurl");

        if (!pdfurl.equalsIgnoreCase("")){
           new Handler().postDelayed(new Runnable() {
               @Override
               public void run() {
                   pdfWebViewClient.loadPdfUrl(pdfurl);
               }
           },2000);
        }


        homeBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // To get User Id and Roll Id
        webView = (WebView) findViewById(R.id.reportWView);
        downloadIView = (ImageView)findViewById(R.id.downloadIView);

        pdfWebViewClient = new PdfWebViewClient(this, webView);


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

    private void DownloadPDF() {

        File reportDirectory = new File(Environment.getExternalStorageDirectory() + REPORT_DIRECTORY);
        if (!reportDirectory.exists()) {
            reportDirectory.mkdirs();
        }
        new DownloadTask(this,pdfurl,reportDirectory);
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
}

