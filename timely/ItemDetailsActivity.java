package com.example.timely;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.timely.courses.Course;
import com.example.timely.courses.StudyTime;

public class ItemDetailsActivity extends AppCompatActivity {

    private Course course;
    private StudyTime studyTime;
    private DatabaseHelper db;
    private String courseId;
    private int studyTimeId;

    // Views
    TextView titleView, noteView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_details);

        // get views
        titleView = findViewById(R.id.noteTitle);
        noteView = findViewById(R.id.note);

        // the id's are sent from TimetableActivity
        Intent intent = getIntent();
        courseId = intent.getStringExtra(TimetableActivity.COURSE_ID);
        studyTimeId = intent.getIntExtra(TimetableActivity.STUDY_TIME_ID, -1);

        // get information from database
        db = new DatabaseHelper(this);

        updateScreen();

    }


    // update all information for the chosen item to display
    public void updateScreen()
    {
        // get the course and study time from the database
        course = db.getCourse(courseId);
        studyTime = db.getStudyTime(studyTimeId);

        // display name
        String title = course.getName();
        titleView.setText(title);

        // display note
        String note = studyTime.getNote();
        noteView.setText(note);

        // display date, study time, test, section, instructor below
    }


    public void goBack(View view)
    {
        // if there is something changed
//        this.setResult(Activity.RESULT_OK);
        // if nothing change
//        this.setResult(Activity.RESULT_CANCELED);

        // go back
        finish();
    }
}