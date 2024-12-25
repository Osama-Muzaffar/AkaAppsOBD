package com.akapps.obd2carscannerapp.Activity

import android.graphics.drawable.AnimatedVectorDrawable
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.akapps.obd2carscannerapp.Ads.BannerAdView
import com.akapps.obd2carscannerapp.BuildConfig
import com.akapps.obd2carscannerapp.Database.DatabaseHelper
import com.akapps.obd2carscannerapp.Database.ObdEntry
import com.akapps.obd2carscannerapp.R
import com.akapps.obd2carscannerapp.Trips.PairedDeviceSharedPreference
import com.akapps.obd2carscannerapp.databinding.ActivityCodeDetailsBinding
import com.quadsquad.savestatus.videodownloader.storysaver.statuskeeper.adsConfigs.adsConfig
import java.util.Locale

class FaultCodeDetailsActivity : AppCompatActivity(), TextToSpeech.OnInitListener {
    lateinit var binding: ActivityCodeDetailsBinding
    private lateinit var dbHelper: DatabaseHelper
    var code: String = "P0001"
    private lateinit var textToSpeech: TextToSpeech
    private var isAnimating = false
    var playingicon: ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding= ActivityCodeDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.decorView.layoutDirection = View.LAYOUT_DIRECTION_LTR
        changeStatusBarColorFromResource(R.color.unchange_black)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        textToSpeech = TextToSpeech(this, this)

        dbHelper = DatabaseHelper(this)

        binding.backrelative.setOnClickListener {
            finish()
        }
        code= intent.getStringExtra("code")!!
        val obdEntries: List<ObdEntry> = dbHelper.getRowsFromObdTableWithCode(code)

        if(obdEntries.size>0){
            binding.codestxt.text = code
            binding.solutiontxt.text= obdEntries.get(0).sol
            binding.causetxt.text= obdEntries.get(0).causes
            binding.symptomstxt.text= obdEntries.get(0).symptom

            binding.solutionspeak.setOnClickListener {
                if (obdEntries.get(0).sol!!.isNotEmpty()){
                    speakOut(obdEntries.get(0).sol!!)
                    startAnimation(binding.solic)

                }

            }

            binding.causespeak.setOnClickListener {
                if (obdEntries.get(0).causes!!.isNotEmpty()){
                    speakOut(obdEntries.get(0).causes!!)
                    startAnimation(binding.causeic)

                }
            }

            binding.symptomsspeak.setOnClickListener {
                if (obdEntries.get(0).symptom!!.isNotEmpty()){
                    speakOut(obdEntries.get(0).symptom!!)
                    startAnimation(binding.symptomsic)
                }
            }
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
                    if(adsConfig!!.faultcode_details_banner) {
                        val bannerview = findViewById<BannerAdView>(R.id.banneradsview)
                        bannerview.visibility = View.VISIBLE
                        bannerview.loadBanner(
                            this@FaultCodeDetailsActivity,
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
                    if(adsConfig!!.faultcode_details_banner) {
                        val bannerview = findViewById<BannerAdView>(R.id.banneradsview)
                        bannerview.visibility = View.VISIBLE
                        bannerview.loadBanner(
                            this@FaultCodeDetailsActivity,
                            BuildConfig.admob_banner
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
                    bannerview.visibility = View.VISIBLE
                    bannerview.loadBanner(
                        this@FaultCodeDetailsActivity,
                        BuildConfig.admob_banner
                    )
                }
            }

        }


    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val langResult = textToSpeech.setLanguage(Locale.US)
            if (langResult == TextToSpeech.LANG_MISSING_DATA ||
                langResult == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TextToSpeech", "Language is not supported or missing data")
            }

            textToSpeech.setOnUtteranceProgressListener(MyUtteranceProgressListener())


        } else {
            Log.e("TextToSpeech", "Initialization failed")
        }
    }

    private fun speakOut(text: String) {
        // Set the utterance ID
        val utteranceId = "unique_utterance_id"
        val params = Bundle().apply {
            putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, utteranceId)
        }

        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, params, utteranceId)
    }

    override fun onDestroy() {
        // Shutdown TextToSpeech
        if (::textToSpeech.isInitialized) {
            textToSpeech.stop()
            textToSpeech.shutdown()
        }
        super.onDestroy()
    }
    private inner class MyUtteranceProgressListener : UtteranceProgressListener() {
        override fun onStart(utteranceId: String?) {
            runOnUiThread {
                // Handle the start of speech here
                Log.d("TextToSpeech", "Speech started")
                // You can update the UI or show a notification here
            }
        }

        override fun onDone(utteranceId: String?) {
            runOnUiThread {
                // Handle the end of speech here
                Log.d("TextToSpeech", "Speech finished")
                // You can update the UI or show a notification here
                if(playingicon != null) {
                    (playingicon!!.drawable as? AnimatedVectorDrawable)?.stop()
                }
            }
        }

        override fun onError(utteranceId: String?) {
            runOnUiThread {
                // Handle any errors during speech here
                Log.e("TextToSpeech", "Speech error")
                // You can update the UI or show a notification here
                if(playingicon != null) {
                    (playingicon!!.drawable as? AnimatedVectorDrawable)?.stop()
                }
            }
        }
    }
    private fun startAnimation(icon: ImageView) {
        if(playingicon != null) {
            (playingicon!!.drawable as? AnimatedVectorDrawable)?.stop()
        }

        (icon.drawable as? AnimatedVectorDrawable)?.start()
        playingicon= icon
        isAnimating = true
    }

    private fun stopAnimation(icon: ImageView) {
        (icon.drawable as? AnimatedVectorDrawable)?.stop()
        isAnimating = false
    }

}