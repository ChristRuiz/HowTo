package com.example.tfg;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ResultadosBuscador extends AppCompatActivity implements View.OnClickListener {

    TextView no_resultados;
    FirebaseAuth mAuth;
    RecyclerView resultados;
    AdaptadorRecyclerUsuarios adaptador_resultados;
    List<Usuario> resultadosList;
    private String buscar;
    boolean query1_terminada = false;
    boolean query2_terminada = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.resultados_buscador);

        buscar = getIntent().getExtras().getString("buscar");
        if(buscar.startsWith("@")){
            buscar = buscar.substring(1);
        }

        //Creamos una toolbar personalizada
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Buscar");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(this);

        no_resultados = findViewById(R.id.no_resultados);
        resultados = findViewById(R.id.lista_resultados);

        // Inicializacimos la Autentificación de Firebase
        mAuth = FirebaseAuth.getInstance();

        no_resultados.setVisibility(View.VISIBLE);

        //Le indicamos que tipo de layout tiene para que se agilice la carga de datos
        resultados.setLayoutManager(new LinearLayoutManager(this));
        resultados.setNestedScrollingEnabled(false);

        resultadosList = new ArrayList<Usuario>();

        adaptador_resultados = new AdaptadorRecyclerUsuarios(resultadosList,this);
        adaptador_resultados.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Usuario usuario = resultadosList.get(resultados.getChildAdapterPosition(v));
                startActivity(new Intent(getApplicationContext(),MostrarPerfil.class)
                        .putExtra("uid",usuario.getUid()));
            }
        });
        resultados.setAdapter(adaptador_resultados);

        //Le pones cargando mientras busca todos los resultados
        final ProgressDialog progressDialog = new ProgressDialog(this, R.style.AppCompatAlertDialogStyle);
        progressDialog.setMessage("Cargando Resultados...");
        progressDialog.show();

        //Buscamos usuarios por su nombre completo
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Usuarios");
        Query query = myRef.orderByChild("nombre").startAt(buscar)
                .endAt(buscar+"\uf8ff");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot data: dataSnapshot.getChildren()) {
                    Usuario usuario = new Usuario(data.getKey());
                    resultadosList.add(0,usuario);
                }

                //Buscamos usuarios por su nombre de usuario
                DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Usuarios");
                Query query = myRef.orderByChild("username").startAt(buscar)
                        .endAt(buscar+"\uf8ff");
                query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot data: dataSnapshot.getChildren()) {
                            boolean repetido = false;
                            Usuario usuario = new Usuario(data.getKey());
                            //Comprobamos que no este repetido de buscarlo antes por nombre completo
                            for (Usuario user: resultadosList) {
                                if(user.equalsUid(usuario)){
                                    repetido = true;
                                }
                            }
                            //Si no esta repetido se añade a los resultados
                            if(!repetido){
                                resultadosList.add(0,usuario);
                            }
                        }

                        //Si no hay usuarios, mostramos el texto de que no hay usuarios
                        if(resultadosList.size()>0){
                            no_resultados.setVisibility(View.GONE);
                        } else {

                            adaptador_resultados.setItems(resultadosList);
                            adaptador_resultados.notifyDataSetChanged();

                            no_resultados.setVisibility(View.VISIBLE);
                        }

                        //Quitamos la barra de cargando
                        progressDialog.dismiss();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            default:
                onBackPressed();
                break;
        }
    }
}
