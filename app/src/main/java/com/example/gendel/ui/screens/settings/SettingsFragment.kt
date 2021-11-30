package com.example.gendel.ui.screens.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.example.gendel.R
import com.example.gendel.database.USER
import com.example.gendel.database.setEmailToDatabase
import com.example.gendel.database.setFullnameToDatabase
import com.example.gendel.databinding.FragmentSettingsBinding
import com.example.gendel.ui.screens.base.BaseChangeFragment
import com.example.gendel.utilities.*

class SettingsFragment : BaseChangeFragment(R.layout.fragment_settings) {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private var nameChanged = false
    private var emailChanged = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        APP_ACTIVITY.toolbar.title = getString(R.string.title_settings)
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
        binding.profileSettingsChangePasswordRoot.setOnClickListener {
            replaceFragment(ChangePasswordFragment())
        }
        binding.profileSettingsEdittextName.addTextChangedListener(
            AppTextWatcher {
                nameChanged = true
            }
        )
        binding.profileSettingsEdittextEmail.addTextChangedListener(
            AppTextWatcher {
                emailChanged = true
            }
        )
        binding.profileSettingsEdittextEmail.setText(USER.email)
        binding.profileSettingsConfirm.setOnClickListener {
            change()
        }
    }

    private fun change() {
        val name = binding.profileSettingsEdittextName.text.toString()
        val email = binding.profileSettingsEdittextEmail.text.toString()
        if (name.isEmpty() && nameChanged) {
            showToast(getString(R.string.settings_toast_name_is_empty))
        } else if (nameChanged) {
            setFullnameToDatabase(name)
        }
        if (email.isEmpty() && emailChanged) {
            showToast(getString(R.string.settings_toast_email_is_empty))
        } else if (emailChanged) {
            setEmailToDatabase(email)
        }
        APP_ACTIVITY.binding.bottomNavigationMenu.selectedItemId = R.id.ic_profile
    }
}