@file:Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")

package com.pelkinsoft.shopdm

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.*
import android.net.Uri
import android.text.TextUtils
import android.util.Patterns
import android.view.View
import android.view.inputmethod.InputMethodManager

import java.util.regex.Pattern
import android.util.DisplayMetrics
import android.view.ViewGroup
import java.text.ParseException
import java.text.SimpleDateFormat

import android.webkit.MimeTypeMap
import android.content.Intent
import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.os.Handler
import android.provider.MediaStore
import android.telephony.PhoneNumberUtils
import android.util.Base64
import android.util.Log
import android.widget.*
import androidx.annotation.ColorInt
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.*


object CommonUtility {

    var APP_DATE_TIME_FORMAT: String = "yyyy/MM/dd HH:mm:ss"
    var SERVER_DATE_TIME_FORMAT: String = "yyyy-MM-dd HH:mm:ss"
    var APP_DATE_FORMAT: String = "yyyy/MM/dd"
    var JOB_DATE_FORMAT: String = "dd MMMM yyyy"
    var STRIPE_DATE_FORMAT: String = "yyyy-dd-MM"
    var APP_TIME_FORMAT: String = "HH:mm"
    var DATE_FORMAT: String = "MMMM yyyy"
    var YEAR_FORMAT: String = "yyyy"
    private const val SECOND_MILLIS = 1000
    private const val MINUTE_MILLIS = 60 * SECOND_MILLIS
    private const val HOUR_MILLIS = 60 * MINUTE_MILLIS
    private const val DAY_MILLIS = 24 * HOUR_MILLIS
    val DATE_FORMAT_COMMENT_TIME_UTC = SimpleDateFormat(APP_DATE_TIME_FORMAT, Locale.getDefault())


    private val TAG = CommonUtility::class.java.simpleName
    val FONT_ARIAL_REGULAR = "arial.ttf"

    fun getAddressFromLocation(latitude: Double, longitude: Double, activity: Activity?): String {

        val addresses = getAddress(latitude, longitude, activity)
        val result = StringBuilder()

        if (addresses != null && addresses.size > 0) {

            val address = addresses[0]
            Log.e("address", addresses[0].toString())
            result.append(address.getAddressLine(0))
            //stateName = address.adminArea
        }

        return if (result.isNullOrEmpty()) {
            ""
        } else {
            result.toString()
        }


    }

    private val c = charArrayOf('k', 'm', 'b', 't')

    fun coolFormat(n: Double, iteration: Int): String {
        val d = n.toLong() / 100 / 10.0
        val isRound =
            d * 10 % 10 == 0.0//true if the decimal part is equal to 0 (then it's trimmed anyway)
        return (if (d < 1000)
        //this determines the class, i.e. 'k', 'm' etc
            ((if (d > 99.9 || isRound || (!isRound && d > 9.99))
            //this decides whether to trim the decimals
                d.toInt() * 10 / 10
            else
                (d).toString() + "" // (int) d * 10 / 10 drops the decimal
                    )).toString() + "" + c[iteration]
        else
            coolFormat(d, iteration + 1))

    }
    fun colorStateListOf(@ColorInt color: Int): ColorStateList {
        return ColorStateList.valueOf(color)
    }

    fun getFeesString(number: Int): String {
        var numberString = ""
        if (Math.abs(number / 1000000) > 1) {
            numberString = (number / 1000000).toString() + "m"

        } else if (Math.abs(number / 1000) > 1) {
            numberString = (number / 1000).toString() + "k"

        } else {
            numberString = number.toString()

        }
        return numberString
    }

    @SuppressLint("SimpleDateFormat")
    fun getDateDiff(date1: String, date2: String): Int {
        val tempDate1 = SimpleDateFormat(APP_DATE_TIME_FORMAT).parse(date1);
        var tempDate2 = SimpleDateFormat(APP_DATE_TIME_FORMAT).parse(date2);

        val cal = Calendar.getInstance();
        if (tempDate1.before(tempDate2)) {
            cal.setTime(tempDate1);
        } else {
            cal.setTime(tempDate2);
            tempDate2 = tempDate1;
        }
        var c = 0;
        while (cal.time.before(tempDate2)) {
            cal.add(Calendar.MONTH, 1);
            c++;
        }
        return c - 1
    }

    fun getImageUri(context: Context, inImage: Bitmap): Uri? {
        val bytes = ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        val path = MediaStore.Images.Media.insertImage(
            context.contentResolver
            , inImage, "JobIt", null
        );
        return Uri.parse(path);
    }

    @SuppressLint("Recycle")
    fun getRealPathFromURI(context: Context, inImage: Bitmap): String? {
        val uri = getImageUri(context, inImage)
        val cursor = context.contentResolver.query(uri!!, null, null, null, null);
        cursor?.moveToFirst();
        val idx = cursor?.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor?.getString(idx!!);
    }

    fun manageMargin(context: Context, view: View, weight: Int, height: Int) {
        val deviceWidth = getScreenWidth(context)
        val deviceHeight = getScreenHeight(context)

        //Main layout height and width
        val relativeParam = view.layoutParams as ConstraintLayout.LayoutParams
        relativeParam.width = weight * deviceWidth / 100
        relativeParam.height = height * deviceHeight / 100
        view.setLayoutParams(relativeParam)
    }

    fun getStatusBarHeight(context: Context): Int {
        var result = 0
        val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            result = context.resources.getDimensionPixelSize(resourceId)
        }
        return result
    }


    fun showKeyboard(context: Context, view: View?) {
        if (view != null) {
            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
        }
    }

    fun getValueInDoubleDecimalPoint(str: String?): String {

        if (str == null)
            return "0.00"

        if (str == "")
            return "0.00"

        val value = java.lang.Double.parseDouble(str)
        return String.format("%.02f", value)
    }

    fun showPhoto(photoUri: Uri, context: Context) {
        val intent = Intent()
        intent.action = Intent.ACTION_VIEW
        intent.setDataAndType(photoUri, "image/*")
        context.startActivity(intent)
    }

    // Set Custom Fonts
    fun setCustomFont(context: Context, fontType: String): Typeface {
        return Typeface.createFromAsset(
            context.assets,
            "fonts/$fontType"
        );
    }

    fun getScreenHeight(context: Context?): Int {
        val displayMetrics = DisplayMetrics()
        (context as Activity).windowManager?.defaultDisplay?.getMetrics(displayMetrics)
        return displayMetrics.heightPixels
    }

    fun getCurrentDate(): String {
        val todayDate = Calendar.getInstance().time
        val formatter = SimpleDateFormat(APP_DATE_TIME_FORMAT, Locale.getDefault())
        val todayString = formatter.format(todayDate)
        return todayString
    }


    fun hideSoftKeyBordEditText(editText: EditText) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) { // API 21
            editText.showSoftInputOnFocus = false;
        } else { // API 11-20
            editText.setTextIsSelectable(true);
        }

    }

    fun getAddress(latitude: Double, longitude: Double, activity: Activity?): MutableList<Address> {
        val result = StringBuilder()
        var addresses: MutableList<Address>? = null
        try {
            val geocoder = Geocoder(activity!!, Locale.getDefault())
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses.size > 0) {

                val address = addresses[0]
                Log.e("address", addresses[0].toString())
                result.append(address.getAddressLine(0))
            }
        } catch (e: IOException) {
            Log.e("tag", e.message)
        }

        return if (addresses.isNullOrEmpty()) {
            mutableListOf()
        } else {
            addresses
        }

    }

    fun metersToMiles(meters: Double): Double {
        return meters / 1609.3440057765
    }

    fun secondToMinutes(second: Int): Int {
        return second / 60
    }


    fun setMargins(view: View, left: Int, top: Int, right: Int, bottom: Int) {
        if (view.layoutParams is ViewGroup.MarginLayoutParams) {
            val p = view.layoutParams as ViewGroup.MarginLayoutParams
            p.setMargins(left, top, right, bottom)
            view.requestLayout()
        }
    }

    fun convertBase64(bitmap: Bitmap): String {

        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        val b = baos.toByteArray();
        val encImage = Base64.encodeToString(b, Base64.DEFAULT);
        return encImage
//
//        val byteArrayOutputStream = ByteArrayOutputStream()
//        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
//        val byteArray = byteArrayOutputStream.toByteArray()
//        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    fun getScreenWidth(context: Context?): Int {
        val displayMetrics = DisplayMetrics()
        (context as Activity).windowManager?.defaultDisplay?.getMetrics(displayMetrics)
        return displayMetrics.widthPixels
    }

    fun isEmulator(): Boolean {
        return Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")
                || Build.MANUFACTURER.contains("Genymotion")
                || (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
                || "google_sdk".equals(Build.PRODUCT);
    }

    fun hideKeyboard(mContext: Context?) {
        try {
            if (mContext != null) {
                val imm =
                    mContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow((mContext as Activity).currentFocus!!.windowToken, 0)
            }
        } catch (e: Exception) {
            Log.e(TAG, e.message)
        }
    }

    fun hideKeyboard(context: Context, view: View?) {
        try {
            if (view != null) {
                val imm =
                    context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view.windowToken, 0)
            }
        } catch (e: Exception) {
            println("TAG --> " + e.message)
        }
    }

    private fun getCurrentDateinLocal(): Date? {
        //        SimpleDateFormat sourceFormat = new SimpleDateFormat(
        //                "EEE MMM DD HH:mm:ss zzz yyyy", Locale.getDefault());
        var parsedDate: Date? = null
        try {
            val c = Calendar.getInstance()
            try {
                parsedDate = DATE_FORMAT_COMMENT_TIME_UTC
                    .parse(DATE_FORMAT_COMMENT_TIME_UTC.format(c.time))

                Log.e(TAG, "parsed date:" + parsedDate!!.toString())

            } catch (e: Exception) {
                e.printStackTrace()
            }

            //  Debug.e(TAG, "parsedDate =" + parsedDate.toString());
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return parsedDate

    }


    fun getMessageFormat(date2: Date, originalDate: String): String {

        Log.e(TAG, "date received in getLastSeen ==" + date2.toString())

        val date1 = getCurrentDateinLocal()
        val deltaSeconds = ((date1!!.time - date2.time) / 1000).toInt()
        val deltaMinutes = Math.round((deltaSeconds / 60.0f).toDouble()).toInt()
        val minutes: Int
        if (deltaSeconds < 5) {
            return "Just now"
        } else if (deltaSeconds < 60) {
            return deltaSeconds.toString() + " sec ago"
        } else if (deltaSeconds < 120) {
            return "A minute ago"
        } else if (deltaMinutes < 60) {
            return deltaMinutes.toString() + " min ago"
        } else if (deltaMinutes < 120) {
            return "An hour ago"
        } else if (deltaMinutes < 24 * 60) {
            minutes = (deltaMinutes / 60).toInt()
            return minutes.toString() + " hours ago"
        } else if (deltaMinutes < 24 * 60 * 2) {
            return "Yesterday"
        } else if (deltaMinutes < 24 * 60 * 7) {
            minutes = (deltaMinutes / (60 * 24)).toInt()
            return minutes.toString() + " days ago"
        } else {

            return getDate(originalDate)
        }

    }

    fun changeDateFormt(date: String, toFormat: String): String {
        val format = SimpleDateFormat(toFormat, Locale.US)
        return format.format(date)
    }

    fun getDate(ourDate: String): String {
        var ourDate = ourDate
        try {
            val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            formatter.timeZone = TimeZone.getTimeZone("UTC")
            val value = formatter.parse(ourDate)

            val dateFormatter = SimpleDateFormat(APP_DATE_FORMAT) //this format changeable
            dateFormatter.timeZone = TimeZone.getDefault()
            ourDate = dateFormatter.format(value)

            //Log.d("ourDate", ourDate);
        } catch (e: Exception) {
            ourDate = "00-00-0000 00:00"
        }

        return ourDate
    }

    fun getTime(ourDate: String): String {
        var ourDate = ourDate
        try {
            val formatter = SimpleDateFormat(APP_DATE_TIME_FORMAT, Locale.getDefault())
            val value = formatter.parse(ourDate)

            val dateFormatter =
                SimpleDateFormat(APP_TIME_FORMAT, Locale.getDefault()) //this format changeable
            dateFormatter.timeZone = TimeZone.getDefault()
            ourDate = dateFormatter.format(value)

            //Log.d("ourDate", ourDate);
        } catch (e: Exception) {
            ourDate = "00:00"
        }

        return ourDate
    }


    fun isValidEmail(target: CharSequence): Boolean {
        return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches()
    }

    fun getCircularBitmap(bitmap: Bitmap): Bitmap {
        val output = Bitmap.createBitmap(
            bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(output)

        val color = -0xbdbdbe
        val paint = Paint()
        val rect = Rect(0, 0, bitmap.width, bitmap.height)

        paint.isAntiAlias = true
        canvas.drawARGB(0, 0, 0, 0)
        paint.color = color

        canvas.drawCircle(
            (bitmap.width / 2).toFloat(), (bitmap.height / 2).toFloat(),
            (bitmap.width / 2).toFloat(), paint
        )
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(bitmap, rect, rect, paint)
        return output
    }
    private fun currentDate(): Date {
        val calendar = Calendar.getInstance()
        return calendar.time
    }

    @SuppressLint("SimpleDateFormat")
    fun getTimeAgo(dateString: String): String {

        val date = SimpleDateFormat(APP_DATE_TIME_FORMAT).parse(dateString)


        var time = date.time
        if (time < 1000000000000L) {
            time *= 1000
        }

        val now = currentDate().time
        if (time > now || time <= 0) {
            return "in the future"
        }

        val diff = now - time
        return when {
            diff < MINUTE_MILLIS -> "moments ago"
            diff < 2 * MINUTE_MILLIS -> "a minute ago"
            diff < 60 * MINUTE_MILLIS -> "${diff / MINUTE_MILLIS} minutes ago"
            diff < 2 * HOUR_MILLIS -> "an hour ago"
            diff < 24 * HOUR_MILLIS -> "${diff / HOUR_MILLIS} hours ago"
            diff < 48 * HOUR_MILLIS -> "yesterday"
            else -> "${diff / DAY_MILLIS} days ago"
        }
    }



    fun isValidEmail(email: String): Boolean {
        val expression = Patterns.EMAIL_ADDRESS.toString()
        val pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE)
        val matcher = pattern.matcher(email)
        return matcher.matches()
    }

    fun getUniqueId(otherUserId: String): String {
        val timeStamp = System.currentTimeMillis().toString().plus(otherUserId)
        Log.e(TAG, timeStamp)
        return timeStamp
    }

    fun setSpinnerHeight(spinner: Spinner, activity: Activity?) {
        val popup = Spinner::class.java.getDeclaredField("mPopup")
        popup.isAccessible = true
        val popupWindow = popup.get(spinner) as android.widget.ListPopupWindow
        popupWindow.height = getScreenHeight(activity) / 3
        popupWindow.listView?.isScrollbarFadingEnabled = false
    }

    fun setTint(d: Drawable, color: Int): Drawable {
        d.colorFilter = PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY)
        return d
    }

    fun changeDateFormat(inputPattern: String, outputPattern: String, time: String): String? {
        val inputFormat = SimpleDateFormat(inputPattern, Locale.US)
        val outputFormat = SimpleDateFormat(outputPattern, Locale.US)

        var date: Date? = null
        var str: String? = null

        try {
            date = inputFormat.parse(time)
            str = outputFormat.format(date)
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        return str
    }


    @SuppressLint("SimpleDateFormat")
//By Lokesh
    fun convertLocalDateToUTC(ourDate: String, fromFormat: String, toFormat: String): String {
        var tempOurDate = ourDate
        try {
            val formatter = SimpleDateFormat(fromFormat)
            formatter.timeZone = TimeZone.getTimeZone("UTC")
            val value = formatter.parse(ourDate)

            val dateFormatter = SimpleDateFormat(toFormat) //this format changeable
            dateFormatter.timeZone = TimeZone.getDefault()
            tempOurDate = dateFormatter.format(value)

            //Log.d("OurDate", OurDate);
        } catch (e: Exception) {
            tempOurDate = "00-00-0000 00:00"
        }

        return tempOurDate
    }


    //By Lokesh
    fun convertUTCtoLocalDate(date: String, fromFormat: String, toFormat: String): String {
        var dateInUTC = ""
        try {

            val sourceFormat = SimpleDateFormat(toFormat, Locale.getDefault())
            val outFormatter = SimpleDateFormat(fromFormat, Locale.getDefault())
            outFormatter.timeZone = TimeZone.getTimeZone("UTC")
            val parsed = sourceFormat.parse(date)
            dateInUTC = outFormatter.format(parsed)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        // => Date is in UTC now
        return dateInUTC
    }


    fun getMimeType(fileUrl: String): String {
        var mimeType = ""
        val extension = MimeTypeMap.getFileExtensionFromUrl(fileUrl)
        mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)!!
        return mimeType
    }

    fun sharePost(context: Context, path: String) {
        val sharingIntent = Intent(Intent.ACTION_SEND);
        val screenshotUri = Uri.parse(path)
        sharingIntent.type = "text/plain";
        sharingIntent.putExtra(
            android.content.Intent.EXTRA_TEXT,
            "This is the text that will be shared."
        );
        sharingIntent.type = "image/png";
        sharingIntent.putExtra(Intent.EXTRA_STREAM, screenshotUri);
        context.startActivity(Intent.createChooser(sharingIntent, "Share image using"));
    }

    @SuppressLint("SimpleDateFormat")
    fun getDateFromString(date: String, fromFormat: String): Date? {
        val formatFrom = SimpleDateFormat(fromFormat)
        var dateMain: Date? = null
        try {
            dateMain = formatFrom.parse(date)
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        return dateMain
    }


    fun formatNumberCompat(rawPhone: String?, countryIso: String = ""): String {

        if (rawPhone == null) return ""

        var countryName = countryIso
        if (countryName.isBlank()) {
            countryName = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Resources.getSystem().configuration.locales[0].country
            } else {
                Resources.getSystem().configuration.locale.country
            }
        }

        var phoneNo = ""
        phoneNo = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            PhoneNumberUtils.formatNumber(rawPhone)
        } else {
            PhoneNumberUtils.formatNumber(rawPhone, countryName)
        }
        return phoneNo
    }


    fun openFileFromServer(context: Context, fileUrl: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(Uri.parse(fileUrl), getMimeType(fileUrl))
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent);
    }

    fun focusOnView(your_scrollview: NestedScrollView, view: View) {

        // View needs a focus
        Handler().post { your_scrollview.scrollTo(0, view.top) }

//         if (!linearLayout.getLocalVisibleRect(scrollBounds)) {
//             Handler().post { your_scrollview.smoothScrollTo(0, linearLayout.top) }
//         }
        //   your_scrollview.post { your_scrollview.scrollTo(0, linearLayout.top) }
    }

    fun convertUNIXTimeStampToDateTime(unixSeconds: Long?): String {

        // convert seconds to milliseconds
        val date = java.util.Date(unixSeconds!! * 1000L)
        // the format of your date
        val sdf = java.text.SimpleDateFormat(SERVER_DATE_TIME_FORMAT, Locale.US)
        // give a timezone reference for formatting (see comment at the bottom)
        sdf.timeZone = java.util.TimeZone.getTimeZone("GMT-4")

        return sdf.format(date)
    }

    fun setTextViewDrawableColor(textView: TextView, color: Int, context: Context) {
        for (drawable in textView.compoundDrawables) {
            if (drawable != null) {
                drawable.colorFilter =
                    PorterDuffColorFilter(
                        ContextCompat.getColor(context, color),
                        PorterDuff.Mode.SRC_IN
                    );
            }
        }
    }


}