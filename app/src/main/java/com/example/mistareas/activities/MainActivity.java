package com.example.mistareas.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mistareas.R;
import com.example.mistareas.fragments.ChatsFragment;
import com.example.mistareas.fragments.FiltersFragment;
import com.example.mistareas.fragments.HomeFragment;
import com.example.mistareas.fragments.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;


public class MainActivity extends AppCompatActivity{

    BottomNavigationView bottomNavigation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        openFragment(new HomeFragment()); //Indicamos la vista que con la que se inicia el main

        bottomNavigation = findViewById(R.id.bottom_navigation);


        bottomNavigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.itemHome:
                        openFragment(new HomeFragment());
                        return true;
                    case R.id.itemChats:
                        openFragment(new ChatsFragment());
                        return true;
                    case R.id.itemFilters:
                        openFragment(new FiltersFragment());
                        return true;
                    case R.id.itemProfile:
                        openFragment(new ProfileFragment());
                        return true;
                }
             return false;
            }
        });

    }

    public void openFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }




}