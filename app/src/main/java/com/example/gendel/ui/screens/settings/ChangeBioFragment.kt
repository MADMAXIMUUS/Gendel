package com.example.gendel.ui.screens.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.gendel.R
import com.example.gendel.database.*
import com.example.gendel.databinding.FragmentChangeBioBinding
import com.example.gendel.ui.screens.base.BaseChangeFragment
import com.example.gendel.utilities.*

class ChangeBioFragment : BaseChangeFragment(R.layout.fragment_change_bio) {
    private var _binding: FragmentChangeBioBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChangeBioBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        binding.settingsInputBio.setText(USER.bio)
        binding.settingsInputBio.addTextChangedListener(AppTextWatcher{
            binding.bioCount.text = (70 - binding.settingsInputBio.text.toString().length).toString()
        })
        binding.bioCount.text = (70 - binding.settingsInputBio.text.toString().length).toString()
    }

    override fun change() {
        super.change()
        val newBio = binding.settingsInputBio.text.toString()
        setBioToDatabase(newBio)
    }
}