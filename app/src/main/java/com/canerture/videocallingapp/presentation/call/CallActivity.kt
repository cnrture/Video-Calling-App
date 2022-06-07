package com.canerture.videocallingapp.presentation.call

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import com.canerture.videocallingapp.R
import com.canerture.videocallingapp.common.Constants.IS_JOIN
import com.canerture.videocallingapp.common.Constants.MEETING_ID
import com.canerture.videocallingapp.databinding.ActivityCallBinding
import com.canerture.videocallingapp.common.webrtc.WebRtcClient
import dagger.hilt.android.AndroidEntryPoint
import org.webrtc.VideoTrack

@AndroidEntryPoint
class CallActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCallBinding

    private val callViewModel: CallViewModel by viewModels()

    private lateinit var meetingID: String

    private var isJoin = false
    private var isMute = false
    private var isVideoPaused = false

    var millisecondTime = 0L
    var startTime = 0L

    var seconds = 0
    var minutes = 0

    var handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCallBinding.inflate(layoutInflater)
        setContentView(binding.root)

        intent.let {
            meetingID = it.getStringExtra(MEETING_ID).orEmpty()
            isJoin = it.getBooleanExtra(IS_JOIN, false)
        }

        callViewModel.initClasses(meetingID, isJoin)

        initObservers(this)

        WebRtcClient.initSurfaces(binding.localView, binding.remoteView)
        WebRtcClient.startLocalVideoCapture(binding.localView)

        if (!isJoin) callViewModel.startCall(meetingID)

        with(binding) {
            micBtn.setOnClickListener {
                isMute = !isMute
                callViewModel.setAudioState(!isMute)
                if (isMute) binding.micBtn.setImageResource(R.drawable.ic_mic_off)
                else binding.micBtn.setImageResource(R.drawable.ic_mic_on)
            }

            videoBtn.setOnClickListener {
                isVideoPaused = !isVideoPaused
                callViewModel.setVideoState(!isVideoPaused)
                if (isVideoPaused) binding.videoBtn.setImageResource(R.drawable.ic_cam_off)
                else binding.videoBtn.setImageResource(R.drawable.ic_cam_on)
            }

            switchCameraBtn.setOnClickListener {
                callViewModel.switchCamera()
                WebRtcClient.startLocalVideoCapture(binding.localView)
            }

            endCallBtn.setOnClickListener {
                callViewModel.endCall(meetingID)
            }
        }
    }

    private fun initObservers(lifeCyclerOwner: LifecycleOwner) {
        with(callViewModel) {
            rtpTransceiver.observe(lifeCyclerOwner) {
                val videoTrack = it.receiver.track() as VideoTrack
                videoTrack.addSink(binding.remoteView)
            }

            mediaStream.observe(lifeCyclerOwner) {
                it.videoTracks.first().addSink(binding.remoteView)
            }

            connectionEstablished.observe(lifeCyclerOwner) {
                if (it) binding.endCallBtn.isClickable = true
            }

            answerReceived.observe(lifeCyclerOwner) {
                handler.postDelayed(runnable, 0)
                startTime = SystemClock.uptimeMillis()
            }

            callEnded.observe(lifeCyclerOwner) {
                finish()
                callViewModel.clearSdp(meetingID)
            }
        }
    }

    private var runnable = object : Runnable {

        override fun run() {
            millisecondTime = SystemClock.uptimeMillis() - startTime
            seconds = (millisecondTime / 1000).toInt()
            minutes = seconds / 60
            seconds %= 60

            setTimeText(minutes, binding.tvCallingTimeMinute)
            setTimeText(seconds, binding.tvCallingTimeSecond)

            handler.postDelayed(this, 0)
        }
    }

    private fun setTimeText(time: Int, tv: TextView) {
        if (time.toString().length < 2) "0$time:".also { tv.text = it }
        else tv.text = time.toString()
    }

    override fun onDestroy() {
        super.onDestroy()
        callViewModel.destroy(meetingID)
    }
}