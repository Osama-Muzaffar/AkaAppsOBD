package com.floor.planner.models
import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.MediaView
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import com.akapps.obd2carscannerapp.Ads.AdManagerCallback
import com.akapps.obd2carscannerapp.R
import com.akapps.obd2carscannerapp.databinding.LayoutNativeBannerFullPreviewBinding
import kotlin.math.log

class NativeBannerFull @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    val TAG = "NativeAds"

    private val binding: LayoutNativeBannerFullPreviewBinding =
        LayoutNativeBannerFullPreviewBinding.inflate(LayoutInflater.from(context), this)

    private lateinit var adUnitId: String

    var callback: AdManagerCallback? = null

    var  ismediavisible= false
    private var currentAdIndex = 0
    private val nativeAdsList: MutableList<NativeAd> = mutableListOf()

    fun loadNativeBannerAd(activity: Activity, adNativeBanner: String) {
        this.adUnitId = adNativeBanner
        val shimmerFrameLayout: ShimmerFrameLayout = binding.shimmerContainerNative
        val adPlaceholder: FrameLayout = binding.flAdplaceholder


        shimmerFrameLayout.visibility = VISIBLE
        adPlaceholder.visibility = GONE
        val nativeAdView = LayoutInflater.from(activity)
            .inflate(R.layout.layout_native_banner_full, null) as NativeAdView
        nativeAdView.headlineView = nativeAdView.findViewById(R.id.ad_headline)
        nativeAdView.bodyView = nativeAdView.findViewById(R.id.ad_body)
        nativeAdView.callToActionView = nativeAdView.findViewById(R.id.ad_call_to_action)
        nativeAdView.iconView = nativeAdView.findViewById(R.id.ad_app_icon)
        nativeAdView.mediaView = nativeAdView.findViewById(R.id.media_view)
        nativeAdView.advertiserView = nativeAdView.findViewById(R.id.ad_advertiser)
        val builder = AdLoader.Builder(activity, adNativeBanner).forNativeAd { nativeAd ->
            adPlaceholder.removeAllViews()
            adPlaceholder.addView(nativeAdView)
            adPlaceholder.visibility = VISIBLE
            populateNativeAdView(nativeAd, nativeAdView)
            shimmerFrameLayout.visibility = GONE


        }.withAdListener(object : AdListener() {
            override fun onAdLoaded() {
                super.onAdLoaded()
             callback?.onAdLoaded()
            }

            override fun onAdFailedToLoad(adError: LoadAdError) {
                Log.d(TAG, "onAdFailedToLoad: NativeBannerMedium, Error: ${adError.message} ")

                adPlaceholder.visibility = GONE
                shimmerFrameLayout.visibility = GONE

                callback?.onFailedToLoad(adError)

            }
        })



        builder.build().loadAd(AdRequest.Builder().build())
    }

    private fun setAdManagerCallback(callback: AdManagerCallback) {
        this.callback = callback
    }

    private fun populateNativeAdView(nativeAd: NativeAd, nativeAdView: NativeAdView) {
        nativeAdView.headlineView?.let { headlineView ->
            (headlineView as TextView).text = nativeAd.headline ?: ""
        }

        nativeAdView.bodyView?.let { bodyView ->
            (bodyView as TextView).text = nativeAd.body ?: ""
        }

        nativeAdView.callToActionView?.let { callToActionView ->
            (callToActionView as TextView).text = nativeAd.callToAction ?: ""
        }

        nativeAdView.mediaView.let { mediaView ->
            val mv = nativeAd.mediaContent
            if (mv!=null){
                (mediaView as MediaView).mediaContent= mv
                ismediavisible=true
            }
            else{
                if (mediaView!=null){
                    mediaView.visibility= INVISIBLE
                }

            }

        }
//        if (!ismediavisible) {
            nativeAdView.iconView?.let { iconView ->
                val icon = nativeAd.icon
                if (icon == null) {
                    iconView.visibility = INVISIBLE
                } else {
                    (iconView as ImageView).setImageDrawable(icon.drawable)
                    iconView.visibility = VISIBLE
                }
            }
//        }

        nativeAdView.advertiserView?.let { advertiserView ->
            val advertiser = nativeAd.advertiser
            if (advertiser == null) {
                advertiserView.visibility = INVISIBLE
            } else {
                (advertiserView as TextView).text = advertiser
                advertiserView.visibility = VISIBLE
            }
        }

        nativeAdView.setNativeAd(nativeAd)
    }

    public fun hideAd() {
        binding.root.visibility = GONE

    }

    public fun showAd() {
        binding.root.visibility = VISIBLE

    }
    fun loadMultipleNativeAds(activity: Activity, adNativeBanner: String, adCount: Int) {
        this.adUnitId = adNativeBanner
        val shimmerFrameLayout: ShimmerFrameLayout = binding.shimmerContainerNative
        shimmerFrameLayout.visibility = VISIBLE

        val adLoader = AdLoader.Builder(activity, adNativeBanner)
            .forNativeAd { nativeAd ->
                Log.d("NativeBanner", "nativeAd loaded")
                nativeAdsList.add(nativeAd)
                if (nativeAdsList.size == adCount) {
                    shimmerFrameLayout.visibility = GONE
                    displayNextAd(activity)
                }
            }
            .withAdListener(object : AdListener() {
                override fun onAdLoaded() {
                    super.onAdLoaded()
                    callback?.onAdLoaded()
                }

                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Log.d(TAG, "onAdFailedToLoad: NativeBannerMedium, Error: ${adError.message}")
                    shimmerFrameLayout.visibility = GONE
                    callback?.onFailedToLoad(adError)
                }
            })
            .build()

        adLoader.loadAds(AdRequest.Builder().build(), adCount)
    }


    public fun displayNextAd(activity: Activity) {
        Log.d("NativeBanner", "displayNextAd: nativeAdsList size = "+nativeAdsList.size)
        if (currentAdIndex >= nativeAdsList.size) {
            currentAdIndex = 0
        }
        if(nativeAdsList.size>0) {
            val nativeAd = nativeAdsList[currentAdIndex]
            val adPlaceholder: FrameLayout = binding.flAdplaceholder
            adPlaceholder.removeAllViews()

            val nativeAdView = LayoutInflater.from(activity)
                .inflate(R.layout.layout_native_banner_full, null) as NativeAdView
            nativeAdView.headlineView = nativeAdView.findViewById(R.id.ad_headline)
            nativeAdView.bodyView = nativeAdView.findViewById(R.id.ad_body)
            nativeAdView.callToActionView = nativeAdView.findViewById(R.id.ad_call_to_action)
            nativeAdView.iconView = nativeAdView.findViewById(R.id.ad_app_icon)
            nativeAdView.mediaView = nativeAdView.findViewById(R.id.media_view)
            nativeAdView.advertiserView = nativeAdView.findViewById(R.id.ad_advertiser)

            populateNativeAdView(nativeAd, nativeAdView)
            adPlaceholder.addView(nativeAdView)
            adPlaceholder.visibility = VISIBLE

            currentAdIndex++

        }
    }

}
