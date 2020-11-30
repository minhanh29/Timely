package com.example.timely.timetablemaker;

import android.content.Intent;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.LinearLayout;

import com.example.timely.DatabaseHelper;
import com.example.timely.R;
import com.example.timely.courses.StudyTime;

import java.util.ArrayList;

public class StudyTimeActivity extends AppCompatActivity implements TimeRowFragment.OnDeleteStudyTimeFragmentListener {

    private static final String UPDATED = "UPDATED";

    ArrayList<TimeRowFragment> fragments;
    DatabaseHelper db;
    String courseId;
    Animation animation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study_time);

        fragments = new ArrayList<>();
        db = new DatabaseHelper(this, DatabaseHelper.TEMP_DATABASE);

        // get the course id
        Intent intent = getIntent();
        courseId = intent.getStringExtra(TimetableMakerActivity.COURSE_ID);

        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            if (fragment instanceof TimeRowFragment) {
                getSupportFragmentManager().beginTransaction().remove(fragment).commit();
            }
        }

        // initial study time
        ArrayList<StudyTime> studyTimes = db.getAllStudyTime(courseId);
        for (int i = 0; i < studyTimes.size(); i++)
            updateStudyTime(studyTimes.get(i));

        if (studyTimes.size() == 0)
            updateStudyTime(null);

        // animations
        animation = new AlphaAnimation(1F, 0.5F);
        animation.setDuration(100L);
    }


    public void addStudyTime(View view) {
        view.startAnimation(animation);

        TimeRowFragment timeRowFragment = new TimeRowFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.timeLayout, timeRowFragment).commit();

        fragments.add(timeRowFragment);
    }

    // display study time
    private void updateStudyTime(StudyTime studyTime) {
        if (studyTime == null)
            return;

        TimeRowFragment timeRowFragment = new TimeRowFragment();
        timeRowFragment.setStudyTime(studyTime);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.timeLayout, timeRowFragment).commit();

        fragments.add(timeRowFragment);
    }

    @Override
    public void onDeleteStudyTimeFragment(Fragment fragment) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.remove(fragment).commit();
        fragments.remove(fragment);
    }

    // save all study times to the database
    public void saveStudyTime(View view)
    {
        view.startAnimation(animation);

        // reset data for this course id;
        db.deleteAllStudyTime(courseId);

        for (int i = 0; i < fragments.size(); i++)
        {
            StudyTime time = fragments.get(i).getStudyTime();
            time.setCourseId(courseId);
            db.addStudyTime(time);
        }

        finish();
    }


    // go back to previous activity
    public void goBack(View view)
    {
        view.startAnimation(animation);
        finish();
    }
}

