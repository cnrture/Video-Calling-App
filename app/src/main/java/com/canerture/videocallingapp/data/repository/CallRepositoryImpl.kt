package com.canerture.videocallingapp.data.repository

import android.content.Context
import com.canerture.videocallingapp.domain.CallRepository
import com.canerture.videocallingapp.common.webrtc.RTCAudioManager
import com.canerture.videocallingapp.common.webrtc.SignalingClient
import com.canerture.videocallingapp.common.webrtc.WebRtcClient
import com.canerture.videocallingapp.common.webrtc.listener.SignalingListener
import com.canerture.videocallingapp.common.webrtc.util.PeerConnectionUtil
import org.webrtc.EglBase
import org.webrtc.IceCandidate
import org.webrtc.PeerConnection
import org.webrtc.SessionDescription
import javax.inject.Inject

class CallRepositoryImpl @Inject constructor(
    private val context: Context,
    private val eglBase: EglBase
) : CallRepository {

    private val webRtcClient = WebRtcClient
    private lateinit var signalingClient: SignalingClient
    private lateinit var peerConnectionUtil: PeerConnectionUtil

    private val audioManager by lazy { RTCAudioManager.create(context) }

    override fun initClasses(
        meetingID: String,
        peerConnectionObserver: PeerConnection.Observer,
        signalingListener: SignalingListener
    ) {
        peerConnectionUtil = PeerConnectionUtil(
            context,
            eglBase.eglBaseContext
        )

        initWebRtcClient(peerConnectionObserver)
        initSignalingClient(meetingID, signalingListener)
        audioManager.setDefaultAudioDevice(RTCAudioManager.AudioDevice.SPEAKER_PHONE)
    }

    override fun addAndSendIcaCandidate(iceCandidate: IceCandidate, isJoin: Boolean) {
        signalingClient.sendIceCandidateModelToUser(iceCandidate, isJoin)
        webRtcClient.addIceCandidate(iceCandidate)
    }

    override fun switchCamera() = webRtcClient.switchCamera(context)

    override fun setAudioState(isMute: Boolean) = webRtcClient.enableAudio(isMute)

    override fun setVideoState(isVideoPaused: Boolean) = webRtcClient.enableVideo(isVideoPaused)

    override fun startCall(meetingID: String) = webRtcClient.call(meetingID)

    override fun endCall(meetingID: String) = webRtcClient.endCall(meetingID)

    override fun destroy(meetingID: String) {
        webRtcClient.clearCandidates(meetingID)
        webRtcClient.closePeerConnection()
        signalingClient.destroy()
    }

    override fun offerReceived(meetingID: String, sessionDescription: SessionDescription) {
        webRtcClient.setRemoteDescription(sessionDescription)
        webRtcClient.answer(meetingID)
    }

    override fun answerReceived(sessionDescription: SessionDescription) {
        webRtcClient.setRemoteDescription(sessionDescription)
    }

    override fun iceCandidateReceived(iceCandidate: IceCandidate) {
        webRtcClient.addIceCandidate(iceCandidate)
    }

    override fun clearSdp(meetingID: String) {
        webRtcClient.clearSdp(meetingID)
    }

    private fun initWebRtcClient(peerConnectionObserver: PeerConnection.Observer) {
        webRtcClient.initWebRtcClient(
            context = context,
            eglBase = eglBase,
            peerConnectionObserver = peerConnectionObserver
        )
    }

    private fun initSignalingClient(meetingID: String, signalingListener: SignalingListener) {
        signalingClient = SignalingClient(
            meetingID = meetingID,
            signalingListener = signalingListener
        )
    }
}