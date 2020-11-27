package com.example.timely.timetable;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.timely.DatabaseHelper;
import com.example.timely.ItemDetailsActivity;
import com.example.timely.MainActivity;
import com.example.timely.R;
import com.example.timely.courses.Course;
import com.example.timely.courses.StudyTime;
import com.example.timely.timetablemaker.TimetableMakerActivity;

import java.util.ArrayList;
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


    // create random courses
    public static ArrayList<Course> randomCourses()
    {
        // sample courses
        Course course1 = new Course("MATH 2110", null,0, "Sean");
        ArrayList<StudyTime> time = new ArrayList<>();
        time.add(new StudyTime(0, 9, 15, 60, course1.getId()));
        time.add(new StudyTime(1, 14, 0, 60, course1.getId()));
        time.add(new StudyTime(2, 12, 0, 60, course1.getId()));
        time.add(new StudyTime(4, 13, 0, 45, course1.getId()));
        course1.setTime(time);

        Course course2 = new Course("COMP 2130", null, 2, "Babinchuk");
        time = new ArrayList<>();
        time.add(new StudyTime(0, 10, 15, 75, course2.getId()));
        time.add(new StudyTime(2, 9, 15, 75, course2.getId()));
        time.add(new StudyTime(4, 9, 15, 75, course2.getId()));
        course2.setTime(time);

        Course course3 = new Course("COMP 2160", null, 1, "Aras");
        time = new ArrayList<>();
        time.add(new StudyTime(0, 11, 30, 75, course3.getId()));
        time.add(new StudyTime(2, 10, 30, 75, course3.getId()));
        time.add(new StudyTime(3, 12, 45, 60, course3.getId()));
        course3.setTime(time);

        Course course4 = new Course("COMP 2920", null, 0, "Sharma");
        time = new ArrayList<>();
        time.add(new StudyTime(0, 13, 30, 75, course4.getId()));
        time.add(new StudyTime(1, 9, 15, 75, course4.getId()));
        time.add(new StudyTime(3, 13, 45, 60, course4.getId()));
        time.add(new StudyTime(4, 10, 30, 75, course4.getId()));
        course4.setTime(time);

        Course course5 = new Course("ENGL 1100", null, 1, "Erik");
        time = new ArrayList<>();
        time.add(new StudyTime(0, 14, 45, 60, course5.getId()));
        time.add(new StudyTime(1, 10, 30, 60, course5.getId()));
        time.add(new StudyTime(3, 10, 30, 60, course5.getId()));
        course5.setTime(time);

        Course course6 = new Course("MINH 2211", null, 1, "Erik");
        time = new ArrayList<>();
        time.add(new StudyTime(0, 15, 0, 60, course6.getId()));
        time.add(new StudyTime(1, 10, 50, 60, course6.getId()));
        time.add(new StudyTime(3, 10, 30, 60, course6.getId()));
        course6.setTime(time);

        ArrayList<Course> list = new ArrayList<>();
        list.add(course1);
        list.add(course2);
        list.add(course3);
        list.add(course4);
        list.add(course6);
        list.add(course5);

        return list;
    }


    public void goBack(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}