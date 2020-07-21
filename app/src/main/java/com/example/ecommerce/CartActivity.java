package com.example.ecommerce;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ecommerce.Model.Cart;
import com.example.ecommerce.Prevalent.Prevalent;
import com.example.ecommerce.ViewHolder.CartItemViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CartActivity extends AppCompatActivity {

    private TextView cartTPriceTxt, shippingConfirmCartText;
    private RecyclerView cartRecycler;
    private Button nextBtn;
    private RecyclerView.LayoutManager layoutManager;
    private int totalPrice = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        cartTPriceTxt = findViewById(R.id.cart_total_ptice_TxtId);
        cartRecycler = findViewById(R.id.cart_recyclerId);
        nextBtn = findViewById(R.id.cart_next_BtnId);
        shippingConfirmCartText = findViewById(R.id.shipping_confirm_cart_text);

        cartRecycler.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        cartRecycler.setLayoutManager(layoutManager);

    }

    @Override
    protected void onStart() {
        super.onStart();
        checkingOrderState();

        final DatabaseReference cartRef = FirebaseDatabase.getInstance().getReference().child("Cart List");
        final FirebaseRecyclerOptions<Cart> options = new FirebaseRecyclerOptions.Builder<Cart>()
                .setQuery(cartRef.child("User View").child(Prevalent.currentOnlineUsers.getNumber()).child("Products"),Cart.class)
                .build();

        FirebaseRecyclerAdapter<Cart, CartItemViewHolder> adapter = new FirebaseRecyclerAdapter<Cart, CartItemViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull CartItemViewHolder holder, int position, @NonNull final Cart model) {
                holder.cartProdNameTxt.setText(model.getName());
                holder.cartProdPriceTxt.setText("Price: "+model.getPrice());
                holder.cartProdQuantityTxt.setText("Quantity: "+model.getQuantity());

                int OneitemTotalPrice = (Integer.valueOf(model.getPrice()))*(Integer.valueOf(model.getQuantity()));
                totalPrice = totalPrice+OneitemTotalPrice;

                cartTPriceTxt.setText("Total Price: "+totalPrice+" Tk");

                nextBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(CartActivity.this,ConfirmFinalOrderActivity.class);
                        intent.putExtra("TotalPrice", String.valueOf(totalPrice));
                        startActivity(intent);
                    }
                });



                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CharSequence Options[] = new CharSequence[]{
                          "Edit","Remove"
                        };

                        AlertDialog.Builder optionBuilder = new AlertDialog.Builder(CartActivity.this);
                        optionBuilder.setTitle("Choose An action:");
                        optionBuilder.setItems(Options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                if(which == 0){
                                    Intent intent = new Intent(CartActivity.this,ProductDetailsActivity.class);
                                    intent.putExtra("pid",model.getPid());
                                    startActivity(intent);
                                }else {
                                    cartRef.child("User View").child(Prevalent.currentOnlineUsers.getNumber()).child("Products").child(model.getPid())
                                            .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Toast.makeText(getApplicationContext(),"Cart Item removed Successfully",Toast.LENGTH_SHORT).show();

                                        }
                                    });
                                    cartRef.child("Admin View").child(Prevalent.currentOnlineUsers.getNumber()).child("Products").child(model.getPid())
                                            .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                        }
                                    });
                                    startActivity(new Intent(CartActivity.this,HomeActivity.class));
                                }

                            }
                        });

                        optionBuilder.show();
                    }
                });

            }

            @NonNull
            @Override
            public CartItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cart_items_layout,viewGroup,false);
                return new CartItemViewHolder(view);
            }
        };

        cartRecycler.setAdapter(adapter);
        adapter.startListening();


    }

    private void checkingOrderState(){
        final DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference().child("Orders").child(Prevalent.currentOnlineUsers.getNumber());
        orderRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){
                    String orderState = dataSnapshot.child("state").getValue().toString();
                    String UserName = dataSnapshot.child("CustomerName").getValue().toString();

                    if(orderState.equals("Shipped")){

                        cartTPriceTxt.setText("Dear "+UserName+"\nOrder is shipped successfully");
                        cartRecycler.setVisibility(View.GONE);
                        shippingConfirmCartText.setVisibility(View.VISIBLE);
                        nextBtn.setVisibility(View.GONE);

                    }else if(orderState.equals("Not Shipped")){
                        cartTPriceTxt.setText("Dear "+UserName+"\nOrder will be shipped very soon");
                        cartRecycler.setVisibility(View.GONE);
                        shippingConfirmCartText.setVisibility(View.VISIBLE);
                        shippingConfirmCartText.setText("Congratulations.. Your final order has been placed successfully. Soon it will be verified and shipped.");
                        nextBtn.setVisibility(View.GONE);

                        Toast.makeText(getApplicationContext(),"You can purchase more products, once you receive your last order",Toast.LENGTH_LONG).show();




                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}