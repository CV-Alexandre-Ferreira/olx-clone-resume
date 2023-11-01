package com.example.olxapp;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import android.view.View;
import android.widget.AdapterView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.olxapp.adapter.AdapterProducts;
import com.example.olxapp.helper.FirebaseConfig;
import com.example.olxapp.helper.RecyclerItemClickListener;
import com.example.olxapp.model.Product;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
//import com.olx.cursoandroid.jamiltondamasceno.olxapp.R;
//import com.olx.cursoandroid.jamiltondamasceno.olxapp.adapter.AdapterAnuncios;
//import com.olx.cursoandroid.jamiltondamasceno.olxapp.helper.ConfiguracaoFirebase;
//import com.olx.cursoandroid.jamiltondamasceno.olxapp.model.Anuncio;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class MyProductsActivity extends AppCompatActivity {

    private RecyclerView recyclerProducts;
    private List<Product> products = new ArrayList<>();
    private AdapterProducts adapterProducts;
    private DatabaseReference productUserRef;
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_products);

        //Initial configs
        productUserRef = FirebaseConfig.getFirebase()
                .child("meus_anuncios")
                .child( FirebaseConfig.getUserId() );

        setComponents();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), RegisterProductActivity.class));
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //RecyclerView config
        recyclerProducts.setLayoutManager(new LinearLayoutManager(this));
        recyclerProducts.setHasFixedSize(true);
        adapterProducts = new AdapterProducts(products, this);
        recyclerProducts.setAdapter(adapterProducts);

        //recover products for the user
        recoverProducts();

        //add click event on recyclerView
        recyclerProducts.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        this,
                        recyclerProducts,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {

                            }

                            @Override
                            public void onLongItemClick(View view, int position) {

                                Product productSelected = products.get(position);
                                productSelected.remove();

                                adapterProducts.notifyDataSetChanged();

                            }

                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            }
                        }
                )
        );

    }

    private void recoverProducts(){

        dialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage(R.string.loading_products)
                .setCancelable(false)
                .build();
        dialog.show();

        productUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                products.clear();
                for ( DataSnapshot ds : dataSnapshot.getChildren() ){
                    products.add( ds.getValue(Product.class) );
                }

                Collections.reverse(products);
                adapterProducts.notifyDataSetChanged();

                dialog.dismiss();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void setComponents(){

        recyclerProducts = findViewById(R.id.recyclerAnuncios);

    }

}
