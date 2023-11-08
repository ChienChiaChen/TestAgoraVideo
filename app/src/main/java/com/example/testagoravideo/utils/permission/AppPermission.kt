package com.example.testagoravideo.utils.permission

import com.example.testagoravideo.R
import android.Manifest


sealed class AppPermission(
    val permissionName: String,
    val requestCode: Int,
    val deniedMessageId: Int,
    val explanationMessageId: Int
) {
    companion object {
        const val permissionsRequestCode = 3344
        val permissions: List<AppPermission> by lazy { listOf(CAMERA, RECORD_AUDIO) }
    }

    /**MICROPHONE PERMISSIONS**/
    object RECORD_AUDIO : AppPermission(
        Manifest.permission.RECORD_AUDIO,
        1,
        R.string.permission_record_audio_denied,
        R.string.permission_record_audio_explanation
    )

    /**CAMERA PERMISSIONS**/
    object CAMERA : AppPermission(
        Manifest.permission.CAMERA,
        2,
        R.string.permission_camera_denied,
        R.string.permission_camera_explanation
    )
}
