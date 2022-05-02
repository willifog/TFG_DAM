package com.example.mistareas.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mistareas.R;
import com.example.mistareas.adapters.PostsAdapter;
import com.example.mistareas.models.Post;
import com.example.mistareas.providers.AuthProvider;
import com.example.mistareas.providers.PostProvider;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;


public class FiltersActivity extends AppCompatActivity {


    String mExtraCategory;

    AuthProvider mAuthProvider;

    RecyclerView mRecyclerView;
    PostProvider mPostProvider;
    PostsAdapter mPostsAdapter;

    TextView mTextViewNumberFilter;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filters);
        mRecyclerView = findViewById(R.id.recyclerViewFilter);
        mTextViewNumberFilter = findViewById(R.id.textViewnumerFilter);




        //Nos mostrará las tarjetas una debajo de la otra
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(FiltersActivity.this);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mExtraCategory = getIntent().getStringExtra("category");

        mAuthProvider = new AuthProvider();
        mPostProvider = new PostProvider();


        Toast.makeText(this, "la categoria que ha seleccionado es: " + mExtraCategory, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStart() {
        super.onStart();

        Query query = mPostProvider.getPostByCategoryAndTimestamp(mExtraCategory);       //obtenemos datos de firestore
        FirestoreRecyclerOptions<Post> options = new FirestoreRecyclerOptions
                .Builder<Post>().setQuery(query, Post.class)
                .build();
        mPostsAdapter = new PostsAdapter(options, FiltersActivity.this,mTextViewNumberFilter);
        mRecyclerView.setAdapter(mPostsAdapter);
        mPostsAdapter.startListening();  //Obtenemos datos en tiempo real.
    }
}