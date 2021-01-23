package com.pelkinsoft.shopdm.base

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.pelkinsoft.shopdm.MyApplication
import com.pelkinsoft.shopdm.base.network.NetworkObserver
import com.pelkinsoft.shopdm.prefernce.PreferenceInterceptor
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsStatusCodes
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.pelkinsoft.shopdm.helper.ProgressDialog

import io.reactivex.disposables.CompositeDisposable

open class BaseActivity : AppCompatActivity() {

    lateinit var networkObserver: NetworkObserver

    lateinit var fireStoreDatabase: FirebaseFirestore
    lateinit var auth: FirebaseAuth

    protected val compositeDisposable = CompositeDisposable()
    lateinit var progressDialog: ProgressDialog

    protected val preference: PreferenceInterceptor =
        MyApplication.getInstance().getPreference()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeFirebase()
        networkObserver = NetworkObserver(application)
        progressDialog = ProgressDialog(this)

    }

    private fun initializeFirebase() {
        fireStoreDatabase = FirebaseFirestore.getInstance()

        auth = Firebase.auth

    }
    fun selectImage() {
        val options = arrayOf<CharSequence>("Take Photo", "Choose from Gallery", "Cancel")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Add Photo!")
        builder.setItems(options, DialogInterface.OnClickListener { dialog, item ->
            when {
                options[item] == "Take Photo" -> {
                    val takePicture = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    startActivityForResult(takePicture, 0)
                }
                options[item] == "Choose from Gallery" -> {
                    val pickPhoto =
                        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    startActivityForResult(pickPhoto, 1)
                }
                options[item] == "Cancel" -> dialog.dismiss()
            }
        })
        builder.show()
    }

    fun checkPermissionForReadExternalStorage(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val permissions = arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE
                , Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA

            )

            for (permission in permissions) {
                if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                    return false
                }
            }
        }
        return true
    }

    @Throws(Exception::class)
    fun requestPermissionForReadExternalStorage() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(
                    arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ),
                    BaseActivity.READ_STORAGE_PERMISSION_REQUEST_CODE
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }

    }
    fun requestLocationPermissions() {
        startLocationPermissionRequest()
    }

    fun checkIfGPSEnable(): Boolean {
        val lm = getSystemService(LOCATION_SERVICE) as LocationManager

        var gps_enabled = false
        var network_enabled = false

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (ex: Exception) {
        }

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (ex: Exception) {
        }

        return !(!gps_enabled && !network_enabled)
    }


    fun enableLocationIfRequired(context: Context) {
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(LocationRequest().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY))
            .setAlwaysShow(true)

        val settingsClient = LocationServices.getSettingsClient(context)

        val task = settingsClient!!.checkLocationSettings(builder.build())
        task.addOnCompleteListener {
            try {
                val response = it.getResult(ApiException::class.java)

            } catch (exception: ApiException) {
                when (exception.statusCode) {
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                        // Location settings are not satisfied. But could be fixed by showing the
                        // user a dialog.
                        try {
                            // Cast to a resolvable exception.
                            val resolvable = exception as ResolvableApiException
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().

                            this.startIntentSenderForResult(
                                resolvable.resolution.intentSender,
                                REQUEST_CHECK_SETTINGS,
                                null, 0, 0, 0, null
                            )

                        } catch (e: IntentSender.SendIntentException) {
                            // Ignore the error.
                        } catch (e: ClassCastException) {
                            // Ignore, should be an impossible error.
                        }
                    }
                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.
                    }
                }
            }
        }
    }

    private fun startLocationPermissionRequest() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    fun checkLocationPermissions(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val permissions = arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION
                , Manifest.permission.ACCESS_FINE_LOCATION
            )

            for (permission in permissions) {
                if (this.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                    return false
                }
            }
        }
        return true
    }




    override fun onDestroy() {
        compositeDisposable.clear()
        super.onDestroy()
    }
    companion object {
        val READ_STORAGE_PERMISSION_REQUEST_CODE = 1001
        val LOCATION_PERMISSION_REQUEST_CODE = 1001
        val REQUEST_CHECK_SETTINGS = 0x1
    }
}