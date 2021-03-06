package com.example.mistareas.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.content.Intent;

import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.mistareas.R;

import com.example.mistareas.adapters.MyPostsAdapter;
import com.example.mistareas.models.Post;


import com.example.mistareas.providers.AuthProvider;
import com.example.mistareas.providers.PostProvider;
import com.example.mistareas.providers.UsersProvider;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfileActivity extends AppCompatActivity {

    TextView mTextViewUsername;
    TextView mTextViewPhone;
    TextView mTextViewEmail;
    TextView mTextViewPostNumber;
    CircleImageView mCircleImageProfile;
    CircleImageView mCircleImageViewBack; //Boton retroceso
    LinearLayout mLinearLayoutEditProfile;

    UsersProvider mUsersProvider;
    AuthProvider mAuthProvider;
    PostProvider mPostProvider;

    String mExtraIdUser;

    MyPostsAdapter mAdapter;
    RecyclerView mRecyclerView;

    FloatingActionButton mFabChat;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        mTextViewEmail = findViewById(R.id.textViewCorreoUser);
        mTextViewUsername = findViewById(R.id.textViewUserName);
        mTextViewPhone = findViewById(R.id.textViewPhone);
        mTextViewPostNumber = findViewById(R.id.textViewPostNumber);
        mCircleImageProfile = findViewById(R.id.profile_image);

        //Nos mostrar?? las tarjetas(posts) una debajo de la otra
        mRecyclerView = findViewById(R.id.reciclerViewMyPost);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(UserProfileActivity.this);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        mLinearLayoutEditProfile = findViewById(R.id.linearLayoutEditProfile);

        //Metodo el cual nos permite volver atras
        mCircleImageViewBack = findViewById(R.id.circleImageBack);
        mCircleImageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        mUsersProvider = new UsersProvider();
        mAuthProvider = new AuthProvider();
        mPostProvider = new PostProvider();

        //Id del usuario del perfil que accedemos
        mExtraIdUser = getIntent().getStringExtra("idUser");

        mFabChat = findViewById(R.id.fabChat);

        if (mAuthProvider.getUid().equals(mExtraIdUser)){
            mFabChat.setVisibility(View.GONE);
        }

        //Boton para iniciar conversacion
        mFabChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToChatActivity();
            }
        });

        getUser();
        getPostNumber();
    }

    /**
     * Metodo que  incia una nueva activity pasandole los id de los usuarios que comenzaran la conversacion.
     *
     */
    private void goToChatActivity() {
        Intent intent = new Intent(UserProfileActivity.this, ChatActivity.class);
        intent.putExtra("idUser1", mAuthProvider.getUid());  //ID del usuario que comienza la conversacion.
        intent.putExtra("idUser2", mExtraIdUser); // ID del usuario del perfil
        startActivity(intent);
    }

    /**
     * Metodo para obtener numero de publicaciones del usuario
     *
     * utilizando el id de usuario pasado al getExtra
     */
    private void getPostNumber(){
        mPostProvider.getPostByUser(mExtraIdUser).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                int numeroPost = queryDocumentSnapshots.size(); //Nos dice la cantidad de elementos que obtenemos de la consulta
                mTextViewPostNumber.setText(String.valueOf(numeroPost));
            }
        });
    }

    /**
     * Metodo para obtener las publicaciones del usuario
     *
     */
    @Override
    public void onStart() {
        super.onStart();
        getUser();

        Query query = mPostProvider.getPostByUser(mExtraIdUser);  //Obtenemos las publicaciones del usuario
        FirestoreRecyclerOptions<Post> options = new FirestoreRecyclerOptions
                .Builder<Post>().setQuery(query, Post.class)
                .build();
        mAdapter = new MyPostsAdapter(options, UserProfileActivity.this);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.startListening();  //Obtenemos datos en tiempo real.
    }

    @Override
    public void onStop() {
        super.onStop();

        mAdapter.stopListening();
    }

    /**
     * Metodo con el que obtenemos los datos del perfil de usuario que se va a visitar.
     *
     * Nos apoyamos en UsersProvider y le pasamos el id del usuario al que queremos realizar la consulta.
     *
     * Las peticiones a la bbdd se hacen mediante la clase DocumentSnapshot la cual tiene varios
     * metodos para hacer las consultas.
     *
     * .contains() utilizado para buscar el campo de cada documento
     * .getString() obtenemos el contenido de el campo indicado
     */
    private void getUser(){
        mUsersProvider.getUser(mExtraIdUser).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    if(documentSnapshot.contains("email")){
                        String email = documentSnapshot.getString("email");
                        mTextViewEmail.setText(email);
                    }
                    if(documentSnapshot.contains("phone")){
                        String phone = documentSnapshot.getString("phone");
                        mTextViewPhone.setText(phone);
                    }
                    if(documentSnapshot.contains("username")){
                        String username = documentSnapshot.getString("username");
                        mTextViewUsername.setText(username);
                    }
                    if(documentSnapshot.contains("image_profile")){
                        String imageProfile = documentSnapshot.getString("image_profile");
                        if(imageProfile != null && (!imageProfile.isEmpty())){
                            Picasso.with(UserProfileActivity.this).load(imageProfile).into(mCircleImageProfile);
                        }
                    }
                }
            }
        });
    }
}