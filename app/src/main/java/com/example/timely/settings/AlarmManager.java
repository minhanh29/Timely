package com.example.timely.settings;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

public class AlarmManager {
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String SLEEPWAKESWITCH = "sleepWakeSwitch";
    public static final String STUDYSWITCH = "studyTimeSwitch";
    public static final String TESTSWITCH = "testTimeSwitch";
    public static final String SPINNER1 = "spinner1";
    public static final String SPINNER2 = "spinner2";

    public static final String SLEEPHOUR = "sleepHr";
    public static final String SLEEPMIN = "sleepMin";
    public static final String WAKEHOUR = "wakeHr";
    public static final String WAKEMIN = "wakeMin";

    public static final String STUDY_BEFORE = "STUDY_BEFORE";
    public static final String TEST_BEFORE = "TEST_BEFORE";


    private static final String TIMEBEFORESTUDY = "timeBeforeStudy";
    private static final String TIMEBEFORETEST = "timeBeforeTest";
    public static void updateAlarm(Context context)
    {

        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);

        int studyBefore = 0;
        int testBefore = 0;
        boolean sleepWakeSwitchOnOff, studyTimSwitchOnOFf, testTimeSwitchOnOff;
        int spinnerOne, spinnerTwo, wakeHour, wakeMinute, sleepHour, sleepMinute;

        sleepWakeSwitchOnOff = sharedPreferences.getBoolean(SLEEPWAKESWITCH, false);
        studyTimSwitchOnOFf = sharedPreferences.getBoolean(STUDYSWITCH, false);
        testTimeSwitchOnOff = sharedPreferences.getBoolean(TESTSWITCH, false);
        spinnerOne = sharedPreferences.getInt(SPINNER1, 1);
        spinnerTwo = sharedPreferences.getInt(SPINNER2, 1);
        studyBefore = sharedPreferences.getInt(TIMEBEFORESTUDY, 60);
        testBefore = sharedPreferences.getInt(TIMEBEFORETEST, 3);
        wakeHour = sharedPreferences.getInt(WAKEHOUR, 0);
        wakeMinute = sharedPreferences.getInt(WAKEMIN, 0);
        sleepHour = sharedPreferences.getInt(SLEEPHOUR, 0);
        sleepMinute = sharedPreferences.getInt(SLEEPMIN, 0);

        // turn off the alarm
        Intent intent1 = new Intent(context, AlarmService.class);
        context.stopService(intent1);

        // if one of the switches is on, turn on to update the alarm
        Intent intent = new Intent(context, AlarmService.class);
        intent.putExtra(SLEEPWAKESWITCH, sleepWakeSwitchOnOff);
        intent.putExtra(STUDYSWITCH, studyTimSwitchOnOFf);
        intent.putExtra(TESTSWITCH, testTimeSwitchOnOff);

        // time for sleep and wake alarm
        intent.putExtra(SLEEPHOUR, sleepHour);
        intent.putExtra(SLEEPMIN, sleepMinute);
        intent.putExtra(WAKEHOUR, wakeHour);
        intent.putExtra(WAKEMIN, wakeMinute);

        // time before alarm
        intent.putExtra(STUDY_BEFORE, studyBefore);
        intent.putExtra(TEST_BEFORE, testBefore);

        context.startService(intent);
    }
}
