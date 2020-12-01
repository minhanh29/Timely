package com.example.timely.timetable;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.timely.DatabaseHelper;
import com.example.timely.ItemDetailsActivity;
import com.example.timely.MainActivity;
import com.example.timely.R;
import com.example.timely.timetablemaker.TimetableMakerActivity;

import java.util.Calendar;

public class TimetableActivity extends AppCompatActivity implements CourseView.OnCourseSelected {

    public static final String COURSE_ID = "CourseId";
    public static final String STUDY_TIME_ID = "StudyTimeId";
    public static final String ADD_COURSE = "ADD_COURSE";

    private String mCourseId = "";
    private int mStudyTimeId = -1;

    ScheduleFragment schedule;
    TextView header;
    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timetable);

        header = findViewById(R.id.timetable_date);
        header.setText(DateFormat.format("dd MMM yyyy", Calendar.getInstance()));
        schedule = (ScheduleFragment) getSupportFragmentManager().findFragmentById(R.id.schedule_fragment);
        db = new DatabaseHelper(TimetableActivity.this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        schedule.updateCourses(db.getAllCourses());
        Handler h = new Handler();

        h.postDelayed(new Runnable() {

            @Override
            public void run() {
                schedule.scroll();
            }
        }, 250); // 250 ms delay
    }

    public void addCourse(View view) {
        Intent intent = new Intent(this, TimetableMakerActivity.class);
        intent.putExtra(ADD_COURSE, true);
        startActivity(intent);
    }

    public void deleteCourse(View view)
    {
        if (mCourseId == null || mStudyTimeId == -1)
        {
            Toast.makeText(this, "Please select a course", Toast.LENGTH_LONG).show();
            return;
        }

        // delete the time
        db.deleteStudyTime(mStudyTimeId);

        // delete the course when there is no time
        if (db.getAllStudyTime(mCourseId).size() == 0)
            db.deleteCourse(db.getCourse(mCourseId));

        // update the screen
        schedule.updateCourses(db.getAllCourses());
    }

    public void showCourseDetail(View view)
    {
        if (mCourseId == null || mStudyTimeId == -1)
        {
            Toast.makeText(this, "Please select a course", Toast.LENGTH_LONG).show();
            return;
        }

        // move to item detail screen
        Intent intent = new Intent(this, ItemDetailsActivity.class);
        intent.putExtra(COURSE_ID, mCourseId);
        intent.putExtra(STUDY_TIME_ID, mStudyTimeId);
        startActivity(intent);
        finish();
    }

    @Override
    public void onCourseSelected(String courseId, int studyTimeId) {
        // unselect other items
        schedule.unselectAll();

        mCourseId = courseId;
        mStudyTimeId = studyTimeId;
    }


    public void goBack(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}