package com.example.testagoravideo.activities

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.testagoravideo.R
import com.example.testagoravideo.utils.permission.AppPermission
import com.example.testagoravideo.utils.permission.PermissionObserver
import com.example.testagoravideo.utils.permission.isAllPermissionGranted
import com.example.testagoravideo.utils.permission.openPermissionSettingsScreen
import com.example.testagoravideo.utils.showMessage
import com.example.testagoravideo.utils.snackbarWithAction


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        lifecycle.addObserver(PermissionObserver(this@MainActivity))
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissions.forEachIndexed { index, permission ->
            if (PackageManager.PERMISSION_GRANTED != grantResults[index]) {
                snackbarWithAction(
                    findViewById(R.id.contentView),
                    AppPermission.permissions.find { it.permissionName == permission }?.explanationMessageId
                        ?: 0,
                    R.string.action_settings
                ) { openPermissionSettingsScreen() }
                return
            }
        }
    }

    fun onSubmit(view: View?) {
        if (isAllPermissionGranted()) {
            val channel = findViewById<View>(R.id.edtChannelName) as EditText
            val channelName = channel.text.toString()
            if (channelName.isBlank()) {
                showMessage(getString(R.string.str_error_blank_name))
                return
            }
            VideoActivity.open(this@MainActivity, channelName)
        } else {
            openPermissionSettingsScreen()
        }
    }


    companion object {
        const val channelMessage = "com.agora.samtan.agorabroadcast.CHANNEL"
    }
}