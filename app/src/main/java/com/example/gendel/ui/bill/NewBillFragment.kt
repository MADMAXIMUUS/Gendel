package com.example.gendel.ui.bill

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.gendel.R
import com.example.gendel.databinding.FragmentNewBillBinding
import com.example.gendel.ui.screens.base.BaseFragment
import com.example.gendel.utilities.APP_ACTIVITY

class NewBillFragment : BaseFragment(R.layout.fragment_new_bill) {

    private var _binding: FragmentNewBillBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentNewBillBinding.inflate(inflater, container, false)
        APP_ACTIVITY.binding.bottomNavigationMenuRoot.visibility = View.GONE
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}