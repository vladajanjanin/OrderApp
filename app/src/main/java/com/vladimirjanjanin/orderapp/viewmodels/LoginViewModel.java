package com.vladimirjanjanin.orderapp.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.google.gson.JsonObject;
import com.vladimirjanjanin.orderapp.data.Repository;
import com.vladimirjanjanin.orderapp.data.dtos.RegisterBody;
import com.vladimirjanjanin.orderapp.data.dtos.RegisterResponse;
import com.vladimirjanjanin.orderapp.data.models.LoginInfo;
import com.vladimirjanjanin.orderapp.data.models.User;

import okhttp3.ResponseBody;

public class LoginViewModel extends AndroidViewModel {
    private Repository repo;

    public LoginViewModel(@NonNull Application application) {
        super(application);
        repo = Repository.getInstance();
    }

    public LiveData<LoginInfo> login(String email, String password) {
        return repo.login(email, password);
    }

    public void getMerchantItems(String merchantId) {
        repo.getMerchantItems(merchantId);
    }

    public LiveData<RegisterResponse> register(String email, String password, String role, String firstName, String lastName, String phoneNumber) {
        RegisterBody body = new RegisterBody();
        body.setEmail(email);
        body.setPassword(password);
        body.setRole(role);
        body.setFirstName(firstName);
        body.setLastName(lastName);
        body.setPhoneNumber(phoneNumber);

        return repo.register(body);
    }

    public void setFcmToken(String fcmToken) {
        repo.setFcmToken(fcmToken);
    }

    public void updateFcmToken() {
        repo.updateFcmToken();
    }

    public LiveData<Integer> verifyPhoneNumber(String phoneNumber) {
        return repo.verifyPhoneNumber(phoneNumber);
    }

    public LiveData<Integer> verifyCode(String phoneNumber, String code) {
        return repo.verifyCode(phoneNumber, code);
    }
}
