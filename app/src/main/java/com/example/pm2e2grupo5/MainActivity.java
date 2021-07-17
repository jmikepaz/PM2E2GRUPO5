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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    static final int RESULT_GALLERY_IMG = 100;
    Bitmap photo = null;
    ImageView imageView;
    EditText nombre, numero;
    TextView tvlatitud, tvlongitud;
    public static String latitud = "";
    public static String longitud = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().hide();

        imageView =findViewById(R.id.ivactu);
        nombre = findViewById(R.id.txtNombre);
        numero = findViewById(R.id.txtNumero);
        tvlatitud = findViewById(R.id.txtlatitud);
        tvlongitud = findViewById(R.id.txtlongitud);


        //permisosUbicacion();
        //DatosUbicacion();
        //otro();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);
        } else {
            locationStart();
        }


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
                                Crear();
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
            //permisosUbicacion();

            return;
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
// --------------------




    private void locationStart() {
        LocationManager mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Localizacion Local = new Localizacion();
        Local.setMainActivity(this);
        final boolean gpsEnabled = mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!gpsEnabled) {
            Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(settingsIntent);
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);
            return;
        }
        mlocManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, (LocationListener) Local);
        mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, (LocationListener) Local);


    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1000) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                locationStart();
                return;
            }
        }
    }

    public void setLocation(Location loc) {
        //Obtener la direccion de la calle a partir de la latitud y la longitud
        if (loc.getLatitude() != 0.0 && loc.getLongitude() != 0.0) {
            try {
                Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                List<Address> list = geocoder.getFromLocation(
                        loc.getLatitude(), loc.getLongitude(), 1);
                if (!list.isEmpty()) {
                    Address DirCalle = list.get(0);

                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /* Aqui empieza la Clase Localizacion */
    public class Localizacion implements LocationListener {
        MainActivity mainActivity;

        public MainActivity getMainActivity() {
            return mainActivity;
        }

        public void setMainActivity(MainActivity mainActivity) {
            this.mainActivity = mainActivity;
        }

        @Override
        public void onLocationChanged(Location loc) {
            // Este metodo se ejecuta cada vez que el GPS recibe nuevas coordenadas
            // debido a la deteccion de un cambio de ubicacion

            loc.getLatitude();
            loc.getLongitude();

            String Text = "Mi ubicacion actual es: " + "\n Lat = "
                    + loc.getLatitude() + "\n Long = " + loc.getLongitude();


            MainActivity.setLatitud(loc.getLatitude()+"");
            MainActivity.setLongitud(loc.getLongitude()+"");
            tvlatitud.setText(loc.getLatitude()+"");
            tvlongitud.setText(loc.getLongitude()+"");
            this.mainActivity.setLocation(loc);
        }

        @Override
        public void onProviderDisabled(String provider) {
            // Este metodo se ejecuta cuando el GPS es desactivado

        }

        @Override
        public void onProviderEnabled(String provider) {
            // Este metodo se ejecuta cuando el GPS es activado

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            switch (status) {
                case LocationProvider.AVAILABLE:
                    Log.d("debug", "LocationProvider.AVAILABLE");
                    break;
                case LocationProvider.OUT_OF_SERVICE:
                    Log.d("debug", "LocationProvider.OUT_OF_SERVICE");
                    break;
                case LocationProvider.TEMPORARILY_UNAVAILABLE:
                    Log.d("debug", "LocationProvider.TEMPORARILY_UNAVAILABLE");
                    break;
            }
        }
    }
    ///


    public static String getLatitud() {
        return latitud;
    }

    public static void setLatitud(String latitud) {
        MainActivity.latitud = latitud;
    }

    public static String getLongitud() {
        return longitud;
    }

    public static void setLongitud(String longitud) {
        MainActivity.longitud = longitud;
    }
}