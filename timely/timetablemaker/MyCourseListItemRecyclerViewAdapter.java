package com.example.timely.timetablemaker;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.TextView;

import com.example.timely.R;
import com.example.timely.courses.Course;

import java.util.List;


public class MyCourseListItemRecyclerViewAdapter extends RecyclerView.Adapter<MyCourseListItemRecyclerViewAdapter.ViewHolder> {

    private final List<Course> mValues;
    private OnCourseLickListener mListener;

    public MyCourseListItemRecyclerViewAdapter(List<Course> items, OnCourseLickListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_course_item, parent, false);
        return new ViewHolder(view, mListener);

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mIdView.setText("" + (position + 1));
        holder.mContentView.setText(mValues.get(position).getName() + " - " + mValues.get(position).getSection());
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final TextView mContentView;
        public Course mItem;
        private int sBgColor;

        public ViewHolder(View view, final OnCourseLickListener listener) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.item_number);
            mContentView = (TextView) view.findViewById(R.id.content);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Animation animation = new AlphaAnimation(1F, 0.5F);
                    animation.setDuration(100L);
                    mView.startAnimation(animation);
                    listener.onCourseLick(mItem.getId());

                }
            });
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }


    public interface OnCourseLickListener
    {
        void onCourseLick(String courseId);
    }
}