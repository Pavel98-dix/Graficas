package com.example.graficas;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

public class Usuario {
    SQLiteDatabase db=null;
    Context context=null;
    String  tableName="usuario";

    public Usuario(Context context, SQLiteDatabase db)
    {
        this.context=context;
        this.db=db;
    }
    public void Nuevo( String nombre, String direccion, String telefono,String sexo)
    {
        ContentValues valores= new ContentValues();

        valores.put("nombre",nombre);
        valores.put("direccion",direccion);
        valores.put("telefono",telefono);
        valores.put("sexo",sexo);
        db.insert(tableName,null,valores);
        Toast.makeText(context,"Usuario Registrado",Toast.LENGTH_SHORT).show();
    }
    public Cursor Consulta()
    {
        Cursor cursor=db.query(tableName,null,null,null,
                null,null,null);
        return  cursor;
    }



}
