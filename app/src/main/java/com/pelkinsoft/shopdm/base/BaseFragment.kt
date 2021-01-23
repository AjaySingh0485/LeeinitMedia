package com.pelkinsoft.shopdm.base

import android.Manifest
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import com.pelkinsoft.shopdm.MyApplication
import com.pelkinsoft.shopdm.base.network.NetworkObserver
import com.pelkinsoft.shopdm.helper.ProgressDialog
import com.pelkinsoft.shopdm.prefernce.PreferenceInterceptor
import com.google.firebase.firestore.FirebaseFirestore
import io.reactivex.disposables.CompositeDisposable

open class BaseFragment : Fragment() {

    lateinit var networkObserver: NetworkObserver

    lateinit var progressDialog: ProgressDialog

    protected val compositeDisposable = CompositeDisposable()

    protected val preference: PreferenceInterceptor =
        MyApplication.getInstance().getPreference()

    lateinit var fireStoreDatabase: FirebaseFirestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        networkObserver = NetworkObserver(activity?.application!!)

        progressDialog = ProgressDialog(activity!!)

        fireStoreDatabase = FirebaseFirestore.getInstance()

    }


    override fun onDestroy() {
        compositeDisposable.clear()
        super.onDestroy()
    }

    fun checkPermissionForReadExternalStorage(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val permissions = arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE
                , Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA,
                Manifest.permission.ACCESS_FINE_LOCATION

            )

            for (permission in permissions) {
                if (activity?.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
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

    companion object {
        val READ_STORAGE_PERMISSION_REQUEST_CODE = 1001
    }

    fun selectImage() {
        val options = arrayOf<CharSequence>("Take Photo", "Choose from Gallery", "Cancel")
        val builder = AlertDialog.Builder(activity!!)
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

}