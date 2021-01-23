package com.pelkinsoft.shopdm.base.extentions

import android.os.Build
import androidx.appcompat.widget.Toolbar

fun Toolbar.getDefaultHeight(): Float {
    val attrs = intArrayOf(android.R.attr.actionBarSize)
    val typedArray = this.context.obtainStyledAttributes(attrs)
    val toolbarHeight = typedArray.getDimensionPixelSize(0, context.dp2px(56F))
    typedArray.recycle()
    return toolbarHeight.toFloat()
}

fun Toolbar.adjustToolbarShadow(shown: Boolean) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        this.elevation = (if (shown) context.dp2px(4f) else 0).toFloat()
    }
}