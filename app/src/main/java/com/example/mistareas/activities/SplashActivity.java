package com.example.mistareas.activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mistareas.R;

public class SplashActivity extends AppCompatActivity implements Animation.AnimationListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


    //Ocultamos la brra suberior que sale por defecto.
    getSupportActionBar().hide();

        ImageView bird2 = (ImageView) findViewById(R.id.bird2);
        Animation anim3 = AnimationUtils.loadAnimation(this,R.anim.animacion3);
        bird2.startAnimation(anim3);





    //Asignamos fuente auna variable
    Typeface miFuente = Typeface.createFromAsset(getAssets(),"stickynotes.ttf");

    //Asignamos etiqueta a una variable
    TextView titulo = (TextView) findViewById(R.id.titulo);
    titulo.setTypeface(miFuente);

    //Cargamos la animacion y la iniciamos
    Animation anim = AnimationUtils.loadAnimation(this,R.anim.animacion);
    titulo.startAnimation(anim);

    //Ponemos a la escucha la animacion para indicar cuando pasar a la siguiente activity
    anim3.setAnimationListener(this);
    }

    /**
     * Metodo para enlazar con la siguiente Activity y finalizar esta. ya que no se tiene que poder
     * volver a esta.
     *
     * @param animation
     */
    @Override
    public void onAnimationEnd(Animation animation) {
        Intent intent = new Intent(this,LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onAnimationStart(Animation animation) {

    }
    @Override
    public void onAnimationRepeat(Animation animation) {

    }
}