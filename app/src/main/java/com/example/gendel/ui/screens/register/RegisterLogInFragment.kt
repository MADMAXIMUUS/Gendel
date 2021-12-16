package com.example.gendel.ui.screens.register

import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.Fragment
import com.example.gendel.R
import com.example.gendel.database.logInAccount
import com.example.gendel.databinding.FragmentLoginBinding
import com.example.gendel.utilities.*

class RegisterLogInFragment : Fragment(R.layout.fragment_login) {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var email: String
    private lateinit var password: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        APP_ACTIVITY.toolbar.visibility = View.GONE
        APP_ACTIVITY.binding.verificationText.visibility = View.GONE
        APP_ACTIVITY.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
        setBottomNavigationBarColor(R.color.blue_pink)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onStart() {
        super.onStart()
        binding.registerButtonLogin.setOnClickListener {
            email = binding.registerLoginEditTextEmail.text.toString()
            password = binding.registerLoginEditTextPassword.text.toString()
            if (email.isNotEmpty() && password.isNotEmpty()) {
                logInAccount(email, password){
                    showToast(getString(R.string.welcome))
                    restartActivity()
                }
            } else {
                showToast(getString(R.string.register_not_all_field))
            }
        }
        binding.registerLoginTextViewButtonSignIn.setOnClickListener {
            replaceFragment(RegisterSignInFragment(), false)
        }
        binding.registerLoginEditTextPasswordShowPassword.setOnClickListener {
            it.visibility = View.GONE
            binding.registerLoginEditTextPasswordHidePassword.visibility = View.VISIBLE
            binding.registerLoginEditTextPassword.inputType =
                InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            binding.registerLoginEditTextPassword.setSelection(binding.registerLoginEditTextPassword.length());
        }
        binding.registerLoginEditTextPasswordHidePassword.setOnClickListener {
            it.visibility = View.GONE
            binding.registerLoginEditTextPasswordShowPassword.visibility = View.VISIBLE
            binding.registerLoginEditTextPassword.inputType =
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            binding.registerLoginEditTextPassword.setSelection(binding.registerLoginEditTextPassword.length());
        }
    }
}