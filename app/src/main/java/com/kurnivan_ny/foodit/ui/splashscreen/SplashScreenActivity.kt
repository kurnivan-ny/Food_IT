package com.kurnivan_ny.foodit.ui.splashscreen

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.kurnivan_ny.foodit.ui.onboarding.OnBoardingActivity
import com.kurnivan_ny.foodit.R

class SplashScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        val handler = Handler()
        handler.postDelayed({
            val intent = Intent(this@SplashScreenActivity,
                OnBoardingActivity::class.java)
            startActivity(intent)
            finish()
        }, 5000)
    }
}