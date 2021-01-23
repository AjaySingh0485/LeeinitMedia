package com.pelkinsoft.shopdm.request.view

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager

import com.pelkinsoft.shopdm.R
import com.pelkinsoft.shopdm.base.BaseFragment
import com.pelkinsoft.shopdm.base.constant.AppConstants
import com.pelkinsoft.shopdm.base.extentions.getViewModel
import com.pelkinsoft.shopdm.base.rxjava.autoDispose
import com.pelkinsoft.shopdm.register.Request
import com.pelkinsoft.shopdm.request.viewmodel.RequestViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlin.collections.ArrayList

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class CloseFragment : BaseFragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var arrayRequest: ArrayList<Request> = ArrayList()
    private lateinit var viewModel: RequestViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_home, container, false)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {

        setupToolbar()

        viewModel = getViewModel { RequestViewModel() }

        viewModel.addEventListenerForCloseChat(
            fireStoreDatabase,preference.getUserObjectFromPreference().shop_identifiers

        )

        recycleChat.layoutManager = LinearLayoutManager(activity)
        val requestAdapter = RequestAdapter(arrayRequest)
        recycleChat.adapter = requestAdapter

        requestAdapter.itemClick.subscribe {
            startActivity(
                Intent(activity, ChatActivity::class.java)
                    .putExtra(AppConstants.UserBundels.BUNDLE_REQUEST, it)
            )
        }.autoDispose(compositeDisposable)

        viewModel.requestObject.observe(this, Observer { request ->
            arrayRequest.add(request)
            requestAdapter.notifyDataSetChanged()
            checkForNodata()
        })

        viewModel.isLoading.observe(this, Observer { loading ->
            if (loading) {
                if (arrayRequest.isEmpty()) {
                    progressDialog.show()
                } else {
                    progressDialog.dismiss()
                }
            } else {
                progressDialog.dismiss()
            }
        })
    }

    private fun setupToolbar() {
        activity?.textTitle?.visibility = View.VISIBLE
        activity?.textTitle?.text = "Close Chats"

    }

    private fun checkForNodata() {
        if (arrayRequest.isNullOrEmpty()) {
            textNoData.visibility = View.VISIBLE
            recycleChat.visibility = View.GONE
        } else {
            textNoData.visibility = View.GONE
            recycleChat.visibility = View.VISIBLE
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CloseFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
