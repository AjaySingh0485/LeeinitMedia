package com.pelkinsoft.shopdm.base.extentions

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.view.View
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.pelkinsoft.shopdm.R
import com.google.android.material.snackbar.Snackbar


fun Fragment.startActivityWithDefaultAnimations(intent: Intent) {
    activity?.startActivityWithDefaultAnimations(intent)
}

fun Fragment.startActivityForResultWithDefaultAnimations(intent: Intent, requestCode: Int) {
    startActivityForResult(intent, requestCode)
    activity?.overridePendingTransition(R.anim.activity_move_in_from_right, R.anim.activity_move_out_to_left)
}

fun Fragment.startActivityForResultWithBottomInAnimation(intent: Intent, requestCode: Int) {
    startActivityForResult(intent, requestCode)
    activity?.overridePendingTransition(R.anim.activity_move_in_from_bottom, R.anim.default_exit)
}

fun Fragment.startActivityWithBottomInAnimation(intent: Intent) {
    activity?.startActivityWithBottomInAnimation(intent)
}

@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
fun Fragment.startActivityWithSharedElementTransition(intent: Intent, sharedElement: View) {
    activity?.startActivityWithSharedElementTransition(intent, sharedElement)
}

fun Fragment.buildSnackBar(rootView: View,
                                                 message: String,
                                                 snackBarLength: Int = Snackbar.LENGTH_LONG,
                                                 snackBarTextColor: Int = Color.WHITE): Snackbar? {
    return activity?.buildSnackBar(rootView, message, snackBarLength, snackBarTextColor)
}


inline fun <reified T : ViewModel> Fragment.getViewModel(
        crossinline factory: () -> T
): T {
    return createViewModel(factory)
}

@Suppress("UNCHECKED_CAST")
inline fun <reified T : ViewModel> Fragment.createViewModel(crossinline factory: () -> T): T {

    val vmFactory = object : ViewModelProvider.Factory {
        override fun <U : ViewModel> create(modelClass: Class<U>): U = factory() as U
    }

    return ViewModelProviders.of(this, vmFactory)[T::class.java]
}