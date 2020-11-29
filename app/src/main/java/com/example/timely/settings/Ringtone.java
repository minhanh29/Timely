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
        Intent mIntent = new Intent(this, AlarmService.class);
        startService(mIntent);

        stopSelf();

        return START_NOT_STICKY;
    }


    public void startNextAlarm()
    {
        int[] time = getNextTime();
        Log.i("time", "Day:" + time[0] + "hour: " + time[1] + "minute: " + time[2]);

        Intent intent2 = new Intent(getApplicationContext(), AlarmReceiver.class);
        intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pi =PendingIntent.getBroadcast(getApplicationContext(), 1,intent2, PendingIntent.FLAG_CANCEL_CURRENT);

        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_WEEK, time[0]);
        c.set(Calendar.HOUR, time[1]);
        c.set(Calendar.MINUTE, time[2]);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pi);

        Intent intent = new Intent(AlarmClock.ACTION_SET_ALARM);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ArrayList<Integer> days = new ArrayList<>();
        days.add(time[0]);
        intent.putExtra(AlarmClock.EXTRA_DAYS, days);
        intent.putExtra(AlarmClock.EXTRA_HOUR, time[1]);
        intent.putExtra(AlarmClock.EXTRA_MINUTES, time[2]);

        startActivity(intent);

    }


    public int[] getNextTime()
    {
        int[] time = new int[3];
        DatabaseHelper db = new DatabaseHelper(getApplicationContext());
        ArrayList<StudyTime> studyTimes = db.getAllStudyTime();
        Collections.sort(studyTimes);

        Calendar calendar = Calendar.getInstance();
        StudyTime minTime = new StudyTime(calendar.get(Calendar.DAY_OF_WEEK)-2,
                calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE)+1, 0, "");
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
