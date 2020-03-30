package com.diversion.generardia;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class GenerarMenu extends AppCompatActivity {
    private TextView tv1;
    private TextView tvRonda;
    private TextView tvCombRestante;
    private String dato;
    /**
     * lista de alimentos
     */
    ArrayList<Alimento> alimento = new ArrayList<>();
    /**
     * lista de contornos
     */
    ArrayList<Contorno> contorno = new ArrayList<>();
    /**
     * lista de acompañantes
     */
    ArrayList<Acompanante> acompanante = new ArrayList<>();
    /**
     * lista de ensaladas
     */
    ArrayList<Ensalada> ensalada = new ArrayList<>();

    /**
     * combinaciones posibles
     */
    int combPosibles = 0;
    /**
     * alimentos existentes
     */
    int alimExistentes = 0;
    /**
     * alimentos tomados en cada menu
     */
    int alimTomados = 2;
    /**
     * parametro para definir el minimo posible de acompañantes
     */
    int minAcompanante = 2;
    /**
     * parametro para definir el maximo posible de acompañantes
     */
    int maxAcompanante = 4;
    /**
     * codigos de los alimentos a guardar en menus generado bd
     */
    int[] codAlimentoGenerado;
    /**
     * lista de los menus generados almacenados
     */
    ArrayList<int[]> listMenusAlmacenados = new ArrayList<>();
    /**
     * numero de la ronda de menus
     */
    int rondaMenu = 0;
    /**
     * numero de la ronda de menus
     */
    int menuGeneradoEnRonda = 0;

    /*****Variables utilizadas para generar menu(desayuno, almuerzo y cena)****/
    /**
     * corresponde a si se elige contorno o no
     * */
    int banderaContorno = 0;
    /**
     * corresponde a si se elige ensalada o no
     * */
    int banderaEnsalada = 0;
    /**
     * corresponde al indice del alimento escogido
     * */
    int indiceAlimento = 0;
    /**
     * corresponde al indice del contorno escogido
     * */
    int indiceContorno = 0;
    /**
     * corresponde al indice de la ensalada escogido
     * */
    int indiceEnsalada = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generar_menu);

        tv1 = (TextView) findViewById(R.id.textViewGen);
        tvRonda = (TextView) findViewById(R.id.textView4);
        tvCombRestante = (TextView) findViewById(R.id.textView5);
        dato = getIntent().getStringExtra("turnoMenu");
        tv1.setText(dato);

        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "administracion_comida", null, 1);
        SQLiteDatabase BaseDeDatos = admin.getWritableDatabase();

        ArrayList<String> condicion = new ArrayList<>();//arraylist de los tipos de alimentos
        condicion.add("ALIMENTO");
        condicion.add("CONTORNO");
        condicion.add("ACOMPAÑANTE");
        condicion.add("ENSALADA");

        Alimento alimentoObj;//creamos un objeto de la clase alimento
        Contorno contornoObj;//creamos un objeto de la clase contorno
        Acompanante acompananteObj;//creamos un objeto de la clase acompanante
        Ensalada ensaladaObj;//creamos un objeto de la clase ensalada

        /******consultamos la ultima ronda generada*******/
        Cursor fila0 = BaseDeDatos.rawQuery
                ("select max(ronda_menu), count(ronda_menu) from menu_generado where ronda_menu in (select max(ronda_menu) from menu_generado where turno_comida = " + "'" + dato + "'" + ")", null);

        if (fila0.moveToFirst()) {
            String nroRondaMenu = fila0.getString(0);
            String nroMenuGeneradoEnRonda = fila0.getString(1);
            if (nroRondaMenu == null || nroRondaMenu.equals("")) {
                nroRondaMenu = "0";
                nroMenuGeneradoEnRonda = "0";
            }
            rondaMenu = Integer.parseInt(nroRondaMenu);
            menuGeneradoEnRonda = Integer.parseInt(nroMenuGeneradoEnRonda);
        }
        /**fin de la consulta de la ultima ronda generada*/
        tvRonda.setText("Ronda: " + rondaMenu);

        for (int i = 0; i < condicion.size(); i++) {
            /**consultamos los alimentos que existen en bd*/
            Cursor fila = BaseDeDatos.rawQuery
                    ("select cod_comida, turno_comida, tipo_comida, desc_comida from comidas where turno_comida =" + "'" + dato + "'" + " and tipo_comida =" + "'" + condicion.get(i) + "'", null);

            if (fila.moveToFirst()) {
                do {
                    if (i == 0) {
                        alimentoObj = new Alimento();
                        alimentoObj.setCodAlimento(Integer.parseInt(fila.getString(0)));
                        alimentoObj.setDescAlimento(fila.getString(3));
                        alimento.add(alimentoObj);
                    } else if (i == 1) {
                        contornoObj = new Contorno();
                        contornoObj.setCodContorno(Integer.parseInt(fila.getString(0)));
                        contornoObj.setDescContorno(fila.getString(3));
                        contorno.add(contornoObj);
                    } else if (i == 2) {
                        acompananteObj = new Acompanante();
                        acompananteObj.setCodAcompanante(Integer.parseInt(fila.getString(0)));
                        acompananteObj.setDescAcompanante(fila.getString(3));
                        acompanante.add(acompananteObj);
                    } else if (i == 3) {
                        ensaladaObj = new Ensalada();
                        ensaladaObj.setCodEnsalada(Integer.parseInt(fila.getString(0)));
                        ensaladaObj.setDescEnsalada(fila.getString(3));
                        ensalada.add(ensaladaObj);
                    }

                    //if(dato.equals("MERIENDA")){
                    alimExistentes++;//nro de alimentos existentes
                    //}
                } while (fila.moveToNext());
            }
        }

        if (alimExistentes != 0) {
            /**cuando no sea merienda, alimTomados tomara el valor de los acompañantes*/
            if (!dato.equals("MERIENDA")) {
                alimTomados = minAcompanante + 1;//por los momentos se le sumara 1 al minAcompanante, despues deberia ser el promedio o combinados etc...
            }
            /**formula para calcular combinaciones sin repeticiones*/
            combPosibles = factorial(alimExistentes) / (factorial(alimTomados) * factorial(alimExistentes - alimTomados));
            /**********************************************/
            /**zona para calcular combinacion posibles con rangos de alimentos tomados. Por los momentos esta como si alimentos tomados tuviera un nro fijo*/
            /**********************************************/
        } else if (alimTomados > alimExistentes) {
            Toast.makeText(this, "Los alimentos existentes son menores a los alimentos a tomar", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "No tienes alimentos registrados", Toast.LENGTH_SHORT).show();
        }
        int combRestantes = combPosibles - menuGeneradoEnRonda;//variable sera usada para almacenar el nro de combos de menus restantes
        tvCombRestante.setText("Comb. Restantes: " + combRestantes);
    }

    public void GenerarMenu(View view) {
        String menuGenerado = "";//variable para almacenar la descripcion del menu generado
        String alimentoGenerado = "";//variable para almacenar la descripcion del alimento generado

        /*****************consultamos los menus generados*****************/
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "administracion_comida", null, 1);
        SQLiteDatabase BaseDeDatos = admin.getWritableDatabase();
        /**consultamos los alimentos generados que componen a todos los menus de la ultima rondaMenu*/
        Cursor fila2 = BaseDeDatos.rawQuery
                ("select cod_menu_generado, items_menu, fecha_registro from menu_generado where turno_comida = " + "'" + dato + "'" + " and ronda_menu = " + "'" + rondaMenu + "'", null);

        String[] arregloItemString;//creamos un array de string
        int[] arregloItemMenu;//creamos un array de int
        listMenusAlmacenados.clear();

        if (fila2.moveToFirst()) {
            do {
                arregloItemString = fila2.getString(1).split(",", 0);//convertimos a un array de string en donde el delimitador es una coma
                arregloItemMenu = new int[arregloItemString.length];//declaramos un array de int en donde la longitud es igual al arregloitemstring

                for (int i = 0; i < arregloItemMenu.length; i++) {
                    arregloItemMenu[i] = Integer.parseInt(arregloItemString[i]);//agregamos los codigos de los alimentos al array de tipo int
                }
                listMenusAlmacenados.add(arregloItemMenu);//agregamos el array(lista de alimentos que compone al menu) a un arraylist(lista de todos los menus) de array de tipo int
            } while (fila2.moveToNext());
        }
        /**************fin de consultar los menus generados**************/

        if (!dato.equals("MERIENDA")) {//si no es igual a MERIENDA
            if(acompanante.size() >= maxAcompanante || alimento.size() > 0 || contorno.size() > 0 || ensalada.size() > 0) {
                ArrayList<String> acompananteGenerados = generarMenuBasico();
                codAlimentoGenerado = burbuja(codAlimentoGenerado);

                /**REVISAR ESTA SECCION*/
                if (listMenusAlmacenados.size() > 0 && listMenusAlmacenados.size() < combPosibles) {//evaluamos si los menus almacenados es mayor a cero; si ya previamente se guardo un menu
                    while (validarMenuExisteBd()) {//validamos hasta si el menu existe en bd
                        acompananteGenerados = generarMenuBasico();//generamos otro menu basico
                        codAlimentoGenerado = burbuja(codAlimentoGenerado);//ordenamos los codigos de los alimentos generados
                    }
                } else if (listMenusAlmacenados.size() >= combPosibles) {//evaluamos si los menus almacenados es mayor a la combinaciones posibles
                    rondaMenu++;//le sumamos uno a rondaMenu; el menu generado pertenecera a otra ronda, se almacena en la columna ronda_menu en bd
                }

                /********Armamos el menu generado, para mostrar en pantalla********/
                menuGenerado = alimento.get(indiceAlimento).getDescAlimento().trim();//se almacena el alimento elegido
                /**si banderaContorno es igual a 1 entonces se le agrega contorno*/
                if (banderaContorno == 1) {
                    menuGenerado += " CON " + contorno.get(indiceContorno).getDescContorno();
                }

                for (int i = 0; i < acompananteGenerados.size(); i++) {
                    /**cuando el contador es menor a 1 se concatena una coma y se agrega el primer acompañante a menuGenerado; el primer acompañante*/
                    if (i < 1) {
                        menuGenerado += ", " + acompananteGenerados.get(i);
                        /**cuando el contador es mayor a la cantidad de acompañantes generados(es decir, si ya se agregaron todos los acompañantes) se procede a colocar o no una ensalada al menuGenerado*/
                    } else if (i > acompananteGenerados.size() - 2) {
                        /**si banderaEnsalada es igual a 1 entonces se agrega ensalada al menuGenerado, de lo contrario no*/
                        if (banderaEnsalada == 1) {
                            menuGenerado += ", " + acompananteGenerados.get(i) + " Y " + ensalada.get(indiceEnsalada).getDescEnsalada();
                            /**si banderaEnsalada no es igual a 1 entonces se le agrega solo el acompanante*/
                        } else {
                            menuGenerado += " Y " + acompananteGenerados.get(i);
                        }
                        /**si el contador es mayor a 0 solamente se le agrega un acompañante, los demas acompañantes*/
                    } else if (i > 0) {
                        menuGenerado += ", " + acompananteGenerados.get(i);
                    }
                }
                /********fin de armar el menu generado, para mostrar en pantalla*******/
            } else {
                Toast.makeText(this, "Insuficientes alimentos registrados para armar el menú", Toast.LENGTH_SHORT).show();
                menuGenerado += dato.toUpperCase();
            }
        } else {//cuando sea igual a MERIENDA
            /**validamos si los alimentos a tomar es menor o igual a los alimentos existentes para poder generar el menu*/
            if (alimTomados <= alimExistentes) {
                ArrayList<String> alimentosGenerados = new ArrayList<>();//array para almacenar los nombres de los alimentos que componen al menu

                alimentosGenerados = generarMenuMerienda();
                codAlimentoGenerado = burbuja(codAlimentoGenerado);

                if (listMenusAlmacenados.size() > 0 && listMenusAlmacenados.size() < combPosibles) {//evaluamos si los menus almacenados es mayor a cero; si ya previamente se guardo un menu
                    while (validarMenuExisteBd()) {//validamos hasta si el menu existe en bd
                        alimentosGenerados = generarMenuMerienda();//generamos otro menu
                        codAlimentoGenerado = burbuja(codAlimentoGenerado);//ordenamos los codigos de los alimentos generados
                    }
                } else if (listMenusAlmacenados.size() >= combPosibles) {//evaluamos si los menus almacenados es mayor a la combinaciones posibles
                    rondaMenu++;//le sumamos uno a rondaMenu; el menu generado pertenecera a otra ronda, se almacena en la columna ronda_menu en bd
                }

                /*****armamos el menu generado (merienda)*****/
                for (int i = 0; i < alimentosGenerados.size(); i++) {
                    /**cuando el contador es menor a 1 se agrega el alimento al menuGenerado*/
                    if (i < 1) {
                        menuGenerado += alimentosGenerados.get(i);
                    } else if (i > 0) {
                        if (i >= alimentosGenerados.size() - 1) {
                            menuGenerado += " y " + alimentosGenerados.get(i);
                        } else {
                            menuGenerado += ", " + alimentosGenerados.get(i);
                        }
                    }
                }
                /**fin de armamos el menu generado (merienda)*/
            } else {
                Toast.makeText(this, "Los alimentos existentes son menores a los alimentos a tomar", Toast.LENGTH_SHORT).show();
                menuGenerado += "MERIENDA";
            }
        }
        //menuGenerado += ".";
        tv1.setText(menuGenerado);
    }

    /**
     * funcion para validar si el alimento generado esta repetido
     */
    public boolean validarAlimento(ArrayList<String> alimentoGenerados, String alimentoGen) {
        boolean alimentoRepetido = false;
        for (String valAlimento : alimentoGenerados) {
            /*si es igual, entonces contornoRepetido es igual a true*/
            if (valAlimento.equals(alimentoGen)) {
                alimentoRepetido = true;
            }
        }
        return alimentoRepetido;
    }

    public boolean validarAcompanante(ArrayList<String> acompananteGenerados, String acompananteGen) {
        boolean acompananteRepetido = false;
        for (String valAcompanante : acompananteGenerados) {
            /*si es igual, entonces contornoRepetido es igual a true*/
            if (valAcompanante.equals(acompananteGen)) {
                acompananteRepetido = true;
            }
        }
        return acompananteRepetido;
    }

    public boolean validarMenuExisteBd() {
        Boolean menuRepetido = false;
        for (int[] valMenu : listMenusAlmacenados) {//recorro los menus almacenados en bd
            /*si es igual, entonces menuRepetido es igual a true*/
            if (Arrays.equals(codAlimentoGenerado, valMenu)) {//evaluo la igualdad entre los dos arrays
                menuRepetido = true;
                break;
            }
        }
        return menuRepetido;
    }

    /**
     * funcion para generar el menu de basico(desayuno,almuerzo y cena) con la lista de alimentos
     */
    public ArrayList<String> generarMenuBasico() {
        /********En esta seccion se establece al azar todos los alimentos que compondra al menu*******/
        int longitudCodAlimento = 0;//variable para saber cuantos alimentos componen el menu

        int tamanoAlimento = alimento.size() - 1;//tamaño del array alimento menos 1
        indiceAlimento = (int) (Math.random() * ((tamanoAlimento - 0) + 1)) + 0;//se elige un alimento al azar
        //alimentoGenerado = alimento.get(indiceAlimento).getDescAlimento();//se almacena el alimento elegido
        longitudCodAlimento++;//contamos el alimento

        banderaContorno = (int) (Math.random() * ((1 - 0) + 1)) + 0;//se decide al azar si se agrega o no un contorno
        int tamanoContorno = contorno.size() - 1;//tamaño del array contorno menos 1
        indiceContorno = (int) (Math.random() * ((tamanoContorno - 0) + 1)) + 0;//se elige un contorno al azar
        if (banderaContorno == 1) {//validamos si el contorno se agrega al menu para contarlo
            longitudCodAlimento++;//agregamos el contorno
        }

        banderaEnsalada = (int) (Math.random() * ((1 - 0) + 1)) + 0;//variable para almacenar un indicador si se agrega o no la ensalada
        indiceEnsalada = 0;
        if (!dato.equals("DESAYUNO")) {//cuando no sea desayuno, el desayuno no lleva ensalada normalmente
            if (banderaEnsalada == 1) {//validamos si la ensalada se agrega al menu para contarla
                int tamanoEnsalada = ensalada.size() - 1;//tamaño del array ensalada menos 1
                indiceEnsalada = (int) (Math.random() * ((tamanoEnsalada - 0) + 1)) + 0;//se elige una ensalada al azar
                longitudCodAlimento++;//contamos la ensalada
            }
        }

        int tamanoAcompanante = acompanante.size() - 1;
        ArrayList<String> acompananteGenerados = new ArrayList<>();
        ArrayList<Integer> acompananteGeneradosCod = new ArrayList<>();
        int indiceAcompanante = 0;
        String acompananteGen = "";
        int nroAcompanantesMax = (int) (Math.random() * ((maxAcompanante - minAcompanante) + 1)) + minAcompanante;//se define al azar cuantos acompañantes se agregaran al menu
        longitudCodAlimento += nroAcompanantesMax;//contamos los acompañantes

        /**genera una cierta cantidad de contorno, en donde la cantidad es definida por el random que se guarda en la variable nroContornosMax*/
        for (int i = 0; i < nroAcompanantesMax; i++) {
            indiceAcompanante = (int) (Math.random() * ((tamanoAcompanante - 0) + 1)) + 0;
            acompananteGen = acompanante.get(indiceAcompanante).getDescAcompanante();

            /**validamos que el contorno generado en contornoGen no sea igual a unos de los elementos del array contornosGenerados.
             * si el contorno generado es igual, se llamara a la funcion validarContorno hasta que el contorno generado sea diferente*/
            while (validarAcompanante(acompananteGenerados, acompananteGen)) {
                indiceAcompanante = (int) (Math.random() * ((tamanoAcompanante - 0) + 1)) + 0;
                acompananteGen = acompanante.get(indiceAcompanante).getDescAcompanante();
            }
            acompananteGenerados.add(acompananteGen);//agregamos el acompañante en tipo text al arraylist
            acompananteGeneradosCod.add(acompanante.get(indiceAcompanante).getCodAcompanante());//agregamos el acompañante en tipo integer(cod) al arraylist
        }
        /**fin de generar una cierta cantidad de contorno, en donde la cantidad es definida por el random que se guarda en la variable nroContornosMax*/
        codAlimentoGenerado = null;//limpiamos la variable global
        codAlimentoGenerado = new int[longitudCodAlimento];//establecemos la longitud del array
        longitudCodAlimento = 0;//establecemos en 0
        codAlimentoGenerado[longitudCodAlimento++] = alimento.get(indiceAlimento).getCodAlimento();
        if(banderaContorno == 1) {//si se va a agregar contorno
            codAlimentoGenerado[longitudCodAlimento++] = contorno.get(indiceContorno).getCodContorno();
        }
        if(banderaEnsalada == 1){//si se va a agregar ensalada
            codAlimentoGenerado[longitudCodAlimento++] = ensalada.get(indiceEnsalada).getCodEnsalada();
        }//revisar esta condicion

        for (int i = 0 ; i < acompananteGeneradosCod.size(); i++){
            codAlimentoGenerado[longitudCodAlimento++] = acompananteGeneradosCod.get(i);
        }
        /********Fin de la seccion donde se establece al azar todos los alimentos que compondra al menu*******/
        return acompananteGenerados;
    }

    /**
     * funcion para generar el menu de merienda con la lista de alimentos
     */
    public ArrayList<String> generarMenuMerienda() {
        codAlimentoGenerado = null;//limpiamos la variable global
        codAlimentoGenerado = new int[alimTomados];
        int indiceAlimento = 0;
        int tamanoAlimento = alimento.size() - 1;//tamaño del array alimento menos 1
        String alimentoGenerado = "";
        ArrayList<String> alimentosGenerados = new ArrayList<>();

        for (int i = 0; i < alimTomados; i++) {
            indiceAlimento = (int) (Math.random() * ((tamanoAlimento - 0) + 1)) + 0;
            alimentoGenerado = alimento.get(indiceAlimento).getDescAlimento().trim();

            while (validarAlimento(alimentosGenerados, alimentoGenerado)) {
                indiceAlimento = (int) (Math.random() * ((tamanoAlimento - 0) + 1)) + 0;
                alimentoGenerado = alimento.get(indiceAlimento).getDescAlimento().trim();
            }
            alimentosGenerados.add(alimentoGenerado);//nombre de los alimentos generados para mostrar en la vista
            codAlimentoGenerado[i] = alimento.get(indiceAlimento).getCodAlimento();//codigos de los alimentos generados para guardar en bd
        }
        return alimentosGenerados;
    }

    public int factorial(int numero) {
        if (numero == 0)
            return 1;
        else
            return numero * factorial(numero - 1);
    }

    public void GuardarMenu(View view) {
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "administracion_comida", null, 1);
        SQLiteDatabase BaseDeDatos = admin.getWritableDatabase();

        if (codAlimentoGenerado != null) {
            ContentValues registro = new ContentValues();
            Date fecha = new Date();
            String codAlimentoText = "";
            for (int val : codAlimentoGenerado) {//recorremos los valores del array
                codAlimentoText += val + ",";//concatenamos los valores en una variable separado por coma
            }
            codAlimentoText = codAlimentoText.substring(0, codAlimentoText.length() - 1);//eliminamos el ultimo caracter, la coma
            registro.put("items_menu", codAlimentoText);
            registro.put("ronda_menu", rondaMenu);
            registro.put("fecha_registro", fecha.toString());
            registro.put("turno_comida", dato);

            BaseDeDatos.insert("menu_generado", null, registro);

            /*******************************/
            Cursor fila = BaseDeDatos.rawQuery
                    ("select cod_menu_generado, items_menu, ronda_menu, turno_comida, fecha_registro from menu_generado", null);

            ArrayList<String> listMenus = new ArrayList<>();
            if (fila.moveToFirst()) {
                do {
                    listMenus.add(fila.getString(0) + " - " + fila.getString(1) + " - " + fila.getString(2) + " - " + fila.getString(3) + " - " + fila.getString(4));
                } while (fila.moveToNext());
            }
            /*******************************/

            BaseDeDatos.close();
            tv1.setText("MERIENDA");

            finish();
            startActivity(getIntent());

            Toast.makeText(this, "Registro Exitoso", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "No has averiguado lo que quieres!", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * ordenar numeros (metodo burbuja)
     */
    public static int[] burbuja(int[] arreglo) {
        int auxiliar;
        int[] arregloOrdenado;
        for (int i = 1; i < arreglo.length; i++) {
            for (int j = 0; j < arreglo.length - i; j++) {
                if (arreglo[j] > arreglo[j + 1]) {
                    auxiliar = arreglo[j];
                    arreglo[j] = arreglo[j + 1];
                    arreglo[j + 1] = auxiliar;
                }
            }
        }
        arregloOrdenado = arreglo;
        return arregloOrdenado;
    }

    //Metodo para el boton anterior
    public void Anterior(View view) {
        Intent anterior = new Intent(this, MainActivity.class);
        startActivity(anterior);
    }
}