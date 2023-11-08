package com.example.testagoravideo.activities

import android.os.Bundle
import android.view.SurfaceView
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.testagoravideo.R
import com.example.testagoravideo.utils.changeSelectedColor
import com.example.testagoravideo.utils.showMessage
import io.agora.rtc2.Constants
import io.agora.rtc2.IRtcEngineEventHandler
import io.agora.rtc2.RtcEngine
import io.agora.rtc2.video.VideoCanvas
import io.agora.rtc2.video.VideoEncoderConfiguration

class VideoActivity : AppCompatActivity() {

    private var mRtcEngine: RtcEngine? = null
    private var channelName: String? = null

    private val mRtcEventHandler: IRtcEngineEventHandler = object : IRtcEngineEventHandler() {
        override fun onUserOffline(uid: Int, reason: Int) {
            runOnUiThread { onRemoteUserLeft() }
        }

        override fun onUserMuteVideo(uid: Int, muted: Boolean) {
            showMessage(if (muted) "Muted" else "Unmuted")
            runOnUiThread { onRemoteUserVideoMuted(uid, muted) }
        }

        override fun onError(err: Int) {
            super.onError(err)
            showMessage("onError : $err")
        }

        override fun onJoinChannelSuccess(channel: String?, uid: Int, elapsed: Int) {
            runOnUiThread { setupRemoteVideo(uid) }
        }

        override fun onUserJoined(uid: Int, elapsed: Int) {
            showMessage("Remote user joined $uid")
            runOnUiThread { setupRemoteVideo(uid) }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video)
        channelName = intent.getStringExtra(MainActivity.channelMessage)
        findViewById<TextView>(R.id.txtChannelName).text = getString(R.string.str_channel_name, channelName)
        initAgoraEngineAndJoinChannel()
    }

    override fun onDestroy() {
        super.onDestroy()
        leaveChannel()
        RtcEngine.destroy()
        mRtcEngine = null
    }

    private fun setupRemoteVideo(uid: Int) {
        val container = findViewById<View>(R.id.remote_video_view_container) as FrameLayout
        val surfaceView: SurfaceView = RtcEngine.CreateRendererView(baseContext)
        container.addView(surfaceView)
        mRtcEngine?.setupRemoteVideo(VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_FIT, uid))
    }

    private fun onRemoteUserLeft() {
        val container = findViewById<View>(R.id.remote_video_view_container) as FrameLayout
        container.removeAllViews()
    }

    private fun onRemoteUserVideoMuted(uid: Int, muted: Boolean) {
        val container = findViewById<View>(R.id.remote_video_view_container) as FrameLayout
        val surfaceView = container.getChildAt(0) as SurfaceView
        val tag = surfaceView.tag
        if (tag != null && tag as Int == uid) {
            surfaceView.visibility = if (muted) View.GONE else View.VISIBLE
        }
    }

    private fun initAgoraEngineAndJoinChannel() {
        initAgoraEngine()
        mRtcEngine?.setChannelProfile(Constants.CHANNEL_PROFILE_LIVE_BROADCASTING)
        mRtcEngine?.setClientRole(Constants.CLIENT_ROLE_BROADCASTER)
        setupVideoProfile()
        setupLocalVideo()
        joinChannel()
    }

    private fun initAgoraEngine() {
        try {
            mRtcEngine =
                RtcEngine.create(baseContext, getString(R.string.private_app_id), mRtcEventHandler)
        } catch (e: Exception) {
            showMessage("init AgoraEngine error")
            e.printStackTrace()
        }
    }

    private fun setupVideoProfile() {
        mRtcEngine?.enableVideo()
        mRtcEngine?.setVideoEncoderConfiguration(
            VideoEncoderConfiguration(
                VideoEncoderConfiguration.VD_640x480,
                VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_15,
                VideoEncoderConfiguration.STANDARD_BITRATE,
                VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_FIXED_PORTRAIT
            )
        )
    }

    private fun setupLocalVideo() {
        val container = findViewById<View>(R.id.local_video_view_container) as FrameLayout
        val surfaceView: SurfaceView = RtcEngine.CreateRendererView(baseContext)
        surfaceView.setZOrderMediaOverlay(true)
        container.addView(surfaceView)
        mRtcEngine?.setupLocalVideo(VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_FIT, 0))
    }

    private fun joinChannel() {
        mRtcEngine?.joinChannel(null, channelName, null, 0)
    }

    private fun leaveChannel() {
        mRtcEngine?.leaveChannel()
    }

    fun onSwitchCameraClicked(view: View?) {
        mRtcEngine?.switchCamera()
    }

    fun onEndCallClicked(view: View?) {
        finish()
    }

    fun onLocalVideoMuteClicked(view: View) {
        view.changeSelectedColor()
        mRtcEngine?.muteLocalVideoStream(view.isSelected)
        val container = findViewById<View>(R.id.local_video_view_container) as FrameLayout
        val surfaceView = container.getChildAt(0) as SurfaceView
        surfaceView.setZOrderMediaOverlay(!view.isSelected)
        surfaceView.visibility = if (view.isSelected) View.GONE else View.VISIBLE
    }

    fun onLocalAudioMuteClicked(view: View) {
        view.changeSelectedColor()
        mRtcEngine?.muteLocalAudioStream(view.isSelected)
    }

}
