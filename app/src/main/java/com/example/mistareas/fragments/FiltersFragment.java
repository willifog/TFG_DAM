package com.example.mistareas.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.example.mistareas.R;
import com.example.mistareas.activities.FiltersActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class FiltersFragment extends Fragment {

    View mView;

    CardView mCardViewDesayuno;
    CardView mCardViewComida;
    CardView mCardViewCena;

    public FiltersFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_filters, container,  false);

        mCardViewCena = mView.findViewById(R.id.cardViewCena);
        mCardViewComida = mView.findViewById(R.id.cardViewComida);
        mCardViewDesayuno = mView.findViewById(R.id.cardViewDesayuno);

        //Cena
       mCardViewCena.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               FiltroActividad("CENA");

           }
       });


       //comida

        mCardViewComida.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FiltroActividad( "COMIDA");

            }
        });


        //desayuno/postre
        mCardViewDesayuno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FiltroActividad( "DESAYUNO / POSTRE");

            }
        });
       return mView;


    }


    private void FiltroActividad(String category)
    {
        Intent intent = new Intent(getContext(), FiltersActivity.class);
        intent.putExtra( "category", category);
        startActivity(intent);
    }

}