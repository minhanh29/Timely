package com.example.timely.courses;

import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.util.JsonWriter;

import java.util.ArrayList;

public class Course {
    private static int courseCount = 0;
    private String name;
    private ArrayList<StudyTime> time;
    private Integer section;
    private String instructor;
    private String id;

    public Course() {
        name = "undefined";
        time = null;
        section = 0;
        instructor = "undefined";
        courseCount++;
    }

    public Course(String id, String name, ArrayList<StudyTime> time, Integer section, String instructor) {
        this.name = name;
        this.time = time;
        this.section = section;
        this.instructor = instructor;
        this.id = id;
        courseCount++;
    }

    public Course(String name, ArrayList<StudyTime> time, Integer section, String instructor) {
        this.name = name;
        this.time = time;
        this.section = section;
        this.instructor = instructor;
        courseCount++;
        generateID();
    }


    // generate courseId
    public void generateID() {
        id = "CS";
        String s[] = name.split(" ");
        for (int i = 0; i < s.length; i++)
            id += s[i];
        id += section;
        for (int i = 0; i < instructor.length() && i < 2; i++)
            id += instructor.charAt(i);
        id += courseCount;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<StudyTime> getTime() {
        return time;
    }

    public void setTime(ArrayList<StudyTime> time) {
        this.time = time;
    }

    public Integer getSection() {
        return section;
    }

    public void setSection(Integer section) {
        this.section = section;
    }

    public String getInstructor() {
        return instructor;
    }

    public void setInstructor(String instructor) {
        this.instructor = instructor;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String toString() {
        return "ID: " + id + ", Name: " + name + ", Section: " + section + ", Instructor: " + instructor;
    }

    // check if two courses have conflicts
    public boolean isConflict(Course c1) {
        ArrayList<StudyTime> time1 = c1.getTime();

        for (int i = 0; i < time1.size(); i++) {
            for (int j = 0; j < time.size(); j++) {
                if (time1.get(i).isOverlap(time.get(j)))
                    return true;
            }
        }

        return false;
    }


    @Override
    public boolean equals(@Nullable Object obj) {
        if (!(obj instanceof Course))
            return false;

        Course c = (Course) obj;

        return id.equals(c.id);
    }
}
