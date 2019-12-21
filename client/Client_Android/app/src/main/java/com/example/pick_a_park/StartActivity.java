package com.example.pick_a_park;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.pick_a_park.ui.login.LoginActivity;

public class StartActivity extends AppCompatActivity {



    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        RunAnimation();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                Intent i = new Intent(StartActivity.this, LoginActivity.class);
                startActivity(i);
            }
        }, 4500);

    }
    private void RunAnimation()
    {
        //First line
        Animation a = AnimationUtils.loadAnimation(this, R.anim.start_animation);
        a.reset();
        TextView tv = (TextView) findViewById(R.id.first_text);
        tv.clearAnimation();
        tv.startAnimation(a);
        a.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                TextView tv = (TextView) findViewById(R.id.first_text);
                tv.setShadowLayer(30, 0, 0, Color.WHITE);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        TextView tv = (TextView) findViewById(R.id.first_text);
                        tv.setShadowLayer(30, 0, 0, Color.TRANSPARENT);
                    }
                }, 750);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        //second line
        a = AnimationUtils.loadAnimation(this, R.anim.start_animation);
        a.reset();
        tv = (TextView) findViewById(R.id.second_text);
        tv.clearAnimation();
        tv.startAnimation(a);
        a.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        TextView tv = (TextView) findViewById(R.id.second_text);
                        tv.setShadowLayer(30, 0, 0, Color.WHITE);
                    }
                }, 750);

                handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        TextView tv = (TextView) findViewById(R.id.second_text);
                        tv.setShadowLayer(30, 0, 0, Color.TRANSPARENT);
                    }
                }, 1500);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        //third line
        a = AnimationUtils.loadAnimation(this, R.anim.start_animation);
        a.reset();
        tv = (TextView) findViewById(R.id.third_text);
        tv.clearAnimation();
        tv.startAnimation(a);
        a.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        TextView tv = (TextView) findViewById(R.id.third_text);
                        tv.setShadowLayer(30, 0, 0, Color.WHITE);
                    }
                }, 1500);

                handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        TextView tv = (TextView) findViewById(R.id.third_text);
                        tv.setShadowLayer(30, 0, 0, Color.TRANSPARENT);

                    }
                }, 2250);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }
}