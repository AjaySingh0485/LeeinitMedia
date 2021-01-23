package com.pelkinsoft.shopdm.prefernce

import android.content.Context
import com.pelkinsoft.shopdm.register.Users

interface PreferenceInterceptor {

    companion object {
        const val PREF_USER_OBJECT="user_object"
        const val PREF_DEVICE_TOKEN = "pref_device_token"
        const val PREF_SECURITY_TOKEN="pref_security_token"
        const val PREF_REMEMBER_ME="pref_remember_me"
        const val PREF_COUNTDOWN_TIMER="pref_countdown_timer"
    }

    /* -----------------------------------------------------------------------------------*/

    fun getUserObject(): String
    fun setUserObject(userDetail:String)


    fun getSecurityToken(): String
    fun setSecurityToken(securityToken: String)

    fun getDeviceToken(): String
    fun setDeviceToken(deviceToken: String)

    fun getRememberMe():Boolean
    fun setRememberMe(rememberMe:Boolean)

    fun getCountDownTimer():Boolean
    fun setCountDownTimer(countDownTimer:Boolean)

    fun clearPreference()

    fun getUserObjectFromPreference(): Users

    fun setUserObjectToPreference(userDetail: Users)

    fun removeUserPreference(context: Context)
}