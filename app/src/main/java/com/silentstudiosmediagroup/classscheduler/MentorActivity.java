package com.silentstudiosmediagroup.classscheduler;

import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class MentorActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener{
EditText name, email, phone;
DatabaseHelper dataBaseHelper;
SQLiteDatabase database;
String nameStr;
String emailStr;
String phoneStr;
ListView mentorsList;
Boolean isFromList;
SimpleCursorAdapter dataAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        init();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mentor);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        updateList();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email = findViewById(R.id.emailEditText);
                if(!email.getText().toString().trim().isEmpty()){
                    Intent mailIntent = new Intent(Intent.ACTION_SENDTO);
                    String mailTo = "mailto:" + email.getText().toString();

                    mailIntent.setData(Uri.parse(mailTo));
                    try {
                        startActivity(mailIntent);
                    }catch (ActivityNotFoundException e){
                        Toast.makeText(MentorActivity.this,"Please install an email app to send emails", Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(MentorActivity.this,"Please specify an email", Toast.LENGTH_LONG).show();
                }
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void updateList() {
        int [] destination = new int[] {R.id.listTextView};
        String [] dataFrom = new String[] {"name"};
        Cursor cursor = database.rawQuery("SELECT * FROM mentors ", null);
        cursor.moveToFirst();
        dataAdapter = new SimpleCursorAdapter(this, R.layout.layout_text_view, cursor, dataFrom, destination, 0);
        mentorsList = findViewById(R.id.mentorList);
        mentorsList.setOnItemClickListener(this);
        mentorsList.setAdapter(dataAdapter);
    }

    private void init() {
        name = findViewById(R.id.nameEditText);
        email = findViewById(R.id.emailEditText);
        phone = findViewById(R.id.phoneEditText);
        mentorsList = findViewById(R.id.mentorList);
        isFromList = false;
        dataBaseHelper = new DatabaseHelper(this);
        database = dataBaseHelper.getWritableDatabase();

    }

    @Override
    public void onClick(View view) {
        name = findViewById(R.id.nameEditText);
        email = findViewById(R.id.emailEditText);
        phone = findViewById(R.id.phoneEditText);

        nameStr = name.getText().toString();
        emailStr = email.getText().toString();
        phoneStr = phone.getText().toString();

        name.setText("");
        email.setText("");
        phone.setText("");

        ContentValues values = new ContentValues();
        values.put("name", nameStr);
        values.put("email", emailStr);
        values.put("phone", phoneStr);

        switch (view.getId()) {
            case  R.id.saveBtn:
                if(nameStr.trim().isEmpty() || emailStr.trim().isEmpty() || phoneStr.trim().isEmpty()){
                    Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_LONG).show();
                }else {
                    if (!isFromList) {
                        //THIS MEANS TO INSERT NEW MENTOR

                        database.insert("mentors", null, values);
                    } else {
                        //ELSE THE INFORMATION IS FOR AN UPDATE
                        database.update("mentors", values, "name = '" + nameStr + "' ;", null);
                        isFromList = false; //SET BACK TO FALSE SET TO TRUE FROM LIST CLICK LISTENER WHEN ITEM SELECTED
                    }
                    updateList();
                }
            break;

            case R.id.deleteBtn:
                //DELETE THE MENTOR
                if(nameStr.trim().isEmpty() || emailStr.trim().isEmpty() || phoneStr.trim().isEmpty()){
                    Toast.makeText(this, "Some fields are missing please select a mentor from the list below", Toast.LENGTH_LONG).show();
                }else {
                    database.delete("mentors", "name = '" + nameStr + "' ;", null);
                    updateList();
                }
                break;

            default:
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        name = findViewById(R.id.nameEditText);
        email = findViewById(R.id.emailEditText);
        phone = findViewById(R.id.phoneEditText);

        Cursor itemCursor = (Cursor)mentorsList.getItemAtPosition(position);
        String itemStr = itemCursor.getString(itemCursor.getColumnIndex("name"));

        Cursor databaseDataCursor = database.rawQuery("SELECT name, email, phone FROM mentors WHERE name = '"+ itemStr+ "' ;" , null);
        databaseDataCursor.moveToFirst();
        name.setText(databaseDataCursor.getString(databaseDataCursor.getColumnIndex("name")));
        email.setText(databaseDataCursor.getString(databaseDataCursor.getColumnIndex("email")));
        phone.setText(databaseDataCursor.getString(databaseDataCursor.getColumnIndex("phone")));
        isFromList = true;
    }
}
