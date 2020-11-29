package com.example.timely.settings;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.provider.AlarmClock;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.timely.DatabaseHelper;
import com.example.timely.R;
import com.example.timely.courses.StudyTime;

import java.security.Provider;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

public class Ringtone extends Service {
    MediaPlayer mediaPlayer;
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

        // set the next alarm
        Intent mIntent = new Intent(this, AlarmService.class);
        startService(mIntent);

        // stop this ringing service
        stopSelf();

        return START_NOT_STICKY;
    }
}
