package com.example.olxapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.olxapp.model.Product;
import com.squareup.picasso.Picasso;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;

public class ProductDetailsActivity extends AppCompatActivity {

    //private CarouselView carouselView;
    private TextView title;
    private TextView description;
    private TextView state;
    private TextView price;
    private Product productSelected;
    private ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        getSupportActionBar().setTitle(R.string.product_details);
        setComponents();

        productSelected = (Product) getIntent().getSerializableExtra("anuncioSelecionado");

        if(productSelected != null) {
            title.setText(productSelected.getTitle());
            description.setText(productSelected.getDescription());
            state.setText(productSelected.getState());
            price.setText(productSelected.getPrice());

            ImageListener imageListener = new ImageListener() {
                @Override
                public void setImageForPosition(int position, ImageView imageView) {

                    String urlString = productSelected.getPictures().get(position);
                    Picasso.get().load(urlString).into(imageView);

                }
            };

            //carouselView.setPageCount(productSelected.getPictures().size());
            //carouselView.setImageListener(imageListener);
        }

        String urlString = productSelected.getPictures().get(0);
        Picasso.get().load(urlString).into(image);
    }

    public void setComponents(){
        //carouselView = findViewById(R.id.carouselView);
        image = findViewById(R.id.imageViewPD);
        title = findViewById(R.id.textTituloDetalhe);
        description = findViewById(R.id.textDescricaoDetalhe);
        state = findViewById(R.id.textEstadoDetalhe);
        price = findViewById(R.id.textPrecoDetalhe);

    }
}