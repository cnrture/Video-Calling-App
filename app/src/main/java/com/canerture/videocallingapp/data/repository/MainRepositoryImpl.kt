package com.canerture.videocallingapp.data.repository

import android.content.Context
import android.util.Log
import com.canerture.videocallingapp.R
import com.canerture.videocallingapp.data.model.Sdp
import com.canerture.videocallingapp.domain.MainRepository
import com.canerture.videocallingapp.common.webrtc.CloudDbWrapper
import javax.inject.Inject

class MainRepositoryImpl @Inject constructor(private val context: Context) : MainRepository {

    override fun checkMeetingId(meetingID: String, hasMeetingId: (Boolean) -> Unit) {
        CloudDbWrapper.checkMeetingId(meetingID, object : CloudDbWrapper.ResultListener {
            override fun onSuccess(result: Any?) {
                val resultList: ArrayList<Sdp>? = result as? ArrayList<Sdp>

                resultList?.forEach {
                    if (it.meetingID == meetingID) hasMeetingId(true) else hasMeetingId(false)
                }
            }

            override fun onFailure(e: Exception) {
                e.localizedMessage?.let {
                    if (it == "noElements")
                        hasMeetingId(false)
                    else
                        Log.e(
                            CHECK_MEETING_ID,
                            context.getString(R.string.error_meeting_id_check)
                        )
                }
            }
        })
    }

    companion object {
        private const val CHECK_MEETING_ID = "CHECK MEETING ID"
    }
}