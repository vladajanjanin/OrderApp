package com.vladimirjanjanin.orderapp.recyclerviews;

import android.content.Context;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.vladimirjanjanin.orderapp.R;
import com.vladimirjanjanin.orderapp.interfaces.InventoryItemClickListener;
import com.vladimirjanjanin.orderapp.utils.Utils;
import com.vladimirjanjanin.orderapp.data.models.InventoryItem;
import com.vladimirjanjanin.orderapp.data.models.MerchantItem;

import java.util.List;

import io.github.luizgrp.sectionedrecyclerviewadapter.Section;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters;

public class InventorySection extends Section {
    private String title;
    private List<MerchantItem> items;
    private InventoryItemClickListener listener;
    private Context context;
    private boolean isMerchant;

    public static class MyItemViewHolder extends RecyclerView.ViewHolder {
        private final ImageView ivItemImage;
        private final TextView tvItemName;
        private final TextView tvItemPrice;
        private final TextView tvItemCount;
        private final ImageButton btnMinus;
        private final ImageButton btnPlus;

        public MyItemViewHolder(View itemView) {
            super(itemView);
            ivItemImage = itemView.findViewById(R.id.iv_item_image);
            tvItemName = itemView.findViewById(R.id.tv_item_name);
            tvItemPrice = itemView.findViewById(R.id.tv_item_price);
            tvItemCount = itemView.findViewById(R.id.tv_item_count);
            btnMinus = itemView.findViewById(R.id.btn_minus);
            btnPlus = itemView.findViewById(R.id.btn_plus);
        }
    }

    public static class MyHeaderViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvTitle;

        public MyHeaderViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_title);
        }
    }

    public InventorySection(Context context, String title, List<MerchantItem> items, boolean isMerchant, InventoryItemClickListener listener) {
        // call constructor with layout resources for this Section header and items
        super(SectionParameters.builder()
                .itemResourceId(R.layout.row_inventory)
                .headerResourceId(R.layout.header_inventory)
                .build());
        this.title = title;
        this.items = items;
        this.listener = listener;
        this.context = context;
        this.isMerchant = isMerchant;
    }

    @Override
    public int getContentItemsTotal() {
        return items.size(); // number of items of this section
    }

    @Override
    public RecyclerView.ViewHolder getItemViewHolder(View view) {
        // return a custom instance of ViewHolder for the items of this section
        return new MyItemViewHolder(view);
    }

    @Override
    public void onBindItemViewHolder(RecyclerView.ViewHolder holder, int position) {
        MyItemViewHolder itemHolder = (MyItemViewHolder) holder;
        final MerchantItem merchantItem = items.get(position);
        final InventoryItem inventoryItem = merchantItem.getInventoryItem();

        itemHolder.ivItemImage.setImageBitmap(Utils.getBitmapFromBase64(inventoryItem.getIcon()));
        itemHolder.tvItemName.setText(inventoryItem.getName());
        itemHolder.tvItemPrice.setText(context.getString(R.string.price_format, inventoryItem.getPrice()));

        if (isMerchant) {
            itemHolder.tvItemCount.setText(String.valueOf((int)merchantItem.getQuantity()));
        } else {
            itemHolder.tvItemCount.setText(String.valueOf((int)merchantItem.getOrderQuantity()));
        }

        itemHolder.ivItemImage.setOnClickListener(c -> {
            listener.onClick(inventoryItem);
        });
        itemHolder.tvItemName.setOnClickListener(c -> {
            listener.onClick(inventoryItem);
        });
        itemHolder.tvItemPrice.setOnClickListener(c -> {
            listener.onClick(inventoryItem);
        });

        itemHolder.btnMinus.setOnClickListener(c -> {
            int itemCount = items.get(position).getOrderQuantity();

            if (itemCount > 0) {
                itemCount--;
                if (isMerchant) {
                    itemHolder.tvItemCount.setText(String.valueOf((int)items.get(position).getOrderQuantity() + itemCount));
                } else {
                    itemHolder.tvItemCount.setText(String.valueOf(itemCount));
                }
                items.get(position).setOrderQuantity(itemCount);
            }

        });

        itemHolder.btnPlus.setOnClickListener(c -> {
            int itemCount = items.get(position).getOrderQuantity();

            if (isMerchant) {
                // Merchant logic
                itemCount++;
                itemHolder.tvItemCount.setText(String.valueOf((int)items.get(position).getQuantity() + itemCount));
                items.get(position).setOrderQuantity(itemCount);
            } else {
                // Customer logic
                if (itemCount < items.get(position).getQuantity()) {
                    itemCount++;
                    itemHolder.tvItemCount.setText(String.valueOf(itemCount));
                    items.get(position).setOrderQuantity(itemCount);
                } else {
                    Utils.showToast(context, "Maximum number of this item.");
                }
            }
        });
    }

    @Override
    public RecyclerView.ViewHolder getHeaderViewHolder(View view) {
        return new MyHeaderViewHolder(view);
    }

    @Override
    public void onBindHeaderViewHolder(final RecyclerView.ViewHolder holder) {
        final MyHeaderViewHolder headerHolder = (MyHeaderViewHolder) holder;
        headerHolder.tvTitle.setText(title);
    }

}
