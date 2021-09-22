package com.example.gendel.ui.screens.bill

import android.app.Instrumentation
import android.os.Bundle
import android.view.*
import android.widget.CalendarView
import android.widget.PopupWindow
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.gendel.R
import com.example.gendel.database.*
import com.example.gendel.databinding.FragmentNewBillBinding
import com.example.gendel.ui.screens.base.BaseFragment
import com.example.gendel.ui.screens.main_list.MainListFragment
import com.example.gendel.utilities.APP_ACTIVITY
import com.example.gendel.utilities.replaceFragment
import com.example.gendel.utilities.showToast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NewBillFragment : BaseFragment(R.layout.fragment_new_bill) {

    private var _binding: FragmentNewBillBinding? = null
    private val binding get() = _binding!!
    private lateinit var customView: View
    private lateinit var popupWindow: PopupWindow
    private var isDateChosen = false
    private lateinit var inst: Instrumentation

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
            val calendar = customView.findViewById<CalendarView>(R.id.calendar)
            var date = calendar.date.toString()
            calendar.minDate = calendar.date
            customView.findViewById<CalendarView>(R.id.calendar)
                .setOnDateChangeListener { _, year, month, dayOfMonth ->
                    date = if (dayOfMonth < 10 && month < 10)
                        "0$dayOfMonth.0${month + 1}.$year"
                    else if (dayOfMonth < 10 && month >= 10)
                        "0$dayOfMonth.${month + 1}.$year"
                    else if ((dayOfMonth >= 10 && month < 10))
                        "$dayOfMonth.0${month + 1}.$year"
                    else
                        "$dayOfMonth.${month + 1}.$year"
                }
            customView.findViewById<ConstraintLayout>(R.id.calendar_button_confirm)
                .setOnClickListener {
                    binding.newBillEndDateTitle.text = date
                    isDateChosen = true
                    popupWindow.dismiss()
                }
            customView.findViewById<ConstraintLayout>(R.id.calendar_button_cancel)
                .setOnClickListener {
                    popupWindow.dismiss()
                }
        }
        binding.newBillButtonCreate.setOnClickListener {
            val storeName = binding.newBillEdittextStoreName.text.toString()
            val date = binding.newBillEndDateTitle.text.toString()
            val cost = binding.newBillCostEdittext.text.toString()
            if (storeName.isEmpty())
                showToast("Введите название магазина")
            else if (!isDateChosen)
                showToast("Выберите дату окончания")
            else if (cost.isEmpty())
                showToast("Введите стоимость доставки")
            else {
                createBillAndPushToDatabase(storeName, date, cost) {
                    CoroutineScope(Dispatchers.IO).launch {
                        inst = Instrumentation()
                        inst.sendKeyDownUpSync(KeyEvent.KEYCODE_BACK)
                    }
                }
            }
        }
    }
}