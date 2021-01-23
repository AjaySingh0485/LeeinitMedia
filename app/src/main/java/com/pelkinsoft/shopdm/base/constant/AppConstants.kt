package com.pelkinsoft.shopdm.base.constant

object AppConstants {

    object UserBundels {
        /*All Bundles*/
        const val BUNDLE_USER_DATA = "user_data"
        const val BUNDLE_USER_NAME = "user_name"
        const val BUNDLE_REQUEST = "conversation"
        const val BUNDLE_IS_FROM_NEW = "is_from_new"
        const val BUNDLE_EXPERIENCE = "experience_data"
        const val BUNDLE_EDUCATION = "education_data"
        const val BUNDLE_LANGUAGE = "language_data"
        const val BUNDLE_JOB = "job_data"
        const val BUNDLE_JOB_ID = "job_id"
        const val BUNDLE_USER_ID = "user_id"
    }

    const val DEVICE_TYPE: Int = 2

    object BroadCastConstant {

        const val DELETE_BROADCAST = "delete_broadcast"


    }

    object RequestCode {
        const val EXP_CODE = 1000
        const val EDU_CODE = 1001
        const val LAN_CODE = 1002

    }


    enum class DeliveryType(val deliveryType: String) {
        EXPENSIVE("1"),
        NORMAL("0")
    }

    enum class Provider(val provider: String) {
        EMAIL("email"),
        FACEBOOK("facebook"),
        APPLE("apple"),
        LINKEDIN("linkedin"),
        PHONENUMBER("phone_number")
    }
}