package com.example.mistareas.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mistareas.R;
import com.example.mistareas.activities.PostDetailActivity;
import com.example.mistareas.models.Comment;
import com.example.mistareas.providers.UsersProvider;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.squareup.picasso.Picasso;

public class CommentAdapter extends FirestoreRecyclerAdapter<Comment, CommentAdapter.ViewHolder> {

    Context context;
    UsersProvider mUsersProvider;


    public CommentAdapter(FirestoreRecyclerOptions<Comment> options, Context context){
        super(options);
        this.context = context;

        mUsersProvider = new UsersProvider();
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Comment comment) {

        DocumentSnapshot document = getSnapshots().getSnapshot(position);  //Obtenemos el documento con la info de la publicacion
        String commentId = document.getId();
        String idUser = document.getString("idUser");

        holder.textViewComment.setText(comment.getComment());

        getUserInfo(idUser, holder);
    }

    private void getUserInfo(String idUser, ViewHolder holder){
        mUsersProvider.getUser(idUser).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                if(documentSnapshot.exists()){
                    if(documentSnapshot.contains("username")){
                        String username = documentSnapshot.getString("username");
                        holder.textViewUserName.setText(username);
                    }
                    if(documentSnapshot.contains("image_profile")){
                        String imagePofile = documentSnapshot.getString("image_profile");
                        if(imagePofile != null && !imagePofile.isEmpty()){
                            Picasso.with(context).load(imagePofile).into(holder.imageViewComment);
                        }
                    }
                }
            }
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_comment, parent,false);
        return new ViewHolder(view);
    }



    public class ViewHolder extends RecyclerView.ViewHolder{

       TextView textViewUserName;
       TextView textViewComment;
       ImageView imageViewComment;
       View viewHolder; //Variable para contener toda la informacion de la publicacion


       //Nos mandaran la CardView desde onCreateViewHolder
       public ViewHolder(View view){
           super(view);
           textViewUserName = view.findViewById(R.id.textViewUserName);
           textViewComment = view.findViewById(R.id.textViewComment);
           imageViewComment = view.findViewById(R.id.circleImageComment);

           viewHolder = view;
       }
   }
}
