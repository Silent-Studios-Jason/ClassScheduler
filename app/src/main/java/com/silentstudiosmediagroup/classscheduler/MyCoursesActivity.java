package com.silentstudiosmediagroup.classscheduler;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MyCoursesActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
ListView classes;
TextView descView;
List <String> classList;

    DatabaseHelper databaseHelper;
    SQLiteDatabase database;
    Cursor courseCursor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_courses);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        classes = findViewById(R.id.classView);
        descView = findViewById(R.id.classDescTextView);
        databaseHelper = new DatabaseHelper(this);
        database = databaseHelper.getWritableDatabase();
        classList = new ArrayList<>();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        courseCursor = database.rawQuery("SELECT * FROM courses", null);
        courseCursor.moveToFirst();

        if(courseCursor.getCount() != 0 && courseCursor != null){
            do{

                classList.add(courseCursor.getString(courseCursor.getColumnIndex("name")));
                classes.setOnItemClickListener(this);
                classes.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_selectable_list_item, classList));

            }while(courseCursor.moveToNext());
        }else{
            descView.setText("There are no classes saved please save a class from the course screen");
        }
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

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        String[] name = {classList.get(position)};
        String[] column = {"description"};
        Cursor descCursor = database.query("courses", column, "name = ?" , name, null,null,null);
        if(descCursor.getCount() != 0 || descCursor != null){
        descCursor.moveToFirst();
        String description = descCursor.getString(descCursor.getColumnIndex("description"));

            descView.setText(description);

        }else{
            descView.setText("There is no description saved. A desription can be saved from the course detail screen");
        }
    }
}
