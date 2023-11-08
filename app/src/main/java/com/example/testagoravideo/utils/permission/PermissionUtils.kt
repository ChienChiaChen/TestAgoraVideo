package com.example.testagoravideo.utils.permission

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.core.content.ContextCompat
import com.example.testagoravideo.R

fun Activity.openPermissionSettingsScreen() {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    intent.data = Uri.fromParts(getString(R.string.package_key_word), packageName, null)
    startActivity(intent)
}

fun Activity.isPermissionGranted(permission: AppPermission) =
    (ContextCompat.checkSelfPermission(
        this,
        permission.permissionName
    ) == PackageManager.PERMISSION_GRANTED)

fun Activity.isAllPermissionGranted(): Boolean {
    return AppPermission.permissions.filter {
        !isPermissionGranted(it)
    }.toList().isEmpty()
}
