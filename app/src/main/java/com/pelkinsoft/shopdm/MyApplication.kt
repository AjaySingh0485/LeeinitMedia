package com.pelkinsoft.shopdm

import android.app.Application
import com.pelkinsoft.shopdm.prefernce.MyPreferenceManager
import com.pelkinsoft.shopdm.prefernce.PreferenceInterceptor
import com.google.firebase.FirebaseApp



class MyApplication: Application() {

    private val TAG = MyApplication::class.java.simpleName
    private lateinit var preferences: PreferenceInterceptor

    companion object {
        private lateinit var instance: MyApplication
        fun getInstance() = instance
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

        FirebaseApp.initializeApp(this);
        preferences = MyPreferenceManager(this)


    }
    fun getPreference() = preferences
}
