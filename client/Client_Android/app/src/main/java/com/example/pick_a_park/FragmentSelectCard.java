package com.example.pick_a_park;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentSelectCard extends Fragment {


    public FragmentSelectCard() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_fragment_select_card, container, false);
        TextView tv = (TextView) view.findViewById(R.id.card_select);
        tv.setRotation(-45);
        View main = view.findViewById(R.id.linearLayoutVisualizza);
        View []children  = new RelativeLayout[Parametri.cards.size()];
        TextView []card_numbers =  new TextView[Parametri.cards.size()];
        TextView []validities =  new TextView[Parametri.cards.size()];
        TextView []names =  new TextView[Parametri.cards.size()];

        if(Parametri.cards.size()==1) {
            children[0] = view.findViewById(R.id.rl_card0);
            children[0].setVisibility(View.VISIBLE);
            card_numbers[0] = view.findViewById(R.id.tv_card_number0);
            card_numbers[0].setText((Integer.toString(Parametri.cards.get(0).code)));
            validities[0] = view.findViewById(R.id.tv_validity0);
            validities[0].setText(Parametri.cards.get(0).expire);
            names[0] = view.findViewById(R.id.tv_member_name0);
            names[0].setText(Parametri.cards.get(0).name);
            ((LinearLayout) main).addView(card_numbers[0]);
            ((LinearLayout) main).addView(validities[0]);
            ((LinearLayout) main).addView(names[0]);
        }
        if(Parametri.cards.size()==2) {
            children[0] = view.findViewById(R.id.rl_card0);
            children[0].setVisibility(View.VISIBLE);
            children[0].setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view){
                    FragmentPayment fragment = new FragmentPayment();
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,fragment).commit();
                }
            });
            card_numbers[0] = view.findViewById(R.id.tv_card_number0);
            card_numbers[0].setText((Integer.toString(Parametri.cards.get(0).code)));
            validities[0] = view.findViewById(R.id.tv_validity0);
            validities[0].setText(Parametri.cards.get(0).expire);
            names[0] = view.findViewById(R.id.tv_member_name0);
            names[0].setText(Parametri.cards.get(0).name);


            children[1] = view.findViewById(R.id.rl_card1);
            children[1].setVisibility(View.VISIBLE);
            children[1].setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view){
                    FragmentPayment fragment = new FragmentPayment();
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,fragment).commit();
                }
            });
            card_numbers[1] = view.findViewById(R.id.tv_card_number1);
            card_numbers[1].setText((Integer.toString(Parametri.cards.get(1).code)));
            validities[1] = view.findViewById(R.id.tv_validity1);
            validities[1].setText(Parametri.cards.get(1).expire);
            names[1] = view.findViewById(R.id.tv_member_name1);
            names[1].setText(Parametri.cards.get(1).name);

        }

        // Inflate the layout for this fragment
        return view;
    }

}
