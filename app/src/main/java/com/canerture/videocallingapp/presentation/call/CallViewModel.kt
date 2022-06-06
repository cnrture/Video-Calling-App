package com.canerture.videocallingapp.presentation.call

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.canerture.videocallingapp.domain.CallRepository
import com.canerture.videocallingapp.common.webrtc.observer.PeerConnectionObserverImpl
import com.canerture.videocallingapp.common.webrtc.observer.SignalingListenerImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import org.webrtc.MediaStream
import org.webrtc.RtpTransceiver
import javax.inject.Inject

@HiltViewModel
class CallViewModel @Inject constructor(private val callRepository: CallRepository) :
    ViewModel() {

    private var _rtpTransceiver = MutableLiveData<RtpTransceiver>()
    val rtpTransceiver: LiveData<RtpTransceiver>
        get() = _rtpTransceiver

    private var _mediaStream = MutableLiveData<MediaStream>()
    val mediaStream: LiveData<MediaStream>
        get() = _mediaStream

    private var _connectionEstablished = MutableLiveData<Boolean>()
    val connectionEstablished: LiveData<Boolean>
        get() = _connectionEstablished

    private var _answerReceived = MutableLiveData<Boolean>()
    val answerReceived: LiveData<Boolean>
        get() = _answerReceived

    private var _callEnded = MutableLiveData<Boolean>()
    val callEnded: LiveData<Boolean>
        get() = _callEnded

    fun initClasses(
        meetingID: String,
        isJoin: Boolean
    ) = callRepository.initClasses(
        meetingID,
        PeerConnectionObserverImpl(
            onIceCandidateCallback = {
                callRepository.addAndSendIcaCandidate(it, isJoin)
            },
            onTrackCallback = {
                _rtpTransceiver.postValue(it)
            },
            onAddStreamCallback = {
                _mediaStream.postValue(it)
            },
        ),
        SignalingListenerImpl(
            onConnectionEstablishedCallback = {
                _connectionEstablished.postValue(true)
            },
            onOfferReceivedCallback = {
                callRepository.offerReceived(meetingID, it)
            },
            onAnswerReceivedCallback = {
                callRepository.answerReceived(it)
            },
            onIceCandidateReceivedCallback = {
                callRepository.iceCandidateReceived(it)
            },
            onCallEndedCallback = {
                _callEnded.postValue(true)
            }
        ))

    fun setAudioState(isMute: Boolean) = callRepository.setAudioState(isMute)

    fun setVideoState(isVideoPaused: Boolean) = callRepository.setVideoState(isVideoPaused)

    fun switchCamera() = callRepository.switchCamera()

    fun startCall(meetingID: String) = callRepository.startCall(meetingID)

    fun endCall(meetingID: String) = callRepository.endCall(meetingID)

    fun clearSdp(meetingID: String) = callRepository.clearSdp(meetingID)

    fun destroy(meetingID: String) = callRepository.destroy(meetingID)
}