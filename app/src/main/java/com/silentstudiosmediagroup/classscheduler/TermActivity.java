package com.silentstudiosmediagroup.classscheduler;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class TermActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener{
EditText name, start, end;
DatabaseHelper databaseHelper;
SQLiteDatabase database;
ListView termList;
SimpleCursorAdapter adapter;
String termName, termStart, termEnd;
int matchTermIdInt;
Cursor courseCursor;
List<String> matchingCourses;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_term);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        init();
        updateList();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void init() {
        name = findViewById(R.id.nameEditText);
        start = findViewById(R.id.startEditBox);
        end = findViewById(R.id.endEditBox);
        termList = findViewById(R.id.termList);
        databaseHelper = new DatabaseHelper(this);
        database = databaseHelper.getWritableDatabase();
//        matchingCourses = new ArrayList<>();
        matchTermIdInt = 0;
    }

    private void updateList() {
        int [] destination = new int[] {R.id.termTextView, R.id.startTextView, R.id.endTextView};
        String [] dataFrom = new String[] {"term", "start", "end"};
        Cursor cursor = database.rawQuery("SELECT * FROM terms ", null);
        cursor.moveToFirst();
        adapter = new SimpleCursorAdapter(this, R.layout.layout_term_view, cursor, dataFrom, destination, 0);
        termList = findViewById(R.id.termList);
        termList.setOnItemClickListener(this);
        termList.setAdapter(adapter);
        matchingCourses = new ArrayList<>();
        matchingCourses.clear();
    }

    @Override
    protected void onPause() {
        super.onPause();
        database.close();
        databaseHelper.close();
    }

    @Override
    public void onClick(View view) {
        name = findViewById(R.id.nameEditText);
        start = findViewById(R.id.startEditBox);
        end = findViewById(R.id.endEditBox);
        termName = name.getText().toString();
        termStart = start.getText().toString();
        termEnd = end.getText().toString();
        ContentValues values = new ContentValues();
        values.put("term", termName);
        values.put("start", termStart);
        values.put("end", termEnd);
        deleteDecisionCursorLoad();

        switch (view.getId()){

            case R.id.addTermBtn:
                if(termName.trim().isEmpty() || termStart.trim().isEmpty() || termEnd.trim().isEmpty()){
                    Toast.makeText(this,"Please fill in all fields", Toast.LENGTH_LONG).show();
                }else {
                    database.insert("terms",null, values);
                    updateList();
                }

                name.setText("");
                start.setText("");
                end.setText("");
                break;

            case R.id.deleteTermBtn:
                if(termName.trim().isEmpty()){
                    Toast.makeText(this,"Please enter the name of the term to delete", Toast.LENGTH_LONG).show();
                    break;
                }
                if(!matchingCourses.isEmpty()){
                Toast.makeText(this,"Term contains courses or was not saved in term detail screen the term cannot be deleted", Toast.LENGTH_LONG).show();
                    break;
            }
                database.delete("terms", "term = '" + termName + "';", null);
                matchingCourses.clear();
                updateList();


                name.setText("");
                start.setText("");
                end.setText("");
                break;
            default:
                break;
        }
    }

    private void deleteDecisionCursorLoad() {
        Cursor currentTerm = database.rawQuery("SELECT * FROM terms ", null);//database.query("terms", null, "term = ", selectedTerm, null, null, null);
        courseCursor = database.rawQuery("SELECT * FROM courses ", null);
        courseCursor.moveToFirst();
        currentTerm.moveToFirst();
if(currentTerm.getCount() == 0 || currentTerm ==null){
    return;
}
        do{
            if(currentTerm.getString(currentTerm.getColumnIndex("term")).equals(termName)){
                matchTermIdInt =  currentTerm.getInt(currentTerm.getColumnIndex("_id"));
                break;
            }

        }while (currentTerm.moveToNext());
        matchingCourses.clear();
        do{
            if(courseCursor.getInt(courseCursor.getColumnIndex("termId")) == matchTermIdInt){
                String courseName = courseCursor.getString(courseCursor.getColumnIndex("name"));
                matchingCourses.add(courseName);
            }
        }while (courseCursor.moveToNext());

    }

    @Override
    protected void onResume() {
        super.onResume();
        databaseHelper = new DatabaseHelper(this);
        database = databaseHelper.getWritableDatabase();
        updateList();

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        Cursor itemCursor = (Cursor)termList.getItemAtPosition(position);
        int itemIdInt = itemCursor.getInt(itemCursor.getColumnIndex("_id"));
        String itemName = itemCursor.getString(itemCursor.getColumnIndex("term"));
        String itemStart = itemCursor.getString(itemCursor.getColumnIndex("start"));
        String itemEnd = itemCursor.getString(itemCursor.getColumnIndex("end"));
        Intent termDetailsIntent = new Intent(this, TermDetailActivity.class);
        termDetailsIntent.putExtra("TERM_ID", itemIdInt);
        termDetailsIntent.putExtra("TERM_NAME", itemName);
        termDetailsIntent.putExtra("TERM_START", itemStart);
        termDetailsIntent.putExtra("TERM_END", itemEnd);
        startActivity(termDetailsIntent);

    }
}
