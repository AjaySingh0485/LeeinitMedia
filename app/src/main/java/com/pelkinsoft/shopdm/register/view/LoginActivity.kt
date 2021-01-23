package com.pelkinsoft.shopdm.register.view

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import com.pelkinsoft.shopdm.main.MainActivity
import com.pelkinsoft.shopdm.R
import com.pelkinsoft.shopdm.base.BaseActivity
import com.pelkinsoft.shopdm.base.extentions.getViewModel
import com.pelkinsoft.shopdm.base.rxjava.autoDispose
import com.pelkinsoft.shopdm.base.rxjava.throttleClicks
import com.pelkinsoft.shopdm.register.viewmodel.LoginViewModel
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : BaseActivity() {

    private lateinit var viewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        initView()
    }

    private fun initView() {
        val currentUser = auth.currentUser

        viewModel = getViewModel {
            LoginViewModel()
        }
        textSignIn.throttleClicks().subscribe {
            loginClick()
        }.autoDispose(compositeDisposable)

        textSignUp.throttleClicks().subscribe {
            startActivity(Intent(this, RegisterActivity::class.java))
        }.autoDispose(compositeDisposable)

        viewModel.success.observe(this, Observer { status ->
            if (status) {
                startActivity(Intent(this, MainActivity::class.java))
            }else
            {
                Toast.makeText(this,getString(R.string.S_LOGIN_FAIL), Toast.LENGTH_SHORT).show()
            }
        })

        viewModel.isLoading.observe(this, Observer { isLoding ->
            if (isLoding) {
                progressDialog.show()
            }else
            {
                progressDialog.dismiss()
            }
        })



        viewModel.loginData.observe(this, Observer { users ->
            preference.setUserObjectToPreference(users)
            preference.setRememberMe(true)
            startActivity(Intent(this, MainActivity::class.java))
        })
    }

    private fun loginClick() {
        // Reset errors.
        editEmail.error = null
        editPassword.error = null


        // Store values at the time of the login attempt.
        val emailStr = editEmail.text.toString()
        val passwordStr = editPassword.text.toString()

        var cancel = false
        var focusView: View? = null


        if (TextUtils.isEmpty(emailStr)) {
            editEmail.error = getString(R.string.S_ENTER_EMAIL)
            focusView = editEmail
            cancel = true

        }


        if (TextUtils.isEmpty(passwordStr)) {
            editPassword.error = getString(R.string.S_ENTER_PASSWORD)
            focusView = editPassword
            cancel = true
        }



        if (cancel) {
            focusView?.requestFocus()
        } else {
            viewModel.checkUserAvailable(emailStr, passwordStr, fireStoreDatabase,auth)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()

    }
}
