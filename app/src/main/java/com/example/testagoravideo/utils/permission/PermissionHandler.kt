package com.example.testagoravideo.utils.permission

import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat


inline fun Activity.isPermissionGranted(permission: AppPermission) =
    (ContextCompat.checkSelfPermission(
        this,
        permission.permissionName
    ) == PackageManager.PERMISSION_GRANTED)

inline fun Activity.isRationaleNeeded(permission: AppPermission) =
    ActivityCompat.shouldShowRequestPermissionRationale(this, permission.permissionName)