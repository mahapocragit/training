package in.gov.pocra.training.activity.common.about_us;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.AppCompatTextView;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import in.gov.pocra.training.R;

public class AboutUsActivity extends AppCompatActivity {

    private ImageView homeBack;
    private WebView wvAboutUs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        /* ** For actionbar title in center ***/
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.attendance_actionbar_layout);
        AppCompatTextView actionTitleTextView = (AppCompatTextView) getSupportActionBar().getCustomView().findViewById(R.id.actionTitleTextView);
        homeBack = (ImageView) getSupportActionBar().getCustomView().findViewById(R.id.backImageView);
        homeBack.setVisibility(View.VISIBLE);
        actionTitleTextView.setText(getResources().getString(R.string.title_about_us));

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
        wvAboutUs = (WebView) findViewById(R.id.aboutUsWebView);
    }

    private void defaultConfiguration() {

        WebSettings webSetting = wvAboutUs.getSettings();
        // webSetting.setBuiltInZoomControls(true);
        //webSetting.setJavaScriptEnabled(true);
        wvAboutUs.setWebViewClient(new WebViewClient());
        wvAboutUs.loadUrl("file:///android_asset/about_us.html");


    }
}
