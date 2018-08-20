package com.silentstudiosmediagroup.classscheduler;

import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class NoteActivity extends AppCompatActivity {
    DatabaseHelper databaseHelper;
    SQLiteDatabase database;
    String courseName;
    String addOrModify, finalNote, finalNoteTitle, bundleTitleOfNote, bundleId;
    EditText noteTitle, noteField;
    Bundle bundle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        bundle = getIntent().getExtras();
        courseName = bundle.getString("COURSE");
        addOrModify = bundle.getString("ADD_EDIT");

        databaseHelper = new DatabaseHelper(this);
        database = databaseHelper.getWritableDatabase();
        noteTitle = findViewById(R.id.titleEditText);
        noteField = findViewById(R.id.noteEdit);
        noteField.setFocusable(true);
        if(addOrModify.equalsIgnoreCase("edit")){
            bundleTitleOfNote = bundle.getString("TITLE");
            noteTitle.setText(bundleTitleOfNote);
            String[] params = new String[]{bundleTitleOfNote};

            Cursor noteQuery = database.query("notes",null,"title = ?",params,null,null,null);
            noteQuery.moveToFirst();
            String fullText = noteQuery.getString(noteQuery.getColumnIndex("note"));
            noteField.setText(fullText);
        }

        FloatingActionButton addNoteFab = (FloatingActionButton) findViewById(R.id.addNoteFab);
        FloatingActionButton deleteNoteFab = (FloatingActionButton) findViewById(R.id.deleteNoteFab);
        FloatingActionButton shareNoteFab = (FloatingActionButton) findViewById(R.id.shareNoteFab);

        addNoteFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    if (noteTitle.getText().toString().trim().isEmpty()) {
                        Toast.makeText(NoteActivity.this, "Please enter a title for your note", Toast.LENGTH_LONG).show();
                    } else {
                        saveData();
                    }
            }
        });
    deleteNoteFab.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {

        if (noteTitle.getText().toString().trim().isEmpty()) {
            Toast.makeText(NoteActivity.this, "Please enter a title the note to delete", Toast.LENGTH_LONG).show();
        } else {
            deleteData();
        }
        }
    });
    shareNoteFab.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            shareNote();
        }
    });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void shareNote() {
        Intent mailIntent = new Intent(Intent.ACTION_SENDTO);
        String mailTo = "mailto:" + "";
        //mailIntent.putExtra(Intent.EXTRA_EMAIL, "");
        mailIntent.putExtra(Intent.EXTRA_SUBJECT, noteTitle.getText().toString());
        mailIntent.putExtra(Intent.EXTRA_TEXT, noteField.getText().toString());

        mailIntent.setData(Uri.parse(mailTo));

        try {
            startActivity(mailIntent);
        }catch (ActivityNotFoundException e){
            Toast.makeText(NoteActivity.this,"Please install an email app to send emails", Toast.LENGTH_LONG).show();
        }
    }


    private void deleteData() {
        noteTitle = findViewById(R.id.titleEditText);
        finalNoteTitle = noteTitle.getText().toString();
        String[] whereDel = new String[]{finalNoteTitle};
        try {
            database.delete("notes", "title = ?",whereDel);
            database.close();
            databaseHelper.close();
            this.finish();
        }catch (SQLException exception){
            Toast.makeText(this,"Note does not exist please re-enter the right title to delete",Toast.LENGTH_LONG).show();
        }


    }

    private void saveData() {
        noteTitle = findViewById(R.id.titleEditText);
        noteField = findViewById(R.id.noteEdit);
        finalNoteTitle = noteTitle.getText().toString();
        finalNote = noteField.getText().toString();
        addOrModify = bundle.getString("ADD_EDIT");


        ContentValues noteValue = new ContentValues();
        noteValue.put("note", finalNote);
        noteValue.put("title", finalNoteTitle);
        noteValue.put("name", courseName);


        if(addOrModify.equalsIgnoreCase("add")){
            database.insert("notes", null, noteValue);
            
        }else if(addOrModify.equalsIgnoreCase("edit")){
            //String[] whereArgs = new String[]{finalNoteTitle};
            bundleId = bundle.getString("ID");
            String[] whereArgs = new String[]{bundleId};
            database.update("notes",noteValue,"_id = ?", whereArgs);
        }

        database.close();
        databaseHelper.close();
        this.finish();
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
    }
