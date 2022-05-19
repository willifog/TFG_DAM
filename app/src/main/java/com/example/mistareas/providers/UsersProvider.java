package com.example.mistareas.providers;

import com.example.mistareas.models.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class UsersProvider {

    private CollectionReference mCollection;

    public UsersProvider(){
        mCollection = FirebaseFirestore.getInstance().collection("Users");
    }


    /**
     *Metodo utilizado para la consulta de los datos del usuario
     *
     * @param id id del usuario que queremos consultar
     * @return retorna todos los datos del usuario.
     */
    public Task<DocumentSnapshot> getUser(String id){
        return mCollection.document(id).get();
    }

    /**
     * Metodo utilizado para añadir los datos de un usuario en la bbdd
     *
     * @param user objeto que contiene los datos del usuario
     * @return retorna una tarea  que se controla desde el metodo llamado
     */
    public Task<Void> create(User user){
        return mCollection.document(user.getId()).set(user);
    }

    /**
     * Metodo que permite enviar datos a firebase para ser actualizado.
     * Se utiliza un hasmap para indicar los datos que hay que actualizar en el fichero de firestore.
     *
     * @param user usuario que se va actualizar
     * @return mediante el id del usuario pasado por parametro hacemos la actualizacion de los datos.
     */
    public Task<Void> update(User user){
        Map<String,Object> map = new HashMap<>();
        map.put("username", user.getUsername());
        map.put("phone", user.getPhone());
        map.put("timestamp", new Date().getTime());
        map.put("image_profile", user.getImageProfile());

        return  mCollection.document(user.getId()).update(map);
    }
}
