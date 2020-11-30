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
import java.util.Timer;
import java.util.TimerTask;

import static com.example.timely.settings.App.CHANNEL_ID;

public class AlarmService extends Service {

    public static String TIME_INFO = "time_infor";

    private CountDownTimer timer;
    //    Test Timer
    private Timer timerTest;
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
            cal.set(Calendar.HOUR_OF_DAY, studyTimes.get(i).getHour());
            cal.set(Calendar.MINUTE, studyTimes.get(i).getMinute());
            calendar.add(cal);
        }

    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        // get information from activity
        boolean isStudy = intent.getBooleanExtra(NotificationSettingsActivity.STUDYSWITCH, true);
        boolean isTest = intent.getBooleanExtra(NotificationSettingsActivity.TESTSWITCH, true);
        boolean isSleepAwake = intent.getBooleanExtra(NotificationSettingsActivity.SLEEPWAKESWITCH, true);

        int studyBefore = intent.getIntExtra(NotificationSettingsActivity.STUDY_BEFORE, 60);
        int testBefore = intent.getIntExtra(NotificationSettingsActivity.TEST_BEFORE, 3);

        int sleepHour = intent.getIntExtra(NotificationSettingsActivity.SLEEPHOUR, 0);
        int sleepMinute = intent.getIntExtra(NotificationSettingsActivity.SLEEPMIN, 0);
        int wakeHour = intent.getIntExtra(NotificationSettingsActivity.WAKEHOUR, 0);
        int wakeMinute = intent.getIntExtra(NotificationSettingsActivity.WAKEMIN, 0);

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

        //create a list for sleep wake alarm
        if(isSleepAwake)
        {
            Calendar wakeCal= Calendar.getInstance();
            Calendar sleepCal=Calendar.getInstance();

            wakeCal.set(Calendar.HOUR_OF_DAY,wakeHour);
            wakeCal.set(Calendar.MINUTE,wakeMinute);
            if(wakeCal.getTime().before(Calendar.getInstance().getTime()))
            {
                wakeCal.add(Calendar.DAY_OF_MONTH,1);
            }
            calendar.add(wakeCal);

            sleepCal.set(Calendar.HOUR_OF_DAY,sleepHour);
            sleepCal.set(Calendar.MINUTE,sleepMinute);
            if(sleepCal.getTime().before(Calendar.getInstance().getTime()))
            {
                sleepCal.add(Calendar.DAY_OF_MONTH,1);
            }
            calendar.add(sleepCal);
        }

        // add test to calendar
        for (int i= 0; i < testCal.size(); i++)
            calendar.add(testCal.get(i));

        // sort the date
        Collections.sort(calendar);


        // SET ALARM
        final Calendar time = getNextTime();
        if (time ==  null)
        {
            stopSelf();
            return START_NOT_STICKY;
        }

        Log.i("time", "Setting new alarm");

        int day = time.get(Calendar.DAY_OF_MONTH);
        int hour = time.get(Calendar.HOUR_OF_DAY);
        int min = time.get(Calendar.MINUTE);
        Log.i("time", "Alarm day: " + day + " hour: " + hour + " minute: " + min);
        Log.i("Timely: "," Alarm time:"+ time.getTime());

        long countTime = time.getTimeInMillis() - Calendar.getInstance().getTimeInMillis();
        day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        min = Calendar.getInstance().get(Calendar.MINUTE);
        Log.i("time", "Today: " + day + " hour: " + hour + " minute: " + min);

        // count down the time
        // This is a separate thread, so the codes after this timer will still run
        timerTest = new Timer();
        timerTest.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Log.i("Countdown", " "+Calendar.getInstance().getTime());
                if(time.getTimeInMillis()==Calendar.getInstance().getTimeInMillis())
                {
                    Intent intent2 = new Intent(getApplicationContext(), Ringtone.class);
                    startService(intent2);
                    Log.i("time", "Alarm finish");
                    timerTest.cancel();
                }
            }
        },10,1000);
//        timer = new CountDownTimer(countTime, 1000) {
//
//            public void onTick(long millisUntilFinished) {
//                Log.i("time", "(milliseconds) count down: " + millisUntilFinished);
//            }
//
//            public void onFinish() {
//                Intent intent2 = new Intent(getApplicationContext(), Ringtone.class);
//                startService(intent2);
//                Log.i("time", "Alarm finish");
//            }
//        };
//        timer.start();

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

        Log.i("time", "Day: " + minCal.get(Calendar.DAY_OF_MONTH));

        return minCal;
    }

}
