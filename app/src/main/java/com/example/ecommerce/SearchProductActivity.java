package com.example.ecommerce;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.ecommerce.Model.Products;
import com.example.ecommerce.ViewHolder.ProductViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class SearchProductActivity extends AppCompatActivity {

    private EditText searchEdt;
    private Button searchBtn;
    private RecyclerView searchProductsRecycler;
    private String searchText;
    private DatabaseReference searchProductRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_product);

        searchEdt = findViewById(R.id.search_Edit_TxtId);
        searchBtn = findViewById(R.id.search_buttonId);
        searchProductsRecycler = findViewById(R.id.search_product_recyclerId);
        searchProductsRecycler.setHasFixedSize(true);
        searchProductsRecycler.setLayoutManager(new LinearLayoutManager(this));
        searchProductRef = FirebaseDatabase.getInstance().getReference().child("Products");



        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchText = searchEdt.getText().toString();
                onStart();

            }
        });



    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Products> searchProductOption = new FirebaseRecyclerOptions.Builder<Products>()
                .setQuery(searchProductRef.orderByChild("name").startAt(searchText),Products.class)
                .build();


        FirebaseRecyclerAdapter<Products, ProductViewHolder> adapter = new FirebaseRecyclerAdapter<Products, ProductViewHolder>(searchProductOption) {
            @Override
            protected void onBindViewHolder(@NonNull ProductViewHolder holder, int position, @NonNull final Products model) {

                holder.cardProductNameTxt.setText(model.getName());
                holder.cardProductDescTxt.setText(model.getDetails());
                holder.cardProductPriceTxt.setText("Price: "+model.getPrice());
                Picasso.get().load(model.getImage()).into(holder.cardProductImageView);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(SearchProductActivity.this,ProductDetailsActivity.class);
                        intent.putExtra("pid",model.getPid());
                        startActivity(intent);
                    }
                });



            }

            @NonNull
            @Override
            public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                return new ProductViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.product_items_layout,viewGroup,false));
            }
        };

        searchProductsRecycler.setAdapter(adapter);
        adapter.startListening();
    }
}