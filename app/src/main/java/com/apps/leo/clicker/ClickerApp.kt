package com.apps.leo.clicker

import android.app.Application
import com.apps.leo.clicker.sentry.SentryConfig
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class ClickerApp : Application() {
    override fun onCreate() {
        SentryConfig.initialize(this)
        super.onCreate()
    }
}