package com.silentstudiosmediagroup.classscheduler;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;

import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class CourseDetailsActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{
    DatabaseHelper databaseHelper;
    SQLiteDatabase database;
    TextView termHeader;
    String courseName, termName, addOrEdit;
    EditText startDate, endDate, courseHeader;
    CheckBox obj, assess, notification;
    int term, courseID;
    List<String> mentorArrayList;
    List<String> checkedItems;
    ListView notesList, mentorList;
    SimpleCursorAdapter noteSimpleCursor;
    Cursor noteCursor;
    SharedPreferences mentorSharedPrefs;
    Button addNoteBtn;
    String start, end;

    Set<String> mentorCheckedSet;
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
        loadData();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        init();
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabSave);
        FloatingActionButton deleteFab = (FloatingActionButton) findViewById(R.id.deleteFab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveData();
            }
        });
        deleteFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteCourse();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void deleteCourse() {

        ContentValues updateValues = new ContentValues();
        updateValues.put("start","");
        updateValues.put("end", "");
        updateValues.put("objective", 0);
        updateValues.put("assessment", 0);
        updateValues.put("notification", 0);
        updateValues.put("termId",1000); //SET THE TERM ID 1000 BECAUSE THERE IS A TERM 0 IF ONE TERM EXISTS 0 ID WOULD ASSIGN THIS CLASS TO TERM ONE EVERYTIME
        database.update("courses", updateValues,"name = '" + courseName +"';",null);
        database.close();
        databaseHelper.close();
        this.finish();
    }

    private void saveData() {
        String start = startDate.getText().toString();
        String end = endDate.getText().toString();
        String newCourseName = courseHeader.getText().toString();

        int objValue = 0;
        int assessValue = 0;
        int notifyfyValue = 0;
        if(obj.isChecked()){
            objValue = 1;
        }else if(!obj.isChecked()){
            objValue = 0;
        }
        if(assess.isChecked()){
            assessValue = 1;
        }else if(!assess.isChecked()){
            assessValue = 0;
        }
        if(notification.isChecked()){
            notifyfyValue = 1;
        }else if(!notification.isChecked()){
            notifyfyValue = 0;
        }

        ContentValues updateValues = new ContentValues();
        if (newCourseName.trim().isEmpty()){
            Toast.makeText(this, "Please enter a course name", Toast.LENGTH_LONG).show();

            return;
        }else{
            updateValues.put("name", newCourseName);
        }

        updateValues.put("start",start);
        updateValues.put("end", end);
        updateValues.put("objective", objValue);
        updateValues.put("assessment", assessValue);
        updateValues.put("notification", notifyfyValue);
        database.update("courses", updateValues,"name = '" + courseName +"';",null);

//GET THE CHECKED MENTORS FROM LIST AND ADD TO SHARED PREFS
        checkedItems = new ArrayList<>();
        for(int i = 0; i<mentorList.getCount(); i++){
            if(mentorList.isItemChecked(i)){
            checkedItems.add(String.valueOf(mentorList.getItemAtPosition(i)));
            }
        }

        if(checkedItems.isEmpty()){
            Toast.makeText(this, "A mininum of one mentor is required to save class information." +
                    "\n" +
                    "Please select a mentor for "+ courseName, Toast.LENGTH_LONG).show();
        }else {
            mentorCheckedSet = new HashSet<>();
            mentorCheckedSet.addAll(checkedItems);
            SharedPreferences.Editor editor = mentorSharedPrefs.edit();
            editor.clear();
            editor.putStringSet(courseName, mentorCheckedSet);
            editor.commit();
if(!start.trim().isEmpty() && !end.trim().isEmpty()) {
    if(notification.isChecked()) {
        try {
            notificationCheck();
        } catch (ParseException e) {
            Toast.makeText(this, "Please enter the dates in the format of DD/MM/YYYY", Toast.LENGTH_LONG).show();
        }
    }
    database.close();
    databaseHelper.close();
    this.finish();
}else{
    Toast.makeText(this, "A start and end date must be entered to save dates need to be in the format DD/MM/YYYY", Toast.LENGTH_LONG).show();
}

        }
    }

    private void init() {
        Bundle bundle = getIntent().getExtras();
        courseName = bundle.getString("NAME");
        term = bundle.getInt("TERM");
        termName = bundle.getString("TERM_NAME");
        databaseHelper = new DatabaseHelper(this);
        database = databaseHelper.getWritableDatabase();
        courseHeader = findViewById(R.id.courseNameText);
        termHeader = findViewById(R.id.termText);
        courseHeader.setText(courseName);
        termHeader.setText(termName);
        addNoteBtn = findViewById(R.id.addNotesBtn);
        notesList = findViewById(R.id.noteList);
        mentorList = findViewById(R.id.mentorList);

        loadData();
    }

    private void loadData() {
        startDate = findViewById(R.id.startEdit);
        endDate = findViewById(R.id.endEdit);
        assess = findViewById(R.id.assessmentCheckBox);
        obj = findViewById(R.id.objCheckBox);
        notification = findViewById(R.id.notifyCheckBox);


        String[] whereArgs = new String[]{courseName};
        String[] columns = new String[]{"_id","name", "start", "end", "objective", "assessment", "termId", "notification"};
        Cursor loadCursor = database.query("courses", null, "name = ?", whereArgs, null, null, null);
        loadCursor.moveToFirst();
        start = loadCursor.getString(loadCursor.getColumnIndex("start"));
        end = loadCursor.getString(loadCursor.getColumnIndex("end"));
        courseID = loadCursor.getInt(loadCursor.getColumnIndex("_id"));
        startDate.setText(start);
        endDate.setText(end);

        int objCheckValue = loadCursor.getInt(loadCursor.getColumnIndex("objective"));
        int assessCheckValue = loadCursor.getInt(loadCursor.getColumnIndex("assessment"));
        int notifyCheckValue = loadCursor.getInt(loadCursor.getColumnIndex("notification"));
        if (objCheckValue == 0) {
            obj.setChecked(false);
        } else if (objCheckValue == 1) {
            obj.setChecked(true);
        } else {
            obj.setChecked(false);
        }
        if (assessCheckValue == 0) {
            assess.setChecked(false);
        } else if (assessCheckValue == 1) {
            assess.setChecked(true);
        } else {
            assess.setChecked(false);
        }
        if (notifyCheckValue == 0) {
            notification.setChecked(false);
        } else if (notifyCheckValue == 1) {
            notification.setChecked(true);
        } else {
            notification.setChecked(false);
        }

        //GET ALL NOTES FOR THE CLASS
        String[] col = new String[]{"title"};
        String[] whereNote = new String[]{courseName};
        noteCursor = database.query("notes", null, "name = ?", whereNote, null, null, null);
        noteCursor.moveToFirst();
        String[] fromNotes = new String[]{"title"};
        int[] toNotes = new int[]{R.id.classTextView};
        noteSimpleCursor = new SimpleCursorAdapter(this, R.layout.note_layout, noteCursor, fromNotes, toNotes, 0);
        notesList.setOnItemClickListener(this);
        notesList.setAdapter(noteSimpleCursor);

//GET ALL MENTORS FOR CLASS
        Cursor mentorCursor = database.rawQuery("SELECT * FROM mentors", null);
        mentorCursor.moveToFirst();

        mentorArrayList = new ArrayList<>();
        for (int i = 0; i < mentorCursor.getCount(); i++) {
            mentorArrayList.add(mentorCursor.getString(mentorCursor.getColumnIndex("name")));
            mentorCursor.moveToNext();
        }


        mentorList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        mentorList.setItemChecked(2, true);
        mentorList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, mentorArrayList));

        mentorSharedPrefs = getSharedPreferences(courseName, Context.MODE_PRIVATE);
        Set<String> emptySet = new HashSet<>();
        emptySet.add("empty");
        if(mentorSharedPrefs.getAll().isEmpty()){

            SharedPreferences.Editor editor = mentorSharedPrefs.edit();
            editor.putStringSet(courseName, emptySet);
            editor.commit();
        }

        Set<String> set = mentorSharedPrefs.getStringSet(courseName, null);
        List<String> checkedList = new ArrayList<>(set);
        int checkCount = 0;
    for (int i = 0; i < mentorArrayList.size(); i++) {
                if (mentorList.getItemAtPosition(i).toString().equals(checkedList.get(checkCount))) {
                    checkCount++;
                    mentorList.setItemChecked(i, true);
                }
            }
        }

    public void onButtonsClick(View view){//CALLED WHEN A NEW NOTE IS CREATED
        Intent noteIntent = new Intent(this, NoteActivity.class);
        noteIntent.putExtra("COURSE", courseName);
        addOrEdit = "add";
        noteIntent.putExtra("ADD_EDIT", addOrEdit);
        startActivity(noteIntent);
}

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) { //CALLED WHEN AN EXISTING NOTE IS CLICKED
            Cursor noteListCursorClick = (Cursor) notesList.getItemAtPosition(position);
            Intent noteIntent = new Intent(this, NoteActivity.class);
            noteIntent.putExtra("COURSE", courseName);
            addOrEdit = "edit";
            noteIntent.putExtra("ADD_EDIT", addOrEdit);
            String noteTitleFromList = noteListCursorClick.getString(noteListCursorClick.getColumnIndex("title"));
            String noteId = (noteListCursorClick.getString(noteListCursorClick.getColumnIndex("_id")));
            noteIntent.putExtra("ID", noteId);
            noteIntent.putExtra("TITLE", noteTitleFromList);
            startActivity(noteIntent);

    }
    private void notificationCheck() throws ParseException {

        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH);
        Date courseStart  = dateFormat.parse(start);


            //CREATE NOTIFICATION
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);
            NotificationManager manager;
            Intent clickIntent = new Intent(this, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this,0,clickIntent,0);
            //ASSIGN THE NOTIFICATION INFORMATION
            notificationBuilder.setContentIntent(pendingIntent);
            notificationBuilder.setAutoCancel(true);
            notificationBuilder.setContentTitle("Course Starting Date");
            notificationBuilder.setContentText(courseName + " is starting on " + courseStart);
            notificationBuilder.setTicker("Class information");
            notificationBuilder.setSmallIcon(R.mipmap.ic_launcher);

            manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            manager.notify(courseID,notificationBuilder.build());

    }


}
