package com.example.testagoravideo.utils

import android.app.Activity
import android.view.View
import android.widget.Toast
import com.example.testagoravideo.R
import com.google.android.material.snackbar.Snackbar

fun Activity.showMessage(message: String?) {
    runOnUiThread {
        Toast.makeText(
            applicationContext,
            message,
            Toast.LENGTH_SHORT
        ).show()
    }
}

fun snackbarWithAction(
    contentView: View,
    messageId: Int,
    actionText: Int = R.string.request_permission,
    action: () -> Unit
) {
    Snackbar.make(contentView, messageId, Snackbar.LENGTH_LONG)
        .setAction(actionText) { action() }
        .show()
}