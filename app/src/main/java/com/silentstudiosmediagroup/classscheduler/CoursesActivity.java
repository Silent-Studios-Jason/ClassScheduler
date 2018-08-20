package com.silentstudiosmediagroup.classscheduler;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import java.util.List;

public class CoursesActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    //THE COURSES NAMES TO A STRING ARRAY
DatabaseHelper databaseHelper;
SQLiteDatabase database;
ListView listView;
int termId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_courses);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        databaseHelper = new DatabaseHelper(this);
        database = databaseHelper.getWritableDatabase();


        listView = (ListView) findViewById(R.id.course_listView);

        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listView.setItemChecked(2, true);

        final String[] ALL_COURSES = getResources().getStringArray(R.array.courses);
        listView.setOnItemClickListener(this);
        listView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, ALL_COURSES));

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.courseSaveFab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveData();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        databaseHelper = new DatabaseHelper(this);
        database = databaseHelper.getWritableDatabase();
    }

    @Override
    protected void onPause() {
        super.onPause();
        database.close();
        databaseHelper.close();
    }

    private void saveData() {
        Bundle bundle = getIntent().getExtras();
        termId = bundle.getInt("TERM");
        int size = listView.getCount();
        ContentValues updateValues = new ContentValues();
        updateValues.put("termId", termId);
        String name;
        for(int i = 0; i < size; i++){
            if(listView.isItemChecked(i)){
                //IF THE ITEM IS CHECKED SAVE THE TERM ID TO COURSES
                name = listView.getItemAtPosition(i).toString();
                database.update("courses", updateValues, "name = '" + name + "';", null);
            }
        }
        database.close();
        databaseHelper.close();
        this.finish();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

    }
}
