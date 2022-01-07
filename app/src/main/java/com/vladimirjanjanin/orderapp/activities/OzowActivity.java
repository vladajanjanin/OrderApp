package com.vladimirjanjanin.orderapp.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.net.http.SslError;
import android.os.Bundle;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.vladimirjanjanin.orderapp.R;
import com.vladimirjanjanin.orderapp.utils.Utils;


public class OzowActivity extends AppCompatActivity {
    private String                      url;
    private WebView                     webView;
    private ImageButton                 btnBack;
    private TextView                    tvBack;
    private ProgressBar                 progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ozow);

        initViews();
        setListeners();

        launchWebView();
    }

    private void setListeners() {
        btnBack.setOnClickListener(c -> {
            setResult(Activity.RESULT_OK);
            this.onBackPressed();
        });

        tvBack.setOnClickListener(c -> {
            setResult(Activity.RESULT_OK);
            this.onBackPressed();
        });
    }

    private void initViews() {
        webView = findViewById(R.id.webview);
        btnBack = findViewById(R.id.btn_back);
        tvBack = findViewById(R.id.tv_back);
        progressBar = findViewById(R.id.progress_bar_ozow);
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void launchWebView() {

        url = getIntent().getStringExtra("url");

        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());

        webView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    progressBar.setVisibility(View.GONE);
                }
            }
        });

        webView.loadUrl(url);

    }
}