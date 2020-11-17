package com.example.timely;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.security.Provider;

public class Ringtone extends Service {
    MediaPlayer mediaPlayer;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mediaPlayer = MediaPlayer.create(this,R.raw.alarm_sound);
        mediaPlayer.start();
        Log.e("Hi this music is running", "Lalala");
        return START_NOT_STICKY;
    }
}
