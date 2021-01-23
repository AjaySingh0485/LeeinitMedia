package com.pelkinsoft.shopdm.request.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.pelkinsoft.shopdm.base.BaseViewModel
import com.pelkinsoft.shopdm.base.constant.ApiConstants
import com.pelkinsoft.shopdm.register.Message
import com.pelkinsoft.shopdm.register.Request
import com.pelkinsoft.shopdm.register.Users


class RequestViewModel() : BaseViewModel() {
    //val hits: MutableLiveData<Hits> = MutableLiveData()
    val arrayRequest: MutableLiveData<MutableList<Request>> = MutableLiveData()
    val arrayRequestTemp: MutableList<Request> = mutableListOf()

    val messageObject: MutableLiveData<Message> = MutableLiveData()
    val requestObject: MutableLiveData<Request> = MutableLiveData()

    val arrayMessageTemp: MutableList<Message> = mutableListOf()
    val arrayMessage: MutableLiveData<MutableList<Message>> = MutableLiveData()

    val success: MutableLiveData<Boolean> = MutableLiveData()
    val message: MutableLiveData<String> = MutableLiveData()


    fun addEventListenerForOpenChat(fireStore: FirebaseFirestore,storeIdentifier:List<String>) {

       for(i in storeIdentifier)
       {
           val docRef = fireStore.collection(ApiConstants.FirebaseDatabasePath.requestDatabasePath)
               .whereArrayContains("shop_identifier", i)
               .whereEqualTo("is_done", false)
           isLoading.value = true

           docRef.addSnapshotListener(object : EventListener<QuerySnapshot> {

               override fun onEvent(
                   snapshots: QuerySnapshot?,
                   e: FirebaseFirestoreException?
               ) {

                   isLoading.value = false

                   if (e != null) {
                       return
                   }

                   for (dc in snapshots!!.documentChanges) {
                       requestObject.value = dc.document.toObject(Request::class.java)

                       //Update last message

                   }
               }
           })
       }

    }


    //    fun getRequestList(
//        storeIdentifier: String,
//        firestore: FirebaseFirestore
//    ) {
//        isLoading.value = true
//        firestore.collection(ApiConstants.FirebaseDatabasePath.requestDatabasePath)
//            .whereArrayContains("shop_identifier",storeIdentifier)
//            .get()
//            .addOnSuccessListener(OnSuccessListener<QuerySnapshot> {
//                if (it.isEmpty) {
//                    isLoading.value = false
//                    arrayRequestTemp.clear()
//                } else {
//                    isLoading.value = false
//                    arrayRequestTemp.clear()
//                    for (doc in it) {
//                        isLoading.value = false
//                        arrayRequestTemp.add(doc.toObject(Request::class.java))
//                    }
//                }
//                arrayRequest.value = arrayRequestTemp
//
//
//            })
//    }
    fun addEventListener(fireStore: FirebaseFirestore) {
        val docRef = fireStore.collection(ApiConstants.FirebaseDatabasePath.messageDatabasePath)
        docRef.addSnapshotListener(object : EventListener<QuerySnapshot> {

            override fun onEvent(
                snapshots: QuerySnapshot?,
                e: FirebaseFirestoreException?
            ) {

                if (e != null) {
                    return
                }

                for (dc in snapshots!!.documentChanges) {
                    messageObject.value = dc.document.toObject(Message::class.java)

                    //Update last message

                }
            }
        })

    }

    fun addEventListenerForCloseChat(fireStore: FirebaseFirestore, storeIdentifier: List<String>) {

       for(i in storeIdentifier)
       {
           val docRef = fireStore.collection(ApiConstants.FirebaseDatabasePath.requestDatabasePath)
               .whereArrayContains("shop_identifier", i)
               .whereEqualTo("is_done", true)
           docRef.addSnapshotListener(object : EventListener<QuerySnapshot> {

               override fun onEvent(
                   snapshots: QuerySnapshot?,
                   e: FirebaseFirestoreException?
               ) {

                   isLoading.value = true
                   if (e != null) {
                       return
                   }

                   for (dc in snapshots!!.documentChanges) {
                       isLoading.value = false
                       requestObject.value = dc.document.toObject(Request::class.java)

                       //Update last message

                   }
               }
           })
       }

    }

    fun uploadImage(
        firestore: FirebaseFirestore
        , uri: Uri, identifier: String
        , messageHashMap: HashMap<String, Any>,
        fileName: String
    ) {

        val storageReference = FirebaseStorage.getInstance().reference

        val ref = storageReference.child("Request/attachments/$identifier/$fileName")
        val uploadTask = ref.putFile(uri)

        isLoading.value = true

        val urlTask =
            uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                if (!task.isSuccessful) {
                    task.exception.let {
                        throw it!!
                    }
                }
                return@Continuation ref.downloadUrl
            }).addOnCompleteListener { task ->
                isLoading.value = false

                if (task.isSuccessful) {
                    val downloadUri = task.result
                    updateUserDataToDb(
                        firestore
                        , downloadUri.toString(), messageHashMap
                    )
                } else {
                    isLoading.value = false

                }
            }.addOnFailureListener {
                isLoading.value = false

            }
    }

    fun updateUserDataToDb(
        firestore: FirebaseFirestore,
        imageUri: String,
        message: HashMap<String, Any>
    ) {
        if (!imageUri.isNullOrBlank()) {
            message["thumbnial_string"] = imageUri
        }
        isLoading.value = true
        firestore.collection(ApiConstants.FirebaseDatabasePath.messageDatabasePath)
            .add(message)
            .addOnSuccessListener {
                isLoading.value = false
                success.value = true


            }
            .addOnFailureListener(OnFailureListener
            {
                isLoading.value = false
            })
    }

    fun sendMessage(
        firestore: FirebaseFirestore,
        identifier: String,
        message: HashMap<String, Any>
    ) {

        // isLoading.postValue(true)
        firestore.collection(ApiConstants.FirebaseDatabasePath.messageDatabasePath)
            .document(identifier)
            .set(message)
            .addOnSuccessListener {

                // isLoading.postValue(true)

                isLoading.value = false
                success.value = true
            }

            .addOnFailureListener(OnFailureListener
            {
                isLoading.value = false
                success.value = false
            })
    }


    fun getMessageList(
        requestIdentifier: String,
        firestore: FirebaseFirestore
    ) {

        isLoading.value = true
        firestore.collection(ApiConstants.FirebaseDatabasePath.messageDatabasePath)
            .whereEqualTo("request_identifier", requestIdentifier)
            .get()
            .addOnSuccessListener(OnSuccessListener<QuerySnapshot> {
                arrayMessageTemp.clear()
                for (doc in it) {
                    isLoading.value = false
                    arrayMessageTemp.add(doc.toObject(Message::class.java))
                }
                isLoading.value = false
                arrayMessage.value = arrayMessageTemp
            })
    }


}

