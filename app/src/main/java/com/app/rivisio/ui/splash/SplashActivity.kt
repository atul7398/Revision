package com.app.rivisio.ui.splash

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.app.rivisio.R
import com.app.rivisio.ui.base.BaseActivity
import com.app.rivisio.ui.base.BaseViewModel
import com.app.rivisio.ui.onboarding.OnboardingActivity
import com.app.rivisio.utils.NetworkResult
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber

@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class SplashActivity : BaseActivity() {

    private val splashViewModel: SplashViewModel by viewModels()

    override fun getViewModel(): BaseViewModel {
        return splashViewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        setupObserver()
    }

    private fun setupObserver() {
        splashViewModel.users.observe(this, Observer {
            when (it) {
                is NetworkResult.Success -> {
                    hideLoading()
                    showMessage("Network call is successful")
                }
                is NetworkResult.Loading -> {
                    showLoading()
                }
                is NetworkResult.Error -> {
                    hideLoading()
                    showError(it.message)
                }
                is NetworkResult.Exception -> {
                    hideLoading()
                    showError(it.e.message)
                }
                else -> {
                    hideLoading()
                    Timber.e(it.toString())
                }
            }
        })

        lifecycleScope.launch {
            delay(500)
            startActivity(OnboardingActivity.getStartIntent(this@SplashActivity))
            finish()
        }
        //splashViewModel.fetchUsers()
    }

}