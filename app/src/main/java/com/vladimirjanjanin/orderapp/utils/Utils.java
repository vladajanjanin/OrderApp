package com.vladimirjanjanin.orderapp.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.vladimirjanjanin.orderapp.R;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

public class Utils {
    private static String TAG = "OrderTag";

    public static void log(String message) {
        Log.d(TAG, message);
    }

    public static boolean isHttpCallSuccessful(int code) {
        return (code >= 200 && code < 300);
    }

    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    public static void showSnackbar(View root, Context context, boolean positive, String message) {
        Snackbar snackbar = Snackbar.make(context, root, message, Snackbar.LENGTH_SHORT);
        snackbar.getView().setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        if (positive) {
            snackbar.setBackgroundTint(context.getColor(R.color.green));
        } else {
            snackbar.setBackgroundTint(context.getColor(R.color.design_default_color_error));
        }
        snackbar.show();
    }

    public static Bitmap getBitmapFromBase64(String imageString) {
        byte[] decodedString = Base64.decode(imageString, Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        return bitmap;
    }

    public static double roundToTwoDecimal(Double value) {
        return (double) Math.round(value * 100.0) / 100.0;
    }

    public static String getSha512(String input) {
        try {
            // getInstance() method is called with algorithm SHA-512
            MessageDigest md = MessageDigest.getInstance("SHA-512");

            // digest() method is called
            // to calculate message digest of the input string
            // returned as array of byte
            byte[] messageDigest = md.digest(input.getBytes());

            // Convert byte array into signum representation
            BigInteger no = new BigInteger(1, messageDigest);

            // Convert message digest into hex value
            String hashtext = no.toString(16);

            // Add preceding 0s to make it 32 bit
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }

            // return the HashText
            return hashtext;
        }

        // For specifying wrong message digest algorithms
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }


    }

    public static String getRandomString(int length) {
        String charPool = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder result = new StringBuilder();
        Random rnd = new Random();
        while (result.length() < length) { // length of the random string.
            int index = (int) (rnd.nextFloat() * charPool.length());
            result.append(charPool.charAt(index));
        }

        return result.toString();
    }

    public static void saveCredentials(Activity activity, String email, String password) {
        SharedPreferences sharedPref = activity.getSharedPreferences(
                activity.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("login_email", email);
        editor.putString("login_password", password);
        editor.apply();
    }

    public static String getSavedEmail(Activity activity) {
        SharedPreferences sharedPref = activity.getSharedPreferences(
                activity.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        return sharedPref.getString("login_email", "");
    }

    public static String getSavedPassword(Activity activity) {
        SharedPreferences sharedPref = activity.getSharedPreferences(
                activity.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        return sharedPref.getString("login_password", "");
    }

    public static void removePassword(Activity activity) {
        SharedPreferences sharedPref = activity.getSharedPreferences(
                activity.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.remove("login_password");
        editor.commit();
    }

    public static String getCountryDialCode(Context context){
        String countryId = null;
        String countryDialCode = null;

        TelephonyManager telephonyMngr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        countryId = telephonyMngr.getSimCountryIso().toUpperCase();
        String[] arrCountryCode = context.getResources().getStringArray(R.array.DialingCountryCode);
        for (int i = 0; i < arrCountryCode.length; i++) {
            String[] arrDial = arrCountryCode[i].split(",");
            if(arrDial[1].trim().equals(countryId.trim())){
                countryDialCode = arrDial[0];
                break;
            }
        }
        return countryDialCode;
    }
}
