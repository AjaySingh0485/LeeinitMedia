@file:JvmName("ActivityExtensions")

//required to call the extension functions from JAVA
package com.pelkinsoft.shopdm.base.extentions

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.annotation.RequiresApi
import com.google.android.material.snackbar.Snackbar
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.core.view.ViewCompat
import androidx.appcompat.widget.Toolbar
import android.util.DisplayMetrics
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.pelkinsoft.shopdm.R
import timber.log.Timber


const val SHARED_ELEMENT_STATUS_BAR = "statusBar"
const val SHARED_ELEMENT_NAVIGATION_BAR = "navigationBar"

fun Activity.startActivityWithDefaultAnimations(intent: Intent) {
    startActivity(intent)
    overridePendingTransition(R.anim.activity_move_in_from_right, R.anim.activity_move_out_to_left)
}

fun Activity.startActivityWithBottomInAnimation(intent: Intent) {
    startActivity(intent)
    overridePendingTransition(R.anim.activity_move_in_from_bottom, R.anim.default_exit)
}

fun Activity.startActivityForResultWithBottomInAnimation(intent: Intent, requestCode: Int) {
    startActivityForResult(intent, requestCode)
    overridePendingTransition(R.anim.activity_move_in_from_bottom, R.anim.default_exit)
}

// Try and find the statusBarBackground and navigationBarBackground and pass them as shared elements
// So they're drawn at same level of the shared view and thus not causing the shared view to draw over
// Navigation bar and status bar
@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
fun Activity.startActivityWithSharedElementTransition(intent: Intent, sharedElement: View) {
    val statusBar = window.decorView.findViewById<View>(android.R.id.statusBarBackground)
    val navBar = window.decorView.findViewById<View>(android.R.id.navigationBarBackground)

    val listOfSharedElements = mutableListOf<Pair<View, String>>()
    statusBar?.let { listOfSharedElements.add(Pair(it,
        SHARED_ELEMENT_STATUS_BAR
    )) }
    navBar?.let { listOfSharedElements.add(Pair(it,
        SHARED_ELEMENT_NAVIGATION_BAR
    )) }
    sharedElement.let { listOfSharedElements.add(Pair(it, ViewCompat.getTransitionName(sharedElement))) }

    val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
            this, *listOfSharedElements.toTypedArray()
    )
    startActivity(intent, options.toBundle())
}

fun Activity.startActivityForResultWithFadeInAnimation(intent: Intent, requestCode: Int) {
    startActivityForResult(intent, requestCode)
    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
}

fun Activity.startActivityWithFadeInAnimation(intent: Intent) {
    startActivity(intent)
    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
}

fun Activity.endActivityWithFadeOutAnimation() {
    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
}

fun Activity.adjustToolbarShadow(toolbar: Toolbar?, shown: Boolean) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        toolbar?.elevation = (if (shown) this.dp2px(4f) else 0).toFloat()
    }
}

fun Activity.buildSnackBar(rootView: View,
                           message: String,
                           snackBarLength: Int = Snackbar.LENGTH_LONG,
                           snackBarTextColor: Int = Color.WHITE): Snackbar {
    val snackBar = Snackbar.make(rootView, message, snackBarLength)
    val textView = snackBar.view.findViewById(R.id.snackbar_text) as TextView
    textView.setTextColor(snackBarTextColor)
    return snackBar
}

fun Window?.enterFullScreenMode() {
    if (this != null && this.decorView != null) {
        this.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        this.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
    } else {
        Timber.e("Try to enter full screen mode but failed")
    }
}

fun Window?.exitFullScreenMode() {
    if (this != null && this.decorView != null) {
        this.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        this.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
    } else {
        Timber.e("Try to enter full screen mode but failed")
    }
}

fun Window?.exitImmersiveMode() {
    this?.decorView?.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
}

fun Window?.enterImmersiveMode() {
    var visibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
            View.SYSTEM_UI_FLAG_FULLSCREEN
    if (Build.VERSION.SDK_INT >= 19) {
        visibility = visibility or View.SYSTEM_UI_FLAG_IMMERSIVE or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
    }
    this?.decorView?.systemUiVisibility = visibility
}

fun Activity?.getDisplayHeightInPx(): Int {
    val displayMetrics = DisplayMetrics()
    this?.windowManager?.defaultDisplay?.getMetrics(displayMetrics)
    return displayMetrics.heightPixels
}

inline fun <reified T : ViewModel> AppCompatActivity.getViewModel(
        crossinline factory: () -> T
): T {
    return createViewModel(factory)
}

@Suppress("UNCHECKED_CAST")
inline fun <reified T : ViewModel> AppCompatActivity.createViewModel(crossinline factory: () -> T): T {

    val vmFactory = object : ViewModelProvider.Factory {
        override fun <U : ViewModel> create(modelClass: Class<U>): U = factory() as U
    }

    return ViewModelProviders.of(this, vmFactory)[T::class.java]
}