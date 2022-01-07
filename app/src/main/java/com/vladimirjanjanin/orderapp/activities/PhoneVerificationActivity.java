package com.vladimirjanjanin.orderapp.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.vladimirjanjanin.orderapp.R;
import com.vladimirjanjanin.orderapp.utils.Utils;
import com.vladimirjanjanin.orderapp.viewmodels.LoginViewModel;

public class PhoneVerificationActivity extends AppCompatActivity {
    private LoginViewModel              viewModel;
    private ImageButton                 btnBack;
    private TextView                    tvBack;
    private EditText                    etCode;
    private Button                      btnVerify;
    private ProgressBar                 progressBar;
    private String                      phoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_verification);

        viewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        phoneNumber = getIntent().getStringExtra("phoneNumber");

        initViews();
        setListeners();
    }

    private void setListeners() {
        btnBack.setOnClickListener(c -> finish());
        tvBack.setOnClickListener(c -> finish());
        btnVerify.setOnClickListener(c -> {
            progressBar.setVisibility(View.VISIBLE);
            verifyCode();
        });
    }

    private void verifyCode() {
        viewModel.verifyCode(phoneNumber, etCode.getText().toString().trim()).observe(this, v -> {
            progressBar.setVisibility(View.GONE);
            if (v >= 200 && v < 300) {
                setResult(RESULT_OK);
                finish();
            } else {
                Utils.showToast(this, "Error!");
            }
        });
    }

    private void initViews() {
        btnBack = findViewById(R.id.btn_back);
        tvBack = findViewById(R.id.tv_back);
        etCode = findViewById(R.id.et_code);
        btnVerify = findViewById(R.id.btn_verify);
        progressBar = findViewById(R.id.progress_bar_verification);
    }
}