package com.example.timely.settings;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.timely.DatabaseHelper;
import com.example.timely.R;
import com.example.timely.courses.StudyTime;

import java.util.ArrayList;
import java.util.Calendar;

public class NotificationSettingsActivity extends AppCompatActivity{

    TextView wakeTimer, sleepTimer;
    int wakeHour, wakeMinute, sleepHour, sleepMinute;
    AlarmManager alarmManager;
    PendingIntent pendingIntent;
    DatabaseHelper db;
    ArrayList<StudyTime> studyTimes;
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_settings);
        wakeTimer = (TextView) findViewById(R.id.wakeTime);
        sleepTimer = (TextView) findViewById(R.id.sleepTime);
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        db = new DatabaseHelper(NotificationSettingsActivity.this);

        final Intent intent = new Intent(NotificationSettingsActivity.this, AlarmReceiver.class);

        Spinner spinner1 = findViewById(R.id.studySpinner);
        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this,
                R.array.timeBeforeClass, android.R.layout.simple_spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner1.setAdapter(adapter1);
        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id){
                studyTimes=db.getAllStudyTime();
                Calendar calendar = Calendar.getInstance();
                for(int i=0;i<studyTimes.size();i++) {
                    StudyTime studyTime= studyTimes.get(i);
                    int hour=0 ,min =0;
                    hour+=studyTime.getHour();
                    min+=studyTime.getMinute();
                    switch (position) {
                        case 1:
                            calendar.set(0,0,0,hour-1,min);
                            break;
                        case 2:
                            calendar.set(0,0,0,hour,min-45);
                            break;
                        case 3:
                            calendar.set(0,0,0,hour,min-30);
                            break;
                        case 4:
                            calendar.set(0,0,0,hour,min-15);
                            break;
                    }
                    pendingIntent = PendingIntent.getBroadcast(
                            NotificationSettingsActivity.this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT
                    );
                    alarmManager.set(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),pendingIntent);
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
        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                studyTimes=db.getAllStudyTime();
                Calendar calendar = Calendar.getInstance();
                for(int i=0;i<studyTimes.size();i++) {
                    StudyTime studyTime= studyTimes.get(i);
                    int day=0;
                    day=studyTime.getDay();
                    if(studyTime.isHasTest()) {
                        switch (position) {
                            case 2:
                                calendar.set(0, 0, day-7, 8, 0 );
                                break;
                            case 3:
                                calendar.set(0, 0, day-3, 8, 0);
                                break;
                            case 4:
                                calendar.set(0, 0, day-1,8, 0);
                                break;
                        }
                        pendingIntent = PendingIntent.getBroadcast(
                                NotificationSettingsActivity.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT
                        );
                        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                    }
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
                                wakeHour = hourOfDay;
                                wakeMinute = minute;

                                Calendar calendar1 = Calendar.getInstance();

                                calendar1.set(0, 0, 0, wakeHour, wakeMinute);
                                pendingIntent = PendingIntent.getBroadcast(
                                        NotificationSettingsActivity.this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT
                                );
                                alarmManager.set(AlarmManager.RTC_WAKEUP,calendar1.getTimeInMillis(),pendingIntent);
                                wakeTimer.setText(DateFormat.format("hh:mm aa",calendar1));
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
                                sleepHour = hourOfDay;
                                sleepMinute = minute;

                                Calendar calendar2 = Calendar.getInstance();

                                calendar2.set(0, 0, 0, sleepHour, sleepMinute);
                                pendingIntent = PendingIntent.getBroadcast(
                                        NotificationSettingsActivity.this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT
                                );
                                alarmManager.set(AlarmManager.RTC_WAKEUP,calendar2.getTimeInMillis(),pendingIntent);
                                sleepTimer.setText(android.text.format.DateFormat.format("hh:mm aa",calendar2));
                            }
                        }, 12, 0, false
                );
                timePickerDialog.updateTime(sleepHour, sleepMinute);
                timePickerDialog.show();

            }

        });
    }


    }