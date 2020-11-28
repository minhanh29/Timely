package com.example.timely.timetable;

import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.timely.R;
import com.example.timely.courses.Course;
import com.example.timely.courses.StudyTime;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.Queue;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ScheduleFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ScheduleFragment extends Fragment {

    private final int HOUR = 180;   // height of a course view for 1 hour in dp
    private final int START_HOUR = 6;  // the default earliest hour in the timetable
    private Queue<Integer> courseColors, selectedColors;   // colors background for courses
    private ArrayList<CourseView> courseViews;
    ConstraintLayout[] daysLayout;
    TextView[] dayHeader;
    ScrollView scrollView;
    private int startPos;


    public static ScheduleFragment newInstance(String param1, String param2) {
        ScheduleFragment fragment = new ScheduleFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_schedule, container, false);

        courseViews = new ArrayList<>();
        startPos = HOUR * 24;
        scrollView = view.findViewById(R.id.parent_scroll_view);

        // initialize day headers
        dayHeader = new TextView[5];
        dayHeader[0] = view.findViewById(R.id.monday);
        dayHeader[1] = view.findViewById(R.id.tuesday);
        dayHeader[2] = view.findViewById(R.id.wednesday);
        dayHeader[3] = view.findViewById(R.id.thursday);
        dayHeader[4] = view.findViewById(R.id.friday);
        boldHeader();

        // initialize day layouts
        daysLayout = new ConstraintLayout[5];
        daysLayout[0] = view.findViewById(R.id.monday_layout);
        daysLayout[1] = view.findViewById(R.id.tuesday_layout);
        daysLayout[2] = view.findViewById(R.id.wednesday_layout);
        daysLayout[3] = view.findViewById(R.id.thursday_layout);
        daysLayout[4] = view.findViewById(R.id.friday_layout);

        // initialize course colors
        courseColors = new LinkedList<Integer>();
        courseColors.offer(R.drawable.course1_bg);
        courseColors.offer(R.drawable.course2_bg);
        courseColors.offer(R.drawable.course3_bg);
        courseColors.offer(R.drawable.course4_bg);
        courseColors.offer(R.drawable.course5_bg);
        courseColors.offer(R.drawable.course6_bg);
        courseColors.offer(R.drawable.course7_bg);

        // selected colors
        selectedColors = new LinkedList<>();
        selectedColors.offer(R.drawable.course1_select_bg);
        selectedColors.offer(R.drawable.course2_select_bg);
        selectedColors.offer(R.drawable.course3_select_bg);
        selectedColors.offer(R.drawable.course4_select_bg);
        selectedColors.offer(R.drawable.course5_select_bg);
        selectedColors.offer(R.drawable.course6_select_bg);
        selectedColors.offer(R.drawable.course7_select_bg);

        return view;
    }


    // receive courses and draw them to the screen
    public void updateCourses(ArrayList<Course> courses)
    {
        startPos = HOUR * 24;
        // clear all old courses
        for (int i = 0; i < daysLayout.length; i++)
            daysLayout[i].removeAllViewsInLayout();

        for (int i = 0; i < courses.size(); i++)
        {
            // get a course
            Course current = courses.get(i);

            // retrieve values
            String name = current.getName();
            ArrayList<StudyTime> time = current.getTime();

            // draw the course
            int bg = courseColors.poll();
            courseColors.offer(bg);  // reuse
            int selectBg = selectedColors.poll();
            selectedColors.offer(selectBg);
            for (int scan = 0; scan < time.size(); scan++)
            {
                CourseView.OnCourseSelected selector = (CourseView.OnCourseSelected) getActivity();
                Typeface tf = getActivity().getResources().getFont(R.font.antic);
                CourseView courseView = new CourseView(getContext(), name, time.get(scan), bg, selectBg, tf, selector);
                courseView.drawView(daysLayout);

                // add to the list
                courseViews.add(courseView);

                // find the start position
                int startTime = (int) ((time.get(scan).getHour() - START_HOUR + (double) time.get(scan).getMinute()/60.0) * HOUR);
                if (startTime < startPos)
                    startPos = startTime;
            }
        }
    }


    public void scroll()
    {
        // update the start point
        scrollView.setScrollY(startPos);
    }


    // unselect all courses
    public void unselectAll()
    {
        for (int i = 0; i < courseViews.size(); i++)
        {
            courseViews.get(i).unselect();
        }
    }


    // update the day of week to bold a header
    private void boldHeader()
    {
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);

        // unbold all headers
        for (int i = 0; i < dayHeader.length; i++)
            dayHeader[i].setTextColor(Color.GRAY);

        // out of range
        if (day < 2 || day > 6)
            return;

        dayHeader[day-2].setTextColor(Color.BLACK);
    }
}