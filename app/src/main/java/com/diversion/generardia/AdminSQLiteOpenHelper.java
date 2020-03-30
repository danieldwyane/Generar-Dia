package com.diversion.generardia;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class AdminSQLiteOpenHelper extends SQLiteOpenHelper{
    public AdminSQLiteOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase BaseDeDatos) {
        BaseDeDatos.execSQL("create table ropas(codigo INTEGER primary key, tipo_ropa text, descripcion text, precio real)");
        BaseDeDatos.execSQL("create table comidas(cod_comida INTEGER primary key AUTOINCREMENT, turno_comida text, tipo_comida text, desc_comida text)");
        BaseDeDatos.execSQL("create table menu_generado(cod_menu_generado INTEGER primary key AUTOINCREMENT, items_menu text, ronda_menu INTEGER, turno_comida text, fecha_registro date)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
