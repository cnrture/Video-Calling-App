package com.canerture.videocallingapp.common.webrtc.util

import android.content.Context
import org.webrtc.*

class PeerConnectionUtil(
    context: Context,
    eglBaseContext: EglBase.Context
) {

    init {
        PeerConnectionFactory.InitializationOptions
            .builder(context)
            .setEnableInternalTracer(true)
            .setFieldTrials("WebRTC-H264HighProfile/Enabled/")
            .createInitializationOptions().also { initializationOptions ->
                PeerConnectionFactory.initialize(initializationOptions)
            }
    }

    private val defaultVideoEncoderFactory = DefaultVideoEncoderFactory(eglBaseContext, true, true)

    private val defaultVideoDecoderFactory = DefaultVideoDecoderFactory(eglBaseContext)

    val peerConnectionFactory: PeerConnectionFactory = PeerConnectionFactory.builder()
        .setVideoDecoderFactory(defaultVideoDecoderFactory)
        .setVideoEncoderFactory(defaultVideoEncoderFactory)
        .setOptions(PeerConnectionFactory.Options().apply {
            disableEncryption = false
            disableNetworkMonitor = true
        }).createPeerConnectionFactory()

    val iceServer =
        listOf(PeerConnection.IceServer.builder("stun:stun.l.google.com:19302").createIceServer())

}