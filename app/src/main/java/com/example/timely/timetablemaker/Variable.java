package com.example.timely.timetablemaker;

import android.support.annotation.Nullable;

import com.example.timely.courses.Course;

import java.util.ArrayList;

public class Variable implements Comparable<Variable> {
    private static int numVar = 0;
    private int id;
    private ArrayList<Course> courses;
    private Course value;

    public Variable(ArrayList<Course> yCourses)
    {
        this.courses = new ArrayList<>();
        for (int i = 0; i < yCourses.size(); i++)
        {
            courses.add(yCourses.get(i));
        }
        value = null;
        id = numVar;
        numVar++;
    }


    public ArrayList<Course> getDomain()
    {
        return courses;
    }

    public void removeCourse(Course course)
    {
        courses.remove(course);
    }

    public void addCourse(Course course)
    {
        courses.add(course);
    }

    public Course getValue() {
        return value;
    }

    public void setValue(Course value) {
        this.value = value;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == null || !(obj instanceof Course))
            return false;
        Variable var = (Variable) obj;

        return id == var.id;
    }


    @Override
    public int hashCode() {
        return id;
    }


    public String toString()
    {
        return "" + id;
    }

    @Override
    public int compareTo(Variable o) {
        return id - o.id;
    }
}
