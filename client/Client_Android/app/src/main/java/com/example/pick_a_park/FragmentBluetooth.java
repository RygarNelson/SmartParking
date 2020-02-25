package com.example.pick_a_park;


import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentBluetooth extends Fragment implements ConnessioneListener {

    private ProgressDialog caricamento = null;
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
                GetCard();
                //sendDataForGetCard();
            }
        });
        Button btn1 = view.findViewById(R.id.Gone);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Gone();
            }
        });
        // Inflate the layout for this fragment
        return view;
    }
    public void GetCard(){
        //Chiede al server quali sono le mie carte:
        Parametri.cards = new ArrayList<Card>();
        Parametri.cards.add(new Card(1,1,"20/11/2023", "Luca Marasca"));
        Parametri.cards.add(new Card(2,2,"11/08/2026", "Luca Marasca"));
        Parametri.cards.add(new Card(1,1,"20/11/2023", "Luca Marasca"));
        Parametri.cards.add(new Card(2,2,"11/08/2026", "Luca Marasca"));
        Parametri.cards.add(new Card(1,1,"20/11/2023", "Luca Marasca"));
        Parametri.cards.add(new Card(2,2,"11/08/2026", "Luca Marasca"));
        Parametri.cards.add(new Card(1,1,"20/11/2023", "Luca Marasca"));
        Parametri.cards.add(new Card(2,2,"11/08/2026", "Luca Marasca"));
        //Avvia il fragment di pagamento:
        FragmentSelectCard fragment = new FragmentSelectCard();
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,fragment).commit();
    }

    public void sendDataForGetCard(){
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
        conn.execute(Parametri.IP + "/api/data/card/get");
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
            Fragment_PR_password fragment = new Fragment_PR_password();
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,fragment).commit();





        }
    }
    public void Gone()
    {
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
        conn.execute(Parametri.IP + "/api/data/parking/departure");
    }

}
