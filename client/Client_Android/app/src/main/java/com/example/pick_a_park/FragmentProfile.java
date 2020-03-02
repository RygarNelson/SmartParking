package com.example.pick_a_park;


import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentProfile extends Fragment implements ConnessioneListener{
    private ProgressDialog caricamento = null;
    ArrayList<Prenotazione> prenotazioni;
    View view;
    public FragmentProfile() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_profile, container, false);

        File imgFile = new File("/data/user/0/com.example.pick_a_park/app_imageDir/profile"+Parametri.id+".jpg");
        try {
            if (imgFile.exists()) {

                Bitmap myBitmap = BitmapFactory.decodeFile(Parametri.path+"/profile"+Parametri.id+".jpg");

                ImageView myImage = (ImageView) view.findViewById(R.id.img);

                myImage.setImageBitmap(myBitmap);

            }
        }catch(Exception e){}
        TextView mail = view.findViewById(R.id.Mail);
        mail.setText(Parametri.email);
        TextView nome = view.findViewById(R.id.Name);
        nome.setText(Parametri.nome + " " + Parametri.cognome);
        TextView data = view.findViewById(R.id.Date);
        data.setText(Parametri.data_nascita);
        GetPrenotations();



        // Inflate the layout for this fragment
        return view;

    }
    //Chiedo La lista dei parcheggi al server
    private void GetPrenotations() {
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
                CreaUI();


            }catch (Exception e){
                String message = "Response Error.";
                caricamento.dismiss();
                Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
                return;
            }

        }



    }
private void CreaUI(){
    CardView cardView1 = view.findViewById(R.id.card1);
    CardView cardView2 = view.findViewById(R.id.card2);
    CardView cardView3 = view.findViewById(R.id.card3);
    TextView text1 = view.findViewById(R.id.info_text);
    TextView text2 = view.findViewById(R.id.info_text2);
    TextView text3 = view.findViewById(R.id.info_text3);
    if (prenotazioni.isEmpty()){
        cardView1.setVisibility(View.GONE);
        cardView2.setVisibility(View.GONE);
        cardView3.setVisibility(View.GONE);
    }
    if (prenotazioni.size()==1){
        if (prenotazioni.get(0).cash == 0.0 && prenotazioni.get(0).Data.isEmpty())
            text1.setText("ID: "+prenotazioni.get(0).id+"\n"+"Data Arrivo: N/A"+"\n"+"Prezzo: N/A"+"\nStato: "
                    +state.values()[prenotazioni.get(0).stato]);
        else
            text1.setText("ID: "+prenotazioni.get(0).id+"\nData Arrivo: "+prenotazioni.get(0).Data+"\nPrezzo: "+prenotazioni.get(0).cash+"\nStato: "
                    +state.values()[prenotazioni.get(0).stato]);
        cardView2.setVisibility(View.GONE);
        cardView3.setVisibility(View.GONE);
    }
    if (prenotazioni.size()==2){
        if (prenotazioni.get(0).cash == 0.0 && prenotazioni.get(0).Data.isEmpty())
            text1.setText("ID: "+prenotazioni.get(1).id+"\n"+"Data Arrivo: N/A"+"\n"+"Prezzo: N/A"+"\nStato: "
                    +state.values()[prenotazioni.get(1).stato]);
        else
            text1.setText("ID: "+prenotazioni.get(1).id+"\nData Arrivo: "+prenotazioni.get(1).Data+"\nPrezzo: "+prenotazioni.get(1).cash+"\nStato: "
                    +state.values()[prenotazioni.get(1).stato]);
        if (prenotazioni.get(1).cash == 0.0 && prenotazioni.get(1).Data.isEmpty())
            text2.setText("ID: "+prenotazioni.get(1).id+"\n"+"Data Arrivo: N/A"+"\n"+"Prezzo: N/A"+"\nStato: "
                    +state.values()[prenotazioni.get(1).stato]);
        else
            text2.setText("ID: "+prenotazioni.get(1).id+"\nData Arrivo: "+prenotazioni.get(1).Data+"\nPrezzo: "+prenotazioni.get(1).cash+"\nStato: "
                    +state.values()[prenotazioni.get(1).stato]);
        cardView3.setVisibility(View.GONE);
    }
    if (prenotazioni.size()>=3){
        if (prenotazioni.get(0).cash == 0.0 && prenotazioni.get(0).Data.isEmpty())
            text1.setText("ID: "+prenotazioni.get(1).id+"\n"+"Data Arrivo: N/A"+"\n"+"Prezzo: N/A"+"\nStato: "
                    +state.values()[prenotazioni.get(1).stato]);
        else
            text1.setText("ID: "+prenotazioni.get(1).id+"\nData Arrivo: "+prenotazioni.get(1).Data+"\nPrezzo: "+prenotazioni.get(1).cash+"\nStato: "
                    +state.values()[prenotazioni.get(1).stato]);
        if (prenotazioni.get(1).cash == 0.0 && prenotazioni.get(1).Data.isEmpty())
            text2.setText("ID: "+prenotazioni.get(1).id+"\n"+"Data Arrivo: N/A"+"\n"+"Prezzo: N/A"+"\nStato: "
                    +state.values()[prenotazioni.get(1).stato]);
        else
            text2.setText("ID: "+prenotazioni.get(1).id+"\nData Arrivo: "+prenotazioni.get(1).Data+"\nPrezzo: "+prenotazioni.get(1).cash+"\nStato: "
                    +state.values()[prenotazioni.get(1).stato]);
        if (prenotazioni.get(2).cash == 0.0 && prenotazioni.get(2).Data.isEmpty())
            text3.setText("ID: "+prenotazioni.get(2).id+"\n"+"Data Arrivo: N/A"+"\n"+"Prezzo: N/A"+"\nStato: "
                    +state.values()[prenotazioni.get(2).stato]);
        else
            text3.setText("ID: "+prenotazioni.get(2).id+"\nData Arrivo: "+prenotazioni.get(2).Data+"\nPrezzo: "+prenotazioni.get(2).cash+"\nStato: "
                    +state.values()[prenotazioni.get(2).stato]);
    }
}
}

