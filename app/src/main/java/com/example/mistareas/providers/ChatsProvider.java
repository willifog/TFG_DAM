package com.example.mistareas.providers;

import com.example.mistareas.models.Chat;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;

public class ChatsProvider {

    CollectionReference mCollection;

    public ChatsProvider(){
        mCollection = FirebaseFirestore.getInstance().collection("Chats");

    }

    public void create(Chat chat){
       mCollection.document(chat.getIdUser1() + chat.getIdUser2()).set(chat);
    }

    public Query getAll(String idUser){

        return mCollection.whereArrayContains("arrayIds", idUser);
    }

    public Query getChatbyUser1AndUser2(String idUser1, String idUser2){
        ArrayList<String> arrayIds = new ArrayList<>();
        arrayIds.add(idUser1 + idUser2);
        arrayIds.add(idUser2 + idUser1);
        return mCollection.whereIn("id", arrayIds);
    }
}
