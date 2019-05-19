package com.baiz.myfavoriteplaces;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import java.io.Serializable;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private Toolbar toolbar ;
    private DrawerLayout drawer ;
    public String baiz ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
         toolbar = (Toolbar) findViewById(R.id.toolbar);
         setSupportActionBar(toolbar);

         drawer = findViewById(R.id.draw_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawer,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        Bundle bundle = new Bundle();
        bundle.putString("from","menu");
        MapFragment mapFragment = new MapFragment();
        mapFragment.setArguments(bundle);
        FragmentManager fragmentManager= getSupportFragmentManager();
        FragmentTransaction ft=fragmentManager.beginTransaction();
        ft.replace(R.id.fragment_container,mapFragment);
        ft.commit();


    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START))
            drawer.closeDrawer(GravityCompat.START);
        else
            super.onBackPressed();
    }

    public void click(View view)
    {

        try {
            Intent intent = new Intent(this,AddNewPlace.class);
            startActivity(intent);
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId())
        {
            case R.id.nav_places :
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container ,new AllPlaces()).commit();
                break;
            case R.id.nav_add :
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container ,new AddNewPlace()).commit();
                break;
            case R.id.map_google :

                Bundle bundle = new Bundle();
                bundle.putString("from","menu");
                MapFragment mapFragment = new MapFragment();
                mapFragment.setArguments(bundle);
                FragmentManager fragmentManager= getSupportFragmentManager();
                FragmentTransaction ft=fragmentManager.beginTransaction();
                ft.replace(R.id.fragment_container,mapFragment);
                ft.commit();
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
