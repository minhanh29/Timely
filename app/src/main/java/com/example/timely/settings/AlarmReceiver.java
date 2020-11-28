package com.example.timely.settings;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.AlarmClock;
import android.util.Log;

import com.example.timely.DatabaseHelper;
import com.example.timely.courses.StudyTime;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("Timely","You've got a notification !!!!");
//        Intent myIntent = new Intent(context, Ringtone.class);
//        context.startService(myIntent);
        Intent mIntent = new Intent(context, NotificationSettingsActivity.class);
        mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mIntent.putExtra(NotificationSettingsActivity.UPDATE_ALARM, true);
        context.startActivity(mIntent);
        //startNextAlarm(context);
    }

    public void startNextAlarm(Context context)
    {
        int[] time = getNextTime(context);
        Log.i("time", "Day:" + time[0] + "hour: " + time[1] + "minute: " + time[2]);

        Intent intent2 = new Intent(context, AlarmReceiver.class);
        intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pi =PendingIntent.getBroadcast(context, 1,intent2, 0);

        Calendar c = Calendar.getInstance();
        //c.set(Calendar.DAY_OF_WEEK, time[0]);
        c.set(Calendar.HOUR_OF_DAY, time[1]);
        c.set(Calendar.MINUTE, time[2]);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pi);

        Intent intent = new Intent(AlarmClock.ACTION_SET_ALARM);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ArrayList<Integer> days = new ArrayList<>();
        days.add(time[0]);
        intent.putExtra(AlarmClock.EXTRA_DAYS, days);
        intent.putExtra(AlarmClock.EXTRA_HOUR, time[1]);
        intent.putExtra(AlarmClock.EXTRA_MINUTES, time[2]);

        context.startActivity(intent);

    }


    public int[] getNextTime(Context context)
    {
        int[] time = new int[3];
        DatabaseHelper db = new DatabaseHelper(context);
        ArrayList<StudyTime> studyTimes = db.getAllStudyTime();
        Collections.sort(studyTimes);

        Calendar calendar = Calendar.getInstance();
        StudyTime minTime = new StudyTime(calendar.get(Calendar.DAY_OF_WEEK)-2,
                calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), 0, "");
        boolean change = false;
        for (int i = 0; i < studyTimes.size(); i++)
        {
            if (minTime.compareTo(studyTimes.get(i)) < 0)
            {
                minTime = studyTimes.get(i);
                change = true;
                break;
            }
        }

        // this is the end of the week
        // find the min time of next week
        if (!change)
        {
            minTime = studyTimes.get(0);
        }

        time[0] = minTime.getDay()+2;
        time[1] = minTime.getHour();
        time[2] = minTime.getMinute();

        return time;
    }
}
