package com.example.pick_a_park;

import androidx.fragment.app.Fragment;

// Classe dei fragment con la gestione del tasto onBack tramite MainActivity
public class FragmentWithOnBack extends Fragment {
    public boolean onBackPressed() {
        return false;
    }
}