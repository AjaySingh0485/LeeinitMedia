package com.pelkinsoft.shopdm.main

import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import com.pelkinsoft.shopdm.CommonUtility
import com.pelkinsoft.shopdm.R
import com.pelkinsoft.shopdm.base.rxjava.autoDispose
import com.pelkinsoft.shopdm.base.rxjava.throttleClicks
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.custom_menu_layout.view.*


/**
 * Created by Narola on 26/10/18.
 */
class NavigationView(context: Context) : FrameLayout(context) {

    private val itemClickSubject: PublishSubject<NavigationOptionModel> = PublishSubject.create()
    var itemClick: Observable<NavigationOptionModel> = itemClickSubject.hide()

    private val compositeDisposable = CompositeDisposable()

    init {
        layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        View.inflate(context, R.layout.custom_menu_layout, this)
    }

    fun bind(navigationOption: NavigationOptionModel) {

        textNavigationOption.compoundDrawablePadding = 50
        textNavigationOption.text = navigationOption.optionName
        textNavigationOption.setCompoundDrawablesWithIntrinsicBounds(
            navigationOption.optionDrawable,
            0, 0, 0

        )

        if (navigationOption.optionSelected) {
            textNavigationOption.setTextColor(
                ContextCompat.getColor(
                    context,
                    R.color.colorFont
                )
            )

            CommonUtility.setTextViewDrawableColor(
                textNavigationOption,R.color.colorFont, context
            )
        } else {
            textNavigationOption.setTextColor(Color.BLACK)
            CommonUtility.setTextViewDrawableColor(textNavigationOption, R.color.colorPrimary, context)
        }

        textNavigationOption.text = navigationOption.optionName



        throttleClicks().subscribe {
            itemClickSubject.onNext(navigationOption)
        }.autoDispose(compositeDisposable)


    }


}