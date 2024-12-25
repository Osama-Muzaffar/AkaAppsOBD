package com.akapps.obd2carscannerapp.Activity

import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.akapps.obd2carscannerapp.Ads.AdManager
import com.google.android.gms.ads.AdError
import com.google.android.play.core.splitinstall.SplitInstallManagerFactory
import com.akapps.obd2carscannerapp.Ads.AdManagerCallback
import com.akapps.obd2carscannerapp.Ads.BannerAdView
import com.akapps.obd2carscannerapp.Ads.billing.AppPurchase
import com.akapps.obd2carscannerapp.Ads.ump.AdsConsentManager
import com.akapps.obd2carscannerapp.BuildConfig
import com.akapps.obd2carscannerapp.Database.DatabaseHelper
import com.akapps.obd2carscannerapp.Models.BillingManager
import com.akapps.obd2carscannerapp.Models.SharedPreferencesHelper
import com.akapps.obd2carscannerapp.MyApp
import com.akapps.obd2carscannerapp.R
import com.akapps.obd2carscannerapp.Reciever.NotificationReceiver
import com.akapps.obd2carscannerapp.Trips.PairedDeviceSharedPreference
import com.akapps.obd2carscannerapp.Utils.AppUtils.intersitialAdUtils
import com.akapps.obd2carscannerapp.Utils.AppUtils.issplashOpening
import com.akapps.obd2carscannerapp.databinding.ActivitySplashBinding
import com.android.billingclient.api.Purchase
import com.google.firebase.remoteconfig.ConfigUpdate
import com.google.firebase.remoteconfig.ConfigUpdateListener
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigException
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.google.gson.Gson
import com.quadsquad.savestatus.videodownloader.storysaver.statuskeeper.adsConfigs.AdsConfig
import com.quadsquad.savestatus.videodownloader.storysaver.statuskeeper.adsConfigs.adsConfig
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.Executors


class SplashActivity : AppCompatActivity() {
    private lateinit var billingManager: BillingManager
    private var prefs: SharedPreferencesHelper? = null
    private lateinit var dbHelper: DatabaseHelper

    lateinit var binding: ActivitySplashBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        window.decorView.layoutDirection = View.LAYOUT_DIRECTION_LTR
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        changeStatusBarColorFromResource(R.color.unchange_black)


        // Initialize BillingManager
        billingManager = BillingManager(this, object : BillingManager.BillingListner {
            override fun onPurchaseFinished(purchase: Purchase, isSub: Boolean) {

            }

            override fun onPurchaseFailed(message: String) {

            }
        })

        // Start billing client connection
        billingManager.startConnection(single = false)

        if (isInternetAvailable(this)) {
            inilizeRemoteConfig()
            Handler(Looper.getMainLooper()).postDelayed(Runnable {
                Log.d("Restore_Purchase", "starting restoring purchases")

                billingManager?.restorePurchases(object : BillingManager.BillingListner {
                    override fun onPurchaseFinished(purchase: Purchase, isSub: Boolean) {
                        Log.d(
                            "Restore_Purchase",
                            "onPurchaseFinished: purchase = ${purchase.skus.first()}"
                        )
                        if (purchase.skus.first()
                                .equals(getString(R.string.in_app_purchase_clear_mil_for_one_time_product_id))
                        ) {
                        } else {
                            PairedDeviceSharedPreference.getInstance(this@SplashActivity)
                                .addItemToList(purchase.skus.first())

                        }
                    }

                    override fun onPurchaseFailed(messages: String) {
                        Log.d("Restore_Purchase", "onPurchaseFailed: " + messages)
                    }

                })


                billingManager?.restoreSubscriptions(object : BillingManager.BillingListner {
                    override fun onPurchaseFinished(purchase: Purchase, isSub: Boolean) {
                        Log.d(
                            "Restore_Purchase",
                            "onPurchaseFinished: subs = ${purchase.skus.first()}"
                        )
                        PairedDeviceSharedPreference.getInstance(this@SplashActivity)
                            .addItemToSubsList(purchase.skus.first())


                    }

                    override fun onPurchaseFailed(messages: String) {
                        Log.d("Restore_Purchase", "onPurchaseFailed: " + messages)
                    }

                })
            }, 1000)

            /*  Handler(Looper.getMainLooper()).postDelayed(Runnable {
                  Log.d("remote", "test banner ad at splash")
  //                if(PairedDeviceSharedPreference.getInstance(this@SplashActivity).list.size>0){
  //                    Log.d("Purchases_Splash", "purchases list is greater than 0")
  //                    var isshwoingad= true
  //                    for(purchases in PairedDeviceSharedPreference.getInstance(this@SplashActivity).list){
  //                        if (purchases.equals(getString(R.string.in_app_purchase_remove_ads_product_id)) ||
  //                            purchases.equals(getString(R.string.in_app_purchase_unlock_all_features_product_id))){
  //                            isshwoingad= false
  //                        }
  //                        else{
  //                            isshwoingad= true
  //                        }
  //                    }
  //                    Log.d("Purchases_Splash", "isshwoingad = $isshwoingad")
  //
  //                    if(isshwoingad) {
  //                        AdManager.getInstance()
  //                            .loadInterstitialAd(this@SplashActivity, BuildConfig.admob_intersitial)
  //
  //                        if (RemoteConfigManager.isSplashBannerEnable) {
  //                            val bannerview = findViewById<BannerAdView>(R.id.bannerView)
  //                            bannerview.visibility=View.VISIBLE
  //                            bannerview.loadBanner(this@SplashActivity, BuildConfig.admob_banner)
  //                        } else {
  //                            val bannerview = findViewById<BannerAdView>(R.id.bannerView)
  //                            bannerview.visibility = View.GONE
  //                        }
  //                    }
  //                    else{
  //                        val bannerview = findViewById<BannerAdView>(R.id.bannerView)
  //                        bannerview.visibility= View.GONE
  //                    }
  //
  //                }
  //                else{
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
                              Log.d("Purchases_Splash", "purchases list is empty")
                              AdManager.getInstance()
                                  .loadInterstitialAd(this@SplashActivity, BuildConfig.admob_intersitial)

                              if(adsConfig!=null) {
                                      val bannerview = findViewById<BannerAdView>(R.id.banneradsview)
                                      bannerview.visibility = View.VISIBLE
                                      bannerview.loadBanner(
                                          this@SplashActivity,
                                          BuildConfig.admob_banner
                                      )

                              } else {
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
                          Log.d("Purchases_Splash", "purchases list is empty")
                          AdManager.getInstance()
                              .loadInterstitialAd(this@SplashActivity, BuildConfig.admob_intersitial)

                          if(adsConfig!=null) {
                              if(adsConfig?.splash_banner!!) {
                                  val bannerview = findViewById<BannerAdView>(R.id.banneradsview)
                                  bannerview.visibility = View.VISIBLE
                                  bannerview.loadBanner(
                                      this@SplashActivity,
                                      BuildConfig.admob_banner
                                  )
                              }
                              else{
                                  val bannerview = findViewById<BannerAdView>(R.id.banneradsview)
                                  bannerview.visibility = View.GONE
                              }

                          } else {
                              Log.d("test_remote", "remote config is null")

                              val bannerview = findViewById<BannerAdView>(R.id.banneradsview)
                              bannerview.visibility = View.VISIBLE
                              bannerview.loadBanner(
                                  this@SplashActivity,
                                  BuildConfig.admob_banner
                              )
                          }
                      }
  //                }
              },5000)
  */

        } else {
            Handler(Looper.getMainLooper()).postDelayed(Runnable {
                binding.progressbar.visibility = View.GONE
                binding.letsgocard.visibility = View.VISIBLE
            }, 4000)
        }
//        Handler(Looper.getMainLooper()).postDelayed(Runnable {
//            binding.progressbar.visibility=View.GONE
//            binding.letsgocard.visibility=View.VISIBLE
//        },7000)

        dbHelper = DatabaseHelper(this)
        dbHelper.createDatabase()

        prefs = SharedPreferencesHelper(this)

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, NotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 10) // 4:00 PM
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            if (timeInMillis < System.currentTimeMillis()) {
                add(Calendar.DAY_OF_MONTH, 1) // Schedule for the next day if time has passed
            }
        }

        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )


        val adsConsentManager: AdsConsentManager = AdsConsentManager.getInstance(this)

        adsConsentManager.requestUMP(this@SplashActivity) {
            Log.d("Splash", "Request Ump: " + it)
            if (it) {
                Log.d("Splash", "Is Purchased: " + AppPurchase.getInstance().isPurchased())

            }


        }
//      val listpurchases=listOf("com.obd.inapp", "com.obdblue.allpremium", "com.obd.milbundle")

//        val listpurchases = listOf(
//            PurchaseItem("com.obd.inapp",AppPurchase.TYPE_IAP.PURCHASE),
//            PurchaseItem("com.obdblue.allpremium",AppPurchase.TYPE_IAP.PURCHASE),
//            PurchaseItem("com.obd.milbundle",AppPurchase.TYPE_IAP.PURCHASE),
//        )
//
//        AppPurchase.getInstance().initBilling(application,listpurchases)
//
//        AppPurchase.getInstance().setBillingListener(object : BillingListener {
//            override fun onInitBillingFinished(resultCode: Int) {
//
//                adsConsentManager.requestUMP(this@SplashActivity) {
//                    Log.d("Splash", "Request Ump: "+it)
//                    if(it){
//                        Log.d("Splash", "Is Purchased: "+AppPurchase.getInstance().isPurchased())
//
//                    }
//
//
//                }
//            }
//        }, 10 * 1000)
//

        Log.d("Splash_Checker", "oncreate: running")
        Log.d("Splash_Checker", "issplashOpening: $issplashOpening")

        if (!issplashOpening) {
//            checkPurchaseStatus()
        }
        issplashOpening = true
        /*
                // Change status bar icon color
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    val decor = window.decorView
                    if (decor != null) {
                        decor.setSystemUiVisibility(0); // Clears the SYSTEM_UI_FLAG_LIGHT_STATUS_BAR flag
                    }
                }


                // Optional: Change status bar background color
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    val window = window
                    window.statusBarColor = ContextCompat.getColor(this,R.color.black)
                }*/

        binding.letsgocard.setOnClickListener {


            if (PairedDeviceSharedPreference.getInstance(this).subsList.size > 0) {
                var isshwoingad = true
                for (purchases in PairedDeviceSharedPreference.getInstance(this).subsList) {
//                    if (purchases.equals(getString(R.string.in_app_subscription_yearly_product_id))){
//                        isshwoingad= false
//                        break
//
//                    }
//                    else{
                    isshwoingad = false
//                    }
                }
                if (isshwoingad) {
                    Log.d("Purchases_Splash", "subs list is not empty")
                    if(adsConfig!=null){
                        if(adsConfig!!.splash_intersitial){
                            intersitialAdUtils++
                            if (AdManager.getInstance().isReady() && intersitialAdUtils%2==0) {
                                AdManager.getInstance().forceShowInterstitial(this@SplashActivity,
                                    object : AdManagerCallback() {
                                        override fun onNextAction() {
                                            super.onNextAction()
                                            navigateToMainActivity()
                                        }

                                        override fun onFailedToLoad(error: AdError?) {
                                            Log.d("Track_Ads", "Failed to ad")
                                            super.onFailedToLoad(error)
                                            navigateToMainActivity()
                                        }
                                    },true)
                            } else {
                                Log.d("Track_Ads", "ad is not ready")
                                navigateToMainActivity()
                            }
                        }
                        else {
                            Log.d("Track_Ads", "splash intersitial is off")
                            navigateToMainActivity()
                        }
                    }
                    else {
                        Log.d("Track_Ads", "ads config is null")
                        navigateToMainActivity()
                    }


                } else {
                    navigateToMainActivity()
                }
            } else {
                Log.d("Splash_Tracking", "purchase is null")
                if(adsConfig!=null){
                    Log.d("Splash_Tracking", "ads config is not null")
                    if(adsConfig!!.splash_intersitial){
                        Log.d("Splash_Tracking", "remote splash intersiitial is true")

                        intersitialAdUtils++
                        if (AdManager.getInstance().isReady() && intersitialAdUtils%2==0) {
                            Log.d("Splash_Tracking", "ad is ready trying to show")
                            AdManager.getInstance().forceShowInterstitial(this@SplashActivity,
                                object : AdManagerCallback() {
                                    override fun onNextAction() {
                                        super.onNextAction()
                                        navigateToMainActivity()
                                    }

                                    override fun onFailedToLoad(error: AdError?) {
                                        super.onFailedToLoad(error)
                                        navigateToMainActivity()
                                    }
                                },true)
                        } else {
                            Log.d("Splash_Tracking", "ad is not ready")
                            navigateToMainActivity()
                        }
                    }
                    else {
                        Log.d("Splash_Tracking", "remote splash is false")
                        navigateToMainActivity()
                    }
                }
                else {
                    Log.d("Splash_Tracking", "purchase is null")
                    navigateToMainActivity()
                }
            }

        }
    }

    override fun onResume() {
        super.onResume()
        // Ensure to check for purchases or restore them on resume
    }

    private fun checkPurchaseStatus() {

        Log.d("Splash_Checker", "checkPurchaseStatus: running")
        // Check and restore purchases
        billingManager.checkAndRestorePurchases()

        // Simulate loading or processing time
        Handler(Looper.getMainLooper()).postDelayed({
            Log.d("Splash_Checker", "mAIN HAndler running")

//            navigateToNextActivity()

            Handler(Looper.getMainLooper()).postDelayed({
                Log.d("Splash_Checker", "Inner HAndler running")
//                binding.letsgocard.visibility= View.VISIBLE
//                binding.progressbar.visibility= View.GONE
                if (!AppPurchase.getInstance().isPurchased()) {
                    Log.d("Splash_Checker", "Purchase is not")
                    // Initialize AppOpenManager
                    val appOpenManager = (application as MyApp).appOpenManager

                    // Show app open ad if available
                    appOpenManager.showSplashAdIfAvailable(this) {
                        navigateToMainActivity()
                    }
                } else {
                    Log.d("Splash_Checker", "Purchase is true")
                    var isshwoingad = true
                    for (purchases in AppPurchase.getInstance().ownerIdInapps) {
                        if (purchases.equals("com.obd.inapp") || purchases.equals("com.obdblue.allpremium")) {
                            isshwoingad = false
                        } else {
                            isshwoingad = true
                        }
                    }
                    if (isshwoingad) {
                        Log.d("Splash_Checker", "isshwoingad = true")

                        // Initialize AppOpenManager
                        val appOpenManager = (application as MyApp).appOpenManager

                        // Show app open ad if available
                        appOpenManager.showSplashAdIfAvailable(this) {
                            navigateToMainActivity()
                        }
                    } else {
                        Log.d("Splash_Checker", "isshwoingad = false")
                        navigateToMainActivity()
                    }
                }

            }, 5000)

            if (!AppPurchase.getInstance().isPurchased()) {
                val bannerad = findViewById<BannerAdView>(R.id.banneradsview)
                bannerad.loadBanner(
                    this@SplashActivity,
                    BuildConfig.admob_banner,
                    object : AdManagerCallback() {
                        override fun onAdLoaded() {
                            super.onAdLoaded()
                            Log.d("Splash", "onAdLoaded: ")
                        }

                        override fun onFailedToLoad(error: AdError?) {
                            super.onFailedToLoad(error)
                            Log.d("Splash", "onFailedToLoad: ")
                        }
                    })
            } else {
                Log.d("Splash", "purchase true else is running ")
                var isshwoingad = true
                for (purchases in AppPurchase.getInstance().ownerIdInapps) {
                    if (purchases.equals("com.obd.inapp") || purchases.equals("com.obdblue.allpremium")) {
                        isshwoingad = false
                    } else {
                        isshwoingad = true
                    }
                }
                Log.d("Splash", "isshwoingad = $isshwoingad ")
                if (isshwoingad) {
                    val bannerad = findViewById<BannerAdView>(R.id.banneradsview)
                    Log.d("Splash", "Trying to load Bnnaer ad where isshowing ad = $isshwoingad ")
                    bannerad.loadBanner(
                        this@SplashActivity,
                        BuildConfig.admob_banner,
                        object : AdManagerCallback() {
                            override fun onAdLoaded() {
                                super.onAdLoaded()
                                Log.d("Splash", "onAdLoaded: ")
                            }

                            override fun onFailedToLoad(error: AdError?) {
                                super.onFailedToLoad(error)
                                Log.d("Splash", "onFailedToLoad: ")
                            }
                        })
                }
            }
        }, 5000)
    }

    private fun navigateToNextActivity() {
        val lastPurchaseSku = billingManager.getSavedPurchase()
        if (lastPurchaseSku != null) {
            // User has a purchase, proceed to the main activity

            if (lastPurchaseSku.equals("com.obd.milbundle")) {
                prefs!!.putString("purchase", "10mil")
            } else if (lastPurchaseSku.equals("com.obdblue.allpremium")) {
                prefs!!.putString("purchase", "allpremium")
            } else {
                prefs!!.putString("purchase", "inapp")
            }
        }

    }

    private fun navigateToMainActivity() {
        val prefs = SharedPreferencesHelper(this)
        val preflng = prefs.getString(SharedPreferencesHelper.KEY_LANGUAGE, "en")
        setLocale(this, preflng)
        val firstime = prefs.getBoolean(SharedPreferencesHelper.KEY_FIRSTTIME, true)
        if (firstime) {
            val intent = Intent(this, SelectLanguageActivity::class.java)
            startActivity(intent)
            finish()
        } else {

            val intent = Intent(this, MyMainActivity::class.java)
            startActivity(intent)
            finish()
        }

    }

    private fun navigateToPurchaseActivity() {
        val intent = Intent(this, PurchaseActivity::class.java)
        startActivity(intent)
        finish() // Optional: close the splash activity
    }

    override fun onDestroy() {
        super.onDestroy()
        // End the billing client connection when the activity is destroyed
        billingManager.endConnection()
    }

    fun setLocale(activity: Activity, langcode: String?) {
        if (langcode != "") {
            downloadSelectedLanguage(langcode)
            val appLocale = LocaleListCompat.forLanguageTags(langcode)
            AppCompatDelegate.setApplicationLocales(appLocale)
        }

    }


    fun downloadSelectedLanguage(lan: String?) {
        val splitInstallManager = SplitInstallManagerFactory.create(this)
        val locales: MutableList<Locale> = ArrayList()
        locales.add(Locale.forLanguageTag(lan))
        splitInstallManager.deferredLanguageInstall(locales)
    }

    fun isInternetAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
            capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        } else {
            val activeNetworkInfo = connectivityManager.activeNetworkInfo
            activeNetworkInfo != null && activeNetworkInfo.isConnected
        }
    }


    fun inilizeRemoteConfig() {
        var remoteConfig = FirebaseRemoteConfig.getInstance()
        val configSettings = FirebaseRemoteConfigSettings.Builder()
            .setMinimumFetchIntervalInSeconds(0) // 1 hour
            .build()
        remoteConfig.setConfigSettingsAsync(configSettings)
        remoteConfig.setDefaultsAsync(R.xml.ads_remote_config)
        val executor = Executors.newSingleThreadExecutor()
        remoteConfig.fetchAndActivate().addOnCompleteListener(executor)
        { task ->
            if (task.isSuccessful) {
                val updated = task.result
                Log.d("remote", "Config params updated: $updated")
                val jsonConfig = remoteConfig.getString("ads_json_strings")
                val adsJsonConfig = Gson().fromJson(jsonConfig, AdsConfig::class.java)
                if (adsJsonConfig != null) {
                    adsConfig = adsJsonConfig
                    if (PairedDeviceSharedPreference.getInstance(this).subsList.size > 0) {
                        var isshwoingad = true
                        for (purchases in PairedDeviceSharedPreference.getInstance(this).subsList) {
//                    if (purchases.equals(getString(R.string.in_app_subscription_yearly_product_id))){
//                        isshwoingad= false
//                        break
//
//                    }
//                    else{
                            isshwoingad = false
//                    }
                        }
                        if (isshwoingad) {
                            Log.d("Purchases_Splash", "purchases list is empty")
                            runOnUiThread {

                                AdManager.getInstance()
                                    .loadInterstitialAd(
                                        this@SplashActivity,
                                        BuildConfig.admob_intersitial
                                    )

                                if (adsConfig != null) {
                                    if (adsConfig!!.splash_banner!!) {
                                        val bannerview =
                                            findViewById<BannerAdView>(R.id.banneradsview)
                                        bannerview.visibility = View.VISIBLE
                                        bannerview.loadBanner(
                                            this@SplashActivity,
                                            BuildConfig.admob_banner
                                        )
                                    } else {
                                        val bannerview =
                                            findViewById<BannerAdView>(R.id.banneradsview)
                                        bannerview.visibility = View.GONE
                                    }

                                } else {
                                    Log.d("test_remote", "remote config is null")
                                    val bannerview = findViewById<BannerAdView>(R.id.banneradsview)
                                    bannerview.visibility = View.GONE
                                }

                            }
                        } else {
                            val bannerview = findViewById<BannerAdView>(R.id.banneradsview)
                            bannerview.visibility = View.GONE
                        }
                    } else {
                        runOnUiThread {
                            Log.d("Purchases_Splash", "purchases list is empty")
                            AdManager.getInstance()
                                .loadInterstitialAd(
                                    this@SplashActivity,
                                    BuildConfig.admob_intersitial
                                )

                            if (adsConfig != null) {
                                if (adsConfig?.splash_banner!!) {
                                    val bannerview = findViewById<BannerAdView>(R.id.banneradsview)
                                    bannerview.visibility = View.VISIBLE
                                    bannerview.loadBanner(
                                        this@SplashActivity,
                                        BuildConfig.admob_banner
                                    )
                                } else {
                                    val bannerview = findViewById<BannerAdView>(R.id.banneradsview)
                                    bannerview.visibility = View.GONE
                                }

                            } else {
                                Log.d("test_remote", "remote config is null")

                                val bannerview = findViewById<BannerAdView>(R.id.banneradsview)
                                bannerview.visibility = View.VISIBLE
                                bannerview.loadBanner(
                                    this@SplashActivity,
                                    BuildConfig.admob_banner
                                )
                            }
                        }

                    }

                    Handler(Looper.getMainLooper()).postDelayed(Runnable {
                        binding.progressbar.visibility = View.GONE
                        binding.letsgocard.visibility = View.VISIBLE
                    }, 4000)
                }
            } else {
                Log.d("remote", "Config params update failed")
            }
        }
        remoteConfig.addOnConfigUpdateListener(object : ConfigUpdateListener {
            override fun onUpdate(configUpdate: ConfigUpdate) {
                Log.d("remote", "Updated keys: " + configUpdate.updatedKeys)

                // Check if the updated keys contain the relevant JSON key
                if (configUpdate.updatedKeys.contains("ads_json_strings")) {
                    remoteConfig.activate().addOnCompleteListener {
                        val jsonConfig = remoteConfig.getString("ads_json_strings")
                        val adsJsonConfig = Gson().fromJson(jsonConfig, AdsConfig::class.java)
                        if (adsJsonConfig != null) {
                            adsConfig = adsJsonConfig
                        }
                    }
                }
            }

            override fun onError(error: FirebaseRemoteConfigException) {
                Log.w("remote", "Config update error with code: " + error.code, error)
            }
        })
    }

}