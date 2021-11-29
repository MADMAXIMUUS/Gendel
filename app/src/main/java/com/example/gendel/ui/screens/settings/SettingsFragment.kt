package com.example.gendel.ui.screens.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.example.gendel.R
import com.example.gendel.database.AUTH
import com.example.gendel.database.USER
import com.example.gendel.database.setEmailToDatabase
import com.example.gendel.database.setFullnameToDatabase
import com.example.gendel.databinding.FragmentSettingsBinding
import com.example.gendel.ui.screens.base.BaseChangeFragment
import com.example.gendel.utilities.APP_ACTIVITY
import com.example.gendel.utilities.setBottomNavigationBarColor
import com.example.gendel.utilities.showToast

class SettingsFragment : BaseChangeFragment(R.layout.fragment_settings) {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    var nameIsChanged: Boolean = false
    var emailIsChanged: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        APP_ACTIVITY.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
        setBottomNavigationBarColor(R.color.white)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        initFields()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initFields() {
        binding.profileSettingsEdittextName.setText(USER.name)
        binding.profileSettingsEdittextEmail.setText(USER.email)
    }

    fun change() {
        val name = binding.profileSettingsEdittextName.text.toString()
        val email = binding.profileSettingsEdittextEmail.text.toString()
        if (name.isEmpty()) {
            showToast(getString(R.string.settings_toast_name_is_empty))
        } else {
            setFullnameToDatabase(name) {
                nameIsChanged = true
            }
        }
        if (email.isEmpty()) {
            showToast(getString(R.string.settings_toast_email_is_empty))
        } else {
            setEmailToDatabase(name) {
                emailIsChanged = true
            }
        }
        if (nameIsChanged || emailIsChanged) {
            APP_ACTIVITY.supportFragmentManager.popBackStack()
        }
    }
}