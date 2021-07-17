package com.example.pm2e2grupo5;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class Actualizar extends AppCompatActivity {

    ImageView imageView;
    EditText nombre, numero;
    TextView tvlatitud, tvlongitud,mensaje;

    static final int RESULT_GALLERY_IMG = 100;
    Bitmap photo = null;

    public static String latitud = "";
    public static String longitud = "";

    public static String id;
    public static String nombre1;
    public static String url;
    public static String telefono;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actualizar);

        getSupportActionBar().hide();

        imageView =findViewById(R.id.ivactu);
        nombre = findViewById(R.id.txtNombre);
        numero = findViewById(R.id.txtNumero);
        tvlatitud = findViewById(R.id.txtlatitud);
        tvlongitud = findViewById(R.id.txtlongitud);
        mensaje = findViewById(R.id.mensaje);


       // Intent intent = getIntent();
        //id = intent.getStringExtra("id1");
       // nombre1 = intent.getStringExtra("nombre1");
      // url = intent.getStringExtra("url1");
        //telefono = intent.getStringExtra("telefono1");
        //latitud = intent.getStringExtra("latitud1");
        //longitud = intent.getStringExtra("longitud1");

        Picasso.with(getApplicationContext()).load(url).into(imageView);
        nombre.setText(nombre1+"");
        numero.setText(telefono);
        tvlatitud.setText(latitud);
        tvlongitud.setText(longitud);
        mensaje.setText("");

        findViewById(R.id.btnImage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GaleriaImagenes();

            }
        });

        findViewById(R.id.btnsalvar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (photo != null) {


                    if (nombre.getText().toString().trim().length() == 0) {
                        nombre.setError("Este campo es obligatorio");

                    } else {
                        nombre.setError(null);
                        if (numero.getText().toString().trim().length() == 0) {
                            numero.setError("Este campo es obligatorio");
                        } else {
                            numero.setError(null);
                            if (numero.getText().toString().length() >= 11) {
                                numero.setError("No se puede poner mayor de 11 cracteres");
                            } else {
                                numero.setError(null);
                                Actualizar();
                            }
                        }

                    }


                } else {
                    if (nombre.getText().toString().trim().length() == 0) {
                        nombre.setError("Este campo es obligatorio");
                    } else {
                        nombre.setError(null);
                    }

                    if (numero.getText().toString().trim().length() == 0) {
                        numero.setError("Este campo es obligatorio");
                    } else {
                        numero.setError(null);
                    }

                    if (numero.getText().toString().length()  >=  11 ) {
                        numero.setError("No se puede poner mayor de 8 cracteres");
                    } else {
                        numero.setError(null);
                    }


                    AlertaDialogo("Seleccione la imagen por favor", "Imagen no seleccionada");
                }
            }
        });


    }

    private void Actualizar()
    {

        String url = "http://167.99.158.191/examen/api/actualizar_usuario.php";

        HashMap<String, String> params = new HashMap<String, String>();



        String image = GetStringImage(photo);
        params.put("id_usuario", id);
        params.put("nombre", nombre.getText().toString().toLowerCase());
        params.put("telefono", numero.getText().toString());
        params.put("latitud", latitud);
        params.put("longitud", longitud);
        params.put("url_foto", image);

        mensaje.setText("Espere un momento mientras se modifica...");

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(getApplicationContext(),"Se ha Modificado exitosamente",Toast.LENGTH_SHORT).show();

                        //Toast.makeText(getApplicationContext(),"Se ha Ingresado Exitosamente",Toast.LENGTH_SHORT).show();
                        Intent list = new Intent(getApplicationContext(),Listado.class);
                        startActivity(list);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("Error", "Error: " + error.getMessage());
                AlertaDialogo("Error al modificar "+error.getMessage(),"Error");
            }
        });


        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(jsonObjectRequest);


    }

    private void GaleriaImagenes()
    {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(photoPickerIntent, RESULT_GALLERY_IMG);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Uri imageUri;

        if (resultCode == RESULT_OK && requestCode == RESULT_GALLERY_IMG)
        {
            imageUri = data.getData();
            imageView.setImageURI(imageUri);

            try
            {
                photo = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
            }
            catch (Exception ex)
            {}
        }
    }
    private String GetStringImage(Bitmap imagen)
    {
        ByteArrayOutputStream ba = new ByteArrayOutputStream();
        imagen.compress(Bitmap.CompressFormat.JPEG,100, ba);
        byte[] imagebyte = ba.toByteArray();
        String encode = Base64.encodeToString(imagebyte, Base64.DEFAULT);
        return "data:image/png;base64,"+encode;
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

    public static String getLatitud() {
        return latitud;
    }

    public static void setLatitud(String latitud) {
        Actualizar.latitud = latitud;
    }

    public static String getLongitud() {
        return longitud;
    }

    public static void setLongitud(String longitud) {
        Actualizar.longitud = longitud;
    }

    public static String getId() {
        return id;
    }

    public static void setId(String id) {
        Actualizar.id = id;
    }

    public static String getNombre1() {
        return nombre1;
    }

    public static void setNombre1(String nombre1) {
        Actualizar.nombre1 = nombre1;
    }

    public static String getUrl() {
        return url;
    }

    public static void setUrl(String url) {
        Actualizar.url = url;
    }

    public static String getTelefono() {
        return telefono;
    }

    public static void setTelefono(String telefono) {
        Actualizar.telefono = telefono;
    }


///



}