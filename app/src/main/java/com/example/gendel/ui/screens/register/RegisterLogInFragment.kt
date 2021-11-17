package com.example.gendel.ui.screens.register

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.Fragment
import com.example.gendel.R
import com.example.gendel.database.AUTH
import com.example.gendel.database.logInAccount
import com.example.gendel.databinding.FragmentLoginBinding
import com.example.gendel.utilities.APP_ACTIVITY
import com.example.gendel.utilities.replaceFragment
import com.example.gendel.utilities.restartActivity
import com.example.gendel.utilities.showToast
import com.google.android.gms.common.api.Api


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
        binding.registerButtonLogin.setOnClickListener {
            email = binding.registerLoginEditTextEmail.text.toString()
            password = binding.registerLoginEditTextPassword.text.toString()
            if (email.isNotEmpty() && password.isNotEmpty()) {
                logInAccount(email, password){
                    showToast("Добро пожаловать")
                    restartActivity()
                }
            } else {
                showToast("Не все поля заполнены")
            }
        }
        binding.registerLoginTextViewButtonSignIn.setOnClickListener {
            replaceFragment(RegisterSignInFragment(), false)
        }
    }
}