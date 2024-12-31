package com.akapps.obd2carscannerapp.Activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.floor.planner.models.NativeBannerFull
import com.akapps.obd2carscannerapp.BuildConfig
import com.akapps.obd2carscannerapp.R
import com.akapps.obd2carscannerapp.Trips.PairedDeviceSharedPreference
import com.akapps.obd2carscannerapp.databinding.ActivityAppSettingBinding
import com.quadsquad.savestatus.videodownloader.storysaver.statuskeeper.adsConfigs.adsConfig

class SettingActivity : AppCompatActivity() {
    lateinit var binding: ActivityAppSettingBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        window.decorView.layoutDirection = View.LAYOUT_DIRECTION_LTR
        binding= ActivityAppSettingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        changeStatusBarColorFromResource(R.color.unchange_black)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.backrelative.setOnClickListener {
            finish()
        }

        binding.sharelinear.setOnClickListener {
            shareApp()
        }

        binding.ratelinear.setOnClickListener {
            rateApp()
        }

        binding.languagelinear.setOnClickListener {
            startActivity(Intent(this@SettingActivity,SelectLanguageActivity::class.java))
        }

        binding.morelinear.setOnClickListener {
            showMoreApps()
        }

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

                     if(adsConfig!!.setting_native) {
                         val nativefull = findViewById<NativeBannerFull>(R.id.nativefull)
                         nativefull.loadNativeBannerAd(this, BuildConfig.admob_native)
                    }
                    else{
                         val nativefull = findViewById<NativeBannerFull>(R.id.nativefull)
                         nativefull.visibility = View.GONE
                    }

                }
                else {
                    Log.d("test_remote", "remote config is null")
                    val nativefull = findViewById<NativeBannerFull>(R.id.nativefull)
                    nativefull.visibility = View.GONE
                }
            }
            else{
                val nativefull = findViewById<NativeBannerFull>(R.id.nativefull)
                nativefull.visibility = View.GONE
            }
        }
        else {
            runOnUiThread {

                if(adsConfig !=null) {

                    if(adsConfig!!.setting_native) {
                        val nativefull = findViewById<NativeBannerFull>(R.id.nativefull)
                        nativefull.loadNativeBannerAd(this, BuildConfig.admob_native)
                    }
                    else{
                        val nativefull = findViewById<NativeBannerFull>(R.id.nativefull)
                        nativefull.visibility = View.GONE
                    }

                }
                else {
                    Log.d("test_remote", "remote config is null")
                    val nativefull = findViewById<NativeBannerFull>(R.id.nativefull)
                    nativefull.visibility = View.GONE
                }
            }

        }
       /* if(!AppPurchase.getInstance().isPurchased){
            if(RemoteConfig.isSettingNativeOpen) {
                val nativefull = findViewById<NativeBannerFull>(R.id.nativefull)
                nativefull.loadNativeBannerAd(this, BuildConfig.admob_native)
            }
            else{
                val nativefull= findViewById<NativeBannerFull>(R.id.nativefull)
                nativefull.visibility= View.GONE
            }
        }
        else{
            val nativefull= findViewById<NativeBannerFull>(R.id.nativefull)
            nativefull.visibility= View.GONE
        }
*/
    }
    private fun shareApp() {
        val appPackageName = packageName // Get the app package name
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, "Check out this amazing app: https://play.google.com/store/apps/details?id=$appPackageName")
            type = "text/plain"
        }
        startActivity(Intent.createChooser(shareIntent, "Share App"))
    }

    private fun showMoreApps() {
//        val developerId = getString(R.string.ak_apps_games)
        val moreAppsUrl = getString(R.string.dev_link)
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(moreAppsUrl))
        startActivity(intent)
    }

    private fun rateApp() {
        val appPackageName = packageName // Get the app package name
        try {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$appPackageName")))
        } catch (e: Exception) {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")))
        }
    }
}