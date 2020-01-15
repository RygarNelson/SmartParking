package com.example.pick_a_park;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.TextAppearanceSpan;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.internal.NavigationMenu;
import com.google.android.material.navigation.NavigationView;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        NavigationView nav;
        nav  = (NavigationView)findViewById(R.id.navigation);
        if (Parametri.profile_image != null) {
            byte[] decodedString = Base64.decode(Parametri.profile_image, Base64.DEFAULT);
            Bitmap foto = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            View header = nav.getHeaderView(0);
            ImageView profile;

            ///data/user/0/com.example.pick_a_park/app_imageDir
            Bitmap myBitmap = BitmapFactory.decodeFile(Parametri.path+"/profile.jpg");
            if (myBitmap == null) {
                profile = (ImageView) header.findViewById(R.id.img);
                profile.setImageBitmap(foto);
            }else{
                    profile = (ImageView) header.findViewById(R.id.img);
                    profile.setImageBitmap(myBitmap);
            }
        }
        nav.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                // Handle item selection
                switch (item.getItemId()) {
                    case R.id.home:
                        Toast.makeText(getApplicationContext(),"primo",Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.Profile:
                        Profile();
                        return true;
                    default:
                        Toast.makeText(getApplicationContext(),item.getItemId(),Toast.LENGTH_SHORT).show();
                }

                return true;
            }
        });
        // Find the toolbar view inside the activity layout
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        // Sets the Toolbar to act as the ActionBar for this Activity window.
        // Make sure the toolbar exists in the activity and is not null
        setSupportActionBar(toolbar);


        getSupportActionBar().setDisplayShowTitleEnabled(false);

        OpenFragment();
    }
    @Override
    public void onBackPressed() {
        Fragment myFragment =  getSupportFragmentManager().findFragmentByTag("HOME FRAGMENT");
        if (myFragment != null && myFragment.isVisible()) {
           ClearFragmentStack();
            finish();
        }else{
            ClearFragmentStack();
            OpenFragment();
        }
    }
    public void ClearFragmentStack()
    {
        FragmentManager fm = this.getSupportFragmentManager();
        for(int i = 0; i < fm.getBackStackEntryCount(); ++i) {
            fm.popBackStack();
        }
    }
    public void OpenFragment(){
        HomeFragment firstFragment = new HomeFragment();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, firstFragment,"HOME FRAGMENT").commit();

    }
    public void Profile(){
        ProfileFragment fragment = new ProfileFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,fragment).commit();
    }
    public void About(MenuItem item) {
        AboutFragment fragment = new AboutFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,fragment).commit();

    }
    // Menu icons are inflated just as they were with actionbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    public void ShowMenu(View view){

        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerLayout.openDrawer(GravityCompat.START);

    }
}
