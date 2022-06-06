package com.canerture.videocallingapp.presentation.main

import androidx.lifecycle.ViewModel
import com.canerture.videocallingapp.domain.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class MainViewModel @Inject constructor(private val mainRepository: MainRepository) : ViewModel() {

    fun checkMeetingId(meetingID: String, hasMeetingId: (Boolean) -> Unit) {
        mainRepository.checkMeetingId(meetingID, hasMeetingId)
    }

    fun generateMeetingId(): String {
        return (1000000 + Random.nextInt(900000)).toString()
    }
}