package com.example.pick_a_park;


import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RemoteViews;

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
        TextView tv =  view.findViewById(R.id.card_select);
        tv.setRotation(-45);
        View main = view.findViewById(R.id.linearLayoutVisualizza);

        TextView []card_numbers =  new TextView[Parametri.cards.size()];
        TextView []validities =  new TextView[Parametri.cards.size()];
        TextView []names =  new TextView[Parametri.cards.size()];
        //Array di view inerenti alle carte
        View []children =  new View[Parametri.cards.size()];


        float density = getContext().getResources()
                .getDisplayMetrics()
                .density;
        int width = Math.round((float) 230 * density);
        for (int i = 0; i < Parametri.cards.size(); i++)
        {
            final int x = i;
            children[i] = LayoutInflater.from(getContext()).inflate(R.layout.fragment_fragment_select_card, null);

            children[i].findViewById(R.id.Payment).setVisibility(View.GONE);
            //Tolgo la scritta aggiungi nuova carta
            children[i].findViewById(R.id.card_select1).setVisibility(View.INVISIBLE);;

            //Setto il codice della carta
            card_numbers[i] = children[i].findViewById(R.id.tv_card_number1);
            card_numbers[i].setText(Integer.toString(Parametri.cards.get(i).code));
            //setto la data di scadenza
            validities[i] = children[i].findViewById(R.id.tv_validity1);
            validities[i].setText(Parametri.cards.get(i).expire);
            //setto il nome del proprietario
            names[i] = children[i].findViewById(R.id.tv_member_name1);
            names[i].setText(Parametri.cards.get(i).name);
          

            children[i].findViewById(R.id.rl_card1).setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, width));
            children[i].findViewById(R.id.rl_card1).setVisibility(View.VISIBLE);
            children[i].findViewById(R.id.rl_card).setVisibility(View.GONE);
            children[i].findViewById(R.id.rl_card1).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Payment(x);
                }
            });
            ((LinearLayout) main).addView(children[i]);

        }

        main.findViewById(R.id.rl_card).getBackground().setColorFilter(Color.parseColor("#C4C4C4"),PorterDuff.Mode.ADD);
        main.findViewById(R.id.rl_card).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AddCard();
            }
        });
        //Blank space at the end (Graphics only)
        View blank = new View(getContext());
        blank.setVisibility(View.INVISIBLE);
        int height = Math.round((float) 50 * density);
        blank.setLayoutParams(new LinearLayout.LayoutParams(height, height));
        ((LinearLayout) main).addView(blank);
        // Inflate the layout for this fragment
        return view;
    }
    public void Payment(int x){
        //Avvia il fragment di pagamento:
        FragmentPayment fragment = new FragmentPayment();
        Bundle data = new Bundle();
        data.putInt("card_number", x);
        fragment.setArguments(data);
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,fragment).commit();
    }
    public void AddCard(){
        FragmentAddCard fragment = new FragmentAddCard();
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,fragment).commit();
    }
}
