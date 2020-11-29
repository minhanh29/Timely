package com.example.timely.settings;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.timely.DatabaseHelper;
import com.example.timely.MainActivity;
import com.example.timely.R;
import com.example.timely.courses.StudyTime;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

import static com.example.timely.settings.App.CHANNEL_ID;

public class AlarmService extends Service {

    public static String TIME_INFO = "time_infor";

    private CountDownTimer timer;
    private ArrayList<StudyTime> studyTimes;
    private ArrayList<Calendar> calendar;

    @Override
    public void onCreate() {
        super.onCreate();

        DatabaseHelper db = new DatabaseHelper(getApplicationContext());
        studyTimes = db.getAllStudyTime();
        Collections.sort(studyTimes);
        calendar = new ArrayList<>();
        for (int i = 0; i < studyTimes.size(); i++)
        {
            Calendar cal = Calendar.getInstance();
            int offset = studyTimes.get(i).getDay() + 2 - cal.get(Calendar.DAY_OF_WEEK);
            cal.add(Calendar.DAY_OF_MONTH, offset);
            calendar.add(cal);
        }

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // get information from activity
        boolean isStudy = intent.getBooleanExtra(NotificationSettingsActivity.STUDYSWITCH, false);
        boolean isTest = intent.getBooleanExtra(NotificationSettingsActivity.TESTSWITCH, false);
        boolean isSleepAwake = intent.getBooleanExtra(NotificationSettingsActivity.SLEEPWAKESWITCH, false);

        int studyBefore = intent.getIntExtra(NotificationSettingsActivity.STUDY_BEFORE, 0);
        int testBefore = intent.getIntExtra(NotificationSettingsActivity.TEST_BEFORE, 0);

        // create a list for test time
        ArrayList<Calendar> testCal = new ArrayList<>();
        if (isTest)
        {
            for (int i = 0 ; i < studyTimes.size(); i++)
            {
                if (studyTimes.get(i).isHasTest())
                {
                    Calendar cal = Calendar.getInstance();
                    int offset = studyTimes.get(i).getDay() + 2 - cal.get(Calendar.DAY_OF_WEEK);
                    cal.add(Calendar.DAY_OF_MONTH, offset);
                    cal.add(Calendar.DAY_OF_MONTH, -testBefore);
                    testCal.add(cal);
                }
            }

        }

        if (!isStudy)
            calendar.clear();
        else
        {
            for (int i = 0; i < calendar.size(); i++)
                calendar.get(i).add(Calendar.MINUTE, -studyBefore);
        }

        // add test to calendar
        for (int i= 0; i < testCal.size(); i++)
            calendar.add(testCal.get(i));

        // sort the date
        Collections.sort(calendar);


        // SET ALARM
        Calendar time = getNextTime();
        int day = time.get(Calendar.DAY_OF_MONTH);
        int hour = time.get(Calendar.HOUR_OF_DAY);
        int min = time.get(Calendar.MINUTE);
        Log.i("time", "Alarm day:" + day + " hour: " + hour + " minute: " + min);

        long countTime = time.getTimeInMillis() - Calendar.getInstance().getTimeInMillis();
        day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        min = Calendar.getInstance().get(Calendar.MINUTE);
        Log.i("time", "Today:" + day + " hour: " + hour + " minute: " + min);

        // count down the time
        // This is a separate thread, so the codes after this timer will still run
        timer = new CountDownTimer(countTime, 1000) {

            public void onTick(long millisUntilFinished) {
                Log.i("alarm", "count down (milliseconds): " + millisUntilFinished);
            }

            public void onFinish() {
                Intent intent2 = new Intent(getApplicationContext(), Ringtone.class);
                startService(intent2);
                Log.i("alarm", "Alarm finish");
            }
        };
        timer.start();

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
        timer.cancel();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public Calendar getNextTime()
    {
        Calendar minCal = Calendar.getInstance();
        boolean change = false;
        for (int i = 0; i < calendar.size(); i++)
        {
            if (minCal.compareTo(calendar.get(i)) < 0)
            {
                minCal = calendar.get(i);
                change = true;
                break;
            }
        }


        // this is the end of the week
        // find the min time of next week
        if (!change)
        {
            minCal = calendar.get(0);
            minCal.add(Calendar.DAY_OF_MONTH, 7);
        }

        Log.i("time", "Day: " + minCal.get(Calendar.DAY_OF_MONTH));

        return minCal;
    }

}
