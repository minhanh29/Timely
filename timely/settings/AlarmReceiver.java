package com.example.timely.settings;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("Hi","This receiver is working !");
        Intent myIntent = new Intent(context, Ringtone.class);
        context.startService(myIntent);
    }
}
