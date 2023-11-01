package com.example.olxapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.olxapp.adapter.AdapterProducts;
import com.example.olxapp.helper.FirebaseConfig;
import com.example.olxapp.helper.RecyclerItemClickListener;
import com.example.olxapp.model.Product;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class ProductsActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private RecyclerView recyclerPublicProducts;
    private Button buttonRegion, buttonCategory;
    private AdapterProducts adapterProducts;
    private AlertDialog dialog;
    private String stateFilter = "";
    private String categoryFilter = "";
    private boolean filterPerState = false;

    private DatabaseReference publicProductsRef;

    private List<Product> productsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);

        setComponents();

        publicProductsRef = FirebaseConfig.getFirebase().child("anuncios");

        //recyclerView Config
        recyclerPublicProducts.setLayoutManager(new LinearLayoutManager(this));
        recyclerPublicProducts.setHasFixedSize(true);
        adapterProducts = new AdapterProducts(productsList, this);
        recyclerPublicProducts.setAdapter(adapterProducts);

        recoverPublicProducts();

        auth = FirebaseConfig.getFirebaseAuth();

        recyclerPublicProducts.addOnItemTouchListener(new RecyclerItemClickListener(
                this, recyclerPublicProducts,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Product productSelected = productsList.get(position);
                        Intent i = new Intent(ProductsActivity.this, ProductDetailsActivity.class);
                        i.putExtra("anuncioSelecionado", productSelected);
                        startActivity(i);

                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                    }

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    }
                }
        ));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        if(auth.getCurrentUser() == null){

            menu.setGroupVisible(R.id.group_deslogado, true);

        }else{

            menu.setGroupVisible(R.id.group_logado, true);


        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){

            case R.id.menu_cadastrar:
                startActivity(new Intent(getApplicationContext(), SignUpActivity.class));
                break;

            case R.id.menu_sair:
                auth.signOut();
                invalidateOptionsMenu(); //Invalidate menu items calling onPrepare again
                break;

            case R.id.menu_anuncios:

            startActivity(new Intent(getApplicationContext(), MyProductsActivity.class));

                break;

        }

        return super.onOptionsItemSelected(item);
    }

    public void filterPerState(View view){

        AlertDialog.Builder dialogState = new AlertDialog.Builder(this);
        dialogState.setTitle(R.string.select_state);

        //spinner config

        View viewSpinner = getLayoutInflater().inflate(R.layout.dialog_spinner, null);
        Spinner stateSpinner = viewSpinner.findViewById(R.id.spinnerFiltro);

        String[] states = getResources().getStringArray(R.array.states);

        ArrayAdapter<String > adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, states
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        stateSpinner.setAdapter(adapter);


        dialogState.setView(viewSpinner);


        dialogState.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                stateFilter = stateSpinner.getSelectedItem().toString();
                recoverProductsPerState();
                filterPerState = true;

            }
        });

        dialogState.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        AlertDialog dialog = dialogState.create();
        dialog.show();

    }

    public void filterPerCategory(View view){

        if (filterPerState == true) {

            AlertDialog.Builder stateDialog = new AlertDialog.Builder(this);
            stateDialog.setTitle(R.string.select_category);

            //set spinner

            View viewSpinner = getLayoutInflater().inflate(R.layout.dialog_spinner, null);
            Spinner categorySpinner = viewSpinner.findViewById(R.id.spinnerFiltro);

            String[] states = getResources().getStringArray(R.array.categories);

            ArrayAdapter<String > adapter = new ArrayAdapter<String>(
                    this, android.R.layout.simple_spinner_item, states
            );
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            categorySpinner.setAdapter(adapter);


            stateDialog.setView(viewSpinner);


            stateDialog.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    categoryFilter = categorySpinner.getSelectedItem().toString();
                    recoverProductsPerCategory();

                }
            });

            stateDialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });

            AlertDialog dialog = stateDialog.create();
            dialog.show();
            
            
        }else{
            Toast.makeText(this, R.string.select_region_first, Toast.LENGTH_SHORT).show();
        }



    }

    public void recoverProductsPerCategory(){
        publicProductsRef = FirebaseConfig.getFirebase().child("anuncios")
                .child(stateFilter)
                .child(categoryFilter);

        publicProductsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                productsList.clear();

                for(DataSnapshot products: snapshot.getChildren()){

                    Product product = products.getValue(Product.class);
                    productsList.add(product);


                }

                Collections.reverse(productsList);
                adapterProducts.notifyDataSetChanged();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void recoverProductsPerState(){
        publicProductsRef = FirebaseConfig.getFirebase().child("anuncios")
                .child(stateFilter);

        publicProductsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                productsList.clear();

                for(DataSnapshot categories: snapshot.getChildren()){

                    for(DataSnapshot products: categories.getChildren()){

                        Product product = products.getValue(Product.class);
                        productsList.add(product);


                    }

                }

                Collections.reverse(productsList);
                adapterProducts.notifyDataSetChanged();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void recoverPublicProducts(){

        dialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage(R.string.loading_products)
                .setCancelable(false)
                .build();

        productsList.clear();
        publicProductsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot states: snapshot.getChildren()){

                    for(DataSnapshot categories: states.getChildren()){

                        for(DataSnapshot products: categories.getChildren()){

                            Product product = products.getValue(Product.class);
                            productsList.add(product);

                        }

                    }

                }

                Collections.reverse(productsList);
                adapterProducts.notifyDataSetChanged();

                dialog.dismiss();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void setComponents(){

        recyclerPublicProducts = findViewById(R.id.recyclerAnunciosPublicos);

    }
}
