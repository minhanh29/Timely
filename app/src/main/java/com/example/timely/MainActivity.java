package com.example.timely;

import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;

import com.example.timely.settings.NotificationSettingsActivity;
import com.example.timely.timetable.TimetableActivity;
import com.example.timely.timetablemaker.TimetableMakerActivity;

public class MainActivity extends AppCompatActivity {

    CardView timetable, generator, settings;
    Animation animation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        timetable = findViewById(R.id.timetable_card);
        generator = findViewById(R.id.generator_card);
        settings = findViewById(R.id.settings_card);

        animation = new ScaleAnimation(1f, 1.3f, 1f, 1.3f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setDuration(105L);

        timetable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(animation);
                new CountDownTimer(110, 10) {
                    public void onFinish() {
                        goToTimetable();
                    }

                    public void onTick(long millisUntilFinished) { }
                }.start();
            }
        });

        generator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(animation);
                new CountDownTimer(110, 10) {
                    public void onFinish() {
                        goToGenerator();
                    }

                    public void onTick(long millisUntilFinished) { }
                }.start();
            }
        });

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(animation);
                new CountDownTimer(110, 10) {
                    public void onFinish() {
                        goToSettings();
                    }

                    public void onTick(long millisUntilFinished) { }
                }.start();
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
