package com.example.pick_a_park.ui.login;

// Interfaccia per gestire eventi tra il Connessione e i fragment
public interface ConnessioneListener {
    void ResultResponse(String responseCode, String result);
}
