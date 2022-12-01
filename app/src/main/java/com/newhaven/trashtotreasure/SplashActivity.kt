package com.newhaven.trashtotreasure

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.newhaven.trashtotreasure.home.TrashToTreasure
import com.newhaven.trashtotreasure.login.LoginActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        CoroutineScope(Dispatchers.Main).launch {
            delay(3000)
            val intent: Intent?
            val sh = getSharedPreferences("MySharedPref", MODE_PRIVATE)
            val isAdmin = sh.getBoolean("isAdmin",false)
            intent = if(sh.getBoolean("isLogin",false) && !isAdmin )
                Intent(this@SplashActivity, TrashToTreasure::class.java)
            else if (sh.getBoolean("isLogin",false) && isAdmin)
                Intent(this@SplashActivity, AdminActivity::class.java)
            else
                Intent(this@SplashActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}