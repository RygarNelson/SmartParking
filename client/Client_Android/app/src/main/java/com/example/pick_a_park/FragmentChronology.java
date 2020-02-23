package com.example.pick_a_park;


import android.graphics.Color;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.widget.ListPopupWindow.MATCH_PARENT;
import static android.widget.ListPopupWindow.WRAP_CONTENT;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentChronology extends Fragment {

    ArrayList<Prenotazione> prenotazioni;

    public FragmentChronology() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fragment_chronology, container, false);

        GetPrenotazioni();
        CreaUI(view);

        // Inflate the layout for this fragment
        return view;
    }
    public void GetPrenotazioni(){
        prenotazioni = new ArrayList<Prenotazione>();
        //Simulo qualche prenotazione a mano
        prenotazioni.add(new Prenotazione(1,"Parcheggio_Bello", "data1"));
        prenotazioni.add(new Prenotazione(2,"Parcheggio_Bello2", "data2"));
        prenotazioni.add(new Prenotazione(2,"Parcheggio_Bello2", "data2"));
        prenotazioni.add(new Prenotazione(2,"Parcheggio_Bello2", "data2"));
        prenotazioni.add(new Prenotazione(2,"Parcheggio_Bello2", "data2"));
        prenotazioni.add(new Prenotazione(2,"Parcheggio_Bello2", "data2"));
        prenotazioni.add(new Prenotazione(2,"Parcheggio_Bello2", "data2"));
        prenotazioni.add(new Prenotazione(2,"Parcheggio_Bello2", "data2"));

        //Inserire QUI LA RICHIESTA DELLA LISTA DELLE PRENOTAZIONI AL SERVER

    }
    //Crea la grafica inserendo le varie prenotazioni
    public void CreaUI(View view)
    {
        View linearLayout = view.findViewById(R.id.linearLayoutVisualizza);
        TextView[] info_parcheggio = new TextView[prenotazioni.size()];
        View [] separators = new View[prenotazioni.size()];




        for (int i = 0; i < prenotazioni.size();i++)
        {
            //Immagine parcheggio

            info_parcheggio[i] = new TextView(view.getContext());
            info_parcheggio[i].setText(prenotazioni.get(i).id+"\n"+prenotazioni.get(i).NomeParcheggio+"\n"+prenotazioni.get(i).Data+"\n");

            info_parcheggio[i].setPaddingRelative(0, 8, 0, 8);
            // Setto l'id della text view come indice del vettore dei parcheggi vicini
            info_parcheggio[i].setId(i);
            info_parcheggio[i].setTextSize(19);
            info_parcheggio[i].setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
            info_parcheggio[i].setTextColor(Color.BLACK);
            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            param.setMargins(0,0,0,5);
            info_parcheggio[i].setLayoutParams(param);
            //Aggiungo la textview
            ((LinearLayout) linearLayout).addView(info_parcheggio[i]);

            LinearLayout.LayoutParams paramview = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    1
            );
            paramview.setMargins(0,0,0,30);
            separators[i] = new ImageView(view.getContext());
            separators[i].setLayoutParams(paramview);
            separators[i].setBackgroundResource(R.color.dark_gray);
            separators[i].setLayoutParams(paramview);
            ((LinearLayout) linearLayout).addView(separators[i]);

        }
    }

}
