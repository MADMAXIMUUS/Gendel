package com.example.gendel.ui.screens.settings

import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.example.gendel.R
import com.example.gendel.database.AUTH
import com.example.gendel.databinding.FragmentChangePasswordBinding
import com.example.gendel.ui.screens.base.BaseChangeFragment
import com.example.gendel.utilities.APP_ACTIVITY
import com.example.gendel.utilities.setBottomNavigationBarColor
import com.example.gendel.utilities.showToast

class ChangePasswordFragment : BaseChangeFragment(R.layout.fragment_change_password) {
    private var _binding: FragmentChangePasswordBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChangePasswordBinding.inflate(inflater, container, false)
        APP_ACTIVITY.toolbar.title = getString(R.string.title_change_password)
        APP_ACTIVITY.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
        setBottomNavigationBarColor(R.color.white)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        initFields()
    }

    private fun initFields() {
        binding.changePasswordConfirmNewPasswordShowPassword.setOnClickListener {
            it.visibility = View.GONE
            binding.changePasswordConfirmNewPasswordHidePassword.visibility = View.VISIBLE
            binding.changePasswordConfirmNewPassword.inputType =
                InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            binding.changePasswordConfirmNewPassword
                .setSelection(binding.changePasswordConfirmNewPassword.length());
        }
        binding.changePasswordConfirmNewPasswordHidePassword.setOnClickListener {
            it.visibility = View.GONE
            binding.changePasswordConfirmNewPasswordShowPassword.visibility = View.VISIBLE
            binding.changePasswordConfirmNewPassword.inputType =
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            binding.changePasswordConfirmNewPassword
                .setSelection(binding.changePasswordConfirmNewPassword.length());
        }

        binding.changePasswordNewPasswordShowPassword.setOnClickListener {
            it.visibility = View.GONE
            binding.changePasswordNewPasswordHidePassword.visibility = View.VISIBLE
            binding.changePasswordNewPassword.inputType =
                InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            binding.changePasswordNewPassword
                .setSelection(binding.changePasswordNewPassword.length());
        }
        binding.changePasswordNewPasswordHidePassword.setOnClickListener {
            it.visibility = View.GONE
            binding.changePasswordNewPasswordShowPassword.visibility = View.VISIBLE
            binding.changePasswordNewPassword.inputType =
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            binding.changePasswordNewPassword
                .setSelection(binding.changePasswordNewPassword.length());
        }

        binding.changePasswordConfirm.setOnClickListener {
            change()
        }
    }

    private fun change() {
        val newPassword = binding.changePasswordNewPassword.text.toString()
        val confirmNewPassword = binding.changePasswordConfirmNewPassword.text.toString()
        if (newPassword == confirmNewPassword)
            AUTH.currentUser?.updatePassword(newPassword)
                ?.addOnSuccessListener {
                    APP_ACTIVITY.supportFragmentManager.popBackStack()
                }
                ?.addOnFailureListener { showToast(it.message.toString()) }
        else
            showToast(getString(R.string.password_mismatch))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}