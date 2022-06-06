package com.canerture.videocallingapp.common.webrtc.observer

import org.webrtc.SdpObserver
import org.webrtc.SessionDescription

class SdpObserverImpl(
    private val onCreateSuccessCallback: (SessionDescription) -> Unit = {},
    private val onSetSuccessCallback: () -> Unit = {},
    private val onCreateFailureCallback: (String) -> Unit = {},
    private val onSetFailureCallback: (String) -> Unit = {}
) : SdpObserver {
    override fun onCreateSuccess(sessionDescription: SessionDescription?) {
        sessionDescription ?: return
        onCreateSuccessCallback(sessionDescription)
    }

    override fun onSetSuccess() {
        onSetSuccessCallback()
    }

    override fun onCreateFailure(errorMessage: String?) {
        errorMessage ?: return
        onCreateFailureCallback(errorMessage)
    }

    override fun onSetFailure(errorMessage: String?) {
        errorMessage ?: return
        onSetFailureCallback(errorMessage)
    }
}