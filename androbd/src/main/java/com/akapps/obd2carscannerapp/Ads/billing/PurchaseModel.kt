package com.akapps.obd2carscannerapp.Ads.billing

import com.android.billingclient.api.ProductDetails

data class PurchaseModel(val skuDetails: ProductDetails,var ischecked: Boolean)
