package com.canerture.videocallingapp.common.webrtc.observer

import org.webrtc.*

class PeerConnectionObserverImpl(
    private val onIceCandidateCallback: (IceCandidate) -> Unit = {},
    private val onAddStreamCallback: (MediaStream) -> Unit = {},
    private val onTrackCallback: (RtpTransceiver) -> Unit = {},
    private val onDataChannelCallback: (DataChannel) -> Unit = {},
) : PeerConnection.Observer {
    override fun onIceCandidate(iceCandidate: IceCandidate?) {
        iceCandidate?.let {
            onIceCandidateCallback(iceCandidate)
        }
    }

    override fun onDataChannel(dataChannel: DataChannel?) {
        dataChannel?.let {
            onDataChannelCallback(it)
        }
    }

    override fun onIceConnectionReceivingChange(p0: Boolean) {
    }

    override fun onIceConnectionChange(p0: PeerConnection.IceConnectionState?) {
    }

    override fun onIceGatheringChange(p0: PeerConnection.IceGatheringState?) {
    }

    override fun onAddStream(mediaStream: MediaStream?) {
        mediaStream?.let {
            onAddStreamCallback(mediaStream)
        }
    }

    override fun onSignalingChange(p0: PeerConnection.SignalingState?) {
    }

    override fun onIceCandidatesRemoved(p0: Array<out IceCandidate>?) {
    }

    override fun onRemoveStream(p0: MediaStream?) {
    }

    override fun onRenegotiationNeeded() {
    }

    override fun onAddTrack(p0: RtpReceiver?, p1: Array<out MediaStream>?) {

    }

    override fun onTrack(transceiver: RtpTransceiver?) {
        super.onTrack(transceiver)
        transceiver?.let {
            onTrackCallback(transceiver)
        }
    }
}