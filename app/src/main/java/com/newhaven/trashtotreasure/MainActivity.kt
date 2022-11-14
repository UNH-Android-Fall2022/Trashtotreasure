package com.newhaven.trashtotreasure

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.newhaven.trashtotreasure.home.HomeActivity
import com.newhaven.trashtotreasure.home.TrashToTreasure
import com.newhaven.trashtotreasure.login.LoginActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        CoroutineScope(Dispatchers.Main).launch {
            delay(3000)
            var intent: Intent? = null
            val sh = getSharedPreferences("MySharedPref", MODE_PRIVATE)
            intent = if(sh.getBoolean("isLogin",false))
                Intent(this@MainActivity, TrashToTreasure::class.java)
            else
                Intent(this@MainActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}