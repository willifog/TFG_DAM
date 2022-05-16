package com.example.mistareas.channel;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Color;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.example.mistareas.R;

public class NotificationHelper extends ContextWrapper {

    private static final String CHANNEL_ID = "com.example.mistareas";
    private static final String CHANNEL_NAME= "Mis Tareas";

    private NotificationManager manager;

    public NotificationHelper(Context context){
        super(context);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            createChannels();
        }
    }

    private void createChannels(){
        NotificationChannel notificationChannel = new NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
        );
        notificationChannel.enableLights(true);
        notificationChannel.enableVibration(true);
        notificationChannel.setLightColor(Color.BLUE);
        notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        getManager().createNotificationChannel(notificationChannel);
    }

    public NotificationManager getManager(){
        if(manager == null){
            manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return manager;
    }

    public NotificationCompat.Builder getNotification(String title, String body){
        return new NotificationCompat.Builder(
                getApplicationContext(),
                CHANNEL_ID
        ).setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setColor(Color.BLUE)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(body).setBigContentTitle(title));
    }

}
