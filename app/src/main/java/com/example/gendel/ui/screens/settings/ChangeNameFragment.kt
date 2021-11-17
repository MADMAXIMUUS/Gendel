package com.example.gendel.ui.screens.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.gendel.R
import com.example.gendel.database.*
import com.example.gendel.databinding.FragmentChangeNameBinding
import com.example.gendel.ui.screens.base.BaseChangeFragment
import com.example.gendel.utilities.*

class ChangeNameFragment : BaseChangeFragment(R.layout.fragment_change_name) {
    private var _binding: FragmentChangeNameBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChangeNameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        initFullnameList()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initFullnameList() {
            binding.settingsInputName.setText(USER.name)
    }

    override fun change() {
        val name = binding.settingsInputName.text.toString()
        if (name.isEmpty()){
            showToast(getString(R.string.settings_toast_name_is_empty))
        } else {
            setFullnameToDatabase(name)
        }
    }
}