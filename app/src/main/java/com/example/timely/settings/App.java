package com.example.timely.settings;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

/*
 * This class is used to register the alarm channel
 * to run in foreground
 */

public class App extends Application {
    public static String CHANNEL_ID = "alarmService";
    public static String CHANNEL_TIMELY_ID="channelTimely";

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
    }

    private void createNotificationChannel()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Alarm Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            NotificationChannel timelyChannel = new NotificationChannel(
                    CHANNEL_TIMELY_ID,
                    "Alarm Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
            manager.createNotificationChannel(timelyChannel);
        }
    }
}
