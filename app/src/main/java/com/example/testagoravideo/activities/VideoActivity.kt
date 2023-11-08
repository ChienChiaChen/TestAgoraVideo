package com.example.testagoravideo.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.SurfaceView
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.testagoravideo.R
import com.example.testagoravideo.utils.agora.AgoraManager
import com.example.testagoravideo.utils.changeSelectedColor
import com.example.testagoravideo.utils.showMessage

class VideoActivity : AppCompatActivity() {

    private lateinit var agoraManager: AgoraManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video)
        val channelName = intent.getStringExtra(MainActivity.channelMessage) ?: ""
        if (channelName.isBlank()) {
            showMessage(getString(R.string.str_error_blank_name))
            finish()
            return
        }
        findViewById<TextView>(R.id.txtChannelName).text =
            getString(R.string.str_channel_name, channelName)

        agoraManager = AgoraManager(this@VideoActivity, channelName).apply {
            showMsg = { info -> showMessage(info) }

            onLocalViewPrepared = { surfaceView ->
                runOnUiThread {
                    val container =
                        findViewById<View>(R.id.local_video_view_container) as FrameLayout
                    container.addView(surfaceView)
                }
            }

            onRemoteViewPrepared = { surfaceView ->
                runOnUiThread {
                    val container =
                        findViewById<View>(R.id.remote_video_view_container) as FrameLayout
                    container.addView(surfaceView)
                }
            }

            onRemoteUserLeft = {
                runOnUiThread {
                    val container =
                        findViewById<View>(R.id.remote_video_view_container) as FrameLayout
                    container.removeAllViews()
                }
            }

            onUserMuteVideo = { uid, muted ->
                runOnUiThread {
                    val container =
                        findViewById<View>(R.id.remote_video_view_container) as FrameLayout
                    val surfaceView = container.getChildAt(0) as SurfaceView
                    val tag = surfaceView.tag
                    if (tag != null && tag as Int == uid) {
                        surfaceView.visibility = if (muted) View.GONE else View.VISIBLE
                    }
                }
            }
        }
        lifecycle.addObserver(agoraManager)
    }

    //region features
    fun onLocalVideoMuteClicked(view: View) {
        view.changeSelectedColor()
        agoraManager.onLocalVideoMuteClicked(view.isSelected)
        val container = findViewById<View>(R.id.local_video_view_container) as FrameLayout
        val surfaceView = container.getChildAt(0) as SurfaceView
        surfaceView.setZOrderMediaOverlay(!view.isSelected)
        surfaceView.visibility = if (view.isSelected) View.GONE else View.VISIBLE
    }

    fun onLocalAudioMuteClicked(view: View) {
        view.changeSelectedColor()
        agoraManager.muteLocalAudioStream(view.isSelected)
    }

    fun onSwitchCameraClicked(view: View?) {
        agoraManager.onSwitchCameraClicked()
    }

    fun onEndCallClicked(view: View?) {
        finish()
    }
    //endregion

    companion object {
        fun open(context: Context, channelName: String) {
            val intent = Intent(context, VideoActivity::class.java).apply {
                putExtra(MainActivity.channelMessage, channelName)
            }
            context.startActivity(intent)
        }
    }

}
