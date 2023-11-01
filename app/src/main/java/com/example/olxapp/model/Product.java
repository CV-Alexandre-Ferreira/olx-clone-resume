package com.example.olxapp.model;

import com.example.olxapp.helper.FirebaseConfig;
import com.google.firebase.database.DatabaseReference;

import java.io.Serializable;
import java.util.List;

public class Product implements Serializable {

    private String productId;
    private String state;
    private String category;
    private String title;
    private String price;
    private String phone;
    private String description;
    private List<String> pictures;


    public Product() {
        DatabaseReference productRef = FirebaseConfig.getFirebase()
                .child("meus_anuncios");
        setProductId(productRef.push().getKey());

    }

    public void save(){

        String userId = FirebaseConfig.getUserId();

        DatabaseReference productRef = FirebaseConfig.getFirebase()
                .child("meus_anuncios");

        productRef.child(userId).child(getProductId()).setValue(this);

        savePublicProduct();

    }

    public void remove(){

        String userId = FirebaseConfig.getUserId();

        DatabaseReference productRef = FirebaseConfig.getFirebase()
                .child("meus_anuncios")
                .child(userId)
                .child(getProductId());

        productRef.removeValue();
        removePublicProduct();

    }

    public void removePublicProduct(){


        DatabaseReference productRef = FirebaseConfig.getFirebase()
                .child("anuncios")
                .child(getState())
                .child(getCategory())
                .child(getProductId());

        productRef.removeValue();

    }

    public void savePublicProduct(){


        DatabaseReference productRef = FirebaseConfig.getFirebase()
                .child("anuncios");

        productRef.child(getState()).child(getCategory()).child(getProductId()).setValue(this);

    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getPictures() {
        return pictures;
    }

    public void setPictures(List<String> pictures) {
        this.pictures = pictures;
    }
}
