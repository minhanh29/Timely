package com.example.timely.timetablemaker;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.timely.R;
import com.example.timely.courses.StudyTime;

import java.util.Calendar;


public class TimeRowFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private static final String HOUR = "HOUR";
    private static final String MINUTE = "MINUTE";
    private static final String DURATION = "DURATTION";
    private static final String DAY = "DAY";


    private Spinner daySpinner;
    private TextView timeView;
    private EditText durationInput;
    private Button deleteButton;

    private int day = 0, hour = 0, minutes = 0, duration = 0;
    private String courseId;
    private boolean firstTime = true;
    private OnTimeRowListener listener;

    public TimeRowFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
        {
            courseId = getArguments().getString(TimetableMakerActivity.COURSE_ID);
        }

        if (savedInstanceState != null)
        {
            hour = savedInstanceState.getInt(HOUR);
            minutes = savedInstanceState.getInt(MINUTE);
            duration = savedInstanceState.getInt(DURATION);
            day = savedInstanceState.getInt(DAY);
        }

        firstTime = true;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_time_row, container, false);
        listener = (OnTimeRowListener) getActivity();

        daySpinner = view.findViewById(R.id.daySpinner);
        timeView = view.findViewById(R.id.timeView);
        durationInput = view.findViewById(R.id.durationInput);
        deleteButton = view.findViewById(R.id.destroyButton);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.week_days, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        daySpinner.setAdapter(adapter);
        daySpinner.setOnItemSelectedListener(this);

        durationInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                listener.onItemChange(true);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // time picker
        timeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        // initialize (values passed from latter parameters)
                        hour = hourOfDay;
                        minutes = minute;

                        Calendar calendar = Calendar.getInstance();
                        calendar.set(0, 0, 0, hour, minutes);

                        timeView.setText(DateFormat.format("hh:mm aa", calendar));
                        listener.onItemChange(true);
                    }
                }, hour, minutes, false);  // initial values

                timePickerDialog.updateTime(hour, minutes);
                timePickerDialog.show();
            }
        });


        // delete the fragment
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onDeleteStudyTimeFragment(TimeRowFragment.this);
            }
        });

        // update the content for each one
        daySpinner.setSelection(day);
        Calendar calendar = Calendar.getInstance();
        calendar.set(0, 0, 0, hour, minutes);
        timeView.setText(DateFormat.format("hh:mm aa", calendar));
        durationInput.setText("" + duration);

        return view;
    }


    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(HOUR, hour);
        outState.putInt(MINUTE, minutes);
        outState.putInt(DURATION, duration);
        outState.putInt(DAY, day);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        day = position;

        if (firstTime)
            firstTime = false;
        else
            listener.onItemChange(true);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }


    // return the StudyTime object fro this row
    public StudyTime getStudyTime()
    {
        // retrieve the duration
        try {
            duration = Integer.parseInt(durationInput.getText().toString());
        }
        catch (Exception e) { duration = 0; }

        StudyTime time = new StudyTime(day, hour, minutes, duration, courseId);

        return time;
    }


    public void setStudyTime(StudyTime time)
    {
        day = time.getDay();
        hour = time.getHour();
        minutes = time.getMinute();
        duration = time.getDuration();
    }


    // delete fragment
    public interface OnTimeRowListener
    {
        void onDeleteStudyTimeFragment(Fragment fragment);
        void onItemChange(boolean change);
    }
}