package com.example.mistareas.activities;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mistareas.R;
import com.example.mistareas.adapters.CommentAdapter;
import com.example.mistareas.models.Comment;
import com.example.mistareas.models.FCMBody;
import com.example.mistareas.models.FCMResponse;
import com.example.mistareas.models.Post;
import com.example.mistareas.providers.AuthProvider;
import com.example.mistareas.providers.CommentProvider;
import com.example.mistareas.providers.NotificationProvider;
import com.example.mistareas.providers.PostProvider;
import com.example.mistareas.providers.TokenProvider;
import com.example.mistareas.providers.UsersProvider;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.squareup.picasso.Picasso;

import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostDetailActivity extends AppCompatActivity {

    ImageView mImageView;
    PostProvider mPostProvider; //Nos permite traer informacion de las publicaciones
    UsersProvider mUsersProviders;  //Nos permite traer informacion de los usuarios
    CommentProvider mCommentProvider;  //Nos permite obtener informacion de los comentarios
    AuthProvider mAuthProvider;

    CommentAdapter mAdapter;

    TextView mTextViewTitle;
    TextView mTextViewDescription;
    TextView mTextViewUserName;
    TextView mTextViewPhone;
    TextView mTextViewNameCategory;
    CircleImageView mCircleImageViewProfile;
    CircleImageView mCircleImageViewBack; //Boton retroceso
    Button mButtonShowProfile;
    RecyclerView mRecyclerView;

    NotificationProvider mNotificationProvider;
    TokenProvider mTokenProvider;

    String mExtraPostId;
    String mIdUser = "";

    FloatingActionButton mBtnComent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        mImageView = findViewById(R.id.imagePostDetail);

        mExtraPostId = getIntent().getStringExtra("id"); //recibimos el id del usuario para buscar su foto mas a delante


        mTextViewTitle = findViewById(R.id.textViewTitle);
        mTextViewDescription = findViewById(R.id.textViewDescription);
        mTextViewUserName= findViewById(R.id.textViewUserNamePost);
        mTextViewPhone = findViewById(R.id.textViewPhonePost);
        mTextViewNameCategory = findViewById(R.id.textViewNameCategory);
        mCircleImageViewProfile = findViewById(R.id.circleImageProfile);
        mButtonShowProfile= findViewById(R.id.btnShowProfile);
        mRecyclerView = findViewById(R.id.reciclerViewComment);

        //Nos mostrar?? las tarjetas una debajo de la otra
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(PostDetailActivity.this);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        mPostProvider = new PostProvider();
        mUsersProviders = new UsersProvider();
        mCommentProvider = new CommentProvider();
        mAuthProvider = new AuthProvider();
        mNotificationProvider = new NotificationProvider();
        mTokenProvider = new TokenProvider();


        /**
         * Funcion que nos permite mostrar un dialogo para insertar un comentario.
         */
        mBtnComent = findViewById(R.id.btnComent);
        mBtnComent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogComent();
            }
        });

        //Boton de retroceso
        mCircleImageViewBack = findViewById(R.id.circleImageBack);
        mCircleImageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        getPost();


        mButtonShowProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToShowProfile();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        //Obenemos los comentarios  pasandole el id del post en el que estamos.
        Query query = mCommentProvider.getCommentByPost(mExtraPostId);
        FirestoreRecyclerOptions<Comment> options = new FirestoreRecyclerOptions
                .Builder<Comment>().setQuery(query, Comment.class)
                .build();
        mAdapter = new CommentAdapter(options, PostDetailActivity.this);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.startListening();  //Obtenemos datos en tiempo real.
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAdapter.stopListening();
    }

    /**
     * Metodo el cual muestra un AlertDialog para poder publicar comentarios en las publicaciones.
     */
    private void showDialogComent() {
        final EditText cajaTexto = new EditText(this);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("A??adir comentario")
                .setView(cajaTexto)
                .setPositiveButton("Publicar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String comentario = cajaTexto.getText().toString();
                        if(!comentario.isEmpty()){
                            createComment(comentario);
                        }
                        else{
                            Toast.makeText(PostDetailActivity.this, "Debe escribir un comentario", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("Cancelar",null)
                .create();
        dialog.show();
    }

    /**
     * Metodo utilizado para crear un nuevo objeto comentario
     *
     * @param comentario String introducido por usuario desde un DialogInterface
     */
    private void createComment(final String comentario) {
        Comment comment = new Comment();
        comment.setComment(comentario);
        comment.setIdPost(mExtraPostId);
        comment.setIdUser(mAuthProvider.getUid());
        comment.setTimestamp(new Date().getTime());

        //Metodo utilizado para saber cuando se ha completado la tarea
        mCommentProvider.create(comment).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                //    sendNotification(comentario);
                    Toast.makeText(PostDetailActivity.this , "Comentario a??adido", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(PostDetailActivity.this, "No se ha podido publicar el comentario", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void sendNotification(String comment){
        if(mIdUser == null){
            return;
        }
        mTokenProvider.getToken(mIdUser).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    if(documentSnapshot.contains("token")){
                        String token = documentSnapshot.getString("token");
                        Map<String, String> data = new HashMap<>();
                        data.put("title", "NUEVO COMENTARIO");
                        data.put("body", comment);
                        FCMBody body = new FCMBody(token, "high", "4500s", data);
                        mNotificationProvider.sendNotification(body).enqueue(new Callback<FCMResponse>() {
                            @Override
                            public void onResponse(Call<FCMResponse> call, Response<FCMResponse> response) {
                                if(response.body() != null){
                                    if(response.body().getSuccess() == 1){
                                        Toast.makeText(PostDetailActivity.this, "La notificaci??n se envi?? correctamente",Toast.LENGTH_SHORT).show();
                                    }else{
                                        Toast.makeText(PostDetailActivity.this, "La notificaci??n no se pudo enviar",Toast.LENGTH_SHORT).show();
                                    }
                                }else{
                                    Toast.makeText(PostDetailActivity.this, "La notificaci??n no se pudo enviar",Toast.LENGTH_SHORT).show();
                                }
                            }
                            @Override
                            public void onFailure(Call<FCMResponse> call, Throwable t) {
                            }
                        });
                    }
                }else{
                    Toast.makeText(PostDetailActivity.this, "El token de notificaciones no existe",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * Metodo que nos permite mostrar el perfil del usuario que ha creado el post,
     * utilizando el id del usuario obtenido a partir de la publicacion en la que estamos situados.
     *
     * (nos apoyamos en la clase PostProvider)
     */
    private void goToShowProfile() {
        if(!mIdUser.equals("")){
            Intent intent = new Intent(PostDetailActivity.this, UserProfileActivity.class);
            intent.putExtra("idUser", mIdUser);
            startActivity(intent);
        }else{
            Toast.makeText(this, "No se ha podido cargar el perfil de usuario", Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * Metodo utilizado para obtener la informacion de la publicacion.
     *
     * Nos apoyamos en la clase PostProvider para acceder a los datos mediante el id.
     *
     * Para hacer la consulta utilizamos la clase DocumentSnapshot y sus metodos.
     */
    private void getPost(){

        mPostProvider.getPostById(mExtraPostId).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {  //Obtenemos la publicacion del user
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists() && documentSnapshot.contains("image1")){   //Buscamos la imagen del user en el documento de la bbbdd
                    String image = documentSnapshot.getString("image1");
                    Picasso.with(PostDetailActivity.this).load(image).into(mImageView);
                }

                //Obtenemos el titulo del documento de la bbdd y lo asignamos ala variable
                if(documentSnapshot.contains("title")){
                    String title = documentSnapshot.getString("title");
                    mTextViewTitle.setText(title.toUpperCase());
                }
                //Obtenemos la descripcion
                if(documentSnapshot.contains("description")){
                    String description = documentSnapshot.getString("description");
                    mTextViewDescription.setText(description);
                }
                //Obtenemos la categoria
                if(documentSnapshot.contains("category")){
                    String categoria = documentSnapshot.getString("category");
                    mTextViewNameCategory.setText(categoria);
                }

                //Obtenemos el ID del User
                if(documentSnapshot.contains("idUser")){
                    mIdUser = documentSnapshot.getString("idUser");
                    getUserInfo(mIdUser);
                }

            }
        });
    }

    /**
     * Metodo utilizado para obtener los datos del usuario que ha creado la publicacion.
     *
     * @param idUser ID del usuario del que queremos obtener los datos.
     */
    private void getUserInfo(String idUser) {

        //Consultamos los datos del usuario
        mUsersProviders.getUser(idUser).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){

                    //Si se cumplen las condiciones establecemos el nombre del usuario
                    if(documentSnapshot.contains("username")){
                        String userName = documentSnapshot.getString("username");
                        mTextViewUserName.setText(userName);
                    }
                    //Establecemos el numero de telefono
                    if(documentSnapshot.contains("phone")){
                        String phone = documentSnapshot.getString("phone");
                        mTextViewPhone.setText(phone);
                    }
                    //Establecemos el numero de telefono
                    if(documentSnapshot.contains("image_profile")){
                        String imageProfile = documentSnapshot.getString("image_profile");

                        //Utilizamos  Picasso para indicar el contexto donde estamos, cargar la imagen recibida y colocarla en la variable correspondiente
                        Picasso.with(PostDetailActivity.this).load(imageProfile).into(mCircleImageViewProfile);
                    }

                }

            }
        });
    }
}