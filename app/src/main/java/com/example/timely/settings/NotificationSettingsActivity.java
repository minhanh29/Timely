package com.example.timely.settings;

import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.provider.AlarmClock;
import android.provider.CalendarContract;
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

import static android.provider.Telephony.Mms.Part.TEXT;

public class NotificationSettingsActivity extends AppCompatActivity {

    TextView wakeTimer, sleepTimer;
    int wakeHour, wakeMinute, sleepHour, sleepMinute;
    Switch sleepWakeSwitch, studyTimSwitch, testTimeSwitch;
    DatabaseHelper db;
    Spinner spinner1,spinner2;
    ArrayList<StudyTime> studyTimes;
    ArrayList<Calendar> globalStudyTime, globalTestTime;
    Calendar[] globalSleepWakeCalendar = new Calendar[2];


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




    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_settings);
        wakeTimer = (TextView) findViewById(R.id.wakeTime);
        sleepTimer = (TextView) findViewById(R.id.sleepTime);
        db = new DatabaseHelper(NotificationSettingsActivity.this);
        globalStudyTime= new ArrayList<>();
        globalTestTime= new ArrayList<>();

        sleepWakeSwitch = (Switch) findViewById(R.id.sleepWakeSwitch);
        sleepWakeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked == true){
                    Toast.makeText(getBaseContext(),"Sleep/Wake Alarm: ON", Toast.LENGTH_SHORT).show();
                    startSleepWakeAlarm(globalSleepWakeCalendar);
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
                    startStudyAlarm();
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
        studyTimes = db.getAllStudyTime();
        for(int i=0;i<studyTimes.size();i++) {
            Intent [] cancelIntent=new Intent[studyTimes.size()];
            cancelIntent[i].putExtra(CalendarContract.Events.DELETED, 1);
        }
    }

    private void startTestAlarm() {
        studyTimes = db.getAllStudyTime();
        for(int i=0;i<studyTimes.size();i++) {
            StudyTime studyTime=studyTimes.get(i);
            Intent[] testIntent = new Intent[studyTimes.size()];
            testIntent[i] = new Intent(Intent.ACTION_INSERT);

            testIntent[i].setData(CalendarContract.Events.CONTENT_URI);
            testIntent[i].putExtra(CalendarContract.EventDays.STARTDAY,globalTestTime.get(i).get(Calendar.DATE));
            testIntent[i].putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME,globalTestTime.get(i).getTimeInMillis());
            testIntent[i].putExtra(CalendarContract.Events.STATUS, 1);
            testIntent[i].putExtra(CalendarContract.Events.HAS_ALARM, 1);
            testIntent[i].putExtra(CalendarContract.Events.EVENT_TIMEZONE, globalTestTime.get(i).getTimeZone());
            testIntent[i].putExtra(CalendarContract.Events.DURATION,studyTime.getDuration());

            if(testIntent[i].resolveActivity(getPackageManager()) != null){
                startActivity(testIntent[i]);
            }else{
                Toast.makeText(NotificationSettingsActivity.this, "There is no app that support this action", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void cancelStudyAlarm() {
        studyTimes = db.getAllStudyTime();
        for(int i=0;i<studyTimes.size();i++)
        {
            Intent[] cancelSWAlarm = new Intent[studyTimes.size()];
            cancelSWAlarm[i] = new Intent(AlarmClock.ACTION_DISMISS_ALARM);
            startActivity(cancelSWAlarm[i]);
        }
    }

    private void startStudyAlarm() {
        studyTimes = db.getAllStudyTime();
        for(int i=0;i<studyTimes.size();i++) {
            int hour = globalStudyTime.get(i).get(Calendar.HOUR_OF_DAY);
            int min = globalStudyTime.get(i).get(Calendar.MINUTE);
            int dayOfWeek=globalStudyTime.get(i).get(Calendar.DAY_OF_WEEK);

            Intent[] alarmIntent = new Intent[studyTimes.size()];
            alarmIntent[i] = new Intent(AlarmClock.ACTION_SET_ALARM);
            alarmIntent[i].putExtra(AlarmClock.EXTRA_HOUR, hour);
            alarmIntent[i].putExtra(AlarmClock.EXTRA_MINUTES, min);
            alarmIntent[i].putExtra(AlarmClock.EXTRA_DAYS,dayOfWeek);

            startActivity(alarmIntent[i]);
        }
    }

    private void cancelSleepWakeAlarm() {
        for(int i=0;i<globalSleepWakeCalendar.length;i++)
        {
            Intent[] cancelSWAlarm = new Intent[globalSleepWakeCalendar.length];
            cancelSWAlarm[i] = new Intent(AlarmClock.ACTION_DISMISS_ALARM);
            startActivity(cancelSWAlarm[i]);
        }
    }


    private void startSleepWakeAlarm(Calendar[] c) {
        ArrayList<PendingIntent>intentArray = new ArrayList<>();
        for(int i=0;i<c.length;i++) {
            ArrayList<Integer> allDays = new ArrayList<>();
            allDays.add(2);
            allDays.add(3);
            allDays.add(4);
            allDays.add(5);
            allDays.add(6);
            int hour = c[i].get(Calendar.HOUR_OF_DAY);
            int min = c[i].get(Calendar.MINUTE);
            Intent[] alarmIntent = new Intent[c.length];
            alarmIntent[i] = new Intent(AlarmClock.ACTION_SET_ALARM);
            alarmIntent[i].putExtra(AlarmClock.EXTRA_HOUR, hour);
            alarmIntent[i].putExtra(AlarmClock.EXTRA_MINUTES, min);
            alarmIntent[i].putExtra(AlarmClock.EXTRA_DAYS,allDays);

            PendingIntent pendingIntent = PendingIntent.getActivities(NotificationSettingsActivity.this,0, alarmIntent,PendingIntent.FLAG_UPDATE_CURRENT);

            intentArray.add(pendingIntent);
        }
    }

    public void goBack(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
//
//    public void saveData() {
//        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        editor.putString(WAKETIMER, wakeTimer.getText().toString());
//        editor.putString(SLEEPTIMER, sleepTimer.getText().toString());
//        editor.putInt(SPINNER1, spinner1.getSelectedItemPosition());
//        editor.putInt(SPINNER2, spinner2.getSelectedItemPosition());
//        editor.putBoolean(SLEEPWAKESWITCH, sleepWakeSwitch.isChecked());
//        editor.putBoolean(STUDYSWITCH, studyTimSwitch.isChecked());
//        editor.putBoolean(TESTSWITCH, testTimeSwitch.isChecked());
//        editor.apply();
//        Toast.makeText(this, "Data saved", Toast.LENGTH_SHORT).show();
//    }
//
//    private void updateViews() {
//        wakeTimer.setText(wakeTime);
//        sleepTimer.setText(sleepTime);
//        sleepWakeSwitch.setChecked(sleepWakeSwitchOnOff);
//        studyTimSwitch.setChecked(studyTimSwitchOnOFf);
//        testTimeSwitch.setChecked(testTimeSwitchOnOff);
//        spinner1.setSelection(spinnerOne);
//        spinner2.setSelection(spinnerTwo);
//    }
//
//    private void loadData() {
//        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
//        wakeTime = sharedPreferences.getString(WAKETIMER, "00:00 AM");
//        sleepTime = sharedPreferences.getString(SLEEPTIMER, "00:00 AM");
//        sleepWakeSwitchOnOff = sharedPreferences.getBoolean(SLEEPWAKESWITCH, false);
//        studyTimSwitchOnOFf = sharedPreferences.getBoolean(STUDYSWITCH, false);
//        testTimeSwitchOnOff = sharedPreferences.getBoolean(TESTSWITCH, false);
//        spinnerOne = sharedPreferences.getInt(SPINNER1, 1);
//        spinnerTwo = sharedPreferences.getInt(SPINNER2, 1);
//    }
}