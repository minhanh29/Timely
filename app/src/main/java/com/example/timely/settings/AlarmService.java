package com.example.timely.settings;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
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
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import static com.example.timely.settings.App.CHANNEL_ID;

public class AlarmService extends Service {

    public static String ALARM_MESSAGE = "ALARM_MESSAGE";

    //    Test Timer
    private DatabaseHelper db;
    private Timer timerTest;
    private ArrayList<StudyTime> studyTimes;
    private ArrayList<Calendar> calendar;

    private HashMap<String, String> messageMap;

    private static boolean isStudy, isTest, isSleepAwake, useOldData;
    private static int studyBefore, testBefore, sleepHour, sleepMinute, wakeHour, wakeMinute;

    @Override
    public void onCreate() {
        super.onCreate();

        useOldData = false;
        isTest = isStudy = isSleepAwake = false;
        studyBefore = testBefore = 0;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!useOldData)
            updateIntent(intent);

        initializeValues();

        // create a list for study time
        if (isStudy)
        {
            for (int i = 0; i < studyTimes.size(); i++)
            {
                Calendar cal = Calendar.getInstance();
                StudyTime time = studyTimes.get(i);
                int offset = time.getDay() + 2 - cal.get(Calendar.DAY_OF_WEEK);
                cal.add(Calendar.DAY_OF_MONTH, offset);
                cal.set(Calendar.HOUR_OF_DAY, time.getHour());
                cal.set(Calendar.MINUTE, time.getMinute());
                cal.add(Calendar.MINUTE, -studyBefore);
                calendar.add(cal);

                // add message
                String hashCode = cal.getTime().toString();
                String course = db.getCourse(time.getCourseId()).getName();
                messageMap.put(hashCode, "Time to study " + course);
            }
        }

        // create a list for test time
        if (isTest)
        {
            for (int i = 0 ; i < studyTimes.size(); i++)
            {
                if (studyTimes.get(i).isHasTest())
                {
                    Calendar cal = Calendar.getInstance();
                    StudyTime time = studyTimes.get(i);

                    int offset = time.getDay() + 2 - cal.get(Calendar.DAY_OF_WEEK);
                    cal.add(Calendar.DAY_OF_MONTH, offset);
                    cal.add(Calendar.DAY_OF_MONTH, -testBefore);
                    cal.set(Calendar.HOUR_OF_DAY, time.getHour());
                    cal.set(Calendar.MINUTE, time.getMinute());
                    calendar.add(cal);

                    // add message
                    String hashCode = cal.getTime().toString();
                    String course = db.getCourse(time.getCourseId()).getName();
                    messageMap.put(hashCode, "Test for " + course + " on the next " + testBefore + " days");
                }
            }

        }


        //create a list for sleep wake alarm
        if(isSleepAwake)
        {
            Calendar wakeCal= Calendar.getInstance();
            Calendar sleepCal=Calendar.getInstance();

            wakeCal.set(Calendar.HOUR_OF_DAY,wakeHour);
            wakeCal.set(Calendar.MINUTE,wakeMinute);
            if(wakeCal.getTime().before(Calendar.getInstance().getTime()))
                wakeCal.add(Calendar.DAY_OF_MONTH,1);
            calendar.add(wakeCal);

            String hashCode = wakeCal.getTime().toString();
            messageMap.put(hashCode, "Time to wake up");


            sleepCal.set(Calendar.HOUR_OF_DAY,sleepHour);
            sleepCal.set(Calendar.MINUTE,sleepMinute);
            if(sleepCal.getTime().before(Calendar.getInstance().getTime()))
                sleepCal.add(Calendar.DAY_OF_MONTH,1);
            calendar.add(sleepCal);

            hashCode = sleepCal.getTime().toString();
            messageMap.put(hashCode, "Time to sleep");
        }

        // sort the date
        Collections.sort(calendar);


        // SET ALARM
        final Calendar time = getNextTime();

        // no available time
        if (time ==  null)
            return START_NOT_STICKY;

        Log.i("time", "Setting new alarm");

        Log.i("Timely: "," Alarm time:"+ time.getTime());

        long countTime = time.getTimeInMillis() - Calendar.getInstance().getTimeInMillis();

        // count the time
        // This is a separate thread, so the codes after this timer will still run
        timerTest = new Timer();
        timerTest.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Log.i("Countdown", " "+Calendar.getInstance().getTime());
                if(time.getTimeInMillis()/(1000 * 60) == Calendar.getInstance().getTimeInMillis()/(1000 * 60))
                {
                    Intent intent2 = new Intent(getApplicationContext(), Ringtone.class);
                    intent2.putExtra(ALARM_MESSAGE, messageMap.get(time.getTime().toString()));
                    cancel();
                    startService(intent2);
                }
            }
        },10,1000);

        Intent mIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, mIntent, 0);
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Timely")
                .setContentText("Alarm is set successfully")
                .setSmallIcon(R.drawable.ic_foreground)
                .setContentIntent(pendingIntent)
                .build();

        startForeground(1, notification);

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (timerTest != null)
            timerTest.cancel();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public Calendar getNextTime()
    {
        // no time available
        if (calendar.size() == 0)
            return null;

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

        return minCal;
    }

    private void initializeValues()
    {
        // get information from database
        db = new DatabaseHelper(getApplicationContext());
        studyTimes = db.getAllStudyTime();

        messageMap = new HashMap<>();

        calendar = new ArrayList<>();
    }

    private void updateIntent(Intent intent)
    {
        // get information from activity
        isStudy = intent.getBooleanExtra(NotificationSettingsActivity.STUDYSWITCH, false);
        isTest = intent.getBooleanExtra(NotificationSettingsActivity.TESTSWITCH, false);
        isSleepAwake = intent.getBooleanExtra(NotificationSettingsActivity.SLEEPWAKESWITCH, false);

        studyBefore = intent.getIntExtra(NotificationSettingsActivity.STUDY_BEFORE, 0);
        testBefore = intent.getIntExtra(NotificationSettingsActivity.TEST_BEFORE, 0);

        sleepHour = intent.getIntExtra(NotificationSettingsActivity.SLEEPHOUR, 0);
        sleepMinute = intent.getIntExtra(NotificationSettingsActivity.SLEEPMIN, 0);
        wakeHour = intent.getIntExtra(NotificationSettingsActivity.WAKEHOUR, 0);
        wakeMinute = intent.getIntExtra(NotificationSettingsActivity.WAKEMIN, 0);


        useOldData = true;
    }
}
