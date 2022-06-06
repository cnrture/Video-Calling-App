package com.canerture.videocallingapp.domain

import com.canerture.videocallingapp.common.webrtc.listener.SignalingListener
import org.webrtc.IceCandidate
import org.webrtc.PeerConnection
import org.webrtc.SessionDescription

interface CallRepository {

    fun initClasses(
        meetingID: String,
        peerConnectionObserver: PeerConnection.Observer,
        signalingListener: SignalingListener
    )

    fun addAndSendIcaCandidate(iceCandidate: IceCandidate, isJoin: Boolean)

    fun switchCamera()

    fun setAudioState(isMute: Boolean)

    fun setVideoState(isVideoPaused: Boolean)

    fun startCall(meetingID: String)

    fun endCall(meetingID: String)

    fun destroy(meetingID: String)

    fun offerReceived(meetingID: String, sessionDescription: SessionDescription)

    fun answerReceived(sessionDescription: SessionDescription)

    fun iceCandidateReceived(iceCandidate: IceCandidate)

    fun clearSdp(meetingID: String)
}