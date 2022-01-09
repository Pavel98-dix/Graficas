package com.example.graficas;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class Graficar extends AppCompatActivity {
    BarChart barChart;
    SQLiteHelper sqlh=null;
    SQLiteDatabase db=null;
    Button btnAlmacenar, btnMostrar;
    LinearLayout llAreapdf;
    Bitmap bitmapChido;
    private File pdfFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graficar);

        barChart=(BarChart) findViewById(R.id.barChart);
        //Crear o utilizar la bd
        sqlh=new SQLiteHelper(getApplicationContext(),"agenda.db",null,
                1);
        btnAlmacenar=(Button)findViewById(R.id.btnAlmacenar);
        btnMostrar=(Button)findViewById(R.id.btnMostrar);
        llAreapdf=(LinearLayout) findViewById(R.id.llAreaPdf);

        btnAlmacenar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bitmapChido = cargarBitmapFromView(llAreapdf,llAreapdf.getWidth(),llAreapdf.getHeight());
                crearPdf();
            }
        });
        btnMostrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarPdf();
            }
        });

        cantidad();
    }




    private static Bitmap cargarBitmapFromView(View llAreaPdf, int width, int height) {
        Bitmap bitmap2= Bitmap.createBitmap(width,height,Bitmap.Config.RGB_565);

        Canvas canvas= new Canvas(bitmap2);
        llAreaPdf.draw(canvas);

        return bitmap2;
    }
    private void crearPdf() {
        DisplayMetrics dm= new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getRealMetrics(dm);

        int ancho= (int) dm.heightPixels;
        int alto=(int) dm.widthPixels;

        PdfDocument document= new PdfDocument();
        PdfDocument.PageInfo info= new PdfDocument.PageInfo.Builder(ancho,alto,1).create();
        PdfDocument.Page page= document.startPage(info);

        Canvas canvas2= page.getCanvas();
        Paint paint= new Paint();

        canvas2.drawPaint(paint);

        bitmapChido= Bitmap.createScaledBitmap(bitmapChido,ancho,alto,true);

        canvas2.drawBitmap(bitmapChido,0,0,null);
        document.finishPage(page);
        File folder = new File(Environment.getExternalStorageDirectory().toString(), "Download");
        pdfFile = new File(folder, "grafica.pdf");


        try {

            document.writeTo(new FileOutputStream(pdfFile));

            document.close();
            Toast.makeText(getApplicationContext(),"Pdf generado",Toast.LENGTH_LONG).show();
        } catch (IOException ex)
        {
            Toast.makeText(getApplicationContext(),"Error"+ex.getMessage().toString(),Toast.LENGTH_SHORT).show();
        }
    }
    private void mostrarPdf()
    {
        File file=new File(Environment.getExternalStorageDirectory(),"Download");
        pdfFile= new File(file,"grafica.pdf");
        Intent target= new Intent(Intent.ACTION_VIEW);
        target.setDataAndType(Uri.fromFile(pdfFile),"application/pdf");
        target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        Intent mostrar= Intent.createChooser(target,"Abrir PDF");
        try {
            startActivity(mostrar);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(getApplicationContext(),"pdf no encontrado",Toast.LENGTH_SHORT).show();
        }
    }

    private void cantidad() {
        db=sqlh.getWritableDatabase();
        Usuario usuario= new Usuario(getApplicationContext(),db);
        Cursor cursor= usuario.Consulta();

        String sexoV,valor="hombre";
        int idU;
        int hombre=0;
        int mujer=0;

        int filas= cursor.getCount();
        cursor.moveToFirst();

        for (int i=0;i<filas;i++)
        {
            idU=cursor.getInt(0);
            sexoV= cursor.getString(4);

            cursor.moveToNext();
            switch (sexoV){
                case "hombre":
                    hombre++;
                    break;
                case "mujer":
                    mujer++;
                    break;
            }
        }
        graficoPastel(hombre,mujer);
        Toast.makeText(getApplicationContext(),"mujer: "+mujer+"  hombre: "+hombre,Toast.LENGTH_LONG).show();

    }



    private void graficoPastel(int hombre, int mujer)
    {
        Description description= new Description();
        description.setText("Grafica de pastel");

        barChart.setDescription(description);

        ArrayList<BarEntry> barEntry=new ArrayList<>();

        barEntry.add(new BarEntry(1,hombre));
        barEntry.add(new BarEntry(2,mujer));


        BarDataSet dataSet= new BarDataSet(barEntry,"Generos");
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);

        BarData data= new BarData(dataSet);
        barChart.setData(data);


    }
}