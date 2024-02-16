package com.app.rivisio.ui.notification

import android.Manifest
import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.app.rivisio.databinding.ActivityNotificationBinding
import com.app.rivisio.reminder.RemindersManager
import com.app.rivisio.ui.base.BaseActivity
import com.app.rivisio.ui.base.BaseViewModel
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class NotificationActivity : BaseActivity() {

    val PERMISSION_REQUEST_CODE = 800

    private lateinit var binding: ActivityNotificationBinding

    private val notificationViewModel: NotificationViewModel by viewModels()

    private var isNotificationEnabled: Boolean = false

    @Inject
    lateinit var remindersManager: RemindersManager

    companion object {
        fun getStartIntent(context: Context) = Intent(context, NotificationActivity::class.java)
    }

    override fun getViewModel(): BaseViewModel = notificationViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backButton.setOnClickListener { finish() }

        notificationViewModel.notificationTime.observe(this, Observer {
            binding.reminderTime.text = "$it (24hr)"
        })

        notificationViewModel.notificationEnabled.observe(this, Observer {

            isNotificationEnabled = it
            binding.notificationSwitch.isChecked = it

            binding.notificationSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
                isNotificationEnabled = isChecked
                if (isChecked) {
                    getPermissions()
                } else {
                    remindersManager.stopReminder(applicationContext)
                }
                notificationViewModel.enableNotification(isChecked)
            }
        })

        notificationViewModel.notificationTimeUpdated.observe(this, Observer {
            remindersManager.stopReminder(applicationContext)
            remindersManager.startReminder(applicationContext)
        })

        binding.reminderTime.setOnClickListener {

            if (!isNotificationEnabled) {
                showMessage("Enable notification first")
                return@setOnClickListener
            }

            val picker =
                MaterialTimePicker.Builder()
                    .setTimeFormat(TimeFormat.CLOCK_12H)
                    .setHour(12)
                    .setMinute(0)
                    .setTitleText("Select time")
                    .build()
            picker.show(supportFragmentManager, "time_picker")

            picker.addOnPositiveButtonClickListener {
                Timber.e("Selected time: ${picker.hour}:${picker.minute}")
                val formattedMinute = String.format("%02d", picker.minute) // Format minutes with leading zero
                val formattedTime = "${picker.hour}:$formattedMinute"
                notificationViewModel.saveTime(picker.hour, picker.minute)
                binding.reminderTime.text = formattedTime // Set formatted time to the TextView
            }
        }

        notificationViewModel.isNotificationEnabled()
        notificationViewModel.getTime()
    }

    private fun getPermissions() {
        if (Build.VERSION.SDK_INT >= 31) {

            var permissionsArray = emptyArray<String>()

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                permissionsArray = arrayOf(
                    Manifest.permission.POST_NOTIFICATIONS
                )
            }

            if (!hasPermissions(permissionsArray))
                requestPermissionsSafely(permissionsArray, PERMISSION_REQUEST_CODE)
            else
                startReminder()

        } else {
            startReminder()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty()) {
                    // Permission is granted. Continue the action or workflow
                    // in your app.
                    var allPermissionsGranted = true
                    grantResults.forEach {
                        if (it == PackageManager.PERMISSION_DENIED) {
                            allPermissionsGranted = false
                        }
                    }
                    if (allPermissionsGranted) {
                        showMessage("Permissions granted")
                        startReminder()
                    } else
                        showError("Permissions not granted, some features will not work")

                } else {
                    showError("Permissions not granted, some features will not work")
                }
                return
            }

            else -> {
                // Ignore all other requests.
            }
        }
    }

    private fun startReminder() {
        val manager = ContextCompat.getSystemService(this, AlarmManager::class.java)

        if (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                manager?.canScheduleExactAlarms() == false
            } else {
                false
            }) {
            Intent().also {
                it.action = Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM
                startActivity(it)
            }
        } else {
            remindersManager.startReminder(applicationContext)
        }
    }
}