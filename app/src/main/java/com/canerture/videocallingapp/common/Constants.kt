package com.canerture.videocallingapp.common

object Constants {

    const val CloudDbZoneName = "VideoCallingAppZone"

    const val VIDEO_WIDTH = 280
    const val VIDEO_HEIGHT = 200
    const val VIDEO_FPS = 60

    const val LOCAL_TRACK_ID = "local_track"
    const val LOCAL_STREAM_ID = "stream_track"

    const val MEETING_ID = "meetingID"
    const val IS_JOIN = "isJoin"

    enum class USERTYPE {
        OFFER_USER,
        ANSWER_USER
    }

    enum class TYPE {
        OFFER,
        ANSWER,
        END
    }
}

