package com.pelkinsoft.shopdm.helper

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.ViewGroup
import android.view.Window
import com.pelkinsoft.shopdm.R

class ProgressDialog(context: Context) : Dialog(context) {


    init {
        initViews()
    }

    private fun initViews() {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setCancelable(false)
        setContentView(R.layout.progress_dialog)
//        window?.setLayout(
//                (ScreenDimensions.screenWidth * 0.85).toInt(), ViewGroup.LayoutParams.WRAP_CONTENT
//        )

        window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,  ViewGroup.LayoutParams.MATCH_PARENT
        )

        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        // tvMessage = findViewById(R.id.tvMessage)
    }

    fun isCancelable(isCancelable: Boolean) {
        setCancelable(isCancelable)
    }

//    fun setMessage(message: String) {
//        tvMessage!!.text = message
//    }
//
//    fun setMessage(messageId: Int) = tvMessage?.setText(messageId)
}