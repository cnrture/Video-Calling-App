package com.canerture.videocallingapp.data.repository

import android.content.Context
import com.canerture.videocallingapp.domain.LoginRepository
import com.huawei.hms.support.account.AccountAuthManager
import com.huawei.hms.support.account.request.AccountAuthParams
import com.huawei.hms.support.account.request.AccountAuthParamsHelper
import com.huawei.hms.support.account.service.AccountAuthService
import javax.inject.Inject

class LoginRepositoryImpl @Inject constructor(private val context: Context) : LoginRepository {

    override fun signInWithHuawei(): AccountAuthService {
        val authParam = AccountAuthParamsHelper(AccountAuthParams.DEFAULT_AUTH_REQUEST_PARAM)
            .setIdToken()
            .createParams()
        return AccountAuthManager.getService(context, authParam)
    }
}