package com.example.pm2e2grupo5;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class ListAdapter extends ArrayAdapter {

    private List<Modelo> mLista;
    private Context context;
    private int resourceLayout;


    public ListAdapter(@NonNull Context context, int resource, @NonNull List<Modelo> objects) {
        super(context, resource, objects);
        this.context = context;
        this.mLista = objects;
        this.resourceLayout = resource;

    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View view = convertView;

        if(view==null){
            view = LayoutInflater.from(context).inflate(resourceLayout,null);
        }

        Modelo modelo = mLista.get(position);

        ImageView imageView = view.findViewById(R.id.image);

        //downloadFile(modelo.getUrl(),imageView);
        Picasso.with(context).load(modelo.getUrl()).into(imageView);


        /*
        try {
            URL url = new URL(modelo.getUrl());
            Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            imageView.setImageBitmap(bmp);
        } catch (IOException e) {

        }

         */




        TextView id = view.findViewById(R.id.txtId);
        id.setText(modelo.getId());

        TextView ruta = view.findViewById(R.id.txtRuta);
        ruta.setText(modelo.getNombre());

        //return super.getView(position, convertView, parent);
        return view;
    }

/*
    void downloadFile(String imageHttpAddress,ImageView imageView) {
        URL imageUrl = null;
        try {
            imageUrl = new URL(imageHttpAddress);
            HttpURLConnection conn = (HttpURLConnection) imageUrl.openConnection();
            conn.connect();
            loadedImage = BitmapFactory.decodeStream(conn.getInputStream());
            imageView.setImageBitmap(loadedImage);
        } catch (IOException e) {
            Toast.makeText(getContext(), "Error cargando la imagen: "+e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }
    *
 */



}
