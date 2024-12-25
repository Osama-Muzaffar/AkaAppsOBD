package com.akapps.obd2carscannerapp.Activity

import android.os.Bundle
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.akapps.obd2carscannerapp.Ads.BannerAdView
import com.akapps.obd2carscannerapp.Ads.billing.AppPurchase
import com.akapps.obd2carscannerapp.BuildConfig
import com.akapps.obd2carscannerapp.R
import com.akapps.obd2carscannerapp.databinding.ActivityTermsPrivacyBinding

class TermsPrivacyActivity : AppCompatActivity() {
    lateinit var binding: ActivityTermsPrivacyBinding
    var which = "privacy"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        changeStatusBarColorFromResource(R.color.unchange_black)
        window.decorView.layoutDirection = View.LAYOUT_DIRECTION_LTR
        binding= ActivityTermsPrivacyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        if(!AppPurchase.getInstance().isPurchased){
            val banneradsview= findViewById<BannerAdView>(R.id.banneradsview)
            banneradsview.loadBanner(this, BuildConfig.admob_banner)
        }
        else{
            val banneradsview= findViewById<BannerAdView>(R.id.banneradsview)
            banneradsview.visibility= View.GONE
        }

        binding.backimg.setOnClickListener {
            finish()
        }
        which=intent.getStringExtra("which")!!
        // Enable JavaScript
        binding.webview.settings.javaScriptEnabled = true

        // Set WebViewClient to handle loading within the WebView
        binding.webview.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: android.graphics.Bitmap?) {
                super.onPageStarted(view, url, favicon)
                binding.progressbar.visibility = android.view.View.VISIBLE
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                binding.progressbar.visibility = android.view.View.GONE
            }
        }

        // Set WebChromeClient to handle progress updates
        binding.webview.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
                binding.progressbar.progress = newProgress
                if (newProgress == 100) {
                    binding.progressbar.visibility = android.view.View.GONE
                } else {
                    binding.progressbar.visibility = android.view.View.VISIBLE
                }
            }
        }

        if(which.equals("privacy")){
            binding.webview.loadUrl(getString(R.string.privacy_link))
            binding.titletxt.text= "Privacy Policy"
        }
        else{
            binding.webview.loadUrl(getString(R.string.terms_link))
            binding.titletxt.text= "Terms of Use"

        }
    }

}