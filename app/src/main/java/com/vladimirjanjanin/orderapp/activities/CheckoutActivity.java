package com.vladimirjanjanin.orderapp.activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.vladimirjanjanin.orderapp.R;
import com.vladimirjanjanin.orderapp.data.dtos.MerchantTransactionBody;
import com.vladimirjanjanin.orderapp.data.models.InventoryTransaction;
import com.vladimirjanjanin.orderapp.data.models.MerchantItem;
import com.vladimirjanjanin.orderapp.recyclerviews.CheckoutAdapter;
import com.vladimirjanjanin.orderapp.utils.Utils;
import com.vladimirjanjanin.orderapp.viewmodels.CheckoutViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.vladimirjanjanin.orderapp.utils.PaymentUtils.createPayGateData;

public class CheckoutActivity extends AppCompatActivity {
    private CheckoutViewModel               viewModel;
    private CheckoutAdapter                 adapter;
    private RecyclerView                    rvCheckout;
    private ImageButton                     btnBack;
    private TextView                        tvBack;
    private TextView                        tvTotalPrice;
    private double                          totalPrice = 0;
    private ImageButton                     btnOzow;
    private TextView                        tvOzow;
    private ImageButton                     btnPayGate;
    private TextView                        tvPayGate;
    private ProgressBar                     progressBar;
    private ActivityResultLauncher<Intent>  ozowActivityResultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        viewModel = new ViewModelProvider(this).get(CheckoutViewModel.class);

        initViews();
        setListeners();
        setObservers();
        setActivityResultLaunchers();


    }

    private void setActivityResultLaunchers() {

        ozowActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        viewModel.getLastOzowTransaction().observe(this, response -> {

                            if (response.code() >= 200 && response.code() < 300) {
                                if (response.body() != null && response.body().size() > 0) {

                                    boolean positiveStatus = false;
                                    if (response.body().get(0).getStatus().equals("Complete")) positiveStatus = true;
                                    Utils.showSnackbar(findViewById(android.R.id.content).getRootView(), this, positiveStatus, response.body().get(0).getStatus());

                                    //TODO Refresh activity
                                    notifySuccessfulTransaction();
                                }
                            }
                        });
                    }
                });
    }

    private void notifySuccessfulTransaction() {

        MerchantTransactionBody body = new MerchantTransactionBody();
        body.setMerchantId(viewModel.getCurrentMerchantId());
        body.setTotalPrice(totalPrice);

        List<MerchantItem> boughtItems = viewModel.getCheckoutItemsLiveData().getValue();

        Date date = new Date();

        SimpleDateFormat dateF = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        String timestamp = dateF.format(date);

        body.setTimestamp(timestamp);

        List<InventoryTransaction> transactionItems = new ArrayList<>();
        if (boughtItems != null) {
            for (MerchantItem item : boughtItems) {
                InventoryTransaction transactionItem = new InventoryTransaction();
                transactionItem.setItemId(item.getInventoryItem().getId());
                transactionItem.setQuantity(item.getOrderQuantity());
                transactionItem.setPrice(item.getInventoryItem().getPrice());
                transactionItems.add(transactionItem);
            }
        }

        body.setTransactionItems(transactionItems);

        viewModel.sendSuccessfulTransaction(body);

    }

    private void setListeners() {
        btnBack.setOnClickListener(c -> finish());
        tvBack.setOnClickListener(c -> finish());

        btnOzow.setOnClickListener(c -> checkoutOzow());
        tvOzow.setOnClickListener(c -> checkoutOzow());

        btnPayGate.setOnClickListener(c -> checkoutPayGate());
        tvPayGate.setOnClickListener(c -> checkoutPayGate());
    }

    private void checkoutOzow() {

        progressBar.setVisibility(View.VISIBLE);
        if (totalPrice == 0) {
            Utils.showToast(this, "Something went wrong. Please try again.");
        } else {

            viewModel.requestOzowPayment(totalPrice).observe(this, response -> {

                progressBar.setVisibility(View.GONE);
                if (response.getCode() >= 200 && response.getCode() < 300) {
                    if (response.getUrl() != null) {
                        ozowActivityResultLauncher.launch(new Intent(this, OzowActivity.class).putExtra("url", response.getUrl()));
                    } else {
                        if (response.getErrorMessage() != null) {
                            Utils.showToast(this, response.getErrorMessage());
                        }
                    }
                } else {
                    if (response.getErrorMessage() != null) {
                        Utils.showToast(this, response.getErrorMessage());
                    }
                }
            });
        }
    }

    private void checkoutPayGate() {
        Utils.log(createPayGateData(totalPrice, viewModel.getUser()));
        viewModel.requestPayGatePayment(totalPrice, viewModel.getUser());
        //startActivity(new Intent(this, PayGateActivity.class));
    }

    private void setObservers() {
        viewModel.getCheckoutItemsLiveData().observe(this, items -> {
            adapter = new CheckoutAdapter(this, items);
            setTotalPrice(items);
            rvCheckout.setAdapter(adapter);
        });
    }

    private void setTotalPrice(List<MerchantItem> items) {
        for (MerchantItem item : items) {
            totalPrice += item.getOrderQuantity() * item.getInventoryItem().getPrice();
        }
        tvTotalPrice.setText(getString(R.string.total_price, Utils.roundToTwoDecimal(totalPrice)));
    }

    private void initViews() {
        rvCheckout = findViewById(R.id.rv_checkout);
        btnBack = findViewById(R.id.btn_back);
        tvBack = findViewById(R.id.tv_back);
        tvTotalPrice = findViewById(R.id.tv_total_price);
        btnOzow = findViewById(R.id.btn_ozow);
        tvOzow = findViewById(R.id.tv_payment_ozow);
        btnPayGate = findViewById(R.id.btn_paygate);
        tvPayGate = findViewById(R.id.tv_payment_paygate);
        progressBar = findViewById(R.id.progress_bar_checkout);

        rvCheckout.setLayoutManager(new LinearLayoutManager(this));
    }
}