package com.diversion.generardia;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void ModuloGenerarDia(View view){
        Intent generardia = new Intent(this, GenerarDia.class);
        startActivity(generardia);
    }

    public void ModuloGenerarMenu(View view){
        Intent tipomenu = new Intent(this, TurnoMenu.class);
        startActivity(tipomenu);
    }

    public void ModuloRegistrarRopa(View view){
        Intent registrarRopa = new Intent(this, RegistrarRopa.class);
        startActivity(registrarRopa);
    }

    public void ModuloRegistrarComida(View view){
        Intent registrarComida = new Intent(this, RegistrarComida.class);
        startActivity(registrarComida);
    }
}
