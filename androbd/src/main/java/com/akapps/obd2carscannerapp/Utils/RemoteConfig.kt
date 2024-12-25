package com.akapps.obd2carscannerapp.Utils
import android.util.Log
import com.google.firebase.remoteconfig.ConfigUpdate
import com.google.firebase.remoteconfig.ConfigUpdateListener
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigException
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.akapps.obd2carscannerapp.R

object RemoteConfig {
    var islangugaenativeopen = false
    var isSettingNativeOpen = false
    var setting_native = "setting_native"
    var language_native = "language_native"


    val configSettings = FirebaseRemoteConfigSettings.Builder()
        .setMinimumFetchIntervalInSeconds(60)
        .build()
    var remoteConfig = FirebaseRemoteConfig.getInstance().apply {
        setConfigSettingsAsync(configSettings)
        setDefaultsAsync(R.xml.remote_config_default)
        addOnConfigUpdateListener(object : ConfigUpdateListener {
            override fun onUpdate(configUpdate: ConfigUpdate) {

                if (configUpdate.updatedKeys.contains(setting_native)) {
                    this@apply.activate().addOnCompleteListener {

                        isSettingNativeOpen = this@apply.getBoolean(setting_native)

                    }
                }

                if (configUpdate.updatedKeys.contains(language_native)) {
                    this@apply.activate().addOnCompleteListener {
                        islangugaenativeopen = this@apply.getBoolean(language_native)

                    }
                }

            }

            override fun onError(error: FirebaseRemoteConfigException) {

            }
        })
    }


    fun activateFirebase(remoteConfig: FirebaseRemoteConfig) {

        try {
            remoteConfig.fetchAndActivate()
                .addOnCompleteListener { task ->
                    val msg = task.result
                    if (task.isSuccessful) {

                        Log.d("msg", "All set up: $msg")

                    } else {
                        Log.d("msg", "set up status: $msg")
                    }

                }
        } catch (e: Exception) {

        }
    }
    fun fetchRemoteResult(remoteKey: String, onResultSuccess: (Boolean) -> Unit){
        onResultSuccess(remoteConfig.getBoolean(remoteKey))
    }
}