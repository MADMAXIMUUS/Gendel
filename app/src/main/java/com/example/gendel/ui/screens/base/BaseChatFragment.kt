package com.example.gendel.ui.screens.base

import android.view.View
import androidx.fragment.app.Fragment
import com.example.gendel.R
import com.example.gendel.utilities.APP_ACTIVITY

open class BaseChatFragment(layout: Int) : Fragment(layout) {

    override fun onStart() {
        super.onStart()
        APP_ACTIVITY.binding.bottomNavigationMenu.visibility = View.GONE
        enableDrawer()
    }

    override fun onDestroy() {
        super.onDestroy()
        APP_ACTIVITY.binding.bottomNavigationMenu.visibility = View.VISIBLE
        APP_ACTIVITY.binding.bottomNavigationMenu.selectedItemId = R.id.ic_home
        disableDrawer()
    }

    private fun disableDrawer() {
        APP_ACTIVITY.supportActionBar?.setDisplayHomeAsUpEnabled(false)
    }

    private fun enableDrawer() {
        APP_ACTIVITY.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        APP_ACTIVITY.toolbar.setNavigationOnClickListener {
            APP_ACTIVITY.supportFragmentManager.popBackStack()
        }
    }
}