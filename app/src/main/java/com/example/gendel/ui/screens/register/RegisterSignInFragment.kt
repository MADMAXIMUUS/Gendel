package com.example.gendel.ui.screens.register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
                    createNewAccount()
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

    private fun createNewAccount() {
        AUTH.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val uid = AUTH.currentUser?.uid.toString()
                val dateMap = mutableMapOf<String, Any>()
                dateMap[CHILD_ID] = uid
                dateMap[CHILD_NAME] = name
                dateMap[CHILD_EMAIL] = email
                dateMap[CHILD_PASSWORD] = password
                REF_DATABASE_ROOT.child(NODE_USERS).child(uid)
                    .addListenerForSingleValueEvent(AppValueEventListener {
                        REF_DATABASE_ROOT.child(NODE_USERS).child(uid).updateChildren(dateMap)
                            .addOnSuccessListener {
                                showToast("Добро пожаловать")
                                restartActivity()
                            }
                            .addOnFailureListener { showToast("Ошибка входа в аккаунт") }
                    })
            } else showToast("Ошибка создания аккаунта")
        }
    }
}