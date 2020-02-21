package com.example.pick_a_park;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentSettings extends Fragment {
    String provider;

    public FragmentSettings() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        provider = Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        Switch gps = view.findViewById(R.id.gps_button);
        if(provider.contains("gps")){ //if gps is enabled
           gps.setChecked(true);
        }
        else
            gps.setChecked(false);
        gps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(provider.contains("gps")){
                    turnGPSOn();
                }
                else
                    turnGPSOff();
            }
        });
        TextView about = view.findViewById(R.id.about);
        about.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                FragmentAbout fragment = new FragmentAbout();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,fragment).commit();
            }
        });
        // Inflate the layout for this fragment
        return view;
    }
    private void turnGPSOn(){
        if(!provider.contains("gps")){ //if gps is disabled
            final Intent poke = new Intent();
            poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
            poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
            poke.setData(Uri.parse("3"));
            getContext().sendBroadcast(poke);
        }
    }

    private void turnGPSOff(){
        final Intent poke = new Intent();
        poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
        poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
        poke.setData(Uri.parse("3"));
        getContext().sendBroadcast(poke);
    }
}
