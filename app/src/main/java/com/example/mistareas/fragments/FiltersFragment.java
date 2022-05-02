package com.example.mistareas.fragments;


import android.content.Intent;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.cardview.widget.CardView;
import com.example.mistareas.R;
import com.example.mistareas.activities.FiltersActivity;


/**
 * A simple {@link Fragment} subclass.
 */
public class FiltersFragment extends Fragment {


    View mView;

    CardView mCardViewChina;
    CardView mCardViewAmericana;
    CardView mCardViewMediterranea;


    public FiltersFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        mView = inflater.inflate(R.layout.fragment_filters, container,  false);

        mCardViewMediterranea = mView.findViewById(R.id.cardViewMediterranea);
        mCardViewChina = mView.findViewById(R.id.cardViewChina);
        mCardViewAmericana = mView.findViewById(R.id.cardViewAmericana);

        //Cena
        mCardViewMediterranea.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               FiltroActividad("MEDITERRANEA");

           }
       });


       //comida

        mCardViewAmericana.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FiltroActividad( "AMERICANA");

            }
        });


        //desayuno/postre
        mCardViewChina.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FiltroActividad( "CHINA");

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


    //    return inflater.inflate(R.layout.fragment_filters, container, false);
   // }

}