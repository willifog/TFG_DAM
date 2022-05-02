package com.example.mistareas.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mistareas.R;
import com.example.mistareas.models.Post;
import com.example.mistareas.providers.AuthProvider;
import com.example.mistareas.providers.ImageProvider;
import com.example.mistareas.providers.PostProvider;
import com.example.mistareas.utils.FileUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.storage.UploadTask;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import dmax.dialog.SpotsDialog;


public class PostActivity extends AppCompatActivity {

    ImageView mImageViewPost1;


    private final int GALLERY_REQUEST_CODE = 1;
    private final int PHOTO_REQUEST_CODE = 3;

    File mImageFile;


    Button mButtonPost;
    ImageProvider mImageProvider;

    TextInputEditText mTextInputTitle;
    TextInputEditText mTextInputDescription;
    TextInputEditText mTextInputDificultad;

    TextView mImageViewComida;
    TextView mImageViewCena;
    TextView mImageViewDesayuno;

    TextView mTextViewCategory;

    String mCategory = "";
    String mTitle = "";
    String mDescription = "";

    PostProvider mPostProvider;
    AuthProvider mAutProvider;

    AlertDialog mDialog;

    //Variables necesarias para mostrar opciones al subir imagen
    AlertDialog.Builder mBuilderSelector;  //Lo utilizaremos para dar la opcion de hacer una foto cn la camara
    CharSequence options[];

    //Foto1
    String mAbsolutePath;
    String mPhotoPath;
    File mPhotoFile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        mImageProvider =  new ImageProvider();
        mPostProvider = new PostProvider();
        mAutProvider = new AuthProvider();

        mTextInputTitle = findViewById(R.id.nombreReceta);
        mTextInputDescription = findViewById(R.id.descripcionReceta);
        mTextViewCategory = findViewById(R.id.textViewCategory);

        mBuilderSelector = new AlertDialog.Builder(this);
        mBuilderSelector.setTitle("Selecciona una opcion");

        options = new CharSequence[]{"Imagen de galeria","Tomar foto"};

        mDialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Espere un momento")
                .setCancelable(false).build();


        mImageViewComida = findViewById(R.id.imageViewComida);
        mImageViewComida.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCategory = "COMIDA";
                mTextViewCategory.setText(mCategory);
            }
        });

        mImageViewCena = findViewById(R.id.imageViewCena);
        mImageViewCena.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCategory = "CENA";

                mTextViewCategory.setText(mCategory);
            }
        });


        mImageViewDesayuno = findViewById(R.id.imageViewDesayuno);
        mImageViewDesayuno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCategory = "DESAYUNO / POSTRE";

                mTextViewCategory.setText(mCategory);
            }
        });

        //Boton publicar
        mButtonPost = findViewById(R.id.btnPublicar);
        mButtonPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickPost();
            }
        });

        //Establecer Imagen
        mImageViewPost1 = findViewById(R.id.imageViewPost1);
        mImageViewPost1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                selectOptionImage(1);
            }
        });

    }


    private void selectOptionImage(int numberImage){

        mBuilderSelector.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                if(i == 0){ //Imagen de la galeria

                    if(numberImage == 1){
                        openGallery(GALLERY_REQUEST_CODE);
                    }
                }else if(i == 1){    //Imagen de la Camara

                    if(numberImage == 1){
                        takePhoto(PHOTO_REQUEST_CODE);
                    }
                }
            }
        });
        mBuilderSelector.show();

    }
    
    private void takePhoto(int requestCode){

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(takePictureIntent.resolveActivity(getPackageManager()) != null){

            File photoFile = null;
            try {
                photoFile = createPhotoFile(requestCode);
            }catch (Exception e){
                Toast.makeText(this, "Hubo un error con el archivo" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            if(photoFile != null){
                Uri photoUri = FileProvider.getUriForFile(PostActivity.this, "com.example.mistareas",photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,photoUri);
                startActivityForResult(takePictureIntent,PHOTO_REQUEST_CODE); //Esta funcionalidad siempre sobrescribe al metodo onActivityResult
            }
        }
    }

    //Metodo necesario para crear el archivo de la imagen.
    private File createPhotoFile(int requestCode) throws IOException {
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File photoFile = File.createTempFile(
                new Date() + "_photo",
                ".jpg",
                storageDir
        );

        //Asignacion de Paths de imagenes dependiendo de la eleccion del usuario
        if( requestCode == PHOTO_REQUEST_CODE){
            mPhotoPath = "file:" + photoFile.getAbsolutePath();
            mAbsolutePath = photoFile.getAbsolutePath();
        }

        return photoFile;
    }



    private void clickPost(){
        mTitle = mTextInputTitle.getText().toString();
        mDescription = mTextInputDescription.getText().toString();

        
        if(!mTitle.isEmpty() && !mDescription.isEmpty() && !mCategory.isEmpty()){


            //Selecciono ambas imagenes de la galeria
            if(mImageFile != null){
                saveImage(mImageFile);
            }
            //Selecciono las dos fotos de la camara
            else if (mPhotoFile != null){
                saveImage(mPhotoFile);
            }
            else{

                Toast.makeText(PostActivity.this, "Seleccione una imagen", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(PostActivity.this, "Completa todos los campos para continuar", Toast.LENGTH_SHORT).show();
        }
    }



    private void saveImage(File imageFile1){
        mDialog.show(); //Mostramos dialogo de espera
        mImageProvider.save(PostActivity.this, imageFile1).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {

            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if(task.isSuccessful()){
                    mImageProvider.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            final String url = uri.toString();    //Aqui ya hemos obtenidos la url de la imagen.


                            Post post = new Post(); //Establecemos cada uno de los campos que queremos guardar.
                            post.setImage1(url);
                            post.setTitle(mTitle);
                            post.setDescription(mDescription);
                            post.setCategory(mCategory);
                            post.setIdUser(mAutProvider.getUid());
                            post.setTimestamp(new Date().getTime());

                            mPostProvider.save(post).addOnCompleteListener(new OnCompleteListener<Void>() {  //Escuchador para saber en que momento se termina la tarea
                                @Override
                                public void onComplete(@NonNull Task<Void> taskSave) {

                                    mDialog.dismiss();
                                    if(taskSave.isSuccessful()){
                                        Intent intent = new Intent(PostActivity.this, MainActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK); //Evitamos que se pueda volver borrando el historial de activitys
                                        startActivity(intent);
                                        Toast.makeText(PostActivity.this, "LA informacion se almaceno correctamente", Toast.LENGTH_SHORT).show();
                                    }else{
                                        Toast.makeText(PostActivity.this, "No se pudo almacenar la informacion", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    });
                }else{
                    mDialog.dismiss();
                    Toast.makeText(PostActivity.this, "Error al almacenar la imagen", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void openGallery(int requestCode){
    Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
    galleryIntent.setType("image/*");
    startActivityForResult(galleryIntent, requestCode);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        /**
         * Seleccion de imagen desde la galeria
         */
        if(requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK){
            try {
                mPhotoFile = null;

                mImageFile = FileUtil.from(this, data.getData()); //Nos transforma la uri en un archivo
                mImageViewPost1.setImageBitmap(BitmapFactory.decodeFile(mImageFile.getAbsolutePath()));  //mostramos la imagen en activity inicial

            }catch (Exception e){
                Log.d("ERROR", "Se produjo un error" + e.getMessage());
                Toast.makeText(this, "Se produjo un error", Toast.LENGTH_SHORT).show();
            }
        }

        /**
         * Validacion seleccion de fotografia (desde la camara)
         */
        if(requestCode == PHOTO_REQUEST_CODE && resultCode == RESULT_OK){
            mImageFile = null;
            mPhotoFile = new File(mAbsolutePath);   //Creamos el archivo para la imagen de la camara

            //Nos permitira mostrar una imagen a partir de una URL (libreria Picasso)
            Picasso.with(PostActivity.this).load(mPhotoPath).into(mImageViewPost1);
        }

    }
}