package com.example.timely;

import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.timely.courses.Course;
import com.example.timely.courses.StudyTime;

import java.util.ArrayList;
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


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ScheduleFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ScheduleFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ScheduleFragment newInstance(String param1, String param2) {
        ScheduleFragment fragment = new ScheduleFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_schedule, container, false);

        courseViews = new ArrayList<>();

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

        // selected colors
        selectedColors = new LinkedList<>();
        selectedColors.offer(R.drawable.course1_select_bg);
        selectedColors.offer(R.drawable.course2_select_bg);
        selectedColors.offer(R.drawable.course3_select_bg);
        selectedColors.offer(R.drawable.course4_select_bg);
        selectedColors.offer(R.drawable.course5_select_bg);

        return view;
    }


    // receive courses and draw them to the screen
    public void updateCourses(ArrayList<Course> courses)
    {
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

            Log.i("database", "Adding " + name);

            // draw the course
            int bg = courseColors.poll();
            courseColors.offer(bg);  // reuse
            int selectBg = selectedColors.poll();
            selectedColors.offer(selectBg);
            for (int scan = 0; scan < time.size(); scan++)
            {
                CourseView.OnCourseSelected selector = (CourseView.OnCourseSelected) getActivity();
                CourseView courseView = new CourseView(getContext(), name, time.get(scan), bg, selectBg, selector);
                courseView.drawView(daysLayout);

                // add to the list
                courseViews.add(courseView);
            }
        }
    }


    // unselect all courses
    public void unselectAll()
    {
        for (int i = 0; i < courseViews.size(); i++)
        {
            courseViews.get(i).unselect();
        }
    }
}