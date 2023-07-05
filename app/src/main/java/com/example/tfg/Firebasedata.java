package com.example.tfg;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

public class Firebasedata extends Application {

    @Override
    public void onCreate() {
        //Establecemos que no haya persistencia de datos de Firebase para evitar algunos errores con la bbdd del servidor
        FirebaseDatabase.getInstance().setPersistenceEnabled(false);
        super.onCreate();
    }
}
