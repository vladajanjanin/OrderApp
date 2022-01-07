package com.vladimirjanjanin.orderapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.vladimirjanjanin.orderapp.R;
import com.vladimirjanjanin.orderapp.utils.Utils;
import com.vladimirjanjanin.orderapp.viewmodels.LoginViewModel;

import java.util.Arrays;

public class LoginActivity extends AppCompatActivity {
    private static final int        RC_SIGN_IN = 1;
    private LoginViewModel          viewModel;
    private EditText                etEmail;
    private EditText                etPassword;
    private Button                  btnLogin;
    private LoginButton             btnFacebook;
    private SignInButton            btnGoogle;
    private ProgressBar             progressBar;
    private TextView                tvRegister;
    private static final String     EMAIL = "email";
    private GoogleSignInClient      googleSignInClient;
    private CallbackManager         callbackManager;
    private ProfileTracker          profileTracker;
    private LoginManager            loginManager;
    private String                  fcmToken = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        viewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        initViews();
        setListeners();

        getFcmToken();
        configureGoogleSignIn();
        configureFacebookSignIn();

        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                Utils.log(currentProfile.getFirstName() + " " + currentProfile.getLastName());
            }
        };

        autoLogin();
    }

    private void getFcmToken() {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Utils.log("Fetching FCM registration token failed");
                            return;
                        }

                        // Get new FCM registration token
                        fcmToken = task.getResult();

                        // Log
                        Utils.log(fcmToken);
                        viewModel.setFcmToken(fcmToken);
                    }
                });
    }

    private void configureGoogleSignIn() {
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        googleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void googleSignIn() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void configureFacebookSignIn() {
        btnFacebook.setReadPermissions(Arrays.asList(EMAIL));
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Utils.log("ID: " + loginResult.getAccessToken().getUserId());
            }

            @Override
            public void onCancel() {
                Utils.log("Canceled");
            }

            @Override
            public void onError(FacebookException error) {
                Utils.log("Error: " + error.toString());
            }
        });
    }

    private void autoLogin() {
        etEmail.setText(Utils.getSavedEmail(this));
        etPassword.setText(Utils.getSavedPassword(this));

        if (!etPassword.getText().toString().equals("")) {
            progressBar.setVisibility(View.VISIBLE);
            login();
        }
    }

    private void login() {
        viewModel.login(etEmail.getText().toString(), etPassword.getText().toString()).observe(this, loginResult -> {
            progressBar.setVisibility(View.GONE);
            if (Utils.isHttpCallSuccessful(loginResult.getCode())) {
                Utils.saveCredentials(this, etEmail.getText().toString(), etPassword.getText().toString());
                viewModel.updateFcmToken();
                if (loginResult.getUser().getRole().equals("Merchant")) {
                    viewModel.getMerchantItems(loginResult.getUser().getId());
                    startActivity(new Intent(this, InventoryActivity.class));
                } else {
                    startActivity(new Intent(this, QrScanActivity.class));
                }
                finish();
            } else {
                Utils.showToast(this, loginResult.getMessage());
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        callbackManager.onActivityResult(requestCode, resultCode, data);
        Utils.log("Code: " + resultCode);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.
            Utils.showToast(this, "Welcome: " + account.getDisplayName() + " " + account.getEmail());

        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Utils.log("signInResult:failed code=" + e.getStatusCode());
        }
    }

    private void setListeners() {
        btnLogin.setOnClickListener(v -> {
            if (!TextUtils.isEmpty(etEmail.getText()) && !TextUtils.isEmpty(etPassword.getText())) {
                progressBar.setVisibility(View.VISIBLE);
                login();
            }
        });

        btnGoogle.setOnClickListener(c -> googleSignIn());

        tvRegister.setOnClickListener(c -> startActivity(new Intent(this, RegisterActivity.class)));
    }

    private void initViews() {
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        progressBar = findViewById(R.id.progress_bar_login);
        tvRegister = findViewById(R.id.tv_register);
        btnFacebook = findViewById(R.id.btn_facebook_login);
        btnGoogle = findViewById(R.id.btn_google_login);

        btnGoogle.setSize(SignInButton.SIZE_WIDE);
    }
}