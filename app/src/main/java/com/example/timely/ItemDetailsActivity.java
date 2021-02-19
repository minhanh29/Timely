package com.example.timely;

import android.Manifest;
import android.content.DialogInterface;
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
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.TimePickerDialog;

import java.io.InputStream;
import java.util.Calendar;

import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.timely.courses.Course;
import com.example.timely.courses.StudyTime;
import com.example.timely.settings.AlarmManager;
import com.example.timely.settings.AlarmService;
import com.example.timely.settings.NotificationSettingsActivity;
import com.example.timely.timetable.TimetableActivity;

import android.widget.AdapterView;



public class ItemDetailsActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, TextWatcher {
    TimePickerDialog pickerTime;
    EditText sectionNo, instructorName, editDuration, note;
    TextView editTime;
    CheckBox hasTest;
    Spinner dateSpinner;
    private Course course;
    private StudyTime studyTime;
    private DatabaseHelper db;
    private String courseId;
    private int studyTimeId;
    private int hour, minutes;

    private ImageView imageNote;
    private String selectedImagePath;

    // Views
    TextView noteView, itemHeader;
    LinearLayout layoutImage;
    Button deleteImageBtn;

    private static final int REQUEST_CODE_STORAGE_PERMISSION = 1;
    private static final int REQUEST_CODE_SELECT_IMAGE = 2;

    private boolean changed = false;
    private boolean first = true;

    Animation animation;

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
        editDuration = findViewById(R.id.editDuration);
        deleteImageBtn = findViewById(R.id.deleteImageBtn);
        layoutImage = findViewById(R.id.layoutAddImage);
        note = findViewById(R.id.note);

        selectedImagePath = "";

        changed = false;
        first = true;
        // text changes
        sectionNo.addTextChangedListener(this);
        instructorName.addTextChangedListener(this);
        editDuration.addTextChangedListener(this);
        note.addTextChangedListener(this);
        hasTest.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                changed = true;
            }
        });

        initMiscellaneuos();

        // animations
        animation = new AlphaAnimation(1F, 0.5F);
        animation.setDuration(100L);

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
        adapterdate.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
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
                        Calendar cal = Calendar.getInstance();
                        cal.set(Calendar.HOUR_OF_DAY, hour);
                        cal.set(Calendar.MINUTE, minutes);
                        editTime.setText(android.text.format.DateFormat.format("hh:mm aa", cal));
                        changed = true;
                    }
                }, hour, minutes,false);
                pickerTime.show();
            }
        });

        updateScreen();
    }


    @Override
    protected void onResume() {
        super.onResume();
        changed = false;
    }

    //    Add Images
    private void initMiscellaneuos() {
        layoutImage.setOnClickListener(new View.OnClickListener() {
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
        changed = true;
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
                        layoutImage.setVisibility(View.INVISIBLE);
                        deleteImageBtn.setVisibility(View.VISIBLE);

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

        //hasTest check
        Boolean test = studyTime.isHasTest();
        hasTest.setChecked(test);


        //get time
        hour = studyTime.getHour();
        minutes = studyTime.getMinute();
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minutes);
        editTime.setText(android.text.format.DateFormat.format("hh:mm aa", cal));

        //get duration
        int duration = studyTime.getDuration();
        editDuration.setText("" + duration);

        //get section no. and instructor's name
        String section = course.getSection().toString();
        sectionNo.setText(section);

        String instructor = course.getInstructor();
        instructorName.setText(instructor);

        //get image
        String path = studyTime.getImagePath();
        selectedImagePath = path;
        if (path != null && !path.equals(""))
        {
            imageNote.setImageBitmap(BitmapFactory.decodeFile(selectedImagePath));
            imageNote.setVisibility(View.VISIBLE);
            layoutImage.setVisibility(View.INVISIBLE);
            deleteImageBtn.setVisibility(View.VISIBLE);
        }

        //get date
        int day = studyTime.getDay();
        dateSpinner.setSelection(day);
    }


    public void goBack(View view)
    {
        if (changed)
        {
            changed = false;
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Do you want to save changes?");
            builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ItemDetailsActivity.this.save();
                    ItemDetailsActivity.this.goBack();
                }
            });
            builder.setNegativeButton("Discard", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ItemDetailsActivity.this.goBack();
                }
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        } else {
            // go back
            goBack();
        }
    }

    public void goBack()
    {
        Intent intent = new Intent(this, TimetableActivity.class);
        startActivity(intent);
        finish();
    }

    public void save()
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

        //set duration
        String duration = editDuration.getText().toString();
        int dur = Integer.parseInt(duration);
        studyTime.setDuration(dur);

        //set day
        int position = dateSpinner.getSelectedItemPosition();
        studyTime.setDay(position);

        //save image
        studyTime.setImagePath(selectedImagePath);
        //modify
        db.modifyStudyTime(studyTime);
        Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();

        // update the alarm
        AlarmManager.updateAlarm(this);
    }

    public void save(View view)
    {
        view.startAnimation(animation);
        save();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (first)
            first = false;
        else
            changed = true;
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) { }

    public void deleteImage(View view) {
        selectedImagePath = "";
        imageNote.setVisibility(View.GONE);

        layoutImage.setVisibility(View.VISIBLE);
        deleteImageBtn.setVisibility(View.INVISIBLE);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        changed = true;
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}