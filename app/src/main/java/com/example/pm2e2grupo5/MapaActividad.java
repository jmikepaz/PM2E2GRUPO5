package com.example.pm2e2grupo5;

import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.pm2e2grupo5.databinding.ActivityMapaActividadBinding;

public class MapaActividad extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapaActividadBinding binding;

    public static Double latitud_map=0.00,longitud_map=0.00;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapaActividadBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    public static Double getLatitud_map() {
        return latitud_map;
    }

    public static void setLatitud_map(Double latitud_map) {
        MapaActividad.latitud_map = latitud_map;
    }

    public static Double getLongitud_map() {
        return longitud_map;
    }

    public static void setLongitud_map(Double longitud_map) {
        MapaActividad.longitud_map = longitud_map;
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney;

        sydney = new LatLng(latitud_map, longitud_map);

        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
}