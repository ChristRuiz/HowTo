package com.example.tfg;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class SplashActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inicializacimos la Autentificación de Firebase
        mAuth = FirebaseAuth.getInstance();
    }

    /**
     * Al iniciar la activity se ejecuta este metodo donde se comprueba si el usuario estaba logueado
     */
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser == null){
            Intent i = new Intent(SplashActivity.this,MainActivity.class);
            startActivity(i);
            finish();
        } else {
            //Si el usuario estaba logueado se comprueba si tiene nombre de usuario o no
            perfil_registrado();
        }
    }

    /**
     * Comprueba si el perfil estaba registrado
     */
    public void perfil_registrado(){
        String uid = mAuth.getCurrentUser().getUid();

        //Ejecutamos una consulta en la bbdd
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Usuarios");
        Query query = myRef.child(uid).child("username");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                Object value = dataSnapshot.getValue();

                //Comprobamos si existe el campo username
                if(value!=null) {
                    Log.d("Prueba", "Value is: " + value.toString());
                    Intent i = new Intent(SplashActivity.this,Inicio.class);
                    startActivity(i);
                    finish();
                } else {
                    Intent i = new Intent(SplashActivity.this,AsignarPerfil.class);
                    startActivity(i);
                    finish();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });
    }


}
