package com.vladimirjanjanin.orderapp.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.vladimirjanjanin.orderapp.R;
import com.vladimirjanjanin.orderapp.data.models.MerchantItem;
import com.vladimirjanjanin.orderapp.interfaces.CheckoutClickListener;
import com.vladimirjanjanin.orderapp.recyclerviews.InventorySection;
import com.vladimirjanjanin.orderapp.utils.DialogUtils;
import com.vladimirjanjanin.orderapp.utils.Utils;
import com.vladimirjanjanin.orderapp.viewmodels.InventoryViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;

public class InventoryActivity extends AppCompatActivity {
    private InventoryViewModel              viewModel;
    private RecyclerView                    recyclerView;
    private SectionedRecyclerViewAdapter    sectionAdapter;
    private List<MerchantItem>              merchantItems;
    private List<MerchantItem>              checkoutItems;
    private boolean                         isMerchant;

    private ConstraintLayout                root;
    private ProgressBar                     progressBar;
    private ImageButton                     btnCheckout;
    private TextView                        tvLogout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

        viewModel = new ViewModelProvider(this).get(InventoryViewModel.class);
        isMerchant = viewModel.isMerchant();

        initViews();
        setupRecyclerView();
        setListeners();
        setObservers();
    }

    private void setListeners() {

        btnCheckout.setOnClickListener(c -> {
            checkoutItems = processItems(merchantItems);
            if (checkoutItems.size() == 0) {
                Utils.showSnackbar(root, this, false, getString(R.string.empty_shopping_cart));
            } else {
                DialogUtils.showCheckoutDialog(this, checkoutItems, new CheckoutClickListener() {
                    @Override
                    public void onProceedToCheckoutClick() {
                        proceedToCheckout();
                    }

                    @Override
                    public void onEmptyCartClick() {
                        emptyCart();
                    }
                });
            }
        });

        tvLogout.setOnClickListener(c -> {
            Utils.removePassword(this);
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }

    private void proceedToCheckout() {
        viewModel.setCheckoutItemsLiveData(checkoutItems);
        startActivity(new Intent(this, CheckoutActivity.class));
    }

    private void emptyCart() {
        for (MerchantItem item : merchantItems) {
            item.setOrderQuantity(0);
        }
        checkoutItems = null;
        sectionAdapter.notifyDataSetChanged();
    }

    private List<MerchantItem> processItems(List<MerchantItem> items) {
        List<MerchantItem> checkoutItems = new ArrayList<>();
        for (MerchantItem item : items) {
            if (item.getOrderQuantity() != 0) {
                checkoutItems.add(item);
            }
        }
        return checkoutItems;
    }

    private void initViews() {
        root = findViewById(R.id.inventory_root);
        recyclerView =  findViewById(R.id.rv_inventory);
        progressBar = findViewById(R.id.progress_bar_inventory);
        btnCheckout = findViewById(R.id.btn_checkout);
        tvLogout = findViewById(R.id.tv_logout);
    }

    private void setObservers() {
        viewModel.getInventoryLiveData().observe(this, inventory -> {
            progressBar.setVisibility(View.GONE);
            merchantItems = inventory;
            if (!isMerchant) {
                // we don't show items with 0 quantity to customers
                for (MerchantItem item : merchantItems) {
                    if (item.getQuantity() <= 0) merchantItems.remove(item);
                }
            }
            addSectionsToAdapter(sectionAdapter, createSections(inventory));
            recyclerView.setAdapter(sectionAdapter);
        });
    }

    private void setupRecyclerView() {
        // Create an instance of SectionedRecyclerViewAdapter
        sectionAdapter = new SectionedRecyclerViewAdapter();

        // Set up your RecyclerView with the SectionedRecyclerViewAdapter
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(sectionAdapter);
    }

    private void addSectionsToAdapter(SectionedRecyclerViewAdapter adapter, HashMap<String, List<MerchantItem>> sections) {
        for (List<MerchantItem> sectionList : sections.values()) {
            String title = sectionList.get(0).getInventoryItem().getCategory().getName();
            adapter.addSection(new InventorySection(this, title, sectionList, isMerchant, item -> DialogUtils.showItemInfoDialog(this, item)));
        }
    }

    private HashMap<String,List<MerchantItem>> createSections(List<MerchantItem> inventory) {
        HashMap<String, List<MerchantItem>> sections = new HashMap<>();
        for (MerchantItem item : inventory) {
            if (item.getInventoryItem() != null) {
                String id = item.getInventoryItem().getCategory().getId();
                List<MerchantItem> itemList = sections.get(id);
                if (itemList == null) {
                    itemList = new ArrayList<>();
                    itemList.add(item);
                    sections.put(id, itemList);
                } else {
                    itemList.add(item);
                }
            }
        }

        return sections;
    }
}