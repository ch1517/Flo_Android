package com.example.flo_application

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AppCompatActivity


class SplashActivity : AppCompatActivity() {
    private val SPLASH_TIME_OUT:Long = 2000 // 2초 뒤 splash 화면 사라짐
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Handler().postDelayed({
            val intent = Intent(this, MainActivity::class.java)  // 메인 액티비티로 화면 넘어감
            startActivity(intent)
            finish()
        },SPLASH_TIME_OUT)
    }

}