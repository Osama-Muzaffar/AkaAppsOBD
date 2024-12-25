package com.akapps.obd2carscannerapp.Activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.akapps.obd2carscannerapp.R
import com.akapps.ecu.EcuDataPv
import com.akapps.obd2carscannerapp.Ads.BannerAdView
import com.akapps.obd2carscannerapp.BuildConfig
import com.akapps.obd2carscannerapp.Trips.PairedDeviceSharedPreference
import com.akapps.obd2carscannerapp.databinding.ActivityMyObdDataBinding
import com.quadsquad.savestatus.videodownloader.storysaver.statuskeeper.adsConfigs.adsConfig


class ObdDataActivity : AppCompatActivity(){
    lateinit var binding: ActivityMyObdDataBinding

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        binding= ActivityMyObdDataBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.decorView.layoutDirection = View.LAYOUT_DIRECTION_LTR
        changeStatusBarColorFromResource(R.color.unchange_black)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
//        CommService.elm.setService(
//            OBD_SVC_DATA,
//            (MainActivity.Constants.mode != MainActivity.MODE.FILE && MainActivity.Constants.mode != MainActivity.MODE.OFFLINE)
//        )

        binding.backrelative.setOnClickListener {
            finish()
        }
        Handler().postDelayed(Runnable {


        if (MyMainActivity.Constants.currDataAdapter.getPvs().size>0) {
            binding.datalist.adapter = MyMainActivity.Constants.currDataAdapter

            binding.datalist.setOnItemClickListener { parent, view, position, id ->

                // get data PV
                val currPv =
                    MyMainActivity.Constants.currDataAdapter.getItem(position) as EcuDataPv

                Toast.makeText(
                    this,
                    "Clicked by position " + currPv.get(EcuDataPv.FID_DESCRIPT),
                    Toast.LENGTH_SHORT
                ).show()
            }
            binding.nodatatxt.visibility= View.GONE

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
                        if(adsConfig!!.obddata_banner_simple && adsConfig!!.obddata_banner_collapse) {
                            val bannerview = findViewById<BannerAdView>(R.id.banneradsview)
                            bannerview.visibility = View.VISIBLE
                            bannerview.loadCollapsibleBanner(
                                this@ObdDataActivity,
                                BuildConfig.admob_banner,
                                true
                            )
                        }
                        else if(adsConfig!!.obddata_banner_simple) {
                            val bannerview = findViewById<BannerAdView>(R.id.banneradsview)
                            bannerview.visibility = View.VISIBLE
                            bannerview.loadBanner(
                                this@ObdDataActivity,
                                BuildConfig.admob_banner
                            )
                        }
                        else  if(adsConfig!!.obddata_banner_collapse) {
                            val bannerview = findViewById<BannerAdView>(R.id.banneradsview)
                            bannerview.visibility = View.VISIBLE
                            bannerview.loadCollapsibleBanner(
                                this@ObdDataActivity,
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
                        if(adsConfig!!.obddata_banner_simple && adsConfig!!.obddata_banner_collapse) {
                            val bannerview = findViewById<BannerAdView>(R.id.banneradsview)
                            bannerview.visibility = View.VISIBLE
                            bannerview.loadCollapsibleBanner(
                                this@ObdDataActivity,
                                BuildConfig.admob_banner,
                                true
                            )
                        }
                        else if(adsConfig!!.obddata_banner_simple) {
                            val bannerview = findViewById<BannerAdView>(R.id.banneradsview)
                            bannerview.visibility = View.VISIBLE
                            bannerview.loadBanner(
                                this@ObdDataActivity,
                                BuildConfig.admob_banner
                            )
                        }
                        else  if(adsConfig!!.obddata_banner_collapse) {
                            val bannerview = findViewById<BannerAdView>(R.id.banneradsview)
                            bannerview.visibility = View.VISIBLE
                            bannerview.loadCollapsibleBanner(
                                this@ObdDataActivity,
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
           /* if(!AppPurchase.getInstance().isPurchased){
                val banneradsview= findViewById<BannerAdView>(R.id.banneradsview)
                banneradsview.loadBanner(this, BuildConfig.admob_banner)
            }
            else{
                val banneradsview= findViewById<BannerAdView>(R.id.banneradsview)
                banneradsview.visibility= View.GONE
            }*/

        }
        else{
            binding.nodatatxt.visibility= View.VISIBLE
        }
            binding.scanninglayout.visibility= View.GONE
            binding.mainlayout.visibility= View.VISIBLE
            val banneradsview= findViewById<BannerAdView>(R.id.banneradsview)
            banneradsview.visibility= View.GONE
        },3000)

    }

}