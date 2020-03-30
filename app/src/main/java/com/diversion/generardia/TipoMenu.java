package com.diversion.generardia;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class TipoMenu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tipo_menu);
    }

    public void ModuloGenerarMenuPersonal(View view){
        Intent generarmenupersonal = new Intent(this, GenerarMenu.class);
        String dato = getIntent().getStringExtra("turnoMenu");
        generarmenupersonal.putExtra("turnoMenu", dato);
        startActivity(generarmenupersonal);
    }

    public void ModuloGenerarMenuAleatorio(View view){
        Intent generarmenualeatorio = new Intent(this, GenerarMenuAleatorio.class);
        String dato = getIntent().getStringExtra("turnoMenu");
        generarmenualeatorio.putExtra("turnoMenu", dato);
        startActivity(generarmenualeatorio);
    }
}
