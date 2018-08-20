package com.silentstudiosmediagroup.classscheduler;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jason on 11/14/2017.
 */

public class DatabaseHelper extends SQLiteOpenHelper {
    //CHANGE VERSION IF DATABASE STRUCTURE CHANGES
    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "scheduler.db";
    //THE FOLLOWING TABLES NEED TO BE CREATED FOR SCHEDULER
    public static final String TABLE_COURSES = "courses";
    public static final String TABLE_MENTORS = "mentors";
    public static final String TABLE_TERMS = "terms";
    public static final String TABLE_NOTES = "notes";
    public static final String TABLE_NOTIFICATIONS = "notifications";
    //COURSES COLUMNS
    public static final String ID_COLUMN = "_id"; //USED IN ALL TABLES
    public static final String COURSE_NAME_COLUMN = "name"; //USED IN COURSES AND NOTES
    public static final String START_COLUMN = "start";
    public static final String END_COLUMN = "end";
    public static final String OBJECTIVE_COLUMN = "objective";
    public static final String ASSESSMENT_COLUMN = "assessment";
    public static final String NOTIFICATION_COLUMN = "notification";
    public static final String START_NOTIFICATION_COLUMN = "startNotify";
    public static final String END_NOTIFICATION_COLUMN = "endNotify";
    public static final String OBJECTIVE_GOAL_NOTIFICATION_COLUMN = "objGoalNotify";
    public static final String PERFORMANCE_GOAL_NOTIFICATION_COLUMN = "performGoalNotify";
    public static final String OBJECTIVE_DATE_NOTIFICATION_COLUMN = "objDate";
    public static final String PERFORMANCE_DATE_NOTIFICATION_COLUMN = "performDate";
    public static final String COURSE_DESCRIPTION_COLUMN = "description";

    public static final String TERM_ID = "termId";

    //MENTORS COLUMNS
    public static final String NAME_COLUMN = "name";
    public static final String PHONE_COLUMN = "phone";
    public static final String EMAIL_COLUMN = "email";
    //TERMS COLUMNS
    public static final String TERM_NAME_COLUMN = "term";
    public static final String TERM_START_COLUMN = "start";
    public static final String TERM_END_COLUMN = "end";
    //NOTE COLUMNS

    public static final String NOTES_COLUMN = "note";
    public static final String NOTE_TITLE_COLUMN = "title";

    //NOTIFICATIONS COLUMNS
    public static final String START_COURSE_COLUMN = "startNotify";
    public static final String END_COURSE_COLUMN = "endNotify";
    public static final String PREFORM_COURSE_COLUMN = "performNotify";
    public static final String ASSESSMENT_COURSE_COLUMN = "assessNotify";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
        }

    @Override
    public void onCreate(SQLiteDatabase database) {
        String courseTableQuery = "CREATE TABLE IF NOT EXISTS " + TABLE_COURSES + "(" +
                ID_COLUMN + " INTEGER primary key autoincrement, " +
                COURSE_NAME_COLUMN + " TEXT, " +
                START_COLUMN + " TEXT, " +
                END_COLUMN + " TEXT, " +
                OBJECTIVE_COLUMN + " INTEGER, " +
                ASSESSMENT_COLUMN + " INTEGER, " +
                TERM_ID + " INTEGER," +
                NOTIFICATION_COLUMN + " INTEGER, " +
                START_NOTIFICATION_COLUMN + " INTEGER, " +
                END_NOTIFICATION_COLUMN + " INTEGER, " +
                OBJECTIVE_GOAL_NOTIFICATION_COLUMN + " INTEGER, " +
                PERFORMANCE_GOAL_NOTIFICATION_COLUMN + " INTEGER, " +
                OBJECTIVE_DATE_NOTIFICATION_COLUMN + " TEXT, " +
                PERFORMANCE_DATE_NOTIFICATION_COLUMN + " TEXT, " +
                COURSE_DESCRIPTION_COLUMN + " TEXT " +
                " );";
        String mentorTableQuery = "CREATE TABLE IF NOT EXISTS " + TABLE_MENTORS + "( " +
                ID_COLUMN + " INTEGER primary key autoincrement, " +
                NAME_COLUMN + " TEXT, " +
                EMAIL_COLUMN + " TEXT, " +
                PHONE_COLUMN + " TEXT " +
                " );";
        String termTableQuery = "CREATE TABLE IF NOT EXISTS " + TABLE_TERMS + "(" +
                ID_COLUMN + " INTEGER primary key autoincrement, " +
                TERM_NAME_COLUMN + " TEXT, " +
                TERM_START_COLUMN + " TEXT, " +
                TERM_END_COLUMN + " TEXT " +
                " );";
        String noteTableQuery = "CREATE TABLE IF NOT EXISTS " + TABLE_NOTES + "(" +
                ID_COLUMN + " INTEGER primary key autoincrement, " +
                COURSE_NAME_COLUMN + " TEXT, " +
                NOTES_COLUMN + " TEXT, " +
                NOTE_TITLE_COLUMN + " TEXT " +
                " );";
        String notifyQuery = "CREATE TABLE IF NOT EXISTS " + TABLE_NOTIFICATIONS + "(" +
                ID_COLUMN + " INTEGER primary key autoincrement, " +
                COURSE_NAME_COLUMN + " TEXT, " +
                START_COURSE_COLUMN + " TEXT, " +
                END_COURSE_COLUMN + " TEXT, " +
                PREFORM_COURSE_COLUMN + " TEXT, " +
                ASSESSMENT_COURSE_COLUMN + " TEXT " +
                " );";
        database.execSQL(courseTableQuery);
        database.execSQL(mentorTableQuery);
        database.execSQL(termTableQuery);
        database.execSQL(noteTableQuery);
        database.execSQL(notifyQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_COURSES);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_MENTORS);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_TERMS);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTES);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTIFICATIONS);
        onCreate(sqLiteDatabase);
    }


}
