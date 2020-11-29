package com.example.timely.settings;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.example.timely.AlarmActivity;
import com.example.timely.DatabaseHelper;
import com.example.timely.MainActivity;
import com.example.timely.R;
import com.example.timely.courses.StudyTime;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

import static com.example.timely.settings.App.CHANNEL_ID;

public class AlarmService extends Service {

    public static String TIME_INFO = "time_infor";

    private CounterClass timer;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // set alarm
        int[] time = getNextTime();
        Log.i("time", "Day:" + time[0] + "hour: " + time[1] + "minute: " + time[2]);

        // convert to millis
        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_WEEK, time[0]);
        c.set(Calendar.HOUR, time[1]);
        c.set(Calendar.MINUTE, time[2]);

        long countTime = c.getTimeInMillis() - Calendar.getInstance().getTimeInMillis();

        timer = new CounterClass(countTime, 1000);
        timer.start();
        Log.i("alarm", "Minh Anh here");
        Intent intent2 = new Intent(this, Ringtone.class);
        startService(intent2);
//
//        Intent intent2 = new Intent(getApplicationContext(), AlarmReceiver.class);
//        intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        PendingIntent pi =PendingIntent.getBroadcast(getApplicationContext(), 1,intent2, PendingIntent.FLAG_CANCEL_CURRENT);
//
//
//        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
//        alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pi);

        Intent mIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, mIntent, 0);
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Alarm study")
                .setContentText("Time to study")
                .setSmallIcon(R.drawable.ic_course)
                .setContentIntent(pendingIntent)
                .build();

        startForeground(1, notification);

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
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

    public class CounterClass extends CountDownTimer {

        public CounterClass(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            long millis = millisUntilFinished;
            String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
                    TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                    TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
            System.out.println(hms);
            Intent timerInfoIntent = new Intent(TIME_INFO);
            timerInfoIntent.putExtra("VALUE", hms);
            LocalBroadcastManager.getInstance(AlarmService.this).sendBroadcast(timerInfoIntent);
        }

        @Override
        public void onFinish() {
            Intent timerInfoIntent = new Intent(TIME_INFO);
            timerInfoIntent.putExtra("VALUE", "Completed");
            LocalBroadcastManager.getInstance(AlarmService.this).sendBroadcast(timerInfoIntent);
        }
    }
}
