package com.app.rivisio.ui.subscribe

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import com.app.rivisio.R
import com.app.rivisio.databinding.ActivitySubscribeBinding
import com.app.rivisio.ui.base.BaseActivity
import com.app.rivisio.ui.base.BaseViewModel
import com.app.rivisio.ui.profile.ProfileActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SubscribeActivity : BaseActivity() {

    private val subscribeViewModel: SubscribeViewModel by viewModels()

    private lateinit var binding: ActivitySubscribeBinding

    override fun getViewModel(): BaseViewModel = subscribeViewModel

    companion object {
        fun getStartIntent(context: Context) = Intent(context, SubscribeActivity::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySubscribeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.closeButton.setOnClickListener {
            finish()
        }

        val listener: (view: View) -> Unit = { view ->
            binding.quarter.setBackgroundResource(R.drawable.edit_text_bg)
            binding.year.setBackgroundResource(R.drawable.edit_text_bg)
            binding.lifetime.setBackgroundResource(R.drawable.edit_text_bg)

            view.setBackgroundResource(R.drawable.selected_plan_bg)
        }

        binding.quarter.setOnClickListener { listener(it) }
        binding.year.setOnClickListener { listener(it) }
        binding.lifetime.setOnClickListener { listener(it) }

    }
}