package com.example.gendel.ui.screens.bill

import android.annotation.SuppressLint
import android.app.Instrumentation
import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.CalendarView
import android.widget.PopupWindow
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.gendel.R
import com.example.gendel.database.*
import com.example.gendel.databinding.FragmentNewBillBinding
import com.example.gendel.ui.screens.base.BaseFragment
import com.example.gendel.utilities.APP_ACTIVITY
import com.example.gendel.utilities.showToast
import com.mynameismidori.currencypicker.CurrencyPicker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class NewBillFragment : BaseFragment(R.layout.fragment_new_bill) {

    private var _binding: FragmentNewBillBinding? = null
    private val binding get() = _binding!!
    private lateinit var customView: View
    private lateinit var popupWindow: PopupWindow
    private lateinit var calendar: CalendarView
    private var isDateChosen = false
    private lateinit var inst: Instrumentation
    private var symbol = "₽"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentNewBillBinding.inflate(inflater, container, false)
        APP_ACTIVITY.toolbar.title = "Новое объявление"
        customView = layoutInflater.inflate(R.layout.end_date_picker, null)
        val window = APP_ACTIVITY.window
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.navigationBarColor = APP_ACTIVITY.resources.getColor(R.color.white)
        if (Build.VERSION.SDK_INT >= 29)
            window.isNavigationBarContrastEnforced = true
        initFunc()
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    @SuppressLint("SimpleDateFormat")
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
            calendar = customView.findViewById(R.id.calendar)
            var date = calendar.date.toString()
            calendar.minDate = calendar.date
            customView.findViewById<CalendarView>(R.id.calendar)
                .setOnDateChangeListener { _, year, month, dayOfMonth ->
                    date = if (dayOfMonth < 10 && month < 9)
                        "0$dayOfMonth.0${month + 1}.$year"
                    else if (dayOfMonth < 10 && month >= 9)
                        "0$dayOfMonth.${month + 1}.$year"
                    else if ((dayOfMonth >= 10 && month < 9))
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
            val cost = binding.newBillCostEdittext.text.toString() + symbol
            val format = SimpleDateFormat("dd.MM.yyyy")
            val startDate = format.format(Date(calendar.date)).toString()
            if (storeName.isEmpty())
                showToast("Введите название магазина")
            else if (!isDateChosen)
                showToast("Выберите дату окончания")
            else if (cost.isEmpty())
                showToast("Введите стоимость доставки")
            else {
                createBillAndPushToDatabase(storeName, date, cost, startDate) { billID ->
                    val mapData = hashMapOf<String, Any>()
                    mapData[billID] = "1"
                    REF_DATABASE_ROOT.child(NODE_USERS).child(CURRENT_UID).child(CHILD_REGISTERED)
                        .updateChildren(mapData)
                        .addOnSuccessListener {
                            USER.registered[billID] = "1"
                            CoroutineScope(Dispatchers.IO).launch {
                                inst = Instrumentation()
                                inst.sendKeyDownUpSync(KeyEvent.KEYCODE_BACK)
                            }
                        }.addOnFailureListener { showToast(it.message.toString()) }
                }
            }
        }
        binding.newBillCurrency.setOnClickListener {
            openCurrencyPicker()
        }
        binding.newBillCurrencySymbol.setOnClickListener {
            openCurrencyPicker()
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    fun openCurrencyPicker() {
        val picker = CurrencyPicker.newInstance("Выберите валюту")
        picker.setListener { _, _, symbol, _ ->
            binding.newBillCurrencySymbol.text = symbol
            binding.newBillCurrency.visibility = View.GONE
            this.symbol = symbol
            picker.dismiss()
        }
        picker.show(APP_ACTIVITY.supportFragmentManager, "CURRENCY_PICKER")
    }
}