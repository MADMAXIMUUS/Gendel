package com.example.gendel.ui.screens.register

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.Fragment
import com.example.gendel.R
import com.example.gendel.database.*
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
        val window = APP_ACTIVITY.window
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.navigationBarColor = APP_ACTIVITY.resources.getColor(R.color.blue_pink)
        if (Build.VERSION.SDK_INT >= 29)
            window.isNavigationBarContrastEnforced = true
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
                        showToast("Добро пожаловать. Регистрация прошла успешно. Подтвердите email")
                        restartActivity()
                    }
                else
                    showToast("Пароли не совпадают")
            } else {
                showToast("Не все поля заполнены")
            }
        }
        binding.registerSignInTextViewButtonLogin.setOnClickListener {
            replaceFragment(RegisterLogInFragment(), false)
        }
    }
}
