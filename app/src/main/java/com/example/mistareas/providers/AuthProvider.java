package com.example.mistareas.providers;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AuthProvider {

    private FirebaseAuth mAuth;

    public AuthProvider(){
        mAuth = FirebaseAuth.getInstance();
    }

    /**
     * Metodo para añadir un nuevo usuario a la base de datos
     *
     * @param email email introducido por el usuario
     * @param password contraseña introducida por usuario
     *
     * @return retorna una tarea(se controla con el metodo que la llama)
     */
    public Task<AuthResult> register(String email, String password){
        return  mAuth.createUserWithEmailAndPassword(email,password);
    }

    /**
     * Metodo que permite el acceso a la aplicacion si existen las credenciales en la bbdd
     * @param email correo electronico del usuario
     * @param password contraseña a introducir por el usuario
     * @return acceso a la app
     */
    public Task<AuthResult> login(String email, String password){
       return mAuth.signInWithEmailAndPassword(email,password);
    }

    /**
     * Metodo que obtiene el id del usuario
     * @return ID del usuario actual
     */
    public String getUid(){
        if(mAuth.getCurrentUser() != null){
            return mAuth.getCurrentUser().getUid();
        }else{
            return null;
        }
    }

    /**
     * Metodo que permite mantener la sesion iniciada
     * @return sesion del usuario
     */
    public FirebaseUser getUserSession(){
        if(mAuth.getCurrentUser() != null){
            return mAuth.getCurrentUser();
        }else{
            return null;
        }
    }

    //Buscar el Email
    public String getEmail(){
        if(mAuth.getCurrentUser() != null){
            return mAuth.getCurrentUser().getEmail();
        }else{
            return null;
        }
    }

    /**
     * metodo que cierra la sesion del usuario
     */
    public void logOut(){
        if(mAuth != null){
            mAuth.signOut();
        }
    }
}
