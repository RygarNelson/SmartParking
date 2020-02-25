package com.example.pick_a_park;


import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import static android.content.Context.MODE_PRIVATE;


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
        SharedPreferences sharedPreferences = getActivity().getPreferences(MODE_PRIVATE);
        Parametri.nearest_park = sharedPreferences.getBoolean("nearest_park", true);

        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        provider = Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        final Switch nearest_park_button = view.findViewById(R.id.nearest_park_button);
        if (Parametri.nearest_park)
            nearest_park_button.setChecked(true);
        else
            nearest_park_button.setChecked(false);
        nearest_park_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean prova = nearest_park_button.isChecked();
                if(Parametri.nearest_park) {
                    SharedPreferences sharedPreferences1 = getActivity().getPreferences(MODE_PRIVATE);
                    nearest_park_button.setChecked(false);
                    sharedPreferences1 = getActivity().getPreferences(MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences1.edit();
                    editor.putBoolean("nearest_park", false);

                    editor.commit();
                    Parametri.nearest_park = false;
                }
                else{
                    nearest_park_button.setChecked(true);
                    SharedPreferences sharedPreferences1 = getActivity().getPreferences(MODE_PRIVATE);
                    sharedPreferences1 = getActivity().getPreferences(MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences1.edit();
                    editor.putBoolean("nearest_park", true);

                    editor.commit();
                    Parametri.nearest_park = true;
            }
            }
        });

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
