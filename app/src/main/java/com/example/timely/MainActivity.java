package com.example.timely;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;

import com.example.timely.courses.StudyTime;
import com.example.timely.settings.NotificationSettingsActivity;
import com.example.timely.timetable.TimetableActivity;
import com.example.timely.timetablemaker.StudyTimeActivity;
import com.example.timely.timetablemaker.TimetableMakerActivity;

public class MainActivity extends AppCompatActivity {

    CardView timetable, generator, settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        timetable = findViewById(R.id.timetable_card);
        generator = findViewById(R.id.generator_card);
        settings = findViewById(R.id.settings_card);

        timetable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToTimetable();
            }
        });

        generator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToGenerator();
            }
        });

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToSettings();
            }
        });
//        Intent intent = new Intent(this, TimetableMakerActivity.class);
//        startActivity(intent);
    }

    public void goToTimetable() {
        Intent intent = new Intent(this, TimetableActivity.class);
        startActivity(intent);
        finish();
    }

    public void goToGenerator() {
        Intent intent = new Intent(this, TimetableMakerActivity.class);
        startActivity(intent);
        finish();
    }

    public void goToSettings() {
        Intent intent = new Intent(this, NotificationSettingsActivity.class);
        startActivity(intent);
        finish();
    }
}
