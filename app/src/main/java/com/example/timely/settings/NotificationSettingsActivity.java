package com.example.timely.settings;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class NotificationSettingsActivity extends AppCompatActivity {

    TextView wakeTimer, sleepTimer;
    int wakeHour, wakeMinute, sleepHour, sleepMinute;
    Switch sleepWakeSwitch, studyTimSwitch, testTimeSwitch;
    DatabaseHelper db;
    Spinner spinner1, spinner2;

    ArrayList<PendingIntent> intentSWArray, intentSArray, intentTArray;
    ArrayList<StudyTime> studyTimes;

    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String WAKETIMER = "text";
    public static final String SLEEPTIMER = "text";
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

    private int studyBefore = 0;
    private int testBefore = 0;


    int newStudyBefore, newTestBefore, newWakeHour, newWakeMinute, newSleepHour, newSleepMinute;
    private String wakeTime, sleepTime;
    private boolean sleepWakeSwitchOnOff, studyTimSwitchOnOFf, testTimeSwitchOnOff;
    private int spinnerOne, spinnerTwo;

    public static final String UPDATE_ALARM = "UPDATE_ALARM";

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_settings);
        wakeTimer = (TextView) findViewById(R.id.wakeTime);
        sleepTimer = (TextView) findViewById(R.id.sleepTime);
        db = new DatabaseHelper(NotificationSettingsActivity.this);

        globalStudyTime = new ArrayList<>();
        globalTestTime = new ArrayList<>();
        globalSleepWakeCalendar = new Calendar[2];




        sleepWakeSwitch = (Switch) findViewById(R.id.sleepWakeSwitch);
        sleepWakeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked == true) {
                    Toast.makeText(getBaseContext(), "Sleep/Wake Alarm: ON", Toast.LENGTH_SHORT).show();
                    startSleepWakeAlarm();
                } else {
                    Toast.makeText(getBaseContext(), "Sleep/Wake Alarm: OFF", Toast.LENGTH_SHORT).show();
                    cancelSleepWakeAlarm();
                }
//                saveData();

                updateAlarm();
                saveData();

            }
        });

        studyTimSwitch = (Switch) findViewById(R.id.studyAlarm);
        studyTimSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked == true) {
                    Toast.makeText(getBaseContext(), "Study Alarm: ON", Toast.LENGTH_SHORT).show();
                    startStudyAlarm();
                } else {
                    Toast.makeText(getBaseContext(), "Study Alarm: OFF", Toast.LENGTH_SHORT).show();
                    cancelStudyAlarm();
                }
//                saveData();

                updateAlarm();
                saveData();

            }
        });


        testTimeSwitch = (Switch) findViewById(R.id.testAlarm);
        testTimeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked == true) {
                    Toast.makeText(getBaseContext(), "Study Alarm: ON", Toast.LENGTH_SHORT).show();
                    startTestAlarm();
                } else {
                    Toast.makeText(getBaseContext(), "Study Alarm: OFF", Toast.LENGTH_SHORT).show();
                    cancelTestAlarm();
                }
//                saveData();

                updateAlarm();
                saveData();

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
                    System.out.println(position);
                    switch (position) {

                        case 1:
                            calendar.set(Calendar.DAY_OF_WEEK, day);
                            calendar.set(Calendar.HOUR_OF_DAY, hour);
                            calendar.set(Calendar.MINUTE, min);
                            calendar.add(Calendar.HOUR_OF_DAY, -1);
                            break;
                        case 2:
                            calendar.set(Calendar.DAY_OF_WEEK, day);
                            calendar.set(Calendar.HOUR_OF_DAY, hour);
                            calendar.set(Calendar.MINUTE, min);
                            calendar.add(Calendar.MINUTE, -45);

                        case 0:
                            calendar.set(Calendar.DAY_OF_WEEK, day + 2);
                            calendar.set(Calendar.HOUR_OF_DAY, hour);
                            calendar.set(Calendar.MINUTE, min);
                            calendar.add(Calendar.HOUR_OF_DAY, -1);
                            studyBefore = 60;

                            break;
                        case 1:
                            calendar.set(Calendar.DAY_OF_WEEK, day + 2);
                            calendar.set(Calendar.HOUR_OF_DAY, hour);
                            calendar.set(Calendar.MINUTE, min);

                            calendar.add(Calendar.MINUTE, -30);

                            calendar.add(Calendar.MINUTE, -45);
                            studyBefore = 45;

                            break;
                        case 2:
                            calendar.set(Calendar.DAY_OF_WEEK, day + 2);
                            calendar.set(Calendar.HOUR_OF_DAY, hour);
                            calendar.set(Calendar.MINUTE, min);


                            calendar.add(Calendar.MINUTE, -30);
                            studyBefore = 30;
                            break;
                        case 3:
//                            calendar.set(Calendar.DAY_OF_WEEK, day + 2);
//                            calendar.set(Calendar.HOUR_OF_DAY, hour);
//                            calendar.set(Calendar.MINUTE, min);
//                            calendar.add(Calendar.MINUTE, -15);
                            studyBefore = 15;

                            break;
                    }
                }
                saveData();
                updateAlarm();
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
                            case 0:
//                                calendar.set(Calendar.DAY_OF_WEEK, day + 2);
//                                calendar.set(Calendar.HOUR_OF_DAY, studyTime.getHour());
//                                calendar.set(Calendar.MINUTE, studyTime.getMinute());
//                                calendar.add(Calendar.DATE, -3);
                                testBefore = 3;
                                break;
                            case 1:
//                                calendar.set(Calendar.DAY_OF_WEEK, day);
//                                calendar.set(Calendar.HOUR_OF_DAY, studyTime.getHour());
//                                calendar.set(Calendar.MINUTE, studyTime.getMinute());
//                                calendar.add(Calendar.DATE, -1);
                                testBefore = 1;
                                break;
                        }
                    }
                }
                saveData();
                updateAlarm();
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


                                globalSleepWakeCalendar[1] = c;

                                wakeTimer.setText(android.text.format.DateFormat.format("hh:mm aa", c));
                                saveData();
                            }
                        }, 12, 0, false
                );
                timePickerDialog.updateTime(wakeHour, wakeMinute);
                timePickerDialog.show();
                updateAlarm();
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

                                sleepTimer.setText(android.text.format.DateFormat.format("hh:mm aa", c));
                                saveData();
                            }
                        }, 12, 0, false
                );
                timePickerDialog.updateTime(sleepHour, sleepMinute);
                timePickerDialog.show();
                updateAlarm();
            }
        });
        loadData();
        updateViews();
        updateAlarm();
    }



    private void cancelTestAlarm() {
        System.out.println("It cancels the alarm");
        AlarmManager[] alarmManager = new AlarmManager[globalTestTime.size()];
        if (intentTArray.size() > 0) {
            for (int i = 0; i < intentTArray.size(); i++) {
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
        AlarmManager[] alarmManager = new AlarmManager[globalTestTime.size()];
        intentTArray = new ArrayList<PendingIntent>();
        for (int f = 0; f < globalTestTime.size(); f++) {
            Intent intent = new Intent(this, AlarmReceiver.class);
            PendingIntent pi = PendingIntent.getBroadcast(this, f, intent, 0);

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
        AlarmManager[] alarmManager = new AlarmManager[globalStudyTime.size()];
        if (intentSArray.size() > 0) {
            for (int i = 0; i < intentSArray.size(); i++) {
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

        AlarmManager[] alarmManager = new AlarmManager[globalStudyTime.size()];
        intentSArray = new ArrayList<PendingIntent>();
        for (int f = 0; f < globalStudyTime.size(); f++) {
            Intent intent = new Intent(this, AlarmReceiver.class);
            PendingIntent pi = PendingIntent.getBroadcast(this, f, intent, 0);

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
        System.out.println("It cancels the alarm");
        AlarmManager[] alarmManager = new AlarmManager[globalSleepWakeCalendar.length];
        if (intentSWArray.size() > 0) {
            for (int i = 0; i < intentSWArray.size(); i++) {
                alarmManager[i] = (AlarmManager) getSystemService(ALARM_SERVICE);
                alarmManager[i].cancel(intentSWArray.get(i));
            }
            intentSWArray.clear();
        }
    }


    private void startSleepWakeAlarm() {
        ArrayList<PendingIntent> intentArray = new ArrayList<>();
        alarmIntent = new Intent[globalSleepWakeCalendar.length];
        for (int i = 0; i < globalSleepWakeCalendar.length; i++) {
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

//            alarmManager[i] = (AlarmManager) getSystemService(ALARM_SERVICE);
//            alarmManager[i].setExact(AlarmManager.RTC_WAKEUP, globalSleepWakeCalendar[i].getTimeInMillis() ,pi);

//            intentSWArray.add(pi);
//        }
//    }

    public void goBack(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void saveData() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(WAKETIMER, wakeTimer.getText().toString());
        editor.putString(SLEEPTIMER, sleepTimer.getText().toString());
        editor.putInt(SPINNER1, spinner1.getSelectedItemPosition());
        editor.putInt(SPINNER2, spinner2.getSelectedItemPosition());
        editor.putBoolean(SLEEPWAKESWITCH, sleepWakeSwitch.isChecked());
        editor.putBoolean(STUDYSWITCH, studyTimSwitch.isChecked());
        editor.putBoolean(TESTSWITCH, testTimeSwitch.isChecked());
        editor.putInt(TIMEBEFORESTUDY,studyBefore);
        editor.putInt(TIMEBEFORETEST, testBefore);
        editor.putInt(WAKEHOUR, wakeHour);
        editor.putInt(WAKEMIN, wakeMinute);
        editor.putInt(SLEEPHOUR, sleepHour);
        editor.putInt(SLEEPMIN, sleepMinute);
        editor.apply();
        Toast.makeText(this, "Data saved", Toast.LENGTH_SHORT).show();
    }

    private void updateViews() {
        wakeTimer.setText(wakeTime);
        sleepTimer.setText(sleepTime);
        sleepWakeSwitch.setChecked(sleepWakeSwitchOnOff);
        studyTimSwitch.setChecked(studyTimSwitchOnOFf);
        testTimeSwitch.setChecked(testTimeSwitchOnOff);
        spinner1.setSelection(spinnerOne);
        spinner2.setSelection(spinnerTwo);
        studyBefore=newStudyBefore;
        testBefore=newTestBefore;
        wakeHour=newWakeHour;
        wakeMinute=newWakeMinute;
        sleepHour=newSleepHour;
        sleepMinute=newWakeMinute;
    }

    private void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);

        wakeTime = sharedPreferences.getString(WAKETIMER, "00:00 AM");
        sleepTime = sharedPreferences.getString(SLEEPTIMER, "00:00 AM");
        sleepWakeSwitchOnOff = sharedPreferences.getBoolean(SLEEPWAKESWITCH, false);
        studyTimSwitchOnOFf = sharedPreferences.getBoolean(STUDYSWITCH, false);
        testTimeSwitchOnOff = sharedPreferences.getBoolean(TESTSWITCH, false);
        spinnerOne = sharedPreferences.getInt(SPINNER1, 1);
        spinnerTwo = sharedPreferences.getInt(SPINNER2, 1);
        newStudyBefore = sharedPreferences.getInt(TIMEBEFORESTUDY, 60);
        newTestBefore = sharedPreferences.getInt(TIMEBEFORETEST, 3);
        newWakeHour = sharedPreferences.getInt(WAKEHOUR, 0);
        newWakeMinute = sharedPreferences.getInt(WAKEMIN, 0);
        newSleepHour = sharedPreferences.getInt(SLEEPHOUR, 0);
        newSleepMinute = sharedPreferences.getInt(SLEEPMIN, 0);
    }

        private void updateAlarm()
        {
            // turn off the alarm
            Intent intent1 = new Intent(this, AlarmService.class);
            stopService(intent1);

            boolean sleepAwake = sleepWakeSwitch.isChecked();
            boolean study = studyTimSwitch.isChecked();
            boolean test = testTimeSwitch.isChecked();

            // if one of the switches is on, turn on to update the alarm
            if (sleepAwake || study || test) {
                Intent intent = new Intent(this, AlarmService.class);
                intent.putExtra(SLEEPWAKESWITCH, sleepAwake);
                intent.putExtra(STUDYSWITCH, study);
                intent.putExtra(TESTSWITCH, test);

                // time for sleep and wake alarm
                intent.putExtra(SLEEPHOUR, sleepHour);
                intent.putExtra(SLEEPMIN, sleepMinute);
                intent.putExtra(WAKEHOUR, wakeHour);
                intent.putExtra(WAKEMIN, wakeMinute);

                // time before alarm
                intent.putExtra(STUDY_BEFORE, studyBefore);
                intent.putExtra(TEST_BEFORE, testBefore);
                startService(intent);
            }
        }
    }




        }
    }
}