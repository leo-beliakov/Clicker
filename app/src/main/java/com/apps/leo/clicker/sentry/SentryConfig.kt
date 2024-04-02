package com.apps.leo.clicker.sentry

import android.content.Context
import com.apps.leo.clicker.BuildConfig
import io.sentry.Sentry
import io.sentry.android.core.SentryAndroid
import io.sentry.protocol.User


private const val SENTRY_INTERNAL_DSN =
    "https://c4d16c1fb5e9a493242e7ee37ea6e905@o4506946939453440.ingest.us.sentry.io/4506946949611520"
private const val SENTRY_RELEASE_DSN =
    "https://efa89127e2ca9a808e7dec0578df3ae6@o4506946939453440.ingest.us.sentry.io/4506947337125888"

object SentryConfig {

    fun initialize(context: Context) {
        if (BuildConfig.DEBUG) return

        SentryAndroid.init(context) { options ->
            options.isAnrEnabled = true
            options.dsn = if (BuildConfig.BUILD_TYPE.equals("release", ignoreCase = true)) {
                SENTRY_RELEASE_DSN
            } else {
                SENTRY_INTERNAL_DSN
            }
        }

        Sentry.setUser(
            User().apply {
                id = "test_user_id"
            }
        )
    }
}