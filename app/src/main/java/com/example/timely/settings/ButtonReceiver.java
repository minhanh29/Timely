package com.example.timely.settings;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.util.Log;

import com.example.timely.R;

import java.io.IOException;

public class ButtonReceiver extends BroadcastReceiver {
    MediaPlayer mediaPlayer;
    @Override
    public void onReceive(Context context, Intent intent) {
        int notificationId = intent.getIntExtra("notificationId", 0);

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(notificationId);
        int mp3 = intent.getIntExtra("selectedMp3",R.raw.alarm_sound);
        Log.e("timely alarm","this cancel the notification");
    }
}
