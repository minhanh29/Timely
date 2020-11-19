package com.example.timely.timetablemaker;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.timely.DatabaseHelper;
import com.example.timely.R;
import com.example.timely.TimeRowFragment;
import com.example.timely.courses.StudyTime;

import java.sql.Time;
import java.util.ArrayList;

public class StudyTimeActivity extends AppCompatActivity implements TimeRowFragment.OnDeleteStudyTimeFragmentListener {

    ArrayList<TimeRowFragment> fragments;
    DatabaseHelper db;
    String courseId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study_time);

        fragments = new ArrayList<>();
        db = new DatabaseHelper(this);

        // get the course id
        Intent intent = getIntent();
        courseId = intent.getStringExtra(TimetableMakerActivity.COURSE_ID);

        // initial study time
        ArrayList<StudyTime> studyTimes = db.getAllStudyTime(courseId);
        for (int i = 0; i < studyTimes.size(); i++)
            updateStudyTime(studyTimes.get(i));

        if (studyTimes.size() == 0)
            updateStudyTime(null);
    }


    public void addStudyTime(View view) {
        TimeRowFragment timeRowFragment = new TimeRowFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.timeLayout, timeRowFragment).commit();

        fragments.add(timeRowFragment);
    }

    // display study time
    private void updateStudyTime(StudyTime studyTime) {
        TimeRowFragment timeRowFragment = new TimeRowFragment();
        if (studyTime != null)
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
        finish();
    }
}

