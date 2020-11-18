package com.example.timely.timetablemaker;

import android.content.Context;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.timely.DatabaseHelper;
import com.example.timely.R;


public class CourseItemFragment extends Fragment {

    private DatabaseHelper db;
    MyCourseListItemRecyclerViewAdapter.OnCourseLickListener listener;
    RecyclerView recyclerView;
    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public CourseItemFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static CourseItemFragment newInstance(int columnCount) {
        CourseItemFragment fragment = new CourseItemFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_course_item_list, container, false);

        db = new DatabaseHelper(getContext());

        listener = (MyCourseListItemRecyclerViewAdapter.OnCourseLickListener) getActivity();

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            recyclerView.setAdapter(new MyCourseListItemRecyclerViewAdapter(db.getAllCourses(), listener));
        }
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (!(getActivity() instanceof MyCourseListItemRecyclerViewAdapter.OnCourseLickListener))
            throw new ClassCastException();
    }


    // update the list
    public void updateList()
    {
        recyclerView.setAdapter(new MyCourseListItemRecyclerViewAdapter(db.getAllCourses(), listener));
    }
}