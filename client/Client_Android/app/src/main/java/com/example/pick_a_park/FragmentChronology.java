package com.example.pick_a_park;


import android.app.ProgressDialog;
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
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.widget.ListPopupWindow.MATCH_PARENT;
import static android.widget.ListPopupWindow.WRAP_CONTENT;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentChronology extends Fragment implements ConnessioneListener{
    private ProgressDialog caricamento = null;
    ArrayList<Prenotazione> prenotazioni;
    View view;
    public FragmentChronology() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_fragment_chronology, container, false);

        sendDataGetPark();
        /*
        GetPrenotazioni();
        CreaUI();
        */

        // Inflate the layout for this fragment
        return view;
    }
    //METODO SERVER OFFLINE, metto i parcheggi a mano
    public void GetPrenotazioni(){
        prenotazioni = new ArrayList<Prenotazione>();
        //Simulo qualche prenotazione a mano
        prenotazioni.add(new Prenotazione(1, "data1", 2.0,1));
        prenotazioni.add(new Prenotazione(1, "data1", 2.0,1));
        prenotazioni.add(new Prenotazione(1, "data1", 2.0,1));
        prenotazioni.add(new Prenotazione(1, "data1", 2.0,1));
        prenotazioni.add(new Prenotazione(1, "data1", 2.0,1));
        prenotazioni.add(new Prenotazione(1, "data1", 2.0,1));


        //Inserire QUI LA RICHIESTA DELLA LISTA DELLE PRENOTAZIONI AL SERVER

    }
    //Crea la grafica inserendo le varie prenotazioni
    public void CreaUI()
    {
        View linearLayout = view.findViewById(R.id.linearLayoutVisualizza);
        TextView[] info_parcheggio = new TextView[prenotazioni.size()];
        View [] separators = new View[prenotazioni.size()];




        for (int i = 0; i < prenotazioni.size();i++)
        {

            //Immagine parcheggio

            info_parcheggio[i] = new TextView(view.getContext());
            if(prenotazioni.get(i).Data.isEmpty())
            {
                info_parcheggio[i].setText("ID: "+prenotazioni.get(i).id+"\n"+"Data Arrivo: N/A"+"\nPrezzo:"+prenotazioni.get(i).cash+"\nStato: "
                        +state.values()[prenotazioni.get(i).stato]);
            }
            if(prenotazioni.get(i).cash == 0.0)
            {
                info_parcheggio[i].setText("ID: "+prenotazioni.get(i).id+"\nData Arrivo:"+prenotazioni.get(i).Data+"\n"+"Prezzo: N/A"+"\nStato: "
                        +state.values()[prenotazioni.get(i).stato]);
            }
            if (prenotazioni.get(i).cash == 0.0 && prenotazioni.get(i).Data.isEmpty())
                info_parcheggio[i].setText("ID: "+prenotazioni.get(i).id+"\n"+"Data Arrivo: N/A"+"\n"+"Prezzo: N/A"+"\nStato: "
                        +state.values()[prenotazioni.get(i).stato]);
            if (prenotazioni.get(i).cash != 0.0 && !prenotazioni.get(i).Data.isEmpty())
                info_parcheggio[i].setText("ID: "+prenotazioni.get(i).id+"\nData Arrivo: "+prenotazioni.get(i).Data+"\nPrezzo: "+prenotazioni.get(i).cash+"\nStato: "
                +state.values()[prenotazioni.get(i).stato]);

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
    //Chiedo La lista dei parcheggi al server
    private void sendDataGetPark() {
        // Avverto l'utente del tentativo di invio dei dati di login al server
        caricamento = ProgressDialog.show(getContext(), "Login",
                "Connection...", true);
        caricamento.show();

        JSONObject postData = new JSONObject();
        try {
            postData.put("email", Parametri.email);

        } catch (Exception e) {
            caricamento.dismiss();
            return;
        }

        Connessione conn = new Connessione(postData, "POST");
        conn.addListener(this);
        conn.execute(Parametri.IP + "/api/data/history/get");
    }

    @Override
    public void ResultResponse(String responseCode, String result) {
        if (responseCode == null) {
            caricamento.dismiss();
            Toast.makeText(getContext(), "ERROR:\nNo connection or Server Offline.", Toast.LENGTH_LONG).show();
            return;
        }

        if (responseCode.equals("400")) {
            String message = Connessione.estraiErrore(result);
            caricamento.dismiss();
            Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
            return;
        }
        if (responseCode.equals("200")) {
            caricamento.dismiss();
            prenotazioni = new ArrayList<Prenotazione>();
            try {

                JSONObject risposta = new JSONObject(result);
                JSONArray js_prenotazioni = new JSONArray(risposta.getString("history"));
                for(int i=0; i<js_prenotazioni.length(); i++){
                    JSONObject prenotazione = (JSONObject) js_prenotazioni.get(i);

                    prenotazioni.add(new Prenotazione(prenotazione.getInt("idParking"),prenotazione.getString("dateArrival"),
                            prenotazione.getDouble("cashAmount"), prenotazione.getInt("code")));
                }


            }catch (Exception e){

            }
            CreaUI();






        }



    }

}
enum state{
    IN_CORSO,
    PARCHEGGIATO,
    COMPLETATO
}
