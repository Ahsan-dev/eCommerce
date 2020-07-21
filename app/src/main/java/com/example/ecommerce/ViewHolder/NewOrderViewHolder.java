package com.example.ecommerce.ViewHolder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.ecommerce.R;

public class NewOrderViewHolder extends RecyclerView.ViewHolder {

    public TextView newOrderNameText, newOrderPhnTxt, newOrderTotalPriceTxt, newOrderAddressText, newOrderTimeText;
    public Button showOrderProductsBtn;
    public NewOrderViewHolder(@NonNull View itemView) {
        super(itemView);
        newOrderNameText = itemView.findViewById(R.id.new_order_item_customer_name_textId);
        newOrderPhnTxt = itemView.findViewById(R.id.new_order_item_customer_phone_textId);
        newOrderTotalPriceTxt = itemView.findViewById(R.id.new_order_item_total_price_textId);
        newOrderAddressText = itemView.findViewById(R.id.new_order_item_customer_address_textId);
        newOrderTimeText = itemView.findViewById(R.id.new_order_date_timeTxtID);
        showOrderProductsBtn = itemView.findViewById(R.id.new_order_show_products_buttonId);
    }
}
