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
import android.widget.Toast;

import com.example.ecommerce.Model.Orders;
import com.example.ecommerce.ViewHolder.NewOrderViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AdminNewOrderActivity extends AppCompatActivity {

    private RecyclerView orderRecycler;
    private RecyclerView.LayoutManager orderlayoutManager;
    private DatabaseReference newOrderRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_new_order);

        orderRecycler = findViewById(R.id.new_order_recyclerId);

        orderlayoutManager = new LinearLayoutManager(this);
        orderRecycler.setLayoutManager(orderlayoutManager);

        newOrderRef = FirebaseDatabase.getInstance().getReference().child("Orders");


    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Orders>orderOption = new FirebaseRecyclerOptions.Builder<Orders>()
                .setQuery(newOrderRef,Orders.class)
                .build();

        FirebaseRecyclerAdapter<Orders, NewOrderViewHolder> OrderAdapter = new FirebaseRecyclerAdapter<Orders, NewOrderViewHolder>(orderOption) {
            @Override
            protected void onBindViewHolder(@NonNull final NewOrderViewHolder holder, final int position, @NonNull final Orders model) {
                holder.newOrderNameText.setText("Customer Name: "+model.getCustomerName());
                holder.newOrderPhnTxt.setText("Customer Phone: "+model.getCustomerPhone());
                holder.newOrderTotalPriceTxt.setText("Total Price: "+model.getTotalPrice());
                holder.newOrderAddressText.setText("Customer Address: "+model.getCustomerAddress());
                holder.newOrderTimeText.setText("Ordered At: "+model.getDate()+" "+model.getTime());
                holder.showOrderProductsBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String UID = getRef(holder.getAdapterPosition()).getKey();
                        Intent intent = new Intent(AdminNewOrderActivity.this,ShowNewOrderProductsActivity.class);
                        intent.putExtra("uid",UID);
                        startActivity(intent);
                    }
                });

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CharSequence actions[] = new CharSequence[]{

                                "Yes",
                                "No"

                        };

                        AlertDialog.Builder builder = new AlertDialog.Builder(AdminNewOrderActivity.this);
                        builder.setTitle("Have you yet shipped this order?");
                        builder.setItems(actions, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                if(which == 0){
                                    String UID = getRef(holder.getAdapterPosition()).getKey();
                                    newOrderRef.child(UID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                Toast.makeText(getApplicationContext(),"Order shipped",Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });

                                }else {

                                    finish();

                                }

                            }
                        });

                        builder.show();
                    }
                });


            }

            @NonNull
            @Override
            public NewOrderViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.new_order_item_layout,viewGroup,false);

                return new  NewOrderViewHolder(view);
            }
        };

        orderRecycler.setAdapter(OrderAdapter);
        OrderAdapter.startListening();

    }
}