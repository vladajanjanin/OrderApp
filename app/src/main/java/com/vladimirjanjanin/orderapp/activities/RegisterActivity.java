package com.vladimirjanjanin.orderapp.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.vladimirjanjanin.orderapp.R;
import com.vladimirjanjanin.orderapp.data.dtos.RegisterResponse;
import com.vladimirjanjanin.orderapp.utils.Utils;
import com.vladimirjanjanin.orderapp.viewmodels.LoginViewModel;

import io.michaelrocks.libphonenumber.android.PhoneNumberUtil;

public class RegisterActivity extends AppCompatActivity {
    private static final int            AC_PHONE = 1;
    private ScrollView                  root;
    private LoginViewModel              viewModel;
    private EditText                    etFirstName;
    private EditText                    etLastName;
    private EditText                    etPhoneNumber;
    private EditText                    etEmail;
    private EditText                    etPassword;
    private EditText                    etConfirmPassword;
    private AppCompatButton             btnCustomer;
    private AppCompatButton             btnMerchant;
    private AppCompatButton             btnRegister;
    private ProgressBar                 progressBar;
    private TextView                    tvBack;
    private ImageButton                 btnBack;
    private boolean                     isCustomerChosen = false;
    private boolean                     isMerchantChosen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        viewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        initViews();
        setListeners();
    }

    private void setListeners() {
        btnCustomer.setOnClickListener(c -> chooseCustomer());
        btnMerchant.setOnClickListener(c -> chooseMerchant());
        btnRegister.setOnClickListener(c -> register());
        tvBack.setOnClickListener(c -> finish());
        btnBack.setOnClickListener(c -> finish());
    }

    private void register() {
        if (anyFieldEmpty()) {
            Utils.showToast(this, "Fields can't be empty!");
        } else if (!passwordsValid()) {
            Utils.showToast(this, "Passwords must match!");
        } else if (!isMerchantChosen && !isCustomerChosen) {
            Utils.showToast(this, "Please select a type of user you wish to be.");
        } else {
            if (isCustomerChosen) verifyPhoneNumber();
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    private void verifyPhoneNumber() {
        String phoneNumber = etPhoneNumber.getText().toString();
        String plus = "+";
        String processedPhoneNumber = "";

        String countryCode = "+" + Utils.getCountryDialCode(this);

        //if (!(phoneNumber.charAt(0) == plus.charAt(0))) processedPhoneNumber = "+" + countryCodeValue + phoneNumber;
        if (!(phoneNumber.charAt(0) == plus.charAt(0))) {
            processedPhoneNumber = countryCode + phoneNumber;
        }
        Utils.log(processedPhoneNumber);
        String finalProcessedPhoneNumber = processedPhoneNumber;
        viewModel.verifyPhoneNumber(processedPhoneNumber).observe(this, v -> {
            Utils.log(String.valueOf(v));
            progressBar.setVisibility(View.INVISIBLE);
            if (v >= 200 && v < 300) {
                startActivityForResult(new Intent(this, PhoneVerificationActivity.class).putExtra("phoneNumber", finalProcessedPhoneNumber), AC_PHONE);
            } else {
                Utils.showToast(this, "Error!");
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Utils.log("Result code: " + resultCode + " " + (resultCode == RESULT_OK));
        if (resultCode == RESULT_OK && requestCode == AC_PHONE) {

            Utils.log("Attempting registration...");
            String role = "";
            if (isCustomerChosen) role = "Customer";
            else role = "Merchant";

            viewModel.register(etEmail.getText().toString(),
                    etPassword.getText().toString(),
                    role,
                    etFirstName.getText().toString(),
                    etLastName.getText().toString(),
                    etPhoneNumber.getText().toString()).observe(this, this::processRegisterResponse);
        }
    }

    private void processRegisterResponse(RegisterResponse response) {
        progressBar.setVisibility(View.GONE);
        if (!(response.getCode() >= 200 && response.getCode() < 300)) {
            Utils.log("Error");
            Utils.showToast(this, getString(R.string.oops_something_went_wrong));
        } else {
            clearEditTexts();
            Utils.showSnackbar(root, this, true, "Successful registration!");
            //startActivity(new Intent(this, QrScanActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
        }
    }

    private void clearEditTexts() {
        etFirstName.setText("");
        etLastName.setText("");
        etPhoneNumber.setText("");
        etPassword.setText("");
        etConfirmPassword.setText("");
        etEmail.setText("");
    }

    private boolean passwordsValid() {
        return (etPassword.getText().toString().equals(etConfirmPassword.getText().toString()));
    }

    private boolean anyFieldEmpty() {
        if (etFirstName.getText().toString().equals("") ||
            etLastName.getText().toString().equals("") ||
            etPhoneNumber.getText().toString().equals("") ||
            etEmail.getText().toString().equals("") ||
            etPassword.getText().toString().equals("") ||
            etConfirmPassword.getText().toString().equals("")) return true;
        else return false;
    }

    private void chooseCustomer() {
        btnCustomer.setSelected(true);
        btnMerchant.setSelected(false);
        isCustomerChosen = true;
        isMerchantChosen = false;
    }

    private void chooseMerchant() {
        btnMerchant.setSelected(true);
        btnCustomer.setSelected(false);
        isMerchantChosen = true;
        isCustomerChosen = false;
    }


    private void initViews() {
        etFirstName = findViewById(R.id.et_first_name);
        etLastName = findViewById(R.id.et_last_name);
        etPhoneNumber = findViewById(R.id.et_phone_number);
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        etConfirmPassword = findViewById(R.id.et_password_confirm);
        root = findViewById(R.id.registration_root);
        tvBack = findViewById(R.id.tv_back);
        btnBack = findViewById(R.id.btn_back);

        btnCustomer = findViewById(R.id.btn_customer);
        btnMerchant = findViewById(R.id.btn_merchant);
        btnRegister = findViewById(R.id.btn_register);

        progressBar = findViewById(R.id.progress_bar_registration);

    }
}