package com.diversion.generardia;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class RegistrarComida extends AppCompatActivity implements OnItemSelectedListener{

    private Spinner spinner1;
    private Spinner spinner2;
    private EditText et_descripcion_comida;
    private TextView et_consulta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_comida);

        spinner1 = (Spinner)findViewById(R.id.spinner);
        spinner2 = (Spinner)findViewById(R.id.spinner2);
        et_descripcion_comida = (EditText)findViewById(R.id.txt_desc_comida);
        et_consulta = (TextView) findViewById(R.id.textView2);

        String [] opcionesSpinner1 = {"DESAYUNO","ALMUERZO","CENA","MERIENDA"};
        final String[][] opcionesSpinner2 = {{}};

        spinner1.setOnItemSelectedListener(this);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_item_tipo_ropa, opcionesSpinner1);
        spinner1.setAdapter(adapter);

  /*      ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this, R.layout.spinner_item_tipo_ropa, opcionesSpinner2[0]);
        spinner2.setAdapter(adapter2);*/

        et_descripcion_comida.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
    }

    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
                               long arg3) {
        // TODO Auto-generated method stub
        String sp1= String.valueOf(spinner1.getSelectedItem());
        Toast.makeText(this, sp1, Toast.LENGTH_SHORT).show();
        if(sp1.contentEquals("DESAYUNO")) {
            List<String> list = new ArrayList<String>();
            list.add("ALIMENTO");
            list.add("CONTORNO");
            list.add("ACOMPAÑANTE");
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                    R.layout.spinner_item_tipo_ropa, list);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            dataAdapter.notifyDataSetChanged();
            spinner2.setAdapter(dataAdapter);
        }
        if(sp1.contentEquals("ALMUERZO") || sp1.contentEquals("CENA")) {
            List<String> list = new ArrayList<String>();
            list.add("ALIMENTO");
            list.add("CONTORNO");
            list.add("ACOMPAÑANTE");
            list.add("ENSALADA");
            ArrayAdapter<String> dataAdapter2 = new ArrayAdapter<String>(this,
                    R.layout.spinner_item_tipo_ropa, list);
            dataAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            dataAdapter2.notifyDataSetChanged();
            spinner2.setAdapter(dataAdapter2);
        }
        if(sp1.contentEquals("MERIENDA")) {
            List<String> list = new ArrayList<String>();
            list.add("ALIMENTO");
            ArrayAdapter<String> dataAdapter2 = new ArrayAdapter<String>(this,
                    R.layout.spinner_item_tipo_ropa, list);
            dataAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            dataAdapter2.notifyDataSetChanged();
            spinner2.setAdapter(dataAdapter2);
        }

    }
    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub

    }

    //Metodo para darle alta a la comida
    public void Registrar(View view){
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "administracion_comida", null, 1);
        SQLiteDatabase BaseDeDatos = admin.getWritableDatabase();

        String turno_comida = spinner1.getSelectedItem().toString();
        String tipo_comida = spinner2.getSelectedItem().toString();
        String descripcion_comida = et_descripcion_comida.getText().toString();

        if(!turno_comida.isEmpty() && !tipo_comida.isEmpty() && !descripcion_comida.isEmpty()){
            ContentValues registro = new ContentValues();
            registro.put("turno_comida", turno_comida);
            registro.put("tipo_comida", tipo_comida);
            registro.put("desc_comida", descripcion_comida);

            BaseDeDatos.insert("comidas", null, registro);

            BaseDeDatos.close();
            spinner1.clearFocus();
            spinner2.clearFocus();
            et_descripcion_comida.setText("");

            Toast.makeText(this, "Registro Exitoso", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "Debes llenar todos los campos", Toast.LENGTH_SHORT).show();
        }
    }

    //Metodo para consultar una comida
    public void Buscar(View view){
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this,"administracion_comida", null, 1);
        SQLiteDatabase BaseDeDatos = admin.getWritableDatabase();

        String codigo = et_descripcion_comida.getText().toString();

        if(!codigo.isEmpty()){
            Cursor fila = BaseDeDatos.rawQuery
                    ("select cod_comida, turno_comida, tipo_comida, desc_comida from comidas where cod_comida ="+codigo, null);

            if(fila.moveToFirst()){
                for(int i= 0; i < spinner1.getAdapter().getCount(); i++)
                {
                    if(spinner1.getAdapter().getItem(i).toString().contains(fila.getString(1)))
                    {
                        spinner1.setSelection(i);
                    }
                }

                String comida_reg = fila.getString(0) +" "+ fila.getString(1) +" "+ fila.getString(2) +" "+ fila.getString(3);
                et_consulta.setText(comida_reg);
                BaseDeDatos.close();
            }else{
                Toast.makeText(this, "No existe la comida", Toast.LENGTH_SHORT).show();
                BaseDeDatos.close();
            }
        }else{
            Toast.makeText(this, "Debes introducir el codigo de la comida", Toast.LENGTH_SHORT).show();
        }
    }

    //Metodo para eliminar una comida
    public void Eliminar(View view){
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper
                (this, "administracion_comida", null, 1);
        SQLiteDatabase BaseDeDatos = admin.getWritableDatabase();

        String codigo = et_descripcion_comida.getText().toString();

        if(!codigo.isEmpty()){
            int cantidad = BaseDeDatos.delete("comidas", "cod_comida=" + codigo, null);
            BaseDeDatos.close();

            et_descripcion_comida.setText("");

            if(cantidad == 1){
                Toast.makeText(this, "Artículo eliminado exitosamente", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this, "El artículo no existe", Toast.LENGTH_SHORT).show();
            }

        }else{
            Toast.makeText(this, "Debes introducir el código del artículo", Toast.LENGTH_SHORT).show();
        }
    }

    //Metodo para actualizar una comida
    public void Modificar(View view){
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "administracion_comida", null, 1);
        SQLiteDatabase BaseDeDatos = admin.getWritableDatabase();

        String desc_comida = et_descripcion_comida.getText().toString();

        if(!desc_comida.isEmpty() && !desc_comida.isEmpty() && !desc_comida.isEmpty()){
            ContentValues registro = new ContentValues();
            registro.put("desc_comida", desc_comida);

            int cantidad = BaseDeDatos.update("comidas", registro, "cod_comida="+desc_comida, null);
            BaseDeDatos.close();

            if(cantidad == 1){
                Toast.makeText(this, "Artículo modificado correctamente", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this, "El artículo no existe", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(this, "Debes llenar todos los campos", Toast.LENGTH_SHORT).show();
        }
    }
}