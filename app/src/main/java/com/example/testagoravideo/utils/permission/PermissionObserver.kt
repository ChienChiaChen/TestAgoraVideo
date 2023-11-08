package com.example.testagoravideo.utils.permission

import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner

class PermissionObserver(var activity: FragmentActivity?) : DefaultLifecycleObserver {
    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
        activity?.let { fragmentActivity ->
            if (!fragmentActivity.isAllPermissionGranted()) {
                ActivityCompat.requestPermissions(
                    fragmentActivity,
                    AppPermission.permissions.map { it.permissionName }.toTypedArray(),
                    AppPermission.permissionsRequestCode
                )
            }
        }
    }

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        activity = null
    }
}