package com.example.mistareas.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.AlertDialog;
import android.app.Dialog;
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
    ImageView mImageViewPost2;

    private final int GALLERY_REQUEST_CODE = 1;
    private final int GALLERY_REQUEST_CODE_2 = 2;
    private final int PHOTO_REQUEST_CODE = 3;
    private final int PHOTO_REQUEST_CODE_2 = 4;

    File mImageFile;
    File mImageFile2;

    Button mButtonPost;
    ImageProvider mImageProvider;

    TextInputEditText mTextInputTitle;
    TextInputEditText mTextInputDescription;
    TextInputEditText mTextInputDificultad;
    ImageView mImageViewPc;
    ImageView mImageViewPs4;
    ImageView mImageViewXbox;
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

    //Foto2
    String mAbsolutePath2;
    String mPhotoPath2;
    File mPhotoFile2;

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


        mImageViewPc = findViewById(R.id.imageViewPc);
        mImageViewPc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCategory = "PC";
                mTextViewCategory.setText(mCategory);
            }
        });

        mImageViewPs4 = findViewById(R.id.imageViewPs4);
        mImageViewPs4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCategory = "PS4";
                mTextViewCategory.setText(mCategory);
            }
        });

        mImageViewXbox = findViewById(R.id.imageViewXbox);
        mImageViewXbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCategory = "XBOX";
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

        mImageViewPost2 = findViewById(R.id.imageViewPost2);
        mImageViewPost2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectOptionImage(2);
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
                    else if(numberImage == 2){
                        openGallery(GALLERY_REQUEST_CODE_2);
                    }
                }else if(i == 1){    //Imagen de la Camara

                    if(numberImage == 1){
                        takePhoto(PHOTO_REQUEST_CODE);
                    }
                    else if(numberImage == 2){
                        takePhoto(PHOTO_REQUEST_CODE_2);
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
        else if(requestCode == PHOTO_REQUEST_CODE_2){
            mPhotoPath2 = "file:" + photoFile.getAbsolutePath();
            mAbsolutePath2 = photoFile.getAbsolutePath();
        }

        return photoFile;
    }


    private void clickPost(){
        mTitle = mTextInputTitle.getText().toString();
        mDescription = mTextInputDescription.getText().toString();

        
        if(!mTitle.isEmpty() && !mDescription.isEmpty() && !mCategory.isEmpty()){

            //Selecciono ambas imagenes de la galeria
            if(mImageFile != null && mImageFile2 != null){
                saveImage(mImageFile, mImageFile2);
            }
            //Selecciono las dos fotos de la camara
            else if (mPhotoFile != null && mPhotoFile2 != null){
                saveImage(mPhotoFile, mPhotoFile2);
            }
            else if (mImageFile != null && mPhotoFile2 != null){
                saveImage(mImageFile, mPhotoFile2);
            }
            else if (mPhotoFile != null && mImageFile2 != null){
                saveImage(mPhotoFile, mImageFile2);
            }
            else{
                Toast.makeText(PostActivity.this, "Seleccione una imagen", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(PostActivity.this, "Completa todos los campos para continuar", Toast.LENGTH_SHORT).show();
        }
    }


    private void saveImage(File imageFile1, File imageFile2){
        mDialog.show(); //Mostramos dialogo de espera
        mImageProvider.save(PostActivity.this, imageFile1).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if(task.isSuccessful()){
                    mImageProvider.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            final String url = uri.toString();    //Aqui ya hemos obtenidos la url de la imagen.

                            mImageProvider.save(PostActivity.this, imageFile2).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> taskImage2) {
                                    if(taskImage2.isSuccessful()){
                                        mDialog.dismiss();;
                                        mImageProvider.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri2) {
                                                final String url2 = uri2.toString();

                                                Post post = new Post(); //Establecemos cada uno de los campos que queremos guardar.
                                                post.setImage1(url);
                                                post.setImage2(url2);
                                                post.setTitle(mTitle);
                                                post.setDescription(mDescription);
                                                post.setCategory(mCategory);
                                                post.setIdUser(mAutProvider.getUid());

                                                mPostProvider.save(post).addOnCompleteListener(new OnCompleteListener<Void>() {  //Escuchador para saber en que momento se termina la tarea
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> taskSave) {
                                                        mDialog.dismiss();

                                                        if(taskSave.isSuccessful()){
                                                            clearForm();

                                                            Toast.makeText(PostActivity.this, "LA informacion se almaceno correctamente", Toast.LENGTH_SHORT).show();
                                                        }else{
                                                            Toast.makeText(PostActivity.this, "No se pudo almacenar la informacion", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                            }
                                        });
                                    }
                                    else{
                                        mDialog.dismiss(); // Cerramos el dialogo de espera
                                        Toast.makeText(PostActivity.this, "La imagen numero2 no se ha podido guardar", Toast.LENGTH_SHORT).show();
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


    private void clearForm(){
        //Reiniciamos el texto
        mTextInputTitle.setText("");
        mTextViewCategory.setText("");
        mTextInputDescription.setText("");
        mTextInputDificultad.setText("");

        //reinicioamos las imagenes
        mImageViewPost1.setImageResource(R.drawable.upload_image);
        mImageViewPost2.setImageResource(R.drawable.upload_image);

        mTitle = "";
        mDescription = "";
        mCategory = "";
        mImageFile = null;
        mImageFile2 = null;
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

        if(requestCode == GALLERY_REQUEST_CODE_2 && resultCode == RESULT_OK){
            try {
                mPhotoFile2 = null;
                mImageFile2 = FileUtil.from(this, data.getData()); //Nos transforma la uri en un archivo
                mImageViewPost2.setImageBitmap(BitmapFactory.decodeFile(mImageFile2.getAbsolutePath()));  //mostramos la imagen en activity inicial

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

        //Hacer Foto desde la segunda opcion
        if(requestCode == PHOTO_REQUEST_CODE_2 && resultCode == RESULT_OK){
            mImageFile2 = null;
            mPhotoFile2 = new File(mAbsolutePath2);
            Picasso.with(PostActivity.this).load(mPhotoPath2).into(mImageViewPost2);
        }


    }
}