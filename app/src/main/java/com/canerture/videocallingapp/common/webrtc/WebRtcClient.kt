package com.canerture.videocallingapp.common.webrtc

import android.content.Context
import android.util.Log
import com.canerture.videocallingapp.common.Constants
import com.canerture.videocallingapp.common.Constants.LOCAL_STREAM_ID
import com.canerture.videocallingapp.common.Constants.LOCAL_TRACK_ID
import com.canerture.videocallingapp.common.Constants.VIDEO_FPS
import com.canerture.videocallingapp.common.Constants.VIDEO_HEIGHT
import com.canerture.videocallingapp.common.Constants.VIDEO_WIDTH
import com.canerture.videocallingapp.data.model.Candidates
import com.canerture.videocallingapp.data.model.Sdp
import com.canerture.videocallingapp.common.webrtc.observer.SdpObserverImpl
import com.canerture.videocallingapp.common.webrtc.util.PeerConnectionUtil
import com.huawei.agconnect.cloud.database.CloudDBZone
import com.huawei.agconnect.cloud.database.CloudDBZoneQuery
import com.huawei.agconnect.cloud.database.Text
import com.huawei.agconnect.cloud.database.exceptions.AGConnectCloudDBException
import org.webrtc.*

object WebRtcClient {

    private var cloudDBZone: CloudDBZone? = null

    private var localAudioTrack: AudioTrack? = null
    private var localVideoTrack: VideoTrack? = null

    private var peerConnectionUtil: PeerConnectionUtil? = null

    private var peerConnectionFactory: PeerConnectionFactory? = null

    private var mediaConstraints = MediaConstraints().apply {
        mandatory.add(MediaConstraints.KeyValuePair("OfferToReceiveVideo", "true"))
        mandatory.add(MediaConstraints.KeyValuePair("RtpDataChannels", "true"))
        mandatory.add(MediaConstraints.KeyValuePair("DtlsSrtpkeyAgreement", "true"))
        mandatory.add(MediaConstraints.KeyValuePair("internalSctpDataChannels", "true"))
    }

    private var localAudioSource: AudioSource? = null
    private var localVideoSource: VideoSource? = null
    private var peerConnection: PeerConnection? = null

    private var videoCapturer: CameraVideoCapturer? = null

    private var isFrontCamera: Boolean = false

    private var eglBase: EglBase? = null

    fun initWebRtcClient(
        context: Context,
        eglBase: EglBase,
        peerConnectionObserver: PeerConnection.Observer
    ) {
        cloudDBZone = CloudDbWrapper.cloudDBZone
        this.eglBase = eglBase

        peerConnectionUtil = PeerConnectionUtil(
            context,
            eglBase.eglBaseContext
        )

        peerConnectionFactory = peerConnectionUtil?.peerConnectionFactory

        mediaConstraints = MediaConstraints().apply {
            mandatory.add(MediaConstraints.KeyValuePair("OfferToReceiveVideo", "true"))
            mandatory.add(MediaConstraints.KeyValuePair("RtpDataChannels", "true"))
            mandatory.add(MediaConstraints.KeyValuePair("DtlsSrtpkeyAgreement", "true"))
            mandatory.add(MediaConstraints.KeyValuePair("internalSctpDataChannels", "true"))
        }

        localAudioSource = peerConnectionFactory?.createAudioSource(MediaConstraints())
        localVideoSource = peerConnectionFactory?.createVideoSource(false)
        peerConnection = buildPeerConnection(peerConnectionObserver)

        videoCapturer = getFrontCameraCapturer(context)

        isFrontCamera = true
    }

    private fun buildPeerConnection(observer: PeerConnection.Observer) =
        peerConnectionFactory?.createPeerConnection(
            peerConnectionUtil?.iceServer,
            observer
        )

    private fun initSurfaceView(view: SurfaceViewRenderer) = view.run {
        setMirror(false)
        setEnableHardwareScaler(true)
        init(eglBase?.eglBaseContext, null)
    }

    fun initSurfaces(local: SurfaceViewRenderer, remote: SurfaceViewRenderer) {
        initSurfaceView(local)
        initSurfaceView(remote)
    }

    private fun getFrontCameraCapturer(context: Context) = Camera2Enumerator(context).run {
        deviceNames.find {
            isFrontFacing(it)
        }?.let {
            createCapturer(it, null)
        } ?: throw IllegalStateException()
    }

    private fun getBackCameraCapturer(context: Context) = Camera2Enumerator(context).run {
        deviceNames.find {
            isBackFacing(it)
        }?.let {
            createCapturer(it, null)
        } ?: throw IllegalStateException()
    }

    fun startLocalVideoCapture(localSurfaceView: SurfaceViewRenderer) {
        val surfaceTextureHelper =
            SurfaceTextureHelper.create(Thread.currentThread().name, eglBase?.eglBaseContext)
        (videoCapturer as VideoCapturer).initialize(
            surfaceTextureHelper,
            localSurfaceView.context,
            localVideoSource?.capturerObserver
        )
        videoCapturer?.startCapture(VIDEO_HEIGHT, VIDEO_WIDTH, VIDEO_FPS)

        localAudioTrack =
            peerConnectionFactory?.createAudioTrack(LOCAL_TRACK_ID + "_audio", localAudioSource)
        localVideoTrack =
            peerConnectionFactory?.createVideoTrack(LOCAL_TRACK_ID + "_video", localVideoSource)

        localVideoTrack?.addSink(localSurfaceView)

        val localStream = peerConnectionFactory?.createLocalMediaStream(LOCAL_STREAM_ID)
        localStream?.addTrack(localVideoTrack)
        localStream?.addTrack(localAudioTrack)

        peerConnection?.addStream(localStream)
    }

    fun call(meetingID: String) {
        Log.d(TAG, "contacts: called")

        peerConnection?.createOffer(
            SdpObserverImpl(
                onCreateSuccessCallback = { sdp ->
                    Log.d(TAG, "contacts: onCreateSuccessCallback called")
                    peerConnection?.setLocalDescription(SdpObserverImpl(
                        onSetSuccessCallback = {
                            Log.d(TAG, "contacts: onSetSuccess called")

                            val offerSdp =
                                Sdp()
                            offerSdp.meetingID = meetingID
                            offerSdp.sdp = Text(sdp.description)
                            offerSdp.callType = sdp.type.name

                            val upsertTask = cloudDBZone?.executeUpsert(offerSdp)
                            upsertTask?.addOnSuccessListener { cloudDBZoneResult ->
                                Log.i(TAG, "Calls Sdp Upsert success: $cloudDBZoneResult")
                            }?.addOnFailureListener {
                                Log.i(TAG, "Calls Sdp Upsert failed: ${it.message}")
                            }
                            Log.e(TAG, "onSetSuccess")
                        }
                    ), sdp)
                }
            ), mediaConstraints
        )
    }

    fun answer(meetingID: String) {
        Log.d(TAG, "answer: called")

        peerConnection?.createAnswer(
            SdpObserverImpl(
                onCreateSuccessCallback = { sdp ->
                    Log.d(TAG, "answer: onCreateSuccessCallback called")

                    val answerSdp = Sdp()
                    answerSdp.meetingID = meetingID
                    answerSdp.sdp = Text(sdp.description)
                    answerSdp.callType = sdp.type.name

                    val upsertTask = cloudDBZone?.executeUpsert(answerSdp)
                    upsertTask?.addOnSuccessListener { cloudDBZoneResult ->
                        Log.i(TAG, "Calls Sdp Upsert success: $cloudDBZoneResult")
                    }?.addOnFailureListener {
                        Log.i(TAG, "Calls Sdp Upsert failed: ${it.message}")
                    }

                    peerConnection?.setLocalDescription(SdpObserverImpl(
                        onSetSuccessCallback = {
                            Log.d(TAG, "answer: onSetSuccessCallback called")
                        }
                    ), sdp)
                }
            ), mediaConstraints
        )
    }

    fun endCall(meetingID: String) {

        val callsSdp = Sdp()
        callsSdp.meetingID = meetingID
        callsSdp.callType = Constants.TYPE.END.name
        val upsertTask = cloudDBZone?.executeUpsert(callsSdp)

        upsertTask?.addOnSuccessListener { cloudDBZoneResult ->
            Log.i(TAG, "Calls Sdp Upsert success: $cloudDBZoneResult")
        }?.addOnFailureListener {
            Log.i(TAG, "Calls Sdp Upsert failed: ${it.message}")
        }
    }

    fun setRemoteDescription(sessionDescription: SessionDescription) =
        peerConnection?.setRemoteDescription(SdpObserverImpl(), sessionDescription)

    fun addIceCandidate(iceCandidate: IceCandidate) = peerConnection?.addIceCandidate(iceCandidate)

    fun closePeerConnection() = peerConnection?.close()

    fun clearCandidates(meetingID: String) {
        val queryCallsCandidates =
            CloudDBZoneQuery.where(Candidates::class.java).equalTo("meetingID", meetingID)
        val queryTaskCallsCandidates = cloudDBZone?.executeQuery(
            queryCallsCandidates,
            CloudDBZoneQuery.CloudDBZoneQueryPolicy.POLICY_QUERY_FROM_CLOUD_ONLY
        )
        queryTaskCallsCandidates?.addOnSuccessListener { snapshot ->
            val callsCandidatesList = mutableListOf<Candidates>()
            try {
                while (snapshot.snapshotObjects.hasNext()) {
                    callsCandidatesList.add(snapshot.snapshotObjects.next())
                }
            } catch (e: AGConnectCloudDBException) {
                Log.w(TAG, "Snapshot Error: " + e.message)
            } finally {
                val iceCandidateArray: MutableList<IceCandidate> = mutableListOf()
                for (data in callsCandidatesList) {
                    if (data.callType != null && data.callType == Constants.USERTYPE.OFFER_USER.name) {
                        iceCandidateArray.add(
                            IceCandidate(
                                data.sdpMid,
                                data.sdpMLineIndex,
                                data.sdpCandidate
                            )
                        )
                    } else if (data.callType != null && data.callType == Constants.USERTYPE.ANSWER_USER.name) {
                        iceCandidateArray.add(
                            IceCandidate(
                                data.sdpMid,
                                data.sdpMLineIndex,
                                data.sdpCandidate
                            )
                        )
                    }
                }

                peerConnection?.removeIceCandidates(iceCandidateArray.toTypedArray())

                val deleteTask = cloudDBZone?.executeDelete(callsCandidatesList)
                deleteTask?.addOnSuccessListener {
                    Log.i(TAG, "Candidates Delete success: $it")
                }?.addOnFailureListener {
                    Log.i(TAG, "Candidates Delete failed: $it")
                }
                snapshot.release()
            }
        }?.addOnFailureListener {
            Log.w(TAG, "QueryTask Failure: " + it.message)
        }
    }

    fun clearSdp(meetingID: String) {

        val queryCallsSdp =
            CloudDBZoneQuery.where(Sdp::class.java).equalTo("meetingID", meetingID)
        val queryTaskCallsSdp = cloudDBZone?.executeQuery(
            queryCallsSdp,
            CloudDBZoneQuery.CloudDBZoneQueryPolicy.POLICY_QUERY_FROM_CLOUD_ONLY
        )
        queryTaskCallsSdp?.addOnSuccessListener { snapshot ->
            val callsSdpList = mutableListOf<Sdp>()
            try {
                while (snapshot.snapshotObjects.hasNext()) {
                    callsSdpList.add(snapshot.snapshotObjects.next())
                }
            } catch (e: AGConnectCloudDBException) {
                Log.w(TAG, "Snapshot Error: " + e.message)
            } finally {
                val deleteTask = cloudDBZone?.executeDelete(callsSdpList)
                deleteTask?.addOnSuccessListener {
                    Log.i(TAG, "Sdp Delete success: $it")
                }?.addOnFailureListener {
                    Log.i(TAG, "Sdp Delete failed: $it")
                }
                snapshot.release()
            }
        }?.addOnFailureListener {
            Log.w(TAG, "QueryTask Failure: " + it.message)
        }

        peerConnection?.close()
    }

    fun enableVideo(isVideoEnabled: Boolean) {
        localVideoTrack?.setEnabled(isVideoEnabled)
    }

    fun enableAudio(isAudioEnable: Boolean) {
        localAudioTrack?.setEnabled(isAudioEnable)
    }

    fun switchCamera(context: Context) {
        isFrontCamera = !isFrontCamera
        videoCapturer?.stopCapture()
        videoCapturer = if (isFrontCamera) getFrontCameraCapturer(context)
        else getBackCameraCapturer(context)
    }

    private const val TAG = "WebRtcClient"
}