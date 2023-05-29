package com.app.rivisio.ui.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.fragment.app.FragmentTransaction
import com.app.rivisio.R
import com.app.rivisio.databinding.ActivityHomeBinding
import com.app.rivisio.ui.add_topic.AddTopicActivity
import com.app.rivisio.ui.base.BaseActivity
import com.app.rivisio.ui.base.BaseViewModel
import com.app.rivisio.ui.home.fragments.account.AccountFragment
import com.app.rivisio.ui.home.fragments.calendar.CalendarFragment
import com.app.rivisio.ui.home.fragments.home_fragment.HomeFragment
import com.app.rivisio.ui.home.fragments.topics.TopicsFragment
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class HomeActivity : BaseActivity() {

    private val homeViewModel: HomeViewModel by viewModels()

    private lateinit var binding: ActivityHomeBinding

    private var pressedTime: Long = 0
    companion object {
        fun getStartIntent(context: Context) = Intent(context, HomeActivity::class.java)

        fun getStartIntentNewTask(context: Context): Intent {
            val intent = Intent(context, HomeActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            return intent
        }
    }

    override fun getViewModel(): BaseViewModel = homeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.floatingButton.setOnClickListener {
            startActivity(AddTopicActivity.getStartIntent(this@HomeActivity))
        }

        setUpFragments()
    }

    private fun setUpFragments() {
        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home_item -> {
                    val fragment = HomeFragment.newInstance()
                    val fragmentTransaction: FragmentTransaction =
                        supportFragmentManager.beginTransaction()
                    fragmentTransaction.replace(R.id.home_container, fragment, "")
                    fragmentTransaction.commit()
                }
                R.id.topics_item -> {
                    val fragment = TopicsFragment.newInstance()
                    val fragmentTransaction: FragmentTransaction =
                        supportFragmentManager.beginTransaction()
                    fragmentTransaction.replace(R.id.home_container, fragment, "")
                    fragmentTransaction.commit()
                }
                R.id.calendar_item -> {
                    val fragment = CalendarFragment.newInstance()
                    val fragmentTransaction: FragmentTransaction =
                        supportFragmentManager.beginTransaction()
                    fragmentTransaction.replace(R.id.home_container, fragment, "")
                    fragmentTransaction.commit()
                }

                R.id.account_item -> {
                    val fragment = AccountFragment.newInstance()
                    val fragmentTransaction: FragmentTransaction =
                        supportFragmentManager.beginTransaction()
                    fragmentTransaction.replace(R.id.home_container, fragment, "")
                    fragmentTransaction.commit()
                }
            }
            true
        }

        binding.bottomNav.selectedItemId = R.id.home_item

    }

    override fun onBackPressed() {
        if (pressedTime + 2000 > System.currentTimeMillis()) {
            super.onBackPressed()
            finish()
        } else {
            showMessage("Press back again to exit")
        }
        pressedTime = System.currentTimeMillis()
    }
}