package com.akapps.obd2carscannerapp.Activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.RelativeLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.floor.planner.models.NativeBannerFull
import com.google.android.play.core.splitinstall.SplitInstallManagerFactory
import com.akapps.obd2carscannerapp.Adapter.LanguageAdapter
import com.akapps.obd2carscannerapp.Ads.BannerAdView
import com.akapps.obd2carscannerapp.BuildConfig
import com.akapps.obd2carscannerapp.Models.LanguageModel
import com.akapps.obd2carscannerapp.Models.SharedPreferencesHelper
import com.akapps.obd2carscannerapp.R
import com.akapps.obd2carscannerapp.Trips.PairedDeviceSharedPreference
import com.akapps.obd2carscannerapp.databinding.ActivitySelectLanguageBinding
import com.quadsquad.savestatus.videodownloader.storysaver.statuskeeper.adsConfigs.adsConfig
import java.util.Locale


class SelectLanguageActivity : AppCompatActivity() {
    lateinit var binding: ActivitySelectLanguageBinding
//    lateinit var nativefull: NativeBannerFull
    lateinit var LanguageList: ArrayList<LanguageModel>
    var selectedLanguageModel= LanguageModel("GB","English","en",false)
    lateinit var prefs: SharedPreferencesHelper
    lateinit var nativeBannerFull: NativeBannerFull
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        binding=ActivitySelectLanguageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.decorView.layoutDirection = View.LAYOUT_DIRECTION_LTR
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.backrelative.setOnClickListener {
            finish()
        }
        prefs= SharedPreferencesHelper(this)
        changeStatusBarColorFromResource(R.color.unchange_black)

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
                    if(adsConfig!!.language_native){
                        nativeBannerFull = findViewById<NativeBannerFull>(R.id.nativefull)
                        nativeBannerFull.visibility=View.VISIBLE
                        nativeBannerFull.loadMultipleNativeAds(this@SelectLanguageActivity,BuildConfig.admob_native,3)
                    }
                    else if(adsConfig!!.language_banner_simple && adsConfig!!.language_banner_collapsible) {
                        val bannerview = findViewById<BannerAdView>(R.id.banneradsview)
                        bannerview.visibility=View.VISIBLE
                        bannerview.visibility = View.VISIBLE
                        bannerview.loadBanner(
                            this@SelectLanguageActivity,
                            BuildConfig.admob_banner
                        )
                    }
                    else if(adsConfig!!.language_banner_simple) {

                        val bannerview = findViewById<BannerAdView>(R.id.banneradsview)
                        bannerview.visibility = View.VISIBLE
                        bannerview.loadBanner(
                            this@SelectLanguageActivity,
                            BuildConfig.admob_banner
                        )
                    }
                    else  if(adsConfig!!.language_banner_collapsible) {
                        val bannerview = findViewById<BannerAdView>(R.id.banneradsview)
                        bannerview.visibility = View.VISIBLE
                        bannerview.loadCollapsibleBanner(
                            this@SelectLanguageActivity,
                            BuildConfig.admob_banner,
                            true
                        )
                    }
                    else{
                        val adscontainer = findViewById<RelativeLayout>(R.id.ad_container)
                        adscontainer.visibility = View.GONE
                    }

                }
                else {
                    Log.d("test_remote", "remote config is null")
                    val adscontainer = findViewById<RelativeLayout>(R.id.ad_container)
                    adscontainer.visibility = View.GONE
                }
            }
            else {
                Log.d("test_remote", "remote config is null")
                val adscontainer = findViewById<RelativeLayout>(R.id.ad_container)
                adscontainer.visibility = View.GONE
            }
        }
        else {
            runOnUiThread {

                if(adsConfig !=null) {
                    if(adsConfig!!.language_native){
                        nativeBannerFull = findViewById<NativeBannerFull>(R.id.nativefull)
                        nativeBannerFull.visibility=View.VISIBLE
                        nativeBannerFull.loadMultipleNativeAds(this@SelectLanguageActivity,BuildConfig.admob_native,3)
                    }
                    else if(adsConfig!!.language_banner_simple && adsConfig!!.language_banner_collapsible) {
                        val bannerview = findViewById<BannerAdView>(R.id.banneradsview)
                        bannerview.visibility=View.VISIBLE
                        bannerview.visibility = View.VISIBLE
                        bannerview.loadBanner(
                            this@SelectLanguageActivity,
                            BuildConfig.admob_banner
                        )
                    }
                    else if(adsConfig!!.language_banner_simple) {

                        val bannerview = findViewById<BannerAdView>(R.id.banneradsview)
                        bannerview.visibility = View.VISIBLE
                        bannerview.loadBanner(
                            this@SelectLanguageActivity,
                            BuildConfig.admob_banner
                        )
                    }
                    else  if(adsConfig!!.language_banner_collapsible) {
                        val bannerview = findViewById<BannerAdView>(R.id.banneradsview)
                        bannerview.visibility = View.VISIBLE
                        bannerview.loadCollapsibleBanner(
                            this@SelectLanguageActivity,
                            BuildConfig.admob_banner,
                            true
                        )
                    }
                    else{
                        val adscontainer = findViewById<RelativeLayout>(R.id.ad_container)
                        adscontainer.visibility = View.GONE
                    }

                }
                else {
                    Log.d("test_remote", "remote config is null")
                    val adscontainer = findViewById<RelativeLayout>(R.id.ad_container)
                    adscontainer.visibility = View.GONE
                }
            }

        }
      /*  if(RemoteConfig.islangugaenativeopen) {
            nativefull = findViewById<NativeBannerFull>(R.id.nativefull)
            nativefull.loadNativeBannerAd(this, BuildConfig.admob_native)
        }
        else{
            nativefull = findViewById<NativeBannerFull>(R.id.nativefull)
            nativefull.visibility= View.GONE
        }*/
        LanguageList= arrayListOf()

        LanguageList.add(LanguageModel("GB", getString(R.string.english),"en",false))
        LanguageList.add(LanguageModel("ZA", getString(R.string.afrikaans_afrikaans),"af",false))
        LanguageList.add(LanguageModel("SA", getString(R.string.arabic),"ar",false))
        LanguageList.add(LanguageModel("CN", getString(R.string.chinese),"zh",false))
        LanguageList.add(LanguageModel("CZ", getString(R.string.czech_e_tina),"cs",false))
        LanguageList.add(LanguageModel("DK", getString(R.string.danish_dansk),"en",false))
        LanguageList.add(LanguageModel("NL", getString(R.string.dutch_nederlands),"nl",false))
        LanguageList.add(LanguageModel("FR", getString(R.string.french_fran_ais),"fr",false))
        LanguageList.add(LanguageModel("DE", getString(R.string.german_deutsch),"de",false))
        LanguageList.add(LanguageModel("GR", getString(R.string.greek),"el",false))
        LanguageList.add(LanguageModel("IN", getString(R.string.hindi),"hi",false))
        LanguageList.add(LanguageModel("ID", getString(R.string.indonesian_indonesia),"in",false))
        LanguageList.add(LanguageModel("IT", getString(R.string.italian_italiano),"it",false))
        LanguageList.add(LanguageModel("JP", getString(R.string.japanese),"ja",false))
        LanguageList.add(LanguageModel("KR", getString(R.string.korean),"ko",false))
        LanguageList.add(LanguageModel("MY", getString(R.string.malay_melayu),"ms",false))
        LanguageList.add(LanguageModel("NO", getString(R.string.norwegian_norsk),"no",false))
        LanguageList.add(LanguageModel("IR", getString(R.string.persian),"fa",false))
        LanguageList.add(LanguageModel("PT", getString(R.string.portugues_portugu_s),"pt",false))
        LanguageList.add(LanguageModel("RU", getString(R.string.russian),"ru",false))
        LanguageList.add(LanguageModel("ES", getString(R.string.spanish_espa_ol),"es",false))
        LanguageList.add(LanguageModel("TH", getString(R.string.thai),"th",false))
        LanguageList.add(LanguageModel("TR", getString(R.string.turkish_t_rk_e),"tr",false))
        LanguageList.add(LanguageModel("PK", getString(R.string.urdu),"ur",false))
        LanguageList.add(LanguageModel("VN", getString(R.string.vietnamese_ti_ng_vi_t),"vi",false))



        var languageAdapter= LanguageAdapter(this, languageList = LanguageList){
//            nativefull.loadNativeBannerAd(this,BuildConfig.admob_native){
            adsConfig?.let {
                if(it.language_native){
                    nativeBannerFull.displayNextAd(this@SelectLanguageActivity)
                }
            }
            selectedLanguageModel=it
        }
        binding.recyclerView.adapter= languageAdapter

        binding.savecard.setOnClickListener {
            setLocale(this,selectedLanguageModel.lngCode)
        }
    }
    fun setLocale(activity: Activity, langcode: String?) {
        if (langcode != "") {
            downloadSelectedLanguage(langcode)
            val appLocale = LocaleListCompat.forLanguageTags(langcode)
            AppCompatDelegate.setApplicationLocales(appLocale)
            prefs.putString(SharedPreferencesHelper.Companion.KEY_LANGUAGE,langcode!!)
            prefs.putBoolean(SharedPreferencesHelper.Companion.KEY_FIRSTTIME,false)
            val intent = Intent(activity, MyMainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }

//        val locale = Locale(langcode)
//        Locale.setDefault(locale)
//        val resources = activity.resources
//        val config = resources.configuration
//        config.setLocale(locale)
//        resources.updateConfiguration(config, resources.displayMetrics)
    }


    fun downloadSelectedLanguage(lan: String?) {
//        SplitCompat.install(this); for all langugae download
        val splitInstallManager = SplitInstallManagerFactory.create(this)
        val locales: MutableList<Locale> = ArrayList()
        locales.add(Locale.forLanguageTag(lan))
        splitInstallManager.deferredLanguageInstall(locales)
    }

}