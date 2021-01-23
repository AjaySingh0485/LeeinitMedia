package com.pelkinsoft.shopdm.register.view

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.pelkinsoft.shopdm.main.MainActivity
import com.pelkinsoft.shopdm.R
import com.pelkinsoft.shopdm.base.BaseActivity
import com.pelkinsoft.shopdm.base.constant.ApiConstants
import com.pelkinsoft.shopdm.base.extentions.getViewModel
import com.pelkinsoft.shopdm.base.rxjava.autoDispose
import com.pelkinsoft.shopdm.base.rxjava.throttleClicks
import com.pelkinsoft.shopdm.register.Users
import com.pelkinsoft.shopdm.register.viewmodel.LoginViewModel
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : BaseActivity() {

    private lateinit var loginViewModel: LoginViewModel

    private var userObject: Users? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        initView()
    }

    private fun initView() {
        loginViewModel = getViewModel {
            LoginViewModel()
        }
        textSignIn.throttleClicks().subscribe {
            startActivity(Intent(this, LoginActivity::class.java))
        }.autoDispose(compositeDisposable)

        textSignUp.throttleClicks().subscribe {
            registerClick()
        }.autoDispose(compositeDisposable)


        loginViewModel.isLoading.observe(this, Observer { isLoding ->
            if (isLoding) {
                progressDialog.show()
            } else {
                progressDialog.dismiss()
            }
        })


        loginViewModel.message.observe(this, Observer { message ->
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

        })

        loginViewModel.loginData.observe(this, Observer { users ->
            preference.setUserObjectToPreference(users)
            preference.setRememberMe(true)

            startActivity(Intent(this, MainActivity::class.java))
        })
    }


    private fun registerClick() {
        // Reset errors.
        editEmail.error = null
        editName.error = null


        // Store values at the time of the login attempt.
        val emailStr = editEmail.text.toString()
        val firstNameStr = editName.text.toString()

        // Store values at the time of the login attempt.
        val passwordStr = editPassword.text.toString()
        var cancel = false
        var focusView: View? = null


        // Check for a valid email address.
        if (TextUtils.isEmpty(firstNameStr)) {
            editName.error = getString(R.string.L_NAME)
            focusView = editName
            cancel = true
        } else if (TextUtils.isEmpty(passwordStr)) {
            editPassword.error = getString(R.string.S_ENTER_PASSWORD)
            focusView = editPassword
            cancel = true
        }

        if (TextUtils.isEmpty(emailStr)) {
            editEmail.error = getString(R.string.S_ENTER_EMAIL)
            focusView = editEmail
            cancel = true
        }



        if (cancel) {
            focusView?.requestFocus()
        } else {
            adduserDataToFirebase()
        }
    }

    private fun adduserDataToFirebase() {
        val identifier =
            fireStoreDatabase.collection(ApiConstants.FirebaseDatabasePath.userDatabasePath)
                .document().id

        val shopIdentifierArray: ArrayList<String> = ArrayList()
        shopIdentifierArray.add(editShopIdentifier.text.toString().trim())

        val userTemp = HashMap<String, Any>()
        userTemp["identifier"] = identifier
        userTemp["name"] = editName.text.toString().trim()
        userTemp["email"] = editName.text.toString().trim()
        userTemp["password"] = editPassword.text.toString().trim()
        userTemp["shop_identifier"] = editShopIdentifier.text.toString().trim()
        userTemp["shop_identifiers"] =shopIdentifierArray
        userTemp["is_active"] =true

        val shopIdentifier = editShopIdentifier.text.toString().trim();
        val email = editEmail.text.toString().trim();
        val password = editPassword.text.toString().trim();

//        val gson = Gson()
//        val jsonElement = gson.toJsonTree(userTemp)
//        userObject = gson.fromJson<Users>(jsonElement, Users::class.java)

        loginViewModel.addDataInFirebase(
            identifier,
            shopIdentifier,
            email,
            password,
            fireStoreDatabase,
            userTemp,
            auth
        )

    }

    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()

    }

}
