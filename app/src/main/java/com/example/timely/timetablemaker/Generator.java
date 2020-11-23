package com.example.timely.timetablemaker;

import android.util.Log;

import com.example.timely.courses.Course;

import java.util.ArrayList;
import java.util.TreeMap;

public class Generator {
    private Schedule schedule;
    private ArrayList<Course> courses;
    private Variable[] variables;
    private TreeMap<Variable, Course> mAssignment;
    private int number;

    public Generator(ArrayList<Course> courses, int numberOfCourse)
    {
        this.courses = courses;
        number = numberOfCourse;
        schedule = new Schedule(number);

        variables = new Variable[number];
        initializeVariables();

        mAssignment = new TreeMap<>();
    }


    // initialize variable values
    private void initializeVariables()
    {
        for (int i = 0; i < variables.length; i++)
        {
            variables[i] = new Variable(courses);
        }
    }


    // return the list of non-conflict courses
    public ArrayList<Course> getResult()
    {
        TreeMap<Variable, Course> assignment = backtrack(mAssignment);
        Log.i("database", "assign: " + assignment);
        if (assignment != null)
        {
            ArrayList<Course> result = new ArrayList<>();
            for (Course c : assignment.values())
                result.add(c);

            return result;
        }
        return null;
    }


    public TreeMap<Variable, Course> backtrack(TreeMap<Variable, Course> assignment)
    {
        if (isCompleted(assignment))
            return assignment;

        Variable var = selectUnassignedVar(assignment);
        Log.i("database", "Variable " + var);
        ArrayList<Course> domain = domainValues(var, assignment);
        Log.i("database", "Domain size " + domain.size());
        for (Course value : domain)
        {
            if (isConsistent(value, assignment))
            {
                Log.i("database", "COnsistent " + value);
                assignment.put(var, value);
                TreeMap<Variable, Course> result = backtrack(assignment);
                if (result != null)
                    return result;
                Log.i("database", "assignment " + assignment.toString());
                assignment.remove(var);
                Log.i("database", "remove " + value);
            }

            Log.i("database", "NOT CONSIT ");
        }

        return null;
    }


    private Variable selectUnassignedVar(TreeMap<Variable, Course> assignment)
    {
         for (int i = 0; i < variables.length; i++)
         {
            if (!assignment.containsKey(variables[i]))
            {
                Log.i("database", "got it");
                return variables[i];
            }

         }

        Log.i("database", "null variable");
        return null;
    }


    private boolean isCompleted(TreeMap<Variable, Course> assignment)
    {
        return assignment.size() == number;
    }


    private boolean isConsistent(Course course, TreeMap<Variable, Course> assignment)
    {
        if (assignment.containsValue(course))
        return false;

        for (Course c : assignment.values())
        {
            if (c.isConflict(course) || c.getName().equals(course.getName()))
                return false;
        }

        return true;
    }


    private ArrayList<Course> domainValues(Variable var, TreeMap<Variable, Course> assignment)
    {
        ArrayList<Course> domain = new ArrayList<>();
        for (Course course : var.getDomain())
        {
            if (!assignment.containsValue(course))
                domain.add(course);
        }

        return domain;
    }
}
