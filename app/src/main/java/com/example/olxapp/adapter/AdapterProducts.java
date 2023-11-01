package com.example.olxapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.olxapp.R;
import com.example.olxapp.model.Product;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterProducts extends RecyclerView.Adapter<AdapterProducts.MyViewHolder> {

    private List<Product> products;
    private Context context;

    public AdapterProducts(List<Product> products, Context context) {
        this.products = products;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_product, parent, false);
        return new MyViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Product product = products.get(position);
        holder.title.setText(product.getTitle());
        holder.price.setText(product.getPrice());

        //get first image of the list
        List<String> urlPictures = product.getPictures();
        String urlCover = urlPictures.get(0);

        Picasso.get().load(urlCover).into(holder.picture);

    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        TextView price;
        ImageView picture;

        public MyViewHolder(View itemView){
            super(itemView);

            title = itemView.findViewById(R.id.textTitulo);
            price = itemView.findViewById(R.id.textPreco);
            picture = itemView.findViewById(R.id.imageAnuncio);
        }
    }

}
