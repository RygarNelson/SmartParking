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

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static com.example.pick_a_park.LoginActivity.SHA1;
import static com.mapbox.mapboxsdk.Mapbox.getApplicationContext;


/**
 * A simple {@link Fragment} subclass.
 */
public class Fragment_PR_password extends Fragment implements ConnessioneListener {

    private ProgressDialog caricamento = null;
    public Fragment_PR_password() {
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

        try {
            password = SHA1(password);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Riscontrati problemi nell' hashing della password.", Toast.LENGTH_LONG).show();
            return;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Riscontrati problemi nell' hashing della password.", Toast.LENGTH_LONG).show();
            return;
        }
        // Avverto l'utente del tentativo di invio dei dati di login al server
        caricamento = ProgressDialog.show(getContext(), "Login",
                "Connection...", true);
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
        conn.execute(Parametri.IP + "/api/auth/recover_password/password");
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
            Parametri.email = null;
            Toast.makeText(getContext(), "Password changed!", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);


        }



    }
    // Criptazione SHA1
    public static String SHA1(String text) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        byte[] sha1hash;
        md.update(text.getBytes("iso-8859-1"), 0, text.length());
        sha1hash = md.digest();
        return convertToHex(sha1hash);
    }

    private static String convertToHex(byte[] data) {
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < data.length; i++) {
            int halfbyte = (data[i] >>> 4) & 0x0F;
            int two_halfs = 0;
            do {
                if ((0 <= halfbyte) && (halfbyte <= 9)) {
                    buf.append((char) ('0' + halfbyte));
                } else {
                    buf.append((char) ('a' + (halfbyte - 10)));
                }
                halfbyte = data[i] & 0x0F;
            } while (two_halfs++ < 1);
        }
        return buf.toString();
    }
}
