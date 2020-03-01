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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mapbox.mapboxsdk.plugins.annotation.Line;

import org.json.JSONObject;

import java.util.Random;

import static com.mapbox.mapboxsdk.Mapbox.getApplicationContext;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentPayment extends Fragment implements ConnessioneListener{
    EditText cvv;
    private ProgressDialog caricamento = null;
    public FragmentPayment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final int card = getArguments().getInt("card_number");
        View view = inflater.inflate(R.layout.fragment_fragment_payment, container, false);
        TextView card_number = view.findViewById(R.id.tv_card_number);
        card_number.setText(Parametri.cards.get(card).code);
        TextView validity = view.findViewById(R.id.tv_validity);
        validity.setText(Parametri.cards.get(card).expire);
        TextView name = view.findViewById(R.id.tv_member_name);
        name.setText(Parametri.cards.get(card).name);
        Random random = new Random();
        final double randomNumber =  Math.floor((random.nextDouble()* (10 - 1) + 1)*100)/100;

        TextView amount = view.findViewById(R.id.Amount);
        amount.setText("Amount: "+ Double.toString(randomNumber)+" €");
        cvv = view.findViewById(R.id.CCV_data);

        Button cancel = view.findViewById(R.id.btnCancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentHome fragment = new FragmentHome();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,fragment).commit();
            }
        });
        Button send = view.findViewById(R.id.btnSend);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String cvv_data = cvv.getText().toString();
                SendPayment(card,randomNumber,cvv_data);
            }
        });
        // Inflate the layout for this fragment
        return view;
    }
    public void SendPayment(int card, double payment, String cvv_data)
    {
        // Avverto l'utente del tentativo di invio dei dati di login al server
        caricamento = ProgressDialog.show(getContext(), "Login",
                "Connection...", true);
        caricamento.show();

        JSONObject postData = new JSONObject();
        try {
            postData.put("email", Parametri.email);
            postData.put("card", Parametri.cards.get(card).code);
            postData.put("cash", payment);
            postData.put("cvv",cvv_data);


        } catch (Exception e) {
            caricamento.dismiss();
            return;
        }

        Connessione conn = new Connessione(postData, "POST");
        conn.addListener(this);
        conn.execute(Parametri.IP + "/api/data/parking/payment");
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
            Toast.makeText(getContext(), "Pagamento Effettuato", Toast.LENGTH_LONG).show();
            startActivity(new Intent(getActivity(), MainActivity.class));
            getActivity().finish();


        }
    }
}
