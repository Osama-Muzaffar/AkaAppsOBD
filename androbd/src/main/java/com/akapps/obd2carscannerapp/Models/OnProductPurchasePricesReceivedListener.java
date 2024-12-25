package com.akapps.obd2carscannerapp.Models;

import com.android.billingclient.api.SkuDetails;

import java.util.List;

public interface OnProductPurchasePricesReceivedListener {
    void onPricesReceived (List<SkuDetails> skuDetailsList);
}
