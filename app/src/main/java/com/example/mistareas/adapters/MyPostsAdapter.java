package com.example.mistareas.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mistareas.R;
import com.example.mistareas.models.Post;
import com.example.mistareas.providers.PostProvider;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyPostsAdapter extends FirestoreRecyclerAdapter<Post,MyPostsAdapter.ViewHolder> {

    Context context;
    PostProvider mPostProvider;



    public MyPostsAdapter(FirestoreRecyclerOptions<Post> options, Context context){
        super(options);
        this.context = context;
        mPostProvider = new PostProvider();
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Post post) {

        DocumentSnapshot document = getSnapshots().getSnapshot(position);
        final String postId = document.getId();

        //Para establecer el contenido que se muestra en cada una de estas Views
        holder.textViewTitle.setText(post.getTitle());
        if(post.getImage1() != null){
            if(!post.getImage1().isEmpty()){
                Picasso.with(context).load(post.getImage1()).into(holder.circleImagePost);
            }
        }

        holder.imageViewDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deletePost(postId);
           }
        });
    }


    private void deletePost(String postId){
        mPostProvider.delete(postId).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(context, "El post se elimino correctamente", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(context, "No se pudo eliminar el Post", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_my_post, parent,false);
        return new ViewHolder(view);
    }



    public class ViewHolder extends RecyclerView.ViewHolder{

       TextView textViewTitle;
       CircleImageView circleImagePost;

       ImageView imageViewDelete;

       //Nos mandaran la CardView desde onCreateViewHolder
       public ViewHolder(View view){
           super(view);
           textViewTitle = view.findViewById(R.id.textViewTitleMyPost);
           circleImagePost = view.findViewById(R.id.circleImageMyPost);
           imageViewDelete = view.findViewById(R.id.imageViewDeleteMyPost);
       }
   }
}
