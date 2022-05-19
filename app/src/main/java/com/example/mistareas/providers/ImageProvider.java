package com.example.mistareas.providers;

import android.content.Context;

import com.example.mistareas.utils.CompressorBitmapImage;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.Date;

public class ImageProvider {

    StorageReference mStorage;

    public ImageProvider(){
        mStorage = FirebaseStorage.getInstance().getReference(); //Hacemos referencia la modulo de storage en firebase
    }

    /**
     * Metodo al que se le pasa el contexto y la imagen que queremos almacenar
     *  se establece un tamaño reducido para ahorrar espacio de almacenamiento.
     *  se crea una instancia en firabase y una vez creada  se actualiza con la
     *  nueva imagen comprimida.
     *
     * @param context contexto en el que se encuentra la imagen
     * @param file fichero/imagen  que queremos almacenar.
     *
     * @return retorna una tarea que será controlada desde la llamada.
     */
    public UploadTask save(Context context, File file){
        byte[] imageByte = CompressorBitmapImage.getImage(context,file.getPath(),500,500);

        StorageReference storage = FirebaseStorage.getInstance().getReference().child(new Date() + ".jpg");
        mStorage = storage;
        UploadTask task = storage.putBytes(imageByte);
        return task;
    }

    //Nos devuelve el objeto mStorage
    public StorageReference getStorage(){
        return mStorage; //Nos permitirá obtener la url de la imagen
    }
}
