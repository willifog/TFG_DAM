package com.example.mistareas.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mistareas.R;
import com.example.mistareas.activities.ChatActivity;
import com.example.mistareas.models.Message;
import com.example.mistareas.providers.AuthProvider;
import com.example.mistareas.providers.UsersProvider;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends FirestoreRecyclerAdapter<Message, MessageAdapter.ViewHolder> {

    Context context;
    UsersProvider mUsersProvider;
    AuthProvider mAuthProvider;


    public MessageAdapter(FirestoreRecyclerOptions<Message> options, Context context){
        super(options);
        this.context = context;

        mUsersProvider = new UsersProvider();
        mAuthProvider = new AuthProvider();
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Message message) {

        DocumentSnapshot document = getSnapshots().getSnapshot(position);  //Obtenemos el documento con la info de la publicacion
        final String messageId = document.getId();
       holder.textViewMessage.setText(message.getMessage());

        if(message.getIdSender().equals(mAuthProvider.getUid())){
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
            );
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            params.setMargins(150, 0, 0, 0);
            holder.linearLayoutMessage.setLayoutParams(params);
            holder.linearLayoutMessage.setPadding(30, 20, 0, 20);
            holder.linearLayoutMessage.setBackground(context.getResources().getDrawable(R.drawable.rounded_linear_layout));
            holder.imageViewViewed.setVisibility(View.VISIBLE);
        }
        else{
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
            );
            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            params.setMargins(0, 0, 150, 0);
            holder.linearLayoutMessage.setLayoutParams(params);
            holder.linearLayoutMessage.setPadding(30, 20, -40, 20);
            holder.linearLayoutMessage.setBackground(context.getResources().getDrawable(R.drawable.rounded_linear_layout_2));
            holder.imageViewViewed.setVisibility(View.INVISIBLE);
        }

        if(message.isViewed()){
            holder.imageViewViewed.setImageResource(R.drawable.icon_check_blue);
        }else{
            holder.imageViewViewed.setImageResource(R.drawable.icon_check_grey);
        }

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_message, parent,false);
        return new ViewHolder(view);
    }



    public class ViewHolder extends RecyclerView.ViewHolder{

       TextView textViewMessage;
       TextView textViewDate;
       ImageView imageViewViewed;
       LinearLayout linearLayoutMessage;
       View viewHolder; //Variable para contener toda la informacion de la publicacion


       //Nos mandaran la CardView desde onCreateViewHolder
       public ViewHolder(View view){
           super(view);
           textViewMessage = view.findViewById(R.id.textViewMessage);
           imageViewViewed = view.findViewById(R.id.imageViewViewedMessage);
           linearLayoutMessage = view.findViewById(R.id.linearLayoutMessage);

           viewHolder = view;
       }
   }
}
