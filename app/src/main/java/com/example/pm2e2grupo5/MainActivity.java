package com.example.pm2e2grupo5;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    static final int RESULT_GALLERY_IMG = 100;
    Bitmap photo = null;
    ImageView imageView;
    EditText nombre, numero;
    TextView tvlatitud, tvlongitud;
    String latitud = "";
    String longitud = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView =findViewById(R.id.imageView);
        nombre = findViewById(R.id.txtNombre);
        numero = findViewById(R.id.txtNumero);
        tvlatitud = findViewById(R.id.txtlatitud);
        tvlongitud = findViewById(R.id.txtlongitud);


        //permisosUbicacion();
        //DatosUbicacion();
        otro();


        findViewById(R.id.btnlistado).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(),Listado.class);
                startActivity(i);
            }
        });


        findViewById(R.id.btnImage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GaleriaImagenes();
            }
        });


        findViewById(R.id.btnsalvar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(photo!= null){
                    Crear();
                }else{
                    if(nombre.getText().toString().trim().length()==0){
                        nombre.setError("Este campo es obligatorio");
                    }else{
                        nombre.setError(null);
                    }

                    if(numero.getText().toString().trim().length()==0){
                        numero.setError("Este campo es obligatorio");
                    }else{
                        numero.setError(null);
                    }

                    AlertaDialogo("Seleccione la imagen por favor","Imagen no seleccionada");
                }
            }
        });

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

    //https://ranjithexpertisers.medium.com/load-image-from-url-in-android-studio-fe755a3348dd
    private void Crear()
    {

        String url = "http://167.99.158.191/examen/api/crear_usuario.php";

        HashMap<String, String> params = new HashMap<String, String>();

        String image = GetStringImage(photo);
        params.put("nombre", nombre.getText().toString().toLowerCase());
        params.put("telefono", numero.getText().toString());
        params.put("latitud", latitud);
        params.put("longitud", longitud);
        params.put("url_foto", image);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        AlertaDialogo("Se ha Ingresado Exitosamente","Registro Exitoso");
                    //Toast.makeText(getApplicationContext(),"Se ha Ingresado Exitosamente",Toast.LENGTH_SHORT).show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("Error", "Error: " + error.getMessage());
                AlertaDialogo("Error al ingresar "+error.getMessage(),"Error");
            }
        });


        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(jsonObjectRequest);


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

    public void permisosUbicacion(){
        ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
    }

    public void DatosUbicacion(){
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            //Toast.makeText(getApplicationContext(),"",Toast.LENGTH_SHORT).show();
            permisosUbicacion();

            //return;
        }else
        {
            LocationManager locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            Location loc = locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            latitud = loc.getLatitude()+"";
            longitud = loc.getLongitude()+"";

            tvlatitud.setText(latitud);
            tvlongitud.setText(longitud);

            /*
            tvLatitud.setText(String.valueOf(loc.getLatitude()));
            tvLongitud.setText(String.valueOf(loc.getLongitude()));
            tvAltura.setText(String.valueOf(loc.(getAltitude)));
            tvPrecision.setText(String.valueOf(loc.getAccuracy()));

             */
        }
    }

    public void otro(){


        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            //Toast.makeText(getApplicationContext(),"",Toast.LENGTH_SHORT).show();
            permisosUbicacion();

            //return;
        }else
        {
            LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            //Location loc = locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            LocationListener locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(@NonNull Location location) {
                    latitud = location.getLatitude()+"";
                    longitud = location.getLongitude()+"";

                    tvlatitud.setText(latitud);
                    tvlongitud.setText(longitud);
                }
            };

            int permissionCheck = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);

            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,0,0,locationListener);



        }

    }


}