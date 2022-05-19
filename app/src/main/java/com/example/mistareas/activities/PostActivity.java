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
import java.util.Locale;

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


        options = new CharSequence[]{"Imagen de galeria","Tomar foto"}; //Indicamos las opciones posibles.

        mDialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Espere un momento")
                .setCancelable(false).build();


        //Categoria 1
        mImageViewComida = findViewById(R.id.imageViewAmericana);
        mImageViewComida.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCategory = "AMERICANA";
                mTextViewCategory.setText(mCategory);
            }
        });

        //Categoria 2
        mImageViewCena = findViewById(R.id.imageViewChina);
        mImageViewCena.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCategory = "CHINA";
                mTextViewCategory.setText(mCategory);
            }
        });

        //Categoria 3
        mImageViewDesayuno = findViewById(R.id.imageViewMediterraneo);
        mImageViewDesayuno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCategory = "MEDITERRANEA";
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


    /**
     * Metodo que nos muestra las opciones para subir una imagen (galeria/camara)
     *
     * Dependiendo de la seleccion abrirá la galeria o la camara.
     */
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

    /**
     * Metodo utilizado para hacer una foto desde la camara.
     *
     * Se debe de dar permisos en en AndroidManifest para poder acceder a la camara de fotos.
     *
     * @param requestCode numero para diferenciar entre galeria y camara debe de ser positivo.
     */
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


    /**
     * Metodo utilizado para crear el archivo de imagen en caso de hacer una foto con la camara
     * @param requestCode
     * @return
     * @throws IOException
     */
    private File createPhotoFile(int requestCode) throws IOException {
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File photoFile = File.createTempFile(new Date() + "_photo",".jpg",storageDir);

        //Asignacion de Paths de imagenes dependiendo de la eleccion del usuario
        if( requestCode == PHOTO_REQUEST_CODE){
            mPhotoPath = "file:" + photoFile.getAbsolutePath();
            mAbsolutePath = photoFile.getAbsolutePath();
        }
        return photoFile;
    }


    /**
     * Metodo que selecciona la imagen a guardar dependiendo de si una opcion está vacia.
     *
     * Si la imagen de galeria es distinto de null almacenará esta. en caso contrario almacena
     * la imagen tomada con la camara
     *
     */
    private void clickPost(){
        mTitle = mTextInputTitle.getText().toString();
        mDescription = mTextInputDescription.getText().toString();
        if(!mTitle.isEmpty() && !mDescription.isEmpty() && !mCategory.isEmpty()){
            //Selecciono  imagen de la galeria
            if(mImageFile != null){
                saveImage(mImageFile);
            }
            //Selecciono la foto de la camara
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


    /**
     * Metodo utilizado para almacenar imagenes en la base de datos.
     * EL metodo se apoya en la clase ImageProvider que nos permite utilizar un metodo para guardar
     * el fichero y nos retorna una tarea que la controlamos en funcion de si es correcta o falla.
     *
     * Si la tarea es satisfactoria se crea un objeto post con los datos requeridos.
     *
     * Una vez se ha creado el objeto post nos apoyamos en la clase PostProvider para almacenar este post,
     * y pasar a la siguiente activity.
     *
     * en caso de error se muestra una alerta.
     *
     * @param imageFile1 Imagen que queremos almacenar
     */
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
                            post.setTitle(mTitle.toLowerCase());
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
                                        Toast.makeText(PostActivity.this, "La información se almaceno correctamente", Toast.LENGTH_SHORT).show();
                                    }else{
                                        Toast.makeText(PostActivity.this, "No se pudo almacenar la información", Toast.LENGTH_SHORT).show();
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


    /**
     * Metodo utilizado para obtener un resultado de la activity (imagenes)
     *
     * Dependiendo del codigo introducido el metodo selecciona si es una imagen de la camara o de la
     * galeria. una vez seleccionada la imagen la muestra en la ImageView.
     *
     * @param requestCode codigo de identificacion de imagen (camara/galeria)
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

         //Seleccion de imagen desde la galeria
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

        //Validacion seleccion de fotografia (desde la camara)
        if(requestCode == PHOTO_REQUEST_CODE && resultCode == RESULT_OK){
            mImageFile = null;
            mPhotoFile = new File(mAbsolutePath);   //Creamos el archivo para la imagen de la camara
            //Nos permitira mostrar una imagen a partir de una URL (libreria Picasso)
            Picasso.with(PostActivity.this).load(mPhotoPath).into(mImageViewPost1);
        }

    }
}