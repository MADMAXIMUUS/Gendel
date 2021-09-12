package com.example.gendel.ui.screens.register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.gendel.R
import com.example.gendel.database.AUTH
import com.example.gendel.databinding.FragmentLoginBinding
import com.example.gendel.utilities.APP_ACTIVITY
import com.example.gendel.utilities.replaceFragment
import com.example.gendel.utilities.restartActivity
import com.example.gendel.utilities.showToast


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
                logInAccount()
            } else {
                showToast("Не все поля заполнены")
            }
        }
        binding.registerLoginTextViewButtonSignIn.setOnClickListener {
            replaceFragment(RegisterSignInFragment(), false)
        }
    }

    private fun logInAccount() {
        AUTH.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    showToast("Добро пожаловать")
                    restartActivity()
                } else {
                    showToast("Ошибка входа в аккаунт")
                }
            }
    }
}