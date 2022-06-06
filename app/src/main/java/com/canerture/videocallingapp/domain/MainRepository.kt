package com.canerture.videocallingapp.domain

interface MainRepository {

    fun checkMeetingId(meetingID: String, hasMeetingId: (Boolean) -> Unit)
}