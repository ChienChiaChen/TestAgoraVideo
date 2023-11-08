package com.example.testagoravideo.utils.permission

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import com.example.testagoravideo.R

fun Activity.openPermissionSettingsScreen() {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    intent.data = Uri.fromParts(getString(R.string.package_key_word), packageName, null)
    startActivity(intent)
}