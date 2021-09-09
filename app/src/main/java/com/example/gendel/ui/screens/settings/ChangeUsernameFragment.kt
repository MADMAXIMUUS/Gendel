package com.example.gendel.ui.screens.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.gendel.R
import com.example.gendel.database.*
import com.example.gendel.databinding.FragmentChangeUsernameBinding
import com.example.gendel.ui.screens.base.BaseChangeFragment
import com.example.gendel.utilities.*

class ChangeUsernameFragment : BaseChangeFragment(R.layout.fragment_change_username) {

    private var _binding: FragmentChangeUsernameBinding? = null
    private val binding get() = _binding!!
    lateinit var newUsername: String
    lateinit var oldUserName: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChangeUsernameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        binding.settingsInputUsername.setText(USER.username)
        oldUserName = binding.settingsInputUsername.text.toString()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun change() {
        newUsername = binding.settingsInputUsername.text.toString()
        if (newUsername.isEmpty()) {
            USER.username = USER.id
            replaceFragment(ProfileFragment())
        } else {
            if (oldUserName == newUsername) {
                replaceFragment(ProfileFragment())
            } else {
                REF_DATABASE_ROOT.child(NODE_USERNAMES)
                    .addListenerForSingleValueEvent(AppValueEventListener {
                        if (it.hasChild(newUsername)) {
                            showToast("Такой пользователь уже существует")
                        } else {
                            changeUsername()
                        }
                    })
            }
        }
    }

    private fun changeUsername() {
        REF_DATABASE_ROOT.child(NODE_USERNAMES).child(newUsername).setValue(CURRENT_UID)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    updateCurrentUsername(newUsername)
                }
            }
    }
}