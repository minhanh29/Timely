package com.example.timely.timetablemaker;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.timely.R;
import com.example.timely.courses.Course;

public class AddCourseFragment extends DialogFragment {

    EditText nameInput, sectionInput, instructorInput;
    Button addButton, cancelButton;

    OnAddCourseListener mListener;

    public AddCourseFragment(){}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_course, container, false);

        mListener = (OnAddCourseListener) getActivity();

        nameInput = view.findViewById(R.id.name_input);
        sectionInput = view.findViewById(R.id.section_input);
        instructorInput = view.findViewById(R.id.instructor_input);

        addButton = view.findViewById(R.id.add_button);
        cancelButton = view.findViewById(R.id.new_cancel_button);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleAddCourse();
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getDialog().getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        return view;
    }


    public void handleAddCourse()
    {
        boolean isValid = true;

        String name = nameInput.getText().toString();
        String instructor = instructorInput.getText().toString();

        if (name.equals("") || instructor.equals(""))
            isValid = false;

        int section = 0;
        try {
            section = Integer.parseInt(sectionInput.getText().toString());
        }
        catch (Exception e)
        {
            isValid = false;
        }

        if (isValid)
        {
            Course course = new Course(name, null, section, instructor);
            mListener.onAddCourse(course);

            getDialog().dismiss();
        }
        else
            Toast.makeText(getContext(), "Please fill out all the fields", Toast.LENGTH_LONG).show();
    }


    public interface OnAddCourseListener{
        void onAddCourse(Course course);
    }
}
