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
import com.example.mistareas.models.Post;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.squareup.picasso.Picasso;

public class PostsAdapter extends FirestoreRecyclerAdapter<Post,PostsAdapter.ViewHolder> {

    TextView mTextViewNumberFilter;
    Context context;
    public PostsAdapter(FirestoreRecyclerOptions<Post> options, Context context){
        super(options);
        this.context = context;
    }


    public PostsAdapter(FirestoreRecyclerOptions<Post> options, Context context, TextView textView){
        super(options);
        this.context = context;
        mTextViewNumberFilter = textView;
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Post post) {

        DocumentSnapshot document = getSnapshots().getSnapshot(position);  //Obtenemos el documento con la info de la publicacion
        String postId = document.getId();

        if (mTextViewNumberFilter !=null)
        {
            int numberFilter = getSnapshots().size();
            mTextViewNumberFilter.setText(String.valueOf(numberFilter));
        }

        //Para establecer el contenido que se muestra en cada una de estas Views
        holder.textViewTitle.setText(post.getTitle());
        holder.textViewDescription.setText(post.getDescription());
        if(post.getImage1() != null){
            if(!post.getImage1().isEmpty()){
                Picasso.with(context).load(post.getImage1()).into(holder.imageViewPost);
            }
        }

        //Añadimos evento para pulsar en cualquier publicacion
        holder.viewHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, PostDetailActivity.class);
                intent.putExtra("id", postId);
                context.startActivity(intent);  //Pasamos a la siguiente activity con el contexto
            }
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_post, parent,false);
        return new ViewHolder(view);
    }



    public class ViewHolder extends RecyclerView.ViewHolder{

       TextView textViewTitle;
       TextView textViewDescription;
       ImageView imageViewPost;

       View viewHolder; //Variable para contener toda la informacion de la publicacion



       //Nos mandaran la CardView desde onCreateViewHolder
       public ViewHolder(View view){
           super(view);
           textViewTitle = view.findViewById(R.id.textViewTitlePostCard);
           textViewDescription = view.findViewById(R.id.textViewDescriptionPostCard);
           imageViewPost = view.findViewById(R.id.imageViewPostCard);

           viewHolder = view;
       }
   }
}
