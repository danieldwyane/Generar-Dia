package com.example.generardia;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private TextView tv1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv1 = (TextView)findViewById(R.id.textView);
    }

    public void GenerarAprendizaje(View view){
        ArrayList<String> leccion = new ArrayList<>();
        leccion.add("Valyrian");
        leccion.add("Plantas");
        leccion.add("Mecánica");
        leccion.add("Informática");
        leccion.add("Cocina");

        int tamanoLecciones = leccion.size() - 1;
        int x = (int)(Math.random()*((tamanoLecciones-0)+1))+0;

        String leccionElegida = leccion.get(x);

        tv1.setText(leccionElegida);
    }
}
