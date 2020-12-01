package com.example.timely.settings;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.example.timely.R;
import static com.example.timely.settings.App.CHANNEL_TIMELY_ID;

public class Ringtone extends Service {
    MediaPlayer mediaPlayer;
    private NotificationManager notificationManager;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        mediaPlayer = MediaPlayer.create(this, R.raw.alarm_sound);
        mediaPlayer.start();
        Log.e("Hi this music is running", "Lalala");

        //Create an Intent for the BroadcastReceiver
        Intent buttonIntent = new Intent(this, ButtonReceiver.class);
        buttonIntent.putExtra("notificationId",2);
        buttonIntent.putExtra("selected_sound", R.raw.alarm_sound);

        //Create the PendingIntent
        PendingIntent btPendingIntent = PendingIntent.getBroadcast(this, 0, buttonIntent,0);

        // start a notification
        String message = intent.getStringExtra(AlarmService.ALARM_MESSAGE);
        notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_TIMELY_ID)
                .setSmallIcon(R.drawable.ic_baseline_alarm_24)
                .setContentTitle("Timely Alarm")
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .addAction(R.drawable.ic_baseline_alarm_off_24,"Dismiss Alarm", btPendingIntent)
                .build();
        Log.e("Timely", "This starts the nortifications");
        startForeground(2,notification);


        // set the next alarm
        Intent mIntent = new Intent(this, AlarmService.class);
        startService(mIntent);


        // stop this ringing service
        new CountDownTimer(10000, 1000) {

            public void onTick(long millisUntilFinished) {

            }

            public void onFinish() {
                stopSelf();
            }
        }.start();


        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if(mediaPlayer!=null)
        {
            mediaPlayer.stop();
        }
        stopSelf();
    }
}
