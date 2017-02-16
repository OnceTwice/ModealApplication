package com.ff.modealapplication.app.ui.message;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.ff.modealapplication.R;
import com.ff.modealapplication.app.ui.main.MainActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by BIT on 2017-02-06.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) { // 메시지가 날라올때

        Log.d(TAG, "From : " + remoteMessage.getFrom());

        // Check if the message contains data
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data : " + remoteMessage.getData());
        }

        // Check if the message contains notification
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message title : " + remoteMessage.getNotification().getTitle());
            Log.d(TAG, "Message body : " + remoteMessage.getNotification().getBody());
            sendNotification(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody());

            // 알림 올 경우 화면꺼짐상태에서 화면 켜기 (deprecated 된거라서 간당간당함...)
            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            PowerManager.WakeLock wakelock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "TAG");
            wakelock.acquire(3000);
        }
    }

    private void sendNotification(String title, String body) { // 메세지 날라온거 띄울때

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        getApplicationContext().startActivity(intent);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, (int) (Math.random() * 100000), intent, PendingIntent.FLAG_ONE_SHOT);

        // Set sound of notification
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.logo1)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify((int) (Math.random() * 100000), notificationBuilder.build());
    }
}
