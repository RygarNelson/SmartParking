package com.example.pick_a_park;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class PasswordRecoveryActivity extends AppCompatActivity  {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_recovery);
        Fragment_PR_mail fragment = new Fragment_PR_mail();
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, fragment,"MAIL RECOVERY FRAGMENT").commit();
    }

}
