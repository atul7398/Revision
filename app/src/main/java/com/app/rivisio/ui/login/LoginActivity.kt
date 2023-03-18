package com.app.rivisio.ui.login

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.AppCompatButton
import com.app.rivisio.R
import com.app.rivisio.ui.home.HomeActivity

class LoginActivity : AppCompatActivity() {

    companion object {
        fun getStartIntent(context: Context) = Intent(context, LoginActivity::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        findViewById<AppCompatButton>(R.id.google_button).setOnClickListener {
            startActivity(HomeActivity.getStartIntent(this@LoginActivity))
            finish()
        }
    }
}