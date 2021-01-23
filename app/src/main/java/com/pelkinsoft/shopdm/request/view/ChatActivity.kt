package com.pelkinsoft.shopdm.request.view

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.ImageDecoder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.gson.Gson
import com.pelkinsoft.shopdm.CommonUtility
import com.pelkinsoft.shopdm.R
import com.pelkinsoft.shopdm.base.BaseActivity
import com.pelkinsoft.shopdm.base.constant.ApiConstants
import com.pelkinsoft.shopdm.base.constant.AppConstants
import com.pelkinsoft.shopdm.base.extentions.getViewModel
import com.pelkinsoft.shopdm.base.rxjava.autoDispose
import com.pelkinsoft.shopdm.base.rxjava.throttleClicks
import com.pelkinsoft.shopdm.register.Message
import com.pelkinsoft.shopdm.register.Request
import com.pelkinsoft.shopdm.request.viewmodel.RequestViewModel
import kotlinx.android.synthetic.main.activity_chat.*
import java.text.SimpleDateFormat
import kotlin.Comparator
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import android.provider.MediaStore
import android.net.Uri
import java.io.File
import kotlin.math.floor


class ChatActivity : BaseActivity() {

    private lateinit var viewModel: RequestViewModel
    private var arrayListMessage: MutableList<Message> = mutableListOf()
    private var messageObject: Message? = null
    private var messageAdapter: MessageListAdapter? = null

    private var requestObject: Request? = null
    private var filePath: Uri? = null
    private var fileName: String? = null

    private val PICK_IMAGE_REQUEST = 71

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        initView()
    }

    @SuppressLint("SimpleDateFormat")
    private fun initView() {

        requestObject = intent.getParcelableExtra(AppConstants.UserBundels.BUNDLE_REQUEST)

        setupToolbar()

        viewModel = getViewModel { RequestViewModel() }

        recycleMessage.layoutManager = LinearLayoutManager(this)
        messageAdapter = MessageListAdapter(
            this,
            preference.getUserObjectFromPreference().identifier,
            arrayListMessage
        )
        recycleMessage.adapter = messageAdapter

        viewModel.getMessageList(requestObject?.identifier!!, fireStoreDatabase)


        viewModel.addEventListener(fireStoreDatabase)


        viewModel.arrayMessage.observe(this, Observer { messageList ->
            if (!messageList.isNullOrEmpty()) {
                arrayListMessage.clear()
                arrayListMessage.addAll(
                    ArrayList(messageList)
                        .sortedWith(compareBy {
                            SimpleDateFormat(CommonUtility.APP_DATE_TIME_FORMAT).parse(
                                it.created_date
                            )
                        })
                )

                messageAdapter?.notifyDataSetChanged()
            }
        })

        viewModel.requestObject.observe(this, Observer { request ->

            if (request != null) {
                if (request.identifier == requestObject?.identifier) {
                    linearChat.visibility = View.GONE
                }
            }
        })


        imageSend.throttleClicks().subscribe {

            if (editTextMessage.text.toString().trim() != "") {
                setMessageData()
            }
        }.autoDispose(compositeDisposable)


        viewModel.success.observe(this, Observer { successSend ->
            if (successSend) {
                messageAdapter?.notifyDataSetChanged()
            }
        })


        viewModel.isLoading.observe(this, Observer { isLoding ->
            if (isLoding) {
                progressDialog.show()
            } else {
                progressDialog.dismiss()
            }
        })
        imageAdd.throttleClicks().subscribe {
            chooseImage()
        }.autoDispose(compositeDisposable)



        viewModel.addEventListener(fireStoreDatabase)

    }

    private fun chooseImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK
            && data != null && data.data != null
        ) {
            filePath = data.data
            val file = File(filePath?.path!!)
            setMessageData()
            try {

            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }

    private fun setupToolbar() {
        textTitle.text = requestObject?.message

        imageBack.throttleClicks().subscribe {
            onBackPressed()
        }.autoDispose(compositeDisposable)
    }


    private fun setMessageData() {

        val identifier =
            fireStoreDatabase.collection(ApiConstants.FirebaseDatabasePath.messageDatabasePath)
                .document().id

        val messageHashMap: HashMap<String, Any> = HashMap<String, Any>()
        messageHashMap["created_date"] = CommonUtility.getCurrentDate()
        messageHashMap["identifier"] = identifier
        messageHashMap["message"] = editTextMessage.text.toString().trim()
        messageHashMap["is_read"] = 0

        messageHashMap["sender_identifier"] = preference.getUserObjectFromPreference().identifier
        messageHashMap["receiver_identifier"] = requestObject?.user_identifier!!
        messageHashMap["request_identifier"] = requestObject?.identifier!!

        if (!preference.getUserObjectFromPreference().shop_identifiers.isNullOrEmpty())
            messageHashMap["shop_identifier"] =
                preference.getUserObjectFromPreference().shop_identifiers[0]


        if (filePath != null) {
            messageHashMap["thumbnial_string"] = filePath.toString()
            messageHashMap["message_type"] = 1
        } else {
            messageHashMap["thumbnial_string"] = ""
            messageHashMap["message_type"] = 0

        }
        val gson = Gson()
        val jsonElement = gson.toJsonTree(messageHashMap)
        messageObject = gson.fromJson<Message>(jsonElement, Message::class.java)
        arrayListMessage.add(messageObject!!)
        messageAdapter?.notifyDataSetChanged()
        editTextMessage.setText("")
        recycleMessage.scrollToPosition(arrayListMessage.size - 1)
        fileName = (floor(Math.random() * 900000000L).toLong() + 10000000L).toString()
        if (messageObject?.message_type == 1) {

            viewModel.uploadImage(
                fireStoreDatabase,
                filePath!!,
                requestObject?.identifier!!,
                messageHashMap,
                fileName!!
            )

            filePath = null
        } else {
            viewModel.sendMessage(
                fireStoreDatabase,
                identifier,
                messageHashMap
            )
        }


    }
}
