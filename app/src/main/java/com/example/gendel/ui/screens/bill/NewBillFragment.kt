package com.example.gendel.ui.screens.bill

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Instrumentation
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.example.gendel.R
import com.example.gendel.database.*
import com.example.gendel.databinding.FragmentNewBillBinding
import com.example.gendel.ui.screens.base.BaseFragment
import com.example.gendel.utilities.APP_ACTIVITY
import com.example.gendel.utilities.setBottomNavigationBarColor
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
    private var tags: MutableList<String> = mutableListOf()
    private var checkBoxes: MutableList<CheckBox> = mutableListOf()
    private var chosenTags: MutableList<String> = mutableListOf()
    private lateinit var customView: View
    private lateinit var dialogView: View
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
        APP_ACTIVITY.toolbar.title = getString(R.string.new_bill)
        customView = layoutInflater.inflate(R.layout.end_date_picker, null)
        dialogView = layoutInflater.inflate(R.layout.choose_tags_dialog, null)
        calendar = customView.findViewById(R.id.calendar)
        APP_ACTIVITY.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
        setBottomNavigationBarColor(R.color.white)
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
                showToast(APP_ACTIVITY.getString(R.string.enter_store_name))
            else if (!isDateChosen)
                showToast(APP_ACTIVITY.getString(R.string.choose_end_date))
            else if (cost.isEmpty())
                showToast(getString(R.string.enter_delivery_cost))
            else {
                createBillAndPushToDatabase(storeName, date, cost, startDate, chosenTags) { billID ->
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
        binding.newBillTagsRoot.setOnClickListener {
            getTagsFromDatabase { tagsList ->
                tags = tagsList
                tags.forEach {
                    val checkBox = CheckBox(APP_ACTIVITY)
                    checkBox.text = it
                    checkBox.textSize = 20f
                    checkBox.setTextColor(ContextCompat.getColor(APP_ACTIVITY, R.color.pink))
                    checkBox.buttonTintList =
                        ContextCompat.getColorStateList(APP_ACTIVITY, R.color.pink)
                    if (it in chosenTags)
                        checkBox.isChecked = true
                    dialogView.findViewById<LinearLayout>(R.id.choose_tags_container)
                        .addView(checkBox, 0)
                    checkBoxes.add(checkBox)
                }
            }
            dialogView.findViewById<TextView>(R.id.choose_tags_add_tag_button).setOnClickListener {
                dialogView.findViewById<LinearLayout>(R.id.choose_tags_new_tag_root)
                    .visibility = View.VISIBLE
                dialogView.findViewById<TextView>(R.id.choose_tags_add_tag_button)
                    .visibility = View.GONE
                dialogView.findViewById<ImageButton>(R.id.choose_tags_add_new_tag_button)
                    .setOnClickListener {
                        val tag = "#" +
                                dialogView.findViewById<EditText>(R.id.choose_tags_add_new_tag_edt)
                                    .text.toString()
                        if (tag !in tags) {
                            addTagToDatabase(tag) {
                                tags.add(tag)
                                dialogView.findViewById<EditText>(R.id.choose_tags_add_new_tag_edt)
                                    .setText("")
                                dialogView.findViewById<LinearLayout>(R.id.choose_tags_new_tag_root)
                                    .visibility = View.GONE
                                dialogView.findViewById<TextView>(R.id.choose_tags_add_tag_button)
                                    .visibility = View.VISIBLE
                                val checkBox = CheckBox(APP_ACTIVITY)
                                checkBox.text = tag
                                checkBox.textSize = 20f
                                checkBox.setTextColor(
                                    ContextCompat.getColor(
                                        APP_ACTIVITY,
                                        R.color.pink
                                    )
                                )
                                checkBox.buttonTintList =
                                    ContextCompat.getColorStateList(APP_ACTIVITY, R.color.pink)
                                dialogView.findViewById<LinearLayout>(R.id.choose_tags_container)
                                    .addView(checkBox, 0)
                                checkBoxes.add(checkBox)
                            }
                        }
                    }
            }
            AlertDialog.Builder(APP_ACTIVITY, R.style.MyDialogTheme)
                .setTitle("Выберите тэги")
                .setView(dialogView)
                .setPositiveButton(R.string.choose_tags_save) { dialog, _ ->
                    chosenTags.clear()
                    checkBoxes.forEach {
                        if (it.isChecked) {
                            chosenTags.add(it.text.toString())
                        }
                    }
                    chosenTags.forEach {
                        Log.e("chosenTags", it)
                    }
                    var tags: String = when {
                        chosenTags.size == 1 -> chosenTags[0]
                        chosenTags.size == 2 -> chosenTags[0] + " " + chosenTags[1]
                        chosenTags.size >= 3 -> chosenTags[0] + "" +
                                " " + chosenTags[1] +
                                " " + chosenTags[2]
                        else -> getString(R.string.new_bill_tags_title)
                    }
                    if (tags.length>20)
                       tags = tags.substring(0..16)+"..."
                    binding.newBillTagsTitle.text = tags
                    dialog.dismiss()
                }
                .setNegativeButton(R.string.calendar_button_cancel) { dialog, _ ->
                    dialog.cancel()
                }.setOnDismissListener {
                    dialogView.findViewById<LinearLayout>(R.id.choose_tags_new_tag_root)
                        .visibility = View.GONE
                    dialogView.findViewById<TextView>(R.id.choose_tags_add_tag_button)
                        .visibility = View.VISIBLE
                    dialogView.findViewById<LinearLayout>(R.id.choose_tags_container)
                        .removeAllViews()
                    checkBoxes.clear()
                    (dialogView.parent as ViewGroup).removeView(dialogView)
                }.create().show()

        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    fun openCurrencyPicker() {
        val picker = CurrencyPicker.newInstance(getString(R.string.сhoose_currency))
        picker.setListener { _, _, symbol, _ ->
            binding.newBillCurrencySymbol.text = symbol
            binding.newBillCurrency.visibility = View.GONE
            this.symbol = symbol
            picker.dismiss()
        }
        picker.show(APP_ACTIVITY.supportFragmentManager, "CURRENCY_PICKER")
    }
}