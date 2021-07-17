package com.example.pm2e2grupo5;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.HashMap;

public class SegundoActividad extends AppCompatActivity {

    String id ;
    String nombre;
    String url ;
    String latitud ;
    String longitud;
    String telefono;

    public static int resultado = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_segundo_actividad);

        getSupportActionBar().hide();

        ImageView imagenSecond = (ImageView) findViewById(R.id.imageView2);
        TextView caja = (TextView) findViewById(R.id.caja);

        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        nombre = intent.getStringExtra("nombre");
        url = intent.getStringExtra("url");
        telefono = intent.getStringExtra("telefono");

        latitud = intent.getStringExtra("latitud");
        longitud = intent.getStringExtra("longitud");

        String texto = "Informacion personal\n\n"+
                "ID: "+id + "\n"+
                "Nombre: "+nombre + "\n"+
                "Telefono: "+telefono + "\n"+
                "Latitud: "+latitud + "\n"+
                "Longitud: "+longitud + "\n";

        caja.setText(texto);

        Picasso.with(getApplicationContext()).load(url).into(imagenSecond);

        findViewById(R.id.btnEliminar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Eliminar();

            }
        });

        findViewById(R.id.btnActu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Actualizar.class);


                Actualizar.setId(id);
                Actualizar.setLatitud(latitud);
                Actualizar.setLongitud(longitud);
                Actualizar.setNombre1(nombre);
                Actualizar.setTelefono(telefono);
                Actualizar.setUrl(url);



                startActivityForResult(intent,resultado);
            }
        });

        findViewById(R.id.btnUbicacion).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent list = new Intent(getApplicationContext(),MapaActividad.class);
                MapaActividad.setLatitud_map(Double.parseDouble(latitud));
                MapaActividad.setLongitud_map(Double.parseDouble(longitud));
                startActivity(list);
            }
        });

        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String geoUri = "http://maps.google.com/maps?q=loc:" + latitud + "," + longitud + " (" + "Destino" + ")";
                Uri location = Uri.parse(geoUri);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, location);
                startActivity(mapIntent);
            }
        });

    }


    private void AlertaDialogo(String mensaje, String title){

        AlertDialog.Builder alerta = new AlertDialog.Builder(this);
        alerta.setMessage(mensaje)
                .setCancelable(false)
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        AlertDialog titulo = alerta.create();
        titulo.setTitle(title);
        titulo.show();

    }

    private void Eliminar()
    {

        String url = "http://167.99.158.191/examen/api/eliminar_usuario.php";

        HashMap<String, String> params = new HashMap<String, String>();


        params.put("id_usuario", id);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(getApplicationContext(),"Se ha Eliminado exitosamente",Toast.LENGTH_SHORT).show();
                        Intent list = new Intent(getApplicationContext(),Listado.class);
                        startActivity(list);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("Error", "Error: " + error.getMessage());
             }
        });


        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(jsonObjectRequest);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == resultado && requestCode == RESULT_OK) {


        }
    }



}