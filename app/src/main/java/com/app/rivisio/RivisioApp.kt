package com.app.rivisio

import android.app.Application
import android.content.res.Resources
import android.util.TypedValue
import android.view.Gravity
import dagger.hilt.android.HiltAndroidApp
import es.dmoral.toasty.Toasty
import timber.log.Timber

@HiltAndroidApp
class RivisioApp : Application() {

    override fun onCreate() {
        super.onCreate()

        Timber.plant(Timber.DebugTree())

        val dip = 100f
        val r: Resources = resources
        val px = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dip,
            r.displayMetrics
        )

        Toasty.Config.getInstance()
            .setGravity(
                Gravity.BOTTOM,
                0,
                px.toInt()
            ) // optional (set toast gravity, offsets are optional)
            .apply();
    }
}