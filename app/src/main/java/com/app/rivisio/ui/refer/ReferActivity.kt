package com.app.rivisio.ui.refer

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import com.app.rivisio.BuildConfig
import com.app.rivisio.R
import es.dmoral.toasty.Toasty


class ReferActivity : AppCompatActivity() {

    companion object {
        fun getStartIntent(context: Context) = Intent(context, ReferActivity::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_refer)

        findViewById<AppCompatImageView>(R.id.back_button).setOnClickListener { finish() }

        findViewById<AppCompatImageView>(R.id.copy_referral_code).setOnClickListener {
            val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Referral code", "XXXXXXXXXX")
            clipboard.setPrimaryClip(clip)

            Toasty.custom(
                this,
                "Referral code coped to clipboard",
                R.drawable.ic_info,
                es.dmoral.toasty.R.color.infoColor,
                Toast.LENGTH_SHORT,
                true,
                true
            ).show()
        }

        findViewById<AppCompatImageView>(R.id.refer1).setOnClickListener { shareApp() }
        findViewById<AppCompatImageView>(R.id.refer2).setOnClickListener { shareApp() }
        findViewById<AppCompatImageView>(R.id.refer3).setOnClickListener { shareApp() }
        findViewById<AppCompatImageView>(R.id.refer4).setOnClickListener { shareApp() }
        findViewById<AppCompatImageView>(R.id.refer5).setOnClickListener { shareApp() }

    }

    private fun shareApp() {
        val sendIntent = Intent()
        sendIntent.action = Intent.ACTION_SEND
        sendIntent.putExtra(
            Intent.EXTRA_TEXT,
            "Hey check out my app at: https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID
        )
        sendIntent.type = "text/plain"
        startActivity(sendIntent)
    }
}