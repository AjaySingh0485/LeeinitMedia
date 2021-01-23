package com.pelkinsoft.shopdm.register.viewmodel

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.ktx.Firebase
import com.pelkinsoft.shopdm.base.BaseViewModel
import com.pelkinsoft.shopdm.base.constant.ApiConstants
import com.pelkinsoft.shopdm.register.Users


class LoginViewModel() : BaseViewModel() {
    //val hits: MutableLiveData<Hits> = MutableLiveData()
    val success: MutableLiveData<Boolean> = MutableLiveData()
    val message: MutableLiveData<String> = MutableLiveData()
    val loginData: MutableLiveData<Users> = MutableLiveData()


    fun addDataInFirebase(
        userId: String,
        shopIdentifier: String,
        email: String,
        password: String,
        userRef: FirebaseFirestore,
        user: HashMap<String, Any>,
        auth: FirebaseAuth
    ) {
        checkUserAvailableForStore(userId, shopIdentifier, email, password, userRef, user, auth)

        //checkUserAlreadyAvailable(userId,shopIdentifier,email,password,userRef,user, auth)


    }

    private fun checkUserAvailableForStore(
        userId: String,
        shopIdentifier: String,
        email: String,
        password: String,
        firestore: FirebaseFirestore,
        user: HashMap<String, Any>,
        auth: FirebaseAuth
    ) {
        isLoading.value = true
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val socialRef =
                        firestore.collection(ApiConstants.FirebaseDatabasePath.storeDatabasePath)
                    socialRef.whereEqualTo("identifier", shopIdentifier).get()
                        .addOnSuccessListener(OnSuccessListener<QuerySnapshot> {
                            isLoading.value = false
                            if (!it.isEmpty) {

                                firestore.collection(ApiConstants.FirebaseDatabasePath.userDatabasePath)
                                    .document(userId)
                                    .set(user)
                                    .addOnSuccessListener(OnSuccessListener
                                    {
                                        isLoading.value = false
                                        success.value = true
                                        getUserFromIdentifier(firestore, userId)
                                    })
                                    .addOnFailureListener(OnFailureListener
                                    {
                                        isLoading.value = false
                                        success.value = false
                                    })

                            } else {
                                isLoading.value = false
                                message.value = "Shop identifier is incorrect!!"
                            }
                        })

                } else {
                    isLoading.value = false
                    message.value = "User is already available"
                    success.value = false
                }
            }
    }

    private fun getUserFromIdentifier(firestore: FirebaseFirestore, userId: String) {
        isLoading.value = true
        val loginRef = firestore.collection(ApiConstants.FirebaseDatabasePath.userDatabasePath)
        loginRef.whereEqualTo("identifier", userId).get()
            .addOnSuccessListener(OnSuccessListener<QuerySnapshot> {
                isLoading.value = false
                if (!it.isEmpty) {
                    for (doc in it) {
                        isLoading.value = false
                        val userObject = doc.toObject(Users::class.java)
                        userObject.identifier = doc.id
                        loginData.value = userObject
                        break
                    }
                }
            })
    }

    private fun checkUserAlreadyAvailable(
        userId: String,
        shopIdentifier: String,
        email: String,
        password: String,
        firestore: FirebaseFirestore,
        user: HashMap<String, Any>,
        auth: FirebaseAuth

    ) {

        isLoading.value = true
        val socialRef = firestore.collection(ApiConstants.FirebaseDatabasePath.userDatabasePath)
        socialRef.whereEqualTo("email", email)
            .get()
            .addOnSuccessListener(OnSuccessListener<QuerySnapshot> {
                isLoading.value = false
                if (it.isEmpty) {
                    firestore.collection(ApiConstants.FirebaseDatabasePath.userDatabasePath)
                        .document(userId)
                        .set(user)
                        .addOnSuccessListener(OnSuccessListener
                        {
                            isLoading.value = false
                            success.value = true
                            getUserFromIdentifier(firestore, userId)
                        })
                        .addOnFailureListener(OnFailureListener
                        {
                            isLoading.value = false
                            success.value = false
                        })
                    // checkUserAvailableForStore(userId,shopIdentifier,email,password,firestore,user, auth)
                } else {
                    isLoading.value = false
                    message.value = "User already available"
                }
            })
    }

    fun checkUserAvailable(
        email: String,
        password: String,
        firestore: FirebaseFirestore,
        auth: FirebaseAuth
    ) {
        isLoading.value = true
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val questionRef =
                        firestore.collection(ApiConstants.FirebaseDatabasePath.userDatabasePath)
                    questionRef.whereEqualTo("email", email)
                        .get()
                        .addOnSuccessListener(OnSuccessListener<QuerySnapshot> {
                            if (it.isEmpty) {
                                isLoading.value = false
                                success.value = false

                            } else {

                                for (doc in it) {
                                    isLoading.value = false
                                    val userObject = doc.toObject(Users::class.java)
                                    userObject.identifier = doc.id
                                    loginData.value = userObject
                                    break
                                }
                            }
                        })

                } else {
                    isLoading.value = false
                    message.value = "Authentication failed"
                    success.value = false
                }

                // ...
            }
    }
}


/*
* For get all post list
* @Param:offset(for load more)
* */