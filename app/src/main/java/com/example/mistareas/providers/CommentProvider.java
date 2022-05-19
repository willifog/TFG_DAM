package com.example.mistareas.providers;

import com.example.mistareas.models.Comment;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class CommentProvider {

    CollectionReference mCollection;

    public CommentProvider() {
        mCollection = FirebaseFirestore.getInstance().collection("Comments");
    }

    /**
     * Metodo utilizado para crear un documento con un Id Unico para los comentarios dentro de la bbdd
     *
     * @param comment comentario introducido por el user
     * @return  documento con toda la info del comentario
     */
    public Task<Void> create(Comment comment){
        return mCollection.document().set(comment);
    }

    /**
     * Metodo que hace una consulta a firebase con el id pasado por parametro
     * @param id id del comentario a buscar
     * @return
     */
    public Query getCommentByPost(String id){
        return mCollection.whereEqualTo("idPost", id);
    }
}
