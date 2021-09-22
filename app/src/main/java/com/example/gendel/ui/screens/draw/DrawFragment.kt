package com.example.gendel.ui.screens.draw

import android.annotation.SuppressLint
import android.app.Instrumentation
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.PopupWindow
import android.widget.RadioButton
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.example.gendel.R
import com.example.gendel.database.getMessageKey
import com.example.gendel.database.getMessageKeyForGroup
import com.example.gendel.database.uploadFileToStorage
import com.example.gendel.database.uploadFileToStorageForGroup
import com.example.gendel.databinding.FragmentDrawBinding
import com.example.gendel.utilities.*
import com.jaredrummler.android.colorpicker.ColorPickerDialog
import com.jaredrummler.android.colorpicker.ColorShape
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class DrawFragment(private val dialogId: String, val type: String) :
    Fragment(R.layout.fragment_draw) {
    private var _binding: FragmentDrawBinding? = null
    val binding get() = _binding!!
    private lateinit var popupWindow: PopupWindow
    private lateinit var inst: Instrumentation
    private lateinit var file: File
    private lateinit var customView: View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        hideKeyboard()
        APP_ACTIVITY.toolbar.visibility = View.GONE
        _binding = FragmentDrawBinding.inflate(inflater, container, false)
        customView = layoutInflater.inflate(R.layout.seekbar_popup_window, null)
        DRAW_FRAGMENT = this
        return binding.root
    }

    @SuppressLint("ResourceType")
    override fun onResume() {
        super.onResume()
        binding.graffitiColorPicker.background.current.setTint(
            ResourcesCompat.getColor(
                resources,
                R.attr.colorPrimary,
                null
            )
        )
        binding.graffitiLineWeight.setOnClickListener {
            popupWindow = PopupWindow(
                customView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            popupWindow.elevation = 10f
            popupWindow.isOutsideTouchable = true
            popupWindow.showAsDropDown(
                binding.graffitiLineWeight,
                -65,
                -770,
                Gravity.CENTER
            )
            customView.findViewById<RadioButton>(R.id.weight_10).setOnClickListener {
                binding.graffitiCanvas.paint.strokeWidth = 10F
                popupWindow.dismiss()
            }
            customView.findViewById<RadioButton>(R.id.weight_15).setOnClickListener {
                binding.graffitiCanvas.paint.strokeWidth = 15F
                popupWindow.dismiss()
            }
            customView.findViewById<RadioButton>(R.id.weight_20).setOnClickListener {
                binding.graffitiCanvas.paint.strokeWidth = 20F
                popupWindow.dismiss()
            }
            customView.findViewById<RadioButton>(R.id.weight_40).setOnClickListener {
                binding.graffitiCanvas.paint.strokeWidth = 40F
                popupWindow.dismiss()
            }
            customView.findViewById<RadioButton>(R.id.weight_60).setOnClickListener {
                binding.graffitiCanvas.paint.strokeWidth = 60F
                popupWindow.dismiss()
            }
        }
        binding.graffitiClose.setOnClickListener {
            backToChat()
        }
        binding.graffitiEraser.setOnClickListener {
            binding.graffitiEraser.background.current.setTint(
                ResourcesCompat.getColor(
                    resources,
                    R.attr.colorPrimary,
                    null
                )
            )
            binding.graffitiColorPicker.background.current.setTint(
                ResourcesCompat.getColor(
                    resources,
                    R.color.grey,
                    null
                )
            )
            binding.graffitiCanvas.paint.color =
                ResourcesCompat.getColor(resources, R.color.colorBackground, null)
        }
        binding.graffitiColorPicker.setOnClickListener {
            binding.graffitiEraser.background.current.setTint(
                ResourcesCompat.getColor(
                    resources,
                    R.color.grey,
                    null
                )
            )
            createColorPickerDialog()
        }
        binding.graffitiButtonOk.setOnClickListener {
            send()
        }
    }

    private fun send() {
        /*val bitmap = Bitmap.createBitmap(binding.graffitiCanvas.extraBitmap)
        when (type) {
            TYPE_CHAT -> {
                val messageKey = getMessageKey(dialogId)
                file = File(APP_ACTIVITY.filesDir, messageKey)
                file.writeBitmap(bitmap, Bitmap.CompressFormat.PNG, 85)
                file.createNewFile()
                val uri = Uri.fromFile(file)
                uploadFileToStorage(
                    uri,
                    messageKey,
                    dialogId,
                    TYPE_MESSAGE_IMAGE,
                    "Графити"
                )
                file.delete()
            }
            TYPE_BILL -> {
                val messageKey = getMessageKeyForGroup(dialogId)
                file = File(APP_ACTIVITY.filesDir, messageKey)
                file.writeBitmap(bitmap, Bitmap.CompressFormat.PNG, 85)
                file.createNewFile()
                val uri = Uri.fromFile(file)
                uploadFileToStorageForGroup(
                    uri,
                    messageKey,
                    dialogId,
                    TYPE_MESSAGE_IMAGE,
                    "Графити"
                )
                file.delete()
            }
        }
        backToChat()*/
    }

    private fun backToChat() {
        APP_ACTIVITY.toolbar.visibility = View.VISIBLE
        CoroutineScope(Dispatchers.IO).launch {
            inst = Instrumentation()
            inst.sendKeyDownUpSync(KeyEvent.KEYCODE_BACK)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    @SuppressLint("ResourceType")
    private fun createColorPickerDialog() {
        ColorPickerDialog.newBuilder()
            .setColor(ResourcesCompat.getColor(resources, R.attr.colorPrimary, null))
            .setDialogType(ColorPickerDialog.TYPE_PRESETS)
            .setAllowCustom(true)
            .setAllowPresets(true)
            .setColorShape(ColorShape.CIRCLE)
            .setDialogId(1)
            .show(APP_ACTIVITY)
    }
}