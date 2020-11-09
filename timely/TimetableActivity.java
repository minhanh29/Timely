package com.example.timely;

import android.app.ActionBar;
import android.graphics.Color;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import java.util.LinkedList;
import java.util.Queue;

public class TimetableActivity extends AppCompatActivity {

    private final int HOUR = 180;   // height of a course view for 1 hour in dp
    private final int START_HOUR = 6;  // the default earliest hour in the timetable
    private Queue<Integer> courseColors;   // colors background for courses
    ConstraintLayout[] daysLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timetable);

        // initialize day layouts
        daysLayout = new ConstraintLayout[5];
        daysLayout[0] = findViewById(R.id.monday_layout);
        daysLayout[1] = findViewById(R.id.tuesday_layout);
        daysLayout[2] = findViewById(R.id.wednesday_layout);
        daysLayout[3] = findViewById(R.id.thursday_layout);
        daysLayout[4] = findViewById(R.id.friday_layout);

        // initialize course colors
        courseColors = new LinkedList<Integer>();
        courseColors.offer(R.drawable.course1_bg);
        courseColors.offer(R.drawable.course2_bg);
        courseColors.offer(R.drawable.course3_bg);
        courseColors.offer(R.drawable.course4_bg);
        courseColors.offer(R.drawable.course5_bg);

        // sample courses
        // MATH 2110
        addCourseUtility("MATH 2110",
                new int[] {0, 1, 2, 4},
                new int[] {9, 14, 12, 13},
                new int[] {15, 0, 0, 0},
                new int[] {60, 60, 60, 45},
                courseColors.poll());
        addCourseUtility("COMP 2130",
                new int[] {0, 2, 4},
                new int[] {10, 9, 9},
                new int[] {15, 15, 15},
                new int[] {75, 75, 75},
                courseColors.poll());
        addCourseUtility("COMP 2160",
                new int[] {0, 2, 3},
                new int[] {11, 10, 12},
                new int[] {30, 30, 45},
                new int[] {75, 75, 60},
                courseColors.poll());
        addCourseUtility("COMP 2920",
                new int[] {0, 1, 3, 4},
                new int[] {13, 9, 13, 10},
                new int[] {30, 15, 45, 30},
                new int[] {75, 75, 60, 75},
                courseColors.poll());
        addCourseUtility("ENGL 1100",
                new int[] {0, 1, 3},
                new int[] {14, 10, 10},
                new int[] {45, 30, 30},
                new int[] {60, 60, 60},
                courseColors.poll());
    }


    // add 1 course with fixed time for each day
    private void addCourseUtility(String name, int[] days, int hours, int minutes, int duration, int background)
    {
        for (int i = 0; i < days.length; i++)
            addCourse(name, days[i], hours, minutes, duration, background);
    }


    // add 1 course with different time for different days
    private void addCourseUtility(String name, int[] days, int[] hours, int[] minutes, int[] duration, int background)
    {
        for (int i = 0; i < days.length; i++)
            addCourse(name, days[i], hours[i], minutes[i], duration[i], background);
    }


    /*
     * create and add the view for the new course to timetable
     * @param name the course name
     * @param day the week day of the course
     * @param hour the start hour of the course
     * @param minute the start minute of the course
     * @param duration the course's duration in minutes
     */
    public void addCourse(String name, int day, int hour, int minute, int duration, int background)
    {
        // create the view
        TextView textView = new TextView(this);
        textView.setText(name);
        textView.setId(View.generateViewId());
        textView.setGravity(Gravity.CENTER_HORIZONTAL);
        textView.setTextColor(Color.WHITE);
        textView.setBackgroundResource(background);

        // compute the height based on the course's duration
        int height = (int) (duration / 60.0 * HOUR);
        textView.setHeight(height);

        // compute the start point for the view based on
        // hour and minute
        int startTime = (int) ((hour - START_HOUR + (double) minute/60.0) * HOUR);

        // and view to corresponding layout
        daysLayout[day].addView(textView, 0);

        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(daysLayout[day]);
        constraintSet.connect(textView.getId(), ConstraintSet.TOP, daysLayout[day].getId(), ConstraintSet.TOP, startTime);
        constraintSet.connect(textView.getId(), ConstraintSet.LEFT, daysLayout[day].getId(), ConstraintSet.LEFT, 0);
        constraintSet.connect(textView.getId(), ConstraintSet.RIGHT, daysLayout[day].getId(), ConstraintSet.RIGHT, 0);
        constraintSet.applyTo(daysLayout[day]);

        // set width to match_parent
        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) textView.getLayoutParams();
        layoutParams.width = ConstraintLayout.LayoutParams.MATCH_PARENT;
    }
}