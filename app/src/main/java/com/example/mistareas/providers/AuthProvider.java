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

    public Task<AuthResult> register(String email, String password){
        return  mAuth.createUserWithEmailAndPassword(email,password);
    }
    public Task<AuthResult> login(String email, String password){
       return mAuth.signInWithEmailAndPassword(email,password);
    }

    //Buscar el Id del Usuario
    public String getUid(){
        if(mAuth.getCurrentUser() != null){
            return mAuth.getCurrentUser().getUid();
        }else{
            return null;
        }
    }

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

    public void logOut(){
        if(mAuth != null){
            mAuth.signOut();
        }
    }
}
