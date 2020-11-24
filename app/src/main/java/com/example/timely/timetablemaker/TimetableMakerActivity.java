package com.example.timely.timetablemaker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.timely.DatabaseHelper;
import com.example.timely.MainActivity;
import com.example.timely.R;
import com.example.timely.courses.Course;
import com.example.timely.timetable.TimetableActivity;
import com.example.timely.timetablemaker.generator.SampleScheduleActivity;

public class TimetableMakerActivity extends AppCompatActivity implements MyCourseListItemRecyclerViewAdapter.OnCourseLickListener,
        AddCourseFragment.OnAddCourseListener, ConfirmCourseFragment.OnCreateTimetableListener {

    public static final String MY_ADD_COURSE_FRAGMENT = "MY_ADD_COURSE_FRAGMENT";
    public static final String MY_CONFIRM_FRAGMENT = "MY_CONFIRM_FRAGMENT";
    public static final String COURSE_ID = "TIMETABLE_MAKER_COURSE_ID";
    public static final String NUMBER_COURSE = "NUMBER_COURSE";
    public static final int STUDY_TIME_CODE = 505045;

    private DatabaseHelper db;
    private CourseItemFragment courseItemFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timetable_maker);

        db = new DatabaseHelper(this, DatabaseHelper.TEMP_DATABASE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        courseItemFragment = (CourseItemFragment) getSupportFragmentManager().findFragmentById(R.id.course_list);
    }

    public void addCourse(View view) {
        AddCourseFragment addCourseFragment = new AddCourseFragment();
        addCourseFragment.show(getSupportFragmentManager(), MY_ADD_COURSE_FRAGMENT);
    }

    @Override
    public void onCourseLick(String courseId) {
        // go to StudyTime Screen
        Intent intent = new Intent(this, StudyTimeActivity.class);
        intent.putExtra(COURSE_ID, courseId);
        startActivity(intent);
    }

    @Override
    public void onAddCourse(Course course) {
        db.addCourse(course);
        courseItemFragment.updateList();

        // go to StudyTime Screen
        Intent intent = new Intent(this, StudyTimeActivity.class);
        intent.putExtra(COURSE_ID, course.getId());
        startActivity(intent);
    }

    @Override
    public void onCreateTimetable(int numberOfCourse) {
        Intent intent = new Intent(this, SampleScheduleActivity.class);
        intent.putExtra(NUMBER_COURSE, numberOfCourse);
        startActivity(intent);
    }

    public void startCreating(View view) {
        ConfirmCourseFragment confirmCourseFragment = new ConfirmCourseFragment();
        confirmCourseFragment.show(getSupportFragmentManager(), MY_CONFIRM_FRAGMENT);
    }

    public void resetCourses(View view) {
        db.clearData();
        courseItemFragment.updateList();
    }

    public void goBack(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
