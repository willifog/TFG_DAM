package com.example.mistareas.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.mistareas.R;
import com.example.mistareas.fragments.ProfileFragment;
import com.example.mistareas.models.Post;
import com.example.mistareas.models.User;
import com.example.mistareas.providers.AuthProvider;
import com.example.mistareas.providers.ImageProvider;
import com.example.mistareas.providers.UsersProvider;
import com.example.mistareas.utils.FileUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;

public class EditProfileActivity extends AppCompatActivity {

    CircleImageView mCircleImageViewBack; //Boton retroceso
    CircleImageView mCircleImageViewProfile;    //Imagen perfil
    ImageView mImageViewCover;
    TextInputEditText mTextInputPhone;
    TextInputEditText mTextInputUserName;
    Button mButtonEditProfile;

    private final int GALLERY_REQUEST_CODE_PROFILE = 1;

    //Variables necesarias para mostrar opciones al subir imagen
    AlertDialog.Builder mBuilderSelector;  //Lo utilizaremos para dar la opcion de hacer una foto cn la camara
    CharSequence options[];
    File mImageFile;
    
    String mUserName = "";
    String mPhone = "";
    String mImageProfile = "";

    AlertDialog mDialog;
    ImageProvider mImageProvider;
    UsersProvider mUsersProvider;
    AuthProvider mAuthProvider;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        //Ocultamos la brra suberior que sale por defecto.
        getSupportActionBar().hide();

        //Ventana emergente para seleccionar imagen
        mBuilderSelector = new AlertDialog.Builder(this);
        mBuilderSelector.setTitle("Selecciona una opcion");
        options = new CharSequence[]{"Imagen de galeria","Tomar foto"};


        mImageViewCover = findViewById(R.id.imageViewCover);
        mTextInputUserName = findViewById(R.id.textInputNameEdit);
        mTextInputPhone = findViewById(R.id.textInputPhoneEdit);

        mImageProvider = new ImageProvider();

        mDialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Espere un momento")
                .setCancelable(false).build();
        
        mUsersProvider = new UsersProvider();
        mAuthProvider = new AuthProvider();

        mButtonEditProfile = findViewById(R.id.btnEditProfile);
        mButtonEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickEditProfile();
            }
        });

        mCircleImageViewProfile = findViewById(R.id.circleImageProfileEdit);
        mCircleImageViewProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery(GALLERY_REQUEST_CODE_PROFILE);
            }
        });

        mCircleImageViewBack = findViewById(R.id.circleImageBack);
        mCircleImageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        getUser();
    }

    private void getUser(){
        mUsersProvider.getUser(mAuthProvider.getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){      //Comprobamos si el documento del usuario existe en la bbdd y contiene los campos necesarios
                    if(documentSnapshot.contains("username")){
                        mUserName = documentSnapshot.getString("username");  //Aqui asignamos en el mUserName el nombre de usuario desde la bbdd
                        mTextInputUserName.setText(mUserName);
                    }
                    if(documentSnapshot.contains("phone")){
                        mPhone = documentSnapshot.getString("phone");
                        mTextInputPhone.setText(mPhone);
                    }
                    if(documentSnapshot.contains("image_profile")){
                        mImageProfile = documentSnapshot.getString("image_profile");
                        if(mImageProfile!= null && (!mImageProfile.isEmpty())){
                            Picasso.with(EditProfileActivity.this).load(mImageProfile).into(mCircleImageViewProfile);
                        }
                    }
                }
            }
        });
    }

    private void clickEditProfile() {
        mUserName = mTextInputUserName.getText().toString();
        mPhone = mTextInputPhone.getText().toString();

        if(!mUserName.isEmpty() && !mPhone.isEmpty()){
            if(mImageFile != null){
                saveImage(mImageFile);
            }else{
                User user = new User();
                user.setUsername(mUserName);
                user.setPhone(mPhone);
                user.setId(mAuthProvider.getUid());
                user.setImageProfile(mImageProfile);
                updateInfo(user);
            }

        }else{
            Toast.makeText(this, "Ingrese el nombre de usuario y el telefono", Toast.LENGTH_SHORT).show();
        }
        
    }

    private void saveImage(File imageFile){
        mDialog.show(); //Mostramos dialogo de espera
        mImageProvider.save(EditProfileActivity.this, imageFile).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if(task.isSuccessful()){
                    mImageProvider.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            final String urlProfile = uri.toString();    //Aqui ya hemos obtenidos la url de la imagen.

                            User user = new User();
                            user.setId(mAuthProvider.getUid());
                            user.setUsername(mUserName);
                            user.setPhone(mPhone);
                            user.setImageProfile(mImageProfile);    //Añadimos la imagen ya que sino apareceria a null y daria error al actualizar
                            user.setImageProfile(urlProfile);

                            updateInfo(user);
                        }
                    });
                }else{
                    mDialog.dismiss();
                    Toast.makeText(EditProfileActivity.this, "Error al almacenar la imagen", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void updateInfo(User user){
        if(mDialog.isShowing()){        //Para que no se solape con el del metodo que lo ha llamado
            mDialog.show();
        }

        mUsersProvider.update(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                mDialog.dismiss();
                if(task.isSuccessful()){
                    Toast.makeText(EditProfileActivity.this, "La informacion se actualizo correctamente", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(EditProfileActivity.this, "La informacion no se pudo actualizar", Toast.LENGTH_SHORT).show();
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
        if(requestCode == GALLERY_REQUEST_CODE_PROFILE && resultCode == RESULT_OK){
            try {
                mImageFile = FileUtil.from(this, data.getData()); //Nos transforma la uri en un archivo
                mCircleImageViewProfile.setImageBitmap(BitmapFactory.decodeFile(mImageFile.getAbsolutePath()));  //mostramos la imagen en activity inicial

            }catch (Exception e){
                Log.d("ERROR", "Se produjo un error" + e.getMessage());
                Toast.makeText(this, "Se produjo un error", Toast.LENGTH_SHORT).show();
            }
        }

    }
}