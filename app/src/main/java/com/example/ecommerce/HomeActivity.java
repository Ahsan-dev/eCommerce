package com.example.ecommerce;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;




import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;

import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ecommerce.Model.Products;
import com.example.ecommerce.Model.Users;
import com.example.ecommerce.Prevalent.Prevalent;
import com.example.ecommerce.ViewHolder.ProductViewHolder;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    private DatabaseReference ProductsRef;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private String adminChecker = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Home");
        setSupportActionBar(toolbar);
        Paper.init(this);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this,CartActivity.class));
            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        ProductsRef = FirebaseDatabase.getInstance().getReference().child("Products");
        recyclerView = findViewById(R.id.recyclerViewId);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if(bundle != null){

            adminChecker = getIntent().getExtras().get("admin").toString();

        }


        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);
        TextView UserNameTextView = headerView.findViewById(R.id.user_profile_nameId);
        CircleImageView UserProfileImage = headerView.findViewById(R.id.user_profile_image);

        if(!adminChecker.equals("Admin")){

            UserNameTextView.setText(Prevalent.currentOnlineUsers.getName());
            Picasso.get().load(Prevalent.currentOnlineUsers.getImage()).placeholder(R.drawable.profile).into(UserProfileImage);

        }




    }

    @Override
    protected void onStart() {
        super.onStart();

        displaySecuritySetter();


        FirebaseRecyclerOptions<Products> options = new FirebaseRecyclerOptions.Builder<Products>()
                .setQuery(ProductsRef, Products.class)
                .build();


        FirebaseRecyclerAdapter<Products,  ProductViewHolder> adapter = new FirebaseRecyclerAdapter<Products, ProductViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ProductViewHolder holder, int position, @NonNull final Products model) {

                holder.cardProductNameTxt.setText(model.getName());
                holder.cardProductDescTxt.setText(model.getDetails());
                holder.cardProductPriceTxt.setText("Price: "+model.getPrice());
                Picasso.get().load(model.getImage()).into(holder.cardProductImageView);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if(!adminChecker.equals("Admin")){

                            Intent intent = new Intent(HomeActivity.this,ProductDetailsActivity.class);
                            intent.putExtra("pid",model.getPid());
                            startActivity(intent);

                        }else {

                            Intent intent = new Intent(HomeActivity.this,AdminProductManageActivity.class);
                            intent.putExtra("pid",model.getPid());
                            startActivity(intent);

                        }


                    }
                });

                

            }

            @NonNull
            @Override
            public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
               View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.product_items_layout,viewGroup,false);



                return new  ProductViewHolder(view);
            }

        };

        recyclerView.setAdapter(adapter);
        adapter.startListening();

    }

    private void displaySecuritySetter() {

        DatabaseReference securityUserNeedRef = FirebaseDatabase.getInstance().getReference().child("Users");

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Set your Security questions");
        builder.setMessage("Please Set the security requirements. It will be mandatory in case of resetting your password. Unless you can not reset your password in later. ");
        builder.setCancelable(false);
        builder.setPositiveButton("Go to the process", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
                Intent intent = new Intent(HomeActivity.this,ResetPasswordActivity.class);
                intent.putExtra("check","settings");
                startActivity(intent);
                finish();

            }
        });

        securityUserNeedRef.child(Prevalent.currentOnlineUsers.getNumber()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){
                    if(!dataSnapshot.hasChild("SecurityAnswers")){

                        builder.show();
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

//        if (id == R.id.action_settings)
//        {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        // Handle navigation view item clicks here.
        int id = menuItem.getItemId();

        if (id == R.id.nav_cart)
        {
            if(!adminChecker.equals("Admin")){

                startActivity(new Intent(HomeActivity.this,CartActivity.class));

            }


        }
        else if (id == R.id.nav_search)
        {
            if(!adminChecker.equals("Admin")){

                startActivity(new Intent(HomeActivity.this,SearchProductActivity.class));

            }


        }
        else if (id == R.id.nav_categories)
        {

        }
        else if (id == R.id.nav_settings)
        {
            if(!adminChecker.equals("Admin")){

                Intent intent = new Intent(HomeActivity.this,SettingsActivity.class);
                startActivity(intent);

            }


        }
        else if (id == R.id.nav_logout)
        {
            if(!adminChecker.equals("Admin")){

                Paper.book().destroy();

                Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();

            }

        }

        return true;
    }
}

