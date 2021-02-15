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

    private static final String IS_FIRST_LOAD = "IS_FIRST_LOAD";

    DatabaseHelper db;
    String courseId;
    Animation animation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study_time);

        db = new DatabaseHelper(this, DatabaseHelper.TEMP_DATABASE);

        // get the course id
        Intent intent = getIntent();
        courseId = intent.getStringExtra(TimetableMakerActivity.COURSE_ID);
        boolean isNewCourse = intent.getBooleanExtra(TimetableMakerActivity.IS_NEW_COURSE, false);
        boolean isFirstLoad = true;
        if (savedInstanceState != null)
        {
            isNewCourse = savedInstanceState.getBoolean(TimetableMakerActivity.IS_NEW_COURSE);
            isFirstLoad = savedInstanceState.getBoolean(IS_FIRST_LOAD, true);
        }

        if (isNewCourse)
        {
            TimeRowFragment timeRowFragment = new TimeRowFragment();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(R.id.timeLayout, timeRowFragment).commit();
        }


        if (isFirstLoad)
        {
            // initial study time
            ArrayList<StudyTime> studyTimes = db.getAllStudyTime(courseId);
            for (int i = 0; i < studyTimes.size(); i++)
                updateStudyTime(studyTimes.get(i));

            if (studyTimes.size() == 0)
                updateStudyTime(null);
        }

        // animations
        animation = new AlphaAnimation(1F, 0.5F);
        animation.setDuration(100L);
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(TimetableMakerActivity.IS_NEW_COURSE, false);
        outState.putBoolean(IS_FIRST_LOAD, false);
    }

    public void addStudyTime(View view) {
        view.startAnimation(animation);

        TimeRowFragment timeRowFragment = new TimeRowFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.timeLayout, timeRowFragment).commit();
    }

    // display study time
    private void updateStudyTime(StudyTime studyTime) {
        if (studyTime == null)
            return;

        TimeRowFragment timeRowFragment = new TimeRowFragment();
        timeRowFragment.setStudyTime(studyTime);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.timeLayout, timeRowFragment).commit();
    }

    @Override
    public void onDeleteStudyTimeFragment(Fragment fragment) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.remove(fragment).commit();
    }

    // save all study times to the database
    public void saveStudyTime(View view)
    {
        view.startAnimation(animation);

        // reset data for this course id;
        db.deleteAllStudyTime(courseId);

        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            if (fragment instanceof TimeRowFragment) {
                StudyTime time = ((TimeRowFragment) fragment).getStudyTime();
                time.setCourseId(courseId);
                db.addStudyTime(time);
            }
        }
    }


    // go back to previous activity
    public void goBack(View view)
    {
        view.startAnimation(animation);
        finish();
    }
}

