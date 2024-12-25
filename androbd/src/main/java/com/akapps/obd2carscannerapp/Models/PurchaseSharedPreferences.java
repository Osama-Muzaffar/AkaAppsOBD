package com.akapps.obd2carscannerapp.Models;

import android.content.Context;
import android.content.SharedPreferences;

public class PurchaseSharedPreferences {

    private static final String PURCHASE_SHARED_PREFERENCES = "purchase-pref";
    private static final String KEY_ADS_PURCHASE_STATUS = "ads-purchase-status";
    private static final String KEY_CLEARMIL_PURCHASE_STATUS = "clear-mil-purchase-status";
    private static final String KEY_UNLOCK_ALL_FEATURES_PURCHASE_STATUS = "unlock-all-purchase-status";
    private static final String KEY_REMOVE_ADS_FEATURES_PURCHASE_STATUS = "remove-ads-purchase-status";
    private static final String KEY_UNLOCK_CLEAR_MIL_FOR_SIX_TIMES_PURCHASE_STATUS = "clear-mil-for-six-times-purchase-status";
    private static final String KEY_CLEAR_MIL_CLICKS_COUNTER = "clear-mil-click-counter";
    private static final String KEY_UNLOCK_CLEAR_MIL_BUNDLE_PURCHASE_PRICE = "clear-mil-bundle-purchase-price";
    private static final String KEY_UNLOCK_CLEAR_MIL_ONETIME_PURCHASE_PRICE = "clear-mil-onetime-purchase-price";
    private static final String KEY_WATCH_VIDEO_POINTS = "watch-video_points";

    private SharedPreferences sharedPreferences;
    private static PurchaseSharedPreferences instance;

    private PurchaseSharedPreferences(Context context) {
        this.sharedPreferences = context.getSharedPreferences(PURCHASE_SHARED_PREFERENCES , Context.MODE_PRIVATE);
    }

    public static PurchaseSharedPreferences getInstance(Context context) {
        if(instance == null){
            instance = new PurchaseSharedPreferences(context);
        }
        return instance;
    }

    public void putAdsPurchaseStatus (boolean purchaseStatus){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_ADS_PURCHASE_STATUS , purchaseStatus);
        editor.apply();
    }

    public boolean getAdsPurchaseStatus (){
        return sharedPreferences.getBoolean(KEY_ADS_PURCHASE_STATUS , false);
    }

    public void putClearMIlPurchaseStatus (boolean purchaseStatus){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_CLEARMIL_PURCHASE_STATUS , purchaseStatus);
        editor.apply();
    }

    public boolean getClearMILPurchaseStatus (){
        return sharedPreferences.getBoolean(KEY_CLEARMIL_PURCHASE_STATUS , false);
    }

    public void putUnlockAllFeaturesPurchaseStatus (boolean purchaseStatus){
        putAdsPurchaseStatus(purchaseStatus);
        putClearMIlPurchaseStatus(purchaseStatus);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_UNLOCK_ALL_FEATURES_PURCHASE_STATUS , purchaseStatus);
        editor.apply();
    }

    public boolean getUnlockAllFeaturesPurchaseStatus (){
        return sharedPreferences.getBoolean(KEY_UNLOCK_ALL_FEATURES_PURCHASE_STATUS , false);
    }

    public void putRemoveAdsPurchaseStatus (boolean purchaseStatus){
        putAdsPurchaseStatus(purchaseStatus);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_REMOVE_ADS_FEATURES_PURCHASE_STATUS , purchaseStatus);
        editor.apply();
    }

    public boolean getRemoveAdsPurchaseStatus (){
        return sharedPreferences.getBoolean(KEY_REMOVE_ADS_FEATURES_PURCHASE_STATUS , false);
    }

    public void putUnlockClearMilForSixTimesPurchaseStatus (boolean purchaseStatus){
        putClearMIlPurchaseStatus(purchaseStatus);
        updateClearMILClicksOnPurchased();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_UNLOCK_CLEAR_MIL_FOR_SIX_TIMES_PURCHASE_STATUS , purchaseStatus);
        editor.apply();
    }

    public boolean getUnlockClearMilForSixTimesPurchaseStatus (){
        return sharedPreferences.getBoolean(KEY_UNLOCK_CLEAR_MIL_FOR_SIX_TIMES_PURCHASE_STATUS , false);
    }

    public void increaseClearMILClicks (){
        int counter = getClearMilRemainingClicksCount();
        counter--;
        putClearMilRemainingClicksCount(counter);
    }

    public void updateClearMILClicksOnPurchased (){
        int counter = getClearMilRemainingClicksCount();
        counter =  counter + 5;
        putClearMilRemainingClicksCount(counter);

    }
    private void putClearMilRemainingClicksCount (int count){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(KEY_CLEAR_MIL_CLICKS_COUNTER , count);
        editor.apply();    }

    public int getClearMilRemainingClicksCount (){
        return sharedPreferences.getInt(KEY_CLEAR_MIL_CLICKS_COUNTER , 0);
    }

    public void putClearMilBundlePurchasePrice (String price){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_UNLOCK_CLEAR_MIL_BUNDLE_PURCHASE_PRICE , price);
        editor.apply();    }

    public String getClearMilBundlePurchasePrice (){
        return sharedPreferences.getString(KEY_UNLOCK_CLEAR_MIL_BUNDLE_PURCHASE_PRICE , "");
    }

    public void putClearMilOnetimePurchasePrice (String price){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_UNLOCK_CLEAR_MIL_ONETIME_PURCHASE_PRICE , price);
        editor.apply();    }

    public String getClearMilOnetimePurchasePrice (){
        return sharedPreferences.getString(KEY_UNLOCK_CLEAR_MIL_ONETIME_PURCHASE_PRICE , "");
    }

    public void putWatchVideoPoints (){
        int points = getWatchVideoPoints();
        points++;
        if (points == 5){
            int clearMILClicks = getClearMilRemainingClicksCount();
            clearMILClicks++;
            putClearMilRemainingClicksCount(clearMILClicks);
            putClearMIlPurchaseStatus(true);
            points = 0;
        }
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(KEY_WATCH_VIDEO_POINTS , points);
        editor.apply();
    }

    public int getWatchVideoPoints (){
        return sharedPreferences.getInt(KEY_WATCH_VIDEO_POINTS , 0);
    }
}
