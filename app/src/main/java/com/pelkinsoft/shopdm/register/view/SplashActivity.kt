package com.pelkinsoft.shopdm.register.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.pelkinsoft.shopdm.R
import com.pelkinsoft.shopdm.base.BaseActivity
import com.pelkinsoft.shopdm.main.MainActivity

class SplashActivity : BaseActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        initView()
    }

    private fun initView() {
        Handler().postDelayed({
            if (preference.getRememberMe())
            {
                startActivity(Intent(this, MainActivity::class.java))


            }else
            {
                startActivity(Intent(this, LoginActivity::class.java))

            }
        }, 3000)
    }
}
