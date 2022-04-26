package com.example.mistareas.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;

import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mistareas.R;
import com.example.mistareas.providers.AuthProvider;
import com.example.mistareas.providers.UsersProvider;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import dmax.dialog.SpotsDialog;


public class LoginActivity extends AppCompatActivity {

    TextInputEditText mTextInputEmail;
    TextInputEditText mTextInputPassword;
    Button mButtonLogin;

    AuthProvider  mAuthProvider;
    UsersProvider mUsersProvider;

    AlertDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide(); //Ocultamos barra superior

        Typeface miFuente = Typeface.createFromAsset(getAssets(),"stickynotes.ttf");
        TextView titulo = (TextView) findViewById(R.id.titulo);
        titulo.setTypeface(miFuente);

        mTextInputEmail = findViewById(R.id.cajaUser);
        mTextInputPassword = findViewById(R.id.cajaPass);
        mButtonLogin = findViewById(R.id.botonLogin);

        mAuthProvider = new AuthProvider();

        mDialog = new SpotsDialog.Builder() //Inicializamos en mensaje de espera
                .setContext(this)
                .setMessage("Espere un momento")
                .setCancelable(false).build();

        //Escuchador del boton Login
        mButtonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });

     }


     //Dejamos la sesion iniciada (accederia sin tener que logearse)
    @Override
    protected void onStart() {
        super.onStart();
        if(mAuthProvider.getUserSession() != null){
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK); //Evitamos que se pueda volver borrando el historial de activitys
            startActivity(intent);
        }
    }

    //Cambio activity Registro
    public void crearCuenta(View view){
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    //Nos logeamos con correo y contraseña
    public void login(){
            String email = mTextInputEmail.getText().toString();
            String password = mTextInputPassword.getText().toString();
            mDialog.show();

            mAuthProvider.login(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    mDialog.dismiss();
                    if(task.isSuccessful()){
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);

                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);  //Limpiamos el historial de pantallas que hayamos abierto.
                        startActivity(intent);
                    }else{
                        Toast.makeText(LoginActivity.this, "Email o ocntraseña erroneo", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            
    }



}