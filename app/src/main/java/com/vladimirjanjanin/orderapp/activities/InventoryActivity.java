package com.vladimirjanjanin.orderapp.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
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
import com.vladimirjanjanin.orderapp.interfaces.CustomPriceListener;
import com.vladimirjanjanin.orderapp.recyclerviews.InventorySection;
import com.vladimirjanjanin.orderapp.utils.DialogUtils;
import com.vladimirjanjanin.orderapp.utils.Utils;
import com.vladimirjanjanin.orderapp.viewmodels.InventoryViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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
    private TextView                        tvUpdateQuantity;
    private SwipeRefreshLayout              swipeRefresh;

    private Context                         context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

        viewModel = new ViewModelProvider(this).get(InventoryViewModel.class);
        context = this;
        isMerchant = viewModel.isMerchant();

        initViews();
        setupRecyclerView();
        setListeners();
        setObservers();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshItems();
    }

    private void refreshItems() {
        if (isMerchant) {
            Utils.log("Refresh merch");
            viewModel.refreshInventory(viewModel.getUserId());
        } else {
            Utils.log("Refresh cust");
            viewModel.refreshInventory(viewModel.getCurrentMerchantId());
        }
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

        tvUpdateQuantity.setOnClickListener(c -> {
            if (isMerchant) {
                viewModel.updateMerchantInventory(viewModel.getUserId(), merchantItems).observe(this, r -> {
                    if (r >= 200 && r < 300) {
                        Utils.showSnackbar(root, this, true, "Successful inventory update!");
                    } else {
                        Utils.showSnackbar(root, this, false, "Error!");
                    }
                    viewModel.refreshInventory(viewModel.getUserId());
                    Utils.log("Update inventory: " + r);
                });
            } else {
                // Customer
                checkoutItems = processItems(merchantItems);
                if (checkoutItems.size() == 0) {
                    Utils.showSnackbar(root, this, false, getString(R.string.empty_shopping_cart));
                } else {
                    proceedToCheckout();
                }
            }

        });

        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefresh.setRefreshing(false);
                refreshItems();
            }
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
            Utils.log("Quantity: " + item.getQuantity() + " Order quantity: " + item.getOrderQuantity());
            if (item.getOrderQuantity() > 0) {
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
        tvUpdateQuantity = findViewById(R.id.tv_update_quantity);
        swipeRefresh = findViewById(R.id.swipe_refresh);

        if (isMerchant) {
            tvUpdateQuantity.setText("Update quantity");
        } else {
            tvUpdateQuantity.setText("Proceed to checkout");
        }
    }

    private void setObservers() {
        viewModel.getInventoryLiveData().observe(this, inventory -> {
            Utils.log("Got new items!");
            progressBar.setVisibility(View.GONE);
            merchantItems = inventory;
            if (!isMerchant) {
                // we don't show items with 0 quantity to customers
                Iterator<MerchantItem> iter = merchantItems.iterator();

                while (iter.hasNext()) {
                    MerchantItem item = iter.next();

                    if (item.getQuantity() <= 0)
                        iter.remove();
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
        adapter.removeAllSections();
        for (List<MerchantItem> sectionList : sections.values()) {
            String title = sectionList.get(0).getInventoryItem().getCategory().getName();
            adapter.addSection(new InventorySection(
                    this,
                    title,
                    sectionList,
                    isMerchant,
                    item -> {
                        if (item.getName().equals("Custom item")) {
                            // Custom item price set
                            DialogUtils.showCustomItemPriceDialog(this, item, new CustomPriceListener() {
                                @Override
                                public void onPriceConfirm(String price) {
                                    double newPrice = Double.parseDouble(price);
                                    progressBar.setVisibility(View.VISIBLE);
                                    viewModel.setNewCustomItemPrice(item.getId(), newPrice).observe((LifecycleOwner) context, r -> {
                                        progressBar.setVisibility(View.INVISIBLE);
                                        refreshItems();
                                        if (r >= 200 && r < 300) {
                                            Utils.showSnackbar(getWindow().getDecorView().getRootView(), context, true, "Success");
                                        } else {
                                            Utils.showSnackbar(getWindow().getDecorView().getRootView(), context, false, "Error");
                                        }
                                    });
                                }
                            });
                        } else {
                            DialogUtils.showItemInfoDialog(this, item);
                        }
                    }
            ));
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