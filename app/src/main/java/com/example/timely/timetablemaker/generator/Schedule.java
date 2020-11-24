package com.example.timely.timetablemaker.generator;

import android.support.annotation.Nullable;

import com.example.timely.courses.Course;

import java.util.ArrayList;

public class Schedule {
    private ArrayList<Course> courses;
    private int number;

    public Schedule(int numberOfCourse)
    {
        courses = new ArrayList<>();
        number = numberOfCourse;
    }

    public Schedule(ArrayList<Course> courses, int numberOfCourses)
    {
        this(numberOfCourses);
        this.courses = courses;
    }

    // add course to schedule
    public void add(Course course)
    {
        courses.add(course);
    }


    // remove course
    public void remove(Course course)
    {
        courses.remove(course);
    }


    // check contain
    public boolean contains(Course course)
    {
        return courses.contains(course);
    }


    public int size()
    {
        return courses.size();
    }


    public Course get(int index)
    {
        return courses.get(index);
    }


    @Override
    public boolean equals(@Nullable Object obj) {
        if (!(obj instanceof Schedule))
            return false;

        Schedule s = (Schedule) obj;

        if (size() != s.size())
            return false;

        for (int i = 0; i < s.size(); i++)
        {
            if (!courses.contains(s.get(i)))
                return false;
        }
        return true;
    }


    public Schedule copy()
    {
        Schedule s = new Schedule(number);
        for (int i = 0; i < size(); i++)
        {
            s.add(courses.get(i));
        }

        return s;
    }


    public ArrayList<Course> getCourses()
    {
        return courses;
    }
}
