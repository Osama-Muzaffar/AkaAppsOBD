package com.akapps.obd2carscannerapp.Activity

import android.bluetooth.BluetoothSocket
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.android.billingclient.api.SkuDetails
import com.akapps.ecu.EcuCodeItem
import com.akapps.ecu.prot.obd.ObdProt
import com.akapps.obd2carscannerapp.Ads.BannerAdView
import com.akapps.obd2carscannerapp.Ads.billing.AppPurchase
import com.akapps.obd2carscannerapp.BuildConfig
import com.akapps.obd2carscannerapp.CommService
import com.akapps.obd2carscannerapp.Models.BillingManager
import com.akapps.obd2carscannerapp.Models.ClearMILHelper
import com.akapps.obd2carscannerapp.Models.InAppPurchase
import com.akapps.obd2carscannerapp.Models.PurchaseModel
import com.akapps.obd2carscannerapp.R
import com.akapps.obd2carscannerapp.Trips.PairedDeviceSharedPreference
import com.akapps.obd2carscannerapp.Utils.AppUtils
import com.akapps.obd2carscannerapp.databinding.ActivityScanCarBinding
import com.akapps.pvs.IndexedProcessVar
import com.android.billingclient.api.Purchase
import com.quadsquad.savestatus.videodownloader.storysaver.statuskeeper.adsConfigs.adsConfig
import java.io.OutputStream
import java.io.PrintWriter

class ScanningActivity : AppCompatActivity(),
    ClearMILHelper.OnLoadTroubleCodesProgressFinishedListener {
    lateinit var binding: ActivityScanCarBinding
    var isforclear= false
    private var inAppPurchase: InAppPurchase? = null
    lateinit var billingManager: BillingManager
    lateinit var prefs: SharedPreferences
    var isAlreadyCleared= false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        binding= ActivityScanCarBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.decorView.layoutDirection = View.LAYOUT_DIRECTION_LTR
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        changeStatusBarColorFromResource(R.color.unchange_black)
        prefs= PreferenceManager.getDefaultSharedPreferences(this)
        billingManager = BillingManager(this,object : BillingManager.BillingListner{
            override fun onPurchaseFinished(purchase: Purchase, isSub: Boolean) {

            }

            override fun onPurchaseFailed(message: String) {

            }
        })
        billingManager.startConnection(true)
//        inAppPurchase = InAppPurchase.getInstance(
//            this@ScanningActivity,
//            getString(R.string.base64key)
//        )
//        ClearMILHelper.setContext(
//            this,
//            binding.clearcard,
//            inAppPurchase,
//            object : OnLoadTroubleCodesProgressFinishedListener{
//                override fun onLoadTroubleCodesProgressFinished(result: String?, response: Int) {
//
//                }
//
//            }
//        )


//        inAppPurchase = InAppPurchase.getInstance(
//            this,
//            getResources().getString(R.string.base64key)
//        )
//        initInAppPurchase()
//        ClearMILHelper.setContext(
//            this,
//            binding.clearcard,
//            inAppPurchase,
//            this
//        )
//        ClearMILHelper.loadAndShowTroubleCodes()
        binding.backrelative.setOnClickListener {
            finish()
        }
        if (isforclear) {
            MyMainActivity.Constants.currDataAdapter.setPvList(
                ObdProt.tCodes
            )
        }
        Handler().postDelayed(Runnable {

        if(MyMainActivity.Constants.currDataAdapter.getPvs().size>0) {
//            if(MainActivity.Constants.currDataAdapter.getPvs().size == 1){
//                if((MainActivity.Constants.currDataAdapter.getPvs().get(0)as IndexedProcessVar).get(EcuCodeItem.FID_CODE).equals("P0000")){
//                    binding.nodatatxt.visibility= View.VISIBLE
//                }
//                else{
//                    binding.scanninglist.adapter = MainActivity.Constants.currDataAdapter
//
//                    binding.scanninglist.setOnItemClickListener { parent, view, position, id ->
//                        val currPv =
//                            MainActivity.Constants.currDataAdapter.getItem(position) as IndexedProcessVar
//
//                        startActivity(
//                            Intent(this@ScanningActivity, CodeDetailsActivity::class.java)
//                                .putExtra("code", currPv.get(EcuCodeItem.FID_CODE).toString())
//                        )
//                    }
//                    binding.nodatatxt.visibility = View.GONE
//                }
//            }
//            else {
                binding.scanninglist.adapter = MyMainActivity.Constants.currDataAdapter

                binding.scanninglist.setOnItemClickListener { parent, view, position, id ->
                    val currPv =
                        MyMainActivity.Constants.currDataAdapter.getItem(position) as IndexedProcessVar

                    startActivity(
                        Intent(this@ScanningActivity, FaultCodeDetailsActivity::class.java)
                            .putExtra("code", currPv.get(EcuCodeItem.FID_CODE).toString())
                    )
                }
                binding.nodatatxt.visibility = View.GONE
//            }

          /*  if(PairedDeviceSharedPreference.getInstance(this).subsList.size>0){
                var isshwoingad= true
                for(purchases in PairedDeviceSharedPreference.getInstance(this).subsList){
//                    if (purchases.equals(getString(R.string.in_app_subscription_yearly_product_id))){
//                        isshwoingad= false
//                        break
//
//                    }
//                    else{
                    isshwoingad= false
//                    }
                }
                if (isshwoingad){

                    if(adsConfig !=null) {
                        if(adsConfig!!.scanning_banner_simple && adsConfig!!.scanning_banner_collapse) {
                            val bannerview = findViewById<BannerAdView>(R.id.banneradsview)
                            bannerview.visibility = View.VISIBLE
                            bannerview.loadCollapsibleBanner(
                                this@ScanningActivity,
                                BuildConfig.admob_banner,
                                true
                            )
                        }
                        else if(adsConfig!!.scanning_banner_simple) {
                            val bannerview = findViewById<BannerAdView>(R.id.banneradsview)
                            bannerview.visibility = View.VISIBLE
                            bannerview.loadBanner(
                                this@ScanningActivity,
                                BuildConfig.admob_banner
                            )
                        }
                        else  if(adsConfig!!.scanning_banner_collapse) {
                            val bannerview = findViewById<BannerAdView>(R.id.banneradsview)
                            bannerview.visibility = View.VISIBLE
                            bannerview.loadCollapsibleBanner(
                                this@ScanningActivity,
                                BuildConfig.admob_banner,
                                true
                            )
                        }
                        else{
                            val bannerview = findViewById<BannerAdView>(R.id.banneradsview)
                            bannerview.visibility = View.GONE
                        }

                    }
                    else {
                        Log.d("test_remote", "remote config is null")
                        val bannerview = findViewById<BannerAdView>(R.id.banneradsview)
                        bannerview.visibility = View.GONE
                    }
                }
                else{
                    val bannerview= findViewById<BannerAdView>(R.id.banneradsview)
                    bannerview.visibility=View.GONE
                }
            }
            else {
                runOnUiThread {

                    if(adsConfig !=null) {
                        if(adsConfig!!.scanning_banner_simple && adsConfig!!.scanning_banner_collapse) {
                            val bannerview = findViewById<BannerAdView>(R.id.banneradsview)
                            bannerview.visibility = View.VISIBLE
                            bannerview.loadCollapsibleBanner(
                                this@ScanningActivity,
                                BuildConfig.admob_banner,
                                true
                            )
                        }
                        else if(adsConfig!!.scanning_banner_simple) {
                            val bannerview = findViewById<BannerAdView>(R.id.banneradsview)
                            bannerview.visibility = View.VISIBLE
                            bannerview.loadBanner(
                                this@ScanningActivity,
                                BuildConfig.admob_banner
                            )
                        }
                        else  if(adsConfig!!.scanning_banner_collapse) {
                            val bannerview = findViewById<BannerAdView>(R.id.banneradsview)
                            bannerview.visibility = View.VISIBLE
                            bannerview.loadCollapsibleBanner(
                                this@ScanningActivity,
                                BuildConfig.admob_banner,
                                true
                            )
                        }
                        else{
                            val bannerview = findViewById<BannerAdView>(R.id.banneradsview)
                            bannerview.visibility = View.GONE
                        }

                    }
                    else {
                        Log.d("test_remote", "remote config is null")
                        val bannerview = findViewById<BannerAdView>(R.id.banneradsview)
                        bannerview.visibility = View.GONE
                    }
                }

            }*/
         /*   if(!AppPurchase.getInstance().isPurchased){
                val banneradsview= findViewById<BannerAdView>(R.id.banneradsview)
                banneradsview.loadBanner(this, BuildConfig.admob_banner)
            }
            else{
                val banneradsview= findViewById<BannerAdView>(R.id.banneradsview)
                banneradsview.visibility= View.GONE
            }*/

        }
            else{
            binding.nodatatxt.visibility= View.VISIBLE
            val banneradsview= findViewById<BannerAdView>(R.id.banneradsview)
            banneradsview.visibility= View.GONE
        }
            binding.scanninglayout.visibility= View.GONE
            binding.mainlayout.visibility= View.VISIBLE
        },5000)

        binding.clearcard.setOnClickListener {

            if(isAlreadyCleared){
                Toast.makeText(this@ScanningActivity, "Already cleared", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
//            if (Constants.mConnectedDeviceName!=null && Constants.mConnectedDeviceName!!.isNotEmpty()) {
            var currentPurchase= ""
            if(PairedDeviceSharedPreference.getInstance(this).list.size>0){
                for (purchases in PairedDeviceSharedPreference.getInstance(this).list) {
                    Log.d("Clear_worker", "purchases= $purchases")

//                    if (purchases.equals(getString(R.string.in_app_purchase_clear_mil_for_six_times_product_id))) {
//                        Log.d("Clear_worker", "mil bundle buyed")
//                        currentPurchase= getString(R.string.in_app_purchase_clear_mil_for_six_times_product_id)
//                        break
//
//                    }
                    if (purchases.equals(getString(R.string.in_app_purchase_clear_mil_for_one_time_product_id))) {
                        Log.d("Clear_worker", "mil one buyed")
                        currentPurchase= getString(R.string.in_app_purchase_clear_mil_for_one_time_product_id)
                        break
                    }
                    else if (purchases.equals(getString(R.string.in_app_subscription_lifetime_product_id))) {
                        currentPurchase= getString(R.string.in_app_subscription_lifetime_product_id)
                        Log.d("Clear_worker", "all premimum")
                        break
                    }

                }

                if (currentPurchase.equals(getString(R.string.in_app_purchase_clear_mil_for_one_time_product_id))) {
                    billingManager.clearPurchase(getString(R.string.in_app_purchase_clear_mil_for_one_time_product_id))
                    CommService.elm.service = ObdProt.OBD_SVC_CLEAR_CODES
                    binding.nodatatxt.visibility=View.VISIBLE
                    binding.scanninglist.visibility=View.GONE
                    isAlreadyCleared=true
                    Toast.makeText(this, "Cleared Successfully", Toast.LENGTH_SHORT).show()
//                    finish()
                }
                else if (currentPurchase.equals(getString(R.string.in_app_subscription_lifetime_product_id))) {
                    CommService.elm.service = ObdProt.OBD_SVC_CLEAR_CODES
                    binding.nodatatxt.visibility=View.VISIBLE
                    binding.scanninglist.visibility=View.GONE
                    isAlreadyCleared=true
                    Toast.makeText(this, "Cleared Successfully", Toast.LENGTH_SHORT).show()
//                    finish()
                }
                else{
                    billingManager.clearPurchase(getString(R.string.in_app_purchase_clear_mil_for_one_time_product_id))
                    startActivity(Intent(this@ScanningActivity,PurchaseActivity::class.java)
                        .putExtra("which","single"))
                }
            }
            else{
                if(PairedDeviceSharedPreference.getInstance(this).subsList.size>0){
                    CommService.elm.service = ObdProt.OBD_SVC_CLEAR_CODES
                    binding.nodatatxt.visibility=View.VISIBLE
                    binding.scanninglist.visibility=View.GONE
                    isAlreadyCleared=true
                    Toast.makeText(this, "Cleared Successfully", Toast.LENGTH_SHORT).show()
                }
                else{
                    Log.d("Clear_worker", "purchases list is empty")
                    billingManager.clearPurchase(getString(R.string.in_app_purchase_clear_mil_for_one_time_product_id))
                    startActivity(Intent(this@ScanningActivity,PurchaseActivity::class.java)
                        .putExtra("which","single"))
                }

            }

        /*    Log.d("Clear_worker", "Starting Clear card")
            if(AppPurchase.getInstance().isPurchased){
                Log.d("Clear_worker", "App is purchased")
                for(purchases in AppPurchase.getInstance().ownerIdInapps){
                    if (purchases.equals("com.obd.milbundle")){
                        Log.d("Clear_worker", "mil bundle buyed")
                        if (AppUtils.connectedSocket != null) {
                            var milleft = prefs.getInt("mil_left",0)
                            if(milleft>0){
                                milleft = milleft-1
                                if(milleft==0){
                                    billingManager.clearPurchase("com.obd.milbundle")
                                }
                                prefs.edit().putInt("mil_left",milleft).apply()
                                CommService.elm.service = ObdProt.OBD_SVC_CLEAR_CODES
                                Toast.makeText(this, "Clear Successfully", Toast.LENGTH_SHORT).show()
                                finish()
                            }
                            else{
                                billingManager.clearPurchase("com.obd.milbundle")
                                val list  = billingManager.getSkuDetailsList()
                                val skuDetailsList:  ArrayList<PurchaseModel> = arrayListOf()
                                skuDetailsList.add(PurchaseModel(list!!.get(0),true))
                                var skuDetail: SkuDetails?= null
                                for (i in skuDetailsList){
                                    if (i.ischecked){
                                        skuDetail = i.skuDetails
                                        break
                                    }
                                }
//                val skuDetail = skuDetailsList?.find { it.sku == "com.obd.milbundle" }

                                if (skuDetail != null) {
                                    billingManager.launchBillingFlow(this, skuDetail)
                                } else {
                                    Toast.makeText(this, "Product details not available", Toast.LENGTH_SHORT).show()
                                }
                            }

                            break
                        }
                    }
                }
            }
            else{
                try {
                    val list  = billingManager.getSkuDetailsList()
                    val skuDetailsList:  ArrayList<PurchaseModel> = arrayListOf()
                    skuDetailsList.add(PurchaseModel(list!!.get(0),true))
                    var skuDetail: SkuDetails?= null
                    for (i in skuDetailsList){
                        if (i.ischecked){
                            skuDetail = i.skuDetails
                            break
                        }
                    }
                    if (skuDetail != null) {
                        billingManager.launchBillingFlow(this, skuDetail)
                    } else {
                        Toast.makeText(this, "Product details not available", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {

                }
            }
*/

        }

        binding.searchet.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(MyMainActivity.Constants.currDataAdapter!=null) {
                    MyMainActivity.Constants.currDataAdapter.filter.filter(s)
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })


    }
    private fun clearDiagnosticData(bluetoothSocket: BluetoothSocket) {
        bluetoothSocket?.let { socket ->
            try {
                // Send the command to clear all diagnostic data
                val outputStream: OutputStream = socket.outputStream
                val writer = PrintWriter(outputStream, true)

                // Command to clear diagnostic trouble codes (DTCs)
                writer.println("AT Z") // ELM327 command to clear all DTCs
                writer.flush()

                // Command to clear Freeze Frame Data (typically part of DTC clearing)
                writer.println("AT C") // ELM327 command to clear Freeze Frame Data
                writer.flush()

                // Optionally, you might want to send more commands here based on your needs

                // Confirm command was sent
                Toast.makeText(this@ScanningActivity, "Diagnostic data cleared successfully", Toast.LENGTH_SHORT).show()

            } catch (e: Exception) {
//                e.printStackTrace()
                Log.d("clearDiagnosticData", "clearDiagnosticData: exception: ${e.printStackTrace()}")
            }
        }?: Toast.makeText(this@ScanningActivity, "Socket Error", Toast.LENGTH_SHORT).show()
    }

    override fun onLoadTroubleCodesProgressFinished(result: String?, response: Int) {

    }
    private fun initInAppPurchase() {
        inAppPurchase = InAppPurchase.getInstance(this, getString(R.string.base64key))
        inAppPurchase!!.initPurchasing()
    }

}