package com.akapps.obd2carscannerapp.Activity

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.denzcoskun.imageslider.ImageSlider
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.akapps.obd2carscannerapp.R
import com.akapps.obd2carscannerapp.databinding.ActivityHelpsBinding

class HelpsActivity : AppCompatActivity() {
    lateinit var binding: ActivityHelpsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        binding= ActivityHelpsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.decorView.layoutDirection = View.LAYOUT_DIRECTION_LTR
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        changeStatusBarColorFromResource(R.color.unchange_black)

        val imageList = ArrayList<SlideModel>() // Create image list

// imageList.add(SlideModel("String Url" or R.drawable)
// imageList.add(SlideModel("String Url" or R.drawable, "title") You can add title


        imageList.add(SlideModel(R.drawable.help_1,ScaleTypes.FIT))
        imageList.add(SlideModel(R.drawable.help_2,ScaleTypes.FIT))
        imageList.add(SlideModel(R.drawable.help_3,ScaleTypes.FIT))

        val imageSlider = findViewById<ImageSlider>(R.id.sliderview)
        imageSlider.setImageList(imageList)
        imageSlider.startSliding()

    }
}