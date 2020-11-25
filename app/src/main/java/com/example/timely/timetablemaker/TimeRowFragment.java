package com.example.timely.timetablemaker;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
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

    private Spinner daySpinner;
    private TextView timeView;
    private EditText durationInput;
    private Button deleteButton;

    private int day = 0, hour = 0, minutes = 0, duration = 0;
    private String courseId;

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
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_time_row, container, false);

        daySpinner = view.findViewById(R.id.daySpinner);
        timeView = view.findViewById(R.id.timeView);
        durationInput = view.findViewById(R.id.durationInput);
        deleteButton = view.findViewById(R.id.destroyButton);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.week_days, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        daySpinner.setAdapter(adapter);
        daySpinner.setOnItemSelectedListener(this);

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

                        timeView.setText(DateFormat.format("hh:mm", calendar));
                    }
                }, hour, minutes, false);  // initial values

                timePickerDialog.updateTime(hour, minutes);
                timePickerDialog.show();
            }
        });


        // delete the fragment
        final OnDeleteStudyTimeFragmentListener listener = (OnDeleteStudyTimeFragmentListener) getActivity();
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
        timeView.setText(DateFormat.format("HH:mm", calendar));
        durationInput.setText("" + duration);

        return view;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        day = position;
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
    public interface OnDeleteStudyTimeFragmentListener
    {
        void onDeleteStudyTimeFragment(Fragment fragment);
    }
}