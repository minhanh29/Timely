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
import java.util.concurrent.TimeUnit;

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

    public static final String STUDY_BEFORE = "STUDY_BEFORE";
    public static final String TEST_BEFORE = "TEST_BEFORE";
    private int studyBefore = 0;
    private int testBefore = 0;

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


        sleepWakeSwitch = (Switch) findViewById(R.id.sleepWakeSwitch);
        sleepWakeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                updateAlarm();
//                saveData();
            }
        });

        studyTimSwitch = (Switch) findViewById(R.id.studyAlarm);
        studyTimSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                updateAlarm();
//                saveData();
            }
        });


        testTimeSwitch = (Switch) findViewById(R.id.testAlarm);
        testTimeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                updateAlarm();
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
                            studyBefore = 60;
                            break;
                        case 2:
                            calendar.set(Calendar.DAY_OF_WEEK, day);
                            calendar.set(Calendar.HOUR_OF_DAY, hour);
                            calendar.set(Calendar.MINUTE, min);
                            calendar.add(Calendar.MINUTE,-45);
                            studyBefore = 45;
                            break;
                        case 3:
                            calendar.set(Calendar.DAY_OF_WEEK, day);
                            calendar.set(Calendar.HOUR_OF_DAY, hour);
                            calendar.set(Calendar.MINUTE, min);
                            calendar.add(Calendar.MINUTE,-30);
                            studyBefore = 30;
                            break;
                        case 4:
                            calendar.set(Calendar.DAY_OF_WEEK, day);
                            calendar.set(Calendar.HOUR_OF_DAY, hour);
                            calendar.set(Calendar.MINUTE, min);
                            calendar.add(Calendar.MINUTE,-15);
                            studyBefore = 15;
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
                                testBefore = 3;
                                break;
                            case 2:
                                calendar.set(Calendar.DAY_OF_WEEK, day);
                                calendar.add(Calendar.DATE, -1);
                                testBefore = 1;
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




    public void goBack(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
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
        if (sleepAwake || study || test)
        {
            Intent intent = new Intent(this, AlarmService.class);
            intent.putExtra(SLEEPWAKESWITCH, sleepAwake);
            intent.putExtra(STUDYSWITCH, study);
            intent.putExtra(TESTSWITCH, test);

            // time before alarm
            intent.putExtra(STUDY_BEFORE, studyBefore);
            intent.putExtra(TEST_BEFORE, testBefore);
            startService(intent);
        }
    }
}


