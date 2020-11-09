package com.example.timely;

import android.app.TimePickerDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

public class NotificationSettingsActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    TextView wakeTimer, sleepTimer;
    int wakeHour, wakeMinute, sleepHour, sleepMinute;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_settings);
        wakeTimer = findViewById(R.id.wakeTime);
        sleepTimer = findViewById(R.id.sleepTime);

        Spinner spinner1 = findViewById(R.id.studySpinner);
        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this,
                R.array.timeBeforeClass, android.R.layout.simple_spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner1.setAdapter(adapter1);
        spinner1.setOnItemSelectedListener(this);

        Spinner spinner2 = findViewById(R.id.testSpinner);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this,
                R.array.timeBeforeTest, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner2.setAdapter(adapter2);
        spinner2.setOnItemSelectedListener(this);


        wakeTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(
                        NotificationSettingsActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                wakeHour = hourOfDay;
                                wakeMinute = minute;

                                Calendar calendar1 = Calendar.getInstance();

                                calendar1.set(0, 0, 0, wakeHour, wakeMinute);

                                wakeTimer.setText(DateFormat.format("hh:mm aa",calendar1));
                            }
                        }, 12, 0, false
                );
                timePickerDialog.updateTime(wakeHour, wakeMinute);
                timePickerDialog.show();
            }

        });

        sleepTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(
                        NotificationSettingsActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                sleepHour = hourOfDay;
                                sleepMinute = minute;

                                Calendar calendar1 = Calendar.getInstance();

                                calendar1.set(0, 0, 0, sleepHour, sleepMinute);

                                sleepTimer.setText(android.text.format.DateFormat.format("hh:mm aa",calendar1));
                            }
                        }, 12, 0, false
                );
                timePickerDialog.updateTime(wakeHour, wakeMinute);
                timePickerDialog.show();
            }

        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String text = parent.getItemAtPosition(position).toString();
        Toast.makeText(parent.getContext(), text, Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }
}