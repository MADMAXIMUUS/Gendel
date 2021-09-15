package com.example.gendel.ui.screens.bill

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import android.widget.PopupWindow
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.gendel.R
import com.example.gendel.databinding.FragmentNewBillBinding
import com.example.gendel.ui.screens.base.BaseFragment
import com.example.gendel.utilities.APP_ACTIVITY
import com.mynameismidori.currencypicker.CurrencyPicker
import com.mynameismidori.currencypicker.CurrencyPickerListener

class NewBillFragment : BaseFragment(R.layout.fragment_new_bill) {

    private var _binding: FragmentNewBillBinding? = null
    private val binding get() = _binding!!
    private lateinit var customView: View
    private lateinit var popupWindow: PopupWindow

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentNewBillBinding.inflate(inflater, container, false)
        APP_ACTIVITY.toolbar.title = "Новое объявление"
        customView = layoutInflater.inflate(R.layout.end_date_picker, null)
        initFunc()
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initFunc() {
        binding.newBillEndDateRoot.setOnClickListener {
            popupWindow = PopupWindow(
                customView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            popupWindow.elevation = 10f
            popupWindow.isOutsideTouchable = true
            popupWindow.showAsDropDown(
                binding.newBillEndDateRoot, 20, 0,
                Gravity.CENTER
            )
            var date = customView.findViewById<CalendarView>(R.id.calendar).date.toString()
            customView.findViewById<CalendarView>(R.id.calendar)
                .setOnDateChangeListener { _, year, month, dayOfMonth ->
                    date = if (dayOfMonth < 10 && month < 10)
                        "0$dayOfMonth.0${month + 1}.$year"
                    else if (dayOfMonth < 10 && month > 10)
                        "0$dayOfMonth.${month + 1}.$year"
                    else if ((dayOfMonth > 10 && month < 10))
                        "$dayOfMonth.0${month + 1}.$year"
                    else
                        "$dayOfMonth.${month + 1}.$year"
                }
            customView.findViewById<ConstraintLayout>(R.id.calendar_button_confirm)
                .setOnClickListener {
                    binding.newBillEndDateTitle.text = date
                    popupWindow.dismiss()
                }
            customView.findViewById<ConstraintLayout>(R.id.calendar_button_cancel)
                .setOnClickListener {
                    popupWindow.dismiss()
                }
        }
    }
}