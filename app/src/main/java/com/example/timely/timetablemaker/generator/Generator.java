package com.example.timely.timetablemaker.generator;

import com.example.timely.courses.Course;
import com.example.timely.courses.StudyTime;

import java.util.ArrayList;

public class Generator {
    private ArrayList<Course> courses;
    private ArrayList<Schedule> scheduleList;
    private int number;

    public Generator(ArrayList<Course> courses, int numberOfCourse)
    {
        this.courses = new ArrayList<>();
        for (int i = 0; i < courses.size(); i++)
        {
            Course course = courses.get(i);
            ArrayList<StudyTime> times = course.getTime();
            boolean isValid = true;
            for (int k = 0; k < times.size(); k++)
            {
                int hour = times.get(k).getHour();
                if (hour < 6 || hour > 17)
                {
                    isValid = false;
                    break;
                }
            }

            if (isValid)
                this.courses.add(course);
        }
        number = numberOfCourse;

        scheduleList = new ArrayList<>();
    }


    // return the list of non-conflict courses
    public ArrayList<Schedule> getResult()
    {
        backtrack(new Schedule(number));
        return scheduleList;
    }


    public void backtrack(Schedule assignment)
    {
        if (isCompleted(assignment))
        {
            if (!scheduleList.contains(assignment))
                scheduleList.add(assignment.copy());
            return;
        }

        ArrayList<Course> domain = domainValues(assignment);
        for (Course value : domain)
        {
            // add value if it is valid
            if (isConsistent(value, assignment))
            {
                assignment.add(value);
                backtrack(assignment);

                // reset the assignment status for next try
                assignment.remove(value);
            }
        }
    }


    private boolean isCompleted(Schedule assignment)
    {
        return assignment.size() == number;
    }


    private boolean isConsistent(Course course, Schedule assignment)
    {
        if (assignment.contains(course))
            return false;

        for (int i = 0; i < assignment.size(); i++)
        {
            Course c = assignment.get(i);
            if (c.isConflict(course) || c.getName().equals(course.getName()))
                return false;
        }

        return true;
    }


    private ArrayList<Course> domainValues(Schedule assignment)
    {
        ArrayList<Course> domain = new ArrayList<>();
        for (Course course : courses)
        {
            if (!assignment.contains(course))
                domain.add(course);
        }

        return domain;
    }
}
