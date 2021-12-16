package com.example.gendel.ui.screens.register

import android.os.Build
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.Fragment
import com.example.gendel.R
import com.example.gendel.database.createNewAccount
import com.example.gendel.databinding.FragmentSigninBinding
import com.example.gendel.utilities.*

class RegisterSignInFragment() : Fragment(R.layout.fragment_signin) {

    private var _binding: FragmentSigninBinding? = null
    private val binding get() = _binding!!
    private lateinit var name: String
    private lateinit var email: String
    private lateinit var password: String
    private lateinit var cpassword: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSigninBinding.inflate(inflater, container, false)
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
        binding.registerButtonSignIn.setOnClickListener {
            name = binding.registerSignInEditTextName.text.toString()
            email = binding.registerSignInEditTextEmail.text.toString()
            password = binding.registerSignInEditTextPassword.text.toString()
            cpassword = binding.registerSignInEditTextConfirmPassword.text.toString()
            if (name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty() && cpassword.isNotEmpty()) {
                if (password == cpassword)
                    createNewAccount(name, email, password){
                        showToast(getString(R.string.signin_welcome))
                        restartActivity()
                    }
                else
                    showToast(getString(R.string.password_mismatch))
            } else {
                showToast(getString(R.string.register_not_all_field))
            }
        }
        binding.registerSignInTextViewButtonLogin.setOnClickListener {
            replaceFragment(RegisterLogInFragment(), false)
        }
        binding.registerSignInEditTextConfirmPasswordShowPassword.setOnClickListener {
            it.visibility = View.GONE
            binding.registerSignInEditTextConfirmPasswordHidePassword.visibility = View.VISIBLE
            binding.registerSignInEditTextConfirmPassword.inputType =
                InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            binding.registerSignInEditTextConfirmPassword.setSelection(binding.registerSignInEditTextConfirmPassword.length());
        }
        binding.registerSignInEditTextConfirmPasswordHidePassword.setOnClickListener {
            it.visibility = View.GONE
            binding.registerSignInEditTextConfirmPasswordShowPassword.visibility = View.VISIBLE
            binding.registerSignInEditTextConfirmPassword.inputType =
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            binding.registerSignInEditTextConfirmPassword.setSelection(binding.registerSignInEditTextConfirmPassword.length());
        }
        binding.registerSignInEditTextPasswordShowPassword.setOnClickListener {
            it.visibility = View.GONE
            binding.registerSignInEditTextPasswordHidePassword.visibility = View.VISIBLE
            binding.registerSignInEditTextPassword.inputType =
                InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            binding.registerSignInEditTextPassword.setSelection(binding.registerSignInEditTextPassword.length());
        }
        binding.registerSignInEditTextPasswordHidePassword.setOnClickListener {
            it.visibility = View.GONE
            binding.registerSignInEditTextPasswordShowPassword.visibility = View.VISIBLE
            binding.registerSignInEditTextPassword.inputType =
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            binding.registerSignInEditTextPassword.setSelection(binding.registerSignInEditTextPassword.length());
        }
    }
}
