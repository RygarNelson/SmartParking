package com.example.pick_a_park;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mapbox.mapboxsdk.plugins.annotation.Line;

import java.util.Random;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentPayment extends Fragment {


    public FragmentPayment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fragment_payment, container, false);
        TextView card_number = view.findViewById(R.id.tv_card_number);
        card_number.setText((Integer.toString(Parametri.cards.get(0).code)));
        TextView validity = view.findViewById(R.id.tv_validity);
        validity.setText(Parametri.cards.get(0).expire);
        TextView name = view.findViewById(R.id.tv_member_name);
        name.setText(Parametri.cards.get(0).name);
        Random random = new Random();
        double randomNumber = random.nextDouble()* (10 - 1) + 1;
        randomNumber = Math.floor(randomNumber * 100) / 100;
        TextView amount = view.findViewById(R.id.Amount);
        amount.setText("Amount: "+ Double.toString(randomNumber)+" €");
        // Inflate the layout for this fragment
        return view;
    }

}
