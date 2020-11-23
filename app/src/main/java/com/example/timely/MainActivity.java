package com.example.timely;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.timely.courses.StudyTime;
import com.example.timely.settings.NotificationSettingsActivity;
import com.example.timely.timetable.TimetableActivity;
import com.example.timely.timetablemaker.StudyTimeActivity;
import com.example.timely.timetablemaker.TimetableMakerActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = new Intent(this, TimetableActivity.class);
        startActivity(intent);
    }
}
