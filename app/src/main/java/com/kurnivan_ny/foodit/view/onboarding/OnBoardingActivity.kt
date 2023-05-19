package com.kurnivan_ny.foodit.view.onboarding

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Html
import android.util.Log
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.kurnivan_ny.foodit.view.adapter.ImageSlideAdapter
import com.kurnivan_ny.foodit.data.model.modelui.onboarding.ImageSlideModel
import com.kurnivan_ny.foodit.R
import com.kurnivan_ny.foodit.databinding.ActivityOnBoardingBinding
import com.kurnivan_ny.foodit.view.auth.LoginActivity
import com.kurnivan_ny.foodit.view.auth.RegisterActivity
import com.kurnivan_ny.foodit.data.model.preferences.SharedPreferences

class OnBoardingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOnBoardingBinding

    private lateinit var adapter: ImageSlideAdapter
    private val list = ArrayList<ImageSlideModel>()
    private lateinit var dots: ArrayList<TextView>

    private lateinit var handler: Handler
    private lateinit var runnable: Runnable

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnBoardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        handler = Handler(Looper.getMainLooper())
        runnable = object : Runnable{
            var index = 0
            override fun run() {
                if (index == list.size)
                    index = 0
                Log.e("Runnable","$index")
                binding.vpImage.setCurrentItem(index)
                index++
                handler.postDelayed(this, 3000)
            }
        }

        list.add(
            ImageSlideModel(R.drawable.karbohidrat, "Memantau Asupan\nKadar Karbohidrat")
        )

        list.add(
            ImageSlideModel(R.drawable.lemak, "Memantau Asupan\nKadar Protein")
        )

        list.add(
            ImageSlideModel(R.drawable.protein, "Memantau Asupan\nKadar Lemak")
        )

        adapter = ImageSlideAdapter(list)
        binding.vpImage.adapter = adapter
        dots = ArrayList()
        setIndicator()

        binding.vpImage.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                selectedDot(position)
                super.onPageSelected(position)
            }
        })

        handler.post(runnable)

        sharedPreferences = SharedPreferences(this)

//        if (sharedPreferences.getValuesString("onboarding").equals("1")){
//            sharedPreferences.setValuesString("onboarding", "1")
//
//            val intent = Intent(this@OnBoardingActivity, LoginActivity::class.java)
//            startActivity(intent)
//        }

        binding.btnMasuk.setOnClickListener {
//            sharedPreferences.setValuesString("onboarding", "1")
//            finishAffinity()

            val intent = Intent(this@OnBoardingActivity, LoginActivity::class.java)
            startActivity(intent)
        }

        binding.btnDaftar.setOnClickListener {
//            sharedPreferences.setValuesString("onboarding", "1")
//            finishAffinity()

            val intent = Intent(this@OnBoardingActivity, RegisterActivity::class.java)
            startActivity(intent)
        }

    }

    private fun setIndicator() {
        for (i in 0 until list.size) {
            dots.add(TextView(this))
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                dots[i].text = Html.fromHtml("&#9679", Html.FROM_HTML_MODE_LEGACY).toString()
            } else {
                dots[i].text = Html.fromHtml("&#9679")
            }
            dots[i].textSize = 18f
            binding.dotsIndicator.addView(dots[i])
        }
    }

    private fun selectedDot(position: Int) {
        for(i in 0 until list.size){
            if (i == position){
                dots[i].setTextColor(ContextCompat.getColor(this, R.color.white))
            } else {
                dots[i].setTextColor(ContextCompat.getColor(this, R.color.hint))
            }
        }
    }

    override fun onStop(){
        super.onStop()
        handler.removeCallbacks(runnable)
    }

}