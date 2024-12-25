package com.akapps.obd2carscannerapp.Models;

import static com.android.billingclient.api.BillingClient.SkuType.INAPP;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.AcknowledgePurchaseResponseListener;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeParams;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesResponseListener;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.akapps.obd2carscannerapp.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class InAppPurchase implements PurchasesUpdatedListener, BillingClientStateListener, SkuDetailsResponseListener, AcknowledgePurchaseResponseListener, PurchasesResponseListener, ConsumeResponseListener {

    private Activity mContext;
    private BillingClient billingClient;
    private String PRODUCT_ID;
    private String base64Key;
    private boolean requestPurchasing;
    private boolean isConsumeablePurchasing;
    private boolean isRequestPricicng;

    private boolean initConn ;
    private boolean connStablished ;

    private PurchaseSharedPreferences purchasePrefs;
    private static Object object = new Object();
    private static InAppPurchase instance = null;

    private static AlertDialog mDiscoverStartDialog;


    private OnPurchaseFinishedListener mListener;
    private OnProductPurchasePricesReceivedListener pricesReceivedListener;

    private InAppPurchase(Activity mContext, String base64Key ) {
        this.mContext = mContext;
        this.base64Key = base64Key;
        initConn = true;
    }

    public static InAppPurchase getInstance(Activity mContext,String base64Key ) {
        if(instance == null)
            instance = new InAppPurchase(mContext , base64Key );
        return instance;
    }

    public void setOnPurchaseFinishedListener (OnPurchaseFinishedListener listener){
        mListener = listener;
    }

    public void setPricesReceivedListener(OnProductPurchasePricesReceivedListener pricesReceivedListener) {
        this.pricesReceivedListener = pricesReceivedListener;
    }

    public boolean getConnStatus (){
        return connStablished;
    }

    @Override
    public void onPurchasesUpdated(@NonNull BillingResult billingResult, @Nullable List<Purchase> list) {
        if(list != null && billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK){
            if(billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && list.size() > 0){
                if(isConsumeablePurchasing)
                    handleConsumeablePurchases(list);
                else
                    handlePurchases(list);
            }else if(billingResult.getResponseCode() == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED){
                billingClient.queryPurchasesAsync(INAPP , InAppPurchase.this);
            }else if(billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED){

            }
        }
    }

    @Override
    public void onBillingServiceDisconnected() {
        if(initConn)
             billingClient.startConnection(this);
    }

    @Override
    public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
        if(billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK){
            connStablished = true;
            billingClient.queryPurchasesAsync(INAPP , InAppPurchase.this);
        }
    }


    @Override
    public void onSkuDetailsResponse(@NonNull BillingResult billingResult, @Nullable List<SkuDetails> list) {

        Log.d("inapp", "onSkuDetailsResponse: track 4"+list);

        if(list != null && list.size() > 0){
            if (isRequestPricicng){
                Log.d("inapp", "onSkuDetailsResponse: index 0 "+list.get(0));
                sendPricesRequestResponseToHolderActivity(list);
            }else {
                Log.d("inapp", "onSkuDetailsResponse: index 1 "+list.get(0));
                BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                        .setSkuDetails(list.get(0))
                        .build();
                billingClient.launchBillingFlow(mContext , billingFlowParams);
            }
            return;
        }
        if (isRequestPricicng)
            sendPricesRequestResponseToHolderActivity(null);
        else
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    if (mListener != null)
                        mListener
                            .onPurchaseFinished(false, mContext.getString(R.string.Purchase_ERROR));
                }
            });

    }

    @Override
    public void onAcknowledgePurchaseResponse(@NonNull BillingResult billingResult) {
        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK){
            sendResponseToHolderActivity(true, mContext.getString(R.string.Purchase_OK));
        }else {
            sendResponseToHolderActivity(false, mContext.getString(R.string.Purchase_ERROR));
        }

    }

    void sendResponseToHolderActivity (boolean purchaseStatus, String result){
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                createAndShowPurchaseFinishedDialog(purchaseStatus,
                        result);
            }
        });
    }

    void sendPricesRequestResponseToHolderActivity (List<SkuDetails> skuDetailsList){
        isRequestPricicng = false;
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                pricesReceivedListener.onPricesReceived(skuDetailsList);
            }
        });
    }

    @Override
    public void onQueryPurchasesResponse(@NonNull BillingResult billingResult, @NonNull List<Purchase> list) {
        Log.d("inapp", "unlockAllFeatures: track 4");

        if(billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK){
            Log.d("inapp", "unlockAllFeatures: track 5");

            if (list.size() > 0 ){
                Log.d("inapp", "unlockAllFeatures: track 6");
                if(!isConsumeablePurchasing){
                    Log.d("inapp", "unlockAllFeatures: track 7");

                    handlePurchases(list);
                }
                else
                    handleConsumeablePurchases(list);
            }
            else
                Log.d("inapp", "onQueryPurchasesResponse: track 8");


        }
    }

    @Override
    public void onConsumeResponse(@NonNull BillingResult billingResult, @NonNull String s) {
        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
            mListener
                    .onPurchaseFinished(true, mContext.getString(R.string.Purchase_OK));
        }else {
            switch (billingResult.getResponseCode()){
                case BillingClient.BillingResponseCode.BILLING_UNAVAILABLE:
                case BillingClient.BillingResponseCode.ERROR:
                case BillingClient.BillingResponseCode.USER_CANCELED:
                case BillingClient.BillingResponseCode.ITEM_NOT_OWNED:{
                    mListener
                            .onPurchaseFinished(true, mContext.getString(R.string.Purchase_ERROR));
                }
            }
        }
    }

    public void initPurchasing (){
        Log.d("track", "initPurchasing: track 1");
        billingClient = BillingClient.newBuilder(mContext)
                .enablePendingPurchases()
                .setListener(this)
                .build();
        billingClient.startConnection(this);

    }

    public void finishPurchasing (){
        initConn = false;
        connStablished = false;
        if (isRequestPricicng){
            isRequestPricicng = false;
            setOnPurchaseFinishedListener(null);
        }
        if (billingClient != null){
            if(!billingClient.isReady())
                return;
            billingClient.endConnection();
        }
    }

    public void requestPurchasing(String productId, boolean isConsumeablePurchasing){
        Log.d("inapp", "unlockAllFeatures: track 3 "+productId);

        this.PRODUCT_ID = productId;

        this.isConsumeablePurchasing = isConsumeablePurchasing;

        List<String> skuList = new ArrayList<>();
        skuList.add(productId);
        SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
        params.setSkusList(skuList).setType(INAPP);
        billingClient.querySkuDetailsAsync(params.build() , this);
    }

    public void requestProductPrices(List<String> skuList, boolean isRequestPricing){
        this.isRequestPricicng = isRequestPricing;
        SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
        params.setSkusList(skuList).setType(INAPP);
        Log.d("track", "requestProductPrices: size "+skuList.get(0));
        billingClient.querySkuDetailsAsync(params.build() , this);
    }

    private void handlePurchases(List<Purchase>  purchases) {
        for(Purchase purchase:purchases) {
            //if item is adsPurchased

            if (purchase.getSkus().get(0) != null) {
                if (PRODUCT_ID.equals(purchase.getSkus().get(0)) && purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
              /*  if (!verifyValidSignature(purchase.getOriginalJson(), purchase.getSignature())) {
                    // Invalid purchase
                    // show error to user
                    //Toast.makeText(getApplicationContext(), "Error : Invalid Purchase", Toast.LENGTH_SHORT).show();
                    mListener.onPurchaseFinished(false, mContext.getString(R.string.Purchase_INVALID));
                    return;
                }*/
                    // else purchase is valid
                    //if item is adsPurchased and not acknowledged
                    if (!purchase.isAcknowledged()) {

                        AcknowledgePurchaseParams acknowledgePurchaseParams =
                                AcknowledgePurchaseParams.newBuilder()
                                        .setPurchaseToken(purchase.getPurchaseToken())
                                        .build();
                        billingClient.acknowledgePurchase(acknowledgePurchaseParams, this);

                        mListener.onPurchaseFinished(true, mContext.getString(R.string.Purchase_OK));
                    }
                    //else item is adsPurchased and also acknowledged
                    else {
                        // Grant entitlement to the user on item purchase
                        // restart activity
                        mListener.onPurchaseFinished(true, mContext.getString(R.string.Purchase_OK));

                    }
                } else if (PRODUCT_ID.equals(purchase.getSkus().get(0)) && purchase.getPurchaseState() == Purchase.PurchaseState.PENDING) {
                    mListener.onPurchaseFinished(false, mContext.getString(R.string.Purchase_PENDING));
                }
                //if purchase is unknown
                else if (PRODUCT_ID.equals(purchase.getSkus().get(0)) && purchase.getPurchaseState() == Purchase.PurchaseState.UNSPECIFIED_STATE) {
                    //    purchaseStatus.setText("Purchase Status : Not Purchased");
                    //  purchaseButton.setVisibility(View.VISIBLE);
                    // Toast.makeText(getApplicationContext(), "Purchase Status Unknown", Toast.LENGTH_SHORT).show();
                    mListener.onPurchaseFinished(false, mContext.getString(R.string.Purchase_UNKNOWN));

                }
            }
        }
    }

    private void handleConsumeablePurchases(List<Purchase>  purchases) {
        for(Purchase purchase:purchases) {
            //if item is adsPurchased
            if(PRODUCT_ID.equals(purchase.getSkus().get(0)) && purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED){
                ConsumeParams consumeParams =
                        ConsumeParams.newBuilder()
                                .setPurchaseToken(purchase.getPurchaseToken())
                                .build();
                billingClient.consumeAsync(consumeParams, this);
            }

        }
    }


    private boolean verifyValidSignature(String signedData, String signature) {
        try {
            // To get key go to Developer Console > Select your app > Development Tools > Services & APIs.
            //   String base64Key = "Add Your Key Here";
            return Security.verifyPurchase(base64Key, signedData, signature);
        } catch (IOException e) {
            return false;
        }
    }

    private void createAndShowPurchaseFinishedDialog (boolean purchaseStatus, String result){

        View progressDialogView = LayoutInflater.from(mContext).inflate(R.layout.purchase_ok_dialog_view , null);
        Button btnContinue = progressDialogView.findViewById(R.id.dialogView_purchaseOKBtn);
        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onPurchaseFinished(purchaseStatus, result);
            }
        });

        if ( !((Activity) mContext).isFinishing())
        {
            AlertDialog.Builder builder = new AlertDialog.Builder( mContext);
            builder.setView(progressDialogView);
            mDiscoverStartDialog = builder.create();
            mDiscoverStartDialog.setCancelable(false);
            mDiscoverStartDialog.show();
        }



    }

}
