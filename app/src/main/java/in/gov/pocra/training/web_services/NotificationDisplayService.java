package in.gov.pocra.training.web_services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import in.co.appinventor.services_api.settings.AppSettings;
import in.gov.pocra.training.R;
import in.gov.pocra.training.activity.common.login.LoginActivity;
import in.gov.pocra.training.activity.common.notification.NotificationListActivity;
import in.gov.pocra.training.activity.common.splash.SplashActivity;
import in.gov.pocra.training.util.ApConstants;


public class NotificationDisplayService extends AppCompatActivity {

    private static final int NotifyId = 1;
    public static final String chanelId = "Chanel_Id";
    private static final String chanelName = "Chanel_Name";
    private static final String chanelDescription = "Chanel_Description";
    private static String token = "";
    private static PendingIntent pendingIntent;
    private static NotificationManager notificationManager;
    Context mContext;


    public NotificationDisplayService(){

    }

     static void displayService(Context mContext, String mMessageTitle, String mMessageContent) {
//        if (notificationManager == null) {
//            notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//            Log.d("message111", "notificationManager");
//        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel(chanelId,chanelName, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(chanelDescription);
            NotificationManager manager = mContext.getSystemService(NotificationManager.class);
            // NotificationManager manager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }

        showNotification(mContext,mMessageTitle,mMessageContent);
    }



    public static void showNotification(Context context, String title, String message){

        String loginType = AppSettings.getInstance().getValue(context, ApConstants.kLOGIN_TYPE,ApConstants.kLOGIN_TYPE);
        if(loginType.equalsIgnoreCase(ApConstants.kCOORD_TYPE) || loginType.equalsIgnoreCase(ApConstants.kPS_TYPE) ||loginType.equalsIgnoreCase(ApConstants.kPMU_TYPE) ||loginType.equalsIgnoreCase(ApConstants.kCA_TYPE) ) {
            // To open an Intent on click by Notification
            Intent intent = new Intent(context, NotificationListActivity.class);
            pendingIntent = PendingIntent.getActivity(context, NotifyId, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        }
        else {
            // To open an Intent on click by Notification
            Intent intent = new Intent(context, LoginActivity.class);
            pendingIntent = PendingIntent.getActivity(context, NotifyId, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, chanelId)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentText(title)
                .setContentText(message)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(context);
        managerCompat.notify(NotifyId,builder.build());
    }


    public static String getFCMToken(){

        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                if (task.isSuccessful()) {
                     token = task.getResult().getToken();
                }
            }
        });
        return token;
    }
}
