package com.example.timely.timetable;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v7.widget.AppCompatTextView;
import android.view.Gravity;
import android.view.View;

import com.example.timely.R;
import com.example.timely.courses.StudyTime;

public class CourseView  extends AppCompatTextView implements View.OnClickListener {
    private final int HOUR = 180;   // height of a course view for 1 hour in dp
    private final int START_HOUR = 0;  // the default earliest hour in the timetable

    private StudyTime time;
    private OnCourseSelected selector;
    private int color, selectedColor;
    private boolean isSelected;

    public CourseView(Context context, String name, StudyTime time, int color, int selectedColor, Typeface typeface, OnCourseSelected selector) {
        super(context);
        this.time = time;
        this.selector = selector;
        this.color = color;
        this.selectedColor = selectedColor;
        isSelected = false;

        setText(name);
        setId(View.generateViewId());
        setGravity(Gravity.CENTER_HORIZONTAL);
        setTextColor(Color.WHITE);
        setTypeface(typeface, Typeface.BOLD);
        setBackgroundResource(color);
        setOnClickListener(this);

        // mark as has test
        if (time.isHasTest())
            setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, R.drawable.ic_star);
    }

    public void drawView(ConstraintLayout[] daysLayout)
    {
        // compute the height based on the course's duration
        int height = (int) (time.getDuration() / 60.0 * HOUR);
        setHeight(height);

        // compute the start point for the view based on
        // hour and minute
        int startTime = (int) ((time.getHour() - START_HOUR + (double) time.getMinute()/60.0) * HOUR);

        // and view to corresponding layout
        int day = time.getDay();
        daysLayout[day].addView( this, 0);

        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(daysLayout[day]);
        constraintSet.connect(getId(), ConstraintSet.TOP, daysLayout[day].getId(), ConstraintSet.TOP, startTime);
        constraintSet.connect(getId(), ConstraintSet.LEFT, daysLayout[day].getId(), ConstraintSet.LEFT, 0);
        constraintSet.connect(getId(), ConstraintSet.RIGHT, daysLayout[day].getId(), ConstraintSet.RIGHT, 0);
        constraintSet.applyTo(daysLayout[day]);

        // set width to match_parent
        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) getLayoutParams();
        layoutParams.width = ConstraintLayout.LayoutParams.MATCH_PARENT;
    }


    public String getCourseId()
    {
        return time.getCourseId();
    }


    public int getStudyTimeId()
    {
        return time.getId();
    }

    @Override
    public void onClick(View v) {
        boolean wasSelected = isSelected;

        // click to unselect
        if (wasSelected)
            selector.onCourseSelected(null, -1);
        else
            selector.onCourseSelected(time.getCourseId(), time.getId());

        isSelected = !wasSelected;
        setBackgroundResource(isSelected ? selectedColor : color);
    }

    // unselect the course
    public void unselect()
    {
        isSelected = false;
        setBackgroundResource(color);
    }

    // communicate with other activity
    public interface OnCourseSelected
    {
        void onCourseSelected(String courseId, int studyTimeId);
    }
}
