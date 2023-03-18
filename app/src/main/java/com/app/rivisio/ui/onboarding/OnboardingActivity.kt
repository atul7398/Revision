package com.app.rivisio.ui.onboarding


import android.content.Context
import android.content.Intent
import com.app.rivisio.ui.base.BaseActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.app.rivisio.R
import com.app.rivisio.ui.base.BaseViewModel
import com.app.rivisio.ui.splash.SplashViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OnboardingActivity : BaseActivity() {

    private val onboardingViewModel: OnboardingViewModel by viewModels()

    companion object {
        fun getStartIntent(context: Context): Intent {
            return Intent(context, OnboardingActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)
    }

    override fun getViewModel(): BaseViewModel {
        return onboardingViewModel
    }
}