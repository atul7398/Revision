package com.app.rivisio.ui.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
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
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeActivity : BaseActivity() {

    private val homeViewModel: HomeViewModel by viewModels()

    private lateinit var binding: ActivityHomeBinding

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

    fun onDialog(view3: View) {

        var dialog: AlertDialog? = null

        val view =
            LayoutInflater.from(this).inflate(R.layout.topic_created_dialog, null)

        val dialogBuilder =
            MaterialAlertDialogBuilder(this, R.style.ThemeOverlay_App_MaterialAlertDialog).setView(
                view
            )


        view.findViewById<AppCompatButton>(R.id.topic_created_button)?.setOnClickListener {
            dialog?.dismiss()
            startActivity(HomeActivity.getStartIntentNewTask(this))
        }

        dialog = dialogBuilder.show()
    }
}