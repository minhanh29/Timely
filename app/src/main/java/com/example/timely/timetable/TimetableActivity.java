package com.example.timely.timetable;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.timely.DatabaseHelper;
import com.example.timely.ItemDetailsActivity;
import com.example.timely.R;
import com.example.timely.courses.Course;
import com.example.timely.courses.StudyTime;
import com.example.timely.timetablemaker.Generator;

import java.util.ArrayList;

public class TimetableActivity extends AppCompatActivity implements CourseView.OnCourseSelected {

    public static final String COURSE_ID = "CourseId";
    public static final String STUDY_TIME_ID = "StudyTimeId";
    public static final int MY_REQUEST_CODE = 1;

    private String mCourseId = "";
    private int mStudyTimeId = -1;

    ScheduleFragment schedule;
    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timetable);

        schedule = (ScheduleFragment) getSupportFragmentManager().findFragmentById(R.id.schedule_fragment);
        db = new DatabaseHelper(TimetableActivity.this);
    }

    public void addCourse(View view) {
        Generator generator = new Generator(randomCourses(), 5);
        ArrayList<Course> list = generator.getResult();

        Log.i("database", "list: " + list);
        // create a database
        db.clearData();
        for (int i = 0; list != null && i < list.size(); i++)
        {
            db.addCourse(list.get(i));
        }
        Log.i("database", "Added courses");

        // show the courses
        ArrayList<Course> courses = db.getAllCourses();
        Log.i("database", "Get Course");
        Log.i("database", "size: " + courses.size());
        for (int i = 0; i < courses.size(); i++)
        {
            Log.i("database", courses.get(i).getName());
        }

        schedule.updateCourses(db.getAllCourses());
        Log.i("database", "Show course");
    }

    public void deleteCourse(View view)
    {
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
        if (mCourseId == null)
        {
            Toast.makeText(this, "Please select a course", Toast.LENGTH_LONG).show();
            return;
        }

        // move to item detail screen
        Intent intent = new Intent(this, ItemDetailsActivity.class);
        intent.putExtra(COURSE_ID, mCourseId);
        intent.putExtra(STUDY_TIME_ID, mStudyTimeId);
        startActivityForResult(intent, MY_REQUEST_CODE);
    }

    @Override
    public void onCourseSelected(String courseId, int studyTimeId) {
        // unselect other items
        schedule.unselectAll();

        if (courseId != null && studyTimeId != -1)
        {
            Course course = db.getCourse(courseId);
            StudyTime time = db.getStudyTime(studyTimeId);
            Log.i("database", course.toString());
            Log.i("database", time.toString());
        }

        mCourseId = courseId;
        mStudyTimeId = studyTimeId;
    }


    // create random courses
    private ArrayList<Course> randomCourses()
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == MY_REQUEST_CODE)
        {
            if (resultCode == Activity.RESULT_OK)
            {
                schedule.updateCourses(db.getAllCourses());
            }
        }
    }
}