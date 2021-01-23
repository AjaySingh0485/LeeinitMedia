package com.pelkinsoft.shopdm.base.constant

object ApiConstants {

    object ApiURL {
        //const val BASE_URL = "http://clientapp.narola.online/pg/Anon/Webservices/"
        const val USER_AGREEMENT = "https://jobit.world/privacy-policy/"

    }

    object FirebaseDatabasePath{
//        const val userDatabasePath = "test/env/users/"
//        const val databasePath = "test/env/"
//        const val convDatabasePath = "test/env/conversations/"
//        const val userConversationDatabasePath="test/env/users/conversations/"


        const val userDatabasePath="employees/"
        const val storeDatabasePath="stores/"
        const val requestDatabasePath="request/"
        const val messageDatabasePath="message/"
    }

    enum class ResponseStatus(val status: String) {
        SUCCESS("success"),
        FAIL("failed"),
    }
    enum class UserType(val userType: Int) {
        NORMAL(1),
        DRIVER(2),
    }


    //Basic status and data
    const val STATUS = "status"
    const val MESSAGE = "message"
    const val DATA = "data"


    //TODO : app security
    const val SECRET_KEY: String = "secret_key"
    const val ACCESS_KEY: String = "access_key"
    const val USER_TOKEN: String = "userToken"
    const val IS_TEST_DATA: String = "is_testdata"
    const val DEVICE_TOKEN: String = "device_token"
    const val DEVICE_TYPE: String = "device_type"

    //ToDo common string
    const val USER_ID: String = "userId"
    const val CARD_ID: String = "cardId"

    //ToDo Login or Registration
    const val EMAIL_ID: String = "email"
    const val PASSWORD: String = "password"


}