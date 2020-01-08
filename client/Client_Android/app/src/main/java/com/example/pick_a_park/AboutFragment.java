package com.example.pick_a_park;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

import static android.content.ContentValues.TAG;
import static android.provider.MediaStore.Files.FileColumns.PARENT;


/**
 * A simple {@link Fragment} subclass.
 */
public class AboutFragment extends Fragment {

    ScrollView parentScroll;
    public ScrollView childScroll;
    public AboutFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate (R.layout.fragment_about, container, false);

        parentScroll=(ScrollView)view.findViewById(R.id.scroll);
        childScroll=(ScrollView)view.findViewById(R.id.scroll_mission);


        parentScroll.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {

                view.findViewById(R.id.scroll_mission).getParent().requestDisallowInterceptTouchEvent(false);
                return false;
            }
        });
        childScroll.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event)
            {

                // Disallow the touch request for parent scroll on touch of child view
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

        // Inflate the layout for this fragment
        return view;
    }

}
