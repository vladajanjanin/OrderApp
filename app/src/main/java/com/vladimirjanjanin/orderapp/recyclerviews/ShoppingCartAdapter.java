package com.vladimirjanjanin.orderapp.recyclerviews;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.vladimirjanjanin.orderapp.R;
import com.vladimirjanjanin.orderapp.data.models.MerchantItem;

import java.util.List;


public class ShoppingCartAdapter extends RecyclerView.Adapter<ShoppingCartAdapter.ViewHolder> {
    Activity activity;
    List<MerchantItem> items;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvItemName;
        private TextView tvQuantity;

        public ViewHolder(View view) {
            super(view);
            tvItemName = view.findViewById(R.id.tv_item_name);
            tvQuantity = view.findViewById(R.id.tv_item_quantity);
        }
    }

    public ShoppingCartAdapter(Activity activity, List<MerchantItem> items) {
        this.activity = activity;
        this.items = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.row_shopping_cart, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        viewHolder.tvItemName.setText(items.get(position).getInventoryItem().getName());
        viewHolder.tvQuantity.setText(String.valueOf(items.get(position).getOrderQuantity()));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
