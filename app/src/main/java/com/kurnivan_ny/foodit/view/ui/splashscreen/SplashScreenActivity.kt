package com.kurnivan_ny.foodit.view.ui.splashscreen

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.google.firebase.auth.FirebaseAuth
import com.kurnivan_ny.foodit.view.ui.onboarding.OnBoardingActivity
import com.kurnivan_ny.foodit.R
import com.kurnivan_ny.foodit.view.ui.main.activity.HomeActivity

class SplashScreenActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        auth = FirebaseAuth.getInstance()

        val handler = Handler()
        handler.postDelayed({

            val currentUser = auth.currentUser

            if (currentUser != null){
                startActivity(
                    Intent(this@SplashScreenActivity,
                HomeActivity::class.java)
                )
            } else {
                startActivity(
                    Intent(this@SplashScreenActivity,
                OnBoardingActivity::class.java)
                )
            }

            finish()

        }, 5000)
    }
}