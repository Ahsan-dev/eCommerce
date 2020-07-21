package com.example.ecommerce.ViewHolder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.ecommerce.Interface.ItemClickListener;
import com.example.ecommerce.R;

public class CartItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    public TextView cartProdNameTxt, cartProdQuantityTxt, cartProdPriceTxt;
    private ItemClickListener itemClickListener;

    public CartItemViewHolder(@NonNull View itemView) {
        super(itemView);
        cartProdNameTxt = itemView.findViewById(R.id.cart_item_product_nameID);
        cartProdPriceTxt = itemView.findViewById(R.id.cart_item_product_PriceID);
        cartProdQuantityTxt = itemView.findViewById(R.id.cart_item_product_quantityID);
    }

    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v,getAdapterPosition(),false);

    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }
}
