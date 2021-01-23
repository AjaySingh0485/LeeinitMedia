package com.pelkinsoft.shopdm.base.network

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.telephony.TelephonyManager
import android.text.TextUtils
import android.util.Log
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import timber.log.Timber

class NetworkObserver(application: Application): BroadcastReceiver() {

    companion object {
        val intentFilter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
    }
    private var connectivityManager: ConnectivityManager? = null
    private var telephonyManager: TelephonyManager? = null
    private var previousNetworkType = NetworkType.NetworkTypeUnknown

    private val networkChangesSubject = BehaviorSubject.create<Boolean>()
    // This can be observed to get notified when the network changes. Note it is a BehaviorSubject so the last emitted value
    // (the current network state) will be emitted on subscribing
    val networkChanges: Observable<Boolean> = networkChangesSubject.hide()

    init {
        try {
            connectivityManager = application.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            telephonyManager = application.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        } catch (e: Exception) {
            Timber.e("Error in tryToInitialize().\n" + Log.getStackTraceString(e))
        }
        networkChangesSubject.onNext(isConnected())
        application.registerReceiver(this, intentFilter)
    }

    override fun onReceive(context: Context, intent: Intent) {
        val newNetworkType = getNetworkType()
        if (previousNetworkType != newNetworkType) {

            if ((previousNetworkType == NetworkType.NetworkTypeUnknown || previousNetworkType == NetworkType.NetworkTypeNone)
                    && (newNetworkType == NetworkType.NetworkTypeCellular || newNetworkType == NetworkType.NetworkTypeWifi)) {
                networkChangesSubject.onNext(true)
            } else if ((previousNetworkType == NetworkType.NetworkTypeCellular || previousNetworkType == NetworkType.NetworkTypeWifi)
                    && (newNetworkType == NetworkType.NetworkTypeUnknown || newNetworkType == NetworkType.NetworkTypeNone)) {
                networkChangesSubject.onNext(false)
            }

            previousNetworkType = newNetworkType
        }
    }

    private fun getNetworkType(): NetworkType {
        val networkInfo = connectivityManager?.activeNetworkInfo
        if (networkInfo == null || !networkInfo.isAvailable || !networkInfo.isConnected) {
            return NetworkType.NetworkTypeNone
        }
        when (networkInfo.type) {
            ConnectivityManager.TYPE_MOBILE -> {
                try {
                    if (telephonyManager != null && telephonyManager?.dataState == TelephonyManager.DATA_CONNECTED) {
                        return NetworkType.NetworkTypeCellular
                    }
                } catch (e: SecurityException) {
                    // It seems SAMSUNG devices on lollipop do not allow access to the TelephonyManager.getDataState() method without the ACCESS_PHONE_STATE permission.
                    // If this has happened, just assume we are connected. In the worst case scenario, we will experience a timeout when trying to fetch info from the internet.
                    Timber.e("getNetworkType() unable to determine data state. Assuming valid cellular connection.")
                    return NetworkType.NetworkTypeCellular
                }

                // We have also seen on some devices this be shown as disconnected even though that is not the case.
                // We will ico_check the network info extra
                val networkExtra = networkInfo.extraInfo
                // Assume we're good if the device gives back anything
                if (!TextUtils.isEmpty(networkExtra)) {
                    return NetworkType.NetworkTypeCellular
                }
            }
            ConnectivityManager.TYPE_WIFI,
            ConnectivityManager.TYPE_BLUETOOTH, // (Bluetooth modem or tethering)
            ConnectivityManager.TYPE_ETHERNET,  // (USB-ethernet connection)
            ConnectivityManager.TYPE_WIMAX -> return NetworkType.NetworkTypeWifi
            else -> return NetworkType.NetworkTypeNone
        }
        return NetworkType.NetworkTypeNone
    }

    fun isConnected(): Boolean =
            when (getNetworkType()) {
                NetworkType.NetworkTypeWifi,
                NetworkType.NetworkTypeCellular -> true
                else -> false
            }

}

enum class NetworkType {
    NetworkTypeNone, NetworkTypeCellular, NetworkTypeWifi, NetworkTypeUnknown
}