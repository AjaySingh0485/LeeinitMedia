package com.pelkinsoft.shopdm.base.extentions

import android.graphics.drawable.Drawable
import androidx.annotation.ColorInt
import androidx.annotation.DimenRes
import androidx.annotation.IntegerRes
import androidx.annotation.StringRes
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.appbar.AppBarLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.graphics.drawable.DrawableCompat
import android.util.TypedValue
import android.view.Menu
import android.view.View
import android.view.animation.*
import android.widget.LinearLayout
import android.widget.ProgressBar

private const val ANIMATION_TIME = 500L

fun View.translateInFromBottomWithAnimation() {
    visibility = View.VISIBLE
    val fadeAnimation = AlphaAnimation(0.0f, 1.0f)
    val translateAnimation = TranslateAnimation(0.0f, 0.0f, this.height.toFloat(), 0f)

    val animationSet = AnimationSet(false)
    animationSet.addAnimation(fadeAnimation)
    animationSet.addAnimation(translateAnimation)
    animationSet.interpolator = DecelerateInterpolator()
    animationSet.duration = ANIMATION_TIME
    animationSet.fillAfter = true
    this.startAnimation(animationSet)
}

fun LinearLayout.viewGroupTranslateSlideInAnimation() {
    val animationSet = AnimationSet(true)
    var animation: Animation = AlphaAnimation(0.0f, 1.0f)
    animationSet.addAnimation(animation)
    animation = TranslateAnimation(
            Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 0.0f,
            Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f
    )
    animationSet.addAnimation(animation)
    animationSet.duration = ANIMATION_TIME
    val controller = LayoutAnimationController(animationSet, 0.25f)
    controller.order = LayoutAnimationController.ORDER_REVERSE
    this.layoutAnimation = controller
}

fun View.getDimensionPixelSize(@DimenRes dimenId: Int): Int {
    return resources.getDimensionPixelSize(dimenId)
}

fun View.updateMargin(left: Int, top: Int, right: Int, bottom: Int) {
    when {
        this.layoutParams is CoordinatorLayout.LayoutParams -> {
            val layoutParams = this.layoutParams as CoordinatorLayout.LayoutParams
            layoutParams.setMargins(left, top, right, bottom)
            this.layoutParams = layoutParams
        }
        this.layoutParams is AppBarLayout.LayoutParams -> {
            val layoutParams = this.layoutParams as AppBarLayout.LayoutParams
            layoutParams.setMargins(left, top, right, bottom)
            this.layoutParams = layoutParams
        }
        this.layoutParams is ConstraintLayout.LayoutParams -> {
            val layoutParams = this.layoutParams as ConstraintLayout.LayoutParams
            layoutParams.setMargins(left, top, right, bottom)
        }
    }
}

fun View.updateWidthPercent(@IntegerRes percent: Int) {
    if (layoutParams is ConstraintLayout.LayoutParams) {
        val typedValue = TypedValue()
        val layoutParams = this.layoutParams as ConstraintLayout.LayoutParams
        context.resources.getValue(percent, typedValue, true)
        layoutParams.matchConstraintPercentWidth = typedValue.float
        this.layoutParams = layoutParams
    }
}

fun View.updateRatio(@StringRes ratio: Int) {
    if (layoutParams is ConstraintLayout.LayoutParams) {
        val layoutParams = this.layoutParams as ConstraintLayout.LayoutParams
        layoutParams.dimensionRatio = context.getString(ratio)
        this.layoutParams = layoutParams
    }
}

fun Menu.tintMenuIcons(color: Int) {
    for (i in 0 until size()) {
        val menuItem = getItem(i)
        val normalDrawable = menuItem.icon
        menuItem.icon = normalDrawable.tintDrawable(color)
    }
}

fun Drawable.tintDrawable(color: Int): Drawable {
    val wrappedDrawable = DrawableCompat.wrap(this)
    DrawableCompat.setTint(wrappedDrawable.mutate(), color)
    return DrawableCompat.unwrap(wrappedDrawable)
}

fun ProgressBar.tintProgressDrawable(@ColorInt colour: Int) {
    if (indeterminateDrawable != null) {
        indeterminateDrawable = indeterminateDrawable.tintDrawable(colour)
    }
}
