package com.example.mistareas.providers;

import com.example.mistareas.models.Post;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;

public class PostProvider {

    CollectionReference mCollection;

    public PostProvider() {
        mCollection = FirebaseFirestore.getInstance().collection("Posts");
    }

    /**
     * Metodo que almacena un nuevo post a la coleccion de posts en firebase
     * @param post publcacion creada por el usuario
     * @return utiliza  la coleccion para añadir el post
     */
    public Task<Void> save(Post post){
        return mCollection.document().set(post);
    }

    /**
     * Metodo utilizado para obtener todos los post y ordenarlos por la fecha.
     * @return
     */
    public Query getAll(){
       return  mCollection.orderBy("timestamp",Query.Direction.DESCENDING);
    }


    //para obtener datos del filtro de búsqueda
    public Query getPostByCategoryAndTimestamp(String category){
        return  mCollection.whereEqualTo("category", category).orderBy("timestamp",Query.Direction.DESCENDING);
    }

    /**
     * Metodo que devuelve los post de un usuario
     * @param id ID del usuario que queremos obtener sus posts
     * @return retorna los post del usuario
     */
    public Query getPostByUser(String id){
        return mCollection.whereEqualTo("idUser", id);
    }

    /**
     * Metodo que borra el post pasado por parametro
     * @param id post que queremos eliminar
     * @return
     */
    public Task<Void> delete (String id){
        return mCollection.document(id).delete();
    }

    public Task<DocumentSnapshot> getPostById(String id){
        return mCollection.document(id).get();
    }
}
