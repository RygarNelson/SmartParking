package com.example.pick_a_park;


import android.app.ProgressDialog;
import android.content.Intent;
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
public class PR_password extends Fragment implements ConnessioneListener {

    private ProgressDialog caricamento = null;
    public PR_password() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pr_password, container, false);
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

        TextView password1 = getView().findViewById(R.id.password1);
        String spassword1 = password1.getText().toString();
        TextView password2 = getView().findViewById(R.id.password2);
        String spassword2 = password2.getText().toString();
        CheckPassword(spassword1,spassword2);

    }
    private void CheckPassword(String password, String password2){
        if (password.equals(password2)) {
            if (password.equals("")||password2.equals(""))
                Toast.makeText(getContext(), "Password can not be empty", Toast.LENGTH_LONG).show();
            else
                if (password.indexOf(" ") == -1 || password2.indexOf(" ") == -1)
                    sendDataForRecovery(password);
                else
                    Toast.makeText(getContext(), "Password can not contain spaces", Toast.LENGTH_LONG).show();
        }
        else
                Toast.makeText(getContext(), "The two password are not equal", Toast.LENGTH_LONG).show();
    }
    private void sendDataForRecovery(String password) {
        // Avverto l'utente del tentativo di invio dei dati di login al server
        caricamento = ProgressDialog.show(getContext(), "Login in corso",
                "Connessione con il server in corso...", true);
        caricamento.show();

        JSONObject postData = new JSONObject();
        try {
            postData.put("email", Parametri.email);
            postData.put("password", password);

        } catch (Exception e) {
            caricamento.dismiss();
            return;
        }

        Connessione conn = new Connessione(postData, "POST");
        conn.addListener(this);
        conn.execute(Parametri.IP + "/api/data/recover_password/password");
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
            Parametri.email = null;
            Toast.makeText(getContext(), "Password changed!", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);


        }



    }

}
