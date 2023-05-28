package com.app.rivisio.ui.refer

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.app.rivisio.R

class ReferActivity : AppCompatActivity() {

    companion object {
        fun getStartIntent(context: Context) = Intent(context, ReferActivity::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_refer)
    }
}