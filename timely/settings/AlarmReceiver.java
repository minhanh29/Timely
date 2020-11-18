package com.example.timely.settings;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Calendar;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("Timely","You've got a notification !!!!");
        Intent myIntent = new Intent(context, Ringtone.class);
        context.startService(myIntent);
    }
}
