package in.gov.pocra.training.web_services;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class FBaseMessagingService extends FirebaseMessagingService {
    String channelId = "default_channel_id";
    String channelDescription = "Default Channel";
    private NotificationManager notificationManager;
    Context mcontext;
    private String s;

    String token = FirebaseInstanceId.getInstance().getToken();

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d("firebaseMassage", remoteMessage.toString());
        if (remoteMessage.getNotification() != null) {
            String title = remoteMessage.getNotification().getTitle();
            String body = remoteMessage.getNotification().getBody();
            sendNotification(title, body );


        }
    }



    private void sendNotification(String title, String body) {
        Log.d("message22", body);

        if (notificationManager == null) {
            notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            Log.d("message111", "notificationManager");
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.d("message2222", "SDK_INT");
            NotificationChannel notificationChannel = notificationManager.getNotificationChannel(channelId);
            if (notificationChannel == null) {
                Log.d("message333", "notificationChannel");
                int importance = NotificationManager.IMPORTANCE_HIGH; //Set the importance level
                notificationChannel = new NotificationChannel(channelId, channelDescription, importance);
                notificationChannel.setLightColor(Color.GREEN); //Set if it is necesssary
                notificationChannel.enableVibration(true); //Set if it is necesssary
                notificationManager.createNotificationChannel(notificationChannel);


            }
            Notification.getInstance(this).displayNotification(title, body );

        }


    }
    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
    }
}

