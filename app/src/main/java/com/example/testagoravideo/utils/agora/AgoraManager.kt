package com.example.testagoravideo.utils.agora

import android.content.Context
import android.view.SurfaceView
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.example.testagoravideo.R
import io.agora.rtc2.Constants
import io.agora.rtc2.IRtcEngineEventHandler
import io.agora.rtc2.RtcEngine
import io.agora.rtc2.video.VideoCanvas
import io.agora.rtc2.video.VideoEncoderConfiguration

class AgoraManager(var context: Context?, val channelName: String) : DefaultLifecycleObserver {

    private var mRtcEngine: RtcEngine? = null

    var onLocalViewPrepared: ((SurfaceView) -> Unit)? = null
    var onRemoteViewPrepared: ((SurfaceView) -> Unit)? = null
    var onRemoteUserLeft: (() -> Unit)? = null
    var onUserMuteVideo: ((uid: Int, muted: Boolean) -> Unit)? = null
    var showMsg: ((String) -> Unit)? = null

    private val mRtcEventHandler: IRtcEngineEventHandler = object : IRtcEngineEventHandler() {
        override fun onUserOffline(uid: Int, reason: Int) {
            showMsg?.invoke(context!!.getString(R.string.str_info_remote_user_left))
            onRemoteUserLeft?.invoke()
        }

        override fun onUserMuteVideo(uid: Int, muted: Boolean) {
            onUserMuteVideo?.invoke(uid, muted)
        }

        override fun onError(err: Int) {
            super.onError(err)
            showMsg?.invoke(context!!.getString(R.string.str_error_msg,err))
        }

        override fun onJoinChannelSuccess(channel: String?, uid: Int, elapsed: Int) {
            showMsg?.invoke(context!!.getString(R.string.str_info_joined_successfully))
        }

        override fun onUserJoined(uid: Int, elapsed: Int) {
            showMsg?.invoke(context!!.getString(R.string.str_info_remote_user_joined))
            setupRemoteVideo(uid)
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
                RtcEngine.create(
                    context,
                    context?.getString(R.string.private_app_id),
                    mRtcEventHandler
                )
        } catch (e: Exception) {
            showMsg?.invoke(context!!.getString(R.string.str_error_init_agora_error))
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

    private fun setupRemoteVideo(uid: Int) {
        val surfaceView: SurfaceView = RtcEngine.CreateRendererView(context)
        onRemoteViewPrepared?.invoke(surfaceView)
        mRtcEngine?.setupRemoteVideo(VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_FIT, uid))
    }

    private fun setupLocalVideo() {
        val surfaceView: SurfaceView = RtcEngine.CreateRendererView(context)
        surfaceView.setZOrderMediaOverlay(true)
        mRtcEngine?.setupLocalVideo(VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_FIT, 0))
        onLocalViewPrepared?.invoke(surfaceView)
    }

    private fun joinChannel() {
        mRtcEngine?.joinChannel(null, channelName, null, 0)
    }

    fun onSwitchCameraClicked() {
        mRtcEngine?.switchCamera()
    }

    fun onLocalVideoMuteClicked(shouldMute: Boolean) {
        mRtcEngine?.muteLocalVideoStream(shouldMute)
    }

    fun muteLocalAudioStream(shouldMute: Boolean) {
        mRtcEngine?.muteLocalAudioStream(shouldMute)
    }

    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
        initAgoraEngineAndJoinChannel()
    }

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        mRtcEngine?.leaveChannel()
        RtcEngine.destroy()
        mRtcEngine = null
        context = null
    }


}