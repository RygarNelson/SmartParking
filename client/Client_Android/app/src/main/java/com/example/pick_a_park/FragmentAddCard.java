package com.example.pick_a_park;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.Random;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentAddCard extends FragmentWithOnBack implements ConnessioneListener {
    EditText Code;
    EditText Expire;
    EditText Holder;
    EditText CVV;
    private ProgressDialog caricamento = null;

    public FragmentAddCard() {

        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fragment_add_card, container, false);
        Button confirm = view.findViewById(R.id.confirm);
        Code = view.findViewById(R.id.Code);
        Expire = view.findViewById(R.id.Data);
        Holder = view.findViewById(R.id.Name_data);
        CVV = view.findViewById(R.id.CVV);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String code_data = Code.getText().toString();
                String expire_data = Expire.getText().toString();
                String holder_data = Holder.getText().toString();
                String cvv_data = CVV.getText().toString();

                SendData(code_data,expire_data,holder_data,cvv_data);
            }
        });
        // Inflate the layout for this fragment
        return view;
    }
    void SendData(String code_data, String expire_data, String holder_data, String cvv_data){
        // Avverto l'utente del tentativo di invio dei dati di login al server
        caricamento = ProgressDialog.show(getContext(), "Login",
                "Connection...", true);
        caricamento.show();

        JSONObject postData = new JSONObject();
        try {
            Random random = new Random();
            final double randomNumber =  Math.floor((random.nextDouble()* (10 - 1) + 1)*100)/100;
            postData.put("email", Parametri.email);
            postData.put("card", code_data);
            postData.put("expires", expire_data);
            postData.put("holder", holder_data);
            postData.put("cvv",cvv_data);
            postData.put("amount",randomNumber);


        } catch (Exception e) {
            caricamento.dismiss();
            return;
        }

        Connessione conn = new Connessione(postData, "POST");
        conn.addListener(this);
        conn.execute(Parametri.IP + "/api/data/card/new");
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
            Toast.makeText(getContext(), "Card added", Toast.LENGTH_LONG).show();
            startActivity(new Intent(getActivity(), MainActivity.class));
            getActivity().finish();


        }
    }
    @Override
    public void onBackPressed(){
        FragmentSelectCard fragment = new FragmentSelectCard();
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,fragment).commit();
    }
}
