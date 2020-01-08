package com.example.pick_a_park;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.TextAppearanceSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.internal.NavigationMenu;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        NavigationView nav;
        nav  = (NavigationView)findViewById(R.id.navigation);


        nav.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                // Handle item selection
                switch (item.getItemId()) {
                    case R.id.home:
                        Toast.makeText(getApplicationContext(),"primo",Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.nav_profile2:
                        Toast.makeText(getApplicationContext(),"secondo",Toast.LENGTH_SHORT).show();
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
