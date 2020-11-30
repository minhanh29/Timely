package com.example.timely;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;

import java.io.InputStream;
import java.util.Calendar;
import android.text.InputType;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.timely.courses.Course;
import com.example.timely.courses.StudyTime;
import com.example.timely.settings.AlarmService;
import com.example.timely.timetable.TimetableActivity;

import android.widget.AdapterView;



public class ItemDetailsActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    DatePickerDialog pickerDate;
    TimePickerDialog pickerTime;
    EditText editTime, sectionNo, instructorName;
    Switch hasTest;
    Spinner dateSpinner;
    private Course course;
    private StudyTime studyTime;
    private DatabaseHelper db;
    private String courseId;
    private int studyTimeId;
    private int hour, minutes, year, day, month;

    private ImageView imageNote;
    private String selectedImagePath;

    // Views
    TextView noteView, itemHeader;

    private static final int REQUEST_CODE_STORAGE_PERMISSION = 1;
    private static final int REQUEST_CODE_SELECT_IMAGE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_details);
        dateSpinner = findViewById(R.id.dateSpinner);
        editTime = findViewById(R.id.editTime);
        imageNote = findViewById(R.id.imageNote);
        hasTest = findViewById(R.id.hasTest);
        itemHeader = findViewById(R.id.item_header);
        sectionNo = findViewById(R.id.section_No);
        instructorName = findViewById(R.id.instructor_Name);

        selectedImagePath = "";

        initMiscellaneuos();


        // get views
        noteView = findViewById(R.id.note);

        // the id's are sent from TimetableActivity
        Intent intent = getIntent();
        courseId = intent.getStringExtra(TimetableActivity.COURSE_ID);
        studyTimeId = intent.getIntExtra(TimetableActivity.STUDY_TIME_ID, -1);

        // get information from database
        db = new DatabaseHelper(this);

//        Date Picker
        ArrayAdapter<CharSequence> adapterdate = ArrayAdapter.createFromResource(this,R.array.days, android.R.layout.simple_spinner_item);
        adapterdate.setDropDownViewResource(android.R.layout.simple_spinner_item);
        dateSpinner.setAdapter(adapterdate);
        dateSpinner.setOnItemSelectedListener(this);
//        Time Picker
        editTime.setInputType(InputType.TYPE_NULL);
        editTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // time picker dialog
                pickerTime = new TimePickerDialog(ItemDetailsActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker tp, int sHour, int sMinute) {
                        hour = sHour;
                        minutes = sMinute;
                        editTime.setText(String.format("%02d:%02d",sHour,sMinute));
                    }
                }, hour, minutes,true);
                pickerTime.show();
            }
        });

        updateScreen();
    }

//    Add Images

    private void initMiscellaneuos() {
        final LinearLayout layoutMiscellaneuos = findViewById(R.id.layoutMiscellaneous);

        layoutMiscellaneuos.findViewById(R.id.layoutAddImage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(
                        getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(
                            ItemDetailsActivity.this,
                            new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},
                            REQUEST_CODE_STORAGE_PERMISSION
                    );
                }else {
                    selectImage();
                }

            }
        });
    }

    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if (intent.resolveActivity(getPackageManager()) != null){
            startActivityForResult(intent, REQUEST_CODE_SELECT_IMAGE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_STORAGE_PERMISSION && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                selectImage();
            }else {
                Toast.makeText(this, "Permission Denied!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SELECT_IMAGE && resultCode == RESULT_OK){
            if (data != null){
                Uri selectedImageUri = data.getData();
                if (selectedImageUri != null){
                    try {
                        InputStream inputStream = getContentResolver().openInputStream(selectedImageUri);
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        imageNote.setImageBitmap(bitmap);
                        imageNote.setVisibility(View.VISIBLE);

                        selectedImagePath = getPathFromUri(selectedImageUri);

                    }catch (Exception exception ){
                        Toast.makeText(this, exception.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }

    private String getPathFromUri(Uri contentUri) {
        String filePath;
        Cursor cursor = getContentResolver()
                .query(contentUri, null, null,null,null);
        if (cursor == null) {
            filePath = contentUri.getPath();
        }else{
            cursor.moveToFirst();
            int index = cursor.getColumnIndex("_data");
            filePath = cursor.getString(index);
            cursor.close();
        }
        return filePath;
    }

    public void updateScreen()
    {
        // get the course and study time from the database
        course = db.getCourse(courseId);
        studyTime = db.getStudyTime(studyTimeId);


        // display name
        String header = course.getName();
        itemHeader.setText(header);

        // display note
        String note = studyTime.getNote();
        noteView.setText(note);

        // display date, study time, test, section, instructor below

        //hasTest check
        Boolean test = studyTime.isHasTest();
        hasTest.setChecked(test);


        //get time
        hour = studyTime.getHour();
        minutes = studyTime.getMinute();
        editTime.setText(String.format("%02d:%02d", hour, minutes));


        //get section no. and instructor's name
        String section = course.getSection().toString();
        sectionNo.setText(section);

        String instructor = course.getInstructor();
        instructorName.setText(instructor);

        //get image


        //get date
        int day = studyTime.getDay();
        dateSpinner.setSelection(day);

    }


    public void goBack(View view)
    {
        // go back
        Intent intent = new Intent(this, TimetableActivity.class);
        startActivity(intent);
        finish();
    }

    public void save(View view)
    {
        //set note
        String note = noteView.getText().toString();
        studyTime.setNote(note);

        //set has test
        Boolean test = hasTest.isChecked();
        studyTime.setHasTest(test);

        //set time
        studyTime.setHour(hour);
        studyTime.setMinute(minutes);

        //set day
        int position = dateSpinner.getSelectedItemPosition();
        studyTime.setDay(position);

        //save image
        

        //modify
        db.modifyStudyTime(studyTime);
        Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();

        // update the alarm
        Intent mIntent = new Intent(this, AlarmService.class);
        startService(mIntent);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}