package com.akapps.obd2carscannerapp

import android.app.Activity
import android.app.Application
import android.content.Context
import android.util.Log
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.akapps.obd2carscannerapp.Ads.AppOpenManager
import com.akapps.obd2carscannerapp.Database.DatabaseHelper
import com.google.firebase.remoteconfig.ConfigUpdate
import com.google.firebase.remoteconfig.ConfigUpdateListener
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigException
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.google.gson.Gson
import com.onesignal.OneSignal
import com.onesignal.debug.LogLevel
import com.quadsquad.savestatus.videodownloader.storysaver.statuskeeper.adsConfigs.AdsConfig
import com.quadsquad.savestatus.videodownloader.storysaver.statuskeeper.adsConfigs.adsConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.Executors

class MyApp : Application() {
    lateinit var appOpenManager: AppOpenManager
    val ONESIGNAL_APP_ID = "########-####-####-####-############"
    fun initAds() {

        val testDeviceIds: List<String> = mutableListOf(
            "EC60C39375F6619F5C03850A0E440646"
        )
        val configuration: RequestConfiguration =
            RequestConfiguration.Builder().setTestDeviceIds(testDeviceIds).build()
        MobileAds.setRequestConfiguration(configuration)
        MobileAds.initialize(this)

        appOpenManager = AppOpenManager(this, BuildConfig.admob_appopen)
    }

    override fun onCreate() {
        super.onCreate()
        val dbHelper = DatabaseHelper(applicationContext)
        dbHelper.createDatabase()
        // Verbose Logging set to help debug issues, remove before releasing your app.
        OneSignal.Debug.logLevel = LogLevel.VERBOSE

        // OneSignal Initialization
        OneSignal.initWithContext(this, ONESIGNAL_APP_ID)

        // requestPermission will show the native Android notification permission prompt.
        // NOTE: It's recommended to use a OneSignal In-App Message to prompt instead.
        CoroutineScope(Dispatchers.IO).launch {
            OneSignal.Notifications.requestPermission(false)
        }
        initAds()


    }


    companion object {
        lateinit var instance: MyApp
            private set
    }


    init {
        instance = this
    }
}