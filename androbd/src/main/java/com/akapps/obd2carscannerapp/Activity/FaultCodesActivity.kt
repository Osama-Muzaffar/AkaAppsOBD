package com.akapps.obd2carscannerapp.Activity

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.akapps.obd2carscannerapp.Adapter.FaultRvAdapter
import com.akapps.obd2carscannerapp.Ads.BannerAdView
import com.akapps.obd2carscannerapp.Ads.billing.AppPurchase
import com.akapps.obd2carscannerapp.BuildConfig
import com.akapps.obd2carscannerapp.Database.DatabaseHelper
import com.akapps.obd2carscannerapp.Database.ObdEntry
import com.akapps.obd2carscannerapp.R
import com.akapps.obd2carscannerapp.Trips.PairedDeviceSharedPreference
import com.akapps.obd2carscannerapp.databinding.ActivityFaultCodesBinding
import com.quadsquad.savestatus.videodownloader.storysaver.statuskeeper.adsConfigs.adsConfig

class FaultCodesActivity : AppCompatActivity() {
    lateinit var  binding: ActivityFaultCodesBinding
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        window.decorView.layoutDirection = View.LAYOUT_DIRECTION_LTR
        binding= ActivityFaultCodesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        changeStatusBarColorFromResource(R.color.unchange_black)
        dbHelper = DatabaseHelper(this)
        dbHelper.createDatabase()

        // Get all rows from the obd table
        val obdEntries: List<ObdEntry> = dbHelper.getAllRowsFromObdTable()

        val adapter= FaultRvAdapter(this,obdEntries as ArrayList<ObdEntry>)
        binding.faultrecyclerview.layoutManager= LinearLayoutManager(this)
        binding.faultrecyclerview.adapter= adapter


        binding.backrelative.setOnClickListener {
            finish()
        }

        binding.searchet.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                adapter.filter(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        if(PairedDeviceSharedPreference.getInstance(this).subsList.size>0){
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
                    if(adsConfig!!.faultcodes_banner_simple && adsConfig!!.faultcodes_banner_collapse){
                        val bannerview = findViewById<BannerAdView>(R.id.banneradsview)
                        bannerview.visibility = View.VISIBLE
                        bannerview.loadBanner(
                            this@FaultCodesActivity,
                            BuildConfig.admob_banner
                        )
                    }
                    else if(adsConfig!!.faultcodes_banner_simple) {
                        val bannerview = findViewById<BannerAdView>(R.id.banneradsview)
                        bannerview.visibility = View.VISIBLE
                        bannerview.loadBanner(
                            this@FaultCodesActivity,
                            BuildConfig.admob_banner
                        )
                    }
                    else  if(adsConfig!!.faultcodes_banner_collapse) {
                        val bannerview = findViewById<BannerAdView>(R.id.banneradsview)
                        bannerview.visibility = View.VISIBLE
                        bannerview.loadCollapsibleBanner(
                            this@FaultCodesActivity,
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
                    if(adsConfig!!.faultcodes_banner_simple && adsConfig!!.faultcodes_banner_collapse){
                        val bannerview = findViewById<BannerAdView>(R.id.banneradsview)
                        bannerview.visibility = View.VISIBLE
                        bannerview.loadBanner(
                            this@FaultCodesActivity,
                            BuildConfig.admob_banner
                        )
                    }
                    else if(adsConfig!!.faultcodes_banner_simple) {
                        val bannerview = findViewById<BannerAdView>(R.id.banneradsview)
                        bannerview.visibility = View.VISIBLE
                        bannerview.loadBanner(
                            this@FaultCodesActivity,
                            BuildConfig.admob_banner
                        )
                    }
                    else  if(adsConfig!!.faultcodes_banner_collapse) {
                        val bannerview = findViewById<BannerAdView>(R.id.banneradsview)
                        bannerview.visibility = View.VISIBLE
                        bannerview.loadCollapsibleBanner(
                            this@FaultCodesActivity,
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

        }

     /*   if(!AppPurchase.getInstance().isPurchased){
            val banneradsview= findViewById<BannerAdView>(R.id.banneradsview)
            banneradsview.loadBanner(this, BuildConfig.admob_banner)
        }
        else{
            val banneradsview= findViewById<BannerAdView>(R.id.banneradsview)
            banneradsview.visibility= View.GONE
        }*/
    }
}