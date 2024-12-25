package com.akapps.obd2carscannerapp.Activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.akapps.obd2carscannerapp.Activity.MyMainActivity.Constants.mCommService
import com.akapps.obd2carscannerapp.Activity.MyMainActivity.Constants.mode
import com.android.billingclient.api.SkuDetails
import com.akapps.obd2carscannerapp.Adapter.PurchaseAdapter
import com.akapps.obd2carscannerapp.Models.BillingManager
import com.akapps.obd2carscannerapp.Models.PurchaseModel
import com.akapps.obd2carscannerapp.R
import com.akapps.obd2carscannerapp.Trips.PairedDeviceSharedPreference
import com.akapps.obd2carscannerapp.databinding.ActivityPurchaseBinding
import com.android.billingclient.api.Purchase


class PurchaseActivity : AppCompatActivity() {
    lateinit var binding: ActivityPurchaseBinding
    lateinit var billingManager: BillingManager
    var list: List<SkuDetails>?= null
    var from = "multi"
    var adapter: PurchaseAdapter?= null
    var isYearlypurchased= false
    var isAllPremium= false
    var isSubscibed= false
    var subsClick= ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        window.decorView.layoutDirection = View.LAYOUT_DIRECTION_LTR
        binding= ActivityPurchaseBinding.inflate(layoutInflater)
        setContentView(binding.root)
        changeStatusBarColorFromResource(R.color.unchange_black)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
//        billingClient = BillingClient.newBuilder(this)
//            .enablePendingPurchases()
//            .setListener { billingResult, list ->
//                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && list != null) {
//                    for (purchase in list) {
//                        verifySubPurchase(purchase)
//                    }
//                }
//            }.build()

        from= intent.getStringExtra("which")!!
        billingManager = BillingManager(this,object : BillingManager.BillingListner{
            override fun onPurchaseFinished(purchase: Purchase, isSub: Boolean) {

            }

            override fun onPurchaseFailed(message: String) {

            }
        })
        billingManager.startConnection(false,true)
        Log.d("Purchase Activity", "Billing Start Connection: ")

        binding.closeimg.setOnClickListener {
            finish()
        }

        binding.privacytxt.setOnClickListener {
            startActivity(Intent(this@PurchaseActivity,TermsPrivacyActivity::class.java)
                .putExtra("which","privacy"))
        }


        binding.termstxt.setOnClickListener {
            startActivity(Intent(this@PurchaseActivity,TermsPrivacyActivity::class.java)
                .putExtra("which","terms"))
        }

        Handler().postDelayed(Runnable {
            Log.d("Purchases__", "Purchase_checker: subscription   list size; "+ PairedDeviceSharedPreference.getInstance(this).subsList.size)
            Log.d("Purchases__", "Purchase_checker: purchase  list size; "+PairedDeviceSharedPreference.getInstance(this).list.size)
            if(PairedDeviceSharedPreference.getInstance(this).subsList.size>0){
                for(purchases in PairedDeviceSharedPreference.getInstance(this@PurchaseActivity).subsList){
                    if (purchases.equals(getString(R.string.in_app_subscription_yearly_product_id))){
                        isYearlypurchased= true
                        break
                    }
                    else{
                        isYearlypurchased= false
                    }
                }
                if(isYearlypurchased){
                    var skuDetail: SkuDetails? = null
                    val skuDetailsList = billingManager.getSkuDetailsList() // Implement this to return actual SkuDetails
                    skuDetailsList?.let {
                        for (sku in skuDetailsList){
                            if(sku.sku.equals(getString(R.string.in_app_subscription_yearly_product_id))){
                                skuDetail=sku
                                break
                            }
                        }
                    }


                    skuDetail?.let{
                        binding.purchasetitle.text= it.title
                        binding.purchasecost.text= it.price
                        binding.purchasedescription.text= it.description
                    }
                    binding.multisubrelative.visibility=View.GONE
                    binding.progressbar.visibility=View.GONE
                    binding.singlesubrelative.visibility=View.GONE
                    binding.alreadypurchasecard.visibility=View.VISIBLE
                    binding.textView5.setText("Your are a Premimum user")
                    binding.restoretxt.setText("Manage Subscription")
                    binding.getpkgtxt.setText("UnSubscribe")
                }
                else{
                    list = billingManager.getSkuDetailsList()
                    if (list != null) {
                        Log.d("Purchase Activity", "onCreate: list size: " + list!!.size)
                        if (list!!.size > 0) {
                            if (list?.size == 1) {
                                Log.d("Purchase Activity", "onCreate: list size 1: " + list?.size)
                                Log.d("Purchase Activity", "onCreate: Sku: " + list?.get(0)!!.sku)
                                Log.d(
                                    "Purchase Activity",
                                    "onCreate: Description: " + list?.get(0)!!.description
                                )
                                Log.d(
                                    "Purchase Activity",
                                    "onCreate: freeTrialPeriod: " + list?.get(0)!!.freeTrialPeriod
                                )
                                Log.d("Purchase Activity", "onCreate: title: " + list?.get(0)!!.title)
                                Log.d("Purchase Activity", "onCreate: price: " + list?.get(0)!!.price)
                                binding.title.text = list!!.get(0).title
                                binding.price.text = list!!.get(0).price
                                binding.descriptiontxt.text = list!!.get(0).description
                                binding.progressbar.visibility = View.GONE
                                binding.singlesubrelative.visibility = View.VISIBLE
                                binding.multisubrelative.visibility = View.GONE
                            } else {
                                Log.d("Purchase Activity", "onCreate: list size > 1: " + list?.size)
                                for (i in list!!) {
                                    Log.d("Purchase Activity", "onCreate: Sku: " + i.sku)
                                    Log.d(
                                        "Purchase Activity",
                                        "onCreate: Description: " + i.description
                                    )
                                    Log.d(
                                        "Purchase Activity",
                                        "onCreate: freeTrialPeriod: " + i.freeTrialPeriod
                                    )
                                    Log.d("Purchase Activity", "onCreate: title: " + i.title)
                                    Log.d("Purchase Activity", "onCreate: price: " + i.price)
                                    Log.d(
                                        "Purchase Activity",
                                        "onCreate: price: " + i.subscriptionPeriod
                                    )
                                    Log.d("Purchase Activity", "......................")
                                }

                                setAdapter(list!!)
                            }
                        }
                    } else {
                        Log.d("Purchase Activity", "onCreate: list is null: ")
                    }
                }

            }
            else if(PairedDeviceSharedPreference.getInstance(this).list.size>0){
//                for(purchases in PairedDeviceSharedPreference.getInstance(this).list){
//                    billingManager.clearPurchase(purchases)
//                }
//
//                for(purchases in PairedDeviceSharedPreference.getInstance(this).subsList){
//                    billingManager.clearSubscription(purchases, onSubscriptionRemove = {})
//                }


                for(purchases in PairedDeviceSharedPreference.getInstance(this).list){
                    Log.d("Purchases__", "purchases item is: "+purchases)
                    if (purchases.equals(getString(R.string.in_app_subscription_lifetime_product_id))){
                        isAllPremium= true
                        break
                    }
                    else{
                        isAllPremium= false
                    }
                }
                Log.d("Purchases__", "Purchase_checker:   isAllPremium; "+isAllPremium)
                if(isAllPremium){
                    var skuDetail: SkuDetails? = null
                    val skuDetailsList = billingManager.getSkuDetailsList() // Implement this to return actual SkuDetails
                    skuDetailsList?.let {
                        for (sku in skuDetailsList){
                            if(sku.sku.equals(getString(R.string.in_app_subscription_lifetime_product_id))){
                                skuDetail=sku
                                break
                            }
                        }
                    }


                    skuDetail?.let{
                        binding.purchasetitle.text= it.title
                        binding.purchasecost.text= it.price
                        binding.purchasedescription.text= it.description
                    }
                    binding.getpkgcard.visibility=View.GONE
                    binding.restoretxt.visibility=View.GONE
                    binding.multisubrelative.visibility=View.GONE
                    binding.progressbar.visibility=View.GONE
                    binding.singlesubrelative.visibility=View.GONE
                    binding.alreadypurchasecard.visibility=View.VISIBLE
                    binding.textView5.setText("Your are a Premimum user")
                }
                else{
                    list = billingManager.getSkuDetailsList()
                    if (list != null) {
                        Log.d("Purchase Activity", "onCreate: list size: " + list!!.size)
                        if (list!!.size > 0) {
                            if (list?.size == 1) {
                                Log.d("Purchase Activity", "onCreate: list size 1: " + list?.size)
                                Log.d("Purchase Activity", "onCreate: Sku: " + list?.get(0)!!.sku)
                                Log.d(
                                    "Purchase Activity",
                                    "onCreate: Description: " + list?.get(0)!!.description
                                )
                                Log.d(
                                    "Purchase Activity",
                                    "onCreate: freeTrialPeriod: " + list?.get(0)!!.freeTrialPeriod
                                )
                                Log.d("Purchase Activity", "onCreate: title: " + list?.get(0)!!.title)
                                Log.d("Purchase Activity", "onCreate: price: " + list?.get(0)!!.price)
                                binding.title.text = list!!.get(0).title
                                binding.price.text = list!!.get(0).price
                                binding.descriptiontxt.text = list!!.get(0).description
                                binding.progressbar.visibility = View.GONE
                                binding.singlesubrelative.visibility = View.VISIBLE
                                binding.multisubrelative.visibility = View.GONE
                            } else {
                                Log.d("Purchase Activity", "onCreate: list size > 1: " + list?.size)
                                for (i in list!!) {
                                    Log.d("Purchase Activity", "onCreate: Sku: " + i.sku)
                                    Log.d(
                                        "Purchase Activity",
                                        "onCreate: Description: " + i.description
                                    )
                                    Log.d(
                                        "Purchase Activity",
                                        "onCreate: freeTrialPeriod: " + i.freeTrialPeriod
                                    )
                                    Log.d("Purchase Activity", "onCreate: title: " + i.title)
                                    Log.d("Purchase Activity", "onCreate: price: " + i.price)
                                    Log.d(
                                        "Purchase Activity",
                                        "onCreate: price: " + i.subscriptionPeriod
                                    )
                                    Log.d("Purchase Activity", "......................")
                                }

                                setAdapter(list!!)
                            }
                        }
                    } else {
                        Log.d("Purchase Activity", "onCreate: list is null: ")
                    }
                }
            }
            else {
                list = billingManager.getSkuDetailsList()
                if (list != null) {
                    Log.d("Purchase Activity", "onCreate: list size: " + list!!.size)
                    if (list!!.size > 0) {
                        if (list?.size == 1) {
                            Log.d("Purchase Activity", "onCreate: list size 1: " + list?.size)
                            Log.d("Purchase Activity", "onCreate: Sku: " + list?.get(0)!!.sku)
                            Log.d(
                                "Purchase Activity",
                                "onCreate: Description: " + list?.get(0)!!.description
                            )
                            Log.d(
                                "Purchase Activity",
                                "onCreate: freeTrialPeriod: " + list?.get(0)!!.freeTrialPeriod
                            )
                            Log.d("Purchase Activity", "onCreate: title: " + list?.get(0)!!.title)
                            Log.d("Purchase Activity", "onCreate: price: " + list?.get(0)!!.price)
                            binding.title.text = list!!.get(0).title
                            binding.price.text = list!!.get(0).price
                            binding.descriptiontxt.text = list!!.get(0).description
                            binding.progressbar.visibility = View.GONE
                            binding.singlesubrelative.visibility = View.VISIBLE
                            binding.multisubrelative.visibility = View.GONE
                        } else {
                            Log.d("Purchase Activity", "onCreate: list size > 1: " + list?.size)
                            for (i in list!!) {
                                Log.d("Purchase Activity", "onCreate: Sku: " + i.sku)
                                Log.d(
                                    "Purchase Activity",
                                    "onCreate: Description: " + i.description
                                )
                                Log.d(
                                    "Purchase Activity",
                                    "onCreate: freeTrialPeriod: " + i.freeTrialPeriod
                                )
                                Log.d("Purchase Activity", "onCreate: title: " + i.title)
                                Log.d("Purchase Activity", "onCreate: price: " + i.price)
                                Log.d(
                                    "Purchase Activity",
                                    "onCreate: price: " + i.subscriptionPeriod
                                )
                                Log.d("Purchase Activity", "......................")
                            }

                            setAdapter(list!!)
                        }
                    }
                } else {
                    Log.d("Purchase Activity", "onCreate: list is null: ")
                }
            }

            /* if (AppPurchase.getInstance().isPurchased()) {
 //                for(purchases in AppPurchase.getInstance().ownerIdInapps){
 //                    billingManager.clearPurchase(purchases)
 //                }
                 var isAllPremium= false

                 for(purchases in AppPurchase.getInstance().ownerIdInapps){
                     if (purchases.equals(getString(R.string.in_app_purchase_unlock_all_features_product_id))){
                         isAllPremium= true
                     }
                     else{
                         isAllPremium= false
                     }
                 }
                 if(isAllPremium){
                     var skuDetail: SkuDetails? = null
                     val skuDetailsList = billingManager.getSkuDetailsList() // Implement this to return actual SkuDetails
                         skuDetailsList?.let {
                             for (sku in skuDetailsList){
                                 if(sku.sku.equals(getString(R.string.in_app_purchase_unlock_all_features_product_id))){
                                     skuDetail=sku
                                     break
                                 }
                             }
                         }


                     skuDetail?.let{
                         binding.purchasetitle.text= it.title
                         binding.purchasecost.text= it.price
                         binding.purchasedescription.text= it.description
                     }
                     binding.getpkgcard.visibility=View.GONE
                     binding.restoretxt.visibility=View.GONE
                     binding.multisubrelative.visibility=View.GONE
                     binding.progressbar.visibility=View.GONE
                     binding.singlesubrelative.visibility=View.GONE
                     binding.alreadypurchasecard.visibility=View.VISIBLE
                     binding.textView.setText("Purchased Verified")
                     binding.textView5.setText("Your are a Premimum user")
                 }
                 else{
                     list = billingManager.getSkuDetailsList()
                     if (list != null) {
                         Log.d("Purchase Activity", "onCreate: list size: " + list!!.size)
                         if (list!!.size > 0) {
                             if (list?.size == 1) {
                                 Log.d("Purchase Activity", "onCreate: list size 1: " + list?.size)
                                 Log.d("Purchase Activity", "onCreate: Sku: " + list?.get(0)!!.sku)
                                 Log.d(
                                     "Purchase Activity",
                                     "onCreate: Description: " + list?.get(0)!!.description
                                 )
                                 Log.d(
                                     "Purchase Activity",
                                     "onCreate: freeTrialPeriod: " + list?.get(0)!!.freeTrialPeriod
                                 )
                                 Log.d("Purchase Activity", "onCreate: title: " + list?.get(0)!!.title)
                                 Log.d("Purchase Activity", "onCreate: price: " + list?.get(0)!!.price)
                                 binding.title.text = list!!.get(0).title
                                 binding.price.text = list!!.get(0).price
                                 binding.descriptiontxt.text = list!!.get(0).description
                                 binding.progressbar.visibility = View.GONE
                                 binding.singlesubrelative.visibility = View.VISIBLE
                                 binding.multisubrelative.visibility = View.GONE
                             } else {
                                 Log.d("Purchase Activity", "onCreate: list size > 1: " + list?.size)
                                 for (i in list!!) {
                                     Log.d("Purchase Activity", "onCreate: Sku: " + i.sku)
                                     Log.d(
                                         "Purchase Activity",
                                         "onCreate: Description: " + i.description
                                     )
                                     Log.d(
                                         "Purchase Activity",
                                         "onCreate: freeTrialPeriod: " + i.freeTrialPeriod
                                     )
                                     Log.d("Purchase Activity", "onCreate: title: " + i.title)
                                     Log.d("Purchase Activity", "onCreate: price: " + i.price)
                                     Log.d(
                                         "Purchase Activity",
                                         "onCreate: price: " + i.subscriptionPeriod
                                     )
                                     Log.d("Purchase Activity", "......................")
                                 }

                                 setAdapter(list!!)
                             }
                         }
                     } else {
                         Log.d("Purchase Activity", "onCreate: list is null: ")
                     }
                 }

             }
            */

        },3000)

        binding.getpkgcard.setOnClickListener {
            if(isSubscibed|| isYearlypurchased){
                Log.d("RemovingSubs", "Trying to  ")
                for(purchases in PairedDeviceSharedPreference.getInstance(this).subsList){
                    if(subsClick.equals(purchases) || isYearlypurchased) {
                        billingManager.clearSubscription(purchases, onSubscriptionRemove = {
                            subsRemoveDialog(this@PurchaseActivity)
                            PairedDeviceSharedPreference.getInstance(this).removeItemFromSubsList(it)
                            Log.d("RemovingSubs", "Subscription Removed unit")
                        })
                        break
                    }
                }
            }
            else {
                if (adapter != null) {
                    val skuDetailsList = adapter!!.getPurchaseList()
                    var skuDetail: SkuDetails? = null
                    for (i in skuDetailsList) {
                        if (i.ischecked) {
                            skuDetail = i.skuDetails
                            break
                        }
                    }
//                val skuDetail = skuDetailsList?.find { it.sku == "com.obd.milbundle" }

                    if (skuDetail != null) {
                        billingManager.launchBillingFlow(this, skuDetail!!)
                    } else {
                        Toast.makeText(this, "Product details not available", Toast.LENGTH_SHORT)
                            .show()
                    }
                }

            }
            /*if(!from.equals("multi")){
            val skuDetailsList = billingManager.getSkuDetailsList() // Implement this to return actual SkuDetails
            val skuDetail = skuDetailsList?.find { it.sku == getString(R.string.in_app_purchase_clear_mil_for_one_time_product_id) }

            if (skuDetail != null) {
                billingManager.launchBillingFlow(this, skuDetail)
            } else {
                Toast.makeText(this, "Product details not available", Toast.LENGTH_SHORT).show()
            }
            }
            else{
//                val skuDetailsList = billingManager.getSkuDetailsList()
                if(adapter!=null) {
                    val skuDetailsList = adapter!!.getPurchaseList()
                    var skuDetail: SkuDetails? = null
                    for (i in skuDetailsList) {
                        if (i.ischecked) {
                            skuDetail = i.skuDetails
                            break
                        }
                    }
//                val skuDetail = skuDetailsList?.find { it.sku == "com.obd.milbundle" }

                    if (skuDetail != null) {
                        billingManager.launchBillingFlow(this, skuDetail)
                    } else {
                        Toast.makeText(this, "Product details not available", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }*/
        }

        binding.restoretxt.setOnClickListener {
            if(isSubscibed|| isYearlypurchased || isAllPremium){
                Log.d("Manage_test", "Going to Manage subsciption")
                openManageSubscriptions()
            }
            else {
                billingManager.restorePurchases(object : BillingManager.BillingListner{
                    override fun onPurchaseFinished(purchase: Purchase, isSub: Boolean) {

                    }

                    override fun onPurchaseFailed(message: String) {

                    }
                })
            }

        }
    }
    // Open the Google Play subscription management page
    private fun openManageSubscriptions() {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/account/subscriptions"))
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    private fun setAdapter(list: List<SkuDetails>) {
            var purchaselist: ArrayList<PurchaseModel> = ArrayList()
        for(i in 0 until  list.size){
            if (i==0){
                purchaselist.add(PurchaseModel(list.get(i),true))
            }
            else{
                purchaselist.add(PurchaseModel(list.get(i),false))
            }
        }
        adapter= PurchaseAdapter(this,purchaselist,object : PurchaseAdapter.PurchaseInteface{
            override fun onPurchaseClick(sku: String) {
                if(sku.equals(getString(R.string.in_app_subscription_monthly_product_id)) ||
                    sku.equals(getString(R.string.in_app_subscription_yearly_product_id))){
                    if(PairedDeviceSharedPreference.getInstance(this@PurchaseActivity).subsList.size>0){
                        var currentpurchase= ""
                        for(purchases in PairedDeviceSharedPreference.getInstance(this@PurchaseActivity).subsList){
                            if (purchases.equals(getString(R.string.in_app_subscription_yearly_product_id))){
                                currentpurchase = getString(R.string.in_app_subscription_yearly_product_id)
                            }
                            else{
                                currentpurchase = getString(R.string.in_app_subscription_monthly_product_id)
                            }
                        }
                        Log.d("onPurchaseClick", "currentpurchase = $currentpurchase")
                        if(currentpurchase.equals(sku)){
                            isSubscibed=true
                            binding.restoretxt.text = "Manage Subscription"
                            binding.getpkgtxt.text = "Unsubscribe"
                            subsClick = currentpurchase
                        }
                        else{
                            isSubscibed=false
                            binding.restoretxt.text = "Restore Purchases"
                            binding.getpkgtxt.text = "Get this Package"
                        }
                    }
                }
            }
        })
        binding.listrecycler.adapter= adapter
        binding.progressbar.visibility= View.GONE
    }


    override fun onDestroy() {
        super.onDestroy()
        billingManager.endConnection() // Clean up billing client
    }

    private fun subsRemoveDialog(context: Context) {
        Log.d("subsRemoveDialog", "Trying to show dialog")

        // Ensure execution on the main UI thread
        (context as? Activity)?.runOnUiThread {
            // Inflate custom layout for the dialog
            val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_bluetooth, null)

            val dialogBuilder = AlertDialog.Builder(context)
                .setView(dialogView)
                .setCancelable(false)

            val dialog = dialogBuilder.create()

            dialogView.findViewById<TextView>(R.id.body_text).text = "App restart required for further use."
            dialogView.findViewById<TextView>(R.id.header_text).text = "Subscription Removed"
            dialogView.findViewById<Button>(R.id.button_connect).text = "Restart"

            // Connect button action
            dialogView.findViewById<Button>(R.id.button_connect).setOnClickListener {
                dialog.dismiss()

                mode = MyMainActivity.MODE.OFFLINE

                if (mCommService != null) {
                    mCommService!!.stop()
                }

                context.startActivity(Intent(context, SplashActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK))
                (context as Activity).finish()
            }

            dialogView.findViewById<ImageView>(R.id.closebtn).visibility = View.GONE

            dialog.show()
        }
    }


}