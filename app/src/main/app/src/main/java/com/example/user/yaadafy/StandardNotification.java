package com.example.user.yaadafy;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by USER on 2/23/2016.
 */
public class StandardNotification extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Intent resultIntent=new Intent(this, MainActivity.class);
        PendingIntent pIntent=PendingIntent.getActivity(this,0,resultIntent,0);
        Notification nBuilder= new Notification.Builder(this)
                .setContentTitle("You got a new Yaada!")
                .setContentIntent(pIntent)
                .setSmallIcon(R.mipmap.ic_launcher_2)
                .build();
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        nBuilder.flags |=Notification.FLAG_AUTO_CANCEL;

        notificationManager.notify(1,nBuilder);

        // If we get killed, after returning from here, restart
        return START_STICKY;
    }

}
