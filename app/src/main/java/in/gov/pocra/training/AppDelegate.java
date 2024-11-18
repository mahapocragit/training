package in.gov.pocra.training;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.HashMap;
import java.util.Map;

import in.co.appinventor.services_api.debug.DebugLog;
import in.co.appinventor.services_api.settings.AppSettings;
import in.gov.pocra.training.web_services.ForceUpdateChecker;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.content.ContentValues.TAG;
import static in.gov.pocra.training.web_services.APIServices.BASE_URL;

public class AppDelegate extends Application {

    private static AppDelegate mInstance;

    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;
//        Bitly.initialize(this, "f7846b002a59ab5511f733de477f658ae0c86237");
        FirebaseApp.initializeApp(this);
        AppSettings.getInstance().initAppSettings("pocra_training_shared_preference");
        DebugLog.getInstance().initLoggingEnabled(true);

        final FirebaseRemoteConfig firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        String PACKAGE_NAME = getPackageName();
        String versionName = BuildConfig.VERSION_NAME;
        // set in-app defaults
        Map<String, Object> remoteConfigDefaults = new HashMap();
        remoteConfigDefaults.put(ForceUpdateChecker.KEY_UPDATE_REQUIRED, false);
        remoteConfigDefaults.put(ForceUpdateChecker.KEY_CURRENT_VERSION, versionName);
        remoteConfigDefaults.put(ForceUpdateChecker.KEY_UPDATE_URL, "https://play.google.com/store/apps/details?id="+ PACKAGE_NAME);
        firebaseRemoteConfig.setDefaultsAsync(remoteConfigDefaults);
        firebaseRemoteConfig.fetch(60) // fetch every minutes
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "remote config is fetched.");
                            Log.d("1213234234",firebaseRemoteConfig.getString("force_update_current_version"));
                            firebaseRemoteConfig.activate();

                        }
                    }
                });
    }
    Gson gson = new GsonBuilder()
            .setLenient()
            .create();

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BASE_URL)

            .addConverterFactory(GsonConverterFactory.create(gson))
            .build();

    public static synchronized AppDelegate getInstance() {
        return mInstance;
    }

}
