package com.canerture.videocallingapp.domain

import com.huawei.hms.support.account.service.AccountAuthService

interface LoginRepository {

    fun signInWithHuawei(): AccountAuthService
}