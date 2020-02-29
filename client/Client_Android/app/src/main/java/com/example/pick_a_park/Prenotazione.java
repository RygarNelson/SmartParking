package com.example.pick_a_park;

public class Prenotazione {
    int id;

    String Data;
    Double cash;
    int stato;
    Prenotazione(int id, String date, Double cash, int state)
    {
        this.id = id;
        this.Data = date;
        this.cash = cash;
        this.stato = state;
    }
}

