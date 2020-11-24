package com.example.timely.timetablemaker.generator;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.timely.DatabaseHelper;
import com.example.timely.R;
import com.example.timely.courses.Course;
import com.example.timely.timetable.CourseView;
import com.example.timely.timetable.ScheduleFragment;
import com.example.timely.timetable.TimetableActivity;
import com.example.timely.timetablemaker.TimetableMakerActivity;

import java.util.ArrayList;

public class SampleScheduleActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, CourseView.OnCourseSelected {

    TextView totalText;
    Spinner spinner;
    DatabaseHelper db;
    ArrayList<Schedule> schedules;
    ScheduleFragment scheduleFragment;
    int numberOfCourse, version;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample_schedule);

        // get number of course fro TimetableMakerActivity
        Intent intent = getIntent();
        numberOfCourse = intent.getIntExtra(TimetableMakerActivity.NUMBER_COURSE, 0);

        // update schedule data and generate schedules
        db = new DatabaseHelper(this, DatabaseHelper.TEMP_DATABASE);
        scheduleFragment = (ScheduleFragment) getSupportFragmentManager().findFragmentById(R.id.sample_schedule_frg);
        generateSchedule();

        // initialize spinner
        spinner = findViewById(R.id.version_spinner);
        Integer[] numberArray = new Integer[schedules.size()];
        for (int i = 0; i < numberArray.length; i++)
            numberArray[i] = i+1;

        ArrayAdapter<Integer> adapter = new ArrayAdapter<Integer>(this, android.R.layout.simple_spinner_item, numberArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        totalText = findViewById(R.id.totalText);
        totalText.setText("Total: " + schedules.size());
    }


    @Override
    protected void onStart() {
        super.onStart();
        if (schedules.size() > 0)
        {
            version = 0;
            scheduleFragment.updateCourses(schedules.get(version).getCourses());
        }
        else
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.schedule_message);
            builder.setPositiveButton("Go Back", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    SampleScheduleActivity.this.finish();
                }
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        version = position;
        scheduleFragment.updateCourses(schedules.get(version).getCourses());
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void goBack(View view) {
        finish();
    }


    private void generateSchedule()
    {
        Generator generator = new Generator(db.getAllCourses(), numberOfCourse);
        schedules = generator.getResult();
    }

    @Override
    public void onCourseSelected(String courseId, int studyTimeId) {
        scheduleFragment.unselectAll();
    }


    // mark schedule as official
    public void applySchedule(View view) {
        DatabaseHelper officialDb = new DatabaseHelper(SampleScheduleActivity.this);
        officialDb.clearData();
        ArrayList<Course> courses = schedules.get(version).getCourses();
        for (int i = 0; i < courses.size(); i++)
        {
            officialDb.addCourse(courses.get(i));
        }

        Intent intent = new Intent(this, TimetableActivity.class);
        startActivity(intent);
        finish();
    }
}
