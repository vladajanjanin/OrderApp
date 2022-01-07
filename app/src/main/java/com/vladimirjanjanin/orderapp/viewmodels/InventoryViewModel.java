package com.vladimirjanjanin.orderapp.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.vladimirjanjanin.orderapp.data.Repository;
import com.vladimirjanjanin.orderapp.data.models.MerchantItem;

import java.util.List;

import okhttp3.ResponseBody;

public class InventoryViewModel extends AndroidViewModel {
    private Repository repo;

    public InventoryViewModel(@NonNull Application application) {
        super(application);
        repo = Repository.getInstance();
    }

    public LiveData<List<MerchantItem>> getInventoryLiveData() {
        return repo.getInventoryLiveData();
    }

    public void setCheckoutItemsLiveData(List<MerchantItem> checkoutItems) {
        repo.setCheckoutItemsLiveData(checkoutItems);
    }

    public boolean isMerchant() {
        return repo.isMerchant();
    }

    public LiveData<Integer> updateMerchantInventory(String merchantId, List<MerchantItem> merchantItems) {
        return repo.updateMerchantInventory(merchantId, merchantItems);
    }

    public String getUserId() {
        return repo.getUserId();
    }

    public void refreshInventory(String id) {
        repo.refreshInventory(id);
    }

    public String getCurrentMerchantId() {
        return repo.getCurrentMerchantId();
    }
}
