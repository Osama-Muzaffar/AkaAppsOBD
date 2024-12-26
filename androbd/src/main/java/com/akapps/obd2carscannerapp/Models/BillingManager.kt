package com.akapps.obd2carscannerapp.Models

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.util.Log
import android.widget.Toast
import com.akapps.obd2carscannerapp.Models.Security.verifyPurchase
import com.akapps.obd2carscannerapp.R
import com.akapps.obd2carscannerapp.Trips.PairedDeviceSharedPreference
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ConsumeParams
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import com.android.billingclient.api.SkuDetails
import com.android.billingclient.api.SkuDetailsParams
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

/*
class BillingManager(private val context: Context, private val purchaseListner: BillingListner?) : PurchasesUpdatedListener {

    interface  BillingListner{
        open fun onPurchaseFinished(purchase: Purchase)
        open fun onPurchaseFailed(messages: String)
    }
    private val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    private var billingClient: BillingClient = BillingClient.newBuilder(context)
        .setListener(this)
        .enablePendingPurchases()
        .build()

    private var skuDetailsList: List<SkuDetails>? = null

    fun startConnection(single: Boolean) {
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                Log.d("Billing_Manager", "onBillingSetupFinished: ${billingResult.debugMessage}")
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    Log.d("Billing_Manager", "BillingResponseCode OK")
                    if (single) {
                        queryOneSkuDetails()
                    } else {
                        querySkuDetails()
                    }
                } else {
                    Log.e("Billing_Manager", "Billing setup failed: ${billingResult.debugMessage}")
                }
            }

            override fun onBillingServiceDisconnected() {
                Log.d("Billing_Manager", "onBillingServiceDisconnected: Attempting to reconnect")
                // Optionally retry connection or notify the user
            }
        })
    }

    fun querySkuDetails() {
        val skuList= listOf(
            context.getString(R.string.in_app_purchase_unlock_all_features_product_id),
            context.getString(R.string.in_app_purchase_remove_ads_product_id),
            context.getString(R.string.in_app_purchase_clear_mil_for_six_times_product_id),
        )
//        val skuList = listOf("com.obd.inapp", "com.obdblue.allpremium", "com.obd.milbundle") // Replace with actual product IDs
        val params = SkuDetailsParams.newBuilder()
        params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP)

        billingClient.querySkuDetailsAsync(params.build()) { billingResult, skuDetails ->
            Log.d("Billing_Manager", "querySkuDetailsAsync: ${billingResult.debugMessage}")
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && skuDetails != null) {
                Log.d("Billing_Manager", "skuDetails size: ${skuDetails.size}")
                skuDetailsList = skuDetails
            } else {
                Log.e("Billing_Manager", "skuDetails query failed: ${billingResult.debugMessage}")
            }
        }
    }

    fun queryOneSkuDetails() {
//        val skuList = listOf("mil.onetime.purchase") // Replace with actual product IDs
        val skuList = listOf(context.getString(R.string.in_app_purchase_clear_mil_for_one_time_product_id)) // Replace with actual product IDs
        val params = SkuDetailsParams.newBuilder()
        params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP)

        billingClient.querySkuDetailsAsync(params.build()) { billingResult, skuDetails ->
            Log.d("Billing_Manager", "querySkuDetailsAsync: ${billingResult.debugMessage}")
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && skuDetails != null) {
                Log.d("Billing_Manager", "skuDetails size: ${skuDetails.size}")
                skuDetailsList = skuDetails
            } else {
                Log.e("Billing_Manager", "skuDetails query failed: ${billingResult.debugMessage}")
            }
        }
    }

    fun getSkuDetailsList(): List<SkuDetails>? {
        return skuDetailsList
    }

    fun launchBillingFlow(activity: Activity, skuDetail: SkuDetails) {
        val billingFlowParams = BillingFlowParams.newBuilder()
            .setSkuDetails(skuDetail)
            .build()
        billingClient.launchBillingFlow(activity, billingFlowParams)
    }

    fun restorePurchases(purchaseListner: BillingListner?) {
        Log.d("Billing_Manager", "Trying to restore purchase")
        billingClient.queryPurchasesAsync(BillingClient.SkuType.INAPP) { billingResult, purchases ->
            Log.d("Billing_Manager", "restorePurchases: ${billingResult.debugMessage}")
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
                if(purchases.isEmpty()){
                    Log.d("Billing_Manager", "empty list: removing all purchases prefs")
                    PairedDeviceSharedPreference.getInstance(context).removeAllList()
                }
                else {
                    for (purchase in purchases) {
                        Log.d("Billing_Manager", "Handling Purchases")
                        handlePurchase(purchase)
                        purchaseListner?.onPurchaseFinished(purchase)
                    }
                }
            } else {
                purchaseListner?.onPurchaseFailed("Failed to purchase")
                Log.e("Billing_Manager", "Failed to restore purchases: ${billingResult.debugMessage}")
            }
        }
    }

    override fun onPurchasesUpdated(billingResult: BillingResult, purchases: List<Purchase>?) {
        Log.d("Billing_Manager", "onPurchasesUpdated: ${billingResult.debugMessage}")
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            for (purchase in purchases) {
                handlePurchase(purchase)
                purchaseListner?.onPurchaseFinished(purchase)
            }
        } else {
            Log.e("Billing_Manager", "Purchase update failed: ${billingResult.debugMessage}")
            purchaseListner?.onPurchaseFailed("Purchase update failed: ${billingResult.debugMessage}")
        }
    }

    private fun handlePurchase(purchase: Purchase) {

        if (!verifyValidSignature(purchase.originalJson, purchase.signature)) {
            // Invalid purchase
            // show error to user
            Toast.makeText(context, "Error : Invalid Purchase", Toast.LENGTH_SHORT)
                .show()
            return
        }
        if (!purchase.isAcknowledged) {
            val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                .setPurchaseToken(purchase.purchaseToken)
                .build()

            billingClient.acknowledgePurchase(acknowledgePurchaseParams) { billingResult ->
                Log.d("Billing_Manager", "acknowledgePurchase: ${billingResult.debugMessage}")
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    // Purchase acknowledged
                    savePurchaseToSharedPrefs(purchase.skus.first())
                } else {
                    Log.e("Billing_Manager", "Failed to acknowledge purchase: ${billingResult.debugMessage}")
                }
            }
        }
    }

    fun endConnection() {
        billingClient.endConnection()
    }

    private fun savePurchaseToSharedPrefs(sku: String) {
        val editor = prefs.edit()
        editor.putString("last_purchase_sku", sku)
        if(sku.equals(context.getString(R.string.in_app_purchase_clear_mil_for_six_times_product_id))){
            editor.putInt("mil_left", 6)
        }
        editor.apply()
    }

    fun getSavedPurchase(): String? {
        return prefs.getString("last_purchase_sku", null)
    }

    fun checkAndRestorePurchases() {
        val savedSku = getSavedPurchase()
        if (savedSku != null) {
            Log.d("Billing_Manager", "Restoring purchase for SKU: $savedSku")
            restorePurchases(null) // Optionally call restorePurchases to confirm purchase status
        } else {
            Log.d("Billing_Manager", "No saved purchase found")
        }
    }
    private fun verifyValidSignature(signedData: String, signature: String): Boolean {
        return try {
            // To get key go to Developer Console > Select your app > Development Tools > Services & APIs.
            //   String base64Key = "Add Your Key Here";
            verifyPurchase(
                context.getResources().getString(R.string.base64key),
                signedData,
                signature
            )
        } catch (e: IOException) {
            false
        }
    }
    fun clearPurchase(skuId: String) {
        billingClient.queryPurchasesAsync(BillingClient.SkuType.INAPP) { billingResult, purchases ->
            Log.d("Billing_Manager", "clearPurchase: ${billingResult.debugMessage}")
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
                val purchaseToClear = purchases.find { it.skus.contains(skuId) }
                if (purchaseToClear != null) {
                    if (purchaseToClear.isAcknowledged) {
                        // If purchase is already acknowledged, consume it
                        val consumeParams = ConsumeParams.newBuilder()
                            .setPurchaseToken(purchaseToClear.purchaseToken)
                            .build()

                        billingClient.consumeAsync(consumeParams) { consumeResult, _ ->
                            Log.d("Billing_Manager", "consumePurchase: ${consumeResult.debugMessage}")
                            if (consumeResult.responseCode == BillingClient.BillingResponseCode.OK) {
                                // Successfully consumed the purchase
                                Log.d("Billing_Manager", "Successfully consumed purchase for SKU: $skuId")
                                removePurchaseFromSharedPrefs(skuId)
                            } else {
                                Log.e("Billing_Manager", "Failed to consume purchase: ${consumeResult.debugMessage}")
                            }
                        }
                    } else {
                        // If purchase is not acknowledged, acknowledge it first
                        val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                            .setPurchaseToken(purchaseToClear.purchaseToken)
                            .build()

                        billingClient.acknowledgePurchase(acknowledgePurchaseParams) { billingResult ->
                            Log.d("Billing_Manager", "acknowledgePurchase: ${billingResult.debugMessage}")
                            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                                Log.d("Billing_Manager", "Purchase acknowledged for SKU: $skuId")
                                // Now consume the purchase
                                val consumeParams = ConsumeParams.newBuilder()
                                    .setPurchaseToken(purchaseToClear.purchaseToken)
                                    .build()

                                billingClient.consumeAsync(consumeParams) { consumeResult, _ ->
                                    Log.d("Billing_Manager", "consumePurchase: ${consumeResult.debugMessage}")
                                    if (consumeResult.responseCode == BillingClient.BillingResponseCode.OK) {
                                        // Successfully consumed the purchase
                                        Log.d("Billing_Manager", "Successfully consumed purchase for SKU: $skuId")
                                        removePurchaseFromSharedPrefs(skuId)
                                    } else {
                                        Log.e("Billing_Manager", "Failed to consume purchase: ${consumeResult.debugMessage}")
                                    }
                                }
                            } else {
                                Log.e("Billing_Manager", "Failed to acknowledge purchase: ${billingResult.debugMessage}")
                            }
                        }
                    }
                } else {
                    Log.d("Billing_Manager", "No purchase found for SKU: $skuId")
                }
            }
            else {
                Log.e("Billing_Manager", "Failed to query purchases: ${billingResult.debugMessage}")
            }
        }
    }

    private fun removePurchaseFromSharedPrefs(skuId: String) {
        val editor = prefs.edit()
        if (skuId.equals(context.getString(R.string.in_app_purchase_clear_mil_for_six_times_product_id))) {
            editor.remove("mil_left")
        }
        editor.remove("last_purchase_sku")
        editor.apply()
    }

}
*/


class BillingManager(private val context: Context, private val purchaseListener: BillingListner?) : PurchasesUpdatedListener {

    interface BillingListner {  
        fun onPurchaseFinished(purchase: Purchase,isSub: Boolean)
        fun onPurchaseFailed(message: String)
    }

    private var isSub: Boolean= false
    private val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    private var billingClient: BillingClient = BillingClient.newBuilder(context)
        .setListener(this)
        .enablePendingPurchases()
        .build()

//    private var skuDetailsList: MutableList<SkuDetails> = mutableListOf()
    private var skuDetailsList: MutableList<ProductDetails> = mutableListOf()

    fun startConnection(single: Boolean,isSub: Boolean=false) {
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                Log.d("Billing_Manager", "onBillingSetupFinished: ${billingResult.debugMessage}")
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    Log.d("Billing_Manager", "BillingResponseCode OK")
                    this@BillingManager.isSub= isSub
                    Log.d("startConnection", "isSub: $isSub")
                    Log.d("startConnection", "single: $single")
                    if(isSub){
                        querySubscriptionDetails(single)
                    }
                    else {
                        if (single) {
                            queryOneSkuDetails()
                        } else {
                            querySkuDetails()
                        }
                    }
                } else {
                    Log.e("Billing_Manager", "Billing setup failed: ${billingResult.debugMessage}")
                }
            }

            override fun onBillingServiceDisconnected() {
                Log.d("Billing_Manager", "onBillingServiceDisconnected: Attempting to reconnect")
            }
        })
    }

    // Query one-time purchases (as before)
    fun querySkuDetails() {
        val productList = listOf(
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(context.getString(R.string.in_app_purchase_clear_mil_for_one_time_product_id)) // Replace with your product ID
                .setProductType(BillingClient.ProductType.INAPP) // For one-time purchases
                .build()
        )

        val paramstest = QueryProductDetailsParams.newBuilder()
            .setProductList(productList)
            .build()

        billingClient.queryProductDetailsAsync(paramstest) { billingResult, productDetailsList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && productDetailsList.isNotEmpty()) {
                productDetailsList.forEach { productDetails ->
                    println("Product details: ${productDetails.title}")
                }
                skuDetailsList=productDetailsList

            } else {
                println("Failed to fetch product details: ${billingResult.debugMessage}")
            }
        }


    }

    fun queryOneSkuDetails() {
//        val skuList = listOf("mil.onetime.purchase") // Replace with actual product IDs
        val productList = listOf(
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(context.getString(R.string.in_app_purchase_clear_mil_for_one_time_product_id)) // Replace with your product ID
                .setProductType(BillingClient.ProductType.INAPP) // For one-time purchases
                .build()
        )

        val paramstest = QueryProductDetailsParams.newBuilder()
            .setProductList(productList)
            .build()

        billingClient.queryProductDetailsAsync(paramstest) { billingResult, productDetailsList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && productDetailsList.isNotEmpty()) {
                productDetailsList.forEach { productDetails ->
                    println("Product details: ${productDetails.title}")
                }
                skuDetailsList=productDetailsList

            } else {
                println("Failed to fetch product details: ${billingResult.debugMessage}")
            }
        }

    }
    // Function to query SKUs synchronously
    private suspend fun querySkuDetails(skuList: List<String>, skuType: String): List<SkuDetails> =
        suspendCoroutine { continuation ->
            val params = SkuDetailsParams.newBuilder()
                .setSkusList(skuList)
                .setType(skuType)
                .build()

            billingClient.querySkuDetailsAsync(params) { billingResult, skuDetails ->
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && skuDetails != null) {
                    Log.d("Billing_Manager", "querySkuDetailsAsync: ${billingResult.debugMessage}, size: ${skuDetails.size}")
                    continuation.resume(skuDetails)
                } else {
                    Log.e("Billing_Manager", "querySkuDetailsAsync failed: ${billingResult.debugMessage}")
                    continuation.resumeWithException(
                        Exception("SKU query failed: ${billingResult.debugMessage}")
                    )
                }
            }
        }
    // Query subscriptions (new method)
   /* fun querySubscriptionDetails(single: Boolean) {
        if(single){
            CoroutineScope(Dispatchers.IO).launch {
                val skupurchaseList = listOf(
                    context.getString(R.string.in_app_purchase_clear_mil_for_one_time_product_id)
                )

                val skuList = listOf(
                    context.getString(R.string.in_app_subscription_monthly_product_id),
                    context.getString(R.string.in_app_subscription_yearly_product_id),
                    context.getString(R.string.in_app_subscription_lifetime_product_id)
                )

                // Ensure skuDetailsList is cleared before adding items
                skuDetailsList!!.clear()

                try {
                    // Query one-time purchase SKUs first
                    val inAppSkuDetails = querySkuDetails(skupurchaseList, BillingClient.SkuType.INAPP)
                    skuDetailsList!!.addAll(inAppSkuDetails)

                    // Query subscription SKUs second
                    val subscriptionSkuDetails = querySkuDetails(skuList, BillingClient.SkuType.SUBS)
                    skuDetailsList!!.addAll(subscriptionSkuDetails)

                    Log.d("Billing_Manager", "Final skuDetailsList size = ${skuDetailsList!!.size}")

                } catch (e: Exception) {
                    Log.e("Billing_Manager", "Error querying SKU details: ${e.message}", e)
                }
            }

        }
        else{
            val skupurchaseList = listOf(
//                context.getString(R.string.in_app_subscription_lifetime_product_id)
                context.getString(R.string.in_app_subscription_lifetime_product_id)
            )

            val purchaseparams = SkuDetailsParams.newBuilder()
                .setSkusList(skupurchaseList)
                .setType(BillingClient.SkuType.INAPP)  // For one-time purchases
                .build()

            billingClient.querySkuDetailsAsync(purchaseparams) { billingResult, skuDetails ->
                Log.d("Billing_Manager", "querySkuDetailsAsync: ${billingResult.debugMessage}")
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && skuDetails != null) {
                    Log.d("Billing_Manager", "adding into list purchase skuDetails size = ${skuDetails.size}")
                    skuDetailsList!!.addAll(skuDetails)

                } else {
                    Log.e("Billing_Manager", "skuDetails query failed: ${billingResult.debugMessage}")
                }
            }
            val skuList = listOf(
                context.getString(R.string.in_app_subscription_monthly_product_id),
                context.getString(R.string.in_app_subscription_yearly_product_id)
            )

            val params = SkuDetailsParams.newBuilder()
                .setSkusList(skuList)
                .setType(BillingClient.SkuType.SUBS)  // For subscriptions
                .build()

            billingClient.querySkuDetailsAsync(params) { billingResult, skuDetails ->
                Log.d("Billing_Manager", "querySubscriptionDetailsAsync: ${billingResult.debugMessage}")
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && skuDetails != null) {
                    Log.d("Billing_Manager", "adding into list subscription skuDetails size = ${skuDetails.size}")
                    skuDetailsList!!.addAll(skuDetails)
                } else {
                    Log.e("Billing_Manager", "Subscription query failed: ${billingResult.debugMessage}")
                }
            }
        }

    }
*/
    fun querySubscriptionDetails(single: Boolean) {
        if(single){
            CoroutineScope(Dispatchers.IO).launch {

                val productListpurchases = listOf(
                    QueryProductDetailsParams.Product.newBuilder()
                        .setProductId(context.getString(R.string.in_app_purchase_clear_mil_for_one_time_product_id)) // Replace with your product ID
                        .setProductType(BillingClient.ProductType.INAPP) // For one-time purchases
                        .build(),
                )

                val paramspurchase = QueryProductDetailsParams.newBuilder()
                    .setProductList(productListpurchases)
                    .build()

                billingClient.queryProductDetailsAsync(paramspurchase) { billingResult, productDetailsList ->
                    if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && productDetailsList.isNotEmpty()) {
                        productDetailsList.forEach { productDetails ->
                            println("Product details: ${productDetails.title}")
                        }
                        skuDetailsList.addAll(productDetailsList)

                    } else {
                        println("Failed to fetch product details: ${billingResult.debugMessage}")
                    }
                }




                val productList = listOf(
                    QueryProductDetailsParams.Product.newBuilder()
                        .setProductId(context.getString(R.string.in_app_subscription_monthly_product_id)) // Replace with your product ID
                        .setProductType(BillingClient.ProductType.SUBS) // For one-time purchases
                        .build(),
                    QueryProductDetailsParams.Product.newBuilder()
                        .setProductId(context.getString(R.string.in_app_subscription_yearly_product_id)) // Replace with your product ID
                        .setProductType(BillingClient.ProductType.SUBS) // For one-time purchases
                        .build()
                )

                val paramstest = QueryProductDetailsParams.newBuilder()
                    .setProductList(productList)
                    .build()

                billingClient.queryProductDetailsAsync(paramstest) { billingResult, productDetailsList ->
                    if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && productDetailsList.isNotEmpty()) {
                        productDetailsList.forEach { productDetails ->
                            println("Product details: ${productDetails.title}")
                        }
                        skuDetailsList.addAll(productDetailsList)

                    } else {
                        println("Failed to fetch product details: ${billingResult.debugMessage}")
                    }
                }

            }

        }
        else{


            val productListpurchases = listOf(
                QueryProductDetailsParams.Product.newBuilder()
                    .setProductId(context.getString(R.string.in_app_subscription_lifetime_product_id)) // Replace with your product ID
                    .setProductType(BillingClient.ProductType.INAPP) // For one-time purchases
                    .build(),
            )

            val paramspurchase = QueryProductDetailsParams.newBuilder()
                .setProductList(productListpurchases)
                .build()

            billingClient.queryProductDetailsAsync(paramspurchase) { billingResult, productDetailsList ->
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && productDetailsList.isNotEmpty()) {
                    productDetailsList.forEach { productDetails ->
                        println("Product details: ${productDetails.title}")
                    }
                    skuDetailsList.addAll(productDetailsList)

                } else {
                    println("Failed to fetch product details: ${billingResult.debugMessage}")
                }
            }


            val productList = listOf(
                QueryProductDetailsParams.Product.newBuilder()
                    .setProductId(context.getString(R.string.in_app_subscription_monthly_product_id)) // Replace with your product ID
                    .setProductType(BillingClient.ProductType.SUBS) // For one-time purchases
                    .build(),
                QueryProductDetailsParams.Product.newBuilder()
                    .setProductId(context.getString(R.string.in_app_subscription_yearly_product_id)) // Replace with your product ID
                    .setProductType(BillingClient.ProductType.SUBS) // For one-time purchases
                    .build()
            )

            val paramstest = QueryProductDetailsParams.newBuilder()
                .setProductList(productList)
                .build()

            billingClient.queryProductDetailsAsync(paramstest) { billingResult, productDetailsList ->
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && productDetailsList.isNotEmpty()) {
                    productDetailsList.forEach { productDetails ->
                        println("Product details: ${productDetails.title}")
                    }
                    skuDetailsList.addAll(productDetailsList)

                } else {
                    println("Failed to fetch product details: ${billingResult.debugMessage}")
                }
            }
        }

    }

    fun getSkuDetailsList(): List<ProductDetails>? {
        return skuDetailsList
    }

    fun launchBillingFlow(activity: Activity, productDetails: ProductDetails) {
        // Check if it's a subscription product
        if (!productDetails.subscriptionOfferDetails.isNullOrEmpty()) {
            val subscriptionOffer = productDetails.subscriptionOfferDetails!!.first() // Use the first offer or choose one based on logic
            val offerToken = subscriptionOffer.offerToken // Extract offer token

            // Set up ProductDetailsParams with offer token
            val productDetailsParams = BillingFlowParams.ProductDetailsParams.newBuilder()
                .setProductDetails(productDetails)
                .setOfferToken(offerToken)
                .build()

            // Create BillingFlowParams
            val billingFlowParams = BillingFlowParams.newBuilder()
                .setProductDetailsParamsList(listOf(productDetailsParams))
                .build()

            // Launch Billing Flow
            billingClient.launchBillingFlow(activity, billingFlowParams)
        } else {
            // Handle one-time purchase
            val productDetailsParams = BillingFlowParams.ProductDetailsParams.newBuilder()
                .setProductDetails(productDetails)
                .build()

            val billingFlowParams = BillingFlowParams.newBuilder()
                .setProductDetailsParamsList(listOf(productDetailsParams))
                .build()

            billingClient.launchBillingFlow(activity, billingFlowParams)
        }
    }

    // Restore purchases
    fun restorePurchases(purchaseListener: BillingListner?) {
        Log.d("Billing_Manager", "Trying to restore purchase")
        val params = QueryPurchasesParams.newBuilder()
            .setProductType(BillingClient.ProductType.INAPP)
            .build()
        billingClient.queryPurchasesAsync(params) { billingResult, purchases ->
//        billingClient.queryPurchasesAsync(BillingClient.SkuType.INAPP) { billingResult, purchases ->
            Log.d("Billing_Manager", "restorePurchases: ${billingResult.debugMessage}")
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
                if (purchases.isEmpty()) {
                    Log.d("Billing_Manager", "empty list: removing all purchases prefs")
                    PairedDeviceSharedPreference.getInstance(context).removeAllList()
                } else {
                    for (purchase in purchases) {
                        handlePurchase(purchase)

                        purchaseListener?.onPurchaseFinished(purchase,isSub)
                    }
                }
            } else {
                purchaseListener?.onPurchaseFailed("Failed to restore purchases")
                Log.e("Billing_Manager", "Failed to restore purchases: ${billingResult.debugMessage}")
            }
        }

        /*  // Restore subscriptions
          billingClient.queryPurchasesAsync(BillingClient.SkuType.SUBS) { billingResult, subscriptions ->
              Log.d("Billing_Manager", "restoreSubscriptions: ${billingResult.debugMessage}")
              if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && subscriptions != null) {
                  if (subscriptions.isEmpty()) {
                      Log.d("Billing_Manager", "No active subscriptions found")
                  } else {
                      for (subscription in subscriptions) {
                          handleSubscription(subscription)
                          purchaseListener?.onPurchaseFinished(subscription,isSub)
                      }
                  }
              } else {
                  Log.e("Billing_Manager", "Failed to restore subscriptions: ${billingResult.debugMessage}")
              }
          }*/
    }

    // Restore purchases
    /*  fun restoreSubscriptions(purchaseListener: BillingListner?) {
          Log.d("Billing_Manager", "Trying to restore subscriptions")
          billingClient.queryPurchasesAsync(BillingClient.SkuType.SUBS) { billingResult, purchases ->
              Log.d("Billing_Manager", "restoresubscriptions: ${billingResult.debugMessage}")
              if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
                  if (purchases.isEmpty()) {
                      Log.d("Billing_Manager", "restoresubscriptions empty list: removing all purchases prefs")
                      PairedDeviceSharedPreference.getInstance(context).removeAllSubsList()
                  } else {
                      for (purchase in purchases) {
                          handlePurchase(purchase)

                          purchaseListener?.onPurchaseFinished(purchase,isSub)
                      }
                  }
              } else {
                  purchaseListener?.onPurchaseFailed("Failed to restore purchases")
                  Log.e("Billing_Manager", "Failed to restore purchases: ${billingResult.debugMessage}")
              }
          }

         */
    /* // Restore subscriptions
        billingClient.queryPurchasesAsync(BillingClient.SkuType.SUBS) { billingResult, subscriptions ->
            Log.d("Billing_Manager", "restoreSubscriptions: ${billingResult.debugMessage}")
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && subscriptions != null) {
                if (subscriptions.isEmpty()) {
                    Log.d("Billing_Manager", "No active subscriptions found")
                } else {
                    for (subscription in subscriptions) {
                        handleSubscription(subscription)
                        purchaseListener?.onPurchaseFinished(subscription,isSub)
                    }
                }
            } else {
                Log.e("Billing_Manager", "Failed to restore subscriptions: ${billingResult.debugMessage}")
            }
        }*/
    /*
    }
*/
    fun restoreSubscriptions(purchaseListener: BillingListner?) {
        Log.d("Billing_Manager", "Trying to restore subscriptions")
        val params = QueryPurchasesParams.newBuilder()
            .setProductType(BillingClient.ProductType.SUBS)
            .build()
        billingClient.queryPurchasesAsync(params) { billingResult, purchases ->
//            billingClient.queryPurchasesAsync(BillingClient.SkuType.SUBS) { billingResult, purchases ->
            Log.d("Billing_Manager", "restoreSubscriptions: ${billingResult.debugMessage}")

            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
                if (purchases.isEmpty()) {
                    Log.d("Billing_Manager", "No active subscriptions found, removing all purchases prefs")
                    PairedDeviceSharedPreference.getInstance(context).removeAllSubsList()
                } else {
                    // Track if any valid subscriptions are found
                    var hasValidSubscriptions = false

                    for (purchase in purchases) {
                        // Check if the subscription is still valid
                        val purchaseState = purchase.purchaseState
                        val isPurchased = purchaseState == Purchase.PurchaseState.PURCHASED
                        val currentTime = System.currentTimeMillis()
                        Log.d("Billing_Manager", "isPurchased = $isPurchased")

                        // If subscription is canceled, it should no longer be active
                        if (isPurchased) {
                            // If subscription is valid (not canceled), handle the purchase
                            handlePurchase(purchase)
                            purchaseListener?.onPurchaseFinished(purchase,isSub)
                            hasValidSubscriptions = true


                        }
                        else{
                            // Optionally, handle cleanup for canceled subscriptions here
                            Log.d("Billing_Manager", "Subscription is canceled, skipping...")
                            continue
                        }


                    }

                    // If no valid subscriptions, clear the preferences
                    if (!hasValidSubscriptions) {
                        Log.d("Billing_Manager", "No valid subscriptions found, removing all purchases prefs")
                        PairedDeviceSharedPreference.getInstance(context).removeAllSubsList()
                    }
                }
            } else {
                purchaseListener?.onPurchaseFailed("Failed to restore purchases")
                Log.e("Billing_Manager", "Failed to restore purchases: ${billingResult.debugMessage}")
            }
        }
    }

    override fun onPurchasesUpdated(billingResult: BillingResult, purchases: List<Purchase>?) {
        Log.d("Billing_Manager", "onPurchasesUpdated: ${billingResult.debugMessage}")
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            for (purchase in purchases) {
                handlePurchase(purchase)
                val isSubscription = isSubscriptionSku(purchase.skus.get(0))
                purchaseListener?.onPurchaseFinished(purchase,isSubscription)
            }
        } else {
            Log.e("Billing_Manager", "Purchase update failed: ${billingResult.debugMessage}")
            purchaseListener?.onPurchaseFailed("Purchase update failed: ${billingResult.debugMessage}")
        }
    }

    // Function to check if the SKU is for a subscription
    private fun isSubscriptionSku(sku: String): Boolean {
        // Replace with your actual subscription SKU strings
        val subscriptionSkus = listOf(context.getString(R.string.in_app_subscription_monthly_product_id), context.getString(R.string.in_app_subscription_yearly_product_id))
        return subscriptionSkus.contains(sku)
    }
    private fun handlePurchase(purchase: Purchase) {
        if (!verifyValidSignature(purchase.originalJson, purchase.signature)) {
            Toast.makeText(context, "Error : Invalid Purchase", Toast.LENGTH_SHORT).show()
            return
        }

        if (!purchase.isAcknowledged) {
            val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                .setPurchaseToken(purchase.purchaseToken)
                .build()

            billingClient.acknowledgePurchase(acknowledgePurchaseParams) { billingResult ->
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    savePurchaseToSharedPrefs(purchase.skus.first())
                } else {
                    Log.e("Billing_Manager", "Failed to acknowledge purchase: ${billingResult.debugMessage}")
                }
            }
        }
    }

    private fun handleSubscription(subscription: Purchase) {
        // Handle subscription-specific logic, such as checking for expiry, status, etc.
        // In this case, we just acknowledge it and save it as a valid subscription.
        if (!verifyValidSignature(subscription.originalJson, subscription.signature)) {
            Toast.makeText(context, "Error : Invalid Subscription", Toast.LENGTH_SHORT).show()
            return
        }

        if (!subscription.isAcknowledged) {
            val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                .setPurchaseToken(subscription.purchaseToken)
                .build()

            billingClient.acknowledgePurchase(acknowledgePurchaseParams) { billingResult ->
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    savePurchaseToSharedPrefs(subscription.skus.first())
                } else {
                    Log.e("Billing_Manager", "Failed to acknowledge subscription: ${billingResult.debugMessage}")
                }
            }
        }
    }

    private fun savePurchaseToSharedPrefs(sku: String) {
        val editor = prefs.edit()
        editor.putString("last_purchase_sku", sku)
        editor.apply()
    }

    fun getSavedPurchase(): String? {
        return prefs.getString("last_purchase_sku", null)
    }

    fun endConnection() {
        billingClient.endConnection()
    }

    private fun verifyValidSignature(signedData: String, signature: String): Boolean {
        return try {
            verifyPurchase(
                context.getString(R.string.base64key),
                signedData,
                signature
            )
        } catch (e: IOException) {
            false
        }
    }
    fun clearPurchase(skuId: String) {
        billingClient.queryPurchasesAsync(BillingClient.SkuType.INAPP) { billingResult, purchases ->
            Log.d("Billing_Manager", "clearPurchase: ${billingResult.debugMessage}")
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
                val purchaseToClear = purchases.find { it.skus.contains(skuId) }
                if (purchaseToClear != null) {
                    if (purchaseToClear.isAcknowledged) {
                        // If purchase is already acknowledged, consume it
                        val consumeParams = ConsumeParams.newBuilder()
                            .setPurchaseToken(purchaseToClear.purchaseToken)
                            .build()

                        billingClient.consumeAsync(consumeParams) { consumeResult, _ ->
                            Log.d("Billing_Manager", "consumePurchase: ${consumeResult.debugMessage}")
                            if (consumeResult.responseCode == BillingClient.BillingResponseCode.OK) {
                                // Successfully consumed the purchase
                                Log.d("Billing_Manager", "Successfully consumed purchase for SKU: $skuId")
                                removePurchaseFromSharedPrefs(skuId)
                            } else {
                                Log.e("Billing_Manager", "Failed to consume purchase: ${consumeResult.debugMessage}")
                            }
                        }
                    } else {
                        // If purchase is not acknowledged, acknowledge it first
                        val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                            .setPurchaseToken(purchaseToClear.purchaseToken)
                            .build()

                        billingClient.acknowledgePurchase(acknowledgePurchaseParams) { billingResult ->
                            Log.d("Billing_Manager", "acknowledgePurchase: ${billingResult.debugMessage}")
                            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                                Log.d("Billing_Manager", "Purchase acknowledged for SKU: $skuId")
                                // Now consume the purchase
                                val consumeParams = ConsumeParams.newBuilder()
                                    .setPurchaseToken(purchaseToClear.purchaseToken)
                                    .build()

                                billingClient.consumeAsync(consumeParams) { consumeResult, _ ->
                                    Log.d("Billing_Manager", "consumePurchase: ${consumeResult.debugMessage}")
                                    if (consumeResult.responseCode == BillingClient.BillingResponseCode.OK) {
                                        // Successfully consumed the purchase
                                        Log.d("Billing_Manager", "Successfully consumed purchase for SKU: $skuId")
                                        removePurchaseFromSharedPrefs(skuId)
                                    } else {
                                        Log.e("Billing_Manager", "Failed to consume purchase: ${consumeResult.debugMessage}")
                                    }
                                }
                            } else {
                                Log.e("Billing_Manager", "Failed to acknowledge purchase: ${billingResult.debugMessage}")
                            }
                        }
                    }
                } else {
                    Log.d("Billing_Manager", "No purchase found for SKU: $skuId")
                }
            }
            else {
                Log.e("Billing_Manager", "Failed to query purchases: ${billingResult.debugMessage}")
            }
        }
    }

    fun clearSubscription(skuId: String, onSubscriptionRemove: (String) -> Unit) {
        val params = QueryPurchasesParams.newBuilder()
            .setProductType(BillingClient.ProductType.SUBS)
            .build()

        billingClient.queryPurchasesAsync(params) { billingResult, purchases ->
            Log.d("Billing_Manager", "clearPurchase: ${billingResult.debugMessage}")
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
                val purchaseToClear = purchases.find { it.skus.contains(skuId) }
                if (purchaseToClear != null) {
                    if (!purchaseToClear.isAcknowledged) {
                        // If purchase is not acknowledged, acknowledge it first
                        val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                            .setPurchaseToken(purchaseToClear.purchaseToken)
                            .build()

                        billingClient.acknowledgePurchase(acknowledgePurchaseParams) { billingResult ->
                            Log.d("Billing_Manager", "acknowledgePurchase: ${billingResult.debugMessage}")
                            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                                Log.d("Billing_Manager", "Purchase acknowledged for SKU: $skuId")
                                // Remove subscription from shared preferences
                                removePurchaseFromSharedPrefs(skuId)
                                onSubscriptionRemove(skuId)
                            } else {
                                Log.e("Billing_Manager", "Failed to acknowledge purchase: ${billingResult.debugMessage}")
                            }
                        }
                    } else {
                        // If purchase is already acknowledged, directly remove from shared preferences
                        Log.d("Billing_Manager", "Purchase already acknowledged for SKU: $skuId")
                        removePurchaseFromSharedPrefs(skuId)
                        onSubscriptionRemove(skuId)
                    }
                } else {
                    Log.d("Billing_Manager", "No purchase found for SKU: $skuId")
                }
            } else {
                Log.e("Billing_Manager", "Failed to query purchases: ${billingResult.debugMessage}")
            }
        }
    }

    private fun removePurchaseFromSharedPrefs(skuId: String) {
        val editor = prefs.edit()
//        if (skuId.equals(context.getString(R.string.in_app_purchase_clear_mil_for_six_times_product_id))) {
//            editor.remove("mil_left")
//        }
        editor.remove("last_purchase_sku")
        editor.apply()
    }

    fun checkAndRestorePurchases() {
        val savedSku = getSavedPurchase()
        if (savedSku != null) {
            Log.d("Billing_Manager", "Restoring purchase for SKU: $savedSku")
            restorePurchases(null) // Optionally call restorePurchases to confirm purchase status
        } else {
            Log.d("Billing_Manager", "No saved purchase found")
        }
    }
}
