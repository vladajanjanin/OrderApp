package com.vladimirjanjanin.orderapp.utils;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vladimirjanjanin.orderapp.R;
import com.vladimirjanjanin.orderapp.data.models.InventoryItem;
import com.vladimirjanjanin.orderapp.data.models.MerchantItem;
import com.vladimirjanjanin.orderapp.interfaces.CheckoutClickListener;
import com.vladimirjanjanin.orderapp.interfaces.CustomPriceListener;
import com.vladimirjanjanin.orderapp.recyclerviews.ShoppingCartAdapter;

import java.util.List;

public class DialogUtils {


    public static void showItemInfoDialog(final Activity activity, InventoryItem item){

        LayoutInflater inflater = activity.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_item_info, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setView(dialogView);
        final AlertDialog dialog = builder.create();

        ImageView ivItemImage = dialogView.findViewById(R.id.iv_item_image);
        TextView tvItemName = dialogView.findViewById(R.id.tv_item_name);
        TextView tvItemPrice = dialogView.findViewById(R.id.tv_item_price);

        ivItemImage.setImageBitmap(Utils.getBitmapFromBase64(item.getIcon()));
        tvItemName.setText(item.getName());
        tvItemPrice.setText(activity.getString(R.string.price_format, item.getPrice()));

        dialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(activity.getColor(R.color.transparent)));
        dialog.show();
    }

    public static void showCheckoutDialog(final Activity activity, List<MerchantItem> checkoutItems, CheckoutClickListener listener) {

        LayoutInflater inflater = activity.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_checkout, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setView(dialogView);
        final AlertDialog dialog = builder.create();

        TextView tvEmpty = dialogView.findViewById(R.id.tv_empty_cart);
        TextView tvProceed = dialogView.findViewById(R.id.tv_proceed);
        RecyclerView rvCart = dialogView.findViewById(R.id.rv_cart);

        ShoppingCartAdapter adapter = new ShoppingCartAdapter(activity, checkoutItems);
        rvCart.setLayoutManager(new LinearLayoutManager(activity));
        rvCart.setAdapter(adapter);

        tvEmpty.setOnClickListener(c -> {
            listener.onEmptyCartClick();
            dialog.cancel();
        });

        tvProceed.setOnClickListener(c -> {
            listener.onProceedToCheckoutClick();
            dialog.cancel();
        });

        dialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(activity.getColor(R.color.transparent)));
        dialog.show();
    }

    public static void showCustomItemPriceDialog(final Activity activity, InventoryItem item, CustomPriceListener listener){

        LayoutInflater inflater = activity.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_custom_item_price, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setView(dialogView);
        final AlertDialog dialog = builder.create();

        EditText etItemPrice = dialogView.findViewById(R.id.et_item_price);
        etItemPrice.requestFocus();

        dialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(activity.getColor(R.color.transparent)));
        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "CANCEL", (dialog1, which) -> dialog1.dismiss());
        dialog.setButton(DialogInterface.BUTTON_POSITIVE, "CONFIRM", (dialog12, which) -> listener.onPriceConfirm(etItemPrice.getText().toString()));
        dialog.show();
    }
}
