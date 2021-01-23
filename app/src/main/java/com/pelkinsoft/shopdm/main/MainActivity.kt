package com.pelkinsoft.shopdm.main

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.QuerySnapshot
import com.pelkinsoft.shopdm.R
import com.pelkinsoft.shopdm.base.BaseActivity
import com.pelkinsoft.shopdm.base.constant.ApiConstants
import com.pelkinsoft.shopdm.register.Users
import com.pelkinsoft.shopdm.register.view.LoginActivity
import com.pelkinsoft.shopdm.request.view.CloseFragment
import com.pelkinsoft.shopdm.request.view.HomeFragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.custom_navigation_layout.*
import kotlinx.android.synthetic.main.custom_toolbar_layout_menu.*


class MainActivity : BaseActivity() {

    private var navigationAdapter: NavigationAdapter? = null
    private var arrayNavigation = mutableListOf<NavigationOptionModel>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initView()
    }

    private fun initView() {

        recycleNavigation.layoutManager = LinearLayoutManager(this)

        navigationAdapter = NavigationAdapter(arrayNavigation).apply {
            itemClick.subscribe { option ->
                setSelectedItem(option.selectedPosition)
                manageNavigationClick(option)
            }
        }
        recycleNavigation.adapter = navigationAdapter
        manageNavigationDrawer()

        setHomeFragment()
    }

    private fun setSelectedItem(selectedPosition: Int) {
        for (option in arrayNavigation) {
            option.optionSelected = false
        }

        arrayNavigation[selectedPosition].optionSelected = true

        navigationAdapter?.notifyDataSetChanged()


    }

    private fun addNavigationOption() {

        var navigationOption =
            NavigationOptionModel(getString(R.string.M_HOME), 0)
        arrayNavigation.add(navigationOption)

        navigationOption =
            NavigationOptionModel(getString(R.string.M_CLOSE_CHAT), 0)
        arrayNavigation.add(navigationOption)


        navigationOption =
            NavigationOptionModel(getString(R.string.M_LOGOUT), 0)
        arrayNavigation.add(navigationOption)


    }

    private fun manageNavigationDrawer() {

        textName.text = "Shop Menu"

        setSupportActionBar(toolbarMenu)


        val toggle = ActionBarDrawerToggle(
            this,
            drawerMain,
            toolbarMenu,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerMain.addDrawerListener(toggle)
        toggle.syncState()

        imageBack.setOnClickListener {
            if (drawerMain.isDrawerOpen(GravityCompat.START)) {
                drawerMain.closeDrawer(GravityCompat.START)
            } else {
                drawerMain.openDrawer(navigationView, true)
            }

        }

        addNavigationOption()

        //Add 1st List
        recycleNavigation.layoutManager = LinearLayoutManager(this)

        navigationAdapter = NavigationAdapter(arrayNavigation).apply {
            itemClick.subscribe { option ->
                setSelectedItem(option.selectedPosition)
                manageNavigationClick(option)
            }
        }
        recycleNavigation.adapter = navigationAdapter

    }

    private fun manageNavigationClick(option: NavigationOptionModel) {
        if (drawerMain.isDrawerOpen(GravityCompat.START)) {
            drawerMain.closeDrawer(GravityCompat.START)
        }
        when (option.optionName) {
            getString(R.string.M_HOME) -> {
                if (!checkIfFragmentVisible(getString(R.string.M_HOME)))
                    setHomeFragment()
            }
            getString(R.string.M_CLOSE_CHAT) -> {
                if (!checkIfFragmentVisible(getString(R.string.M_CLOSE_CHAT)))
                    supportFragmentManager.beginTransaction().replace(
                        R.id.frameMain,
                        CloseFragment.newInstance("", ""),
                        getString(R.string.M_CLOSE_CHAT)
                    ).commit()

            }

            getString(R.string.M_LOGOUT) -> {
                auth.signOut()
                preference.removeUserPreference(this)
                preference.clearPreference()
                startActivity(Intent(this, LoginActivity::class.java))
            }

        }

    }

    @SuppressLint("CheckResult")
    private fun setHomeFragment() {
        supportFragmentManager.beginTransaction().replace(
            R.id.frameMain,
            HomeFragment.newInstance("", ""),
            getString(R.string.M_HOME)
        ).commit()
    }

    private fun checkIfFragmentVisible(id: String): Boolean {
        val checkFragment = supportFragmentManager.findFragmentByTag(id)
        return checkFragment != null && checkFragment.isVisible
    }


}
