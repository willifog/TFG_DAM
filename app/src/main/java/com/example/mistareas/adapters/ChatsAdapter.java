package com.example.mistareas.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mistareas.R;
import com.example.mistareas.models.Chat;
import com.example.mistareas.providers.UsersProvider;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatsAdapter extends FirestoreRecyclerAdapter<Chat, ChatsAdapter.ViewHolder> {

    Context context;
    UsersProvider mUsersProvider;


    public ChatsAdapter(FirestoreRecyclerOptions<Chat> options, Context context){
        super(options);
        this.context = context;

        mUsersProvider = new UsersProvider();
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Chat chat) {

        DocumentSnapshot document = getSnapshots().getSnapshot(position);  //Obtenemos el documento con la info de la publicacion
        final String chatId = document.getId();

        getUserInfo(chatId, holder);
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
                        String imageProfile = documentSnapshot.getString("image_profile");
                        if(imageProfile != null && !imageProfile.isEmpty()){
                            Picasso.with(context).load(imageProfile).into(holder.circleImageChat);
                        }
                    }
                }
            }
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_chats, parent,false);
        return new ViewHolder(view);
    }



    public class ViewHolder extends RecyclerView.ViewHolder{

       TextView textViewUserName;
       TextView textViewLastMessage;
       CircleImageView circleImageChat;
       View viewHolder; //Variable para contener toda la informacion de la publicacion


       //Nos mandaran la CardView desde onCreateViewHolder
       public ViewHolder(View view){
           super(view);
           textViewUserName = view.findViewById(R.id.textViewUserNameChat);
           textViewLastMessage = view.findViewById(R.id.textViewLastMessageChat);
           circleImageChat = view.findViewById(R.id.circleImageChat);

           viewHolder = view;
       }
   }
}
