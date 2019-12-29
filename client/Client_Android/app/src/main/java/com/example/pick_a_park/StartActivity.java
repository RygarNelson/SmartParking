package com.example.pick_a_park;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

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
                finish();
            }
        }, 4500);

    }
    private void RunAnimation()
    {
        //First line
        Animation a = AnimationUtils.loadAnimation(this, R.anim.start_animation);
        a.reset();
        ImageView car1 = (ImageView) findViewById(R.id.car1);
        car1.clearAnimation();
        car1.startAnimation(a);
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
        ImageView car2 = (ImageView) findViewById(R.id.car2);
        car2.clearAnimation();
        car2.startAnimation(a);
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
        ImageView car3 = (ImageView) findViewById(R.id.car3);
        car3.clearAnimation();
        car3.startAnimation(a);
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