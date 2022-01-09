package com.example.graficas;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity  {
    EditText etNombre,etDireccion,etTelefono;
    TextView tvConsulta;
    ListView lvConsulta;
    Button consultar,almacenar,mostrar;
    SQLiteHelper sqlh=null;
    SQLiteDatabase db=null;
    ArrayList<String> listaInformacion;
    ArrayList<String> listaUsuario;
    ArrayAdapter adapter;

    Spinner SpSexo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Vinculamos al spinner
        SpSexo =(Spinner) findViewById(R.id.SpSexo);
        String [] opciones={"hombre","mujer"};
        ArrayAdapter <String> adapter1=new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,opciones);
        SpSexo.setAdapter(adapter1);

        etNombre=(EditText)findViewById(R.id.etNombre);
        etDireccion=(EditText)findViewById(R.id.etDireccion);
        etTelefono=(EditText)findViewById(R.id.etTelefono);

        consultar=(Button)findViewById(R.id.consultar);
        almacenar=(Button) findViewById(R.id.almacenar);
        mostrar=(Button) findViewById(R.id.btnGrafica);

        tvConsulta=(TextView) findViewById(R.id.tvConsulta);
            lvConsulta=(ListView)findViewById(R.id.lvConsulta);
        sqlh=new SQLiteHelper(getApplicationContext(),"agenda.db",null,
                1);

        almacenar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NuevoUsuario();
            }
        });
        consultar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ConsultarUsuario();
            }
        });
        mostrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(getApplicationContext(),Graficar.class);
                startActivity(intent);
            }
        });
        comprobarPermisos();
    }
    private void NuevoUsuario()
    {
        String nombre=etNombre.getText().toString();
        String direccion=etDireccion.getText().toString();
        String telefono=etTelefono.getText().toString();
        String sexo= SpSexo.getSelectedItem().toString();

        //Crear o utilizar la bd
        db=sqlh.getWritableDatabase();
        //Instacioamos a usuario
        Usuario usuario= new Usuario(getApplicationContext(),db);
        usuario.Nuevo(nombre,direccion,telefono,sexo);
        db.close();
    }

    private void ConsultarUsuario()
    {

        db=sqlh.getWritableDatabase();

        Usuario usuario= new Usuario(getApplicationContext(),db);
        Cursor cursor= usuario.Consulta();
        listaUsuario= new ArrayList<String>();

        //Recorremos el cursor
        tvConsulta.setText("");
        String nombre,direccion,telefono,sexoV;
        int idU;

        int filas= cursor.getCount();
        cursor.moveToFirst();

        for (int i=0;i<filas;i++)
        {
            idU=cursor.getInt(0);
            nombre=cursor.getString(1);
            direccion=cursor.getString(2);
            telefono=cursor.getString(3);
            sexoV= cursor.getString(4);
            cursor.moveToNext();
            listaUsuario.add("\n"+idU+", "+nombre+", "+direccion+", "+telefono+", "+sexoV);
            tvConsulta.append("\n"+idU+", "+nombre+", "+direccion+", "+telefono+", "+sexoV);

        }
        obtenerLista();
        db.close();
    }

    private void obtenerLista()
    {
        listaInformacion= new ArrayList<String>();
        for (int i=0;i<listaUsuario.size();i++)
        {
            listaInformacion.add(listaUsuario.get(i));
            adapter= new ArrayAdapter(this,android.R.layout.simple_list_item_1,listaUsuario);
            lvConsulta.setAdapter(adapter);
        }



    }
    private  void comprobarPermisos()
    {
        List<String> permisos= new ArrayList<String>();


        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)
            permisos.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            permisos.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permisos.size()>0)
            requestPermissions(permisos.toArray(new String[permisos.size()]),124);





    }



}