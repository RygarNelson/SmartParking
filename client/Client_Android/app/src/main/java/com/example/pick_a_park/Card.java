package com.example.pick_a_park;

public class Card {
    int code;
    String expire;
    int type;
    String name=null;

    Card(int code, int type, String expire, String name){
        this.code = code;
        this.type = type;
        this.expire = expire;
        this.name = name;
    }
}
