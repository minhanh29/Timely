package com.example.timely.timetablemaker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;

import com.example.timely.DatabaseHelper;
import com.example.timely.MainActivity;
import com.example.timely.R;
import com.example.timely.courses.Course;
import com.example.timely.timetable.TimetableActivity;
import com.example.timely.timetablemaker.generator.SampleScheduleActivity;

import java.util.ArrayList;

public class TimetableMakerActivity extends AppCompatActivity implements MyCourseListItemRecyclerViewAdapter.OnCourseLickListener,
        AddCourseFragment.OnAddCourseListener, ConfirmCourseFragment.OnCreateTimetableListener {

    public static final String MY_ADD_COURSE_FRAGMENT = "MY_ADD_COURSE_FRAGMENT";
    public static final String MY_CONFIRM_FRAGMENT = "MY_CONFIRM_FRAGMENT";
    public static final String COURSE_ID = "TIMETABLE_MAKER_COURSE_ID";
    public static final String NUMBER_COURSE = "NUMBER_COURSE";

    private DatabaseHelper db;
    private CourseItemFragment courseItemFragment;
    private Animation animation, animation2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timetable_maker);

        db = new DatabaseHelper(this, DatabaseHelper.TEMP_DATABASE);

        // add course from timetable activity
        Intent intent = getIntent();
        boolean isAddCourse = intent.getBooleanExtra(TimetableActivity.ADD_COURSE, false);
        if (isAddCourse)
        {
            // update database
            DatabaseHelper db2 = new DatabaseHelper(this);
            db.clearData();
            ArrayList<Course> newCourses = db2.getAllCourses();
            for (int i = 0; i < newCourses.size(); i++)
            {
                db.addCourse(newCourses.get(i));
            }

            // show add course dialog
            AddCourseFragment addCourseFragment = new AddCourseFragment();
            addCourseFragment.show(getSupportFragmentManager(), MY_ADD_COURSE_FRAGMENT);
        }

        // animations
        animation = new AlphaAnimation(1F, 0.5F);
        animation.setDuration(100L);

        animation2 = new ScaleAnimation(1f, 1.3f, 1f, 1.3f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation2.setDuration(105L);
    }

    @Override
    protected void onStart() {
        super.onStart();
        courseItemFragment = (CourseItemFragment) getSupportFragmentManager().findFragmentById(R.id.course_list);
        courseItemFragment.updateList();
    }

    public void addCourse(View view) {
        view.startAnimation(animation2);
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
    public void onCourseDelete(String courseId) {
        Course mCourse = db.getCourse(courseId);
        db.deleteCourse(mCourse);
        courseItemFragment.updateList();
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
        view.startAnimation(animation);
        ConfirmCourseFragment confirmCourseFragment = new ConfirmCourseFragment();
        confirmCourseFragment.show(getSupportFragmentManager(), MY_CONFIRM_FRAGMENT);
    }

    public void resetCourses(View view) {
        view.startAnimation(animation2);
        db.clearData();
        courseItemFragment.updateList();
    }

    public void goBack(View view) {
        view.startAnimation(animation);
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
