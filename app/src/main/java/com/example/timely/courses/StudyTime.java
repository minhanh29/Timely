package com.example.timely.courses;


public class StudyTime {
    private int day;  // week day 0 = Monday, 1 = Tuesday, etc.
    private int hour;  //start hour (0 to 24)
    private int minute;  //start minute
    private int duration;  // in minutes
    private boolean hasTest;
    private String note;
    private String imagePath;
    private String courseId;
    private int id;

    public StudyTime(int id, int day, int hour, int minute, int duration, String note, String imagePath, boolean hasTest, String courseId) {
        this.day = day;
        this.hour = hour;
        this.minute = minute;
        this.duration = duration;
        this.hasTest = hasTest;
        this.note = note;
        this.imagePath = imagePath;
        this.courseId = courseId;
        this.id = id;

    }

    public StudyTime(int day, int hour, int minute, int duration, String note, String imagePath, boolean hasTest, String courseId) {
        id = -1;
        this.day = day;
        this.hour = hour;
        this.minute = minute;
        this.duration = duration;
        this.hasTest = hasTest;
        this.note = note;
        this.imagePath = imagePath;
        this.courseId = courseId;
    }

    public StudyTime()
    {
        id = -1;
        day = 0;
        hour = 0;
        minute = 0;
        duration = 0;
        hasTest = false;
        note = "";
        imagePath = "";
        courseId = "";
    }

    public StudyTime(int day, int hour, int minute, int duration, String courseId) {
        this();
        this.day = day;
        this.hour = hour;
        this.minute = minute;
        this.duration = duration;
        this.courseId = courseId;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isHasTest() {
        return hasTest;
    }

    public void setHasTest(boolean hasTest) {
        this.hasTest = hasTest;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }


    public String toString()
    {
        return "Id: " + id + ", day: " + day + ", hour: " + hour + ", minute: " + minute
                + ", duration: " + duration + ", note: " + note + ", test: " + hasTest
                + ", CourseId: " + courseId;
    }

    // check if this time overlap with other time
    public boolean isOverlap(StudyTime time)
    {
        if (day != time.day)
            return false;

        // start time
        int mStart = hour * 60 + minute;
        int oStart = time.hour * 60 + time.minute;

        if ((mStart + duration > oStart) && (mStart < oStart))
            return true;

        if ((oStart + duration > mStart) && (oStart < mStart))
            return true;

        return false;
    }

}
