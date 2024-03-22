package com.example.iitianbuddy.screens

import android.app.ActivityOptions
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.os.Handler
import android.util.Pair
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.example.iitianbuddy.R
import com.example.iitianbuddy.databinding.ActivitySplashScreenBinding

class SplashScreen : AppCompatActivity() {

    private lateinit var binding: ActivitySplashScreenBinding
    private lateinit var topAnimation: Animation
    private lateinit var bottomAnimation: Animation
    private lateinit var middleAnimation: Animation



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initializer()
        startAnimation()

        if(isWorkingInternetPresent()){
            Handler().postDelayed({
                val pref = getSharedPreferences("login", MODE_PRIVATE)
                val flag = pref.getBoolean("flag", false)
                if (flag){
                    val intent: Intent = Intent(this@SplashScreen, MainScreen::class.java)
                    startActivity(intent)
                    finish()
                }
                else {
                    val intent: Intent = Intent(this@SplashScreen, LoginScreen::class.java)
                    val pairs: Array<Pair<View, String>?> = arrayOfNulls(2)
                    pairs[0] = Pair<View, String>(binding.logo, "logo")
                    pairs[1] = Pair<View, String>(binding.appName, "text")
                    val options = ActivityOptions.makeSceneTransitionAnimation(this@SplashScreen, *pairs)
                    startActivity(intent, options.toBundle())
                    finish()
                }
            }, 2000)
        }

    }

    private fun isWorkingInternetPresent(): Boolean {
        val connectivityManager =
            baseContext.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val info = connectivityManager.allNetworkInfo
        for (net in info) {
            if (net.state == NetworkInfo.State.CONNECTED) {
                return true
            }
        }
        return false
    }

    private fun startAnimation(){
        binding.first.startAnimation(topAnimation)
        binding.second.startAnimation(topAnimation)
        binding.third.startAnimation(topAnimation)
        binding.fourth.startAnimation(topAnimation)
        binding.fifth.startAnimation(topAnimation)
        binding.sixth.startAnimation(topAnimation)
        binding.logo.startAnimation(middleAnimation)
        binding.appName.startAnimation(bottomAnimation)
    }

    private fun initializer() {
        topAnimation = AnimationUtils.loadAnimation(this, R.anim.top_animation)
        bottomAnimation = AnimationUtils.loadAnimation(this, R.anim.bottom_animation)
        middleAnimation = AnimationUtils.loadAnimation(this, R.anim.middle_animation)
    }
}