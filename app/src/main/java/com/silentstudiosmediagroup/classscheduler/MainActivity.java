package com.silentstudiosmediagroup.classscheduler;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.icu.util.GregorianCalendar;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {
    DatabaseHelper databaseHelper;
    SQLiteDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        databaseHelper = new DatabaseHelper(this);
        database = databaseHelper.getWritableDatabase();
        //setupCourses();
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        try {
            checkNotifications();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void checkNotifications() throws ParseException {
        Cursor notifyCursor = database.rawQuery("SELECT * FROM notifications", null);
        Cursor courseWithNotification = database.rawQuery("SELECT * FROM courses", null);
        notifyCursor.moveToFirst();
        courseWithNotification.moveToFirst();
        List<String> courses = new ArrayList<>();
        List<String> startTimes = new ArrayList<>();
        List<String> endTimes = new ArrayList<>();
        List<String> performanceTimes = new ArrayList<>();
        List<String> assessementTimes = new ArrayList<>();
        courses.clear();
        startTimes.clear();
        endTimes.clear();
        performanceTimes.clear();
        assessementTimes.clear();


        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateformat = new SimpleDateFormat("MM/dd/yyyy");
        String date = dateformat.format(calendar.getTime());
        if (notifyCursor.getCount() == 0 || notifyCursor == null) {
            return;
        }
        String notifyText;
        int random;
        do {
            courses.add(notifyCursor.getString(notifyCursor.getColumnIndex("name")));
            startTimes.add(notifyCursor.getString(notifyCursor.getColumnIndex("startNotify")));
            endTimes.add(notifyCursor.getString(notifyCursor.getColumnIndex("endNotify")));
            performanceTimes.add(notifyCursor.getString(notifyCursor.getColumnIndex("performNotify")));
            assessementTimes.add(notifyCursor.getString(notifyCursor.getColumnIndex("assessNotify")));

            random = (int) (Math.random() * 100);

            if(!notifyCursor.getString(notifyCursor.getColumnIndex("startNotify")).equals("0")) {
                notifyText = " is starting on ";
                showNotify(notifyCursor.getString(notifyCursor.getColumnIndex("startNotify")), notifyCursor.getString(notifyCursor.getColumnIndex("name")), notifyText, random + 3);
            }
            if(!notifyCursor.getString(notifyCursor.getColumnIndex("endNotify")).equals("0")) {
                notifyText = " is ending on ";
                showNotify(notifyCursor.getString(notifyCursor.getColumnIndex("endNotify")), notifyCursor.getString(notifyCursor.getColumnIndex("name")), notifyText, random + 2);
            }
            if(!notifyCursor.getString(notifyCursor.getColumnIndex("performNotify")).equals("0")) {
                notifyText = " has an objective exam on ";
                showNotify(notifyCursor.getString(notifyCursor.getColumnIndex("performNotify")), notifyCursor.getString(notifyCursor.getColumnIndex("name")), notifyText, random + 4);
            }
            if(!notifyCursor.getString(notifyCursor.getColumnIndex("assessNotify")).equals("0")) {
                notifyText = " has a performance test on ";

                showNotify(notifyCursor.getString(notifyCursor.getColumnIndex("assessNotify")), notifyCursor.getString(notifyCursor.getColumnIndex("name")), notifyText, random + 1);
            }
        } while (notifyCursor.moveToNext());

    }

//    private void checkStart(List<String> startTimes, List<String> courses, int random, int i, SimpleDateFormat dateformat, Calendar calendar) throws ParseException {
//        String notifyText;
//        Date start;
//        if (startTimes.get(i) != null) {
//            start = dateformat.parse(startTimes.get(i));
//
//            if (start.compareTo(calendar.getTime()) > 0) { //THERE IS A CLASS STARTING SOON
//
//                notifyText = " is starting on ";
//                showNotify(startTimes.get(i), courses.get(i), notifyText, random + 1);
//            }
//        }
//
//
//    }


    private void showNotify(String date, String course, String notifyText, int notifyID) {

        //System.out.println(course + notifyText + date);

        //CREATE NOTIFICATION
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);
        NotificationManager manager;
        Intent clickIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, clickIntent, 0);
        //ASSIGN THE NOTIFICATION INFORMATION
        notificationBuilder.setContentIntent(pendingIntent);
        notificationBuilder.setAutoCancel(true);
        notificationBuilder.setContentTitle("Course Alert");
        notificationBuilder.setContentText(course + notifyText + date);
        notificationBuilder.setTicker("Class information");
        notificationBuilder.setSmallIcon(R.mipmap.ic_launcher);

        manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        manager.notify(notifyID, notificationBuilder.build());

    }


    @Override
    protected void onPause() {
        super.onPause();
        database.close();
        databaseHelper.close();
    }

    @Override
    protected void onResume() {
        super.onResume();
        databaseHelper = new DatabaseHelper(this);
        database = databaseHelper.getWritableDatabase();


    }

    private void setupCourses() {
        Cursor isDataCursor = database.rawQuery("SELECT * FROM courses", null);
        isDataCursor.moveToFirst();
        if (isDataCursor.getCount() <= 1) {
            String[] courseArray = getResources().getStringArray(R.array.courses);
            ContentValues values = new ContentValues();
            for (int i = 0; i < courseArray.length; i++) {
                values.put("name", courseArray[i]);
                database.insert("courses", null, values);
            }
        }

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

//        if (id == R.id.nav_courses) {
//            //GO TO THE COURSE ACTIVITY
//            Intent intent = new Intent(this, CoursesActivity.class);
//            startActivity(intent);
//        } else
        if (id == R.id.nav_terms) {
            Intent intent = new Intent(this, TermActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_mentors) {
            Intent intent = new Intent(this, MentorActivity.class);
            startActivity(intent);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //THIS IS CALLED FROM BUTTONS XML ONCLICK
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.termsBtn:
                Intent termIntent = new Intent(this, TermActivity.class);
                startActivity(termIntent);
                break;
            case R.id.myCoursesBtn:
                Intent courseIntent = new Intent(this, MyCoursesActivity.class);
                startActivity(courseIntent);
                break;
            case R.id.mentorBtn:
                Intent mentorIntent = new Intent(this, MentorActivity.class);
                startActivity(mentorIntent);
                break;
            default:
                break;
        }

    }
}
