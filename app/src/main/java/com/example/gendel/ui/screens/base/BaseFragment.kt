package com.example.gendel.ui.screens.base

import android.view.View
import androidx.fragment.app.Fragment
import com.example.gendel.R
import com.example.gendel.utilities.APP_ACTIVITY

open class BaseFragment(layout: Int) : Fragment(layout) {

    override fun onStart() {
        super.onStart()
        APP_ACTIVITY.binding.bottomNavigationMenuRoot.visibility = View.GONE
        APP_ACTIVITY.toolbar.findViewById<View>(R.id.toolbar_search).visibility = View.GONE
        APP_ACTIVITY.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        APP_ACTIVITY.toolbar.setNavigationOnClickListener {
            APP_ACTIVITY.supportFragmentManager.popBackStack()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        APP_ACTIVITY.binding.bottomNavigationMenuRoot.visibility = View.VISIBLE
        APP_ACTIVITY.toolbar.findViewById<View>(R.id.toolbar_search).visibility = View.VISIBLE
        APP_ACTIVITY.supportActionBar?.setDisplayHomeAsUpEnabled(false)
    }
}