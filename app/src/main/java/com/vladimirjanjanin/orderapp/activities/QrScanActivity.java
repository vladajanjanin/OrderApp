package com.vladimirjanjanin.orderapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.vladimirjanjanin.orderapp.R;
import com.vladimirjanjanin.orderapp.viewmodels.QrScanViewModel;

public class QrScanActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE = 1;
    private QrScanViewModel                 viewModel;
    private CodeScanner                     codeScanner;
    private CodeScannerView                 scannerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_scan);
        scannerView = findViewById(R.id.scanner_view);

        viewModel = new ViewModelProvider(this).get(QrScanViewModel.class);

        setupQR();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkCameraPermission();
    }

    private void setupQR() {
        codeScanner = new CodeScanner(this, scannerView);
        codeScanner.setDecodeCallback(result -> runOnUiThread(() -> {
            String res = result.getText();
            viewModel.setCurrentMerchantId(res);
            viewModel.getMerchantItems(res);
            startActivity(new Intent(this, InventoryActivity.class));
        }));
        scannerView.setOnClickListener(v -> codeScanner.startPreview());
    }

    private void checkCameraPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA},
                    PERMISSION_REQUEST_CODE);
        } else {
            codeScanner.startPreview();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                codeScanner.startPreview();
            }  else {
                finish();
            }
        }
    }
}