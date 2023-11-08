package com.example.testagoravideo.activities

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.testagoravideo.R
import com.example.testagoravideo.utils.permission.AppPermission
import com.example.testagoravideo.utils.permission.isPermissionGranted
import com.example.testagoravideo.utils.permission.openPermissionSettingsScreen
import com.example.testagoravideo.utils.showMessage
import com.example.testagoravideo.utils.snackbarWithAction


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        AppPermission.permissions.forEach {
            if (!isPermissionGranted(it)) {
                ActivityCompat.requestPermissions(
                    this@MainActivity,
                    AppPermission.permissions.map { it.permissionName }.toTypedArray(),
                    AppPermission.permissionsRequestCode
                )
                return@forEach
            }
        }
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
        val channel = findViewById<View>(R.id.edtChannelName) as EditText
        val channelName = channel.text.toString()
        if (channelName.isBlank()) {
            showMessage(getString(R.string.str_error_blank_name))
            return
        }
        val intent = Intent(this, VideoActivity::class.java)
        intent.putExtra(channelMessage, channelName)
        startActivity(intent)
    }


    companion object {
        const val channelMessage = "com.agora.samtan.agorabroadcast.CHANNEL"
    }
}