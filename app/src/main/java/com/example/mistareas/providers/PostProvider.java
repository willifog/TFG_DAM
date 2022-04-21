package com.example.mistareas.providers;

import com.example.mistareas.models.Post;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;

public class PostProvider {

    CollectionReference mCollection;

    public PostProvider() {
        mCollection = FirebaseFirestore.getInstance().collection("Posts"); //Creamos una nueva coleccion en FireStore
    }

    public Task<Void> save(Post post){  //Le pasamos la publicacion que vamos a guardar en "POSTS"
        return mCollection.document().set(post);   //Lo ponemos vacio para que nos cree un nuevo documento unico para cada post.
    }

    //Para obtener todos los post
    public Query getAll(){
       return  mCollection.orderBy("title",Query.Direction.DESCENDING);
    }
}
