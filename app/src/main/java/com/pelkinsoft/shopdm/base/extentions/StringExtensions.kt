@file:JvmName("StringExtensions")

//required to call the extension functions from JAVA
package com.pelkinsoft.shopdm.base.extentions

import android.graphics.Color
import androidx.annotation.ColorInt
import android.text.*
import android.text.style.ClickableSpan
import android.text.style.StyleSpan
import android.text.style.URLSpan
import android.view.View

@SuppressWarnings("deprecation")
fun String.encodeToHtml(): Spanned {
    return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
        Html.fromHtml(this, Html.FROM_HTML_MODE_LEGACY)
    } else {
        Html.fromHtml(this)
    }
}

fun String.firstWord(): String {
    return this.substringBefore(" ", this)
}

fun Spanned.constructSpannableString(spanClickListener: (spanUrl: String) -> Unit,
                                     @ColorInt spanColor: Int = Color.BLACK,
                                     spanBold: Boolean = false,
                                     spanUnderLine: Boolean = false): CharSequence {
    val currentSpans = this.getSpans(0, this.length, URLSpan::class.java)
    val buffer = SpannableString(this)
    for (span in currentSpans) {
        val end = this.getSpanEnd(span)
        val start = this.getSpanStart(span)
        buffer.removeSpan(span)
        buffer.setSpan(object : ClickableSpan() {
            override fun onClick(p0: View) {
                spanClickListener(span.url)
            }

            override fun updateDrawState(testPaint: TextPaint) {
                testPaint.color = spanColor
                testPaint.isFakeBoldText = spanBold
                testPaint.isUnderlineText = spanUnderLine
            }

        }, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    }
    return buffer
}


/**
 *  Function will convert part of the text clickable with click link.
 *  link is not specified then it is use `#`
 */
fun CharSequence.makeClickSpannable(spanClickListener: (spanUrl: String) -> Unit,
                                    @ColorInt spanColor: Int = Color.BLACK,
                                    spanBold: Boolean = false,
                                    spanUnderLine: Boolean = false, args: String? = null, link: String? = null): CharSequence {
    //This ico_check will make sure that the String should not have HTML tags if has then it will parse old way.
    //This can happen for other language strings if it was not generated automatically.
    //Once other language are generated without HTML tags will remove this ico_check.
    if (this.contains("[CDATA[")) {
        return this.toString().encodeToHtml().constructSpannableString(spanClickListener, spanColor, spanBold, spanUnderLine)
    } else {
        val buffer = SpannableString(this)
        var argument = args
        if (argument == null) {
            argument = this.toString()
        }
        val startIndex = buffer.indexOf(argument, 0)
        if (startIndex != -1) {
            buffer.setSpan(object : ClickableSpan() {
                override fun onClick(p0: View) {
                    spanClickListener(link ?: "#")
                }

                override fun updateDrawState(testPaint: TextPaint) {
                    testPaint.color = spanColor
                    testPaint.isFakeBoldText = spanBold
                    testPaint.isUnderlineText = spanUnderLine
                }
            }, startIndex, startIndex + argument.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        return buffer
    }
}

/**
 * Function will generate Privacy policy clickable
 */
fun CharSequence.makePrivacyPolicyClickSpannable(spanClickListener: (spanUrl: String) -> Unit,
                                                 @ColorInt spanColor: Int = Color.BLACK,
                                                 spanBold: Boolean = false,
                                                 spanUnderLine: Boolean = false, text: String): CharSequence {
    return this.makeClickSpannable(spanClickListener, spanColor, spanBold, spanUnderLine, text, "privacy")
}

/**
 * Function will generate Term and Condition clickable
 */
fun CharSequence.makeTermsAndConditionClickSpannable(spanClickListener: (spanUrl: String) -> Unit,
                                                     @ColorInt spanColor: Int = Color.BLACK,
                                                     spanBold: Boolean = false,
                                                     spanUnderLine: Boolean = false, text: String): CharSequence {
    return this.makeClickSpannable(spanClickListener, spanColor, spanBold, spanUnderLine, text, "terms")
}

/**
 * Function will style part of the text Bold
 */
fun String.makeStringBold(text: String? = null): CharSequence {
    return if (this.contains("[CDATA[")) {
        this.encodeToHtml()
    } else {
        val buffer = SpannableString(this)
        val boldStyle = StyleSpan(android.graphics.Typeface.BOLD)
        if (text != null) {
            val startIndex = buffer.indexOf(text, 0)
            if (startIndex != -1) {
                buffer.setSpan(boldStyle, startIndex, startIndex + text.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
            }
        } else {
            buffer.setSpan(boldStyle, 0, this.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
        }
        buffer
    }
}

/**
 * Function will help to make Spannable string
 */
fun SpannableStringBuilder.spansAppend(
        text: CharSequence,
        vararg spans: Any
): SpannableStringBuilder {
    val start = length
    append(text)
    removeSpan(StyleSpan::class)
    spans.forEach { span ->
        setSpan(span, start, length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    }
    return this
}