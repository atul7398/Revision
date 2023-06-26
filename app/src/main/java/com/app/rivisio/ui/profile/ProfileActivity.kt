package com.app.rivisio.ui.profile

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.app.rivisio.data.network.EMAIL
import com.app.rivisio.data.network.FIRST_NAME
import com.app.rivisio.data.network.LAST_NAME
import com.app.rivisio.data.network.MOBILE
import com.app.rivisio.data.network.PROFILE_IMAGE_URL
import com.app.rivisio.databinding.ActivityProfileBinding
import com.app.rivisio.ui.base.BaseActivity
import com.app.rivisio.ui.base.BaseViewModel
import com.app.rivisio.utils.NetworkResult
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class ProfileActivity : BaseActivity() {

    private val profileViewModel: ProfileViewModel by viewModels()

    private lateinit var binding: ActivityProfileBinding

    override fun getViewModel(): BaseViewModel = profileViewModel

    companion object {
        fun getStartIntent(context: Context) = Intent(context, ProfileActivity::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backButton.setOnClickListener {
            finish()
        }

        profileViewModel.userData.observe(this, Observer {
            when (it) {
                is NetworkResult.Success -> {
                    hideLoading()
                    try {

                        binding.nameField.setText(it.data.asJsonObject[FIRST_NAME].asString + " " + it.data.asJsonObject[LAST_NAME].asString)
                        binding.emailField.setText(it.data.asJsonObject[EMAIL].asString)
                        binding.phoneField.setText(it.data.asJsonObject[MOBILE].asString)

                        Glide.with(this@ProfileActivity)
                            .asBitmap()
                            .load(it.data.asJsonObject[PROFILE_IMAGE_URL].asString)
                            .into(binding.profileImage)

                    } catch (e: Exception) {
                        Timber.e("Json parsing issue: ")
                        Timber.e(e)
                        showError("Something went wrong")
                    }
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

        profileViewModel.getUserDetails()
    }
}