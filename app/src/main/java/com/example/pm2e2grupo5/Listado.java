package com.example.pm2e2grupo5;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Listado extends AppCompatActivity implements AdapterView.OnItemClickListener{

    private List<Modelo> mLista = new ArrayList<>();
    private ListView mlistView;
    ListAdapter mAdapter;
    EditText txtBuscador;
    public static int resultado = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listado);

        getSupportActionBar().hide();

        mlistView = (ListView) findViewById(R.id.mlistView);
        txtBuscador = (EditText) findViewById(R.id.txtBuscador);
        mlistView.setOnItemClickListener(this);

        BusquedadPersonalizada("");

        findViewById(R.id.main_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);
            }
        });

        txtBuscador.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {


                if (s.toString().trim().length() == 0) {
                    BusquedadPersonalizada("");
                } else {
                    BusquedadPersonalizada(s.toString());
                }


            }
        });
    }

    private void BusquedadPersonalizada(String dato) {
        String url = "http://167.99.158.191/examen/api/search_usuarios.php?texto=" + dato + "";
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                JSONObject jsonObject = null;

                 mLista = new ArrayList<>();
                //ListAdapter mAdapter;

                for (int i = 0; i < response.length(); i++) {
                    try {
                        jsonObject = response.getJSONObject(i);
                        Modelo modelo = new Modelo();
                        modelo.setId(jsonObject.getString("id_usuario"));
                        modelo.setNombre(jsonObject.getString("nombre"));
                        modelo.setTelefono(jsonObject.getString("telefono"));
                        modelo.setLatitud(jsonObject.getString("latitud"));
                        modelo.setLongitud(jsonObject.getString("longitud"));
                        modelo.setUrl(jsonObject.getString("url_foto"));

                        mLista.add(modelo);


                        mAdapter = new ListAdapter(getApplicationContext(), R.layout.item_row, mLista);

                        mlistView.setAdapter(mAdapter);

                    } catch (JSONException e) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Error de conexion al buscar", Toast.LENGTH_SHORT).show();
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonArrayRequest);
    }
/*
    private void buscarProductos(){

        String url = "http://167.99.158.191/examen/api/get_usuarios.php";
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                JSONObject jsonObject = null;


                for (int i = 0; i < response.length(); i++) {
                    try {
                        jsonObject = response.getJSONObject(i);
                        Modelo modelo = new Modelo();
                        modelo.setId(jsonObject.getString("id_usuario"));
                        modelo.setNombre(jsonObject.getString("nombre"));
                        modelo.setLatitud(jsonObject.getString("latitud"));
                        modelo.setLongitud(jsonObject.getString("longitud"));
                        modelo.setUrl(jsonObject.getString("url_foto"));

                        mLista.add(modelo);
                        //Toast.makeText(getApplicationContext(),jsonObject.getString("id_usuario"), Toast.LENGTH_SHORT).show();

                        mAdapter = new ListAdapter(getApplicationContext(), R.layout.item_row, mLista);

                        mlistView.setAdapter(mAdapter);


                    } catch (JSONException e) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }



            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Error de conexion al buscar", Toast.LENGTH_SHORT).show();
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonArrayRequest);
    }


 */


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Intent intent = new Intent(getApplicationContext(), SegundoActividad.class);
        intent.putExtra("id", mLista.get(position).getId());
        intent.putExtra("nombre", mLista.get(position).getNombre());
        intent.putExtra("telefono", mLista.get(position).getTelefono());
        intent.putExtra("url", mLista.get(position).getUrl());
        intent.putExtra("latitud", mLista.get(position).getLatitud());
        intent.putExtra("longitud", mLista.get(position).getLongitud());
        startActivityForResult(intent, resultado);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == resultado && requestCode == RESULT_OK) {
            BusquedadPersonalizada("");
            txtBuscador.setText("");
        }
    }

}