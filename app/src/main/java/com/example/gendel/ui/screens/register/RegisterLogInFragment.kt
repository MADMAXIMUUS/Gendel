package com.example.gendel.ui.screens.register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.gendel.R
import com.example.gendel.databinding.FragmentLoginBinding
import com.example.gendel.utilities.*
import com.google.firebase.auth.PhoneAuthProvider


class RegisterLogInFragment : Fragment(R.layout.fragment_login) {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var phoneNumber: String
    private lateinit var callBack: PhoneAuthProvider.OnVerificationStateChangedCallbacks

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        APP_ACTIVITY.toolbar.visibility = View.GONE
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /*override fun onStart() {
        super.onStart()
        callBack = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                AUTH.signInWithCredential(credential).addOnCompleteListener {
                    if (it.isSuccessful) {
                        showToast("Добро пожаловать")
                        restartActivity()
                    } else showToast(it.exception?.message.toString())
                }
            }

            override fun onVerificationFailed(p0: FirebaseException) {
                showToast(p0.message.toString())
            }

            override fun onCodeSent(id: String, token: PhoneAuthProvider.ForceResendingToken) {
                replaceFragment(RegisterSingInFragment(phoneNumber, id))
            }
        }
        binding.countryCodePicker.selectedCountryCodeWithPlus
        binding.countryCodePicker.setOnCountryChangeListener {
            binding.countryCode.setText(binding.countryCodePicker.selectedCountryCodeWithPlus)
            binding.registerInputPhoneNumber.requestFocus()
        }
        binding.registerButtonNext.setOnClickListener { sendCode() }
        binding.countryCode.addTextChangedListener(AppTextWatcher{
            if (binding.countryCode.text.isEmpty()){
                binding.countryCode.setText("+")
                binding.countryCode.setSelection(binding.countryCode.text.length)
            }
        })
    }

    private fun sendCode() {
        if (binding.registerInputPhoneNumber.text.toString().isEmpty()) {
            showToast(getString(R.string.register_toast_enter_phone))
        } else {
            authUser()
        }
    }

    private fun authUser() {
        phoneNumber = if (binding.countryCode.text.isNotEmpty()) {
            binding.countryCode.text.toString() +
                    binding.registerInputPhoneNumber.text.toString()
        } else {
            binding.registerInputPhoneNumber.text.toString()
        }
        PhoneAuthProvider.verifyPhoneNumber(
            PhoneAuthOptions.newBuilder(FirebaseAuth.getInstance())
            .setActivity(APP_ACTIVITY)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setCallbacks(callBack)
            .build())
    }*/
}