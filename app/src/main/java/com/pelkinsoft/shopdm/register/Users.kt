package com.pelkinsoft.shopdm.register

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.database.IgnoreExtraProperties
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.GeoPoint
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Users(
    @Exclude
    var identifier: String ="",
    var email: String = "",
    var name: String = "",
    var shop_identifier: String = "",
    var emp_code:String="",
    var is_active:Boolean=false,
    var shop_identifiers:List<String> = ArrayList()



):Parcelable

@IgnoreExtraProperties
@Parcelize
data class Request(
    var created_date: String = "",
    var identifier: String = "",
    var is_done: String = "",
    var message:String="",
    var user_identifier:String="",
   // var shop_identifier:String,
    var shop_identifier:List<String> = ArrayList()


):Parcelable

@IgnoreExtraProperties
@Parcelize
data class Message(
    var created_date: String = "",
    var message: String="",
    var message_type: Int=1,
    var identifier: String = "",
    var is_read: Int = 0,
    var receiver_identifier:String="",
    var request_identifier:String="",
    var sender_identifier:String="",
    var shop_identifier:String = "",
    var thumbnial_string:String = ""

):Parcelable

