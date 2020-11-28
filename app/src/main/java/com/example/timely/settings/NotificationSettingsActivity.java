package com.example.timely.settings;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.util.TimeZone;
import android.net.Uri;
import android.provider.AlarmClock;
import android.provider.CalendarContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.timely.DatabaseHelper;
import com.example.timely.MainActivity;
import com.example.timely.R;
import com.example.timely.courses.StudyTime;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;

import static android.provider.Telephony.Mms.Part.TEXT;

public class NotificationSettingsActivity extends AppCompatActivity {

    TextView wakeTimer, sleepTimer;
    int wakeHour, wakeMinute, sleepHour, sleepMinute;
    Switch sleepWakeSwitch, studyTimSwitch, testTimeSwitch;
    DatabaseHelper db;
    Spinner spinner1,spinner2;
    ArrayList<PendingIntent> intentSWArray, intentSArray, intentTArray;
    ArrayList<StudyTime> studyTimes;
    ArrayList<Calendar> globalStudyTime, globalTestTime;
    Calendar[] globalSleepWakeCalendar;

    ArrayList<PendingIntent> intentArray;
    AlarmManager[] alarmManager;
    Intent[] alarmIntent;

    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String WAKETIMER = "text";
    public static final String SLEEPTIMER = "text";
    public static final String SLEEPWAKESWITCH = "sleepWakeSwitch";
    public static final String STUDYSWITCH = "studyTimeSwitch";
    public static final String TESTSWITCH = "testTimeSwitch";
    public static final String SPINNER1 = "spinner1";
    public static final String SPINNER2 = "spinner2";

    private String wakeTime, sleepTime;
    private boolean sleepWakeSwitchOnOff, studyTimSwitchOnOFf, testTimeSwitchOnOff;
    private int spinnerOne,spinnerTwo;


    public static final String UPDATE_ALARM = "UPDATE_ALARM";

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_settings);
        wakeTimer = (TextView) findViewById(R.id.wakeTime);
        sleepTimer = (TextView) findViewById(R.id.sleepTime);
        db = new DatabaseHelper(NotificationSettingsActivity.this);
        globalStudyTime= new ArrayList<>();
        globalTestTime= new ArrayList<>();
        globalSleepWakeCalendar = new Calendar[2];

        Intent intent = getIntent();
        boolean update = intent.getBooleanExtra(UPDATE_ALARM, false);
        if (update)
            startNextAlarm();

        sleepWakeSwitch = (Switch) findViewById(R.id.sleepWakeSwitch);
        sleepWakeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked == true){
                    Toast.makeText(getBaseContext(),"Sleep/Wake Alarm: ON", Toast.LENGTH_SHORT).show();
                    startSleepWakeAlarm();
                }
                else
                {
                    Toast.makeText(getBaseContext(),"Sleep/Wake Alarm: OFF", Toast.LENGTH_SHORT).show();
                    cancelSleepWakeAlarm();
                }
//                saveData();
            }
        });

        studyTimSwitch = (Switch) findViewById(R.id.studyAlarm);
        studyTimSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked == true){
                    Toast.makeText(getBaseContext(),"Study Alarm: ON", Toast.LENGTH_SHORT).show();
                    //startStudyAlarm();
                    startNextAlarm();
                }
                else
                {
                    Toast.makeText(getBaseContext(),"Study Alarm: OFF", Toast.LENGTH_SHORT).show();
                    cancelStudyAlarm();
                }
//                saveData();
            }
        });


        testTimeSwitch = (Switch) findViewById(R.id.testAlarm);
        testTimeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked == true){
                    Toast.makeText(getBaseContext(),"Study Alarm: ON", Toast.LENGTH_SHORT).show();
                    startTestAlarm();
                }
                else
                {
                    Toast.makeText(getBaseContext(),"Study Alarm: OFF", Toast.LENGTH_SHORT).show();
                    cancelTestAlarm();
                }
//                saveData();
            }
        });

        spinner1 = findViewById(R.id.studySpinner);
        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this,
                R.array.timeBeforeClass, android.R.layout.simple_spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner1.setAdapter(adapter1);
        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                studyTimes = db.getAllStudyTime();
                Calendar calendar = Calendar.getInstance();
                for (int i = 0; i < studyTimes.size(); i++) {
                    StudyTime studyTime = studyTimes.get(i);
                    int hour = 0, min = 0, day = 0;
                    hour = studyTime.getHour();
                    min = studyTime.getMinute();
                    day = studyTime.getDay();
                    switch (position) {
                        case 1:
                            calendar.set(Calendar.DAY_OF_WEEK, day);
                            calendar.set(Calendar.HOUR_OF_DAY, hour);
                            calendar.set(Calendar.MINUTE,min);
                            calendar.add(Calendar.HOUR_OF_DAY,-1);
                            break;
                        case 2:
                            calendar.set(Calendar.DAY_OF_WEEK, day);
                            calendar.set(Calendar.HOUR_OF_DAY, hour);
                            calendar.set(Calendar.MINUTE, min);
                            calendar.add(Calendar.MINUTE,-45);
                            break;
                        case 3:
                            calendar.set(Calendar.DAY_OF_WEEK, day);
                            calendar.set(Calendar.HOUR_OF_DAY, hour);
                            calendar.set(Calendar.MINUTE, min);
                            calendar.add(Calendar.MINUTE,-30);
                            break;
                        case 4:
                            calendar.set(Calendar.DAY_OF_WEEK, day);
                            calendar.set(Calendar.HOUR_OF_DAY, hour);
                            calendar.set(Calendar.MINUTE, min);
                            calendar.add(Calendar.MINUTE,-15);
                            break;
                    }
                    globalStudyTime.add(calendar);
                }
//                saveData();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinner2 = findViewById(R.id.testSpinner);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this,
                R.array.timeBeforeTest, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner2.setAdapter(adapter2);
        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                studyTimes = db.getAllStudyTime();
                for (int i = 0; i < studyTimes.size(); i++) {
                    StudyTime studyTime = studyTimes.get(i);
                    Calendar calendar = Calendar.getInstance();
                    int day = 0;
                    day = studyTime.getDay();
                    if (studyTime.isHasTest()) {
                        switch (position) {
                            case 1:
                                calendar.set(Calendar.DAY_OF_WEEK, day);
                                calendar.add(Calendar.DATE, -3);
                                break;
                            case 2:
                                calendar.set(Calendar.DAY_OF_WEEK, day);
                                calendar.add(Calendar.DATE, -1);
                                break;
                        }
                    }
                    globalTestTime.add(calendar);
                }
//                saveData();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        wakeTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(
                        NotificationSettingsActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                Calendar c = Calendar.getInstance();
                                c.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                c.set(Calendar.MINUTE, minute);
                                c.set(Calendar.SECOND, 0);

                                wakeHour = hourOfDay;
                                wakeMinute = minute;

                                globalSleepWakeCalendar[1]=c;
                                wakeTimer.setText(android.text.format.DateFormat.format("hh:mm aa", c));
//                                saveData();
                            }
                        }, 12, 0, false
                );
                timePickerDialog.updateTime(wakeHour, wakeMinute);
                timePickerDialog.show();
            }
        });

        sleepTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(
                        NotificationSettingsActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                String m = "Timely: Sleep Alarm";
                                Calendar c = Calendar.getInstance();
                                c.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                c.set(Calendar.MINUTE, minute);
                                c.set(Calendar.SECOND, 0);

                                sleepHour = hourOfDay;
                                sleepMinute = minute;

                                globalSleepWakeCalendar[0] = c;
                                sleepTimer.setText(android.text.format.DateFormat.format("hh:mm aa", c));
//                                saveData();
                            }
                        }, 12, 0, false
                );
                timePickerDialog.updateTime(sleepHour, sleepMinute);
                timePickerDialog.show();
            }
        });
//        loadData();
//        updateViews();
    }



    private void cancelTestAlarm() {
        System.out.println("It cancels the alarm");
        AlarmManager[] alarmManager=new AlarmManager[globalTestTime.size()];
        if(intentTArray.size()>0){
            for(int i=0; i<intentTArray.size(); i++){
                alarmManager[i] = (AlarmManager) getSystemService(ALARM_SERVICE);
                alarmManager[i].cancel(intentTArray.get(i));
            }
            intentTArray.clear();
        }
    }

    private void startTestAlarm() {
//        studyTimes = db.getAllStudyTime();
//        for(int i=0;i<studyTimes.size();i++) {
//            StudyTime studyTime=studyTimes.get(i);
//            Intent[] testIntent = new Intent[studyTimes.size()];
//            testIntent[i] = new Intent(Intent.ACTION_INSERT);
//
//            testIntent[i].setData(CalendarContract.Events.CONTENT_URI);
//            testIntent[i].putExtra(CalendarContract.EventDays.STARTDAY,globalTestTime.get(i).get(Calendar.DATE));
//            testIntent[i].putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME,globalTestTime.get(i).getTimeInMillis());
//            testIntent[i].putExtra(CalendarContract.Events.STATUS, 1);
//            testIntent[i].putExtra(CalendarContract.Events.HAS_ALARM, 1);
//            testIntent[i].putExtra(CalendarContract.Events.EVENT_TIMEZONE, globalTestTime.get(i).getTimeZone());
//            testIntent[i].putExtra(CalendarContract.Events.DURATION,studyTime.getDuration());
//
//            if(testIntent[i].resolveActivity(getPackageManager()) != null){
//                startActivity(testIntent[i]);
//            }else{
//                Toast.makeText(NotificationSettingsActivity.this, "There is no app that support this action", Toast.LENGTH_SHORT).show();
//            }
//        }
        AlarmManager[] alarmManager=new AlarmManager[globalTestTime.size()];
        intentTArray = new ArrayList<PendingIntent>();
        for(int f=0;f<globalTestTime.size();f++){
            Intent intent = new Intent(this, AlarmReceiver.class);
            PendingIntent pi=PendingIntent.getBroadcast(this, f,intent, 0);

            alarmManager[f] = (AlarmManager) getSystemService(ALARM_SERVICE);
            alarmManager[f].setExact(AlarmManager.RTC_WAKEUP, globalTestTime.get(f).getTimeInMillis(), pi);

            intentTArray.add(pi);
        }
    }

    private void cancelStudyAlarm() {
//        studyTimes = db.getAllStudyTime();
//        for(int i=0;i<studyTimes.size();i++)
//        {
//            Intent[] cancelSWAlarm = new Intent[studyTimes.size()];
//            cancelSWAlarm[i] = new Intent(AlarmClock.ACTION_DISMISS_ALARM);
//            startActivity(cancelSWAlarm[i]);
//        }
        System.out.println("It cancels the alarm");
        AlarmManager[] alarmManager=new AlarmManager[globalStudyTime.size()];
        if(intentSArray.size()>0){
            for(int i=0; i<intentSArray.size(); i++){
                alarmManager[i] = (AlarmManager) getSystemService(ALARM_SERVICE);
                alarmManager[i].cancel(intentSArray.get(i));
            }
            intentSArray.clear();
        }
    }

    private void startStudyAlarm() {
//        studyTimes = db.getAllStudyTime();
//        for(int i=0;i<studyTimes.size();i++) {
//            int hour = globalStudyTime.get(i).get(Calendar.HOUR_OF_DAY);
//            int min = globalStudyTime.get(i).get(Calendar.MINUTE);
//            int dayOfWeek=globalStudyTime.get(i).get(Calendar.DAY_OF_WEEK);
//
//            Intent[] alarmIntent = new Intent[studyTimes.size()];
//            alarmIntent[i] = new Intent(AlarmClock.ACTION_SET_ALARM);
//            alarmIntent[i].putExtra(AlarmClock.EXTRA_HOUR, hour);
//            alarmIntent[i].putExtra(AlarmClock.EXTRA_MINUTES, min);
//            alarmIntent[i].putExtra(AlarmClock.EXTRA_DAYS,dayOfWeek);
//
//            startActivity(alarmIntent[i]);
//        }

        AlarmManager[] alarmManager=new AlarmManager[globalStudyTime.size()];
        intentSArray = new ArrayList<PendingIntent>();
        for(int f=0;f<globalStudyTime.size();f++){
            Intent intent = new Intent(this, AlarmReceiver.class);
            PendingIntent pi=PendingIntent.getBroadcast(this, f,intent, 0);

            alarmManager[f] = (AlarmManager) getSystemService(ALARM_SERVICE);
            alarmManager[f].setExact(AlarmManager.RTC_WAKEUP, globalStudyTime.get(f).getTimeInMillis(), pi);

            intentSArray.add(pi);
        }
    }

    private void cancelSleepWakeAlarm() {
//        for(int i=0;i<globalSleepWakeCalendar.length;i++)
//        {
////            Intent[] cancelSWAlarm = new Intent[globalSleepWakeCalendar.length];
//            ArrayList<Integer> allDays = new ArrayList<>();
//            allDays.add(2);
//            allDays.add(3);
//            allDays.add(4);
//            allDays.add(5);
//            allDays.add(6);
//            int hour =  globalSleepWakeCalendar[i].get(Calendar.HOUR_OF_DAY);
//            int min =  globalSleepWakeCalendar[i].get(Calendar.MINUTE);
//            Intent intent = new Intent(AlarmClock.ACTION_DISMISS_ALARM);
//            intent.putExtra(AlarmClock.EXTRA_HOUR, hour);
//            intent.putExtra(AlarmClock.EXTRA_MINUTES, min);
//            intent.putExtra(AlarmClock.EXTRA_DAYS,allDays);
//            intent.putExtra(AlarmClock.EXTRA_ALARM_SEARCH_MODE, AlarmClock.ALARM_SEARCH_MODE_TIME);
//            startActivity(intent);
//        }
//        System.out.println("It cancels the alarm");
//        AlarmManager[] alarmManager=new AlarmManager[globalSleepWakeCalendar.length];
//        if(intentSWArray.size()>0){
//            for(int i=0; i<intentSWArray.size(); i++){
//                alarmManager[i] = (AlarmManager) getSystemService(ALARM_SERVICE);
//                alarmManager[i].cancel(intentSWArray.get(i));
//            }
//            intentSWArray.clear();
//        }
        for (int i = 0; i < globalSleepWakeCalendar.length; i++)
        {
            Intent intent = new Intent(this, AlarmReceiver.class);
            PendingIntent pi = PendingIntent.getBroadcast(this, i, intent, 0);
            alarmManager[i].cancel(pi);
        }

    }



    private void startSleepWakeAlarm() {
        ArrayList<PendingIntent>intentArray = new ArrayList<>();
        alarmIntent = new Intent[ globalSleepWakeCalendar.length];
        for(int i=0;i< globalSleepWakeCalendar.length;i++) {
//            ArrayList<Integer> allDays = new ArrayList<>();
//            allDays.add(2);
//            allDays.add(3);
//            allDays.add(4);
//            allDays.add(5);
//            allDays.add(6);
//            int hour =  globalSleepWakeCalendar[i].get(Calendar.HOUR_OF_DAY);
//            int min =  globalSleepWakeCalendar[i].get(Calendar.MINUTE);
//            alarmIntent[i] = new Intent(AlarmClock.ACTION_SET_ALARM);
//            alarmIntent[i].putExtra(AlarmClock.EXTRA_HOUR, hour);
//            alarmIntent[i].putExtra(AlarmClock.EXTRA_MINUTES, min);
//            alarmIntent[i].putExtra(AlarmClock.EXTRA_DAYS,allDays);
//
//            startActivity(alarmIntent[i]);

//            setAlarm(6, hour, min, i);
//
//            intentArray.add(pendingIntent);
//        }
        AlarmManager[] alarmManager = new AlarmManager[globalSleepWakeCalendar.length];
        intentSWArray = new ArrayList<PendingIntent>();
        for (int f = 0; f < globalSleepWakeCalendar.length; f++) {
            Intent intent = new Intent(this, AlarmReceiver.class);
            PendingIntent pi = PendingIntent.getBroadcast(this, f, intent, 0);
            //PendingIntent pendingIntent = PendingIntent.getActivities(NotificationSettingsActivity.this,i, alarmIntent,PendingIntent.FLAG_UPDATE_CURRENT);

            //intentArray.add(pendingIntent);
        }
//        PendingIntent pendingIntent = PendingIntent.getActivities(NotificationSettingsActivity.this,0, alarmIntent,PendingIntent.FLAG_UPDATE_CURRENT);
//        intentArray.add(pendingIntent);
//        alarmManager=new AlarmManager[24];
//        intentArray = new ArrayList<PendingIntent>();
//        for(int i = 0; i < globalSleepWakeCalendar.length; i++){
//            Intent intent = new Intent(this, AlarmReceiver.class);
//            PendingIntent pi =PendingIntent.getBroadcast(this, i,intent, 0);

            alarmManager[i] = (AlarmManager) getSystemService(ALARM_SERVICE);
            alarmManager[i].setExact(AlarmManager.RTC_WAKEUP, globalSleepWakeCalendar[i].getTimeInMillis(), pi);

            intentSWArray.add(pi);
        }
    }


    private void setAlarm(int day, int hour, int minute, int id) {
    }

    public void startNextAlarm()
    {
        int[] time = getNextTime();
        Log.i("time", "Day:" + time[0] + "hour: " + time[1] + "minute: " + time[2]);

        Intent intent2 = new Intent(this, AlarmReceiver.class);
        PendingIntent pi =PendingIntent.getBroadcast(this, 1,intent2, 0);

        Calendar c = Calendar.getInstance();
        //c.set(Calendar.DAY_OF_WEEK, time[0]);
        c.set(Calendar.HOUR_OF_DAY, time[1]);
        c.set(Calendar.MINUTE, time[2]);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pi);

        Intent intent = new Intent(AlarmClock.ACTION_SET_ALARM);
        ArrayList<Integer> days = new ArrayList<>();
        days.add(time[0]);
        intent.putExtra(AlarmClock.EXTRA_DAYS, days);
        intent.putExtra(AlarmClock.EXTRA_HOUR, time[1]);
        intent.putExtra(AlarmClock.EXTRA_MINUTES, time[2]);
        //intent.putExtra(AlarmClock.EXTRA_SKIP_UI, true);

        startActivity(intent);

    }


    public int[] getNextTime()
    {
        int[] time = new int[3];
        DatabaseHelper db = new DatabaseHelper(this);
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

    /** Adds Events and Reminders in Calendar. */
    private void addReminderInCalendar() {
        Calendar cal = Calendar.getInstance();
        Intent intent = new Intent(Intent.ACTION_EDIT);
        intent.setType("vnd.android.cursor.item/event");
        intent.putExtra("beginTime", cal.getTimeInMillis());
        intent.putExtra("allDay", false);
        intent.putExtra("rule", "FREQ=DAILY");
        intent.putExtra("endTime", cal.getTimeInMillis()+60*60*1000);
        intent.putExtra("title", "A Test Event from android app");
        startActivity(intent);
    }

    /** Returns Calendar Base URI, supports both new and old OS. */
    private String getCalendarUriBase(boolean eventUri) {
        Uri calendarURI = null;
        try {
            if (android.os.Build.VERSION.SDK_INT <= 7) {
                calendarURI = (eventUri) ? Uri.parse("content://calendar/") : Uri.parse("content://calendar/calendars");
            } else {
                calendarURI = (eventUri) ? Uri.parse("content://com.android.calendar/") : Uri
                        .parse("content://com.android.calendar/calendars");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return calendarURI.toString();
    }

    public void goBack(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}


