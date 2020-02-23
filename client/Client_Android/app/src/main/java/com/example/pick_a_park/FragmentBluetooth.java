package com.example.pick_a_park;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentBluetooth extends Fragment {


    public FragmentBluetooth() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fragment_bluetooth, container, false);
        Button btn = view.findViewById(R.id.Start);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Payment();
            }
        });
        // Inflate the layout for this fragment
        return view;
    }
    public void Payment(){
        //Chiede al server quali sono le mie carte:
        Parametri.cards = new ArrayList<Card>();
        Parametri.cards.add(new Card(1,1,"20/11/2023", "Luca Marasca"));
        Parametri.cards.add(new Card(2,2,"11/08/2026", "Luca Marasca"));
        //Avvia il fragment di pagamento:
        FragmentSelectCard fragment = new FragmentSelectCard();
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,fragment).commit();
    }

}
