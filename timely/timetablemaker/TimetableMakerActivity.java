package com.example.timely.timetablemaker;

        import android.content.Intent;
        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.view.View;

        import com.example.timely.DatabaseHelper;
        import com.example.timely.R;
        import com.example.timely.courses.Course;
        import com.example.timely.courses.StudyTime;

public class TimetableMakerActivity extends AppCompatActivity implements MyCourseListItemRecyclerViewAdapter.OnCourseLickListener, AddCourseFragment.OnAddCourseListener {

    public static final String MY_ADD_COURSE_FRAGMENT = "MY_ADD_COURSE_FRAGMENT";
    public static final String COURSE_ID = "TIMETABLE_MAKER_COURSE_ID";
    public static final int STUDY_TIME_CODE = 505045;

    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timetable_maker);

        db = new DatabaseHelper(this);
    }

    public void addCourse(View view) {
        AddCourseFragment addCourseFragment = new AddCourseFragment();
        addCourseFragment.show(getSupportFragmentManager(), MY_ADD_COURSE_FRAGMENT);
    }

    @Override
    public void onCourseLick(String courseId) {
        Intent intent = new Intent(this, StudyTimeActivity.class);
        intent.putExtra(COURSE_ID, courseId);
        startActivity(intent);
    }

    @Override
    public void onAddCourse(Course course) {
        db.addCourse(course);
        CourseItemFragment courseItemFragment = (CourseItemFragment) getSupportFragmentManager().findFragmentById(R.id.course_list);
        courseItemFragment.updateList();
    }
}
