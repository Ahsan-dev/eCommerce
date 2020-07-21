package com.example.ecommerce;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.example.ecommerce.Model.Cart;
import com.example.ecommerce.ViewHolder.CartItemViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ShowNewOrderProductsActivity extends AppCompatActivity {

    private RecyclerView showOrderProductsRecycler;
    private DatabaseReference showProductsRef;
    private String UID = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_new_order_products);

        UID = getIntent().getStringExtra("uid");

        showOrderProductsRecycler = findViewById(R.id.show_order_products_recyclerId);
        showOrderProductsRecycler.setHasFixedSize(true);
        showOrderProductsRecycler.setLayoutManager(new LinearLayoutManager(this));
        showProductsRef = FirebaseDatabase.getInstance().getReference().child("Cart List").child("Admin View").child(UID).child("Products");

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Cart> ProductsOption = new FirebaseRecyclerOptions.Builder<Cart>()
                .setQuery(showProductsRef,Cart.class)
                .build();


        FirebaseRecyclerAdapter<Cart, CartItemViewHolder> adapter =  new FirebaseRecyclerAdapter<Cart, CartItemViewHolder>(ProductsOption) {
            @Override
            protected void onBindViewHolder(@NonNull CartItemViewHolder holder, int position, @NonNull Cart model) {

                holder.cartProdNameTxt.setText(model.getName());
                holder.cartProdPriceTxt.setText("Price: "+model.getPrice());
                holder.cartProdQuantityTxt.setText("Quantity: "+model.getQuantity());

            }

            @NonNull
            @Override
            public CartItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {


                return new CartItemViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cart_items_layout,viewGroup,false));
            }
        };

        showOrderProductsRecycler.setAdapter(adapter);
        adapter.startListening();
    }
}