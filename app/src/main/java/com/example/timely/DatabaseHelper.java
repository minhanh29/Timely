package com.example.timely;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

import com.example.timely.courses.Course;
import com.example.timely.courses.StudyTime;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String MAIN_DATABASE = "courses.db";
    public static final String TEMP_DATABASE = "tempcourses.db";

    // course table
    public static final String COURSE_TABLE = "COURSE_TABLE";
    public static final String COURSE_NAME = "COURSE_NAME";
    public static final String COURSE_SECTION = "COURSE_SECTION";
    public static final String COURSE_INSTRUCTOR = "COURSE_INSTRUCTOR";
    public static final String COURSE_ID = "ID";

    // item table
    public static final String ITEM_TABLE = "ITEM_TABLE";
    public static final String ITEM_DAY = "ITEM_DAY";
    public static final String ITEM_HOUR = "ITEM_HOUR";
    public static final String ITEM_MINUTE = "ITEM_MINUTE";
    public static final String ITEM_DURATION = "ITEM_DURATION";
    public static final String ITEM_NOTE = "ITEM_NOTE";
    public static final String ITEM_TEST = "ITEM_TEST";
    public static final String ITEM_ID = "ITEM_ID";
    public static final String ITEM_COURSE_ID = "ITEM_COURSE_ID";

    public DatabaseHelper(@Nullable Context context) {
        super(context, MAIN_DATABASE, null, 1);
    }

    public DatabaseHelper(@Nullable Context context, String name) {
        super(context, name, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableStatement = "CREATE TABLE " + COURSE_TABLE + "(" + COURSE_ID +" TEXT PRIMARY KEY, "
                + COURSE_NAME + " TEXT, " + COURSE_SECTION + " INTEGER, " + COURSE_INSTRUCTOR + " TEXT)";
        db.execSQL(createTableStatement);

        // create study time table
        String studyTimeTable = "CREATE TABLE " + ITEM_TABLE + "(" + ITEM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ITEM_DAY + " INTEGER, " + ITEM_HOUR + " INTEGER, " + ITEM_MINUTE + " INTEGER, " + ITEM_DURATION + " INTEGER, "
                + ITEM_NOTE + " TEXT, " + ITEM_TEST + " BOOL, " + ITEM_COURSE_ID + " INTEGER)";
        db.execSQL(studyTimeTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    // add a course to the database
    public boolean addCourse(Course course)
    {
        // access the database
        ContentValues cv = new ContentValues();

        // create content to write
        cv.put(COURSE_ID, course.getId());
        cv.put(COURSE_NAME, course.getName());
        cv.put(COURSE_SECTION, course.getSection());
        cv.put(COURSE_INSTRUCTOR, course.getInstructor());

        // add study time for this course
        ArrayList<StudyTime> time = course.getTime();
        if (time != null)
            for (int i = 0; i < time.size(); i++)
                addStudyTime(time.get(i));

        // insert course to the database
        SQLiteDatabase db = this.getWritableDatabase();
        long success = db.insert(COURSE_TABLE, null, cv);
        db.close();

        return success != -1;
    }


    // delete a course
    public boolean deleteCourse(Course course)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + COURSE_TABLE + " WHERE " + COURSE_ID + " = \'" + course.getId() + "\'";

        Cursor cursor = db.rawQuery(query, null);
        boolean success = cursor.moveToFirst();
        cursor.close();
        db.close();

        // delete all of its study time
        return deleteAllStudyTime(course.getId()) && success;
    }


    // modify the course
    public boolean modifyCourse(Course course)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        // access the database
        ContentValues cv = new ContentValues();

        // create content to write
        cv.put(COURSE_NAME, course.getName());
        cv.put(COURSE_SECTION, course.getSection());
        cv.put(COURSE_INSTRUCTOR, course.getInstructor());

        long success = db.update(COURSE_TABLE, cv, COURSE_ID + " = \'" + course.getId() + "\'", null);
        db.close();

        return success != -1;
    }


    // return a course based on its id
    public Course getCourse(String courseId)
    {
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM " + COURSE_TABLE + " WHERE " + COURSE_ID + " = \'" + courseId + "\'";

        Cursor cursor = db.rawQuery(query, null);

        Course course = null;
        if (cursor.moveToFirst())
        {
            String id = cursor.getString(0);
            String name = cursor.getString(1);
            int section = cursor.getInt(2);
            String instructor = cursor.getString(3);

            ArrayList<StudyTime> time = getAllStudyTime(id);

            course =  new Course(id, name, time, section, instructor);
        }

        cursor.close();
        db.close();

        return course;
    }


    // return the list of courses
    public ArrayList<Course> getAllCourses()
    {
        ArrayList<Course> list = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();

        String queryString = "SELECT * FROM " + COURSE_TABLE;

        Cursor cursor = db.rawQuery(queryString, null);

        // if there is something in the database
        if (cursor.moveToFirst())
        {
            // retrieve the information
            do {
                String id = cursor.getString(0);
                String name = cursor.getString(1);
                int section = cursor.getInt(2);
                String instructor = cursor.getString(3);

                ArrayList<StudyTime> time = getAllStudyTime(id);

                Course course = new Course(id, name, time, section, instructor);
                list.add(course);
            } while (cursor.moveToNext());
        }

        // clean up
        cursor.close();
        db.close();

        return list;
    }


    /* ITEM TABLE */

    // study time tables
    public boolean addStudyTime(StudyTime studyTime)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(ITEM_DAY, studyTime.getDay());
        cv.put(ITEM_HOUR, studyTime.getHour());
        cv.put(ITEM_MINUTE, studyTime.getMinute());
        cv.put(ITEM_DURATION, studyTime.getDuration());
        cv.put(ITEM_NOTE, studyTime.getNote());
        cv.put(ITEM_TEST, studyTime.isHasTest());
        cv.put(ITEM_COURSE_ID, studyTime.getCourseId());

        long success = db.insert(ITEM_TABLE, null, cv);
        db.close();

        return success != -1;
    }

    // modify the study time
    public boolean modifyStudyTime(StudyTime studyTime)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(ITEM_DAY, studyTime.getDay());
        cv.put(ITEM_HOUR, studyTime.getHour());
        cv.put(ITEM_MINUTE, studyTime.getMinute());
        cv.put(ITEM_DURATION, studyTime.getDuration());
        cv.put(ITEM_NOTE, studyTime.getNote());
        cv.put(ITEM_TEST, studyTime.isHasTest());

        long success = db.update(ITEM_TABLE, cv, ITEM_ID + " = " + studyTime.getId(), null);
        db.close();

        return success != -1;

    }

    // delete a study time give its id
    public boolean deleteStudyTime(int id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + ITEM_TABLE + " WHERE " + ITEM_ID + " = " + id;

        Cursor cursor = db.rawQuery(query, null);
        boolean success = cursor.moveToFirst();
        cursor.close();
        db.close();

        return success;
    }

    // delete all study time of a course
    public boolean deleteAllStudyTime(String courseId)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + ITEM_TABLE + " WHERE " + ITEM_COURSE_ID + " = \'" + courseId + "\'";

        Cursor cursor = db.rawQuery(query, null);
        boolean success = cursor.moveToFirst();
        cursor.close();
        db.close();

        return success;
    }

    // return the study time based on its id
    public StudyTime getStudyTime(int studyTimeId)
    {
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM " + ITEM_TABLE + " WHERE " + ITEM_ID + " = " + studyTimeId;

        Cursor cursor = db.rawQuery(query, null);

        StudyTime time = null;
        if (cursor.moveToFirst())
        {
            int id = cursor.getInt(0);
            int day = cursor.getInt(1);
            int hour = cursor.getInt(2);
            int minute = cursor.getInt(3);
            int duration = cursor.getInt(4);
            String note = cursor.getString(5);
            boolean hasTest = cursor.getInt(6) == 0 ? false : true;
            String courseId = cursor.getString(7);

            time = new StudyTime(id, day, hour, minute, duration, note, hasTest, courseId);
        }

        cursor.close();
        db.close();

        return time;

    }

    // return all study time in the table
    public ArrayList<StudyTime> getAllStudyTime()
    {
        ArrayList<StudyTime> list = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + ITEM_TABLE;

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst())
        {
            do {
                int id = cursor.getInt(0);
                int day = cursor.getInt(1);
                int hour = cursor.getInt(2);
                int minute = cursor.getInt(3);
                int duration = cursor.getInt(4);
                String note = cursor.getString(5);
                boolean hasTest = cursor.getInt(6) == 0 ? false : true;
                String courseId = cursor.getString(7);

                StudyTime time = new StudyTime(id, day, hour, minute, duration, note, hasTest, courseId);
                list.add(time);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return list;
    }

    // return the list of study time for a course
    public ArrayList<StudyTime> getAllStudyTime(String courseId)
    {
        ArrayList<StudyTime> list = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + ITEM_TABLE + " WHERE " + ITEM_COURSE_ID + " = \'" + courseId + "\'";

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst())
        {
            do {
                int id = cursor.getInt(0);
                int day = cursor.getInt(1);
                int hour = cursor.getInt(2);
                int minute = cursor.getInt(3);
                int duration = cursor.getInt(4);
                String note = cursor.getString(5);
                boolean hasTest = cursor.getInt(6) == 0 ? false : true;

                StudyTime time = new StudyTime(id, day, hour, minute, duration, note, hasTest, courseId);
                list.add(time);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return list;
    }

    // clear data from all tables
    public void clearData()
    {
        SQLiteDatabase db = this.getWritableDatabase();

        // clear course table
        String query = "DELETE FROM " + COURSE_TABLE;
        db.execSQL(query);

        // clear item table
        String query1 = "DELETE FROM " + ITEM_TABLE;
        db.execSQL(query1);

        db.close();
    }

}
