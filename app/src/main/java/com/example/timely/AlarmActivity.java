package com.example.timely;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.timely.courses.StudyTime;
import com.example.timely.settings.AlarmService;

import java.util.ArrayList;

public class AlarmActivity extends AppCompatActivity {


    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        db = new DatabaseHelper(this);
    }

    public void setStudyAlarm(View view) {
//        ArrayList<StudyTime> times = db.getAllStudyTime();
//        for (int i = 0; i < times.size(); i++)
//        {
//            StudyTime time = times.get(i);
//        }
        Intent intent = new Intent(this, AlarmService.class);
        startService(intent);
    }

    public void stopAlarm(View view) {
        Intent intent = new Intent(this, AlarmService.class);
        stopService(intent);
    }
}