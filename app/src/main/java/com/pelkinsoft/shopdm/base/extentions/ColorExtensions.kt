package com.pelkinsoft.shopdm.base.extentions

import android.content.res.ColorStateList
import android.graphics.Color

fun Int.getStatefulColor(): ColorStateList {
    val states = arrayOf(intArrayOf(-android.R.attr.state_enabled), // disabled
            intArrayOf(android.R.attr.state_pressed), // pressed
            intArrayOf() //normal
    )

    val colors = intArrayOf(this.getLightColor(0.25), this.getDarkColor(0.25), this)
    return ColorStateList(states, colors)
}

/**
 * Fraction should be between 0 to 1
 */
fun Int.getLightColor(fraction: Double): Int {
    val alpha = Color.alpha(this)
    val red = lightenColor(Color.red(this), fraction)
    val green = lightenColor(Color.green(this), fraction)
    val blue = lightenColor(Color.blue(this), fraction)

    return Color.argb(alpha, red, green, blue)
}

/**
 * Fraction should be between 0 to 1
 */
fun Int.getDarkColor(fraction: Double): Int {
    val alpha = Color.alpha(this)
    val red = darkenColor(Color.red(this), fraction)
    val green = darkenColor(Color.blue(this), fraction)
    val blue = darkenColor(Color.blue(this), fraction)

    return Color.argb(alpha, red, green, blue)
}

private fun darkenColor(color: Int, fraction: Double): Int {
    return Math.max(color - color * fraction, 0.0).toInt()
}

private fun lightenColor(color: Int, fraction: Double): Int {
    return Math.min(color + color * fraction, 255.0).toInt()
}