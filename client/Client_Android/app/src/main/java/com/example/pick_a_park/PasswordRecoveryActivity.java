package com.example.pick_a_park;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

public class PasswordRecoveryActivity extends AppCompatActivity  {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_recovery);
        PR_mail fragment = new PR_mail();
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, fragment,"MAIL RECOVERY FRAGMENT").commit();
    }

}
