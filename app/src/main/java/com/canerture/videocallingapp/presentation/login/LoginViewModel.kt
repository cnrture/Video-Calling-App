package com.canerture.videocallingapp.presentation.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.canerture.videocallingapp.domain.LoginRepository
import com.huawei.hms.support.account.service.AccountAuthService
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val loginRepository: LoginRepository) :
    ViewModel() {

    private var _huaweiSignIn = MutableLiveData<AccountAuthService>()
    val huaweiSignIn: LiveData<AccountAuthService>
        get() = _huaweiSignIn

    fun signInWithHuawei() {
        _huaweiSignIn.value = loginRepository.signInWithHuawei()
    }
}