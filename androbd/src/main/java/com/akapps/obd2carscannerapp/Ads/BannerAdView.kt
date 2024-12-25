package com.akapps.obd2carscannerapp.Ads

import android.app.Activity
import android.content.Context
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.RelativeLayout
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.ads.mediation.admob.AdMobAdapter
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.akapps.obd2carscannerapp.R

class BannerAdView : RelativeLayout {

    private var adView: AdView? = null
    lateinit var shimmerFrameLayout: ShimmerFrameLayout
    private var parent: RelativeLayout? = null
    private var context: Activity? = null

    var callback: AdManagerCallback? = null

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context, attrs, defStyleAttr
    ) {
        init(context)
    }

    private fun init(context: Context) {
        LayoutInflater.from(context).inflate(R.layout.banner_ad_view, this, true)

        parent = findViewById(R.id.parent)
        shimmerFrameLayout = findViewById(R.id.shimmer_frame_layout)

        shimmerFrameLayout.startShimmer()

    }

    public fun loadBanner(context: Activity?, adUnitId: String?) {
        loadCollapsibleBanner(context, adUnitId, false, callback)
    }

    public fun loadBanner(context: Activity?, adUnitId: String?, adLoadCallback: AdManagerCallback?) {
        loadCollapsibleBanner(context, adUnitId, false, adLoadCallback)
    }


    public fun setAdCallback(callback: AdManagerCallback?) {
        this.callback = callback
    }

    public fun loadCollapsibleBanner(
        context: Activity?, adUnitId: String?, collapsible: Boolean
    ) {
        loadCollapsibleBanner(context, adUnitId, collapsible, callback)
    }

    public fun loadCollapsibleBanner(
        context: Activity?, adUnitId: String?, collapsible: Boolean, callback: AdManagerCallback?
    ) {
        this.context = context
        // Create a new AdView instance every time you want to load a new ad.
        adView = AdView(context!!)
        adView!!.adUnitId = adUnitId!!
        adView!!.setAdSize(adSize)

        var builder = AdRequest.Builder()

        if (collapsible) {
            val extras = Bundle()
            extras.putString("collapsible", "bottom")
            builder = builder.addNetworkExtrasBundle(AdMobAdapter::class.java, extras)
        }

        adView!!.loadAd(builder.build())

        val adLayoutParams =
            LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        adView!!.adListener = object : AdListener() {


            override fun onAdLoaded() {
                callback?.onAdLoaded()
                parent!!.removeAllViews() // Remove any existing views first
                parent!!.addView(adView)
                adView!!.layoutParams = adLayoutParams
                shimmerFrameLayout.stopShimmer()
                shimmerFrameLayout.visibility = GONE


            }

            override fun onAdClicked() {
                super.onAdClicked()
                callback?.onAdLoaded()
            }

            override fun onAdClosed() {
                super.onAdClosed()

//                callback?.onAdLoaded()
            }

            override fun onAdOpened() {
                super.onAdOpened()
//                callback?.onAdLoaded()
            }


            override fun onAdImpression() {
                super.onAdImpression()

            }

            override fun onAdFailedToLoad(adError: LoadAdError) {
                shimmerFrameLayout.stopShimmer()
                shimmerFrameLayout.visibility = GONE

            }

        }

    }

    private val adSize: AdSize
        get() {
            var bounds = Rect()
            var adWidthPixels = parent!!.width.toFloat()

            // If the ad hasn't been laid out, default to the full screen width.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val windowMetrics = context!!.windowManager.currentWindowMetrics
                bounds = windowMetrics.bounds
            } else {
                // Handle the case for older Android versions
                val windowManager =
                    context!!.getSystemService(Context.WINDOW_SERVICE) as WindowManager
                val display = windowManager.defaultDisplay
                display.getRectSize(bounds)
            }
            if (adWidthPixels == 0f) {
                adWidthPixels = bounds.width().toFloat()
            }

            val density = resources.displayMetrics.density
            val adWidth = (adWidthPixels / density).toInt()

            return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(context!!, adWidth)
        }

    fun hideAd() {
        adView!!.visibility = GONE
        shimmerFrameLayout.visibility = GONE
    }

    fun showAd() {
        adView!!.visibility = VISIBLE
        shimmerFrameLayout.visibility = VISIBLE
    }
}
