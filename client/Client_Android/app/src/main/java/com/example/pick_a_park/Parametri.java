package com.example.pick_a_park;

import android.location.Location;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Parametri {
    // Dati server e connessione
    //93.46.53.229 - 192.168.43.221
    public static String IP = "http://192.168.43.221:5700";
    static String UUIDPARKING = "00001101-0000-1000-8000-00805f9b34fb";
    public static String Token = null;
    public static File login_file;
    public static File advance_setting_file;

    // Dati account utente
    public static String path = "/data/user/0/com.example.pick_a_park/app_imageDir";
    public static String profile_image = null;
    public static String id = null;
    public static String username = null;
    public static String email = null;
    public static String password = null;
    public static String nome = null;
    public static String cognome = null;
    public static String data_nascita = null;
    public static String telefono = null;
    public static String saldo = null;
    public static Location lastKnowPosition = null;
    public static int TEMPO_AVVISO = 5 * 60 * 1000; // 5 minuti
    public static int TEMPO_EXTRA = 20 * 60 * 1000; // 20 minuti

    // Dati carta di credito
   public static ArrayList<Card> cards = null;

    // Parcheggi e prenotazioni
    /*

    static List<Prenotazione> prenotazioniInCorso = null;
    static List<PrenotazionePassata> prenotazioniVecchie = null;
    static List<PrenotazioneDaPagare> prenotazioniDaPagare = null;

    static public void resetAllParametri() {
        Token = null;
        parcheggi = null;
        parcheggi_vicini = null;
        prenotazioniInCorso = null;
        prenotazioniVecchie = null;
        prenotazioniDaPagare = null;
        lastKnowPosition = null;
        id = null;
        username = null;
        email = null;
        password = null;
        nome = null;
        cognome = null;
        data_nascita = null;
        telefono = null;
        saldo = null;
        numero_carta = null;
        data_di_scadenza = null;
        pin = null;
    } */
}
