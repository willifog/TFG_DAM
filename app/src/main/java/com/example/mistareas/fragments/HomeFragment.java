package com.example.mistareas.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.mistareas.R;
import com.example.mistareas.activities.LoginActivity;
import com.example.mistareas.activities.PostActivity;
import com.example.mistareas.adapters.*;
import com.example.mistareas.models.Post;
import com.example.mistareas.providers.AuthProvider;
import com.example.mistareas.providers.PostProvider;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.Query;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    View mView;
    FloatingActionButton mFab;

    AuthProvider mAuthProvider;

    RecyclerView mRecyclerView;
    PostProvider mPostProvider;
    PostsAdapter mPostsAdapter;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_home, container, false);

        setHasOptionsMenu(true);
        mAuthProvider = new AuthProvider();
        mPostProvider = new PostProvider();
        mRecyclerView = mView.findViewById(R.id.reciclerViewHome);


        //Nos mostrará las tarjetas una debajo de la otra
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(linearLayoutManager);


        mFab = mView.findViewById(R.id.fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToPost();
            }
        });
        return mView;
    }

    @Override
    public void onStart() {
        super.onStart();

        Query query = mPostProvider.getAll();       //obtenemos datos de firestore
        FirestoreRecyclerOptions<Post> options = new FirestoreRecyclerOptions
                .Builder<Post>().setQuery(query, Post.class)
                .build();
        mPostsAdapter = new PostsAdapter(options, getContext());
        mRecyclerView.setAdapter(mPostsAdapter);
        mPostsAdapter.startListening();  //Obtenemos datos en tiempo real.
    }

    //Cuando la app pasa a segundo plano
    @Override
    public void onStop() {
        super.onStop();
        mPostsAdapter.stopListening(); //Deja de escuchar los cambios desde la bbdd
    }

    private void goToPost(){
        Intent intent = new Intent(getContext(), PostActivity.class);
        startActivity(intent);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {

        inflater.inflate(R.menu.main_menu,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.itemLogout){    //Opcion seleccionada en el menu de AppBar
            logOut();
        }
        return super.onOptionsItemSelected(item);
    }

    //Cerramos sesion del usuarios
    private void logOut() {
        mAuthProvider.logOut();
        Intent intent = new Intent(getContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK); //Evitamos que se pueda volver borrando el historial de activitys
        startActivity(intent);

    }
}