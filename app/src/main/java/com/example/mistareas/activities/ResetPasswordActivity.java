package com.example.mistareas.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mistareas.R;
import com.example.mistareas.providers.AuthProvider;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ResetPasswordActivity extends AppCompatActivity {

    private TextInputEditText mTextInputEmail;
    private Button mButtonReset;

    private FirebaseAuth mAuth;

    private String email = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        Typeface miFuente = Typeface.createFromAsset(getAssets(),"stickynotes.ttf");
        TextView titulo = (TextView) findViewById(R.id.titulo);
        titulo.setTypeface(miFuente);

        mAuth = FirebaseAuth.getInstance();
        mTextInputEmail = findViewById(R.id.cajaEmailReset);
        mButtonReset = findViewById(R.id.botonReset);


        //Establecemos el escuchador sobre el boton de "solicitar contraseña"
        mButtonReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = mTextInputEmail.getText().toString().trim();
                if(!email.isEmpty()){
                    resetPassword();
                }else{
                    Toast.makeText(ResetPasswordActivity.this, "Error al introducir el email", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * Metodo que solicita a firebase un cambio de contraseña.
     *
     * si todo funciona corectamente enviará un enlace al correo electronico indicado
     * para establecer una nueva contraseña
     */
    public void resetPassword(){
        String emailAddres = mTextInputEmail.getText().toString().trim();
        mAuth.sendPasswordResetEmail(emailAddres).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()){
                    Toast.makeText(ResetPasswordActivity.this, "Se ha enviado un correo para reestablecer su contraseña", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ResetPasswordActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }else{
                    Toast.makeText(ResetPasswordActivity.this, "No se pudo reestablecer la contraseña", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}