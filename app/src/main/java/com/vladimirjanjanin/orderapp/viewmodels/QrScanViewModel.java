package com.vladimirjanjanin.orderapp.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.vladimirjanjanin.orderapp.data.Repository;

public class QrScanViewModel extends AndroidViewModel {
    private Repository repo;

    public QrScanViewModel(@NonNull Application application) {
        super(application);
        repo = Repository.getInstance();
    }

    public void refreshInventory(String id) {
        repo.refreshInventory(id);
    }

    public void setCurrentMerchantId(String res) {
        repo.setCurrentMerchantId(res);
    }
}
