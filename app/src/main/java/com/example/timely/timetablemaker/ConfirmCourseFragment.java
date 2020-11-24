package com.example.timely.timetablemaker;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.timely.R;

public class ConfirmCourseFragment extends DialogFragment {

    EditText input;
    Button createButton, cancelButton;
    OnCreateTimetableListener listener;

    public ConfirmCourseFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_confirm_course, container, false);

        input = view.findViewById(R.id.input_field);
        createButton = view.findViewById(R.id.create_table_button);
        cancelButton = view.findViewById(R.id.cancel_table_button);
        listener = (OnCreateTimetableListener) getActivity();

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (input.getText().toString().trim().equals(""))
                {
                    Toast.makeText(getContext(), "Please enter a number!", Toast.LENGTH_LONG);
                    return;
                }

                int number = Integer.parseInt(input.getText().toString());
                listener.onCreateTimetable(number);
                dismiss();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        return view;
    }


    public interface OnCreateTimetableListener
    {
        void onCreateTimetable(int numberOfCourse);
    }
}