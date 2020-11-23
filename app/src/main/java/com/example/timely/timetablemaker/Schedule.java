package com.example.timely.timetablemaker;

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

    public boolean isCompleted()
    {
        return courses.size() == number && !hasConflicts();
    }

    // check if there are any conflicts in this schedule
    public boolean hasConflicts()
    {
        for (int i = 0; i < courses.size(); i++)
        {
            for (int j = 0; j < courses.size(); j++)
            {
                if (i == j)
                    continue;

                // conflict if overlap time or have the same name
                if (courses.get(i).isConflict(courses.get(j)) || courses.get(i).getName() == courses.get(j).getName())
                    return true;
            }
        }

        return false;
    }


    // add course to schedule
    public void addCourse(Course course)
    {
        courses.add(course);
    }


    // remove course
    public void removeCourse(Course course)
    {
        courses.remove(course);
    }
}
