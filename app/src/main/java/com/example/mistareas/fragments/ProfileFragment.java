package com.example.mistareas.fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.mistareas.R;
import com.example.mistareas.activities.EditProfileActivity;
import com.example.mistareas.adapters.MyPostsAdapter;
import com.example.mistareas.models.Post;
import com.example.mistareas.providers.AuthProvider;
import com.example.mistareas.providers.PostProvider;
import com.example.mistareas.providers.UsersProvider;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    TextView mTextViewUsername;
    TextView mTextViewPhone;
    TextView mTextViewEmail;
    TextView mTextViewPostNumber;
    CircleImageView mCircleImageProfile;

    LinearLayout mLiearLayoutEditProfile;
    View mView;

    UsersProvider mUsersProvider;
    AuthProvider mAuthProvider;
    PostProvider mPostProvider;
    MyPostsAdapter mAdapter;

    RecyclerView mRecyclerView;



    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_profile, container,false);

        mTextViewEmail = mView.findViewById(R.id.textViewCorreoUser);
        mTextViewUsername = mView.findViewById(R.id.textViewUserName);
        mTextViewPhone = mView.findViewById(R.id.textViewPhone);
        mTextViewPostNumber = mView.findViewById(R.id.textViewPostNumber);
        mCircleImageProfile = mView.findViewById(R.id.profile_image);


        mUsersProvider = new UsersProvider();
        mAuthProvider = new AuthProvider();
        mPostProvider = new PostProvider();

        mRecyclerView = mView.findViewById(R.id.reciclerViewMyPost);

        //Nos mostrará las tarjetas una debajo de la otra
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(linearLayoutManager);

        mLiearLayoutEditProfile = mView.findViewById(R.id.linearLayoutEditProfile);
        mLiearLayoutEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToEditProfile();
            }
        });

        getUser();  //Obtenemos el usuario
        getPostNumber();    //Obtenemos numero de post
        return mView;
    }

    @Override
    public void onStart() {
        super.onStart();
        getUser();

        Query query = mPostProvider.getPostByUser(mAuthProvider.getUid());  //Obtenemos las publicaciones del usuario
        FirestoreRecyclerOptions<Post> options = new FirestoreRecyclerOptions
                .Builder<Post>().setQuery(query, Post.class)
                .build();
        mAdapter = new MyPostsAdapter(options, getContext());
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.startListening();  //Obtenemos datos en tiempo real.
    }

    @Override
    public void onStop() {
        super.onStop();

        mAdapter.stopListening();
    }

    private void goToEditProfile() {
        Intent intent = new Intent(getContext(), EditProfileActivity.class);
        startActivity(intent);
    }

    //Se le pasa el id del usuario a buscar
    private void getPostNumber(){
        mPostProvider.getPostByUser(mAuthProvider.getUid()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                int numeroPost = queryDocumentSnapshots.size(); //Nos dice la cantidad de elementos que obtenemos de la consulta
                mTextViewPostNumber.setText(String.valueOf(numeroPost));
            }
        });
    }

    private void getUser(){
        mUsersProvider.getUser(mAuthProvider.getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
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
                            Picasso.with(getContext()).load(imageProfile).into(mCircleImageProfile);
                        }
                    }
                }
            }
        });
    }
}