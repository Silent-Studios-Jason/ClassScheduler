package com.silentstudiosmediagroup.classscheduler;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class AddNewCourseActivity extends AppCompatActivity implements AdapterView.OnClickListener, AdapterView.OnItemClickListener {
    DatabaseHelper databaseHelper;
    SQLiteDatabase database;
    EditText courseText, startEdt, endEdt, objectiveEditText, performanceDateEditText, courseDescEdtTxt;
    TextView termTxt;
    CheckBox startDateAlertCheckBox, endDateAlertCheckBox, objChckBox, objGoalCheckBox, assessmentChckBox, performancGoalCheckBox;
    Button addNotesBtn;
    ListView noteLst, mentorLst;
    String addOrEdit, termNmeFromCursor, startStr, endStr, objDateStr, performDateStr, currentCourse, courseStr, courseDescStr, newOrModify = null;
    int termId;
    List<String> mentorArrayList;
    List<String> checkedItems, checkedList;
    SharedPreferences mentorSharedPrefs;
    Set<String> mentorCheckedSet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_course);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton delFab = (FloatingActionButton) findViewById(R.id.deleteCourseFab);
        delFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteData();
            }
        });
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.addNewCourseFab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveData();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        init();
    }

    private void deleteData() {
        String[] delParams = new String[]{currentCourse};
        database.delete("courses", " name = ? ", delParams);
        database.delete("notifications", " name = ? ", delParams);
        database.close();
        databaseHelper.close();
        this.finish();

    }

    private void init() {
        databaseHelper = new DatabaseHelper(this);
        database = databaseHelper.getWritableDatabase();
        courseText = findViewById(R.id.courseText);
        startEdt = findViewById(R.id.startEdt);
        endEdt = findViewById(R.id.endEdt);
        objectiveEditText = findViewById(R.id.objectiveEditText);
        performanceDateEditText = findViewById(R.id.performanceDateEditText);
        courseDescEdtTxt = findViewById(R.id.courseDescEdtTxt);
        termTxt = findViewById(R.id.termTxt);
        startDateAlertCheckBox = findViewById(R.id.startDateAlertCheckBox);
        endDateAlertCheckBox = findViewById(R.id.endDateAlertCheckBox);
        objChckBox = findViewById(R.id.objChckBox);
        objGoalCheckBox = findViewById(R.id.objGoalCheckBox);
        assessmentChckBox = findViewById(R.id.assessmentChckBox);
        performancGoalCheckBox = findViewById(R.id.performancGoalCheckBox);
        addNotesBtn = findViewById(R.id.addNotesBtn);
        noteLst = findViewById(R.id.noteLst);
        mentorLst = findViewById(R.id.mentorLst);
        Bundle bundle = getIntent().getExtras();
        termId = bundle.getInt("TERM");
        newOrModify = bundle.getString("NEW_OR_MODIFY");
        currentCourse = bundle.getString("NAME");
        String[] whereArgs = new String[]{String.valueOf(termId)};
        Cursor termCursor = database.query("terms", null, "_id = ?", whereArgs, null, null, null);
        termCursor.moveToFirst();
        termNmeFromCursor = termCursor.getString(termCursor.getColumnIndex("term"));
        termTxt.setText(termNmeFromCursor);


        //GET ALL MENTORS FOR CLASS
        Cursor mentorCursor = database.rawQuery("SELECT name FROM mentors", null);
        mentorCursor.moveToFirst();

        mentorArrayList = new ArrayList<>();

        //ASSIGN ALL NAMES IN CURSOR TO AN ARRAY LIST
        if(!mentorCursor.equals(null) && mentorCursor.getCount() !=0) {
            do {
                mentorArrayList.add(mentorCursor.getString(mentorCursor.getColumnIndex("name")));
            } while (mentorCursor.moveToNext());
        }

        mentorLst.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        mentorLst.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, mentorArrayList));

        if (newOrModify.equalsIgnoreCase("modify")) {
            loadSavedData();
        }

    }

    private void loadSavedData() {
        String[] params = new String[]{currentCourse};
        Cursor existingCourseCursor = database.query("courses", null, " name = ? ", params, null, null, null);
        existingCourseCursor.moveToFirst();
        courseText.setText(existingCourseCursor.getString(existingCourseCursor.getColumnIndex("name")));
        startEdt.setText(existingCourseCursor.getString(existingCourseCursor.getColumnIndex("start")));
        endEdt.setText(existingCourseCursor.getString(existingCourseCursor.getColumnIndex("end")));
        objectiveEditText.setText(existingCourseCursor.getString(existingCourseCursor.getColumnIndex("objDate")));
        performanceDateEditText.setText(existingCourseCursor.getString(existingCourseCursor.getColumnIndex("performDate")));
        courseDescEdtTxt.setText(existingCourseCursor.getString(existingCourseCursor.getColumnIndex("description")));
        checkBoxes(existingCourseCursor);
        loadMentors();

    }

    private void checkBoxes(Cursor existingCourseCursor) {
        int startChk = existingCourseCursor.getInt(existingCourseCursor.getColumnIndex("startNotify"));
        int endChk = existingCourseCursor.getInt(existingCourseCursor.getColumnIndex("endNotify"));
        int objChk = existingCourseCursor.getInt(existingCourseCursor.getColumnIndex("objective"));
        int objGoalChk = existingCourseCursor.getInt(existingCourseCursor.getColumnIndex("objGoalNotify"));
        int performanceChk = existingCourseCursor.getInt(existingCourseCursor.getColumnIndex("assessment"));
        int performanceGoalChk = existingCourseCursor.getInt(existingCourseCursor.getColumnIndex("performGoalNotify"));

        if(startChk == 1) {
            startDateAlertCheckBox.setChecked(true);
        }else if(startChk == 0) {
            startDateAlertCheckBox.setChecked(false);
        }else{
            startDateAlertCheckBox.setChecked(false);
        }

        if(endChk == 1) {
            endDateAlertCheckBox.setChecked(true);
        }else if(endChk == 0) {
            endDateAlertCheckBox.setChecked(false);
        }else{
            endDateAlertCheckBox.setChecked(false);
        }

        if(objChk == 1) {
            objChckBox.setChecked(true);
        }else if(objChk == 0) {
            objChckBox.setChecked(false);
        }else{
            objChckBox.setChecked(false);
        }

        if(objGoalChk == 1) {
            objGoalCheckBox.setChecked(true);
        }else if(objGoalChk == 0) {
            objGoalCheckBox.setChecked(false);
        }else{
            objGoalCheckBox.setChecked(false);
        }

        if(performanceChk == 1) {
            assessmentChckBox.setChecked(true);
        }else if(performanceChk == 0) {
            assessmentChckBox.setChecked(false);
        }else{
            assessmentChckBox.setChecked(false);
        }

        if(performanceGoalChk == 1) {
            performancGoalCheckBox.setChecked(true);
        }else if(performanceGoalChk == 0) {
            performancGoalCheckBox.setChecked(false);
        }else{
            performancGoalCheckBox.setChecked(false);
        }

    }

    private void saveData() {
        courseStr = courseText.getText().toString();
        startStr = startEdt.getText().toString();
        endStr = endEdt.getText().toString();
        objDateStr = objectiveEditText.getText().toString();
        performDateStr = performanceDateEditText.getText().toString();
        courseDescStr = courseDescEdtTxt.getText().toString();

        if (courseStr.trim().isEmpty()) {
            Toast.makeText(this, "Please enter a course name", Toast.LENGTH_LONG).show();


        } else {
            saveToCourses();

            //GET THE CHECKED MENTORS FROM LIST AND ADD TO SHARED PREFS
            checkedItems = new ArrayList<>();
            for(int i = 0; i< mentorArrayList.size(); i++){
                if(mentorLst.isItemChecked(i)){
                    checkedItems.add((String) mentorLst.getItemAtPosition(i));
                }
            }

            if(checkedItems.isEmpty()){
                Toast.makeText(this, "A mininum of one mentor is required to save class information." +
                        "\n" +
                        "Please select a mentor for "+ courseStr, Toast.LENGTH_LONG).show();
            }else {
                mentorCheckedSet = new HashSet<>();
                mentorCheckedSet.addAll(checkedItems);
                mentorSharedPrefs = this.getSharedPreferences(courseStr, Context.MODE_PRIVATE);

                SharedPreferences.Editor editor = mentorSharedPrefs.edit();
                editor.clear();
                editor.putStringSet(courseStr, mentorCheckedSet);

                editor.commit();

                database.close();
                databaseHelper.close();
                this.finish();
            }

        }
    }

    private void saveToCourses() {
        databaseHelper = new DatabaseHelper(this);
        database = databaseHelper.getWritableDatabase();
        startStr = startEdt.getText().toString();
        endStr = endEdt.getText().toString();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        ContentValues courseValues = new ContentValues();
        ContentValues notificationValues = new ContentValues();
        Date courseStart = null;
        Date courseEnd = null;
        Date objDate = null;
        Date assessDate = null;
        if (startStr.trim().isEmpty() || endStr.trim().isEmpty()) {
            Toast.makeText(this, "Please fill in a start and end date for " + courseStr + " in the format of mm/dd/yyyy", Toast.LENGTH_LONG).show();
        } else {
            try {
                courseStart = dateFormat.parse(startStr);
                courseEnd = dateFormat.parse(endStr);
                courseValues.put("start", startStr);
                courseValues.put("end", endStr);
            } catch (ParseException e) {
                Toast.makeText(this, "Please fill in a start and end date for " + courseStr + " in the format of mm/dd/yyyy", Toast.LENGTH_LONG).show();
            }
        }

        if (startDateAlertCheckBox.isChecked()) {
            courseValues.put("startNotify", 1);
            notificationValues.put("startNotify", startStr);
        } else {
            courseValues.put("startNotify", 0);
            notificationValues.put("startNotify", "0");
        }
        if (endDateAlertCheckBox.isChecked()) {
            courseValues.put("endNotify", 1);
            notificationValues.put("endNotify", endStr);
        } else {
            courseValues.put("endNotify", 0);
            notificationValues.put("endNotify", "0");
        }

        if (objChckBox.isChecked()) {
            courseValues.put("objective", 1);
            if (objDateStr.trim().isEmpty()) {
                Toast.makeText(this, "Please fill in the objective date for " + courseStr + " in the format of mm/dd/yyyy", Toast.LENGTH_LONG).show();
            } else {
                try {
                    objDate = dateFormat.parse(objDateStr);
                    courseValues.put("objDate", objDateStr);
                    if (objGoalCheckBox.isChecked()) {
                        courseValues.put("objGoalNotify", 1);
                        notificationValues.put("performNotify", objDateStr);
                    } else {
                        courseValues.put("objGoalNotify", 0);
                        notificationValues.put("performNotify", "0");
                    }
                } catch (ParseException e) {
                    Toast.makeText(this, "Please fill in the objective date for " + courseStr + " in the format of mm/dd/yyyy", Toast.LENGTH_LONG).show();
                }

            }

        } else {
            courseValues.put("objGoalNotify", 0);
            courseValues.put("objective", 0);
            notificationValues.put("performNotify", "0");
        }
        //PERFORMACE ASSESSMENT
        if (assessmentChckBox.isChecked()) {
            courseValues.put("assessment", 1);
            if (performDateStr.trim().isEmpty()) {
                Toast.makeText(this, "Please fill in the assessment date for " + courseStr + " in the format of mm/dd/yyyy", Toast.LENGTH_LONG).show();
            } else {
                try {
                    assessDate = dateFormat.parse(performDateStr);
                    courseValues.put("performDate", performDateStr);
                    if (performancGoalCheckBox.isChecked()) {
                        courseValues.put("performGoalNotify", 1);
                        notificationValues.put("assessNotify", performDateStr);
                    } else {
                        courseValues.put("performGoalNotify", 0);
                        notificationValues.put("assessNotify", "0");
                    }
                } catch (ParseException e) {
                    Toast.makeText(this, "Please fill in the assessment date for " + courseStr + " in the format of mm/dd/yyyy", Toast.LENGTH_LONG).show();
                }

            }

        } else {
            courseValues.put("performGoalNotify", 0);
            courseValues.put("assessment", 0);
            notificationValues.put("assessNotify", "0");
        }

        courseValues.put("description", courseDescStr);
        courseValues.put("name", courseStr);
        courseValues.put("termId", termId);
        notificationValues.put("name", courseStr);
        if (newOrModify.equalsIgnoreCase("modify")) {
            String[] updateParams = new String[]{currentCourse};
            database.update("courses", courseValues, " name = ? ", updateParams);
            database.update("notifications", notificationValues, " name = ? ", updateParams);

        }else {
            database.insert("courses", null, courseValues);
            database.insert("notifications", null, notificationValues);
        }

    }


    @Override
    protected void onResume() {
        super.onResume();
        databaseHelper = new DatabaseHelper(this);
        database = databaseHelper.getWritableDatabase();

        //LOAD DATA IF RETURNING FROM SCREENS SUCH AS NOTES
        loadNotes();
    }


    private void loadMentors() {

        mentorLst.clearChoices();
        SharedPreferences prefs = this.getSharedPreferences(courseText.getText().toString(), Context.MODE_PRIVATE);
//        if(prefs.equals(null) || prefs == null){
//            return;
//        }
        Set<String> prefSet = prefs.getStringSet(courseText.getText().toString(), null);

        checkedList = new ArrayList<>(prefSet);


        int checkCount = 0;
        int test = 0;
        for (int i = 0; i < mentorArrayList.size(); i++) {
//do{
            if (checkCount < checkedList.size()) {

                if (mentorArrayList.get(i).equals(checkedList.get(checkCount))) {
//                    if(i == 0){
//                        test = 0;
//                    }else{
//                        test = i;
//                    }
                    mentorLst.setItemChecked(i, true);
                    checkCount++;
                }
                //test++;

            }
//        }while(test != mentorArrayList.size());
        }
    }




    private void loadNotes() {
        courseStr = courseText.getText().toString();
        if (courseStr != null) {
            String[] whereNote = new String[]{courseStr};
            Cursor noteCursor = database.query("notes", null, "name = ?", whereNote, null, null, null);
            noteCursor.moveToFirst();
            String[] fromNotes = new String[]{"title"};
            int[] toNotes = new int[]{R.id.classTextView};
            SimpleCursorAdapter noteSimpleCursor = new SimpleCursorAdapter(this, R.layout.note_layout, noteCursor, fromNotes, toNotes, 0);
            noteLst.setOnItemClickListener(this);
            noteLst.setAdapter(noteSimpleCursor);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        database.close();
        databaseHelper.close();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.addNotesBtn:
                courseStr = courseText.getText().toString();
                if (courseStr.trim().isEmpty() || courseStr == null) {
                    Toast.makeText(this, "Please enter a course name", Toast.LENGTH_LONG).show();


                } else {
                    courseStr = courseText.getText().toString();
                    Intent noteIntent = new Intent(this, NoteActivity.class);
                    noteIntent.putExtra("COURSE", courseStr);
                    addOrEdit = "add";
                    noteIntent.putExtra("ADD_EDIT", addOrEdit);
                    noteIntent.putExtra("COURSE_NAME", courseStr);
                    startActivity(noteIntent);
                }
        }


    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) { //CALLED WHEN AN EXISTING NOTE IS CLICKED
        courseStr = courseText.getText().toString();
        if (courseStr.trim().isEmpty() || courseStr == null) {
            Toast.makeText(this, "Please enter a course name", Toast.LENGTH_LONG).show();

        } else {
            Cursor noteListCursorClick = (Cursor) noteLst.getItemAtPosition(position);
            Intent noteIntent = new Intent(this, NoteActivity.class);
            noteIntent.putExtra("COURSE", courseStr);
            addOrEdit = "edit";
            noteIntent.putExtra("ADD_EDIT", addOrEdit);
            String noteTitleFromList = noteListCursorClick.getString(noteListCursorClick.getColumnIndex("title"));
            String noteId = (noteListCursorClick.getString(noteListCursorClick.getColumnIndex("_id")));
            noteIntent.putExtra("ID", noteId);
            noteIntent.putExtra("TITLE", noteTitleFromList);
            startActivity(noteIntent);
        }
    }
}
