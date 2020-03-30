package com.diversion.generardia;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class TurnoMenu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_turno_menu);
    }

    public void ModuloGenerarMenuDesayuno(View view){
        Intent generarmenuDesayuno = new Intent(this, TipoMenu.class);
        generarmenuDesayuno.putExtra("turnoMenu", "DESAYUNO");
        startActivity(generarmenuDesayuno);
    }

    public void ModuloGenerarMenuAlmuerzo(View view) {
        Intent generarmenualmuerzo = new Intent(this, TipoMenu.class);
        generarmenualmuerzo.putExtra("turnoMenu", "ALMUERZO");
        startActivity(generarmenualmuerzo);
    }

    public void ModuloGenerarMenuCena(View view) {
        Intent generarmenuacena = new Intent(this, TipoMenu.class);
        generarmenuacena.putExtra("turnoMenu", "CENA");
        startActivity(generarmenuacena);
    }

    public void ModuloGenerarMenuMerienda(View view) {
        Intent generarmenumerienda = new Intent(this, TipoMenu.class);
        generarmenumerienda.putExtra("turnoMenu", "MERIENDA");
        startActivity(generarmenumerienda);
    }
}
