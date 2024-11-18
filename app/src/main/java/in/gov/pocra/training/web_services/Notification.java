package in.gov.pocra.training.web_services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

import in.co.appinventor.services_api.settings.AppSettings;
import in.gov.pocra.training.R;
import in.gov.pocra.training.activity.common.login.LoginActivity;
import in.gov.pocra.training.activity.common.notification.NotificationListActivity;
import in.gov.pocra.training.util.ApConstants;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by Belal on 12/8/2017.
 */

public class Notification {
    String channelId = "default_channel_id";
    private Context mCtx;
    private static Notification mInstance;
    PendingIntent pendingIntent;

    private Notification(Context context) {
        mCtx = context;
    }

    public static synchronized Notification getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new Notification(context);
        }
        return mInstance;
    }


    public void displayNotification(String title, String body) {


        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(mCtx,channelId)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(title)
                        .setContentText(body);


        String loginType = AppSettings.getInstance().getValue(mCtx,ApConstants.kLOGIN_TYPE,ApConstants.kLOGIN_TYPE);
        if(loginType.equalsIgnoreCase(ApConstants.kCOORD_TYPE) || loginType.equalsIgnoreCase(ApConstants.kPS_TYPE) ||loginType.equalsIgnoreCase(ApConstants.kPMU_TYPE) ||loginType.equalsIgnoreCase(ApConstants.kCA_TYPE) ) {
            Intent resultIntent = new Intent(mCtx, NotificationListActivity.class);
            resultIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            pendingIntent = PendingIntent.getActivity(mCtx, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        }
        else {
            Intent resultIntent = new Intent(mCtx, LoginActivity.class);
            resultIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            pendingIntent = PendingIntent.getActivity(mCtx, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        }

        mBuilder.setContentIntent(pendingIntent);

        NotificationManager mNotifyMgr =
                (NotificationManager) mCtx.getSystemService(NOTIFICATION_SERVICE);
        if (mNotifyMgr != null) {
            mNotifyMgr.notify(1, mBuilder.build());
        }
    }

}