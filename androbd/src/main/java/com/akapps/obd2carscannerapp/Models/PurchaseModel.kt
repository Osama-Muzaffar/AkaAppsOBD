package com.akapps.obd2carscannerapp.Models

import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.SkuDetails

data class PurchaseModel(val skuDetails: ProductDetails,var ischecked: Boolean)
