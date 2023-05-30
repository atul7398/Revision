package com.app.rivisio.ui.notification

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.app.rivisio.databinding.ActivityNotificationBinding

class NotificationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNotificationBinding

    companion object {
        fun getStartIntent(context: Context) = Intent(context, NotificationActivity::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backButton.setOnClickListener { finish() }
    }
}