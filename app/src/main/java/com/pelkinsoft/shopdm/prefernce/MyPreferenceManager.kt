package com.pelkinsoft.shopdm.prefernce

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.pelkinsoft.shopdm.register.Users


@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class MyPreferenceManager(context: Context) : PreferenceInterceptor {

    private var PREF_NAME="LeeinitPreferences"

    private var preference: SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    override fun getUserObject(): String = preference.getString(PreferenceInterceptor.PREF_USER_OBJECT, "")!!

    override fun setUserObject(userDetail: String) =
        preference.edit().putString(PreferenceInterceptor.PREF_USER_OBJECT, userDetail).apply()

    override fun getDeviceToken(): String =
        preference.getString(PreferenceInterceptor.PREF_DEVICE_TOKEN, "")!!

    override fun setDeviceToken(deviceToken: String) =
        preference.edit().putString(PreferenceInterceptor.PREF_DEVICE_TOKEN, deviceToken).apply()


    override fun getSecurityToken(): String =
        preference.getString(PreferenceInterceptor.PREF_SECURITY_TOKEN, "")!!

    override fun setSecurityToken(securityToken: String) =
        preference.edit().putString(PreferenceInterceptor.PREF_SECURITY_TOKEN, securityToken).apply()

    override fun getRememberMe(): Boolean = preference.getBoolean(PreferenceInterceptor.PREF_REMEMBER_ME, false)


    override fun setRememberMe(rememberMe: Boolean) =
        preference.edit().putBoolean(PreferenceInterceptor.PREF_REMEMBER_ME, rememberMe).apply()



    override fun getCountDownTimer(): Boolean = preference.getBoolean(PreferenceInterceptor.PREF_COUNTDOWN_TIMER, false)

    override fun setCountDownTimer(countDownTimer: Boolean) =
        preference.edit().putBoolean(PreferenceInterceptor.PREF_COUNTDOWN_TIMER, countDownTimer).apply()

    override fun clearPreference() {
        preference.edit().clear().apply()
    }

    override fun getUserObjectFromPreference(): Users {
        val gson = Gson()
        val userString = getUserObject()
        val userDetail = gson.fromJson(userString, Users::class.java)

        return userDetail
    }

    override fun removeUserPreference(context: Context) {
        val preferences = context.getSharedPreferences(PREF_NAME, Activity.MODE_PRIVATE)
        val editor = preferences.edit()
        editor.remove(PreferenceInterceptor.PREF_USER_OBJECT)
        editor.apply()
    }


    override fun setUserObjectToPreference(userDetail: Users) {
        val gson = Gson()
        val userObject = gson.toJson(userDetail)
        setUserObject(userObject)
    }
}