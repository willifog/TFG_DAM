package com.example.mistareas.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;

import com.example.mistareas.R;
import com.example.mistareas.providers.PostProvider;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.squareup.picasso.Picasso;

public class PostDetailActivity extends AppCompatActivity {

    ImageView mImageView;
    PostProvider mPostProvider;
    String mExtraPostId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        mImageView = findViewById(R.id.imagePostDetail);
        mPostProvider = new PostProvider();
        mExtraPostId = getIntent().getStringExtra("id");

        getPost();
    }

    //Obtenemos la imagen desde el documento
    private void getPost(){
        mPostProvider.getPostById(mExtraPostId).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists() && documentSnapshot.contains("image1")){
                    String image = documentSnapshot.getString("image1");

                    Picasso.with(PostDetailActivity.this).load(image).into(mImageView);
                }

            }
        });
    }
}