package com.diversion.generardia;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

public class GenerarMenuAleatorio extends AppCompatActivity {
    private TextView tv1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generar_menu_aleatorio);

        tv1 = (TextView)findViewById(R.id.textViewGen);
        String dato = getIntent().getStringExtra("turnoMenu");
        tv1.setText(dato);
    }

    public void GenerarMenu(View view){
        /**lista de alimentos*/
        ArrayList<String> alimento = new ArrayList<>();
        alimento.add("Pollo");
        alimento.add("Carne");
        alimento.add("Chuleta");
        alimento.add("Huevo");
        alimento.add("Pescado");

        /**lista de contornos*/
        ArrayList<String> contorno = new ArrayList<>();
        contorno.add("Arroz");
        contorno.add("Espagueti");
        contorno.add("Fajitas");
        contorno.add("Tortilla");

        /**lista de acompañantes*/
        ArrayList<String> acompanante = new ArrayList<>();
        acompanante.add("Yuca");
        acompanante.add("Papas");
        acompanante.add("Tajadas");
        acompanante.add("Tostones");
        acompanante.add("Caraotas");

        /**lista de ensaladas*/
        ArrayList<String> ensalada = new ArrayList<>();
        String descEnsalada = "Ensalada";
        ensalada.add(descEnsalada+" (Papa y Huevo)");
        ensalada.add(descEnsalada+" (Lechuga, Tomate, Cebolla)");
        ensalada.add(descEnsalada+" (Remolacha, Zanahoria, Huevo, Papa)");

        int tamanoAlimento = alimento.size() - 1;//tamaño del array alimento menos 1
        int indiceAlimento = (int)(Math.random()*((tamanoAlimento-0)+1))+0;//se elige un alimento al azar
        String alimentoGenerado = alimento.get(indiceAlimento);

        int banderaContorno = (int)(Math.random()*((1-0)+1))+0;//se decide al azar si se agrega o no un contorno
        int tamanoContorno = contorno.size() - 1;//tamaño del array contorno menos 1
        int indiceContorno = (int)(Math.random()*((tamanoContorno-0)+1))+0;//se elige un contorno al azar

        int banderaEnsalada = (int)(Math.random()*((1-0)+1))+0;//se decide al azar si se agrega o no una ensalada
        int tamanoEnsalada = ensalada.size() - 1;//tamaño del array contorno menos 1
        int indiceEnsalada = (int)(Math.random()*((tamanoEnsalada-0)+1))+0;//se elige una ensalada al azar

        int minAcompanante = 2;//parametro para definir el minimo posible de acompañantes
        int maxAcompanante = 4;//parametro para definir el maximo posible de acompañantes
        int tamanoAcompanante = acompanante.size() - 1;
        ArrayList<String> acompananteGenerados = new ArrayList<>();
        int indiceAcompanante = 0;
        String acompananteGen = "";
        int nroAcompanantesMax = (int)(Math.random()*((maxAcompanante-minAcompanante)+1))+minAcompanante;//se define al azar cuando acompañantes se agregaran al menu

        /**
         * genera una cierta cantidad de contorno, en donde la cantidad es definida
         * por el ramdon que se guarda en la variable nroContornosMax
         */
        for (int i = 0; i < nroAcompanantesMax; i++){
            indiceAcompanante = (int)(Math.random()*((tamanoAcompanante-0)+1))+0;
            acompananteGen = acompanante.get(indiceAcompanante);

            /**validamos que el contorno generado en contornoGen no sea igual a unos
             * de los elementos del array contornosGenerados.
             * si el contorno generado es igual, se llamara a la funcion validarContorno
             * hasta que el contorno generado sea diferente*/
            while (validarAcompanante(acompananteGenerados,acompananteGen)){
                indiceAcompanante = (int)(Math.random()*((tamanoAcompanante-0)+1))+0;
                acompananteGen = acompanante.get(indiceAcompanante);
            }
            acompananteGenerados.add(acompananteGen);
        }

        String menuGenerado = alimentoGenerado;

        /**si banderaContorno es igual a 1 entonces se le agrega contorno*/
        if(banderaContorno == 1) {
            menuGenerado += " con "+contorno.get(indiceContorno);
        }

        /**armamos el menu generado*/
        for (int i = 0; i < acompananteGenerados.size(); i++){
            /**cuando el contador es menor a 1 se concatena una coma y se agrega el primer acompañante a menuGenerado*/
            if(i < 1){
                menuGenerado += ", " + acompananteGenerados.get(i);
                /**cuando el contador es mayor a la cantidad de acompañantes generados se procede a colocar o no una ensalada al menuGenerado*/
            }else if (i > acompananteGenerados.size()-2){
                /**si banderaEnsalada es igual a 1 entonces se agrega ensalada al menuGenerado, de lo contrario no*/
                if(banderaEnsalada == 1) {
                    menuGenerado += " y " + ensalada.get(indiceEnsalada);
                    /**si banderaEnsalada no es igual a 1 entonces se le agrega solo el acompanante*/
                }else{
                    menuGenerado += " y " + acompananteGenerados.get(i);
                }
                /**si el contador es mayor a 0 solamente se le agrega un acompañante*/
            }else if (i > 0){
                menuGenerado += ", " + acompananteGenerados.get(i);
            }
        }

        menuGenerado += ".";

        tv1.setText(menuGenerado);
    }

    public boolean validarAcompanante(ArrayList<String> acompananteGenerados, String acompananteGen){
        boolean acompananteRepetido = false;
        for (String valAcompanante : acompananteGenerados) {
            /*si es igual, entonces contornoRepetido es igual a true*/
            if (valAcompanante.equals(acompananteGen)) {
                acompananteRepetido = true;
            }
        }
        return acompananteRepetido;
    }

    //Metodo para el boton anterior
    public void Anterior(View view) {
        Intent anterior = new Intent(this, MainActivity.class);
        startActivity(anterior);
    }
}
