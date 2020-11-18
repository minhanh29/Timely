package com.example.timely.settings;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.timely.DatabaseHelper;
import com.example.timely.R;
import com.example.timely.courses.StudyTime;

import java.util.ArrayList;
import java.util.Calendar;

public class NotificationSettingsActivity extends AppCompatActivity {

    TextView wakeTimer, sleepTimer;
    int wakeHour, wakeMinute, sleepHour, sleepMinute;
    Switch sleepWakeSwitch;
    PendingIntent pendingIntent;
    DatabaseHelper db;
    ArrayList<StudyTime> studyTimes;


    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_settings);
        wakeTimer = (TextView) findViewById(R.id.wakeTime);
        sleepTimer = (TextView) findViewById(R.id.sleepTime);
        db = new DatabaseHelper(NotificationSettingsActivity.this);

        Spinner spinner1 = findViewById(R.id.studySpinner);
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
                            calendar.add(Calendar.HOUR_OF_DAY,-1);
                            break;
                        case 2:
                            calendar.set(Calendar.DAY_OF_WEEK, day);
                            calendar.set(Calendar.MINUTE, min);
                            calendar.add(Calendar.MINUTE,-45);
                            break;
                        case 3:
                            calendar.set(Calendar.DAY_OF_WEEK, day);
                            calendar.set(Calendar.HOUR_OF_DAY, hour);
                            calendar.add(Calendar.MINUTE,-30);
                            break;
                        case 4:
                            calendar.set(Calendar.DAY_OF_WEEK, day);
                            calendar.set(Calendar.MINUTE, min);
                            calendar.add(Calendar.MINUTE,-15);
                            break;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Spinner spinner2 = findViewById(R.id.testSpinner);
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
                                calendar.add(Calendar.DATE, -7);
                                break;
                            case 2:
                                calendar.set(Calendar.DAY_OF_WEEK, day);
                                calendar.add(Calendar.DATE, -3);
                                break;
                            case 3:
                                calendar.set(Calendar.DAY_OF_WEEK, day);
                                calendar.add(Calendar.DATE, -1);
                                break;
                        }
                    }
                    startAlarm(calendar);
                }
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

                                wakeTimer.setText(android.text.format.DateFormat.format("hh:mm aa", c));
                                startAlarm(c);
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
                                Calendar c = Calendar.getInstance();
                                c.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                c.set(Calendar.MINUTE, minute);
                                c.set(Calendar.SECOND, 0);

                                sleepHour = hourOfDay;
                                sleepMinute = minute;

                                startAlarm(c);
                                sleepTimer.setText(android.text.format.DateFormat.format("hh:mm aa", c));
                            }
                        }, 12, 0, false
                );
                timePickerDialog.updateTime(sleepHour, sleepMinute);
                timePickerDialog.show();
            }
        });
    }

    private void startAlarm(Calendar calendar) {
        Intent intent = new Intent(NotificationSettingsActivity.this, AlarmReceiver.class);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        pendingIntent = PendingIntent.getBroadcast(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        if (calendar.before(Calendar.getInstance())) {
            calendar.add(Calendar.DATE, 1);
        }

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }
}