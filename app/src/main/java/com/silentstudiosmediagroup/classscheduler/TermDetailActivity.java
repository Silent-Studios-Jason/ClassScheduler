package com.silentstudiosmediagroup.classscheduler;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import java.sql.ResultSet;
import java.util.List;

public class TermDetailActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
EditText nameText, startText, endText;
String termName, termStart, termEnd;
int termId;
DatabaseHelper databaseHelper;
SQLiteDatabase database;
ListView list;
Cursor courseCursor;
String nameClicked, newOrModifyCourse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_term_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        init();
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.saveFab);
        FloatingActionButton addFab = (FloatingActionButton) findViewById(R.id.addFab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveData();
            }
        });
        addFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Cursor mentor = database.rawQuery("SELECT * FROM mentors", null);
                mentor.moveToFirst();
                if (mentor.getCount() == 0 || mentor == null) {
                    Toast.makeText(TermDetailActivity.this, "At least one mentor has to exist in order to proceed. Please assign one from the mentors tab on the main screen", Toast.LENGTH_LONG).show();
                } else {
                    newOrModifyCourse = "new";
                    Intent addCourseIntent = new Intent(TermDetailActivity.this, AddNewCourseActivity.class);
                    addCourseIntent.putExtra("TERM", termId);
                    addCourseIntent.putExtra("NEW_OR_MODIFY", newOrModifyCourse);
                    startActivity(addCourseIntent);
                }
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        databaseHelper = new DatabaseHelper(this);
        database = databaseHelper.getWritableDatabase();
        listTracker();
    }

    @Override
    protected void onPause() {
        super.onPause();
        database.close();
        databaseHelper.close();
    }

    private void saveData() {

        //UPDATE THE TERM TABLE IN CASE THE INFORMATION WAS CHANGED
        ContentValues values = new ContentValues();
        values.put("term", nameText.getText().toString());
        values.put("start", startText.getText().toString());
        values.put("end", endText.getText().toString());
        database.update("terms", values, "_id = '" + termId +"';", null);


        database.close();
        databaseHelper.close();
        this.finish();
    }

    private void init() {
        databaseHelper = new DatabaseHelper(this);
        database = databaseHelper.getWritableDatabase();
        Bundle bundle = getIntent().getExtras();
        termId = bundle.getInt("TERM_ID");
        termName = bundle.getString("TERM_NAME");
        termStart = bundle.getString("TERM_START");
        termEnd = bundle.getString("TERM_END");
        nameText = findViewById(R.id.termNameEditText);
        startText = findViewById(R.id.startTermEditText);
        endText = findViewById(R.id.endTermEditText);

        nameText.setText(termName);
        startText.setText(termStart);
        endText.setText(termEnd);

        listTracker();


    }

    private void listTracker() {
        int [] destination  = new int[] {R.id.courseTextView};
        String [] from = new String[] {"name"};
        list = findViewById(R.id.addCourseList);
        list.removeAllViewsInLayout();
        list.setOnItemClickListener(this);

        //GET ALL THE COURSES FROM THE DATABASE
        String[] params = new String[]{String.valueOf(termId)};
        courseCursor = database.query("courses", null, "termId = ?" , params, null,null,null);
        courseCursor.moveToFirst();
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.layout_course_list, courseCursor, from, destination, 0);
        list.setAdapter(adapter);

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

        newOrModifyCourse = "modify";

        Cursor itemCursor = (Cursor) list.getItemAtPosition(position);


        nameClicked = itemCursor.getString(itemCursor.getColumnIndex("name"));

        Intent seeCourseIntent = new Intent(this, AddNewCourseActivity.class);// CourseDetailsActivity.class);
        seeCourseIntent.putExtra("TERM", termId);
        seeCourseIntent.putExtra("TERM_NAME", termName);
        seeCourseIntent.putExtra("NAME", nameClicked);

        seeCourseIntent.putExtra("NEW_OR_MODIFY", newOrModifyCourse);
        startActivity(seeCourseIntent);
    }

}
