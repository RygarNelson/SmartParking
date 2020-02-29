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


/**
 * A simple {@link Fragment} subclass.
 */
public class Fragment_PR_mail extends Fragment implements ConnessioneListener {

    private ProgressDialog caricamento = null;
    public Fragment_PR_mail() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pr_mail, container, false);
        Button btn = view.findViewById(R.id.btnSend);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Send(view);
            }
        });
        // Inflate the layout for this fragment
        return view;
    }
    public void Send(View view){


        TextView mail = getView().findViewById(R.id.mail);
        String email = mail.getText().toString();
        Parametri.email = email;
        //Da rimuovere
        /*
        Fragment_PR_code fragment = new Fragment_PR_code();
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,fragment).commit();

         */
        sendDataForRecovery(email);


    }

    private void sendDataForRecovery(String email) {
        // Avverto l'utente del tentativo di invio dei dati di login al server
        caricamento = ProgressDialog.show(getContext(), "Login",
                "Connection...", true);
        caricamento.show();

        JSONObject postData = new JSONObject();
        try {
            postData.put("email", email);

        } catch (Exception e) {
            caricamento.dismiss();
            return;
        }

        Connessione conn = new Connessione(postData, "POST");
        conn.addListener(this);
        conn.execute(Parametri.IP + "/api/auth/recover_password");
    }

    @Override
    public void ResultResponse(String responseCode, String result) {
        if (responseCode == null) {
            caricamento.dismiss();
            Toast.makeText(getContext(), "ERROR:\nNo connection or server offline.", Toast.LENGTH_LONG).show();
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

            Fragment_PR_code fragment = new Fragment_PR_code();
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,fragment).commit();





        }
    }

}
