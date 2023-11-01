package com.example.olxapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.blackcat.currencyedittext.CurrencyEditText;
import com.example.olxapp.helper.FirebaseConfig;
import com.example.olxapp.helper.Permissions;
import com.example.olxapp.model.Product;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.santalu.maskara.widget.MaskEditText;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import dmax.dialog.SpotsDialog;

public class RegisterProductActivity extends AppCompatActivity
implements View.OnClickListener {

    private EditText fieldTitle, fieldDescription;
    private ImageView image1, image2, image3;
    private CurrencyEditText fieldPrice;
    private MaskEditText fieldPhone;
    private Spinner fieldState, fieldCategory;

    private AlertDialog dialog;

    private Product product;

    private StorageReference storage;

    private String[] permissions = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    private List<String> listRecoveredPictures = new ArrayList<>();
    private List<String> urlPicturesList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_product);

        Permissions.validatePermissions(permissions,this, 1);

        setComponents();
        loadSpinnerData();
        storage = FirebaseConfig.getFirebaseStorage();
    }

    private void showErrorMessage(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

    }

    public void saveProduct(){

        dialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage(R.string.saving_product)
                .setCancelable(false)
                .build();
        dialog.show();

        for (int i = 0; i < listRecoveredPictures.size(); i++){
            String imageUrl = listRecoveredPictures.get(i);
            int listSize = listRecoveredPictures.size();
            savePictureOnStorage(imageUrl, listSize, i );
        }

    }

    private void savePictureOnStorage(String urlString, int totalPictures, int counter){

        final StorageReference productImage = storage.child("imagens")
                .child("anuncios").child(product.getProductId())
                .child("imagem"+counter);

        UploadTask uploadTask = productImage.putFile( Uri.parse(urlString) );
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                productImage.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        Uri firebaseUrl = task.getResult();
                        String convertedUrl = firebaseUrl.toString();

                        urlPicturesList.add(convertedUrl);

                        if(totalPictures == urlPicturesList.size()){

                            product.setPictures(urlPicturesList);
                            product.save();

                            dialog.dismiss();
                            finish();

                        }
                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                showErrorMessage(getResources().getString(R.string.upload_error));
            }
        });

    }


    private Product setProduct(){

        String state = fieldState.getSelectedItem().toString();
        String category = fieldCategory.getSelectedItem().toString();
        String title = fieldTitle.getText().toString();
        String price = fieldPrice.getText().toString();
        String phone = fieldPhone.getText().toString();
        String description = fieldDescription.getText().toString();

        Product product = new Product();
        product.setState( state );
        product.setCategory(category);
        product.setTitle(title);
        product.setPrice(price);
        product.setPhone( phone );
        product.setDescription(description);

        return product;
    }


    public void validateProductData(View view){

        product = setProduct();

        String price = String.valueOf(fieldPrice.getRawValue());

        if( listRecoveredPictures.size() != 0  ){
            if( !product.getState().isEmpty() ){
                if( !product.getCategory().isEmpty() ){
                    if( !product.getTitle().isEmpty() ){
                        if( !price.isEmpty() && !price.equals("0") ){
                            if( !product.getPhone().isEmpty()  ){
                                if( !product.getDescription().isEmpty() ){

                                    saveProduct();

                                }else {
                                    showErrorMessage(getResources().getString(R.string.fill_description));
                                }
                            }else {
                                showErrorMessage(getResources().getString(R.string.fill_phone));
                            }
                        }else {
                            showErrorMessage(getResources().getString(R.string.fill_value));
                        }
                    }else {
                        showErrorMessage(getResources().getString(R.string.fill_title));
                    }
                }else {
                    showErrorMessage(getResources().getString(R.string.fill_category));
                }
            }else {
                showErrorMessage(getResources().getString(R.string.fill_state));
            }
        }else {
            showErrorMessage(getResources().getString(R.string.select_one_image));
        }

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.imageCadastro1:
                chooseImage(1);
                break;
            case R.id.imageCadastro2:
                chooseImage(2);
                break;
            case R.id.imageCadastro3:
                chooseImage(3);
                break;
        }

    }

    public void chooseImage(int requestCode){

        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, requestCode);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == Activity.RESULT_OK){
            //RecoverImage
            Uri imageSelected = data.getData();
            String imagePath = imageSelected.toString();

            //config image on imageView
            if(requestCode == 1) {
                image1.setImageURI(imageSelected);

            }else if(requestCode == 2){
                image2.setImageURI(imageSelected);

            }
            else if (requestCode == 3){
                image3.setImageURI(imageSelected);

            }

            listRecoveredPictures.add(imagePath);

        }

    }

    private void loadSpinnerData(){

        String[] states = getResources().getStringArray(R.array.states);

        ArrayAdapter<String > adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, states
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fieldState.setAdapter(adapter);

        String[] categories = getResources().getStringArray(R.array.categories);

        ArrayAdapter<String > adapterCategories = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, categories
        );
        adapterCategories.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fieldCategory.setAdapter(adapterCategories);

    }

    private void setComponents(){
        fieldTitle = findViewById(R.id.editTitulo);
        fieldDescription = findViewById(R.id.editDescricao);
        fieldPrice = findViewById(R.id.editValor);
        fieldPhone = findViewById(R.id.editTelefone);
        fieldState = findViewById(R.id.spinnerEstado);
        fieldCategory = findViewById(R.id.spinnerCategoria);

        image2 = findViewById(R.id.imageCadastro2);
        image1 = findViewById(R.id.imageCadastro1);
        image3 = findViewById(R.id.imageCadastro3);

        image1.setOnClickListener(this);
        image2.setOnClickListener(this);
        image3.setOnClickListener(this);

        //config loacale
        Locale locale = new Locale("pt", "BR");
        fieldPrice.setLocale(locale);
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for(int resultPermission:grantResults){

            if(resultPermission == PackageManager.PERMISSION_DENIED){

            validatePermissionAlert();

            }

        }
    }

    private void validatePermissionAlert(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.permissions_denied);
        builder.setMessage(R.string.accept_permissions);
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
