package com.example.pick_a_park;


import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import static com.mapbox.mapboxsdk.Mapbox.getApplicationContext;


/**
 * A simple {@link Fragment} subclass.
 */
public class PR_code extends Fragment  implements ConnessioneListener{

    private ProgressDialog caricamento = null;
    public PR_code() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pr_code, container, false);
        // Inflate the layout for this fragment
        Button btn = view.findViewById(R.id.btnSend);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Send(view);
            }
        });
        return view;
    }

    public void Send(View view){

        TextView code = getView().findViewById(R.id.code);
        String scode = code.getText().toString();
        //Da rimuovere
        PR_password fragment = new PR_password();
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,fragment).commit();
        //sendDataForRecovery(scode);
    }
    private void sendDataForRecovery(String code) {
        // Avverto l'utente del tentativo di invio dei dati di login al server
        caricamento = ProgressDialog.show(getContext(), "Login in corso",
                "Connessione con il server in corso...", true);
        caricamento.show();

        JSONObject postData = new JSONObject();
        try {
            postData.put("email", Parametri.email);
            postData.put("code", code);

        } catch (Exception e) {
            caricamento.dismiss();
            return;
        }

        Connessione conn = new Connessione(postData, "POST");
        conn.addListener(this);
        conn.execute(Parametri.IP + "/api/data/recover_password/code");
    }

    @Override
    public void ResultResponse(String responseCode, String result) {
        if (responseCode == null) {
            caricamento.dismiss();
            Toast.makeText(getContext(), "ERRORE:\nConnessione Assente o server offline.", Toast.LENGTH_LONG).show();
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
            PR_password fragment = new PR_password();
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,fragment).commit();





        }



    }


}
