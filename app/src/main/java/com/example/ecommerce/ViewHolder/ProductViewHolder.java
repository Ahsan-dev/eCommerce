package com.example.ecommerce.ViewHolder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ecommerce.Interface.ItemClickListener;
import com.example.ecommerce.R;

import org.w3c.dom.Text;

public class ProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView cardProductNameTxt,cardProductDescTxt, cardProductPriceTxt;
    public ImageView cardProductImageView;
    public ItemClickListener listener;


    public ProductViewHolder( View itemView) {

        super(itemView);

        cardProductNameTxt = itemView.findViewById(R.id.card_product_nameID);
        cardProductDescTxt = itemView.findViewById(R.id.card_product_descriptionID);
        cardProductImageView = itemView.findViewById(R.id.card_product_imageID);
        cardProductPriceTxt = itemView.findViewById(R.id.card_product_priceID);


    }

    public void setItemClickListener(ItemClickListener listener){

        this.listener = listener;

    }

    @Override
    public void onClick(View v) {

        listener.onClick(v, getAdapterPosition(), false);

    }
}
